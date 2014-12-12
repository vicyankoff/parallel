import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

public class MatrixRow implements Runnable {

    CyclicBarrier barrier;
    int[][] origMatrix;
    int[][] labelMatrix;
    AtomicBoolean globalChange;
    AtomicBoolean localGo;
    Updater updater;
    int colSize;
    int rowSize;
    int tID;

    public MatrixRow(int[][] origMatrix, int[][] labelMatrix, int colSize,
                     int rowSize, int tID, CyclicBarrier barrier,
                     AtomicBoolean globalChange, AtomicBoolean localChange, Updater updater) {
        this.origMatrix = origMatrix;
        this.labelMatrix = labelMatrix;
        this.globalChange = globalChange;
        this.colSize = colSize;
        this.rowSize = rowSize;
        this.tID = tID;
        this.barrier = barrier;
        this.localGo = localChange;
        this.updater = updater;
    }

    @Override
    public void run() {
        int max;

        while (localGo.get()) {
            for (int i = 0; i < colSize; i++) {
                max = labelMatrix[tID][i];
                if (tID - 1 >= 0) {
                    if (labelMatrix[tID - 1][i] > max && origMatrix[tID - 1][i] == origMatrix[tID][i]) {
                        max = labelMatrix[tID - 1][i];
                        globalChange.set(true);
                    }
                }

                if (tID + 1 < rowSize) {
                    if (labelMatrix[tID + 1][i] > max && origMatrix[tID + 1][i] == origMatrix[tID][i]) {
                        max = labelMatrix[tID + 1][i];
                        globalChange.set(true);
                    }
                }

                if (i - 1 >= 0) {
                    if (labelMatrix[tID][i - 1] > max && origMatrix[tID][i - 1] == origMatrix[tID][i]) {
                        max = labelMatrix[tID][i - 1];
                        globalChange.set(true);
                    }
                }

                if (i + 1 < colSize) {
                    if (labelMatrix[tID][i + 1] > max && origMatrix[tID][i + 1] == origMatrix[tID][i]) {
                        max = labelMatrix[tID][i + 1];
                        globalChange.set(true);
                    }
                }

                labelMatrix[tID][i] = max;
            }


            try {
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }


            if (tID == 0) {
                localGo.set(false);
                if (globalChange.get()) {
                    localGo.set(true);
                }
                globalChange.set(false);
            }

            try {
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }

        }
    }
//
//    @Override
//    public void run() {
//        while (localGo.get()) {
//            for (int col = 0; col < colSize; col++) {
//                int maxLabel = findNeighborMaxLabel(origMatrix, labelMatrix, col, rowSize, colSize);
//                if (labelMatrix[tID][col] < maxLabel) {
//                    globalChange.set(true);
//                }
//                labelMatrix[tID][col] = Integer.max(labelMatrix[tID][col], maxLabel);
//            }
//
//            try {
//                barrier.await();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (BrokenBarrierException e) {
//                e.printStackTrace();
//            }
//
//            if (tID == 0) {
//                localGo.set(false);
//                if (globalChange.get()) {
//                    localGo.set(true);
//                }
//            }
//            globalChange.set(false);
//
//            try {
//                barrier.await();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (BrokenBarrierException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private int findNeighborMaxLabel(int[][] origMatrix, int[][] labelMatrix, int j, int row, int col) {
//        ArrayList<Integer> neighbors = new ArrayList<Integer>();
//        int pixelVal = origMatrix[tID][j];
//        if (tID == 0) {
//            if (j == 0) {
//                checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID + 1, j, pixelVal);
//                checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID, j + 1, pixelVal);
//            } else if (j == col - 1) {
//                checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID + 1, j, pixelVal);
//                checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID, j - 1, pixelVal);
//
//            } else {
//                checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID + 1, j, pixelVal);
//                checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID, j - 1, pixelVal);
//                checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID, j + 1, pixelVal);
//            }
//        } else if (tID == row - 1) {
//            if (j == 0) {
//                checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID - 1, j, pixelVal);
//                checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID, j + 1, pixelVal);
//
//            } else if (j == col - 1) {
//                checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID - 1, j, pixelVal);
//                checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID, j - 1, pixelVal);
//
//            } else {
//                checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID - 1, j, pixelVal);
//                checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID, j - 1, pixelVal);
//                checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID, j + 1, pixelVal);
//            }
//        } else if (j == 0) {
//            checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID - 1, j, pixelVal);
//            checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID, j + 1, pixelVal);
//            checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID + 1, j, pixelVal);
//        } else if (j == col - 1) {
//            checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID - 1, j, pixelVal);
//            checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID, j - 1, pixelVal);
//            checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID + 1, j, pixelVal);
//        } else {
//            checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID - 1, j, pixelVal);
//            checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID + 1, j, pixelVal);
//            checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID, j - 1, pixelVal);
//            checkNeighborEquality(origMatrix, labelMatrix, neighbors, tID, j + 1, pixelVal);
//        }
//
//        if (neighbors.size() == 0) {
//            return -1;
//        } else {
//            return Collections.max(neighbors);
//        }
//    }
//
//    private void checkNeighborEquality(int[][] origMatrix, int[][] labelMatrix, ArrayList<Integer> neighborsList, int row, int col, int pixelVal) {
//        if (origMatrix[row][col] == pixelVal) {
//            neighborsList.add(labelMatrix[row][col]);
//        }
//    }
}
