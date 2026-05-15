package teammates.ui.webapi;

/**
 * Represents the type of authentication.
 */
public enum AuthType {

    /**
     * Public access.
     */
    PUBLIC(0),

    /**
     * Access via registration key.
     */
    REG_KEY(1),

    /**
     * Logged in user.
     */
    LOGGED_IN(2),

    /**
     * Admin masquerading as another user.
     */
    MASQUERADE(2),

    /**
     * Verified automated service (cron/worker).
     */
    AUTOMATED_SERVICE(2),

    /**
     * All-access pass via secret key.
     */
    ALL_ACCESS(3);

    private final int level;

    AuthType(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

}
