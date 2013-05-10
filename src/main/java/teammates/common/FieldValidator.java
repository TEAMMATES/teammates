package teammates.common;

public class FieldValidator {

	//Allows English alphabet, numbers, underscore,  dot and hyphen.
	private static final String REGEX_GOOGLE_ID_NON_EMAIL = "[a-zA-Z0-9_.-]+";
	
	//Allows English alphabet, numbers, underscore,  dot, dollar sign and hyphen.
	private static final String REGEX_COURSE_ID = "[a-zA-Z0-9_.$-]+";

	//Allows anything with some non-empty text followed by '@' followed by another non-empty text
	// We have made this less restrictive because there is no accepted regex to check the validity of email addresses.
	public static final String REGEX_EMAIL = "[^@\\s]+@[^@\\s]+";
	
	public static final String REASON_EMPTY = "is empty";
	public static final String REASON_TOO_LONG = "is too long";
	public static final String REASON_INCORRECT_FORMAT = "is not in the correct format";
	
	public static final String NAME_STRING_ERROR_MESSAGE = 
			"\"%s\" is not acceptable to TEAMMATES as %s because it %s. " +
			"The value of %s should be no longer than %d characters. " +
			"It should not be empty.";
	
	private static final String PERSON_NAME_FIELD_NAME = "a person name";
	public static final int PERSON_NAME_MAX_LENGTH = 40;
	public static final String PERSON_NAME_ERROR_MESSAGE = 
			"\"%s\" is not acceptable to TEAMMATES as "+PERSON_NAME_FIELD_NAME+" because it %s. " +
			"The value of "+PERSON_NAME_FIELD_NAME+" should be no longer than "+
			PERSON_NAME_MAX_LENGTH+" characters. It should not be empty.";
	
	private static final String INSTITUTE_NAME_FIELD_NAME = "an institute name";
	public static final int INSTITUTE_NAME_MAX_LENGTH = 64;
	public static final String INSTITUTE_NAME_ERROR_MESSAGE = 
			"\"%s\" is not acceptable to TEAMMATES as "+INSTITUTE_NAME_FIELD_NAME+" because it %s. " +
			"The value of "+INSTITUTE_NAME_FIELD_NAME+" should be no longer than "+
			INSTITUTE_NAME_MAX_LENGTH+" characters. It should not be empty.";
	
	private static final String COURSE_NAME_FIELD_NAME = "a course name";
	public static final int COURSE_NAME_MAX_LENGTH = 64;
	public static final String COURSE_NAME_ERROR_MESSAGE = 
			"\"%s\" is not acceptable to TEAMMATES as "+COURSE_NAME_FIELD_NAME+" because it %s. " +
					"The value of "+COURSE_NAME_FIELD_NAME+" should be no longer than "+
					COURSE_NAME_MAX_LENGTH+" characters. It should not be empty.";
	
	private static final String EVALUATION_NAME_FIELD_NAME = "an evaluation name";
	public static final int EVALUATION_NAME_MAX_LENGTH = 38;
	public static final String EVALUATION_NAME_ERROR_MESSAGE = 
			"\"%s\" is not acceptable to TEAMMATES as "+EVALUATION_NAME_FIELD_NAME+" because it %s. " +
					"The value of "+EVALUATION_NAME_FIELD_NAME+" should be no longer than "+
					EVALUATION_NAME_MAX_LENGTH+" characters. It should not be empty.";
	
	private static final String EVALUATION_INSTRUCTIONS_FIELD_NAME = "instructions for an evaluation";
	public static final int EVALUATION_INSTRUCTIONS_MAX_LENGTH = 500;
	public static final String EVALUATION_INSTRUCTIONS_ERROR_MESSAGE = 
			"\"%s\" is not acceptable to TEAMMATES as "+EVALUATION_INSTRUCTIONS_FIELD_NAME+" because it %s. " +
					"The value of "+EVALUATION_INSTRUCTIONS_FIELD_NAME+" should be no longer than "+
					EVALUATION_INSTRUCTIONS_MAX_LENGTH+" characters. It should not be empty.";
	
	private static final String STUDENT_ROLE_COMMENTS_FIELD_NAME = "comments about a student enrolled in a course";
	public static final int STUDENT_ROLE_COMMENTS_MAX_LENGTH = 500;
	public static final String STUDENT_ROLE_COMMENTS_ERROR_MESSAGE = 
			"\"%s\" is not acceptable to TEAMMATES as "+STUDENT_ROLE_COMMENTS_FIELD_NAME+" because it %s. " +
					"The value of "+STUDENT_ROLE_COMMENTS_FIELD_NAME+" should be no longer than "+
					STUDENT_ROLE_COMMENTS_MAX_LENGTH+" characters. It should not be empty.";
	
	private static final String TEAM_NAME_FIELD_NAME = "a team name";
	public static final int TEAM_NAME_MAX_LENGTH = 25;
	public static final String TEAM_NAME_ERROR_MESSAGE = 
			"\"%s\" is not acceptable to TEAMMATES as "+TEAM_NAME_FIELD_NAME+" because it %s. " +
					"The value of "+TEAM_NAME_FIELD_NAME+" should be no longer than "+
					TEAM_NAME_MAX_LENGTH+" characters. It should not be empty.";
	
	public static final int GOOGLE_ID_MAX_LENGTH = 45;
	public static final String GOOGLE_ID_ERROR_MESSAGE = 
			"\"%s\" is not acceptable to TEAMMATES as a Google ID because it %s. "+
			"A Google ID must be a valid id already registered with Google. " +
			"It cannot be longer than "+GOOGLE_ID_MAX_LENGTH+" characters. " +
			"It cannot be empty.";
	
	public static final int COURSE_ID_MAX_LENGTH = 40;
	public static final String COURSE_ID_ERROR_MESSAGE = 
			"\"%s\" is not acceptable to TEAMMATES as a Course ID because it %s. "+
					"A Course ID can contain letters, numbers, fullstops, hyphens, underscores, and dollar signs. " +
					"It cannot be longer than "+COURSE_ID_MAX_LENGTH+" characters. " +
					"It cannot be empty or contain spaces.";
	
	public static final int EMAIL_MAX_LENGTH = 45;
	public static final String EMAIL_ERROR_MESSAGE = 
			"\"%s\" is not acceptable to TEAMMATES as an email because it %s. "+
			"An email address contains some text followed by one '@' sign followed by some more text. " +
			"It cannot be longer than "+EMAIL_MAX_LENGTH+" characters. " +
			"It cannot be empty and it cannot have spaces.";


	public enum FieldType {
		PERSON_NAME, INSTITUTE_NAME, GOOGLE_ID, COURSE_ID, EMAIL, COURSE_NAME, 
		EVALUATION_NAME, EVALUATION_INSTRUCTIONS, TEAM_NAME, STUDENT_ROLE_COMMENTS
	}

	public String getValidityInfo(FieldType fieldType, Object value) {
		return getValidityInfo(fieldType, "", value);
	}
	
	public String getValidityInfo(FieldType fieldType, String fieldName, Object value) {
		String returnValue = null;
		switch (fieldType) {
		case PERSON_NAME:
			returnValue = getValidityInfoForSizeCappedString(
			PERSON_NAME_FIELD_NAME, PERSON_NAME_MAX_LENGTH, (String)value);
			break;
		case INSTITUTE_NAME:
			returnValue = getValidityInfoForSizeCappedString(
			INSTITUTE_NAME_FIELD_NAME, INSTITUTE_NAME_MAX_LENGTH, (String)value);
			break;
		case COURSE_NAME:
			returnValue = getValidityInfoForSizeCappedString(
			COURSE_NAME_FIELD_NAME, COURSE_NAME_MAX_LENGTH, (String)value);
			break;
		case EVALUATION_NAME:
			returnValue = getValidityInfoForSizeCappedString(
			EVALUATION_NAME_FIELD_NAME, EVALUATION_NAME_MAX_LENGTH, (String)value);
			break;
		case EVALUATION_INSTRUCTIONS:
			returnValue = getValidityInfoForSizeCappedString(
					EVALUATION_INSTRUCTIONS_FIELD_NAME, EVALUATION_INSTRUCTIONS_MAX_LENGTH, (String)value);
			break;
		case STUDENT_ROLE_COMMENTS:
			returnValue = getValidityInfoForSizeCappedString(
					STUDENT_ROLE_COMMENTS_FIELD_NAME, STUDENT_ROLE_COMMENTS_MAX_LENGTH, (String)value);
			break;
		case TEAM_NAME:
			returnValue = getValidityInfoForSizeCappedString(
			TEAM_NAME_FIELD_NAME, TEAM_NAME_MAX_LENGTH, (String)value);
			break;
		case GOOGLE_ID:
			returnValue = getInvalidStateInfo_GOOGLE_ID((String)value);
			break;
		case COURSE_ID:
			returnValue = getValidityInfo_COURSE_ID((String)value);
			break;
		case EMAIL:
			returnValue = getValidityInfo_EMAIL((String)value);
			break;
		default:
			throw new AssertionError("Unrecognized field type : " + fieldType);
		}
		
		if (!fieldName.isEmpty() && !returnValue.isEmpty()) {
			return "Invalid " + fieldName + ": " + returnValue;
		} else {
			return returnValue;
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
	
	private String getValidityInfo_COURSE_ID(String value) {
		
		Assumption.assertTrue("Non-null value expected", value != null);
		Assumption.assertTrue("\""+value+"\""+  "is expected to be trimmed.", isTrimmed(value));
		
		if (value.isEmpty()) {
			return String.format(COURSE_ID_ERROR_MESSAGE, value, REASON_EMPTY);
		}else if(value.length()>COURSE_ID_MAX_LENGTH){
			return String.format(COURSE_ID_ERROR_MESSAGE, value, REASON_TOO_LONG);
		}else if(!value.matches(REGEX_COURSE_ID)){
			return String.format(COURSE_ID_ERROR_MESSAGE, value, REASON_INCORRECT_FORMAT);
		}
		return "";
	}
	
	private String getValidityInfo_EMAIL(String value) {
		
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

	public String getValidityInfoForSizeCappedString(String fieldName, int maxLength, String value) {
		
		Assumption.assertTrue("Non-null value expected", value != null);
		Assumption.assertTrue("\""+value+"\""+  "is expected to be trimmed.", isTrimmed(value));
		
		if (value.isEmpty()) {
			return String.format(NAME_STRING_ERROR_MESSAGE, value, fieldName, REASON_EMPTY, fieldName, maxLength);
		}else if(value.length()>maxLength){
			return String.format(NAME_STRING_ERROR_MESSAGE, value, fieldName, REASON_TOO_LONG, fieldName, maxLength);
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
