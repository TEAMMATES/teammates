package teammates.logic.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import jakarta.servlet.http.Cookie;

import org.mockito.MockedStatic;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AuthContext;
import teammates.common.datatransfer.Provider;
import teammates.common.datatransfer.UserInfo;
import teammates.common.datatransfer.UserInfoCookie;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.StringHelper;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.UsersLogic;
import teammates.storage.entity.Account;
import teammates.test.BaseTestCase;
import teammates.test.MockHttpServletRequest;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.webapi.AuthType;

/**
 * SUT: {@link UserProvision}.
 */
public class UserProvisionTest extends BaseTestCase {

    private UserProvision userProvision;
    private UsersLogic mockUsersLogic;
    private AccountsLogic mockAccountsLogic;
    private MockedStatic<Config> mockConfigStatic;
    private MockedStatic<UsersLogic> mockUsersLogicStatic;
    private MockedStatic<AccountsLogic> mockAccountsLogicStatic;

    @BeforeClass
    public void setUpClass() {
        // Ensure the singleton is initialized before static collaborators are mocked in individual tests.
        UserProvision.inst();
    }

    @BeforeMethod
    public void setUpMethod() {
        mockUsersLogic = mock(UsersLogic.class);
        mockUsersLogicStatic = mockStatic(UsersLogic.class);
        mockUsersLogicStatic.when(UsersLogic::inst).thenReturn(mockUsersLogic);

        mockAccountsLogic = mock(AccountsLogic.class);
        mockAccountsLogicStatic = mockStatic(AccountsLogic.class);
        mockAccountsLogicStatic.when(AccountsLogic::inst).thenReturn(mockAccountsLogic);

        userProvision = new UserProvision();

        when(mockUsersLogic.isInstructorInAnyCourse(anyString())).thenReturn(false);
        when(mockUsersLogic.isStudentInAnyCourse(anyString())).thenReturn(false);
        when(mockAccountsLogic.getAccountForGoogleId(anyString())).thenReturn(null);

        mockConfigStatic = mockStatic(Config.class);
        mockConfigStatic.when(Config::getAppAdmins).thenReturn(List.of());
        mockConfigStatic.when(Config::getAppMaintainers).thenReturn(List.of());
    }

    @AfterMethod
    public void tearDownMethod() {
        mockConfigStatic.close();
        mockUsersLogicStatic.close();
        mockAccountsLogicStatic.close();
    }

    @Test
    public void getAuthContextFromRequest_noCookie_returnsPublicContext() throws Exception {
        AuthContext authContext = userProvision.getAuthContextFromRequest(createRequest());

        assertEquals(AuthType.PUBLIC, authContext.authType());
        assertNull(authContext.account());
        assertFalse(authContext.isAdmin());
        assertFalse(authContext.isMaintainer());
    }

    @Test
    public void getAuthContextFromRequest_validCookie_returnsLoggedInAccountContext() throws Exception {
        Account account = createAccount("user-id", "user@example.com");
        MockHttpServletRequest req = createRequestWithAuthCookie(account);
        when(mockAccountsLogic.getAccount(account.getId())).thenReturn(account);

        AuthContext authContext = userProvision.getAuthContextFromRequest(req);

        assertEquals(AuthType.LOGGED_IN, authContext.authType());
        assertEquals(account, authContext.account());
        assertEquals("user-id", authContext.account().getGoogleId());
        assertHasNoRoles(authContext);
    }

    @Test
    public void getAuthContextFromRequest_loggedInRequestWithRegKey_returnsLoggedInAccountContext() throws Exception {
        Account account = createAccount("user-id", "user@example.com");
        MockHttpServletRequest req = createRequestWithAuthCookie(account);
        req.addParam(Const.ParamsNames.REGKEY, "registration-key");
        when(mockAccountsLogic.getAccount(account.getId())).thenReturn(account);

        AuthContext authContext = userProvision.getAuthContextFromRequest(req);

        assertEquals(AuthType.LOGGED_IN, authContext.authType());
        assertEquals(account, authContext.account());
        assertNull(authContext.regKeyUser());
    }

    @Test
    public void getAuthContextFromRequest_adminCookie_returnsAdminContext() throws Exception {
        Account account = createAccount("admin-id", "admin@example.com");
        MockHttpServletRequest req = createRequestWithAuthCookie(account);
        when(mockAccountsLogic.getAccount(account.getId())).thenReturn(account);
        mockConfigStatic.when(Config::getAppAdmins).thenReturn(List.of(account.getEmail()));

        AuthContext authContext = userProvision.getAuthContextFromRequest(req);

        assertEquals(AuthType.LOGGED_IN, authContext.authType());
        assertEquals(account, authContext.account());
        assertHasRoles(authContext, Role.ADMIN);
    }

    @Test
    public void getAuthContextFromRequest_maintainerCookie_returnsMaintainerContext() throws Exception {
        Account account = createAccount("maintainer-id", "maintainer@example.com");
        MockHttpServletRequest req = createRequestWithAuthCookie(account);
        when(mockAccountsLogic.getAccount(account.getId())).thenReturn(account);
        mockConfigStatic.when(Config::getAppMaintainers).thenReturn(List.of(account.getEmail()));

        AuthContext authContext = userProvision.getAuthContextFromRequest(req);

        assertEquals(AuthType.LOGGED_IN, authContext.authType());
        assertEquals(account, authContext.account());
        assertHasRoles(authContext, Role.MAINTAINER);
    }

    @Test
    public void getAuthContextFromRequest_nonAdminMasquerade_throwsUnauthorizedAccessException() {
        Account account = createAccount("user-id", "user@example.com");
        MockHttpServletRequest req = createRequestWithAuthCookie(account);
        req.addParam(Const.ParamsNames.MASQUERADE_ACCOUNT_ID, "target-id");
        when(mockAccountsLogic.getAccount(account.getId())).thenReturn(account);

        UnauthorizedAccessException ex = assertThrows(
                UnauthorizedAccessException.class, () -> userProvision.getAuthContextFromRequest(req));

        assertEquals("Masquerade failed: user user@example.com does not have admin privilege", ex.getMessage());
    }

    @Test
    public void getAuthContextFromRequest_adminMasquerade_returnsTargetAccountContext() throws Exception {
        Account adminAccount = createAccount("admin-id", "admin@example.com");
        Account targetAccount = createAccount("target-id", "target@example.com");
        MockHttpServletRequest req = createRequestWithAuthCookie(adminAccount);
        req.addParam(Const.ParamsNames.MASQUERADE_ACCOUNT_ID, targetAccount.getId().toString());
        when(mockAccountsLogic.getAccount(adminAccount.getId())).thenReturn(adminAccount);
        when(mockAccountsLogic.getAccount(targetAccount.getId())).thenReturn(targetAccount);
        mockConfigStatic.when(Config::getAppAdmins).thenReturn(List.of(adminAccount.getEmail()));

        AuthContext authContext = userProvision.getAuthContextFromRequest(req);

        assertEquals(AuthType.MASQUERADE, authContext.authType());
        assertEquals(targetAccount, authContext.account());
        assertHasNoRoles(authContext);
    }

    @Test
    public void getUserInfo_nullOrPublicContext_returnsNull() {
        assertNull(userProvision.getUserInfo(null));
        assertNull(userProvision.getUserInfo(new AuthContext(AuthType.PUBLIC, null, null, false, false)));
        verifyNoInteractions(mockUsersLogic);
    }

    @Test
    public void getUserInfo_loggedInContext_returnsRolesForAccountGoogleId() {
        Account account = createAccount("user-id", "user@example.com");
        when(mockUsersLogic.isInstructorInAnyCourse(account.getGoogleId())).thenReturn(true);
        when(mockUsersLogic.isStudentInAnyCourse(account.getGoogleId())).thenReturn(true);
        AuthContext authContext = new AuthContext(AuthType.LOGGED_IN, account, null, true, true);

        UserInfo userInfo = userProvision.getUserInfo(authContext);

        assertEquals(account.getGoogleId(), userInfo.id);
        assertEquals(account.getId(), userInfo.accountId);
        assertTrue(userInfo.isAdmin);
        assertTrue(userInfo.isMaintainer);
        assertTrue(userInfo.isInstructor);
        assertTrue(userInfo.isStudent);
    }

    private static MockHttpServletRequest createRequest() {
        return new MockHttpServletRequest("GET", "/test");
    }

    private static MockHttpServletRequest createRequestWithAuthCookie(Account account) {
        MockHttpServletRequest req = createRequest();
        UserInfoCookie userInfoCookie = new UserInfoCookie(account.getId());
        String cookieValue = StringHelper.encrypt(JsonUtils.toCompactJson(userInfoCookie));
        req.addCookie(new Cookie(Const.SecurityConfig.AUTH_COOKIE_NAME, cookieValue));
        return req;
    }

    private static Account createAccount(String googleId, String email) {
        Account account = new Account(
                googleId, Provider.TEAMMATES_DEV, "testUserSubject", "tenant-id",
                "Test User", email);
        account.setId(UUID.randomUUID());
        return account;
    }

    private static void assertHasNoRoles(AuthContext authContext) {
        assertHasRoles(authContext);
    }

    private static void assertHasRoles(AuthContext authContext, Role... expectedRoles) {
        List<Role> expected = List.of(expectedRoles);
        assertEquals(expected.contains(Role.ADMIN), authContext.isAdmin());
        assertEquals(expected.contains(Role.MAINTAINER), authContext.isMaintainer());
    }

    private enum Role {
        ADMIN, MAINTAINER
    }

}
