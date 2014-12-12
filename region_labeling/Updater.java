import java.util.ArrayList;

public class Updater {

    ArrayList<MatrixRow> threadArray;
    boolean[] localGo;
    public Updater(int row) {
        localGo = new boolean[row];
        for (int i = 0; i < localGo.length; i++) {
            localGo[i] = true;
        }
    }


    public void updateCond(boolean val) {
        for (int i = 0; i < localGo.length; i++) {
            localGo[i] = val;
        }
    }

    public boolean get(int val) {
        return localGo[val];
    }
}
