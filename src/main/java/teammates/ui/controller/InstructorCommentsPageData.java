package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentParticipantType;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.logic.api.Logic;
import teammates.ui.template.CommentRow;
import teammates.ui.template.InstructorCommentsCommentRow;
import teammates.ui.template.SearchCommentsForStudentsTable;
import teammates.ui.template.VisibilityCheckboxes;

/**
 * PageData: the data to be used in the InstructorCommentsPage
 */
public class InstructorCommentsPageData extends PageData {
    public static final String COMMENT_GIVER_NAME_THAT_COMES_FIRST = "0you";
    
    public Boolean isViewingDraft;
    public Boolean isDisplayArchive;
    public String courseId;
    public String courseName;
    public List<String> coursePaginationList;
    public Map<String, List<CommentAttributes>> comments;
    // TODO: remove this field
    public String instructorEmail;
    public InstructorAttributes currentInstructor;
    public CourseRoster roster;
    public List<FeedbackSessionAttributes> feedbackSessions;
    public String previousPageLink;
    public String nextPageLink;
    public int numberOfPendingComments = 0;
    public Map<String, String> giverEmailToGiverNameMap;
    
    private List<SearchCommentsForStudentsTable> commentsForStudentsTables;
    
    public InstructorCommentsPageData(AccountAttributes account) {
        super(account);
    }
    
    public Set<String> getCommentsKeySet() {
        return comments.keySet();
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
    
    public List<SearchCommentsForStudentsTable> getCommentsForStudentsTables() {
        return commentsForStudentsTables;
    }
    
    public boolean isDisplayArchive() {
        return isDisplayArchive;
    }
    
    public boolean isResponseCommentPublicToRecipient(FeedbackResponseCommentAttributes comment) {
        return comment.showCommentTo.size() > 0;
    }
    
    public boolean isViewingDraft() {
        return isViewingDraft;
    }
    
    public boolean isInstructorAllowedToModifyCommentInSection(CommentAttributes comment) {
        return comment.giverEmail.equals(instructorEmail)
                       || (currentInstructor != null 
                               && isInstructorAllowedForPrivilegeOnComment(
                                               comment, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS));
    }
        
    public boolean isInstructorAllowedForPrivilegeOnComment(CommentAttributes comment, String privilegeName) {
        // TODO: remember to come back and change this if later CommentAttributes.recipients can have multiple later!!!
        Logic logic = new Logic();
        if (this.currentInstructor == null) {
            return false;
        }
        if (comment.recipientType == CommentParticipantType.COURSE) {
            return this.currentInstructor.isAllowedForPrivilege(privilegeName);          
        } else if (comment.recipientType == CommentParticipantType.SECTION) {
            String section = "";
            if (!comment.recipients.isEmpty()) {
                Iterator<String> iterator = comment.recipients.iterator();
                section = iterator.next();
            }
            return this.currentInstructor.isAllowedForPrivilege(section, privilegeName);
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
            return this.currentInstructor.isAllowedForPrivilege(section, privilegeName);
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
            return this.currentInstructor.isAllowedForPrivilege(section, privilegeName);
        } else {
            // TODO: implement this if instructor is later allowed to be added to recipients
            return false;
        }
    }

    public void init(Boolean isViewingDraft, Boolean isDisplayArchive, String courseId,
                                    String courseName, List<String> coursePaginationList,
                                    Map<String, List<CommentAttributes>> comments,
                                    String string, InstructorAttributes instructor, CourseRoster roster,
                                    List<FeedbackSessionAttributes> feedbackSessions,
                                    String previousPageLink, String nextPageLink,
                                    int numberOfPendingComments,
                                    Map<String, String> giverEmailToGiverNameMap) {
        this.isViewingDraft = isViewingDraft;
        this.isDisplayArchive = isDisplayArchive;
        this.courseId = courseId;
        this.courseName = courseName;
        this.coursePaginationList = coursePaginationList;
        this.comments = comments;
        this.currentInstructor = instructor;
        this.roster = roster;
        this.feedbackSessions = feedbackSessions;
        this.previousPageLink = previousPageLink;
        this.nextPageLink = nextPageLink;
        this.numberOfPendingComments = numberOfPendingComments;
        this.giverEmailToGiverNameMap = giverEmailToGiverNameMap;

        setCommentsForStudentsTables();
                                        
    }

    private void setCommentsForStudentsTables() {
        commentsForStudentsTables = new ArrayList<SearchCommentsForStudentsTable>();      
          
        for (String giverEmail : comments.keySet()) {
            commentsForStudentsTables.add(new SearchCommentsForStudentsTable(
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
            
            String editedAt = comment.getEditedAtTextForInstructor(giverEmailToGiverNameMap.get(giverEmail).equals("Anonymous"));
            
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
