package teammates.logic.email.model;

/**
 * Represents a named email contact for email rendering.
 *
 * @param name the name of the contact
 * @param email the email address of the contact
 */
public record EmailContact(
        String name,
        String email
) {
}
