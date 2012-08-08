package teammates.logic;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import teammates.common.Common;

public class BuildProperties {
	
	private static Logger log = Common.getLogger();
	
	public String TEAMMATES_APP_ADMIN_EMAIL = null;
	public String TEAMMATES_APP_URL = null;
	
	/*Email templates*/
	public String STUDENT_EMAIL_TEMPLATE_EVALUATION_ = null;
	public String STUDENT_EMAIL_TEMPLATE_EVALUATION_PUBLISHED = null;
	public String STUDENT_EMAIL_TEMPLATE_COURSE_JOIN = null;
	public String STUDENT_EMAIL_FRAGMENT_COURSE_JOIN = null;


	/**
	 * Password used by Test driver to identify itself. 
	 */
	public String BACKDOOR_KEY = null;

	/**
	 * Generate delay to handle slow writing IO in datastore
	 */
	public int EXISTENCE_CHECKING_MAX_RETRIES = 10;
	
	public static BuildProperties instance = null;
	
	

	public static BuildProperties inst() {
		if (instance == null) {
			Properties prop = new Properties();
			try {
				prop.load(BuildProperties.class.getClassLoader().getResourceAsStream(
						"build.properties"));
				instance = new BuildProperties(prop);
			} catch (IOException e) {
				log.severe("Cannot create Config:"+Common.stackTraceToString(e));
			}
		}
		return instance;
	}
	
	/**
	 * This method can be used to create a BuildProperties object during a different 
	 *   Properties object. 
	 * @param prop
	 * @return
	 */
	public static BuildProperties inst(Properties prop) {
		if (instance == null) {
			instance = new BuildProperties(prop);
		}
		return instance;
	}
	

	private BuildProperties(Properties prop) {
		TEAMMATES_APP_ADMIN_EMAIL = prop.getProperty("app.admin.email");
		TEAMMATES_APP_URL = Common.trimTrailingSlash(prop.getProperty("app.url"));
		
		BACKDOOR_KEY = prop.getProperty("app.backdoor.key");
		
		EXISTENCE_CHECKING_MAX_RETRIES = Integer.valueOf(prop.getProperty("app.persistence.numretries"));
		
		STUDENT_EMAIL_TEMPLATE_EVALUATION_ = Common.readStream(BuildProperties.class.getClassLoader().getResourceAsStream(
						"studentEmailTemplate-evaluation_.html"));
		
		STUDENT_EMAIL_TEMPLATE_EVALUATION_PUBLISHED = Common.readStream(BuildProperties.class.getClassLoader().getResourceAsStream(
				"studentEmailTemplate-evaluationPublished.html"));
		
		STUDENT_EMAIL_FRAGMENT_COURSE_JOIN  = Common.readStream(BuildProperties.class.getClassLoader().getResourceAsStream(
				"studentEmailFragment-courseJoin.html"));
		
		STUDENT_EMAIL_TEMPLATE_COURSE_JOIN  = Common.readStream(BuildProperties.class.getClassLoader().getResourceAsStream(
				"studentEmailTemplate-courseJoin.html"));

	}
	

}