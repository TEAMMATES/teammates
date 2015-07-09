package teammates.ui.template;

import java.util.List;

import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackParticipantType;

public class FeedbackResponseComment {
    private Long commentId;
    private String extraClass = "";
    private String giverDisplay;
    private String createdAt;
    private String editedAt;
    private String commentText;
    private String feedbackResponseId;
    private String courseId;
    private String feedbackSessionName;
    private String responseGiverName;
    private String responseRecipientName;
    private String showCommentToString;
    private String showGiverNameToString;
    private String linkToCommentsPage;
    private List<FeedbackParticipantType> showCommentTo;
    private List<FeedbackParticipantType> showGiverNameTo;
    private boolean withVisibilityIcon;
    private boolean withNotificationIcon;
    private boolean withLinkToCommentsPage;
    private boolean editDeleteEnabled;
    private boolean editDeleteEnabledOnlyOnHover;
    private boolean instructorAllowedToDelete;
    private boolean instructorAllowedToEdit;
    private boolean responseVisibleToRecipient;
    private boolean responseVisibleToGiverTeam;
    private boolean responseVisibleToRecipientTeam;
    private boolean responseVisibleToStudents;
    private boolean responseVisibleToInstructors;

    public FeedbackResponseComment(FeedbackResponseCommentAttributes frc, String giverDisplay) {
        this.commentId = frc.getId();
        this.giverDisplay = giverDisplay;
        this.createdAt = frc.createdAt.toString();
        this.editedAt = frc.getEditedAtText(giverDisplay.equals("Anonymous"));
        this.commentText = frc.commentText.getValue();
        this.feedbackResponseId = frc.feedbackResponseId;
        this.courseId = frc.courseId;
        this.feedbackSessionName = frc.feedbackSessionName;
        this.showCommentTo = frc.showCommentTo;
        this.showGiverNameTo = frc.showGiverNameTo;
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

    public void setInstructorAllowedToDelete(boolean isInstructorAllowedToDelete) {
        this.instructorAllowedToDelete = isInstructorAllowedToDelete;
    }
    
    public boolean isInstructorAllowedToDelete() {
        return instructorAllowedToDelete;
    }

    public void setInstructorAllowedToEdit(boolean isInstructorAllowedToEdit) {
        this.instructorAllowedToEdit = isInstructorAllowedToEdit;
    }

    public boolean isInstructorAllowedToEdit() {
        return instructorAllowedToEdit;
    }

    public void setEditDeleteEnabled(boolean isEditDeleteEnabled) {
        this.editDeleteEnabled = isEditDeleteEnabled;
    }

    public boolean isResponseVisibleToRecipient() {
        return responseVisibleToRecipient;
    }

    public void setResponseVisibleToRecipient(boolean isResponseVisibleToRecipient) {
        this.responseVisibleToRecipient = isResponseVisibleToRecipient;
    }

    public boolean isResponseVisibleToGiverTeam() {
        return responseVisibleToGiverTeam;
    }

    public void setResponseVisibleToGiverTeam(boolean isResponseVisibleToGiverTeam) {
        this.responseVisibleToGiverTeam = isResponseVisibleToGiverTeam;
    }

    public boolean isResponseVisibleToRecipientTeam() {
        return responseVisibleToRecipientTeam;
    }

    public void setResponseVisibleToRecipientTeam(boolean isResponseVisibleToRecipientTeam) {
        this.responseVisibleToRecipientTeam = isResponseVisibleToRecipientTeam;
    }

    public boolean isResponseVisibleToStudents() {
        return responseVisibleToStudents;
    }

    public void setResponseVisibleToStudents(boolean isResponseVisibleToStudents) {
        this.responseVisibleToStudents = isResponseVisibleToStudents;
    }

    public boolean isResponseVisibleToInstructors() {
        return responseVisibleToInstructors;
    }

    public void setResponseVisibleToInstructors(boolean isResponseVisibleToInstructors) {
        this.responseVisibleToInstructors = isResponseVisibleToInstructors;
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

    public String getResponseGiverName() {
        return responseGiverName;
    }

    public String getResponseRecipientName() {
        return responseRecipientName;
    }

    public void setResponseGiverName(String giverName) {
        this.responseGiverName = giverName;
    }

    public void setResponseRecipientName(String recipientName) {
        this.responseRecipientName = recipientName;
    }

    public String getShowCommentToString() {
        return showCommentToString;
    }

    public void setShowCommentToString(String showCommentToString) {
        this.showCommentToString = showCommentToString;
    }

    public String getShowGiverNameToString() {
        return showGiverNameToString;
    }

    public void setShowGiverNameToString(String showGiverNameToString) {
        this.showGiverNameToString = showGiverNameToString;
    }

    public boolean isShowCommentToResponseGiver() {
        return showCommentTo.contains(FeedbackParticipantType.GIVER);
    }

    public boolean contains() {
        return showGiverNameTo.contains(FeedbackParticipantType.GIVER);
    }

    public boolean isShowCommentToResponseRecipient() {
        return showCommentTo.contains(FeedbackParticipantType.RECEIVER);
    }

    public boolean isShowGiverNameToResponseRecipient() {
        return showGiverNameTo.contains(FeedbackParticipantType.RECEIVER);
    }

    public boolean isShowCommentToResponseGiverTeam() {
        return showCommentTo.contains(FeedbackParticipantType.OWN_TEAM_MEMBERS);
    }

    public boolean isShowGiverNameToResponseGiverTeam() {
        return showGiverNameTo.contains(FeedbackParticipantType.OWN_TEAM_MEMBERS);
    }

    public boolean isShowCommentToResponseRecipientTeam() {
        return showCommentTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
    }

    public boolean isShowGiverNameToResponseRecipientTeam() {
        return showGiverNameTo.contains(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
    }

    public boolean isShowCommentToStudents() {
        return showCommentTo.contains(FeedbackParticipantType.STUDENTS);
    }

    public boolean isShowGiverNameToStudents() {
        return showGiverNameTo.contains(FeedbackParticipantType.STUDENTS);
    }

    public boolean isShowCommentToInstructors() {
        return showCommentTo.contains(FeedbackParticipantType.INSTRUCTORS);
    }

    public boolean isShowGiverNameToInstructors() {
        return showGiverNameTo.contains(FeedbackParticipantType.INSTRUCTORS);
    }
}
