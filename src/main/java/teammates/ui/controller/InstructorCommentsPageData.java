package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentParticipantType;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.logic.api.Logic;
import teammates.ui.template.CommentRow;
import teammates.ui.template.InstructorCommentsCommentRow;
import teammates.ui.template.CommentsForStudentsTable;
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
    // TODO: remove this field
    private String instructorEmail;
    private InstructorAttributes currentInstructor;
    private CourseRoster roster;
    private List<FeedbackSessionAttributes> feedbackSessions;
    private String previousPageLink;
    private String nextPageLink;
    private int numberOfPendingComments = 0;
    private Map<String, String> giverEmailToGiverNameMap;
    
    private List<CommentsForStudentsTable> commentsForStudentsTables;
    
    public InstructorCommentsPageData(AccountAttributes account) {
        super(account);
    }
    
    public void init(boolean isViewingDraft, boolean isDisplayArchive, String courseId, String courseName,
                     List<String> coursePaginationList, Map<String, List<CommentAttributes>> comments,
                     InstructorAttributes instructor, CourseRoster roster,
                     List<FeedbackSessionAttributes> feedbackSessions, int numberOfPendingComments) {
        this.isViewingDraft = isViewingDraft;
        this.isDisplayArchive = isDisplayArchive;
        this.courseId = courseId;
        this.courseName = courseName;
        this.coursePaginationList = coursePaginationList;
        this.comments = comments;
        this.instructorEmail = instructor != null ? instructor.email : "no-email";
        this.currentInstructor = instructor;
        this.roster = roster;
        this.feedbackSessions = feedbackSessions;
        this.previousPageLink = retrievePreviousPageLink();
        this.nextPageLink = retrieveNextPageLink();
        this.numberOfPendingComments = numberOfPendingComments;
        this.giverEmailToGiverNameMap = getGiverEmailToGiverNameMap();
    
        setCommentsForStudentsTables();
                                        
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
    
    public Map<String, String> getGiverEmailToGiverNameMap() {
        
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
    
    public List<CommentsForStudentsTable> getCommentsForStudentsTables() {
        return commentsForStudentsTables;
    }
    
    public boolean isDisplayArchive() {
        return isDisplayArchive;
    }
    
    public boolean isViewingDraft() {
        return isViewingDraft;
    }
    
    public boolean isInstructorAllowedToModifyCommentInSection(CommentAttributes comment) {
        return comment.giverEmail.equals(instructorEmail)
                       || (currentInstructor != null 
                               && isInstructorAllowedForPrivilegeOnComment(
                                          comment, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS,
                                          currentInstructor, courseId));
    }
        
    public boolean isInstructorAllowedForPrivilegeOnComment(CommentAttributes comment, String privilegeName,
                                                            InstructorAttributes instructor, String courseId) {
        // TODO: remember to come back and change this if later CommentAttributes.recipients can have multiple later!!!
        Logic logic = new Logic();
        if (instructor == null) {
            return false;
        }
        if (comment.recipientType == CommentParticipantType.COURSE) {
            return instructor.isAllowedForPrivilege(privilegeName);          
        } else if (comment.recipientType == CommentParticipantType.SECTION) {
            String section = "";
            if (!comment.recipients.isEmpty()) {
                Iterator<String> iterator = comment.recipients.iterator();
                section = iterator.next();
            }
            return instructor.isAllowedForPrivilege(section, privilegeName);
        } else if (comment.recipientType == CommentParticipantType.TEAM) {
            String team = "";
            String section = "";
            if (!comment.recipients.isEmpty()) {
                Iterator<String> iterator = comment.recipients.iterator();
                team = iterator.next();
            }
            List<StudentAttributes> students = logic.getStudentsForTeam(team, courseId);
            if (!students.isEmpty()) {
                section = students.get(0).section;
            }
            return instructor.isAllowedForPrivilege(section, privilegeName);
        } else if (comment.recipientType == CommentParticipantType.PERSON) {
            String studentEmail = "";
            String section = "";
            if (!comment.recipients.isEmpty()) {
                Iterator<String> iterator = comment.recipients.iterator();
                studentEmail = iterator.next();
            }
            StudentAttributes student = logic.getStudentForEmail(courseId, studentEmail);
            if (student != null) {
                section = student.section;
            }
            return instructor.isAllowedForPrivilege(section, privilegeName);
        } else {
            // TODO: implement this if instructor is later allowed to be added to recipients
            return false;
        }
    }

    private void setCommentsForStudentsTables() {
        commentsForStudentsTables = new ArrayList<CommentsForStudentsTable>();      
          
        for (String giverEmail : comments.keySet()) {
            commentsForStudentsTables
                    .add(new CommentsForStudentsTable(
                                 giverEmailToGiverNameMap.get(giverEmail), createCommentRows(giverEmail)));
        }
    }
    
    private List<CommentRow> createCommentRows(String giverEmail) {
        
        List<CommentRow> rows = new ArrayList<CommentRow>();
        for (CommentAttributes comment : comments.get(giverEmail)) {            
            String recipientDetails = getRecipientNames(comment.recipients);
            String creationTime = TimeHelper.formatTime(comment.createdAt);          
            
            Boolean isInstructorAllowedToModifyCommentInSection = isInstructorAllowedToModifyCommentInSection(comment);
            
            String typeOfPeopleCanViewComment = getTypeOfPeopleCanViewComment(comment);
            
            String editedAt = comment.getEditedAtTextForInstructor(
                                              giverEmailToGiverNameMap.get(giverEmail).equals("Anonymous"));
            
            String showCommentsTo = getShowCommentsToForComment(comment);
            
            String showGiverNameTo = getShowGiverNameToForComment(comment);
            
            String showRecipientNameTo = getShowRecipientNameToForComment(comment);
            
            VisibilityCheckboxes visibilityCheckboxes = createVisibilityCheckboxes(comment);
            
            rows.add(new InstructorCommentsCommentRow(giverEmail, comment, recipientDetails, creationTime, 
                                 isInstructorAllowedToModifyCommentInSection, typeOfPeopleCanViewComment, editedAt,
                                 visibilityCheckboxes, showCommentsTo, showGiverNameTo, showRecipientNameTo));
        }       
        return rows;
    }
    
    private VisibilityCheckboxes createVisibilityCheckboxes(CommentAttributes comment) {
        boolean isRecipientAbleToSeeComment = comment.showCommentTo.contains(CommentParticipantType.PERSON);
        boolean isRecipientAbleToSeeGiverName = comment.showGiverNameTo.contains(CommentParticipantType.PERSON);
        
        boolean isRecipientTeamAbleToSeeComment = comment.showCommentTo.contains(CommentParticipantType.TEAM);
        boolean isRecipientTeamAbleToSeeGiverName = comment.showGiverNameTo.contains(CommentParticipantType.TEAM);
        boolean isRecipientTeamAbleToSeeRecipientName = comment.showRecipientNameTo.contains(CommentParticipantType.TEAM);
        
        boolean isRecipientSectionAbleToSeeComment = comment.showCommentTo.contains(CommentParticipantType.SECTION);
        boolean isRecipientSectionAbleToSeeGiverName = comment.showGiverNameTo.contains(CommentParticipantType.SECTION);
        boolean isRecipientSectionAbleToSeeRecipientName = comment.showRecipientNameTo.contains(CommentParticipantType.SECTION);
        
        boolean isCourseStudentsAbleToSeeComment = comment.showCommentTo.contains(CommentParticipantType.COURSE);
        boolean isCourseStudentsAbleToSeeGiverName = comment.showGiverNameTo.contains(CommentParticipantType.COURSE);
        boolean isCourseStudentsAbleToSeeRecipientName = comment.showRecipientNameTo.contains(CommentParticipantType.COURSE);
        
        boolean isInstructorsAbleToSeeComment = comment.showCommentTo.contains(CommentParticipantType.INSTRUCTOR);
        boolean isInstructorsAbleToSeeGiverName = comment.showGiverNameTo.contains(CommentParticipantType.INSTRUCTOR);
        boolean isInstructorsAbleToSeeRecipientName = comment.showRecipientNameTo.contains(CommentParticipantType.INSTRUCTOR);
        
        return new VisibilityCheckboxes(isRecipientAbleToSeeComment, isRecipientAbleToSeeGiverName,
                isRecipientTeamAbleToSeeComment, isRecipientTeamAbleToSeeGiverName, isRecipientTeamAbleToSeeRecipientName,
                isRecipientSectionAbleToSeeComment, isRecipientSectionAbleToSeeGiverName, isRecipientSectionAbleToSeeRecipientName,
                isCourseStudentsAbleToSeeComment, isCourseStudentsAbleToSeeGiverName, isCourseStudentsAbleToSeeRecipientName,
                isInstructorsAbleToSeeComment, isInstructorsAbleToSeeGiverName, isInstructorsAbleToSeeRecipientName);
    }

}
