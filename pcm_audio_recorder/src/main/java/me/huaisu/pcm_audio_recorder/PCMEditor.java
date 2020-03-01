package me.huaisu.pcm_audio_recorder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import me.huaisu.common.utils.FileUtils;
import me.huaisu.common.utils.IOUtils;
import me.huaisu.common.utils.Util;

public class PCMEditor {

    /**
     * 将pcm文件的音量减半，保存到原文件路径 + .half
     */
    public static boolean halfVolume(File pcmFile) {
        byte[] audioData = FileUtils.toByteArray(pcmFile);
        if (audioData == null) {
            return false;
        }
        FileOutputStream fos = null;
        try {
            File output = new File(pcmFile.getParentFile(), pcmFile.getName() + ".half");
            fos = new FileOutputStream(output);
            int length = audioData.length;
            for (int i = 0; i < length - 2; i += 2) {
                int volume = Util.toUnsignedInt16(audioData[i], audioData[i + 1]);
                volume = volume / 2;
                byte[] bytes = Util.unsignedInt16ToByteArray(volume);
                audioData[i] = bytes[0];
                audioData[i + 1] = bytes[1];
            }
            fos.write(audioData);
            fos.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fos);
        }
        return false;
    }

    /**
     * 分离pcm文件的左右声道
     */
    public static boolean splitChannel(File pcmFile) {
        byte[] audioData = FileUtils.toByteArray(pcmFile);
        if (audioData == null) {
            return false;
        }
        FileOutputStream fosLeft = null;
        FileOutputStream fosRight = null;
        try {
            File leftOutput = new File(pcmFile.getParentFile(), pcmFile.getName() + ".left");
            fosLeft = new FileOutputStream(leftOutput);
            File rightOutput = new File(pcmFile.getParentFile(), pcmFile.getName() + ".right");
            fosRight  =new FileOutputStream(rightOutput);

            ByteArrayOutputStream bosLeft = new ByteArrayOutputStream((int) (pcmFile.length() / 2));
            ByteArrayOutputStream bosRight = new ByteArrayOutputStream((int) (pcmFile.length() / 2));

            int len = audioData.length;
            for (int i = 0; i < len - 4; i += 4) {
                bosLeft.write(audioData, i, 2);
                bosRight.write(audioData, i + 2, 2);
            }
            fosLeft.write(bosLeft.toByteArray());
            fosRight.write(bosRight.toByteArray());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fosLeft);
            IOUtils.closeQuietly(fosRight);
        }
        return false;
    }

    /**
     * pcm速度提升一倍
     */
    public static boolean doubleSpeed(File pcmFile) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(pcmFile);
            File output = new File(pcmFile.getParentFile(), pcmFile.getName() + ".speed");
            fos = new FileOutputStream(output);

            ByteArrayOutputStream bos = new ByteArrayOutputStream((int) pcmFile.length() / 2);
            byte[] buffer = new byte[1024 * 1024];
            int len = 0;
            while ( (len = fis.read(buffer)) != -1) {
                for (int i = 0; i < len - 4; i += 4) {
                    int index = i / 4;
                    if (index % 2 == 0) {
                        bos.write(buffer, i, 4);
                    }
                }
            }
            fos.write(bos.toByteArray());
            fos.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(fos);
        }
        return false;
    }
}
