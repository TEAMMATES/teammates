package teammates.ui.output;

import java.util.Set;

import teammates.common.util.Config;

/**
 * The data for application configuration.
 */
public class ConfigData implements ApiOutput {
    private final Set<LoginMethod> loginMethods;
    private final String frontendUrl;
    private final String supportEmail;

    public ConfigData() {
        this.loginMethods = Config.getSupportedLoginMethods();
        this.frontendUrl = Config.APP_FRONTEND_URL;
        this.supportEmail = Config.SUPPORT_EMAIL;
    }

    public Set<LoginMethod> getLoginMethods() {
        return loginMethods;
    }

    public String getSupportEmail() {
        return supportEmail;
    }

    public String getFrontendUrl() {
        return frontendUrl;
    }

}
