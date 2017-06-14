package teammates.ui.template;

import java.util.Date;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.util.Logger;
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
    private String commentGiverName;
    private String commentLastEditorName;

    private String showCommentToString;
    private String showGiverNameToString;
    private List<FeedbackParticipantType> showCommentTo;
    private List<FeedbackParticipantType> showGiverNameTo;
    private Map<FeedbackParticipantType, Boolean> responseVisibilities;
    private Map<String, String> instructorEmailNameTable;

    private String whoCanSeeComment;

    private boolean hasVisibilityIcon;

    private boolean isEditDeleteEnabled;
    private boolean isEditDeleteEnabledOnlyOnHover;
    private boolean isInstructorAllowedToDelete;
    private boolean isInstructorAllowedToEdit;

    public FeedbackResponseCommentRow(FeedbackResponseCommentAttributes frc, String giverDisplay) {
        this.commentId = frc.getId();
        this.giverDisplay = giverDisplay;
        this.createdAt = TimeHelper.formatDateTimeForComments(frc.createdAt);
        this.commentText = frc.commentText.getValue();
    }
    //For student feedback Results page.
    public FeedbackResponseCommentRow(FeedbackResponseCommentAttributes frc, String giverDisplay,
            Map<String, String> instructorEmailNameTable) {
        this.instructorEmailNameTable = instructorEmailNameTable;
        this.commentId = frc.getId();
        this.giverDisplay = giverDisplay;
        this.createdAt = TimeHelper.formatDateTimeForComments(frc.createdAt);
        this.commentText = frc.commentText.getValue();
        setCommentGiverName(giverDisplay);
        setCommentLastEditorName(frc.lastEditorEmail);
        this.editedAt = setEditedAtText(frc.createdAt, frc.lastEditedAt);
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

    public FeedbackResponseCommentRow(
            FeedbackResponseCommentAttributes frc,
            Map<String, String> instructorEmailNameTable, String giverDisplay,
            String giverName, String recipientName,
            String showCommentToString,
            String showGiverNameToString,
            Map<FeedbackParticipantType, Boolean> responseVisibilities) {
         
        this(frc, giverDisplay);
        setDataForAddEditDelete(frc, giverName, recipientName,
                                showCommentToString, showGiverNameToString, responseVisibilities);
        this.instructorEmailNameTable = instructorEmailNameTable;
        setCommentGiverName(giverDisplay);
        setCommentLastEditorName(frc.lastEditorEmail);
        this.questionId = frc.feedbackQuestionId;
        this.editedAt = setEditedAtText(frc.createdAt, frc.lastEditedAt);
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
        return hasVisibilityIcon;
    }

    public boolean isEditDeleteEnabled() {
        return isEditDeleteEnabled;
    }

    public boolean isEditDeleteEnabledOnlyOnHover() {
        return isEditDeleteEnabledOnlyOnHover;
    }

    public boolean isInstructorAllowedToDelete() {
        return isInstructorAllowedToDelete;
    }

    public boolean isInstructorAllowedToEdit() {
        return isInstructorAllowedToEdit;
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
        this.isEditDeleteEnabled = true;
    }

    public void enableEdit() {
        enableEditDelete();
        this.isInstructorAllowedToEdit = true;
    }

    public void enableDelete() {
        enableEditDelete();
        this.isInstructorAllowedToDelete = true;
    }

    public void enableEditDeleteOnHover() {
        this.isEditDeleteEnabledOnlyOnHover = true;
    }

    public void enableVisibilityIcon(String whoCanSeeComment) {
        this.hasVisibilityIcon = true;
        this.whoCanSeeComment = whoCanSeeComment;
    }
    
    public String getCommentGiverName() {
        return commentGiverName;
    }
    
    public String getCommentLastEditorName() {
        return commentLastEditorName;
    }
    
    public void setCommentGiverName(String giverEmail) {
        if(giverEmail.equals("Anonymous")) {
            this.commentGiverName = "Anonymous";
            return;
        }
        this.commentGiverName = instructorEmailNameTable.get(giverEmail);
    }
    
    public void setCommentLastEditorName(String lastEditorEmail) {
        if(lastEditorEmail.equals("Anonymous")) {
            this.commentLastEditorName = "Anonymous";
            Logger log = Logger.getLogger();
            log.info(commentLastEditorName);
            return;
        }
        this.commentLastEditorName = instructorEmailNameTable.get(lastEditorEmail);
    }
    
    public String setEditedAtText(Date createdAt, Date lastEditedAt) {
        if(lastEditedAt == null || lastEditedAt.equals(createdAt)) {
            return "";
        }
        boolean isGiverAnonymous = commentLastEditorName.equals("Anonymous");
        return "(last edited "
        + (isGiverAnonymous ? "" : "by " + commentLastEditorName + " ")
        + "at " + TimeHelper.formatDateTimeForComments(lastEditedAt) + ")";
    }
}
