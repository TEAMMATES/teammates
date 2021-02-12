package teammates.client.scripts;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;

import teammates.client.util.ClientProperties;
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
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.logic.api.Logic;
import teammates.storage.api.OfyHelper;

/**
 * Script to mock a course and populate large number of responses.
 */
public final class MockCourseWithLargeResponseScript {
    // Change the following params for different course setup
    private static final int NUMBER_OF_STUDENTS = 600;
    private static final int NUMBER_OF_TEAMS = 100;
    private static final int NUMBER_OF_FEEDBACK_QUESTIONS = 25;

    // Change the course ID to be recognizable
    private static final String COURSE_ID = "TestData.600S25Q100T";
    private static final String COURSE_NAME = "MockLargeCourse";
    private static final String COURSE_TIME_ZONE = "UTC";

    private static final String INSTRUCTOR_ID = "LoadInstructor_id";
    private static final String INSTRUCTOR_NAME = "LoadInstructor";
    private static final String INSTRUCTOR_EMAIL = "tmms.test@gmail.tmt";

    private static final String STUDENT_ID = "LoadStudent.tmms";
    private static final String STUDENT_NAME = "LoadStudent";
    private static final String STUDENT_EMAIL = "studentEmail@gmail.tmt";

    private static final String TEAM_NAME = "Team ";
    private static final String GIVER_SECTION_NAME = "Section 1";
    private static final String RECEIVER_SECTION_NAME = "Section 1";

    private static final String FEEDBACK_SESSION_NAME = "Test Feedback Session";

    private static final String FEEDBACK_QUESTION_ID = "QuestionTest";
    private static final String FEEDBACK_QUESTION_TEXT = "Test Question";

    private static final String FEEDBACK_RESPONSE_ID = "ResponseForQ";

    // Private constructor to prevent instantiation
    private MockCourseWithLargeResponseScript() {
        throw new UnsupportedOperationException();
    }

    private static void generateMockCourse() {
        try {
            Logic logic = new Logic();
            DataBundle data = generateDataBundle();
            logic.removeDataBundle(data);
            logic.persistDataBundle(data);
            System.out.println(data.feedbackResponses.size());
        } catch (InvalidParametersException e) {
            System.err.println(e);
        }
    }

    private static Map<String, AccountAttributes> generateAccounts() {
        return new HashMap<>();
    }

    private static Map<String, CourseAttributes> generateCourses() {
        Map<String, CourseAttributes> courses = new HashMap<>();

        courses.put(COURSE_NAME, CourseAttributes.builder(COURSE_ID)
                .withName(COURSE_NAME)
                .withTimezone(ZoneId.of(COURSE_TIME_ZONE))
                .build());

        return courses;
    }

    private static Map<String, InstructorAttributes> generateInstructors() {
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

    private static Map<String, StudentAttributes> generateStudents() {
        Map<String, StudentAttributes> students = new LinkedHashMap<>();
        StudentAttributes studentAttribute;

        for (int i = 1; i <= NUMBER_OF_STUDENTS; i++) {
            studentAttribute = StudentAttributes.builder(COURSE_ID, i + STUDENT_EMAIL)
                    .withGoogleId(STUDENT_ID + i)
                    .withName(STUDENT_NAME + i)
                    .withComment("This student's name is " + STUDENT_NAME + i)
                    .withSectionName(GIVER_SECTION_NAME)
                    .withTeamName(TEAM_NAME + i % NUMBER_OF_TEAMS)
                    .build();

            students.put(STUDENT_NAME + i, studentAttribute);
        }

        return students;
    }

    private static Map<String, FeedbackSessionAttributes> generateFeedbackSessions() {
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

    private static Map<String, FeedbackQuestionAttributes> generateFeedbackQuestions() {
        List<FeedbackParticipantType> showResponses = new ArrayList<>();
        showResponses.add(FeedbackParticipantType.RECEIVER);
        showResponses.add(FeedbackParticipantType.INSTRUCTORS);

        List<FeedbackParticipantType> showGiverName = new ArrayList<>();
        showGiverName.add(FeedbackParticipantType.INSTRUCTORS);

        List<FeedbackParticipantType> showRecepientName = new ArrayList<>();
        showRecepientName.add(FeedbackParticipantType.INSTRUCTORS);

        Map<String, FeedbackQuestionAttributes> feedbackQuestions = new LinkedHashMap<>();
        FeedbackQuestionDetails details = new FeedbackTextQuestionDetails(FEEDBACK_QUESTION_TEXT);

        for (int i = 1; i <= NUMBER_OF_FEEDBACK_QUESTIONS; i++) {
            feedbackQuestions.put(FEEDBACK_QUESTION_ID + " " + i,
                    FeedbackQuestionAttributes.builder()
                            .withFeedbackSessionName(FEEDBACK_SESSION_NAME)
                            .withQuestionDescription(FEEDBACK_QUESTION_TEXT)
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

    private static Map<String, FeedbackResponseAttributes> generateFeedbackResponses() {
        Map<String, FeedbackResponseAttributes> feedbackResponses = new HashMap<>();

        for (int i = 1; i <= NUMBER_OF_STUDENTS; i++) {
            for (int j = 1; j <= NUMBER_OF_STUDENTS; j++) {
                if (j % NUMBER_OF_TEAMS != i % NUMBER_OF_TEAMS) {
                    continue;
                }

                for (int k = 1; k <= NUMBER_OF_FEEDBACK_QUESTIONS; k++) {
                    String responseText = FEEDBACK_RESPONSE_ID + " " + k
                            + " from student " + i + " to student " + j;
                    FeedbackTextResponseDetails details =
                            new FeedbackTextResponseDetails(responseText);

                    feedbackResponses.put(responseText,
                            FeedbackResponseAttributes.builder(
                                    Integer.toString(k),
                                    i + STUDENT_EMAIL,
                                    j + STUDENT_EMAIL)
                                    .withCourseId(COURSE_ID)
                                    .withFeedbackSessionName(FEEDBACK_SESSION_NAME)
                                    .withGiverSection(GIVER_SECTION_NAME)
                                    .withRecipientSection(RECEIVER_SECTION_NAME)
                                    .withResponseDetails(details)
                                    .build());
                }
            }
        }
        System.out.println(feedbackResponses.size());

        return feedbackResponses;
    }

    private static DataBundle generateDataBundle() {
        DataBundle dataBundle = new DataBundle();

        dataBundle.accounts = generateAccounts();
        dataBundle.courses = generateCourses();
        dataBundle.instructors = generateInstructors();
        dataBundle.students = generateStudents();
        dataBundle.feedbackSessions = generateFeedbackSessions();
        dataBundle.feedbackQuestions = generateFeedbackQuestions();
        dataBundle.feedbackResponses = generateFeedbackResponses();

        return dataBundle;
    }

    private static void setupRemoteClientAndPopulateCourse() throws IOException {
        String appUrl = ClientProperties.TARGET_URL.replaceAll("^https?://", "");
        String appDomain = appUrl.split(":")[0];
        int appPort = appUrl.contains(":") ? Integer.parseInt(appUrl.split(":")[1]) : 443;

        System.out.println("--- Starting remote operation ---");
        System.out.println("Going to connect to:" + appDomain + ":" + appPort);

        RemoteApiOptions options = new RemoteApiOptions().server(appDomain, appPort);

        if (ClientProperties.isTargetUrlDevServer()) {
            // Dev Server doesn't require credential.
            options.useDevelopmentServerCredential();
        } else {
            // Your Google Cloud SDK needs to be authenticated for Application Default Credentials
            // in order to run any script in production server.
            // Refer to https://developers.google.com/identity/protocols/application-default-credentials.
            options.useApplicationDefaultCredential();
        }

        RemoteApiInstaller installer = new RemoteApiInstaller();
        installer.install(options);

        OfyHelper.registerEntityClasses();
        Closeable objectifySession = ObjectifyService.begin();

        try {
            generateMockCourse();
        } finally {
            objectifySession.close();
            installer.uninstall();
        }

        System.out.println("--- Remote operation completed ---");
    }

    public static void main(String[] args) throws IOException {
        setupRemoteClientAndPopulateCourse();
    }

}
