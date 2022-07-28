package teammates.common.util;

import org.apache.commons.lang.RandomStringUtils;

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

    /**
     * Generates a trimmed random string of the given length.
     * Characters will be chosen from the set of characters whose ASCII value is between 32 and 126 (inclusive).
     */
    public static String generateRandomAsciiStringOfLength(int length) {
        assert length >= 0;
        String generatedString = RandomStringUtils.randomAscii(length);
        while (generatedString.length() != generatedString.trim().length()) {
            generatedString = generatedString.trim()
                    .concat(RandomStringUtils.randomAscii(length - generatedString.trim().length()));
        }
        return generatedString;
    }

}
