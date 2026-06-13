package teammates.ui.webapi;

/**
 * An action that is permitted for logged-in users.
 */
abstract class LoggedInAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

}
