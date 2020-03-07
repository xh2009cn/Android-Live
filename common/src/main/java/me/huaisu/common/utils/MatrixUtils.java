package me.huaisu.common.utils;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

public class MatrixUtils {

    public static RealMatrix toMatrix(double[] data, int N) {
        double[][] matrixData = new double[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                matrixData[i][j] = data[i * N + j];
            }
        }
        return new Array2DRowRealMatrix(matrixData);
    }

    public static RealMatrix toMatrix(byte[] data, int N) {
        double[][] matrixData = new double[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                matrixData[i][j] = data[i * N + j] & 0xFF;
            }
        }
        return new Array2DRowRealMatrix(matrixData);
    }

    public static RealMatrix toMatrix(int[] data, int N) {
        double[][] matrixData = new double[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                matrixData[i][j] = data[i * N + j];
            }
        }
        return new Array2DRowRealMatrix(matrixData);
    }

    public static byte[] getMatrixByteData(RealMatrix matrix) {
        int ROW = matrix.getRowDimension();
        int COLUMN = matrix.getColumnDimension();
        double[][] matrixData = matrix.getData();
        byte[] data = new byte[ROW * COLUMN];
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COLUMN; j++) {
                data[i * ROW + j] = NumberUtils.doubleToByte(matrixData[i][j]);
            }
        }
        return data;
    }

    public static int[] getMatrixIntData(RealMatrix matrix) {
        int ROW = matrix.getRowDimension();
        int COLUMN = matrix.getColumnDimension();
        double[][] matrixData = matrix.getData();
        int[] data = new int[ROW * COLUMN];
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COLUMN; j++) {
                data[i * ROW + j] = (int) matrixData[i][j];
            }
        }
        return data;
    }

    public static double[] getMatrixDoubleData(RealMatrix matrix) {
        int ROW = matrix.getRowDimension();
        int COLUMN = matrix.getColumnDimension();
        double[][] matrixData = matrix.getData();
        double[] data = new double[ROW * COLUMN];
        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COLUMN; j++) {
                data[i * ROW + j] = matrixData[i][j];
            }
        }
        return data;
    }
}
