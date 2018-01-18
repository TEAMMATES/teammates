package teammates.client.scripts;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import teammates.client.remoteapi.RemoteApiClient;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.logic.api.Logic;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.FeedbackResponseCommentsDb;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.test.driver.TestProperties;

public class OfflineBackup extends RemoteApiClient {
    protected String backupFileDirectory = "";
    private String currentFileName = "";
    private boolean hasPreviousEntity;
    private Set<String> accountsSaved = new HashSet<>();

    public static void main(String[] args) throws IOException {
        OfflineBackup offlineBackup = new OfflineBackup();
        offlineBackup.doOperationRemotely();
    }

    @Override
    protected void doOperation() {
        List<String> logs = getModifiedLogs();
        Set<String> courses = extractModifiedCourseIds(logs);
        backupFileDirectory = "BackupFiles/Backup/" + getCurrentDateAndTime();
        createBackupDirectory(backupFileDirectory);
        retrieveEntitiesByCourse(courses);
    }

    /**
     * Opens a connection to the entityModifiedLogs servlet to retrieve a log of all recently modified entities.
     */
    private List<String> getModifiedLogs() {
        List<String> modifiedLogs = new ArrayList<>();
        try {
            //Opens a URL connection to obtain the entity modified logs
            URL url = new URL(TestProperties.TEAMMATES_URL + "/entityModifiedLogs");

            URLConnection urlConn = url.openConnection();

            BufferedReader in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            String logMessage;
            while ((logMessage = in.readLine()) != null) {
                modifiedLogs.add(logMessage);
            }
            in.close();
        } catch (IOException e) {
            System.out.println("Error occurred while trying to access modified entity logs: " + e.getMessage());
        }

        return modifiedLogs;
    }

    /**
     * Look through the logs and extracts all recently modified courses.
     */
    private Set<String> extractModifiedCourseIds(List<String> modifiedLogs) {

        //Extracts the course Ids to be backup from the logs
        Set<String> courses = new HashSet<>();
        for (String course : modifiedLogs) {
            course = course.trim();
            if (!course.isEmpty()) {
                courses.add(course);
            }

        }
        return courses;
    }

    /**
     * Returns the current date and time to label the backup folder.
     */
    protected String getCurrentDateAndTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd HH.mm.ss");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    /**
     * Creates a directory to store the backup files.
     */
    protected void createBackupDirectory(String directoryName) {
        File directory = new File(directoryName);

        try {
            directory.mkdirs();
        } catch (SecurityException se) {
            System.out.println("Error making directory: " + directoryName);
        }

    }

    /**
     * Looks through all the modified courses and retrieve their respective entities.
     */
    protected void retrieveEntitiesByCourse(Set<String> coursesList) {

        coursesList.forEach(courseId -> {

            currentFileName = backupFileDirectory + "/" + courseId + ".json";
            appendToFile(currentFileName, "{\n");

            retrieveAndSaveAccountsByCourse(courseId);
            retrieveAndSaveCourse(courseId);
            retrieveAndSaveFeedbackQuestionsByCourse(courseId);
            retrieveAndSaveFeedbackResponsesByCourse(courseId);
            retrieveAndSaveFeedbackResponseCommentsByCourse(courseId);
            retrieveAndSaveFeedbackSessionsByCourse(courseId);
            retrieveAndSaveInstructorsByCourse(courseId);
            retrieveAndSaveStudentsByCourse(courseId);
            retrieveAndSaveStudentProfilesByCourse(courseId);

            appendToFile(currentFileName, "\n}");
        });

    }

    /**
     * Retrieves all the accounts from a course and saves them.
     */
    private void retrieveAndSaveAccountsByCourse(String courseId) {

        Logic logic = new Logic();
        List<StudentAttributes> students = logic.getStudentsForCourse(courseId);
        List<InstructorAttributes> instructors = logic.getInstructorsForCourse(courseId);

        appendToFile(currentFileName, "\t\"accounts\":{\n");

        for (StudentAttributes student : students) {
            saveStudentAccount(student);
        }

        for (InstructorAttributes instructor : instructors) {
            saveInstructorAccount(instructor);
        }

        appendToFile(currentFileName, "\n\t},\n");
        hasPreviousEntity = false;
    }

    /**
     * Retrieves the course and saves them.
     */
    private void retrieveAndSaveCourse(String courseId) {
        Logic logic = new Logic();
        CourseAttributes course = logic.getCourse(courseId);

        if (course == null) {
            return;
        }

        appendToFile(currentFileName, "\t\"courses\":{\n");
        appendToFile(currentFileName, formatJsonString(course.getJsonString(), course.getId()));

        hasPreviousEntity = false;
        appendToFile(currentFileName, "\n\t},\n");
    }

    /**
     * Retrieves all the feedback questions from a course and saves them.
     */
    private void retrieveAndSaveFeedbackQuestionsByCourse(String courseId) {

        FeedbackQuestionsDb feedbackQuestionDb = new FeedbackQuestionsDb();
        List<FeedbackQuestionAttributes> feedbackQuestions = feedbackQuestionDb.getFeedbackQuestionsForCourse(courseId);

        appendToFile(currentFileName, "\t\"feedbackQuestions\":{\n");

        for (FeedbackQuestionAttributes feedbackQuestion : feedbackQuestions) {
            saveFeedbackQuestion(feedbackQuestion);
        }
        hasPreviousEntity = false;
        appendToFile(currentFileName, "\n\t},\n");
    }

    /**
     * Retrieves all the feedback responses from a course and saves them.
     */
    private void retrieveAndSaveFeedbackResponsesByCourse(String courseId) {

        FeedbackResponsesDb feedbackResponsesDb = new FeedbackResponsesDb();
        List<FeedbackResponseAttributes> feedbackResponses = feedbackResponsesDb.getFeedbackResponsesForCourse(courseId);

        appendToFile(currentFileName, "\t\"feedbackResponses\":{\n");

        for (FeedbackResponseAttributes feedbackResponse : feedbackResponses) {
            saveFeedbackResponse(feedbackResponse);
        }
        hasPreviousEntity = false;
        appendToFile(currentFileName, "\n\t},\n");
    }

    /**
     * Retrieves all the feedback responses comments from a course and saves them.
     */
    private void retrieveAndSaveFeedbackResponseCommentsByCourse(String courseId) {

        FeedbackResponseCommentsDb feedbackResponseCommentsDb = new FeedbackResponseCommentsDb();
        List<FeedbackResponseCommentAttributes> feedbackResponseComments =
                feedbackResponseCommentsDb.getFeedbackResponseCommentsForCourse(courseId);

        appendToFile(currentFileName, "\t\"feedbackResponseComments\":{\n");

        for (FeedbackResponseCommentAttributes feedbackResponseComment : feedbackResponseComments) {
            saveFeedbackResponseComment(feedbackResponseComment);
        }
        hasPreviousEntity = false;
        appendToFile(currentFileName, "\n\t},\n");
    }

    /**
     * Retrieves all the feedback sessions from a course and saves them.
     */
    private void retrieveAndSaveFeedbackSessionsByCourse(String courseId) {
        Logic logic = new Logic();
        List<FeedbackSessionAttributes> feedbackSessions = logic.getFeedbackSessionsForCourse(courseId);

        appendToFile(currentFileName, "\t\"feedbackSessions\":{\n");

        for (FeedbackSessionAttributes feedbackSession : feedbackSessions) {
            saveFeedbackSession(feedbackSession);
        }
        hasPreviousEntity = false;
        appendToFile(currentFileName, "\n\t},\n");
    }

    /**
     * Retrieves all the instructors from a course and saves them.
     */
    private void retrieveAndSaveInstructorsByCourse(String courseId) {
        Logic logic = new Logic();
        List<InstructorAttributes> instructors = logic.getInstructorsForCourse(courseId);

        appendToFile(currentFileName, "\t\"instructors\":{\n");

        for (InstructorAttributes instructor : instructors) {
            saveInstructor(instructor);
        }
        hasPreviousEntity = false;
        appendToFile(currentFileName, "\n\t},\n");
    }

    /**
     * Retrieves all the students from a course and saves them.
     */
    private void retrieveAndSaveStudentsByCourse(String courseId) {
        Logic logic = new Logic();
        List<StudentAttributes> students = logic.getStudentsForCourse(courseId);

        appendToFile(currentFileName, "\t\"students\":{\n");

        for (StudentAttributes student : students) {
            saveStudent(student);
        }
        hasPreviousEntity = false;
        appendToFile(currentFileName, "\n\t},\n");
    }

    /**
     * Retrieves all the submissions from a course and saves them.
     */
    private void retrieveAndSaveStudentProfilesByCourse(String courseId) {

        Logic logic = new Logic();
        List<StudentAttributes> students = logic.getStudentsForCourse(courseId);

        appendToFile(currentFileName, "\t\"profiles\":{\n");

        for (StudentAttributes student : students) {
            if (student != null && student.googleId != null && !student.googleId.isEmpty()) {
                StudentProfileAttributes profile = logic.getStudentProfile(student.googleId);
                if (profile != null) {
                    saveProfile(profile);
                }
            }
        }

        appendToFile(currentFileName, "\n\t}\n");
        hasPreviousEntity = false;
    }

    /**
     * Performs formatting of the string to ensure that it conforms to json formatting.
     */
    private String formatJsonString(String entityJsonString, String name) {
        StringBuilder formattedString = new StringBuilder();

        if (hasPreviousEntity) {
            formattedString.append(",\n");
        } else {
            hasPreviousEntity = true;
        }

        formattedString.append("\t\t\"" + name + "\":" + entityJsonString.replace("\n", "\n\t\t"));

        return formattedString.toString();
    }

    /**
     * Retrieves all the student accounts and saves them.
     */
    private void saveStudentAccount(StudentAttributes student) {
        if (student == null) {
            return;
        }

        Logic logic = new Logic();
        AccountAttributes account = logic.getAccount(student.googleId.trim());

        if (account == null || accountsSaved.contains(account.email)) {
            return;
        }

        appendToFile(currentFileName, formatJsonString(account.getJsonString(), account.email));
        accountsSaved.add(account.email);
    }

    /**
     * Retrieves all the instructor accounts and saves them.
     */
    private void saveInstructorAccount(InstructorAttributes instructor) {
        if (instructor == null) {
            return;
        }

        Logic logic = new Logic();
        AccountAttributes account = logic.getAccount(instructor.googleId.trim());

        if (account == null || accountsSaved.contains(account.email)) {
            return;
        }

        appendToFile(currentFileName, formatJsonString(account.getJsonString(), account.email));
        accountsSaved.add(account.email);
    }

    private void saveFeedbackQuestion(FeedbackQuestionAttributes feedbackQuestion) {
        appendToFile(currentFileName, formatJsonString(feedbackQuestion.getJsonString(), feedbackQuestion.getId()));
    }

    private void saveFeedbackResponse(FeedbackResponseAttributes feedbackResponse) {
        appendToFile(currentFileName, formatJsonString(feedbackResponse.getJsonString(), feedbackResponse.getId()));
    }

    private void saveFeedbackResponseComment(FeedbackResponseCommentAttributes feedbackResponseComment) {
        appendToFile(currentFileName,
                     formatJsonString(feedbackResponseComment.getJsonString(),
                                      feedbackResponseComment.getId().toString()));
    }

    private void saveFeedbackSession(FeedbackSessionAttributes feedbackSession) {
        appendToFile(currentFileName,
                     formatJsonString(feedbackSession.getJsonString(),
                                      feedbackSession.getFeedbackSessionName() + "%" + feedbackSession.getCourseId()));
    }

    private void saveInstructor(InstructorAttributes instructor) {
        appendToFile(currentFileName, formatJsonString(instructor.getJsonString(), instructor.googleId));
    }

    private void saveStudent(StudentAttributes student) {
        appendToFile(currentFileName, formatJsonString(student.getJsonString(), student.googleId));
    }

    private void saveProfile(StudentProfileAttributes studentProfile) {
        appendToFile(currentFileName, formatJsonString(studentProfile.getJsonString(), studentProfile.googleId));
    }

    private static void appendToFile(String fileName, String fileContent) {
        try {

            File file = new File(fileName);

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(fileContent);
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
