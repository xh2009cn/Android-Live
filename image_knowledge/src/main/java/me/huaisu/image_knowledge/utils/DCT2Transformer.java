package me.huaisu.image_knowledge.utils;


public class DCT2Transformer {

    public static float[] transform(int[] data, int N) {
        float[] dct = new float[N * N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                float frequency = dct2(data, i, j, N);
                dct[j * N + i] = frequency;
            }
        }
        return dct;
    }

    private static float dct2(int[] data, int u, int v, int N) {
        float frequency = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                frequency += signalToFrequency(data[j * N + i], i, j, u, v, N);
            }
        }
        return (float) (orthogonalFactor(u, N) * orthogonalFactor(v, N) * frequency);
    }

    private static double orthogonalFactor(int frequency, int N) {
        if (frequency == 0) {
            return Math.sqrt(1d / N);
        } else {
            return Math.sqrt(2d / N);
        }
    }

    private static float signalToFrequency(int signal, int i, int j, int u, int v, int N) {
        float cosi = (float) Math.cos((i + 0.5f) * Math.PI * u / N);
        float cosj = (float) Math.cos((j + 0.5f) * Math.PI * v / N);
        return signal * cosi * cosj;
    }
}
