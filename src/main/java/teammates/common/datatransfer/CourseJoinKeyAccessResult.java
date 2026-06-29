package teammates.common.datatransfer;

import jakarta.annotation.Nullable;

/**
 * Result of evaluating access for a course join key.
 *
 * @param decision the decision of the access evaluation
 * @param message an optional message providing additional context for the decision
 */
public record CourseJoinKeyAccessResult(
        CourseJoinKeyAccessDecision decision,
        @Nullable String message) {
}
