package teammates.test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import teammates.common.util.StringHelper;

/**
 * Settings for component tests.
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public final class TestProperties {

    /** The directory where HTML files for testing email contents are stored. */
    public static final String TEST_EMAILS_FOLDER = "src/test/resources/emails";

    /** The directory where JSON files used to create data bundles are stored. */
    public static final String TEST_DATA_FOLDER = "src/test/resources/data";

    /** The value of "test.localdatastore.port" in test.properties file. */
    public static final int TEST_LOCALDATASTORE_PORT;

    /** Indicates whether auto-update snapshot mode is activated. */
    public static final boolean IS_SNAPSHOT_UPDATE;

    /** The value of "test.search.service.host" in test.search.service.host file. */
    public static final String SEARCH_SERVICE_HOST;

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
            TEST_LOCALDATASTORE_PORT = Integer.parseInt(prop.getProperty("test.localdatastore.port"));
            SEARCH_SERVICE_HOST = prop.getProperty("test.search.service.host");

        } catch (IOException | NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isSearchServiceActive() {
        return !StringHelper.isEmpty(SEARCH_SERVICE_HOST);
    }

}
