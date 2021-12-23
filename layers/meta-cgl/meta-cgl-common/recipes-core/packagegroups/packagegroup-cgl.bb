SUMMARY = "Packages required to satisfy the Carrier Grade Linux (CGL) specification"
DESCRIPTION = "This package group is the one that gathers all the available \
               package groups."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

inherit packagegroup


PACKAGES = "packagegroup-cgl"

RDEPENDS_${PN} = "\
    packagegroup-cgl-kernel \
    packagegroup-cgl-swdevtools \
    packagegroup-cgl-middleware \
    packagegroup-cgl-applications \
    "

RRECOMMENDS_${PN} = ""
