package teammates.ui.template;

import teammates.common.datatransfer.CommentParticipantType;
import teammates.common.datatransfer.attributes.CommentAttributes;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;

public class CommentRow {

    private CommentAttributes comment;
    private String giverDisplay;
    private String recipientDisplay;

    private String extraClass = "";

    private boolean withVisibilityIcon;
    private String whoCanSeeComment;

    private boolean withNotificationIcon;

    private boolean withLinkToCommentsPage;
    private String linkToCommentsPage;

    private boolean editDeleteEnabled;
    private boolean editDeleteEnabledOnlyOnHover;
    private boolean fromCommentsPage;
    private String studentEmail;
    private int numComments;

    public CommentRow(CommentAttributes comment, String giverDisplay, String recipientDisplay) {
        this.comment = comment;
        this.giverDisplay = giverDisplay;
        this.recipientDisplay = recipientDisplay;
    }

    public String getCreatedAt() {
        return TimeHelper.formatDateTimeForComments(comment.createdAt);
    }

    public String getEditedAt() {
        return comment.getEditedAtText(giverDisplay.startsWith("Anonymous"));
    }

    public String getCommentText() {
        return comment.commentText.getValue();
    }

    public String getRecipientDisplay() {
        return recipientDisplay;
    }

    public String getExtraClass() {
        return extraClass;
    }

    public void withExtraClass(String extraClass) {
        this.extraClass = " " + extraClass;
    }

    public boolean isWithVisibilityIcon() {
        return withVisibilityIcon;
    }

    public void setVisibilityIcon(String whoCanSeeComment) {
        if (!whoCanSeeComment.isEmpty()) {
            this.withVisibilityIcon = true;
            this.whoCanSeeComment = whoCanSeeComment;
        }
    }

    public String getWhoCanSeeComment() {
        return whoCanSeeComment;
    }

    public boolean isWithNotificationIcon() {
        return withNotificationIcon;
    }

    public void setNotificationIcon(boolean isPendingNotification) {
        this.withNotificationIcon = isPendingNotification;
    }

    public boolean isWithLinkToCommentsPage() {
        return withLinkToCommentsPage;
    }

    public void withLinkToCommentsPage(String linkToCommentsPage) {
        this.withLinkToCommentsPage = true;
        this.linkToCommentsPage = linkToCommentsPage;
    }

    public String getLinkToCommentsPage() {
        return linkToCommentsPage;
    }

    public boolean isEditDeleteEnabled() {
        return editDeleteEnabled;
    }

    public void setEditDeleteEnabled(boolean isEditDeleteEnabledOnlyOnHover) {
        this.editDeleteEnabled = true;
        this.editDeleteEnabledOnlyOnHover = isEditDeleteEnabledOnlyOnHover;
    }

    public boolean isEditDeleteEnabledOnlyOnHover() {
        return editDeleteEnabledOnlyOnHover;
    }

    public boolean isFromCommentsPage() {
        return fromCommentsPage;
    }

    public void setFromCommentsPage() {
        this.fromCommentsPage = true;
    }

    public void setNotFromCommentsPage(String studentEmail) {
        this.fromCommentsPage = false;
        this.studentEmail = studentEmail;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public CommentParticipantType getRecipientType() {
        return comment.recipientType;
    }

    public String getRecipientsString() {
        return StringHelper.removeEnclosingSquareBrackets(comment.recipients.toString());
    }

    public int getNumComments() {
        return numComments;
    }

    public void setNumComments(int numComments) {
        this.numComments = numComments;
    }

    public void setPlaceholderNumComments() {
        this.numComments = 0;
    }

    public Long getCommentId() {
        return comment.getCommentId();
    }

    public String getCourseId() {
        return comment.courseId;
    }

    public boolean isCommentForPerson() {
        return comment.recipientType.equals(CommentParticipantType.PERSON);
    }

    public boolean isCommentForTeam() {
        return comment.recipientType.equals(CommentParticipantType.TEAM);
    }

    public boolean isCommentForSection() {
        return comment.recipientType.equals(CommentParticipantType.SECTION);
    }

    public boolean isCommentForCourse() {
        return comment.recipientType.equals(CommentParticipantType.COURSE);
    }

    public String getShowCommentToString() {
        return StringHelper.removeEnclosingSquareBrackets(comment.showCommentTo.toString());
    }

    public String getShowGiverNameToString() {
        return StringHelper.removeEnclosingSquareBrackets(comment.showGiverNameTo.toString());
    }

    public String getShowRecipientNameToString() {
        return StringHelper.removeEnclosingSquareBrackets(comment.showRecipientNameTo.toString());
    }

    public boolean isShowCommentToRecipient() {
        return comment.showCommentTo.contains(CommentParticipantType.PERSON);
    }

    public boolean isShowGiverNameToRecipient() {
        return comment.showGiverNameTo.contains(CommentParticipantType.PERSON);
    }

    public boolean isShowCommentToRecipientTeam() {
        return comment.showCommentTo.contains(CommentParticipantType.TEAM);
    }

    public boolean isShowGiverNameToRecipientTeam() {
        return comment.showGiverNameTo.contains(CommentParticipantType.TEAM);
    }

    public boolean isShowRecipientNameToRecipientTeam() {
        return comment.showRecipientNameTo.contains(CommentParticipantType.TEAM);
    }

    public boolean isShowCommentToRecipientSection() {
        return comment.showCommentTo.contains(CommentParticipantType.SECTION);
    }

    public boolean isShowGiverNameToRecipientSection() {
        return comment.showGiverNameTo.contains(CommentParticipantType.SECTION);
    }

    public boolean isShowRecipientNameToRecipientSection() {
        return comment.showRecipientNameTo.contains(CommentParticipantType.SECTION);
    }

    public boolean isShowCommentToCourse() {
        return comment.showCommentTo.contains(CommentParticipantType.COURSE);
    }

    public boolean isShowGiverNameToCourse() {
        return comment.showGiverNameTo.contains(CommentParticipantType.COURSE);
    }

    public boolean isShowRecipientNameToCourse() {
        return comment.showRecipientNameTo.contains(CommentParticipantType.COURSE);
    }

    public boolean isShowCommentToInstructors() {
        return comment.showCommentTo.contains(CommentParticipantType.INSTRUCTOR);
    }

    public boolean isShowGiverNameToInstructors() {
        return comment.showGiverNameTo.contains(CommentParticipantType.INSTRUCTOR);
    }

    public boolean isShowRecipientNameToInstructors() {
        return comment.showRecipientNameTo.contains(CommentParticipantType.INSTRUCTOR);
    }

}
