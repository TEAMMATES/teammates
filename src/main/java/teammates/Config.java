package teammates;

import java.io.IOException;
import java.util.Properties;

public class Config {
	// TeamMates-related configuration
	// public static final String TEAMMATES_APP_ACCOUNT = "xialin.z21@gmail.com";
	// public static final String TEAMMATES_APP_ACCOUNT = "wangshasg@gmail.com";
	// public static final String TEAMMATES_APP_ACCOUNT =
	// "app.teammates@gmail.com";
	// public static final String TEAMMATES_APP_ACCOUNT =
	// "kalpitjain03@gmail.com";
	public String TEAMMATES_APP_ACCOUNT = null;
	public String TEAMMATES_APP_URL = null;

	// temporary
	public boolean development_mode;
	public boolean APP_PRODUCTION_MOLD;
	public boolean emailEnabled;

	/**
	 * Password for TestSuite to communicate with the APIServlet. Remember to
	 * change this to something private before deploying to real server
	 */
	public String API_AUTH_CODE = null;

	public static Config instance = null;
	private Properties prop = null;

	private Config() {
		prop = new Properties();
		try {

			// prop.load(new FileInputStream("/build.properties"));
			prop.load(this.getClass().getClassLoader().getResourceAsStream(
				"build.properties"));
			TEAMMATES_APP_ACCOUNT = prop.getProperty("app.account");
			TEAMMATES_APP_URL = prop.getProperty("app.url");
			development_mode = Boolean.parseBoolean(prop.getProperty("app.mode.development"));
			APP_PRODUCTION_MOLD = Boolean.parseBoolean(prop.getProperty("app.mode.production"));
			emailEnabled = Boolean.parseBoolean(prop.getProperty("app.emailEnable"));
			API_AUTH_CODE = prop.getProperty("api.authCode");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static Config inst() {
		if (instance == null) {
			instance = new Config();
		}
		return instance;
	}

}