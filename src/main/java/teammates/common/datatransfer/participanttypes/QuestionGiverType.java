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
     * Students of the same section.
     */
    STUDENTS_IN_SAME_SECTION,

    /**
     * Instructors of the course.
     */
    INSTRUCTORS,

    /**
     * Teams of the course.
     */
    TEAMS,

    /**
     * Teams of the same section.
     */
    TEAMS_IN_SAME_SECTION;

    /**
     * Formats the participant type as a singular noun.
     *
     * @return A user-friendly {@code String} representing this participant in
     *         singular form.
     */
    public String toSingularFormString() {
        switch (this) {
        case INSTRUCTORS:
            return "instructor";
        case STUDENTS, STUDENTS_IN_SAME_SECTION:
            return "student";
        case TEAMS, TEAMS_IN_SAME_SECTION:
            return "team";
        default:
            return super.toString();
        }
    }
}
