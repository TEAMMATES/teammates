package teammates.common.util;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import teammates.common.exception.TeammatesException;

import com.google.appengine.api.utils.SystemProperty;

/**
 * A singleton class that represents the deployment-specific configuration 
 * values of the system. 
 * This can be used to access values in the build.properties file too.
 */
public class Config {

    private static Logger log = Utils.getLogger();
    private static Config instance = inst();
    private static Properties props = null;
    
    /** The value of the "app.url" in build.properties file */
    public static String APP_URL;
    
    /** The value of the "app.gcs.bucketname" in build.properties file */
    public static String GCS_BUCKETNAME;
    
    /** The value of the "app.backdoor.key" in build.properties file */
    public static String BACKDOOR_KEY;
    
    /** The value of the "app.encryption.key" in build.properties file */
    public static String ENCRYPTION_KEY;
    
    /** The value of the "app.persistence.checkduration" in build.properties file */
    public static int    PERSISTENCE_CHECK_DURATION;
    
    /** The value of the "app.crashreport.email" in build.properties file */
    public static String SUPPORT_EMAIL;
    
    /** The value of the "app.student.motd.url" in build.properties file */
    public static String STUDENT_MOTD_URL;
    
    /** The value of the "app.sendgrid.username" in build.properties file */
    public static String SENDGRID_USERNAME;
    
    /** The value of the "app.sendgrid.password" in build.properties file */
    public static String SENDGRID_PASSWORD;

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
        GCS_BUCKETNAME= instance.getGcsBucketname();
        BACKDOOR_KEY = instance.getBackdoorKey();
        ENCRYPTION_KEY = instance.getEncyptionKey();
        PERSISTENCE_CHECK_DURATION = instance.getPersistenceCheckduration();
        SUPPORT_EMAIL = instance.getSupportEmail();
        STUDENT_MOTD_URL = instance.getStudentMotdUrl();
        SENDGRID_USERNAME = instance.getSendgridUsername();
        SENDGRID_PASSWORD = instance.getSendgridPassword();
    }

    private String getGcsBucketname() {
        return props.getProperty("app.gcs.bucketname");
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

    private String getStudentMotdUrl() {
        return props.getProperty("app.student.motd.url");
    }
    
    private String getSendgridUsername() {
        return props.getProperty("app.sendgrid.username");
    }
    
    private String getSendgridPassword() {
        return props.getProperty("app.sendgrid.password");
    }
    
    public static boolean isUsingSendgrid() {
        return SENDGRID_USERNAME != null && !SENDGRID_USERNAME.isEmpty();
    }

}