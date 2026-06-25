package teammates.common.datatransfer;

/**
 * Preflight decisions for student session links.
 */
public enum SessionKeyAccessDecision {
    ALLOW,
    SIGN_IN_REQUIRED,
    SIGN_IN_WITH_ANOTHER_ACCOUNT,
    INVALID_KEY
}
