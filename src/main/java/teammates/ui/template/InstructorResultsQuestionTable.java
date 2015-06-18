package teammates.ui.template;

import java.util.List;

import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackQuestionDetails;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;

public class InstructorResultsQuestionTable {

    private String questionStatisticsHtml;  
    private String panelClass;
    
    private List<InstructorResultsResponseRow> responses;
    
    // store the details of the question for the non-display purposes 
    // such as form inputs
    private FeedbackQuestionAttributes question;

    private String questionText;
    private String additionalInfoText;
    
    private boolean isQuestionHasResponse;
    

    public InstructorResultsQuestionTable(FeedbackSessionResultsBundle bundle,
                                          String questionStatisticsHtml,
                                          List<InstructorResultsResponseRow> responses,
                                          FeedbackQuestionAttributes question) {
        this.questionStatisticsHtml = questionStatisticsHtml;
        this.responses = responses;
        this.question = question;
        
        this.questionText = bundle.getQuestionText(question.getId());
        
        FeedbackQuestionDetails questionDetails = question.getQuestionDetails();
        
        this.additionalInfoText = questionDetails.getQuestionAdditionalInfoHtml(question.questionNumber, "");
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

    public void setPanelClass(String panelClass) {
        this.panelClass = panelClass;
    }
    
    
    
    
}
