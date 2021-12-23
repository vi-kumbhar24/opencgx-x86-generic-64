SUMMARY = "Kernel packages required to satisfy the Carrier Grade Linux (CGL) specification"
DESCRIPTION = "This package group contains hardened device drivers, HW configurations, \
               management, standard, high availability, service and co-processor interfaces."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

inherit packagegroup


PACKAGES = "packagegroup-cgl-kernel"

RDEPENDS_packagegroup-cgl-kernel = " \
    ltp \
    kernel-modules \
    "

RRECOMMENDS_packagegroup-cgl-kernel = ""
