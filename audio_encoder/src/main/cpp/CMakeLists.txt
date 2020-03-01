cmake_minimum_required(VERSION 3.4.1)

#file(GLOB native_files "src/main/cpp/*.cpp")
file(GLOB native_files "*.cpp")
add_library(aac_encoder

             SHARED

             ${native_files} )

find_library( # Sets the name of the path variable.
              log-lib

              log )

#引入依赖的so
set(LIBFFMPEG_DIR ${CMAKE_CURRENT_SOURCE_DIR}/../../../../ffmpeg_libs)
set(FFMPEG_EXTERNAL_DIR ${CMAKE_CURRENT_SOURCE_DIR}/../../../../ffmpeg_external)

#import fdk-aac
add_library(fdk-aac
        SHARED
        IMPORTED)
set_target_properties(fdk-aac
        PROPERTIES IMPORTED_LOCATION
        ${FFMPEG_EXTERNAL_DIR}/libfdk-aac/${ANDROID_ABI}/libfdk-aac.so)

#import avcodec
add_library(avcodec
           SHARED
           IMPORTED)
set_target_properties(avcodec
                    PROPERTIES IMPORTED_LOCATION
                    ${LIBFFMPEG_DIR}/${ANDROID_ABI}/libavcodec.so)

#import avdevice
add_library(avdevice
           SHARED
           IMPORTED)
set_target_properties(avdevice
                    PROPERTIES IMPORTED_LOCATION
                    ${LIBFFMPEG_DIR}/${ANDROID_ABI}/libavdevice.so)

#import avfilter
add_library(avfilter
           SHARED
           IMPORTED)
set_target_properties(avfilter
                    PROPERTIES IMPORTED_LOCATION
                    ${LIBFFMPEG_DIR}/${ANDROID_ABI}/libavfilter.so)

#import avformat
add_library(avformat
        SHARED
        IMPORTED)
set_target_properties(avformat
        PROPERTIES IMPORTED_LOCATION
        ${LIBFFMPEG_DIR}/${ANDROID_ABI}/libavformat.so)

#import avutil
add_library(avutil
        SHARED
        IMPORTED)
set_target_properties(avutil
        PROPERTIES IMPORTED_LOCATION
        ${LIBFFMPEG_DIR}/${ANDROID_ABI}/libavutil.so)

#import swresample
add_library(swresample
        SHARED
        IMPORTED)
set_target_properties(swresample
        PROPERTIES IMPORTED_LOCATION
        ${LIBFFMPEG_DIR}/${ANDROID_ABI}/libswresample.so)

#import swscale
add_library(swscale
        SHARED
        IMPORTED)
set_target_properties(swscale
        PROPERTIES IMPORTED_LOCATION
        ${LIBFFMPEG_DIR}/${ANDROID_ABI}/libswscale.so)

#引入依赖的头文件
include_directories(${LIBFFMPEG_DIR}/armeabi-v7a/include)

target_link_libraries(aac_encoder
        fdk-aac
        avcodec
        avdevice
        avfilter
        avformat
        avutil
        swresample
        swscale
        ${log-lib})