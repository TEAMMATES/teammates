package teammates.logic;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import teammates.common.Common;

public class BuildProperties {

	private static Logger log = Common.getLogger();

	public static BuildProperties instance = null;
	
	public static BuildProperties inst() {
		if (instance == null) {
			Properties prop = new Properties();
			try {
				prop.load(BuildProperties.class.getClassLoader()
						.getResourceAsStream("build.properties"));
				instance = new BuildProperties(prop);
			} catch (IOException e) {
				log.severe("Cannot create Config:"
						+ Common.stackTraceToString(e));
			}
		}
		return instance;
	}

	/**
	 * This method can be used to create a BuildProperties object during a
	 * different Properties object.
	 * 
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
		
		/**
		 * BuildProperties only acts as the loader for retrieveing resources from files
		 * The appropriate values are set to Common for run-time use
		 */
		String resource = "";
		
		resource = prop.getProperty("app.admin.email");
		Common.setParamsFromBuildProperties("TEAMMATES_APP_ADMIN_EMAIL",resource);
		
		resource = Common.trimTrailingSlash(prop.getProperty("app.url"));
		Common.setParamsFromBuildProperties("TEAMMATES_APP_URL",resource);

		resource = prop.getProperty("app.backdoor.key");
		Common.setParamsFromBuildProperties("BACKDOOR_KEY",resource);

		resource = prop.getProperty("app.persistence.checkduration");
		Common.setParamsFromBuildProperties("PERSISTENCE_CHECK_DURATION",resource);

		/**
		 * Email templates
		 */
		resource = Common
				.readStream(BuildProperties.class.getClassLoader()
						.getResourceAsStream(
								"studentEmailTemplate-evaluation_.html"));
		Common.setParamsFromBuildProperties("STUDENT_EMAIL_TEMPLATE_EVALUATION_",resource);

		resource = Common
				.readStream(BuildProperties.class
						.getClassLoader()
						.getResourceAsStream(
								"studentEmailTemplate-evaluationPublished.html"));
		Common.setParamsFromBuildProperties("STUDENT_EMAIL_TEMPLATE_EVALUATION_PUBLISHED",resource);

		resource = Common
				.readStream(BuildProperties.class.getClassLoader()
						.getResourceAsStream(
								"studentEmailFragment-courseJoin.html"));
		Common.setParamsFromBuildProperties("STUDENT_EMAIL_FRAGMENT_COURSE_JOIN",resource);

		resource = Common
				.readStream(BuildProperties.class.getClassLoader()
						.getResourceAsStream(
								"studentEmailTemplate-courseJoin.html"));
		Common.setParamsFromBuildProperties("STUDENT_EMAIL_TEMPLATE_COURSE_JOIN",resource);

	}

}