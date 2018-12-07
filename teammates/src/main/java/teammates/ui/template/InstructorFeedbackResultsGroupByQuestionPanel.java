package teammates.ui.template;

import java.util.List;

import teammates.common.util.FieldValidator;

/**
 * Data model for the giver panel in InstructorFeedbackResults for Giver > Question > Recipient,
 * and for the recipient panel in Recipient > Question > Giver.
 */
public class InstructorFeedbackResultsGroupByQuestionPanel extends InstructorFeedbackResultsParticipantPanel {

    private List<InstructorFeedbackResultsQuestionTable> questionTables;

    public InstructorFeedbackResultsGroupByQuestionPanel(List<InstructorFeedbackResultsQuestionTable> questionTables,
                                    String profilePictureLink,
                                    boolean isGiver, String participantIdentifier, String participantName,
                                    InstructorFeedbackResultsModerationButton moderationButton) {
        this.participantIdentifier = participantIdentifier;
        this.name = participantName;
        this.isGiver = isGiver;

        this.isEmailValid = new FieldValidator()
                                    .getInvalidityInfoForEmail(participantIdentifier)
                                    .isEmpty();
        this.profilePictureLink = profilePictureLink;

        this.questionTables = questionTables;

        this.moderationButton = moderationButton;

        this.isHasResponses = true;
    }

    /**
     * Constructs a GroupByQuestionPanel without a moderation button.
     */
    public InstructorFeedbackResultsGroupByQuestionPanel(
                                    List<InstructorFeedbackResultsQuestionTable> questionTables,
                                    String profilePictureLink,
                                    boolean isGroupedByGiver, String participantIdentifier, String participantName) {
        this(questionTables, profilePictureLink, isGroupedByGiver,
                participantIdentifier, participantName, null);
    }

    /**
     * Constructs a GroupByQuestionPanel with a moderation button.
     */
    public InstructorFeedbackResultsGroupByQuestionPanel(
                                    String participantIdentifier, String participantName,
                                    List<InstructorFeedbackResultsQuestionTable> questionTables,
                                    String profilePictureLink,
                                    boolean isGroupedByGiver,
                                    InstructorFeedbackResultsModerationButton moderationButton) {
        this(questionTables, profilePictureLink, isGroupedByGiver, participantIdentifier,
                participantName, moderationButton);
    }

    public List<InstructorFeedbackResultsQuestionTable> getQuestionTables() {
        return questionTables;
    }

    public void setQuestionTables(List<InstructorFeedbackResultsQuestionTable> questionTables) {
        this.questionTables = questionTables;
    }

}
