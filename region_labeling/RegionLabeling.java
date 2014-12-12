import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

public class RegionLabeling {
    private static int row;
    private static int col;


    public static void main(String[] args) throws IOException {
        int[][] origMatrix = initOrigMatrix(args[0]);
        int[][] labelMatrix = initLabelMatrix(row, col);
        Updater updater = new Updater(row);
        MatrixRow[] matrixRows = new MatrixRow[row];
        CyclicBarrier barrier = new CyclicBarrier(row);
        AtomicBoolean globalChange = new AtomicBoolean(false);
        AtomicBoolean localChange = new AtomicBoolean(true);

        for (int i = 0; i < row; i++) {
            matrixRows[i] = new MatrixRow(origMatrix, labelMatrix,
                    col, row, i, barrier,
                    globalChange,localChange, updater);
            new Thread(matrixRows[i]).start();
        }

        printMatrix(origMatrix, row, col, "Regular Matrix");
        System.out.println();
        printMatrix(labelMatrix, row, col, "Label Matrix");
    }


    private static int[][] initOrigMatrix(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String[] matrixSize = reader.readLine().split(" ");
        row = Integer.parseInt(matrixSize[0]);
        col = Integer.parseInt(matrixSize[1]);
        int[][] origMatrix = new int[row][col];
        String line;
        int matrixRow = 0;
        while ((line = reader.readLine()) != null) {
            String[] numbers = line.split(" ");
            for (int matrixCol = 0; matrixCol < numbers.length; matrixCol++) {
                origMatrix[matrixRow][matrixCol] = Integer.parseInt(numbers[matrixCol]);
            }
            matrixRow++;
        }
        return origMatrix;
    }


    private static int[][] initLabelMatrix(int row, int col) {
        int[][] labelMatrix = new int[row][col];
        int val = 0;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                labelMatrix[i][j] = val++;
            }
        }
        return labelMatrix;
    }

    private static void printMatrix(int[][] matrix, int row, int col, String matrixName) {
        System.out.println(matrixName);
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (matrix[i][j] < 10) {
                    System.out.print(" " + matrix[i][j] + " ");
                } else {
                    System.out.print(matrix[i][j] + " ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }
}
