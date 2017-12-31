package teammates.client.scripts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import teammates.test.driver.TestProperties;

/**
 * Class that create a json data file to be used with ImportData script
 * The result file will be saved in src/test/resources/data/ folder.
 *
 * <p>This script does not use any teamamtes's data structures or json framework for some reasons:
 * <ul>
 * <li>For 5000 or more students, it will consume a lot of memory. Need to store only id
 * of the objects to save memory.</li>
 * <li>To make this script portable, if you remove the teammates.common.Common import, and define
 * the path yourself, this become a totally portable script.(this speeds up the process of creating data)</li>
 * <li>The format of data bundle is json but quite "strange", no relationships, no arrays.</li>
 * </ul>
 */
public final class DataGenerator {
    // Name of the result file, please do not override existing file
    private static final String FILE_NAME = "ResultFileName.json";
    // Prefix used in all entities
    private static final String PREFIX = "D1_";

    private static final Integer NUM_OF_COURSES = 5;
    private static final Integer NUM_OF_STUDENTS = 1000;

    private static final Integer MIN_NUM_OF_INSTRUCTOR_PER_COURSES = 1;
    private static final Integer MAX_NUM_OF_INSTRUCTOR_PER_COURSES = 3;

    private static final Integer MIN_NUM_OF_STUDENTS_PER_COURSE = 50;
    private static final Integer AVERAGE_NUM_OF_STUDENTS_PER_COURSE = 150;
    private static final Integer STANDARD_DEVIATION_STUDENT_PER_COURSE = 100;
    private static final Integer MAX_NUM_OF_STUDENTS_PER_COURSE = 250;

    private static final Integer MAX_TEAM_SIZE = 5;
    private static final Integer MIN_TEAM_SIZE = 3;

    private static final ArrayList<String> courses = new ArrayList<>();
    private static final HashMap<String, String> instructors = new HashMap<>();
    private static final ArrayList<String> studentEmails = new ArrayList<>();
    private static final ArrayList<String> students = new ArrayList<>();
    private static final ArrayList<ArrayList<String>> teams = new ArrayList<>();

    private static final Random random = new Random();

    private DataGenerator() {
        // script, not meant to be instantiated
    }

    public static void main(String[] args) throws IOException {
        String data = generateData();
        writeDataToFile(data, TestProperties.TEST_DATA_FOLDER + "/" + FILE_NAME);
    }

    /**
     * Writes data to file, creates new file if necessary.
     *
     * @param data - Data string to write
     * @param filePath - path to file
     */
    private static void writeDataToFile(String data, String filePath) throws IOException {
        File f = new File(filePath);
        // Create file if it does not exist
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //get the file writer
        BufferedWriter out;
        FileWriter fstream = new FileWriter(filePath);
        out = new BufferedWriter(fstream);
        out.write(data);
        out.close();

    }

    private static String generateData() {
        System.out.println("Start generating data!");
        //Create students
        for (int i = 0; i < NUM_OF_STUDENTS; i++) {
            studentEmails.add(PREFIX + "Stu" + i + "Email@gmail.com");
        }

        // Create courses
        for (int i = 0; i < NUM_OF_COURSES; i++) {
            String courseName = "Course" + i;
            courses.add(courseName);
            generateDataForCourse(courseName);
        }
        System.out.println("Done generating data!");

        //Create output string
        return output();
    }

    /**
     * Randomly creates courses, students and evaluations for a particular instructor.
     */
    private static void generateDataForCourse(String courseName) {
        //number of courses for this particular instructor
        long numOfInstr =
                random.nextInt(MAX_NUM_OF_INSTRUCTOR_PER_COURSES - MIN_NUM_OF_INSTRUCTOR_PER_COURSES + 1)
                + MIN_NUM_OF_INSTRUCTOR_PER_COURSES;

        for (int j = 0; j < numOfInstr; j++) {
            // Add an Instructor
            String instrName = "Instr" + j + "_of_" + courseName;
            instructors.put(instrName, courseName);

        }

        // Add students to this course
        generateStudentsDataForCourse(courseName);

    }

    /**
     * Randomly creates students for a particular course.
     */
    private static void generateStudentsDataForCourse(String courseName) {
        // randomly get a number for student size for this course
        long numOfStudent = getDeviatedNumberOfStudentInCourse();
        //=====================================================================

        // randomly pick student indexes from global list to be put into this course
        Set<Integer> studentIndexs = new HashSet<>();
        while (studentIndexs.size() < numOfStudent) {
            studentIndexs.add(random.nextInt(NUM_OF_STUDENTS));
        }

        ArrayList<String> studentEmailInCourse = new ArrayList<>();
        for (Integer integer : studentIndexs) {
            studentEmailInCourse.add(studentEmails.get(integer));
        }
        //=====================================================================

        //Add teams
        int teamCount = 1;
        while (!studentEmailInCourse.isEmpty()) {
            long teamSize = random.nextInt(MAX_TEAM_SIZE - MIN_TEAM_SIZE + 1) + MIN_TEAM_SIZE;
            ArrayList<String> team = new ArrayList<>();
            for (int k = 0; !studentEmailInCourse.isEmpty() && k < teamSize; k++) {

                String email = studentEmailInCourse.remove(0);

                //add to team, add to students;
                String studentIndex = email.split("Email@gmail.com")[0].split("Stu")[1];
                String studentId = PREFIX + "Stu" + studentIndex + "Team" + teamCount + "_in_" + courseName;

                students.add(studentId);
                team.add(studentId);
            }

            teamCount++;
            teams.add(team);
        }
    }

    /**
     * Returns json string presenting the databundle.
     */
    private static String output() {
        System.out.println("Start writing to file !");
        String allAccounts = allAccounts();
        String allCourses = allCourses();
        String allInstructors = allInstructors();
        String allStudents = allStudents();
        System.out.println("Finish writing to file !");

        return "{\n" + allAccounts + "\n\n" + allCourses + "\n\n" + allInstructors + "\n\n" + allStudents + "\n\n}";
    }

    private static String allAccounts() {
        StringBuilder outputBuilder = new StringBuilder(100);
        outputBuilder.append("\"accounts\":{\n");
        for (String email : studentEmails) {
            email = email.split("@")[0];
            outputBuilder.append('\t').append(account(email));
            outputBuilder.append(",\n");
        }
        String output = outputBuilder.substring(0, outputBuilder.length() - 2);
        return output + "\n},";
    }

    /**
     * Returns Json string presentation for all instructors.
     */
    @SuppressWarnings("PMD.ConsecutiveLiteralAppends") // Seems like a bug in PMD
    private static String allInstructors() {
        StringBuilder outputBuilder = new StringBuilder(100);
        outputBuilder.append("\"instructors\":{\n");
        instructors.forEach((key, value) -> {
            String course = PREFIX + instructors.get(value);
            String instructorWithPrefix = PREFIX + key;
            outputBuilder.append('\t')
                         .append(instructor(instructorWithPrefix, "googleIdOf_" + instructorWithPrefix,
                                            "courseIdOf_" + course, "nameOf_" + instructorWithPrefix,
                                            "emailOf_" + instructorWithPrefix + "@gmail.com"))
                         .append(",\n");
        });
        String output = outputBuilder.substring(0, outputBuilder.length() - 2);
        return output + "\n},";

    }

    /**
     * Returns Json string presentation for all courses.
     */
    private static String allCourses() {
        StringBuilder output = new StringBuilder(100);
        output.append("\"courses\":{\n");
        for (int i = 0; i < courses.size(); i++) {
            String course = PREFIX + courses.get(i);

            output.append('\t').append(course(course, "courseIdOf_" + course, "nameOf_" + course));
            if (i != courses.size() - 1) {
                output.append(",\n");
            }
        }
        return output.append("\n},").toString();
    }

    /**
     * Returns Json string presentation for all students.
     */
    private static String allStudents() {
        StringBuilder outputBuilder = new StringBuilder(100);
        outputBuilder.append("\"students\":{\n");
        for (int i = 0; i < students.size(); i++) {
            String student = students.get(i);
            String index = student.split("Stu")[1].split("Team")[0];
            String team = student.split("Team")[1].split("_")[0];
            String course = PREFIX + student.split("_in_")[1];
            String email = studentEmails.get(Integer.parseInt(index));

            outputBuilder.append('\t')
                         .append(student(student, email, "Student " + index + " in " + course,
                                        "Team " + team, email.split("@")[0], "comment",
                                        "courseIdOf_" + course));
            if (i != students.size() - 1) {
                outputBuilder.append(",\n");
            }
        }
        return outputBuilder.append("\n},").toString();
    }

    private static String account(String acc) {
        return "\"" + acc
              + "\":{\"googleId\":\"" + acc
              + "\",\"name\":\"" + acc
              + "\",\"email\":\"" + acc + "@gmail.com\",\"institute\":\"\"}";
    }

    /**
     * Returns Json string presentation for a instructor entity.
     */
    private static String instructor(String objName, String googleId, String courseId, String name, String email) {
        return "\"" + objName + "\":{\"googleId\":\"" + googleId + "\",\"courseId\":\""
               + courseId + "\",\"name\":\"" + name + "\",\"email\":\"" + email + "\"}";
    }

    /**
     * Returns Json string presentation for a course entity.
     */
    private static String course(String objName, String id, String name) {
        return "\"" + objName + "\":{\"id\":\"" + id + "\",\"name\":\"" + name + "\"}";
    }

    /**
     * Returns Json string presentation for a student entity.
     */
    private static String student(String objName, String email, String name,
                                  String team, String id, String comments, String course) {
        return "\"" + objName + "\":{"
               + "\"email\":\"" + email + "\","
               + "\"name\":\"" + name + "\","
               + "\"team\":\"" + team + "\","
               + "\"id\":\"" + id + "\","
               + "\"comments\":\"" + comments + "\","
               + "\"course\":\"" + course + "\","
               + "\"profile\":{\"value\": \"" + name + "\"}"
               + "}";
    }

    /*helper methods*/

    /**
     * Returns a random number of student in course.
     */
    private static int getDeviatedNumberOfStudentInCourse() {
        int num = 0;
        do {
            num = (int) Math.floor(random.nextGaussian() * STANDARD_DEVIATION_STUDENT_PER_COURSE
                                   + AVERAGE_NUM_OF_STUDENTS_PER_COURSE);
        } while (num > MAX_NUM_OF_STUDENTS_PER_COURSE || num < MIN_NUM_OF_STUDENTS_PER_COURSE);
        return num;
    }

}
