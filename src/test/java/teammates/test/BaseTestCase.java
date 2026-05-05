package teammates.test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.TimeHelperExtension;
import teammates.logic.core.DataBundleLogic;
import teammates.storage.entity.Account;
import teammates.storage.entity.AccountRequest;
import teammates.storage.entity.Course;
import teammates.storage.entity.DeadlineExtension;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackResponseComment;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Notification;
import teammates.storage.entity.Section;
import teammates.storage.entity.Student;
import teammates.storage.entity.Team;
import teammates.storage.entity.UsageStatistics;

/**
 * Base class for all test cases.
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class BaseTestCase {

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

    @BeforeClass
    public void printTestClassHeader() {
        System.out.println("[============================="
                + getClass().getCanonicalName()
                + "=============================]");
    }

    @AfterClass
    public void printTestClassFooter() {
        System.out.println(getClass().getCanonicalName() + " completed");
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
            return DataBundleLogic.deserializeDataBundle(jsonString);
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
        return new Account("google-id", "name", "email@teammates.com");
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
        InstructorPrivileges instructorPrivileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        InstructorPermissionRole role = InstructorPermissionRole
                .getEnum(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);

        return new Instructor(course, "instructor-name", "valid@teammates.tmt",
                false, Const.DEFAULT_DISPLAY_NAME_FOR_INSTRUCTOR, role, instructorPrivileges);
    }

    protected Course getTypicalCourse() {
        return new Course("course-id", "course-name", Const.DEFAULT_TIME_ZONE, "teammates");
    }

    protected Student getTypicalStudent() {
        Team team = getTypicalTeam();
        Course course = team.getSection().getCourse();
        Student student = new Student(course, "student-name", "validstudent@teammates.tmt", "comments");

        student.setTeam(team);
        team.addUser(student);

        return student;
    }

    protected Section getTypicalSection() {
        Course course = getTypicalCourse();
        Section section = new Section(course, "test-section");
        course.addSection(section);
        return section;
    }

    protected Team getTypicalTeam() {
        Section section = getTypicalSection();
        Team team = new Team(section, "test-team");
        section.addTeam(team);
        return team;
    }

    protected FeedbackResponseComment getTypicalFeedbackResponseComment() {
        FeedbackSession typicalFeedbackSession = getTypicalFeedbackSessionForCourse(getTypicalCourse());
        FeedbackQuestion typicalFeedbackQuestion = getTypicalFeedbackQuestionForSession(typicalFeedbackSession);
        FeedbackResponse typicalFeedbackResponse = getTypicalFeedbackResponseForQuestion(typicalFeedbackQuestion);
        FeedbackResponseComment feedbackResponseComment = new FeedbackResponseComment(typicalFeedbackResponse,
                "typical-giver", FeedbackParticipantType.RECEIVER, getTypicalSection(), getTypicalSection(),
                "typical-comment", true, true, List.of(FeedbackParticipantType.GIVER, FeedbackParticipantType.INSTRUCTORS),
                List.of(FeedbackParticipantType.RECEIVER, FeedbackParticipantType.INSTRUCTORS), "email");
        feedbackResponseComment.setId(UUID.fromString("00000000-0000-4000-8000-000000000010"));
        feedbackResponseComment.setCreatedAt(Instant.now());
        feedbackResponseComment.setUpdatedAt(Instant.now());
        return feedbackResponseComment;
    }

    protected FeedbackSession getTypicalFeedbackSessionForCourse(Course course) {
        Instant startTime = TimeHelperExtension.getInstantDaysOffsetFromNow(1);
        Instant endTime = TimeHelperExtension.getInstantDaysOffsetFromNow(7);
        FeedbackSession feedbackSession = new FeedbackSession("test-feedbacksession",
                "test@teammates.tmt",
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
        return FeedbackQuestion.makeQuestion(session, 1, "test-description",
                FeedbackParticipantType.SELF, FeedbackParticipantType.SELF, 1, new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(),
                new FeedbackTextQuestionDetails("test question text"));
    }

    protected FeedbackResponse getTypicalFeedbackResponseForQuestion(FeedbackQuestion question) {
        return FeedbackResponse.makeResponse(question, "test-giver", getTypicalSection(), "test-recipient",
                getTypicalSection(), getTypicalFeedbackResponseDetails());
    }

    protected FeedbackResponseDetails getTypicalFeedbackResponseDetails() {
        return new FeedbackTextResponseDetails();
    }

    protected FeedbackResponseComment getTypicalResponseComment(UUID id) {
        FeedbackResponseComment comment = new FeedbackResponseComment(null, "",
                FeedbackParticipantType.STUDENTS, null, null, "",
                false, false,
                null, null, null);
        comment.setId(id);
        return comment;
    }

    protected AccountRequest getTypicalAccountRequest() {
        return new AccountRequest("valid@test.com", "Test Name", "TEAMMATES Test Institute 1, Test Country",
                AccountRequestStatus.PENDING, "");
    }

    protected UsageStatistics getTypicalUsageStatistics() {
        return getTypicalUsageStatistics(Instant.parse("2011-01-01T00:00:00Z"));
    }

    protected UsageStatistics getTypicalUsageStatistics(Instant startTime) {
        return new UsageStatistics(startTime, 60, 2, 2, 2, 2, 2, 0, 0);
    }

    protected DeadlineExtension getTypicalDeadlineExtensionStudent() {
        return new DeadlineExtension(
                getTypicalStudent(),
                getTypicalFeedbackSessionForCourse(getTypicalCourse()),
                Instant.now());
    }

    protected DeadlineExtension getTypicalDeadlineExtensionInstructor() {
        return new DeadlineExtension(
                getTypicalInstructor(),
                getTypicalFeedbackSessionForCourse(getTypicalCourse()),
                Instant.now());
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

    protected static String getPopulatedEmptyStringErrorMessage(String messageTemplate, String fieldName, int maxLength)
            throws ReflectiveOperationException {
        return (String) invokeMethod(FieldValidator.class, "getPopulatedEmptyStringErrorMessage",
                new Class<?>[] { String.class, String.class, int.class },
                null, new Object[] { messageTemplate, fieldName, maxLength });
    }

    /*
     * Here are some of the most common assertion methods provided by JUnit.
     * They are copied here to prevent repetitive importing in test classes.
     */

    protected static void assertTrue(boolean condition) {
        Assertions.assertTrue(condition);
    }

    protected static void assertTrue(String message, boolean condition) {
        Assertions.assertTrue(condition, message);
    }

    protected static void assertFalse(boolean condition) {
        Assertions.assertFalse(condition);
    }

    protected static void assertFalse(String message, boolean condition) {
        Assertions.assertFalse(condition, message);
    }

    protected static void assertEquals(int expected, int actual) {
        Assertions.assertEquals(expected, actual);
    }

    protected static void assertEquals(String message, int expected, int actual) {
        Assertions.assertEquals(expected, actual, message);
    }

    protected static void assertEquals(long expected, long actual) {
        Assertions.assertEquals(expected, actual);
    }

    protected static void assertEquals(double expected, double actual, double delta) {
        Assertions.assertEquals(expected, actual, delta);
    }

    protected static void assertEquals(Object expected, Object actual) {
        Assertions.assertEquals(expected, actual);
    }

    protected static void assertEquals(String message, Object expected, Object actual) {
        Assertions.assertEquals(expected, actual, message);
    }

    protected static void assertArrayEquals(byte[] expected, byte[] actual) {
        Assertions.assertArrayEquals(expected, actual);
    }

    protected static void assertNotEquals(Object first, Object second) {
        Assertions.assertNotEquals(first, second);
    }

    protected static void assertSame(Object expected, Object actual) {
        Assertions.assertSame(expected, actual);
    }

    protected static void assertNotSame(Object unexpected, Object actual) {
        Assertions.assertNotSame(unexpected, actual);
    }

    protected static void assertNull(Object object) {
        Assertions.assertNull(object);
    }

    protected static void assertNull(String message, Object object) {
        Assertions.assertNull(object, message);
    }

    protected static void assertNotNull(Object object) {
        Assertions.assertNotNull(object);
    }

    protected static void assertNotNull(String message, Object object) {
        Assertions.assertNotNull(object, message);
    }

    protected static void fail(String message) {
        Assertions.fail(message);
    }

    protected static <T extends Throwable> T assertThrows(Class<T> expectedType, Executable executable) {
        return Assertions.assertThrows(expectedType, executable);
    }

}
