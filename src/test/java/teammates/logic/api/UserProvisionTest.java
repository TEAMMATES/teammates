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

import teammates.common.datatransfer.UserInfo;
import teammates.common.datatransfer.UserInfoCookie;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.logic.core.UsersLogic;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link UserProvision}.
 */
public class UserProvisionTest extends BaseTestCase {

    private UserProvision userProvision;
    private UsersLogic mockUsersLogic;
    private MockedStatic<Config> mockConfigStatic;
    private MockedStatic<UsersLogic> mockUsersLogicStatic;

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
        userProvision = new UserProvision();

        // Default mock to return false for all IDs, individual tests will override as needed
        when(mockUsersLogic.isInstructorInAnyCourse(anyString())).thenReturn(false);
        when(mockUsersLogic.isStudentInAnyCourse(anyString())).thenReturn(false);

        mockConfigStatic = mockStatic(Config.class);
        mockConfigStatic.when(Config::getAppAdmins).thenReturn(List.of());
        mockConfigStatic.when(Config::getAppMaintainers).thenReturn(List.of());
    }

    @AfterMethod
    public void tearDownMethod() {
        mockConfigStatic.close();
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
    public void testGetCurrentUser_instructor_returnsUserInfoWithInstructorRole() {
        String userId = "instructor-user-id";
        when(mockUsersLogic.isInstructorInAnyCourse(userId)).thenReturn(true);

        UserInfo user = userProvision.getCurrentUser(createMockValidCookie(userId));

        assertEquals(userId, user.id);
        assertHasRoles(user, Role.INSTRUCTOR);
    }

    @Test
    public void testGetCurrentUser_student_returnsUserInfoWithStudentRole() {
        String userId = "student-user-id";
        when(mockUsersLogic.isStudentInAnyCourse(userId)).thenReturn(true);

        UserInfo user = userProvision.getCurrentUser(createMockValidCookie(userId));

        assertEquals(userId, user.id);
        assertHasRoles(user, Role.STUDENT);
    }

    @Test
    public void testGetCurrentUser_admin_returnsUserInfoWithAdminRole() {
        String adminUserId = "admin-user-id";
        mockConfigStatic.when(Config::getAppAdmins).thenReturn(List.of(adminUserId));

        UserInfo user = userProvision.getCurrentUser(createMockValidCookie(adminUserId));

        assertEquals(adminUserId, user.id);
        assertHasRoles(user, Role.ADMIN);
    }

    @Test
    public void testGetCurrentUser_maintainer_returnsUserInfoWithMaintainerRole() {
        String maintainerUserId = "maintainer-user-id";
        mockConfigStatic.when(Config::getAppMaintainers).thenReturn(List.of(maintainerUserId));

        UserInfo user = userProvision.getCurrentUser(createMockValidCookie(maintainerUserId));

        assertEquals(maintainerUserId, user.id);
        assertHasRoles(user, Role.MAINTAINER);
    }

    @Test
    public void testGetCurrentUser_unregistered_returnsUserInfoWithNoRoles() {
        String userId = "unregistered-user-id";

        UserInfo user = userProvision.getCurrentUser(createMockValidCookie(userId));

        assertEquals(userId, user.id);
        assertHasNoRoles(user);
    }

    @Test
    public void testGetCurrentUser_instructorAndStudent_returnsBothRolesTrue() {
        String userId = "instructor-student-user-id";
        when(mockUsersLogic.isInstructorInAnyCourse(userId)).thenReturn(true);
        when(mockUsersLogic.isStudentInAnyCourse(userId)).thenReturn(true);

        UserInfo user = userProvision.getCurrentUser(createMockValidCookie(userId));

        assertEquals(userId, user.id);
        assertHasRoles(user, Role.INSTRUCTOR, Role.STUDENT);
    }

    @Test
    public void testGetCurrentUser_adminAndMaintainer_returnsBothRolesTrue() {
        String userId = "admin-maintainer-user-id";
        mockConfigStatic.when(Config::getAppAdmins).thenReturn(List.of(userId));
        mockConfigStatic.when(Config::getAppMaintainers).thenReturn(List.of(userId));

        UserInfo user = userProvision.getCurrentUser(createMockValidCookie(userId));

        assertEquals(userId, user.id);
        assertHasRoles(user, Role.ADMIN, Role.MAINTAINER);
    }

    @Test
    public void testGetCurrentUser_instructorStudentAndMaintainer_returnsThreeRolesTrue() {
        String userId = "instructor-student-maintainer-user-id";
        when(mockUsersLogic.isInstructorInAnyCourse(userId)).thenReturn(true);
        when(mockUsersLogic.isStudentInAnyCourse(userId)).thenReturn(true);
        mockConfigStatic.when(Config::getAppMaintainers).thenReturn(List.of(userId));

        UserInfo user = userProvision.getCurrentUser(createMockValidCookie(userId));

        assertEquals(userId, user.id);
        assertHasRoles(user, Role.INSTRUCTOR, Role.STUDENT, Role.MAINTAINER);
    }

    @Test
    public void testGetCurrentUser_allRoles_returnsAllRolesTrue() {
        String userId = "all-roles-user-id";
        when(mockUsersLogic.isInstructorInAnyCourse(userId)).thenReturn(true);
        when(mockUsersLogic.isStudentInAnyCourse(userId)).thenReturn(true);
        mockConfigStatic.when(Config::getAppAdmins).thenReturn(List.of(userId));
        mockConfigStatic.when(Config::getAppMaintainers).thenReturn(List.of(userId));

        UserInfo user = userProvision.getCurrentUser(createMockValidCookie(userId));

        assertEquals(userId, user.id);
        assertHasRoles(user, Role.ADMIN, Role.INSTRUCTOR, Role.STUDENT, Role.MAINTAINER);
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
        assertHasRoles(user, Role.INSTRUCTOR);
    }

    @Test
    public void testGetMasqueradeUser_student_returnsUserInfoWithStudentRole() {
        String googleId = "student-user-id";
        when(mockUsersLogic.isStudentInAnyCourse(googleId)).thenReturn(true);

        UserInfo user = userProvision.getMasqueradeUser(googleId);

        assertEquals(googleId, user.id);
        assertHasRoles(user, Role.STUDENT);
    }

    @Test
    public void testGetMasqueradeUser_maintainer_returnsUserInfoWithMaintainerRole() {
        String maintainerGoogleId = "maintainer-user-id";
        mockConfigStatic.when(Config::getAppMaintainers).thenReturn(List.of(maintainerGoogleId));

        UserInfo user = userProvision.getMasqueradeUser(maintainerGoogleId);

        assertEquals(maintainerGoogleId, user.id);
        assertHasRoles(user, Role.MAINTAINER);
    }

    @Test
    public void testGetMasqueradeUser_admin_returnsUserInfoWithNoRoles() {
        String adminGoogleId = "admin-user-id";
        mockConfigStatic.when(Config::getAppAdmins).thenReturn(List.of(adminGoogleId));

        UserInfo user = userProvision.getMasqueradeUser(adminGoogleId);

        assertEquals(adminGoogleId, user.id);
        assertHasNoRoles(user);
    }

    @Test
    public void testGetMasqueradeUser_unregistered_returnsUserInfoWithNoRoles() {
        String googleId = "unregistered-user-id";

        UserInfo user = userProvision.getMasqueradeUser(googleId);

        assertEquals(googleId, user.id);
        assertHasNoRoles(user);
    }

    @Test
    public void testGetMasqueradeUser_instructorAndStudent_returnsBothRolesTrue() {
        String googleId = "instructor-student-user-id";
        when(mockUsersLogic.isInstructorInAnyCourse(googleId)).thenReturn(true);
        when(mockUsersLogic.isStudentInAnyCourse(googleId)).thenReturn(true);

        UserInfo user = userProvision.getMasqueradeUser(googleId);

        assertEquals(googleId, user.id);
        assertHasRoles(user, Role.INSTRUCTOR, Role.STUDENT);
    }

    @Test
    public void testGetMasqueradeUser_instructorStudentAndMaintainer_returnsThreeRolesTrue() {
        String googleId = "instructor-student-maintainer-user-id";
        when(mockUsersLogic.isInstructorInAnyCourse(googleId)).thenReturn(true);
        when(mockUsersLogic.isStudentInAnyCourse(googleId)).thenReturn(true);
        mockConfigStatic.when(Config::getAppMaintainers).thenReturn(List.of(googleId));

        UserInfo user = userProvision.getMasqueradeUser(googleId);

        assertEquals(googleId, user.id);
        assertHasRoles(user, Role.INSTRUCTOR, Role.STUDENT, Role.MAINTAINER);
    }

    @Test
    public void testGetAdminOnlyUser_returnsUserInfoWithOnlyIsAdminTrue() {
        String userId = "admin-user-id";

        UserInfo user = userProvision.getAdminOnlyUser(userId);

        assertEquals(userId, user.id);
        assertHasRoles(user, Role.ADMIN);
        verifyNoInteractions(mockUsersLogic);
    }

    @Test
    public void testGetAutomatedServiceUser_returnsUserInfoWithOnlyIsAutomatedServiceTrue() {
        String serviceId = Const.AutomatedService.CRON_SERVICE_USER_ID;

        UserInfo user = userProvision.getAutomatedServiceUser(serviceId);

        assertEquals(serviceId, user.id);
        assertHasRoles(user, Role.AUTOMATED_SERVICE);
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

    private static void assertHasNoRoles(UserInfo user) {
        // This method just calls assertHasRoles with an empty array to improve readability
        assertHasRoles(user);

    }

    private static void assertHasRoles(UserInfo user, Role... expectedRoles) {
        Set<Role> expected = Set.of(expectedRoles);
        assertEquals(expected.contains(Role.ADMIN), user.isAdmin);
        assertEquals(expected.contains(Role.INSTRUCTOR), user.isInstructor);
        assertEquals(expected.contains(Role.STUDENT), user.isStudent);
        assertEquals(expected.contains(Role.MAINTAINER), user.isMaintainer);
        assertEquals(expected.contains(Role.AUTOMATED_SERVICE), user.isAutomatedService);
    }

    private enum Role {
        ADMIN, INSTRUCTOR, STUDENT, MAINTAINER, AUTOMATED_SERVICE
    }

}
