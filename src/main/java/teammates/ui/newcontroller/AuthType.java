package teammates.ui.newcontroller;

/**
 * Represents the type of authentication.
 */
public enum AuthType {

    /**
     * Unauthenticated user.
     */
    UNAUTHENTICATED(0),

    /**
     * Unregistered user who uses the application via regkey.
     */
    UNREGISTERED(1),

    /**
     * Registered user who uses the application via login.
     */
    REGISTERED(2),

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
