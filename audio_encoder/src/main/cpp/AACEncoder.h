//
// Created by Administrator on 2020/2/23.
//

#ifndef ANDROID_LIVE_AACENCODER_H
#define ANDROID_LIVE_AACENCODER_H

#ifdef __cplusplus
extern "C" {
#endif

#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#ifdef __cplusplus
}
#endif

#include "AndroidLog.h"

class AACEncoder {
private:
    uint8_t** src_data = NULL;//一帧的数据，是个二位数组
    int src_linesize;
    int src_bufsize;//一帧数据的长度

    AVFormatContext* pFormatContext;
    AVStream* audioStream;
    AVCodecParameters* param;
    AVCodecContext* pCodecContext;
    AVCodec* pCodec;
    AVFrame* pFrame;
    int frame_cnt = 0;
    AVPacket *pkt;
    int ret;

    int initCodec();
    int initAudioStream(const char* aac_file);
    int initAudioFrame();
public:
    AACEncoder();
    ~AACEncoder();
    int encode(const char *pcm_file, const char *aac_file);
};


#endif //ANDROID_LIVE_AACENCODER_H
