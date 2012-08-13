package teammates.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Logger;

public class BuildProperties {

	private static Logger log = Common.getLogger();

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
		return new Scanner(stream).useDelimiter("\\Z").next();
	}
	
	/**
	 * Get property value from props
	 */
	public String getBuildProperty(String key) {
		if (key.equals("TEAMMATES_APP_ADMIN_EMAIL"))
			return props.getProperty("app.admin.email");
		else if (key.equals("TEAMMATES_APP_URL"))
			return props.getProperty("app.url");
		else if (key.equals("BACKDOOR_KEY"))
			return props.getProperty("app.backdoor.key");
		else if (key.equals("PERSISTENCE_CHECK_DURATION"))
			return props.getProperty("app.persistence.checkduration");
		else 
			return "";
	}

}