package teammates.test.driver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;

/**
 * Provides additional assertion methods that are often used during testing.
 */
public final class AssertHelper {

    private AssertHelper() {
        // utility class
    }

    /**
     * Assert instant is now +- 1 min.
     */
    public static void assertInstantIsNow(Instant instant) {
        assertInstantWithinRange(instant, TimeHelperExtension.getInstantMinutesOffsetFromNow(-1),
                TimeHelperExtension.getInstantMinutesOffsetFromNow(1));
    }

    private static void assertInstantWithinRange(Instant instant, Instant start, Instant end) {
        assertTrue(!(instant.isBefore(start) || instant.isAfter(end)));
    }

    /**
     * Asserts that the {@link String} {@code superstringActual} contains the exact occurrence of
     * <b>every</b> String in the {@link List} of Strings {@code substringsExpected}.
     * Display the difference between the two on failure.
     */
    public static void assertContains(List<String> substringsExpected,
            String superstringActual) {
        for (String substringExpected : substringsExpected) {
            if (!superstringActual.contains(substringExpected)) {
                assertEquals(substringExpected, superstringActual);
            }
        }
    }

    /**
     * Asserts that the superstringActual contains the exact occurrence of
     * substringExpected. Display the difference between the two on failure.
     */
    public static void assertContains(String substringExpected,
            String superstringActual) {
        if (!superstringActual.contains(substringExpected)) {
            assertEquals(substringExpected, superstringActual);
        }
    }

    /**
     * Asserts that the superstringActual contains the exact occurence of
     * substringExpected. Display the difference between the two on failure
     * with the specified message.
     */
    public static void assertContains(String message, String substringExpected,
            String superstringActual) {
        if (!superstringActual.contains(substringExpected)) {
            assertEquals(message, substringExpected, superstringActual);
        }
    }

    /**
     * Asserts that the stringActual contains the occurence regexExpected.
     * Replaces occurences of {*} at regexExpected to match anything in
     * stringActual. Tries to display the difference between the two on failure.
     * Ignores the tab character (i.e., ignore indentation using
     * tabs) and ignores the newline when comparing.
     */
    public static void assertContainsRegex(String regexExpected,
            String stringActual) {
        if (!isContainsRegex(regexExpected, stringActual)) {
            assertEquals(regexExpected, stringActual);
        }
    }

    /**
     * Asserts that the stringActual contains the occurence regexExpected.
     * Replaces occurences of {*} at regexExpected to match anything in
     * stringActual. Tries to display the difference between the two on failure
     * with the specified message.
     */
    public static void assertContainsRegex(String message,
            String regexExpected, String stringActual) {
        if (!isContainsRegex(regexExpected, stringActual)) {
            assertEquals(message, regexExpected, stringActual);
        }
    }

    /**
     * Checks that the stringActual contains the occurrence regexExpected.<br>
     * Occurrences of {*} at regexExpected can match anything (as defined by the regex .*)
     * in stringActual.
     */
    public static boolean isContainsRegex(String regexExpected, String stringActual) {
        String processedActual = stringActual.replaceAll("[\t\r\n]", "");
        String processedRegex = Pattern.quote(regexExpected)
                .replaceAll(Pattern.quote("{*}"), "\\\\E.*?\\\\Q")
                .replaceAll("[\t\r\n]", "");
        return processedActual.matches("(?s)(?m).*?" + processedRegex + ".*?");
    }

    /**
     * Asserts that the two given lists have the same contents, ignoring their order.
     */
    public static void assertSameContentIgnoreOrder(List<?> a, List<?> b) {

        String expectedListAsString = Joiner.on("\t").join(a);
        String actualListAsString = Joiner.on("\t").join(b);

        List<String> expectedStringTypeList = new ArrayList<>(Arrays.asList(expectedListAsString.split("\t")));
        List<String> actualStringTypeList = new ArrayList<>(Arrays.asList(actualListAsString.split("\t")));

        expectedStringTypeList.sort(null);
        actualStringTypeList.sort(null);

        assertEquals(expectedStringTypeList, actualStringTypeList);

    }

}
