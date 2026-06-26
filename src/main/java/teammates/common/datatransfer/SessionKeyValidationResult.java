package teammates.common.datatransfer;

import teammates.storage.entity.Student;

/**
 * Result of validating an encrypted student session key.
 *
 * @param student the student associated with the session key
 * @param sessionKey the session key
 */
public record SessionKeyValidationResult(Student student, SessionKey sessionKey) {
}
