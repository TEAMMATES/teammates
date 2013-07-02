package teammates.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Logger;

import teammates.common.exception.TeammatesException;

import com.google.appengine.api.utils.SystemProperty;

public class BuildProperties {

	private static Logger log = Config.getLogger();

	public static BuildProperties instance = null;
	
	public static Properties props = null;
	
	public static BuildProperties inst() {
		if (instance == null) {
			Properties prop = new Properties();
			try {
				prop.load(BuildProperties.class.getClassLoader()
						.getResourceAsStream("build.properties"));
				instance = new BuildProperties();
				instance.readProperties(prop);
			} catch (IOException e) {
				log.severe("Cannot create Config:"
						+ TeammatesException.toStringWithStackTrace(e));
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
	public BuildProperties inst(Properties prop) {
		if (instance == null) {
			instance = new BuildProperties();
		}
		instance.readProperties(prop);
		
		return instance;
	}

	/**
	 * BuildProperties only acts as the loader for retrieving resources from files
	 * The properties are read and stored in BuildProperties static class then accessed by getters.
	 * This is to minimize dependency of BuildProperties->Common
	 */
	public void readProperties(Properties prop) {
		props = prop;
	}
	
	/**
	 * Reads from a stream and returns the string
	 * 
	 * @param reader
	 * @return
	 */
	public String readStream(InputStream stream) {
		Scanner scanner = new Scanner(stream);
		String content = scanner.useDelimiter("\\Z").next();
		scanner.close();
		return content;
	}
	
	/**
	 * Get property value from props
	 * A different getter for each property to handle various return types.
	 */
	
	public String getAppUrl() {
		return props.getProperty("app.url");
	}
	
	public String getAppBackdoorKey() {
		return props.getProperty("app.backdoor.key");
	}
	
	public String getEncyptionKey(){
		return props.getProperty("app.encryption.key");
	}
	
	public int getAppPersistenceCheckduration() {
		return Integer.valueOf(props.getProperty("app.persistence.checkduration")).intValue();
	}
	
	public static String getAppVersion() {
		return SystemProperty.applicationVersion.get().split("\\.")[0].replace("-", ".");
	}
	public String getAppCrashReportEmail() {
		return props.getProperty("app.crashreport.email");
	}

}