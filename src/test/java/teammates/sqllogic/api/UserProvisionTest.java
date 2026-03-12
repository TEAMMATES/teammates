package teammates.sqllogic.api;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

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
        try (MockedStatic<UsersLogic> usersLogicStatic = mockStatic(UsersLogic.class)) {
            usersLogicStatic.when(UsersLogic::inst).thenReturn(mockUsersLogic);
            userProvision = new UserProvision();
        }
    }

    // ======================= getCurrentUser =======================

    @Test
    public void testGetCurrentUser_nullUserInfoCookie_returnsNull() {
        assertNull(userProvision.getCurrentUser(null));
    }

    @Test
    public void testGetCurrentUser_invalidUserInfoCookie_returnsNull() {
        UserInfoCookie invalidCookie = new UserInfoCookie("userid");
        invalidCookie.setVerificationCode("invalid_signature");

        assertNull(userProvision.getCurrentUser(invalidCookie));
    }

    @Test
    public void testGetCurrentUser_instructor_returnsUserInfoWithIsInstructorTrue() {
        String userId = "typical-instructor";
        when(mockUsersLogic.isInstructorInAnyCourse(userId)).thenReturn(true);
        when(mockUsersLogic.isStudentInAnyCourse(userId)).thenReturn(false);

        UserInfo user = userProvision.getCurrentUser(new UserInfoCookie(userId));

        assertEquals(userId, user.id);
        assertFalse(user.isAdmin);
        assertTrue(user.isInstructor);
        assertFalse(user.isStudent);
    }

    @Test
    public void testGetCurrentUser_student_returnsUserInfoWithIsStudentTrue() {
        String userId = "typical-student";
        when(mockUsersLogic.isInstructorInAnyCourse(userId)).thenReturn(false);
        when(mockUsersLogic.isStudentInAnyCourse(userId)).thenReturn(true);

        UserInfo user = userProvision.getCurrentUser(new UserInfoCookie(userId));

        assertEquals(userId, user.id);
        assertFalse(user.isAdmin);
        assertFalse(user.isInstructor);
        assertTrue(user.isStudent);
    }

    @Test
    public void testGetCurrentUser_admin_returnsUserInfoWithIsAdminTrue() {
        String adminUserId = Config.APP_ADMINS.get(0);

        UserInfo user = userProvision.getCurrentUser(new UserInfoCookie(adminUserId));

        assertEquals(adminUserId, user.id);
        assertTrue(user.isAdmin);
    }

    @Test
    public void testGetCurrentUser_unregistered_returnsUserInfoWithAllRolesFalse() {
        String userId = "unregistered-user";
        when(mockUsersLogic.isInstructorInAnyCourse(userId)).thenReturn(false);
        when(mockUsersLogic.isStudentInAnyCourse(userId)).thenReturn(false);

        UserInfo user = userProvision.getCurrentUser(new UserInfoCookie(userId));

        assertEquals(userId, user.id);
        assertFalse(user.isAdmin);
        assertFalse(user.isInstructor);
        assertFalse(user.isStudent);
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
        when(mockUsersLogic.isInstructorInAnyCourse(userId)).thenReturn(true);
        when(mockUsersLogic.isStudentInAnyCourse(userId)).thenReturn(false);

        try (MockedStatic<HibernateUtil> mockHibernateUtil = mockStatic(HibernateUtil.class)) {
            UserInfo user = userProvision.getCurrentUserWithTransaction(new UserInfoCookie(userId));

            assertEquals(userId, user.id);
            assertTrue(user.isInstructor);
            assertFalse(user.isStudent);
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
        when(mockUsersLogic.isInstructorInAnyCourse(googleId)).thenReturn(true);
        when(mockUsersLogic.isStudentInAnyCourse(googleId)).thenReturn(false);

        UserInfo user = userProvision.getMasqueradeUser(googleId);

        assertEquals(googleId, user.id);
        assertFalse(user.isAdmin);
        assertTrue(user.isInstructor);
        assertFalse(user.isStudent);
    }

    @Test
    public void testGetMasqueradeUser_student_returnsUserInfoWithIsStudentTrue() {
        String googleId = "typical-student";
        when(mockUsersLogic.isInstructorInAnyCourse(googleId)).thenReturn(false);
        when(mockUsersLogic.isStudentInAnyCourse(googleId)).thenReturn(true);

        UserInfo user = userProvision.getMasqueradeUser(googleId);

        assertEquals(googleId, user.id);
        assertFalse(user.isAdmin);
        assertFalse(user.isInstructor);
        assertTrue(user.isStudent);
    }

    @Test
    public void testGetMasqueradeUser_unregistered_returnsUserInfoWithAllRolesFalse() {
        String googleId = "unregistered-user";
        when(mockUsersLogic.isInstructorInAnyCourse(googleId)).thenReturn(false);
        when(mockUsersLogic.isStudentInAnyCourse(googleId)).thenReturn(false);

        UserInfo user = userProvision.getMasqueradeUser(googleId);

        assertEquals(googleId, user.id);
        assertFalse(user.isAdmin);
        assertFalse(user.isInstructor);
        assertFalse(user.isStudent);
    }

    // ======================= getAdminOnlyUser =======================

    @Test
    public void testGetAdminOnlyUser_returnsUserInfoWithOnlyIsAdminTrue() {
        String userId = "admin-user-id";

        UserInfo user = userProvision.getAdminOnlyUser(userId);

        assertEquals(userId, user.id);
        assertTrue(user.isAdmin);
        assertFalse(user.isInstructor);
        assertFalse(user.isStudent);
        assertFalse(user.isMaintainer);
    }

}