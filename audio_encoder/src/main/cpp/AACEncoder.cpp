//
// Created by Administrator on 2020/2/23.
//

#include "AACEncoder.h"


AACEncoder::AACEncoder() {

}

AACEncoder::~AACEncoder() {

}

static void android_log_callback(void *ptr, int level, const char *fmt, va_list vl)
{
    switch (level) {
        case AV_LOG_VERBOSE:
            LOGV(fmt, vl);
            break;
        case AV_LOG_DEBUG:
            LOGD(fmt, vl);
            break;
        case AV_LOG_INFO:
            LOGI(fmt, vl);
            break;
        case AV_LOG_WARNING:
            LOGW(fmt, vl);
            break;
        case AV_LOG_ERROR:
            LOGE(fmt, vl);
            break;
    }
}

int AACEncoder::initCodec() {
    pCodec = avcodec_find_encoder_by_name("libfdk_aac");
    if (!pCodec) {
        LOGE("Codec not found\n");
        return -1;
    }
    pCodecContext = avcodec_alloc_context3(pCodec);
    if (!pCodecContext) {
        LOGE("Codec context alloc fail\n");
        return -1;
    }

    pCodecContext->codec_id = AV_CODEC_ID_AAC;
    pCodecContext->codec_type = AVMEDIA_TYPE_AUDIO;
    pCodecContext->sample_fmt = AV_SAMPLE_FMT_S16;
    pCodecContext->sample_rate = 44100;
    pCodecContext->channel_layout = AV_CH_LAYOUT_STEREO;
    pCodecContext->channels = av_get_channel_layout_nb_channels(pCodecContext->channel_layout);
    pCodecContext->bit_rate = 96000;

    if (avcodec_open2(pCodecContext, pCodec, NULL) < 0) {
        LOGE("Can't open codec\n");
        return -1;
    }
    return 0;
}

int AACEncoder::initAudioStream(const char* aac_file) {
    avformat_alloc_output_context2(&pFormatContext, NULL, NULL, aac_file);
    if (avio_open(&pFormatContext->pb, aac_file, AVIO_FLAG_READ_WRITE) < 0) {
        LOGE("Could't open output file\n");
        return -1;
    }
    audioStream = avformat_new_stream(pFormatContext, pCodec);
    if (audioStream == NULL) {
        LOGE("Could't create stream\n");
        return -1;
    }
    param = avcodec_parameters_alloc();
    ret = avcodec_parameters_from_context(param, pCodecContext);
    if (ret < 0) {
        LOGE("create parameters fail\n");
        return -1;
    }
    audioStream->codecpar = param;
    return 0;
}

 /**
  * ffmpeg一帧有1024个采样点，即pCodecContext->frame_size=1024
  *
  * 双声道，AV_SAMPLE_FMT_S16采样格式的数据方式存储如下：
  * LRLRLRLRLRLRLRLRLRLRLRLRLRLRLRLRLRLRLRLRLRLRLRLRLRLRLRR……
  * 所有数据存在data[0]，大小为1024 * 2(每个采样点占2字节) * 2(双声道) =4096
  *
  * 双声道，AV_SAMPLE_FMT_FLTP采样格式的数据方式存储如下：
  * LLLLLLLLLLLLLLLLLLLLLLLLLLRRRRRRRRRRRRRRRRRRRRRRRRRRRR……
  * 左声道数据存在data[0]，大小为1024 * 4(每个采样点占4字节)=4096
  * 右声道数据存在data[1]，大小为1024 * 4(每个采样点占4字节)=4096
  *
  * pFrame->linesize[0]，表示data[0]数组的长度
  * av_samples_get_buffer_size返回一帧的数据长度：
  * 双声道、AV_SAMPLE_FMT_S16长度为4096
  * 双声道，AV_SAMPLE_FMT_FLTP长度为8192
  */
int AACEncoder::initAudioFrame() {
    ret = av_samples_alloc_array_and_samples(&src_data, &src_linesize, pCodecContext->channels,
            pCodecContext->frame_size, pCodecContext->sample_fmt, 0);
    if (ret < 0) {
        LOGE("Could not allocate source samples\n");
        return -1;
    }
    src_bufsize = av_samples_get_buffer_size(&src_linesize, pCodecContext->channels,
            pCodecContext->frame_size, pCodecContext->sample_fmt, 1);
    pFrame = av_frame_alloc();
    pFrame->nb_samples = pCodecContext->frame_size;
    pFrame->format = pCodecContext->sample_fmt;
    pFrame->channels = pCodecContext->channels;
    pFrame->channel_layout = pCodecContext->channel_layout;
    pFrame->linesize[0] = src_linesize;
    pFrame->sample_rate = pCodecContext->sample_rate;
    return 0;
}

int AACEncoder::encode(const char* pcm_file, const char* aac_file)
{
    //打印ffmpeg系统日志，方便排查问题
    av_log_set_level(AV_LOG_VERBOSE);
    av_log_set_callback(android_log_callback);

    // 初始化编码器
    if (initCodec() < 0) {
        return -1;
    }
    // 创建AVStream
    if (initAudioStream(aac_file) < 0) {
        return -1;
    }
    // 写入aac文件头
    avformat_write_header(pFormatContext, NULL);
    // 初始化AVFrame，存放原始音频数据
    if (initAudioFrame() < 0) {
        return -1;
    }
    // 初始化AVPacket，存放编码后的aac数据
    pkt = av_packet_alloc();
    if (!pkt) {
        LOGE("could not allocate the packet\n");
        return -1;
    }

    FILE* fp_in = fopen(pcm_file, "rb");
    if (!fp_in) {
        LOGE("Can't open pcm input file\n");
        return -1;
    }
    int pts = 0;
    for (;;)
    {
        // 每次从pcm文件读取一帧数据
        if ((ret = fread(src_data[0], 1, src_bufsize, fp_in)) <= 0) {
            LOGE("Fail to read buf from input file\n");
            return -1;
        }
        else if (feof(fp_in)) {
            LOGE("End of input file\n");
            break;
        }
        // 设置当前帧的显示位置
        pFrame->pts = pts;
        pts++;
        // 将读到的帧数据赋值给AVFrame
        pFrame->data[0] = src_data[0];
        // 将AVFrame发送到编码器进行编码
        ret = avcodec_send_frame(pCodecContext, pFrame);
        if (ret < 0) {
            LOGE("Error sending the frame to the encoder\n");
            return -1;
        }
        while (ret >= 0) {
            // 得到编码后的AVPacket
            ret = avcodec_receive_packet(pCodecContext, pkt);
            if (ret == AVERROR(EAGAIN) || ret == AVERROR_EOF) {
//                LOGD("Error encoding audio frame %d\n", ret);
                continue;
            } else if (ret < 0) {
                LOGE("Error encoding audio frame %d\n", ret);
                return -1;
            }
//            LOGD("Success encode frame[%d] size:%d\n", frame_cnt, pkt->size);
            frame_cnt++;
            pkt->stream_index = audioStream->index;
            // 将编码后的AVPacket数据写入aac文件
            ret = av_interleaved_write_frame(pFormatContext, pkt);
            av_packet_unref(pkt);
            if (ret < 0) {
                LOGE("Error write frame to output file,err code=%d", ret);
                return -1;
            }
        }
    }

    //写入文件尾
    av_write_trailer(pFormatContext);

    fclose(fp_in);

    avcodec_close(pCodecContext);
    av_free(pCodecContext);
    av_free(&pFrame->data[0]);
    av_frame_free(&pFrame);
    return 0;
}
