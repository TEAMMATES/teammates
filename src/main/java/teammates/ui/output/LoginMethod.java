package teammates.ui.output;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * The option for login method.
 */
public enum LoginMethod {
    GOOGLE("google"),
    DEV_SERVER("devserver");

    private final String method;

    LoginMethod(String method) {
        this.method = method;
    }

    @JsonValue
    public String getMethod() {
        return method;
    }
}
