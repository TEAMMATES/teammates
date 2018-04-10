package teammates.logic.backdoor;

/**
 * Provides a specification of operations that can be called via BackDoor API,
 * along with the possible parameters to be included.
 *
 * @see BackDoorServlet
 */
public enum BackDoorOperation {

    /** Operation type: deleting an account from the datastore. */
    OPERATION_DELETE_ACCOUNT,

    /** Operation type: deleting a course from the datastore. */
    OPERATION_DELETE_COURSE,

    /** Operation type: deleting a feedback question from the datastore. */
    OPERATION_DELETE_FEEDBACK_QUESTION,

    /** Operation type: deleting a feedback response from the datastore. */
    OPERATION_DELETE_FEEDBACK_RESPONSE,

    /** Operation type: deleting a feedback session from the datastore. */
    OPERATION_DELETE_FEEDBACK_SESSION,

    /** Operation type: deleting an instructor from the datastore. */
    OPERATION_DELETE_INSTRUCTOR,

    /** Operation type: deleting a student from the datastore. */
    OPERATION_DELETE_STUDENT,

    /** Operation type: editing a course in the datastore. */
    OPERATION_EDIT_COURSE,

    /** Operation type: editing a feedback question in the datastore. */
    OPERATION_EDIT_FEEDBACK_QUESTION,

    /** Operation type: editing a feedback session in the datastore. */
    OPERATION_EDIT_FEEDBACK_SESSION,

    /** Operation type: editing a student in the datastore. */
    OPERATION_EDIT_STUDENT,

    /** Operation type: editing a student profile picture in the datastore. */
    OPERATION_EDIT_STUDENT_PROFILE_PICTURE,

    /** Operation type: getting an account data from the datastore as JSON. */
    OPERATION_GET_ACCOUNT_AS_JSON,

    /** Operation type: getting a course data from the datastore as JSON. */
    OPERATION_GET_COURSE_AS_JSON,

    /** Operation type: getting the encrypted registration key for an instructor in the datastore. */
    OPERATION_GET_ENCRYPTED_KEY_FOR_INSTRUCTOR,

    /** Operation type: getting the encrypted registration key for a student the datastore. */
    OPERATION_GET_ENCRYPTED_KEY_FOR_STUDENT,

    /** Operation type: getting a feedback question data from the datastore as JSON. */
    OPERATION_GET_FEEDBACK_QUESTION_AS_JSON,

    /** Operation type: getting a feedback question data for particular ID from the datastore as JSON. */
    OPERATION_GET_FEEDBACK_QUESTION_FOR_ID_AS_JSON,

    /** Operation type: getting a feedback response data from the datastore as JSON. */
    OPERATION_GET_FEEDBACK_RESPONSE_AS_JSON,

    /** Operation type: getting a list of feedback response data for particular giver from the datastore as JSON. */
    OPERATION_GET_FEEDBACK_RESPONSES_FOR_GIVER_AS_JSON,

    /** Operation type: getting a list of feedback response data for particular recipient from the datastore as JSON. */
    OPERATION_GET_FEEDBACK_RESPONSES_FOR_RECEIVER_AS_JSON,

    /** Operation type: getting a feedback session data from the datastore as JSON. */
    OPERATION_GET_FEEDBACK_SESSION_AS_JSON,

    /** Operation type: getting an instructor data for particular google ID from the datastore as JSON. */
    OPERATION_GET_INSTRUCTOR_AS_JSON_BY_ID,

    /** Operation type: getting an instructor data for particular email from the datastore as JSON. */
    OPERATION_GET_INSTRUCTOR_AS_JSON_BY_EMAIL,

    /** Operation type: getting a student data from the datastore as JSON. */
    OPERATION_GET_STUDENT_AS_JSON,

    /** Operation type: getting list of student data from the datastore as JSON. */
    OPERATION_GET_STUDENTS_AS_JSON,

    /** Operation type: getting a student profile data from the datastore as JSON. */
    OPERATION_GET_STUDENTPROFILE_AS_JSON,

    /** Operation type: checking if profile picture is present in GCS. */
    OPERATION_IS_PICTURE_PRESENT_IN_GCS,

    /** Operation type: creating a feedback response in the datastore. */
    OPERATION_CREATE_FEEDBACK_RESPONSE,

    /** Operation type: persisting data bundle into the datastore. */
    OPERATION_PERSIST_DATABUNDLE,

    /** Operation type: putting searchable documents into the datastore. */
    OPERATION_PUT_DOCUMENTS,

    /** Operation type: removing data bundle from the datastore and restoring it afterwards. */
    OPERATION_REMOVE_AND_RESTORE_DATABUNDLE,

    /** Operation type: removing data bundle from the datastore. */
    OPERATION_REMOVE_DATABUNDLE,

    /** Operation type: verifying uploaded group list key. */
    OPERATION_IS_GROUP_LIST_FILE_PRESENT_IN_GCS,

    /** Operation type: removing group list file. */
    OPERATION_DELETE_GROUP_LIST_FILE;

    // CHECKSTYLE.OFF:JavadocVariable self-explanatory variables
    public static final String PARAMETER_BACKDOOR_KEY = "PARAMETER_BACKDOOR_KEY";
    public static final String PARAMETER_BACKDOOR_OPERATION = "PARAMETER_BACKDOOR_OPERATION";
    public static final String PARAMETER_COURSE_ID = "PARAMETER_COURSE_ID";
    public static final String PARAMETER_DATABUNDLE_JSON = "PARAMETER_DATABUNDLE_JSON";
    public static final String PARAMETER_FEEDBACK_QUESTION_ID = "PARAMETER_FEEDBACK_QUESTION_ID";
    public static final String PARAMETER_FEEDBACK_QUESTION_NUMBER = "PARAMETER_FEEDBACK_QUESTION_NUMBER";
    public static final String PARAMETER_FEEDBACK_RESPONSE_JSON = "PARAMETER_FEEDBACK_RESPONSE_JSON";
    public static final String PARAMETER_FEEDBACK_SESSION_NAME = "PARAMETER_FEEDBACK_SESSION_NAME";
    public static final String PARAMETER_GIVER_EMAIL = "PARAMETER_GIVER_EMAIL";
    public static final String PARAMETER_GOOGLE_ID = "PARAMETER_GOOGLE_ID";
    public static final String PARAMETER_INSTRUCTOR_EMAIL = "PARAMETER_INSTRUCTOR_EMAIL";
    public static final String PARAMETER_JSON_STRING = "PARAMETER_JSON_STRING";
    public static final String PARAMETER_PICTURE_DATA = "PARAMETER_PICTURE_DATA";
    public static final String PARAMETER_PICTURE_KEY = "PARAMETER_PICTURE_KEY";
    public static final String PARAMETER_RECIPIENT = "PARAMETER_RECIPIENT";
    public static final String PARAMETER_STUDENT_EMAIL = "PARAMETER_STUDENT_EMAIL";
    public static final String PARAMETER_GROUP_LIST_FILE_KEY = "PARAMETER_GROUP_LIST_FILE_KEY";
    // CHECKSTYLE.ON:JavadocVariable

}
