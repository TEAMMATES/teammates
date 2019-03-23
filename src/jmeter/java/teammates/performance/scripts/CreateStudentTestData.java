package teammates.performance.scripts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import teammates.performance.util.TestProperties;

/**
 * Script to create data for student profile endpoint.
 */
public final class CreateStudentTestData {

    private static final int NUMBER_OF_USER_ACCOUNTS = 5;
    private static final String USER_ID = "userid";

    private CreateStudentTestData() {
        // Utility class
        // Intentional private constructor to prevent instantiation
    }

    public static void main(String[] args) {
        createJsonData();
    }

    /**
     * Creates a Json file with multiple user accounts.
     */
    private static void createJsonData() {
        JSONObject studentData = new JSONObject();

        // course data
        JSONObject courseData = new JSONObject();
        courseData.put("id", "FContribQnUiT.CS2104");
        courseData.put("name", "Programming Language Concepts");
        courseData.put("timeZone", "UTC");
        studentData.put("courses", new JSONObject().put("course", courseData));

        // add user accounts
        JSONObject user = new JSONObject();
        for (int i = 0; i < NUMBER_OF_USER_ACCOUNTS; i++) {
            JSONObject userAccountData = new JSONObject();
            userAccountData.put("googleId", USER_ID + i + ".tmms");
            userAccountData.put("name", USER_ID + i + " Betsy");
            userAccountData.put("isInstructor", false);
            userAccountData.put("email", USER_ID + i + ".tmms@gmail.tmt");
            userAccountData.put("institute", "TEAMMATES Test Institute 1");
            user.put(USER_ID + i, userAccountData);
        }

        JSONObject instructorAccount = new JSONObject();
        instructorAccount.put("googleId", "FContribQnUiT.instructor");
        instructorAccount.put("name", "Teammates Test");
        instructorAccount.put("isInstructor", true);
        instructorAccount.put("email", "tmms.test@gmail.tmt");
        instructorAccount.put("institute", "TEAMMATES Test Institute 1");
        user.put("instructor", instructorAccount);
        studentData.put("accounts", user);

        studentData.put("feedbackQuestions", new JSONObject());
        studentData.put("feedbackResponses", new JSONObject());
        studentData.put("feedbackResponseComments", new JSONObject());
        studentData.put("profiles", new JSONObject());

        // open feedback session
        JSONObject session = new JSONObject();
        session.put("feedbackSessionName", "First Session");
        session.put("courseId", "FContribQnUiT.CS2104");
        session.put("creatorEmail", "tmms.test@gmail.tmt");
        session.put("instructions", "Instructions for first session");
        session.put("createdTime", "2012-04-01T23:59:00Z");
        session.put("startTime", "2012-04-01T21:59:00Z");
        session.put("endTime", "2026-04-30T21:59:00Z");
        session.put("sessionVisibleFromTime", "2012-04-01T21:59:00Z");
        session.put("resultsVisibleFromTime", "2026-05-01T21:59:00Z");
        session.put("timeZone", "Africa/Johannesburg");
        session.put("gracePeriod", 10);
        session.put("sentOpenEmail", true);
        session.put("sentClosingEmail", false);
        session.put("sentPublishedEmail", false);
        session.put("isOpeningEmailEnabled", false);
        session.put("isClosingEmailEnabled", false);
        session.put("isPublishedEmailEnabled", false);

        studentData.put("feedbackSessions", new JSONObject().put("openSession", session));

        // students

        JSONObject students = new JSONObject();
        for (int i = 0; i < NUMBER_OF_USER_ACCOUNTS; i++) {
            JSONObject studentAccountData = new JSONObject();
            studentAccountData.put("googleId", USER_ID + i + ".tmms");
            studentAccountData.put("email", USER_ID + i + ".tmms@gmail.tmt");
            studentAccountData.put("course", "FContribQnUiT.CS2104");
            studentAccountData.put("name", USER_ID + i + " Betsy");
            studentAccountData.put("comments", "This student name is " + USER_ID + i + " Betsy");
            studentAccountData.put("team", "Team 1");
            studentAccountData.put("section", "None");
            students.put(USER_ID + i, studentAccountData);
        }
        studentData.put("students", students);

        // instructor

        JSONObject instructor = new JSONObject();
        JSONObject instructorDetails = new JSONObject();
        instructorDetails.put("googleId", "FContribQnUiT.instructor");
        instructorDetails.put("courseId", "FContribQnUiT.CS2104");
        instructorDetails.put("name", "Teammates Test");
        instructorDetails.put("email", "tmms.test@gmail.tmt");
        instructorDetails.put("role", "Co-owner");
        instructorDetails.put("isDisplayedToStudents", true);
        instructorDetails.put("displayedName", "Co-owner");
        instructorDetails.put("sectionLevel", new JSONObject());
        instructorDetails.put("sessionLevel", new JSONObject());

        JSONObject privileges = new JSONObject();
        JSONObject courseLevel = new JSONObject();
        courseLevel.put("canviewstudentinsection", true);
        courseLevel.put("cansubmitsessioninsection", true);
        courseLevel.put("canmodifysessioncommentinsection", true);
        courseLevel.put("canmodifycourse", true);
        courseLevel.put("canviewsessioninsection", true);
        courseLevel.put("canmodifysession", true);
        courseLevel.put("canmodifystudent", true);
        courseLevel.put("canmodifyinstructor", true);
        privileges.put("courseLevel", courseLevel);

        instructorDetails.put("privileges", privileges);
        instructor.put("teammates.test.instructor", instructorDetails);
        studentData.put("instructors", instructor);

        writeJsonDataToFile(studentData);
    }

    /**
     * Writes the JSON data to the file.
     */
    private static void writeJsonDataToFile(JSONObject studentData) {

        String fileName = TestProperties.TEST_DATA_FOLDER + "/studentProfile.json";
        File file = new File(fileName);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(studentData.toString());
        String prettyJsonString = gson.toJson(element);

        // if file doesnt exists, then create it
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(fileName))) {
                bw.write(prettyJsonString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
