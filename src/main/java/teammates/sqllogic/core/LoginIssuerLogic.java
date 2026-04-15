package teammates.sqllogic.core;

import teammates.common.datatransfer.OidcProviderNameType;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.sqlapi.LoginIssuerDb;
import teammates.storage.sqlentity.LoginIssuer;

/**
 * Handles operations related to issuer to provider name mappings.
 */
public final class LoginIssuerLogic {

    private static final LoginIssuerLogic instance = new LoginIssuerLogic();

    private LoginIssuerDb issuerProviderMappingsDb;

    private LoginIssuerLogic() {
        // prevent initialization
    }

    public static LoginIssuerLogic inst() {
        return instance;
    }

    void initLogicDependencies(LoginIssuerDb issuerProviderMappingsDb) {
        this.issuerProviderMappingsDb = issuerProviderMappingsDb;
    }

    public LoginIssuer getLoginIssuer(String issuerString) {
        assert issuerString != null;
        return issuerProviderMappingsDb.getLoginIssuer(issuerString);
    }

    public OidcProviderNameType getProviderNameForIssuer(String issuerString) {
        LoginIssuer loginIssuer = getLoginIssuer(issuerString);
        return loginIssuer == null ? null : loginIssuer.getProviderName();
    }

    public LoginIssuer createLoginIssuer(LoginIssuer loginIssuer)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert loginIssuer != null;
        return issuerProviderMappingsDb.createLoginIssuer(loginIssuer);
    }

    public void deleteLoginIssuer(String issuerString) {
        assert issuerString != null;
        issuerProviderMappingsDb.deleteLoginIssuer(issuerString);
    }
}
