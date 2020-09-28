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
     * Logged in user.
     */
    LOGGED_IN(1),

    /**
     * Admin masquerading as another user.
     */
    MASQUERADE(1),

    /**
     * All-access pass via secret key.
     */
    ALL_ACCESS(2);

    private final int level;

    AuthType(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

}
