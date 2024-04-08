package teammates.test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.datatransfer.SqlDataBundle;
import teammates.common.datatransfer.questions.FeedbackResponseDetails;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackTextResponseDetails;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.common.util.TimeHelperExtension;
import teammates.sqllogic.core.DataBundleLogic;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.AccountRequest;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Notification;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;

/**
 * Base class for all test cases.
 */
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
        print(" * " + description);
    }
    // CHECKSTYLE.ON:AbbreviationAsWordInName|MethodName

    @BeforeClass
    public void printTestClassHeader() {
        print("[============================="
                + getClass().getCanonicalName()
                + "=============================]");
    }

    @AfterClass
    public void printTestClassFooter() {
        print(getClass().getCanonicalName() + " completed");
    }

    protected static void print(String message) {
        System.out.println(message);
    }

    protected String getTestDataFolder() {
        return TestProperties.TEST_DATA_FOLDER;
    }

    /**
     * Creates a DataBundle as specified in typicalDataBundle.json.
     */
    protected DataBundle getTypicalDataBundle() {
        return loadDataBundle("/typicalDataBundle.json");
    }

    protected DataBundle loadDataBundle(String jsonFileName) {
        try {
            String pathToJsonFile = getTestDataFolder() + jsonFileName;
            String jsonString = FileHelper.readFile(pathToJsonFile);
            return JsonUtils.fromJson(jsonString, DataBundle.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected SqlDataBundle getTypicalSqlDataBundle() {
        return loadSqlDataBundle("/typicalDataBundle.json");
    }

    protected SqlDataBundle loadSqlDataBundle(String jsonFileName) {
        try {
            // TODO: rename to loadDataBundle after migration
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
        Course course = getTypicalCourse();
        return new Student(course, "student-name", "validstudent@teammates.tmt", "comments");
    }

    protected Section getTypicalSection() {
        Course course = getTypicalCourse();
        return new Section(course, "test-section");
    }

    protected Team getTypicalTeam() {
        Section section = getTypicalSection();
        return new Team(section, "test-team");
    }

    protected FeedbackSession getTypicalFeedbackSessionForCourse(Course course) {
        Instant startTime = TimeHelperExtension.getInstantDaysOffsetFromNow(1);
        Instant endTime = TimeHelperExtension.getInstantDaysOffsetFromNow(7);
        return new FeedbackSession("test-feedbacksession",
                course,
                "test@teammates.tmt",
                "test-instructions",
                startTime,
                endTime,
                startTime,
                endTime,
                Duration.ofMinutes(5),
                false,
                false,
                false);
    }

    protected FeedbackQuestion getTypicalFeedbackQuestionForSession(FeedbackSession session) {
        return FeedbackQuestion.makeQuestion(session, 1, "test-description",
                FeedbackParticipantType.SELF, FeedbackParticipantType.SELF, 1, new ArrayList<FeedbackParticipantType>(),
                new ArrayList<FeedbackParticipantType>(), new ArrayList<FeedbackParticipantType>(),
                new FeedbackTextQuestionDetails("test question text"));
    }

    protected FeedbackResponse getTypicalFeedbackResponseForQuestion(FeedbackQuestion question) {
        return FeedbackResponse.makeResponse(question, "test-giver", getTypicalSection(), "test-recipient",
                getTypicalSection(), getTypicalFeedbackResponseDetails());
    }

    protected FeedbackResponseDetails getTypicalFeedbackResponseDetails() {
        return new FeedbackTextResponseDetails();
    }

    protected FeedbackResponseComment getTypicalResponseComment(Long id) {
        FeedbackResponseComment comment = new FeedbackResponseComment(null, "",
                FeedbackParticipantType.STUDENTS, null, null, "",
                false, false,
                null, null, null);
        comment.setId(id);
        return comment;
    }

    protected AccountRequest getTypicalAccountRequest() {
        return new AccountRequest("valid@test.com", "Test account Name", "TEAMMATES Test Institute 1");
    }

    /**
     * Populates the feedback question and response IDs within the data bundle.
     *
     * <p>For tests where simulated database is used, the backend will assign the question and response IDs
     * when the entities are persisted into the database, and modify the relation IDs accordingly.
     * However, for tests that do not use simulated database (e.g. pure data structure tests),
     * the assignment of IDs have to be simulated.
     */
    protected void populateQuestionAndResponseIds(DataBundle dataBundle) {
        Map<String, Map<Integer, String>> sessionToQuestionNumberToId = new HashMap<>();

        dataBundle.feedbackQuestions.forEach((key, question) -> {
            // Assign the same ID as the key as a later function requires a match between the key and the question ID
            question.setId(key);
            Map<Integer, String> questionNumberToId = sessionToQuestionNumberToId.computeIfAbsent(
                    question.getCourseId() + "%" + question.getFeedbackSessionName(), k -> new HashMap<>());
            questionNumberToId.put(question.getQuestionNumber(), key);
        });

        dataBundle.feedbackResponses.forEach((key, response) -> {
            response.setId(key);
            String feedbackQuestionId = sessionToQuestionNumberToId
                    .get(response.getCourseId() + "%" + response.getFeedbackSessionName())
                    .get(Integer.valueOf(response.getFeedbackQuestionId()));
            response.setFeedbackQuestionId(feedbackQuestionId);
        });
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
        Assert.assertTrue(condition);
    }

    protected static void assertTrue(String message, boolean condition) {
        Assert.assertTrue(message, condition);
    }

    protected static void assertFalse(boolean condition) {
        Assert.assertFalse(condition);
    }

    protected static void assertFalse(String message, boolean condition) {
        Assert.assertFalse(message, condition);
    }

    protected static void assertEquals(int expected, int actual) {
        Assert.assertEquals(expected, actual);
    }

    protected static void assertEquals(String message, int expected, int actual) {
        Assert.assertEquals(message, expected, actual);
    }

    protected static void assertEquals(long expected, long actual) {
        Assert.assertEquals(expected, actual);
    }

    protected static void assertEquals(double expected, double actual, double delta) {
        Assert.assertEquals(expected, actual, delta);
    }

    protected static void assertEquals(Object expected, Object actual) {
        Assert.assertEquals(expected, actual);
    }

    protected static void assertEquals(String message, Object expected, Object actual) {
        Assert.assertEquals(message, expected, actual);
    }

    protected static void assertArrayEquals(byte[] expected, byte[] actual) {
        Assert.assertArrayEquals(expected, actual);
    }

    protected static void assertNotEquals(Object first, Object second) {
        Assert.assertNotEquals(first, second);
    }

    protected static void assertSame(Object expected, Object actual) {
        Assert.assertSame(expected, actual);
    }

    protected static void assertNotSame(Object unexpected, Object actual) {
        Assert.assertNotSame(unexpected, actual);
    }

    protected static void assertNull(Object object) {
        Assert.assertNull(object);
    }

    protected static void assertNull(String message, Object object) {
        Assert.assertNull(message, object);
    }

    protected static void assertNotNull(Object object) {
        Assert.assertNotNull(object);
    }

    protected static void assertNotNull(String message, Object object) {
        Assert.assertNotNull(message, object);
    }

    protected static void fail(String message) {
        Assert.fail(message);
    }

    // This method is adapted from JUnit 5's assertThrows.
    // Once we upgrade to JUnit 5, their built-in method shall be used instead.
    @SuppressWarnings({
            "unchecked",
            "PMD.AvoidCatchingThrowable", // As per reference method's specification
    })
    protected static <T extends Throwable> T assertThrows(Class<T> expectedType, Executable executable) {
        try {
            executable.execute();
        } catch (Throwable actualException) {
            if (expectedType.isInstance(actualException)) {
                return (T) actualException;
            } else {
                String message = String.format("Expected %s to be thrown, but %s was instead thrown.",
                        getCanonicalName(expectedType), getCanonicalName(actualException.getClass()));
                throw new AssertionError(message, actualException);
            }
        }

        String message = String.format("Expected %s to be thrown, but nothing was thrown.", getCanonicalName(expectedType));
        throw new AssertionError(message);
    }

    private static String getCanonicalName(Class<?> clazz) {
        String canonicalName = clazz.getCanonicalName();
        return canonicalName == null ? clazz.getName() : canonicalName;
    }

    /**
     * {@code Executable} is a functional interface that can be used to
     * implement any generic block of code that potentially throws a
     * {@link Throwable}.
     *
     * <p>The {@code Executable} interface is similar to {@link Runnable},
     * except that an {@code Executable} can throw any kind of exception.
     */
    // This interface is adapted from JUnit 5's Executable interface.
    // Once we upgrade to JUnit 5, this interface shall no longer be necessary.
    public interface Executable {

        /**
         * Executes a block of code, potentially throwing a {@link Throwable}.
         */
        // CHECKSTYLE.OFF:IllegalThrows
        void execute() throws Throwable;
        // CHECKSTYLE.ON:IllegalThrows

    }
}
