package teammates.lnp.cases;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
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
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.common.exception.HttpRequestFailedException;
import teammates.common.util.Const;
import teammates.lnp.util.JMeterElements;
import teammates.lnp.util.LNPSpecification;
import teammates.lnp.util.LNPTestData;

/**
 * L&P Test Case for instructor viewing feedback sessions results.
 */
public class InstructorSessionResultLNPTest extends BaseLNPTestCase {
    private static final int RAMP_UP_PERIOD = 2;
    private static final int NUMBER_OF_USER_ACCOUNTS = 500;
    private static final int NUMBER_OF_QUESTIONS = 10;
    private static final int SIZE_OF_TEAM = 4;
    private static final int SIZE_OF_SECTION = 100;
    private static final String STUDENT_NAME = "LnPStudent";
    private static final String STUDENT_EMAIL = "studentEmail";
    private static final String INSTRUCTOR_EMAIL = "tmms.test@gmail.tmt";

    private static final String COURSE_ID = "TestData.CS101";
    private static final String FEEDBACK_SESSION_NAME = "Test Feedback Session";

    private static final double ERROR_RATE_LIMIT = 0.01;
    private static final double MEAN_RESP_TIME_LIMIT = 7;

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
                            .withSectionName("Section " + (i / SIZE_OF_SECTION))
                            .withTeamName("Team " + (i / SIZE_OF_TEAM))
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
                FeedbackQuestionDetails details = new FeedbackTextQuestionDetails("Test Question");
                for (int i = 1; i <= NUMBER_OF_QUESTIONS; i++) {
                    feedbackQuestions.put("QuestionTest " + i,
                            FeedbackQuestionAttributes.builder()
                                    .withFeedbackSessionName(FEEDBACK_SESSION_NAME)
                                    .withQuestionDescription("Test Question " + i)
                                    .withCourseId(COURSE_ID)
                                    .withQuestionDetails(details)
                                    .withQuestionNumber(i)
                                    .withGiverType(FeedbackParticipantType.STUDENTS)
                                    .withRecipientType(FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF)
                                    .withShowResponsesTo(showResponses)
                                    .withShowGiverNameTo(showGiverName)
                                    .withShowRecipientNameTo(showRecepientName)
                                    .build()
                    );
                }
                return feedbackQuestions;
            }

            @Override
            protected Map<String, FeedbackResponseAttributes> generateFeedbackResponses() {
                Map<String, FeedbackResponseAttributes> feedbackResponses = new HashMap<>();

                for (int i = 1; i <= NUMBER_OF_QUESTIONS; i++) {
                    for (int j = 0; j < NUMBER_OF_USER_ACCOUNTS; j += SIZE_OF_TEAM) {
                        for (int k = j; k < j + SIZE_OF_TEAM; k++) {
                            for (int l = j; l < j + SIZE_OF_TEAM; l++) {
                                String responseText = "ResponseForQ" + i + "From" + k + "To" + l;
                                FeedbackTextResponseDetails details =
                                        new FeedbackTextResponseDetails(responseText);

                                feedbackResponses.put(responseText,
                                        FeedbackResponseAttributes.builder(Integer.toString(i),
                                            STUDENT_EMAIL + k + "@gmail.tmt",
                                            STUDENT_EMAIL + l + "@gmail.tmt")
                                            .withCourseId(COURSE_ID)
                                            .withFeedbackSessionName(FEEDBACK_SESSION_NAME)
                                            .withGiverSection("Section " + k / 100)
                                            .withRecipientSection("Section " + l / 100)
                                            .withResponseDetails(details)
                                            .build());
                            }
                        }
                    }
                }
                return feedbackResponses;
            }

            @Override
            public List<String> generateCsvHeaders() {
                List<String> headers = new ArrayList<>();

                headers.add("loginId");
                headers.add("courseId");
                headers.add("fsname");

                // For loading section panel for each controller.
                for (int i = 1; i <= NUMBER_OF_USER_ACCOUNTS / SIZE_OF_SECTION; i++) {
                    headers.add("sectionNumber_" + i);
                }

                for (int i = 1; i <= NUMBER_OF_QUESTIONS; i++) {
                    headers.add("feedbackQuestion_" + i);
                }

                return headers;
            }

            @Override
            public List<List<String>> generateCsvData() {
                DataBundle dataBundle = loadDataBundle(getJsonDataPath());
                List<List<String>> csvData = new ArrayList<>();

                dataBundle.instructors.forEach((key, instructor) -> {
                    List<String> csvRow = new ArrayList<>();

                    csvRow.add(instructor.getGoogleId()); // "googleId" is used for logging in, not "email"
                    csvRow.add(COURSE_ID);
                    csvRow.add(FEEDBACK_SESSION_NAME);

                    // For loading section panel for each controller.
                    for (int i = 1; i <= NUMBER_OF_USER_ACCOUNTS / SIZE_OF_SECTION; i++) {
                        csvRow.add(Integer.toString(i));
                    }

                    // For loading feedback question IDs
                    dataBundle.feedbackQuestions.forEach((feedbackQuestionKey, fq) -> {
                        FeedbackQuestionAttributes fqa = backdoor.getFeedbackQuestion(
                                fq.getCourseId(), fq.getFeedbackSessionName(), fq.getQuestionNumber());
                        csvRow.add(fqa.getId());
                    });

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
                JMeterElements.threadGroup(1, RAMP_UP_PERIOD, 1));

        threadGroup.add(JMeterElements.csvDataSet(getPathToTestDataFile(getCsvConfigPath())));
        threadGroup.add(JMeterElements.cookieManager());
        threadGroup.add(JMeterElements.defaultSampler());
        threadGroup.add(JMeterElements.onceOnlyController())
                .add(JMeterElements.loginSampler());

        // Set query param.
        Map<String, String> sectionsArgumentsMap = new HashMap<>();
        sectionsArgumentsMap.put("courseid", "${courseId}");

        Map<String, String> argumentsMap = new HashMap<>(sectionsArgumentsMap);
        argumentsMap.put("fsname", "${fsname}");
        argumentsMap.put("intent", "INSTRUCTOR_RESULT");

        addLoadPageController(threadGroup, argumentsMap);
        addLoadSectionsController(threadGroup, sectionsArgumentsMap);
        addLoadNoResponsePanelController(threadGroup, argumentsMap);
        addLoadQuestionPanelController(threadGroup, argumentsMap);
        addLoadSectionPanelController(threadGroup, argumentsMap);

        return testPlan;
    }

    @Override
    protected void setupSpecification() {
        this.specification = LNPSpecification.builder()
                .withErrorRateLimit(ERROR_RATE_LIMIT)
                .withMeanRespTimeLimit(MEAN_RESP_TIME_LIMIT)
                .build();
    }

    private void addLoadPageController(HashTree threadGroup, Map<String, String> argumentsMap) {
        HashTree loadPageController = threadGroup.add(JMeterElements.genericController());

        loadPageController.add(JMeterElements.defaultSampler(argumentsMap));

        String getSessionPath = Const.ResourceURIs.SESSION;
        loadPageController.add(JMeterElements.httpGetSampler(getSessionPath));

        String getQuestionsPath = Const.ResourceURIs.QUESTIONS;
        loadPageController.add(JMeterElements.httpGetSampler(getQuestionsPath));
    }

    private void addLoadSectionsController(HashTree threadGroup, Map<String, String> sectionsArgumentsMap) {
        HashTree loadSectionsController = threadGroup.add(JMeterElements.genericController());

        loadSectionsController.add(JMeterElements.defaultSampler(sectionsArgumentsMap));

        String getSectionsPath = Const.ResourceURIs.COURSE_SECTIONS;
        loadSectionsController.add(JMeterElements.httpGetSampler(getSectionsPath));
    }

    private void addLoadNoResponsePanelController(HashTree threadGroup, Map<String, String> argumentsMap) {
        HashTree loadNoResponsePanelController = threadGroup.add(JMeterElements.genericController());

        loadNoResponsePanelController.add(JMeterElements.defaultSampler(argumentsMap));
        String getStudentsPath = Const.ResourceURIs.STUDENTS;
        loadNoResponsePanelController.add(JMeterElements.httpGetSampler(getStudentsPath));

        String getSubmittedGiverSetPath = Const.ResourceURIs.SESSION_SUBMITTED_GIVER_SET;
        loadNoResponsePanelController.add(JMeterElements.httpGetSampler(getSubmittedGiverSetPath));
    }

    private void addLoadQuestionPanelController(HashTree threadGroup, Map<String, String> argumentsMap) {
        HashTree loadQuestionPanelController = threadGroup.add(JMeterElements.genericController());

        loadQuestionPanelController.add(JMeterElements.defaultSampler(argumentsMap));

        for (int i = 1; i <= NUMBER_OF_QUESTIONS; i++) {
            String getSessionResultPath =
                    String.format(Const.ResourceURIs.RESULT + "?questionid=${feedbackQuestion_%d}", i);
            loadQuestionPanelController.add(JMeterElements.httpGetSampler(getSessionResultPath));
        }
    }

    private void addLoadSectionPanelController(HashTree threadGroup, Map<String, String> argumentsMap) {
        HashTree loadSectionPanelController = threadGroup.add(
                JMeterElements.foreachController("sectionNumber", "sectionNumber"));

        loadSectionPanelController.add(JMeterElements.defaultSampler(argumentsMap));

        for (int i = 1; i <= NUMBER_OF_USER_ACCOUNTS / SIZE_OF_SECTION; i++) {
            String getSessionResultPath =
                    String.format(Const.ResourceURIs.RESULT + "?frgroupbysection=Section ${sectionNumber_%d}", i);
            loadSectionPanelController.add(JMeterElements.httpGetSampler(getSessionResultPath));
        }
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
