package teammates.ui.template;

import java.util.List;

import teammates.common.util.FieldValidator;


/**
 * Data model for the giver panel in InstructorFeedbackResults for Giver > Question > Recipient,
 * and for the recipient panel in Recipient > Question > Giver
 * 
 *
 */
public class InstructorFeedbackResultsGroupByQuestionPanel extends InstructorResultsParticipantPanel {
    
    List<InstructorResultsQuestionTable> questionTables;
    
    boolean isEmailValid;
    
    public InstructorFeedbackResultsGroupByQuestionPanel(List<InstructorResultsQuestionTable> questionTables,
                                    String profilePictureLink, 
                                    boolean isGiver, String participantIdentifier, String participantName,
                                    InstructorResultsModerationButton moderationButton) {
        this.participantIdentifier = participantIdentifier;
        this.name = participantName;
        this.isGiver = isGiver;
        
        boolean isEmailValid = new FieldValidator()
                                       .getInvalidityInfo(FieldValidator.FieldType.EMAIL, participantIdentifier).isEmpty();
        this.isEmailValid = isEmailValid;
        this.profilePictureLink = profilePictureLink;
        
        this.questionTables = questionTables;
        
        this.moderationButton = moderationButton;
        
        this.isHasResponses = true;
    }
    
    public static InstructorFeedbackResultsGroupByQuestionPanel buildInstructorFeedbackResultsGroupByQuestionPanelWithoutModerationButton(
                                    List<InstructorResultsQuestionTable> questionTables,
                                    String profilePictureLink, 
                                    boolean isGroupedByGiver, String participantIdentifier, String participantName) {
        return new InstructorFeedbackResultsGroupByQuestionPanel(questionTables, profilePictureLink, isGroupedByGiver, 
                                                                participantIdentifier, participantName, 
                                                                null);
    }
    
    public static InstructorFeedbackResultsGroupByQuestionPanel buildInstructorFeedbackResultsGroupByQuestionPanelWithModerationButton(
                                    String participantIdentifier, String participantName, 
                                    List<InstructorResultsQuestionTable> questionTables,
                                    String profilePictureLink, 
                                    boolean isGroupedByGiver,
                                    InstructorResultsModerationButton moderationButton) {
        return new InstructorFeedbackResultsGroupByQuestionPanel(questionTables, profilePictureLink, isGroupedByGiver, 
                                                                 participantIdentifier, participantName, 
                                                                 moderationButton);
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
