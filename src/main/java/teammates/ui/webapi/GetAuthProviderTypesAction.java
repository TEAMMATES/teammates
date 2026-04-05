package teammates.ui.webapi;

import teammates.ui.output.AuthProviderTypesData;

/**
 * Action: gets the list of supported authentication provider types.
 */
public class GetAuthProviderTypesAction extends Action {

    @Override
    public AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() {
        // Auth provider types are available to everyone
    }

    @Override
    public JsonResult execute() {
        return new JsonResult(new AuthProviderTypesData());
    }

}
