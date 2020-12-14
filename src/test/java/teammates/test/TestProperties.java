package teammates.test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Settings for component tests.
 */
public final class TestProperties {

    /** The directory where HTML files for testing email contents are stored. */
    public static final String TEST_EMAILS_FOLDER = "src/test/resources/emails";

    /** The directory where JSON files used to create data bundles are stored. */
    public static final String TEST_DATA_FOLDER = "src/test/resources/data";

    /** The value of "test.persistence.timeout" in test.properties file. */
    public static final int PERSISTENCE_RETRY_PERIOD_IN_S;

    /** Indicates whether auto-update snapshot mode is activated. */
    public static final boolean IS_SNAPSHOT_UPDATE;

    private TestProperties() {
        // access static fields directly
    }

    static {
        Properties prop = new Properties();
        try {
            try (InputStream testPropStream = Files.newInputStream(Paths.get("src/test/resources/test.properties"))) {
                prop.load(testPropStream);
            }

            IS_SNAPSHOT_UPDATE = Boolean.parseBoolean(prop.getProperty("test.snapshot.update", "false"));

            PERSISTENCE_RETRY_PERIOD_IN_S = Integer.parseInt(prop.getProperty("test.persistence.timeout"));

        } catch (IOException | NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

}
