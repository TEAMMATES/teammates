package teammates.logic.api;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.mockito.MockedStatic;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AuthContext;
import teammates.common.datatransfer.UserInfoCookie;
import teammates.common.util.Config;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.UsersLogic;
import teammates.storage.entity.Account;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link UserProvision}.
 */
public class UserProvisionTest extends BaseTestCase {

    private UserProvision userProvision;
    private UsersLogic mockUsersLogic;
    private MockedStatic<Config> mockConfigStatic;
    private MockedStatic<UsersLogic> mockUsersLogicStatic;
    private MockedStatic<AccountsLogic> mockAccountsLogicStatic;

    @BeforeClass
    public void setUpClass() {
        // We need to ensure the UserProvision class' static initialiser has run before setUpMethod() (below) runs.
        // This guarantees the singleton holds a reference to a real UsersLogic instance.
        // Otherwise, the singleton may initialize during setUpMethod() while UsersLogic is mocked, capturing
        // the mock instead. Since the mock is only active for the duration of each test, any other test class
        // that calls UserProvision.inst() directly would receive a singleton with a stale, uncontrolled mock.
        UserProvision.inst();
    }

    @BeforeMethod
    public void setUpMethod() {
        mockUsersLogic = mock(UsersLogic.class);
        mockUsersLogicStatic = mockStatic(UsersLogic.class);
        mockUsersLogicStatic.when(UsersLogic::inst).thenReturn(mockUsersLogic);

        AccountsLogic mockAccountsLogic = mock(AccountsLogic.class);
        mockAccountsLogicStatic = mockStatic(AccountsLogic.class);
        mockAccountsLogicStatic.when(AccountsLogic::inst).thenReturn(mockAccountsLogic);

        userProvision = new UserProvision();

        // Default mock to return false for all IDs, individual tests will override as needed
        when(mockUsersLogic.isInstructorInAnyCourse(anyString())).thenReturn(false);
        when(mockUsersLogic.isStudentInAnyCourse(anyString())).thenReturn(false);
        // Default mock: no account found for any googleId
        when(mockAccountsLogic.getAccountForGoogleId(anyString())).thenReturn((Account) null);

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
    public void testGetCurrentUser_nullUic_returnsNull() {
        assertNull(userProvision.getCurrentUserContext(null));
    }

    @Test
    public void testGetCurrentUser_invalidCookie_returnsNull() {
        assertNull(userProvision.getCurrentUserContext(createMockInvalidCookie()));
    }

    @Test
    public void testGetCurrentUser_instructor_returnsUserInfoWithInstructorRole() {
        String userId = "instructor-user-id";
        when(mockUsersLogic.isInstructorInAnyCourse(userId)).thenReturn(true);

        AuthContext authContext = userProvision.getCurrentUserContext(createMockValidCookie(userId));

        assertEquals(userId, authContext.id());
        assertHasRoles(authContext, Role.INSTRUCTOR);
    }

    @Test
    public void testGetCurrentUser_student_returnsUserInfoWithStudentRole() {
        String userId = "student-user-id";
        when(mockUsersLogic.isStudentInAnyCourse(userId)).thenReturn(true);

        AuthContext authContext = userProvision.getCurrentUserContext(createMockValidCookie(userId));

        assertEquals(userId, authContext.id());
        assertHasRoles(authContext, Role.STUDENT);
    }

    @Test
    public void testGetCurrentUser_admin_returnsUserInfoWithAdminRole() {
        String adminUserId = "admin-user-id";
        mockConfigStatic.when(Config::getAppAdmins).thenReturn(List.of(adminUserId));

        AuthContext authContext = userProvision.getCurrentUserContext(createMockValidCookie(adminUserId));

        assertEquals(adminUserId, authContext.id());
        assertHasRoles(authContext, Role.ADMIN);
    }

    @Test
    public void testGetCurrentUser_maintainer_returnsUserInfoWithMaintainerRole() {
        String maintainerUserId = "maintainer-user-id";
        mockConfigStatic.when(Config::getAppMaintainers).thenReturn(List.of(maintainerUserId));

        AuthContext authContext = userProvision.getCurrentUserContext(createMockValidCookie(maintainerUserId));

        assertEquals(maintainerUserId, authContext.id());
        assertHasRoles(authContext, Role.MAINTAINER);
    }

    @Test
    public void testGetCurrentUser_unregistered_returnsUserInfoWithNoRoles() {
        String userId = "unregistered-user-id";

        AuthContext authContext = userProvision.getCurrentUserContext(createMockValidCookie(userId));

        assertEquals(userId, authContext.id());
        assertHasNoRoles(authContext);
    }

    @Test
    public void testGetCurrentUser_instructorAndStudent_returnsBothRolesTrue() {
        String userId = "instructor-student-user-id";
        when(mockUsersLogic.isInstructorInAnyCourse(userId)).thenReturn(true);
        when(mockUsersLogic.isStudentInAnyCourse(userId)).thenReturn(true);

        AuthContext authContext = userProvision.getCurrentUserContext(createMockValidCookie(userId));

        assertEquals(userId, authContext.id());
        assertHasRoles(authContext, Role.INSTRUCTOR, Role.STUDENT);
    }

    @Test
    public void testGetCurrentUser_adminAndMaintainer_returnsBothRolesTrue() {
        String userId = "admin-maintainer-user-id";
        mockConfigStatic.when(Config::getAppAdmins).thenReturn(List.of(userId));
        mockConfigStatic.when(Config::getAppMaintainers).thenReturn(List.of(userId));

        AuthContext authContext = userProvision.getCurrentUserContext(createMockValidCookie(userId));

        assertEquals(userId, authContext.id());
        assertHasRoles(authContext, Role.ADMIN, Role.MAINTAINER);
    }

    @Test
    public void testGetCurrentUser_instructorStudentAndMaintainer_returnsThreeRolesTrue() {
        String userId = "instructor-student-maintainer-user-id";
        when(mockUsersLogic.isInstructorInAnyCourse(userId)).thenReturn(true);
        when(mockUsersLogic.isStudentInAnyCourse(userId)).thenReturn(true);
        mockConfigStatic.when(Config::getAppMaintainers).thenReturn(List.of(userId));

        AuthContext authContext = userProvision.getCurrentUserContext(createMockValidCookie(userId));

        assertEquals(userId, authContext.id());
        assertHasRoles(authContext, Role.INSTRUCTOR, Role.STUDENT, Role.MAINTAINER);
    }

    @Test
    public void testGetCurrentUser_allRoles_returnsAllRolesTrue() {
        String userId = "all-roles-user-id";
        when(mockUsersLogic.isInstructorInAnyCourse(userId)).thenReturn(true);
        when(mockUsersLogic.isStudentInAnyCourse(userId)).thenReturn(true);
        mockConfigStatic.when(Config::getAppAdmins).thenReturn(List.of(userId));
        mockConfigStatic.when(Config::getAppMaintainers).thenReturn(List.of(userId));

        AuthContext authContext = userProvision.getCurrentUserContext(createMockValidCookie(userId));

        assertEquals(userId, authContext.id());
        assertHasRoles(authContext, Role.ADMIN, Role.INSTRUCTOR, Role.STUDENT, Role.MAINTAINER);
    }

    @Test
    public void testGetCurrentLoggedInUser_nullUic_returnsNull() {
        assertNull(userProvision.getCurrentLoggedInUserContext(null));
    }

    @Test
    public void testGetCurrentLoggedInUser_invalidCookie_returnsNull() {
        assertNull(userProvision.getCurrentLoggedInUserContext(createMockInvalidCookie()));
    }

    @Test
    public void testGetCurrentLoggedInUser_validCookie_returnsUserInfoWithCorrectIdAndNoRoles() {
        String userId = "valid-user-id";

        AuthContext authContext = userProvision.getCurrentLoggedInUserContext(createMockValidCookie(userId));

        assertEquals(userId, authContext.id());
        assertHasNoRoles(authContext);
    }

    @Test
    public void testGetMasqueradeUser_instructor_returnsUserInfoWithInstructorRole() {
        String googleId = "typical-instructor";
        when(mockUsersLogic.isInstructorInAnyCourse(googleId)).thenReturn(true);

        AuthContext authContext = userProvision.getMasqueradeUserContext(googleId);

        assertEquals(googleId, authContext.id());
        assertHasRoles(authContext, Role.INSTRUCTOR);
    }

    @Test
    public void testGetMasqueradeUser_student_returnsUserInfoWithStudentRole() {
        String googleId = "student-user-id";
        when(mockUsersLogic.isStudentInAnyCourse(googleId)).thenReturn(true);

        AuthContext authContext = userProvision.getMasqueradeUserContext(googleId);

        assertEquals(googleId, authContext.id());
        assertHasRoles(authContext, Role.STUDENT);
    }

    @Test
    public void testGetMasqueradeUser_maintainer_returnsUserInfoWithMaintainerRole() {
        String maintainerGoogleId = "maintainer-user-id";
        mockConfigStatic.when(Config::getAppMaintainers).thenReturn(List.of(maintainerGoogleId));

        AuthContext authContext = userProvision.getMasqueradeUserContext(maintainerGoogleId);

        assertEquals(maintainerGoogleId, authContext.id());
        assertHasRoles(authContext, Role.MAINTAINER);
    }

    @Test
    public void testGetMasqueradeUser_admin_returnsUserInfoWithNoRoles() {
        String adminGoogleId = "admin-user-id";
        mockConfigStatic.when(Config::getAppAdmins).thenReturn(List.of(adminGoogleId));

        AuthContext authContext = userProvision.getMasqueradeUserContext(adminGoogleId);

        assertEquals(adminGoogleId, authContext.id());
        assertHasNoRoles(authContext);
    }

    @Test
    public void testGetMasqueradeUser_unregistered_returnsUserInfoWithNoRoles() {
        String googleId = "unregistered-user-id";

        AuthContext authContext = userProvision.getMasqueradeUserContext(googleId);

        assertEquals(googleId, authContext.id());
        assertHasNoRoles(authContext);
    }

    @Test
    public void testGetMasqueradeUser_instructorAndStudent_returnsBothRolesTrue() {
        String googleId = "instructor-student-user-id";
        when(mockUsersLogic.isInstructorInAnyCourse(googleId)).thenReturn(true);
        when(mockUsersLogic.isStudentInAnyCourse(googleId)).thenReturn(true);

        AuthContext authContext = userProvision.getMasqueradeUserContext(googleId);

        assertEquals(googleId, authContext.id());
        assertHasRoles(authContext, Role.INSTRUCTOR, Role.STUDENT);
    }

    @Test
    public void testGetMasqueradeUser_instructorStudentAndMaintainer_returnsThreeRolesTrue() {
        String googleId = "instructor-student-maintainer-user-id";
        when(mockUsersLogic.isInstructorInAnyCourse(googleId)).thenReturn(true);
        when(mockUsersLogic.isStudentInAnyCourse(googleId)).thenReturn(true);
        mockConfigStatic.when(Config::getAppMaintainers).thenReturn(List.of(googleId));

        AuthContext authContext = userProvision.getMasqueradeUserContext(googleId);

        assertEquals(googleId, authContext.id());
        assertHasRoles(authContext, Role.INSTRUCTOR, Role.STUDENT, Role.MAINTAINER);
    }

    @Test
    public void testGetAdminOnlyUser_returnsAuthContextWithAllRolesTrue() {
        String userId = "admin-user-id";

        AuthContext authContext = userProvision.getAdminOnlyUserContext(userId);

        assertEquals(userId, authContext.id());
        assertHasRoles(authContext, Role.ADMIN, Role.MAINTAINER, Role.INSTRUCTOR, Role.STUDENT);
        verifyNoInteractions(mockUsersLogic);
    }

    private static UserInfoCookie createMockValidCookie(String userId) {
        UserInfoCookie cookie = mock(UserInfoCookie.class);
        when(cookie.isValid()).thenReturn(true);
        when(cookie.getUserId()).thenReturn(userId);
        return cookie;
    }

    private static UserInfoCookie createMockInvalidCookie() {
        UserInfoCookie cookie = mock(UserInfoCookie.class);
        when(cookie.isValid()).thenReturn(false);
        return cookie;
    }

    private static void assertHasNoRoles(AuthContext authContext) {
        assertHasRoles(authContext);
    }

    private static void assertHasRoles(AuthContext authContext, Role... expectedRoles) {
        Set<Role> expected = Set.of(expectedRoles);
        assertEquals(expected.contains(Role.ADMIN), authContext.isAdmin());
        assertEquals(expected.contains(Role.INSTRUCTOR), authContext.isInstructor());
        assertEquals(expected.contains(Role.STUDENT), authContext.isStudent());
        assertEquals(expected.contains(Role.MAINTAINER), authContext.isMaintainer());
    }

    private enum Role {
        ADMIN, INSTRUCTOR, STUDENT, MAINTAINER
    }

}
