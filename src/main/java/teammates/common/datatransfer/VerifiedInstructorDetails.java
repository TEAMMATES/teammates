package teammates.common.datatransfer;

/**
 * The verified instructor details associated with an approved account verification request.
 *
 * @param name the verified instructor name
 * @param email the verified instructor email
 */
public record VerifiedInstructorDetails(String name, String email) {
}
