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

    /** The value of the "app.id" in build.properties file. */
    public static final String APP_ID;

    /** The value of the "app.region" in build.properties file. */
    public static final String APP_REGION;

    /** The value of the "app.version" in build.properties file. */
    public static final String APP_VERSION;

    /** The value of the "app.frontenddev.url" in build.properties file. */
    public static final String APP_FRONTENDDEV_URL;

    /** The value of the "app.taskqueue.active" in build.properties file. */
    public static final boolean TASKQUEUE_ACTIVE;

    /** The value of the "app.production.gcs.bucketname" in build.properties file. */
    public static final String PRODUCTION_GCS_BUCKETNAME;

    /** The value of the "app.backup.gcs.bucketname" in build.properties file. */
    public static final String BACKUP_GCS_BUCKETNAME;

    /** The value of the "app.csrf.key" in build.properties file. */
    public static final String CSRF_KEY;

    /** The value of the "app.backdoor.key" in build.properties file. */
    public static final String BACKDOOR_KEY;

    /** The value of the "app.encryption.key" in build.properties file. */
    public static final String ENCRYPTION_KEY;

    /** The value of the "app.captcha.secretkey" in build.properties file. */
    public static final String CAPTCHA_SECRET_KEY;

    /** The value of the "app.crashreport.email" in build.properties file. */
    public static final String SUPPORT_EMAIL;

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

    /** The value of the "app.enable.datastore.backup" in build.properties file. */
    public static final boolean ENABLE_DATASTORE_BACKUP;

    /** The value of the "app.maintenance" in build.properties file. */
    public static final boolean MAINTENANCE;

    static {
        Properties properties = new Properties();
        try (InputStream buildPropStream = FileHelper.getResourceAsStream("build.properties")) {
            properties.load(buildPropStream);
        } catch (IOException e) {
            Assumption.fail(TeammatesException.toStringWithStackTrace(e));
        }
        APP_ID = properties.getProperty("app.id");
        APP_REGION = properties.getProperty("app.region");
        APP_VERSION = properties.getProperty("app.version").replace("-", ".");
        APP_FRONTENDDEV_URL = properties.getProperty("app.frontenddev.url");
        TASKQUEUE_ACTIVE = Boolean.parseBoolean(properties.getProperty("app.taskqueue.active", "true"));
        CSRF_KEY = properties.getProperty("app.csrf.key");
        BACKDOOR_KEY = properties.getProperty("app.backdoor.key");
        PRODUCTION_GCS_BUCKETNAME = properties.getProperty("app.production.gcs.bucketname");
        BACKUP_GCS_BUCKETNAME = properties.getProperty("app.backup.gcs.bucketname");
        ENCRYPTION_KEY = properties.getProperty("app.encryption.key");
        CAPTCHA_SECRET_KEY = properties.getProperty("app.captcha.secretkey");
        SUPPORT_EMAIL = properties.getProperty("app.crashreport.email");
        EMAIL_SENDEREMAIL = properties.getProperty("app.email.senderemail");
        EMAIL_SENDERNAME = properties.getProperty("app.email.sendername");
        EMAIL_REPLYTO = properties.getProperty("app.email.replyto");
        EMAIL_SERVICE = properties.getProperty("app.email.service");
        SENDGRID_APIKEY = properties.getProperty("app.sendgrid.apikey");
        MAILGUN_APIKEY = properties.getProperty("app.mailgun.apikey");
        MAILGUN_DOMAINNAME = properties.getProperty("app.mailgun.domainname");
        MAILJET_APIKEY = properties.getProperty("app.mailjet.apikey");
        MAILJET_SECRETKEY = properties.getProperty("app.mailjet.secretkey");
        ENABLE_DATASTORE_BACKUP = Boolean.parseBoolean(properties.getProperty("app.enable.datastore.backup", "false"));
        MAINTENANCE = Boolean.parseBoolean(properties.getProperty("app.maintenance", "false"));
    }

    private Config() {
        // access static fields directly
    }

    static String getBaseAppUrl() {
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

    /**
     * Returns true if the server is configured to be the dev server.
     */
    public static boolean isDevServer() {
        return SystemProperty.environment.value() != SystemProperty.Environment.Value.Production;
    }

    /**
     * Returns the GAE's internal request ID of a request. This is not related to HttpServletRequest.
     *
     * @see <a href="https://cloud.google.com/appengine/docs/standard/java/how-requests-are-handled">https://cloud.google.com/appengine/docs/standard/java/how-requests-are-handled</a>
     */
    public static String getRequestId() {
        ApiProxy.Environment serverEnvironment = ApiProxy.getCurrentEnvironment();
        if (serverEnvironment == null) {
            // This will be the case in dev server
            return "dummyrequestid";
        }
        return String.valueOf(ApiProxy.getCurrentEnvironment().getAttributes()
                .get("com.google.appengine.runtime.request_log_id"));
    }

    /**
     * Creates an {@link AppUrl} for the supplied {@code relativeUrl} parameter.
     * The base URL will be the application front-end URL.
     * {@code relativeUrl} must start with a "/".
     */
    public static AppUrl getFrontEndAppUrl(String relativeUrl) {
        if (Config.isDevServer() && APP_FRONTENDDEV_URL != null) {
            return new AppUrl(APP_FRONTENDDEV_URL + relativeUrl);
        }

        // In production, the back-end and front-end lives under the same domain
        return getBackEndAppUrl(relativeUrl);
    }

    /**
     * Creates an {@link AppUrl} for the supplied {@code relativeUrl} parameter.
     * The base URL will be the application back-end URL.
     * {@code relativeUrl} must start with a "/".
     */
    private static AppUrl getBackEndAppUrl(String relativeUrl) {
        return new AppUrl(getBaseAppUrl() + relativeUrl);
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
