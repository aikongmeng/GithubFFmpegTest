#!/bin/bash

NDK=/Users/toge/Desktop/ndk/android-ndk-r10e

SYSROOT=$NDK/platforms/android-19/arch-arm/

TOOLCHAIN=$NDK/toolchains/arm-linux-androideabi-4.8/prebuilt/darwin-x86_64

PREFIX=""

function build_one

{

PREFIX=$(pwd)/android/$CPU 

./configure --prefix=$PREFIX --enable-static --enable-memalign-hack --disable-doc --disable-ffplay --disable-ffprobe --disable-ffserver --cross-prefix=$TOOLCHAIN/bin/arm-linux-androideabi- --target-os=linux --arch=arm --enable-cross-compile --sysroot=$SYSROOT --extra-cflags="-Os -fpic $ADDI_CFLAGS" --extra-ldflags="$ADDI_LDFLAGS" $ADDITIONAL_CONFIGURE_FLAG

make clean

make -j4

make install

}

#-----For neon------
CPU=arm_neon

ADDI_CFLAGS="-marm -march=armv7-a -mfpu=neon -mfloat-abi=softfp -mvectorize-with-neon-quad"

ADDI_LDFLAGS="-Wl,--fix-cortex-a8"

build_one

sudo mv $PREFIX/bin/ffmpeg $PREFIX/bin/libffmpeg.so

#-----For general-----
CPU=arm

ADDI_CFLAGS="-marm"

ADDI_LDFLAGS=""

build_one

sudo mv $PREFIX/bin/ffmpeg $PREFIX/bin/libffmpeg.so

#---------------------

sudo chmod 777 -R $(pwd)

echo "Welcome to my github: https://github.com/aikongmeng"
