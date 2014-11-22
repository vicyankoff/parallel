import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommonMeetingProblem {

    private static ArrayList<String> firstPersonTimes;
    private static ArrayList<String> secondPersonTimes;
    private static ArrayList<String> thirdPersonTimes;

    public static void main(String[] args) {

        firstPersonTimes = new ArrayList<String>();
        secondPersonTimes = new ArrayList<String>();
        thirdPersonTimes = new ArrayList<String>();
        List<String> availTimes;
        try {
            availTimes = Files.readAllLines(Paths.get(args[0]));
            Collections.addAll(firstPersonTimes, availTimes.get(0).split(" "));
            firstPersonTimes.remove(0);
            Collections.addAll(secondPersonTimes, availTimes.get(1).split(" "));
            secondPersonTimes.remove(0);
            Collections.addAll(thirdPersonTimes, availTimes.get(2).split(" "));
            thirdPersonTimes.remove(0);

            for (int i = 0; i < firstPersonTimes.size(); i++) {
                new Thread(new CheckTimes(Integer.parseInt(firstPersonTimes.get(i)), secondPersonTimes, thirdPersonTimes)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class CheckTimes implements Runnable {
        int time;
        ArrayList<String> secondPersonTimes = new ArrayList<String>();
        ArrayList<String> thirdPersonTimes = new ArrayList<String>();

        public CheckTimes(int time, ArrayList<String> secondPersonTimes, ArrayList<String> thirdPersonTimes) {
            this.time = time;
            this.secondPersonTimes = secondPersonTimes;
            this.thirdPersonTimes = thirdPersonTimes;
        }

        @Override
        public void run() {
            for (int i = 0; i < secondPersonTimes.size(); i++) {
                if( time == Integer.parseInt(secondPersonTimes.get(i))) {
                    for (int j = 0; j < thirdPersonTimes.size(); j++) {
                        if( time == Integer.parseInt(thirdPersonTimes.get(j))) {
                            System.out.println( time + " is a common meeting time.");
                        }
                    }
                }

            }
        }
    }
}
