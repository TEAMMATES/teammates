package teammates.test.scripts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class generateStudentData {

	private static int LENGTH_OF_STUDENT_NAME = 8;
	private static int LENGTH_OF_TEAM_NAME = 3;
	private static final String RANDOM_ALLOWED_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz";
	
	private static int numberOfStudents;
	private static int numberOfTeams;

	public static void main(String args[]) throws IOException {

		// set parameters
		if (args.length == 2) {
			numberOfStudents = Integer.parseInt(args[0]);
			numberOfTeams = Integer.parseInt(args[1]);
		} else {
			System.out
					.println("Command arguments are java generateStudentData <number of students> <number of teams>");
			return;
		}

		List<String> teamnames = generateTeamNames(numberOfTeams);
		
		List<String> lines = generateEnrollText(numberOfStudents, teamnames);

		//print output
		for (String line : lines) {
			System.out.println(line);
		}

	}

	/**
	 * 
	 * @return Returns enroll lines, sorted by team name
	 */
	private static List<String> generateEnrollText(int numberOfStudents,
			List<String> teamnames) {
		List<String> lines = new ArrayList<String>();
		for (int i = 0; i < numberOfStudents; i++) {

			String name = generateRandomString(LENGTH_OF_STUDENT_NAME);

			String student = new String();
			student += teamnames.get(new Random().nextInt(teamnames.size()))
					+ '|' + name + '|' + name + "@gmail.com";

			lines.add(student);
		}
		Collections.sort(lines);
		return lines;
	}

	private static List<String> generateTeamNames(int numberOfTeams) {
		ArrayList<String> teamnames = new ArrayList<String>();
		for (int i = 0; i < numberOfTeams; i++) {
			String team = generateRandomString(LENGTH_OF_TEAM_NAME);
			teamnames.add("Team " + team);
		}
		return teamnames;
	}

	private static String generateRandomString(int length) {
		String name = new String();

		for (int j = 0; j < length; j++) {
			name += RANDOM_ALLOWED_CHARS.charAt(new Random()
					.nextInt(RANDOM_ALLOWED_CHARS.length()));
		}
		return name;
	}
}
