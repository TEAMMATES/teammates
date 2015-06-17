package teammates.ui.template;

import java.util.List;

public class InstructorResultsQuestionTable {

    private String questionStatisticsHtml;  
    
    private List<InstructorResultsResponseRow> responses;

    

    public InstructorResultsQuestionTable(String questionStatisticsHtml,
                                    List<InstructorResultsResponseRow> responses) {
        this.questionStatisticsHtml = questionStatisticsHtml;
        this.responses = responses;
    }

    public String getQuestionStatisticsHtml() {
        return questionStatisticsHtml;
    }

    public List<InstructorResultsResponseRow> getResponses() {
        return responses;
    }
    
    
    
    
}
