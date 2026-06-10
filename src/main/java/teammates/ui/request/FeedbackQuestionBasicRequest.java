package teammates.ui.request;

import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.participanttypes.QuestionGiverType;
import teammates.common.datatransfer.participanttypes.QuestionRecipientType;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.datatransfer.visibility.FeedbackVisibilityType;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.output.NumberOfEntitiesToGiveFeedbackToSetting;

/**
 * The basic request of modifying a feedback question.
 */
public class FeedbackQuestionBasicRequest extends BasicRequest {
    private int questionNumber;
    private String questionBrief;
    private String questionDescription;

    private Map<String, Object> questionDetails;

    private FeedbackQuestionType questionType;
    private QuestionGiverType giverType;
    private QuestionRecipientType recipientType;

    private NumberOfEntitiesToGiveFeedbackToSetting numberOfEntitiesToGiveFeedbackToSetting;
    private Integer customNumberOfEntitiesToGiveFeedbackTo;

    private List<FeedbackVisibilityType> showResponsesTo;
    private List<FeedbackVisibilityType> showGiverNameTo;
    private List<FeedbackVisibilityType> showRecipientNameTo;

    @Override
    public void validate() throws InvalidHttpRequestBodyException {
        validateTrue(questionNumber >= 1, "Invalid question number");
        validateTrue(questionBrief != null, "Question brief cannot be null");
        validateTrue(!questionBrief.isEmpty(), "Question brief cannot be empty");
        validateTrue(questionDetails != null, "Question details cannot be null");

        validateTrue(questionType != null, "Question type cannot be null");
        validateTrue(giverType != null, "Giver type cannot be null");
        validateTrue(recipientType != null, "Recipient type cannot be null");

        validateTrue(numberOfEntitiesToGiveFeedbackToSetting != null,
                "numberOfEntitiesToGiveFeedbackToSetting cannot be null");
        if (numberOfEntitiesToGiveFeedbackToSetting == NumberOfEntitiesToGiveFeedbackToSetting.CUSTOM) {
            validateTrue(customNumberOfEntitiesToGiveFeedbackTo != null,
                    "customNumberOfEntitiesToGiveFeedbackTo must be set");
        }

        validateTrue(showResponsesTo != null, "showResponsesTo cannot be null");
        validateTrue(showGiverNameTo != null, "showGiverNameTo cannot be null");
        validateTrue(showRecipientNameTo != null, "showRecipientNameTo cannot be null");
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public String getQuestionDescription() {
        return questionDescription;
    }

    /**
     * Get feedback question details.
     */
    public FeedbackQuestionDetails getQuestionDetails() {
        FeedbackQuestionDetails details =
                JsonUtils.fromJson(JsonUtils.toCompactJson(questionDetails), questionType.getQuestionDetailsClass());
        details.setQuestionText(questionBrief);
        return details;
    }

    public QuestionGiverType getGiverType() {
        return giverType;
    }

    public QuestionRecipientType getRecipientType() {
        return recipientType;
    }

    /**
     * Get number of entities to give feedback to.
     */
    public int getNumberOfEntitiesToGiveFeedbackTo() {
        switch (numberOfEntitiesToGiveFeedbackToSetting) {
        case CUSTOM:
            return customNumberOfEntitiesToGiveFeedbackTo;
        case UNLIMITED:
            return Const.MAX_POSSIBLE_RECIPIENTS;
        default:
            assert false : "Unknown numberOfEntitiesToGiveFeedbackToSetting: " + numberOfEntitiesToGiveFeedbackToSetting;
            break;
        }
        return 0;
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

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public void setQuestionBrief(String questionBrief) {
        this.questionBrief = questionBrief;
    }

    public void setQuestionDescription(String questionDescription) {
        this.questionDescription = questionDescription;
    }

    public void setQuestionDetails(FeedbackQuestionDetails questionDetails) {
        this.questionDetails = JsonUtils.fromJson(JsonUtils.toCompactJson(questionDetails), Map.class);
    }

    public void setQuestionType(FeedbackQuestionType questionType) {
        this.questionType = questionType;
    }

    public void setGiverType(QuestionGiverType giverType) {
        this.giverType = giverType;
    }

    public void setRecipientType(QuestionRecipientType recipientType) {
        this.recipientType = recipientType;
    }

    public void setNumberOfEntitiesToGiveFeedbackToSetting(
            NumberOfEntitiesToGiveFeedbackToSetting numberOfEntitiesToGiveFeedbackToSetting) {
        this.numberOfEntitiesToGiveFeedbackToSetting = numberOfEntitiesToGiveFeedbackToSetting;
    }

    public void setCustomNumberOfEntitiesToGiveFeedbackTo(Integer customNumberOfEntitiesToGiveFeedbackTo) {
        this.customNumberOfEntitiesToGiveFeedbackTo = customNumberOfEntitiesToGiveFeedbackTo;
    }

    public void setShowResponsesTo(List<FeedbackVisibilityType> showResponsesTo) {
        this.showResponsesTo = showResponsesTo;
    }

    public void setShowGiverNameTo(List<FeedbackVisibilityType> showGiverNameTo) {
        this.showGiverNameTo = showGiverNameTo;
    }

    public void setShowRecipientNameTo(List<FeedbackVisibilityType> showRecipientNameTo) {
        this.showRecipientNameTo = showRecipientNameTo;
    }
}
