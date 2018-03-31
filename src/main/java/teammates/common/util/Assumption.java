package teammates.common.util;

import teammates.common.exception.NullPostParameterException;

/**
 * This class provides a set of static method to verify assumptions about the
 * system. When the real runtime condition differs from the assumed situation,
 * an unchecked AssertionFailedError will be thrown at runtime.
 *
 * <p>Normally, we uses Java assertion to do runtime checking, but GAE does not
 * support the assertions.is This file is a copy of org.junit.Assert v4.10.
 * Cannot use default java assert due to GAE environment restriction
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
     * Asserts that two objects are equal. If they are not an
     * AssertionFailedError is thrown with the given message.
     */
    public static void assertEquals(String message, Object expected,
            Object actual) {
        if (expected == null && actual == null) {
            return;
        }

        if (expected != null && expected.equals(actual)) {
            return;
        }

        failNotEquals(message, expected, actual);
    }

    /**
     * Asserts that two objects are equal. If they are not an
     * AssertionFailedError is thrown.
     */
    public static void assertEquals(Object expected, Object actual) {
        assertEquals(null, expected, actual);
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
     * Asserts that two Strings are equal.
     */
    public static void assertEquals(String expected, String actual) {
        assertEquals(null, expected, actual);
    }

    /**
     * Asserts that two doubles are equal concerning a delta. If they are not an
     * AssertionFailedError is thrown with the given message. If the expected
     * value is infinity then the delta value is ignored.
     */
    public static void assertEquals(String message, double expected,
            double actual, double delta) {
        if (Double.compare(expected, actual) == 0) {
            return;
        }

        if (Math.abs(expected - actual) > delta) {
            failNotEquals(message, Double.valueOf(expected), Double.valueOf(actual));
        }
    }

    /**
     * Asserts that two doubles are equal concerning a delta. If the expected
     * value is infinity then the delta value is ignored.
     */
    public static void assertEquals(double expected, double actual, double delta) {
        assertEquals(null, expected, actual, delta);
    }

    /**
     * Asserts that two floats are equal concerning a positive delta. If they
     * are not an AssertionFailedError is thrown with the given message. If the
     * expected value is infinity then the delta value is ignored.
     */
    public static void assertEquals(String message, float expected,
            float actual, float delta) {
        if (Float.compare(expected, actual) == 0) {
            return;
        }

        if (Math.abs(expected - actual) > delta) {
            failNotEquals(message, Float.valueOf(expected), Float.valueOf(actual));
        }
    }

    /**
     * Asserts that two floats are equal concerning a delta. If the expected
     * value is infinity then the delta value is ignored.
     */
    public static void assertEquals(float expected, float actual, float delta) {
        assertEquals(null, expected, actual, delta);
    }

    /**
     * Asserts that two longs are equal. If they are not an AssertionFailedError
     * is thrown with the given message.
     */
    public static void assertEquals(String message, long expected, long actual) {
        assertEquals(message, Long.valueOf(expected), Long.valueOf(actual));
    }

    /**
     * Asserts that two longs are equal.
     */
    public static void assertEquals(long expected, long actual) {
        assertEquals(null, expected, actual);
    }

    /**
     * Asserts that two booleans are equal. If they are not an
     * AssertionFailedError is thrown with the given message.
     */
    public static void assertEquals(String message, boolean expected,
            boolean actual) {
        assertEquals(message, Boolean.valueOf(expected),
                Boolean.valueOf(actual));
    }

    /**
     * Asserts that two booleans are equal.
     */
    public static void assertEquals(boolean expected, boolean actual) {
        assertEquals(null, expected, actual);
    }

    /**
     * Asserts that two bytes are equal. If they are not an AssertionFailedError
     * is thrown with the given message.
     */
    public static void assertEquals(String message, byte expected, byte actual) {
        assertEquals(message, Byte.valueOf(expected), Byte.valueOf(actual));
    }

    /**
     * Asserts that two bytes are equal.
     */
    public static void assertEquals(byte expected, byte actual) {
        assertEquals(null, expected, actual);
    }

    /**
     * Asserts that two chars are equal. If they are not an AssertionFailedError
     * is thrown with the given message.
     */
    public static void assertEquals(String message, char expected, char actual) {
        assertEquals(message, Character.valueOf(expected), Character.valueOf(actual));
    }

    /**
     * Asserts that two chars are equal.
     */
    public static void assertEquals(char expected, char actual) {
        assertEquals(null, expected, actual);
    }

    /**
     * Asserts that two shorts are equal. If they are not an
     * AssertionFailedError is thrown with the given message.
     */
    public static void assertEquals(String message, short expected, short actual) {
        assertEquals(message, Short.valueOf(expected), Short.valueOf(actual));
    }

    /**
     * Asserts that two shorts are equal.
     */
    public static void assertEquals(short expected, short actual) {
        assertEquals(null, expected, actual);
    }

    /**
     * Asserts that two ints are equal. If they are not an AssertionFailedError
     * is thrown with the given message.
     */
    public static void assertEquals(String message, int expected, int actual) {
        assertEquals(message, Integer.valueOf(expected), Integer.valueOf(actual));
    }

    /**
     * Asserts that two ints are equal.
     */
    public static void assertEquals(int expected, int actual) {
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

    /**
     * Asserts that an object is null.
     */
    public static void assertNull(Object object) {
        assertNull(null, object);
    }

    /**
     * Asserts that an object is null. If it is not an AssertionFailedError is
     * thrown with the given message.
     */
    public static void assertNull(String message, Object object) {
        assertTrue(message, object == null);
    }

    /**
     * Asserts that two objects refer to the same object. If they are not an
     * AssertionFailedError is thrown with the given message.
     */
    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    // PMD.CompareObjectsWithEquals is suppressed as assertSame are checking if
    // the expected and actual are the same objects (not just equal to each other)
    public static void assertSame(String message, Object expected, Object actual) {
        if (expected == actual) {
            return;
        }

        failNotSame(message, expected, actual);
    }

    /**
     * Asserts that two objects refer to the same object. If they are not the
     * same an AssertionFailedError is thrown.
     */
    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    // PMD.CompareObjectsWithEquals is suppressed as assertSame are checking if
    // the expected and actual are the same objects (not just equal to each other)
    public static void assertSame(Object expected, Object actual) {
        assertSame(null, expected, actual);
    }

    /**
     * Asserts that two objects do not refer to the same object. If they do
     * refer to the same object an AssertionFailedError is thrown with the given
     * message.
     */
    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    // PMD.CompareObjectsWithEquals is suppressed as assertNotSame is checking if
    // the expected and actual are the different objects (not just equal to each other)
    public static void assertNotSame(String message, Object expected,
            Object actual) {
        if (expected == actual) {
            failSame(message);
        }
    }

    /**
     * Asserts that two objects do not refer to the same object. If they do
     * refer to the same object an AssertionFailedError is thrown.
     */
    public static void assertNotSame(Object expected, Object actual) {
        assertNotSame(null, expected, actual);
    }

    public static void failSame(String message) {
        String formatted = "";
        if (message != null) {
            formatted = message + " ";
        }
        fail(formatted + "expected not same");
    }

    public static void failNotSame(String message, Object expected,
            Object actual) {
        String formatted = "";
        if (message != null) {
            formatted = message + " ";
        }
        fail(formatted + "expected same:<" + expected + "> was not:<" + actual + ">");
    }

    public static void failNotEquals(String message, Object expected,
            Object actual) {
        fail(format(message, expected, actual));
    }

    public static String format(String message, Object expected, Object actual) {
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

    public static void assertIsEmpty(String str) {
        assertTrue(str.isEmpty());
    }

    public static void assertIsEmpty(String message, String str) {
        assertTrue(message, str.isEmpty());
    }
}
