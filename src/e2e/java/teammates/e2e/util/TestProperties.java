package teammates.e2e.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Represents properties in test.properties file.
 */
public final class TestProperties {

    /** The directory where JSON files used to create data bundles are stored. */
    public static final String TEST_DATA_FOLDER = "src/e2e/resources/data";

    /** The directory where webdriver downloads files to. */
    public static final String TEST_DOWNLOADS_FOLDER = "src/e2e/resources/downloads";

    /** The value of "test.app.url" in test.properties file. */
    public static final String TEAMMATES_URL;

    /** The email address used for testing that emails are sent by the system. */
    public static final String TEST_EMAIL;

    /** The email address used by the system the send emails. */
    public static final String TEST_SENDER_EMAIL;

    /** The value of "test.csrf.key" in test.properties file. */
    public static final String CSRF_KEY;

    /** The value of "test.backdoor.key" in test.properties file. */
    public static final String BACKDOOR_KEY;

    /** The value of "test.selenium.browser" in test.properties file. */
    public static final String BROWSER;
    /** One of the allowed values of "test.selenium.browser" in test.properties file. */
    public static final String BROWSER_CHROME = "chrome";
    /** One of the allowed values of "test.selenium.browser" in test.properties file. */
    public static final String BROWSER_FIREFOX = "firefox";

    /** The value of "test.browser.closeonfailure" in test.properties file. */
    public static final boolean CLOSE_BROWSER_ON_FAILURE;

    /** The value of "test.firefox.path" in test.properties file. */
    public static final String FIREFOX_PATH;

    /** The value of "test.chromedriver.path" in test.properties file. */
    public static final String CHROMEDRIVER_PATH;

    /** The value of "test.geckodriver.path" in test.properties file. */
    public static final String GECKODRIVER_PATH;

    /** The value of "test.firefox.profile.name" in test.properties file. */
    public static final String FIREFOX_PROFILE_NAME;

    /** The value of "test.chrome.userdata.path" in test.properties file. */
    public static final String CHROME_USER_DATA_PATH;

    /** The value of "test.timeout" in test.properties file. */
    public static final int TEST_TIMEOUT;

    /** The value of "test.persistence.timeout" in test.properties file. */
    public static final int PERSISTENCE_RETRY_PERIOD_IN_S;

    /** The flag to indicate whether emails sent should be verified. */
    public static final boolean INCLUDE_EMAIL_VERIFICATION;

    /** The directory where credentials used in Gmail API are stored. */
    static final String TEST_GMAIL_API_FOLDER = "src/e2e/resources/gmail-api";

    static {
        Properties prop = new Properties();
        try {
            try (InputStream testPropStream = Files.newInputStream(Paths.get("src/e2e/resources/test.properties"))) {
                prop.load(testPropStream);
            }

            TEAMMATES_URL = prop.getProperty("test.app.url");

            TEST_EMAIL = prop.getProperty("test.email");
            TEST_SENDER_EMAIL = prop.getProperty("test.senderemail");

            CSRF_KEY = prop.getProperty("test.csrf.key");
            BACKDOOR_KEY = prop.getProperty("test.backdoor.key");

            BROWSER = prop.getProperty("test.selenium.browser").toLowerCase();
            CLOSE_BROWSER_ON_FAILURE = Boolean.parseBoolean(prop.getProperty("test.browser.closeonfailure"));
            FIREFOX_PATH = prop.getProperty("test.firefox.path");
            CHROMEDRIVER_PATH = prop.getProperty("test.chromedriver.path");
            GECKODRIVER_PATH = prop.getProperty("test.geckodriver.path");
            FIREFOX_PROFILE_NAME = prop.getProperty("test.firefox.profile.name");
            CHROME_USER_DATA_PATH = prop.getProperty("test.chrome.userdata.path");

            TEST_TIMEOUT = Integer.parseInt(prop.getProperty("test.timeout"));
            PERSISTENCE_RETRY_PERIOD_IN_S = Integer.parseInt(prop.getProperty("test.persistence.timeout"));

            INCLUDE_EMAIL_VERIFICATION = Boolean.parseBoolean(prop.getProperty("test.verify.emails"));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private TestProperties() {
        // access static fields directly
    }

    public static boolean isDevServer() {
        return TEAMMATES_URL.matches("^https?://localhost:[0-9]+(/.*)?");
    }

}
