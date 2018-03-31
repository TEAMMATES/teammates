package teammates.ui.datatransfer;

public enum InstructorFeedbackResultsPageViewType {

    QUESTION, GIVER_QUESTION_RECIPIENT, RECIPIENT_QUESTION_GIVER, RECIPIENT_GIVER_QUESTION, GIVER_RECIPIENT_QUESTION;

    @Override
    public String toString() {
        // replace _ to - to keep it consistent with old behavior
        return name().toLowerCase().replaceAll("_", "-");
    }

    public boolean isPrimaryGroupingOfGiverType() {
        return this == GIVER_QUESTION_RECIPIENT || this == GIVER_RECIPIENT_QUESTION;
    }

    public boolean isSecondaryGroupingOfParticipantType() {
        return this == RECIPIENT_GIVER_QUESTION || this == GIVER_RECIPIENT_QUESTION;
    }

    public String additionalInfoId() {
        switch (this) {
        case GIVER_QUESTION_RECIPIENT:
            return "giver-%s-question-%s";
        case RECIPIENT_QUESTION_GIVER:
            return "recipient-%s-question-%s";
        case GIVER_RECIPIENT_QUESTION:
        case RECIPIENT_GIVER_QUESTION:
            return "giver-%s-recipient-%s";
        default:
            return "";
        }
    }

}
