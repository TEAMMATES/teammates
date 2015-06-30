package teammates.ui.template;

import java.util.Arrays;
import java.util.List;

import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackQuestionDetails;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.ui.controller.InstructorFeedbackResultsPageData;

public class InstructorResultsQuestionTable {

    private String courseId;
    private String feedbackSessionName;
    
    private String questionStatisticsHtml;  
    private String panelClass;
    
    private List<InstructorResultsResponseRow> responses;
    
    // store the details of the question for the non-display purposes 
    // such as form inputs
    private FeedbackQuestionAttributes question;

    private String questionText;
    private String additionalInfoText;
    private String questionStatisticsTable;
    
    private boolean isQuestionHasResponses;
    private boolean isShowResponseRows;

    private List<String> columnNames = Arrays.asList("Giver", "Team", "Recipient", "Team", 
                                                     "Feedback", "Actions");

    public InstructorResultsQuestionTable(InstructorFeedbackResultsPageData data,
                                          List<FeedbackResponseAttributes> responses,
                                          String questionStatisticsHtml,
                                          List<InstructorResultsResponseRow> responseRows,
                                          FeedbackQuestionAttributes question,
                                          String additionalInfoId) {
        this.courseId = question.courseId;
        this.feedbackSessionName = question.feedbackSessionName;
        
        this.questionStatisticsHtml = questionStatisticsHtml;
        this.responses = responseRows;
        
        this.isQuestionHasResponses = !responses.isEmpty(); //TODO; just use empty responses? 
        
        this.question = question;
        
        this.questionText = data.bundle.getQuestionText(question.getId());
        
        this.panelClass = responses.isEmpty() ? "panel-default" : "panel-info";
        
        FeedbackQuestionDetails questionDetails = question.getQuestionDetails();
        this.additionalInfoText = questionDetails.getQuestionAdditionalInfoHtml(question.questionNumber, additionalInfoId);        
        this.questionStatisticsTable = questionDetails.getQuestionResultStatisticsHtml(responses, question, data, data.bundle, "question");
    }

    public String getQuestionStatisticsHtml() {
        return questionStatisticsHtml;
    }

    public List<InstructorResultsResponseRow> getResponses() {
        return responses;
    }

    public String getPanelClass() {
        return panelClass;
    }

    public FeedbackQuestionAttributes getQuestion() {
        return question;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getAdditionalInfoText() {
        return additionalInfoText;
    }

    public String getQuestionStatisticsTable() {
        return questionStatisticsTable;
    }

    public boolean isQuestionHasResponses() {
        return isQuestionHasResponses;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getFeedbackSessionName() {
        return feedbackSessionName;
    }

    public boolean isShowResponseRows() {
        return isShowResponseRows;
    }

    public void setShowResponseRows(boolean isShowResponseRows) {
        this.isShowResponseRows = isShowResponseRows;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames;
    }
    
}
