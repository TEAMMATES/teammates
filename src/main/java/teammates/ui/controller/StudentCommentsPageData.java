package teammates.ui.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.StudentAttributes;

/**
 * PageData: the data used in the StudentCommentsPage
 */
public class StudentCommentsPageData extends PageData {

    public String courseId;
    public String courseName;
    public List<String> coursePaginationList;
    public List<CommentAttributes> comments;
    public CourseRoster roster;
    public String previousPageLink;
    public String nextPageLink;
    public String studentEmail;
    public Map<String, FeedbackSessionResultsBundle> feedbackResultBundles;
    
    public StudentCommentsPageData(AccountAttributes account) {
        super(account);
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
}
