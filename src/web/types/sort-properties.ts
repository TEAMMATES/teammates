/**
 * Elements that tables are sorted by.
 */
export enum SortBy {
    /**
     * Nothing.
     */
    NONE,

    /**
     * Section Name.
     */
    SECTION_NAME,

    /**
     * Team name.
     */
    TEAM_NAME,

    /**
     * Student Name.
     */
    STUDENT_NAME,

    /**
     * The email of the student.
     */
    EMAIL,

    /**
     * Join status
     */
     JOIN_STATUS,

    /**
     * Course ID.
     */
    COURSE_ID,

    /**
     * Course Name.
     */
    COURSE_NAME,

    /**
     * The creation time of the course.
     */
    COURSE_CREATION_DATE,

    /**
     * Completion status of feedback session.
     */
    SESSION_COMPLETION_STATUS,

    /**
     * Feedback session name.
     */
    SESSION_NAME,

    /**
     * Start time of the feedback session.
     */
    SESSION_START_DATE,

    /**
     * End time of the feedback session.
     */
    SESSION_END_DATE,

    /**
     * The creation time of the feedback session.
     */
    SESSION_CREATION_DATE,

    /**
     * The time when the feedback session is moved to recycle bin.
     */
    SESSION_DELETION_DATE,

    /**
     * Feedback question type.
     */
    QUESTION_TYPE,

    /**
     * Feedback question text (brief).
     */
    QUESTION_TEXT,
}

/**
 * Sort order.
 */
export enum SortOrder {
    /**
     * Descending sort order.
     */
    DESC,

    /**
     * Ascending sort order
     */
    ASC,
}
