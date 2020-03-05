package me.huaisu.image_knowledge.utils;

public class IDCT2Transformer {

    public static int[] transform(float[] dct, int N) {
        int[] data = new int[N * N];
        for (int u = 0; u < N; u++) {
            for (int v = 0; v < N; v++) {
                float signal = idct2(dct, u, v, N);
                data[v * N + u] = Math.round(signal);
            }
        }
        return data;
    }

    private static float idct2(float[] dct, int i, int j, int N) {
        float signal = 0;
        for (int u = 0; u < N; u++) {
            for (int v = 0; v < N; v++) {
                float frequency = dct[v * N + u];
                signal += orthogonalFactor(u, N) * orthogonalFactor(v, N) * frequency * frequencyToSignal(i, j, u, v, N);
            }
        }
        return signal;
    }

    private static float orthogonalFactor(int u, int N) {
        if (u == 0) {
            return (float) Math.sqrt(1d / N);
        } else {
            return (float) Math.sqrt(2d / N);
        }
    }

    private static float frequencyToSignal(int i, int j, int u, int v, int N) {
        float cosi = (float) Math.cos((i + 0.5f) * Math.PI * u / N);
        float cosj = (float) Math.cos((j + 0.5f) * Math.PI * v / N);
        return cosi * cosj;
    }
}
