package teammates.logic.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
import teammates.common.datatransfer.LinkKeyType;
import teammates.common.datatransfer.Provider;
import teammates.common.datatransfer.UserInfoCookie;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.LinkKeyUtil;
import teammates.common.util.StringHelper;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.UsersLogic;
import teammates.storage.entity.Account;
import teammates.storage.entity.Student;
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

        when(mockUsersLogic.getInstructorsByAccountId(any(UUID.class))).thenReturn(List.of());
        when(mockUsersLogic.getStudentsByAccountId(any(UUID.class))).thenReturn(List.of());

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
        Account account = createAccount("user@example.com");
        MockHttpServletRequest req = createRequestWithAuthCookie(account);
        when(mockAccountsLogic.getAccount(account.getId())).thenReturn(account);

        AuthContext authContext = userProvision.getAuthContextFromRequest(req);

        assertEquals(AuthType.LOGGED_IN, authContext.authType());
        assertEquals(account, authContext.account());
        assertEquals(account.getId(), authContext.account().getId());
        assertHasNoRoles(authContext);
    }

    @Test
    public void getAuthContextFromRequest_loggedInRequestWithRegKey_returnsLoggedInAccountContext() throws Exception {
        Account account = createAccount("user@example.com");
        UUID studentId = UUID.randomUUID();
        UUID feedbackSessionId = UUID.randomUUID();
        String regKey = "registration-key";
        Student student = mock(Student.class);
        when(student.getId()).thenReturn(studentId);
        when(student.getRegKey()).thenReturn(regKey);
        when(student.getAccount()).thenReturn(account);

        MockHttpServletRequest req = createRequestWithAuthCookie(account);
        req.addParam(Const.ParamsNames.REGKEY,
                LinkKeyUtil.encrypt(studentId, LinkKeyType.SUBMISSION, regKey, feedbackSessionId));
        when(mockAccountsLogic.getAccount(account.getId())).thenReturn(account);
        when(mockUsersLogic.getStudent(studentId)).thenReturn(student);

        AuthContext authContext = userProvision.getAuthContextFromRequest(req);

        assertEquals(AuthType.LOGGED_IN, authContext.authType());
        assertEquals(account, authContext.account());
        assertEquals(student, authContext.regKeyStudent());
        assertEquals(feedbackSessionId, authContext.linkKey().feedbackSessionId());
    }

    @Test
    public void getAuthContextFromRequest_encryptedSessionKey_returnsRegKeyStudentContext() throws Exception {
        UUID studentId = UUID.randomUUID();
        UUID feedbackSessionId = UUID.randomUUID();
        String regKey = "registration-key";
        Student student = mock(Student.class);
        when(student.getId()).thenReturn(studentId);
        when(student.getRegKey()).thenReturn(regKey);
        when(student.getAccount()).thenReturn(null);

        MockHttpServletRequest req = createRequest();
        req.addParam(Const.ParamsNames.REGKEY,
                LinkKeyUtil.encrypt(studentId, LinkKeyType.SUBMISSION, regKey, feedbackSessionId));
        when(mockUsersLogic.getStudent(studentId)).thenReturn(student);

        AuthContext authContext = userProvision.getAuthContextFromRequest(req);

        assertEquals(AuthType.REG_KEY, authContext.authType());
        assertEquals(student, authContext.regKeyStudent());
        assertEquals(studentId, authContext.linkKey().userId());
        assertEquals(LinkKeyType.SUBMISSION, authContext.linkKey().type());
        assertEquals(feedbackSessionId, authContext.linkKey().feedbackSessionId());
    }

    @Test
    public void getAuthContextFromRequest_adminCookie_returnsAdminContext() throws Exception {
        Account account = createAccount("admin@example.com");
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
        Account account = createAccount("maintainer@example.com");
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
        Account account = createAccount("user@example.com");
        MockHttpServletRequest req = createRequestWithAuthCookie(account);
        req.addHeader(Const.HeaderNames.MASQUERADE_ACCOUNT_ID, account.getId().toString());
        when(mockAccountsLogic.getAccount(account.getId())).thenReturn(account);

        UnauthorizedAccessException ex = assertThrows(
                UnauthorizedAccessException.class, () -> userProvision.getAuthContextFromRequest(req));

        assertEquals("Masquerade failed: user user@example.com does not have admin privilege", ex.getMessage());
    }

    @Test
    public void getAuthContextFromRequest_adminMasquerade_returnsTargetAccountContext() throws Exception {
        Account adminAccount = createAccount("admin@example.com");
        Account targetAccount = createAccount("target@example.com");
        MockHttpServletRequest req = createRequestWithAuthCookie(adminAccount);
        req.addHeader(Const.HeaderNames.MASQUERADE_ACCOUNT_ID, targetAccount.getId().toString());
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
        assertNull(userProvision.getUserInfo(new AuthContext(AuthType.PUBLIC, null, null, null, false, false)));
        verifyNoInteractions(mockUsersLogic);
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

    private static Account createAccount(String email) {
        Account account = new Account(
                Provider.TEAMMATES_DEV, "testUserSubject", "tenant-id", email);
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
