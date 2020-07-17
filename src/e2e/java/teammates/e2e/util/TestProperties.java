package teammates.e2e.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import teammates.common.util.Url;

/**
 * Represents properties in test.properties file.
 */
public final class TestProperties {

    /** The directory where the L&P test data files are stored. */
    public static final String LNP_TEST_DATA_FOLDER = "src/e2e/lnp/data";

    /** The directory where the L&P test configuration files are stored. */
    public static final String LNP_TEST_CONFIG_FOLDER = "src/e2e/lnp/tests";

    /** The directory where the L&P test results are stored. */
    public static final String LNP_TEST_RESULTS_FOLDER = "src/e2e/lnp/results";

    /** The directory where HTML files for testing pages are stored. */
    public static final String TEST_PAGES_FOLDER = "src/e2e/resources/pages";

    /** The directory where HTML files for testing email contents are stored. */
    public static final String TEST_EMAILS_FOLDER = "src/e2e/resources/emails";

    /** The directory where JSON files used to create data bundles are stored. */
    public static final String TEST_DATA_FOLDER = "src/e2e/resources/data";

    /** The value of "test.app.url" in test.properties file. */
    public static final String TEAMMATES_URL;

    /** The version number of the application under test. */
    public static final String TEAMMATES_VERSION;

    /** The Google ID of the test instructor account. */
    public static final String TEST_INSTRUCTOR_ACCOUNT;

    /** The password of the test instructor account. */
    public static final String TEST_INSTRUCTOR_PASSWORD;

    /** The Google ID of the first test student account. */
    public static final String TEST_STUDENT1_ACCOUNT;

    /** The password of the first test student account. */
    public static final String TEST_STUDENT1_PASSWORD;

    /** The Google ID of the second test student account. */
    public static final String TEST_STUDENT2_ACCOUNT;

    /** The password of the second test student account. */
    public static final String TEST_STUDENT2_PASSWORD;

    /** The Google ID of the test admin account. */
    public static final String TEST_ADMIN_ACCOUNT;

    /** The password of the test admin account. */
    public static final String TEST_ADMIN_PASSWORD;

    /** The Google ID of the test unregistered account. */
    public static final String TEST_UNREG_ACCOUNT;

    /** The password of the test unregistered account. */
    public static final String TEST_UNREG_PASSWORD;

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

    /** The value of "test.jmeter.home" in test.properties file. */
    public static final String JMETER_HOME;

    /** The value of "test.jmeter.properties" in test.properties file. */
    public static final String JMETER_PROPERTIES_PATH;

    /** The directory where credentials used in Gmail API are stored. */
    static final String TEST_GMAIL_API_FOLDER = "src/e2e/resources/gmail-api";

    static {
        Properties prop = new Properties();
        try {
            try (InputStream testPropStream = Files.newInputStream(Paths.get("src/e2e/resources/test.properties"))) {
                prop.load(testPropStream);
            }

            TEAMMATES_URL = Url.trimTrailingSlash(prop.getProperty("test.app.url"));

            Properties buildProperties = new Properties();
            try (InputStream buildPropStream = Files.newInputStream(Paths.get("src/main/resources/build.properties"))) {
                buildProperties.load(buildPropStream);
            }
            TEAMMATES_VERSION = buildProperties.getProperty("app.version");

            TEST_ADMIN_ACCOUNT = prop.getProperty("test.admin.account");
            TEST_ADMIN_PASSWORD = prop.getProperty("test.admin.password");

            TEST_INSTRUCTOR_ACCOUNT = prop.getProperty("test.instructor.account");
            TEST_INSTRUCTOR_PASSWORD = prop.getProperty("test.instructor.password");

            TEST_STUDENT1_ACCOUNT = prop.getProperty("test.student1.account");
            TEST_STUDENT1_PASSWORD = prop.getProperty("test.student1.password");

            TEST_STUDENT2_ACCOUNT = prop.getProperty("test.student2.account");
            TEST_STUDENT2_PASSWORD = prop.getProperty("test.student2.password");

            TEST_UNREG_ACCOUNT = prop.getProperty("test.unreg.account");
            TEST_UNREG_PASSWORD = prop.getProperty("test.unreg.password");

            CSRF_KEY = prop.getProperty("test.csrf.key");
            BACKDOOR_KEY = prop.getProperty("test.backdoor.key");

            BROWSER = prop.getProperty("test.selenium.browser").toLowerCase();
            FIREFOX_PATH = prop.getProperty("test.firefox.path");
            CHROMEDRIVER_PATH = prop.getProperty("test.chromedriver.path");
            GECKODRIVER_PATH = prop.getProperty("test.geckodriver.path");
            FIREFOX_PROFILE_NAME = prop.getProperty("test.firefox.profile.name");
            CHROME_USER_DATA_PATH = prop.getProperty("test.chrome.userdata.path");

            TEST_TIMEOUT = Integer.parseInt(prop.getProperty("test.timeout"));
            PERSISTENCE_RETRY_PERIOD_IN_S = Integer.parseInt(prop.getProperty("test.persistence.timeout"));

            JMETER_HOME = prop.getProperty("test.jmeter.home").toLowerCase();
            JMETER_PROPERTIES_PATH = prop.getProperty("test.jmeter.properties", "").toLowerCase();

        } catch (IOException | NumberFormatException e) {
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
