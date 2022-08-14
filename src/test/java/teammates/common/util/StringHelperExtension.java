package teammates.common.util;

/**
 * Holds additional methods for {@link StringHelper} used only in tests.
 */
public final class StringHelperExtension {

    private StringHelperExtension() {
        // utility class
    }

    /**
     * Generates an arbitrary string of given length.
     * @param length of string to be be generated
     * @return generated string
     */
    public static String generateStringOfLength(int length) {
        return StringHelper.generateStringOfLength(length, 'a');
    }

}
