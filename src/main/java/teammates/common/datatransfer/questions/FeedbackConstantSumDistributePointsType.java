package teammates.common.datatransfer.questions;

import teammates.common.util.Const;

/**
 * Enum that defines different distribute points options for constant sum questions.
 */
public enum FeedbackConstantSumDistributePointsType {
    DISTRIBUTE_ALL_UNEVENLY(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMALLUNEVENDISTRIBUTION),
    DISTRIBUTE_SOME_UNEVENLY(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMSOMEUNEVENDISTRIBUTION),
    NONE(Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMNOUNEVENDISTRIBUTION);

    private String displayedOption;

    FeedbackConstantSumDistributePointsType(String displayedOption) {
        this.displayedOption = displayedOption;
    }

    /**
     * Gets {@code displayedOption} that is associated with a particular distribute points option.
     */
    public String getDisplayedOption() {
        return displayedOption;
    }
}
