package teammates.ui.output;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * The data for login methods supported by the application.
 */
public class LoginMethodsData implements ApiOutput {
    private final Set<LoginMethod> loginMethods;

    public LoginMethodsData(Set<String> loginMethods) {
        this.loginMethods = loginMethods.stream()
            .map(this::convertToLoginMethodEnum).collect(Collectors.toSet());
    }

    public Set<LoginMethod> getLoginMethods() {
        return loginMethods;
    }

    public LoginMethod convertToLoginMethodEnum(String method) {
        switch (method.toLowerCase()) {
            case "google":
                return LoginMethod.GOOGLE;
            case "devserver":
                return LoginMethod.DEV_SERVER;
            default:
                throw new IllegalArgumentException("Unsupported login method: " + method);
        }
    }

}
