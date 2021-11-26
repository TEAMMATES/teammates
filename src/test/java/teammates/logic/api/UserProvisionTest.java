package teammates.logic.api;

import org.testng.annotations.Test;

import teammates.common.datatransfer.UserInfo;
import teammates.common.datatransfer.UserInfoCookie;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Config;

/**
 * SUT: {@link UserProvision}.
 */
public class UserProvisionTest extends BaseLogicTest {

    private static UserProvision userProvision = UserProvision.inst();

    @Test
    public void testGetCurrentUser() {

        ______TS("instructor");

        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        UserInfo user = userProvision.getCurrentUser(new UserInfoCookie(instructor.getGoogleId()));
        assertEquals(instructor.getGoogleId(), user.id);
        assertFalse(user.isAdmin);
        assertTrue(user.isInstructor);
        assertFalse(user.isStudent);

        ______TS("student");

        StudentAttributes student = dataBundle.students.get("student1InCourse1");
        user = userProvision.getCurrentUser(new UserInfoCookie(student.getGoogleId()));
        assertEquals(student.getGoogleId(), user.id);
        assertFalse(user.isAdmin);
        assertFalse(user.isInstructor);
        assertTrue(user.isStudent);

        ______TS("admin");

        String adminUserId = Config.APP_ADMINS.get(0);
        user = userProvision.getCurrentUser(new UserInfoCookie(adminUserId));
        assertEquals(adminUserId, user.id);
        assertTrue(user.isAdmin);

        ______TS("unregistered");

        user = userProvision.getCurrentUser(new UserInfoCookie("unknown"));
        assertEquals("unknown", user.id);
        assertFalse(user.isAdmin);
        assertFalse(user.isInstructor);
        assertFalse(user.isStudent);

        ______TS("not logged in");

        // check for user not logged in
        assertNull(userProvision.getCurrentUser(null));
    }

}
