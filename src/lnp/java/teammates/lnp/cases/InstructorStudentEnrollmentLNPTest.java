package teammates.lnp.cases;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.ListedHashTree;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.HttpRequestFailedException;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.lnp.util.JMeterElements;
import teammates.lnp.util.LNPSpecification;
import teammates.lnp.util.LNPTestData;
import teammates.ui.request.StudentsEnrollRequest;

/**
 * L&P Test Case for instructor's student enrollment API endpoint.
 */
public class InstructorStudentEnrollmentLNPTest extends BaseLNPTestCase {

    private static final int NUM_INSTRUCTORS = 10;
    private static final int RAMP_UP_PERIOD = NUM_INSTRUCTORS * 2;

    private static final int NUM_STUDENTS_PER_INSTRUCTOR = 100;
    private static final int NUM_STUDENTS_PER_SECTION = 25;

    private static final String INSTRUCTOR_NAME = "LnPInstructor";
    private static final String INSTRUCTOR_EMAIL = "personalEmail";
    private static final String COURSE_NAME = "LnPCourse";

    private static final double ERROR_RATE_LIMIT = 0.01;
    private static final double MEAN_RESP_TIME_LIMIT = 80;

    @Override
    protected LNPTestData getTestData() {
        return new LNPTestData() {
            @Override
            protected Map<String, CourseAttributes> generateCourses() {
                Map<String, CourseAttributes> courses = new HashMap<>();

                // Create a course for each instructor
                for (int i = 0; i < NUM_INSTRUCTORS; i++) {
                    courses.put(COURSE_NAME + i, CourseAttributes.builder(COURSE_NAME + "." + i)
                            .withName(COURSE_NAME + i)
                            .withTimezone("UTC")
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
            public List<String> generateCsvHeaders() {
                List<String> headers = new ArrayList<>();

                headers.add("loginId");
                headers.add("courseId");
                headers.add("enrollData");

                return headers;
            }

            @Override
            public List<List<String>> generateCsvData() {
                DataBundle dataBundle = loadDataBundle(getJsonDataPath());
                List<List<String>> csvData = new ArrayList<>();

                dataBundle.instructors.forEach((key, instructor) -> {
                    List<String> csvRow = new ArrayList<>();

                    csvRow.add(instructor.getGoogleId());
                    csvRow.add(instructor.getCourseId());

                    // Create and add student enrollment data with a team number corresponding to each section number
                    List<StudentsEnrollRequest.StudentEnrollRequest> enrollRequests = new ArrayList<>();

                    for (int i = 0; i < NUM_STUDENTS_PER_INSTRUCTOR; i++) {

                        String name = instructor.getName() + ".Student" + i;
                        String email = instructor.getName() + ".Student" + i + "@gmail.tmt";
                        String team = String.valueOf(i / NUM_STUDENTS_PER_SECTION);
                        String section = String.valueOf(i / NUM_STUDENTS_PER_SECTION);
                        String comment = "no comment";

                        enrollRequests.add(
                                new StudentsEnrollRequest.StudentEnrollRequest(name, email, team, section, comment));
                    }
                    String enrollData = sanitizeForCsv(JsonUtils.toJson(new StudentsEnrollRequest(enrollRequests)));
                    csvRow.add(enrollData);

                    csvData.add(csvRow);
                });

                return csvData;
            }
        };
    }

    private Map<String, String> getRequestHeaders() {
        Map<String, String> headers = new HashMap<>();

        headers.put(Const.HeaderNames.CSRF_TOKEN, "${csrfToken}");
        headers.put("Content-Type", "text/csv");

        return headers;
    }

    private String getTestEndpoint() {
        return Const.ResourceURIs.STUDENTS + "?courseid=${courseId}";
    }

    @Override
    protected HashTree getLnpTestPlan() {
        HashTree testPlan = new ListedHashTree(JMeterElements.testPlan());
        HashTree threadGroup = testPlan.add(
                JMeterElements.threadGroup(NUM_INSTRUCTORS, RAMP_UP_PERIOD, 1));

        threadGroup.add(JMeterElements.csvDataSet(getPathToTestDataFile(getCsvConfigPath())));
        threadGroup.add(JMeterElements.cookieManager());
        threadGroup.add(JMeterElements.defaultSampler());

        threadGroup.add(JMeterElements.onceOnlyController())
                .add(JMeterElements.loginSampler())
                .add(JMeterElements.csrfExtractor("csrfToken"));

        // Add HTTP sampler for test endpoint
        HeaderManager headerManager = JMeterElements.headerManager(getRequestHeaders());
        threadGroup.add(JMeterElements.httpSampler(getTestEndpoint(), PUT, "${enrollData}"))
                .add(headerManager);

        return testPlan;
    }

    @Override
    protected void setupSpecification() {
        this.specification = LNPSpecification.builder()
                .withErrorRateLimit(ERROR_RATE_LIMIT)
                .withMeanRespTimeLimit(MEAN_RESP_TIME_LIMIT)
                .build();
    }

    @BeforeClass
    public void classSetup() throws IOException, HttpRequestFailedException {
        generateTimeStamp();
        createTestData();
        setupSpecification();
    }

    @Test
    public void runLnpTest() throws IOException {
        runJmeter(false);
        displayLnpResults();
    }

    /**
     * Removes the entities added for the instructors' student enrollment L&P test.
     */
    @AfterClass
    public void classTearDown() throws IOException {
        // There is no need to add the newly enrolled students to the JSON DataBundle#students. This is because the new
        // CourseStudent entities that were created are automatically deleted when the corresponding course is deleted.
        deleteTestData();
        deleteDataFiles();
        cleanupResults();
    }

}
