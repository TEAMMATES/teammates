package teammates.testing.testcases;

import java.lang.reflect.Field;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BaseTestCase {
	
	public static void printTestCaseHeader(String testCaseName){
		System.out.println("[TestCase]---:"+testCaseName);
	}
	
	public static void printTestCaseHeader(){
		printTestCaseHeader(Thread.currentThread().getStackTrace()[2].getMethodName());
	}
	
	public static void printTestClassHeader(String className){
		System.out.println(
				"[============================="
				+className
				+"=============================]");
	}
	/**
	 * Test Segment divider. Used to divide a test case into logical sections.
	 * The weird name is for easy spotting.
	 * @param description of the logical section. This will be printed.
	 */
	public static void ______TS(String description){
		System.out.println(" * "+description);
	}
	
	public static void printTestClassHeader(){
		printTestClassHeader(Thread.currentThread().getStackTrace()[2].getClassName());
	}
	
	public static void printTestClassFooter(String testClassName){
		System.out.println(testClassName+" completed");
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
	
	
	
}
