package teammates.ui.output;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The option for login method.
 */
public enum LoginMethod {
    GOOGLE("google"),
    MICROSOFT("microsoft"),
    DEV_SERVER("devserver");

    private final String method;

    LoginMethod(String method) {
        this.method = method;
    }

    @JsonValue
    public String getMethod() {
        return method;
    }

    /**
     * Converts a string to a LoginMethod enum value. The comparison is case-insensitive.
     */
    public static LoginMethod fromString(String method) {
        for (LoginMethod loginMethod : values()) {
            if (loginMethod.method.equalsIgnoreCase(method)) {
                return loginMethod;
            }
        }
        throw new IllegalArgumentException("Unsupported login method: " + method);
    }
}
