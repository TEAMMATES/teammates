package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.ui.template.CommentRow;
import teammates.ui.template.CommentsForStudentsTable;
import teammates.ui.template.StudentProfile;

public class InstructorStudentRecordsPageData extends PageData {

    public StudentProfileAttributes spa; // used for testing admin message
    private String courseId;
    private String studentName;
    private String studentEmail;
    private String showCommentBox;
    private StudentProfile studentProfile;
    private List<CommentsForStudentsTable> commentsForStudentTable;
    private List<String> sessionNames;

    public InstructorStudentRecordsPageData(AccountAttributes account, StudentAttributes student,
                                            String courseId, String showCommentBox, StudentProfileAttributes spa,
                                            Map<String, List<CommentAttributes>> giverEmailToCommentsMap,
                                            Map<String, String> giverEmailToGiverNameMap,
                                            List<String> sessionNames, InstructorAttributes instructor) {
        super(account, student);
        this.courseId = courseId;
        this.studentName = student.name;
        this.studentEmail = student.email;
        this.showCommentBox = showCommentBox;
        if (spa != null) {
            this.spa = spa;
            String pictureUrl = getPictureUrl(spa.pictureKey);
            this.studentProfile = new StudentProfile(student.name, spa, pictureUrl);
        }
        this.commentsForStudentTable = new ArrayList<CommentsForStudentsTable>();
        int commentCount = 0;
        for (String giverEmail : giverEmailToCommentsMap.keySet()) {
            commentCount += giverEmailToCommentsMap.get(giverEmail).size();
        }
        for (String giverEmail : giverEmailToCommentsMap.keySet()) {
            addCommentsToTable(student, giverEmailToCommentsMap, giverEmailToGiverNameMap,
                               instructor, giverEmail, commentCount);
        }
        this.sessionNames = sessionNames;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public String getGoogleId() {
        return account.googleId;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getShowCommentBox() {
        return showCommentBox;
    }

    public StudentProfile getStudentProfile() {
        return studentProfile;
    }

    public List<CommentsForStudentsTable> getCommentsForStudentTable() {
        return commentsForStudentTable;
    }

    public List<String> getSessionNames() {
        return sessionNames;
    }

    private void addCommentsToTable(StudentAttributes student,
            Map<String, List<CommentAttributes>> giverEmailToCommentsMap,
            Map<String, String> giverEmailToGiverNameMap, InstructorAttributes instructor,
            String giverEmail, int totalNumOfComments) {
        List<CommentRow> commentDivs = new ArrayList<CommentRow>();
        List<CommentAttributes> comments = giverEmailToCommentsMap.get(giverEmail);
        String giverName = giverEmailToGiverNameMap.get(giverEmail);
        for (CommentAttributes comment : comments) {
            String recipientDetails = student.name + " (" + student.team + ", " + student.email + ")";
            String unsanitizedRecipientDetails = StringHelper.recoverFromSanitizedText(recipientDetails);
            CommentRow commentDiv = new CommentRow(comment, giverEmail, unsanitizedRecipientDetails);
            String whoCanSeeComment = getTypeOfPeopleCanViewComment(comment);
            commentDiv.setVisibilityIcon(whoCanSeeComment);
            if (giverEmail.equals("0You")
                || instructor.isAllowedForPrivilege(student.section,
                           Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS)) {
                commentDiv.setEditDeleteEnabled(false);
            }
            commentDiv.setNotFromCommentsPage(student.email);
            commentDiv.setNumComments(totalNumOfComments);
            commentDivs.add(commentDiv);
        }
        CommentsForStudentsTable commentsForStudent = new CommentsForStudentsTable(giverName, commentDivs);
        commentsForStudent.setInstructorAllowedToGiveComment(
                instructor.isAllowedForPrivilege(student.section,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS));
        commentsForStudentTable.add(commentsForStudent);
    }

}
