package teammates.common.datatransfer;

import java.util.UUID;

import teammates.common.exception.InvalidParametersException;

/**
 * Represents the encrypted course join key payload.
 *
 * @param userId the user ID of the student or instructor to join
 * @param regKey the user's current raw registration key
 */
public record CourseJoinKey(
        UUID userId,
        String regKey) {

    /**
     * Validates that all required fields are present.
     */
    public void validate() throws InvalidParametersException {
        if (userId == null || regKey == null) {
            throw new InvalidParametersException("Invalid encrypted course join key");
        }
    }
}
