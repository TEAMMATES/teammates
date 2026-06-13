package teammates.ui.webapi;

/**
 * An action that is permitted with a valid registration key.
 */
abstract class RegKeyAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.REG_KEY;
    }

}
