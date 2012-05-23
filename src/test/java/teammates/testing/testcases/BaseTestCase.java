package teammates.testing.testcases;

public class BaseTestCase {
	
	public static void printTestCaseHeader(String testCaseName){
		System.out.println("[TestCase]------------:"+testCaseName);
	}
	
	public static void printTestClassHeader(String testClassName){
		System.out.println(
				"[============================="
				+testClassName
				+"=============================]");
	}
	
	public static void printTestClassFooter(String testClassName){
		System.out.println(testClassName+" completed");
	}

	protected String getNameOfThisMethod() {
		return Thread.currentThread().getStackTrace()[2].getMethodName();
	}
	
	protected static String getNameOfThisClass() {
		return Thread.currentThread().getStackTrace()[2].getClassName();
	}

}
