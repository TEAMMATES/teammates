package teammates.testing.testcases;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import teammates.api.Common;

public class BaseTestCase {

	public static void printTestCaseHeader(String testCaseName) {
		System.out.println("[TestCase]---:" + testCaseName);
	}

	public static void printTestCaseHeader() {
		printTestCaseHeader(Thread.currentThread().getStackTrace()[2]
				.getMethodName());
	}

	public static void printTestClassHeader(String className) {
		System.out.println("[=============================" + className
				+ "=============================]");
	}

	/**
	 * Test Segment divider. Used to divide a test case into logical sections.
	 * The weird name is for easy spotting.
	 * 
	 * @param description
	 *            of the logical section. This will be printed.
	 */
	public static void ______TS(String description) {
		System.out.println(" * " + description);
	}

	public static void printTestClassHeader() {
		printTestClassHeader(Thread.currentThread().getStackTrace()[2]
				.getClassName());
	}
	
	public static void printTestClassFooter() {
		printTestClassFooter(Thread.currentThread().getStackTrace()[2]
				.getClassName());
	}

	public static void printTestClassFooter(String testClassName) {
		System.out.println(testClassName + " completed");
	}

	protected static void setLogLevelOfClass(Class<?> testedClass, Level level)
			throws NoSuchFieldException, IllegalAccessException {
		Field logField = testedClass.getDeclaredField("log");
		logField.setAccessible(true);
		Logger log = (Logger) logField.get(null);
		log.setLevel(level);
	}

	protected static void setConsoleLoggingLevel(Level level) {
		Logger topLogger = java.util.logging.Logger.getLogger("");
		Handler consoleHandler = null;
		for (Handler handler : topLogger.getHandlers()) {
			if (handler instanceof ConsoleHandler) {
				consoleHandler = handler;
				break;
			}
		}
		consoleHandler.setLevel(level);
	}

	protected static void setGeneralLoggingLevel(Level level) {
		java.util.logging.Logger.getLogger("").setLevel(level);
	}

	protected static void turnLogginUp(Class<?> classBeingTested)
			throws NoSuchFieldException, IllegalAccessException {
		setGeneralLoggingLevel(Level.WARNING);
		setLogLevelOfClass(classBeingTested, Level.FINE);
		setConsoleLoggingLevel(Level.FINE);
	}

	protected static void turnLoggingDown(Class<?> classBeingTested)
			throws NoSuchFieldException, IllegalAccessException {
		setLogLevelOfClass(classBeingTested, Level.WARNING);
		setConsoleLoggingLevel(Level.WARNING);
	}

	protected static void assertSameDates(Date expected, Date actual) {
		assertEquals(Common.calendarToString(Common.dateToCalendar(expected)),
				Common.calendarToString(Common.dateToCalendar(actual)));
	}

	/**
	 * Asserts that the superstringActual contains the exact occurence of
	 * substringExpected. Display the difference between the two on failure (in
	 * Eclipse).
	 * 
	 * @param message
	 * @param substringExpected
	 * @param superstringActual
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
	 * 
	 * @param message
	 * @param substringExpected
	 * @param superstringActual
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
	 * (in Eclipse).
	 * Ignores the tab character (i.e., ignore indentation using tabs) and
	 * ignores the newline when comparing.
	 * 
	 * @param message
	 * @param regexExpected
	 * @param stringActual
	 */
	public static void assertContainsRegex(String regexExpected, String stringActual){
		String processedActual = stringActual.replaceAll("[\t\r\n]","");
		String processedRegex = Pattern.quote(regexExpected).replaceAll(Pattern.quote("{*}"), "\\\\E.*\\\\Q").replaceAll("[\t\r\n]","");
		if(!processedActual.matches("(?s)(?m).*"+processedRegex+".*")){
			assertEquals(regexExpected, stringActual);
		}
	}

	/**
	 * Asserts that the stringActual contains the occurence regexExpected.
	 * Replaces occurences of {*} at regexExpected to match anything in
	 * stringActual. Tries to display the difference between the two on failure
	 * (in Eclipse) with the specified message.
	 * 
	 * @param message
	 * @param regexExpected
	 * @param stringActual
	 */
	public static void assertContainsRegex(String message,
			String regexExpected, String stringActual) {
		String processedActual = stringActual.replaceAll("[\t\r\n]","");
		String processedRegex = Pattern.quote(regexExpected).replaceAll(Pattern.quote("{*}"), "\\\\E.*\\\\Q").replaceAll("[\t\r\n]","");
		if(!processedActual.matches("(?s)(?m).*"+processedRegex+".*")){
			assertEquals(message, regexExpected, stringActual);
		}
	}

}
