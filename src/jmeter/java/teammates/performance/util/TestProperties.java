package teammates.performance.util;

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
    public static final String TEST_DATA_FOLDER = "src/jmeter/resources/data";

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

            Properties buildProperties = new Properties();
            try (InputStream buildPropStream = Files.newInputStream(Paths.get("src/main/resources/build.properties"))) {
                buildProperties.load(buildPropStream);
            }

            JMETER_HOME = prop.getProperty("test.jmeter.home").toLowerCase();
            JMETER_PROPERTIES_PATH = prop.getProperty("test.jmeter.properties").toLowerCase();

        } catch (IOException | NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

}
