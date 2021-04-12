package teammates.test;

import java.io.IOException;
import java.lang.reflect.Method;

import org.junit.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;

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

    protected static String getPopulatedEmptyStringErrorMessage(String messageTemplate, String fieldName, int maxLength) {
        return FieldValidator.getPopulatedEmptyStringErrorMessage(messageTemplate, fieldName, maxLength);
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
