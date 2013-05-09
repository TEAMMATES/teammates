package teammates.common;

public class FieldValidator {

	//Allows English alphabet, numbers,underscore,  dot and hyphen.
	private static final String REGEX_GOOGLE_ID_NON_EMAIL = "[a-zA-z0-9_\\.-]+";

	//Allows English alphabet, numbers, hyphen, comma, apostrophe, slash, ampersand, round brackets, white space .
	private static final String REGEX_NAME = "[a-zA-Z0-9-,'/.&()\\s]*";
	
	//Allows anything with some non-empty text followed by '@' followed by another non-empty text
	// We have made this less restrictive because there is no accepted regex to check the validity of email addresses.
	public static final String REGEX_EMAIL = "[^@\\s]+@[^@\\s]+";
	
	public static final String REASON_EMPTY = "is empty";
	public static final String REASON_TOO_LONG = "is too long";
	public static final String REASON_INCORRECT_FORMAT = "is not in the correct format";
	
	public static final int PERSON_NAME_MAX_LENGTH = 40;
	public static final String PERSON_NAME_DESCRIPTOR = "A person name can only consist of letters, numbers, spaces, hyphens, apostrophes, fullstops, commas, slashes, round brackets, and ampersands. It cannot be longer than "+PERSON_NAME_MAX_LENGTH+" characters. It cannot be cannot be empty.";
	public static final String PERSON_NAME_ERROR_MESSAGE = "\"%s\" is not acceptable to TEAMMATES as a person name because it %s. "+PERSON_NAME_DESCRIPTOR;
	
	public static final int INSTITUTE_NAME_MAX_LENGTH = 64;
	public static final String INSTITUTE_NAME_DESCRIPTOR = "An institute name can only consist of letters, numbers, spaces, hyphens, apostrophes, fullstops, commas, slashes, round brackets, and ampersands. It cannot be longer than "+INSTITUTE_NAME_MAX_LENGTH+" characters. It cannot be empty.";
	public static final String INSTITUTE_NAME_ERROR_MESSAGE = "\"%s\" is not acceptable to TEAMMATES as an institute name because it %s. "+INSTITUTE_NAME_DESCRIPTOR;
	
	public static final int GOOGLE_ID_MAX_LENGTH = 45;
	public static final String GOOGLE_ID_DESCRIPTOR = "A Google ID can contain letters, numbers, fullstops, and at most one '@' sign. It cannot be longer than "+GOOGLE_ID_MAX_LENGTH+" characters. It cannot be empty.";
	public static final String GOOGLE_ID_ERROR_MESSAGE = "\"%s\" is not acceptable to TEAMMATES as a Google ID because it %s. "+GOOGLE_ID_DESCRIPTOR;
	
	public static final int EMAIL_MAX_LENGTH = 45;
	public static final String EMAIL_DESCRIPTOR = "An email address contains some text followed by one '@' sign followed by some more text. It cannot be longer than "+EMAIL_MAX_LENGTH+" characters. It cannot be empty and it cannot have spaces.";
	public static final String EMAIL_ERROR_MESSAGE = "\"%s\" is not acceptable to TEAMMATES as an email because it %s. "+EMAIL_DESCRIPTOR;


	public enum FieldType {
		PERSON_NAME, INSTITUTE_NAME, GOOGLE_ID, EMAIL		
	}

	public String getInvalidStateInfo(FieldType fieldType, Object value) {
		switch (fieldType) {
		case PERSON_NAME:
			return getInvalidStateInfo_PERSON_NAME((String)value);
		case INSTITUTE_NAME:
			return getInvalidStateInfo_INSTITUTE_NAME((String)value);
		case GOOGLE_ID:
			return getInvalidStateInfo_GOOGLE_ID((String)value);
		case EMAIL:
			return getInvalidStateInfo_EMAIL((String)value);
		default:
			throw new AssertionError("Unrecognized field type : " + fieldType);
		}

	}

	private String getInvalidStateInfo_GOOGLE_ID(String value) {
		
		Assumption.assertTrue("Non-null value expected", value != null);
		Assumption.assertTrue("\""+value+"\""+  "is expected to be trimmed.", isTrimmed(value));
		Assumption.assertTrue("\""+value+"\""+  "is not expected to be a gmail address.", 
				!value.toLowerCase().endsWith("@gmail.com"));
		
		if (value.isEmpty()) {
			return String.format(GOOGLE_ID_ERROR_MESSAGE, value, REASON_EMPTY);
		}else if(value.length()>GOOGLE_ID_MAX_LENGTH){
			return String.format(GOOGLE_ID_ERROR_MESSAGE, value, REASON_TOO_LONG);
		}else if(!isValidEmail(value) && !isValidGoogleUsername(value)){
			return String.format(GOOGLE_ID_ERROR_MESSAGE, value, REASON_INCORRECT_FORMAT);
		}
		return "";
	}
	
	private String getInvalidStateInfo_EMAIL(String value) {
		
		Assumption.assertTrue("Non-null value expected", value != null);
		Assumption.assertTrue("\""+value+"\""+  "is expected to be trimmed.", isTrimmed(value));
		
		if (value.isEmpty()) {
			return String.format(EMAIL_ERROR_MESSAGE, value, REASON_EMPTY);
		}else if(value.length()>EMAIL_MAX_LENGTH){
			return String.format(EMAIL_ERROR_MESSAGE, value, REASON_TOO_LONG);
		}else if(!isValidEmail(value)){
			return String.format(EMAIL_ERROR_MESSAGE, value, REASON_INCORRECT_FORMAT);
		}
		return "";
	}

	private String getInvalidStateInfo_PERSON_NAME(String value) {
		
		Assumption.assertTrue("Non-null value expected", value != null);
		Assumption.assertTrue("\""+value+"\""+  "is expected to be trimmed.", isTrimmed(value));
		
		if (value.isEmpty()) {
			return String.format(PERSON_NAME_ERROR_MESSAGE, value, REASON_EMPTY);
		} else if(value.length()>PERSON_NAME_MAX_LENGTH){
			return String.format(PERSON_NAME_ERROR_MESSAGE, value, REASON_TOO_LONG);
		} else if(!value.matches(REGEX_NAME)){
			return String.format(PERSON_NAME_ERROR_MESSAGE, value, REASON_INCORRECT_FORMAT);
		}
		return "";
	}

	private String getInvalidStateInfo_INSTITUTE_NAME(String value) {
		
		Assumption.assertTrue("Non-null value expected", value != null);
		Assumption.assertTrue("\""+value+"\""+  "is expected to be trimmed.", isTrimmed(value));
		
		if (value.isEmpty()) {
			return String.format(INSTITUTE_NAME_ERROR_MESSAGE, value, REASON_EMPTY);
		}else if(value.length()>INSTITUTE_NAME_MAX_LENGTH){
			return String.format(INSTITUTE_NAME_ERROR_MESSAGE, value, REASON_TOO_LONG);
		} else if(!value.matches(REGEX_NAME)){
			return String.format(INSTITUTE_NAME_ERROR_MESSAGE, value, REASON_INCORRECT_FORMAT);
		}
		return "";
	}

	private boolean isValidGoogleUsername(String value) {
		return value.matches(REGEX_GOOGLE_ID_NON_EMAIL);
	}

	private boolean isValidEmail(String value) {
		return value.matches(REGEX_EMAIL);
	}

	private boolean isTrimmed(String value) {
		return value.length() == value.trim().length();
	}

}
