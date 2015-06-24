package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.TimeHelper;
import teammates.ui.template.CommentRow;
import teammates.ui.template.StudentCommentsCommentRow;

/**
 * PageData: the data used in the StudentCommentsPage
 */
public class StudentCommentsPageData extends PageData {

    private String courseId;
    private String courseName;
    private List<String> coursePaginationList;
    private List<CommentAttributes> comments;
    private CourseRoster roster;
    private String previousPageLink;
    private String nextPageLink;
    private String studentEmail;
    private Map<String, FeedbackSessionResultsBundle> feedbackResultBundles;
    
    List<CommentRow> commentRows;
    
    public StudentCommentsPageData(AccountAttributes account) {
        super(account);
    }
    
    public void init(String courseId, String courseName, List<String> coursePaginationList,
                     List<CommentAttributes> comments, CourseRoster roster, String previousPageLink,
                     String nextPageLink, String studentEmail,
                     Map<String, FeedbackSessionResultsBundle> feedbackResultBundles) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.coursePaginationList = coursePaginationList;
        this.comments = comments;
        this.roster = roster;
        this.previousPageLink = previousPageLink;
        this.nextPageLink = nextPageLink;
        this.studentEmail = studentEmail;
        this.feedbackResultBundles = feedbackResultBundles;
        
        setCommentRows();
    }
    
    public String getCourseId() {
        return courseId;
    }
    
    public String getCourseName() {
        return courseName;
    }
    
    public List<String> getCoursePaginationList() {
        return coursePaginationList;
    }
    
    public List<CommentAttributes> getComments() {
        return comments;
    }
    
    public CourseRoster getRoster() {
        return roster;
    }
    
    public String getPreviousPageLink() {
        return previousPageLink;
    }
    
    public String getNextPageLink() {
        return nextPageLink;
    }
    
    public String getStudentEmail() {
        return studentEmail;
    }
    
    public Map<String, FeedbackSessionResultsBundle> getFeedbackResultBundles() {
        return feedbackResultBundles;
    }
    
    public List<CommentRow> getCommentRows() {
        return commentRows;
    }
    
    public String getRecipientNames(Set<String> recipients){
        StringBuilder namesStringBuilder = new StringBuilder();
        int i = 0;
        for(String recipient : recipients){
            if(i == recipients.size() - 1 && recipients.size() > 1){
                namesStringBuilder.append("and ");
            }
            StudentAttributes student = roster.getStudentForEmail(recipient);
            if(recipient.equals(studentEmail)){
                namesStringBuilder.append("you, ");
            } else if(courseId.equals(recipient)){ 
                namesStringBuilder.append("All Students In This Course, ");
            } else if(student != null){
                namesStringBuilder.append(student.name + ", ");
            } else {
                namesStringBuilder.append(recipient + ", ");
            }
            i++;
        }
        String namesString = namesStringBuilder.toString();
        return removeEndComma(namesString);
    }
    
    private void setCommentRows() {
        commentRows = new ArrayList<CommentRow>();
        for (CommentAttributes comment : comments) {
            String recipientDetails = getRecipientNames(comment.recipients);
            
            InstructorAttributes instructor = roster.getInstructorForEmail(comment.giverEmail);
            String giverDetails = comment.giverEmail;
            if(instructor != null){
                giverDetails = instructor.displayedName + " " + instructor.name;
            }
            String lastEditorDisplay = null;
            if (comment.lastEditorEmail != null) {
                 InstructorAttributes lastEditor = roster.getInstructorForEmail(comment.lastEditorEmail);
                 lastEditorDisplay = lastEditor.displayedName + " " + lastEditor.name;
            }
            String creationTime = TimeHelper.formatDate(comment.createdAt);
            String editedAt = comment.getEditedAtTextForStudent(giverDetails.equals("Anonymous"), lastEditorDisplay);
            CommentRow commentRow = 
                    new StudentCommentsCommentRow(
                                giverDetails, comment, recipientDetails, creationTime, editedAt);
            commentRows.add(commentRow);
        }
    }
}
