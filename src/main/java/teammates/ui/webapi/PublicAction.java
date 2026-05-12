package teammates.ui.webapi;

/**
 * Base class for actions that are accessible to the public.
 */
abstract class PublicAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    void checkSpecificAccessControl() {
        // No specific access control needed for public actions.
    }

}
