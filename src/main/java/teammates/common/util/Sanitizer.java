package teammates.common.util;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
		return str.replace("&", "&amp;")
				.replace("#", "&#35;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;")
				.replace("'", "&#39;")
				.replace("\n", "&#010;")
				.replace("\r", "&#013;");
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
}
