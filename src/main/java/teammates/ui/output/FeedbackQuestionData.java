package teammates.ui.output;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.questions.FeedbackConstantSumDistributePointsType;
import teammates.common.datatransfer.questions.FeedbackConstantSumQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackMcqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackMsqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackRubricQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackTextQuestionDetails;
import teammates.common.util.Assumption;
import teammates.common.util.Const;

/**
 * The API output format of {@link FeedbackQuestionAttributes}.
 */
public class FeedbackQuestionData extends ApiOutput {
    private final String feedbackQuestionId;
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

    public FeedbackQuestionData(FeedbackQuestionAttributes feedbackQuestionAttributes) {
        FeedbackQuestionDetails feedbackQuestionDetails = feedbackQuestionAttributes.getQuestionDetails();

        this.feedbackQuestionId = feedbackQuestionAttributes.getFeedbackQuestionId();
        this.questionNumber = feedbackQuestionAttributes.getQuestionNumber();
        this.questionBrief = feedbackQuestionDetails.getQuestionText();
        this.questionDescription = feedbackQuestionAttributes.getQuestionDescription();

        this.questionDetails = feedbackQuestionDetails;

        this.questionType = feedbackQuestionAttributes.getQuestionType();
        this.giverType = feedbackQuestionAttributes.getGiverType();
        this.recipientType = feedbackQuestionAttributes.getRecipientType();

        if (feedbackQuestionAttributes.getNumberOfEntitiesToGiveFeedbackTo() == Const.MAX_POSSIBLE_RECIPIENTS) {
            this.numberOfEntitiesToGiveFeedbackToSetting = NumberOfEntitiesToGiveFeedbackToSetting.UNLIMITED;
            this.customNumberOfEntitiesToGiveFeedbackTo = null;
        } else {
            this.numberOfEntitiesToGiveFeedbackToSetting = NumberOfEntitiesToGiveFeedbackToSetting.CUSTOM;
            this.customNumberOfEntitiesToGiveFeedbackTo =
                    feedbackQuestionAttributes.getNumberOfEntitiesToGiveFeedbackTo();
        }

        // the visibility types are mixed in feedback participant type
        // therefore, we convert them to visibility types
        this.showResponsesTo = convertToFeedbackVisibilityType(feedbackQuestionAttributes.getShowResponsesTo());
        this.showGiverNameTo = convertToFeedbackVisibilityType(feedbackQuestionAttributes.getShowGiverNameTo());
        this.showRecipientNameTo =
                convertToFeedbackVisibilityType(feedbackQuestionAttributes.getShowRecipientNameTo());

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
            // TODO: remove the abstraction after migration
            // need to migrate CONSTSUM to either CONSTSUM_OPTIONS or CONSTSUM_RECIPIENTS
            // correct to either CONSTSUM_OPTIONS or CONSTSUM_RECIPIENTS
            FeedbackConstantSumQuestionDetails constantSumQuestionDetails =
                    (FeedbackConstantSumQuestionDetails) this.questionDetails;
            this.questionType = constantSumQuestionDetails.isDistributeToRecipients()
                    ? FeedbackQuestionType.CONSTSUM_RECIPIENTS : FeedbackQuestionType.CONSTSUM_OPTIONS;
            this.questionDetails.setQuestionType(this.questionType);

            // TODO: remove after data migration
            // distributePointsFor is added after forceUnevenDistribution, see #8577
            if (constantSumQuestionDetails.isForceUnevenDistribution()
                    && FeedbackConstantSumDistributePointsType.NONE.getDisplayedOption()
                    .equals(constantSumQuestionDetails.getDistributePointsFor())) {
                constantSumQuestionDetails.setDistributePointsFor(
                        FeedbackConstantSumDistributePointsType.DISTRIBUTE_ALL_UNEVENLY.getDisplayedOption());
            }
        }

        if (this.questionType == FeedbackQuestionType.TEXT) {
            // TODO: remove after data migration
            FeedbackTextQuestionDetails feedbackTextQuestionDetails =
                    (FeedbackTextQuestionDetails) this.questionDetails;
            if (feedbackTextQuestionDetails.getRecommendedLength() != null
                    && feedbackTextQuestionDetails.getRecommendedLength() == 0) {
                // for legacy data, 0 is treated as optional for recommended length
                feedbackTextQuestionDetails.setRecommendedLength(null);
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
                Assumption.fail("Unknown feedbackParticipantType" + feedbackParticipantType);
                break;
            }
            return null;
        }).collect(Collectors.toList());
    }

    public String getFeedbackQuestionId() {
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
