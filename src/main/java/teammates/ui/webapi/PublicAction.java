package teammates.ui.webapi;

/**
 * Base class for actions that are accessible to the public.
 *
 * <p>This class ensures that the minimum authentication level is set to
 * {@code AuthType.PUBLIC} and provides a default empty implementation
 * for access control checks. Subclasses only need to implement the
 * {@code execute()} method.
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
