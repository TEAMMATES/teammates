package teammates.ui.request;

/**
 * The intent of calling the REST API.
 */
public enum Intent {

    /**
     * To get the full detail of the entities.
     */
    FULL_DETAIL,

    /**
     * To submit the feedback session as instructors.
     */
    INSTRUCTOR_SUBMISSION,

    /**
     * To submit the feedback session as students.
     */
    STUDENT_SUBMISSION,

    /**
     * To view the feedback session result as instructors.
     */
    INSTRUCTOR_RESULT,

    /**
     * To view the feedback session result as students.
     */
    STUDENT_RESULT,

}
