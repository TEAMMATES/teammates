package teammates.common.util;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.google.common.base.CharMatcher;

import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.TeammatesException;

/**
 * Holds String-related helper functions.
 */

public final class StringHelper {
    private static final Logger log = Logger.getLogger();

    private StringHelper() {
        // utility class
    }

    /**
     * Checks whether the input string is empty or equals {@code null}.
     * @param s The string to be checked
     */
    public static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    /**
     * Generates a string which consists of {@code length} copies of {@code character} without space.
     */
    static String generateStringOfLength(int length, char character) {
        Assumption.assertTrue(length >= 0);
        return String.join("", Collections.nCopies(length, String.valueOf(character)));
    }

    /**
     * Checks whether the input string matches the regex.
     * @param input The string to be matched
     * @param regex The regex  used for the matching
     */
    public static boolean isMatching(String input, String regex) {
        // Important to use the CANON_EQ flag to make sure that canonical characters
        // such as é is correctly matched regardless of single/double code point encoding
        return Pattern.compile(regex, Pattern.CANON_EQ).matcher(input).matches();
    }

    /**
     * Generates a left-indentation of {@code length} units.
     */
    public static String getIndent(int length) {
        return generateStringOfLength(length, ' ');
    }

    /**
     * Trims head of the String if it is longer than specified Length.
     *  E.g., String "12345678" with maximumStringLength = 6, returns "345678"
     * @param maximumStringLength - maximum required length of the string
     * @return String with at most maximumStringLength length
     */
    public static String truncateHead(String inputString, int maximumStringLength) {
        int inputStringLength = inputString.length();
        if (inputStringLength <= maximumStringLength) {
            return inputString;
        }
        return inputString.substring(inputStringLength - maximumStringLength);
    }

    /**
     * Generates the HMAC SHA-1 signature for a supplied string.
     *
     * @param data The string to be signed
     * @return The signature value as a hex-string
     */
    public static String generateSignature(String data) {
        try {
            SecretKeySpec signingKey =
                    new SecretKeySpec(hexStringToByteArray(Config.ENCRYPTION_KEY), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            byte[] value = mac.doFinal(data.getBytes());
            return byteArrayToHexString(value);
        } catch (Exception e) {
            Assumption.fail(TeammatesException.toStringWithStackTrace(e));
            return null;
        }
    }

    /**
     * Verifies the HMAC SHA-1 signature against a given value.
     *
     * @param value The value to be checked
     * @param signature The signature in hex-string format
     * @return True if signature matches value
     */
    public static boolean isCorrectSignature(String value, String signature) {
        if (value == null || signature == null) {
            return false;
        }
        return Objects.equals(generateSignature(value), signature);
    }

    /**
     * Encrypts the supplied string.
     *
     * @param value the plaintext as a string
     * @return the ciphertext
     * @throws RuntimeException if the encryption fails for some reason, such as {@code Cipher} initialization failure.
     */
    public static String encrypt(String value) {
        try {
            SecretKeySpec sks = new SecretKeySpec(hexStringToByteArray(Config.ENCRYPTION_KEY), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, sks, cipher.getParameters());
            byte[] encrypted = cipher.doFinal(value.getBytes());
            return byteArrayToHexString(encrypted);
        } catch (Exception e) {
            Assumption.fail(TeammatesException.toStringWithStackTrace(e));
            return null;
        }
    }

    /**
     * Decrypts the supplied string.
     *
     * @param message the ciphertext as a hexadecimal string
     * @return the plaintext
     * @throws InvalidParametersException if the ciphertext is invalid.
     * @throws RuntimeException if the decryption fails for any other reason, such as {@code Cipher} initialization failure.
     */
    public static String decrypt(String message) throws InvalidParametersException {
        try {
            SecretKeySpec sks = new SecretKeySpec(hexStringToByteArray(Config.ENCRYPTION_KEY), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, sks);
            byte[] decrypted = cipher.doFinal(hexStringToByteArray(message));
            return new String(decrypted);
        } catch (NumberFormatException | IllegalBlockSizeException | BadPaddingException e) {
            log.warning("Attempted to decrypt invalid ciphertext: " + message);
            throw new InvalidParametersException(e);
        } catch (Exception e) {
            Assumption.fail(TeammatesException.toStringWithStackTrace(e));
            return null;
        }
    }

    /**
     * Converts and concatenates a list of objects to a single string, separated by line breaks.
     * The conversion is done by using the {@link Object#toString()} method.
     * @return Concatenated string.
     */
    public static <T> String toString(List<T> list) {
        return toString(list, System.lineSeparator());
    }

    /**
     * Converts and concatenates a list of objects to a single string, separated by the given delimiter.
     * The conversion is done by using the {@link Object#toString()} method.
     * @return Concatenated string.
     */
    public static <T> String toString(List<T> list, String delimiter) {
        return list.stream()
                .map(s -> s.toString())
                .collect(Collectors.joining(delimiter));
    }

    /**
     * Converts a double value between 0 and 1 to 3dp-string.
     */
    public static String toDecimalFormatString(double doubleVal) {
        DecimalFormat df = new DecimalFormat("0.###");
        return df.format(doubleVal);
    }

    /**
     * split a full name string into first and last names
     * <br>
     * 1.If passed in empty string, both last and first name will be empty string
     * <br>
     * 2.If single word, this will be last name and first name will be an empty string
     * <br>
     * 3.If more than two words, the last word will be last name and
     * the rest will be first name.
     * <br>
     * 4.If the last name is enclosed with braces "{}" such as first {Last1 Last2},
     * the last name will be the String inside the braces
     * <br>
     * Example:
     * <br><br>
     * full name "Danny Tim Lin"<br>
     * first name: "Danny Tim" <br>
     * last name: "Lin" <br>
     * processed full name: "Danny Tim Lin" <br>
     * <br>
     * full name "Danny {Tim Lin}"<br>
     * first name: "Danny" <br>
     * last name: "Tim Lin" <br>
     * processed full name: "Danny Tim Lin" <br>
     *
     *
     * @return split name array{0--> first name, 1--> last name, 2--> processed full name by removing "{}"}
     */
    public static String[] splitName(String fullName) {

        if (fullName == null) {
            return new String[] {};
        }

        String lastName;
        String firstName;

        if (fullName.contains("{") && fullName.contains("}")) {
            int startIndex = fullName.indexOf('{');
            int endIndex = fullName.indexOf('}');
            lastName = fullName.substring(startIndex + 1, endIndex);
            firstName = fullName.replace("{", "")
                                .replace("}", "")
                                .replace(lastName, "")
                                .trim();

        } else {
            lastName = fullName.substring(fullName.lastIndexOf(' ') + 1).trim();
            firstName = fullName.replace(lastName, "").trim();
        }

        String processedfullName = fullName.replace("{", "")
                                           .replace("}", "");

        return new String[] {firstName, lastName, processedfullName};
    }

    /**
     * Trims the string and reduces consecutive white spaces to only one space.
     * Example: " a   a  " --> "a a".
     * @return processed string, returns null if parameter is null
     */
    public static String removeExtraSpace(String str) {
        if (str == null) {
            return null;
        }
        return CharMatcher.whitespace().trimFrom(str).replaceAll("\\s+", " ");
    }

    /**
     * Replaces every character in {@code str} that does not match
     * {@code regex} with the character {@code replacement}.
     *
     * @param str String to be replaced.
     * @param regex Pattern that every character is to be matched against.
     * @param replacement Character unmatching characters should be replaced with.
     * @return String with all unmatching characters replaced; null if input is null.
     */
    public static String replaceIllegalChars(String str, String regex, char replacement) {
        if (str == null) {
            return null;
        }

        char[] charArray = str.toCharArray();

        IntStream.range(0, charArray.length)
                .filter(i -> !isMatching(Character.toString(charArray[i]), regex))
                .forEach(i -> charArray[i] = replacement);

        return String.valueOf(charArray);
    }

    /**
     * Converts a byte array to hexadecimal string.
     */
    static String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            int v = b & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase();
    }

    /**
     * Converts a hexadecimal string to byte array.
     */
    public static byte[] hexStringToByteArray(String s) {
        byte[] b = new byte[s.length() / 2];
        IntStream.range(0, b.length)
                .forEach(i -> b[i] = (byte) Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
        return b;
    }

    /**
     * Trims the string if it is not null.
     *
     * @return the trimmed string or null (if the parameter was null).
     */
    public static String trimIfNotNull(String string) {
        return string == null ? null : string.trim();
    }

    /**
     * Converts null input to empty string. Non-null inputs will be left as is.
     * This method is for displaying purpose.
     *
     * @return empty string if null, the string itself otherwise
     */
    public static String convertToEmptyStringIfNull(String str) {
        return str == null ? "" : str;
    }

    /**
     * Returns true if {@code text} contains at least one of the {@code strings} or if {@code strings} is empty.
     * If {@code text} is null, false is returned.
     */
    public static boolean isTextContainingAny(String text, String... strings) {
        if (text == null) {
            return false;
        }

        if (strings.length == 0) {
            return true;
        }

        return Arrays.stream(strings)
                .anyMatch(s -> text.contains(s));
    }

    /**
     * Extract data from quoted string.
     *
     * @param quotedString string to extract data from
     * @return string without quotes
     */
    public static String extractContentFromQuotedString(String quotedString) {
        if (quotedString == null) {
            return null;
        }

        if (quotedString.matches("^\".*\"$")) {
            return quotedString.substring(1, quotedString.length() - 1);
        }
        return quotedString;
    }
}
