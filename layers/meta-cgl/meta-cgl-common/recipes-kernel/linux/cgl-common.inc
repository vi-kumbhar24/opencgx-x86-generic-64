FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI_append_poky-cgl = " file://cfg/00001-systemtap.cfg \
            file://cfg/00002-oprofile.cfg \
            file://cfg/00003-lttng.cfg \
            file://cfg/00004-kgdb.cfg \
            file://cfg/00005-quota.cfg \
            file://cfg/00007-device-mapper-multipath.cfg \
            file://cfg/00008-libcgroup.cfg \
            file://cfg/00009-filesystem-acl.cfg \
            file://cfg/00010-ext4.cfg \
            file://cfg/00011-raid-1.cfg \
            file://cfg/00012-hyper-threading.cfg \
            file://cfg/00013-dot1q-vlans.cfg \
            file://cfg/00014-selinux.cfg \
            file://cfg/00015-managing-transient-data.cfg \
            file://cfg/00016-dynamic-probe-insertion-kprobe.cfg \
            file://cfg/00018-ipsec.cfg \
            file://cfg/00019-raid-0.cfg \
            file://cfg/00020-kexec.cfg \
            file://cfg/00021-sctp.cfg \
            file://cfg/00022-coredump.cfg \
            file://cfg/00023-open-scsi.cfg \
            file://cfg/00024-ftrace.cfg \
            file://cfg/00025-ipmi.cfg \
            file://cfg/00026-lm-sensors.cfg \
            file://cfg/00027-dmidecode.cfg \
            file://cfg/00028-bonding.cfg \
           "

