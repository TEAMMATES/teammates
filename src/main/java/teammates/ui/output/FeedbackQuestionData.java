package teammates.ui.output;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.questions.FeedbackConstantSumQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackMcqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackMsqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackRubricQuestionDetails;
import teammates.common.util.Const;
import teammates.storage.entity.FeedbackQuestion;

/**
 * The API output format of {@link FeedbackQuestion}.
 */
public class FeedbackQuestionData extends ApiOutput {
    private final UUID feedbackQuestionId;
    private int questionNumber;
    private final String questionBrief;
    private final String questionDescription;

    private final FeedbackQuestionDetails questionDetails;

    private FeedbackQuestionType questionType;
    private final FeedbackParticipantType giverType;
    private final FeedbackParticipantType recipientType;

    private final NumberOfEntitiesToGiveFeedbackToSetting numberOfEntitiesToGiveFeedbackToSetting;
    private final Integer customNumberOfEntitiesToGiveFeedbackTo;

    private List<FeedbackVisibilityType> showResponsesTo;
    private List<FeedbackVisibilityType> showGiverNameTo;
    private List<FeedbackVisibilityType> showRecipientNameTo;

    @JsonCreator
    private FeedbackQuestionData(UUID feedbackQuestionId, String questionBrief,
            String questionDescription, FeedbackQuestionDetails questionDetails,
            FeedbackParticipantType giverType, FeedbackParticipantType recipientType,
            NumberOfEntitiesToGiveFeedbackToSetting numberOfEntitiesToGiveFeedbackToSetting,
            Integer customNumberOfEntitiesToGiveFeedbackTo) {
        this.feedbackQuestionId = feedbackQuestionId;
        this.questionBrief = questionBrief;
        this.questionDescription = questionDescription;
        this.questionDetails = questionDetails;
        this.giverType = giverType;
        this.recipientType = recipientType;
        this.numberOfEntitiesToGiveFeedbackToSetting = numberOfEntitiesToGiveFeedbackToSetting;
        this.customNumberOfEntitiesToGiveFeedbackTo = customNumberOfEntitiesToGiveFeedbackTo;
    }

    public FeedbackQuestionData(FeedbackQuestion feedbackQuestion) {
        FeedbackQuestionDetails feedbackQuestionDetails = feedbackQuestion.getQuestionDetailsCopy();

        this.feedbackQuestionId = feedbackQuestion.getId();
        this.questionNumber = feedbackQuestion.getQuestionNumber();
        this.questionBrief = feedbackQuestionDetails.getQuestionText();
        this.questionDescription = feedbackQuestion.getDescription();

        this.questionDetails = feedbackQuestionDetails;

        this.questionType = feedbackQuestion.getQuestionType();
        this.giverType = feedbackQuestion.getGiverType();
        this.recipientType = feedbackQuestion.getRecipientType();

        if (feedbackQuestion.getNumOfEntitiesToGiveFeedbackTo() == Const.MAX_POSSIBLE_RECIPIENTS) {
            this.numberOfEntitiesToGiveFeedbackToSetting = NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED;
            this.customNumberOfEntitiesToGiveFeedbackTo = null;
        } else {
            this.numberOfEntitiesToGiveFeedbackToSetting = NumberOfEntitiesToGiveFeedbackToSetting.CUSTOM;
            this.customNumberOfEntitiesToGiveFeedbackTo =
                    feedbackQuestion.getNumOfEntitiesToGiveFeedbackTo();
        }

        // the visibility types are mixed in feedback participant type
        // therefore, we convert them to visibility types
        this.showResponsesTo = convertToFeedbackVisibilityType(feedbackQuestion.getShowResponsesTo());
        this.showGiverNameTo = convertToFeedbackVisibilityType(feedbackQuestion.getShowGiverNameTo());
        this.showRecipientNameTo =
                convertToFeedbackVisibilityType(feedbackQuestion.getShowRecipientNameTo());

        // specially handling for contribution questions
        // TODO: remove the hack
        if (this.questionType == FeedbackQuestionType.CONTRIB
                && this.giverType == FeedbackParticipantType.STUDENTS
                && this.recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF
                && this.showResponsesTo.contains(FeedbackVisibilityType.GIVER_TEAM_MEMBERS)) {
            // remove the redundant visibility type as GIVER_TEAM_MEMBERS is just RECIPIENT_TEAM_MEMBERS
            // contribution question keep the redundancy for legacy reason
            this.showResponsesTo.remove(FeedbackVisibilityType.RECIPIENT_TEAM_MEMBERS);
        }

        if (this.questionType == FeedbackQuestionType.CONSTSUM) {
            FeedbackConstantSumQuestionDetails constantSumQuestionDetails =
                    (FeedbackConstantSumQuestionDetails) this.questionDetails;
            this.questionType = constantSumQuestionDetails.isDistributeToRecipients()
                    ? FeedbackQuestionType.CONSTSUM_RECIPIENTS : FeedbackQuestionType.CONSTSUM_OPTIONS;
            this.questionDetails.setQuestionType(this.questionType);
        }
    }

    public FeedbackQuestionData(FeedbackQuestion feedbackQuestion, Optional<List<String>> dynamicallyGeneratedOptions) {
        this(feedbackQuestion);
        if (dynamicallyGeneratedOptions.isPresent()) {
            if (this.questionDetails instanceof FeedbackMcqQuestionDetails feedbackMcqQuestionDetails) {
                feedbackMcqQuestionDetails.setMcqChoices(dynamicallyGeneratedOptions.get());
            } else if (this.questionDetails instanceof FeedbackMsqQuestionDetails feedbackMsqQuestionDetails) {
                feedbackMsqQuestionDetails.setMsqChoices(dynamicallyGeneratedOptions.get());
            }
        }
    }

    /**
     * Converts a list of feedback participant type to a list of visibility type.
     */
    private List<FeedbackVisibilityType> convertToFeedbackVisibilityType(
            List<FeedbackParticipantType> feedbackParticipantTypeList) {
        return feedbackParticipantTypeList.stream().map(feedbackParticipantType -> {
            switch (feedbackParticipantType) {
            case STUDENTS:
                return FeedbackVisibilityType.STUDENTS;
            case INSTRUCTORS:
                return FeedbackVisibilityType.INSTRUCTORS;
            case RECEIVER:
                return FeedbackVisibilityType.RECIPIENT;
            case OWN_TEAM_MEMBERS:
                return FeedbackVisibilityType.GIVER_TEAM_MEMBERS;
            case RECEIVER_TEAM_MEMBERS:
                return FeedbackVisibilityType.RECIPIENT_TEAM_MEMBERS;
            default:
                assert false : "Unknown feedbackParticipantType" + feedbackParticipantType;
                break;
            }
            return null;
        }).collect(Collectors.toList());
    }

    public UUID getFeedbackQuestionId() {
        return feedbackQuestionId;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public String getQuestionBrief() {
        return questionBrief;
    }

    public String getQuestionDescription() {
        return questionDescription;
    }

    public FeedbackQuestionDetails getQuestionDetails() {
        return questionDetails;
    }

    public FeedbackQuestionType getQuestionType() {
        return questionType;
    }

    public FeedbackParticipantType getGiverType() {
        return giverType;
    }

    public FeedbackParticipantType getRecipientType() {
        return recipientType;
    }

    public NumberOfEntitiesToGiveFeedbackToSetting getNumberOfEntitiesToGiveFeedbackToSetting() {
        return numberOfEntitiesToGiveFeedbackToSetting;
    }

    public Integer getCustomNumberOfEntitiesToGiveFeedbackTo() {
        return customNumberOfEntitiesToGiveFeedbackTo;
    }

    public List<FeedbackVisibilityType> getShowResponsesTo() {
        return showResponsesTo;
    }

    public List<FeedbackVisibilityType> getShowGiverNameTo() {
        return showGiverNameTo;
    }

    public List<FeedbackVisibilityType> getShowRecipientNameTo() {
        return showRecipientNameTo;
    }

    /**
     * Hides some attributes to a student.
     */
    public void hideInformationForStudent() {
        if (questionDetails instanceof FeedbackMcqQuestionDetails) {
            ((FeedbackMcqQuestionDetails) questionDetails).setMcqWeights(Collections.emptyList());
            ((FeedbackMcqQuestionDetails) questionDetails).setMcqOtherWeight(0);
        } else if (questionDetails instanceof FeedbackMsqQuestionDetails) {
            ((FeedbackMsqQuestionDetails) questionDetails).setMsqWeights(Collections.emptyList());
            ((FeedbackMsqQuestionDetails) questionDetails).setMsqOtherWeight(0);
        } else if (questionDetails instanceof FeedbackRubricQuestionDetails) {
            ((FeedbackRubricQuestionDetails) questionDetails)
                    .setRubricWeightsForEachCell(Collections.singletonList(Collections.emptyList()));
        }
    }
}
