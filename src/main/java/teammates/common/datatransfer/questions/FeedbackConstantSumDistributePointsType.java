package teammates.common.datatransfer.questions;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum that defines different distribute points options for constant sum questions.
 */
public enum FeedbackConstantSumDistributePointsType {
    /**
     * All options need to have different points.
     */
    DISTRIBUTE_ALL_UNEVENLY("All options"),
    /**
     * At least some options need to have different points.
     */
    DISTRIBUTE_SOME_UNEVENLY("At least some options"),
    /**
     * No restrictions.
     */
    NONE("None");

    private String displayedOption;

    FeedbackConstantSumDistributePointsType(String displayedOption) {
        this.displayedOption = displayedOption;
    }

    /**
     * Gets {@code displayedOption} that is associated with a particular distribute points option.
     */
    @JsonValue
    public String getDisplayedOption() {
        return displayedOption;
    }
}
