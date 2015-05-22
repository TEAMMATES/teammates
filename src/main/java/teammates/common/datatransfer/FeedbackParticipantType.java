package teammates.common.datatransfer;

public enum FeedbackParticipantType {
    // booleans represent: isValidGiver?, isValidRecipient? isValidViewer?
    // Strings represents: option shown in giver select box, option shown in recipient select box,
    // text displayed during feedback submission respectively.
    SELF(true, true, false, "Me (Session creator)", "Giver (Self feedback)", ""),
    STUDENTS(true, true, true, "Students in this course", "Other students in the course", "Other students in the course"),
    INSTRUCTORS(true, true, true, "Instructors in this course", "Instructors in the course", "Instructors in this course"),
    TEAMS(true, true, false, "Teams in this course", "Other teams in the course", ""),
    OWN_TEAM(false, true, false, "", "Giver's team", "Your team"),
    OWN_TEAM_MEMBERS(false, true, true, "", "Giver's team members", "Your team members"),
    OWN_TEAM_MEMBERS_INCLUDING_SELF(false, true, true, "","Giver's team members and Giver", "Your team members"),
    RECEIVER(false, false, true, "", "", "The receiving"),
    RECEIVER_TEAM_MEMBERS(false, false, true, "", "", "The recipient's team members"),
    NONE(false, true, false, "", "Nobody specific (For general class feedback)", ""),
    // Used by feedbackResponseComment:
    GIVER(false, false, true, "", "", "");

    private final boolean validGiver;
    private final boolean validRecipient;
    private final boolean validViewer;
    private String displayNameGiver;
    private String displayNameRecipient;
    private String displayNameVisibility;

    FeedbackParticipantType(boolean isGiver, boolean isRecipient, boolean isViewer,
                            String displayNameGiver, String displayNameRecipient, String displayNameVisibility) {
        this.validGiver = isGiver;
        this.validRecipient = isRecipient;
        this.validViewer = isViewer;
        this.displayNameGiver = displayNameGiver;
        this.displayNameRecipient = displayNameRecipient;
        this.displayNameVisibility = displayNameVisibility;
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
     * Formats the participant type as a giver for display to user.
     *
     * @return A user-friendly {@code String} representing this participant as a feedback giver.
     */
    public String toDisplayGiverName() {
        return displayNameGiver;
    }

    /**
     * Formats the participant type as a recipient for display to user.
     *
     * @return A user-friendly {@code String} representing this participant as a feedback recipient.
     */
    public String toDisplayRecipientName() {
        return displayNameRecipient;
    }

    /**
     * Formats the participant type for display to user in the response visibility section.
     *
     * @return A user-friendly {@code String} representing this participant directed to users who are
     * responding to a feedback.
     */
    public String toVisibilityString() {
        return displayNameVisibility;
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
            case OWN_TEAM_MEMBERS:
                // Fallthrough
            case OWN_TEAM_MEMBERS_INCLUDING_SELF:
                return "student";
            case TEAMS:
                return "team";
            case OWN_TEAM:
                return "team";
            default:
                return super.toString();
        }
    }
}
