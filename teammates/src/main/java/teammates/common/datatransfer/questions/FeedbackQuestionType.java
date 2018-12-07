package teammates.common.datatransfer.questions;

import java.util.Map;

import teammates.common.util.Assumption;
import teammates.common.util.Logger;

/**
 * Feedback Question Type Definitions.
 */
public enum FeedbackQuestionType {
    TEXT(FeedbackTextQuestionDetails.class, FeedbackTextResponseDetails.class),
    MCQ(FeedbackMcqQuestionDetails.class, FeedbackMcqResponseDetails.class),
    MSQ(FeedbackMsqQuestionDetails.class, FeedbackMsqResponseDetails.class),
    NUMSCALE(FeedbackNumericalScaleQuestionDetails.class, FeedbackNumericalScaleResponseDetails.class),
    CONSTSUM(FeedbackConstantSumQuestionDetails.class, FeedbackConstantSumResponseDetails.class),
    CONTRIB(FeedbackContributionQuestionDetails.class, FeedbackContributionResponseDetails.class),
    RUBRIC(FeedbackRubricQuestionDetails.class, FeedbackRubricResponseDetails.class),
    RANK_OPTIONS(FeedbackRankOptionsQuestionDetails.class, FeedbackRankOptionsResponseDetails.class),
    RANK_RECIPIENTS(FeedbackRankRecipientsQuestionDetails.class, FeedbackRankRecipientsResponseDetails.class);

    private static final Logger log = Logger.getLogger();

    private final Class<? extends FeedbackQuestionDetails> questionDetailsClass;
    private final Class<? extends FeedbackResponseDetails> responseDetailsClass;

    /**
     * Constructor for FeedbackQuestionType.
     * Pass in the corresponding questionDetailsClass and responseDetailsClass
     */
    FeedbackQuestionType(Class<? extends FeedbackQuestionDetails> questionDetailsClass,
                         Class<? extends FeedbackResponseDetails> responseDetailsClass) {
        this.questionDetailsClass = questionDetailsClass;
        this.responseDetailsClass = responseDetailsClass;
    }

    /**
     * Returns an instance of a corresponding Feedback*QuestionDetails class.
     *
     * @return FeedbackQuestionDetails
     */
    public FeedbackQuestionDetails getFeedbackQuestionDetailsInstance() {
        return getFeedbackQuestionDetailsInstance(null, null);
    }

    public FeedbackQuestionDetails getFeedbackQuestionDetailsInstance(String questionText,
                                                                      Map<String, String[]> requestParameters) {
        FeedbackQuestionDetails feedbackQuestionDetails = null;

        switch (this) {
        case TEXT:
            feedbackQuestionDetails = new FeedbackTextQuestionDetails();
            break;
        case MCQ:
            feedbackQuestionDetails = new FeedbackMcqQuestionDetails();
            break;
        case MSQ:
            feedbackQuestionDetails = new FeedbackMsqQuestionDetails();
            break;
        case NUMSCALE:
            feedbackQuestionDetails = new FeedbackNumericalScaleQuestionDetails();
            break;
        case CONSTSUM:
            feedbackQuestionDetails = new FeedbackConstantSumQuestionDetails();
            break;
        case CONTRIB:
            feedbackQuestionDetails = new FeedbackContributionQuestionDetails();
            break;
        case RUBRIC:
            feedbackQuestionDetails = new FeedbackRubricQuestionDetails();
            break;
        case RANK_OPTIONS:
            feedbackQuestionDetails = new FeedbackRankOptionsQuestionDetails();
            break;
        case RANK_RECIPIENTS:
            feedbackQuestionDetails = new FeedbackRankRecipientsQuestionDetails();
            break;
        default:
            Assumption.fail("Failed to instantiate Feedback*QuestionDetails instance for "
                            + this.toString() + " question type.");
            return null;
        }

        if (questionText != null && requestParameters != null) {
            feedbackQuestionDetails.setQuestionText(questionText);
            feedbackQuestionDetails.extractQuestionDetails(requestParameters, this);
        }

        return feedbackQuestionDetails;
    }

    /**
     * Returns an instance of a corresponding Feedback*ResponseDetails class.
     *
     * @return FeedbackResponseDetails
     */
    public FeedbackResponseDetails getFeedbackResponseDetailsInstance(
            FeedbackQuestionDetails questionDetails, String[] answer, Map<String, String[]> requestParameters,
            int questionIndx, int responseIndx) {
        FeedbackResponseDetails feedbackResponseDetails = null;

        switch (this) {
        case TEXT:
            feedbackResponseDetails = new FeedbackTextResponseDetails();
            break;
        case MCQ:
            feedbackResponseDetails = new FeedbackMcqResponseDetails();
            break;
        case MSQ:
            feedbackResponseDetails = new FeedbackMsqResponseDetails();
            break;
        case NUMSCALE:
            feedbackResponseDetails = new FeedbackNumericalScaleResponseDetails();
            break;
        case CONSTSUM:
            feedbackResponseDetails = new FeedbackConstantSumResponseDetails();
            break;
        case CONTRIB:
            feedbackResponseDetails = new FeedbackContributionResponseDetails();
            break;
        case RUBRIC:
            feedbackResponseDetails = new FeedbackRubricResponseDetails();
            break;
        case RANK_OPTIONS:
            feedbackResponseDetails = new FeedbackRankOptionsResponseDetails();
            break;
        case RANK_RECIPIENTS:
            feedbackResponseDetails = new FeedbackRankRecipientsResponseDetails();
            break;
        default:
            Assumption.fail("Failed to instantiate Feedback*ResponseDetails instance for "
                            + this.toString() + " question type.");
            return null;
        }

        try {
            switch (this) {
            case MCQ:
                ((FeedbackMcqResponseDetails) feedbackResponseDetails)
                        .extractResponseDetails(this, questionDetails, answer, requestParameters,
                                                questionIndx, responseIndx);
                break;
            case MSQ:
                ((FeedbackMsqResponseDetails) feedbackResponseDetails)
                        .extractResponseDetails(this, questionDetails, answer, requestParameters,
                                                questionIndx, responseIndx);
                break;
            default:
                feedbackResponseDetails.extractResponseDetails(this, questionDetails, answer);
                break;
            }
        } catch (Exception e) {
            log.warning("Failed to extract response details.\n" + e.toString());
            return null;
        }

        return feedbackResponseDetails;
    }

    /**
     * Getter for corresponding Feedback*QuestionDetails class.
     *
     * @return Class<? extends FeedbackQuestionDetails>
     */
    public Class<? extends FeedbackQuestionDetails> getQuestionDetailsClass() {
        return questionDetailsClass;
    }

    /**
     * Getter for corresponding Feedback*ResponseDetails class.
     *
     * @return Class<? extends FeedbackResponseDetails>
     */
    public Class<? extends FeedbackResponseDetails> getResponseDetailsClass() {
        return responseDetailsClass;
    }

    /**
     * Returns CONSTSUM if passed CONSTSUM_OPTION or CONSTSUM_RECIPIENT as argument.
     * Any other string is returned as is.
     */
    public static String standardizeIfConstSum(String questionType) {
        if ("CONSTSUM_OPTION".equals(questionType) || "CONSTSUM_RECIPIENT".equals(questionType)) {
            return "CONSTSUM";
        }
        return questionType;
    }
}
