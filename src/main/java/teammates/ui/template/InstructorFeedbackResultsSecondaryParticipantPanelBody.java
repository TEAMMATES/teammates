package teammates.ui.template;

import java.util.List;

/**
 * Data model for the panel body for the participant panels 
 * used in Results by Participant > Participant > Question
 * 
 */
public class InstructorFeedbackResultsSecondaryParticipantPanelBody {
    String secondaryParticipantIdentifier;
    String secondaryParticipantDisplayableName;
    
    List<InstructorFeedbackResultsResponsePanel> responsePanels;

    
    
    
    public InstructorFeedbackResultsSecondaryParticipantPanelBody(
                                    String secondaryParticipantIdentifier,
                                    String secondaryParticipantDisplayableName,
                                    List<InstructorFeedbackResultsResponsePanel> responsePanels) {
        this.secondaryParticipantIdentifier = secondaryParticipantIdentifier;
        this.secondaryParticipantDisplayableName = secondaryParticipantDisplayableName;
        this.responsePanels = responsePanels;
    }

    public String getSecondaryParticipantIdentifier() {
        return secondaryParticipantIdentifier;
    }

    public String getSecondaryParticipantDisplayableName() {
        return secondaryParticipantDisplayableName;
    }

    public List<InstructorFeedbackResultsResponsePanel> getResponsePanels() {
        return responsePanels;
    }
    
    
    
}
