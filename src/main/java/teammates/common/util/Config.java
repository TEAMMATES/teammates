package teammates.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

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

    /** The value of the "app.frontend.url" in build.properties file. */
    public static final String APP_FRONTEND_URL;

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

    /** The value of the "app.auth.type" in build.properties file. */
    public static final String AUTH_TYPE;

    /** The value of the "app.oauth2.client.id" in build.properties file. */
    public static final String OAUTH2_CLIENT_ID;

    /** The value of the "app.oauth2.client.secret" in build.properties file. */
    public static final String OAUTH2_CLIENT_SECRET;

    /** The value of the "app.captcha.secretkey" in build.properties file. */
    public static final String CAPTCHA_SECRET_KEY;

    /** The value of the "app.admins" in build.properties file. */
    public static final List<String> APP_ADMINS;

    /** The value of the "app.maintainers" in build.properties file. */
    public static final List<String> APP_MAINTAINERS;

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

    /** The value of the "app.search.service.host" in build.properties file. */
    public static final String SEARCH_SERVICE_HOST;

    /** The value of the "app.enable.datastore.backup" in build.properties file. */
    public static final boolean ENABLE_DATASTORE_BACKUP;

    /** The value of the "app.maintenance" in build.properties file. */
    public static final boolean MAINTENANCE;

    /** The value of the "app.localdatastore.port" in build-dev.properties file. */
    public static final int APP_LOCALDATASTORE_PORT;

    /** The value of the "app.enable.devserver.login" in build-dev.properties file. */
    public static final boolean ENABLE_DEVSERVER_LOGIN;

    /** The value of the "app.taskqueue.active" in build-dev.properties file. */
    public static final boolean TASKQUEUE_ACTIVE;

    // Other properties

    /** Indicates whether the current server is dev server. */
    public static final boolean IS_DEV_SERVER;

    private static final Logger log = Logger.getLogger();

    static {
        Properties properties = new Properties();
        try (InputStream buildPropStream = FileHelper.getResourceAsStream("build.properties")) {
            properties.load(buildPropStream);
        } catch (IOException e) {
            assert false;
        }

        String appVersion = properties.getProperty("app.version");
        String appId = properties.getProperty("app.id");
        IS_DEV_SERVER = isDevServer(appVersion, appId);

        Properties devProperties = new Properties();
        if (IS_DEV_SERVER) {
            try (InputStream devPropStream = FileHelper.getResourceAsStream("build-dev.properties")) {
                if (devPropStream != null) {
                    devProperties.load(devPropStream);
                }
            } catch (IOException e) {
                log.warning("Dev environment detected but failed to load build-dev.properties file.");
            }
            APP_ID = getProperty(properties, devProperties, "app.id");
            APP_VERSION = getProperty(properties, devProperties, "app.version");
        } else {
            APP_ID = appId;
            APP_VERSION = appVersion;
        }

        APP_REGION = getProperty(properties, devProperties, "app.region");
        APP_FRONTEND_URL = getProperty(properties, devProperties, "app.frontend.url", getDefaultFrontEndUrl());
        CSRF_KEY = getProperty(properties, devProperties, "app.csrf.key");
        BACKDOOR_KEY = getProperty(properties, devProperties, "app.backdoor.key");
        PRODUCTION_GCS_BUCKETNAME = getProperty(properties, devProperties, "app.production.gcs.bucketname");
        BACKUP_GCS_BUCKETNAME = getProperty(properties, devProperties, "app.backup.gcs.bucketname");
        ENCRYPTION_KEY = getProperty(properties, devProperties, "app.encryption.key");
        AUTH_TYPE = getProperty(properties, devProperties, "app.auth.type");
        OAUTH2_CLIENT_ID = getProperty(properties, devProperties, "app.oauth2.client.id");
        OAUTH2_CLIENT_SECRET = getProperty(properties, devProperties, "app.oauth2.client.secret");
        CAPTCHA_SECRET_KEY = getProperty(properties, devProperties, "app.captcha.secretkey");
        APP_ADMINS = Collections.unmodifiableList(
                Arrays.asList(getProperty(properties, devProperties, "app.admins", "").split(",")));
        APP_MAINTAINERS = Collections.unmodifiableList(
                Arrays.asList(getProperty(properties, devProperties, "app.maintainers", "").split(",")));
        SUPPORT_EMAIL = getProperty(properties, devProperties, "app.crashreport.email");
        EMAIL_SENDEREMAIL = getProperty(properties, devProperties, "app.email.senderemail");
        EMAIL_SENDERNAME = getProperty(properties, devProperties, "app.email.sendername");
        EMAIL_REPLYTO = getProperty(properties, devProperties, "app.email.replyto");
        EMAIL_SERVICE = getProperty(properties, devProperties, "app.email.service");
        SENDGRID_APIKEY = getProperty(properties, devProperties, "app.sendgrid.apikey");
        MAILGUN_APIKEY = getProperty(properties, devProperties, "app.mailgun.apikey");
        MAILGUN_DOMAINNAME = getProperty(properties, devProperties, "app.mailgun.domainname");
        MAILJET_APIKEY = getProperty(properties, devProperties, "app.mailjet.apikey");
        MAILJET_SECRETKEY = getProperty(properties, devProperties, "app.mailjet.secretkey");
        SEARCH_SERVICE_HOST = getProperty(properties, devProperties, "app.search.service.host");
        ENABLE_DATASTORE_BACKUP = Boolean.parseBoolean(
                getProperty(properties, devProperties, "app.enable.datastore.backup", "false"));
        MAINTENANCE = Boolean.parseBoolean(getProperty(properties, devProperties, "app.maintenance", "false"));

        // The following properties are not used in production server.
        // So they will only be read from build-dev.properties file.
        APP_LOCALDATASTORE_PORT = Integer.parseInt(devProperties.getProperty("app.localdatastore.port", "8484"));
        ENABLE_DEVSERVER_LOGIN = Boolean.parseBoolean(devProperties.getProperty("app.enable.devserver.login", "false"));
        TASKQUEUE_ACTIVE = Boolean.parseBoolean(devProperties.getProperty("app.taskqueue.active", "true"));
    }

    private Config() {
        // access static fields directly
    }

    /**
     * Returns the a default frontend URL if it is not set in property file(s).
     */
    static String getDefaultFrontEndUrl() {
        return IS_DEV_SERVER ? "http://localhost:" + getPort() : "https://" + APP_ID + ".appspot.com";
    }

    /**
     * Returns the property value based on running environment.
     *
     * <p>If it is in dev server, it will return the value from build-dev.properties file.
     * If the respective key does not exist in build-dev.properties file, or it is in production server,
     * it will return the value from build.properties file instead.
     *
     * <p>If still no key found in build.properties file, the specified default value will be returned.
     */
    private static String getProperty(Properties properties, Properties devProperties, String key, String defaultValue) {
        if (IS_DEV_SERVER) {
            String val = devProperties.getProperty(key);
            if (val != null) {
                return val;
            }
        }
        return defaultValue == null ? properties.getProperty(key) : properties.getProperty(key, defaultValue);
    }

    /**
     * Returns the property value based on running environment. null is returned when no match values are found.
     */
    private static String getProperty(Properties properties, Properties devProperties, String key) {
        return getProperty(properties, devProperties, key, null);
    }

    /**
     * Returns the port number at which the system will be run in.
     */
    public static int getPort() {
        String portEnv = System.getenv("PORT");
        if (portEnv == null || !portEnv.matches("\\d{2,5}")) {
            return 8080;
        }
        return Integer.parseInt(portEnv);
    }

    /**
     * Returns the GAE instance ID.
     */
    public static String getInstanceId() {
        String instanceId = System.getenv("GAE_INSTANCE");
        if (instanceId == null) {
            return "dev_server_instance_id";
        }
        return instanceId;
    }

    /**
     * Returns true if the server is configured to be the dev server.
     */
    private static boolean isDevServer(String appVersion, String appId) {
        // In production server, GAE sets some non-overrideable environment variables.
        // We will make use of some of them to determine whether the server is dev server or not.
        // This means that any developer can replicate this condition in dev server,
        // but it is their own choice and risk should they choose to do so.

        String version = System.getenv("GAE_VERSION");
        if (!appVersion.equals(version)) {
            return true;
        }

        String env = System.getenv("GAE_ENV");
        if ("standard".equals(env)) {
            // GAE standard
            String appName = System.getenv("GAE_APPLICATION");
            return appName == null || !appName.endsWith(appId);
        }

        // GAE flexible; GAE_ENV variable should not exist in GAE flexible environment
        return env != null;
    }

    /**
     * Indicates whether dev server login is enabled.
     */
    public static boolean isDevServerLoginEnabled() {
        return IS_DEV_SERVER && ENABLE_DEVSERVER_LOGIN;
    }

    /**
     * Creates an {@link AppUrl} for the supplied {@code relativeUrl} parameter.
     * The base URL will be the application front-end URL.
     * {@code relativeUrl} must start with a "/".
     */
    public static AppUrl getFrontEndAppUrl(String relativeUrl) {
        return new AppUrl(APP_FRONTEND_URL + relativeUrl);
    }

    public static boolean isUsingFirebase() {
        return "firebase".equalsIgnoreCase(AUTH_TYPE);
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
