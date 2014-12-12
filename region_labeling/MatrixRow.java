import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

public class MatrixRow implements Runnable {

    CyclicBarrier barrier;
    int[][] origMatrix;
    int[][] labelMatrix;
    AtomicBoolean globalChange;
    AtomicBoolean localChange;
    int colSize;
    int rowSize;
    int tID;

    /**
     * The constructor that will init a frame so that the labels can be calculated
     *
     * @param origMatrix   the original Matrix with pixel values
     * @param labelMatrix  the Matrix with the labels
     * @param colSize      the number of columns
     * @param rowSize      the number of rows
     * @param tID          the thread id or the row number
     * @param barrier      the common barrier
     * @param globalChange the condition that controls whether or not the label matrix changed at all
     */

    public MatrixRow(int[][] origMatrix, int[][] labelMatrix, int colSize,
                     int rowSize, int tID, CyclicBarrier barrier,
                     AtomicBoolean globalChange,AtomicBoolean localChange) {
        this.origMatrix = origMatrix;
        this.labelMatrix = labelMatrix;
        this.globalChange = globalChange;
        this.localChange = localChange;
        this.colSize = colSize;
        this.rowSize = rowSize;
        this.tID = tID;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        int maxLabel;
        int pixel;
        while (localChange.get()) {
            for (int col = 0; col < colSize; col++) {
                pixel = origMatrix[tID][col];
                maxLabel = labelMatrix[tID][col];
                if (tID - 1 >= 0) {
                    if (labelMatrix[tID - 1][col] > maxLabel && origMatrix[tID - 1][col] == pixel) {
                        maxLabel = labelMatrix[tID - 1][col];
                    }
                }

                if (col - 1 >= 0) {
                    if (labelMatrix[tID][col - 1] > maxLabel && origMatrix[tID][col - 1] == pixel) {
                        maxLabel = labelMatrix[tID][col - 1];
                    }
                }

                if (tID + 1 < rowSize) {
                    if (labelMatrix[tID + 1][col] > maxLabel && origMatrix[tID + 1][col] == pixel) {
                        maxLabel = labelMatrix[tID + 1][col];
                    }
                }

                if (col + 1 < colSize) {
                    if (labelMatrix[tID][col + 1] > maxLabel && origMatrix[tID][col + 1] == pixel) {
                        maxLabel = labelMatrix[tID][col + 1];
                    }
                }
                if (labelMatrix[tID][col] < maxLabel) {
                    labelMatrix[tID][col] = maxLabel;
                    globalChange.set(true);
                }
            }

            try {
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }

            if (tID == 0) {
                localChange.set(false);
                if (globalChange.get()) {
                    localChange.set(true);
                }
                globalChange.set(false);
            }

            try{
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }

    }
}
