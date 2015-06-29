package teammates.common.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


/** Holds String-related helper functions
 */
public class StringHelper {

    public static String generateStringOfLength(int length) {
        return StringHelper.generateStringOfLength(length, 'a');
    }

    public static String generateStringOfLength(int length, char character) {
        assert (length >= 0);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(character);
        }
        return sb.toString();
    }

    public static boolean isWhiteSpace(String string) {
        return string.trim().isEmpty();
    }
    
    /**
     * Check whether the input string matches the regex repression
     * @param input The string to be matched
     * @param regex The regex repression used for the matching
     */
    public static boolean isMatching(String input, String regex) {
        // Important to use the CANON_EQ flag to make sure that canonical characters
        // such as é is correctly matched regardless of single/double code point encoding
        return Pattern.compile(regex, Pattern.CANON_EQ).matcher(input).matches();
    }
    
    /**
     * Check whether any substring of the input string matches any of the group of given regex expressions
     * Currently only used in header row processing in StudentAttributesFactory: locateColumnIndexes
     * Case Insensitive
     * @param input The string to be matched
     * @param regexArray The regex repression array used for the matching
     */
    public static boolean isAnyMatching(String input, String[] regexArray) {
        for (String regex : regexArray) {
            if (isMatching(input.trim().toLowerCase(), regex)) {
                return true;
            }
        }   
        return false;
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
        if (!(inputString.length() > truncateLength)) {
            return inputString;
        }
        String result = inputString;
        if (inputString.length() > truncateLength) {
            result = inputString.substring(0, truncateLength - 3) + "...";
        }
        return result;
    }
    
    /**
     * Substitutes the middle third of the given string with dots
     * and returns the "obscured" string
     * 
     * @param inputString
     * @return
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
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, sks, cipher.getParameters());
            byte[] encrypted = cipher.doFinal(value.getBytes());
            return byteArrayToHexString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(String message) {
        try {
            SecretKeySpec sks = new SecretKeySpec(hexStringToByteArray(Config.ENCRYPTION_KEY), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, sks);
            byte[] decrypted = cipher.doFinal(hexStringToByteArray(message));
            return new String(decrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Concatenates a list of strings to a single string, separated by line breaks.
     * @return Concatenated string.
     */
    public static String toString(List<String> strings) {
        return toString(strings, Const.EOL);    
    }

    /**
     * Concatenates a list of strings to a single string, separated by the given delimiter.
     * @return Concatenated string.
     */
    public static String toString(List<String> strings, String delimiter) {
        String returnValue = "";
        
        if (strings.size() == 0) {
            return returnValue;
        }
        
        for (int i = 0; i < strings.size() - 1; i++) {
            String s = strings.get(i);
            returnValue += s + delimiter;
        }
        //append the last item
        returnValue += strings.get(strings.size() - 1);
        
        return returnValue;        
    }
    
    public static String toDecimalFormatString(double doubleVal) {
        DecimalFormat df = new DecimalFormat("0.###");
        return df.format(doubleVal);
    }

    public static String toUtcFormat(double hourOffsetTimeZone) {
        String utcFormatTimeZone = "UTC";
        if (hourOffsetTimeZone != 0) {
            if ((int) hourOffsetTimeZone == hourOffsetTimeZone) {
                utcFormatTimeZone += String.format(" %+03d:00", (int) hourOffsetTimeZone);
            } else {
                utcFormatTimeZone += String.format(
                                            " %+03d:%02d",
                                            (int) hourOffsetTimeZone,
                                            (int) (Math.abs(hourOffsetTimeZone - (int) hourOffsetTimeZone) * 300 / 5));
            }
        }

        return utcFormatTimeZone;
    }
    
    //From: http://stackoverflow.com/questions/5864159/count-words-in-a-string-method
    public static int countWords(String s) {
        int wordCount = 0;
        boolean word = false;
        int endOfLine = s.length() - 1;
        for (int i = 0; i < s.length(); i++) {
            // if the char is a letter, word = true.
            if (Character.isLetter(s.charAt(i)) && i != endOfLine) {
                word = true;
                // if char isn't a letter and there have been letters before,
                // counter goes up.
            } else if (!Character.isLetter(s.charAt(i)) && word) {
                wordCount++;
                word = false;
                // last word of String; if it doesn't end with a non letter, it
                // wouldn't count without this.
            } else if (Character.isLetter(s.charAt(i)) && i == endOfLine) {
                wordCount++;
            }
        }
        return wordCount;
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
            return null;
        }
           
        String lastName;
        String firstName;
        
        if (fullName.contains("{") && fullName.contains("}")) {
            int startIndex = fullName.indexOf("{");
            int endIndex = fullName.indexOf("}");
            lastName = fullName.substring(startIndex + 1, endIndex);
            firstName = fullName.replace("{", "")
                                .replace("}", "")
                                .replace(lastName, "")
                                .trim();           
            
        } else {         
            lastName = fullName.substring(fullName.lastIndexOf(" ") + 1).trim();
            firstName = fullName.replace(lastName, "").trim();
        }
        
        String processedfullName = fullName.replace("{", "")
                                           .replace("}", "");
        
        String[] splitNames = {firstName, lastName, processedfullName};       
        return splitNames;
    }
    
    
    /**
     * trims the string and reduces consecutive white spaces to only one space
     * Example: " a   a  " --> "a a"
     * @return processed string, returns null if parameter is null
     */
    public static String removeExtraSpace(String str) {       
        if (str == null) {
            return null;
        }
        
        return str.trim().replaceAll("\\s+", " ");
        
    }
    
    
    private static String byteArrayToHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            int v = b[i] & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase();
    }

    private static byte[] hexStringToByteArray(String s) {
        byte[] b = new byte[s.length() / 2];
        for (int i = 0; i < b.length; i++) {
            int index = i * 2;
            int v = Integer.parseInt(s.substring(index, index + 2), 16);
            b[i] = (byte) v;
        }
        return b;
    }
    
    
    
    /**
     * This recovers a html-sanitized string to original encoding for appropriate display in files such as csv file <br>
     * It restores encoding for < > \ / ' &  <br>
     * @param sanitized string 
     * @return recovered string  
     */
    public static String recoverFromSanitizedText(String str) {  
        
        if (str == null) {
            return null;
        }
        
        return str.replace("&lt;", "<")
                  .replace("&gt;", ">")
                  .replace("&quot;", "\"")
                  .replace("&#x2f;", "/")
                  .replace("&#39;", "'")
                  .replaceAll("&amp;", "&");
    }
    
    /**
     * This recovers a set of html-sanitized string to original encoding for appropriate display in files such as csv file <br>
     * It restores encoding for < > \ / ' &  <br>
     * @param sanitized string set
     * @return recovered string set
     */
    public static Set<String> recoverFromSanitizedText(Set<String> textSet) {
        Set<String> textSetTemp = new HashSet<String>();
        for (String text : textSet) {
            textSetTemp.add(StringHelper.recoverFromSanitizedText(text));
        }
        return textSetTemp;
    }
    
    /**
     * Convert a csv string to a html table string for displaying
     * @param str
     * @return html table string
     */
    public static String csvToHtmlTable(String str) {
        str = handleNewLine(str);
        String[] lines = str.split(Const.EOL);

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < lines.length; i++) {
            
            List<String> rowData = getTableData(lines[i]);
            
            if (checkIfEmptyRow(rowData)) {
                continue;
            }
            
            result.append("<tr>");
            for (String td : rowData) {
                result.append(String.format("<td>%s</td>\n", td));
            }
            result.append("</tr>");
        }

        return String.format("<table class=\"table table-bordered table-striped table-condensed\">\n%s</table>",
                             result.toString());
    }

    private static String handleNewLine(String str) {

        StringBuilder buffer = new StringBuilder();
        char[] chars = str.toCharArray();

        boolean inquote = false;

        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '"') {
                inquote = !inquote;
            }

            if (chars[i] == '\n' && inquote) {
                buffer.append("<br>");
            } else {
                buffer.append(chars[i]);
            }
        }

        return buffer.toString();
    }

    private static List<String> getTableData(String str) {
        List<String> data = new ArrayList<String>();
        
        boolean inquote = false;
        StringBuilder buffer = new StringBuilder();
        char[] chars = str.toCharArray();
        
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '"') {
                inquote = !inquote;
                continue;
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
           
        for (String td : rowData) {
            if (!td.isEmpty()) {
                return false;
            }
        }
        
        return true;
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
        String result = "";
        while (n > 0) {
            n--; // 1 => a, not 0 => a
            int remainder = n % 26;
            char digit = (char) (remainder + 97);
            result = digit + result;
            n = (n - remainder) / 26;
        }
        return result;
    }
    
    /**
     * Trim the given string if it is not equals to null
     */
    public static String trimIfNotNull(String untrimmedString) {
        if (untrimmedString != null) {
            return untrimmedString.trim();
        }
        return untrimmedString;
    }

    /**
     * Counts the number of empty strings passed as the argument. Null is
     * considered an empty string, while whitespace is not.
     * 
     * @param String...
     * @return number of empty strings passed
     */
    public static int countEmptyStrings(String... strings) {
        int numOfEmptyStrings = 0;
        for (String s : strings) {
            if (s == null || s.isEmpty()) {
                numOfEmptyStrings += 1;
            }
        }
        return numOfEmptyStrings;
    }
    
    /**
     * Converts null input to empty string. Non-null inputs will be left as is.
     * This method is for displaying purpose.
     * 
     * @param String
     * @return empty string if null, the string itself otherwise
     */
    public static String convertToEmptyStringIfNull(String str) {
        return (str == null) ? "" : str;
    }

}
