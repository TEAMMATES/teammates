package teammates.common.datatransfer;

/**
 * Preflight decisions for student session links.
 */
public enum SessionKeyAccessDecision {
    ALLOW_UNREGISTERED,
    ALLOW_SIGNED_IN,
    SIGN_IN_REQUIRED,
    SIGN_IN_WITH_ANOTHER_ACCOUNT,
    INVALID_KEY
}
