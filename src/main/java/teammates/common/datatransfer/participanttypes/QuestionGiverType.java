package teammates.common.datatransfer.participanttypes;

/**
 * Represents the type of giver that can give responses to a feedback question.
 */
public enum QuestionGiverType {
    /**
     * Represents the feedback session creator.
     */
    SESSION_CREATOR,

    /**
     * Students of the course.
     */
    STUDENTS,

    /**
     * Instructors of the course.
     */
    INSTRUCTORS,

    /**
     * Teams of the course.
     */
    TEAMS,
}
