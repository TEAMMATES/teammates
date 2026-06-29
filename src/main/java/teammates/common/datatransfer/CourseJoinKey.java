package teammates.common.datatransfer;

import java.util.UUID;

import teammates.common.exception.InvalidParametersException;

/**
 * Represents the encrypted course join key payload.
 *
 * @param userId the user ID of the student or instructor to join
 * @param linkVersion the user's current link version
 */
public record CourseJoinKey(
        UUID userId,
        int linkVersion) {

    /**
     * Validates that all required fields are present.
     */
    public void validate() throws InvalidParametersException {
        if (userId == null) {
            throw new InvalidParametersException("Invalid encrypted course join key");
        }
    }
}
