SUMMARY = "Pacemaker command line interface for management and configuration"
DESCRIPTION = "crm shell, a Pacemaker command line interface for management and configuration"

HOMEPAGE = "https://crmsh.github.io"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "asciidoc-native \
           docbook-xsl-stylesheets-native \
           libxslt-native \
           python-setuptools-native \
           "
RDEPENDS_${PN} = "pacemaker python3-lxml python3-parallax gawk bash"

S = "${WORKDIR}/git"
SRC_URI = "git://github.com/ClusterLabs/${BPN}.git \
           file://tweaks_for_build.patch \
          "

SRCREV = "d10d2fbdd1b357500387bebb432c68e88748526b"

inherit autotools-brokensep setuptools3

export HOST_SYS
export BUILD_SYS

# Allow to process DocBook documentations without requiring
# network accesses for the dtd and stylesheets
export SGML_CATALOG_FILES = "${STAGING_DATADIR_NATIVE}/xml/docbook/xsl-stylesheets/catalog.xml"

FILES_${PN} += "${PYTHON_SITEPACKAGES_DIR}/${BPN}"
