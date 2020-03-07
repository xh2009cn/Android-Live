package me.huaisu.image_knowledge.utils;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import me.huaisu.common.utils.MatrixUtils;

public class DCTUtils {

    public static RealMatrix dct2(byte[] signal, int N) {
        RealMatrix signalMatrix = MatrixUtils.toMatrix(signal, N);
        RealMatrix dctMatrix = getDCTMatrix(N);
        RealMatrix frequencyMatrix = dctMatrix.multiply(signalMatrix).multiply(dctMatrix.transpose());
        return frequencyMatrix;
    }

    public static RealMatrix idct2(RealMatrix frequencyMatrix, int N) {
        RealMatrix dctMatrix = getDCTMatrix(N);
        RealMatrix signalMatrix = dctMatrix.transpose().multiply(frequencyMatrix).multiply(dctMatrix);
        return signalMatrix;
    }

    private static RealMatrix getDCTMatrix(int N) {
        double matrixData[][] = new double[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                double factor = i == 0 ? Math.sqrt(1d / N) : Math.sqrt(2d / N);
                matrixData[i][j] = factor * Math.cos((2 * j + 1) * i * Math.PI * 0.5d / N);
            }
        }
        return new Array2DRowRealMatrix(matrixData);
    }
}
