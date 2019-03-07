package teammates.common.datatransfer.questions;

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
