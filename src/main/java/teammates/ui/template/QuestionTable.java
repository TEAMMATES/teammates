package teammates.ui.template;

import java.util.List;

public class QuestionTable {
    private int questionNumber;
    private String questionText;
    private String additionalInfo;
    private List<ResponseRow> responseRows;
    
    public QuestionTable(final int questionNumber, final String questionText, 
                             final String additionalInfo, final List<ResponseRow> responseRows) {
        this.questionNumber = questionNumber;
        this.questionText = questionText;
        this.additionalInfo = additionalInfo;
        this.responseRows = responseRows;
    }
    
    public int getQuestionNumber() {
        return questionNumber;
    }
    
    public String getQuestionText() {
        return questionText;
    }
    
    public String getAdditionalInfo() {
        return additionalInfo;
    }
    
    public List<ResponseRow> getResponseRows() {
        return responseRows;
    }
}
