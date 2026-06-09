package teammates.ui.output;

import java.util.Set;

import teammates.common.util.Config;

/**
 * The data for application configuration.
 */
public class ConfigData implements ApiOutput {
    private final Set<LoginMethod> loginMethods;

    public ConfigData() {
        this.loginMethods = Config.getSupportedLoginMethods();
    }

    public Set<LoginMethod> getLoginMethods() {
        return loginMethods;
    }

}
