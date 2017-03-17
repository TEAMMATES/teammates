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

    public static final String TEST_PAGES_FOLDER = "src/test/resources/pages";
    public static final String TEST_EMAILS_FOLDER = "src/test/resources/emails";
    public static final String TEST_DATA_FOLDER = "src/test/resources/data";

    public static final String TEAMMATES_REMOTEAPI_APP_DOMAIN;
    public static final int TEAMMATES_REMOTEAPI_APP_PORT;

    public static final String TEAMMATES_URL;
    public static final String TEAMMATES_VERSION;

    public static final String TEST_INSTRUCTOR_ACCOUNT;
    public static final String TEST_INSTRUCTOR_PASSWORD;

    public static final String TEST_STUDENT1_ACCOUNT;
    public static final String TEST_STUDENT1_PASSWORD;

    public static final String TEST_STUDENT2_ACCOUNT;
    public static final String TEST_STUDENT2_PASSWORD;

    public static final String TEST_ADMIN_ACCOUNT;
    public static final String TEST_ADMIN_PASSWORD;

    public static final String TEST_UNREG_ACCOUNT;
    public static final String TEST_UNREG_PASSWORD;

    public static final String BACKDOOR_KEY;

    public static final String BROWSER;
    public static final String FIREFOX_PATH;
    public static final String CHROMEDRIVER_PATH;

    public static final int TEST_TIMEOUT;

    static {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("src/test/resources/test.properties"));

            TEAMMATES_URL = Url.trimTrailingSlash(prop.getProperty("test.app.url"));

            // remove "http\://" and "https\://"
            String remoteApiDomain = TEAMMATES_URL.substring(TEAMMATES_URL.indexOf("://") + 3);
            TEAMMATES_REMOTEAPI_APP_DOMAIN = remoteApiDomain.split(":")[0];
            TEAMMATES_REMOTEAPI_APP_PORT =
                    remoteApiDomain.contains(":") ? Integer.parseInt(remoteApiDomain.split(":")[1]) : 443;

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

    public static String extractVersionNumber(String inputString) {
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
