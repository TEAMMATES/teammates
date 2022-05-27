package teammates.common.datatransfer;

/**
 * Represents the type of an entity that is involved in a feedback question or response.
 */
public enum FeedbackParticipantType {
    // booleans represent: isValidGiver?, isValidRecipient?, isValidViewer?

    /**
     * Represents "own self".
     *
     * <p>As a recipient, it represents the same person as the response giver.
     *
     * <p>As a giver, it represents the feedback session creator.
     */
    SELF(true, true, false),

    /**
     * Students of the course.
     */
    STUDENTS(true, true, true),

    /**
     * Students of the same section.
     */
    STUDENTS_IN_SAME_SECTION(true, true, true),

    /**
     * Students of the course, excluding the response giver.
     *
     * <p>Used to generate options for MCQ & MSQ.
     */
    STUDENTS_EXCLUDING_SELF(false, true, false),

    /**
     * Instructors of the course.
     */
    INSTRUCTORS(true, true, true),

    /**
     * Teams of the course.
     */
    TEAMS(true, true, false),

    /**
     * Teams of the same section.
     */
    TEAMS_IN_SAME_SECTION(true, true, false),

    /**
     * Teams of the course, excluding the response giver.
     */
    TEAMS_EXCLUDING_SELF(false, true, false),

    /**
     * Team of the response giver.
     */
    OWN_TEAM(false, true, false),

    /**
     * Team members of the response giver, excluding the response giver.
     */
    OWN_TEAM_MEMBERS(false, true, true),

    /**
     * Team members of the response giver, including the response giver.
     */
    OWN_TEAM_MEMBERS_INCLUDING_SELF(false, true, true),

    /**
     * Receiver of the response.
     */
    RECEIVER(false, false, true),

    /**
     * Team members of the receiver of the response.
     */
    RECEIVER_TEAM_MEMBERS(false, false, true),

    /**
     * Represents "no specific recipient".
     */
    NONE(false, true, false),

    /**
     * Giver of the response.
     *
     * <p>Used by feedbackResponseComment.
     */
    GIVER(false, false, true);

    private final boolean validGiver;
    private final boolean validRecipient;
    private final boolean validViewer;

    FeedbackParticipantType(boolean isGiver, boolean isRecipient, boolean isViewer) {
        this.validGiver = isGiver;
        this.validRecipient = isRecipient;
        this.validViewer = isViewer;
    }

    public boolean isValidGiver() {
        return validGiver;
    }

    public boolean isValidRecipient() {
        return validRecipient;
    }

    public boolean isValidViewer() {
        return validViewer;
    }

    public boolean isTeam() {
        return this == TEAMS || this == TEAMS_EXCLUDING_SELF || this == OWN_TEAM || this == TEAMS_IN_SAME_SECTION;
    }

    /**
     * Formats the participant type as a singular noun.
     *
     * @return A user-friendly {@code String} representing this participant in singular form.
     */
    public String toSingularFormString() {
        switch (this) {
        case INSTRUCTORS:
            return "instructor";
        case STUDENTS:
            // Fallthrough
        case STUDENTS_IN_SAME_SECTION:
            // Fallthrough
        case STUDENTS_EXCLUDING_SELF:
            // Fallthrough
        case OWN_TEAM_MEMBERS:
            // Fallthrough
        case OWN_TEAM_MEMBERS_INCLUDING_SELF:
            return "student";
        case TEAMS:
            // Fallthrough
        case TEAMS_IN_SAME_SECTION:
            // Fallthrough
        case TEAMS_EXCLUDING_SELF:
            // Fallthrough
        case OWN_TEAM:
            return "team";
        default:
            return super.toString();
        }
    }

}
