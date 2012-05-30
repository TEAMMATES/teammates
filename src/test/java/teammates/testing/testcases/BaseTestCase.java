package teammates.testing.testcases;

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
	
	public static void printTestClassHeader(){
		printTestClassHeader(Thread.currentThread().getStackTrace()[2].getClassName());
	}
	
	public static void printTestClassFooter(String testClassName){
		System.out.println(testClassName+" completed");
	}

	/*
	 * @deprecated No need to use this anymore.  
	 */
	@Deprecated
	protected String getNameOfThisMethod() {
		return Thread.currentThread().getStackTrace()[2].getMethodName();
	}
	
	/*
	 * @deprecated No need to use this anymore.  
	 */
	@Deprecated
	protected static String getNameOfThisClass() {
		return Thread.currentThread().getStackTrace()[2].getClassName();
	}

}
