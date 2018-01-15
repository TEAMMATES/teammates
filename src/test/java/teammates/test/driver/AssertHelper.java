package teammates.test.driver;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;

import teammates.common.util.Const;
import teammates.common.util.TimeHelper;

/**
 * Provides additional assertion methods that are often used during testing.
 */
public final class AssertHelper {

    private AssertHelper() {
        // utility class
    }

    /**
     * Assert date is now +- 1 min.
     */
    public static void assertDateIsNow(Date date) {
        assertDateWithinRange(date, TimeHelper.getMsOffsetToCurrentTime(-1000 * 60),
                                    TimeHelper.getMsOffsetToCurrentTime(1000 * 60));
    }

    private static void assertDateWithinRange(Date date, Date startDate, Date endDate) {
        assertTrue(!(date.before(startDate) || date.after(endDate)));
    }

    /**
     * Asserts that the {@link String} {@code superstringActual} contains the exact occurrence of
     * <b>every</b> String in the {@link List} of Strings {@code substringsExpected}.
     * Display the difference between the two on failure (in
     * Eclipse).
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
     * Asserts that the superstringActual contains the exact occurence of
     * substringExpected. Display the difference between the two on failure (in
     * Eclipse).
     */
    public static void assertContains(String substringExpected,
            String superstringActual) {
        if (!superstringActual.contains(substringExpected)) {
            assertEquals(substringExpected, superstringActual);
        }
    }

    /**
     * Asserts that the superstringActual contains the exact occurence of
     * substringExpected. Display the difference between the two on failure (in
     * Eclipse) with the specified message.
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
     * stringActual. Tries to display the difference between the two on failure
     * (in Eclipse). Ignores the tab character (i.e., ignore indentation using
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
     * (in Eclipse) with the specified message.
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
     * in stringActual, however, in its usage with {@link HtmlHelper}, please refrain from these
     * usages as they will not pass:
     * <ol>
     * <li>Empty contents right after an HTML tag, e.g <code>&lt;p&gt;{*}&lt;/p&gt;</code> will not match
     *     <code>&lt;p&gt;&lt;/p&gt;</code> and neither will <code>&lt;p&gt;content&lt;br&gt;{*}&lt;/p&gt;</code>
     *     match <code>&lt;p&gt;content&lt;br&gt;&lt;/p&gt;</code>.</li>
     * <li>HTML attribute-value pair without the = separator, e.g <code>&lt;div class{*}&gt;</code>
     *     will not match <code>&lt;div class="test"&gt;</code> but <code>&lt;div class={*}&gt;</code>
     *     or <code>&lt;div class="{*}"&gt;</code> will.</li>
     * <li>Non-empty HTML attribute-value pairs, e.g <code>&lt;div {*}&gt;</code> will not match
     *     <code>&lt;div class="test"&gt;</code> but will match <code>&lt;div class=""&gt;</code>.
     *     <code>&lt;div class="{*}"&gt;</code>, however, will match both.</li>
     * </ol>
     */
    public static boolean isContainsRegex(String regexExpected, String stringActual) {
        String processedActual = stringActual.replaceAll("[\t\r\n]", "");
        String processedRegex = Pattern.quote(regexExpected)
                .replaceAll(Pattern.quote("{*}"), "\\\\E.*?\\\\Q")
                .replaceAll("[\t\r\n]", "");
        return processedActual.matches("(?s)(?m).*?" + processedRegex + ".*?");
    }

    /**
     * Asserts that the actual log message, excluding its ID, is equal to the expected log message,
     * and that the actual log message's ID contains the expected google ID.
     */
    public static void assertLogMessageEquals(String expected, String actual) {
        String expectedGoogleId =
                expected.split(Pattern.quote(Const.ActivityLog.FIELD_SEPARATOR))[6]; // GoogleId is at position 6

        assertLogMessageEqualsIgnoreLogId(expected, actual);
        assertLogIdContainsUserId(actual, expectedGoogleId);
    }

    /**
     * Assert that the actual log message contains userId in its id field.
     */
    public static void assertLogIdContainsUserId(String actualMessage, String userIdentifier) {
        int endIndex = actualMessage.lastIndexOf(Const.ActivityLog.FIELD_SEPARATOR);
        String actualId = actualMessage.substring(endIndex + Const.ActivityLog.FIELD_SEPARATOR.length());
        assertTrue("expected actual message's id to contain " + userIdentifier
                   + " but was " + actualId,
                   actualId.contains(userIdentifier));
    }

    /**
     * Assert that the actual log message, excluding its ID, is equal to the expected log message.
     */
    public static void assertLogMessageEqualsIgnoreLogId(String expected, String actual) {
        int endIndex = actual.lastIndexOf(Const.ActivityLog.FIELD_SEPARATOR);
        String actualLogWithoutId = actual.substring(0, endIndex);

        assertEquals(expected, actualLogWithoutId);
    }

    /**
     * Asserts that the actual log message, excluding its ID, is equal to the expected log message,
     * and that the actual log message's ID contains information of the google id of admin.
     */
    public static void assertLogMessageEqualsInMasqueradeMode(String expected,
            String actual, String adminGoogleId) {
        assertLogMessageEqualsIgnoreLogId(expected, actual);
        assertLogIdContainsUserId(actual, adminGoogleId);
    }

    /**
     * Asserts that the actual log message, excluding its ID, is equal to the expected log message,
     * and that the actual log message's ID contains information of the specified student email and course ID.
     */
    public static void assertLogMessageEqualsForUnregisteredStudentUser(
            String expected, String actual, String studentEmail, String courseId) {
        assertLogMessageEqualsIgnoreLogId(expected, actual);
        assertLogIdContainsUserId(actual,
                String.join(Const.ActivityLog.FIELD_CONNECTOR, studentEmail, courseId));
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
