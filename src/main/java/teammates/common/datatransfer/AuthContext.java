package teammates.common.datatransfer;

import java.util.UUID;

/**
 * Represents the authentication context of a user.
 *
 * @param id           The user's Google ID.
 * @param accountId    The user's account ID.
 * @param isAdmin      Indicates whether the user has admin privilege.
 * @param isInstructor Indicates whether the user has instructor privilege.
 * @param isStudent    Indicates whether the user has student privilege.
 * @param isMaintainer Indicates whether the user has maintainer privilege.
 */
public record AuthContext(
        String id,
        UUID accountId,
        boolean isAdmin,
        boolean isInstructor,
        boolean isStudent,
        boolean isMaintainer) {
}
