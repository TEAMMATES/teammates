package teammates.storage.sqlapi;

import static teammates.common.util.Const.ERROR_CREATE_ENTITY_ALREADY_EXISTS;
import static teammates.common.util.Const.ERROR_UPDATE_NON_EXISTENT;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.LoginIssuer;

/**
 * Handles CRUD operations for issuer to provider name mappings.
 */
public final class LoginIssuerDb {

    private static final LoginIssuerDb instance = new LoginIssuerDb();

    private LoginIssuerDb() {
        // prevent initialization
    }

    public static LoginIssuerDb inst() {
        return instance;
    }

    public LoginIssuer getLoginIssuer(String issuerString) {
        assert issuerString != null;
        return HibernateUtil.get(LoginIssuer.class, issuerString);
    }

    public LoginIssuer createLoginIssuer(LoginIssuer loginIssuer)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert loginIssuer != null;

        if (!loginIssuer.isValid()) {
            throw new InvalidParametersException(loginIssuer.getInvalidityInfo());
        }

        if (getLoginIssuer(loginIssuer.getIssuer()) != null) {
            throw new EntityAlreadyExistsException(String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, loginIssuer));
        }

        HibernateUtil.persist(loginIssuer);
        return loginIssuer;
    }

    public LoginIssuer updateLoginIssuer(LoginIssuer loginIssuer)
            throws InvalidParametersException, EntityDoesNotExistException {
        assert loginIssuer != null;

        if (!loginIssuer.isValid()) {
            throw new InvalidParametersException(loginIssuer.getInvalidityInfo());
        }

        if (getLoginIssuer(loginIssuer.getIssuer()) == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT + loginIssuer);
        }

        return HibernateUtil.merge(loginIssuer);
    }

    public void deleteLoginIssuer(String issuerString) {
        LoginIssuer loginIssuer = getLoginIssuer(issuerString);
        if (loginIssuer != null) {
            HibernateUtil.remove(loginIssuer);
        }
    }
}
