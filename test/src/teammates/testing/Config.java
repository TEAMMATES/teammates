package teammates.testing;

public class Config {
	// Live Site URL
	public static final String TEAMMATES_LIVE_SITE = "http://teammatesv4.appspot.com/";

	// TeamMates-related configuration
	public static final String TEAMMATES_APP = "teammates-wangsha";
    public static String TEAMMATES_URL = "http://" + TEAMMATES_APP + ".appspot.com/";
    //public static final String TEAMMATES_URL = "http://localhost:8888/";
	public static final String MAIL_HOST = "imap.gmail.com";

	public static final String TEAMMATES_APP_ACCOUNT = "teammates.coord";
	public static final String TEAMMATES_APP_PASSWD = "makeitright";
	
	public static final String TEAMMATES_COORD_ID = "teammates.coord";

	// Individual Evaluation Reminder Testing Account
	public static final String INDiVIDUAL_NAME = "Emily";
	public static final String INDIVIDUAL_ACCOUNT = "emily.tmms@gmail.com";

	/**
	 * Password for TestSuite to communicate with the Teammates APIServlet.
	 * Remember to change this to match the server's auth code before running
	 */
	public static final String API_AUTH_CODE = "3011";

	/**
	 * Which browser are we using? Please use the following: firefox, chrome,
	 * iexplore, opera, htmlunit, safariproxy
	 */
	public static String BROWSER = "firefox";

	public static final String SELENIUMRC_HOST = "localhost";
	public static final int SELENIUMRC_PORT = 4444;

	
	public static String MAIL_STRESS_TEST_ACCOUNT = "emily.tmms@gmail.com";
	public static int MAIL_STRESS_TEST_SIZE = 20;

	public static String getChromeDriverPath() {
		String os = System.getProperty("os.name");
		if (os.startsWith("Windows")) {
			return "./chromedriver/chromedriver.exe";
		} else if (os.startsWith("Mac OS")) {
			return "./chromedriver/chromedriver_osx";
		}
		return "";
	}

}