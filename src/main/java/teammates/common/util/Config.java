package teammates.common.util;

import java.io.IOException;
import java.util.Properties;

import com.google.appengine.api.utils.SystemProperty;

/**
 * Represents the deployment-specific configuration values of the system.
 * This can be used to access values in the build.properties file too.
 */
public final class Config {

    /** The value of the "app.url" in build.properties file */
    public static final String APP_URL;
    
    /** The value of the "app.gcs.bucketname" in build.properties file */
    public static final String GCS_BUCKETNAME;
    
    /** The value of the "app.backdoor.key" in build.properties file */
    public static final String BACKDOOR_KEY;
    
    /** The value of the "app.encryption.key" in build.properties file */
    public static final String ENCRYPTION_KEY;
    
    /** The value of the "app.persistence.checkduration" in build.properties file */
    public static final int PERSISTENCE_CHECK_DURATION;
    
    /** The value of the "app.crashreport.email" in build.properties file */
    public static final String SUPPORT_EMAIL;
    
    /** The value of the "app.student.motd.url" in build.properties file */
    public static final String STUDENT_MOTD_URL;
    
    /** The value of the "app.email.service" in build.properties file */
    public static final String EMAIL_SERVICE;
    
    /** The value of the "app.sendgrid.apikey" in build.properties file */
    public static final String SENDGRID_APIKEY;
    
    static {
        Properties properties = new Properties();
        try {
            properties.load(FileHelper.getResourceAsStream("build.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        APP_URL = Url.trimTrailingSlash(properties.getProperty("app.url"));
        BACKDOOR_KEY = properties.getProperty("app.backdoor.key");
        GCS_BUCKETNAME = properties.getProperty("app.gcs.bucketname");
        ENCRYPTION_KEY = properties.getProperty("app.encryption.key");
        PERSISTENCE_CHECK_DURATION = Integer.valueOf(properties.getProperty("app.persistence.checkduration"));
        SUPPORT_EMAIL = properties.getProperty("app.crashreport.email");
        STUDENT_MOTD_URL = properties.getProperty("app.student.motd.url");
        EMAIL_SERVICE = properties.getProperty("app.email.service");
        SENDGRID_APIKEY = properties.getProperty("app.sendgrid.apikey");
    }
    
    private Config() {
        // access static fields directly
    }
    
    /**
     * @return The app ID e.g., "teammatesv4"
     */
    public static String getAppId() {
        return SystemProperty.applicationId.get();
    }

    /**
     * @return The app version specifed in appengine-web.xml but with '.'
     * instead of '-' e.g., "4.53"
     */
    public static String getAppVersion() {
        String appVersion = SystemProperty.applicationVersion.get();
        return appVersion == null ? null : appVersion.split("\\.")[0].replace("-", ".");
    }

    /**
     * This method is not to be used by classes not compiled by GAE (e.g non-production codes).
     * @return true if the system is running at development environment
     */
    public static boolean isDevServer() {
        return SystemProperty.environment.value() == SystemProperty.Environment.Value.Development;
    }

    /**
     * Creates an {@link AppUrl} for the supplied {@code relativeUrl} parameter.
     * The base URL will be the value of app.url in build.properties.
     * {@code relativeUrl} must start with a "/".
     */
    public static AppUrl getAppUrl(String relativeUrl) {
        return new AppUrl(APP_URL + relativeUrl);
    }

    public static boolean isUsingSendgrid() {
        return "sendgrid".equalsIgnoreCase(EMAIL_SERVICE) && SENDGRID_APIKEY != null && !SENDGRID_APIKEY.isEmpty();
    }
}
