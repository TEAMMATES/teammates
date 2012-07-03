package teammates.testing.lib;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class TestProperties {
	
	
	public String TEAMMATES_URL;

	public String TEAMMATES_URL_IN_EMAILS;

	public String TEAMMATES_COMMON_PASSWORD_FOR_STUDENT_ACCOUNTS;

	public String TEST_COORD_ACCOUNT;
	public String TEST_COORD_PASSWORD;

	public String TEST_STUDENT_ACCOUNT;
	public String TEST_STUDENT_PASSWORD;
	
	public String TEST_ADMIN_ACCOUNT;
	public String TEST_ADMIN_PASSWORD;
	
	public String TEST_UNREG_ACCOUNT;
	public String TEST_UNREG_PASSWORD;

	public String BACKDOOR_KEY;

	public String BROWSER;
	public String SELENIUMRC_HOST;
	public int SELENIUMRC_PORT;

	
	private static TestProperties instance;
	private Properties prop;
	
	private TestProperties() {
		prop = new Properties();
		try {
			
			prop.load(new FileInputStream("src/test/resources/test.properties"));
			
			// Remove trailing space and remove slash at the end
			TEAMMATES_URL = prop.getProperty("test.app.url").trim().replaceAll("/(?=$)","");
			
			TEAMMATES_URL_IN_EMAILS = prop.getProperty("test.app.urlInEmails");
			
			//TODO: abolish the use of common password and find a better alternative
			TEAMMATES_COMMON_PASSWORD_FOR_STUDENT_ACCOUNTS = prop.getProperty("test.common.password");
			
			TEST_ADMIN_ACCOUNT = prop.getProperty("test.admin.account");
			TEST_ADMIN_PASSWORD = prop.getProperty("test.admin.password");
			
			TEST_COORD_ACCOUNT = prop.getProperty("test.coord.account");
			TEST_COORD_PASSWORD = prop.getProperty("test.coord.password");
			
			TEST_STUDENT_ACCOUNT = prop.getProperty("test.student.account");
			TEST_STUDENT_PASSWORD = prop.getProperty("test.student.password");
			
			TEST_UNREG_ACCOUNT = prop.getProperty("test.unreg.account");
			TEST_UNREG_PASSWORD = prop.getProperty("test.unreg.password");
			
			BACKDOOR_KEY = prop.getProperty("test.backdoor.key");
			
			BROWSER = prop.getProperty("test.selenium.browser");
			SELENIUMRC_HOST = prop.getProperty("test.selenium.host");
			SELENIUMRC_PORT = Integer.parseInt(prop.getProperty("test.selenium.port"));

		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	public static TestProperties inst() {
		if (instance == null)
			instance = new TestProperties();
		return instance;
	}


	public static String getChromeDriverPath() {
		String os = System.getProperty("os.name");
		if (os.startsWith("Windows")) {
			return "./chromedriver/chromedriver.exe";
		} else if (os.startsWith("Mac OS")) {
			return "./chromedriver/chromedriver_osx";
		}
		return "";
	}
	
	public boolean isLocalHost(){
		return TEAMMATES_URL.contains("localhost");
	}

}