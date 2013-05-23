package teammates.common;

/**
 * Class contains methods to sanitize user provided
 * parameters so that they conform to our data format
 * and possible threats can be removed first.
 */
public class Sanitizer {
	
	//TODO: add more info to method header comments. i.e., how is the value santized?
	
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
	 * Sanitizes an email address.
	 * 
	 * @param rawGoogleId
	 * @return the sanitized google ID or null (if the parameter was null).
	 */
	public static String sanitizeEmail(String rawEmail) {
		return trimIfNotNull(rawEmail);
	}	
	
	/**
	 * Sanitizes a Instructor or Student's name.
	 * 
	 * @param string
	 * @return the sanitized string or null (if the parameter was null).
	 */
	public static String sanitizeName(String rawName) {
		return trimIfNotNull(rawName);
	}
	
	/**
	 * Sanitizes a Course or Team's name.
	 * 
	 * @param string
	 * @return the sanitized string or null (if the parameter was null).
	 */
	public static String sanitizeTitle(String rawName) {
		return trimIfNotNull(rawName);
	}
	
	/**
	 * Sanitizes a user input text field.
	 * i.e. comments, instructions, etc.
	 * 
	 * @param string
	 * @return the sanitized string or null (if the parameter was null).
	 */
	public static String sanitizeTextField(String rawText) {
		return trimIfNotNull(rawText);
	}

	/**
	 * Trims the string if it is not null. 
	 * 
	 * @param string
	 * @return the trimmed string or null (if the parameter was null).
	 */
	private static String trimIfNotNull(String string) {
		return ((string == null) ? "" : string.trim());
	}
}
