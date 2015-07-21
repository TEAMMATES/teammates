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
    
    
    public InstructorFeedbackResultsGroupByQuestionPanel() {
    }
    
    public static InstructorFeedbackResultsGroupByQuestionPanel buildInstructorFeedbackResultsGroupByQuestionPanel(
                                                                    List<InstructorResultsQuestionTable> questionTables,
                                                                    boolean isEmailValid, Url profilePictureLink, 
                                                                    boolean isGiver, String participantIdentifier, String participantName,
                                                                    InstructorResultsModerationButton moderationButton, 
                                                                    boolean isModerationButtonDisplayed) {

        InstructorFeedbackResultsGroupByQuestionPanel byQuestionPanel = new InstructorFeedbackResultsGroupByQuestionPanel();
        byQuestionPanel.setParticipantIdentifier(participantIdentifier);
        byQuestionPanel.setName(participantName);
        byQuestionPanel.setGiver(isGiver);
        
        byQuestionPanel.setEmailValid(isEmailValid);
        byQuestionPanel.profilePictureLink = profilePictureLink.toString();
        
        byQuestionPanel.questionTables = questionTables;
        
        byQuestionPanel.setModerationButton(moderationButton);
        byQuestionPanel.setModerationButtonDisplayed(isModerationButtonDisplayed);
        
        byQuestionPanel.setHasResponses(true);
        
        return byQuestionPanel;
    }

    public List<InstructorResultsQuestionTable> getQuestionTables() {
        return questionTables;
    }

    public void setQuestionTables(List<InstructorResultsQuestionTable> questionTables) {
        this.questionTables = questionTables;
    }

    
}
