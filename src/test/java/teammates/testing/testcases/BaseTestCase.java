package teammates.testing.testcases;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.junit.Rule;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import teammates.BackDoorLogic;
import teammates.BackDoorServlet;
import teammates.Config;
import teammates.api.Common;
import teammates.api.Logic;
import teammates.api.TeammatesException;
import teammates.datatransfer.CoordData;
import teammates.datatransfer.DataBundle;
import teammates.datatransfer.UserData;
import teammates.testing.lib.BackDoor;

import com.google.appengine.api.taskqueue.dev.LocalTaskQueue;
import com.google.appengine.api.taskqueue.dev.QueueStateInfo;
import com.google.appengine.api.taskqueue.dev.QueueStateInfo.TaskStateInfo;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;

public class BaseTestCase {
	
	//Find the un-deprecated way of doing the below
	
	@Rule
	public MethodRule methodRule = new MethodRule (){
	    private String testName;

	    @Override
	    public Statement apply(Statement statement, FrameworkMethod method, Object target) {
	        String methodName = method.getName();
	        printTestCaseHeader(methodName);
	        return statement;
	    }
	};
	
	/* 
	 * Here, we initialize the Config object using Config.inst(Properties) 
	 *   because Config.inst() that is usually used cannot find the 
	 *   build.properties files when called from test suite. 
	 *   An alternative approach may be to add the build.properties location
	 *   to the test suit's classpath.
	 */
	static {
		String buildFile = System.getProperty("user.dir")+"\\src\\main\\webapp\\WEB-INF\\classes\\"+"build.properties";
		Properties buildProperties = new Properties();
		try {
			buildProperties.load(new FileInputStream(buildFile));
		} catch (Exception e){
			e.printStackTrace();
			throw new RuntimeException("Could not initialize Config object"+TeammatesException.stackTraceToString(e));
		}
		Config.inst(buildProperties);
	}
	
	LocalServiceTestHelper helper;

	protected static String queueXmlFilePath = System.getProperty("user.dir")
				+ File.separator + "src" + File.separator + "main" + File.separator
				+ "webapp" + File.separator + "WEB-INF" + File.separator
				+ "queue.xml";
	protected static long start;

	@Deprecated
	public static void printTestCaseHeader(String testCaseName) {
		print("[TestCase]---:" + testCaseName);
	}

	public static void printTestCaseHeader() {
		printTestCaseHeader(Thread.currentThread().getStackTrace()[2]
				.getMethodName());
	}

	@Deprecated
	public static void printTestClassHeader(String className) {
		print("[=============================" + className
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
		print(" * " + description);
	}

	public static void printTestClassHeader() {
		printTestClassHeader(Thread.currentThread().getStackTrace()[2]
				.getClassName());
	}
	
	public static void printTestClassFooter() {
		printTestClassFooter(Thread.currentThread().getStackTrace()[2]
				.getClassName());
	}

	@Deprecated
	public static void printTestClassFooter(String testClassName) {
		print(testClassName + " completed");
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

	protected static void turnLoggingUp(Class<?> classBeingTested)
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

	/**
	 * Checks that the stringActual contains the occurence regexExpected.
	 * Replaces occurences of {*} at regexExpected to match anything in
	 * stringActual.
	 * @param regexExpected
	 * @param stringActual
	 * @return
	 * 		boolean whether the actual matches the expected
	 */
	public static boolean isContainsRegex(String regexExpected, String stringActual){
		String processedActual = stringActual.replaceAll("[\t\r\n]","");
		String processedRegex = Pattern.quote(regexExpected).replaceAll(Pattern.quote("{*}"), "\\\\E.*\\\\Q").replaceAll("[\t\r\n]","");
		return processedActual.matches("(?s)(?m).*"+processedRegex+".*");
	}

	protected static DataBundle getTypicalDataBundle() {
		String jsonString;
		try {
			jsonString = Common.readFile(Common.TEST_DATA_FOLDER
					+ "/typicalDataBundle.json");
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		return Common.getTeammatesGson().fromJson(jsonString, DataBundle.class);
	}

	public void restoreTypicalDataInDatastore() throws Exception {
		setGeneralLoggingLevel(Level.SEVERE);
		
		BackDoorLogic backDoorLogic = new BackDoorLogic();
		//memorize the logged in user
		UserData loggedInUser = backDoorLogic.getLoggedInUser();
		
		//switch to admin (writing operations require admin access)
		loginAsAdmin("admin.user");
		
		// also reduce logging verbosity of these classes as we are going to
		// use them intensively here.
		setLogLevelOfClass(BackDoorServlet.class, Level.SEVERE);
		setLogLevelOfClass(Logic.class, Level.SEVERE);
		
		DataBundle dataBundle = getTypicalDataBundle();
		HashMap<String, CoordData> coords = dataBundle.coords;
		for (CoordData coord : coords.values()) {
			backDoorLogic.deleteCoord(coord.id);
		}
		backDoorLogic.persistNewDataBundle(dataBundle);
		
		//restore logging levels to normal
		//TODO: restore to previous levels
		setGeneralLoggingLevel(Level.WARNING);
		setLogLevelOfClass(BackDoorServlet.class, Level.FINE);
		setLogLevelOfClass(Logic.class, Level.FINE);

		//restore the logged in user
		logoutUser();
		if(loggedInUser!=null){
			helper.setEnvIsLoggedIn(true);
			helper.setEnvEmail(loggedInUser.id);
			helper.setEnvAuthDomain("gmail.com");
			helper.setEnvIsAdmin(loggedInUser.isAdmin);
		} 
		
	}

	//TODO: check if this bug is fixed in new SDK
	protected void setEmailQueuePath(LocalTaskQueueTestConfig ltqtc) {
		/*
		 * We have to explicitly set the path of queue.xml because the test
		 * environment cannot find it. Apparently, this is a bug in the test
		 * environment (as mentioned in
		 * http://turbomanage.wordpress.com/2010/03/
		 * 03/a-recipe-for-unit-testing-appengine-task-queues/ The bug might get
		 * fixed in future SDKs.
		 */
		ltqtc.setQueueXmlPath(queueXmlFilePath);
	}

	//TODO: check if this bug is fixed in new SDK
	protected void setHelperTimeZone(LocalServiceTestHelper localTestHelper) {
		/**
		 * LocalServiceTestHelper is supposed to run in the same timezone as Dev
		 * server and production server i.e. (i.e. UTC timezone), as stated in
		 * https
		 * ://developers.google.com/appengine/docs/java/tools/localunittesting
		 * /javadoc/com/google/appengine/tools/development/testing/
		 * LocalServiceTestHelper#setTimeZone%28java.util.TimeZone%29
		 * 
		 * But it seems Dev server does not run on UTC timezone, but it runs on
		 * "GMT+8:00" (Possibly, a bug). Therefore, I'm changing timeZone of
		 * LocalServiceTestHelper to match the Dev server. But note that tests
		 * that run on Dev server might fail on Production server due to this
		 * problem. We need to find a fix.
		 */
		localTestHelper.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
	}

	protected int getNumberOfEmailTasksInQueue() {
		LocalTaskQueue ltq = LocalTaskQueueTestConfig.getLocalTaskQueue();
		QueueStateInfo qsi = ltq.getQueueStateInfo().get("email-queue");
		return qsi.getTaskInfo().size();
	}

	protected List<TaskStateInfo> getTasksInQueue(String queueName) {
		LocalTaskQueue ltq = LocalTaskQueueTestConfig.getLocalTaskQueue();
		QueueStateInfo qsi = ltq.getQueueStateInfo().get(queueName);
	
		List<TaskStateInfo> taskInfoList = qsi.getTaskInfo();
		return taskInfoList;
	}
	
	/**
	 * Checks whether a JSON string represents a null object
	 * @param json
	 * @return
	 */
	protected boolean isNullJSON(String json){
		return json==null || json.equals("null");
	}
	
	protected void loginUser(String userId) {
		helper.setEnvIsLoggedIn(true);
		helper.setEnvEmail(userId);
		helper.setEnvAuthDomain("gmail.com");
		helper.setEnvIsAdmin(false);
	}
	
	protected void logoutUser() {
		helper.setEnvIsLoggedIn(false);
		helper.setEnvIsAdmin(false);
	}

	protected void loginAsAdmin(String userId) {
		loginUser(userId);
		helper.setEnvIsAdmin(true);
	}

	protected void loginAsCoord(String userId) {
		loginUser(userId);
		Logic logic = new Logic();
		assertEquals(true, logic.getLoggedInUser().isCoord);
		assertEquals(false, logic.getLoggedInUser().isAdmin);
	}
	
	protected void loginAsStudent(String userId) {
		loginUser(userId);
		Logic logic = new Logic();
		assertEquals(true, logic.getLoggedInUser().isStudent);
		assertEquals(false, logic.getLoggedInUser().isCoord);
		assertEquals(false, logic.getLoggedInUser().isAdmin);
	}

	protected static void print(String message){
		System.out.println(message);
	}

	protected static void startRecordingTimeForDataImport() {
		start = System.currentTimeMillis();
	}
	
	protected static void reportTimeForDataImport() {
		print("Data import finished in "+(System.currentTimeMillis()-start)+" ms");
	}

}
