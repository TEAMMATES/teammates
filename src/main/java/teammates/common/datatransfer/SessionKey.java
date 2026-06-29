package teammates.common.datatransfer;

import java.util.UUID;

import teammates.common.exception.InvalidParametersException;

/**
 * Represents the encrypted session key payload for student session links.
 *
 * @param userId the student user ID
 * @param type the allowed session-link type
 * @param linkVersion the student's current link version
 * @param feedbackSessionId the feedback session associated with the link
 */
public record SessionKey(
        UUID userId,
        SessionKeyType type,
        int linkVersion,
        UUID feedbackSessionId) {

    /**
     * Validates that all required fields are present.
     */
    public void validate() throws InvalidParametersException {
        if (userId == null || type == null || feedbackSessionId == null) {
            throw new InvalidParametersException("Invalid encrypted session key");
        }
    }
}
