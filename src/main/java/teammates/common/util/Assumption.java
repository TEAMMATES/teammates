package teammates.common.util;

import teammates.common.exception.NullPostParameterException;

/**
 * This class provides a set of static method to verify assumptions about the
 * system. When the real runtime condition differs from the assumed situation,
 * an unchecked AssertionFailedError will be thrown at runtime.
 * 
 * Normally, we uses Java assertion to do runtime checking, but GAE does not
 * support the assertions.is This file is a copy of org.junit.Assert v4.10.
 * Cannot use default java assert due to GAE environment restriction
 * 
 * @see Assert
 */
public class Assumption {

    protected Assumption() {
    }

    /**
     * Asserts that a condition is true. If it isn't it throws an
     * AssertionFailedError with the given message.
     */
    public static void assertTrue(final String message, final boolean condition) {
        if (!condition) {
            fail(message);
        }
    }

    /**
     * Asserts that a condition is true. If it isn't it throws an
     * AssertionFailedError.
     */
    public static void assertTrue(final boolean condition) {
        assertTrue(null, condition);
    }

    /**
     * Asserts that a condition is false. If it isn't it throws an
     * AssertionFailedError with the given message.
     */
    public static void assertFalse(final String message, final boolean condition) {
        assertTrue(message, !condition);
    }

    /**
     * Asserts that a condition is false. If it isn't it throws an
     * AssertionFailedError.
     */
    public static void assertFalse(final boolean condition) {
        assertFalse(null, condition);
    }

    /**
     * Fails a test with the given message.
     */
    public static void fail(final String message) {
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
    public static void assertEquals(final String message, final Object expected,
            final Object actual) {
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
    public static void assertEquals(final Object expected, final Object actual) {
        assertEquals(null, expected, actual);
    }

    /**
     * Asserts that two Strings are equal.
     */
    public static void assertEquals(final String message, final String expected,
            final String actual) {
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
    public static void assertEquals(final String expected, final String actual) {
        assertEquals(null, expected, actual);
    }

    /**
     * Asserts that two doubles are equal concerning a delta. If they are not an
     * AssertionFailedError is thrown with the given message. If the expected
     * value is infinity then the delta value is ignored.
     */
    public static void assertEquals(final String message, final double expected,
            final double actual, final double delta) {
        if (Double.compare(expected, actual) == 0) {
            return;
        }
        
        if (!(Math.abs(expected - actual) <= delta)) {
            failNotEquals(message, new Double(expected), new Double(actual));
        }
    }

    /**
     * Asserts that two doubles are equal concerning a delta. If the expected
     * value is infinity then the delta value is ignored.
     */
    public static void assertEquals(final double expected, final double actual, final double delta) {
        assertEquals(null, expected, actual, delta);
    }

    /**
     * Asserts that two floats are equal concerning a positive delta. If they
     * are not an AssertionFailedError is thrown with the given message. If the
     * expected value is infinity then the delta value is ignored.
     */
    public static void assertEquals(final String message, final float expected,
            final float actual, final float delta) {
        if (Float.compare(expected, actual) == 0) {
            return;
        }
        
        if (!(Math.abs(expected - actual) <= delta)) {
            failNotEquals(message, new Float(expected), new Float(actual));
        }
    }

    /**
     * Asserts that two floats are equal concerning a delta. If the expected
     * value is infinity then the delta value is ignored.
     */
    public static void assertEquals(final float expected, final float actual, final float delta) {
        assertEquals(null, expected, actual, delta);
    }

    /**
     * Asserts that two longs are equal. If they are not an AssertionFailedError
     * is thrown with the given message.
     */
    public static void assertEquals(final String message, final long expected, final long actual) {
        assertEquals(message, new Long(expected), new Long(actual));
    }

    /**
     * Asserts that two longs are equal.
     */
    public static void assertEquals(final long expected, final long actual) {
        assertEquals(null, expected, actual);
    }

    /**
     * Asserts that two booleans are equal. If they are not an
     * AssertionFailedError is thrown with the given message.
     */
    public static void assertEquals(final String message, final boolean expected,
            final boolean actual) {
        assertEquals(message, Boolean.valueOf(expected),
                Boolean.valueOf(actual));
    }

    /**
     * Asserts that two booleans are equal.
     */
    public static void assertEquals(final boolean expected, final boolean actual) {
        assertEquals(null, expected, actual);
    }

    /**
     * Asserts that two bytes are equal. If they are not an AssertionFailedError
     * is thrown with the given message.
     */
    public static void assertEquals(final String message, final byte expected, final byte actual) {
        assertEquals(message, new Byte(expected), new Byte(actual));
    }

    /**
     * Asserts that two bytes are equal.
     */
    public static void assertEquals(final byte expected, final byte actual) {
        assertEquals(null, expected, actual);
    }

    /**
     * Asserts that two chars are equal. If they are not an AssertionFailedError
     * is thrown with the given message.
     */
    public static void assertEquals(final String message, final char expected, final char actual) {
        assertEquals(message, new Character(expected), new Character(actual));
    }

    /**
     * Asserts that two chars are equal.
     */
    public static void assertEquals(final char expected, final char actual) {
        assertEquals(null, expected, actual);
    }

    /**
     * Asserts that two shorts are equal. If they are not an
     * AssertionFailedError is thrown with the given message.
     */
    public static void assertEquals(final String message, final short expected, final short actual) {
        assertEquals(message, new Short(expected), new Short(actual));
    }

    /**
     * Asserts that two shorts are equal.
     */
    public static void assertEquals(final short expected, final short actual) {
        assertEquals(null, expected, actual);
    }

    /**
     * Asserts that two ints are equal. If they are not an AssertionFailedError
     * is thrown with the given message.
     */
    public static void assertEquals(final String message, final int expected, final int actual) {
        assertEquals(message, new Integer(expected), new Integer(actual));
    }

    /**
     * Asserts that two ints are equal.
     */
    public static void assertEquals(final int expected, final int actual) {
        assertEquals(null, expected, actual);
    }

    /**
     * Asserts that an object isn't null.
     */
    public static void assertNotNull(final Object object) {
        assertNotNull(null, object);
    }

    /**
     * Asserts that an object isn't null. If it is an AssertionFailedError is
     * thrown with the given message.
     */
    public static void assertNotNull(final String message, final Object object) {
        assertTrue(message, object != null);
    }

    /**
     * Asserts that an object is null.
     */
    public static void assertNull(final Object object) {
        assertNull(null, object);
    }

    /**
     * Asserts that an object is null. If it is not an AssertionFailedError is
     * thrown with the given message.
     */
    public static void assertNull(final String message, final Object object) {
        assertTrue(message, object == null);
    }

    /**
     * Asserts that two objects refer to the same object. If they are not an
     * AssertionFailedError is thrown with the given message.
     */
    public static void assertSame(final String message, final Object expected, final Object actual) {
        if (expected == actual) {
            return;
        }
        
        failNotSame(message, expected, actual);
    }

    /**
     * Asserts that two objects refer to the same object. If they are not the
     * same an AssertionFailedError is thrown.
     */
    public static void assertSame(final Object expected, final Object actual) {
        assertSame(null, expected, actual);
    }

    /**
     * Asserts that two objects do not refer to the same object. If they do
     * refer to the same object an AssertionFailedError is thrown with the given
     * message.
     */
    public static void assertNotSame(final String message, final Object expected,
            final Object actual) {
        if (expected == actual) {
            failSame(message);
        }
    }

    /**
     * Asserts that two objects do not refer to the same object. If they do
     * refer to the same object an AssertionFailedError is thrown.
     */
    public static void assertNotSame(final Object expected, final Object actual) {
        assertNotSame(null, expected, actual);
    }

    public static void failSame(final String message) {
        String formatted = "";
        if (message != null) {
            formatted = message + " ";
        }
        fail(formatted + "expected not same");
    }

    public static void failNotSame(final String message, final Object expected,
            final Object actual) {
        String formatted = "";
        if (message != null) {
            formatted = message + " ";
        }
        fail(formatted + "expected same:<" + expected + "> was not:<" + actual + ">");
    }

    public static void failNotEquals(final String message, final Object expected,
            final Object actual) {
        fail(format(message, expected, actual));
    }

    public static String format(final String message, final Object expected, final Object actual) {
        String formatted = "";
        if (message != null) {
            formatted = message + " ";
        }
        return formatted + "expected:<" + expected + "> but was:<" + actual + ">";
    }
    
    
    public static void assertPostParamNotNull(final String parameterName, final String postParameter) {
        if (postParameter == null) {
            throw new NullPostParameterException(String.format(Const.StatusCodes.NULL_POST_PARAMETER, 
                    parameterName));
        }
    }
    
    public static void assertNotEmpty(final String str) {
        assertFalse(str.isEmpty());
    }
    
    public static void assertNotEmpty(final String message, final String str) {
        assertFalse(message, str.isEmpty());
    }
    
    public static void assertIsEmpty(final String str) {
        assertTrue(str.isEmpty());
    }
    
    public static void assertIsEmpty(final String message, final String str) {
        assertTrue(message, str.isEmpty());
    }
}
