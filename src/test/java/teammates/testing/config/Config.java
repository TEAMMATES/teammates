package teammates.testing.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
	
	// Live Site URL
	public String TEAMMATES_LIVE_SITE;

	// TeamMates-related configuration
	public String TEAMMATES_APP;
	public String TEAMMATES_URL;
	// public static final String TEAMMATES_URL = "http://localhost:8080/";
	public String MAIL_HOST;

	public String TEAMMATES_APP_ACCOUNT;
	public String TEAMMATES_APP_PASSWD;

	public String TEAMMATES_COORD_ID;

	// Individual Evaluation Reminder Testing Account
	public String INDIVIDUAL_NAME;
	public String INDIVIDUAL_ACCOUNT;
	
	public String TEAMMATES_ADMIN_ACCOUNT;
	public String TEAMMATES_ADMIN_PASSWORD;

	/**
	 * Password for TestSuite to communicate with the Teammates APIServlet.
	 * Remember to change this to match the server's auth code before running
	 */
	public String API_AUTH_CODE;

	/**
	 * Which browser are we using? Please use the following: firefox, chrome,
	 * iexplore, opera, htmlunit, safariproxy
	 */
	public String BROWSER;

	public String SELENIUMRC_HOST;
	public int SELENIUMRC_PORT;

	public String MAIL_STRESS_TEST_ACCOUNT;
	public int MAIL_STRESS_TEST_SIZE;
	
	private static Config instance;
	private Properties prop;
	
	
	
	

	private Config() {
		prop = new Properties();
		try {
			// This is teammates.testing.config.Config file
			
			prop.load(new FileInputStream("src/test/resources/test.properties"));
			
			TEAMMATES_LIVE_SITE = prop.getProperty("test.app.liveSite");
			TEAMMATES_URL = prop.getProperty("test.app.url");
			TEAMMATES_APP = prop.getProperty("test.app.id");
			TEAMMATES_APP_ACCOUNT = prop.getProperty("test.app.account");
			TEAMMATES_APP_PASSWD = prop.getProperty("test.app.password");
			TEAMMATES_COORD_ID = prop.getProperty("test.coord.id");
			INDIVIDUAL_ACCOUNT = prop.getProperty("test.individual.account");
			INDIVIDUAL_NAME = prop.getProperty("test.individual.name");
			TEAMMATES_ADMIN_ACCOUNT = prop.getProperty("test.admin.account");
			TEAMMATES_ADMIN_PASSWORD = prop.getProperty("test.admin.password");
			API_AUTH_CODE = prop.getProperty("test.app.authCode");
			BROWSER = prop.getProperty("test.selenium.browser");
			SELENIUMRC_HOST = prop.getProperty("test.selenium.host");
			SELENIUMRC_PORT = Integer.parseInt(prop.getProperty("test.selenium.port"));
			MAIL_HOST = prop.getProperty("test.mail.host");
			MAIL_STRESS_TEST_ACCOUNT = prop.getProperty("test.mail.account");
			MAIL_STRESS_TEST_SIZE = Integer.parseInt(prop.getProperty("test.mail.size"));

		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	public static Config inst() {
		if (instance == null)
			instance = new Config();
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