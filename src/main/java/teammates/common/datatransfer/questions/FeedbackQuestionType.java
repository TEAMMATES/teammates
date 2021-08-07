package teammates.common.datatransfer.questions;

/**
 * Feedback Question Type Definitions.
 */
// CHECKSTYLE.OFF:JavadocVariable enum names are self-documenting
public enum FeedbackQuestionType {
    TEXT(FeedbackTextQuestionDetails.class, FeedbackTextResponseDetails.class),
    MCQ(FeedbackMcqQuestionDetails.class, FeedbackMcqResponseDetails.class),
    MSQ(FeedbackMsqQuestionDetails.class, FeedbackMsqResponseDetails.class),
    NUMSCALE(FeedbackNumericalScaleQuestionDetails.class, FeedbackNumericalScaleResponseDetails.class),

    CONSTSUM(FeedbackConstantSumQuestionDetails.class, FeedbackConstantSumResponseDetails.class),

    // In the back-end, only CONSTSUM and not its branched versions is recognized as a valid question type.
    // The branching here is done to make it simpler for the front-end to render the right items.
    CONSTSUM_OPTIONS(FeedbackConstantSumQuestionDetails.class, FeedbackConstantSumResponseDetails.class),
    CONSTSUM_RECIPIENTS(FeedbackConstantSumQuestionDetails.class, FeedbackConstantSumResponseDetails.class),

    CONTRIB(FeedbackContributionQuestionDetails.class, FeedbackContributionResponseDetails.class),
    RUBRIC(FeedbackRubricQuestionDetails.class, FeedbackRubricResponseDetails.class),
    RANK_OPTIONS(FeedbackRankOptionsQuestionDetails.class, FeedbackRankOptionsResponseDetails.class),
    RANK_RECIPIENTS(FeedbackRankRecipientsQuestionDetails.class, FeedbackRankRecipientsResponseDetails.class);

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

}
