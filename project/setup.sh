   if [ -n "$BASH_SOURCE" ]; then
      THIS_SCRIPT=$BASH_SOURCE
   elif [ -n "$ZSH_NAME" ]; then
      THIS_SCRIPT=$0
   else
      THIS_SCRIPT="$(pwd)/setup.sh"
   fi
   PROJECT_DIR=$(dirname $(readlink -f $THIS_SCRIPT))
   cd $PROJECT_DIR
   source ../buildtools/environment-setup-*
   source ../layers/poky/oe-init-build-env $PROJECT_DIR
