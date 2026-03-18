package teammates.sqllogic.api;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.List;

import org.mockito.MockedStatic;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.UserInfo;
import teammates.common.datatransfer.UserInfoCookie;
import teammates.common.util.Config;
import teammates.common.util.HibernateUtil;
import teammates.sqllogic.core.UsersLogic;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link UserProvision}.
 */
public class UserProvisionTest extends BaseTestCase {

    private UserProvision userProvision;
    private UsersLogic mockUsersLogic;
    private MockedStatic<Config> mockConfigStatic;
    private MockedStatic<HibernateUtil> mockHibernateUtilStatic;
    private MockedStatic<UsersLogic> mockUsersLogicStatic;

    @BeforeClass
    public void setUpClass() {
        UserProvision.inst();
    }

    @BeforeMethod
    public void setUpMethod() {
        mockUsersLogic = mock(UsersLogic.class);
        mockUsersLogicStatic = mockStatic(UsersLogic.class);
        mockUsersLogicStatic.when(UsersLogic::inst).thenReturn(mockUsersLogic);
        userProvision = new UserProvision();

        // Default mock to return false for all IDs, individual tests will override as needed
        when(mockUsersLogic.isInstructorInAnyCourse(anyString())).thenReturn(false);
        when(mockUsersLogic.isStudentInAnyCourse(anyString())).thenReturn(false);

        mockConfigStatic = mockStatic(Config.class);
        mockConfigStatic.when(Config::getAppAdmins).thenReturn(List.of());
        mockConfigStatic.when(Config::getAppMaintainers).thenReturn(List.of());

        mockHibernateUtilStatic = mockStatic(HibernateUtil.class);
    }

    @AfterMethod
    public void tearDownMethod() {
        mockConfigStatic.close();
        mockHibernateUtilStatic.close();
        mockUsersLogicStatic.close();
    }

    @Test
    public void testGetCurrentUser_nullUic_returnsNull() {
        assertNull(userProvision.getCurrentUser(null));
    }

    @Test
    public void testGetCurrentUser_invalidCookie_returnsNull() {
        assertNull(userProvision.getCurrentUser(createMockInvalidCookie()));
    }

    @Test
    public void testGetCurrentUser_instructor_returnsUserInfoWithIsInstructorTrue() {
        String userId = "typical-instructor";
        when(mockUsersLogic.isInstructorInAnyCourse(userId)).thenReturn(true);

        UserInfo user = userProvision.getCurrentUser(createMockValidCookie(userId));

        assertEquals(userId, user.id);
        assertIsInstructorOnly(user);
    }

    @Test
    public void testGetCurrentUser_student_returnsUserInfoWithIsStudentTrue() {
        String userId = "typical-student";
        when(mockUsersLogic.isStudentInAnyCourse(userId)).thenReturn(true);

        UserInfo user = userProvision.getCurrentUser(createMockValidCookie(userId));

        assertEquals(userId, user.id);
        assertIsStudentOnly(user);
    }

    @Test
    public void testGetCurrentUser_admin_returnsUserInfoWithIsAdminTrue() {
        String adminUserId = "admin-user-id";
        mockConfigStatic.when(Config::getAppAdmins).thenReturn(List.of(adminUserId));

        UserInfo user = userProvision.getCurrentUser(createMockValidCookie(adminUserId));

        assertEquals(adminUserId, user.id);
        assertIsAdminOnly(user);
    }

    @Test
    public void testGetCurrentUser_maintainer_returnsUserInfoWithIsMaintainerTrue() {
        String maintainerUserId = "maintainer-user-id";
        mockConfigStatic.when(Config::getAppMaintainers).thenReturn(List.of(maintainerUserId));

        UserInfo user = userProvision.getCurrentUser(createMockValidCookie(maintainerUserId));

        assertEquals(maintainerUserId, user.id);
        assertIsMaintainerOnly(user);
    }

    @Test
    public void testGetCurrentUser_unregistered_returnsUserInfoWithNoRoles() {
        String userId = "unregistered-user";

        UserInfo user = userProvision.getCurrentUser(createMockValidCookie(userId));

        assertEquals(userId, user.id);
        assertHasNoRoles(user);
    }

    @Test
    public void testGetCurrentUserWithTransaction_instructor_wrapsInTransactionAndReturnsUserInfo() {
        String userId = "typical-instructor";
        when(mockUsersLogic.isInstructorInAnyCourse(userId)).thenReturn(true);

        UserInfo user = userProvision.getCurrentUserWithTransaction(createMockValidCookie(userId));

        assertEquals(userId, user.id);
        assertIsInstructorOnly(user);
        mockHibernateUtilStatic.verify(HibernateUtil::beginTransaction);
        mockHibernateUtilStatic.verify(HibernateUtil::commitTransaction);
    }

    @Test
    public void testGetCurrentLoggedInUser_nullUic_returnsNull() {
        assertNull(userProvision.getCurrentLoggedInUser(null));
    }

    @Test
    public void testGetCurrentLoggedInUser_invalidCookie_returnsNull() {
        assertNull(userProvision.getCurrentLoggedInUser(createMockInvalidCookie()));
    }

    @Test
    public void testGetCurrentLoggedInUser_validCookie_returnsUserInfoWithCorrectIdAndNoRoles() {
        String userId = "valid-user-id";

        UserInfo user = userProvision.getCurrentLoggedInUser(createMockValidCookie(userId));

        assertEquals(userId, user.id);
        assertHasNoRoles(user);
    }

    @Test
    public void testGetMasqueradeUser_instructor_returnsUserInfoWithInstructorRole() {
        String googleId = "typical-instructor";
        when(mockUsersLogic.isInstructorInAnyCourse(googleId)).thenReturn(true);

        UserInfo user = userProvision.getMasqueradeUser(googleId);

        assertEquals(googleId, user.id);
        assertIsInstructorOnly(user);
    }

    @Test
    public void testGetMasqueradeUser_student_returnsUserInfoWithStudentRole() {
        String googleId = "typical-student";
        when(mockUsersLogic.isStudentInAnyCourse(googleId)).thenReturn(true);

        UserInfo user = userProvision.getMasqueradeUser(googleId);

        assertEquals(googleId, user.id);
        assertIsStudentOnly(user);
    }

    @Test
    public void testGetMasqueradeUser_maintainer_returnsUserInfoWithMaintainerRole() {
        String maintainerGoogleId = "maintainer-user-id";
        mockConfigStatic.when(Config::getAppMaintainers).thenReturn(List.of(maintainerGoogleId));

        UserInfo user = userProvision.getMasqueradeUser(maintainerGoogleId);

        assertEquals(maintainerGoogleId, user.id);
        assertIsMaintainerOnly(user);
    }

    @Test
    public void testGetMasqueradeUser_admin_returnsUserInfoWithIsAdminFalse() {
        String adminGoogleId = "admin-user-id";
        mockConfigStatic.when(Config::getAppAdmins).thenReturn(List.of(adminGoogleId));

        UserInfo user = userProvision.getMasqueradeUser(adminGoogleId);

        assertEquals(adminGoogleId, user.id);
        assertHasNoRoles(user);
    }

    @Test
    public void testGetMasqueradeUser_unregistered_returnsUserInfoWithNoRoles() {
        String googleId = "unregistered-user";

        UserInfo user = userProvision.getMasqueradeUser(googleId);

        assertEquals(googleId, user.id);
        assertHasNoRoles(user);
    }

    @Test
    public void testGetAdminOnlyUser_returnsUserInfoWithOnlyIsAdminTrue() {
        String userId = "admin-user-id";

        UserInfo user = userProvision.getAdminOnlyUser(userId);

        assertEquals(userId, user.id);
        assertIsAdminOnly(user);
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

    private static void assertHasExactRoles(UserInfo user,
                                            boolean isAdmin, boolean isInstructor, boolean isStudent, boolean isMaintainer) {
        assertEquals(isAdmin, user.isAdmin);
        assertEquals(isInstructor, user.isInstructor);
        assertEquals(isStudent, user.isStudent);
        assertEquals(isMaintainer, user.isMaintainer);
    }

    private static void assertIsInstructorOnly(UserInfo user) {
        assertHasExactRoles(user, false, true, false, false);
    }

    private static void assertIsStudentOnly(UserInfo user) {
        assertHasExactRoles(user, false, false, true, false);
    }

    private static void assertIsAdminOnly(UserInfo user) {
        assertHasExactRoles(user, true, false, false, false);
    }

    private static void assertIsMaintainerOnly(UserInfo user) {
        assertHasExactRoles(user, false, false, false, true);
    }

    private static void assertHasNoRoles(UserInfo user) {
        assertHasExactRoles(user, false, false, false, false);
    }

}
