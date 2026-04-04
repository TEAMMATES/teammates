package teammates.common.util;

import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.google.common.base.CharMatcher;

import teammates.common.exception.InvalidParametersException;

/**
 * Holds String-related helper functions.
 */

public final class StringHelper {
    private static final Logger log = Logger.getLogger();
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int MASTER_KEY_LENGTH_BYTES = 32;
    private static final int AES_GCM_IV_LENGTH_BYTES = 12;
    private static final int AES_GCM_TAG_LENGTH_BITS = 128;

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
        assert length >= 0;
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
     * Encrypts the supplied string.
     *
     * @param value the plaintext as a string
     * @return the ciphertext
    * @throws IllegalStateException if the encryption fails for some reason,
    *         such as {@code Cipher} initialization failure.
     */
    public static String encrypt(String value) {
        try {
            SecretKeySpec sks = new SecretKeySpec(getEncryptionKey(), "AES");
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            byte[] iv = new byte[AES_GCM_IV_LENGTH_BYTES];
            SECURE_RANDOM.nextBytes(iv);
            cipher.init(Cipher.ENCRYPT_MODE, sks, new GCMParameterSpec(AES_GCM_TAG_LENGTH_BITS, iv));
            byte[] encrypted = cipher.doFinal(value.getBytes(Const.ENCODING));

            byte[] encryptedWithIv = new byte[iv.length + encrypted.length];
            System.arraycopy(iv, 0, encryptedWithIv, 0, iv.length);
            System.arraycopy(encrypted, 0, encryptedWithIv, iv.length, encrypted.length);
            return byteArrayToHexString(encryptedWithIv);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to encrypt value", e);
        }
    }

    /**
     * Decrypts the supplied string.
     *
     * @param message the ciphertext as a hexadecimal string
     * @return the plaintext
     * @throws InvalidParametersException if the ciphertext is invalid.
    * @throws IllegalStateException if the decryption fails for any other reason,
    *         such as {@code Cipher} initialization failure.
     */
    public static String decrypt(String message) throws InvalidParametersException {
        byte[] encryptedWithIv;
        try {
            encryptedWithIv = hexStringToByteArray(message);
        } catch (NumberFormatException e) {
            log.warning("Attempted to decrypt invalid ciphertext, input length: "
                    + (message == null ? -1 : message.length()));
            throw new InvalidParametersException(e);
        }

        if (encryptedWithIv.length <= AES_GCM_IV_LENGTH_BYTES) {
            log.warning("Attempted to decrypt invalid ciphertext, byte length: " + encryptedWithIv.length);
            throw new InvalidParametersException("Ciphertext does not contain IV and payload");
        }

        try {
            SecretKeySpec sks = new SecretKeySpec(getEncryptionKey(), "AES");
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, sks,
                    new GCMParameterSpec(AES_GCM_TAG_LENGTH_BITS, encryptedWithIv, 0, AES_GCM_IV_LENGTH_BYTES));
            byte[] decrypted = cipher.doFinal(encryptedWithIv, AES_GCM_IV_LENGTH_BYTES,
                    encryptedWithIv.length - AES_GCM_IV_LENGTH_BYTES);
            return new String(decrypted, Const.ENCODING);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            log.warning("Attempted to decrypt invalid ciphertext, byte length: " + encryptedWithIv.length);
            throw new InvalidParametersException(e);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to decrypt message", e);
        }
    }

    /**
     * Generates a deterministic HMAC SHA-256 signature for the supplied string.
     *
     * @param data the plaintext as a string
     * @return the HMAC SHA-256 signature as a hex-string
     * @throws IllegalStateException if signature generation fails.
     */
    public static String generateSha256Hmac(String data) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(getHmacKey(), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);
            byte[] value = mac.doFinal(data.getBytes(Const.ENCODING));
            return byteArrayToHexString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate HMAC-SHA256 signature", e);
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
        DecimalFormatSymbols syms = new DecimalFormatSymbols();
        syms.setDecimalSeparator('.');
        DecimalFormat df = new DecimalFormat("0.###", syms);
        return df.format(doubleVal);
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

    private static byte[] getEncryptionKey() {
        byte[] masterKey = hexStringToByteArray(Config.ENCRYPTION_KEY);
        if (masterKey.length != MASTER_KEY_LENGTH_BYTES) {
            throw new IllegalStateException("Encryption key must be 32 bytes (64 hex chars)");
        }
        return masterKey;
    }

    private static byte[] getHmacKey() {
        byte[] hmacKey = hexStringToByteArray(Config.HMAC_KEY);
        if (hmacKey.length != MASTER_KEY_LENGTH_BYTES) {
            throw new IllegalStateException("HMAC key must be 32 bytes (64 hex chars)");
        }
        return hmacKey;
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

}
