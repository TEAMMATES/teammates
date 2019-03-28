package teammates.lnp.util;

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

    /** The directory where the JMeter performance test files are stored. */
    public static final String JMETER_TEST_DIRECTORY = "src/jmeter/tests/";

    /** The directory where JSON files used to create data bundles are stored. */
    public static final String JMETER_TEST_DATA_DIRECTORY = "src/jmeter/resources/data";

    /** The directory where the JMeter performance test results are stored. */
    public static final String JMETER_TEST_RESULTS_DIRECTORY = "src/jmeter/results/";

    /** The value of "test.app.url" in test.properties file. */
    public static final String TEAMMATES_URL;

    /** The value of "test.csrf.key" in test.properties file. */
    public static final String CSRF_KEY;

    /** The value of "test.backdoor.key" in test.properties file. */
    public static final String BACKDOOR_KEY;

    /** The value of "test.jmeter.home" in test.properties file. */
    public static final String JMETER_HOME;

    /** The value of "test.jmeter.properties" in test.properties file. */
    public static final String JMETER_PROPERTIES_PATH;

    private TestProperties() {
        // access static fields directly
    }

    static {
        Properties prop = new Properties();
        try {
            try (InputStream testPropStream = Files.newInputStream(Paths.get("src/jmeter/resources/test.properties"))) {
                prop.load(testPropStream);
            }

            TEAMMATES_URL = Url.trimTrailingSlash(prop.getProperty("test.app.url"));

            CSRF_KEY = prop.getProperty("test.csrf.key");
            BACKDOOR_KEY = prop.getProperty("test.backdoor.key");

            JMETER_HOME = prop.getProperty("test.jmeter.home").toLowerCase();
            JMETER_PROPERTIES_PATH = prop.getProperty("test.jmeter.properties", "").toLowerCase();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
