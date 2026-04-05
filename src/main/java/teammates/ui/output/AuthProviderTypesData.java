package teammates.ui.output;

import java.util.List;

import teammates.common.util.Config;

/**
 * Output format for the list of supported authentication provider types.
 */
public class AuthProviderTypesData extends ApiOutput {

    private final List<String> authProviderTypes;

    public AuthProviderTypesData() {
        this.authProviderTypes = Config.AUTH_PROVIDER_TYPES;
    }

    public List<String> getAuthProviderTypes() {
        return authProviderTypes;
    }
}
