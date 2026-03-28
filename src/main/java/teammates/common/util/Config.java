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

    /**
     * The value of the "app.cron.and.worker.secret" in build.properties file.
     * Used for bearer token auth of cron and worker requests via Authorization: Bearer <token>.
     * Used by external cron providers and Cloud Tasks when calling /auto/* and /worker/* endpoints.
     * */
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

    /** The value of the "app.smtp.host" in build.properties file. */
    public static final String SMTP_HOST;

    /** The value of the "app.smtp.port" in build.properties file. */
    public static final String SMTP_PORT;

    /** The value of the "app.smtp.security.protocol" in build.properties file. */
    public static final String SMTP_SECURITY_PROTOCOL;

    /** The value of the "app.smtp.auth" in build.properties file. */
    public static final String SMTP_AUTH;

    /** The value of the "app.smtp.username" in build.properties file. */
    public static final String SMTP_USERNAME;

    /** The value of the "app.smtp.password" in build.properties file. */
    public static final String SMTP_PASSWORD;

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

        String appId = properties.getProperty("app.id");

        // Hardcoded application version (single source of truth).
        // NOTE: keep in sync with src/web/environments/config.template.ts (frontend)
        final String HARDCODED_APP_VERSION = "8.0.0";

        // Use the hardcoded version when determining dev vs prod server.
        IS_DEV_SERVER = isDevServer(HARDCODED_APP_VERSION, appId);

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
            // Do not read `app.version` from build.properties anymore; use the hardcoded value.
            APP_VERSION = HARDCODED_APP_VERSION;
        } else {
            APP_ID = appId;
            APP_VERSION = HARDCODED_APP_VERSION;
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
        SMTP_HOST = getProperty(properties, devProperties, "app.smtp.host");
        SMTP_PORT = getProperty(properties, devProperties, "app.smtp.port");
        SMTP_AUTH = getProperty(properties, devProperties, "app.smtp.auth");
        SMTP_USERNAME = getProperty(properties, devProperties, "app.smtp.username");
        SMTP_PASSWORD = getProperty(properties, devProperties, "app.smtp.password");
        SMTP_SECURITY_PROTOCOL = getProperty(properties, devProperties, "app.smtp.security.protocol");
        SENDGRID_APIKEY = getProperty(properties, devProperties, "app.sendgrid.apikey");
        MAILGUN_APIKEY = getProperty(properties, devProperties, "app.mailgun.apikey");
        MAILGUN_DOMAINNAME = getProperty(properties, devProperties, "app.mailgun.domainname");
        MAILJET_APIKEY = getProperty(properties, devProperties, "app.mailjet.apikey");
        MAILJET_SECRETKEY = getProperty(properties, devProperties, "app.mailjet.secretkey");
        MAINTENANCE = Boolean.parseBoolean(getProperty(properties, devProperties, "app.maintenance", "false"));

        // The following properties are not used in production server.
        // So they will only be read from build-dev.properties file.
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

    /**
     * Indicates whether SMTP email service is used.
     * @return true if SMTP email service is properly configured and can be used; false otherwise.
     */
    public static boolean isUsingSmtp() {
        boolean isSecurityProtocolValid = "ssl".equalsIgnoreCase(SMTP_SECURITY_PROTOCOL)
                || "starttls".equalsIgnoreCase(SMTP_SECURITY_PROTOCOL);
        boolean isSmtpAuthValid = "true".equalsIgnoreCase(SMTP_AUTH) || "false".equalsIgnoreCase(SMTP_AUTH);
        boolean isAuthEnabled = "true".equalsIgnoreCase(SMTP_AUTH);
        boolean isCredentialValid = !StringHelper.isEmpty(SMTP_USERNAME) && !StringHelper.isEmpty(SMTP_PASSWORD);

        return "smtp".equalsIgnoreCase(EMAIL_SERVICE)
                && !StringHelper.isEmpty(SMTP_HOST) && !StringHelper.isEmpty(SMTP_PORT)
                && isSecurityProtocolValid && isSmtpAuthValid && (!isAuthEnabled || isCredentialValid);
    }

    /**
     * Ensures {@link #CRON_AND_WORKER_SECRET} is configured for authenticating worker/cron HTTP requests.
     *
     * @throws IllegalStateException if the secret is missing or blank
     */
    public static void requireCronAndWorkerSecret() {
        if (!InternalRequestAuth.isCronAndWorkerSecretWellFormed(CRON_AND_WORKER_SECRET)) {
            throw new IllegalStateException(
                    "app.cron.and.worker.secret must be set in build.properties without leading or trailing "
                            + "whitespace for worker/cron requests.");
        }
    }

}
