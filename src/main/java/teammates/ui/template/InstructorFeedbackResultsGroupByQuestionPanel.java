package teammates.ui.template;

import java.util.List;

import teammates.common.util.Url;


/**
 * Data model for the giver panel in InstructorFeedbackResults for Giver > Question > Recipient,
 * and for the recipient panel in Recipient > Question > Giver
 * 
 *
 */
public class InstructorFeedbackResultsGroupByQuestionPanel extends InstructorResultsParticipantPanel {
    
    List<InstructorResultsQuestionTable> questionTables;
    
    boolean isEmailValid;
    
    
    public InstructorFeedbackResultsGroupByQuestionPanel() {
        
    
    }
    
    public static InstructorFeedbackResultsGroupByQuestionPanel buildInstructorFeedbackResultsGroupByQuestionPanel(
                                                                    List<InstructorResultsQuestionTable> questionTables,
                                                                    boolean isEmailValid, Url profilePictureLink, String mailtoStyle) {

        InstructorFeedbackResultsGroupByQuestionPanel byQuestionPanel = new InstructorFeedbackResultsGroupByQuestionPanel();
        
        
        byQuestionPanel.questionTables = questionTables;
        
        return byQuestionPanel;
        
    }

    public List<InstructorResultsQuestionTable> getQuestionTables() {
        return questionTables;
    }

    public void setQuestionTables(List<InstructorResultsQuestionTable> questionTables) {
        this.questionTables = questionTables;
    }

    public boolean isEmailValid() {
        return isEmailValid;
    }

    public void setEmailValid(boolean isEmailValid) {
        this.isEmailValid = isEmailValid;
    }
    
}
