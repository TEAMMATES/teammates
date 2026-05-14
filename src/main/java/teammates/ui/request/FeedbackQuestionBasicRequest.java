package teammates.ui.request;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import teammates.common.datatransfer.participanttypes.QuestionGiverType;
import teammates.common.datatransfer.participanttypes.QuestionRecipientType;
import teammates.common.datatransfer.participanttypes.ViewerType;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.datatransfer.questions.FeedbackQuestionType;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.ui.output.FeedbackVisibilityType;
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
        assertTrue(questionNumber >= 1, "Invalid question number");
        assertTrue(questionBrief != null, "Question brief cannot be null");
        assertTrue(!questionBrief.isEmpty(), "Question brief cannot be empty");
        assertTrue(questionDetails != null, "Question details cannot be null");

        assertTrue(questionType != null, "Question type cannot be null");
        assertTrue(giverType != null, "Giver type cannot be null");
        assertTrue(recipientType != null, "Recipient type cannot be null");

        assertTrue(numberOfEntitiesToGiveFeedbackToSetting != null,
                "numberOfEntitiesToGiveFeedbackToSetting cannot be null");
        if (numberOfEntitiesToGiveFeedbackToSetting == NumberOfEntitiesToGiveFeedbackToSetting.CUSTOM) {
            assertTrue(customNumberOfEntitiesToGiveFeedbackTo != null,
                    "customNumberOfEntitiesToGiveFeedbackTo must be set");
        }

        assertTrue(showResponsesTo != null, "showResponsesTo cannot be null");
        assertTrue(showGiverNameTo != null, "showGiverNameTo cannot be null");
        assertTrue(showRecipientNameTo != null, "showRecipientNameTo cannot be null");
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
        if (questionType == FeedbackQuestionType.CONSTSUM_OPTIONS
                || questionType == FeedbackQuestionType.CONSTSUM_RECIPIENTS) {
            details.setQuestionType(FeedbackQuestionType.CONSTSUM);
        }
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

    /**
     * Get feedback participants who can see responses.
     */
    public List<ViewerType> getShowResponsesTo() {
        List<ViewerType> showResponsesTo =
                this.convertToViewerType(this.showResponsesTo);

        // specially handling for contribution questions
        // TODO: remove the hack
        if (this.questionType == FeedbackQuestionType.CONTRIB
                && this.giverType == QuestionGiverType.STUDENTS
                && this.recipientType == QuestionRecipientType.OWN_TEAM_MEMBERS_INCLUDING_SELF
                && showResponsesTo.contains(ViewerType.OWN_TEAM_MEMBERS)) {
            // add the redundant participant type OWN_TEAM_MEMBERS even if it is just RECIPIENT_TEAM_MEMBERS
            // contribution question keep the redundancy for legacy reason
            showResponsesTo.add(ViewerType.RECEIVER_TEAM_MEMBERS);
        }

        return showResponsesTo;
    }

    public List<ViewerType> getShowGiverNameTo() {
        return this.convertToViewerType(showGiverNameTo);
    }

    public List<ViewerType> getShowRecipientNameTo() {
        return this.convertToViewerType(showRecipientNameTo);
    }

    /**
     * Converts a list of feedback visibility type to a list of feedback participant type.
     */
    private List<ViewerType> convertToViewerType(
            List<FeedbackVisibilityType> feedbackVisibilityTypes) {
        return feedbackVisibilityTypes.stream().map(feedbackVisibilityType -> {
            switch (feedbackVisibilityType) {
            case STUDENTS:
                return ViewerType.STUDENTS;
            case INSTRUCTORS:
                return ViewerType.INSTRUCTORS;
            case RECIPIENT:
                return ViewerType.RECEIVER;
            case GIVER_TEAM_MEMBERS:
                return ViewerType.OWN_TEAM_MEMBERS;
            case RECIPIENT_TEAM_MEMBERS:
                return ViewerType.RECEIVER_TEAM_MEMBERS;
            default:
                assert false : "Unknown feedbackVisibilityType" + feedbackVisibilityType;
                break;
            }
            return null;
        }).collect(Collectors.toList());
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
