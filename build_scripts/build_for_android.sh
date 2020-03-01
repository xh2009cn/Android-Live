#!/bin/bash
source config.sh $1

TOOLCHAIN=$NDK/toolchains/llvm/prebuilt/windows-x86_64
SYSROOT=$TOOLCHAIN/sysroot
PREFIX=$FFMPEG_OUTPUT_DIR/$AOSP_ABI

echo "TOOLCHAIN="$TOOLCHAIN
echo "PREFIX="$PREFIX

cd $FFMPEG_SRC_DIR

./configure \
--ln_s="cp -rf" \
--prefix=$PREFIX \
--disable-runtime-cpudetect \
--disable-asm \
--arch=arm \
--cpu=cortex-a7 \
--target-os=android \
--cc=$TOOLCHAIN/bin/armv7a-linux-androideabi$API_LEVEL-clang \
--cross-prefix=$TOOLCHAIN/bin/arm-linux-androideabi- \
--disable-stripping \
--enable-cross-compile \
--sysroot=$SYSROOT \
--extra-cflags="-I$SYSROOT/usr/include $FF_EXTRA_CFLAGS  $FF_CFLAGS $AOSP_FLAGS " \
--extra-ldflags="-L$SYSROOT/usr/lib " \
--enable-shared \
--enable-nonfree \
--enable-encoder=aac \
--enable-decoder=aac \
--enable-decoder=aac_latm \
--enable-demuxer=aac \
--enable-parser=aac \
--enable-avcodec \
--enable-avdevice \
--enable-avfilter \
--enable-avformat \
--enable-avutil \
--enable-swresample \
--enable-swscale \
--disable-static \
--disable-outdevs \
--disable-ffprobe \
--disable-ffplay \
--disable-ffmpeg \
--disable-debug \
--disable-ffprobe \
--disable-ffplay \
--disable-ffmpeg \
--disable-symver \
--disable-stripping \

make clean
make -j4
make install

mv $FFMPEG_OUTPUT_DIR/armeabi-v7a/lib/* $FFMPEG_OUTPUT_DIR/armeabi-v7a