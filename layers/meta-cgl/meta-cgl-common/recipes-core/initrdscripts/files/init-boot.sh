#!/bin/sh -eu

# Fail function, either called explicitly or when shell will quit
fail () {
    # Avoid recursive traps
    trap '' ERR EXIT

    # If message provided, print it
    [ -n '${1:-}' ] && echo $@

    # Generic error message and shell access
    echo "Error occured, giving a shell"
    exec sh
}

trap fail ERR EXIT

interrupt () {
    echo "User interrupt received, giving a shell. When exiting shell, execution will continue."
    sh
}

trap interrupt INT

# Find session ID for an iSCSI disk given its IQN name
iqn_to_sid () {
    iscsiadm -m session | fgrep $1 | sed -r 's/.*\[([0-9])\].*/\1/'
}

# Find device name, without path, for an iSCSI disk given its IQN name
iqn_to_dev () {
    iscsiadm -m session -r $(iqn_to_sid $1) -P3 | sed -rn 's/.*Attached scsi disk ([a-zA-Z0-9_]+).*/\1/p'
}

# Parse input parameters expecting name=value pairs.
# Name only matches known parameters.
# On match, set variable <name> to value <value>.
# E.g. given "parse_cmdline trythis="ok, do it"
# the shell variable "trythis" will now have the value "ok, do it".
parse_cmdline () {
    iscsi_chap_user=""
    iscsi_chap_pwd=""
    iscsi_dev=""
    iscsi_debug=0

    while [ -n "${1:-}" ]; do
        name="${1%%=*}"
        val="${1#*=}"
        case $name in
            iscsi_chap_user|iscsi_chap_pw)
                eval $name=\"$val\";;
            iscsi_dev)
                eval $name=\"$iscsi_dev $val\"
                ;;
            iscsi_debug)
                set -x
                iscsi_debug=1
                ;;
        esac
        shift
    done

    [ -n "${iscsi_dev}" ] || fail "Mandatory kernel boot parameter 'iscsi_dev' not given."

}

PATH=/sbin:/bin:/usr/sbin:/usr/bin

echo "Mounting /proc"
mount -t proc proc /proc

KERNEL_CMDLINE="$(cat /proc/cmdline)"

echo "Parsing kernel parameters"
parse_cmdline $KERNEL_CMDLINE

echo "Mounting /sys"
mount -t sysfs sysfs /sys

# udev is needed for multipath
echo "Starting udev"
/etc/init.d/udev start

# Add CHAP autenthication, if given as kernel boot parameters
echo "Configuring iSCSI"
[ -n "$iscsi_chap_user" -o -n "$iscsi_chap_pw" ] && cat <<EOF >> /etc/iscsi/iscsid.conf
node.session.auth.authmethod = CHAP
EOF
[ -n "$iscsi_chap_user" ] && cat <<EOF >> /etc/iscsi/iscsid.conf
node.session.auth.username = $iscsi_chap_user
EOF
[ -n "$iscsi_chap_pw" ] && cat <<EOF >> /etc/iscsi/iscsid.conf
node.session.auth.password = $iscsi_chap_pw
EOF

echo >> /etc/iscsi/iscsid.conf

echo "Starting iSCSI daemon"
/etc/init.d/iscsid restart

echo "Discovering iSCSI devices"

for dev in $iscsi_dev; do
    target_ip="${dev%%:*}"
    dev_name="${dev#*:}"
    echo "Logging in to iscsi devices: $dev_name, target: $target_ip"
    iscsiadm --mode discoverydb --type sendtargets --discover --portal $target_ip
    iscsiadm --mode node --targetname $dev_name --login --portal $target_ip
done

echo "Configuring multipath"
cat <<EOF > /etc/multipath.conf
defaults {
        path_grouping_policy    multibus
        # If no path, then queue requests
        no_path_retry queue
}
devices {
        device {
                vendor                  IET
                product                 VIRTUAL-DISK
                path_grouping_policy    multibus
        }
}
blacklist {
        devnode ".*"
}
blacklist_exceptions {
$(for dev in $iscsi_dev; do
    echo "        devnode   \"^$(iqn_to_dev ${dev#*:})\""
  done)
        property  ".*"
}
EOF

echo "Starting multipath daemon"
# Make sure lock file directory exists
mkdir -p /var/lock/subsys/multipathd
/etc/init.d/multipathd start

mpath_template='/dev/disk/by-id/dm-uuid-mpath-*'

echo "Waiting for mpath device to appear"
while [ -z "$(ls $mpath_template 2>/dev/null)" ]; do
    sleep 1
done
MPATH_DEV="$(ls $mpath_template)"
echo "mpath device: $MPATH_DEV"

echo "Mounting mpath device $MPATH_DEV"
mount $MPATH_DEV /mnt

echo "Stopping multipath daemon"
# Should be using "/etc/init.d/multipathd stop", but did not work.
# Got "killall: /sbin/multipathd: no process killed".
# Kill the process based on the saved pid.
kill $(cat /run/multipathd.pid)

echo "Moving iscsi pid and lock files"
cp /run/iscsid.pid /mnt/run/iscsid.pid
cp /run/lock/iscsi/* /mnt/run/lock/iscsi

# In case iscsi_debug is given, open a shell at this point
if [ $iscsi_debug -eq 1 ]; then
    echo "iscsi_debug given, opening a shell. When exiting shell, boot will continue."
    sh
fi

# udev needs to be restart when real init runs, so stop it
echo "Stopping udev"
/etc/init.d/udev stop

echo "Moving sys, proc and dev mounts to new root"
mount --move /sys /mnt/sys
mount --move /proc /mnt/proc
mount --move /dev /mnt/dev

echo "Switching to new root"
exec switch_root /mnt /sbin/init $KERNEL_CMDLINE
