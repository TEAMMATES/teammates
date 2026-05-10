package teammates.ui.webapi;

/**
 * An action that is accessible by the public (no authentication required).
   
 */
abstract class PublicAction extends Action {
    
  @Override
      AuthType getMinAuthLevel() {
                return AuthType.PUBLIC;
      }

    @Override
      void checkSpecificAccessControl() {
                // No access control needed for public actions
      }

}
