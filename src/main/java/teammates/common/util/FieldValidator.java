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
    // possible reasons for invalidity
    public static final String REASON_EMPTY = "is empty";
    public static final String REASON_TOO_LONG = "is too long";
    public static final String REASON_INCORRECT_FORMAT = "is not in the correct format";
    public static final String REASON_CONTAINS_INVALID_CHAR = "contains invalid characters";
    public static final String REASON_START_WITH_NON_ALPHANUMERIC_CHAR = "starts with a non-alphanumeric character";

    // error message components
    public static final String ERROR_INFO =
            "\"{userInput}\" is not acceptable to TEAMMATES as {fieldName} because it {reason}.";

    public static final String HINT_FOR_CORRECT_FORMAT_FOR_SIZE_CAPPED_NON_EMPTY =
            "The value of {fieldName} should be no longer than {maxLength} characters. It should not be empty.";

    public static final String HINT_FOR_CORRECT_FORMAT_FOR_SIZE_CAPPED_POSSIBLY_EMPTY =
            "The value of {fieldName} should be no longer than {maxLength} characters.";

    public static final String HINT_FOR_CORRECT_FORMAT_FOR_INVALID_NAME =
            "All {fieldName} must start with an alphanumeric character, and cannot contain any vertical bar "
            + "(|) or percent sign (%).";

    // generic error messages
    public static final String SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE =
            ERROR_INFO + " " + HINT_FOR_CORRECT_FORMAT_FOR_SIZE_CAPPED_NON_EMPTY;

    public static final String SIZE_CAPPED_POSSIBLY_EMPTY_STRING_ERROR_MESSAGE =
            ERROR_INFO + " " + HINT_FOR_CORRECT_FORMAT_FOR_SIZE_CAPPED_POSSIBLY_EMPTY;

    public static final String INVALID_NAME_ERROR_MESSAGE =
            ERROR_INFO + " " + HINT_FOR_CORRECT_FORMAT_FOR_INVALID_NAME;

    public static final String WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE =
            "The provided {fieldName} is not acceptable to TEAMMATES as it contains only whitespace "
            + "or contains extra spaces at the beginning or at the end of the text.";

    public static final String NON_HTML_FIELD_ERROR_MESSAGE =
            Sanitizer.sanitizeForHtml("The provided {fieldName} is not acceptable to TEAMMATES as it cannot contain"
                                      + " the following special html characters in brackets: (< > \\ / ' &)");

    public static final String NON_NULL_FIELD_ERROR_MESSAGE =
            "The provided {fieldName} is not acceptable to TEAMMATES as it cannot be empty.";

    // ////////////////////////////////////////////////////////////////////////
    // ////////////////// Generic types ///////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////
    
    /*
     * =======================================================================
     * Field: Email
     */
    public static final String EMAIL_FIELD_NAME = "an email";
    public static final int EMAIL_MAX_LENGTH = 254;
    public static final String HINT_FOR_CORRECT_EMAIL =
            "An email address contains some text followed by one '@' sign followed by some more text. "
            + "It cannot be longer than {maxLength} characters. "
            + "It cannot be empty and it cannot have spaces.";
    public static final String EMAIL_ERROR_MESSAGE =
            ERROR_INFO + " " + HINT_FOR_CORRECT_EMAIL;
    
    public static final String EMAIL_TAKEN_MESSAGE =
            "Trying to update to an email that is already used by: %s/%s";
    
    /*
     * =======================================================================
     * Field: Person name
     */
    public static final String PERSON_NAME_FIELD_NAME = "a person name";
    public static final int PERSON_NAME_MAX_LENGTH = 100;
    public static final String HINT_FOR_CORRECT_PERSON_NAME =
            "The value of {fieldName} should be no longer than {maxLength} characters. It should not be empty.";
    public static final String PERSON_NAME_ERROR_MESSAGE =
            ERROR_INFO + " " + HINT_FOR_CORRECT_PERSON_NAME;

    // ////////////////////////////////////////////////////////////////////////
    // ////////////////// Specific types //////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////

    /*
     * =======================================================================
     * Field: Email Subject
     */
    public static final String EMAIL_SUBJECT_FIELD_NAME = "email subject";
    public static final int EMAIL_SUBJECT_MAX_LENGTH = 200;
    public static final String HINT_FOR_CORRECT_EMAIL_SUBJECT =
            "The value of {fieldName} should be no longer than {maxLength} characters. It should not be empty.";
    public static final String EMAIL_SUBJECT_ERROR_MESSAGE =
            ERROR_INFO + " " + HINT_FOR_CORRECT_EMAIL_SUBJECT;
    
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
    public static final String HINT_FOR_CORRECT_COURSE_NAME =
            "The value of {fieldName} should be no longer than {maxLength} characters. It should not be empty.";
    public static final String COURSE_NAME_ERROR_MESSAGE =
            ERROR_INFO + " " + HINT_FOR_CORRECT_COURSE_NAME;
    
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
    public static final String COURSE_ID_FIELD_NAME = "a Course ID";
    public static final int COURSE_ID_MAX_LENGTH = 40;
    public static final String HINT_FOR_CORRECT_COURSE_ID =
            "A Course ID can contain letters, numbers, fullstops, hyphens, underscores, and dollar signs. "
            + "It cannot be longer than {maxLength} characters. "
            + "It cannot be empty or contain spaces.";
    public static final String COURSE_ID_ERROR_MESSAGE =
            ERROR_INFO + " " + HINT_FOR_CORRECT_COURSE_ID;

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
    public static final String HINT_FOR_CORRECT_FEEDBACK_SESSION_NAME =
            "The value of {fieldName} should be no longer than {maxLength} characters. It should not be empty.";
    public static final String FEEDBACK_SESSION_NAME_ERROR_MESSAGE =
            ERROR_INFO + " " + HINT_FOR_CORRECT_FEEDBACK_SESSION_NAME;
    
    /*
     * =======================================================================
     * Field: Google ID
     */
    public static final String GOOGLE_ID_FIELD_NAME = "Google ID";
    public static final int GOOGLE_ID_MAX_LENGTH = 254;
    public static final String HINT_FOR_CORRECT_FORMAT_OF_GOOGLE_ID =
            "A Google ID must be a valid id already registered with Google. "
            + "It cannot be longer than " + GOOGLE_ID_MAX_LENGTH + " characters. "
            + "It cannot be empty.";

    public static final String GOOGLE_ID_ERROR_MESSAGE =
            ERROR_INFO + " " + HINT_FOR_CORRECT_FORMAT_OF_GOOGLE_ID;
    
    /*
     * =======================================================================
     * Field: Gender
     */
    public static final String GENDER_FIELD_NAME = "gender";
    public static final List<String> GENDER_ACCEPTED_VALUES = Arrays.asList(Const.GenderTypes.MALE,
                                                                            Const.GenderTypes.FEMALE,
                                                                            Const.GenderTypes.OTHER);
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
    public static final String HINT_FOR_CORRECT_INSTITUTE_NAME =
            "The value of {fieldName} should be no longer than {maxLength} characters. It should not be empty.";
    public static final String INSTITUTE_NAME_ERROR_MESSAGE =
            ERROR_INFO + " " + HINT_FOR_CORRECT_INSTITUTE_NAME;

    /*
     * =======================================================================
     * Field: Student comment
     * Not allowed: |
     */
    public static final String STUDENT_ROLE_COMMENTS_FIELD_NAME = "comments about a student enrolled in a course";
    public static final int STUDENT_ROLE_COMMENTS_MAX_LENGTH = 500;
    public static final String HINT_FOR_CORRECT_STUDENT_ROLE =
            "The value of {fieldName} should be no longer than {maxLength} characters.";
    public static final String STUDENT_ROLE_COMMENTS_ERROR_MESSAGE =
            ERROR_INFO + " " + HINT_FOR_CORRECT_STUDENT_ROLE;
    
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
    public static final String HINT_FOR_CORRECT_TEAM_NAME =
            "The value of {fieldName} should be no longer than {maxLength} characters. It should not be empty.";
    public static final String TEAM_NAME_ERROR_MESSAGE =
            ERROR_INFO + " " + HINT_FOR_CORRECT_TEAM_NAME;
    
    /*
     * =======================================================================
     * Field: Section name
     */
    public static final String SECTION_NAME_FIELD_NAME = "a section name";
    public static final int SECTION_NAME_MAX_LENGTH = 60;
    public static final String HINT_FOR_CORRECT_SECTION_NAME =
            "The value of {fieldName} should be no longer than {maxLength} characters. It should not be empty.";
    public static final String SECTION_NAME_ERROR_MESSAGE =
            ERROR_INFO + " " + HINT_FOR_CORRECT_SECTION_NAME;

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
    public static final String PARTICIPANT_TYPE_TEAM_ERROR_MESSAGE =
            "The feedback recipients cannot be \"%s\" when the feedback giver is \"%s\". "
            + "Did you mean to use \"Self\" instead?";
    
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
    public static final String[] REGEX_COLUMN_TEAM =
            {"teams?", "groups?", "students?\\s+teams?", "students?\\s+groups?", "courses?\\s+teams?"};
    public static final String[] REGEX_COLUMN_NAME =
            {"names?", "students?\\s+names?", "full\\s+names?", "students?\\s+full\\s+names?"};
    public static final String[] REGEX_COLUMN_EMAIL =
            {"emails?", "mails?", "e-mails?", "e\\s+mails?", "emails?\\s+address(es)?",
             "e-mails?\\s+address(es)?", "contacts?"};
    public static final String[] REGEX_COLUMN_COMMENT = {"comments?", "notes?"};
    
    /**
     * Checks if {@code emailContent} is not null and not empty
     * @param emailContent
     * @return An explanation of why the {@code emailContent} is not acceptable.
     *         Returns an empty string if the {@code emailContent} is acceptable.
     */
    public String getInvalidityInfoForEmailContent(Text emailContent) {
        Assumption.assertTrue("Non-null value expected", emailContent != null);
        if (emailContent.getValue().isEmpty()) {
            return EMAIL_CONTENT_ERROR_MESSAGE;
        }
        return "";
    }

    /**
     * Checks if {@code emailSubject} is a non-null non-empty string no longer than the specified length
     * {@code EMAIL_SUBJECT_MAX_LENGTH}, and also does not contain any invalid characters (| or %).
     * @param emailSubject
     * @return An explanation of why the {@code emailSubject} is not acceptable.
     *         Returns an empty string if the {@code emailSubject} is acceptable.
     */
    public String getInvalidityInfoForEmailSubject(String emailSubject) {
        return getValidityInfoForAllowedName(
                EMAIL_SUBJECT_FIELD_NAME, EMAIL_SUBJECT_MAX_LENGTH, emailSubject);
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
            return EMAIL_ERROR_MESSAGE.replace("{userInput}", email)
                                      .replace("{fieldName}", EMAIL_FIELD_NAME)
                                      .replace("{reason}", REASON_EMPTY)
                                      .replace("{maxLength}", String.valueOf(EMAIL_MAX_LENGTH));
        } else if (isUntrimmed(email)) {
            return WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE.replace("{fieldName}", EMAIL_FIELD_NAME);
        } else if (email.length() > EMAIL_MAX_LENGTH) {
            return EMAIL_ERROR_MESSAGE.replace("{userInput}", sanitizedValue)
                                      .replace("{fieldName}", EMAIL_FIELD_NAME)
                                      .replace("{reason}", REASON_TOO_LONG)
                                      .replace("{maxLength}", String.valueOf(EMAIL_MAX_LENGTH));
        } else if (!StringHelper.isMatching(email, REGEX_EMAIL)) {
            return EMAIL_ERROR_MESSAGE.replace("{userInput}", sanitizedValue)
                                      .replace("{fieldName}", EMAIL_FIELD_NAME)
                                      .replace("{reason}", REASON_INCORRECT_FORMAT)
                                      .replace("{maxLength}", String.valueOf(EMAIL_MAX_LENGTH));
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
            return GOOGLE_ID_ERROR_MESSAGE.replace("{userInput}", googleId)
                                          .replace("{fieldName}", GOOGLE_ID_FIELD_NAME)
                                          .replace("{reason}", REASON_EMPTY);
        } else if (isUntrimmed(googleId)) {
            return WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE.replace("{fieldName}", GOOGLE_ID_FIELD_NAME);
        } else if (googleId.length() > GOOGLE_ID_MAX_LENGTH) {
            return GOOGLE_ID_ERROR_MESSAGE.replace("{userInput}", sanitizedValue)
                                          .replace("{fieldName}", GOOGLE_ID_FIELD_NAME)
                                          .replace("{reason}", REASON_TOO_LONG);
        } else if (!(isValidFullEmail || isValidEmailWithoutDomain)) {
            return GOOGLE_ID_ERROR_MESSAGE.replace("{userInput}", sanitizedValue)
                                          .replace("{fieldName}", GOOGLE_ID_FIELD_NAME)
                                          .replace("{reason}", REASON_INCORRECT_FORMAT);
        }
        return "";
    }

    /**
     * Checks if {@code courseId} is not null, not empty, has no surrounding whitespaces, not longer than
     * {@code COURSE_ID_MAX_LENGTH}, is sanitized for HTML, and match the REGEX {@code REGEX_COURSE_ID}
     * @param courseId
     * @return An explanation of why the {@code courseId} is not acceptable.
     *         Returns an empty string if the {@code courseId} is acceptable.
     */
    public String getInvalidityInfoForCourseId(String courseId) {

        Assumption.assertTrue("Non-null value expected", courseId != null);

        if (courseId.isEmpty()) {
            return COURSE_ID_ERROR_MESSAGE.replace("{userInput}", courseId)
                                          .replace("{fieldName}", COURSE_ID_FIELD_NAME)
                                          .replace("{reason}", REASON_EMPTY)
                                          .replace("{maxLength}", String.valueOf(COURSE_ID_MAX_LENGTH));
        }
        if (isUntrimmed(courseId)) {
            return WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE.replace("{fieldName}", COURSE_NAME_FIELD_NAME);
        }
        String sanitizedValue = Sanitizer.sanitizeForHtml(courseId);
        if (courseId.length() > COURSE_ID_MAX_LENGTH) {
            return COURSE_ID_ERROR_MESSAGE.replace("{userInput}", sanitizedValue)
                                          .replace("{fieldName}", COURSE_ID_FIELD_NAME)
                                          .replace("{reason}", REASON_TOO_LONG)
                                          .replace("{maxLength}", String.valueOf(COURSE_ID_MAX_LENGTH));
        }
        if (!StringHelper.isMatching(courseId, REGEX_COURSE_ID)) {
            return COURSE_ID_ERROR_MESSAGE.replace("{userInput}", sanitizedValue)
                                          .replace("{fieldName}", COURSE_ID_FIELD_NAME)
                                          .replace("{reason}", REASON_INCORRECT_FORMAT)
                                          .replace("{maxLength}", String.valueOf(COURSE_ID_MAX_LENGTH));
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
     * {@code FEEDBACK_SESSION_NAME_MAX_LENGTH}, does not contain any invalid characters (| or %), and has no
     * unsantized HTML characters
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
            return SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE.replace("{userInput}", value)
                                                             .replace("{fieldName}", fieldName)
                                                             .replace("{reason}", REASON_EMPTY)
                                                             .replace("{maxLength}", String.valueOf(maxLength));
        }
        if (isUntrimmed(value)) {
            return WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE.replace("{fieldName}", fieldName);
        }
        String sanitizedValue = Sanitizer.sanitizeForHtml(value);
        if (value.length() > maxLength) {
            return SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE.replace("{userInput}", sanitizedValue)
                                                             .replace("{fieldName}", fieldName)
                                                             .replace("{reason}", REASON_TOO_LONG)
                                                             .replace("{maxLength}", String.valueOf(maxLength));
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
            return SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE.replace("{userInput}", value)
                                                             .replace("{fieldName}", fieldName)
                                                             .replace("{reason}", REASON_EMPTY)
                                                             .replace("{maxLength}", String.valueOf(maxLength));
        }
        if (isUntrimmed(value)) {
            return WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE.replace("{fieldName}", fieldName);
        }
        String sanitizedValue = Sanitizer.sanitizeForHtml(value);
        if (value.length() > maxLength) {
            return SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE.replace("{userInput}", sanitizedValue)
                                                             .replace("{fieldName}", fieldName)
                                                             .replace("{reason}", REASON_TOO_LONG)
                                                             .replace("{maxLength}", String.valueOf(maxLength));
        }
        if (!Character.isLetterOrDigit(value.codePointAt(0))) {
            boolean startsWithBraces = value.charAt(0) == '{' && value.contains("}");
            if (!startsWithBraces) {
                return INVALID_NAME_ERROR_MESSAGE.replace("{userInput}", sanitizedValue)
                                                 .replace("{fieldName}", fieldName)
                                                 .replace("{reason}", REASON_START_WITH_NON_ALPHANUMERIC_CHAR);
            }
            if (!StringHelper.isMatching(value.substring(1), REGEX_NAME)) {
                return INVALID_NAME_ERROR_MESSAGE.replace("{userInput}", sanitizedValue)
                                                 .replace("{fieldName}", fieldName)
                                                 .replace("{reason}", REASON_CONTAINS_INVALID_CHAR);
            }
            return "";
        }
        if (!StringHelper.isMatching(value, REGEX_NAME)) {
            return INVALID_NAME_ERROR_MESSAGE.replace("{userInput}", sanitizedValue)
                                             .replace("{fieldName}", fieldName)
                                             .replace("{reason}", REASON_CONTAINS_INVALID_CHAR);
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
            return WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE.replace("{fieldName}", fieldName);
        }
        if (value.length() > maxLength) {
            String sanitizedValue = Sanitizer.sanitizeForHtml(value);
            return SIZE_CAPPED_POSSIBLY_EMPTY_STRING_ERROR_MESSAGE.replace("{userInput}", sanitizedValue)
                                                                  .replace("{fieldName}", fieldName)
                                                                  .replace("{reason}", REASON_TOO_LONG)
                                                                  .replace("{maxLength}", String.valueOf(maxLength));
        }
        return "";
    }
    
    /**
     * Checks if Session Start Time is before Session End Time
     * @return Error string if {@code sessionStart} is before {@code sessionEnd}
     *         Empty string if {@code sessionStart} is after {@code sessionEnd}
     */
    public String getInvalidityInfoForTimeForSessionStartAndEnd(Date sessionStart, Date sessionEnd) {
        return getInvalidtyInfoForFirstTimeIsBeforeSecondTime(
                sessionStart, sessionEnd, START_TIME_FIELD_NAME, END_TIME_FIELD_NAME);
    }

    /**
     * Checks if Session Visibility Start Time is before Session Start Time
     * @return Error string if {@code visibilityStart} is before {@code sessionStart}
     *         Empty string if {@code visibilityStart} is after {@code sessionStart}
     */
    public String getInvalidityInfoForTimeForVisibilityStartAndSessionStart(Date visibilityStart,
                                                                            Date sessionStart) {
        return getInvalidtyInfoForFirstTimeIsBeforeSecondTime(
                visibilityStart, sessionStart, SESSION_VISIBLE_TIME_FIELD_NAME, START_TIME_FIELD_NAME);
    }

    /**
     * Checks if Visibility Start Time is before Results Publish Time
     * @return Error string if {@code visibilityStart} is before {@code resultsPublish}
     *         Empty string if {@code visibilityStart} is after {@code resultsPublish}
     */
    public String getInvalidityInfoForTimeForVisibilityStartAndResultsPublish(Date visibilityStart,
                                                                              Date resultsPublish) {
        return getInvalidtyInfoForFirstTimeIsBeforeSecondTime(visibilityStart, resultsPublish,
                SESSION_VISIBLE_TIME_FIELD_NAME, RESULTS_VISIBLE_TIME_FIELD_NAME);
    }

    private String getInvalidtyInfoForFirstTimeIsBeforeSecondTime(Date earlierTime, Date laterTime,
            String earlierTimeFieldName, String laterTimeFieldName) {
        Assumption.assertTrue("Non-null value expected", earlierTime != null);
        Assumption.assertTrue("Non-null value expected", laterTime != null);
        if (TimeHelper.isSpecialTime(earlierTime) || TimeHelper.isSpecialTime(laterTime)) {
            return "";
        }
        if (laterTime.before(earlierTime)) {
            return String.format(TIME_FRAME_ERROR_MESSAGE, laterTimeFieldName,
                                 FEEDBACK_SESSION_NAME, earlierTimeFieldName);
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
        return value.equals(sanitizedValue) ? "" : NON_HTML_FIELD_ERROR_MESSAGE.replace("{fieldName}", fieldName);
    }
    
    public String getValidityInfoForNonNullField(String fieldName, Object value) {
        return (value == null) ? NON_NULL_FIELD_ERROR_MESSAGE.replace("{fieldName}", fieldName) : "";
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
