package teammates.common.util;

import teammates.common.exception.NullPostParameterException;

/**
 * This class provides a set of static method to verify assumptions about the system.
 * When the real runtime condition differs from the assumed situation,
 * an unchecked {@link AssertionError} will be thrown at runtime.
 *
 * <p>Normally, we use Java assertion to do runtime checking, but GAE does not support the assertions.
 * The methods of this file is adapted from org.junit.Assert v4.10.
 *
 * @see org.junit.Assert
 */
public final class Assumption {

    private Assumption() {
        // utility class
        // Intentional private constructor to prevent instantiation.
    }

    /**
     * Asserts that a condition is true. If it isn't it throws an
     * AssertionFailedError with the given message.
     */
    public static void assertTrue(String message, boolean condition) {
        if (!condition) {
            fail(message);
        }
    }

    /**
     * Asserts that a condition is true. If it isn't it throws an
     * AssertionFailedError.
     */
    public static void assertTrue(boolean condition) {
        assertTrue(null, condition);
    }

    /**
     * Asserts that a condition is false. If it isn't it throws an
     * AssertionFailedError with the given message.
     */
    public static void assertFalse(String message, boolean condition) {
        assertTrue(message, !condition);
    }

    /**
     * Asserts that a condition is false. If it isn't it throws an
     * AssertionFailedError.
     */
    public static void assertFalse(boolean condition) {
        assertFalse(null, condition);
    }

    /**
     * Fails a test with the given message.
     */
    public static void fail(String message) {
        throw new AssertionError(message);
    }

    /**
     * Fails a test with no message.
     */
    public static void fail() {
        fail(null);
    }

    /**
     * Asserts that two Strings are equal.
     */
    public static void assertEquals(String message, String expected,
            String actual) {
        if (expected == null && actual == null) {
            return;
        }

        if (expected != null && expected.equals(actual)) {
            return;
        }

        throw new AssertionError(format(message, expected, actual));
    }

    /**
     * Asserts that two longs are equal. If they are not an AssertionFailedError
     * is thrown with the given message.
     */
    public static void assertEquals(String message, long expected, long actual) {
        if (expected == actual) {
            return;
        }

        failNotEquals(message, expected, actual);
    }

    /**
     * Asserts that two longs are equal.
     */
    public static void assertEquals(long expected, long actual) {
        assertEquals(null, expected, actual);
    }

    /**
     * Asserts that an object isn't null.
     */
    public static void assertNotNull(Object object) {
        assertNotNull(null, object);
    }

    /**
     * Asserts that all objects aren't null. If it is an AssertionFailedError is
     * thrown with the given message.
     */
    public static void assertNotNull(String message, Object... objects) {
        for (Object object : objects) {
            assertTrue(message, object != null);
        }
    }

    public static void failNotEquals(String message, Object expected,
            Object actual) {
        fail(format(message, expected, actual));
    }

    private static String format(String message, Object expected, Object actual) {
        String formatted = "";
        if (message != null) {
            formatted = message + " ";
        }
        return formatted + "expected:<" + expected + "> but was:<" + actual + ">";
    }

    public static <T> void assertPostParamNotNull(String parameterName, T postParameter) {
        if (postParameter == null) {
            throw new NullPostParameterException(String.format(Const.StatusCodes.NULL_POST_PARAMETER,
                    parameterName));
        }
    }

    public static void assertNotEmpty(String str) {
        assertFalse(str.isEmpty());
    }

    public static void assertNotEmpty(String message, String str) {
        assertFalse(message, str.isEmpty());
    }

}
