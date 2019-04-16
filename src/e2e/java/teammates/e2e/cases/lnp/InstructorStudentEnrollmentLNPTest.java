package teammates.e2e.cases.lnp;

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
import teammates.e2e.util.LNPTestData;

/**
 * L&P Test Case for instructor's student enrollment API endpoint.
 */
public class InstructorStudentEnrollmentLNPTest extends BaseLNPTestCase {

    private static final String JSON_DATA_PATH = "/instructorStudentEnrollmentData.json";
    private static final String CSV_CONFIG_PATH = "/instructorStudentEnrollmentConfig.csv";

    private static final int NUM_INSTRUCTORS = 10;
    private static final int NUM_STUDENTS_PER_INSTRUCTOR = 100;
    private static final int NUM_STUDENTS_PER_SECTION = 25;

    private static final String INSTRUCTOR_NAME = "LnPInstructor";
    private static final String INSTRUCTOR_EMAIL = "personalEmail";
    private static final String COURSE_NAME = "LnPCourse";

    @Override
    protected LNPTestData getTestData() {
        return new LNPTestData() {
            @Override
            protected Map<String, AccountAttributes> generateAccounts() {
                return new HashMap<>();
            }

            @Override
            protected Map<String, CourseAttributes> generateCourses() {
                Map<String, CourseAttributes> courses = new HashMap<>();

                // Create a course for each instructor
                for (int i = 0; i < NUM_INSTRUCTORS; i++) {
                    courses.put(COURSE_NAME + i, CourseAttributes.builder(COURSE_NAME + "." + i)
                            .withName(COURSE_NAME + i)
                            .withTimezone(ZoneId.of("UTC"))
                            .build()
                    );
                }

                return courses;
            }

            @Override
            protected Map<String, InstructorAttributes> generateInstructors() {
                Map<String, InstructorAttributes> instructors = new HashMap<>();

                for (int i = 0; i < NUM_INSTRUCTORS; i++) {
                    instructors.put(INSTRUCTOR_NAME + i,
                            InstructorAttributes.builder(COURSE_NAME + "." + i, INSTRUCTOR_EMAIL + i + "@gmail.tmt")
                                    .withGoogleId(INSTRUCTOR_NAME + i)
                                    .withName(INSTRUCTOR_NAME + i)
                                    .withRole("Co-owner")
                                    .withIsDisplayedToStudents(true)
                                    .withDisplayedName("Co-owner")
                                    .withPrivileges(new InstructorPrivileges(
                                            Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER))
                                    .build()
                    );
                }

                return instructors;
            }

            @Override
            protected Map<String, StudentAttributes> generateStudents() {
                return new HashMap<>();
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
                return new HashMap<>();
            }

            @Override
            public List<String> generateCsvHeaders() {
                List<String> headers = new ArrayList<>();

                headers.add("email");
                headers.add("isAdmin");
                headers.add("courseId");
                headers.add("enrollData");

                return headers;
            }

            @Override
            public List<List<String>> generateCsvData() {
                DataBundle dataBundle = loadDataBundle(JSON_DATA_PATH);
                List<List<String>> csvData = new ArrayList<>();

                dataBundle.instructors.forEach((key, instructor) -> {
                    List<String> csvRow = new ArrayList<>();

                    csvRow.add(instructor.googleId);
                    csvRow.add("no");
                    csvRow.add(instructor.courseId);

                    // Create and add student enrollment data with a team number corresponding to each section number
                    int dataLength = 75 * NUM_STUDENTS_PER_INSTRUCTOR;
                    StringBuilder enrollData = new StringBuilder(dataLength);
                    enrollData.append("\"Section|Team|Name|Email|Comments\n");

                    for (int i = 0; i < NUM_STUDENTS_PER_INSTRUCTOR; i++) {
                        String name = instructor.name + ".Student" + i;
                        String email = instructor.name + ".Student" + i + "@gmail.tmt";

                        enrollData.append(i / NUM_STUDENTS_PER_SECTION)
                                .append('|')
                                .append(i / NUM_STUDENTS_PER_SECTION)
                                .append('|')
                                .append(name)
                                .append('|')
                                .append(email)
                                .append("|no comment\n");
                    }
                    enrollData.append('\"');
                    csvRow.add(enrollData.toString());

                    csvData.add(csvRow);
                });

                return csvData;
            }
        };
    }

    @Override
    protected String getCsvConfigPath() {
        return CSV_CONFIG_PATH;
    }

    @Override
    protected String getJsonDataPath() {
        return JSON_DATA_PATH;
    }

    @Override
    protected int getNumberOfThreads() {
        return NUM_INSTRUCTORS;
    }

    @Override
    protected int getRampUpPeriod() {
        return NUM_INSTRUCTORS * 2;
    }

    @Override
    protected String getTestEndpoint() {
        return Const.ResourceURIs.URI_PREFIX + Const.ResourceURIs.COURSE_ENROLL_SAVE + "?courseid=${courseId}";
    }

    @Override
    protected String getTestMethod() {
        return POST;
    }

    @Override
    protected Map<String, String> getRequestParameters() {
        return new HashMap<>();
    }

    @Override
    protected String getRequestBody() {
        return "${enrollData}";
    }

    @Override
    protected String getRequestBodyContentType() {
        return "text/csv";
    }

    @BeforeClass
    public void classSetup() {
        createTestData();
        persistTestData(JSON_DATA_PATH);
    }

    @Test
    public void runLnpTest() throws IOException {
        runJmeter(false);
    }

    /**
     * Removes the entities added for the instructors' student enrollment L&P test.
     */
    @AfterClass
    public void classTearDown() throws IOException {
        // There is no need to add the newly enrolled students to the JSON DataBundle#students. This is because the new
        // CourseStudent entities that were created are automatically deleted when the corresponding course is deleted.
        deleteTestData(JSON_DATA_PATH);
        deleteDataFiles();
    }

}
