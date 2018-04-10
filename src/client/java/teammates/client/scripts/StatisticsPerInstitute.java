package teammates.client.scripts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.storage.entity.Account;
import teammates.storage.entity.CourseStudent;
import teammates.storage.entity.Instructor;

/**
 * Generate list of institutes and number of users per institute.
 */
public class StatisticsPerInstitute extends RemoteApiClient {

    private static final int INSTRUCTOR_INDEX = 0;
    private static final int STUDENT_INDEX = 1;
    private static final String UNKNOWN_INSTITUTE = "Unknown Institute";

    private int iterationCounter;

    private Map<String, String> courseIdToInstituteMap = new HashMap<>();
    private Map<String, String> googleIdToInstituteMap = new HashMap<>();

    public static void main(String[] args) throws IOException {
        StatisticsPerInstitute statistics = new StatisticsPerInstitute();
        statistics.doOperationRemotely();
    }

    @Override
    protected void doOperation() {

        List<CourseStudent> allStudents = ofy().load().type(CourseStudent.class).list();

        List<Instructor> allInstructors = ofy().load().type(Instructor.class).list();

        List<Account> allAccounts = ofy().load().type(Account.class).list();

        StatsBundle statsBundle = generateStatsPerInstitute(allStudents, allInstructors, allAccounts);
        List<InstituteStats> statsPerInstituteList = statsBundle.instituteStatsList;

        String statsForUniqueStudentEmail =
                generateUniqueStudentEmailStatsInWholeSystem(statsBundle.numOfAllStudentEmails,
                                                             statsBundle.numOfUniqueStudentEmails);
        String statsForUniqueInstructorEmail =
                generateUniqueInstructorEmailStatsInWholeSystem(statsBundle.numOfAllInstructorEmail,
                                                                statsBundle.numOfUniqueInstructorEmails);

        print(statsPerInstituteList);
        System.out.println("\n\n" + "***************************************************" + "\n\n");
        System.out.println(statsForUniqueStudentEmail);

        System.out.println("\n\n" + "***************************************************" + "\n\n");
        System.out.println(statsForUniqueInstructorEmail);
    }

    private String generateUniqueInstructorEmailStatsInWholeSystem(int totalCountOfEmails, int totalCountOfUniqueEmails) {
        return "===============Unique Instructor Emails===============\n"
                + "Format=> Total Unique Emails [Total Emails]\n"
                + "===================================================\n"
                + totalCountOfUniqueEmails + " [ " + totalCountOfEmails + " ]\n";
    }

    private boolean isTestingInstructorData(Instructor instructor, List<Account> allAccounts) {
        boolean isTestingData = false;

        if (instructor.getEmail() != null && instructor.getEmail().toLowerCase().endsWith(".tmt")) {
            isTestingData = true;
        }

        String instituteForInstructor = getInstituteForInstructor(instructor, allAccounts);
        if (instituteForInstructor == null || instituteForInstructor.contains("TEAMMATES Test Institute")) {
            isTestingData = true;
        }

        return isTestingData;
    }

    private String generateUniqueStudentEmailStatsInWholeSystem(int totalCountOfEmails, int totalCountOfUniqueEmails) {
        return "===============Unique Student Emails===============\n"
                + "Format=> Total Unique Emails [Total Emails]\n"
                + "===================================================\n"
                + totalCountOfUniqueEmails + " [ " + totalCountOfEmails + " ]\n";
    }

    private boolean isTestingStudentData(
            CourseStudent student, List<Instructor> allInstructors, List<Account> allAccounts) {
        boolean isTestingData = false;

        if (student.getEmail().toLowerCase().endsWith(".tmt")) {
            isTestingData = true;
        }

        if (getInstituteForStudent(student, allInstructors, allAccounts).contains("TEAMMATES Test Institute")) {
            isTestingData = true;
        }

        return isTestingData;
    }

    private StatsBundle generateStatsPerInstitute(
            List<CourseStudent> allStudents, List<Instructor> allInstructors, List<Account> allAccounts) {
        Map<String, Map<Integer, Set<String>>> institutes = new HashMap<>();
        Set<String> allInstructorEmailSet = new HashSet<>();
        Set<String> allStudentEmailSet = new HashSet<>();
        int studentEmailCounter = 0;
        int instructorEmailCounter = 0;

        for (Instructor instructor : allInstructors) {

            if (isTestingInstructorData(instructor, allAccounts) || instructor.getEmail() == null) {
                continue;
            }

            String institute = getInstituteForInstructor(instructor, allAccounts);

            if (!institutes.containsKey(institute)) {
                institutes.put(institute, new HashMap<Integer, Set<String>>());
                institutes.get(institute).put(INSTRUCTOR_INDEX, new HashSet<String>());
                institutes.get(institute).put(STUDENT_INDEX, new HashSet<String>());
            }
            institutes.get(institute).get(INSTRUCTOR_INDEX).add(instructor.getEmail().toLowerCase());
            allInstructorEmailSet.add(instructor.getEmail().toLowerCase());
            instructorEmailCounter++;
            updateProgressIndicator();
        }

        for (CourseStudent student : allStudents) {

            if (isTestingStudentData(student, allInstructors, allAccounts) || student.getEmail() == null) {
                continue;
            }

            String institute = getInstituteForStudent(student, allInstructors, allAccounts);

            if (!institutes.containsKey(institute)) {
                institutes.put(institute, new HashMap<Integer, Set<String>>());

                institutes.get(institute).put(INSTRUCTOR_INDEX, new HashSet<String>());
                institutes.get(institute).put(STUDENT_INDEX, new HashSet<String>());
            }

            institutes.get(institute).get(STUDENT_INDEX).add(student.getEmail().toLowerCase());
            allStudentEmailSet.add(student.getEmail().toLowerCase());
            studentEmailCounter++;
            updateProgressIndicator();
        }

        List<InstituteStats> statList = convertToList(institutes);
        sortByTotalStudentsDescending(statList);

        StatsBundle statsBundle = new StatsBundle();
        statsBundle.instituteStatsList = statList;
        statsBundle.numOfAllInstructorEmail = instructorEmailCounter;
        statsBundle.numOfAllStudentEmails = studentEmailCounter;
        statsBundle.numOfUniqueInstructorEmails = allInstructorEmailSet.size();
        statsBundle.numOfUniqueStudentEmails = allStudentEmailSet.size();

        return statsBundle;
    }

    private String getInstituteForStudent(
            CourseStudent student, List<Instructor> allInstructors, List<Account> allAccounts) {

        if (courseIdToInstituteMap.containsKey(student.getCourseId())) {
            return courseIdToInstituteMap.get(student.getCourseId());
        }

        List<Instructor> instructorList = getInstructorsOfCourse(allInstructors, student.getCourseId());

        String institute = getInstituteForInstructors(instructorList, allAccounts);

        courseIdToInstituteMap.put(student.getCourseId(), institute);

        return institute;

    }

    private String getInstituteForInstructors(List<Instructor> instructorList, List<Account> allAccounts) {
        String institute = UNKNOWN_INSTITUTE;

        for (Instructor instructor : instructorList) {

            String tempIns = getInstituteForInstructor(instructor, allAccounts);
            if (tempIns != null) {
                institute = tempIns;
                break;
            }

        }

        return institute;
    }

    private String getInstituteForInstructor(Instructor instructor, List<Account> allAccounts) {
        if (instructor.getGoogleId() == null) {
            return null;
        }

        return getInstituteFromGoogleId(instructor.getGoogleId(), allAccounts);
    }

    private List<Instructor> getInstructorsOfCourse(List<Instructor> allInstructors, String courseId) {
        List<Instructor> instructorsOfCourse = new ArrayList<>();
        for (Instructor instructor : allInstructors) {
            if (instructor.getCourseId().equals(courseId)) {
                instructorsOfCourse.add(instructor);
            }
        }

        return instructorsOfCourse;
    }

    private String getInstituteFromGoogleId(String googleId, List<Account> allAccounts) {
        if (googleIdToInstituteMap.containsKey(googleId)) {
            return googleIdToInstituteMap.get(googleId);
        }

        for (Account account : allAccounts) {
            if (account.getGoogleId().equals(googleId) && account.getInstitute() != null) {
                googleIdToInstituteMap.put(googleId, account.getInstitute());
                return account.getInstitute();
            }
        }

        return null;
    }

    private void print(List<InstituteStats> statList) {
        System.out.println("===============Stats Per Institute=================");
        System.out.println("Format=> Instructors + Students = Total [Institute]");
        System.out.println("===================================================");
        int i = 0;
        int runningTotal = 0;
        for (InstituteStats stats : statList) {
            i++;
            int numInstructors = stats.instructorTotal;
            int numStudents = stats.studentTotal;
            int total = numInstructors + numStudents;
            runningTotal += total;
            System.out.println(
                    "[" + i + "]" + numInstructors + " + " + numStudents + "="
                            + total + "{" + runningTotal + "}\t[" + stats.name + "]");
        }

    }

    private List<InstituteStats> convertToList(
            Map<String, Map<Integer, Set<String>>> institutes) {
        List<InstituteStats> list = new ArrayList<>();
        institutes.forEach((insName, insStudents) -> {
            InstituteStats insStat = new InstituteStats();
            insStat.name = insName;
            insStat.studentTotal = insStudents.get(STUDENT_INDEX).size();
            insStat.instructorTotal = insStudents.get(INSTRUCTOR_INDEX).size();
            list.add(insStat);
        });
        return list;
    }

    private void sortByTotalStudentsDescending(List<InstituteStats> list) {
        list.sort(Comparator.comparing((InstituteStats institute) -> institute.studentTotal).reversed());
    }

    private void updateProgressIndicator() {
        iterationCounter++;
        if (iterationCounter % 1000 == 0) {
            System.out.print("------------------ iterations count:" + iterationCounter + " ------------------------\n");
        }
    }

    private static class InstituteStats {
        String name;
        int studentTotal;
        int instructorTotal;
    }

    private static class StatsBundle {
        List<InstituteStats> instituteStatsList;
        int numOfUniqueStudentEmails;
        int numOfAllStudentEmails;
        int numOfUniqueInstructorEmails;
        int numOfAllInstructorEmail;
    }
}
