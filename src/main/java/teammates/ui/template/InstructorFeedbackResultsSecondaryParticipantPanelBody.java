package teammates.ui.template;

import java.util.List;

/**
 * Data model for the panel body for the participant panels.
 */
public class InstructorFeedbackResultsSecondaryParticipantPanelBody {
    private String secondaryParticipantIdentifier;
    private String secondaryParticipantDisplayableName;

    private String profilePictureLink;

    private InstructorFeedbackResultsModerationButton moderationButton;

    private List<InstructorFeedbackResultsResponsePanel> responsePanels;

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

    public String getProfilePictureLink() {
        return profilePictureLink;
    }

    public void setProfilePictureLink(String profilePictureLink) {
        this.profilePictureLink = profilePictureLink;
    }

    public InstructorFeedbackResultsModerationButton getModerationButton() {
        return moderationButton;
    }

    public void setModerationButton(InstructorFeedbackResultsModerationButton moderationButton) {
        this.moderationButton = moderationButton;
    }

}
