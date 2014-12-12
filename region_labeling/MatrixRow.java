import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class MatrixRow implements Runnable {

    CyclicBarrier barrier;
    CyclicBarrier barrier2;
    int[][] origMatrix;
    AtomicInteger[][] labelMatrix;
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

    public MatrixRow(int[][] origMatrix, AtomicInteger[][] labelMatrix, int colSize,
                     int rowSize, int tID, CyclicBarrier barrier, CyclicBarrier barrier2,
                     AtomicBoolean globalChange,AtomicBoolean localChange) {
        this.origMatrix = origMatrix;
        this.labelMatrix = labelMatrix;
        this.globalChange = globalChange;
        this.localChange = localChange;
        this.colSize = colSize;
        this.rowSize = rowSize;
        this.tID = tID;
        this.barrier = barrier;
        this.barrier2 = barrier2;
    }

    @Override
    public void run() {
        while (localChange.get()) {
            for (int col = 0; col < colSize; col++) {
                AtomicInteger maxLabel = new AtomicInteger(findNeighborMaxLabel(origMatrix, labelMatrix, col, rowSize, colSize));
                if (labelMatrix[tID][col].get() < maxLabel.get()) {
                    globalChange.set(true);
                }
                labelMatrix[tID][col].set(Integer.max(labelMatrix[tID][col].get(), maxLabel.get()));
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

            try {
                barrier2.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Find the maximum label for a given point in the matrix
     *
     * @param origMatrix  the original matrix
     * @param labelMatrix the matrix with all labels
     * @param col         the column number
     * @param rowSize     the number of rows
     * @param colSize     the number of columns
     * @return the maximum label of a given point in the Matrix
     */
    private int findNeighborMaxLabel(int[][] origMatrix, AtomicInteger[][] labelMatrix, int col, int rowSize, int colSize) {
        ArrayList<Integer> neighbors = new ArrayList<Integer>();
        int pixelVal = origMatrix[tID][col];
        if (tID == 0) {
            if (col == 0) {
                checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID + 1, col, pixelVal);
                checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID, col + 1, pixelVal);
            } else if (col == colSize - 1) {
                checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID + 1, col, pixelVal);
                checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID, col - 1, pixelVal);

            } else {
                checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID + 1, col, pixelVal);
                checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID, col - 1, pixelVal);
                checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID, col + 1, pixelVal);
            }
        } else if (tID == rowSize - 1) {
            if (col == 0) {
                checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID - 1, col, pixelVal);
                checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID, col + 1, pixelVal);

            } else if (col == colSize - 1) {
                checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID - 1, col, pixelVal);
                checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID, col - 1, pixelVal);

            } else {
                checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID - 1, col, pixelVal);
                checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID, col - 1, pixelVal);
                checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID, col + 1, pixelVal);
            }
        } else if (col == 0) {
            checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID - 1, col, pixelVal);
            checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID, col + 1, pixelVal);
            checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID + 1, col, pixelVal);
        } else if (col == colSize - 1) {
            checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID - 1, col, pixelVal);
            checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID, col - 1, pixelVal);
            checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID + 1, col, pixelVal);
        } else {
            checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID - 1, col, pixelVal);
            checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID + 1, col, pixelVal);
            checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID, col - 1, pixelVal);
            checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID, col + 1, pixelVal);
        }

        if (neighbors.size() == 0) {
            return -1;
        } else {
            return Collections.max(neighbors);
        }
    }

    /**
     * Check if a neighbour should be included in the list of neighbors of a point in the matrix
     * If the values in the original matrix are the same it will be included
     * otherwise it wont
     *
     * @param origMatrix    the original matrix
     * @param labelMatrix   the matrix with labels
     * @param neighborsList the list of all neighbors of a point
     * @param row           the row coordinate
     * @param col           the column coordinate
     * @param pixelVal      the number of pixel in the original Matrix
     */
    private void checkNeighborEquality(int[][] origMatrix, AtomicInteger[][] labelMatrix, ArrayList<Integer> neighborsList, int row, int col, int pixelVal) {
        if (origMatrix[row][col] == pixelVal) {
            neighborsList.add(labelMatrix[row][col].get());
        }
    }
}
