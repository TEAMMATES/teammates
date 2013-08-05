package teammates.common.util;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import teammates.common.datatransfer.FeedbackParticipantType;

/**
 * Used to handle the data validation aspect e.g. validate emails, names, etc.
 */
public class FieldValidator {
		
	public enum FieldType {
		COURSE_ID,  
		COURSE_NAME, 
		EMAIL, 
		EVALUATION_INSTRUCTIONS, 
		EVALUATION_NAME, 
		FEEDBACK_SESSION_NAME, 
		/** This can be a Google username e.g. david.lo 
		 * or an email address e.g. david.lo@yahoo.com
		 */
		GOOGLE_ID, 
		INSTITUTE_NAME, 
		PERSON_NAME, 
		/** Comments entered when enrolling a student in a course */
		STUDENT_ROLE_COMMENTS,
		TEAM_NAME,
		START_TIME,
		END_TIME,
		SESSION_VISIBLE_TIME,
		RESULTS_VISIBLE_TIME,
		EVALUATION_TIME_FRAME,
		FEEDBACK_SESSION_TIME_FRAME,
		FEEDBACK_QUESTION_TEXT
	}

	
	private static final String COURSE_NAME_FIELD_NAME = "a course name";
	public static final int COURSE_NAME_MAX_LENGTH = 64;
	public static final String COURSE_NAME_ERROR_MESSAGE = 
			"\"%s\" is not acceptable to TEAMMATES as "+COURSE_NAME_FIELD_NAME+" because it %s. " +
					"The value of "+COURSE_NAME_FIELD_NAME+" should be no longer than "+
					COURSE_NAME_MAX_LENGTH+" characters. It should not be empty.";	
		
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
	
	private static final String EVALUATION_INSTRUCTIONS_FIELD_NAME = "instructions for an evaluation";
	public static final int EVALUATION_INSTRUCTIONS_MAX_LENGTH = 500;
	public static final String EVALUATION_INSTRUCTIONS_ERROR_MESSAGE = 
			"\"%s\" is not acceptable to TEAMMATES as "+EVALUATION_INSTRUCTIONS_FIELD_NAME+" because it %s. " +
					"The value of "+EVALUATION_INSTRUCTIONS_FIELD_NAME+" should be no longer than "+
					EVALUATION_INSTRUCTIONS_MAX_LENGTH+" characters.";	
	
	private static final String FEEDBACK_SESSION_NAME_FIELD_NAME = "a feedback session name";
	public static final int FEEDBACK_SESSION_NAME_MAX_LENGTH = 38;
	public static final String FEEDBACK_SESSION_NAME_ERROR_MESSAGE = 
			"\"%s\" is not acceptable to TEAMMATES as "+FEEDBACK_SESSION_NAME_FIELD_NAME+" because it %s. " +
					"The value of "+FEEDBACK_SESSION_NAME_FIELD_NAME+" should be no longer than "+
					FEEDBACK_SESSION_NAME_MAX_LENGTH+" characters. It should not be empty.";
	
	private static final String FEEDBACK_QUESTION_TEXT_FIELD_NAME = "a feedback question";
	public static final int FEEDBACK_QUESTION_TEXT_MAX_LENGTH = 38;
	public static final String FEEDBACK_QUESTION_TEXT_ERROR_MESSAGE = 
			"\"%s\" is not acceptable to TEAMMATES as "+FEEDBACK_SESSION_NAME_FIELD_NAME+" because it %s. " +
					"The value of "+FEEDBACK_SESSION_NAME_FIELD_NAME+" should be no longer than "+
					FEEDBACK_SESSION_NAME_MAX_LENGTH+" characters. It should not be empty. " +
							"If you require more characters for your question, " +
							"please use the instructions box below.";
	
	private static final String EVALUATION_NAME_FIELD_NAME = "an evaluation name";
	public static final int EVALUATION_NAME_MAX_LENGTH = 38;
	public static final String EVALUATION_NAME_ERROR_MESSAGE = 
			"\"%s\" is not acceptable to TEAMMATES as "+EVALUATION_NAME_FIELD_NAME+" because it %s. " +
					"The value of "+EVALUATION_NAME_FIELD_NAME+" should be no longer than "+
					EVALUATION_NAME_MAX_LENGTH+" characters. It should not be empty.";
		
	public static final int GOOGLE_ID_MAX_LENGTH = 45;
	public static final String GOOGLE_ID_ERROR_MESSAGE = 
			"\"%s\" is not acceptable to TEAMMATES as a Google ID because it %s. "+
			"A Google ID must be a valid id already registered with Google. " +
			"It cannot be longer than "+GOOGLE_ID_MAX_LENGTH+" characters. " +
			"It cannot be empty.";
		
	private static final String INSTITUTE_NAME_FIELD_NAME = "an institute name";
	public static final int INSTITUTE_NAME_MAX_LENGTH = 64;
	public static final String INSTITUTE_NAME_ERROR_MESSAGE = 
			"\"%s\" is not acceptable to TEAMMATES as "+INSTITUTE_NAME_FIELD_NAME+" because it %s. " +
			"The value of "+INSTITUTE_NAME_FIELD_NAME+" should be no longer than "+
			INSTITUTE_NAME_MAX_LENGTH+" characters. It should not be empty.";
	
	private static final String PERSON_NAME_FIELD_NAME = "a person name";
	public static final int PERSON_NAME_MAX_LENGTH = 40;
	public static final String PERSON_NAME_ERROR_MESSAGE = 
			"\"%s\" is not acceptable to TEAMMATES as "+PERSON_NAME_FIELD_NAME+" because it %s. " +
			"The value of "+PERSON_NAME_FIELD_NAME+" should be no longer than "+
			PERSON_NAME_MAX_LENGTH+" characters. It should not be empty.";
	
	private static final String STUDENT_ROLE_COMMENTS_FIELD_NAME = "comments about a student enrolled in a course";
	public static final int STUDENT_ROLE_COMMENTS_MAX_LENGTH = 500;
	public static final String STUDENT_ROLE_COMMENTS_ERROR_MESSAGE = 
			"\"%s\" is not acceptable to TEAMMATES as "+STUDENT_ROLE_COMMENTS_FIELD_NAME+" because it %s. " +
					"The value of "+STUDENT_ROLE_COMMENTS_FIELD_NAME+" should be no longer than "+
					STUDENT_ROLE_COMMENTS_MAX_LENGTH+" characters.";

	public static final String SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE = 
			"\"%s\" is not acceptable to TEAMMATES as %s because it %s. " +
			"The value of %s should be no longer than %d characters. " +
			"It should not be empty.";
	
	public static final String SIZE_CAPPED_POSSIBLY_EMPTY_STRING_ERROR_MESSAGE = 
			"\"%s\" is not acceptable to TEAMMATES as %s because it %s. " +
					"The value of %s should be no longer than %d characters.";
		
	private static final String TEAM_NAME_FIELD_NAME = "a team name";
	public static final int TEAM_NAME_MAX_LENGTH = 25;
	public static final String TEAM_NAME_ERROR_MESSAGE = 
			"\"%s\" is not acceptable to TEAMMATES as "+TEAM_NAME_FIELD_NAME+" because it %s. " +
					"The value of "+TEAM_NAME_FIELD_NAME+" should be no longer than "+
					TEAM_NAME_MAX_LENGTH+" characters.";
	

	public static final String EVALUATION_NAME = "evaluation";
	public static final String FEEDBACK_SESSION_NAME = "feedback session";
	public static final String START_TIME_FIELD_NAME = "start time";
	public static final String END_TIME_FIELD_NAME = "end time";
	public static final String SESSION_VISIBLE_TIME_FIELD_NAME = "time when the session will be visible";
	public static final String RESULTS_VISIBLE_TIME_FIELD_NAME = "time when the results will be visible";
	
	public static final String TIME_FRAME_ERROR_MESSAGE = "The %s for this %s cannot be earlier than the %s.";
	public static final String EVALUATION_START_TIME_ERROR_MESSAGE = "Evaluation cannot be activated before start time";
	public static final String EVALUATION_END_TIME_ERROR_MESSAGE = "Evaluation cannot be published before end time";
	
	public static final String PARTICIPANT_TYPE_ERROR_MESSAGE = "%s is not a valid %s.";
	public static final String GIVER_TYPE_NAME = "feedback giver.";
	public static final String RECIPIENT_TYPE_NAME = "feedback recipient.";
	public static final String VIEWER_TYPE_NAME = "feedback viewer.";
	public static final String PARTICIPANT_TYPE_TEAM_ERROR_MESSAGE = "The feedback recipients cannot be \"%s\" when the feedback giver is \"%s\". Did you mean to use \"Self\" instead?";
	
	//Allows English alphabet, numbers, underscore,  dot, dollar sign and hyphen.
	private static final String REGEX_COURSE_ID = "[a-zA-Z0-9_.$-]+";
	
	//Allows anything with some non-empty text followed by '@' followed by another non-empty text
	// We have made this less restrictive because there is no accepted regex to check the validity of email addresses.
	public static final String REGEX_EMAIL = "[^@\\s]+@[^@\\s]+";

	//Allows English alphabet, numbers, underscore,  dot and hyphen.
	private static final String REGEX_GOOGLE_ID_NON_EMAIL = "[a-zA-Z0-9_.-]+";
	
	//Reasons for not accepting a value. Used for constructing error messages.
	public static final String REASON_EMPTY = "is empty";
	public static final String REASON_TOO_LONG = "is too long";
	public static final String REASON_INCORRECT_FORMAT = "is not in the correct format";
	
	/**
	 * 
	 * @param fieldType
	 *            The field type. e.g., FieldType.E_MAIL
	 * @param value
	 *            The value of the field. e.g., "david@yahoo.com"
	 * @return A string explaining reasons why the value is not acceptable and
	 *         what are the acceptable values. Returns an empty string "" if the
	 *         value is acceptable
	 */
	public String getInvalidityInfo(FieldType fieldType, Object value) {
		return getInvalidityInfo(fieldType, "", value);
	}
	
	/**
	 * Similar to {@link #getInvalidityInfo(FieldType, Object)} except this takes
	 * an extra parameter fieldName
	 * 
	 * @param fieldType
	 * @param fieldName
	 *            A descriptive name of the field. e.g. "Instructor's name".
	 *            This will be used to make the return value more descriptive.
	 * @param value
	 * @return
	 */
	public String getInvalidityInfo(FieldType fieldType, String fieldName, Object value) {
		//TODO: should be break this into individual methods? We already have some methods like that in this class.
		String returnValue = null;
		switch (fieldType) {
		case PERSON_NAME:
			returnValue = getValidityInfoForSizeCappedNonEmptyString(
			PERSON_NAME_FIELD_NAME, PERSON_NAME_MAX_LENGTH, (String)value);
			break;
		case INSTITUTE_NAME:
			returnValue = getValidityInfoForSizeCappedNonEmptyString(
			INSTITUTE_NAME_FIELD_NAME, INSTITUTE_NAME_MAX_LENGTH, (String)value);
			break;
		case COURSE_NAME:
			returnValue = getValidityInfoForSizeCappedNonEmptyString(
			COURSE_NAME_FIELD_NAME, COURSE_NAME_MAX_LENGTH, (String)value);
			break;
		case EVALUATION_NAME:
			returnValue = getValidityInfoForSizeCappedNonEmptyString(
			EVALUATION_NAME_FIELD_NAME, EVALUATION_NAME_MAX_LENGTH, (String)value);
			break;
		case EVALUATION_INSTRUCTIONS:
			returnValue = getValidityInfoForSizeCappedPossiblyEmptyString(
					EVALUATION_INSTRUCTIONS_FIELD_NAME, EVALUATION_INSTRUCTIONS_MAX_LENGTH, (String)value);
			break;
		case FEEDBACK_SESSION_NAME:
			returnValue = getValidityInfoForSizeCappedNonEmptyString(
					FEEDBACK_SESSION_NAME_FIELD_NAME, FEEDBACK_SESSION_NAME_MAX_LENGTH, (String)value);
			break;
		case FEEDBACK_QUESTION_TEXT:
			returnValue = getValidityInfoForSizeCappedNonEmptyString(
					FEEDBACK_QUESTION_TEXT_FIELD_NAME, FEEDBACK_QUESTION_TEXT_MAX_LENGTH, (String)value);
			break;
		case STUDENT_ROLE_COMMENTS:
			returnValue = getValidityInfoForSizeCappedPossiblyEmptyString(
					STUDENT_ROLE_COMMENTS_FIELD_NAME, STUDENT_ROLE_COMMENTS_MAX_LENGTH, (String)value);
			break;
		case TEAM_NAME:
			returnValue = getValidityInfoForSizeCappedPossiblyEmptyString(
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

	/**
	 * Checks if the given string is a non-null non-empty string no longer than
	 * the specified length {@code maxLength}.
	 * 
	 * @param fieldName
	 *            A descriptive name of the field e.g., "student name", to be
	 *            used in the return value to make the explanation more
	 *            descriptive. 
	 * @param maxLength
	 * @param value
	 *            The string to be checked.
	 * @return An explanation of why the {@code value} is not acceptable.
	 *         Returns an empty string "" if the {@code value} is acceptable.
	 */
	public String getValidityInfoForSizeCappedNonEmptyString(String fieldName, int maxLength, String value) {
		
		Assumption.assertTrue("Non-null value expected for "+fieldName, value != null);
		Assumption.assertTrue("\""+value+"\""+  "is expected to be trimmed.", isTrimmed(value));
		
		if (value.isEmpty()) {
			return String.format(SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, value, fieldName, REASON_EMPTY, fieldName, maxLength);
		}else if(value.length()>maxLength){
			return String.format(SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, value, fieldName, REASON_TOO_LONG, fieldName, maxLength);
		} 
		return "";
	}
	
	/**
	 * Checks if the given string is a non-null string no longer than
	 * the specified length {@code maxLength}. However, this string can be empty.
	 * 
	 * @param fieldName
	 *            A descriptive name of the field e.g., "student name", to be
	 *            used in the return value to make the explanation more
	 *            descriptive. 
	 * @param maxLength
	 * @param value
	 *            The string to be checked.
	 * @return An explanation of why the {@code value} is not acceptable.
	 *         Returns an empty string "" if the {@code value} is acceptable.
	 */
	public String getValidityInfoForSizeCappedPossiblyEmptyString(String fieldName, int maxLength, String value) {
		
		Assumption.assertTrue("Non-null value expected for "+fieldName, value != null);
		Assumption.assertTrue("\""+value+"\""+  "is expected to be trimmed.", isTrimmed(value));
		
		if (value.length()>maxLength){
			return String.format(SIZE_CAPPED_POSSIBLY_EMPTY_STRING_ERROR_MESSAGE, value, fieldName, REASON_TOO_LONG, fieldName, maxLength);
		} 
		return "";
	}
	
	public String getValidityInfoForTimeFrame(FieldType mainFieldType, FieldType earlierFieldType,
			FieldType laterFieldType, Date earlierTime, Date laterTime){
		
		Assumption.assertTrue("Non-null value expected", laterFieldType != null);
		Assumption.assertTrue("Non-null value expected", earlierTime != null);
		Assumption.assertTrue("Non-null value expected", laterTime != null);
		
		if(TimeHelper.isSpecialTime(earlierTime) || TimeHelper.isSpecialTime(laterTime)) {
			return "";
		}
		
		String mainFieldName, earlierFieldName, laterFieldName;
		
		switch (mainFieldType) {
		case EVALUATION_TIME_FRAME:
			mainFieldName = EVALUATION_NAME;
			break;
		case FEEDBACK_SESSION_TIME_FRAME:
			mainFieldName = FEEDBACK_SESSION_NAME; 
			break;
		default:
			throw new AssertionError("Unrecognized field type for time frame validity check : " + mainFieldType);
		}
		
		switch (earlierFieldType) {
		case START_TIME:
			earlierFieldName = START_TIME_FIELD_NAME;
			break;
		case END_TIME:
			earlierFieldName = END_TIME_FIELD_NAME; 
			break;
		case SESSION_VISIBLE_TIME:
			earlierFieldName = SESSION_VISIBLE_TIME_FIELD_NAME;
			break;
		case RESULTS_VISIBLE_TIME:
			earlierFieldName = RESULTS_VISIBLE_TIME_FIELD_NAME; 
			break;
		default:
			throw new AssertionError("Unrecognized field type for time frame validity check : " + earlierFieldType);
		}
		
		switch (laterFieldType) {
		case START_TIME:
			laterFieldName = START_TIME_FIELD_NAME;
			break;
		case END_TIME:
			laterFieldName = END_TIME_FIELD_NAME; 
			break;
		case SESSION_VISIBLE_TIME:
			laterFieldName = SESSION_VISIBLE_TIME_FIELD_NAME;
			break;	
		case RESULTS_VISIBLE_TIME:
			laterFieldName = RESULTS_VISIBLE_TIME_FIELD_NAME; 
			break;
		default:
			throw new AssertionError("Unrecognized field type for time frame validity check : " + laterFieldType);
		}
		
		if (laterTime.before(earlierTime)) {
			return String.format(TIME_FRAME_ERROR_MESSAGE, laterFieldName, mainFieldName, earlierFieldName);
		}
		
		return "";
	}

	public String getValidityInfoForEvalStartTime(Date startTime, double timeZone,
			boolean activated) {
		if (isCurrentTimeInUsersTimezoneEarlierThan(startTime, timeZone) && activated) {
			return EVALUATION_START_TIME_ERROR_MESSAGE;
		}
		return "";
	}

	public String getValidityInfoForEvalEndTime(Date endTime, double timeZone,
			boolean published) {
		if (isCurrentTimeInUsersTimezoneEarlierThan(endTime, timeZone) && published) {
			return EVALUATION_END_TIME_ERROR_MESSAGE;
		}
		return "";
	}
	
	public String getValidityInfoForFeedbackParticipantType(
			FeedbackParticipantType giverType, FeedbackParticipantType recipientType) {
		
		Assumption.assertTrue("Non-null value expected", giverType != null);
		Assumption.assertTrue("Non-null value expected", recipientType != null);
		
		String error = "";
		if (giverType.isValidGiver() == false) {
			error += String.format(PARTICIPANT_TYPE_ERROR_MESSAGE, giverType.toString(), GIVER_TYPE_NAME);
		}
		if (recipientType.isValidRecipient() == false) {
			error += String.format(PARTICIPANT_TYPE_ERROR_MESSAGE, recipientType.toString(), RECIPIENT_TYPE_NAME);
		}
		if (giverType == FeedbackParticipantType.TEAMS) {
			if (recipientType == FeedbackParticipantType.OWN_TEAM ||
				recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS) {
				error += String.format(PARTICIPANT_TYPE_TEAM_ERROR_MESSAGE,
						recipientType.toDisplayRecipientName(),
						giverType.toDisplayGiverName());
			}
		}
		
		return error;
	}
	
	public String getValidityInfoForFeedbackResponseVisibility(
			List<FeedbackParticipantType> showResponsesTo, 
			List<FeedbackParticipantType> showGiverNameTo, 
			List<FeedbackParticipantType> showRecipientNameTo) {
		
		Assumption.assertTrue("Non-null value expected", showResponsesTo != null);
		Assumption.assertTrue("Non-null value expected", showGiverNameTo != null);
		Assumption.assertTrue("Non-null value expected", showRecipientNameTo != null);		
		Assumption.assertTrue("Non-null value expected", !showResponsesTo.contains(null));
		Assumption.assertTrue("Non-null value expected", !showGiverNameTo.contains(null));
		Assumption.assertTrue("Non-null value expected", !showRecipientNameTo.contains(null));
		
		String error = "";
		
		for (FeedbackParticipantType type : showGiverNameTo) {
			if (type.isValidViewer() == false) {
				error += String.format(PARTICIPANT_TYPE_ERROR_MESSAGE,
						type.toString(), VIEWER_TYPE_NAME);
			}			
			if (showResponsesTo.contains(type) == false) {
				error += "Trying to show giver name to "
						+ type.toString()
						+ " without showing response first.";
			}
		}
		
		for (FeedbackParticipantType type : showRecipientNameTo) {
			if (type.isValidViewer() == false) {
				error += String.format(PARTICIPANT_TYPE_ERROR_MESSAGE,
						type.toString(), VIEWER_TYPE_NAME);
			}			
			if (showResponsesTo.contains(type) == false) {
				error += "Trying to show recipient name to "
						+ type.toString()
						+ " without showing response first.";
			}
		}
		
		for (FeedbackParticipantType type : showResponsesTo) {
			if (type.isValidViewer() == false) {
				error += String.format(PARTICIPANT_TYPE_ERROR_MESSAGE,
						type.toString(), VIEWER_TYPE_NAME);
			}
		}

		if (showResponsesTo.contains(FeedbackParticipantType.SELF) &&
				!showRecipientNameTo.contains(FeedbackParticipantType.SELF)) {
			error += "Cannot show response to feedback recipient without" +
					" showing recipient's own name to himself.";
		}
		
		return error;
	}
	
	
	private boolean isCurrentTimeInUsersTimezoneEarlierThan(Date time, double timeZone) {
		Date nowInUserTimeZone = TimeHelper.convertToUserTimeZone(
				Calendar.getInstance(TimeZone.getTimeZone("UTC")), timeZone).getTime();
		return nowInUserTimeZone.before(time);
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

	private boolean isValidGoogleUsername(String value) {
		return value.matches(REGEX_GOOGLE_ID_NON_EMAIL);
	}

	private boolean isValidEmail(String value) {
		return value.matches(REGEX_EMAIL);
	}

	private boolean isTrimmed(String value) {
		return value.length() == value.trim().length();
	}

	public boolean isLegitimateRedirectUrl(String redirectUrl) {
		// TODO do better validation
		return redirectUrl.startsWith("/page/");
	}
}
