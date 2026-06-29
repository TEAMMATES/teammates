package teammates.common.datatransfer;

/**
 * Preflight decisions for course join links.
 */
public enum CourseJoinKeyAccessDecision {
    VALID,
    ALREADY_JOINED,
    SIGN_IN_REQUIRED,
    INVALID_KEY
}
