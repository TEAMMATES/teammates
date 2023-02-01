package teammates.common.util;

/**
 * Configurable login link options.
 */
public class LoginLinkOptions {

    private String userEmail;
    private String continueUrl;

    /**
     * Returns a builder for {@link LoginLinkOptions}.
     */
    public static Builder builder() {
        return new Builder();
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getContinueUrl() {
        return continueUrl;
    }

    /**
     * A Builder class for {@link LoginLinkOptions}.
     */
    public static class Builder {
        private final LoginLinkOptions loginLinkOptions;

        private Builder() {
            loginLinkOptions = new LoginLinkOptions();
        }

        public LoginLinkOptions.Builder withUserEmail(String userEmail) {
            assert userEmail != null;

            loginLinkOptions.userEmail = userEmail;
            return this;
        }

        public LoginLinkOptions.Builder withContinueUrl(String continueUrl) {
            assert continueUrl != null;

            loginLinkOptions.continueUrl = continueUrl;
            return this;
        }

        public LoginLinkOptions build() {
            return loginLinkOptions;
        }
    }
}
