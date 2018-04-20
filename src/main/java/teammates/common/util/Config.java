package teammates.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.google.appengine.api.utils.SystemProperty;
import com.google.apphosting.api.ApiProxy;

import teammates.common.exception.TeammatesException;

/**
 * Represents the deployment-specific configuration values of the system.
 * This can be used to access values in the build.properties file too.
 */
public final class Config {

    /** The value of the application URL, or null if no server instance is running. */
    public static final String APP_URL;

    /** The value of the "app.gcs.bucketname" in build.properties file. */
    public static final String GCS_BUCKETNAME;

    /** The value of the "app.backdoor.key" in build.properties file. */
    public static final String BACKDOOR_KEY;

    /** The value of the "app.encryption.key" in build.properties file. */
    public static final String ENCRYPTION_KEY;

    /** The value of the "app.persistence.checkduration" in build.properties file. */
    public static final int PERSISTENCE_CHECK_DURATION;

    /** The value of the "app.crashreport.email" in build.properties file. */
    public static final String SUPPORT_EMAIL;

    /** The value of the "app.student.motd.url" in build.properties file. */
    public static final String STUDENT_MOTD_URL;

    /** The value of the "app.email.senderemail" in build.properties file. */
    public static final String EMAIL_SENDEREMAIL;

    /** The value of the "app.email.sendername" in build.properties file. */
    public static final String EMAIL_SENDERNAME;

    /** The value of the "app.email.replyto" in build.properties file. */
    public static final String EMAIL_REPLYTO;

    /** The value of the "app.email.service" in build.properties file. */
    public static final String EMAIL_SERVICE;

    /** The value of the "app.sendgrid.apikey" in build.properties file. */
    public static final String SENDGRID_APIKEY;

    /** The value of the "app.mailgun.apikey" in build.properties file. */
    public static final String MAILGUN_APIKEY;

    /** The value of the "app.mailgun.domainname" in build.properties file. */
    public static final String MAILGUN_DOMAINNAME;

    /** The value of the "app.mailjet.apikey" in build.properties file. */
    public static final String MAILJET_APIKEY;

    /** The value of the "app.mailjet.secretkey" in build.properties file. */
    public static final String MAILJET_SECRETKEY;

    static {
        APP_URL = readAppUrl();
        Properties properties = new Properties();
        try (InputStream buildPropStream = FileHelper.getResourceAsStream("build.properties")) {
            properties.load(buildPropStream);
        } catch (IOException e) {
            Assumption.fail(TeammatesException.toStringWithStackTrace(e));
        }
        BACKDOOR_KEY = properties.getProperty("app.backdoor.key");
        GCS_BUCKETNAME = properties.getProperty("app.gcs.bucketname");
        ENCRYPTION_KEY = properties.getProperty("app.encryption.key");
        PERSISTENCE_CHECK_DURATION = Integer.parseInt(properties.getProperty("app.persistence.checkduration"));
        SUPPORT_EMAIL = properties.getProperty("app.crashreport.email");
        STUDENT_MOTD_URL = properties.getProperty("app.student.motd.url");
        EMAIL_SENDEREMAIL = properties.getProperty("app.email.senderemail");
        EMAIL_SENDERNAME = properties.getProperty("app.email.sendername");
        EMAIL_REPLYTO = properties.getProperty("app.email.replyto");
        EMAIL_SERVICE = properties.getProperty("app.email.service");
        SENDGRID_APIKEY = properties.getProperty("app.sendgrid.apikey");
        MAILGUN_APIKEY = properties.getProperty("app.mailgun.apikey");
        MAILGUN_DOMAINNAME = properties.getProperty("app.mailgun.domainname");
        MAILJET_APIKEY = properties.getProperty("app.mailjet.apikey");
        MAILJET_SECRETKEY = properties.getProperty("app.mailjet.secretkey");
    }

    private Config() {
        // access static fields directly
    }

    /**
     * This method is not to be used by classes not compiled by GAE (e.g non-production codes).
     * @return The app ID specified in appengine-web.xml, e.g. "teammatesv4"
     */
    public static String getAppId() {
        return SystemProperty.applicationId.get();
    }

    /**
     * Returns The app version specifed in appengine-web.xml but with '.' instead of '-' e.g., "6.0.0".
     */
    public static String getAppVersion() {
        String appVersion = SystemProperty.applicationVersion.get();
        return appVersion == null ? null : appVersion.split("\\.")[0].replace("-", ".");
    }

    private static String readAppUrl() {
        ApiProxy.Environment serverEnvironment = ApiProxy.getCurrentEnvironment();
        if (serverEnvironment == null) {
            return null;
        }
        String hostname = (String) serverEnvironment.getAttributes()
                .get("com.google.appengine.runtime.default_version_hostname");
        if (hostname == null) {
            return null;
        }
        return (isDevServer() ? "http://" : "https://") + hostname;
    }

    private static boolean isDevServer() {
        return SystemProperty.environment.value() != SystemProperty.Environment.Value.Production;
    }

    /**
     * Creates an {@link AppUrl} for the supplied {@code relativeUrl} parameter.
     * The base URL will be the application URL.
     * {@code relativeUrl} must start with a "/".
     */
    public static AppUrl getAppUrl(String relativeUrl) {
        return new AppUrl(APP_URL + relativeUrl);
    }

    public static boolean isUsingSendgrid() {
        return "sendgrid".equalsIgnoreCase(EMAIL_SERVICE) && SENDGRID_APIKEY != null && !SENDGRID_APIKEY.isEmpty();
    }

    public static boolean isUsingMailgun() {
        return "mailgun".equalsIgnoreCase(EMAIL_SERVICE) && MAILGUN_APIKEY != null && !MAILGUN_APIKEY.isEmpty()
                && MAILGUN_DOMAINNAME != null && !MAILGUN_DOMAINNAME.isEmpty();
    }

    public static boolean isUsingMailjet() {
        return "mailjet".equalsIgnoreCase(EMAIL_SERVICE) && MAILJET_APIKEY != null && !MAILJET_APIKEY.isEmpty()
                && MAILJET_SECRETKEY != null && !MAILJET_SECRETKEY.isEmpty();
    }

}
