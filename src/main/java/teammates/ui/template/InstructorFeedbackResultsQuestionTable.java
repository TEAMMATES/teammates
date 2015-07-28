package teammates.ui.template;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackQuestionDetails;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.ui.controller.InstructorFeedbackResultsPageData;

public class InstructorFeedbackResultsQuestionTable {

    private String courseId;
    private String feedbackSessionName;
    
    private String panelClass;
    private String responsesBodyClass;
    
    private List<InstructorFeedbackResultsResponseRow> responses;
    
    // store the attributes of the question for the non-display purposes 
    // such as form inputs
    private FeedbackQuestionAttributes question;

    private String questionText;
    private String additionalInfoText;
    private String questionStatisticsTable;
    
    private boolean isQuestionHasResponses;
    private boolean isShowResponseRows;
    
    private boolean isCollapsible;
    private boolean isBoldQuestionNumber;

    private List<ElementTag> columns;
    private Map<String, Boolean> isColumnSortable;

    public InstructorFeedbackResultsQuestionTable(InstructorFeedbackResultsPageData data,
                                          List<FeedbackResponseAttributes> responses,
                                          String questionStatisticsHtml,
                                          List<InstructorFeedbackResultsResponseRow> responseRows,
                                          FeedbackQuestionAttributes question,
                                          String additionalInfoId,
                                          List<ElementTag> columns,
                                          Map<String, Boolean> isColumnSortable) {
        this.courseId = question.courseId;
        this.feedbackSessionName = question.feedbackSessionName;
        
        this.questionStatisticsTable = questionStatisticsHtml;
        this.responses = responseRows;
        
        this.isQuestionHasResponses = !responses.isEmpty(); //TODO: just check is response is empty in jsp? 
        
        this.question = question;
        
        this.questionText = data.bundle.getQuestionText(question.getId());
        
        this.panelClass = responses.isEmpty() ? 
                          "panel-default" : 
                          "panel-info";
        
        this.responsesBodyClass = data.bundle.isComplete() && !data.isShouldCollapsed() ? 
                                  "panel-collapse collapse in" :
                                  "panel-collapse collapse";
        
        FeedbackQuestionDetails questionDetails = question.getQuestionDetails();
        this.additionalInfoText = questionDetails.getQuestionAdditionalInfoHtml(question.questionNumber, additionalInfoId);
        
        this.columns = columns;
        
        this.isBoldQuestionNumber = true;
        this.isColumnSortable = isColumnSortable;
    }

    public List<InstructorFeedbackResultsResponseRow> getResponses() {
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

    public List<ElementTag> getColumns() {
        return columns;
    }

    public boolean isCollapsible() {
        return isCollapsible;
    }
    
    public boolean isBoldQuestionNumber() {
        return isBoldQuestionNumber;
    }
    
    public Map<String, Boolean> getIsColumnSortable() {
        return isColumnSortable;
    }

    public String getResponsesBodyClass() {
        return responsesBodyClass;
    }

    public void setResponsesBodyClass(String responsesBodyClass) {
        this.responsesBodyClass = responsesBodyClass;
    }
    
    

    public void setShowResponseRows(boolean isShowResponseRows) {
        this.isShowResponseRows = isShowResponseRows;
    }

    public void setCollapsible(boolean isCollapsible) {
        this.isCollapsible = isCollapsible;
    }

    public void setBoldQuestionNumber(boolean isBoldQuestionNumber) {
        this.isBoldQuestionNumber = isBoldQuestionNumber;
    }

    public static void sortByQuestionNumber(List<InstructorFeedbackResultsQuestionTable> questionTables) {
        Collections.sort(questionTables, new Comparator<InstructorFeedbackResultsQuestionTable>() {
            public int compare(InstructorFeedbackResultsQuestionTable questionTable1, InstructorFeedbackResultsQuestionTable questionTable2) {
                return questionTable1.question.questionNumber - questionTable2.question.questionNumber;
            }
        });
    }
    
}
