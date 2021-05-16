package teammates.logic.api;

import org.testng.annotations.Test;

import teammates.common.datatransfer.UserInfo;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;

/**
 * SUT: {@link UserProvision}.
 */
public class UserProvisionTest extends BaseLogicTest {

    private static UserProvision userProvision = new UserProvision();

    @Test
    public void testGetLoginUrl() {
        gaeSimulation.logoutUser();
        assertEquals("/_ah/login?continue=www.abc.com", userProvision.getLoginUrl("www.abc.com"));
    }

    @Test
    public void testGetLogoutUrl() {
        gaeSimulation.loginUser("any.user");
        assertEquals("/_ah/logout?continue=www.def.com", userProvision.getLogoutUrl("www.def.com"));
    }

    @Test
    public void testGetCurrentUser() throws Exception {

        ______TS("admin+instructor+student");

        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        CourseAttributes course = dataBundle.courses.get("typicalCourse2");
        gaeSimulation.loginAsAdmin(instructor.googleId);
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

        UserInfo user = userProvision.getCurrentUser();
        assertEquals(instructor.googleId, user.id);
        assertTrue(user.isAdmin);
        assertTrue(user.isInstructor);
        assertTrue(user.isStudent);

        ______TS("unregistered");

        gaeSimulation.loginUser("unknown");

        user = userProvision.getCurrentUser();
        assertEquals("unknown", user.id);
        assertFalse(user.isAdmin);
        assertFalse(user.isInstructor);
        assertFalse(user.isStudent);

        ______TS("not logged in");

        // check for user not logged in
        gaeSimulation.logoutUser();
        assertNull(userProvision.getCurrentUser());
    }

}
