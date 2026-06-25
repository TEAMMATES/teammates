package teammates.common.datatransfer;

import teammates.common.exception.InvalidParametersException;

import java.util.UUID;

/**
 * Represents the encrypted session key payload for student session links.
 *
 * @param userId the student user ID
 * @param type the allowed session-link type
 * @param regKey the student's current raw registration key
 * @param feedbackSessionId the feedback session associated with the link
 */
public record LinkKey(
        UUID userId,
        LinkKeyType type,
        String regKey,
        UUID feedbackSessionId) {

    /**
     * Validates that all required fields are present.
     */
    public void validate() throws InvalidParametersException {
        if (userId == null || type == null || regKey == null || feedbackSessionId == null) {
            throw new InvalidParametersException("Invalid encrypted session key");
        }
    }
}
