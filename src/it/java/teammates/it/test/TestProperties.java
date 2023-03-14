package teammates.it.test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Settings for integration tests.
 */
public final class TestProperties {

    /** The directory where JSON files used to create data bundles are stored. */
    public static final String TEST_DATA_FOLDER = "src/it/resources/data";

    /** The value of "test.localdatastore.port" in test.properties file. */
    public static final int TEST_LOCALDATASTORE_PORT;

    private TestProperties() {
        // prevent instantiation
    }

    static {
        Properties prop = new Properties();
        try {
            // TODO: remove after migration
            try (InputStream testPropStream = Files.newInputStream(Paths.get("src/test/resources/test.properties"))) {
                prop.load(testPropStream);
            }

            TEST_LOCALDATASTORE_PORT = Integer.parseInt(prop.getProperty("test.localdatastore.port"));

        } catch (IOException | NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

}
