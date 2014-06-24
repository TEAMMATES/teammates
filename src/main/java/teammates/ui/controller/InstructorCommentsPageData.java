package teammates.ui.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;

public class InstructorCommentsPageData extends PageData {
    public static final String COMMENT_GIVER_NAME_THAT_COMES_FIRST = "0you";
    
    public Boolean isViewingDraft;
    public Boolean isDisplayArchive;
    public String courseId;
    public String courseName;
    public List<String> coursePaginationList;
    public Map<String, List<CommentAttributes>> comments;
    public String instructorEmail;
    public CourseRoster roster;
    public Map<String, FeedbackSessionResultsBundle> feedbackResultBundles;
    public String previousPageLink;
    public String nextPageLink;
    public int numberOfPendingComments = 0;
    
    public InstructorCommentsPageData(AccountAttributes account) {
        super(account);
    }
    
    public String removeBracketsForArrayString(String arrayString){
        return arrayString.substring(1, arrayString.length() - 1).trim();
    }
    
    public String getGiverName(String giverEmail){
        InstructorAttributes instructor = roster.getInstructorForEmail(giverEmail);
        String giverDisplay = giverEmail;
        if(giverEmail.equals(COMMENT_GIVER_NAME_THAT_COMES_FIRST)){
            giverDisplay = "You";
        } else if(instructor != null){
            String title = instructor.displayedName;
            if(!title.equals(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_TUTOR) &&
                    !title.equals(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_HELPER)){
                title = "Instructor";
            }
            giverDisplay = title + " " + instructor.name;
        }
        return giverDisplay;
    }
    
    public String getRecipientNames(Set<String> recipients){
        StringBuilder namesStringBuilder = new StringBuilder();
        int i = 0;
        for(String recipient : recipients){
            if(i == recipients.size() - 1 && recipients.size() > 1){
                namesStringBuilder.append("and ");
            }
            StudentAttributes student = roster.getStudentForEmail(recipient);
            if(courseId.equals(recipient)){ 
                namesStringBuilder.append("All students in this course, ");
            } else if(student != null){
                if(recipients.size() == 1){
                    namesStringBuilder.append(student.name 
                            + " (" + student.team + ", <a href=\"mailto:" + student.email + "\">" + student.email + "</a>), ");
                } else {
                    namesStringBuilder.append(student.name + ", ");
                }
            } else {
                namesStringBuilder.append(recipient + ", ");
            }
            i++;
        }
        String namesString = namesStringBuilder.toString();
        return removeEndComma(namesString);
    }
}
