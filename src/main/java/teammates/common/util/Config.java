package teammates.common.util;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Properties;
import java.util.logging.Logger;

import teammates.common.exception.TeammatesException;

import com.google.appengine.api.utils.SystemProperty;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * A singleton class that represents the configuration values of the system. 
 * This can be used to access values in the build.properties file too.
 */
public class Config {

	private static Logger log = Config.getLogger();
	private static Config instance = inst();
	private static Properties props = null;
	
	public static final String ENCODING = "UTF8";

	public static final int NUMBER_OF_HOURS_BEFORE_CLOSING_ALERT = 24;

	/** e.g. "2014-04-01 11:59 PM UTC" */
	public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd h:mm a Z";

	/** Number to trim the Google ID when displaying to the user*/
	public static final int USER_ID_MAX_DISPLAY_LENGTH = 23;

	/** The value of the "app.url" in build.properties file */
	public static String APP_URL;
	
	/** The value of the "app.backdoor.key" in build.properties file */
	public static String BACKDOOR_KEY;
	
	/** The value of the "app.encryption.key" in build.properties file */
	public static String ENCRYPTION_KEY;
	
	/** The value of the "app.persistence.checkduration" in build.properties file */
	public static int    PERSISTENCE_CHECK_DURATION;
	
	/** The value of the "app.crashreport.email" in build.properties file */
	public static String SUPPORT_EMAIL;
	

	public static String EMAIL_TEMPLATE_STUDENT_EVALUATION_ = FileHelper.readResourseFile("studentEmailTemplate-evaluation_.html");
	public static String EMAIL_TEMPLATE_STUDENT_EVALUATION_PUBLISHED = FileHelper.readResourseFile("studentEmailTemplate-evaluationPublished.html");
	public static String EMAIL_TEMPLATE_STUDENT_COURSE_JOIN = FileHelper.readResourseFile("studentEmailTemplate-courseJoin.html");
	public static String EMAIL_TEMPLATE_FRAGMENT_STUDENT_COURSE_JOIN = FileHelper.readResourseFile("studentEmailFragment-courseJoin.html");
	public static String EMAIL_TEMPLATE_USER_FEEDBACK_SESSION = FileHelper.readResourseFile("userEmailTemplate-feedbackSession.html");
	public static String EMAIL_TEMPLATE_USER_FEEDBACK_SESSION_PUBLISHED = FileHelper.readResourseFile("userEmailTemplate-feedbackSessionPublished.html");
	public static String EMAIL_TEMPLATE_SYSTEM_ERROR = FileHelper.readResourseFile("systemErrorEmailTemplate.html");

	public static Config inst() {
		if (instance == null) {
			Properties prop = new Properties();
			try {
				prop.load(Config.class.getClassLoader()
						.getResourceAsStream("build.properties"));
				instance = new Config();
				props = prop;
				initProperties();
			} catch (IOException e) {
				log.severe("Cannot create Config:"
						+ TeammatesException.toStringWithStackTrace(e));
			}
		}
		return instance;
	}

	/** 
	 * This method should be used when instantiating loggers within the system.
	 * @return A {@link Logger} class configured with the name of the calling class.
	 */
	public static Logger getLogger() {
		StackTraceElement logRequester = Thread.currentThread().getStackTrace()[2];
		return Logger.getLogger(logRequester.getClassName());
	}

	/**
	 * This creates a Gson object that can handle the Date format we use in the
	 * Json file and also reformat the Json string in pretty-print format. <br>
	 * Technique found in <a href=
	 * "http://code.google.com/p/google-gson/source/browse/trunk/gson/src/test/java/com/google/gson/functional/DefaultTypeAdaptersTest.java?spec=svn327&r=327"
	 * >here </a>
	 */
	public static Gson getTeammatesGson() {
		return new GsonBuilder()
				.setDateFormat(DateFormat.FULL)
				.setDateFormat(DEFAULT_DATE_TIME_FORMAT)
				.setPrettyPrinting()
				.create();
	}

	/**
	 * @return The app ID e.g., "teammatesv4"
	 */
	public String getAppId(){
		return SystemProperty.applicationId.get();
	}

	/**
	 * @return The app version specifed in appengine-web.xml but with '.' 
	 * instead of '-' e.g., "4.53"
	 */
	public String getAppVersion() {
		return SystemProperty.applicationVersion.get().split("\\.")[0].replace("-", ".");
	}

	private static void initProperties(){
		APP_URL = instance.getAppUrl();
		BACKDOOR_KEY = instance.getBackdoorKey();
		ENCRYPTION_KEY = instance.getEncyptionKey();
		PERSISTENCE_CHECK_DURATION = instance.getPersistenceCheckduration();
		SUPPORT_EMAIL = instance.getSupportEmail();
	}

	private String getAppUrl() {
		return props.getProperty("app.url");
	}

	private String getBackdoorKey() {
		return props.getProperty("app.backdoor.key");
	}

	private String getEncyptionKey(){
		return props.getProperty("app.encryption.key");
	}

	private int getPersistenceCheckduration() {
		return Integer.valueOf(props.getProperty("app.persistence.checkduration")).intValue();
	}

	private String getSupportEmail() {
		return props.getProperty("app.crashreport.email");
	}

}