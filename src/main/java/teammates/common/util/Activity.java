package teammates.common.util;

/**
 * This is not used at the moment. It is for future reference. We plan
 * to pass Activity as an additional parameter to access control methods for
 * finer-grain access control. e.g., to block some instructors from viewing
 * results of an evaluation.
 */
public enum Activity {
    ADD, VIEW, UPDATE, DELETE
}
