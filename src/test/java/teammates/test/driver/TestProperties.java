package teammates.test.driver;

import static org.testng.AssertJUnit.fail;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import teammates.common.util.Config;
import teammates.common.util.Url;

/**
 * Represents properties in test.properties file.
 */
public final class TestProperties {

    /** The directory where HTML files for testing pages are stored. */
    public static final String TEST_PAGES_FOLDER = "src/test/resources/pages";

    /** The directory where HTML files for testing email contents are stored. */
    public static final String TEST_EMAILS_FOLDER = "src/test/resources/emails";

    /** The directory where JSON files used to create data bundles are stored. */
    public static final String TEST_DATA_FOLDER = "src/test/resources/data";

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

    /** The value of "test.backdoor" in test.properties file. */
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

    /** The value of "test.timeout" in test.properties file. */
    public static final int TEST_TIMEOUT;

    /** The value of "test.godmode.enabled" in test.properties file. */
    public static final boolean IS_GODMODE_ENABLED;

    /** Maximum period for verification retries due to persistence delays. */
    public static final int PERSISTENCE_RETRY_PERIOD_IN_S = 128;

    /** The directory where credentials used in Gmail API are stored. */
    static final String TEST_GMAIL_API_FOLDER = "src/test/resources/gmail-api";

    static {
        Properties prop = new Properties();
        try {
            String propertiesFilename;
            if (isTravis()) {
                propertiesFilename = "test.travis.properties";
            } else if (isAppveyor()) {
                propertiesFilename = "test.appveyor.properties";
            } else {
                propertiesFilename = "test.properties";
            }
            try (FileInputStream testPropStream = new FileInputStream("src/test/resources/" + propertiesFilename)) {
                prop.load(testPropStream);
            }

            TEAMMATES_URL = Url.trimTrailingSlash(prop.getProperty("test.app.url"));

            TEAMMATES_VERSION = extractVersionNumber(FileHelper.readFile("src/main/webapp/WEB-INF/appengine-web.xml"));

            IS_GODMODE_ENABLED = Boolean.parseBoolean(prop.getProperty("test.godmode.enabled", "false"));

            if (isDevServer() && (isCiEnvironment() || IS_GODMODE_ENABLED)) {
                // For CI and GodMode, we do not read the account details from the test properties file, but generate
                // random account names. This is for detection and prevention of hard-coded account names in test files.
                // The password values are not required for login to the dev server and hence, set to null.

                String dotSalt = "." + StringHelperExtension.generateSaltOfLength(8);

                TEST_ADMIN_ACCOUNT = "yourGoogleId" + dotSalt;
                TEST_ADMIN_PASSWORD = null;

                TEST_INSTRUCTOR_ACCOUNT = "teammates.coord" + dotSalt;
                TEST_INSTRUCTOR_PASSWORD = null;

                TEST_STUDENT1_ACCOUNT = "alice.tmms" + dotSalt;
                TEST_STUDENT1_PASSWORD = null;

                TEST_STUDENT2_ACCOUNT = "charlie.tmms" + dotSalt;
                TEST_STUDENT2_PASSWORD = null;

                TEST_UNREG_ACCOUNT = "teammates.unreg" + dotSalt;
                TEST_UNREG_PASSWORD = null;

            } else {
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
            }

            BACKDOOR_KEY = prop.getProperty("test.backdoor.key");

            BROWSER = prop.getProperty("test.selenium.browser").toLowerCase();
            FIREFOX_PATH = prop.getProperty("test.firefox.path");
            CHROMEDRIVER_PATH = prop.getProperty("test.chromedriver.path");

            TEST_TIMEOUT = Integer.parseInt(prop.getProperty("test.timeout"));

        } catch (IOException | NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

    private TestProperties() {
        // access static fields directly
    }

    public static boolean isTravis() {
        return System.getenv("TRAVIS") != null;
    }

    public static boolean isAppveyor() {
        return System.getenv("APPVEYOR") != null;
    }

    public static boolean isCiEnvironment() {
        return isTravis() || isAppveyor();
    }

    public static boolean isDevServer() {
        return TEAMMATES_URL.contains("localhost");
    }

    private static String extractVersionNumber(String inputString) {
        String startTag = "<version>";
        String endTag = "</version>";
        int startPos = inputString.indexOf(startTag) + startTag.length();
        int endPos = inputString.indexOf(endTag);

        return inputString.substring(startPos, endPos).replace("-", ".").trim();
    }

    /**
     * Verifies that the test properties specified in test.properties file allows for HTML
     * regeneration via GodMode to work smoothly (i.e all test HTML files are correctly regenerated,
     * strings that need to be replaced with placeholders are correctly replaced, and
     * strings that are not supposed to be replaced with placeholders are not replaced).
     */
    public static void verifyReadyForGodMode() {
        if (!isDevServer()) {
            fail("GodMode regeneration works only in dev server.");
        }
        if (isStudentMotdUrlEmpty()) {
            fail("Student MOTD URL defined in app.student.motd.url in build.properties "
                    + "must not be empty. It is advised to use test-student-motd.html to test it.");
        }
    }

    private static boolean isStudentMotdUrlEmpty() {
        return Config.STUDENT_MOTD_URL.isEmpty();
    }

}
