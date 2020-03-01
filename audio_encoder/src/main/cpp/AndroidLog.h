//
// Created by Administrator on 2020/2/23.
//

#ifndef ANDROID_LIVE_ANDROIDLOG_H
#define ANDROID_LIVE_ANDROIDLOG_H

#endif //ANDROID_LIVE_ANDROIDLOG_H

#include <android/log.h>

#define TAG "AndroidLive"

#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)