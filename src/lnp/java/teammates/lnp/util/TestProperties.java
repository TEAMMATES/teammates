package teammates.lnp.util;

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

    /** The directory where the L&P test data files are stored. */
    public static final String LNP_TEST_DATA_FOLDER = "src/lnp/resources/data";

    /** The directory where the L&P test configuration files are stored. */
    public static final String LNP_TEST_CONFIG_FOLDER = "src/lnp/resources/tests";

    /** The directory where the L&P test results are stored. */
    public static final String LNP_TEST_RESULTS_FOLDER = "src/lnp/resources/results";

    /** The value of "test.jmeter.home" in test.properties file. */
    public static final String JMETER_HOME;

    /** The value of "test.jmeter.properties" in test.properties file. */
    public static final String JMETER_PROPERTIES_PATH;

    /** The value of "test.app.url" in test.properties file. */
    public static final String TEAMMATES_URL;

    /** The value of "test.app.domain" in test.properties file. */
    public static final String TEAMMATES_DOMAIN;

    /** The value of "test.app.port" in test.properties file. */
    public static final String TEAMMATES_PORT;

    /** The value of "test.csrf.key" in test.properties file. */
    public static final String CSRF_KEY;

    /** The value of "test.backdoor.key" in test.properties file. */
    public static final String BACKDOOR_KEY;

    static {
        Properties prop = new Properties();
        try {
            try (InputStream testPropStream = Files.newInputStream(Paths.get("src/lnp/resources/test.properties"))) {
                prop.load(testPropStream);
            }
            TEAMMATES_URL = prop.getProperty("test.app.url");
            TEAMMATES_DOMAIN = prop.getProperty("test.app.domain");
            TEAMMATES_PORT = prop.getProperty("test.app.port");
            CSRF_KEY = prop.getProperty("test.csrf.key");
            BACKDOOR_KEY = prop.getProperty("test.backdoor.key");

            JMETER_HOME = prop.getProperty("test.jmeter.home").toLowerCase();
            JMETER_PROPERTIES_PATH = prop.getProperty("test.jmeter.properties", "").toLowerCase();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private TestProperties() {
        // access static fields directly
    }

}
