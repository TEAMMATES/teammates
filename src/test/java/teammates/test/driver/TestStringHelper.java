package teammates.test.driver;

import teammates.common.util.StringHelper;

/**
 * Holds String-related helper functions used only in tests.
 */

public final class TestStringHelper {

    private TestStringHelper() {
        // utility class
    }

    /**
     *generate string of given length.
     */
    public static String generateStringOfLength(int length) {
        return StringHelper.generateStringOfLength(length, 'a');
    }

}
