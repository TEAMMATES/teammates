package teammates.ui.pagedata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CommentAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.ui.template.CommentRow;
import teammates.ui.template.CommentsForStudentsTable;
import teammates.ui.template.StudentProfile;

public class InstructorStudentRecordsPageData extends PageData {
    public static final String COMMENT_GIVER_NAME_THAT_COMES_FIRST = "0you";

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
        for (Map.Entry<String, List<CommentAttributes>> entry : giverEmailToCommentsMap.entrySet()) {
            commentCount += entry.getValue().size();
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
        List<CommentAttributes> comments = giverEmailToCommentsMap.get(giverEmail);
        if (!comments.isEmpty() || COMMENT_GIVER_NAME_THAT_COMES_FIRST.equals(giverEmail)) {
            String giverName = giverEmailToGiverNameMap.get(giverEmail);
            List<CommentRow> commentDivs = generateCommentRows(student, instructor, giverEmail,
                                                               totalNumOfComments, comments);

            CommentsForStudentsTable commentsForStudent = new CommentsForStudentsTable(giverName, commentDivs);
            commentsForStudent.setInstructorAllowedToGiveComment(
                    instructor.isAllowedForPrivilege(student.section,
                    Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS));
            commentsForStudent.setIsRepresentingSelf(Const.DISPLAYED_NAME_FOR_SELF_IN_COMMENTS.equals(giverName));
            commentsForStudentTable.add(commentsForStudent);
        }
    }

    /**
     * Generates the comment rows for a specific giver,
     * based on the current instructor's privilege to modify comments.
     * @return A list of comment rows for comments from giverEmail.
     */
    private List<CommentRow> generateCommentRows(StudentAttributes student, InstructorAttributes instructor,
            String giverEmail, int totalNumOfComments, List<CommentAttributes> comments) {
        List<CommentRow> commentDivs = new ArrayList<CommentRow>();

        for (CommentAttributes comment : comments) {
            String recipientDetails = student.name + " (" + student.team + ", " + student.email + ")";
            String unsanitizedRecipientDetails = SanitizationHelper.desanitizeFromHtml(recipientDetails);
            CommentRow commentDiv = new CommentRow(comment, giverEmail, unsanitizedRecipientDetails);
            String whoCanSeeComment = getTypeOfPeopleCanViewComment(comment);
            commentDiv.setVisibilityIcon(whoCanSeeComment);

            boolean canModifyComment = COMMENT_GIVER_NAME_THAT_COMES_FIRST.equals(giverEmail)
                                       || instructor.isAllowedForPrivilege(student.section,
                                                  Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS);

            if (canModifyComment) {
                commentDiv.setEditDeleteEnabled(false);
            }
            commentDiv.setNotFromCommentsPage(student.email);
            commentDiv.setNumComments(totalNumOfComments);
            commentDivs.add(commentDiv);
        }

        return commentDivs;
    }

}
