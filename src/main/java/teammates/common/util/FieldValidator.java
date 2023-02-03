package teammates.common.util;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;

/**
 * Used to handle the data validation aspect e.g. validate emails, names, etc.
 */
public final class FieldValidator {

    /////////////////
    // FIELD TYPES //
    /////////////////

    // name-related
    public static final String PERSON_NAME_FIELD_NAME = "person name";
    public static final int PERSON_NAME_MAX_LENGTH = 100;

    public static final String COURSE_NAME_FIELD_NAME = "course name";
    public static final int COURSE_NAME_MAX_LENGTH = 80;

    public static final String FEEDBACK_SESSION_NAME_FIELD_NAME = "feedback session name";
    public static final int FEEDBACK_SESSION_NAME_MAX_LENGTH = 64;

    public static final String TEAM_NAME_FIELD_NAME = "team name";
    public static final int TEAM_NAME_MAX_LENGTH = 60;

    public static final String SECTION_NAME_FIELD_NAME = "section name";
    public static final int SECTION_NAME_MAX_LENGTH = 60;

    public static final String INSTITUTE_NAME_FIELD_NAME = "institute name";
    public static final int INSTITUTE_NAME_MAX_LENGTH = 64;

    // email-related
    public static final String EMAIL_FIELD_NAME = "email";
    public static final int EMAIL_MAX_LENGTH = 254;

    // notification-related
    public static final String NOTIFICATION_TITLE_FIELD_NAME = "notification title";
    public static final String NOTIFICATION_MESSAGE_FIELD_NAME = "notification message";
    public static final String NOTIFICATION_NAME = "notification";
    public static final String NOTIFICATION_VISIBLE_TIME_FIELD_NAME = "time when the notification will be visible";
    public static final String NOTIFICATION_EXPIRY_TIME_FIELD_NAME = "time when the notification will expire";
    public static final String NOTIFICATION_STYLE_FIELD_NAME = "notification style";
    public static final String NOTIFICATION_TARGET_USER_FIELD_NAME = "notification target user";
    public static final int NOTIFICATION_TITLE_MAX_LENGTH = 80;

    public static final List<String> NOTIFICATION_STYLE_ACCEPTED_VALUES =
            Collections.unmodifiableList(
                    Arrays.stream(
                            NotificationStyle.values())
                            .map(NotificationStyle::toString)
                            .collect(Collectors.toList())
            );

    public static final List<String> NOTIFICATION_TARGET_USER_ACCEPTED_VALUES =
            Collections.unmodifiableList(
                    Arrays.stream(
                            NotificationTargetUser.values())
                            .map(NotificationTargetUser::toString)
                            .collect(Collectors.toList())
            );

    // others
    public static final String STUDENT_ROLE_COMMENTS_FIELD_NAME = "comments about a student enrolled in a course";
    public static final int STUDENT_ROLE_COMMENTS_MAX_LENGTH = 500;

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
    public static final String COURSE_ID_FIELD_NAME = "course ID";
    public static final int COURSE_ID_MAX_LENGTH = 64;

    public static final String SESSION_NAME = "feedback session";
    public static final String SESSION_START_TIME_FIELD_NAME = "start time";
    public static final String SESSION_END_TIME_FIELD_NAME = "end time";
    public static final String TIME_ZONE_FIELD_NAME = "time zone";

    public static final String GOOGLE_ID_FIELD_NAME = "Google ID";
    public static final int GOOGLE_ID_MAX_LENGTH = 254;

    public static final String ROLE_FIELD_NAME = "access-level";
    public static final List<String> ROLE_ACCEPTED_VALUES =
            Collections.unmodifiableList(
                    Arrays.asList(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER,
                            Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER,
                            Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER,
                            Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_TUTOR,
                            Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM));

    public static final String GIVER_TYPE_NAME = "feedback giver";
    public static final String RECIPIENT_TYPE_NAME = "feedback recipient";
    public static final String VIEWER_TYPE_NAME = "feedback viewer";

    public static final String EXTENDED_DEADLINES_FIELD_NAME = "extended deadlines";

    ////////////////////
    // ERROR MESSAGES //
    ////////////////////

    public static final String REASON_TOO_LONG = "is too long";
    public static final String REASON_INCORRECT_FORMAT = "is not in the correct format";
    public static final String REASON_CONTAINS_INVALID_CHAR = "contains invalid characters";
    public static final String REASON_START_WITH_NON_ALPHANUMERIC_CHAR = "starts with a non-alphanumeric character";
    public static final String REASON_UNAVAILABLE_AS_CHOICE = "is not available as a choice";

    // error message components
    public static final String EMPTY_STRING_ERROR_INFO =
            "The field '${fieldName}' is empty.";
    public static final String ERROR_INFO =
            "\"${userInput}\" is not acceptable to TEAMMATES as a/an ${fieldName} because it ${reason}.";
    public static final String HINT_FOR_CORRECT_FORMAT_FOR_SIZE_CAPPED_POSSIBLY_EMPTY =
            "The value of a/an ${fieldName} should be no longer than ${maxLength} characters.";
    public static final String HINT_FOR_CORRECT_FORMAT_FOR_SIZE_CAPPED_NON_EMPTY =
            HINT_FOR_CORRECT_FORMAT_FOR_SIZE_CAPPED_POSSIBLY_EMPTY + " It should not be empty.";
    public static final String HINT_FOR_CORRECT_FORMAT_FOR_SIZE_CAPPED_NON_EMPTY_NO_SPACES =
            "It cannot be longer than ${maxLength} characters, cannot be empty and cannot contain spaces.";
    public static final String HINT_FOR_CORRECT_FORMAT_FOR_INVALID_NAME =
            "A/An ${fieldName} must start with an alphanumeric character, and cannot contain any vertical bar "
            + "(|) or percent sign (%).";

    // generic (i.e., not specific to any field) error messages
    public static final String SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE =
            ERROR_INFO + " " + HINT_FOR_CORRECT_FORMAT_FOR_SIZE_CAPPED_NON_EMPTY;
    public static final String SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE_EMPTY_STRING =
            EMPTY_STRING_ERROR_INFO + " " + HINT_FOR_CORRECT_FORMAT_FOR_SIZE_CAPPED_NON_EMPTY;
    public static final String SIZE_CAPPED_POSSIBLY_EMPTY_STRING_ERROR_MESSAGE =
            ERROR_INFO + " " + HINT_FOR_CORRECT_FORMAT_FOR_SIZE_CAPPED_POSSIBLY_EMPTY;
    public static final String SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE_EMPTY_STRING_FOR_SESSION_NAME =
            "The field '${fieldName}' should not be empty." + " "
            + "The value of '${fieldName}' field should be no longer than ${maxLength} characters.";
    public static final String INVALID_NAME_ERROR_MESSAGE =
            ERROR_INFO + " " + HINT_FOR_CORRECT_FORMAT_FOR_INVALID_NAME;
    public static final String TEAM_NAME_IS_VALID_EMAIL_ERROR_MESSAGE =
            "The field " + TEAM_NAME_FIELD_NAME + " is not acceptable to TEAMMATES as the suggested value for "
                    + TEAM_NAME_FIELD_NAME + " can be mis-interpreted as an email.";

    public static final String WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE =
            "The provided ${fieldName} is not acceptable to TEAMMATES as it contains only whitespace "
            + "or contains extra spaces at the beginning or at the end of the text.";
    public static final String NON_HTML_FIELD_ERROR_MESSAGE =
            "The provided ${fieldName} is not acceptable to TEAMMATES "
                    + "as it cannot contain the following special html characters"
                    + " in brackets: (< > \" / ' &)";
    public static final String NON_NULL_FIELD_ERROR_MESSAGE =
            "The provided ${fieldName} is not acceptable to TEAMMATES as it cannot be empty.";

    // field-specific error messages
    public static final String HINT_FOR_CORRECT_EMAIL =
            "An email address contains some text followed by one '@' sign followed by some more text, and should end "
                    + "with a top level domain address like .com. "
            + HINT_FOR_CORRECT_FORMAT_FOR_SIZE_CAPPED_NON_EMPTY_NO_SPACES;
    public static final String EMAIL_ERROR_MESSAGE =
            ERROR_INFO + " " + HINT_FOR_CORRECT_EMAIL;
    public static final String EMAIL_ERROR_MESSAGE_EMPTY_STRING =
            EMPTY_STRING_ERROR_INFO + " " + HINT_FOR_CORRECT_EMAIL;

    public static final String HINT_FOR_CORRECT_COURSE_ID =
            "A course ID can contain letters, numbers, fullstops, hyphens, underscores, and dollar signs. "
            + HINT_FOR_CORRECT_FORMAT_FOR_SIZE_CAPPED_NON_EMPTY_NO_SPACES;
    public static final String COURSE_ID_ERROR_MESSAGE =
            ERROR_INFO + " " + HINT_FOR_CORRECT_COURSE_ID;
    public static final String COURSE_ID_ERROR_MESSAGE_EMPTY_STRING =
            EMPTY_STRING_ERROR_INFO + " " + HINT_FOR_CORRECT_COURSE_ID;

    public static final String HINT_FOR_CORRECT_FORMAT_OF_GOOGLE_ID =
            "A Google ID must be a valid id already registered with Google. "
            + HINT_FOR_CORRECT_FORMAT_FOR_SIZE_CAPPED_NON_EMPTY_NO_SPACES;
    public static final String GOOGLE_ID_ERROR_MESSAGE =
            ERROR_INFO + " " + HINT_FOR_CORRECT_FORMAT_OF_GOOGLE_ID;
    public static final String GOOGLE_ID_ERROR_MESSAGE_EMPTY_STRING =
            EMPTY_STRING_ERROR_INFO + " " + HINT_FOR_CORRECT_FORMAT_OF_GOOGLE_ID;

    public static final String HINT_FOR_CORRECT_TIME_ZONE =
            "The value must be one of the values from the time zone dropdown selector.";
    public static final String TIME_ZONE_ERROR_MESSAGE =
            ERROR_INFO + " " + HINT_FOR_CORRECT_TIME_ZONE;

    public static final String HINT_FOR_CORRECT_GRACE_PERIOD =
            "The value must be one of the options in the grace period dropdown selector.";
    public static final String GRACE_PERIOD_NEGATIVE_ERROR_MESSAGE = "Grace period should not be negative." + " "
            + HINT_FOR_CORRECT_GRACE_PERIOD;

    public static final String ROLE_ERROR_MESSAGE =
            "\"%s\" is not an accepted " + ROLE_FIELD_NAME + " to TEAMMATES. ";

    public static final String NOTIFICATION_STYLE_ERROR_MESSAGE =
            "\"%s\" is not an accepted " + NOTIFICATION_STYLE_FIELD_NAME + " to TEAMMATES. ";

    public static final String NOTIFICATION_TARGET_USER_ERROR_MESSAGE =
            "\"%s\" is not an accepted " + NOTIFICATION_TARGET_USER_FIELD_NAME + " to TEAMMATES. ";

    public static final String SESSION_VISIBLE_TIME_FIELD_NAME = "time when the session will be visible";
    public static final String RESULTS_VISIBLE_TIME_FIELD_NAME = "time when the results will be visible";

    public static final String TIME_BEFORE_ERROR_MESSAGE =
            "The %s for this %s cannot be earlier than the %s.";
    public static final String TIME_BEFORE_OR_EQUAL_ERROR_MESSAGE =
            "The %s for this %s cannot be earlier than or at the same time as the %s.";

    public static final String PARTICIPANT_TYPE_ERROR_MESSAGE = "%s is not a valid %s.";
    public static final String PARTICIPANT_TYPE_TEAM_ERROR_MESSAGE =
            "The feedback recipients cannot be \"%s\" when the feedback giver is \"%s\". "
            + "Did you mean to use \"Self\" instead?";

    public static final String NOT_EXACT_HOUR_ERROR_MESSAGE = "The %s for this feedback session must be at exact hour mark.";

    ///////////////////////////////////////
    // VALIDATION REGEX FOR INTERNAL USE //
    ///////////////////////////////////////

    /**
     * Must start with alphanumeric character, cannot contain vertical bar(|) or percent sign(%).
     */
    public static final String REGEX_NAME = "^[\\p{IsL}\\p{IsN}][^|%]*+$";

    /**
     * Allows English alphabet, numbers, underscore,  dot, dollar sign and hyphen.
     */
    public static final String REGEX_COURSE_ID = "[a-zA-Z0-9_.$-]+";

    /**
     * Local part:
     * <li>Can only start with letters, digits, hyphen or plus sign;
     * <li>Special characters allowed are ! # $ % & ' * + - / = ? ^ _ ` { } ~
     * <li>Dot can only appear between any 2 characters and cannot appear continuously<br>
     * Domain part:
     * <li>Only allow letters, digits, hyphen and dot; Must end with letters; Must have TLD
     */
    public static final String REGEX_EMAIL = "^[\\w+-][\\w+!#$%&'*/=?^_`{}~-]*+(\\.[\\w+!#$%&'*/=?^_`{}~-]+)*+"
                                            + "@([A-Za-z0-9-]+\\.)+[A-Za-z]+$";

    /**
     * Allows English alphabet, numbers, underscore,  dot and hyphen.
     */
    public static final String REGEX_GOOGLE_ID_NON_EMAIL = "[a-zA-Z0-9_.-]+";

    private FieldValidator() {
        // utility class
        // Intentional private constructor to prevent instantiation.
    }

    /////////////////////////////////////////
    // VALIDATION METHODS FOR EXTERNAL USE //
    /////////////////////////////////////////

    /**
     * Checks if {@code email} is not null, not empty, not longer than {@code EMAIL_MAX_LENGTH}, and is a
     * valid email address according to {@code REGEX_EMAIL}.
     * @return An explanation of why the {@code email} is not acceptable.
     *         Returns an empty string if the {@code email} is acceptable.
     */
    public static String getInvalidityInfoForEmail(String email) {

        assert email != null;

        if (email.isEmpty()) {
            return getPopulatedEmptyStringErrorMessage(EMAIL_ERROR_MESSAGE_EMPTY_STRING, EMAIL_FIELD_NAME,
                                            EMAIL_MAX_LENGTH);
        } else if (isUntrimmed(email)) {
            return WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE.replace("${fieldName}", EMAIL_FIELD_NAME);
        } else if (email.length() > EMAIL_MAX_LENGTH) {
            return getPopulatedErrorMessage(EMAIL_ERROR_MESSAGE, email, EMAIL_FIELD_NAME,
                                            REASON_TOO_LONG, EMAIL_MAX_LENGTH);
        } else if (!isValidEmailAddress(email)) {
            return getPopulatedErrorMessage(EMAIL_ERROR_MESSAGE, email, EMAIL_FIELD_NAME,
                                            REASON_INCORRECT_FORMAT, EMAIL_MAX_LENGTH);
        }
        return "";
    }

    /**
     * Checks if {@code gracePeriod} is not negative.
     * @return An explanation why the {@code gracePeriod} is not acceptable.
     *         Returns an empty string if the {@code gracePeriod} is acceptable.
     */
    public static String getInvalidityInfoForGracePeriod(Duration gracePeriod) {
        if (gracePeriod.isNegative()) {
            return GRACE_PERIOD_NEGATIVE_ERROR_MESSAGE;
        }
        return "";
    }

    /**
     * Checks if {@code googleId} is not null, not empty, not longer than {@code GOOGLE_ID_MAX_LENGTH}, does
     * not contain any invalid characters (| or %), AND is either a Google username (without the "@gmail.com")
     * or a valid email address.
     * @return An explanation of why the {@code googleId} is not acceptable.
     *         Returns an empty string if the {@code googleId} is acceptable.
     */
    public static String getInvalidityInfoForGoogleId(String googleId) {

        assert googleId != null;

        boolean isValidFullEmail = isValidEmailAddress(googleId);
        boolean isValidEmailWithoutDomain = StringHelper.isMatching(googleId, REGEX_GOOGLE_ID_NON_EMAIL);

        if (googleId.isEmpty()) {
            return getPopulatedEmptyStringErrorMessage(GOOGLE_ID_ERROR_MESSAGE_EMPTY_STRING,
                                            GOOGLE_ID_FIELD_NAME, GOOGLE_ID_MAX_LENGTH);
        } else if (isUntrimmed(googleId)) {
            return WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE.replace("${fieldName}", GOOGLE_ID_FIELD_NAME);
        } else if (googleId.length() > GOOGLE_ID_MAX_LENGTH) {
            return getPopulatedErrorMessage(GOOGLE_ID_ERROR_MESSAGE, googleId, GOOGLE_ID_FIELD_NAME,
                                            REASON_TOO_LONG, GOOGLE_ID_MAX_LENGTH);
        } else if (!(isValidFullEmail || isValidEmailWithoutDomain)) {
            return getPopulatedErrorMessage(GOOGLE_ID_ERROR_MESSAGE, googleId, GOOGLE_ID_FIELD_NAME,
                                            REASON_INCORRECT_FORMAT, GOOGLE_ID_MAX_LENGTH);
        }
        return "";
    }

    /**
     * Checks if {@code courseId} is not null, not empty, has no surrounding whitespaces, not longer than
     * {@code COURSE_ID_MAX_LENGTH}, is sanitized for HTML, and match the REGEX {@code REGEX_COURSE_ID}.
     * @return An explanation of why the {@code courseId} is not acceptable.
     *         Returns an empty string if the {@code courseId} is acceptable.
     */
    public static String getInvalidityInfoForCourseId(String courseId) {

        assert courseId != null;

        if (courseId.isEmpty()) {
            return getPopulatedEmptyStringErrorMessage(COURSE_ID_ERROR_MESSAGE_EMPTY_STRING,
                                            COURSE_ID_FIELD_NAME, COURSE_ID_MAX_LENGTH);
        }
        if (isUntrimmed(courseId)) {
            return WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE.replace("${fieldName}",
                    COURSE_ID_FIELD_NAME);
        }
        if (courseId.length() > COURSE_ID_MAX_LENGTH) {
            return getPopulatedErrorMessage(COURSE_ID_ERROR_MESSAGE, courseId, COURSE_ID_FIELD_NAME,
                                            REASON_TOO_LONG, COURSE_ID_MAX_LENGTH);
        }
        if (!StringHelper.isMatching(courseId, REGEX_COURSE_ID)) {
            return getPopulatedErrorMessage(COURSE_ID_ERROR_MESSAGE, courseId, COURSE_ID_FIELD_NAME,
                                            REASON_INCORRECT_FORMAT, COURSE_ID_MAX_LENGTH);
        }
        return "";
    }

    /**
     * Checks if {@code sectionName} is a non-null non-empty string no longer than the specified length
     * {@code SECTION_NAME_MAX_LENGTH}, and also does not contain any invalid characters (| or %).
     * @return An explanation of why the {@code sectionName} is not acceptable.
     *         Returns an empty string if the {@code sectionName} is acceptable.
     */
    public static String getInvalidityInfoForSectionName(String sectionName) {
        return getValidityInfoForAllowedName(SECTION_NAME_FIELD_NAME, SECTION_NAME_MAX_LENGTH, sectionName);
    }

    /**
     * Checks if {@code teamName} is a non-null non-empty string no longer than the specified length
     * {@code TEAM_NAME_MAX_LENGTH}, does not contain any invalid characters (| or %) and is not a valid email.
     * @return An explanation of why the {@code teamName} is not acceptable.
     *         Returns an empty string if the {@code teamName} is acceptable.
     */
    public static String getInvalidityInfoForTeamName(String teamName) {
        boolean isValidEmail = isValidEmailAddress(teamName);
        if (isValidEmail) {
            return TEAM_NAME_IS_VALID_EMAIL_ERROR_MESSAGE;
        }
        return getValidityInfoForAllowedName(TEAM_NAME_FIELD_NAME, TEAM_NAME_MAX_LENGTH, teamName);
    }

    /**
     * Checks if the given studentRoleComments is a non-null string no longer than
     * the specified length {@code STUDENT_ROLE_COMMENTS_MAX_LENGTH}. However, this string can be empty.
     * @return An explanation of why the {@code studentRoleComments} is not acceptable.
     *         Returns an empty string "" if the {@code studentRoleComments} is acceptable.
     */
    public static String getInvalidityInfoForStudentRoleComments(String studentRoleComments) {
        return getValidityInfoForSizeCappedPossiblyEmptyString(STUDENT_ROLE_COMMENTS_FIELD_NAME,
                                                               STUDENT_ROLE_COMMENTS_MAX_LENGTH,
                                                               studentRoleComments);
    }

    /**
     * Checks if {@code feedbackSessionName} is a non-null non-empty string no longer than the specified length
     * {@code FEEDBACK_SESSION_NAME_MAX_LENGTH}, does not contain any invalid characters (| or %), and has no
     * unsanitized HTML characters.
     * @return An explanation of why the {@code feedbackSessionName} is not acceptable.
     *         Returns an empty string if the {@code feedbackSessionName} is acceptable.
     */
    public static String getInvalidityInfoForFeedbackSessionName(String feedbackSessionName) {
        String errorsFromAllowedNameValidation = getValidityInfoForAllowedName(
                FEEDBACK_SESSION_NAME_FIELD_NAME, FEEDBACK_SESSION_NAME_MAX_LENGTH, feedbackSessionName);

        // return early if error already exists because session name is too long etc.
        if (!errorsFromAllowedNameValidation.isEmpty()) {
            return errorsFromAllowedNameValidation;
        }

        // checks for unsanitized HTML characters
        return getValidityInfoForNonHtmlField(FEEDBACK_SESSION_NAME_FIELD_NAME, feedbackSessionName);
    }

    /**
     * Checks if {@code courseName} is a non-null non-empty string no longer than the specified length
     * {@code COURSE_NAME_MAX_LENGTH}, and also does not contain any invalid characters (| or %).
     * @return An explanation of why the {@code courseName} is not acceptable.
     *         Returns an empty string if the {@code courseName} is acceptable.
     */
    public static String getInvalidityInfoForCourseName(String courseName) {
        return getValidityInfoForAllowedName(COURSE_NAME_FIELD_NAME, COURSE_NAME_MAX_LENGTH, courseName);
    }

    /**
     * Checks if {@code instituteName} is a non-null non-empty string no longer than the specified length
     * {@code INSTITUTE_NAME_MAX_LENGTH}, and also does not contain any invalid characters (| or %).
     * @return An explanation of why the {@code instituteName} is not acceptable.
     *         Returns an empty string if the {@code instituteName} is acceptable.
     */
    public static String getInvalidityInfoForInstituteName(String instituteName) {
        return getValidityInfoForAllowedName(INSTITUTE_NAME_FIELD_NAME, INSTITUTE_NAME_MAX_LENGTH,
                                             instituteName);
    }

    /**
     * Checks if {@code personName} is a non-null non-empty string no longer than the specified length
     * {@code PERSON_NAME_MAX_LENGTH}, and also does not contain any invalid characters (| or %).
     * @return An explanation of why the {@code personName} is not acceptable.
     *         Returns an empty string if the {@code personName} is acceptable.
     */
    public static String getInvalidityInfoForPersonName(String personName) {
        return getValidityInfoForAllowedName(PERSON_NAME_FIELD_NAME, PERSON_NAME_MAX_LENGTH, personName);
    }

    /**
     * Checks if the given string is a non-null string contained in Java's list of
     * regional time zone IDs.
     * @return An explanation of why the {@code timeZoneValue} is not acceptable.
     *         Returns an empty string if the {@code timeZoneValue} is acceptable.
     */
    public static String getInvalidityInfoForTimeZone(String timeZoneValue) {
        assert timeZoneValue != null;
        if (!ZoneId.getAvailableZoneIds().contains(timeZoneValue)) {
            return getPopulatedErrorMessage(TIME_ZONE_ERROR_MESSAGE,
                    timeZoneValue, TIME_ZONE_FIELD_NAME, REASON_UNAVAILABLE_AS_CHOICE);
        }
        return "";
    }

    /**
     * Checks if {@code role} is one of the recognized roles {@link #ROLE_ACCEPTED_VALUES}.
     *
     * @return An explanation of why the {@code role} is not acceptable.
     *         Returns an empty string if the {@code role} is acceptable.
     */
    public static String getInvalidityInfoForRole(String role) {
        assert role != null;

        if (!ROLE_ACCEPTED_VALUES.contains(role)) {
            return String.format(ROLE_ERROR_MESSAGE, role);
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
     * @param value
     *            The string to be checked.
     * @return An explanation of why the {@code value} is not acceptable.
     *         Returns an empty string "" if the {@code value} is acceptable.
     */
    static String getValidityInfoForAllowedName(String fieldName, int maxLength, String value) {

        assert value != null : "Non-null value expected for " + fieldName;

        if (value.isEmpty()) {
            if (FEEDBACK_SESSION_NAME_FIELD_NAME.equals(fieldName)) {
                return getPopulatedEmptyStringErrorMessage(
                        SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE_EMPTY_STRING_FOR_SESSION_NAME,
                        fieldName, maxLength);
            } else {
                return getPopulatedEmptyStringErrorMessage(SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE_EMPTY_STRING,
                        fieldName, maxLength);
            }
        }
        if (isUntrimmed(value)) {
            return WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE.replace("${fieldName}", fieldName);
        }
        if (value.length() > maxLength) {
            return getPopulatedErrorMessage(SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, value,
                                            fieldName, REASON_TOO_LONG, maxLength);
        }
        if (!Character.isLetterOrDigit(value.codePointAt(0))) {
            boolean hasStartingBrace = value.charAt(0) == '{' && value.contains("}");
            if (!hasStartingBrace) {
                return getPopulatedErrorMessage(INVALID_NAME_ERROR_MESSAGE, value,
                                                fieldName, REASON_START_WITH_NON_ALPHANUMERIC_CHAR);
            }
            if (!StringHelper.isMatching(value.substring(1), REGEX_NAME)) {
                return getPopulatedErrorMessage(INVALID_NAME_ERROR_MESSAGE, value, fieldName,
                                                REASON_CONTAINS_INVALID_CHAR);
            }
            return "";
        }
        if (!StringHelper.isMatching(value, REGEX_NAME)) {
            return getPopulatedErrorMessage(INVALID_NAME_ERROR_MESSAGE, value, fieldName,
                                            REASON_CONTAINS_INVALID_CHAR);
        }
        return "";
    }

    /**
     * Checks if the notification title is a non-null non-empty string.
     *
     * @param notificationTitle The title of the notification.
     * @return An explanation of why the {@code notificationTitle} is not acceptable.
     *         Returns an empty string "" if the {@code notificationTitle} is acceptable.
     */
    public static String getInvalidityInfoForNotificationTitle(String notificationTitle) {

        assert notificationTitle != null : "Non-null value expected for notification title";

        if (notificationTitle.isEmpty()) {
            return getPopulatedEmptyStringErrorMessage(EMPTY_STRING_ERROR_INFO,
                NOTIFICATION_TITLE_FIELD_NAME, NOTIFICATION_TITLE_MAX_LENGTH);
        } else if (notificationTitle.length() > NOTIFICATION_TITLE_MAX_LENGTH) {
            return getPopulatedErrorMessage(SIZE_CAPPED_NON_EMPTY_STRING_ERROR_MESSAGE, notificationTitle,
                NOTIFICATION_TITLE_FIELD_NAME, REASON_TOO_LONG, NOTIFICATION_TITLE_MAX_LENGTH);
        }

        return "";
    }

    /**
     * Checks if the notification message is a non-null non-empty string.
     *
     * @param notificationMessage The notification message.
     * @return An explanation of why the {@code notificationMessage} is not acceptable.
     *         Returns an empty string "" if the {@code notificationMessage} is acceptable.
     */
    public static String getInvalidityInfoForNotificationBody(String notificationMessage) {

        assert notificationMessage != null : "Non-null value expected for notification message";

        if (notificationMessage.isEmpty()) {
            return getPopulatedEmptyStringErrorMessage(EMPTY_STRING_ERROR_INFO, NOTIFICATION_MESSAGE_FIELD_NAME, 0);
        }

        return "";
    }

    /**
     * Checks if {@code style} is one of the recognized notification style {@link #NOTIFICATION_STYLE_ACCEPTED_VALUES}.
     *
     * @return An explanation of why the {@code style} is not acceptable.
     *         Returns an empty string if the {@code style} is acceptable.
     */
    public static String getInvalidityInfoForNotificationStyle(String style) {
        assert style != null;
        try {
            NotificationStyle.valueOf(style);
        } catch (IllegalArgumentException e) {
            return String.format(NOTIFICATION_STYLE_ERROR_MESSAGE, style);
        }
        return "";
    }

    /**
     * Checks if {@code targetUser} is one of the
     * recognized notification target user groups {@link #NOTIFICATION_TARGET_USER_ACCEPTED_VALUES}.
     *
     * @return An explanation of why the {@code targetUser} is not acceptable.
     *         Returns an empty string if the {@code targetUser} is acceptable.
     */
    public static String getInvalidityInfoForNotificationTargetUser(String targetUser) {
        assert targetUser != null;
        try {
            NotificationTargetUser.valueOf(targetUser);
        } catch (IllegalArgumentException e) {
            return String.format(NOTIFICATION_TARGET_USER_ERROR_MESSAGE, targetUser);
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
     * @param value
     *            The string to be checked.
     * @return An explanation of why the {@code value} is not acceptable.
     *         Returns an empty string "" if the {@code value} is acceptable.
     */
    static String getValidityInfoForSizeCappedPossiblyEmptyString(String fieldName, int maxLength, String value) {
        assert value != null : "Non-null value expected for " + fieldName;

        if (isUntrimmed(value)) {
            return WHITESPACE_ONLY_OR_EXTRA_WHITESPACE_ERROR_MESSAGE.replace("${fieldName}", fieldName);
        }
        if (value.length() > maxLength) {
            return getPopulatedErrorMessage(SIZE_CAPPED_POSSIBLY_EMPTY_STRING_ERROR_MESSAGE, value,
                                            fieldName, REASON_TOO_LONG, maxLength);
        }
        return "";
    }

    /**
     * Checks if the {@code startTime} is valid to be used as a session start time.
     * Returns an empty string if it is valid, or an error message otherwise.
     *
     * <p>The {@code startTime} is valid if it is after 2 hours before now, before 90 days from now
     * and at exact hour mark.
     */
    public static String getInvalidityInfoForNewStartTime(Instant startTime, String timeZone) {
        Instant twoHoursBeforeNow = TimeHelper.getInstantHoursOffsetFromNow(-2);
        String earlierThanThreeHoursBeforeNowError = getInvalidityInfoForFirstTimeComparedToSecondTime(
                twoHoursBeforeNow, startTime, SESSION_NAME,
                "2 hours before now", SESSION_START_TIME_FIELD_NAME,
                (firstTime, secondTime) -> firstTime.isBefore(secondTime) || firstTime.equals(secondTime),
                "The %s for this %s cannot be earlier than %s.");
        if (!earlierThanThreeHoursBeforeNowError.isEmpty()) {
            return earlierThanThreeHoursBeforeNowError;
        }

        Instant ninetyDaysFromNow = TimeHelper.getInstantDaysOffsetFromNow(90);
        String laterThanNinetyDaysFromNowError = getInvalidityInfoForFirstTimeComparedToSecondTime(
                ninetyDaysFromNow, startTime, SESSION_NAME,
                "90 days from now", SESSION_START_TIME_FIELD_NAME,
                (firstTime, secondTime) -> firstTime.isAfter(secondTime) || firstTime.equals(secondTime),
                "The %s for this %s cannot be later than %s.");
        if (!laterThanNinetyDaysFromNowError.isEmpty()) {
            return laterThanNinetyDaysFromNowError;
        }

        String notExactHourError = getInvalidityInfoForExactHourTime(startTime, timeZone, "start time");
        if (!notExactHourError.isEmpty()) {
            return notExactHourError;
        }

        return "";
    }

    /**
     * Checks if the {@code endTime} is valid to be used as a session end time.
     * Returns an empty string if it is valid, or an error message otherwise.
     *
     * <p>The {@code endTime} is valid if it is after 1 hour before now, before 180 days from now
     * and at exact hour mark.
     */
    public static String getInvalidityInfoForNewEndTime(Instant endTime, String timeZone) {
        Instant oneHourBeforeNow = TimeHelper.getInstantHoursOffsetFromNow(-1);
        String earlierThanThreeHoursBeforeNowError = getInvalidityInfoForFirstTimeComparedToSecondTime(
                oneHourBeforeNow, endTime, SESSION_NAME,
                "1 hour before now", SESSION_END_TIME_FIELD_NAME,
                (firstTime, secondTime) -> firstTime.isBefore(secondTime) || firstTime.equals(secondTime),
                "The %s for this %s cannot be earlier than %s.");
        if (!earlierThanThreeHoursBeforeNowError.isEmpty()) {
            return earlierThanThreeHoursBeforeNowError;
        }

        Instant oneHundredEightyDaysFromNow = TimeHelper.getInstantDaysOffsetFromNow(180);
        String laterThanOneHundredEightyDaysError = getInvalidityInfoForFirstTimeComparedToSecondTime(
                oneHundredEightyDaysFromNow, endTime, SESSION_NAME,
                "180 days from now", SESSION_END_TIME_FIELD_NAME,
                (firstTime, secondTime) -> firstTime.isAfter(secondTime) || firstTime.equals(secondTime),
                "The %s for this %s cannot be later than %s.");
        if (!laterThanOneHundredEightyDaysError.isEmpty()) {
            return laterThanOneHundredEightyDaysError;
        }

        String notExactHourError = getInvalidityInfoForExactHourTime(endTime, timeZone, "end time");
        if (!notExactHourError.isEmpty()) {
            return notExactHourError;
        }

        return "";
    }

    /**
     * Checks if Session Start Time is before Session End Time.
     * @return Error string if {@code sessionStart} is before {@code sessionEnd}
     *         Empty string if {@code sessionStart} is after {@code sessionEnd}
     */
    public static String getInvalidityInfoForTimeForSessionStartAndEnd(Instant sessionStart, Instant sessionEnd) {
        return getInvalidityInfoForFirstTimeIsBeforeSecondTime(
                sessionStart, sessionEnd, SESSION_NAME, SESSION_START_TIME_FIELD_NAME, SESSION_END_TIME_FIELD_NAME);
    }

    /**
     * Checks if Session Visibility Start Time is before Session Start Time.
     * @return Error string if {@code visibilityStart} is before {@code sessionStart}
     *         Empty string if {@code visibilityStart} is after {@code sessionStart}
     */
    public static String getInvalidityInfoForTimeForVisibilityStartAndSessionStart(
            Instant visibilityStart, Instant sessionStart) {
        return getInvalidityInfoForFirstTimeIsBeforeSecondTime(visibilityStart, sessionStart,
                SESSION_NAME, SESSION_VISIBLE_TIME_FIELD_NAME, SESSION_START_TIME_FIELD_NAME);
    }

    /**
     * Checks if the {@code visibilityStart} is valid to be used as a session visible start time.
     * Returns an empty string if it is valid, or an error message otherwise.
     *
     * <p>The {@code visibilityStart} is valid if it is less than 30 days before {@code sessionStart}.
     */
    public static String getInvalidityInfoForTimeForNewVisibilityStart(Instant visibilityStart, Instant sessionStart) {
        Instant visibilityStartThirtyDaysBeforeSessionStart = sessionStart.minus(Duration.ofDays(30));
        String visibilityStartMoreThanThirtyDaysBeforeSessionStartError =
                getInvalidityInfoForFirstTimeComparedToSecondTime(
                        visibilityStartThirtyDaysBeforeSessionStart, visibilityStart, SESSION_NAME,
                        "30 days before start time", SESSION_VISIBLE_TIME_FIELD_NAME,
                        (firstTime, secondTime) -> firstTime.isBefore(secondTime) || firstTime.equals(secondTime),
                        "The %s for this %s cannot be earlier than %s.");
        if (!visibilityStartMoreThanThirtyDaysBeforeSessionStartError.isEmpty()) {
            return visibilityStartMoreThanThirtyDaysBeforeSessionStartError;
        }
        return "";
    }

    /**
     * Checks if Visibility Start Time is before Results Publish Time.
     * @return Error string if {@code visibilityStart} is before {@code resultsPublish}
     *         Empty string if {@code visibilityStart} is after {@code resultsPublish}
     */
    public static String getInvalidityInfoForTimeForVisibilityStartAndResultsPublish(
            Instant visibilityStart, Instant resultsPublish) {
        return getInvalidityInfoForFirstTimeIsBeforeSecondTime(visibilityStart, resultsPublish,
                SESSION_NAME, SESSION_VISIBLE_TIME_FIELD_NAME, RESULTS_VISIBLE_TIME_FIELD_NAME);
    }

    /**
     * Checks if the session end time is before all extended deadlines.
     * @return Error string if any deadline in {@code deadlines} is before {@code sessionEnd}, an empty one otherwise.
     */
    public static String getInvalidityInfoForTimeForSessionEndAndExtendedDeadlines(
            Instant sessionEnd, Map<String, Instant> deadlines) {
        return deadlines.entrySet()
                .stream()
                .map(entry -> getInvalidityInfoForFirstTimeIsStrictlyBeforeSecondTime(sessionEnd, entry.getValue(),
                        SESSION_NAME, SESSION_END_TIME_FIELD_NAME, EXTENDED_DEADLINES_FIELD_NAME))
                .filter(invalidityInfo -> !invalidityInfo.isEmpty())
                .findFirst()
                .orElse("");
    }

    /**
     * Checks if Notification Start Time is before Notification End Time.
     * @return Error string if {@code notificationStart} is before {@code notificationEnd}
     *         Empty string if {@code notificationStart} is after {@code notificationEnd}
     */
    public static String getInvalidityInfoForTimeForNotificationStartAndEnd(
            Instant notificationStart, Instant notificationExpiry) {
        return getInvalidityInfoForFirstTimeIsBeforeSecondTime(notificationStart, notificationExpiry,
                NOTIFICATION_NAME, NOTIFICATION_VISIBLE_TIME_FIELD_NAME, NOTIFICATION_EXPIRY_TIME_FIELD_NAME);
    }

    private static String getInvalidityInfoForFirstTimeIsBeforeSecondTime(Instant earlierTime, Instant laterTime,
            String entityName, String earlierTimeFieldName, String laterTimeFieldName) {
        return getInvalidityInfoForFirstTimeComparedToSecondTime(earlierTime, laterTime, entityName,
                earlierTimeFieldName, laterTimeFieldName,
                (firstTime, secondTime) -> firstTime.isBefore(secondTime) || firstTime.equals(secondTime),
                TIME_BEFORE_ERROR_MESSAGE);
    }

    private static String getInvalidityInfoForFirstTimeIsStrictlyBeforeSecondTime(
            Instant earlierTime, Instant laterTime, String entityName, String earlierTimeFieldName,
            String laterTimeFieldName) {
        return getInvalidityInfoForFirstTimeComparedToSecondTime(earlierTime, laterTime, entityName,
                earlierTimeFieldName, laterTimeFieldName, Instant::isBefore,
                TIME_BEFORE_OR_EQUAL_ERROR_MESSAGE);
    }

    private static String getInvalidityInfoForFirstTimeComparedToSecondTime(Instant earlierTime, Instant laterTime,
            String entityName, String earlierTimeFieldName, String laterTimeFieldName,
            BiPredicate<Instant, Instant> validityChecker,
            String invalidityInfoTemplate) {

        assert earlierTime != null;
        assert laterTime != null;

        if (TimeHelper.isSpecialTime(earlierTime) || TimeHelper.isSpecialTime(laterTime)) {
            return "";
        }

        if (!validityChecker.test(earlierTime, laterTime)) {
            return String.format(invalidityInfoTemplate, laterTimeFieldName, entityName, earlierTimeFieldName);
        }

        return "";
    }

    private static String getInvalidityInfoForExactHourTime(Instant time, String timeZone, String timeName) {
        // Timezone offsets are usually a whole number of hours, but a few zones are offset by
        // an additional 30 or 45 minutes, such as in India, South Australia and Nepal.
        boolean isExactHour = LocalDateTime.ofInstant(time, ZoneId.of(timeZone)).getMinute() == 0;
        if (!isExactHour) {
            return String.format(NOT_EXACT_HOUR_ERROR_MESSAGE, timeName);
        }
        return "";
    }

    /**
     * Checks if both the giver type and recipient type for the feedback question is valid.
     *
     * @param giverType feedback question giver type to be checked.
     * @param recipientType feedback question recipient type to be checked.
     * @return Error string if either type is invalid, otherwise empty string.
     */
    public static List<String> getValidityInfoForFeedbackParticipantType(
            FeedbackParticipantType giverType, FeedbackParticipantType recipientType) {

        assert giverType != null;
        assert recipientType != null;

        List<String> errors = new LinkedList<>();
        if (!giverType.isValidGiver()) {
            errors.add(String.format(PARTICIPANT_TYPE_ERROR_MESSAGE, giverType.toString(), GIVER_TYPE_NAME));
        }
        if (!recipientType.isValidRecipient()) {
            errors.add(String.format(PARTICIPANT_TYPE_ERROR_MESSAGE, recipientType.toString(), RECIPIENT_TYPE_NAME));
        }
        if (giverType == FeedbackParticipantType.TEAMS
                && (recipientType == FeedbackParticipantType.OWN_TEAM
                        || recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS)) {
            String displayRecipientName = recipientType == FeedbackParticipantType.OWN_TEAM
                    ? "Giver's team" : "Giver's team members";
            errors.add(String.format(PARTICIPANT_TYPE_TEAM_ERROR_MESSAGE,
                    displayRecipientName,
                    "Teams in this course"));
        }

        return errors;
    }

    /**
     * Checks if comment giver type is either instructor, student or team.
     *
     * @param commentGiverType comment giver type to be checked.
     * @return Error string if type is invalid, otherwise empty string.
     */
    public static String getInvalidityInfoForCommentGiverType(FeedbackParticipantType commentGiverType) {
        assert commentGiverType != null;
        if (!commentGiverType.equals(FeedbackParticipantType.STUDENTS)
                   && !commentGiverType.equals(FeedbackParticipantType.INSTRUCTORS)
                   && !commentGiverType.equals(FeedbackParticipantType.TEAMS)) {
            return "Invalid comment giver type: " + commentGiverType;
        }
        return "";
    }

    /**
     * Checks if visibility of comment is following question when comment is from a feedback participant.
     *
     * @param isCommentFromFeedbackParticipant true if comment is from feedback participant.
     * @param isVisibilityFollowingFeedbackQuestion true if visibility of comment follows question.
     * @return Error string if condition is not met, otherwise empty string.
     */
    public static String getInvalidityInfoForVisibilityOfFeedbackParticipantComments(
            boolean isCommentFromFeedbackParticipant,
            boolean isVisibilityFollowingFeedbackQuestion) {
        if (isCommentFromFeedbackParticipant && !isVisibilityFollowingFeedbackQuestion) {
            return "Comment by feedback participant not following visibility setting of the question.";
        }
        return "";
    }

    /**
     * Checks if all the given participant types are valid for the purpose of
     * showing different fields of a feedback response.
     *
     * @param showResponsesTo the list of participant types to whom responses can be shown
     * @param showGiverNameTo the list of participant types to whom giver name can be shown
     * @param showRecipientNameTo the list of participant types to whom recipient name can be shown
     * @return Error string if any type in any list is invalid, otherwise empty string.
     */
    public static List<String> getValidityInfoForFeedbackResponseVisibility(
            List<FeedbackParticipantType> showResponsesTo,
            List<FeedbackParticipantType> showGiverNameTo,
            List<FeedbackParticipantType> showRecipientNameTo) {

        assert showResponsesTo != null;
        assert showGiverNameTo != null;
        assert showRecipientNameTo != null;
        assert !showResponsesTo.contains(null);
        assert !showGiverNameTo.contains(null);
        assert !showRecipientNameTo.contains(null);

        List<String> errors = new LinkedList<>();

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

    /**
     * Checks if the given {@code value} has no HTML code.
     */
    static String getValidityInfoForNonHtmlField(String fieldName, String value) {
        String sanitizedValue = SanitizationHelper.sanitizeForHtml(value);
        //Fails if sanitized value is not same as value
        return value.equals(sanitizedValue) ? "" : NON_HTML_FIELD_ERROR_MESSAGE.replace("${fieldName}", fieldName);
    }

    /**
     * Checks if the given {@code value} is not null.
     */
    public static String getValidityInfoForNonNullField(String fieldName, Object value) {
        return value == null ? NON_NULL_FIELD_ERROR_MESSAGE.replace("${fieldName}", fieldName) : "";
    }

    private static boolean isUntrimmed(String value) {
        return value.length() != value.trim().length();
    }

    /**
     * Checks whether a given text input represents a format of a valid email address.
     * @param email text input which needs the validation
     * @return true if it is a valid email address, else false.
     */
    private static boolean isValidEmailAddress(String email) {
        return StringHelper.isMatching(email, REGEX_EMAIL);
    }

    /**
     * Checks whether all the elements in a Collection are unique.
     * @param elements The Collection of elements to be checked.
     * @return true if all elements are unique, else false.
     */
    public static <T> boolean areElementsUnique(Collection<T> elements) {
        Set<T> uniqueElements = new HashSet<>(elements);
        return uniqueElements.size() == elements.size();
    }

    private static String getPopulatedErrorMessage(
            String messageTemplate, String userInput, String fieldName, String errorReason, int maxLength) {
        return getPopulatedErrorMessage(messageTemplate, userInput, fieldName, errorReason)
                   .replace("${maxLength}", String.valueOf(maxLength));
    }

    private static String getPopulatedErrorMessage(
            String messageTemplate, String userInput, String fieldName, String errorReason) {
        return messageTemplate.replace("${userInput}", userInput)
                              .replace("${fieldName}", fieldName)
                              .replace("${reason}", errorReason);
    }

    private static String getPopulatedEmptyStringErrorMessage(String messageTemplate,
            String fieldName, int maxLength) {
        return messageTemplate.replace("${fieldName}", fieldName)
                              .replace("${maxLength}", String.valueOf(maxLength));
    }
}
