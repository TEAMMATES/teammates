package teammates;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import teammates.api.Common;
import teammates.api.TeammatesException;

public class Config {
	
	private static Logger log = Common.getLogger();
	public String TEAMMATES_APP_ACCOUNT = null;
	public String TEAMMATES_APP_URL = null;
	
	public String STUDENT_EMAIL_TEMPLATE_EVALUATION_ = null;
	public String STUDENT_EMAIL_TEMPLATE_EVALUATION_PUBLISHED = null;
	public String STUDENT_EMAIL_TEMPLATE_COURSE_JOIN = null;
	public String STUDENT_EMAIL_FRAGMENT_COURSE_JOIN = null;

	// temporary
	public boolean development_mode;
	public boolean APP_PRODUCTION_MOLD;
	public boolean emailEnabled;

	/**
	 * Password for TestSuite to communicate with the APIServlet. Remember to
	 * change this to something private before deploying to real server
	 */
	public String BACKDOOR_KEY = null;

	public static Config instance = null;
	
	

	public static Config inst() {
		if (instance == null) {
			Properties prop = new Properties();
			try {
				prop.load(Config.class.getClassLoader().getResourceAsStream(
						"build.properties"));
				instance = new Config(prop);
			} catch (IOException e) {
				log.severe("Cannot create Config:"+TeammatesException.stackTraceToString(e));
			}
		}
		return instance;
	}
	
	/**
	 * This method can be used to create a Config object during a different 
	 *   Properties object. 
	 * @param prop
	 * @return
	 */
	public static Config inst(Properties prop) {
		if (instance == null) {
			instance = new Config(prop);
		}
		return instance;
	}
	

	private Config(Properties prop) {
		TEAMMATES_APP_ACCOUNT = prop.getProperty("app.account");
		TEAMMATES_APP_URL = prop.getProperty("app.url");
		development_mode = Boolean.parseBoolean(prop
				.getProperty("app.mode.development"));
		APP_PRODUCTION_MOLD = Boolean.parseBoolean(prop
				.getProperty("app.mode.production"));
		emailEnabled = Boolean
				.parseBoolean(prop.getProperty("app.emailEnable"));
		BACKDOOR_KEY = prop.getProperty("app.backdoor.key");
		
		STUDENT_EMAIL_TEMPLATE_EVALUATION_ = Common.readStream(Config.class.getClassLoader().getResourceAsStream(
						"studentEmailTemplate-evaluation_.html"));
		
		STUDENT_EMAIL_TEMPLATE_EVALUATION_PUBLISHED = Common.readStream(Config.class.getClassLoader().getResourceAsStream(
				"studentEmailTemplate-evaluationPublished.html"));
		
		STUDENT_EMAIL_FRAGMENT_COURSE_JOIN  = Common.readStream(Config.class.getClassLoader().getResourceAsStream(
				"studentEmailFragment-courseJoin.html"));
		
		STUDENT_EMAIL_TEMPLATE_COURSE_JOIN  = Common.readStream(Config.class.getClassLoader().getResourceAsStream(
				"studentEmailTemplate-courseJoin.html"));

	}
	

}