package teammates.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Deployment-specific configuration loaded from classpath resources {@code build.properties} (required) and
 * {@code build-dev.properties} (optional).
 *
 * <p><b>Environment</b> — {@link #APP_ENV} is resolved by {@code resolveAppEnv}: non-blank {@code APP_ENV} environment
 * variable if set, otherwise non-blank {@code app.env} in {@code build-dev.properties}, then non-blank {@code app.env}
 * in {@code build.properties}.
 *
 * <p><b>Property resolution</b> — For most keys, when {@link #IS_DEV_SERVER} is {@code true}, the value is taken from
 * {@code build-dev.properties} if the key is present, else from {@code build.properties} (and optional string defaults
 * in code). When {@link #IS_DEV_SERVER} is {@code false}, only {@code build.properties} is used for those keys.
 */
public final class Config {

    /** Value of {@code app.id}. */
    public static final String APP_ID;

    /** Value of {@code app.region}. */
    public static final String APP_REGION;

    /** Backend application version. */
    public static final String APP_VERSION = "9.0.0-beta.6";

    /** Effective deployment environment name (e.g. {@code development}, {@code production}); see class Javadoc. */
    public static final String APP_ENV;

    /** Value of {@code app.frontend.url}. */
    public static final String APP_FRONTEND_URL;

    /** Value of {@code app.postgres.host}. */
    public static final String POSTGRES_HOST;

    /** Value of {@code app.postgres.port}. */
    public static final String POSTGRES_PORT;

    /** Value of {@code app.postgres.databasename}. */
    public static final String POSTGRES_DATABASENAME;

    /** Value of {@code app.postgres.username}. */
    public static final String POSTGRES_USERNAME;

    /** Value of {@code app.postgres.password}. */
    public static final String POSTGRES_PASSWORD;

    /** Value of {@code app.production.gcs.bucketname}. */
    public static final String PRODUCTION_GCS_BUCKETNAME;

    /** Value of {@code app.backup.gcs.bucketname}. */
    public static final String BACKUP_GCS_BUCKETNAME;

    /** Value of {@code app.csrf.key}. */
    public static final String CSRF_KEY;

    /** Value of {@code app.backdoor.key}. */
    public static final String BACKDOOR_KEY;

    /**
     * Value of {@code app.cron.and.worker.secret}. Bearer auth for external schedulers and Cloud Tasks calling
     * {@code /auto/*} and {@code /worker/*}.
     */
    public static final String CRON_AND_WORKER_SECRET;

    /**
     * UTF-8 bytes of {@link #CRON_AND_WORKER_SECRET} when that value is well-formed per
     * {@link AutomatedRequestAuth#isCronAndWorkerSecretWellFormed(String)}; otherwise an empty array.
     * Pre-computed for constant-time comparison without re-encoding on each request.
     */
    public static final byte[] CRON_AND_WORKER_SECRET_BYTES;

    /** Value of {@code app.encryption.key}. */
    public static final String ENCRYPTION_KEY;

    /** The value {@code app.hmac.key}. */
    public static final String HMAC_KEY;

    /** Value of {@code app.auth.type}. */
    public static final String AUTH_TYPE;

    /** Value of {@code app.oauth2.client.id}. */
    public static final String OAUTH2_CLIENT_ID;

    /** Value of {@code app.oauth2.client.secret}. */
    public static final String OAUTH2_CLIENT_SECRET;

    /** Value of {@code app.captcha.secretkey}. */
    public static final String CAPTCHA_SECRET_KEY;

    /** Value of {@code app.admins} (comma-separated in the property file). */
    public static final List<String> APP_ADMINS;

    /** Value of {@code app.maintainers} (comma-separated in the property file). */
    public static final List<String> APP_MAINTAINERS;

    /** Value of {@code app.crashreport.email}. */
    public static final String SUPPORT_EMAIL;

    /** Value of {@code app.email.senderemail}. */
    public static final String EMAIL_SENDEREMAIL;

    /** Value of {@code app.email.sendername}. */
    public static final String EMAIL_SENDERNAME;

    /** Value of {@code app.email.replyto}. */
    public static final String EMAIL_REPLYTO;

    /** Value of {@code app.email.service}. */
    public static final String EMAIL_SERVICE;

    /** Value of {@code app.smtp.host}. */
    public static final String SMTP_HOST;

    /** Value of {@code app.smtp.port}. */
    public static final String SMTP_PORT;

    /** Value of {@code app.smtp.security.protocol}. */
    public static final String SMTP_SECURITY_PROTOCOL;

    /** Value of {@code app.smtp.auth}. */
    public static final String SMTP_AUTH;

    /** Value of {@code app.smtp.username}. */
    public static final String SMTP_USERNAME;

    /** Value of {@code app.smtp.password}. */
    public static final String SMTP_PASSWORD;

    /** Value of {@code app.sendgrid.apikey}. */
    public static final String SENDGRID_APIKEY;

    /** Value of {@code app.mailgun.apikey}. */
    public static final String MAILGUN_APIKEY;

    /** Value of {@code app.mailgun.domainname}. */
    public static final String MAILGUN_DOMAINNAME;

    /** Value of {@code app.mailjet.apikey}. */
    public static final String MAILJET_APIKEY;

    /** Value of {@code app.mailjet.secretkey}. */
    public static final String MAILJET_SECRETKEY;

    /** Value of {@code app.maintenance}. */
    public static final boolean MAINTENANCE;

    /** Value of {@code app.enable.devserver.login} (default {@code false} if unset). */
    public static final boolean ENABLE_DEVSERVER_LOGIN;

    /** Value of {@code app.taskqueue.active} (default {@code true} if unset). */
    public static final boolean TASKQUEUE_ACTIVE;

    /** Value of {@code app.taskqueue.service} (default {@code google-cloud-tasks} in production, {@code local} in dev). */
    public static final String TASKQUEUE_SERVICE;

    // Other properties

    /** {@code true} when {@link #APP_ENV} is {@code development} (case-insensitive). */
    public static final boolean IS_DEV_SERVER;

    private static final int LEGACY_ENCRYPTION_KEY_HEX_LENGTH = 32;
    private static final int ENCRYPTION_KEY_HEX_LENGTH = 64;

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

        APP_ID = getProperty(properties, devProperties, "app.id");

        APP_REGION = getProperty(properties, devProperties, "app.region");
        APP_FRONTEND_URL = getProperty(properties, devProperties, "app.frontend.url", getDefaultFrontEndUrl());
        CSRF_KEY = getProperty(properties, devProperties, "app.csrf.key");
        BACKDOOR_KEY = getProperty(properties, devProperties, "app.backdoor.key");
        CRON_AND_WORKER_SECRET = getProperty(properties, devProperties, "app.cron.and.worker.secret");
        CRON_AND_WORKER_SECRET_BYTES = AutomatedRequestAuth.isCronAndWorkerSecretWellFormed(CRON_AND_WORKER_SECRET)
                ? CRON_AND_WORKER_SECRET.getBytes(StandardCharsets.UTF_8)
                : new byte[0];
        PRODUCTION_GCS_BUCKETNAME = getProperty(properties, devProperties, "app.production.gcs.bucketname");
        POSTGRES_HOST = getProperty(properties, devProperties, "app.postgres.host");
        POSTGRES_PORT = getProperty(properties, devProperties, "app.postgres.port");
        POSTGRES_DATABASENAME = getProperty(properties, devProperties, "app.postgres.databasename");
        POSTGRES_USERNAME = getProperty(properties, devProperties, "app.postgres.username");
        POSTGRES_PASSWORD = getProperty(properties, devProperties, "app.postgres.password");
        BACKUP_GCS_BUCKETNAME = getProperty(properties, devProperties, "app.backup.gcs.bucketname");
        ENCRYPTION_KEY = validateHexKey(getProperty(properties, devProperties, "app.encryption.key"), "app.encryption.key");
        HMAC_KEY = validateHexKey(getProperty(properties, devProperties, "app.hmac.key"), "app.hmac.key");
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

        ENABLE_DEVSERVER_LOGIN = Boolean.parseBoolean(
                getProperty(properties, devProperties, "app.enable.devserver.login", "false"));
        TASKQUEUE_ACTIVE = Boolean.parseBoolean(
                getProperty(properties, devProperties, "app.taskqueue.active", "true"));
        TASKQUEUE_SERVICE = getProperty(properties, devProperties, "app.taskqueue.service",
                IS_DEV_SERVER ? "local" : "google-cloud-tasks");
    }

    private Config() {
        // access static fields directly
    }

    private static String validateHexKey(String key, String propertyName) {
        if (key == null) {
            throw new IllegalStateException("Missing " + propertyName + " in build.properties/build-dev.properties");
        }

        if (!key.matches("[0-9A-Fa-f]+") || key.length() % 2 != 0) {
            throw new IllegalStateException(propertyName + " must be a valid hexadecimal string with 64 chars "
                    + "(32 bytes)");
        }

        if (key.length() == LEGACY_ENCRYPTION_KEY_HEX_LENGTH && "app.encryption.key".equals(propertyName)) {
            // TODO: Remove this migration guard after all active environments have switched to 32-byte keys.
            throw new IllegalStateException("Detected legacy 16-byte app.encryption.key (32 hex chars). "
                    + "Update app.encryption.key to 32 bytes (64 hex chars) and reset the DB.");
        }

        if (key.length() != ENCRYPTION_KEY_HEX_LENGTH) {
            throw new IllegalStateException(propertyName + " must be exactly 64 hex chars (32 bytes)");
        }

        return key;
    }

    /**
     * Default for {@code app.frontend.url} when that key is missing: local URL in development, otherwise the GAE host.
     */
    static String getDefaultFrontEndUrl() {
        return IS_DEV_SERVER ? "http://localhost:" + getPort() : "https://" + APP_ID + ".appspot.com";
    }

    /**
     * Resolves a string property: when {@link #IS_DEV_SERVER} is {@code true}, uses {@code build-dev.properties}
     * if the key exists, otherwise {@code build.properties} (and {@code defaultValue} when provided). When
     * {@link #IS_DEV_SERVER} is {@code false}, only {@code build.properties} (and defaults) are used.
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
     * Like {@link #getProperty(Properties, Properties, String, String)} with no default string; may return {@code null}.
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
     * Resolves effective deployment environment: {@code APP_ENV} environment variable, then {@code app.env} in
     * {@code build-dev.properties}, then {@code build.properties}. The value must be {@code development} or
     * {@code production}.
     */
    private static String resolveAppEnv(Properties properties, Properties devProperties) {
        String fromEnv = System.getenv("APP_ENV");
        if (fromEnv != null && !fromEnv.isBlank()) {
            return validateAppEnv(fromEnv.trim());
        }
        String fromDev = devProperties.getProperty("app.env");
        if (fromDev != null && !fromDev.isBlank()) {
            return validateAppEnv(fromDev.trim());
        }
        String fromBase = properties.getProperty("app.env");
        if (fromBase != null && !fromBase.isBlank()) {
            return validateAppEnv(fromBase.trim());
        }
        throw new IllegalStateException(
                "Set APP_ENV or app.env in build.properties or build-dev.properties to development or production.");
    }

    private static String validateAppEnv(String value) {
        if ("development".equalsIgnoreCase(value) || "production".equalsIgnoreCase(value)) {
            return value;
        }
        throw new IllegalStateException("Invalid environment: " + value + ". Must be development or production.");
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
     * Returns the list of admin Google IDs from {@code app.admins}.
     * TODO: refactor all direct accesses to the field to this method call for consistency.
     */
    public static List<String> getAppAdmins() {
        return APP_ADMINS;
    }

    /**
     * Returns the list of maintainer Google IDs from {@code app.maintainers}.
     * TODO: refactor all direct accesses to the field with this method call for consistency.
     */
    public static List<String> getAppMaintainers() {
        return APP_MAINTAINERS;
    }

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
        if (!AutomatedRequestAuth.isCronAndWorkerSecretWellFormed(CRON_AND_WORKER_SECRET)) {
            throw new IllegalStateException(
                    "app.cron.and.worker.secret must be set in build.properties without leading or trailing "
                            + "whitespace for worker/cron requests.");
        }
    }

}
