package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.ui.template.Comment;
import teammates.ui.template.CommentsForStudentsTable;

/**
 * PageData: the data to be used in the InstructorCommentsPage
 */
public class InstructorCommentsPageData extends PageData {
    public static final String COMMENT_GIVER_NAME_THAT_COMES_FIRST = "0you";
    
    private boolean isViewingDraft;
    private boolean isDisplayArchive;
    private String courseId;
    private String courseName;
    private List<String> coursePaginationList;
    private Map<String, List<CommentAttributes>> comments;
    private Map<String, List<Boolean>> commentModifyPermissions;
    private CourseRoster roster;
    private List<FeedbackSessionAttributes> feedbackSessions;
    private String previousPageLink;
    private String nextPageLink;
    private int numberOfPendingComments = 0;
    
    private List<CommentsForStudentsTable> commentsForStudentsTables;
    
    public InstructorCommentsPageData(AccountAttributes account) {
        super(account);
    }
    
    public void init(boolean isViewingDraft, boolean isDisplayArchive, String courseId, String courseName,
                     List<String> coursePaginationList, Map<String, List<CommentAttributes>> comments,
                     Map<String, List<Boolean>> commentModifyPermissions, CourseRoster roster, 
                     List<FeedbackSessionAttributes> feedbackSessions, int numberOfPendingComments) {
        this.isViewingDraft = isViewingDraft;
        this.isDisplayArchive = isDisplayArchive;
        this.courseId = courseId;
        this.courseName = courseName;
        this.coursePaginationList = coursePaginationList;
        this.comments = comments;
        this.commentModifyPermissions = commentModifyPermissions;
        this.roster = roster;
        this.feedbackSessions = feedbackSessions;
        this.previousPageLink = retrievePreviousPageLink();
        this.nextPageLink = retrieveNextPageLink();
        this.numberOfPendingComments = numberOfPendingComments;
    
        setCommentsForStudentsTables();
                                        
    }

    public Map<String, List<CommentAttributes>> getComments() {
        return comments;
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
    
    public List<FeedbackSessionAttributes> getFeedbackSessions() {
        return feedbackSessions;
    }
    
    public String getNextPageLink() {
        return nextPageLink;
    }
    
    public int getNumberOfPendingComments() {
        return numberOfPendingComments;
    }
    
    public String getPreviousPageLink() {
        return previousPageLink;
    }
    
    public String getRecipientNames(Set<String> recipients) {
        StringBuilder namesStringBuilder = new StringBuilder();
        int i = 0;
        for (String recipient : recipients) {
            if (i == recipients.size() - 1 && recipients.size() > 1) {
                namesStringBuilder.append("and ");
            }
            StudentAttributes student = roster.getStudentForEmail(recipient);
            if (courseId.equals(recipient)) { 
                namesStringBuilder.append("All students in this course, ");
            } else if (student != null) {
                if (recipients.size() == 1) {
                    namesStringBuilder.append(student.name + " (" + student.team + ", " + student.email + "), ");
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
    
    public List<CommentsForStudentsTable> getCommentsForStudentsTables() {
        return commentsForStudentsTables;
    }
    
    public boolean isDisplayArchive() {
        return isDisplayArchive;
    }
    
    public boolean isViewingDraft() {
        return isViewingDraft;
    }        

    private void setCommentsForStudentsTables() {
        Map<String, String> giverEmailToGiverNameMap = getGiverEmailToGiverNameMap();
        commentsForStudentsTables = new ArrayList<CommentsForStudentsTable>();      
          
        for (String giverEmail : comments.keySet()) {
            String giverName = giverEmailToGiverNameMap.get(giverEmail);
            CommentsForStudentsTable table = new CommentsForStudentsTable(giverName,
                                                                          createCommentRows(giverEmail, giverName));
            String extraClass;
            if (giverEmail.equals(COMMENT_GIVER_NAME_THAT_COMES_FIRST)) {
                extraClass = "giver_display-by-you";
            } else {
                extraClass = "giver_display-by-others";
            }
            table.withExtraClass(extraClass);
            commentsForStudentsTables.add(table);
        }
    }
    
    private List<Comment> createCommentRows(String giverEmail, String giverName) {
        
        List<Comment> rows = new ArrayList<Comment>();
        List<CommentAttributes> commentsForGiver = comments.get(giverEmail);
        for (int i = 0; i < commentsForGiver.size(); i++) {            
            CommentAttributes comment = commentsForGiver.get(i);
            String recipientDetails = getRecipientNames(comment.recipients);
            Boolean isInstructorAllowedToModifyCommentInSection = commentModifyPermissions.get(giverEmail).get(i);
            String typeOfPeopleCanViewComment = getTypeOfPeopleCanViewComment(comment);
            Comment commentDiv = new Comment(comment, giverName, recipientDetails);
            String extraClass;
            if (comment.showCommentTo.isEmpty()) {
                extraClass = "status_display-private";
            } else {
                extraClass = "status_display-public";
            }
            commentDiv.withExtraClass(extraClass);
            commentDiv.setVisibilityIcon(typeOfPeopleCanViewComment);
            commentDiv.setNotificationIcon(comment.isPendingNotification());
            if (isInstructorAllowedToModifyCommentInSection) {
                commentDiv.setEditDeleteEnabled(true);
                commentDiv.setFromCommentsPage();
                commentDiv.setPlaceholderNumComments();
            }
            
            rows.add(commentDiv);
        }       
        return rows;
    }
    
    private String retrievePreviousPageLink() {
        int courseIdx = coursePaginationList.indexOf(courseId);
        String previousPageLink = "javascript:;";
        if (courseIdx >= 1) {
            previousPageLink = getInstructorCommentsLink() + "&courseid=" + coursePaginationList.get(courseIdx - 1);
        }
        return previousPageLink;
    }

    private String retrieveNextPageLink() {
        int courseIdx = coursePaginationList.indexOf(courseId);
        String nextPageLink = "javascript:;";
        if (courseIdx < coursePaginationList.size() - 1) {
            nextPageLink = getInstructorCommentsLink() + "&courseid=" + coursePaginationList.get(courseIdx + 1);
        }
        return nextPageLink;
    }

    private Map<String, String> getGiverEmailToGiverNameMap() {
        
        Map<String, String> giverEmailToGiverNameMap = new HashMap<String, String>();
        for (String giverEmail : comments.keySet()) {
            
            InstructorAttributes instructor = roster.getInstructorForEmail(giverEmail);
            String giverDisplay = giverEmail;
            if (giverEmail.equals(InstructorCommentsPageData.COMMENT_GIVER_NAME_THAT_COMES_FIRST)) {
                giverDisplay = "You";
            } else if (instructor != null) {
                String title = instructor.displayedName;
                giverDisplay = title + " " + instructor.name;
            }
            
            giverEmailToGiverNameMap.put(giverEmail, giverDisplay);
        }
        return giverEmailToGiverNameMap;
    }

}
