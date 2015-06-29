package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackQuestionDetails;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.TimeHelper;
import teammates.ui.template.CommentRow;
import teammates.ui.template.FeedbackResponseCommentRow;
import teammates.ui.template.FeedbackSessionRow;
import teammates.ui.template.QuestionTable;
import teammates.ui.template.ResponseRow;
import teammates.ui.template.StudentCommentsCommentRow;
import teammates.ui.template.StudentCommentsFeedbackResponseCommentRow;

/**
 * PageData: the data used in the StudentCommentsPage
 */
public class StudentCommentsPageData extends PageData {

    private String courseId;
    private String courseName;
    private List<String> coursePaginationList;
    private List<CommentAttributes> comments;
    private String previousPageLink;
    private String nextPageLink;
    
    private List<CommentRow> commentRows;
    private List<FeedbackSessionRow> feedbackSessionRows;
    
    public StudentCommentsPageData(AccountAttributes account) {
        super(account);
    }
    
    public void init(String courseId, String courseName, List<String> coursePaginationList,
                     List<CommentAttributes> comments, CourseRoster roster, String studentEmail,
                     Map<String, FeedbackSessionResultsBundle> feedbackResultBundles) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.coursePaginationList = coursePaginationList;
        this.comments = comments;
        this.previousPageLink = retrievePreviousPageLink();
        this.nextPageLink = retrieveNextPageLink();
        
        setCommentRows(studentEmail, roster);
        createFeedbackSessionRows(feedbackResultBundles, roster);
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
    
    public String getPreviousPageLink() {
        return previousPageLink;
    }
    
    public String getNextPageLink() {
        return nextPageLink;
    }
    
    public List<CommentRow> getCommentRows() {
        return commentRows;
    }
    
    public List<FeedbackSessionRow> getFeedbackSessionRows() {
        return feedbackSessionRows;
    }
    
    private String retrievePreviousPageLink() {
        int courseIdx = coursePaginationList.indexOf(courseId);
        String previousPageLink = "javascript:;";
        if (courseIdx >= 1) {
            previousPageLink = getStudentCommentsLink() + "&courseid=" + coursePaginationList.get(courseIdx - 1);
        }
        return previousPageLink;
    }

    private String retrieveNextPageLink() {
        int courseIdx = coursePaginationList.indexOf(courseId);
        String nextPageLink = "javascript:;";
        if (courseIdx < coursePaginationList.size() - 1) {
            nextPageLink = getStudentCommentsLink() + "&courseid=" + coursePaginationList.get(courseIdx + 1);
        }
        return nextPageLink;
    }
    
    private String getRecipientNames(Set<String> recipients, String studentEmail, CourseRoster roster) {
        StringBuilder namesStringBuilder = new StringBuilder();
        int i = 0;
        
        for (String recipient : recipients) {
            if (i == recipients.size() - 1 && recipients.size() > 1) {
                namesStringBuilder.append("and ");
            }
            StudentAttributes student = roster.getStudentForEmail(recipient);
            if (recipient.equals(studentEmail)) {
                namesStringBuilder.append("you, ");
            } else if (courseId.equals(recipient)) { 
                namesStringBuilder.append("All Students In This Course, ");
            } else if (student != null) {
                namesStringBuilder.append(student.name + ", ");
            } else {
                namesStringBuilder.append(recipient + ", ");
            }
            i++;
        }
        String namesString = namesStringBuilder.toString();
        return removeEndComma(namesString);
    }
    
    private void setCommentRows(String studentEmail, CourseRoster roster) {
        commentRows = new ArrayList<CommentRow>();
        
        for (CommentAttributes comment : comments) {
            String recipientDetails = getRecipientNames(comment.recipients, studentEmail, roster);
            InstructorAttributes instructor = roster.getInstructorForEmail(comment.giverEmail);
            String giverDetails = comment.giverEmail;
            if (instructor != null) {
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
    
    private void createFeedbackSessionRows(
            Map<String, FeedbackSessionResultsBundle> feedbackResultBundles, CourseRoster roster) {
        feedbackSessionRows = new ArrayList<FeedbackSessionRow>();
        
        for (String fsName : feedbackResultBundles.keySet()) {
            
            FeedbackSessionRow feedbackSessionRow = 
                    new FeedbackSessionRow(
                            fsName, courseId, createFeedbackQuestionTables(feedbackResultBundles.get(fsName),
                                                                           roster));
            
            feedbackSessionRows.add(feedbackSessionRow);
        }
    }
    
    private List<QuestionTable> createFeedbackQuestionTables(
            FeedbackSessionResultsBundle feedbackResultBundle, CourseRoster roster) {
        List<QuestionTable> feedbackQuestionTables = new ArrayList<QuestionTable>();
        
        for (Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responseEntries 
                     : feedbackResultBundle.getQuestionResponseMap().entrySet()) {
            
            FeedbackQuestionAttributes feedbackQuestion = responseEntries.getKey();
            int questionNumber = feedbackQuestion.questionNumber;
            String questionText = feedbackResultBundle.getQuestionText(feedbackQuestion.getId());
            Map<String, FeedbackQuestionAttributes> questions = feedbackResultBundle.questions;
            
            FeedbackQuestionAttributes question = questions.get(feedbackQuestion.getId());
            FeedbackQuestionDetails questionDetails = question.getQuestionDetails();
            String additionalInfo = questionDetails.getQuestionAdditionalInfoHtml(question.questionNumber, "");
            
            QuestionTable feedbackQuestionTable = 
                    new QuestionTable(questionNumber, questionText, additionalInfo, 
                                      createFeedbackResponseRows(feedbackResultBundle, questionDetails, 
                                                                 responseEntries, roster));
            feedbackQuestionTables.add(feedbackQuestionTable);
        }
        return feedbackQuestionTables;
    }
    
    private List<ResponseRow> createFeedbackResponseRows(
            FeedbackSessionResultsBundle feedbackResultBundle, FeedbackQuestionDetails questionDetails, 
            Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responseEntries,
            CourseRoster roster) {
        List<ResponseRow> feedbackResponseRows = new ArrayList<ResponseRow>();
        
        for (FeedbackResponseAttributes responseEntry : responseEntries.getValue()) {
            String giverName = feedbackResultBundle.getGiverNameForResponse(
                                                            responseEntries.getKey(), responseEntry);
            String giverTeamName = feedbackResultBundle.getTeamNameForEmail(responseEntry.giverEmail);
            giverName = feedbackResultBundle.appendTeamNameToName(giverName, giverTeamName);

            String recipientName = 
                    feedbackResultBundle.getRecipientNameForResponse(responseEntries.getKey(), responseEntry);
            String recipientTeamName = feedbackResultBundle.getTeamNameForEmail(responseEntry.recipientEmail);
            recipientName = feedbackResultBundle.appendTeamNameToName(recipientName, recipientTeamName);
            
            String response = responseEntry.getResponseDetails().getAnswerHtml(questionDetails);
            
            ResponseRow responseRow = 
                    new ResponseRow(giverName, recipientName, response, 
                                    createFeedbackResponseCommentRows(
                                            feedbackResultBundle, responseEntry, roster));
            
            feedbackResponseRows.add(responseRow);
        }
        return feedbackResponseRows;
    }
    
    private List<FeedbackResponseCommentRow> createFeedbackResponseCommentRows(
            FeedbackSessionResultsBundle feedbackResultBundle, FeedbackResponseAttributes responseEntry,
            CourseRoster roster) {
        List<FeedbackResponseCommentRow> feedbackResponseCommentRows = new ArrayList<FeedbackResponseCommentRow>();
        List<FeedbackResponseCommentAttributes> frcList = 
                feedbackResultBundle.responseComments.get(responseEntry.getId());
        
        for (FeedbackResponseCommentAttributes frc : frcList) {
            String frCommentGiver = frc.giverEmail;
            InstructorAttributes instructor = roster.getInstructorForEmail(frc.giverEmail);
            if (instructor != null) {
                frCommentGiver = instructor.displayedName + " " + instructor.name;
            }
            String lastEditorDisplay = null;
            if (frc.lastEditorEmail != null) {
                InstructorAttributes lastEditor = roster.getInstructorForEmail(frc.lastEditorEmail);
                lastEditorDisplay = lastEditor.displayedName + " " + lastEditor.name;
            }
            String creationTime = TimeHelper.formatDate(frc.createdAt);
            String editedAt = frc.getEditedAtTextForStudent(frCommentGiver.equals("Anonymous"), lastEditorDisplay);
            String comment = frc.commentText.getValue();
            
            StudentCommentsFeedbackResponseCommentRow feedbackResponseCommentRow = 
                    new StudentCommentsFeedbackResponseCommentRow(frCommentGiver, comment, creationTime, editedAt);
            
            feedbackResponseCommentRows.add(feedbackResponseCommentRow);
        }
        return feedbackResponseCommentRows;
    }
}
