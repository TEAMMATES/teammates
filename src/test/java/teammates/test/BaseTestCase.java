package teammates.test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import teammates.common.datatransfer.AccountVerificationRequestStatus;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.datatransfer.Provider;
import teammates.common.datatransfer.participanttypes.QuestionGiverType;
import teammates.common.datatransfer.participanttypes.QuestionRecipientType;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.common.datatransfer.visibility.CommentVisibilityType;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.TimeHelperExtension;
import teammates.logic.core.DataBundleLogic;
import teammates.storage.entity.Account;
import teammates.storage.entity.AccountVerificationRequest;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Institute;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Notification;
import teammates.storage.entity.ResponseGiver;
import teammates.storage.entity.ResponseInstructorComment;
import teammates.storage.entity.ResponseRecipient;
import teammates.storage.entity.Section;
import teammates.storage.entity.Student;
import teammates.storage.entity.Team;

/**
 * Base class for all test cases.
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class BaseTestCase {

    /**
     * Test case name in the format of ClassName.methodName, e.g. {@code MyTestClass.testMyFunction}.
     * For e2e tests, this is set to the class name of the test case, e.g. {@code MyE2ETestCase}.
     */
    protected String currentTestName;

    /**
     * Test Segment divider. Used to divide a test case into logical sections.
     * The weird name is for easy spotting.
     *
     * @param description
     *            of the logical section. This will be printed.
     */
    // CHECKSTYLE.OFF:AbbreviationAsWordInName|MethodName the weird name is for easy spotting.
    public static void ______TS(String description) {
        System.out.println(" * " + description);
    }
    // CHECKSTYLE.ON:AbbreviationAsWordInName|MethodName

    @BeforeClass(alwaysRun = true)
    public void printTestClassHeader() {
        currentTestName = getClass().getSimpleName();
        System.out.println("[============================="
                + getClass().getCanonicalName()
                + "=============================]");
    }

    @AfterClass(alwaysRun = true)
    public void printTestClassFooter() {
        System.out.println(getClass().getCanonicalName() + " completed");
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(Method method) {
        currentTestName = method.getDeclaringClass().getSimpleName() + "." + method.getName();
    }

    protected String getTestDataFolder() {
        return TestProperties.TEST_DATA_FOLDER;
    }

    protected DataBundle getTypicalDataBundle() {
        return loadDataBundle("/typicalDataBundle.json");
    }

    protected DataBundle loadDataBundle(String jsonFileName) {
        try {
            String pathToJsonFile = getTestDataFolder() + jsonFileName;
            String jsonString = FileHelper.readFile(pathToJsonFile);
            return DataBundleLogic.deserializeDataBundle(jsonString, currentTestName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * These getTypicalX functions are used to generate typical entities for tests.
     * The entity fields can be changed using setter methods if needed.
     * New entity generator functions for tests should be added here, and follow the
     * same naming convention.
     *
     * <p>Example usage:
     * Account account = getTypicalAccount();
     * Student student = getTypicalStudent();
     * account.setEmail("newemail@teammates.com");
     * student.setName("New Student Name");
     */
    protected Account getTypicalAccount() {
        return new Account(Provider.TEAMMATES_DEV, "google-id",
                "tenant-id", "name", "email@teammates.com");
    }

    protected Notification getTypicalNotificationWithId() {
        Notification notification = new Notification(Instant.parse("2011-01-01T00:00:00Z"),
                Instant.parse("2099-01-01T00:00:00Z"), NotificationStyle.DANGER, NotificationTargetUser.GENERAL,
                "A deprecation note", "<p>Deprecation happens in three minutes</p>");
        notification.setId(UUID.randomUUID());
        return notification;
    }

    protected Instructor getTypicalInstructor() {
        Course course = getTypicalCourse();
        InstructorPermissionRole role = InstructorPermissionRole
                .getEnum(Const.InstructorPermissionRoleNames.COOWNER);

        return new Instructor(course, "instructor-name", "valid@teammates.tmt",
                false, Const.DEFAULT_DISPLAY_NAME_FOR_INSTRUCTOR, role);
    }

    protected Institute getTypicalInstitute() {
        return new Institute("teammates", "SG");
    }

    protected Course getTypicalCourse() {
        Course course = new Course("course-id", "course-name", Const.DEFAULT_TIME_ZONE);
        getTypicalInstitute().addCourse(course);
        return course;
    }

    protected Student getTypicalStudent() {
        Team team = getTypicalTeam();
        Course course = team.getSection().getCourse();
        Student student = new Student(course, "student-name", "validstudent@teammates.tmt", "comments");
        team.addUser(student);

        return student;
    }

    protected Section getTypicalSection() {
        Course course = getTypicalCourse();
        Section section = new Section("test-section");
        course.addSection(section);
        return section;
    }

    protected Team getTypicalTeam() {
        Section section = getTypicalSection();
        Team team = new Team("test-team");
        section.addTeam(team);
        return team;
    }

    protected ResponseInstructorComment getTypicalResponseInstructorComment() {
        FeedbackSession typicalFeedbackSession = getTypicalFeedbackSessionForCourse(getTypicalCourse());
        FeedbackQuestion typicalFeedbackQuestion = getTypicalFeedbackQuestionForSession(typicalFeedbackSession);
        FeedbackResponse typicalFeedbackResponse = getTypicalFeedbackResponseForQuestion(typicalFeedbackQuestion);
        Instructor commentGiver = getTypicalInstructor();
        ResponseInstructorComment responseInstructorComment = new ResponseInstructorComment(commentGiver,
                "typical-comment", List.of(CommentVisibilityType.GIVER, CommentVisibilityType.INSTRUCTORS),
                List.of(CommentVisibilityType.RECIPIENT, CommentVisibilityType.INSTRUCTORS), commentGiver);
        typicalFeedbackResponse.addResponseInstructorComment(responseInstructorComment);
        responseInstructorComment.setId(UUID.fromString("00000000-0000-4000-8000-000000000010"));
        responseInstructorComment.setCreatedAt(Instant.now());
        responseInstructorComment.setUpdatedAt(Instant.now());
        return responseInstructorComment;
    }

    protected FeedbackSession getTypicalFeedbackSessionForCourse(Course course) {
        Instant startTime = TimeHelperExtension.getInstantDaysOffsetFromNow(1);
        Instant endTime = TimeHelperExtension.getInstantDaysOffsetFromNow(7);
        FeedbackSession feedbackSession = new FeedbackSession("test-feedbacksession",
                null,
                "<p>test-instructions</p>",
                startTime,
                endTime,
                startTime,
                endTime,
                Duration.ofMinutes(5),
                false,
                false);
        course.addFeedbackSession(feedbackSession);
        return feedbackSession;
    }

    protected FeedbackQuestion getTypicalFeedbackQuestionForSession(FeedbackSession session) {
        FeedbackQuestion fq = FeedbackQuestion.makeQuestion(1, "test-description",
                QuestionGiverType.SESSION_CREATOR, QuestionRecipientType.SELF, 1, new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(),
                new FeedbackTextQuestionDetails("test question text"));
        session.addFeedbackQuestion(fq);
        return fq;
    }

    protected FeedbackResponse getTypicalFeedbackResponseForQuestion(FeedbackQuestion question) {
        Student receiver = getTypicalStudent();
        Student giver = getTypicalStudent();
        FeedbackResponse feedbackResponse = FeedbackResponse.makeResponse(
                new ResponseGiver(giver), new ResponseRecipient(receiver), getTypicalFeedbackResponseDetails());
        question.addFeedbackResponse(feedbackResponse);
        return feedbackResponse;
    }

    protected FeedbackResponseDetails getTypicalFeedbackResponseDetails() {
        return new FeedbackTextResponseDetails();
    }

    protected AccountVerificationRequest getTypicalAccountVerificationRequest() {
        AccountVerificationRequest accountVerificationRequest = new AccountVerificationRequest("valid@test.com", "Test Name",
                AccountVerificationRequestStatus.PENDING, "");
        getTypicalInstitute().addAccountVerificationRequest(accountVerificationRequest);
        getTypicalAccount().addAccountVerificationRequest(accountVerificationRequest);
        return accountVerificationRequest;
    }

    /**
     * Invokes the method named {@code methodName} as defined in the {@code definingClass}.
     * @param definingClass     the class which defines the method
     * @param parameterTypes    the parameter types of the method,
     *                          which must be passed in the same order defined in the method
     * @param invokingObject    the object which invokes the method, can be {@code null} if the method is static
     * @param args              the arguments to be passed to the method invocation
     */
    protected static Object invokeMethod(Class<?> definingClass, String methodName, Class<?>[] parameterTypes,
                                         Object invokingObject, Object[] args)
            throws ReflectiveOperationException {
        Method method = definingClass.getDeclaredMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method.invoke(invokingObject, args);
    }

    protected static String getPopulatedErrorMessage(String messageTemplate, String userInput,
                                                     String fieldName, String errorReason)
            throws ReflectiveOperationException {
        return getPopulatedErrorMessage(messageTemplate, userInput, fieldName, errorReason, 0);
    }

    protected static String getPopulatedErrorMessage(String messageTemplate, String userInput,
                                                     String fieldName, String errorReason, int maxLength)
            throws ReflectiveOperationException {
        return (String) invokeMethod(FieldValidator.class, "getPopulatedErrorMessage",
                                     new Class<?>[] { String.class, String.class, String.class, String.class, int.class },
                                     null, new Object[] { messageTemplate, userInput, fieldName, errorReason, maxLength });
    }

}
