MV_KERNEL_BRANCH ?= "mvl-5.4/msd.cgx"
MV_KERNEL_TREE ?= "git://github.com/MontaVista-OpenSourceTechnology/linux-mvista.git;protocol=https"
MV_KERNELCACHE_BRANCH ?= "yocto-5.4"
MV_KERNELCACHE_TREE ?= "git://github.com/MontaVista-OpenSourceTechnology/yocto-kernel-cache;protocol=https"

require recipes-kernel/linux/linux-yocto.inc

SRCREV_machine ?= "${MV_KERNEL_BRANCH}"
SRCREV_meta ?= "${MV_KERNELCACHE_BRANCH}"

FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"
LIC_FILES_CHKSUM = "file://COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"

S = "${WORKDIR}/git"

LINUX_VERSION = "5.4"
KERNEL_VERSION_SANITY_SKIP="1"
PV = "${LINUX_VERSION}+git${SRCPV}"

SRC_URI = "${MV_KERNEL_TREE};branch=${MV_KERNEL_BRANCH};name=machine \
           ${MV_KERNELCACHE_TREE};type=kmeta;name=meta;branch=${MV_KERNELCACHE_BRANCH};destsuffix=${KMETA}"
SRC_URI += "file://defconfig"

DEPENDS += "elfutils-native"

KMETA = "kernel-meta"
KCONF_BSP_AUDIT_LEVEL = "0"
COMPATIBLE_MACHINE = "null"
COMPATIBLE_MACHINE_x86-generic-64 = "x86-generic-64"
COMPATIBLE_MACHINE_x86-generic = "x86-generic"
COMPATIBLE_MACHINE_x86-atom-64 = "x86-atom-64"
COMPATIBLE_MACHINE_corei7-mrsv-64 = "corei7-mrsv-64"
COMPATIBLE_MACHINE_dsu-d5-64 = "dsu-d5-64"
