package teammates.ui.template;

import java.util.List;

/**
 * Data model for the panel body for the participant panels 
 * used in Results by Participant > Participant > Question
 * 
 */
public class InstructorFeedbackResultsSecondaryParticipantPanelBody {
    private String secondaryParticipantIdentifier;
    private String secondaryParticipantDisplayableName;
    
    private boolean isEmailValid;
    private String profilePictureLink;
    
    private boolean isModerationButtonDisplayed;
    private InstructorResultsModerationButton moderationButton;
    
    private List<InstructorFeedbackResultsResponsePanel> responsePanels;

    
    public InstructorFeedbackResultsSecondaryParticipantPanelBody(
                                    String secondaryParticipantIdentifier,
                                    String secondaryParticipantDisplayableName,
                                    List<InstructorFeedbackResultsResponsePanel> responsePanels,
                                    boolean isEmailValid) {
        this.secondaryParticipantIdentifier = secondaryParticipantIdentifier;
        this.secondaryParticipantDisplayableName = secondaryParticipantDisplayableName;
        this.responsePanels = responsePanels;
        this.isEmailValid = isEmailValid;
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

    public boolean isEmailValid() {
        return isEmailValid;
    }

    public String getProfilePictureLink() {
        return profilePictureLink;
    }

    public void setProfilePictureLink(String profilePictureLink) {
        this.profilePictureLink = profilePictureLink;
    }

    public boolean isModerationButtonDisplayed() {
        return isModerationButtonDisplayed;
    }

    public void setModerationButtonDisplayed(boolean isModerationButtonDisplayed) {
        this.isModerationButtonDisplayed = isModerationButtonDisplayed;
    }

    public InstructorResultsModerationButton getModerationButton() {
        return moderationButton;
    }

    public void setModerationButton(InstructorResultsModerationButton moderationButton) {
        this.moderationButton = moderationButton;
    }
    
}
