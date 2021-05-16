package teammates.lnp.cases;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
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
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.lnp.util.JMeterElements;
import teammates.lnp.util.LNPSpecification;
import teammates.lnp.util.LNPTestData;
import teammates.ui.request.FeedbackResponsesRequest;

/**
 * L&P Test Case for students submitting feedback questions.
 */
public class FeedbackSessionSubmitLNPTest extends BaseLNPTestCase {

    private static final int NUMBER_OF_USER_ACCOUNTS = 10;
    private static final int RAMP_UP_PERIOD = 2;
    private static final String STUDENT_NAME = "LnPStudent";
    private static final String STUDENT_EMAIL = "personalEmail";

    private static final String INSTRUCTOR_EMAIL = "tmms.test@gmail.tmt";

    private static final String COURSE_ID = "TestData.CS101";
    private static final String FEEDBACK_SESSION_NAME = "Test Feedback Session";

    private static final int NUMBER_OF_QUESTIONS = 20;

    private static final double ERROR_RATE_LIMIT = 0.01;
    private static final double MEAN_RESP_TIME_LIMIT = 2;

    @Override
    protected LNPTestData getTestData() {
        return new LNPTestData() {
            @Override
            protected Map<String, AccountAttributes> generateAccounts() {
                Map<String, AccountAttributes> accounts = new LinkedHashMap<>();

                for (int i = 0; i < NUMBER_OF_USER_ACCOUNTS; i++) {
                    accounts.put(STUDENT_NAME + i, AccountAttributes.builder(STUDENT_NAME + i + ".tmms")
                            .withEmail(STUDENT_EMAIL + i + "@gmail.tmt")
                            .withName(STUDENT_NAME + i)
                            .withIsInstructor(false)
                            .withInstitute("TEAMMATES Test Institute 2")
                            .build()
                    );
                }

                return accounts;
            }

            @Override
            protected Map<String, CourseAttributes> generateCourses() {
                Map<String, CourseAttributes> courses = new LinkedHashMap<>();

                courses.put("course", CourseAttributes.builder(COURSE_ID)
                        .withName("Feedback Load Testing")
                        .withTimezone(ZoneId.of("UTC"))
                        .build()
                );

                return courses;
            }

            @Override
            protected Map<String, InstructorAttributes> generateInstructors() {
                Map<String, InstructorAttributes> instructors = new LinkedHashMap<>();

                instructors.put("teammates.test.instructor",
                        InstructorAttributes.builder(COURSE_ID, INSTRUCTOR_EMAIL)
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
                Map<String, StudentAttributes> students = new LinkedHashMap<>();
                StudentAttributes studentAttribute;

                for (int i = 0; i < NUMBER_OF_USER_ACCOUNTS; i++) {
                    studentAttribute = StudentAttributes.builder(COURSE_ID, STUDENT_EMAIL + i + "@gmail.tmt")
                                            .withGoogleId(STUDENT_NAME + i + ".tmms")
                                            .withName(STUDENT_NAME + i)
                                            .withComment("This student's name is " + STUDENT_NAME + i)
                                            .withSectionName("None")
                                            .withTeamName("Team 1")
                                            .build();
                    students.put(STUDENT_NAME + i, studentAttribute);
                }

                return students;
            }

            @Override
            protected Map<String, FeedbackSessionAttributes> generateFeedbackSessions() {
                Map<String, FeedbackSessionAttributes> feedbackSessions = new LinkedHashMap<>();

                FeedbackSessionAttributes session = FeedbackSessionAttributes
                                                            .builder(FEEDBACK_SESSION_NAME, COURSE_ID)
                                                            .withCreatorEmail(INSTRUCTOR_EMAIL)
                                                            .withStartTime(Instant.now())
                                                            .withEndTime(Instant.now().plusSeconds(500))
                                                            .withSessionVisibleFromTime(Instant.now())
                                                            .withResultsVisibleFromTime(Instant.now())
                                                            .build();

                feedbackSessions.put(FEEDBACK_SESSION_NAME, session);

                return feedbackSessions;
            }

            @Override
            protected Map<String, FeedbackQuestionAttributes> generateFeedbackQuestions() {
                List<FeedbackParticipantType> showResponses = new ArrayList<>();
                showResponses.add(FeedbackParticipantType.INSTRUCTORS);
                showResponses.add(FeedbackParticipantType.RECEIVER);
                List<FeedbackParticipantType> showGiverName = new ArrayList<>();
                showGiverName.add(FeedbackParticipantType.INSTRUCTORS);
                showResponses.add(FeedbackParticipantType.RECEIVER);
                List<FeedbackParticipantType> showRecepientName = new ArrayList<>();
                showRecepientName.add(FeedbackParticipantType.INSTRUCTORS);
                showResponses.add(FeedbackParticipantType.RECEIVER);
                Map<String, FeedbackQuestionAttributes> feedbackQuestions = new LinkedHashMap<>();
                for (int i = 1; i <= NUMBER_OF_QUESTIONS; i++) {
                    FeedbackQuestionDetails details = new FeedbackTextQuestionDetails("Test Question" + i);
                    feedbackQuestions.put("QuestionTest" + i,
                            FeedbackQuestionAttributes.builder()
                                .withFeedbackSessionName(FEEDBACK_SESSION_NAME)
                                    .withQuestionDescription("Test Question" + i)
                                .withCourseId(COURSE_ID)
                                .withQuestionDetails(details)
                                .withQuestionNumber(i)
                                .withGiverType(FeedbackParticipantType.STUDENTS)
                                .withRecipientType(FeedbackParticipantType.SELF)
                                .withShowResponsesTo(showResponses)
                                .withShowGiverNameTo(showGiverName)
                                .withShowRecipientNameTo(showRecepientName)
                                .withNumberOfEntitiesToGiveFeedbackTo(1)
                                .build()
                    );

                }

                return feedbackQuestions;
            }

            @Override
            public List<String> generateCsvHeaders() {
                List<String> headers = new ArrayList<>();

                headers.add("loginId");
                headers.add("isAdmin");
                headers.add("googleId");
                headers.add("studentEmail");
                for (int i = 1; i <= NUMBER_OF_QUESTIONS; i++) {
                    headers.add("question" + i + "id");
                }

                return headers;
            }

            @Override
            public List<List<String>> generateCsvData() {
                DataBundle dataBundle = loadDataBundle(getJsonDataPath());
                List<List<String>> csvData = new ArrayList<>();

                dataBundle.students.forEach((studentKey, student) -> {
                    List<String> csvRow = new ArrayList<>();

                    csvRow.add(student.googleId); // "googleId" is used for logging in, not "email"
                    csvRow.add("no");
                    csvRow.add(student.googleId);
                    csvRow.add(student.email);

                    dataBundle.feedbackQuestions.forEach((feedbackQuestionKey, feedbackQuestion) -> {
                        csvRow.add(feedbackQuestion.getId());
                    });

                    csvData.add(csvRow);

                });

                return csvData;
            }
        };
    }

    private Map<String, String> getRequestHeaders() {
        Map<String, String> headers = new LinkedHashMap<>();

        headers.put("X-CSRF-TOKEN", "${csrfToken}");
        headers.put("Content-Type", "application/json");

        return headers;
    }

    @Override
    protected ListedHashTree getLnpTestPlan() {
        ListedHashTree testPlan = new ListedHashTree(JMeterElements.testPlan());
        HashTree threadGroup = testPlan.add(
                JMeterElements.threadGroup(NUMBER_OF_USER_ACCOUNTS, RAMP_UP_PERIOD, 1));
        threadGroup.add(JMeterElements.csvDataSet(getPathToTestDataFile(getCsvConfigPath())));
        threadGroup.add(JMeterElements.cookieManager());
        threadGroup.add(JMeterElements.defaultSampler());
        threadGroup.add(JMeterElements.onceOnlyController())
                .add(JMeterElements.loginSampler())
                .add(JMeterElements.csrfExtractor("csrfToken"));

        HeaderManager headerManager = JMeterElements.headerManager(getRequestHeaders());
        threadGroup.add(headerManager);

        for (int i = 1; i <= NUMBER_OF_QUESTIONS; i++) {
            FeedbackResponsesRequest responsesRequest = new FeedbackResponsesRequest();

            FeedbackTextResponseDetails responseDetails = new FeedbackTextResponseDetails();
            responseDetails.setAnswer("<p>test</p>");
            FeedbackResponsesRequest.FeedbackResponseRequest responseRequest =
                    new FeedbackResponsesRequest.FeedbackResponseRequest("${studentEmail}", responseDetails);

            responsesRequest.setResponses(Collections.singletonList(responseRequest));
            String path = Const.ResourceURIs.RESPONSES + "?questionid=${question" + i + "id}"
                    + "&intent=STUDENT_SUBMISSION";
            threadGroup.add(JMeterElements.httpSampler(path, PUT, JsonUtils.toJson(responsesRequest)));
        }

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
    public void classSetup() {
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
