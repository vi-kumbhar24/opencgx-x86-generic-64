require core-image-cgl.bb

# Recipe is based on core-image-minimal.bb
DESCRIPTION = "Initramfs used to mount multipath device as root file system"

PACKAGE_INSTALL = "initramfs-cgl-boot busybox base-passwd udev"

# Do not pollute the initrd image with rootfs features
IMAGE_FEATURES = ""

export IMAGE_BASENAME = "core-image-cgl-initramfs"
IMAGE_LINGUAS = ""

LICENSE = "MIT"
IMAGE_FSTYPES ??= "cpio.gz.u-boot"

IMAGE_ROOTFS_SIZE = "8192"

BAD_RECOMMENDATIONS += "busybox-syslog"
