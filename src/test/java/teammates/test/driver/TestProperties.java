package teammates.test.driver;

/**
 * Settings for component tests.
 */
public final class TestProperties {

    /** The directory where HTML files for testing pages are stored. */
    public static final String TEST_PAGES_FOLDER = "src/test/resources/pages";

    /** The directory where HTML files for testing email contents are stored. */
    public static final String TEST_EMAILS_FOLDER = "src/test/resources/emails";

    /** The directory where JSON files used to create data bundles are stored. */
    public static final String TEST_DATA_FOLDER = "src/test/resources/data";

    /** Maximum period for verification retries due to persistence delays. */
    public static final int PERSISTENCE_RETRY_PERIOD_IN_S = 128;

    /** Indicates whether "God mode" is activated. */
    public static final boolean IS_GODMODE_ENABLED = false;

    private TestProperties() {
        // access static fields directly
    }

}
