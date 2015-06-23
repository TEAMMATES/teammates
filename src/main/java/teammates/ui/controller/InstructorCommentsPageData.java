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
import teammates.common.util.TimeHelper;
import teammates.ui.template.CommentRow;
import teammates.ui.template.InstructorCommentsCommentRow;
import teammates.ui.template.InstructorCommentsForStudentsTable;
import teammates.ui.template.VisibilityCheckboxes;

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
    
    private List<InstructorCommentsForStudentsTable> commentsForStudentsTables;
    
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
                namesStringBuilder.append("<b>All students in this course</b>, ");
            } else if (student != null) {
                if (recipients.size() == 1) {
                    namesStringBuilder.append("<b>" + student.name + "</b>" 
                            + " (" + student.team + ", <a href=\"mailto:" + student.email + "\">" + student.email + "</a>), ");
                } else {
                    namesStringBuilder.append("<b>" + student.name + "</b>" + ", ");
                }
            } else {
                namesStringBuilder.append("<b>" + recipient + "</b>" + ", ");
            }
            i++;
        }
        String namesString = namesStringBuilder.toString();
        return removeEndComma(namesString);
    }
    
    public String getShowCommentsToForComment(CommentAttributes comment) {
        return removeBracketsForArrayString(comment.showCommentTo.toString());
    }
    
    public String getShowGiverNameToForComment(CommentAttributes comment) {
        return removeBracketsForArrayString(comment.showGiverNameTo.toString());
    }
    
    public String getShowRecipientNameToForComment(CommentAttributes comment) {
        return removeBracketsForArrayString(comment.showRecipientNameTo.toString());
    }
    
    public List<InstructorCommentsForStudentsTable> getCommentsForStudentsTables() {
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
        commentsForStudentsTables = new ArrayList<InstructorCommentsForStudentsTable>();      
          
        for (String giverEmail : comments.keySet()) {
            String giverName = giverEmailToGiverNameMap.get(giverEmail);
            commentsForStudentsTables
                    .add(new InstructorCommentsForStudentsTable(
                                 giverEmail, giverName, createCommentRows(giverEmail, giverName)));
        }
    }
    
    private List<CommentRow> createCommentRows(String giverEmail, String giverName) {
        
        List<CommentRow> rows = new ArrayList<CommentRow>();
        List<CommentAttributes> commentsForGiver = comments.get(giverEmail);
        for (int i = 0; i < commentsForGiver.size(); i++) {            
            String recipientDetails = getRecipientNames(commentsForGiver.get(i).recipients);
            String creationTime = TimeHelper.formatTime(commentsForGiver.get(i).createdAt);          
            Boolean isInstructorAllowedToModifyCommentInSection = commentModifyPermissions.get(giverEmail).get(i);
            String typeOfPeopleCanViewComment = getTypeOfPeopleCanViewComment(commentsForGiver.get(i));
            String editedAt = commentsForGiver.get(i).getEditedAtTextForInstructor(giverName.equals("Anonymous"));
            String showCommentsTo = getShowCommentsToForComment(commentsForGiver.get(i));
            String showGiverNameTo = getShowGiverNameToForComment(commentsForGiver.get(i));
            String showRecipientNameTo = getShowRecipientNameToForComment(commentsForGiver.get(i));
            VisibilityCheckboxes visibilityCheckboxes = createVisibilityCheckboxes(commentsForGiver.get(i));
            
            rows.add(new InstructorCommentsCommentRow(giverEmail, commentsForGiver.get(i), recipientDetails, creationTime, 
                                 isInstructorAllowedToModifyCommentInSection, typeOfPeopleCanViewComment, editedAt,
                                 visibilityCheckboxes, showCommentsTo, showGiverNameTo, showRecipientNameTo));
        }       
        return rows;
    }
    
    private VisibilityCheckboxes createVisibilityCheckboxes(CommentAttributes comment) {
        return new VisibilityCheckboxes(comment);
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
