package teammates.logic.api;

import org.testng.annotations.Test;

import teammates.common.datatransfer.UserInfo;
import teammates.common.datatransfer.UserInfoCookie;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Config;

/**
 * SUT: {@link UserProvision}.
 */
public class UserProvisionTest extends BaseLogicTest {

    private static UserProvision userProvision = new UserProvision();

    @Test
    public void testGetCurrentUser() throws Exception {

        ______TS("instructor+student");

        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        CourseAttributes course = dataBundle.courses.get("typicalCourse2");
        // also make this user a student of another course
        StudentAttributes instructorAsStudent = StudentAttributes
                .builder(course.getId(), "instructorasstudent@yahoo.com")
                .withName("Instructor As Student")
                .withSectionName("Section 1")
                .withTeamName("Team 1")
                .withComment("")
                .build();
        instructorAsStudent.googleId = instructor.googleId;
        logic.createStudent(instructorAsStudent);

        UserInfo user = userProvision.getCurrentUser(new UserInfoCookie(instructor.googleId));
        assertEquals(instructor.googleId, user.id);
        assertFalse(user.isAdmin);
        assertTrue(user.isInstructor);
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
