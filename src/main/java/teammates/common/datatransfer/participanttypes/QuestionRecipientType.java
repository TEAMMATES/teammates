package teammates.common.datatransfer.participanttypes;

/**
 * Represents the type of recipient that can receive responses for a feedback
 * question.
 */
public enum QuestionRecipientType {
    /**
     * Represents "own self". As a recipient, it represents the same person as the
     * response giver.
     */
    SELF,

    /**
     * Students of the course.
     */
    STUDENTS,

    /**
     * Students of the same section.
     */
    STUDENTS_IN_SAME_SECTION,

    /**
     * Students of the course, excluding the response giver.
     *
     * <p>
     * Used to generate options for MCQ & MSQ.
     */
    STUDENTS_EXCLUDING_SELF,

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
    TEAMS_IN_SAME_SECTION,

    /**
     * Teams of the course, excluding the response giver.
     */
    TEAMS_EXCLUDING_SELF,

    /**
     * Team of the response giver.
     */
    OWN_TEAM,

    /**
     * Team members of the response giver, excluding the response giver.
     */
    OWN_TEAM_MEMBERS,

    /**
     * Team members of the response giver, including the response giver.
     */
    OWN_TEAM_MEMBERS_INCLUDING_SELF,

    /**
     * Represents "no specific recipient".
     */
    NONE;

    public boolean isTeam() {
        return this == TEAMS || this == TEAMS_EXCLUDING_SELF || this == OWN_TEAM || this == TEAMS_IN_SAME_SECTION;
    }

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
        case STUDENTS, STUDENTS_IN_SAME_SECTION, STUDENTS_EXCLUDING_SELF, OWN_TEAM_MEMBERS,
                OWN_TEAM_MEMBERS_INCLUDING_SELF:
            return "student";
        case TEAMS, TEAMS_EXCLUDING_SELF, TEAMS_IN_SAME_SECTION, OWN_TEAM:
            return "team";
        default:
            return super.toString();
        }
    }

}
