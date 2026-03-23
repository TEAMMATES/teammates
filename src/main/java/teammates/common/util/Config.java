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

    /**
     * Deployment environment: from {@code APP_ENV} if set, otherwise {@code app.env} in property files
     * (defaults: {@code production} in {@code build.properties}, {@code development} in {@code build-dev.properties}).
     */
    public static final String APP_ENV;

    /** The value of the "app.frontend.url" in build.properties file. */
    public static final String APP_FRONTEND_URL;

    /** The value of the "app.postgres.host" in build.properties file. */
    public static final String POSTGRES_HOST;

    /** The value of the "app.postgres.port" in build.properties file. */
    public static final String POSTGRES_PORT;

    /** The value of the "app.postgres.databasename" in build.properties file. */
    public static final String POSTGRES_DATABASENAME;

    /** The value of the "app.postgres.username" in build.properties file. */
    public static final String POSTGRES_USERNAME;

    /** The value of the "app.postgres.password" in build.properties file. */
    public static final String POSTGRES_PASSWORD;

    /** The value of the "app.production.gcs.bucketname" in build.properties file. */
    public static final String PRODUCTION_GCS_BUCKETNAME;

    /** The value of the "app.backup.gcs.bucketname" in build.properties file. */
    public static final String BACKUP_GCS_BUCKETNAME;

    /** The value of the "app.csrf.key" in build.properties file. */
    public static final String CSRF_KEY;

    /** The value of the "app.backdoor.key" in build.properties file. */
    public static final String BACKDOOR_KEY;

    /** The value of the "app.cron.and.worker.secret" in build.properties file. Used for bearer token auth of cron and worker requests. */
    public static final String CRON_AND_WORKER_SECRET;

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

    /** The value of the "app.maintenance" in build.properties file. */
    public static final boolean MAINTENANCE;

    /** The value of the "app.enable.devserver.login" property (defaults to false). */
    public static final boolean ENABLE_DEVSERVER_LOGIN;

    /** The value of the "app.taskqueue.active" property (defaults to true). */
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

        Properties devProperties = new Properties();
        try (InputStream devPropStream = FileHelper.getResourceAsStream("build-dev.properties")) {
            if (devPropStream != null) {
                devProperties.load(devPropStream);
            }
        } catch (IOException e) {
            log.warning("Failed to load build-dev.properties file.");
        }

        APP_ENV = resolveAppEnv(properties, devProperties);
        IS_DEV_SERVER = "development".equalsIgnoreCase(APP_ENV);

        if (IS_DEV_SERVER) {
            APP_ID = getProperty(properties, devProperties, "app.id");
            APP_VERSION = getProperty(properties, devProperties, "app.version");
        } else {
            APP_ID = properties.getProperty("app.id");
            APP_VERSION = properties.getProperty("app.version");
        }

        APP_REGION = getProperty(properties, devProperties, "app.region");
        APP_FRONTEND_URL = getProperty(properties, devProperties, "app.frontend.url", getDefaultFrontEndUrl());
        CSRF_KEY = getProperty(properties, devProperties, "app.csrf.key");
        BACKDOOR_KEY = getProperty(properties, devProperties, "app.backdoor.key");
        CRON_AND_WORKER_SECRET = getProperty(properties, devProperties, "app.cron.and.worker.secret");
        PRODUCTION_GCS_BUCKETNAME = getProperty(properties, devProperties, "app.production.gcs.bucketname");
        POSTGRES_HOST = getProperty(properties, devProperties, "app.postgres.host");
        POSTGRES_PORT = getProperty(properties, devProperties, "app.postgres.port");
        POSTGRES_DATABASENAME = getProperty(properties, devProperties, "app.postgres.databasename");
        POSTGRES_USERNAME = getProperty(properties, devProperties, "app.postgres.username");
        POSTGRES_PASSWORD = getProperty(properties, devProperties, "app.postgres.password");
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
        MAINTENANCE = Boolean.parseBoolean(getProperty(properties, devProperties, "app.maintenance", "false"));

        // Development-oriented; same resolution as other keys (dev file when IS_DEV_SERVER, else build.properties).
        ENABLE_DEVSERVER_LOGIN = Boolean.parseBoolean(
                getProperty(properties, devProperties, "app.enable.devserver.login", "false"));
        TASKQUEUE_ACTIVE = Boolean.parseBoolean(
                getProperty(properties, devProperties, "app.taskqueue.active", "true"));
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
     * Resolves effective deployment environment. Precedence: {@code APP_ENV} environment variable,
     * then {@code app.env} in {@code build-dev.properties} (if set), then {@code app.env} in {@code build.properties},
     * then backward-compatible defaults when {@code app.env} is missing in both files.
     */
    private static String resolveAppEnv(Properties properties, Properties devProperties) {
        String fromEnv = System.getenv("APP_ENV");
        if (fromEnv != null && !fromEnv.isBlank()) {
            return fromEnv.trim();
        }
        String fromDev = devProperties.getProperty("app.env");
        if (fromDev != null && !fromDev.isBlank()) {
            return fromDev.trim();
        }
        String fromBase = properties.getProperty("app.env");
        if (fromBase != null && !fromBase.isBlank()) {
            return fromBase.trim();
        }
        if (!devProperties.isEmpty()) {
            return "development";
        }
        return "production";
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
     * Returns the list of admin Google IDs configured in build.properties.
     * TODO: refactor all direct accesses to the field to this method call for consistency.
     */
    public static List<String> getAppAdmins() {
        return APP_ADMINS;
    }

    /**
     * Returns the list of maintainer Google IDs configured in build.properties.
     * TODO: refactor all direct accesses to the field with this method call for consistency.
     */
    public static List<String> getAppMaintainers() {
        return APP_MAINTAINERS;
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

    /**
     * Returns db connection URL.
     */
    public static String getDbConnectionUrl() {
        return String.format("jdbc:postgresql://%s:%s/%s", POSTGRES_HOST, POSTGRES_PORT, POSTGRES_DATABASENAME);
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
