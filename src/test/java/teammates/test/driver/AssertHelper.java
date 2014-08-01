package teammates.test.driver;

import static org.junit.Assert.assertEquals;

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
     * Checks that the stringActual contains the occurence regexExpected.
     * Replaces occurences of {*} at regexExpected to match anything in
     * stringActual.
     */
    public static boolean isContainsRegex(String regexExpected,    String stringActual) {
        String processedActual = stringActual.replaceAll("[\t\r\n]", "");
        String processedRegex = Pattern.quote(regexExpected)
                .replaceAll(Pattern.quote("{*}"), "\\\\E.*?\\\\Q")
                .replaceAll("[\t\r\n]", "");
        return processedActual.matches("(?s)(?m).*?" + processedRegex + ".*?");
    }

}
