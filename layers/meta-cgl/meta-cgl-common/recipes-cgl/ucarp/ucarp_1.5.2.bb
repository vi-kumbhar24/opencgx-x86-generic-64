SUMMARY = "Common Address Redundancy Protocol for Unix"
DESCRIPTION = "UCARP allows a couple of hosts to share common \
virtual IP addresses in order to provide automatic failover. \
It is a portable userland implementation of the secure and \
patent-free Common Address Redundancy Protocol (CARP, OpenBSD's \
alternative to the patents-bloated VRRP. \
Strong points of the CARP protocol are: very low overhead, \
cryptographically signed messages, interoperability between \
different operating systems and no need for any dedicated extra \
network link between redundant hosts."

HOMEPAGE = "http://www.ucarp.org"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=278a886e91f2f6c983ffdf040130cdc6"

SRC_URI = "http://download.pureftpd.org/pub/ucarp/${BPN}-${PV}.tar.gz \
           file://ucarp-configure-sha1.patch \
           file://ucarp-configure-snprintf.patch \
           file://ucarp.init \
           file://vip-001.conf.example \
           file://vip-common.conf \
           file://vip-up.sh \
           file://vip-down.sh \
           file://ucarp.service \
          "

SRC_URI[md5sum] = "e3caa733316a32c09e5d3817617e9145"
SRC_URI[sha256sum] = "f3cc77e28481fd04f62bb3d4bc03104a97dd316c80c0ed04ad7be24b544112f3"

inherit autotools gettext systemd

DEPENDS = "libpcap"

SYSTEMD_SERVICE_${PN} = "ucarp.service"
SYSTEMD_AUTO_ENABLE = "disable"

EXTRA_OECONF += "--sysconfdir=${sysconfdir}/${BPN}"

# fix the perms for config.rpath
do_configure_prepend() {
    chmod 755 ${S}/config.rpath
}

do_install_append() {
    sed -i -e 's#\(UPSCRIPT=\).*#\1${libexecdir}/vip-up.sh#' \
           -e 's#\(DOWNSCRIPT=\).*#\1${libexecdir}/vip-down.sh#' ${WORKDIR}/ucarp.init

    install -D -m 0755 ${WORKDIR}/ucarp.init ${D}${sysconfdir}/init.d/ucarp

    # For systemd
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -D -m 0755 ${WORKDIR}/ucarp.init ${D}${libexecdir}/ucarp
        install -D -m 0644 ${WORKDIR}/ucarp.service ${D}${systemd_system_unitdir}/ucarp.service
        sed -i -e 's,@LIBEXECDIR@,${libexecdir},g' ${D}${systemd_system_unitdir}/ucarp.service
    fi

    install -m 0755 -d ${D}${sysconfdir}/ucarp
    install -m 0600 ${WORKDIR}/vip-001.conf.example ${D}${sysconfdir}/ucarp/vip-001.conf.example
    install -m 0600 ${WORKDIR}/vip-common.conf ${D}${sysconfdir}/ucarp/vip-common.conf

    install -m 0755 -d ${D}${libexecdir}
    install -m 0700 ${WORKDIR}/vip-up.sh ${D}${libexecdir}/vip-up.sh
    install -m 0700 ${WORKDIR}/vip-down.sh ${D}${libexecdir}/vip-down.sh
}
