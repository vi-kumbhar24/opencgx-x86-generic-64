SUMMARY = "Scalable High-Availability cluster resource manager"
DESCRIPTION = "Pacemaker is an advanced, scalable High-Availability \
cluster resource manager for Linux-HA (Heartbeat) and/or OpenAIS. \
It supports n-node clusters with significant capabilities for \
managing resources and dependencies. \
It will run scripts at initialization, when machines go up or down, \
when related resources fail and can be configured to periodically \
check resource health."
HOMEPAGE = "http://www.clusterlabs.org"

LICENSE = "GPLv2+ & LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=000212f361a81b100d9d0f0435040663"

DEPENDS = "corosync libxslt libxml2 gnutls resource-agents libqb python3-native"

SRC_URI = "git://github.com/ClusterLabs/${BPN}.git \
           file://0006-Fix-tools-Fix-definition-of-curses_indented_printf.patch \
           file://0001-Fix-python3-usage.patch \
           file://volatiles \
           file://tmpfiles \
          "

CFLAGS += "-I${STAGING_INCDIR}/heartbeat"
CPPFLAGS +="-I${STAGING_INCDIR}/heartbeat"
SRC_URI_append_libc-musl = "file://0001-pacemaker-fix-compile-error-of-musl-libc.patch"

SRCREV = "4b1f869f0f64ef0d248b6aa4781d38ecccf83318"

inherit autotools-brokensep pkgconfig systemd python3native python3-dir useradd

S = "${WORKDIR}/git"

UPSTREAM_CHECK_GITTAGREGEX = "Pacemaker-(?P<pver>\d+(\.\d+)+)"

CLEANBROKEN = "1"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[systemd] = "--enable-systemd,--disable-systemd,systemd"
PACKAGECONFIG[libesmtp] = "--with-esmtp=yes,--with-esmtp=no,libesmtp"

EXTRA_OECONF += "STAGING_INCDIR=${STAGING_INCDIR} \
                 --disable-fatal-warnings \
                 --with-ais \
                 --without-heartbeat \
                 --disable-pretty \
                 --disable-tests \
                 "

CACHED_CONFIGUREVARS += " \
    ac_cv_path_BASH_PATH=/bin/bash \
"

do_install_append() {
    install -d ${D}${sysconfdir}/default
    install -d ${D}${sysconfdir}/default/volatiles
    install -m 0644 ${WORKDIR}/volatiles ${D}${sysconfdir}/default/volatiles/06_${BPN}
    install -d ${D}${sysconfdir}/tmpfiles.d
    install -m 0644 ${WORKDIR}/tmpfiles ${D}${sysconfdir}/tmpfiles.d/${BPN}.conf

    # Don't package some files
    find ${D} -name "*.pyo" -exec rm {} \;
    find ${D} -name "*.pyc" -exec rm {} \;
    rm -rf ${D}/${libdir}/service_crm.so

    rm -rf ${D}${localstatedir}/lib/heartbeat
    rm -rf ${D}${localstatedir}/run
}

PACKAGES_prepend = "${PN}-cli-utils ${PN}-libs ${PN}-cluster-libs ${PN}-remote "

FILES_${PN}-cli-utils = "${sbindir}/crm* ${sbindir}/iso8601"
RDEPENDS_${PN}-cli-utils += "libqb bash"
FILES_${PN}-libs = "${libdir}/libcib.so.* \
                    ${libdir}/liblrmd.so.* \
                    ${libdir}/libcrmservice.so.* \
                    ${libdir}/libcrmcommon.so.* \
                    ${libdir}/libpe_status.so.* \
                    ${libdir}/libpe_rules.so.* \
                    ${libdir}/libpengine.so.* \
                    ${libdir}/libstonithd.so.* \
                    ${libdir}/libtransitioner.so.* \
                   "
RDEPENDS_${PN}-libs += "libqb dbus-lib"
FILES_${PN}-cluster-libs = "${libdir}/libcrmcluster.so.*"
RDEPENDS_${PN}-cluster-libs += "libqb"
FILES_${PN}-remote = "${sysconfdir}/init.d/pacemaker_remote \
                      ${sbindir}/pacemaker_remoted \
                      ${libdir}/ocf/resource.d/pacemaker/remote \
                     "
RDEPENDS_${PN}-remote += "libqb bash"
FILES_${PN} += " ${datadir}/snmp                             \
                 ${libdir}/corosync/lcrso/pacemaker.lcrso    \
                 ${libdir}/${PYTHON_DIR}/dist-packages/cts/  \
                 ${libdir}/ocf/resource.d/ \
                 ${libdir}/${PYTHON_DIR}/site-packages/cts/ \
               "
FILES_${PN}-dbg += "${libdir}/corosync/lcrso/.debug"
RDEPENDS_${PN} = "bash python3-core perl libqb ${PN}-cli-utils"

SYSTEMD_AUTO_ENABLE = "disable"

SYSTEMD_PACKAGES += "${PN}-remote"
SYSTEMD_SERVICE_${PN} += "pacemaker.service crm_mon.service"
SYSTEMD_SERVICE_${PN}-remote += "pacemaker_remote.service"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "-r -g haclient -s ${base_sbindir}/nologin hacluster"
GROUPADD_PARAM_${PN} = "-r haclient"
