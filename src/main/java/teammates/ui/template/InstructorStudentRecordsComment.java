package teammates.ui.template;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentParticipantType;
import teammates.common.util.TimeHelper;

public class InstructorStudentRecordsComment {

    private String typeOfPeopleCanViewComment;
    private String courseId;
    private String studentName;
    private String studentEmail;
    private String googleId;
    private int numOfComments;
    
    private String commentText;
    private long commentId;
    private String commentCreatedAt;
    private CommentParticipantType recipientType;
    private String recipientsString;
    private String showCommentToString;
    private String showGiverNameToString;
    private String showRecipientNameToString;
    
    private String checkIfShowCommentToRecipient;
    private String checkIfShowCommentToTeam;
    private String checkIfShowCommentToSection;
    private String checkIfShowCommentToCourse;
    private String checkIfShowCommentToInstructor;
    private String checkIfShowGiverNameToRecipient;
    private String checkIfShowGiverNameToTeam;
    private String checkIfShowGiverNameToSection;
    private String checkIfShowGiverNameToCourse;
    private String checkIfShowGiverNameToInstructor;
    private String checkIfShowRecipientNameToTeam;
    private String checkIfShowRecipientNameToSection;
    private String checkIfShowRecipientNameToCourse;
    private String checkIfShowRecipientNameToInstructor;
    
    public InstructorStudentRecordsComment(CommentAttributes comment, String typeOfPeopleCanViewComment,
                                           String courseId, String studentName, String studentEmail,
                                           String googleId, int numOfComments) {
        this.typeOfPeopleCanViewComment = typeOfPeopleCanViewComment;
        this.courseId = courseId;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.googleId = googleId;
        this.numOfComments = numOfComments;

        this.commentText = comment.commentText.getValue();
        this.commentId = comment.getCommentId();
        this.commentCreatedAt = TimeHelper.formatTime(comment.createdAt);
        this.recipientType = comment.recipientType;
        this.recipientsString = removeBracketsForArrayString(comment.recipients.toString());
        this.showCommentToString = removeBracketsForArrayString(comment.showCommentTo.toString());
        this.showGiverNameToString = removeBracketsForArrayString(comment.showGiverNameTo.toString());
        this.showRecipientNameToString= removeBracketsForArrayString(comment.showRecipientNameTo.toString());

        this.checkIfShowCommentToRecipient = checkIfTrue(comment.showCommentTo.contains(CommentParticipantType.PERSON));
        this.checkIfShowCommentToTeam = checkIfTrue(comment.showCommentTo.contains(CommentParticipantType.TEAM));
        this.checkIfShowCommentToSection = checkIfTrue(comment.showCommentTo.contains(CommentParticipantType.SECTION));
        this.checkIfShowCommentToCourse = checkIfTrue(comment.showCommentTo.contains(CommentParticipantType.COURSE));
        this.checkIfShowCommentToInstructor = checkIfTrue(comment.showCommentTo.contains(CommentParticipantType.INSTRUCTOR));
        this.checkIfShowGiverNameToRecipient = checkIfTrue(comment.showGiverNameTo.contains(CommentParticipantType.PERSON));
        this.checkIfShowGiverNameToTeam = checkIfTrue(comment.showGiverNameTo.contains(CommentParticipantType.TEAM));
        this.checkIfShowGiverNameToSection = checkIfTrue(comment.showGiverNameTo.contains(CommentParticipantType.SECTION));
        this.checkIfShowGiverNameToCourse = checkIfTrue(comment.showGiverNameTo.contains(CommentParticipantType.COURSE));
        this.checkIfShowGiverNameToInstructor = checkIfTrue(comment.showGiverNameTo.contains(CommentParticipantType.INSTRUCTOR));
        this.checkIfShowRecipientNameToTeam = checkIfTrue(comment.showRecipientNameTo.contains(CommentParticipantType.TEAM));
        this.checkIfShowRecipientNameToSection = checkIfTrue(comment.showRecipientNameTo.contains(CommentParticipantType.SECTION));
        this.checkIfShowRecipientNameToCourse = checkIfTrue(comment.showRecipientNameTo.contains(CommentParticipantType.COURSE));
        this.checkIfShowRecipientNameToInstructor = checkIfTrue(comment.showRecipientNameTo.contains(CommentParticipantType.INSTRUCTOR));
    }

    public String getTypeOfPeopleCanViewComment() {
        return typeOfPeopleCanViewComment;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public String getGoogleId() {
        return googleId;
    }

    public int getNumOfComments() {
        return numOfComments;
    }

    public String getCommentText() {
        return commentText;
    }

    public String getCommentCreatedAt() {
        return commentCreatedAt;
    }

    public long getCommentId() {
        return commentId;
    }

    public CommentParticipantType getRecipientType() {
        return recipientType;
    }

    public String getRecipientsString() {
        return recipientsString;
    }

    public String getShowCommentToString() {
        return showCommentToString;
    }

    public String getShowGiverNameToString() {
        return showGiverNameToString;
    }

    public String getShowRecipientNameToString() {
        return showRecipientNameToString;
    }

    public String getCheckIfShowCommentToRecipient() {
        return checkIfShowCommentToRecipient;
    }

    public String getCheckIfShowCommentToTeam() {
        return checkIfShowCommentToTeam;
    }

    public String getCheckIfShowCommentToSection() {
        return checkIfShowCommentToSection;
    }

    public String getCheckIfShowCommentToCourse() {
        return checkIfShowCommentToCourse;
    }

    public String getCheckIfShowCommentToInstructor() {
        return checkIfShowCommentToInstructor;
    }

    public String getCheckIfShowGiverNameToRecipient() {
        return checkIfShowGiverNameToRecipient;
    }

    public String getCheckIfShowGiverNameToTeam() {
        return checkIfShowGiverNameToTeam;
    }

    public String getCheckIfShowGiverNameToSection() {
        return checkIfShowGiverNameToSection;
    }

    public String getCheckIfShowGiverNameToCourse() {
        return checkIfShowGiverNameToCourse;
    }

    public String getCheckIfShowGiverNameToInstructor() {
        return checkIfShowGiverNameToInstructor;
    }

    public String getCheckIfShowRecipientNameToTeam() {
        return checkIfShowRecipientNameToTeam;
    }

    public String getCheckIfShowRecipientNameToSection() {
        return checkIfShowRecipientNameToSection;
    }

    public String getCheckIfShowRecipientNameToCourse() {
        return checkIfShowRecipientNameToCourse;
    }

    public String getCheckIfShowRecipientNameToInstructor() {
        return checkIfShowRecipientNameToInstructor;
    }

    // TODO move this and the one in PageData to StringHelper
    private String removeBracketsForArrayString(String arrayString) {
        return arrayString.substring(1, arrayString.length() - 1).trim();
    }
    
    private String checkIfTrue(boolean bool) {
        return bool ? "checked=\"checked\"" : "";
    }

}
