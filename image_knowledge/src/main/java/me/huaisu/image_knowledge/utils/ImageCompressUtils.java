package me.huaisu.image_knowledge.utils;

public class ImageCompressUtils {

    public static final int BLOCK_SIZE = 8;
    public static final int BLOCK_DATA_SIZE = BLOCK_SIZE * BLOCK_SIZE;

    public static final int[] QUANTIZATION_MATRIX = new int[] {
            16,  11,  10,  16,  24,  40,  51,  61,
            12,  12,  14,  19,  26,  58,  60,  55,
            14,  13,  16,  24,  40,  57,  69,  56,
            14,  17,  22,  29,  51,  87,  80,  62,
            18,  22,  37,  56,  68, 109, 103,  77,
            24,  35,  55,  64,  81, 104, 113,  92,
            49,  64,  78,  87, 103, 121, 120, 101,
            72,  92,  95,  98, 112, 100, 103,  99
    };

    public static double[] quantization(double[] block) {
        int len = block.length;
        double[] quantizationBlock = new double[len];
        for (int i = 0; i < len; i++) {
            quantizationBlock[i] = Math.round(block[i] / QUANTIZATION_MATRIX[i]) * QUANTIZATION_MATRIX[i];
        }
        return quantizationBlock;
    }

    public static byte[][] splitToBlocks(byte[] data, int N) {
        int len = N * N;
        int blockCountBySide = N / BLOCK_SIZE;
        byte[][] blocks = new byte[len / BLOCK_DATA_SIZE][];
        // 每次按顺序copy 8个字节
        for (int i = 0; i < len; i += BLOCK_SIZE) {
            int row = i / N;
            int rowInBlock = row / BLOCK_SIZE;
            int columnInBlock = (i / BLOCK_SIZE) % blockCountBySide;
            int blockIndex = rowInBlock * blockCountBySide + columnInBlock;
            byte[] block = blocks[blockIndex];
            if (block == null) {
                block = new byte[BLOCK_DATA_SIZE];
                blocks[blockIndex] = block;
            }
            int rowOfBlock = row % BLOCK_SIZE;
            System.arraycopy(data, i, block, rowOfBlock * BLOCK_SIZE, BLOCK_SIZE);
        }
        return blocks;
    }

    public static double[] concatBlocks(double[][] blocks, int N) {
        double[] data = new double[N * N];
        int len = N * N;
        int blockCountBySide = N / BLOCK_SIZE;
        // 每次按顺序copy 8个字节
        for (int i = 0; i < len; i += BLOCK_SIZE) {
            int row = i / N;
            int rowInBlock = row / BLOCK_SIZE;
            int columnInBlock = (i / BLOCK_SIZE) % blockCountBySide;
            int blockIndex = rowInBlock * blockCountBySide + columnInBlock;
            double[] block = blocks[blockIndex];
            int rowOfBlock = row % BLOCK_SIZE;
            System.arraycopy(block, rowOfBlock * BLOCK_SIZE,  data, i, BLOCK_SIZE);
        }
        return data;
    }

    public static byte[] concatBlocks(byte[][] blocks, int N) {
        byte[] data = new byte[N * N];
        int len = N * N;
        int blockCountBySide = N / BLOCK_SIZE;
        // 每次按顺序copy 8个字节
        for (int i = 0; i < len; i += BLOCK_SIZE) {
            int row = i / N;
            int rowInBlock = row / BLOCK_SIZE;
            int columnInBlock = (i / BLOCK_SIZE) % blockCountBySide;
            int blockIndex = rowInBlock * blockCountBySide + columnInBlock;
            byte[] block = blocks[blockIndex];
            int rowOfBlock = row % BLOCK_SIZE;
            System.arraycopy(block, rowOfBlock * BLOCK_SIZE,  data, i, BLOCK_SIZE);
        }
        return data;
    }
}
