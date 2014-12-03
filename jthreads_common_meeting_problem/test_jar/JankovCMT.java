import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
    A program to calculate the common meeting time problem
    using JThreads for better efficiency
    by Viktor Jankov
*/

public class JankovCMT {

    // The times available for each person
    private static ArrayList<String> firstPersonTimes;
    private static ArrayList<String> secondPersonTimes;
    private static ArrayList<String> thirdPersonTimes;

    public static void main(String[] args) {

        firstPersonTimes = new ArrayList<String>();
        secondPersonTimes = new ArrayList<String>();
        thirdPersonTimes = new ArrayList<String>();
        List<String> availTimes;

        // Read the file and parse the input into the hours for each person
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
}