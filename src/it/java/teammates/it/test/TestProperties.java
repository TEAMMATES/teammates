package teammates.it.test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Settings for integration tests.
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public final class TestProperties {

    /** The directory where JSON files used to create data bundles are stored. */
    public static final String TEST_DATA_FOLDER = "src/it/resources/data";

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
        } catch (IOException | NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

}
