package teammates.lnp.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Represents properties in test.properties file.
 */
public final class TestProperties {

    /** The directory where the JMeter performance test files are stored. */
    public static final String JMETER_TEST_DIRECTORY = "src/test/lnpTests/";

    /** The directory where JSON data files used to create the CSV data configs are stored. */
    public static final String JMETER_TEST_DATA_DIRECTORY = "src/test/resources/data";

    /** The directory where the JMeter performance test results are stored. */
    public static final String JMETER_TEST_RESULTS_DIRECTORY = "src/test/lnpResults/";

    /** The value of "test.jmeter.home" in test.properties file. */
    public static final String JMETER_HOME;

    /** The value of "test.jmeter.properties" in test.properties file. */
    public static final String JMETER_PROPERTIES_PATH;

    /** Path to the test.properties file. */
    private static String testPropertiesPath = "src/test/resources/test.properties";

    private TestProperties() {
        // access static fields directly
    }

    static {
        Properties prop = new Properties();
        try {
            try (InputStream testPropStream = Files.newInputStream(Paths.get(testPropertiesPath))) {
                prop.load(testPropStream);
            }

            JMETER_HOME = prop.getProperty("test.jmeter.home").toLowerCase();
            JMETER_PROPERTIES_PATH = prop.getProperty("test.jmeter.properties", "").toLowerCase();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
