package teammates.common.datatransfer.participanttypes;

/**
 * Represents the type of giver that can give responses to a feedback question.
 */
public enum QuestionGiverType {
    /**
     * Represents "own self". As a giver, it represents the feedback session
     * creator.
     */
    SELF, // TODO: rename to SESSION_CREATOR or similar to avoid ambiguity

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
