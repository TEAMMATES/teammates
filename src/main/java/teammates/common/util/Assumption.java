package teammates.common.util;

/**
 * This class provides a set of static method to verify assumptions about the system.
 * When the real runtime condition differs from the assumed situation,
 * an unchecked {@link AssertionError} will be thrown at runtime.
 *
 * <p>Normally, we use Java assertion to do runtime checking, but GAE does not support the assertions.
 * The methods of this file are adapted from org.junit.jupiter.api.Assertions v5.3.1.
 */
public final class Assumption {

    private Assumption() {
        // utility class
        // Intentional private constructor to prevent instantiation.
    }

    /**
     * <em>Asserts</em> that the supplied {@code condition} is {@code true}.
     *
     * <p>Fails with the supplied failure {@code message}.
     */
    public static void assertTrue(String message, boolean condition) {
        if (!condition) {
            fail(buildPrefix(message) + "expected: <true> but was: <false>");
        }
    }

    /**
     * <em>Asserts</em> that the supplied {@code condition} is {@code true}.
     */
    public static void assertTrue(boolean condition) {
        assertTrue(null, condition);
    }

    /**
     * <em>Asserts</em> that the supplied {@code condition} is not {@code true}.
     *
     * <p>Fails with the supplied failure {@code message}.
     */
    public static void assertFalse(String message, boolean condition) {
        if (condition) {
            fail(buildPrefix(message) + "expected: <false> but was: <true>");
        }
    }

    /**
     * <em>Asserts</em> that the supplied {@code condition} is not {@code true}.
     */
    public static void assertFalse(boolean condition) {
        assertFalse(null, condition);
    }

    /**
     * <em>Fails</em> a test with the given failure {@code message}.
     */
    public static void fail(String message) {
        throw new AssertionError(message);
    }

    /**
     * <em>Asserts</em> that {@code expected} and {@code actual} are equal.
     *
     * <p>If both are {@code null}, they are considered equal.
     *
     * <p>Fails with the supplied failure {@code message}.
     */
    public static void assertEquals(String message, String expected, String actual) {
        if (!objectsAreEqual(expected, actual)) {
            failNotEqual(expected, actual, message);
        }
    }

    /**
     * <em>Asserts</em> that {@code expected} and {@code actual} are equal.
     *
     * <p>Fails with the supplied failure {@code message}.
     */
    public static void assertEquals(String message, long expected, long actual) {
        if (expected != actual) {
            failNotEqual(String.valueOf(expected), String.valueOf(actual), message);
        }
    }

    /**
     * <em>Asserts</em> that {@code expected} and {@code actual} are equal.
     */
    public static void assertEquals(long expected, long actual) {
        assertEquals(null, expected, actual);
    }

    /**
     * <em>Asserts</em> that {@code actual} is not {@code null}.
     */
    public static void assertNotNull(Object object) {
        assertNotNull(null, object);
    }

    /**
     * <em>Asserts</em> that {@code actuals} are all not {@code null}.
     */
    public static void assertNotNull(Object... objects) {
        for (Object object : objects) {
            assertNotNull(object);
        }
    }

    /**
     * <em>Asserts</em> that {@code objects} are all not {@code null}.
     *
     * <p>Fails with the supplied failure {@code message}.
     */
    public static void assertNotNull(String message, Object... objects) {
        for (Object object : objects) {
            if (object == null) {
                fail(message);
            }
        }
    }

    /**
     * <em>Asserts</em> that the given string is neither {@code null} nor empty.
     */
    public static void assertNotEmpty(String str) {
        assertFalse(StringHelper.isEmpty(str));
    }

    /**
     * <em>Asserts</em> that the given string is neither {@code null} nor empty.
     *
     * <p>Fails with the supplied failure {@code message}.
     */
    public static void assertNotEmpty(String message, String str) {
        assertFalse(message, StringHelper.isEmpty(str));
    }

    private static String format(String expected, String actual, String message) {
        return buildPrefix(message) + formatValues(expected, actual);
    }

    private static boolean objectsAreEqual(Object obj1, Object obj2) {
        if (obj1 == null) {
            return obj2 == null;
        }
        return obj1.equals(obj2);
    }

    private static void failNotEqual(String expected, String actual, String message) {
        fail(format(expected, actual, message));
    }

    private static String buildPrefix(String message) {
        return StringHelper.isEmpty(message) ? "" : message + " ==> ";
    }

    private static String formatValues(String expected, String actual) {
        return String.format("expected: <%s> but was: <%s>", expected, actual);
    }

}
