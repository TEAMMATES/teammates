package teammates.test.cases.logic;

import org.testng.annotations.Test;

import teammates.common.datatransfer.UserType;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.logic.api.GateKeeper;
import teammates.logic.api.Logic;

/**
 * SUT: {@link GateKeeper}.
 */
public class GateKeeperTest extends BaseLogicTest {

    private static GateKeeper gateKeeper = new GateKeeper();

    @Test
    public void testGetLoginUrl() {
        gaeSimulation.logoutUser();
        assertEquals("/_ah/login?continue=www.abc.com", gateKeeper.getLoginUrl("www.abc.com"));
    }

    @Test
    public void testGetLogoutUrl() {
        gaeSimulation.loginUser("any.user");
        assertEquals("/_ah/logout?continue=www.def.com", gateKeeper.getLogoutUrl("www.def.com"));
    }

    //TODO: test isUserLoggedIn method

    @Test
    public void testGetCurrentUser() throws Exception {

        ______TS("admin+instructor+student");

        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        CourseAttributes course = dataBundle.courses.get("typicalCourse2");
        gaeSimulation.loginAsAdmin(instructor.googleId);
        // also make this user a student of another course
        StudentAttributes instructorAsStudent = StudentAttributes
                .builder(course.getId(), "Instructor As Student", "instructorasstudent@yahoo.com")
                .withSection("Section 1")
                .withTeam("Team 1")
                .withComments("")
                .build();
        instructorAsStudent.googleId = instructor.googleId;
        new Logic().createStudentWithoutDocument(instructorAsStudent);

        UserType user = gateKeeper.getCurrentUser();
        assertEquals(instructor.googleId, user.id);
        assertTrue(user.isAdmin);
        assertTrue(user.isInstructor);
        assertTrue(user.isStudent);

        ______TS("unregistered");

        gaeSimulation.loginUser("unknown");

        user = gateKeeper.getCurrentUser();
        assertEquals("unknown", user.id);
        assertFalse(user.isAdmin);
        assertFalse(user.isInstructor);
        assertFalse(user.isStudent);

        ______TS("not logged in");

        // check for user not logged in
        gaeSimulation.logoutUser();
        assertNull(gateKeeper.getCurrentUser());
    }

}
