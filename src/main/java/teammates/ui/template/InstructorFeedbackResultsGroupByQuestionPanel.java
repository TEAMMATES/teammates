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
                                                                    boolean isEmailValid, Url profilePictureLink, String mailtoStyle,
                                                                    boolean isGiver, String participantIdentifier, String participantName,
                                                                    InstructorResultsModerationButton moderationButton, 
                                                                    boolean isModerationButtonDisplayed) {

        InstructorFeedbackResultsGroupByQuestionPanel byQuestionPanel = new InstructorFeedbackResultsGroupByQuestionPanel();
        byQuestionPanel.setParticipantIdentifier(participantIdentifier);
        byQuestionPanel.setName(participantName);
        byQuestionPanel.setGiver(isGiver);
        
        byQuestionPanel.setEmailValid(isEmailValid);
        byQuestionPanel.profilePictureLink = profilePictureLink.toString();
        byQuestionPanel.mailtoStyle = mailtoStyle;
        
        byQuestionPanel.questionTables = questionTables;
        
        byQuestionPanel.setModerationButton(moderationButton);
        byQuestionPanel.setModerationButtonDisplayed(isModerationButtonDisplayed);
        
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
