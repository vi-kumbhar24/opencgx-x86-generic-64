SUMMARY = "Software development tools packages required to satisfy the Carrier Grade Linux (CGL) specification"
DESCRIPTION = "This package group contains programs or applications used to create, debug, maintain, \
               or otherwise support other programs and applications."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

inherit packagegroup


PACKAGES = "packagegroup-cgl-swdevtools"

NUMACTL = "numactl"
NUMACTL_arm = ""

RDEPENDS_packagegroup-cgl-swdevtools = " \
    libuio \
    libcap-ng \
    libwww-perl \
    libtest-pod-perl \
    libsocket6-perl \
    libmailtools-perl \
    libhtml-tagset-perl \
    ${NUMACTL} \
    "

RRECOMMENDS_packagegroup-cgl-swdevtools = ""
