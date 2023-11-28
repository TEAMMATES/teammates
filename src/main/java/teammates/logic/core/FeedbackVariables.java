package teammates.logic.core;

public class FeedbackVariables {
    protected FeedbackResponsesLogic frLogic;
    protected FeedbackSessionsLogic fsLogic;
    protected DeadlineExtensionsLogic deLogic;
    protected FeedbackResponseCommentsLogic frcLogic;
    public FeedbackQuestionsLogic fqLogic;

    void initLogicDependencies() {
        frLogic = FeedbackResponsesLogic.inst();
        fsLogic = FeedbackSessionsLogic.inst();
        deLogic = DeadlineExtensionsLogic.inst();
        fqLogic = FeedbackQuestionsLogic.inst();
        frcLogic = FeedbackResponseCommentsLogic.inst();
    }
}
