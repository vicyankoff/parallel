import java.util.concurrent.atomic.AtomicBoolean;

public class Updater {

    AtomicBoolean[] localGo;

    /**
     * Construct the corresponding booleans for all threds
     * @param row
     */
    public Updater(int row) {
        localGo = new AtomicBoolean[row];
        for (int i = 0; i < localGo.length; i++) {
            localGo[i] = new AtomicBoolean(true);
        }
    }


    public void updateCond(boolean val) {
        for (int i = 0; i < localGo.length; i++) {
            localGo[i].set(val);
        }
    }

    public boolean get(int val) {
        return localGo[val].get();
    }

    public void set(int val, boolean cond) {
        localGo[val].set(cond);
    }
}
