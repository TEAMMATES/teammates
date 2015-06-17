package teammates.ui.template;

/**
 * Data model for InstructorFeedback Results view by question, view by...
 *
 */
public class InstructorResultsResponseRow {
    
    private String giverDisplayableIdentifier;
    private String giverTeam;
    
    private String recipientDisplayableIdentifier;
    private String recipientTeam;
    
    private String displayableResponse;
    
    private boolean isModerationsButtonShown;
    private ModerationsButton moderationButton;
    
    public InstructorResultsResponseRow(String giverDisplayableIdentifier, String giverTeam,
                                        String recipientDisplayableIdentifier, String recipientTeam,
                                        String displayableResponse, boolean isModerationsButtonShown,
                                        ModerationsButton moderationButton) {
        this.giverDisplayableIdentifier = giverDisplayableIdentifier;
        this.giverTeam = giverTeam;
        this.recipientDisplayableIdentifier = recipientDisplayableIdentifier;
        this.recipientTeam = recipientTeam;
        this.displayableResponse = displayableResponse;
        this.isModerationsButtonShown = isModerationsButtonShown;
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
    
    public boolean isModerationsButtonShown() {
        return isModerationsButtonShown;
    }
    
    public ModerationsButton getModerationButton() {
        return moderationButton;
    }

}
