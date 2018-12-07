package teammates.ui.template;

import java.util.Comparator;
import java.util.List;

/**
 * Data model for Instructor Feedback Results view by question, view by...
 */
public class InstructorFeedbackResultsResponseRow {
    private ElementTag rowAttributes;
    private boolean isRowGrey;

    private boolean isGiverDisplayed = true;
    private String giverDisplayableIdentifier;
    private String giverTeam;

    private boolean isGiverProfilePictureAColumn;
    private String giverProfilePictureLink;

    private boolean isRecipientDisplayed = true;
    private String recipientDisplayableIdentifier;
    private String recipientTeam;

    private boolean isRecipientProfilePictureAColumn;
    private String recipientProfilePictureLink;

    private boolean isFeedbackParticipantCommentsOnResponsesAllowed;
    private String feedbackParticipantComment;

    private boolean isActionsDisplayed;

    private String displayableResponse;
    private InstructorFeedbackResultsModerationButton moderationButton;
    private FeedbackResponseCommentRow addCommentButton;

    private List<FeedbackResponseCommentRow> instructorComments;
    private int responseRecipientIndex;
    private int responseGiverIndex;
    private boolean isInstructorCommentsOnResponsesAllowed;

    public InstructorFeedbackResultsResponseRow(String giverDisplayableIdentifier, String giverTeam,
            String recipientDisplayableIdentifier, String recipientTeam, String displayableResponse,
            InstructorFeedbackResultsModerationButton moderationButton,
            boolean isFeedbackParticipantCommentsOnResponsesAllowed) {
        this(giverDisplayableIdentifier, giverTeam, recipientDisplayableIdentifier, recipientTeam,
                displayableResponse, moderationButton, false, isFeedbackParticipantCommentsOnResponsesAllowed);
    }

    public InstructorFeedbackResultsResponseRow(String giverDisplayableIdentifier, String giverTeam,
            String recipientDisplayableIdentifier, String recipientTeam, String displayableResponse,
            InstructorFeedbackResultsModerationButton moderationButton, boolean isRowGrey,
            boolean isFeedbackParticipantCommentsOnResponsesAllowed) {
        this.giverDisplayableIdentifier = giverDisplayableIdentifier;
        this.giverTeam = giverTeam;

        this.recipientDisplayableIdentifier = recipientDisplayableIdentifier;
        this.recipientTeam = recipientTeam;

        this.displayableResponse = displayableResponse;

        this.moderationButton = moderationButton;

        this.isRowGrey = isRowGrey;
        this.isFeedbackParticipantCommentsOnResponsesAllowed = isFeedbackParticipantCommentsOnResponsesAllowed;
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

    public String getFeedbackParticipantComment() {
        return feedbackParticipantComment;
    }

    public void setFeedbackParticipantComment(String feedbackParticipantComment) {
        this.feedbackParticipantComment = feedbackParticipantComment;
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

    public static List<InstructorFeedbackResultsResponseRow> sortListWithDefaultOrder(
            List<InstructorFeedbackResultsResponseRow> responseRows) {
        responseRows.sort(Comparator.comparing((InstructorFeedbackResultsResponseRow obj) -> obj.getGiverTeam())
                .thenComparing(obj -> obj.getGiverDisplayableIdentifier()));
        return responseRows;
    }

    public FeedbackResponseCommentRow getAddCommentButton() {
        return addCommentButton;
    }

    public void setAddCommentButton(FeedbackResponseCommentRow addCommentButton) {
        this.addCommentButton = addCommentButton;
    }

    public List<FeedbackResponseCommentRow> getInstructorComments() {
        return instructorComments;
    }

    public void setInstructorComments(List<FeedbackResponseCommentRow> instructorComments) {
        this.instructorComments = instructorComments;
    }

    public int getResponseRecipientIndex() {
        return responseRecipientIndex;
    }

    public void setResponseRecipientIndex(int responseRecipientIndex) {
        this.responseRecipientIndex = responseRecipientIndex;
    }

    public int getResponseGiverIndex() {
        return responseGiverIndex;
    }

    public void setResponseGiverIndex(int responseGiverIndex) {
        this.responseGiverIndex = responseGiverIndex;
    }

    public boolean isInstructorCommentsOnResponsesAllowed() {
        return isInstructorCommentsOnResponsesAllowed;
    }

    public void setInstructorCommentsOnResponsesAllowed(boolean isCommentsOnResponsesAllowed) {
        this.isInstructorCommentsOnResponsesAllowed = isCommentsOnResponsesAllowed;
    }

    public boolean isFeedbackParticipantCommentsOnResponsesAllowed() {
        return isFeedbackParticipantCommentsOnResponsesAllowed;
    }
}
