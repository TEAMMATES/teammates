package teammates.ui.template;

import teammates.common.datatransfer.FeedbackResponseCommentAttributes;

public class FeedbackResponseComment {

    private String extraClass = "";

    private Long commentId;
    private String giverDisplay;
    private String createdAt;
    private String editedAt;
    private String commentText;
    private String feedbackResponseId;
    private String courseId;
    private String feedbackSessionName;

    private boolean withVisibilityIcon = false;

    private boolean withNotificationIcon = false;

    private boolean withLinkToCommentsPage = false;
    private String linkToCommentsPage;

    private boolean editDeleteEnabled = false;
    private boolean editDeleteEnabledOnlyOnHover = false;
    private boolean instructorAllowedToDelete = false;
    private boolean instructorAllowedToEdit = false;

    public FeedbackResponseComment(FeedbackResponseCommentAttributes frc, String giverDisplay) {
        this.commentId = frc.getId();
        this.giverDisplay = giverDisplay;
        this.createdAt = frc.createdAt.toString();
        this.editedAt = frc.getEditedAtText(giverDisplay.equals("Anonymous"));
        this.commentText = frc.commentText.getValue();
        this.feedbackResponseId = frc.feedbackResponseId;
        this.courseId = frc.courseId;
        this.feedbackSessionName = frc.feedbackSessionName;
    }

    public String getExtraClass() {
        return extraClass;
    }

    public void setExtraClass(String extraClass) {
        this.extraClass = extraClass;
    }

    public Long getCommentId() {
        return commentId;
    }

    public String getGiverDisplay() {
        return giverDisplay;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getEditedAt() {
        return editedAt;
    }

    public String getCommentText() {
        return commentText;
    }

    public boolean isWithVisibilityIcon() {
        return withVisibilityIcon;
    }

    public boolean isWithNotificationIcon() {
        return withNotificationIcon;
    }

    public boolean isWithLinkToCommentsPage() {
        return withLinkToCommentsPage;
    }

    public FeedbackResponseComment setLinkToCommentsPage(String linkToCommentsPage) {
        this.withLinkToCommentsPage = true;
        this.linkToCommentsPage = linkToCommentsPage;
        return this;
    }

    public String getLinkToCommentsPage() {
        return linkToCommentsPage;
    }

    public boolean isEditDeleteEnabled() {
        return editDeleteEnabled;
    }

    public boolean isEditDeleteEnabledOnlyOnHover() {
        return editDeleteEnabledOnlyOnHover;
    }
    
    public boolean isInstructorAllowedToDelete() {
        return instructorAllowedToDelete;
    }

    public boolean isInstructorAllowedToEdit() {
        return instructorAllowedToEdit;
    }

    public void setEditDeleteEnabled(boolean isSettable) {
        this.editDeleteEnabled = isSettable;
    }

    public String getFeedbackResponseId() {
        return feedbackResponseId;   
    }

    public String getCourseId() {
        return courseId;
    }

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }
}
