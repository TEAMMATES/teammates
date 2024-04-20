package teammates.lnp.cases;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.HttpRequestFailedException;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.lnp.util.JMeterElements;
import teammates.lnp.util.LNPSpecification;
import teammates.lnp.util.LNPTestData;
import teammates.ui.request.CourseUpdateRequest;

/**
 * L&P Test Case for course update cascade API.
 */
public class InstructorCourseUpdateLNPTest extends BaseLNPTestCase {
    private static final int NUM_INSTRUCTORS = 1;
    private static final int RAMP_UP_PERIOD = NUM_INSTRUCTORS * 2;

    private static final int NUM_FEEDBACK_SESSIONS = 500;

    private static final String COURSE_ID = "TestData.CS101";
    private static final String COURSE_NAME = "LnPCourse";
    private static final String COURSE_TIME_ZONE = "UTC";

    private static final String UPDATE_COURSE_NAME = "updatedCourse";
    private static final String UPDATE_COURSE_TIME_ZONE = "GMT";

    private static final String INSTRUCTOR_ID = "LnPInstructor_id";
    private static final String INSTRUCTOR_NAME = "LnPInstructor";
    private static final String INSTRUCTOR_EMAIL = "tmms.test@gmail.tmt";

    private static final String FEEDBACK_SESSION_NAME = "Test Feedback Session";

    private static final double ERROR_RATE_LIMIT = 0.01;
    private static final double MEAN_RESP_TIME_LIMIT = 10;

    @Override
    protected LNPTestData getTestData() {
        return new LNPTestData() {
            @Override
            protected Map<String, CourseAttributes> generateCourses() {
                Map<String, CourseAttributes> courses = new HashMap<>();

                courses.put(COURSE_NAME, CourseAttributes.builder(COURSE_ID)
                        .withName(COURSE_NAME)
                        .withTimezone(COURSE_TIME_ZONE)
                        .build());

                return courses;
            }

            @Override
            protected Map<String, InstructorAttributes> generateInstructors() {
                Map<String, InstructorAttributes> instructors = new HashMap<>();

                instructors.put(INSTRUCTOR_NAME,
                        InstructorAttributes.builder(COURSE_ID, INSTRUCTOR_EMAIL)
                                .withGoogleId(INSTRUCTOR_ID)
                                .withName(INSTRUCTOR_NAME)
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
            protected Map<String, FeedbackSessionAttributes> generateFeedbackSessions() {
                Map<String, FeedbackSessionAttributes> feedbackSessions = new LinkedHashMap<>();

                for (int i = 1; i <= NUM_FEEDBACK_SESSIONS; i++) {
                    Instant now = Instant.now();
                    FeedbackSessionAttributes session = FeedbackSessionAttributes
                            .builder(FEEDBACK_SESSION_NAME + " " + i, COURSE_ID)
                            .withCreatorEmail(INSTRUCTOR_EMAIL)
                            .withStartTime(now.plus(Duration.ofMinutes(1)))
                            .withEndTime(now.plus(Duration.ofDays(1)))
                            .withSessionVisibleFromTime(now)
                            .withResultsVisibleFromTime(now.plus(Duration.ofDays(2)))
                            .build();

                    feedbackSessions.put(FEEDBACK_SESSION_NAME + " " + i, session);
                }

                return feedbackSessions;
            }

            @Override
            public List<String> generateCsvHeaders() {
                List<String> headers = new ArrayList<>();

                headers.add("loginId");
                headers.add("courseId");
                headers.add("updateData");

                return headers;
            }

            @Override
            public List<List<String>> generateCsvData() {
                DataBundle dataBundle = loadDataBundle(getJsonDataPath());
                List<List<String>> csvData = new ArrayList<>();

                dataBundle.instructors.forEach((key, instructor) -> {
                    List<String> csvRow = new ArrayList<>();

                    csvRow.add(INSTRUCTOR_ID);
                    csvRow.add(COURSE_ID);

                    CourseUpdateRequest courseUpdateRequest = new CourseUpdateRequest();
                    courseUpdateRequest.setCourseName(UPDATE_COURSE_NAME);
                    courseUpdateRequest.setTimeZone(UPDATE_COURSE_TIME_ZONE);

                    String updateData = sanitizeForCsv(JsonUtils.toJson(courseUpdateRequest));
                    csvRow.add(updateData);

                    csvData.add(csvRow);
                });

                return csvData;
            }
        };
    }

    private Map<String, String> getRequestHeaders() {
        Map<String, String> headers = new HashMap<>();

        headers.put(Const.HeaderNames.CSRF_TOKEN, "${csrfToken}");
        headers.put("Content-Type", "application/json");

        return headers;
    }

    private String getTestEndpoint() {
        return Const.ResourceURIs.COURSE + "?courseid=${courseId}";
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
        threadGroup.add(JMeterElements.httpSampler(getTestEndpoint(), PUT, "${updateData}"))
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

    @AfterClass
    public void classTearDown() throws IOException {
        deleteTestData();
        deleteDataFiles();
        cleanupResults();
    }
}
