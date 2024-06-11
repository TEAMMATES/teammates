package teammates.lnp.cases;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import teammates.common.exception.HttpRequestFailedException;
import teammates.common.util.Const;
import teammates.lnp.util.JMeterElements;
import teammates.lnp.util.LNPSpecification;
import teammates.lnp.util.LNPTestData;

/**
 * L&P Test Case for students accessing feedback sessions.
 */
public class FeedbackSessionViewLNPTest extends BaseLNPTestCase {

    private static final int NUMBER_OF_USER_ACCOUNTS = 10;
    private static final int RAMP_UP_PERIOD = 2;
    private static final String STUDENT_NAME = "LnPStudent";
    private static final String STUDENT_EMAIL = "personalEmail";

    private static final String INSTRUCTOR_EMAIL = "tmms.test@gmail.tmt";

    private static final String COURSE_ID = "TestData.CS101";
    private static final String FEEDBACK_SESSION_NAME = "Test Feedback Session";

    private static final int NUMBER_OF_QUESTIONS = 10;

    private static final double ERROR_RATE_LIMIT = 0.01;
    private static final double MEAN_RESP_TIME_LIMIT = 1;

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
                        .withTimezone("UTC")
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
                                                            .withStartTime(Instant.now().plusMillis(100))
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
                showResponses.add(FeedbackParticipantType.RECEIVER);
                showResponses.add(FeedbackParticipantType.INSTRUCTORS);
                List<FeedbackParticipantType> showGiverName = new ArrayList<>();
                showGiverName.add(FeedbackParticipantType.INSTRUCTORS);
                List<FeedbackParticipantType> showRecepientName = new ArrayList<>();
                showRecepientName.add(FeedbackParticipantType.INSTRUCTORS);
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
                headers.add("googleId");
                headers.add("courseId");
                headers.add("fsname");

                return headers;
            }

            @Override
            public List<List<String>> generateCsvData() {
                DataBundle dataBundle = loadDataBundle(getJsonDataPath());
                List<List<String>> csvData = new ArrayList<>();

                dataBundle.students.forEach((key, student) -> {
                    List<String> csvRow = new ArrayList<>();

                    csvRow.add(student.getGoogleId()); // "googleId" is used for logging in, not "email"
                    csvRow.add(student.getGoogleId());
                    csvRow.add(COURSE_ID);
                    csvRow.add(FEEDBACK_SESSION_NAME);

                    csvData.add(csvRow);
                });

                return csvData;
            }
        };
    }

    @Override
    protected HashTree getLnpTestPlan() {
        HashTree testPlan = new ListedHashTree(JMeterElements.testPlan());
        HashTree threadGroup = testPlan.add(
                JMeterElements.threadGroup(NUMBER_OF_USER_ACCOUNTS, RAMP_UP_PERIOD, 1));
        threadGroup.add(JMeterElements.csvDataSet(getPathToTestDataFile(getCsvConfigPath())));
        threadGroup.add(JMeterElements.cookieManager());
        threadGroup.add(JMeterElements.defaultSampler());
        threadGroup.add(JMeterElements.onceOnlyController())
                .add(JMeterElements.loginSampler());

        // Add HTTP samplers for test endpoint
        String getSessionsPath = Const.ResourceURIs.STUDENT + "?courseid=${courseId}";
        threadGroup.add(JMeterElements.httpSampler(getSessionsPath, GET, null));

        String getSessionDetailsPath = Const.ResourceURIs.SESSION
                + "?courseid=${courseId}&fsname=${fsname}&intent=STUDENT_SUBMISSION";
        threadGroup.add(JMeterElements.httpSampler(getSessionDetailsPath, GET, null));

        String getQuestionsPath = Const.ResourceURIs.QUESTIONS
                + "?courseid=${courseId}&fsname=${fsname}&intent=STUDENT_SUBMISSION";
        threadGroup.add(JMeterElements.httpSampler(getQuestionsPath, GET, null));

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
