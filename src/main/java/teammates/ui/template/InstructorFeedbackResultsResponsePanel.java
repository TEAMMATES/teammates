package teammates.ui.template;

import teammates.common.datatransfer.FeedbackQuestionAttributes;

public class InstructorFeedbackResultsResponsePanel {
    private FeedbackQuestionAttributes question;
    private String questionText;
    private String additionalInfoText;
    
    private ElementTag rowAttributes;
    
    private String displayableResponse;

    
    public InstructorFeedbackResultsResponsePanel(FeedbackQuestionAttributes question, String questionText,
                                                  String additionalInfoText, ElementTag rowAttributes,
                                                  String displayableResponse) {
        this.question = question;
        this.questionText = questionText;
        this.additionalInfoText = additionalInfoText;
        this.rowAttributes = rowAttributes;
        this.displayableResponse = displayableResponse;
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

    public ElementTag getRowAttributes() {
        return rowAttributes;
    }

    public String getDisplayableResponse() {
        return displayableResponse;
    }
    
    // TODO response comments
    //private List<>
    
    
}
