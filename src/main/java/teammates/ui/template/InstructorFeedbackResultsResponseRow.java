package teammates.ui.template;


/**
 * Data model for Instructor Feedback Results view by question, view by...
 */
public class InstructorFeedbackResultsResponseRow {
    private ElementTag rowAttributes;
    private boolean isRowGrey;
    
    private boolean isGiverDisplayed = true;
    private String giverDisplayableIdentifier;
    private String giverTeam;
    
    private boolean isGiverProfilePictureAColumn = false;
    private String giverProfilePictureLink;
    
    private boolean isRecipientDisplayed = true;
    private String recipientDisplayableIdentifier;
    private String recipientTeam;
    
    private boolean isRecipientProfilePictureAColumn = false;
    private String recipientProfilePictureLink;
    
    private boolean isActionsDisplayed;
    
    private String displayableResponse;
    private InstructorFeedbackResultsModerationButton moderationButton;
    
    
    public InstructorFeedbackResultsResponseRow(String giverDisplayableIdentifier, String giverTeam,
                                        String recipientDisplayableIdentifier, String recipientTeam,
                                        String displayableResponse, 
                                        InstructorFeedbackResultsModerationButton moderationButton) {
        this(giverDisplayableIdentifier, giverTeam, recipientDisplayableIdentifier, recipientTeam,
             displayableResponse, moderationButton, false);
    }
    
    public InstructorFeedbackResultsResponseRow(String giverDisplayableIdentifier, String giverTeam,
                                        String recipientDisplayableIdentifier, String recipientTeam,
                                        String displayableResponse, 
                                        InstructorFeedbackResultsModerationButton moderationButton, boolean isRowGrey) {
        this.giverDisplayableIdentifier = giverDisplayableIdentifier;
        this.giverTeam = giverTeam;
        
        this.recipientDisplayableIdentifier = recipientDisplayableIdentifier;
        this.recipientTeam = recipientTeam;
        
        this.displayableResponse = displayableResponse;
        
        this.moderationButton = moderationButton;
        
        this.isRowGrey = isRowGrey;
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
    
    
    public InstructorFeedbackResultsModerationButton getModerationButton() {
        return moderationButton;
    }

    public String getGiverProfilePictureLink() {
        return giverProfilePictureLink;
    }

    public String getRecipientProfilePictureLink() {
        return recipientProfilePictureLink;
    }

    public boolean isRowGrey() {
        return isRowGrey;
    }

    public void setGiverProfilePictureLink(String giverProfilePictureLink) {
        this.giverProfilePictureLink = giverProfilePictureLink;
                                                                       
    }

    public void setRecipientProfilePictureLink(String recipientProfilePictureLink) {
        this.recipientProfilePictureLink = recipientProfilePictureLink;
    }

    public ElementTag getRowAttributes() {
        return rowAttributes;
    }

    public void setRowAttributes(ElementTag rowAttributes) {
        this.rowAttributes = rowAttributes;
    }

    public boolean isGiverDisplayed() {
        return isGiverDisplayed;
    }

    public void setGiverDisplayed(boolean isGiverDisplayed) {
        this.isGiverDisplayed = isGiverDisplayed;
    }

    public boolean isGiverProfilePictureAColumn() {
        return isGiverProfilePictureAColumn;
    }

    public void setGiverProfilePictureAColumn(boolean isGiverProfilePictureAColumn) {
        this.isGiverProfilePictureAColumn = isGiverProfilePictureAColumn;
    }

    public boolean isRecipientDisplayed() {
        return isRecipientDisplayed;
    }

    public void setRecipientDisplayed(boolean isRecipientDisplayed) {
        this.isRecipientDisplayed = isRecipientDisplayed;
    }

    public boolean isRecipientProfilePictureAColumn() {
        return isRecipientProfilePictureAColumn;
    }

    public void setRecipientProfilePictureAColumn(boolean isRecipientProfilePictureAColumn) {
        this.isRecipientProfilePictureAColumn = isRecipientProfilePictureAColumn;
    }

    public boolean isActionsDisplayed() {
        return isActionsDisplayed;
    }

    public void setActionsDisplayed(boolean isActionsDisplayed) {
        this.isActionsDisplayed = isActionsDisplayed;
    }
    
    
}