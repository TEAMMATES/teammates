package teammates.common.datatransfer.participanttypes;

/**
 * Represents the type of participant that can view responses or comments for a
 * feedback question.
 */
public enum ViewerType {
    /**
     * Students of the course.
     */
    STUDENTS,

    /**
     * Students of the same section.
     */
    STUDENTS_IN_SAME_SECTION,

    /**
     * Instructors of the course.
     */
    INSTRUCTORS,

    /**
     * Team members of the response giver, excluding the response giver.
     */
    OWN_TEAM_MEMBERS,

    /**
     * Team members of the response giver, including the response giver.
     */
    OWN_TEAM_MEMBERS_INCLUDING_SELF,

    /**
     * Receiver of the response.
     */
    RECEIVER,

    /**
     * Team members of the receiver of the response.
     */
    RECEIVER_TEAM_MEMBERS,

    /**
     * Giver of the response.
     *
     * <p>
     * Used by feedbackResponseComment.
     */
    GIVER
}
