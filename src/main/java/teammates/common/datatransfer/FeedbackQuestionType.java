package teammates.common.datatransfer;

public enum FeedbackQuestionType {
    
    /**
     * Feedback Question Type Definitions
     */
    TEXT(FeedbackTextQuestionDetails.class, FeedbackTextResponseDetails.class),
    MCQ(FeedbackMcqQuestionDetails.class, FeedbackMcqResponseDetails.class),
    MSQ(FeedbackMsqQuestionDetails.class, FeedbackMsqResponseDetails.class),
    NUMSCALE(FeedbackNumericalScaleQuestionDetails.class, FeedbackNumericalScaleResponseDetails.class),
    CONSTSUM(FeedbackConstantSumQuestionDetails.class, FeedbackConstantSumResponseDetails.class),
    CONTRIB(FeedbackContributionQuestionDetails.class, FeedbackContributionResponseDetails.class);
    
    
    private final Class<? extends FeedbackAbstractQuestionDetails> questionDetailsClass;
    private final Class<? extends FeedbackAbstractResponseDetails> responseDetailsClass;
    
    /**
     * Constructor for FeedbackQuestionType.
     * Pass in the corresponding questionDetailsClass and responseDetailsClass
     * @param questionDetailsClass
     * @param responseDetailsClass
     */
    private FeedbackQuestionType(Class<? extends FeedbackAbstractQuestionDetails> questionDetailsClass,
            Class<? extends FeedbackAbstractResponseDetails> responseDetailsClass) {
        this.questionDetailsClass = questionDetailsClass;
        this.responseDetailsClass = responseDetailsClass;
    }
    
    /**
     * Getter for corresponding Feedback*QuestionDetails class
     * @return Class<? extends FeedbackAbstractQuestionDetails>
     */
    public Class<? extends FeedbackAbstractQuestionDetails> getQuestionDetailsClass() {
        return questionDetailsClass;
    }
    
    /**
     * Getter for corresponding Feedback*ResponseDetails class
     * @return Class<? extends FeedbackAbstractResponseDetails>
     */
    public Class<? extends FeedbackAbstractResponseDetails> getResponseDetailsClass() {
        return responseDetailsClass;
    }
}

