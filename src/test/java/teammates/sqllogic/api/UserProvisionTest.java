package teammates.sqllogic.api;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.util.List;

import org.mockito.MockedStatic;
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

    @BeforeMethod
    public void setUpMethod() {
        mockUsersLogic = mock(UsersLogic.class);
        // UserProvision stores UsersLogic.inst() in a final field at construction time.
        // We mock the static method during construction so the field captures our mock.
        try (MockedStatic<UsersLogic> usersLogicStatic = mockStatic(UsersLogic.class)) {
            usersLogicStatic.when(UsersLogic::inst).thenReturn(mockUsersLogic);
            userProvision = new UserProvision();
        }
        // After the try block, userProvision.usersLogic still holds the mockUsersLogic reference.
    }

    /** Asserts all four roles at once so no role is accidentally left unchecked. */
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

    private static void assertNoRoles(UserInfo user) {
        assertHasExactRoles(user, false, false, false, false);
    }

    // ======================= getCurrentUser =======================

    @Test
    public void testGetCurrentUser_nullUic_returnsNull() {
        assertNull(userProvision.getCurrentUser(null));
    }

    @Test
    public void testGetCurrentUser_invalidCookie_returnsNull() {
        UserInfoCookie invalidCookie = new UserInfoCookie("userid");
        invalidCookie.setVerificationCode("invalid_signature");

        assertNull(userProvision.getCurrentUser(invalidCookie));
    }

    @Test
    public void testGetCurrentUser_instructor_returnsUserInfoWithIsInstructorTrue() {
        String userId = "typical-instructor";
        assertFalse(Config.APP_ADMINS.contains(userId)); // precondition: must not be an admin
        assertFalse(Config.APP_MAINTAINERS.contains(userId)); // precondition: must not be a maintainer
        when(mockUsersLogic.isInstructorInAnyCourse(userId)).thenReturn(true);
        when(mockUsersLogic.isStudentInAnyCourse(userId)).thenReturn(false);

        UserInfo user = userProvision.getCurrentUser(new UserInfoCookie(userId));

        assertEquals(userId, user.id);
        assertIsInstructorOnly(user);
    }

    @Test
    public void testGetCurrentUser_student_returnsUserInfoWithIsStudentTrue() {
        String userId = "typical-student";
        assertFalse(Config.APP_ADMINS.contains(userId)); // precondition: must not be an admin
        assertFalse(Config.APP_MAINTAINERS.contains(userId)); // precondition: must not be a maintainer
        when(mockUsersLogic.isInstructorInAnyCourse(userId)).thenReturn(false);
        when(mockUsersLogic.isStudentInAnyCourse(userId)).thenReturn(true);

        UserInfo user = userProvision.getCurrentUser(new UserInfoCookie(userId));

        assertEquals(userId, user.id);
        assertIsStudentOnly(user);
    }

    @Test
    public void testGetCurrentUser_admin_returnsUserInfoWithIsAdminTrue() {
        // Use the first non-blank configured admin; skip if app.admins is not set.
        List<String> configuredAdmins = Config.APP_ADMINS.stream()
                .filter(s -> !s.isBlank())
                .toList();
        if (configuredAdmins.isEmpty()) {
            return;
        }
        String adminUserId = configuredAdmins.get(0);

        UserInfo user = userProvision.getCurrentUser(new UserInfoCookie(adminUserId));

        assertEquals(adminUserId, user.id);
        assertTrue(user.isAdmin);
    }

    @Test
    public void testGetCurrentUser_maintainer_returnsUserInfoWithIsMaintainerTrue() {
        // Use the first non-blank configured maintainer; skip if app.maintainers is not set.
        List<String> configuredMaintainers = Config.APP_MAINTAINERS.stream()
                .filter(s -> !s.isBlank())
                .toList();
        if (configuredMaintainers.isEmpty()) {
            return;
        }
        String maintainerUserId = configuredMaintainers.get(0);

        UserInfo user = userProvision.getCurrentUser(new UserInfoCookie(maintainerUserId));

        assertEquals(maintainerUserId, user.id);
        assertTrue(user.isMaintainer);
    }

    @Test
    public void testGetCurrentUser_unregistered_returnsUserInfoWithAllRolesFalse() {
        String userId = "unregistered-user";
        assertFalse(Config.APP_ADMINS.contains(userId)); // precondition: must not be an admin
        assertFalse(Config.APP_MAINTAINERS.contains(userId)); // precondition: must not be a maintainer
        when(mockUsersLogic.isInstructorInAnyCourse(userId)).thenReturn(false);
        when(mockUsersLogic.isStudentInAnyCourse(userId)).thenReturn(false);

        UserInfo user = userProvision.getCurrentUser(new UserInfoCookie(userId));

        assertEquals(userId, user.id);
        assertNoRoles(user);
    }

    // ======================= getCurrentUserWithTransaction =======================

    @Test
    public void testGetCurrentUserWithTransaction_nullUic_returnsNull() {
        try (MockedStatic<HibernateUtil> mockHibernateUtil = mockStatic(HibernateUtil.class)) {
            assertNull(userProvision.getCurrentUserWithTransaction(null));
        }
    }

    @Test
    public void testGetCurrentUserWithTransaction_instructor_wrapsInTransactionAndReturnsUserInfo() {
        String userId = "typical-instructor";
        assertFalse(Config.APP_ADMINS.contains(userId)); // precondition: must not be an admin
        assertFalse(Config.APP_MAINTAINERS.contains(userId)); // precondition: must not be a maintainer
        when(mockUsersLogic.isInstructorInAnyCourse(userId)).thenReturn(true);
        when(mockUsersLogic.isStudentInAnyCourse(userId)).thenReturn(false);

        try (MockedStatic<HibernateUtil> mockHibernateUtil = mockStatic(HibernateUtil.class)) {
            UserInfo user = userProvision.getCurrentUserWithTransaction(new UserInfoCookie(userId));

            assertEquals(userId, user.id);
            assertIsInstructorOnly(user);
            mockHibernateUtil.verify(HibernateUtil::beginTransaction);
            mockHibernateUtil.verify(HibernateUtil::commitTransaction);
        }
    }

    // ======================= getCurrentLoggedInUser =======================

    @Test
    public void testGetCurrentLoggedInUser_nullUic_returnsNull() {
        assertNull(userProvision.getCurrentLoggedInUser(null));
    }

    @Test
    public void testGetCurrentLoggedInUser_invalidCookie_returnsNull() {
        UserInfoCookie invalidCookie = new UserInfoCookie("some-user");
        invalidCookie.setVerificationCode("invalid-signature");

        assertNull(userProvision.getCurrentLoggedInUser(invalidCookie));
    }

    @Test
    public void testGetCurrentLoggedInUser_validCookie_returnsUserInfoWithCorrectId() {
        String userId = "valid-user-id";

        UserInfo user = userProvision.getCurrentLoggedInUser(new UserInfoCookie(userId));

        assertEquals(userId, user.id);
    }

    // ======================= getMasqueradeUser =======================

    @Test
    public void testGetMasqueradeUser_instructor_returnsUserInfoWithIsInstructorTrue() {
        String googleId = "typical-instructor";
        assertFalse(Config.APP_MAINTAINERS.contains(googleId)); // precondition: must not be a maintainer
        when(mockUsersLogic.isInstructorInAnyCourse(googleId)).thenReturn(true);
        when(mockUsersLogic.isStudentInAnyCourse(googleId)).thenReturn(false);

        UserInfo user = userProvision.getMasqueradeUser(googleId);

        assertEquals(googleId, user.id);
        assertIsInstructorOnly(user);
    }

    @Test
    public void testGetMasqueradeUser_student_returnsUserInfoWithIsStudentTrue() {
        String googleId = "typical-student";
        assertFalse(Config.APP_MAINTAINERS.contains(googleId)); // precondition: must not be a maintainer
        when(mockUsersLogic.isInstructorInAnyCourse(googleId)).thenReturn(false);
        when(mockUsersLogic.isStudentInAnyCourse(googleId)).thenReturn(true);

        UserInfo user = userProvision.getMasqueradeUser(googleId);

        assertEquals(googleId, user.id);
        assertIsStudentOnly(user);
    }

    @Test
    public void testGetMasqueradeUser_maintainer_returnsUserInfoWithIsMaintainerTrue() {
        // Use the first non-blank configured maintainer; skip if app.maintainers is not set.
        List<String> configuredMaintainers = Config.APP_MAINTAINERS.stream()
                .filter(s -> !s.isBlank())
                .toList();
        if (configuredMaintainers.isEmpty()) {
            return;
        }
        String maintainerGoogleId = configuredMaintainers.get(0);

        UserInfo user = userProvision.getMasqueradeUser(maintainerGoogleId);

        assertEquals(maintainerGoogleId, user.id);
        assertTrue(user.isMaintainer);
        assertFalse(user.isAdmin); // getMasqueradeUser explicitly sets isAdmin=false
    }

    @Test
    public void testGetMasqueradeUser_unregistered_returnsUserInfoWithAllRolesFalse() {
        String googleId = "unregistered-user";
        assertFalse(Config.APP_MAINTAINERS.contains(googleId)); // precondition: must not be a maintainer
        when(mockUsersLogic.isInstructorInAnyCourse(googleId)).thenReturn(false);
        when(mockUsersLogic.isStudentInAnyCourse(googleId)).thenReturn(false);

        UserInfo user = userProvision.getMasqueradeUser(googleId);

        assertEquals(googleId, user.id);
        assertNoRoles(user);
    }

    // ======================= getAdminOnlyUser =======================

    @Test
    public void testGetAdminOnlyUser_returnsUserInfoWithOnlyIsAdminTrue() {
        String userId = "admin-user-id";

        UserInfo user = userProvision.getAdminOnlyUser(userId);

        assertEquals(userId, user.id);
        assertIsAdminOnly(user);
    }

}
