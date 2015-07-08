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
import teammates.common.util.Const;
import teammates.ui.template.CommentRow;
import teammates.ui.template.CoursePagination;
import teammates.ui.template.FeedbackResponseComment;
import teammates.ui.template.FeedbackSessionRow;
import teammates.ui.template.QuestionTable;
import teammates.ui.template.ResponseRow;
import teammates.ui.template.StudentCommentsCommentRow;

/**
 * PageData: the data used in the StudentCommentsPage
 */
public class StudentCommentsPageData extends PageData {

    private String courseId;
    private String courseName;
    
    private CoursePagination coursePagination;
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
        
        setCoursePagination(coursePaginationList);
        setCommentRows(studentEmail, roster, comments);
        createFeedbackSessionRows(feedbackResultBundles, roster);
    }

    public String getCourseId() {
        return courseId;
    }
    
    public String getCourseName() {
        return courseName;
    }
    
    public CoursePagination getCoursePagination() {
        return coursePagination;
    }
    
    public List<CommentRow> getCommentRows() {
        return commentRows;
    }
    
    public List<FeedbackSessionRow> getFeedbackSessionRows() {
        return feedbackSessionRows;
    }
    
    private String retrievePreviousPageLink(List<String> coursePaginationList) {
        int courseIdx = coursePaginationList.indexOf(courseId);
        String previousPageLink = "javascript:;";
        if (courseIdx >= 1) {
            previousPageLink = getStudentCommentsLink() + "&courseid=" + coursePaginationList.get(courseIdx - 1);
        }
        return previousPageLink;
    }

    private String retrieveNextPageLink(List<String> coursePaginationList) {
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
            } else if(student != null){
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
    
    
    private void setCoursePagination(List<String> coursePaginationList) {
        String previousPageLink = retrievePreviousPageLink(coursePaginationList);
        String nextPageLink = retrieveNextPageLink(coursePaginationList);
        String activeCourse = coursePaginationList.contains(courseId) ? courseId : "";
        String userCommentsLink = getStudentCommentsLink(false);
        coursePagination = new CoursePagination(previousPageLink, nextPageLink, coursePaginationList,
                                                activeCourse, userCommentsLink);
    }
    
    private void setCommentRows(String studentEmail, CourseRoster roster, List<CommentAttributes> comments) {
        commentRows = new ArrayList<CommentRow>();
        
        for (CommentAttributes comment : comments) {
            String recipientDetails = getRecipientNames(comment.recipients, studentEmail, roster);
            InstructorAttributes instructor = roster.getInstructorForEmail(comment.giverEmail);
            String giverDetails = comment.giverEmail;
            if (instructor != null) {
                giverDetails = instructor.displayedName + " " + instructor.name;
            }
            String creationTime = Const.SystemParams.COMMENTS_SIMPLE_DATE_FORMATTER.format(comment.createdAt);
            String editedAt = comment.getEditedAtText(giverDetails.equals("Anonymous"));
            
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
                            fsName, courseId, createFeedbackQuestionTables(
                                                      feedbackResultBundles.get(fsName), roster));
            
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
                                      createFeedbackResponseRows(
                                              feedbackResultBundle, questionDetails, responseEntries, roster));
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
            String giverName = 
                    feedbackResultBundle.getGiverNameForResponse(responseEntries.getKey(), responseEntry);
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
    
    private List<FeedbackResponseComment> createFeedbackResponseCommentRows(
            FeedbackSessionResultsBundle feedbackResultBundle, FeedbackResponseAttributes responseEntry,
            CourseRoster roster) {
        List<FeedbackResponseComment> feedbackResponseCommentRows = new ArrayList<FeedbackResponseComment>();
        List<FeedbackResponseCommentAttributes> frcList = 
                feedbackResultBundle.responseComments.get(responseEntry.getId());
        
        for (FeedbackResponseCommentAttributes frc : frcList) {
            FeedbackResponseComment feedbackResponseCommentRow = 
                    new FeedbackResponseComment(frc, frc.giverEmail);
            
            feedbackResponseCommentRows.add(feedbackResponseCommentRow);
        }
        return feedbackResponseCommentRows;
    }
}
