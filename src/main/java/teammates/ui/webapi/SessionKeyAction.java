package teammates.ui.webapi;

/**
 * An action that is permitted with a valid session key.
 */
abstract class SessionKeyAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.SESSION_KEY;
    }

}
