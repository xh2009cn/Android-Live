#!/bin/bash
#NDK路径
export NDK=C:/android-sdk/ndk-bundle

export API_LEVEL=16
export AOSP_TOOLCHAIN_SUFFIX=4.9
export AOSP_API="android-$API_LEVEL"

export PROJECT_DIR=C:/code/media/Android-Live
export FFMPEG_SRC_DIR=$PROJECT_DIR/ffmpeg_src
export FFMPEG_EXTERNAL_DIR=$PROJECT_DIR/ffmpeg_external
export FFMPEG_OUTPUT_DIR=$PROJECT_DIR/ffmpeg_libs
export TMPDIR=$FFMPEG_DIR/fftemp

echo "FFMPEG_SRC_DIR="$FFMPEG_SRC_DIR
echo "FFMPEG_EXTERNAL_DIR="$FFMPEG_EXTERNAL_DIR
echo "FFMPEG_OUTPUT_DIR="$FFMPEG_OUTPUT_DIR
echo "TMPDIR="$TMPDIR

#架构
if [ "$#" -lt 1 ]; then
	THE_ARCH=armeabi-v7a
else
	THE_ARCH=$(tr [A-Z] [a-z] <<< "$1")
fi

#根据不同架构配置环境变量
case "$THE_ARCH" in
  arm|armv5|armv6|armv7|armeabi)
    echo "arm branch"
	TOOLCHAIN_BASE="arm-linux-androideabi"
	TOOLNAME_BASE="arm-linux-androideabi"
	AOSP_ABI="armeabi"
	AOSP_ARCH="arch-arm"
	LLVM_ARCH="arm7a"
	HOST="arm-linux-androideabi"
	AOSP_FLAGS="-march=armv5te -mtune=xscale -mthumb -msoft-float -funwind-tables -fexceptions -frtti"
	FF_EXTRA_CFLAGS="-O3 -fpic -fasm  -fno-short-enums -fno-strict-aliasing  -mfloat-abi=softfp -mfpu=vfp -marm -march=armv6 "
	FF_CFLAGS="-O3 -Wall -pipe -ffast-math -fstrict-aliasing -Werror=strict-aliasing  -Wa,--noexecstack -DANDROID  "
	;;
  armv7a|armeabi-v7a)
    echo "armeabi-v7a branch"
	TOOLCHAIN_BASE="arm-linux-androideabi"
	TOOLNAME_BASE="arm-linux-androideabi"
	AOSP_ABI="armeabi-v7a"
	AOSP_ARCH="arch-arm"
	LLVM_ARCH="arm7a"
	HOST="arm-linux-androideabi"
	AOSP_FLAGS="-march=armv7-a -mthumb -mfpu=vfpv3-d16 -mfloat-abi=softfp -Wl,--fix-cortex-a8 -funwind-tables -fexceptions -frtti"
	FF_EXTRA_CFLAGS="-DANDROID -fPIC -ffunction-sections -funwind-tables -fstack-protector -march=armv7-a -mfloat-abi=softfp -mfpu=vfpv3-d16 -fomit-frame-pointer -fstrict-aliasing "
	FF_CFLAGS="-O3 -Wall -pipe -ffast-math -fstrict-aliasing -Werror=strict-aliasing  -Wa,--noexecstack -DANDROID "
	;;
  hard|armv7a-hard|armeabi-v7a-hard)
	TOOLCHAIN_BASE="arm-linux-androideabi"
	TOOLNAME_BASE="arm-linux-androideabi"
	AOSP_ABI="armeabi-v7a"
	AOSP_ARCH="arch-arm"
	LLVM_ARCH="arm7a"
	HOST="arm-linux-androideabi"
	AOSP_FLAGS="-mhard-float -D_NDK_MATH_NO_SOFTFP=1 -march=armv7-a -mfpu=vfpv3-d16 -mfloat-abi=softfp -Wl,--fix-cortex-a8 -funwind-tables -fexceptions -frtti -Wl,--no-warn-mismatch -Wl,-lm_hard"
	FF_EXTRA_CFLAGS="-DANDROID -fPIC -ffunction-sections -funwind-tables -fstack-protector -march=armv7-a -mfloat-abi=softfp -mfpu=vfpv3-d16 -fomit-frame-pointer -fstrict-aliasing   "
	FF_CFLAGS="-O3 -Wall -pipe -ffast-math -fstrict-aliasing -Werror=strict-aliasing  -Wa,--noexecstack -DANDROID  "
	;;
  neon|armv7a-neon)
	TOOLCHAIN_BASE="arm-linux-androideabi"
	TOOLNAME_BASE="arm-linux-androideabi"
	AOSP_ABI="armeabi-v7a"
	AOSP_ARCH="arch-arm"
	LLVM_ARCH="arm7a"
	HOST="arm-linux-androideabi"
	AOSP_FLAGS="-march=armv7-a -mfpu=vfpv3-d16 -mfloat-abi=softfp -Wl,--fix-cortex-a8 -funwind-tables -fexceptions -frtti"
	FF_EXTRA_CFLAGS="-DANDROID -fPIC -ffunction-sections -funwind-tables -fstack-protector -march=armv7-a -mfloat-abi=softfp -mfpu=vfpv3-d16 -fomit-frame-pointer -fstrict-aliasing   "
	FF_CFLAGS="-O3 -Wall -pipe -ffast-math -fstrict-aliasing -Werror=strict-aliasing  -Wa,--noexecstack -DANDROID  "
	;;
  armv8|armv8a|aarch64|arm64|arm64-v8a)
	TOOLCHAIN_BASE="aarch64-linux-android"
	TOOLNAME_BASE="aarch64-linux-android"
	AOSP_ABI="arm64-v8a"
	AOSP_ARCH="arch-arm64"
	LLVM_ARCH="aarch64"
	HOST="aarch64-linux"
	AOSP_FLAGS="-funwind-tables -fexceptions -frtti"
	FF_EXTRA_CFLAGS=""
	FF_CFLAGS="-O3 -Wall -pipe -ffast-math -fstrict-aliasing -Werror=strict-aliasing  -Wa,--noexecstack -DANDROID  "
	;;
  mips|mipsel)
	TOOLCHAIN_BASE="mipsel-linux-android"
	TOOLNAME_BASE="mipsel-linux-android"
	AOSP_ABI="mips"
	AOSP_ARCH="arch-mips"
	LLVM_ARCH="i686"
	HOST="mipsel-linux"
	AOSP_FLAGS="-funwind-tables -fexceptions -frtti"
	;;
  mips64|mipsel64|mips64el)
	TOOLCHAIN_BASE="mips64el-linux-android"
	TOOLNAME_BASE="mips64el-linux-android"
	AOSP_ABI="mips64"
	LLVM_ARCH="i686"
	AOSP_ARCH="arch-mips64"
	HOST="mipsel64-linux"
	AOSP_FLAGS="-funwind-tables -fexceptions -frtti"
	;;
  x86)
	TOOLCHAIN_BASE="x86"
	TOOLNAME_BASE="i686-linux-android"
	AOSP_ABI="x86"
	AOSP_ARCH="arch-x86"
	LLVM_ARCH="x86-64"
	HOST="i686-linux"
	AOSP_FLAGS="-march=i686 -mtune=intel -mssse3 -mfpmath=sse -funwind-tables -fexceptions -frtti"
	FF_EXTRA_CFLAGS="-O3 -DANDROID -Dipv6mr_interface=ipv6mr_ifindex -fasm  -fno-short-enums -fno-strict-aliasing -fomit-frame-pointer -march=k8 "
	FF_CFLAGS="-O3 -Wall -pipe -ffast-math -fstrict-aliasing -Werror=strict-aliasing  -Wa,--noexecstack -DANDROID  "
	;;
  x86_64|x64)
	TOOLCHAIN_BASE="x86_64"
	TOOLNAME_BASE="x86_64-linux-android"
	AOSP_ABI="x86_64"
	AOSP_ARCH="arch-x86_64"
	LLVM_ARCH="x86-64"
	HOST="x86_64-linux"
	AOSP_FLAGS="-march=x86-64 -msse4.2 -mpopcnt -mtune=intel -funwind-tables -fexceptions -frtti"
	FF_EXTRA_CFLAGS="-O3 -DANDROID -Dipv6mr_interface=ipv6mr_ifindex -fasm  -fno-short-enums -fno-strict-aliasing -fomit-frame-pointer -march=k8 "
    FF_CFLAGS="-O3 -Wall -pipe -ffast-math -fstrict-aliasing -Werror=strict-aliasing  -Wa,--noexecstack -DANDROID  "
	;;
  *)
	echo "ERROR: Unknown architecture $1"
	[ "$0" = "$BASH_SOURCE" ] && exit 1 || return 1
	;;
esac

#LLVM_ARCH，取值范围：aarch64 armv7a i686 x86-64，NDK\toolchains\llvm\prebuilt\windows-x86_64\bin目录下的clang文件前缀

echo "NDK="$NDK
echo "API_LEVEL="$API_LEVEL
echo "LLVM_ARCH="$LLVM_ARCH
echo "TOOLCHAIN_BASE="$TOOLCHAIN_BASE
echo "TOOLNAME_BASE="$TOOLNAME_BASE
echo "AOSP_ABI="$AOSP_ABI
echo "AOSP_ARCH="$AOSP_ARCH
echo "AOSP_FLAGS="$AOSP_FLAGS
echo "FF_CFLAGS="$FF_CFLAGS
echo "FF_EXTRA_CFLAGS="$FF_EXTRA_CFLAGS
echo "HOST="$HOST

