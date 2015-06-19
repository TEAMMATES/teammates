package teammates.ui.template;

import teammates.common.util.Url;

/**
 * Data model for Instructor Feedback Results view by question, view by...
 *
 */
public class InstructorResultsResponseRow {
    
    private String giverDisplayableIdentifier;
    private String giverTeam;
    private boolean isGiverProfilePictureDisplayed;
    private Url giverProfilePictureLink;
    
    
    private String recipientDisplayableIdentifier;
    private String recipientTeam;
    private boolean isRecipientProfilePictureDisplayed;
    private Url recipientProfilePictureLink;
    
    private String displayableResponse;
    
    private boolean isModerationsButtonDisplayed;
    private ModerationButton moderationButton;
    
    
    public InstructorResultsResponseRow(String giverDisplayableIdentifier, String giverTeam,
                                        String recipientDisplayableIdentifier, String recipientTeam,
                                        String displayableResponse, boolean isModerationsButtonShown,
                                        ModerationButton moderationButton) {
        this.giverDisplayableIdentifier = giverDisplayableIdentifier;
        this.giverTeam = giverTeam;
        this.recipientDisplayableIdentifier = recipientDisplayableIdentifier;
        this.recipientTeam = recipientTeam;
        this.displayableResponse = displayableResponse;
        this.isModerationsButtonDisplayed = isModerationsButtonShown;
        this.moderationButton = moderationButton;
    }
    
    public String getGiverDisplayableIdentifier() {
        return giverDisplayableIdentifier;
    }
    
    public String getGiverTeam() {
        return giverTeam;
    }
    
    public String getRecipientDisplayableIdentifier() {
        return recipientDisplayableIdentifier;
    }
    
    public String getRecipientTeam() {
        return recipientTeam;
    }
    
    public String getDisplayableResponse() {
        return displayableResponse;
    }
    
    public boolean isModerationsButtonDisplayed() {
        return isModerationsButtonDisplayed;
    }
    
    public ModerationButton getModerationButton() {
        return moderationButton;
    }

    public Url getGiverProfilePictureLink() {
        return giverProfilePictureLink;
    }

    public Url getRecipientProfilePictureLink() {
        return recipientProfilePictureLink;
    }

    public boolean isGiverProfilePictureDisplayed() {
        return isGiverProfilePictureDisplayed;
    }

    public boolean isRecipientProfilePictureDisplayed() {
        return isRecipientProfilePictureDisplayed;
    }
    
}
