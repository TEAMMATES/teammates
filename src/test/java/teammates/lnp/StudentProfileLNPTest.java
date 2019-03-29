package teammates.lnp;

import java.io.IOException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.util.Const;
import teammates.lnp.util.LNPTestData;

/**
 * L&P Test Case for student profile API endpoint.
 */
public final class StudentProfileLNPTest extends BaseLNPTestCase {

    private static final String DATA_JSON_PATH = "/studentProfile.json";
    private static final String CONFIG_CSV_PATH = "/studentProfileConfig.csv";
    private static final String JMX_FILE = "studentProfile.jmx";

    private static final int NUMBER_OF_USER_ACCOUNTS = 100;
    private static final String USER_NAME = "DummyUser";
    private static final String USER_EMAIL = "personalEmail";

    private final LNPTestData testData = new LNPTestData() {

        @Override
        protected Map<String, AccountAttributes> generateAccounts() {
            Map<String, AccountAttributes> accounts = new HashMap<>();

            for (int i = 0; i < NUMBER_OF_USER_ACCOUNTS; i++) {
                accounts.put(USER_NAME + i, AccountAttributes.builder(USER_NAME + i + ".tmms")
                        .withEmail(USER_EMAIL + i + "@gmail.tmt")
                        .withName(USER_NAME + i)
                        .withIsInstructor(false)
                        .withInstitute("TEAMMATES Test Institute 1")
                        .build()
                );
            }

            return accounts;
        }

        @Override
        protected Map<String, CourseAttributes> generateCourses() {
            Map<String, CourseAttributes> courses = new HashMap<>();

            courses.put("course", CourseAttributes.builder("TestData.CS101")
                    .withName("Intro To Programming")
                    .withTimezone(ZoneId.of("UTC"))
                    .build()
            );

            return courses;
        }

        @Override
        protected Map<String, InstructorAttributes> generateInstructors() {
            Map<String, InstructorAttributes> instructors = new HashMap<>();

            instructors.put("teammates.test.instructor",
                    InstructorAttributes.builder("TestData.CS101", "tmms.test@gmail.tmt")
                            .withGoogleId("TestData.instructor")
                            .withName("Teammates Test")
                            .withRole("Co-owner")
                            .withIsDisplayedToStudents(true)
                            .withDisplayedName("Co-owner")
                            .withPrivileges(new InstructorPrivileges(
                                    Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER))
                            .build()
            );

            return instructors;
        }

        @Override
        protected Map<String, StudentAttributes> generateStudents() {
            Map<String, StudentAttributes> students = new HashMap<>();

            for (int i = 0; i < NUMBER_OF_USER_ACCOUNTS; i++) {
                students.put(USER_NAME + i, StudentAttributes.builder("TestData.CS101", USER_EMAIL + i + "@gmail.tmt")
                        .withGoogleId(USER_NAME + i + ".tmms")
                        .withName(USER_NAME + i)
                        .withComment("This student's name is " + USER_NAME + i)
                        .withTeamName("Team 1")
                        .withSectionName("None")
                        .build()
                );
            }

            return students;
        }

        @Override
        protected Map<String, FeedbackSessionAttributes> generateFeedbackSessions() {
            return new HashMap<>();
        }

        @Override
        protected Map<String, FeedbackQuestionAttributes> generateFeedbackQuestions() {
            return new HashMap<>();
        }

        @Override
        protected Map<String, FeedbackResponseAttributes> generateFeedbackResponses() {
            return new HashMap<>();
        }

        @Override
        protected Map<String, FeedbackResponseCommentAttributes> generateFeedbackResponseComments() {
            return new HashMap<>();
        }

        @Override
        protected Map<String, StudentProfileAttributes> generateProfiles() {
            Map<String, StudentProfileAttributes> profiles = new HashMap<>();

            for (int i = 0; i < NUMBER_OF_USER_ACCOUNTS; i++) {
                profiles.put(USER_NAME + i, StudentProfileAttributes.builder(USER_NAME + i + ".tmms")
                        .withEmail(USER_EMAIL + i + "@gmail.tmt")
                        .withShortName(String.valueOf(i))
                        .withInstitute("TEAMMATES Test Institute 222")
                        .withMoreInfo("I am " + i)
                        .withPictureKey("")
                        .withGender(StudentProfileAttributes.Gender.MALE)
                        .withNationality("American")
                        .build()
                );
            }

            return profiles;
        }

        @Override
        public List<String> generateCsvHeaders() {
            List<String> headers = new ArrayList<>();

            headers.add("email");
            headers.add("isAdmin");
            headers.add("googleid");

            return headers;
        }

        @Override
        public List<List<String>> generateCsvData() throws IOException {
            DataBundle dataBundle = loadDataBundle(DATA_JSON_PATH);
            List<List<String>> csvData = new ArrayList<>();

            dataBundle.students.forEach((key, student) -> {
                List<String> csvRow = new ArrayList<>();

                csvRow.add(student.googleId); // "googleid" is used for logging in, not "email"
                csvRow.add("no");
                csvRow.add(student.googleId);

                csvData.add(csvRow);
            });

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
        runJmeter(JMX_FILE);
    }

    @AfterClass
    public void classTearDown() {
        deleteTestData(DATA_JSON_PATH);
    }

}
