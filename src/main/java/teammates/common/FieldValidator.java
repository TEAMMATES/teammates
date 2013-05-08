package teammates.common;

public class FieldValidator {

	public static final String REASON_EMPTY = "is empty";
	public static final String REASON_TOO_LONG = "is too long";
	public static final String REASON_DISALLOWED_CHAR = "contains a disallowed character";
	
	public static final int PERSON_NAME_MAX_LENGTH = 40;
	public static final String PERSON_NAME_DESCRIPTOR = "A person name can only consist of letters, numbers, spaces, hyphens, apostrophes, fullstops, commas, slashes, round brackets and cannot be longer than "+PERSON_NAME_MAX_LENGTH+" characters. A person name cannot be empty.";
	public static final String PERSON_NAME_ERROR_MESSAGE = "\"%s\" is not acceptable to TEAMMATES as a person name because it %s. "+PERSON_NAME_DESCRIPTOR;


	public enum FieldType {
		PERSON_NAME
	}

	public String getInvalidStateInfo(FieldType fieldType, Object value) {
		switch (fieldType) {
		case PERSON_NAME:
			return getInvalidStateInfo_PERSON_NAME(value);

		}

		throw new AssertionError("Unrecognized field type : " + fieldType);
	}

	public String getValidityDescription(FieldType fieldType) {
		switch (fieldType) {
		case PERSON_NAME:
			return PERSON_NAME_DESCRIPTOR;

		}

		throw new AssertionError("Unrecognized field type : " + fieldType);
	}

	private String getInvalidStateInfo_PERSON_NAME(Object value) {
		Assumption.assertTrue(value != null);
		String name = (String) value;
		if (name.trim().isEmpty()) {
			return String.format(PERSON_NAME_ERROR_MESSAGE, name, REASON_EMPTY);
		} else if(name.trim().length()>PERSON_NAME_MAX_LENGTH){
			return String.format(PERSON_NAME_ERROR_MESSAGE, name, REASON_TOO_LONG);
		} else if(!name.trim().matches("[a-zA-Z0-9-,'/.()\\s]*")){
			return String.format(PERSON_NAME_ERROR_MESSAGE, name, REASON_DISALLOWED_CHAR);
		}
		return "";
	}

}
