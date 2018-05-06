package teammates.ui.template;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.TimeHelper;

public class FeedbackResponseCommentRow {
    private Long commentId;
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

    private String showCommentToString;
    private String showGiverNameToString;
    private List<FeedbackParticipantType> showCommentTo;
    private List<FeedbackParticipantType> showGiverNameTo;
    private Map<FeedbackParticipantType, Boolean> responseVisibilities;
    private Map<String, String> instructorEmailNameTable;

    private String whoCanSeeComment;
    private ZoneId sessionTimeZone;

    private boolean hasVisibilityIcon;

    private boolean isEditDeleteEnabled;

    public FeedbackResponseCommentRow(FeedbackResponseCommentAttributes frc, String giverDisplay,
            Map<String, String> instructorEmailNameTable, ZoneId sessionTimeZone) {
        this.instructorEmailNameTable = instructorEmailNameTable;
        this.commentId = frc.getId();
        this.giverDisplay = giverDisplay;
        this.sessionTimeZone = sessionTimeZone;
        this.createdAt = TimeHelper.formatDateTimeForDisplay(frc.createdAt, this.sessionTimeZone);
        this.commentText = frc.commentText.getValue();

        //TODO TO REMOVE AFTER DATA MIGRATION
        this.commentGiverName = SanitizationHelper.desanitizeIfHtmlSanitized(getCommentGiverNameFromEmail(giverDisplay));
        this.editedAt = getEditedAtText(frc.lastEditorEmail, frc.createdAt, frc.lastEditedAt);
    }

    public FeedbackResponseCommentRow(FeedbackResponseCommentAttributes frc, String giverDisplay,
            String giverName, String recipientName, String showCommentToString, String showGiverNameToString,
            Map<FeedbackParticipantType, Boolean> responseVisibilities, Map<String, String> instructorEmailNameTable,
            ZoneId sessionTimeZone) {
        this(frc, giverDisplay, instructorEmailNameTable, sessionTimeZone);
        setDataForAddEditDelete(frc, giverName, recipientName,
                showCommentToString, showGiverNameToString, responseVisibilities);
    }

    // for adding comments
    public FeedbackResponseCommentRow(FeedbackResponseCommentAttributes frc,
            String giverName, String recipientName, String showCommentToString, String showGiverNameToString,
            Map<FeedbackParticipantType, Boolean> responseVisibilities, ZoneId sessionTimeZone) {
        setDataForAddEditDelete(frc, giverName, recipientName,
                showCommentToString, showGiverNameToString, responseVisibilities);
        this.questionId = frc.feedbackQuestionId;
        this.sessionTimeZone = sessionTimeZone;
    }

    private void setDataForAddEditDelete(FeedbackResponseCommentAttributes frc, String giverName, String recipientName,
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

    public void enableEditDelete() {
        this.isEditDeleteEnabled = true;
    }

    public void setVisibilityIcon(boolean hasVisibilityIcon, String whoCanSeeComment) {
        this.hasVisibilityIcon = hasVisibilityIcon;
        this.whoCanSeeComment = whoCanSeeComment;
    }

    public String getCommentGiverName() {
        return commentGiverName;
    }

    private String getCommentGiverNameFromEmail(String giverEmail) {
        if (Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT.equals(giverEmail)) {
            return Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT;
        }
        return instructorEmailNameTable.get(giverEmail);
    }

    private String getEditedAtText(String lastEditorEmail, Instant createdAt, Instant lastEditedAt) {
        if (lastEditedAt == null || lastEditedAt.equals(createdAt)) {
            return "";
        }
        boolean isGiverAnonymous = Const.DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT.equals(commentGiverName);
        return "(last edited "
                + (isGiverAnonymous
                    ? ""
                    : "by " + SanitizationHelper.sanitizeForHtml(instructorEmailNameTable.get(lastEditorEmail)) + " ")
                + "at " + TimeHelper.formatDateTimeForDisplay(lastEditedAt, sessionTimeZone) + ")";
    }
}
