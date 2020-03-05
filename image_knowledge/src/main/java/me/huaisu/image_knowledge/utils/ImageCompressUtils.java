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

    public static float[] quantization(float[] block) {
        int len = block.length;
        float[] quantizationBlock = new float[len];
        for (int i = 0; i < len; i++) {
            quantizationBlock[i] = Math.round(block[i] / QUANTIZATION_MATRIX[i]) * QUANTIZATION_MATRIX[i];
        }
        return quantizationBlock;
    }

    /**
     * 将图片分割成一个个8x8的方块
     */
    public static int[][] splitToBlocks(int[] data, int N) {
        int len = N * N;
        int blockCountBySide = N / BLOCK_SIZE;
        int[][] blocks = new int[len / BLOCK_DATA_SIZE][];
        // 每次按顺序copy 8个字节
        for (int i = 0; i < len; i += BLOCK_SIZE) {
            int row = i / N;
            int rowInBlock = row / BLOCK_SIZE;
            int columnInBlock = (i / BLOCK_SIZE) % blockCountBySide;
            int blockIndex = rowInBlock * blockCountBySide + columnInBlock;
            int[] block = blocks[blockIndex];
            if (block == null) {
                block = new int[BLOCK_DATA_SIZE];
                blocks[blockIndex] = block;
            }
            int rowOfBlock = row % BLOCK_SIZE;
            System.arraycopy(data, i, block, rowOfBlock * BLOCK_SIZE, BLOCK_SIZE);
        }
        return blocks;
    }

    public static float[] concatBlocks(float[][] blocks, int N) {
        float[] data = new float[N * N];
        int len = N * N;
        int blockCountBySide = N / BLOCK_SIZE;
        // 每次按顺序copy 8个字节
        for (int i = 0; i < len; i += BLOCK_SIZE) {
            int row = i / N;
            int rowInBlock = row / BLOCK_SIZE;
            int columnInBlock = (i / BLOCK_SIZE) % blockCountBySide;
            int blockIndex = rowInBlock * blockCountBySide + columnInBlock;
            float[] block = blocks[blockIndex];
            int rowOfBlock = row % BLOCK_SIZE;
            System.arraycopy(block, rowOfBlock * BLOCK_SIZE,  data, i, BLOCK_SIZE);
        }
        return data;
    }

    public static int[] concatBlocks(int[][] blocks, int N) {
        int[] data = new int[N * N];
        int len = N * N;
        int blockCountBySide = N / BLOCK_SIZE;
        // 每次按顺序copy 8个字节
        for (int i = 0; i < len; i += BLOCK_SIZE) {
            int row = i / N;
            int rowInBlock = row / BLOCK_SIZE;
            int columnInBlock = (i / BLOCK_SIZE) % blockCountBySide;
            int blockIndex = rowInBlock * blockCountBySide + columnInBlock;
            int[] block = blocks[blockIndex];
            int rowOfBlock = row % BLOCK_SIZE;
            System.arraycopy(block, rowOfBlock * BLOCK_SIZE,  data, i, BLOCK_SIZE);
        }
        return data;
    }
}
