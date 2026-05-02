package teammates.e2e.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Represents properties in test.properties file.
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public final class TestProperties {

    /** The directory where JSON files used to create data bundles are stored. */
    public static final String TEST_DATA_FOLDER = "src/e2e/resources/data";

    /** The directory where webdriver downloads files to. */
    public static final String TEST_DOWNLOADS_FOLDER = "src/e2e/resources/downloads";

    /** The value of "test.app.frontend.url" in test.properties file. */
    public static final String TEAMMATES_FRONTEND_URL;

    /** The value of "test.app.backend.url" in test.properties file. */
    public static final String TEAMMATES_BACKEND_URL;

    /** The Google ID of user with admin permission. */
    public static final String TEST_ADMIN;

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
    /** One of the allowed values of "test.selenium.browser" in test.properties file. */
    public static final String BROWSER_EDGE = "edge";

    /** The value of "test.browser.closeonfailure" in test.properties file. */
    public static final boolean CLOSE_BROWSER_ON_FAILURE;

    /** The value of "test.timeout" in test.properties file. */
    public static final int TEST_TIMEOUT;

    /**
     * Line separator to be used when performing text comparison.
     *
     * <p>It needs to be redefined here because either the browser or Selenium uses a predetermined line separator
     * instead of the system's line separator.
     */
    public static final String LINE_SEPARATOR = "\n";

    static {
        Properties prop = new Properties();
        try {
            try (InputStream testPropStream = Files.newInputStream(Paths.get("src/e2e/resources/test.properties"))) {
                prop.load(testPropStream);
            }

            TEAMMATES_FRONTEND_URL = prop.getProperty("test.app.frontend.url");
            TEAMMATES_BACKEND_URL = prop.getProperty("test.app.backend.url");

            TEST_ADMIN = prop.getProperty("test.admin");

            CSRF_KEY = prop.getProperty("test.csrf.key");
            BACKDOOR_KEY = prop.getProperty("test.backdoor.key");

            BROWSER = prop.getProperty("test.selenium.browser").toLowerCase();
            CLOSE_BROWSER_ON_FAILURE = Boolean.parseBoolean(prop.getProperty("test.browser.closeonfailure"));

            TEST_TIMEOUT = Integer.parseInt(prop.getProperty("test.timeout"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private TestProperties() {
        // access static fields directly
    }

    public static boolean isDevServer() {
        return TEAMMATES_FRONTEND_URL.matches("^https?://localhost:[0-9]+(/.*)?");
    }

}
