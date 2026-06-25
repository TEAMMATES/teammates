package teammates.common.datatransfer;

import jakarta.annotation.Nullable;

/**
 * Result of evaluating access for a student session key.
 *
 * @param decision the decision of the access evaluation
 * @param message an optional message providing additional context for the decision
 */
public record SessionKeyAccessResult(
        SessionKeyAccessDecision decision,
        @Nullable String message) {
}
