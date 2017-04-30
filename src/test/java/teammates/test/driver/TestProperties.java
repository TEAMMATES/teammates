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

    /** The value of "test.instructor.account" in test.properties file. */
    public static final String TEST_INSTRUCTOR_ACCOUNT;

    /** The value of "test.instructor.password" in test.properties file. */
    public static final String TEST_INSTRUCTOR_PASSWORD;

    /** The value of "test.student1.account" in test.properties file. */
    public static final String TEST_STUDENT1_ACCOUNT;

    /** The value of "test.student1.password" in test.properties file. */
    public static final String TEST_STUDENT1_PASSWORD;

    /** The value of "test.student2.account" in test.properties file. */
    public static final String TEST_STUDENT2_ACCOUNT;

    /** The value of "test.student2.password" in test.properties file. */
    public static final String TEST_STUDENT2_PASSWORD;

    /** The value of "test.admin.account" in test.properties file. */
    public static final String TEST_ADMIN_ACCOUNT;

    /** The value of "test.admin.password" in test.properties file. */
    public static final String TEST_ADMIN_PASSWORD;

    /** The value of "test.unreg.account" in test.properties file. */
    public static final String TEST_UNREG_ACCOUNT;

    /** The value of "test.unreg.password" in test.properties file. */
    public static final String TEST_UNREG_PASSWORD;

    /** The value of "test.backdoor" in test.properties file. */
    public static final String BACKDOOR_KEY;

    /** The value of "test.selenium.browser" in test.properties file. */
    public static final String BROWSER;

    /** The value of "test.firefox.path" in test.properties file. */
    public static final String FIREFOX_PATH;

    /** The value of "test.chromedriver.path" in test.properties file. */
    public static final String CHROMEDRIVER_PATH;

    /** The value of "test.timeout" in test.properties file. */
    public static final int TEST_TIMEOUT;

    static {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("src/test/resources/test.properties"));

            TEAMMATES_URL = Url.trimTrailingSlash(prop.getProperty("test.app.url"));

            TEAMMATES_VERSION = extractVersionNumber(FileHelper.readFile("src/main/webapp/WEB-INF/appengine-web.xml"));

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
     * regeneration via God mode to work smoothly (i.e all test HTML files are correctly regenerated,
     * strings that need to be replaced with placeholders are correctly replaced, and
     * strings that are not supposed to be replaced with placeholders are not replaced).
     */
    public static void verifyReadyForGodMode() {
        if (!isDevServer()) {
            fail("God mode regeneration works only in dev server.");
        }
        if (!areTestAccountsReadyForGodMode()) {
            fail("Please append a unique id (e.g your name) to each of the default account in "
                    + "test.properties in order to use God mode, e.g change alice.tmms to "
                    + "alice.tmms.<yourName>, charlie.tmms to charlie.tmms.<yourName>, etc.");
        }
        if (isStudentMotdUrlEmpty()) {
            fail("Student MOTD URL defined in app.student.motd.url in build.properties "
                    + "must not be empty. It is advised to use test-student-motd.html to test it.");
        }
    }

    private static boolean areTestAccountsReadyForGodMode() {
        if (!TEST_STUDENT1_ACCOUNT.startsWith("alice.tmms.")) {
            return false;
        }
        String uniqueId = TEST_STUDENT1_ACCOUNT.substring("alice.tmms.".length());
        if (uniqueId.isEmpty()) {
            return false;
        }

        boolean isSecondStudentAccountReady = ("charlie.tmms." + uniqueId).equals(TEST_STUDENT2_ACCOUNT);
        boolean isInstructorAccountReady = ("teammates.coord." + uniqueId).equals(TEST_INSTRUCTOR_ACCOUNT);
        boolean isAdminAccountReady = ("yourGoogleId." + uniqueId).equals(TEST_ADMIN_ACCOUNT);
        return isSecondStudentAccountReady && isInstructorAccountReady && isAdminAccountReady;
    }

    private static boolean isStudentMotdUrlEmpty() {
        return Config.STUDENT_MOTD_URL.isEmpty();
    }

}
