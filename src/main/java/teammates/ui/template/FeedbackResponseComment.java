package teammates.ui.template;

import java.util.List;
import java.util.Map;

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
    private String questionId;
    private String feedbackSessionName;
    private String responseGiverName;
    private String responseRecipientName;
    private String showCommentToString;
    private String showGiverNameToString;
    private String linkToCommentsPage;
    private String whoCanSeeComment;
    private List<FeedbackParticipantType> showCommentTo;
    private List<FeedbackParticipantType> showGiverNameTo;
    private boolean withVisibilityIcon;
    private boolean withNotificationIcon;
    private boolean withLinkToCommentsPage;
    private boolean editDeleteEnabled;
    private boolean editDeleteEnabledOnlyOnHover;
    private boolean instructorAllowedToDelete;
    private boolean instructorAllowedToEdit;
    private Map<FeedbackParticipantType, Boolean> responseVisibilities;

    public FeedbackResponseComment(FeedbackResponseCommentAttributes frc, String giverDisplay) {
        this.commentId = frc.getId();
        this.giverDisplay = giverDisplay;
        this.createdAt = frc.createdAt.toString();
        this.editedAt = frc.getEditedAtText(isAnonymous(giverDisplay));
        this.commentText = frc.commentText.getValue();
    }

    // Used in InstructorFeedbackResponseComment which is part of 
    // InstructorFeedbackResponseCommentsLoadPageData
    public FeedbackResponseComment(FeedbackResponseCommentAttributes frc, String giverDisplay,
                                   String giverName, String recipientName, String extraClass,
                                   boolean withVisibilityIcon, boolean withNotificationIcon, String whoCanSeeComment,
                                   String showCommentToString, String showGiverNameToString,
                                   boolean isAllowedToEditAndDeleteComment, boolean editDeleteEnabledOnlyOnHover,
                                   Map<FeedbackParticipantType, Boolean> responseVisiblities) {
        this.commentId = frc.getId();
        this.createdAt = frc.createdAt.toString();
        this.editedAt = frc.getEditedAtText(isAnonymous(giverDisplay));
        this.feedbackResponseId = frc.feedbackResponseId;
        this.courseId = frc.courseId;
        this.feedbackSessionName = frc.feedbackSessionName;
        
        this.giverDisplay = giverDisplay;
        this.responseGiverName = giverName;
        this.responseRecipientName = recipientName;
        this.commentText = frc.commentText.getValue();
        
        this.extraClass = extraClass;
        this.withVisibilityIcon = withVisibilityIcon;
        this.whoCanSeeComment = whoCanSeeComment;
        this.withNotificationIcon = withNotificationIcon;
        
        this.whoCanSeeComment = whoCanSeeComment;
        this.showCommentTo = frc.showCommentTo;
        this.showGiverNameTo = frc.showGiverNameTo;
        this.showCommentToString = showCommentToString;
        this.showGiverNameToString = showGiverNameToString;
        this.editDeleteEnabledOnlyOnHover = editDeleteEnabledOnlyOnHover;
        this.editDeleteEnabled = isAllowedToEditAndDeleteComment;
        this.instructorAllowedToEdit = this.instructorAllowedToDelete = this.editDeleteEnabled;
        
        this.responseVisibilities = responseVisiblities;
    }

    // Used in InstructorFeedbackResponseCommentAjaxPageData for instructorFeedbackResponseCommentsAdd.jsp
    public FeedbackResponseComment(FeedbackResponseCommentAttributes frc, String giverDisplay, 
                                   String giverName, String recipientName, String showCommentToString,
                                   String showGiverNameToString, Map<FeedbackParticipantType, Boolean> responseVisiblities,
                                   boolean isEditDeleteEnabled, boolean isInstructorAllowedToDelete,
                                   boolean isInstructorAllowedToEdit) {
        this.commentId = frc.getId();
        this.giverDisplay = giverDisplay;
        this.createdAt = frc.createdAt.toString();
        this.editedAt = frc.getEditedAtText(isAnonymous(giverDisplay));
        this.commentText = frc.commentText.getValue();
        this.feedbackResponseId = frc.feedbackResponseId;
        this.courseId = frc.courseId;
        this.feedbackSessionName = frc.feedbackSessionName;
        this.showCommentTo = frc.showCommentTo;
        this.showGiverNameTo = frc.showGiverNameTo;
        this.responseGiverName = giverName;
        this.responseRecipientName = recipientName;
        this.showCommentToString = showCommentToString;
        this.showGiverNameToString = showGiverNameToString;;
        this.responseVisibilities = responseVisiblities;
        this.editDeleteEnabled = isEditDeleteEnabled;
        this.instructorAllowedToDelete = isInstructorAllowedToDelete;
        this.instructorAllowedToEdit = isInstructorAllowedToEdit;
    }
    
    // for adding comments
    public FeedbackResponseComment(FeedbackResponseCommentAttributes frc,
                                   String giverName, String recipientName,String showCommentToString,
                                   String showGiverNameToString, Map<FeedbackParticipantType, Boolean> responseVisiblities,
                                   List<FeedbackParticipantType> showCommentTo, List<FeedbackParticipantType> showGiverNameTo,
                                   boolean isAddEnabled) {
        this.questionId = frc.feedbackQuestionId;
        this.feedbackResponseId = frc.feedbackResponseId;
        this.courseId = frc.courseId;
        this.feedbackSessionName = frc.feedbackSessionName;
        this.responseGiverName = giverName;
        this.responseRecipientName = recipientName;
        this.showCommentToString = showCommentToString;
        this.showGiverNameToString = showGiverNameToString;
        this.responseVisibilities = responseVisiblities;
        this.showCommentTo = showCommentTo;
        this.showGiverNameTo = showGiverNameTo;
    }

    private boolean isAnonymous(String participant) {
        return participant.equals("Anonymous");
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

    public FeedbackResponseComment setLinkToCommentsPage(String linkToCommentsPage) {
        this.withLinkToCommentsPage = true;
        this.linkToCommentsPage = linkToCommentsPage;
        return this;
    }
}
