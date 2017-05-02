package teammates.ui.template;

import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.util.TimeHelper;

public class FeedbackResponseCommentRow {
    private Long commentId;
    private String extraClass = "";
    private String giverDisplay;
    private String createdAt;
    private String editedAt;
    private String commentText;
    private String feedbackResponseId;
    private String courseId;
    private String questionId;
    private String feedbackSessionName;
    private String responseGiverName;
    private String responseRecipientName;

    private String showCommentToString;
    private String showGiverNameToString;
    private List<FeedbackParticipantType> showCommentTo;
    private List<FeedbackParticipantType> showGiverNameTo;
    private Map<FeedbackParticipantType, Boolean> responseVisibilities;

    private String whoCanSeeComment;
    private boolean withVisibilityIcon;
    private boolean withNotificationIcon;
    private boolean withLinkToCommentsPage;
    private String linkToCommentsPage;

    private boolean editDeleteEnabled;
    private boolean editDeleteEnabledOnlyOnHover;
    private boolean instructorAllowedToDelete;
    private boolean instructorAllowedToEdit;

    public FeedbackResponseCommentRow(FeedbackResponseCommentAttributes frc, String giverDisplay) {
        this.commentId = frc.getId();
        this.giverDisplay = giverDisplay;
        this.createdAt = TimeHelper.formatDateTimeForComments(frc.createdAt);
        this.editedAt = frc.getEditedAtText("Anonymous".equals(giverDisplay));
        this.commentText = frc.commentText.getValue();
    }

    // for editing / deleting comments
    public FeedbackResponseCommentRow(FeedbackResponseCommentAttributes frc, String giverDisplay,
                                      String giverName, String recipientName, String showCommentToString,
                                      String showGiverNameToString,
                                      Map<FeedbackParticipantType, Boolean> responseVisibilities) {
        this(frc, giverDisplay);
        setDataForAddEditDelete(frc, giverName, recipientName,
                                showCommentToString, showGiverNameToString, responseVisibilities);
    }

    // for adding comments
    public FeedbackResponseCommentRow(FeedbackResponseCommentAttributes frc,
                                      String giverName, String recipientName, String showCommentToString,
                                      String showGiverNameToString,
                                      Map<FeedbackParticipantType, Boolean> responseVisibilities) {
        setDataForAddEditDelete(frc, giverName, recipientName,
                                showCommentToString, showGiverNameToString, responseVisibilities);
        this.questionId = frc.feedbackQuestionId;
    }

    private void setDataForAddEditDelete(FeedbackResponseCommentAttributes frc,
            String giverName, String recipientName,
            String showCommentToString, String showGiverNameToString,
            Map<FeedbackParticipantType, Boolean> responseVisibilities) {
        this.responseGiverName = giverName;
        this.responseRecipientName = recipientName;

        this.showCommentTo = frc.showCommentTo;
        this.showGiverNameTo = frc.showGiverNameTo;

        this.responseVisibilities = responseVisibilities;

        // meta data for form
        this.feedbackResponseId = frc.feedbackResponseId;
        this.courseId = frc.courseId;
        this.feedbackSessionName = frc.feedbackSessionName;
        this.showCommentToString = showCommentToString;
        this.showGiverNameToString = showGiverNameToString;

    }

    public String getExtraClass() {
        return extraClass;
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

    public String getLinkToCommentsPage() {
        return linkToCommentsPage;
    }

    public String getFeedbackResponseId() {
        return feedbackResponseId;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getQuestionId() {
        return questionId;
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

    public String getShowCommentToString() {
        return showCommentToString;
    }

    public String getShowGiverNameToString() {
        return showGiverNameToString;
    }

    public String getWhoCanSeeComment() {
        return whoCanSeeComment;
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

    private boolean isResponseVisibleTo(FeedbackParticipantType type) {
        return responseVisibilities.containsKey(type) && responseVisibilities.get(type);
    }

    private boolean isShowCommentTo(FeedbackParticipantType type) {
        return showCommentTo.contains(type);
    }

    private boolean isShowGiverNameTo(FeedbackParticipantType type) {
        return showGiverNameTo.contains(type);
    }

    public boolean isResponseVisibleToRecipient() {
        return isResponseVisibleTo(FeedbackParticipantType.RECEIVER);
    }

    public boolean isResponseVisibleToGiverTeam() {
        return isResponseVisibleTo(FeedbackParticipantType.OWN_TEAM_MEMBERS);
    }

    public boolean isResponseVisibleToRecipientTeam() {
        return isResponseVisibleTo(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
    }

    public boolean isResponseVisibleToStudents() {
        return isResponseVisibleTo(FeedbackParticipantType.STUDENTS);
    }

    public boolean isResponseVisibleToInstructors() {
        return isResponseVisibleTo(FeedbackParticipantType.INSTRUCTORS);
    }

    public boolean isShowCommentToResponseGiver() {
        return isShowCommentTo(FeedbackParticipantType.GIVER);
    }

    public boolean isShowGiverNameToResponseGiver() {
        return isShowGiverNameTo(FeedbackParticipantType.GIVER);
    }

    public boolean isShowCommentToResponseRecipient() {
        return isShowCommentTo(FeedbackParticipantType.RECEIVER);
    }

    public boolean isShowGiverNameToResponseRecipient() {
        return isShowGiverNameTo(FeedbackParticipantType.RECEIVER);
    }

    public boolean isShowCommentToResponseGiverTeam() {
        return isShowCommentTo(FeedbackParticipantType.OWN_TEAM_MEMBERS);
    }

    public boolean isShowGiverNameToResponseGiverTeam() {
        return isShowGiverNameTo(FeedbackParticipantType.OWN_TEAM_MEMBERS);
    }

    public boolean isShowCommentToResponseRecipientTeam() {
        return isShowCommentTo(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
    }

    public boolean isShowGiverNameToResponseRecipientTeam() {
        return isShowGiverNameTo(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS);
    }

    public boolean isShowCommentToStudents() {
        return isShowCommentTo(FeedbackParticipantType.STUDENTS);
    }

    public boolean isShowGiverNameToStudents() {
        return isShowGiverNameTo(FeedbackParticipantType.STUDENTS);
    }

    public boolean isShowCommentToInstructors() {
        return isShowCommentTo(FeedbackParticipantType.INSTRUCTORS);
    }

    public boolean isShowGiverNameToInstructors() {
        return isShowGiverNameTo(FeedbackParticipantType.INSTRUCTORS);
    }

    public void setExtraClass(String extraClass) {
        this.extraClass = extraClass;
    }

    private void enableEditDelete() {
        this.editDeleteEnabled = true;
    }

    public void enableEdit() {
        enableEditDelete();
        this.instructorAllowedToEdit = true;
    }

    public void enableDelete() {
        enableEditDelete();
        this.instructorAllowedToDelete = true;
    }

    public void enableEditDeleteOnHover() {
        this.editDeleteEnabledOnlyOnHover = true;
    }

    public void enableVisibilityIcon(String whoCanSeeComment) {
        this.withVisibilityIcon = true;
        this.whoCanSeeComment = whoCanSeeComment;
    }

    public void enableNotificationIcon() {
        this.withNotificationIcon = true;
    }

    public FeedbackResponseCommentRow setLinkToCommentsPage(String linkToCommentsPage) {
        this.withLinkToCommentsPage = true;
        this.linkToCommentsPage = linkToCommentsPage;
        return this;
    }
}
