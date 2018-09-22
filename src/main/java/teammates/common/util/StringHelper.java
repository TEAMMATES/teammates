package teammates.common.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
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

    public static String generateStringOfLength(int length, char character) {
        Assumption.assertTrue(length >= 0);
        return String.join("", Collections.nCopies(length, String.valueOf(character)));
    }

    public static boolean isWhiteSpace(String string) {
        return string.trim().isEmpty();
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
     * Checks whether any substring of the input string matches any of the group of given regex expressions.
     * @param input The string to be matched
     * @param regexList The regex list used for the matching
     */
    public static boolean isAnyMatching(String input, List<String> regexList) {
        return regexList.stream()
                .anyMatch(r -> isMatching(input.trim().toLowerCase(), r));
    }

    public static String getIndent(int length) {
        return generateStringOfLength(length, ' ');
    }

    /**
     * Checks whether the {@code inputString} is longer than a specified length
     * if so returns the truncated name appended by ellipsis,
     * otherwise returns the original input. <br>
     * E.g., "12345678" truncated to length 6 returns "123..."
     */
    public static String truncate(String inputString, int truncateLength) {
        if (inputString.length() <= truncateLength) {
            return inputString;
        }

        return inputString.substring(0, truncateLength - 3) + "...";
    }

    /**
     * Trims head of the String if it is longer than specified Length.
     *  E.g., String "12345678" with maximumStringLength = 6, returns "345678"
     * @param maximumStringLength - maximum required length of the string
     * @return String with at most maximumStringLength length
     */
    public static String truncateHead(String inputString, final int maximumStringLength) {
        final int inputStringLength = inputString.length();
        if (inputStringLength <= maximumStringLength) {
            return inputString;
        }
        return inputString.substring(inputStringLength - maximumStringLength);
    }

    /**
     * Checks whether the {@code longId} is longer than the length specified
     * in {@link Const.SystemParams},
     * if so returns the truncated longId appended by ellipsis,
     * otherwise returns the original longId.
     */
    public static String truncateLongId(String longId) {
        return truncate(longId, Const.SystemParams.USER_ID_MAX_DISPLAY_LENGTH);
    }

    /**
     * Substitutes the middle third of the given string with dots
     * and returns the "obscured" string.
     */
    public static String obscure(String inputString) {
        Assumption.assertNotNull(inputString);
        String frontPart = inputString.substring(0, inputString.length() / 3);
        String endPart = inputString.substring(2 * inputString.length() / 3);
        return frontPart + ".." + endPart;
    }

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

    /*
     * Decrypts the supplied string.
     *
     * @param message the ciphertext as a hexadecimal string
     * @return the plaintext
     * @throws InvalidParameterException if the ciphertext is invalid.
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

    public static String toDecimalFormatString(double doubleVal) {
        DecimalFormat df = new DecimalFormat("0.###");
        return df.format(doubleVal);
    }

    @Deprecated
    public static String toUtcFormat(double hourOffsetTimeZone) {
        String utcFormatTimeZone = "UTC";
        if (hourOffsetTimeZone == 0) {
            return utcFormatTimeZone;
        }

        if ((int) hourOffsetTimeZone == hourOffsetTimeZone) {
            return utcFormatTimeZone + String.format(" %+03d:00", (int) hourOffsetTimeZone);
        }

        return utcFormatTimeZone + String.format(
                                    " %+03d:%02d",
                                    (int) hourOffsetTimeZone,
                                    (int) (Math.abs(hourOffsetTimeZone - (int) hourOffsetTimeZone) * 300 / 5));
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
     * Trims all strings in the set and reduces consecutive white spaces to only one space.
     */
    public static Set<String> removeExtraSpace(Set<String> strSet) {
        if (strSet == null) {
            return null;
        }
        Set<String> result = new TreeSet<>();
        for (String s : strSet) {
            result.add(removeExtraSpace(s));
        }
        return result;
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

    public static String byteArrayToHexString(byte[] bytes) {
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

    public static byte[] hexStringToByteArray(String s) {
        byte[] b = new byte[s.length() / 2];
        IntStream.range(0, b.length)
                .forEach(i -> b[i] = (byte) Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
        return b;
    }

    /**
     * Converts a csv string to a html table string for displaying.
     * @return html table string
     */
    public static String csvToHtmlTable(String str) {
        String[] lines = handleNewLine(str).split(System.lineSeparator());

        StringBuilder result = new StringBuilder();

        for (String line : lines) {

            List<String> rowData = getTableData(line);

            if (checkIfEmptyRow(rowData)) {
                continue;
            }

            result.append("<tr>");
            for (String td : rowData) {
                result.append(String.format("<td>%s</td>", SanitizationHelper.sanitizeForHtml(td)));
            }
            result.append("</tr>");
        }

        return String.format("<table class=\"table table-bordered table-striped table-condensed\">%s</table>",
                             result.toString());
    }

    private static String handleNewLine(String str) {

        StringBuilder buffer = new StringBuilder();
        char[] chars = str.toCharArray();

        boolean isInQuote = false;

        for (char c : chars) {
            if (c == '"') {
                isInQuote = !isInQuote;
            }

            if (c == '\n' && isInQuote) {
                buffer.append("<br>");
            } else {
                buffer.append(c);
            }
        }

        return buffer.toString();
    }

    private static List<String> getTableData(String str) {
        List<String> data = new ArrayList<>();

        boolean inquote = false;
        StringBuilder buffer = new StringBuilder();
        char[] chars = str.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '"') {
                if (i + 1 < chars.length && chars[i + 1] == '"') {
                    i++;
                } else {
                    inquote = !inquote;
                    continue;
                }
            }

            if (chars[i] == ',') {
                if (inquote) {
                    buffer.append(chars[i]);
                } else {
                    data.add(buffer.toString());
                    buffer.delete(0, buffer.length());
                }
            } else {
                buffer.append(chars[i]);
            }

        }

        data.add(buffer.toString().trim());

        return data;
    }

    private static boolean checkIfEmptyRow(List<String> rowData) {
        return rowData.stream()
                .allMatch(r -> r.isEmpty());
    }

    /**
     * From: http://stackoverflow.com/questions/11969840/how-to-convert-a-base-10-number-to-alphabetic-like-ordered-list-in-html
     * Converts an integer to alphabetical form (base26)
     * 1 - a
     * 2 - b
     * ...
     * 26 - z
     * 27 - aa
     * 28 - ab
     * ...
     *
     * @param n - number to convert
     */
    public static String integerToLowerCaseAlphabeticalIndex(int n) {
        StringBuilder result = new StringBuilder();
        int n0 = n;
        while (n0 > 0) {
            n0--; // 1 => a, not 0 => a
            int remainder = n0 % 26;
            char digit = (char) (remainder + 97);
            result.append(digit);
            n0 = (n0 - remainder) / 26;
        }
        return result.reverse().toString();
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
     * Counts the number of empty strings passed as the argument. Null is
     * considered an empty string, while whitespace is not.
     *
     * @return number of empty strings passed
     */
    public static int countEmptyStrings(String... strings) {
        return Math.toIntExact(Arrays.stream(strings)
                .filter(s -> isEmpty(s))
                .count());
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
     * Removes the outermost enclosing square brackets surrounding a string.
     *
     * @return the string without the outermost enclosing square brackets
     *         if the given string is enclosed by square brackets <br>
     *         the string itself if the given string is not enclosed by square brackets <br>
     *         null if the given string is null
     */
    public static String removeEnclosingSquareBrackets(String str) {
        if (str == null) {
            return null;
        }

        if (!str.startsWith("[") || !str.endsWith("]")) {
            return str;
        }

        return str.substring(1, str.length() - 1);
    }

    /**
     * Returns a String array after removing white spaces leading and
     * trailing any string in the input array.
     */
    public static String[] trim(String[] stringsToTrim) {
        return Arrays.stream(stringsToTrim)
                .map(s -> s.trim())
                .toArray(size -> new String[size]);
    }

    /**
     * Returns a String array after converting them to lower case.
     */
    public static String[] toLowerCase(String[] stringsToConvertToLowerCase) {
        return Arrays.stream(stringsToConvertToLowerCase)
                .map(s -> s.toLowerCase())
                .toArray(size -> new String[size]);
    }

    /**
     * Returns text with all non-ASCII characters removed.
     */
    public static String removeNonAscii(String text) {
        return text.replaceAll("[^\\x00-\\x7F]", "");
    }

    /**
     * Returns a new String composed of copies of the String elements joined together
     * with a copy of the specified delimiter.
     */
    public static String join(String delimiter, List<Integer> elements) {
        return String.join(delimiter, toStringArray(elements));
    }

    /**
     * Converts list of integer to array of strings.
     */
    private static String[] toStringArray(List<Integer> elements) {
        if (elements == null) {
            throw new IllegalArgumentException("Provided arguments cannot be null");
        }

        return elements.stream()
                .map(s -> String.valueOf(s))
                .toArray(size -> new String[size]);
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
