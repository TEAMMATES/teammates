package teammates.common.datatransfer;

public enum FeedbackParticipantType {
    // booleans represent: isValidGiver?, isValidRecipient? isValidViewer?
    SELF(true, true, false),
    STUDENTS(true, true, true),
    //used to generate options for MCQ & MSQ:
    STUDENTS_EXCLUDING_SELF(false, false, false),

    INSTRUCTORS(true, true, true),
    TEAMS(true, true, false),
    TEAMS_EXCLUDING_SELF(false, false, false),
    OWN_TEAM(false, true, false),
    OWN_TEAM_MEMBERS(false, true, true),
    OWN_TEAM_MEMBERS_INCLUDING_SELF(false, true, true),
    RECEIVER(false, false, true),
    RECEIVER_TEAM_MEMBERS(false, false, true),
    NONE(false, true, false),
    // Used by feedbackResponseComment:
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
        return this == TEAMS || this == OWN_TEAM;
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
        case STUDENTS_EXCLUDING_SELF:
            // Fallthrough
        case OWN_TEAM_MEMBERS:
            // Fallthrough
        case OWN_TEAM_MEMBERS_INCLUDING_SELF:
            return "student";
        case TEAMS:
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
