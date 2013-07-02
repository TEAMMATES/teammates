package teammates.test.cases;

import static org.junit.Assert.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import teammates.common.Common;
import teammates.common.FileHelper;
import teammates.common.TimeHelper;
import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.UserType;
import teammates.common.exception.TeammatesException;
import teammates.logic.AccountsLogic;
import teammates.logic.CoursesLogic;
import teammates.logic.api.Logic;
import teammates.logic.backdoor.BackDoorLogic;
import teammates.test.driver.TestProperties;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class BaseTestCase {

	static {
		String buildFile = System.getProperty("user.dir")
				+ "/src/main/resources/"
				+ "build.properties";
		
		buildFile = buildFile.replace("/", File.separator);
		
		Properties buildProperties = new Properties();
		try {
			buildProperties.load(new FileInputStream(buildFile));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Could not initialize Config object"
					+ TeammatesException.toStringWithStackTrace(e));
		}
	}

	protected static LocalServiceTestHelper helper;

	protected static long start;

	private static void printTestCaseHeader(String testCaseName) {
		print("[TestCase]---:" + testCaseName);
	}

	public static void printTestCaseHeader() {
		printTestCaseHeader(Thread.currentThread().getStackTrace()[2]
				.getMethodName());
	}

	/**
	 * Test Segment divider. Used to divide a test case into logical sections.
	 * The weird name is for easy spotting.
	 * 
	 * @param description
	 *            of the logical section. This will be printed.
	 */
	public static void ______TS(String description) {
		print(" * " + description);
	}

	public static void printTestClassHeader() {
		print("[============================="
				+ Thread.currentThread().getStackTrace()[2].getClassName()
				+ "=============================]");
	}

	public static void printTestClassFooter() {
		print(Thread.currentThread().getStackTrace()[2].getClassName()
				+ " completed");
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

	/**
	 * Sets the general logging level to WARNING <br>
	 * Sets the logging level of the given class to FINE <br>
	 * Sets the logging level of the console to FINE <br>
	 */
	protected static void turnLoggingUp(Class<?> classBeingTested)
			throws NoSuchFieldException, IllegalAccessException {
		setGeneralLoggingLevel(Level.WARNING);
		setLogLevelOfClass(classBeingTested, Level.FINE);
		setConsoleLoggingLevel(Level.FINE);
	}

	/**
	 * Sets the logging level of the given class to WARNING <br>
	 * Sets the logging level of the console to WARNING <br>
	 */
	protected static void turnLoggingDown(Class<?> classBeingTested)
			throws NoSuchFieldException, IllegalAccessException {
		setLogLevelOfClass(classBeingTested, Level.WARNING);
		setConsoleLoggingLevel(Level.WARNING);
	}

	protected static void assertSameDates(Date expected, Date actual) {
		assertEquals(TimeHelper.calendarToString(TimeHelper.dateToCalendar(expected)),
				TimeHelper.calendarToString(TimeHelper.dateToCalendar(actual)));
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
	public static boolean isContainsRegex(String regexExpected,	String stringActual) {
		String processedActual = stringActual.replaceAll("[\t\r\n]", "");
		String processedRegex = Pattern.quote(regexExpected)
				.replaceAll(Pattern.quote("{*}"), "\\\\E.*\\\\Q")
				.replaceAll("[\t\r\n]", "");
		return processedActual.matches("(?s)(?m).*" + processedRegex + ".*");
	}

	/**
	 * Creates a DataBundle as specified in typicalDataBundle.json
	 */
	protected static DataBundle getTypicalDataBundle() {
		return loadDataBundle("/typicalDataBundle.json");
	}
	
	protected static DataBundle loadDataBundle(String pathToJsonFile){
		if(pathToJsonFile.startsWith("/")){
			pathToJsonFile = Common.TEST_DATA_FOLDER + pathToJsonFile;
		}
		String jsonString;
		try {
			jsonString = FileHelper.readFile(pathToJsonFile);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		jsonString = injectRealAccounts(jsonString);
		return Common.getTeammatesGson().fromJson(jsonString, DataBundle.class);
	}

	private static String injectRealAccounts(String jsonString) {
		
		return jsonString
				.replace("{$test.student1}", TestProperties.inst().TEST_STUDENT1_ACCOUNT)
				.replace("{$test.student2}", TestProperties.inst().TEST_STUDENT2_ACCOUNT)
				.replace("{$test.instructor}", TestProperties.inst().TEST_INSTRUCTOR_ACCOUNT);
	}

	/**
	 * Creates in the datastore a fresh copy of data in typicalDataBundle.json
	 */
	public void restoreTypicalDataInDatastore() throws Exception {
		setGeneralLoggingLevel(Level.SEVERE);

		BackDoorLogic backDoorLogic = new BackDoorLogic();
		// memorize the logged in user
		UserType loggedInUser = backDoorLogic.getCurrentUser();

		// switch to admin (writing operations require admin access)
		loginAsAdmin("admin.user");

		// also reduce logging verbosity of these classes as we are going to
		// use them intensively here.
		setLogLevelOfClass(Logic.class, Level.SEVERE);

		DataBundle dataBundle = getTypicalDataBundle();
		
		for (AccountAttributes account : dataBundle.accounts.values()) {
			AccountsLogic.inst().deleteAccountCascade(account.googleId);
		}

		// delete courses first in case there are existing courses with same id
		// but under different instructors.
		for (CourseAttributes course : dataBundle.courses.values()) {
			CoursesLogic.inst().deleteCourseCascade(course.id);
		}

		HashMap<String, InstructorAttributes> instructors = dataBundle.instructors;
		for (InstructorAttributes instructor : instructors.values()) {
			AccountsLogic.inst().downgradeInstructorToStudentCascade(instructor.googleId);
		}
		
		backDoorLogic.persistDataBundle(dataBundle);

		// restore logging levels to normal
		// TODO: restore to previous levels
		setGeneralLoggingLevel(Level.WARNING);
		setLogLevelOfClass(Logic.class, Level.FINE);

		// restore the logged in user
		logoutUser();
		if (loggedInUser != null) {
			helper.setEnvIsLoggedIn(true);
			helper.setEnvEmail(loggedInUser.id);
			helper.setEnvAuthDomain("gmail.com");
			helper.setEnvIsAdmin(loggedInUser.isAdmin);
		}

	}


	/**
	 * Checks whether a JSON string represents a null object
	 */
	protected boolean isNullJSON(String json) {
		return json == null || json.equals("null");
	}

	/**Logs in the user to the local test environment 
	 */
	protected void loginUser(String userId) {
		helper.setEnvIsLoggedIn(true);
		helper.setEnvEmail(userId);
		helper.setEnvAuthDomain("gmail.com");
		helper.setEnvIsAdmin(false);
	}

	/**Logs user out of the local test environment 
	 */
	protected void logoutUser() {
		helper.setEnvIsLoggedIn(false);
		helper.setEnvIsAdmin(false);
	}

	protected void loginAsAdmin(String userId) {
		loginUser(userId);
		helper.setEnvIsAdmin(true);
	}

	/**Logs in the user to the local test environment as an admin 
	 */
	protected void loginAsInstructor(String userId) {
		loginUser(userId);
		Logic logic = new Logic();
		assertEquals(true, logic.getCurrentUser().isInstructor);
		assertEquals(false, logic.getCurrentUser().isAdmin);
	}

	/**Logs in the user to the local test environment as a student 
	 */
	protected void loginAsStudent(String userId) {
		loginUser(userId);
		Logic logic = new Logic();
		assertEquals(true, logic.getCurrentUser().isStudent);
		assertEquals(false, logic.getCurrentUser().isInstructor);
		assertEquals(false, logic.getCurrentUser().isAdmin);
	}

	protected void signalFailureToDetectException(String... messages) {
		throw new RuntimeException("Expected exception not detected."+ Arrays.toString(messages));
	}

	protected void ignoreExpectedException() {
		assertTrue(true);
	}

	protected static void print(String message) {
		System.out.println(message);
	}

	protected static void startRecordingTimeForDataImport() {
		start = System.currentTimeMillis();
	}

	protected static void reportTimeForDataImport() {
		print("Data import finished in " + (System.currentTimeMillis() - start)
				+ " ms");
	}
	
	protected void waitFor(long miliseconds) {
		try {
			Thread.sleep(miliseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
