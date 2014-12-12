import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A program to determine the common region labeling from a given Matrix
 * by Viktor Jankov
 */

public class RegionLabeling {
    private static int rowSize;
    private static int colSize;


    public static void main(String[] args) throws IOException {
        // Fill out the original matrix from a file
        int[][] origMatrix = initOrigMatrix(args[0]);
        // Init the label matrix
        AtomicInteger[][] labelMatrix = initLabelMatrix(rowSize, colSize);
        MatrixRow[] matrixRows = new MatrixRow[rowSize];
        CyclicBarrier barrier = new CyclicBarrier(rowSize);
        CyclicBarrier barrier2 = new CyclicBarrier(rowSize);
        AtomicBoolean globalChange = new AtomicBoolean(false);
        AtomicBoolean localChange = new AtomicBoolean(true);

        for (int i = 0; i < rowSize; i++) {
            matrixRows[i] = new MatrixRow(origMatrix, labelMatrix,
                    colSize, rowSize, i, barrier, barrier2,
                    globalChange,localChange);
        }

        for (int i = 0; i < matrixRows.length; i++) {
            new Thread(matrixRows[i]).start();
        }

        printMatrix(origMatrix, rowSize, colSize, "Regular Matrix");
        System.out.println();
        printLabelMatrix(labelMatrix, rowSize, colSize, "Label Matrix");
    }

    /**
     * Initialize the original matrix
     * @param filename the filename
     * @return the original matrix from the file
     * @throws IOException
     */
    private static int[][] initOrigMatrix(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String[] matrixSize = reader.readLine().split(" ");
        rowSize = Integer.parseInt(matrixSize[0]);
        colSize = Integer.parseInt(matrixSize[1]);
        int[][] origMatrix = new int[rowSize][colSize];
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

    /**
     * Init the label matrix
     * @param rowSize the number of rows
     * @param colSize the number of cols
     * @return
     */
    private static AtomicInteger[][] initLabelMatrix(int rowSize, int colSize) {
        AtomicInteger[][] labelMatrix = new AtomicInteger[rowSize][colSize];
        int val = 0;
        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < colSize; j++) {
                labelMatrix[i][j] = new AtomicInteger(val++);
            }
        }
        return labelMatrix;
    }

    /**
     * Print the pixel matrix
     * @param matrix the matrix to be printed
     * @param rowSize the number of rows
     * @param colSize the number of columns
     * @param matrixName the name of the matrix
     */
    private static void printMatrix(int[][] matrix, int rowSize, int colSize, String matrixName) {
        System.out.println(matrixName);
        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < colSize; j++) {
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

    /**
     * Print the label matrix
     * @param matrix the matrix to be printed
     * @param rowSize the number of rows
     * @param colSize the number of columns
     * @param matrixName the name of the matrix
     */
    private static void printLabelMatrix(AtomicInteger[][] matrix, int rowSize, int colSize, String matrixName) {
        System.out.println(matrixName);
        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < colSize; j++) {
                if (matrix[i][j].get() < 10) {
                    System.out.print(" " + matrix[i][j].get() + " ");
                } else {
                    System.out.print(matrix[i][j] + " ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }
}
