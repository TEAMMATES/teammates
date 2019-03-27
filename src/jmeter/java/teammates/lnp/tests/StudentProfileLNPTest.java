package teammates.lnp.tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.lnp.util.LNPTestData;

/**
 * L&P Test Case for student profile.
 */
public final class StudentProfileLNPTest extends BaseLNPTestCase {

    private static final String DATA_JSON_PATH = "/studentProfile.json";
    private static final String CONFIG_CSV_PATH = "/studentProfileConfig.csv";
    private static final String JMX_PATH = "studentProfile.jmx";

    private static final int NUMBER_OF_USER_ACCOUNTS = 100;
    private static final String USER_NAME = "DummyUser";
    private static final String USER_EMAIL = "personalEmail";

    private final LNPTestData testData = new LNPTestData() {

        @Override
        protected JSONObject generateAccountsJson() {
            JSONObject accounts = new JSONObject();

            for (int i = 0; i < NUMBER_OF_USER_ACCOUNTS; i++) {
                JSONObject studentAccountData = new JSONObject();

                studentAccountData.put("googleId", USER_NAME + i + ".tmms");
                studentAccountData.put("name", USER_NAME + i);
                studentAccountData.put("isInstructor", false);
                studentAccountData.put("email", USER_EMAIL + i + "@gmail.tmt");
                studentAccountData.put("institute", "TEAMMATES Test Institute 1");

                accounts.put(USER_NAME + i, studentAccountData);
            }

            JSONObject instructorAccount = new JSONObject();
            instructorAccount.put("googleId", "TestData.instructor");
            instructorAccount.put("name", "TEAMMATES Test Instructor");
            instructorAccount.put("isInstructor", true);
            instructorAccount.put("email", "tmms.test@gmail.tmt");
            instructorAccount.put("institute", "TEAMMATES Test Institute 1");
            accounts.put("instructor", instructorAccount);

            return accounts;
        }

        @Override
        protected JSONObject generateCoursesJson() {
            JSONObject courseData = new JSONObject();
            courseData.put("id", "TestData.CS101");
            courseData.put("name", "Intro To Programming");
            courseData.put("timeZone", "UTC");

            return new JSONObject().put("course", courseData);
        }

        @Override
        protected JSONObject generateInstructorsJson() {
            JSONObject instructors = new JSONObject();

            JSONObject instructorDetails = new JSONObject();
            instructorDetails.put("googleId", "TestData.instructor");
            instructorDetails.put("courseId", "TestData.CS101");
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
            instructors.put("teammates.test.instructor", instructorDetails);

            return instructors;
        }

        @Override
        protected JSONObject generateStudentsJson() {
            JSONObject students = new JSONObject();

            for (int i = 0; i < NUMBER_OF_USER_ACCOUNTS; i++) {
                JSONObject studentAccountData = new JSONObject();
                studentAccountData.put("googleId", USER_NAME + i + ".tmms");
                studentAccountData.put("email", USER_EMAIL + i + "@gmail.tmt");
                studentAccountData.put("course", "TestData.CS101");
                studentAccountData.put("name", USER_NAME + i);
                studentAccountData.put("comments", "This student's name is " + USER_NAME + i);
                studentAccountData.put("team", "Team 1");
                studentAccountData.put("section", "None");

                students.put(USER_NAME + i, studentAccountData);
            }

            return students;
        }

        @Override
        protected JSONObject generateFeedbackSessionsJson() {
            JSONObject session = new JSONObject();

            session.put("feedbackSessionName", "First Session");
            session.put("courseId", "TestData.CS101");
            session.put("creatorEmail", "tmms.test@gmail.tmt");
            session.put("instructions", "Instructions for first session");
            session.put("createdTime", "2018-04-01T23:59:00Z");
            session.put("startTime", "2018-04-01T21:59:00Z");
            session.put("endTime", "2026-04-30T21:59:00Z");
            session.put("sessionVisibleFromTime", "2018-04-01T21:59:00Z");
            session.put("resultsVisibleFromTime", "2026-05-01T21:59:00Z");
            session.put("timeZone", "Africa/Johannesburg");
            session.put("gracePeriod", 10);
            session.put("sentOpenEmail", true);
            session.put("sentClosingEmail", false);
            session.put("sentPublishedEmail", false);
            session.put("isOpeningEmailEnabled", false);
            session.put("isClosingEmailEnabled", false);
            session.put("isPublishedEmailEnabled", false);

            return new JSONObject().put("openSession", session);
        }

        @Override
        protected JSONObject generateFeedbackQuestionsJson() {
            return new JSONObject();
        }

        @Override
        protected JSONObject generateFeedbackResponsesJson() {
            return new JSONObject();
        }

        @Override
        protected JSONObject generateFeedbackResponseCommentsJson() {
            return new JSONObject();
        }

        @Override
        protected JSONObject generateProfilesJson() {
            JSONObject profiles = new JSONObject();

            for (int i = 0; i < NUMBER_OF_USER_ACCOUNTS; i++) {
                JSONObject profileData = new JSONObject();
                profileData.put("googleId", USER_NAME + i + ".tmms");
                profileData.put("email", USER_EMAIL + i + "@gmail.tmt");
                profileData.put("shortName", i);
                profileData.put("institute", "TEAMMATES Test Institute 222");
                profileData.put("moreInfo", "I am " + i);
                profileData.put("pictureKey", "");
                profileData.put("gender", "MALE");
                profileData.put("nationality", "American");

                profiles.put(USER_NAME + i, profileData);
            }

            return profiles;
        }

        @Override
        public List<String> getCsvHeaders() {
            List<String> headers = new ArrayList<>();

            headers.add("email");
            headers.add("isAdmin");
            headers.add("googleid");

            return headers;
        }

        @Override
        public List<List<String>> getCsvData() throws IOException, ParseException {
            org.json.simple.JSONObject jsonObject = getJsonObjectFromFile();

            org.json.simple.JSONObject studentsJson = (org.json.simple.JSONObject) jsonObject.get("students");
            List<List<String>> csvData = new ArrayList<>();

            for (Object e : studentsJson.entrySet()) {
                Map.Entry entry = (Map.Entry) e;
                org.json.simple.JSONObject studentJson = (org.json.simple.JSONObject) entry.getValue();

                List<String> csvRow = new ArrayList<>();

                csvRow.add((String) studentJson.get("googleId")); // "googleid" is used for logging in, not "email"
                csvRow.add("no");
                csvRow.add((String) studentJson.get("googleId"));

                csvData.add(csvRow);
            }

            return csvData;
        }

    };

    @Override
    protected String getPathToCsvConfigFile() {
        return CONFIG_CSV_PATH;
    }

    @Override
    protected String getPathToJsonDataFile() {
        return DATA_JSON_PATH;
    }

    @BeforeClass
    public void classSetup() {
        createTestData(testData);
        persistTestData(DATA_JSON_PATH);
    }

    @Test
    public void runLnpTest() throws Exception {
        runJmeter(JMX_PATH);
    }

    @AfterClass
    public void classTearDown() {
        deleteTestData(DATA_JSON_PATH);
    }

}
