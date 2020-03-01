package me.huaisu.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileUtils {

    public static byte[] toByteArray(File file) {
        FileInputStream fis = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
        try {
            fis = new FileInputStream(file);
            byte[] buffer = new byte[1024 * 1024];
            int len = 0;
            while ( (len = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fis);
        }
        return null;
    }
}
