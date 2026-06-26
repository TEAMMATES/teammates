package teammates.ui.output;

import java.util.Set;

import teammates.common.util.Config;

/**
 * The data for application configuration.
 */
public class ConfigData implements ApiOutput {
    private final Set<LoginMethod> loginMethods;
    private final String frontendUrl;

    public ConfigData() {
        this.loginMethods = Config.getSupportedLoginMethods();
        this.frontendUrl = Config.APP_FRONTEND_URL;
    }

    public Set<LoginMethod> getLoginMethods() {
        return loginMethods;
    }

    public String getFrontendUrl() {
        return frontendUrl;
    }

}
