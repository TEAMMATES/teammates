package teammates.common.util;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.util.Const;

/**
 * Used to handle the data validation aspect e.g. validate emails, names, etc.
 */
public class FieldValidator {
        
    public enum FieldType {
        COURSE_ID,  
        COURSE_NAME, 
        NATIONALITY, 
        EMAIL, 
        EVALUATION_INSTRUCTIONS, 
        EVALUATION_NAME, 
        FEEDBACK_SESSION_NAME, 
        GENDER, 
        /** This can be a Google username e.g. david.lo 
         * or an email address e.g. david.lo@yahoo.com
         */
        GOOGLE_ID, 
        INSTITUTE_NAME, 
        PERSON_NAME, 
        INTRUCTOR_ROLE,
        /** Comments entered when enrolling a student in a course */
        STUDENT_ROLE_COMMENTS,
        TEAM_NAME,
        SECTION_NAME,
        START_TIME,
        END_TIME,
        SESSION_VISIBLE_TIME,
        RESULTS_VISIBLE_TIME,
        EVALUATION_TIME_FRAME,
        FEEDBACK_SESSION_TIME_FRAME,
        FEEDBACK_QUESTION_TEXT
    }
    
    // ////////////////////////////////////////////////////////////////////////
    // ////////////////// Generic types ///////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////
    
    /*
     * =======================================================================
     * Field: Contribution
     * Allowed: N/A, Not sure, 0%, E-90% to E+90% in 10% increments
     */
    
    /*
     * =======================================================================
     * Field: Email
     */
    public static final int EMAIL_MAX_LENGTH = 45;
    public static final String EMAIL_ERROR_MESSAGE = 
            "\"%s\" is not acceptable to TEAMMATES as an email because it %s. "+
            "An email address contains some text followed by one '@' sign followed by some more text. " +
            "It cannot be longer than "+EMAIL_MAX_LENGTH+" characters. " +
            "It cannot be empty and it cannot have spaces.";
    
    public static final String EMAIL_TAKEN_MESSAGE = 
            "Trying to update to an email that is already used by: %s/%s";
    
    /*
     * =======================================================================
     * Field: Person name 
     */
    private static final String PERSON_NAME_FIELD_NAME = "a person name";
    public static final int PERSON_NAME_MAX_LENGTH = 100;
    public static final String PERSON_NAME_ERROR_MESSAGE = 
            "\"%s\" is not acceptable to TEAMMATES as "+PERSON_NAME_FIELD_NAME+" because it %s. " +
            "The value of "+PERSON_NAME_FIELD_NAME+" should be no longer than "+
            PERSON_NAME_MAX_LENGTH+" characters. It should not be empty.";
    
    /*
     * =======================================================================
     * Field: Time zone 
     */

    // ////////////////////////////////////////////////////////////////////////
    // ////////////////// Specific types //////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////

    /*
     * ======================================================================= 
     * Field: Nationality
     */
    private static final String NATIONALITY_FIELD_NAME = "nationality";
    // one more than longest official nationality name
    public static final int NATIONALITY_MAX_LENGTH = 55;
    public static final String NATIONALITY_ERROR_MESSAGE = 
            "\"%s\" is not acceptable to TEAMMATES as "+NATIONALITY_FIELD_NAME+" because it %s. " +
                    "The value of "+NATIONALITY_FIELD_NAME+" should be no longer than "+
                    NATIONALITY_MAX_LENGTH+" characters. It should not be empty.";
    
    /*
     * =======================================================================
     * Field: Course name
     */
    private static final String COURSE_NAME_FIELD_NAME = "a course name";
    public static final int COURSE_NAME_MAX_LENGTH = 64;
    public static final String COURSE_NAME_ERROR_MESSAGE = 
            "\"%s\" is not acceptable to TEAMMATES as "+COURSE_NAME_FIELD_NAME+" because it %s. " +
                    "The value of "+COURSE_NAME_FIELD_NAME+" should be no longer than "+
                    COURSE_NAME_MAX_LENGTH+" characters. It should not be empty.";    
    
    /*
     * =======================================================================
     * Field: Course ID
     * Unique: system-wide, not just among the course of that instructor.
     * Technically, we can get rid of CourseID field and enforce users to use
     * CourseName as a unique ID. In that case, we have to enforce CourseName
     * must be unique across the full system. However, users expect names to be
     * non-unique and more tolerant of enforcing uniqueness on an ID. Whenever
     * possible, must be displayed in the same case as user entered. This is
     * because the case of the letters can mean something. Furthermore,
     * converting to same case can reduce readability.
     * 
     * Course ID is necessary because the course name is not unique enough to
     * distinguish between courses because the same course can be offered
     * multiple times and courses can be shared between instructors and many
     * students. Allowing same Course ID among different instructors could be
     * problematic if we allow multiple instructors for a single course.
     * TODO: make case insensitive
     */
    public static final int COURSE_ID_MAX_LENGTH = 40;
    public static final String COURSE_ID_ERROR_MESSAGE = 
            "\"%s\" is not acceptable to TEAMMATES as a Course ID because it %s. "+
                    "A Course ID can contain letters, numbers, fullstops, hyphens, underscores, and dollar signs. " +
                    "It cannot be longer than "+COURSE_ID_MAX_LENGTH+" characters. " +
                    "It cannot be empty or contain spaces.";  
    /*
     * =======================================================================
     * Field instructor permission role
     */
    public static final String INSTRUCTOR_ROLE_ERROR_MESSAGE = 
            "\"%s\" is not accepted to TEAMMATES as a role %s."+
                    "Role can be one of the following: " + 
                    Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER + ", " +
                    Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER + ", " +
                    Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER + ", " +
                    Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_TUTOR + ", " +
                    Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM + ", ";
    public static final String INSTRUCTOR_ROLE_ERROR_REASON_NOT_MATCHING = 
            "it does not match the predifined roles";
    
    /*
     * =======================================================================
     * Field Course Instructor Name
     * Allowed: <= 40
     */
    public static final int COURSE_INSTRUCTORNAME_MAX_LENGTH = 40;
    
    /*
     * =======================================================================
     * Field: Course Student Name
     * Allowed: <= 40
     */
    public static final int COURSE_STUDENTNAME_MAX_LENGTH = 40;
    
    /*
     * =======================================================================
     * Field: Course Team Name
     * Allowed: <= 60
     */
    public static final int COURSE_TEAMNAME_MAX_LENGTH = 60;

    /*
     * =======================================================================
     * Field: Evaluation grace period
     * Allowed: 0,5,10,15,20,30.
     */
    
    /*
     * =======================================================================
     * Field: Evaluation instructions
     */
    private static final String EVALUATION_INSTRUCTIONS_FIELD_NAME = "instructions for an evaluation";
    
    /*
     * =======================================================================
     * Field: Evaluation name
     * Unique: within the course
     * TODO: make case insensitive
     */
    private static final String EVALUATION_NAME_FIELD_NAME = "an evaluation name";
    public static final int EVALUATION_NAME_MAX_LENGTH = 38; //TODO: increase this
    public static final String EVALUATION_NAME_ERROR_MESSAGE = 
            "\"%s\" is not acceptable to TEAMMATES as "+EVALUATION_NAME_FIELD_NAME+" because it %s. " +
                    "The value of "+EVALUATION_NAME_FIELD_NAME+" should be no longer than "+
                    EVALUATION_NAME_MAX_LENGTH+" characters. It should not be empty.";
    /*
     * =======================================================================
     * Field: Evaluation status (Derived field)
     * Allowed: AWAITING, OPEN, CLOSED, PUBLISHED.
     */
    
    /*
     * =======================================================================
     * Field: Evaluation start/end times
     * Start time should be before end time.
     * Only 1 hour increments allowed.
     * TODO: allow smaller increments.
     */
    public static final String START_TIME_FIELD_NAME = "start time";
    public static final String END_TIME_FIELD_NAME = "end time";
    public static final String EVALUATION_START_TIME_ERROR_MESSAGE = "Evaluation cannot be activated before start time";
    public static final String EVALUATION_END_TIME_ERROR_MESSAGE = "Evaluation cannot be published before end time";
    
    /*
     * =======================================================================
     * Field: Feedback session name
     */
    public static final String FEEDBACK_SESSION_NAME = "feedback session";
    public static final String FEEDBACK_SESSION_NAME_FIELD_NAME = "feedback session name";
    public static final int FEEDBACK_SESSION_NAME_MAX_LENGTH = 38;
    public static final String FEEDBACK_SESSION_NAME_ERROR_MESSAGE = 
            "\"%s\" is not acceptable to TEAMMATES as "+FEEDBACK_SESSION_NAME_FIELD_NAME+" because it %s. " +
                    "The value of "+FEEDBACK_SESSION_NAME_FIELD_NAME+" should be no longer than "+
                    FEEDBACK_SESSION_NAME_MAX_LENGTH+" characters. It should not be empty.";
    
    /*
     * =======================================================================
     * Field: Feedback question text
     * TODO: remove if this field is not used
     */
    private static final String FEEDBACK_QUESTION_TEXT_FIELD_NAME = "a feedback question";
    public static final int FEEDBACK_QUESTION_TEXT_MAX_LENGTH = 38;
    public static final String FEEDBACK_QUESTION_TEXT_ERROR_MESSAGE = 
            "\"%s\" is not acceptable to TEAMMATES as "+FEEDBACK_SESSION_NAME_FIELD_NAME+" because it %s. " +
                    "The value of "+FEEDBACK_SESSION_NAME_FIELD_NAME+" should be no longer than "+
                    FEEDBACK_SESSION_NAME_MAX_LENGTH+" characters. It should not be empty. " +
                            "If you require more characters for your question, " +
                            "please use the instructions box below.";
    
    
    /*
     * =======================================================================
     * Field: Google ID
     */    
    public static final int GOOGLE_ID_MAX_LENGTH = 45;
    public static final String GOOGLE_ID_ERROR_MESSAGE = 
            "\"%s\" is not acceptable to TEAMMATES as a Google ID because it %s. "+
            "A Google ID must be a valid id already registered with Google. " +
            "It cannot be longer than "+GOOGLE_ID_MAX_LENGTH+" characters. " +
            "It cannot be empty.";
    
    /*
     * ======================================================================= 
     * Field: Gender
     */
    private static final String GENDER_FIELD_NAME = "gender";
    private static final List<String> GENDER_ACCEPTED_VALUES = Arrays.asList(Const.GenderTypes.MALE, Const.GenderTypes.FEMALE, Const.GenderTypes.OTHER);
    public static final String GENDER_ERROR_MESSAGE = 
            "\"%s\" is not an accepted " + GENDER_FIELD_NAME + " to TEAMMATES. " +
            "Values have to be one of: " + Const.GenderTypes.MALE + ", " +
            Const.GenderTypes.FEMALE + ", " + Const.GenderTypes.OTHER + ".";

    /*
     * =======================================================================
     * Field: Institute name
     */    
    private static final String INSTITUTE_NAME_FIELD_NAME = "an institute name";
    public static final int INSTITUTE_NAME_MAX_LENGTH = 64;
    public static final String INSTITUTE_NAME_ERROR_MESSAGE = 
            "\"%s\" is not acceptable to TEAMMATES as "+INSTITUTE_NAME_FIELD_NAME+" because it %s. " +
            "The value of "+INSTITUTE_NAME_FIELD_NAME+" should be no longer than "+
            INSTITUTE_NAME_MAX_LENGTH+" characters. It should not be empty.";

    /*
     * =======================================================================
     * Field: Student comment
     * Not allowed: |
     */
    private static final String STUDENT_ROLE_COMMENTS_FIELD_NAME = "comments about a student enrolled in a course";
    public static final int STUDENT_ROLE_COMMENTS_MAX_LENGTH = 500;
    public static final String STUDENT_ROLE_COMMENTS_ERROR_MESSAGE = 
            "\"%s\" is not acceptable to TEAMMATES as "+STUDENT_ROLE_COMMENTS_FIELD_NAME+" because it %s. " +
                    "The value of "+STUDENT_ROLE_COMMENTS_FIELD_NAME+" should be no longer than "+
                    STUDENT_ROLE_COMMENTS_MAX_LENGTH+" characters.";
    
    /*
     * =======================================================================
     * Field: Student email [Refer generic field: email]
     * Must be unique within a course.
     */
    
    /*
     * =======================================================================
     * Field: Student name [Refer generic field: person name]
     * May not be unique, even within a course.
     * TODO: make case insensitive
     */
    
    /*
     * =======================================================================
     * Field: Team name
     */
    private static final String TEAM_NAME_FIELD_NAME = "a team name";
    public static final int TEAM_NAME_MAX_LENGTH = 60;
    public static final String TEAM_NAME_ERROR_MESSAGE = 
            "\"%s\" is not acceptable to TEAMMATES as "+TEAM_NAME_FIELD_NAME+" because it %s. " +
                    "The value of "+TEAM_NAME_FIELD_NAME+" should be no longer than "+
                    TEAM_NAME_MAX_LENGTH+" characters. It should not be empty.";
    
    /*
     * =======================================================================
     * Field: Section name
     */
    private static final String SECTION_NAME_FIELD_NAME = "a section name";
    public static final int SECTION_NAME_MAX_LENGTH = 60;
    public static final String SECTION_NAME_ERROR_MESSAGE =
            "\"%s\" is not acceptable to TEAMMATES as "+SECTION_NAME_FIELD_NAME+" because it %s. " +
                    "The value of "+SECTION_NAME_FIELD_NAME+" should be no longer than "+
                    SECTION_NAME_MAX_LENGTH+" characters. It should not be empty.";

    // ////////////////////////////////////////////////////////////////////////
    // ///////////////////End of field type info //////////////////////////////
    // ////////////////////////////////////////////////////////////////////////
    
    public static final String EVALUATION_NAME = "evaluation";
    
    public static final String SESSION_VISIBLE_TIME_FIELD_NAME = "time when the session will be visible";
    public static final String RESULTS_VISIBLE_TIME_FIELD_NAME = "time when the results will be visible";
    
    public static final String TIME_FRAME_ERROR_MESSAGE = "The %s for this %s cannot be earlier than the %s.";
    
    public static final String PARTICIPANT_TYPE_ERROR_MESSAGE = "%s is not a valid %s.";
    public static final String GIVER_TYPE_NAME = "feedback giver.";
    public static final String RECIPIENT_TYPE_NAME = "feedback recipient.";
    public static final String VIEWER_TYPE_NAME = "feedback viewer.";
    public static final String PARTICIPANT_TYPE_TEAM_ERROR_MESSAGE = "The feedback recipients cannot be \"%s\" when the feedback giver is \"%s\". Did you mean to use \"Self\" instead?";
    
    /**
     * Must start with alphanumeric character, cannot contain vertical bar(|) or percent sign(%)
     */
    public static final String REGEX_NAME = "^[\\p{IsL}\\p{IsN}][^|%]*+$";
    
    /**
     * Allows English alphabet, numbers, underscore,  dot, dollar sign and hyphen.
     */
    public static final String REGEX_COURSE_ID = "[a-zA-Z0-9_.$-]+";

    /**
     * A normal course ID followed by the word '-demo' and then followed any amount of digits.
     */
    public static final String REGEX_SAMPLE_COURSE_ID = REGEX_COURSE_ID + "-demo\\d*";
    
    /**
     * Local part: 
     * <li>Can only start with letters, digits, hyphen or plus sign;
     * <li>Special characters allowed are ! # $ % & ' * + - / = ? ^ _ ` { } ~
     * <li>Dot can only appear between any 2 characters and cannot appear continuously<br>
     * Domain part:
     * <li>Only allow letters, digits, hyphen and dot; Must end with letters
     */
    public static final String REGEX_EMAIL = "^[\\w+-][\\w+!#$%&'*/=?^_`{}~-]*+(\\.[\\w+!#$%&'*/=?^_`{}~-]+)*+"
                                            + "@([A-Za-z0-9-]+\\.)*[A-Za-z]+$";

    /**
     * Allows English alphabet, numbers, underscore,  dot and hyphen.
     */
    public static final String REGEX_GOOGLE_ID_NON_EMAIL = "[a-zA-Z0-9_.-]+";
    
    /*
     * =======================================================================
     * Regex used for checking header column name in enroll lines
     */
    public static final String REGEX_COLUMN_SECTION = "sections?";
    public static final String REGEX_COLUMN_TEAM = "teams?";
    public static final String REGEX_COLUMN_NAME = "names?";
    public static final String REGEX_COLUMN_EMAIL = "emails?";
    public static final String REGEX_COLUMN_COMMENT = "comments?";
    /*
     * =======================================================================
     */
    
    //Reasons for not accepting a value. Used for constructing error messages.
    public static final String REASON_EMPTY = "is empty";
    public static final String REASON_TOO_LONG = "is too long";
    public static final String REASON_INCORRECT_FORMAT = "is not in the correct format";
    public static final String REASON_CONTAINS_INVALID_CHAR = "contains invalid characters";
    public static final String REASON_START_WITH_NON_ALPHANUMERIC_CHAR= "starts with a non-alphanumeric character";
    
    //TODO: move these out of this area
    public static final String SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE = 
            "\"%s\" is not acceptable to TEAMMATES as %s because it %s. " +
            "The value of %s should be no longer than %d characters. " +
            "It should not be empty.";
    
    public static final String SIZE_CAPPED_POSSIBLY_EMPTY_STRING_ERROR_MESSAGE = 
            "\"%s\" is not acceptable to TEAMMATES as %s because it %s. " +
                    "The value of %s should be no longer than %d characters.";
    
    public static final String ALPHANUMERIC_STRING_ERROR_MESSAGE = 
            "\"%s\" is not acceptable to TEAMMATES as %s because it is non-alphanumeric. " +
                    "Please only use alphabets, numbers and whitespace in %s.";
    
    public static final String INVALID_NAME_ERROR_MESSAGE = 
            "\"%s\" is not acceptable to TEAMMATES as %s because it %s. " +
            "All %s must start with an alphanumeric character, and cannot contain any vertical bar (|) or percent sign (%%).";
    
    public static final String WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE =
            "The provided %s is not acceptable to TEAMMATES as it contains only whitespace or contains extra spaces at the beginning or at the end of the text.";
    
    public static final String NON_HTML_FIELD_ERROR_MESSAGE = 
            Sanitizer.sanitizeForHtml("The provided %s is not acceptable to TEAMMATES as it cannot contain the following special html characters in brackets: (< > \\ / ' &)");
    
    public static final String NON_NULL_FIELD_ERROR_MESSAGE = 
            "The provided %s is not acceptable to TEAMMATES as it cannot be empty.";
    
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
        String returnValue = "";
        switch (fieldType) {
        case PERSON_NAME:
            returnValue = getValidityInfoForAllowedName(
                    PERSON_NAME_FIELD_NAME, PERSON_NAME_MAX_LENGTH, (String)value);
            break;
        case INSTITUTE_NAME:
            returnValue = getValidityInfoForAllowedName(
                    INSTITUTE_NAME_FIELD_NAME, INSTITUTE_NAME_MAX_LENGTH, (String)value);
            break;
        case NATIONALITY:
            returnValue = getValidityInfoForAllowedName(
                     NATIONALITY_FIELD_NAME, NATIONALITY_MAX_LENGTH, (String)value);
            break;
        case COURSE_NAME:
            returnValue = getValidityInfoForAllowedName(
                    COURSE_NAME_FIELD_NAME, COURSE_NAME_MAX_LENGTH, (String)value);
            break;
        case EVALUATION_NAME:
            returnValue = getValidityInfoForAllowedName(
                    EVALUATION_NAME_FIELD_NAME, EVALUATION_NAME_MAX_LENGTH, (String)value);
            break;
        case EVALUATION_INSTRUCTIONS:
            returnValue = getValidityInfoForNonNullField(
                    EVALUATION_INSTRUCTIONS_FIELD_NAME, value);
            break;
        case FEEDBACK_SESSION_NAME:
            returnValue = getValidityInfoForFeedbackSessionName(value);
            break;
        case FEEDBACK_QUESTION_TEXT:
            returnValue = getValidityInfoForSizeCappedNonEmptyString(
                    FEEDBACK_QUESTION_TEXT_FIELD_NAME, FEEDBACK_QUESTION_TEXT_MAX_LENGTH, (String)value);
            break;
        case GENDER:
            returnValue = getValidityInfoForGender((String)value);
            break;
        case STUDENT_ROLE_COMMENTS:
            returnValue = getValidityInfoForSizeCappedPossiblyEmptyString(
                    STUDENT_ROLE_COMMENTS_FIELD_NAME, STUDENT_ROLE_COMMENTS_MAX_LENGTH, (String)value);
            break;
        case TEAM_NAME:
            returnValue = getValidityInfoForAllowedName(
            TEAM_NAME_FIELD_NAME, TEAM_NAME_MAX_LENGTH, (String)value);
            break;
        case SECTION_NAME:
            returnValue = getValidityInfoForAllowedName(
            SECTION_NAME_FIELD_NAME, SECTION_NAME_MAX_LENGTH, (String)value);
            break;
        case GOOGLE_ID:
            returnValue = getInvalidInfoForGoogleId((String)value);
            break;
        case COURSE_ID:
            returnValue = getValidityInfoForCourseId((String)value);
            break;
        case EMAIL:
            returnValue = getValidityInfoForEmail((String)value);
            break;
        case INTRUCTOR_ROLE:
            returnValue = getValidityInfoForInstructorRole((String)value);
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
    public String getValidityInfoForSizeCappedAlphanumericNonEmptyString(String fieldName, int maxLength, String value) {
        
        Assumption.assertTrue("Non-null value expected for "+fieldName, value != null);
        
        if (value.isEmpty()) {
            return String.format(SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, value, fieldName, REASON_EMPTY, fieldName, maxLength);
        } else if (!isTrimmed(value)) {
            return String.format(WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE, fieldName);
        } else if(value.length()>maxLength){
            return String.format(SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, value, fieldName, REASON_TOO_LONG, fieldName, maxLength);
        } else if (StringHelper.isMatching(value, "^.*[^a-zA-Z0-9 ].*$")){
            return String.format(ALPHANUMERIC_STRING_ERROR_MESSAGE, value, fieldName, fieldName);
        }
        return "";
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
        
        if (value.isEmpty()) {
            return String.format(SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, value, fieldName, REASON_EMPTY, fieldName, maxLength);
        } else if (!isTrimmed(value)) {
            return String.format(WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE, fieldName);
        } else if(value.length()>maxLength){
            return String.format(SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, value, fieldName, REASON_TOO_LONG, fieldName, maxLength);
        } 
        return "";
    }
    
    /**
     * Checks if the given name (including person name, institute name, course name, evaluation/feedback and team name)
     * is a non-null non-empty string no longer than the specified length {@code maxLength}, 
     * and also does not contain any invalid characters (| or %).
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
    public String getValidityInfoForAllowedName(String fieldName, int maxLength, String value) {
        
        Assumption.assertTrue("Non-null value expected for "+fieldName, value != null);
        
        if (value.isEmpty()) {
            return String.format(SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, value, fieldName, REASON_EMPTY, fieldName, maxLength);
        } else if (!isTrimmed(value)) {
            return String.format(WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE, fieldName);
        } else if (value.length()>maxLength) {
            return String.format(SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, value, fieldName, REASON_TOO_LONG, fieldName, maxLength);
        } else if (Character.isLetterOrDigit(value.codePointAt(0)) == false) {           
            boolean startsWithBraces = value.charAt(0) == '{' && value.contains("}");
            if(!startsWithBraces){
                return String.format(INVALID_NAME_ERROR_MESSAGE, value, fieldName, REASON_START_WITH_NON_ALPHANUMERIC_CHAR, fieldName);
            }
            if(!StringHelper.isMatching(value.substring(1), REGEX_NAME)){
                return String.format(INVALID_NAME_ERROR_MESSAGE, value, fieldName, REASON_CONTAINS_INVALID_CHAR, fieldName);
            }
            
        } else if (!StringHelper.isMatching(value, REGEX_NAME)) {
            return String.format(INVALID_NAME_ERROR_MESSAGE, value, fieldName, REASON_CONTAINS_INVALID_CHAR, fieldName);
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
        
        if (!isTrimmed(value)) {
            return String.format(WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE, fieldName);
        } 
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
    
    public List<String> getValidityInfoForFeedbackParticipantType(
            FeedbackParticipantType giverType, FeedbackParticipantType recipientType) {
        
        Assumption.assertNotNull("Non-null value expected", giverType);
        Assumption.assertNotNull("Non-null value expected", recipientType);
        
        List<String> errors = new LinkedList<String>();
        if (giverType.isValidGiver() == false) {
            errors.add(String.format(PARTICIPANT_TYPE_ERROR_MESSAGE, giverType.toString(), GIVER_TYPE_NAME));
        }
        if (recipientType.isValidRecipient() == false) {
            errors.add(String.format(PARTICIPANT_TYPE_ERROR_MESSAGE, recipientType.toString(), RECIPIENT_TYPE_NAME));
        }
        if (giverType == FeedbackParticipantType.TEAMS) {
            if (recipientType == FeedbackParticipantType.OWN_TEAM ||
                recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS) {
                errors.add(String.format(PARTICIPANT_TYPE_TEAM_ERROR_MESSAGE,
                        recipientType.toDisplayRecipientName(),
                        giverType.toDisplayGiverName()));
            }
        }
        
        return errors;
    }
    
    public List<String> getValidityInfoForFeedbackResponseVisibility(
            List<FeedbackParticipantType> showResponsesTo, 
            List<FeedbackParticipantType> showGiverNameTo, 
            List<FeedbackParticipantType> showRecipientNameTo) {
        
        Assumption.assertNotNull("Non-null value expected", showResponsesTo);
        Assumption.assertNotNull("Non-null value expected", showGiverNameTo);
        Assumption.assertNotNull("Non-null value expected", showRecipientNameTo);        
        Assumption.assertTrue("Non-null value expected", !showResponsesTo.contains(null));
        Assumption.assertTrue("Non-null value expected", !showGiverNameTo.contains(null));
        Assumption.assertTrue("Non-null value expected", !showRecipientNameTo.contains(null));
        
        List<String> errors = new LinkedList<String>();
        
        for (FeedbackParticipantType type : showGiverNameTo) {
            if (type.isValidViewer() == false) {
                errors.add(String.format(PARTICIPANT_TYPE_ERROR_MESSAGE,
                        type.toString(), VIEWER_TYPE_NAME));
            }            
            if (showResponsesTo.contains(type) == false) {
                errors.add("Trying to show giver name to "
                        + type.toString()
                        + " without showing response first.");
            }
        }
        
        for (FeedbackParticipantType type : showRecipientNameTo) {
            if (type.isValidViewer() == false) {
                errors.add(String.format(PARTICIPANT_TYPE_ERROR_MESSAGE,
                        type.toString(), VIEWER_TYPE_NAME));
            }            
            if (showResponsesTo.contains(type) == false) {
                errors.add("Trying to show recipient name to "
                        + type.toString()
                        + " without showing response first.");
            }
        }
        
        for (FeedbackParticipantType type : showResponsesTo) {
            if (type.isValidViewer() == false) {
                errors.add(String.format(PARTICIPANT_TYPE_ERROR_MESSAGE,
                        type.toString(), VIEWER_TYPE_NAME));
            }
        }

        return errors;
    }

    public String getValidityInfoForFeedbackSessionName(Object value) {
        String returnValue;
        returnValue = getValidityInfoForAllowedName(
                FEEDBACK_SESSION_NAME_FIELD_NAME, FEEDBACK_SESSION_NAME_MAX_LENGTH, (String)value);
        if(returnValue.equals("")){
            returnValue = getValidityInfoForNonHtmlField(
                    FEEDBACK_SESSION_NAME_FIELD_NAME, (String) value);
        }
        return returnValue;
    }
    
    public String getValidityInfoForNonHtmlField(String fieldName, String value) {
        String sanitizedValue = value;
        sanitizedValue = sanitizedValue.replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("/", "&#x2f;")
            .replace("'", "&#39;")
            //To ensure when apply sanitizeForHtml for multiple times, the string's still fine
            //Regex meaning: replace '&' with safe encoding, but not the one that is safe already
            .replaceAll("&(?!(amp;)|(lt;)|(gt;)|(quot;)|(#x2f;)|(#39;))", "&amp;");
        //Fails if sanitized value is not same as value
        return (value.equals(sanitizedValue)) ? "" : String.format(NON_HTML_FIELD_ERROR_MESSAGE, fieldName);
    }
    
    public String getValidityInfoForNonNullField(String fieldName, Object value) {
        return (value == null) ? String.format(NON_NULL_FIELD_ERROR_MESSAGE, fieldName) : "";
    }
    
    private boolean isCurrentTimeInUsersTimezoneEarlierThan(Date time, double timeZone) {
        Date nowInUserTimeZone = TimeHelper.convertToUserTimeZone(
                Calendar.getInstance(TimeZone.getTimeZone("UTC")), timeZone).getTime();
        return nowInUserTimeZone.before(time);
    }

    private String getInvalidInfoForGoogleId(String value) {
        
        Assumption.assertTrue("Non-null value expected", value != null);
        Assumption.assertTrue("\""+value+"\""+  "is not expected to be a gmail address.", 
                !value.toLowerCase().endsWith("@gmail.com"));
        
        if (value.isEmpty()) {
            return String.format(GOOGLE_ID_ERROR_MESSAGE, value, REASON_EMPTY);
        } else if (!isTrimmed(value)) {
            return String.format(WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE, "googleID");
        } else if(value.length()>GOOGLE_ID_MAX_LENGTH){
            return String.format(GOOGLE_ID_ERROR_MESSAGE, value, REASON_TOO_LONG);
        } else if(!StringHelper.isMatching(value, REGEX_EMAIL) && !StringHelper.isMatching(value, REGEX_GOOGLE_ID_NON_EMAIL)){
            return String.format(GOOGLE_ID_ERROR_MESSAGE, value, REASON_INCORRECT_FORMAT);
        }
        return "";
    }
    
    private String getValidityInfoForCourseId(String value) {
        
        Assumption.assertTrue("Non-null value expected", value != null);
        
        if (value.isEmpty()) {
            return String.format(COURSE_ID_ERROR_MESSAGE, value, REASON_EMPTY);
        } else if (!isTrimmed(value)) {
            return String.format(WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE, "course ID");
        } else if(value.length()>COURSE_ID_MAX_LENGTH){
            return String.format(COURSE_ID_ERROR_MESSAGE, value, REASON_TOO_LONG);
        }else if(!StringHelper.isMatching(value, REGEX_COURSE_ID)){
            return String.format(COURSE_ID_ERROR_MESSAGE, value, REASON_INCORRECT_FORMAT);
        }
        return "";
    }
    
    private String getValidityInfoForEmail(String value) {
        
        Assumption.assertTrue("Non-null value expected", value != null);
        
        if (value.isEmpty()) {
            return String.format(EMAIL_ERROR_MESSAGE, value, REASON_EMPTY);
        } else if (!isTrimmed(value)) {
            return String.format(WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE, "email");
        } else if(value.length()>EMAIL_MAX_LENGTH){
            return String.format(EMAIL_ERROR_MESSAGE, value, REASON_TOO_LONG);
        }else if(!StringHelper.isMatching(value, REGEX_EMAIL)){
            return String.format(EMAIL_ERROR_MESSAGE, value, REASON_INCORRECT_FORMAT);
        }
        return "";
    }
    
    private String getValidityInfoForGender(String value) {
        Assumption.assertTrue("Non-null value expected", value != null);
        
        if (!GENDER_ACCEPTED_VALUES.contains(value)) {
            return String.format(GENDER_ERROR_MESSAGE, value);
        }
        return "";
    }
    
    private String getValidityInfoForInstructorRole(String value) {
        
        Assumption.assertTrue("Non-null value expected", value != null);
        
        if (value.isEmpty()) {
            return String.format(INSTRUCTOR_ROLE_ERROR_MESSAGE, value, REASON_EMPTY);
        }
        if (!(value.equals(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER)
                || value.equals(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER)
                || value.equals(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER)
                || value.equals(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_TUTOR)
                || value.equals(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM))) {
            return String.format(INSTRUCTOR_ROLE_ERROR_MESSAGE, value, INSTRUCTOR_ROLE_ERROR_REASON_NOT_MATCHING);
        }
        
        return "";
    }

    private boolean isTrimmed(String value) {
        return value.length() == value.trim().length();
    }

    public boolean isLegitimateRedirectUrl(String redirectUrl) {
        // TODO do better validation
        return redirectUrl.startsWith("/page/");
    }
}
