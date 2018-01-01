package teammates.client.scripts;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class StudentDataGenerator {

    private static final int LENGTH_OF_STUDENT_NAME = 8;
    private static final int LENGTH_OF_TEAM_SUFFIX = 3;
    private static final String RANDOM_ALLOWED_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz";

    private static int numberOfStudents;
    private static int numberOfTeams;

    private StudentDataGenerator() {
        // script, not meant to be instantiated
    }

    public static void main(String[] args) {

        exitIfParametersMissing(args);

        numberOfTeams = Integer.parseInt(args[1]);
        List<String> teamnames = generateTeamNames(numberOfTeams);

        numberOfStudents = Integer.parseInt(args[0]);
        List<String> lines = generateEnrollText(numberOfStudents, teamnames);

        print(lines);

    }

    private static void print(List<String> lines) {
        for (String line : lines) {
            System.out.println(line);
        }
    }

    private static void exitIfParametersMissing(String[] args) {
        if (args.length < 2) {
            System.out
                    .println("Command arguments are java generateStudentData <number of students> <number of teams>");
            System.exit(0);
        }
    }

    /**
     * Returns enroll lines, sorted by team name.
     */
    private static List<String> generateEnrollText(int numberOfStudents,
            List<String> teamnames) {
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < numberOfStudents; i++) {

            String name = generateRandomString(LENGTH_OF_STUDENT_NAME);

            String student = teamnames.get(new Random().nextInt(teamnames.size()))
                             + '|' + name + '|' + name + "@gmail.com";

            lines.add(student);
        }
        lines.sort(null);
        return lines;
    }

    private static List<String> generateTeamNames(int numberOfTeams) {
        ArrayList<String> teamnames = new ArrayList<>();
        for (int i = 0; i < numberOfTeams; i++) {
            String team = generateRandomString(LENGTH_OF_TEAM_SUFFIX);
            teamnames.add("Team " + team);
        }
        return teamnames;
    }

    private static String generateRandomString(int length) {
        StringBuilder name = new StringBuilder();

        for (int j = 0; j < length; j++) {
            name.append(RANDOM_ALLOWED_CHARS.charAt(new Random().nextInt(RANDOM_ALLOWED_CHARS.length())));
        }
        return name.toString();
    }
}
