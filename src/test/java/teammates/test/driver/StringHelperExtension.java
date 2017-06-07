package teammates.test.driver;

import java.util.Random;

import teammates.common.util.StringHelper;

/**
 * Holds additional methods for {@link StringHelper} used only in tests.
 */
public final class StringHelperExtension {

    private static final String UPPERCASE_ALPHANUMERIC_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

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
     * Generates a salt (random alphanumeric string) of given length.
     * @param length of salt to be be generated
     * @return generated salt
     */
    public static String generateSaltOfLength(int length) {
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < length) {
            int index = rnd.nextInt(UPPERCASE_ALPHANUMERIC_CHARS.length());
            salt.append(UPPERCASE_ALPHANUMERIC_CHARS.charAt(index));
        }
        return salt.toString();
    }

}
