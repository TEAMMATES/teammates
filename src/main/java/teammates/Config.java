package teammates;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import teammates.api.Common;

public class Config {
	
	Logger log = Common.getLogger();
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

	public static Config inst(String propertiesFile){
		return createInstance(propertiesFile);
	}

	public static Config inst() {
		return createInstance("build.properties");
	}
	
	private static Config createInstance(String propertiesFile) {
		if (instance == null) {
			instance = new Config(propertiesFile);
		}
		return instance;
	}
	
	private Config(String propertiesFile) {
		try {
			log.info("Loading properties file: "+ propertiesFile);
			prop = getProperties(propertiesFile);
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

	public static Properties getProperties(String propertiesFile) throws IOException {
		Properties returnValue = new Properties();
		
		returnValue.load(Config.class.getClassLoader().getResourceAsStream(
			propertiesFile));

		return returnValue;
	}
	



}