package teammates.ui.output;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;

import teammates.common.datatransfer.participanttypes.QuestionGiverType;
import teammates.common.datatransfer.participanttypes.QuestionRecipientType;
import teammates.common.datatransfer.questions.FeedbackMcqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackMsqQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.questions.FeedbackRubricQuestionDetails;
import teammates.common.datatransfer.visibility.FeedbackVisibilityType;
import teammates.common.util.Const;
import teammates.storage.entity.FeedbackQuestion;

/**
 * The API output format of {@link FeedbackQuestion}.
 */
public class FeedbackQuestionData implements ApiOutput {
    private final UUID feedbackQuestionId;
    private int questionNumber;
    private final String questionBrief;
    private final String questionDescription;

    private final FeedbackQuestionDetails questionDetails;

    private FeedbackQuestionType questionType;
    private final QuestionGiverType giverType;
    private final QuestionRecipientType recipientType;

    private final NumberOfEntitiesToGiveFeedbackToSetting numberOfEntitiesToGiveFeedbackToSetting;
    private final Integer customNumberOfEntitiesToGiveFeedbackTo;

    private List<FeedbackVisibilityType> showResponsesTo;
    private List<FeedbackVisibilityType> showGiverNameTo;
    private List<FeedbackVisibilityType> showRecipientNameTo;

    @JsonCreator
    private FeedbackQuestionData(UUID feedbackQuestionId, String questionBrief,
            String questionDescription, FeedbackQuestionDetails questionDetails,
            QuestionGiverType giverType, QuestionRecipientType recipientType,
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

        this.showResponsesTo = feedbackQuestion.getShowResponsesTo();
        this.showGiverNameTo = feedbackQuestion.getShowGiverNameTo();
        this.showRecipientNameTo = feedbackQuestion.getShowRecipientNameTo();
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

    public QuestionGiverType getGiverType() {
        return giverType;
    }

    public QuestionRecipientType getRecipientType() {
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
