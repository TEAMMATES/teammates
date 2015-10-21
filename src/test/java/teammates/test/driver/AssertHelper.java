package teammates.test.driver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import teammates.common.util.TimeHelper;

public class AssertHelper {

    public static void assertSameDates(Date expected, Date actual) {
        assertEquals(TimeHelper.calendarToString(TimeHelper.dateToCalendar(expected)),
                TimeHelper.calendarToString(TimeHelper.dateToCalendar(actual)));
    }
    
    /**
     * Asserts that the {@link String} {@code superstringActual} contains the exact occurrence of
     * <b>every</b> String in the {@link List} of Strings {@code substringsExpected}. 
     * Display the difference between the two on failure (in
     * Eclipse).
     */
    public static void assertContains(List<String> substringsExpected,
            String superstringActual) {
        for(String substringExpected : substringsExpected) {
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
    public static boolean isContainsRegex(String regexExpected,    String stringActual) {
        String processedActual = stringActual.replaceAll("[\t\r\n]", "");
        String processedRegex = Pattern.quote(regexExpected)
                .replaceAll(Pattern.quote("{*}"), "\\\\E.*?\\\\Q")
                .replaceAll("[\t\r\n]", "");
        return processedActual.matches("(?s)(?m).*?" + processedRegex + ".*?");
    }
    
    /**
     * Asserts that the actual log message, excluding its id, is equal to the expected log message,
     * and that the actual log message's id contains the expected google id. 
     * @param expected
     * @param actual
     */
    public static void assertLogMessageEquals(String expected, String actual) {
        int endIndex = actual.lastIndexOf("|||");
        String actualLogWithoutId = actual.substring(0, endIndex);
        
        assertEquals(expected, actualLogWithoutId);
        
        String expectedGoogleId = expected.split("\\|\\|\\|")[6];
        String actualId = actual.substring(endIndex + "|||".length());
        
        assertTrue("expected actual message's id to contain " + expectedGoogleId 
                   + " but was " + actualId,
                   actualId.contains(expectedGoogleId));
    }

}
