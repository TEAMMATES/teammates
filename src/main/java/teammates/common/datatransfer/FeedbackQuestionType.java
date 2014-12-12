package teammates.common.datatransfer;

import teammates.common.util.Assumption;

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
    
    
    /**
     * Returns an instance of a corresponding Feedback*QuestionDetails class
     * @return FeedbackAbstractQuestionDetails
     */
    public FeedbackAbstractQuestionDetails getFeedbackQuestionDetailsInstance() {
        switch (this) {
        case TEXT:
            return new FeedbackTextQuestionDetails();
        case MCQ:
            return new FeedbackMcqQuestionDetails();
        case MSQ:
            return new FeedbackMsqQuestionDetails();
        case NUMSCALE:
            return new FeedbackNumericalScaleQuestionDetails();
        case CONSTSUM:
            return new FeedbackConstantSumQuestionDetails();
        case CONTRIB:
            return new FeedbackContributionQuestionDetails();
        default:
            Assumption.fail("Failed to instantiate Feedback*QuestionDetails instance for " + this.toString() + " question type.");
            return null;
        }
    }
    
    /**
     * Returns an instance of a corresponding Feedback*ResponseDetails class
     * @return FeedbackAbstractResponseDetails
     */
    public FeedbackAbstractResponseDetails getFeedbackResponseDetailsInstance() {
        switch (this) {
        case TEXT:
            return new FeedbackTextResponseDetails();
        case MCQ:
            return new FeedbackMcqResponseDetails();
        case MSQ:
            return new FeedbackMsqResponseDetails();
        case NUMSCALE:
            return new FeedbackNumericalScaleResponseDetails();
        case CONSTSUM:
            return new FeedbackConstantSumResponseDetails();
        case CONTRIB:
            return new FeedbackContributionResponseDetails();
        default:
            Assumption.fail("Failed to instantiate Feedback*ResponseDetails instance for " + this.toString() + " question type.");
            return null;
        }
    }
    
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

