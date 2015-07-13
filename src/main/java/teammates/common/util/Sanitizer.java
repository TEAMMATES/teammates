package teammates.common.util;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

import com.google.appengine.api.datastore.Text;

/**
 * Class contains methods to sanitize user provided
 * parameters so that they conform to our data format
 * and possible threats can be removed first.
 */
public class Sanitizer {
    
    /**
     * Sanitizes a google ID by removing any whitespaces at the start/end
     * and the trailing "@gmail.com".
     * 
     * @param rawGoogleId
     * @return the sanitized google ID or null (if the parameter was null).
     */
    public static String sanitizeGoogleId(String rawGoogleId) {
        if (rawGoogleId == null) return null;
        
        String sanitized = rawGoogleId.trim();
        // trim @gmail.com in ID field
        if (sanitized.toLowerCase().endsWith("@gmail.com")) {
            sanitized = sanitized.split("@")[0];
        }
        return sanitized.trim();
    }
    
    /**
     * Sanitizes an email address by removing leading/trailing whitespace.
     * 
     * @param rawGoogleId
     * @return the sanitized google ID or null (if the parameter was null).
     */
    public static String sanitizeEmail(String rawEmail) {
        return trimIfNotNull(rawEmail);
    }    
    
    /**
     * Sanitizes a Instructor or Student's name by removing leading/trailing whitespace.
     * 
     * @param string
     * @return the sanitized string or null (if the parameter was null).
     */
    public static String sanitizeName(String rawName) {
        return trimIfNotNull(rawName);
    }
    
    /**
     * Sanitizes a Course or Team's name by removing leading/trailing whitespace.
     * 
     * @param string
     * @return the sanitized string or null (if the parameter was null).
     */
    public static String sanitizeTitle(String rawName) {
        return trimIfNotNull(rawName);
    }
    
    /**
     * Sanitizes a user input text field by removing leading/trailing whitespace.
     * i.e. comments, instructions, etc.
     * 
     * @param string
     * @return the sanitized string or null (if the parameter was null).
     */
    public static String sanitizeTextField(String rawText) {
        return trimIfNotNull(rawText);
    }
    
    /**
     * Sanitizes a user input text field by removing leading/trailing whitespace.
     * i.e. comments, instructions, etc.
     * 
     * @param string
     * @return the sanitized string or null (if the parameter was null).
     */
    public static Text sanitizeTextField(Text rawText) {
        return (rawText==null) ? null :  new Text(trimIfNotNull(rawText.getValue()));
    }
    
    public static String sanitizeHtmlForSaving(String html) {
        return sanitizeForHtml(html);
    }

    /**
     * Escape the string for inserting into javascript code.
     * This automatically calls {@link #escapeHTML} so make it safe for HTML too.
     */
    public static String sanitizeForJs(String str){ 
        return Sanitizer.sanitizeForHtml(
                str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("'", "\\'")
                .replace("#", "\\#"));
    }

    /**
     * Sanitize the string for inserting into HTML. Converts special characters
     * into HTML-safe equivalents.
     */
    public static String sanitizeForHtml(String str){ 
        if(str == null) return null;
        return str.replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("/", "&#x2f;")
                .replace("'", "&#39;")
                //To ensure when apply sanitizeForHtml for multiple times, the string's still fine
                //Regex meaning: replace '&' with safe encoding, but not the one that is safe already
                .replaceAll("&(?!(amp;)|(lt;)|(gt;)|(quot;)|(#x2f;)|(#39;))", "&amp;");
    }
    
    /**
     * Converts a string to be put in URL (replaces some characters)
     */
    public static String sanitizeForUri(String uri) {
        try {
            return URLEncoder.encode(uri, Const.SystemParams.ENCODING);
        } catch (UnsupportedEncodingException wonthappen) {
            return uri;
        }
    }
    
    public static String sanitizeForRichText(String richText) {
        if (richText == null) {
            return null;
        }
        return escapeHtml4(richText);
    }
    
    /**
     * Sanitize the string for searching. 
     */
    public static String sanitizeForSearch(String str){ 
        if(str == null) return null;
        return str
                //general case for punctuation
                .replace("`", " ").replace("!", " ").replace("#", " ").replace("$", " ").replace("%", " ").replace("^", " ")
                .replace("&", " ").replace("[", " ").replace("]", " ").replace("{", " ").replace("}", " ").replace("|", " ")
                .replace(";", " ").replace("*", " ").replace(".", " ").replace("?", " ").replace("'", " ").replace("/", " ")
                //to prevent injection
                .replace("=", " ")
                .replace(":", " ")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
    
    /**
     * Sanitizes the string for comma-separated values (CSV) file output.<br>
     * We follow the definition described by RFC 4180:<br>
     * {@link http://tools.ietf.org/html/rfc4180}
     */
    public static String sanitizeForCsv(String str){ 
        return "\"" + str.replace("\"", "\"\"") + "\"";
    }
    
    /**
     * Sanitizes the list of strings for comma-separated values (CSV) file output.<br>
     * We follow the definition described by RFC 4180:<br>
     * {@link http://tools.ietf.org/html/rfc4180}
     */
    public static List<String> sanitizeListForCsv(List<String> strList){
        List<String> sanitizedStrList = new ArrayList<String>();
        
        Iterator<String> itr = strList.iterator();
        while(itr.hasNext()) {
            sanitizedStrList.add(sanitizeForCsv(itr.next()));
        }
        
        return sanitizedStrList;
    }

    /**
     * Trims the string if it is not null. 
     * 
     * @param string
     * @return the trimmed string or null (if the parameter was null).
     */
    private static String trimIfNotNull(String string) {
        return ((string == null) ? null : string.trim());
    }
    
    /**
     * Convert the string to a safer version for XPath
     * For example:
     * Will o' The Wisp => concat('Will o' , "'" , ' The Wisp' , '')
     * This will result in the same string when read by XPath.
     * 
     * This is used when writing the test case for some special characters
     * such as ' and "
     * 
     * @param text
     * @return safer version of the text for XPath
     */
    public static String convertStringForXPath(String text){
        String result = "";
        int startPos = 0;
        for(int i=0;i<text.length();i++){
            while((i<text.length()) && (text.charAt(i)!='\'')){
                i++;
            }
            if (startPos<i){
                result += "'" + text.substring(startPos, i) + "',";
                startPos = i;
            }
            while((i<text.length()) && (text.charAt(i)=='\'')) i++;
            if (startPos<i){
                result += "\"" + text.substring(startPos, i) + "\",";
                startPos = i;
            }
        }
        if (result.equals("")){
            return "''";
        }
        return "concat(" + result + "'')";
    }
}
