import java.util.ArrayList;

/**
 * Class that will calculate the common meeting times for 
 * each available time of the first person
 */
public class CheckTimes implements Runnable {

    int time;
    ArrayList<String> secondPersonTimes;
    ArrayList<String> thirdPersonTimes;


    /**
     * Constructor for each available hour of the first person 
     * that will determine if there are any common meeting times
     * @param time each available time of the first person
     * @param secondPersonTimes the available times of the second person
     * @param thirdPersonTimes the available times of the third person
    */
    public CheckTimes(int time, ArrayList<String> secondPersonTimes, ArrayList<String> thirdPersonTimes) {
        this.time = time;
        this.secondPersonTimes = secondPersonTimes;
        this.thirdPersonTimes = thirdPersonTimes;
    }

    @Override
    public void run() {
        for (int i = 0; i < secondPersonTimes.size(); i++) {
            if (time == Integer.parseInt(secondPersonTimes.get(i))) {
                for (int j = 0; j < thirdPersonTimes.size(); j++) {
                    if (time == Integer.parseInt(thirdPersonTimes.get(j))) {
                        System.out.println(time + " is a common meeting time.");
                    }
                }
            }
        }
    }
}
