package teammates.ui.template;

import teammates.common.util.Url;

/**
 * Data model for Instructor Feedback Results view by question, view by...
 *
 */
public class InstructorResultsResponseRow {
    private ElementTag rowAttributes;
    private boolean isRowGrey;
    
    private boolean isGiverDisplayed = true;
    private String giverDisplayableIdentifier;
    private String giverTeam;
    private boolean isGiverProfilePictureDisplayed;
    private boolean isGiverProfilePictureAColumn = false;
    private Url giverProfilePictureLink;
    
    private boolean isRecipientDisplayed = true;
    private String recipientDisplayableIdentifier;
    private String recipientTeam;
    private boolean isRecipientProfilePictureDisplayed;
    private boolean isRecipientProfilePictureAColumn = false;
    private Url recipientProfilePictureLink;
    
    
    private String displayableResponse;
    private boolean isModerationsButtonDisplayed;
    private ModerationButton moderationButton;
    
    
    public InstructorResultsResponseRow(String giverDisplayableIdentifier, String giverTeam,
                                        String recipientDisplayableIdentifier, String recipientTeam,
                                        String displayableResponse, boolean isModerationsButtonShown,
                                        ModerationButton moderationButton) {
        this(giverDisplayableIdentifier, giverTeam, recipientDisplayableIdentifier, recipientTeam,
             displayableResponse, isModerationsButtonShown, moderationButton, false);
    }
    
    public InstructorResultsResponseRow(String giverDisplayableIdentifier, String giverTeam,
                                        String recipientDisplayableIdentifier, String recipientTeam,
                                        String displayableResponse, boolean isModerationsButtonShown,
                                        ModerationButton moderationButton, boolean isRowGrey) {
        this.giverDisplayableIdentifier = giverDisplayableIdentifier;
        this.giverTeam = giverTeam;
        this.recipientDisplayableIdentifier = recipientDisplayableIdentifier;
        this.recipientTeam = recipientTeam;
        this.displayableResponse = displayableResponse;
        this.isModerationsButtonDisplayed = isModerationsButtonShown;
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

    public boolean isRowGrey() {
        return isRowGrey;
    }

    public void setGiverProfilePictureDisplayed(boolean isGiverProfilePictureDisplayed) {
        this.isGiverProfilePictureDisplayed = isGiverProfilePictureDisplayed;
    }

    public void setGiverProfilePictureLink(Url giverProfilePictureLink) {
        this.giverProfilePictureLink = giverProfilePictureLink;
    }

    public void setRecipientProfilePictureDisplayed(boolean isRecipientProfilePictureDisplayed) {
        this.isRecipientProfilePictureDisplayed = isRecipientProfilePictureDisplayed;
    }

    public void setRecipientProfilePictureLink(Url recipientProfilePictureLink) {
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
    
    
}