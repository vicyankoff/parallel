import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommonMeetingProblem {

    public static void main(String[] args) {

        ArrayList<String> firstPersonTimes = new ArrayList<String>();
        ArrayList<String> secondPersonTimes = new ArrayList<String>();
        ArrayList<String> thirdPersonTimes = new ArrayList<String>();
        List<String> availTimes;
        try {
            availTimes = Files.readAllLines(Paths.get(args[0]));
            Collections.addAll(firstPersonTimes, availTimes.get(0).split(" "));
            Collections.addAll(secondPersonTimes, availTimes.get(1).split(" "));
            Collections.addAll(thirdPersonTimes, availTimes.get(2).split(" "));


        } catch (IOException e) {
            e.printStackTrace();
        }


        for (int i = 0; i < firstPersonTimes.size(); i++) {

            System.out.println(firstPersonTimes.get(i) + " ");
        }
        System.out.println();

        for (int i = 0; i < secondPersonTimes.size(); i++) {

            System.out.println(secondPersonTimes.get(i) + " ");
        }
        System.out.println();

        for (int i = 0; i < thirdPersonTimes.size(); i++) {

            System.out.println(thirdPersonTimes.get(i) + " ");
        }

    }


}
