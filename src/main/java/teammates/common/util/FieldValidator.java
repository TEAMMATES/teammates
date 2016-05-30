package teammates.common.util;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import teammates.common.datatransfer.FeedbackParticipantType;

import com.google.appengine.api.datastore.Text;

/**
 * Used to handle the data validation aspect e.g. validate emails, names, etc.
 */
public class FieldValidator {
        
    public enum FieldType {
        COURSE_ID,
        INTRUCTOR_ROLE,
        START_TIME,
        END_TIME,
        SESSION_VISIBLE_TIME,
        RESULTS_VISIBLE_TIME,
        FEEDBACK_SESSION_TIME_FRAME,
        EMAIL_SUBJECT,
        EMAIL_CONTENT
    }
    
    // ////////////////////////////////////////////////////////////////////////
    // ////////////////// Generic types ///////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////
    
    /*
     * =======================================================================
     * Field: Email
     */
    public static final String EMAIL_FIELD_NAME = "email";
    public static final int EMAIL_MAX_LENGTH = 254;
    public static final String EMAIL_ERROR_MESSAGE =
            "\"%s\" is not acceptable to TEAMMATES as an email because it %s. "
            + "An email address contains some text followed by one '@' sign followed by some more text. "
            + "It cannot be longer than " + EMAIL_MAX_LENGTH + " characters. "
            + "It cannot be empty and it cannot have spaces.";
    
    public static final String EMAIL_TAKEN_MESSAGE =
            "Trying to update to an email that is already used by: %s/%s";
    
    /*
     * =======================================================================
     * Field: Person name
     */
    public static final String PERSON_NAME_FIELD_NAME = "a person name";
    public static final int PERSON_NAME_MAX_LENGTH = 100;
    public static final String PERSON_NAME_ERROR_MESSAGE =
            "\"%s\" is not acceptable to TEAMMATES as " + PERSON_NAME_FIELD_NAME + " because it %s. "
            + "The value of " + PERSON_NAME_FIELD_NAME + " should be no longer than "
            + PERSON_NAME_MAX_LENGTH + " characters. It should not be empty.";

    // ////////////////////////////////////////////////////////////////////////
    // ////////////////// Specific types //////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////

    /*
     * =======================================================================
     * Field: Email Subject
     */
    public static final String EMAIL_SUBJECT_FIELD_NAME = "email subject";
    public static final int EMAIL_SUBJECT_MAX_LENGTH = 200;
    public static final String EMAIL_SUBJECT_ERROR_MESSAGE =
            "\"%s\" is not acceptable to TEAMMATES as " + EMAIL_SUBJECT_FIELD_NAME + " because it %s. "
            + "The value of " + EMAIL_SUBJECT_FIELD_NAME + " should be no longer than "
            + EMAIL_SUBJECT_MAX_LENGTH + " characters. It should not be empty.";
    
    /*
     * =======================================================================
     * Field: Email Content
     */
    public static final String EMAIL_CONTENT_FIELD_NAME = "email content";
    public static final String EMAIL_CONTENT_ERROR_MESSAGE = EMAIL_CONTENT_FIELD_NAME + " should not be empty.";

    /*
     * =======================================================================
     * Field: Nationality
     */
    public static final String NATIONALITY_FIELD_NAME = "nationality";
    // one more than longest official nationality name
    public static final int NATIONALITY_MAX_LENGTH = 55;
    
    /*
     * =======================================================================
     * Field: Course name
     */
    public static final String COURSE_NAME_FIELD_NAME = "a course name";
    public static final int COURSE_NAME_MAX_LENGTH = 64;
    public static final String COURSE_NAME_ERROR_MESSAGE =
            "\"%s\" is not acceptable to TEAMMATES as " + COURSE_NAME_FIELD_NAME + " because it %s. "
            + "The value of " + COURSE_NAME_FIELD_NAME + " should be no longer than "
            + COURSE_NAME_MAX_LENGTH + " characters. It should not be empty.";
    
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
            "\"%s\" is not acceptable to TEAMMATES as a Course ID because it %s. "
            + "A Course ID can contain letters, numbers, fullstops, hyphens, underscores, and dollar signs. "
            + "It cannot be longer than " + COURSE_ID_MAX_LENGTH + " characters. "
            + "It cannot be empty or contain spaces.";
    /*
     * =======================================================================
     * Field instructor permission role
     */
    public static final String INSTRUCTOR_ROLE_ERROR_MESSAGE =
            "\"%s\" is not accepted to TEAMMATES as a role %s."
            + "Role can be one of the following: "
            + Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER + ", "
            + Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER + ", "
            + Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER + ", "
            + Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_TUTOR + ", "
            + Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM + ", ";
    public static final String INSTRUCTOR_ROLE_ERROR_REASON_NOT_MATCHING =
            "it does not match the predifined roles";

    /*
     * =======================================================================
     * Field: Feedback session start/end times
     * Start time should be before end time.
     * Only 1 hour increments allowed.
     * TODO: allow smaller increments.
     */
    public static final String START_TIME_FIELD_NAME = "start time";
    public static final String END_TIME_FIELD_NAME = "end time";
    
    /*
     * =======================================================================
     * Field: Feedback session name
     */
    public static final String FEEDBACK_SESSION_NAME = "feedback session";
    public static final String FEEDBACK_SESSION_NAME_FIELD_NAME = "feedback session name";
    public static final int FEEDBACK_SESSION_NAME_MAX_LENGTH = 38;
    public static final String FEEDBACK_SESSION_NAME_ERROR_MESSAGE =
            "\"%s\" is not acceptable to TEAMMATES as " + FEEDBACK_SESSION_NAME_FIELD_NAME + " because it %s. "
            + "The value of " + FEEDBACK_SESSION_NAME_FIELD_NAME + " should be no longer than "
            + FEEDBACK_SESSION_NAME_MAX_LENGTH + " characters. It should not be empty.";
    
    /*
     * =======================================================================
     * Field: Google ID
     */
    public static final String GOOGLE_ID_FIELD_NAME = "Google ID";
    public static final int GOOGLE_ID_MAX_LENGTH = 254;
    public static final String GOOGLE_ID_ERROR_MESSAGE =
            "\"%s\" is not acceptable to TEAMMATES as a Google ID because it %s. "
            + "A Google ID must be a valid id already registered with Google. "
            + "It cannot be longer than " + GOOGLE_ID_MAX_LENGTH + " characters. "
            + "It cannot be empty.";
    
    /*
     * =======================================================================
     * Field: Gender
     */
    public static final String GENDER_FIELD_NAME = "gender";
    public static final List<String> GENDER_ACCEPTED_VALUES = Arrays.asList(Const.GenderTypes.MALE, Const.GenderTypes.FEMALE, Const.GenderTypes.OTHER);
    public static final String GENDER_ERROR_MESSAGE =
            "\"%s\" is not an accepted " + GENDER_FIELD_NAME + " to TEAMMATES. "
            + "Values have to be one of: " + Const.GenderTypes.MALE + ", "
            + Const.GenderTypes.FEMALE + ", " + Const.GenderTypes.OTHER + ".";

    /*
     * =======================================================================
     * Field: Institute name
     */
    public static final String INSTITUTE_NAME_FIELD_NAME = "an institute name";
    public static final int INSTITUTE_NAME_MAX_LENGTH = 64;
    public static final String INSTITUTE_NAME_ERROR_MESSAGE =
            "\"%s\" is not acceptable to TEAMMATES as " + INSTITUTE_NAME_FIELD_NAME + " because it %s. "
            + "The value of " + INSTITUTE_NAME_FIELD_NAME + " should be no longer than "
            + INSTITUTE_NAME_MAX_LENGTH + " characters. It should not be empty.";

    /*
     * =======================================================================
     * Field: Student comment
     * Not allowed: |
     */
    public static final String STUDENT_ROLE_COMMENTS_FIELD_NAME = "comments about a student enrolled in a course";
    public static final int STUDENT_ROLE_COMMENTS_MAX_LENGTH = 500;
    public static final String STUDENT_ROLE_COMMENTS_ERROR_MESSAGE =
            "\"%s\" is not acceptable to TEAMMATES as " + STUDENT_ROLE_COMMENTS_FIELD_NAME + " because it %s. "
            + "The value of " + STUDENT_ROLE_COMMENTS_FIELD_NAME + " should be no longer than "
            + STUDENT_ROLE_COMMENTS_MAX_LENGTH + " characters.";
    
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
    public static final String TEAM_NAME_FIELD_NAME = "a team name";
    public static final int TEAM_NAME_MAX_LENGTH = 60;
    public static final String TEAM_NAME_ERROR_MESSAGE =
            "\"%s\" is not acceptable to TEAMMATES as " + TEAM_NAME_FIELD_NAME + " because it %s. "
            + "The value of " + TEAM_NAME_FIELD_NAME + " should be no longer than "
            + TEAM_NAME_MAX_LENGTH + " characters. It should not be empty.";
    
    /*
     * =======================================================================
     * Field: Section name
     */
    public static final String SECTION_NAME_FIELD_NAME = "a section name";
    public static final int SECTION_NAME_MAX_LENGTH = 60;
    public static final String SECTION_NAME_ERROR_MESSAGE =
            "\"%s\" is not acceptable to TEAMMATES as " + SECTION_NAME_FIELD_NAME + " because it %s. "
            + "The value of " + SECTION_NAME_FIELD_NAME + " should be no longer than "
            + SECTION_NAME_MAX_LENGTH + " characters. It should not be empty.";

    // ////////////////////////////////////////////////////////////////////////
    // ///////////////////End of field type info //////////////////////////////
    // ////////////////////////////////////////////////////////////////////////
    
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
    public static final String[] REGEX_COLUMN_SECTION = {"sections?", "sect?", "courses?\\s+sec(tion)?s?"};
    public static final String[] REGEX_COLUMN_TEAM = {"teams?", "groups?", "students?\\s+teams?", "students?\\s+groups?", "courses?\\s+teams?"};
    public static final String[] REGEX_COLUMN_NAME = {"names?", "students?\\s+names?", "full\\s+names?", "students?\\s+full\\s+names?"};
    public static final String[] REGEX_COLUMN_EMAIL = {"emails?", "mails?", "e-mails?", "e\\s+mails?", "emails?\\s+address(es)?", "e-mails?\\s+address(es)?", "contacts?"};
    public static final String[] REGEX_COLUMN_COMMENT = {"comments?", "notes?"};
    /*
     * =======================================================================
     */
    
    //Reasons for not accepting a value. Used for constructing error messages.
    public static final String REASON_EMPTY = "is empty";
    public static final String REASON_TOO_LONG = "is too long";
    public static final String REASON_INCORRECT_FORMAT = "is not in the correct format";
    public static final String REASON_CONTAINS_INVALID_CHAR = "contains invalid characters";
    public static final String REASON_START_WITH_NON_ALPHANUMERIC_CHAR = "starts with a non-alphanumeric character";
    
    //TODO: move these out of this area
    public static final String SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE =
            "\"%s\" is not acceptable to TEAMMATES as %s because it %s. "
            + "The value of %s should be no longer than %d characters. "
            + "It should not be empty.";
    
    public static final String SIZE_CAPPED_POSSIBLY_EMPTY_STRING_ERROR_MESSAGE =
            "\"%s\" is not acceptable to TEAMMATES as %s because it %s. "
            + "The value of %s should be no longer than %d characters.";
    
    public static final String ALPHANUMERIC_STRING_ERROR_MESSAGE =
            "\"%s\" is not acceptable to TEAMMATES as %s because it is non-alphanumeric. "
            + "Please only use alphabets, numbers and whitespace in %s.";
    
    public static final String INVALID_NAME_ERROR_MESSAGE =
            "\"%s\" is not acceptable to TEAMMATES as %s because it %s. "
            + "All %s must start with an alphanumeric character, and cannot contain any vertical bar (|) or percent sign (%%).";
    
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
        case COURSE_ID:
            returnValue = getValidityInfoForCourseId((String) value);
            break;
        case INTRUCTOR_ROLE:
            returnValue = getValidityInfoForInstructorRole((String) value);
            break;
        case EMAIL_SUBJECT:
            returnValue = this.getValidityInfoForAllowedName(EMAIL_SUBJECT_FIELD_NAME, EMAIL_SUBJECT_MAX_LENGTH, (String) value);
            break;
        case EMAIL_CONTENT:
            returnValue = this.getValidityInfoForEmailContent((Text) value);
            break;
        default:
            throw new AssertionError("Unrecognized field type : " + fieldType);
        }
        
        if (fieldName.isEmpty() || returnValue.isEmpty()) {
            return returnValue;
        }
        return "Invalid " + fieldName + ": " + returnValue;
    }

    /**
     * Checks if {@code email} is not null, not empty, not longer than {@code EMAIL_MAX_LENGTH}, and is a
     * valid email address according to {@code REGEX_EMAIL}
     * @param email
     * @return An explanation of why the {@code email} is not acceptable.
     *         Returns an empty string if the {@code email} is acceptable.
     */
    public String getInvalidityInfoForEmail(String email) {

        Assumption.assertTrue("Non-null value expected", email != null);
        String sanitizedValue = Sanitizer.sanitizeForHtml(email);

        if (email.isEmpty()) {
            return String.format(EMAIL_ERROR_MESSAGE, email, REASON_EMPTY);
        } else if (isUntrimmed(email)) {
            return String.format(WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE, EMAIL_FIELD_NAME);
        } else if (email.length() > EMAIL_MAX_LENGTH) {
            return String.format(EMAIL_ERROR_MESSAGE, sanitizedValue, REASON_TOO_LONG);
        } else if (!StringHelper.isMatching(email, REGEX_EMAIL)) {
            return String.format(EMAIL_ERROR_MESSAGE, sanitizedValue, REASON_INCORRECT_FORMAT);
        }
        return "";
    }

    /**
     * Checks if {@code googleId} is not null, not empty, not longer than {@code GOOGLE_ID_MAX_LENGTH}, does
     * not contain any invalid characters (| or %), AND is either a Google username (without the "@gmail.com")
     * or a valid email address that does not end in "@gmail.com"
     * @param googleId
     * @return An explanation of why the {@code googleId} is not acceptable.
     *         Returns an empty string if the {@code googleId} is acceptable.
     */
    public String getInvalidityInfoForGoogleId(String googleId) {

        Assumption.assertTrue("Non-null value expected", googleId != null);
        Assumption.assertTrue("\"" + googleId + "\"" + "is not expected to be a gmail address.",
                !googleId.toLowerCase().endsWith("@gmail.com"));
        String sanitizedValue = Sanitizer.sanitizeForHtml(googleId);

        boolean isValidFullEmail = StringHelper.isMatching(googleId, REGEX_EMAIL);
        boolean isValidEmailWithoutDomain = StringHelper.isMatching(googleId, REGEX_GOOGLE_ID_NON_EMAIL);

        if (googleId.isEmpty()) {
            return String.format(GOOGLE_ID_ERROR_MESSAGE, googleId, REASON_EMPTY);
        } else if (isUntrimmed(googleId)) {
            return String.format(WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE, GOOGLE_ID_FIELD_NAME);
        } else if (googleId.length() > GOOGLE_ID_MAX_LENGTH) {
            return String.format(GOOGLE_ID_ERROR_MESSAGE, sanitizedValue, REASON_TOO_LONG);
        } else if (!(isValidFullEmail || isValidEmailWithoutDomain)) {
            return String.format(GOOGLE_ID_ERROR_MESSAGE, sanitizedValue, REASON_INCORRECT_FORMAT);
        }
        return "";
    }
    
    /**
     * Checks if {@code sectionName} is a non-null non-empty string no longer than the specified length
     * {@code SECTION_NAME_MAX_LENGTH}, and also does not contain any invalid characters (| or %).
     * @param sectionName
     * @return An explanation of why the {@code sectionName} is not acceptable.
     *         Returns an empty string if the {@code sectionName} is acceptable.
     */
    public String getInvalidityInfoForSectionName(String sectionName) {
        return getValidityInfoForAllowedName(SECTION_NAME_FIELD_NAME, SECTION_NAME_MAX_LENGTH, sectionName);
    }
    
    /**
     * Checks if {@code teamName} is a non-null non-empty string no longer than the specified length
     * {@code TEAM_NAME_MAX_LENGTH}, and also does not contain any invalid characters (| or %).
     * @param teamName
     * @return An explanation of why the {@code teamName} is not acceptable.
     *         Returns an empty string if the {@code teamName} is acceptable.
     */
    public String getInvalidityInfoForTeamName(String teamName) {
        return getValidityInfoForAllowedName(TEAM_NAME_FIELD_NAME, TEAM_NAME_MAX_LENGTH, teamName);
    }
    
    /**
     * Checks if the given studentRoleComments is a non-null string no longer than
     * the specified length {@code STUDENT_ROLE_COMMENTS_MAX_LENGTH}. However, this string can be empty.
     * @param studentRoleComments
     * @return An explanation of why the {@code studentRoleComments} is not acceptable.
     *         Returns an empty string "" if the {@code studentRoleComments} is acceptable.
     */
    public String getInvalidityInfoForStudentRoleComments(String studentRoleComments) {
        return getValidityInfoForSizeCappedPossiblyEmptyString(STUDENT_ROLE_COMMENTS_FIELD_NAME,
                                                               STUDENT_ROLE_COMMENTS_MAX_LENGTH,
                                                               studentRoleComments);
    }

    /**
     * Checks if {@code gender} is one of the recognized genders {@code GENDER_ACCEPTED_VALUES}
     * @param gender
     * @return An explanation of why the {@code gender} is not acceptable.
     *         Returns an empty string if the {@code gender} is acceptable.
     */
    public String getInvalidityInfoForGender(String gender) {
        Assumption.assertTrue("Non-null value expected", gender != null);
        String sanitizedValue = Sanitizer.sanitizeForHtml(gender);
        
        if (!GENDER_ACCEPTED_VALUES.contains(gender)) {
            return String.format(GENDER_ERROR_MESSAGE, sanitizedValue);
        }
        return "";
    }

    /**
     * Checks if {@code feedbackSessionName} is a non-null non-empty string no longer than the specified length
     * {@code COURSE_NAME_MAX_LENGTH}, does not contain any invalid characters (| or %), and has no unsantized
     * HTML characters
     * @param feedbackSessionName
     * @return An explanation of why the {@code feedbackSessionName} is not acceptable.
     *         Returns an empty string if the {@code feedbackSessionName} is acceptable.
     */
    public String getInvalidityInfoForFeedbackSessionName(String feedbackSessionName) {
        String errorsFromAllowedNameValidation = getValidityInfoForAllowedName(
                FEEDBACK_SESSION_NAME_FIELD_NAME, FEEDBACK_SESSION_NAME_MAX_LENGTH, feedbackSessionName);

        // return early if error already exists because session name is too long etc.
        if (!errorsFromAllowedNameValidation.isEmpty()) {
            return errorsFromAllowedNameValidation;
        }

        // checks for unsantized HTML characters
        String errorsFromNonHtmlValidation = getValidityInfoForNonHtmlField(FEEDBACK_SESSION_NAME_FIELD_NAME,
                                                                            feedbackSessionName);
        return errorsFromNonHtmlValidation;
    }

    /**
     * Checks if {@code courseName} is a non-null non-empty string no longer than the specified length
     * {@code COURSE_NAME_MAX_LENGTH}, and also does not contain any invalid characters (| or %).
     * @param courseName
     * @return An explanation of why the {@code courseName} is not acceptable.
     *         Returns an empty string if the {@code courseName} is acceptable.
     */
    public String getInvalidityInfoForCourseName(String courseName) {
        return getValidityInfoForAllowedName(COURSE_NAME_FIELD_NAME, COURSE_NAME_MAX_LENGTH, courseName);
    }

    /**
     * Checks if {@code nationality} is a non-null non-empty string no longer than the specified length
     * {@code NATIONALITY_MAX_LENGTH}, and also does not contain any invalid characters (| or %).
     * @param nationality
     * @return An explanation of why the {@code nationality} is not acceptable.
     *         Returns an empty string if the {@code nationality} is acceptable.
     */
    public String getInvalidityInfoForNationality(String nationality) {
        return getValidityInfoForAllowedName(NATIONALITY_FIELD_NAME, NATIONALITY_MAX_LENGTH,
                                             nationality);
    }

    /**
     * Checks if {@code instituteName} is a non-null non-empty string no longer than the specified length
     * {@code INSTITUTE_NAME_MAX_LENGTH}, and also does not contain any invalid characters (| or %).
     * @param instituteName
     * @return An explanation of why the {@code instituteName} is not acceptable.
     *         Returns an empty string if the {@code instituteName} is acceptable.
     */
    public String getInvalidityInfoForInstituteName(String instituteName) {
        return getValidityInfoForAllowedName(INSTITUTE_NAME_FIELD_NAME, INSTITUTE_NAME_MAX_LENGTH,
                                             instituteName);
    }

    /**
     * Checks if {@code personName} is a non-null non-empty string no longer than the specified length
     * {@code PERSON_NAME_MAX_LENGTH}, and also does not contain any invalid characters (| or %).
     * @param personName
     * @return An explanation of why the {@code personName} is not acceptable.
     *         Returns an empty string if the {@code personName} is acceptable.
     */
    public String getInvalidityInfoForPersonName(String personName) {
        return getValidityInfoForAllowedName(PERSON_NAME_FIELD_NAME, PERSON_NAME_MAX_LENGTH, personName);
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
        
        Assumption.assertTrue("Non-null value expected for " + fieldName, value != null);
        
        if (value.isEmpty()) {
            return String.format(SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, value, fieldName, REASON_EMPTY, fieldName, maxLength);
        }
        if (isUntrimmed(value)) {
            return String.format(WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE, fieldName);
        }
        String sanitizedValue = Sanitizer.sanitizeForHtml(value);
        if (value.length() > maxLength) {
            return String.format(SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, sanitizedValue, fieldName, REASON_TOO_LONG, fieldName, maxLength);
        }
        return "";
    }
    
    /**
     * Checks if the given name (including person name, institute name, course name, feedback session and team name)
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
        
        Assumption.assertTrue("Non-null value expected for " + fieldName, value != null);
        
        if (value.isEmpty()) {
            return String.format(SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, value, fieldName, REASON_EMPTY, fieldName, maxLength);
        }
        if (isUntrimmed(value)) {
            return String.format(WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE, fieldName);
        }
        String sanitizedValue = Sanitizer.sanitizeForHtml(value);
        if (value.length() > maxLength) {
            return String.format(SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, sanitizedValue, fieldName, REASON_TOO_LONG, fieldName, maxLength);
        }
        if (!Character.isLetterOrDigit(value.codePointAt(0))) {
            boolean startsWithBraces = value.charAt(0) == '{' && value.contains("}");
            if (!startsWithBraces) {
                return String.format(INVALID_NAME_ERROR_MESSAGE, sanitizedValue, fieldName, REASON_START_WITH_NON_ALPHANUMERIC_CHAR, fieldName);
            }
            if (!StringHelper.isMatching(value.substring(1), REGEX_NAME)) {
                return String.format(INVALID_NAME_ERROR_MESSAGE, sanitizedValue, fieldName, REASON_CONTAINS_INVALID_CHAR, fieldName);
            }
            return "";
        }
        if (!StringHelper.isMatching(value, REGEX_NAME)) {
            return String.format(INVALID_NAME_ERROR_MESSAGE, sanitizedValue, fieldName, REASON_CONTAINS_INVALID_CHAR, fieldName);
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
        Assumption.assertTrue("Non-null value expected for " + fieldName, value != null);
        
        if (isUntrimmed(value)) {
            return String.format(WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE, fieldName);
        }
        if (value.length() > maxLength) {
            String sanitizedValue = Sanitizer.sanitizeForHtml(value);
            return String.format(SIZE_CAPPED_POSSIBLY_EMPTY_STRING_ERROR_MESSAGE, sanitizedValue, fieldName, REASON_TOO_LONG, fieldName, maxLength);
        }
        return "";
    }
    
    public String getValidityInfoForTimeFrame(FieldType mainFieldType, FieldType earlierFieldType,
            FieldType laterFieldType, Date earlierTime, Date laterTime) {
        
        Assumption.assertTrue("Non-null value expected", laterFieldType != null);
        Assumption.assertTrue("Non-null value expected", earlierTime != null);
        Assumption.assertTrue("Non-null value expected", laterTime != null);
        
        if (TimeHelper.isSpecialTime(earlierTime) || TimeHelper.isSpecialTime(laterTime)) {
            return "";
        }

        String mainFieldName;
        
        if (mainFieldType.equals(FieldType.FEEDBACK_SESSION_TIME_FRAME)) {
            mainFieldName = FEEDBACK_SESSION_NAME;
        } else {
            throw new AssertionError("Unrecognized field type for time frame validity check : " + mainFieldType);
        }
        
        String earlierFieldName;
        
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
        
        String laterFieldName;
        
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
    
    public List<String> getValidityInfoForFeedbackParticipantType(
            FeedbackParticipantType giverType, FeedbackParticipantType recipientType) {
        
        Assumption.assertNotNull("Non-null value expected", giverType);
        Assumption.assertNotNull("Non-null value expected", recipientType);
        
        List<String> errors = new LinkedList<String>();
        if (!giverType.isValidGiver()) {
            errors.add(String.format(PARTICIPANT_TYPE_ERROR_MESSAGE, giverType.toString(), GIVER_TYPE_NAME));
        }
        if (!recipientType.isValidRecipient()) {
            errors.add(String.format(PARTICIPANT_TYPE_ERROR_MESSAGE, recipientType.toString(), RECIPIENT_TYPE_NAME));
        }
        if (giverType == FeedbackParticipantType.TEAMS
                && (recipientType == FeedbackParticipantType.OWN_TEAM
                        || recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS)) {
            errors.add(String.format(PARTICIPANT_TYPE_TEAM_ERROR_MESSAGE,
                    recipientType.toDisplayRecipientName(),
                    giverType.toDisplayGiverName()));
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
            if (!type.isValidViewer()) {
                errors.add(String.format(PARTICIPANT_TYPE_ERROR_MESSAGE,
                        type.toString(), VIEWER_TYPE_NAME));
            }
            if (!showResponsesTo.contains(type)) {
                errors.add("Trying to show giver name to "
                        + type.toString()
                        + " without showing response first.");
            }
        }
        
        for (FeedbackParticipantType type : showRecipientNameTo) {
            if (!type.isValidViewer()) {
                errors.add(String.format(PARTICIPANT_TYPE_ERROR_MESSAGE,
                        type.toString(), VIEWER_TYPE_NAME));
            }
            if (!showResponsesTo.contains(type)) {
                errors.add("Trying to show recipient name to "
                        + type.toString()
                        + " without showing response first.");
            }
        }
        
        for (FeedbackParticipantType type : showResponsesTo) {
            if (!type.isValidViewer()) {
                errors.add(String.format(PARTICIPANT_TYPE_ERROR_MESSAGE,
                        type.toString(), VIEWER_TYPE_NAME));
            }
        }

        return errors;
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
        return value.equals(sanitizedValue) ? "" : String.format(NON_HTML_FIELD_ERROR_MESSAGE, fieldName);
    }
    
    public String getValidityInfoForNonNullField(String fieldName, Object value) {
        return (value == null) ? String.format(NON_NULL_FIELD_ERROR_MESSAGE, fieldName) : "";
    }

    private String getValidityInfoForCourseId(String value) {
        
        Assumption.assertTrue("Non-null value expected", value != null);
        
        if (value.isEmpty()) {
            return String.format(COURSE_ID_ERROR_MESSAGE, value, REASON_EMPTY);
        }
        if (isUntrimmed(value)) {
            return String.format(WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE, "course ID");
        }
        String sanitizedValue = Sanitizer.sanitizeForHtml(value);
        if (value.length() > COURSE_ID_MAX_LENGTH) {
            return String.format(COURSE_ID_ERROR_MESSAGE, sanitizedValue, REASON_TOO_LONG);
        }
        if (!StringHelper.isMatching(value, REGEX_COURSE_ID)) {
            return String.format(COURSE_ID_ERROR_MESSAGE, sanitizedValue, REASON_INCORRECT_FORMAT);
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
            String sanitizedValue = Sanitizer.sanitizeForHtml(value);
            return String.format(INSTRUCTOR_ROLE_ERROR_MESSAGE, sanitizedValue, INSTRUCTOR_ROLE_ERROR_REASON_NOT_MATCHING);
        }
        
        return "";
    }
    
    private String getValidityInfoForEmailContent(Text value) {
        Assumption.assertTrue("Non-null value expected", value != null);
        
        if (value.getValue().isEmpty()) {
            return EMAIL_CONTENT_ERROR_MESSAGE;
        }
        
        return "";
    }

    private boolean isUntrimmed(String value) {
        return value.length() != value.trim().length();
    }
    
    /**
     * Checks whether a given text input represents a format of a valid email address.
     * @param email text input which needs the validation
     * @return true if it is a valid email address, else false.
     */
    public static boolean isValidEmailAddress(String email) {
        return StringHelper.isMatching(email, REGEX_EMAIL);
    }
}
