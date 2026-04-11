package teammates.storage.sqlapi;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;

import org.mockito.MockedStatic;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.OidcProviderNameType;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.LoginIssuer;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link LoginIssuerDb}.
 */
public class LoginIssuerDbTest extends BaseTestCase {

    private final LoginIssuerDb loginIssuerDb = LoginIssuerDb.inst();

    private MockedStatic<HibernateUtil> mockHibernateUtil;

    @BeforeMethod
    public void setUpMethod() {
        mockHibernateUtil = mockStatic(HibernateUtil.class);
    }

    @AfterMethod
    public void teardownMethod() {
        mockHibernateUtil.close();
    }

    @Test
    public void testGetLoginIssuer_loginIssuerExists_success() {
        LoginIssuer loginIssuer = getTypicalLoginIssuer();

        mockHibernateUtil.when(() -> HibernateUtil.get(LoginIssuer.class, loginIssuer.getIssuer()))
                .thenReturn(loginIssuer);

        LoginIssuer actualLoginIssuer = loginIssuerDb.getLoginIssuer(loginIssuer.getIssuer());

        mockHibernateUtil.verify(() -> HibernateUtil.get(LoginIssuer.class, loginIssuer.getIssuer()));
        assertEquals(loginIssuer, actualLoginIssuer);
    }

    @Test
    public void testCreateLoginIssuer_loginIssuerDoesNotExist_success()
            throws InvalidParametersException, EntityAlreadyExistsException {
        LoginIssuer loginIssuer = getTypicalLoginIssuer();

        loginIssuerDb.createLoginIssuer(loginIssuer);

        mockHibernateUtil.verify(() -> HibernateUtil.persist(loginIssuer));
    }

    @Test
    public void testCreateLoginIssuer_loginIssuerAlreadyExists_throwsEntityAlreadyExistsException() {
        LoginIssuer loginIssuer = getTypicalLoginIssuer();
        mockHibernateUtil.when(() -> HibernateUtil.get(LoginIssuer.class, loginIssuer.getIssuer()))
                .thenReturn(loginIssuer);

        EntityAlreadyExistsException ex = assertThrows(EntityAlreadyExistsException.class,
                () -> loginIssuerDb.createLoginIssuer(loginIssuer));

        assertEquals("Trying to create an entity that exists: " + loginIssuer.toString(), ex.getMessage());
        mockHibernateUtil.verify(() -> HibernateUtil.persist(loginIssuer), never());
    }

    @Test
    public void testCreateLoginIssuer_invalidIssuer_throwsInvalidParametersException() {
        LoginIssuer loginIssuer = new LoginIssuer("http://accounts.google.com", OidcProviderNameType.GOOGLE);

        InvalidParametersException ex = assertThrows(InvalidParametersException.class,
                () -> loginIssuerDb.createLoginIssuer(loginIssuer));

        assertEquals(
                "\"http://accounts.google.com\" is not acceptable to TEAMMATES as a/an OIDC issuer because it is "
                        + "not in the correct format. An OIDC issuer must be a case-sensitive https URL with a "
                        + "host, may include a port and path, and cannot contain query parameters or fragments. "
                        + "The value of a/an OIDC issuer should be no longer than 2048 characters.",
                ex.getMessage());
        mockHibernateUtil.verify(() -> HibernateUtil.persist(loginIssuer), never());
    }

    @Test
    public void testUpdateLoginIssuer_loginIssuerExists_success()
            throws InvalidParametersException, EntityDoesNotExistException {
        LoginIssuer loginIssuer = getTypicalLoginIssuer();
        mockHibernateUtil.when(() -> HibernateUtil.get(LoginIssuer.class, loginIssuer.getIssuer()))
                .thenReturn(loginIssuer);
        mockHibernateUtil.when(() -> HibernateUtil.merge(loginIssuer)).thenReturn(loginIssuer);
        loginIssuer.setProviderName(OidcProviderNameType.MS_ENTRA);

        loginIssuerDb.updateLoginIssuer(loginIssuer);

        mockHibernateUtil.verify(() -> HibernateUtil.merge(loginIssuer));
    }

    @Test
    public void testUpdateLoginIssuer_loginIssuerDoesNotExist_throwsEntityDoesNotExistException() {
        LoginIssuer loginIssuer = getTypicalLoginIssuer();

        EntityDoesNotExistException ex = assertThrows(EntityDoesNotExistException.class,
                () -> loginIssuerDb.updateLoginIssuer(loginIssuer));

        assertEquals("Trying to update non-existent Entity: " + loginIssuer.toString(), ex.getMessage());
        mockHibernateUtil.verify(() -> HibernateUtil.merge(loginIssuer), never());
    }

    @Test
    public void testDeleteLoginIssuer_loginIssuerExists_success() {
        LoginIssuer loginIssuer = getTypicalLoginIssuer();
        mockHibernateUtil.when(() -> HibernateUtil.get(LoginIssuer.class, loginIssuer.getIssuer()))
                .thenReturn(loginIssuer);

        loginIssuerDb.deleteLoginIssuer(loginIssuer.getIssuer());

        mockHibernateUtil.verify(() -> HibernateUtil.remove(loginIssuer));
    }

    @Test
    public void testDeleteLoginIssuer_loginIssuerDoesNotExist_success() {
        String issuer = "https://accounts.google.com";

        loginIssuerDb.deleteLoginIssuer(issuer);

        mockHibernateUtil.verify(() -> HibernateUtil.get(LoginIssuer.class, issuer));
        mockHibernateUtil.verifyNoMoreInteractions();
    }

    private LoginIssuer getTypicalLoginIssuer() {
        return new LoginIssuer("https://accounts.google.com", OidcProviderNameType.GOOGLE);
    }
}
