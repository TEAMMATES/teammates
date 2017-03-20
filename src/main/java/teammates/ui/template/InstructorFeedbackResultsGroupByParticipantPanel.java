package teammates.ui.template;

import java.util.List;

/**
 * Data model for the giver panel in InstructorFeedbackResults for Giver > Recipient > Question,
 * and for the recipient panel in Recipient > Giver > Question.
 */
public class InstructorFeedbackResultsGroupByParticipantPanel extends InstructorFeedbackResultsParticipantPanel {
    // One InstructorFeedbackResultsSecondaryParticipantPanelBody for each secondary participant
    private List<InstructorFeedbackResultsSecondaryParticipantPanelBody> secondaryParticipantPanels;

    public InstructorFeedbackResultsGroupByParticipantPanel(
            List<InstructorFeedbackResultsSecondaryParticipantPanelBody> secondaryParticipantPanels) {
        this.secondaryParticipantPanels = secondaryParticipantPanels;
    }

    public List<InstructorFeedbackResultsSecondaryParticipantPanelBody> getSecondaryParticipantPanels() {
        return secondaryParticipantPanels;
    }

}
