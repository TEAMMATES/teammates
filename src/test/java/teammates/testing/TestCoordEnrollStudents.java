package teammates.testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import teammates.testing.lib.TMAPI;
import teammates.testing.object.Student;

/**
 * Coordinator enrolling students
 * 
 * @author Huy
 * 
 */
public class TestCoordEnrollStudents extends BaseTest {

        @BeforeClass
        public static void classSetup() throws Exception {
                setupScenario();
                TMAPI.cleanup();
                TMAPI.createCourse(sc.course);

                setupSelenium();
                coordinatorLogin(sc.coordinator.username, sc.coordinator.password);
        }

        @AfterClass
        public static void classTearDown() throws Exception {
                wrapUp();
        }
        
        @Test
        public void CoordEnrollStudents() throws Exception {
        	testEnrollmentFormat();
        	testEnrollNewStudentsSuccess();
        	testEnrollExistingStudentsSuccess();
        	testEnrollStudentsNoEmailsFail();
        }

        /**
         * White Space should be trimmed
         * 
         * @throws Exception
         * @author wangsha
         * @date Sep 14, 2011
         */
        public void testEnrollmentFormat() throws Exception {
                cout("Test: test white spaces in team name");
                String students = "Team 1|User 6|user6@gmail.com\n"
                                    + "    Team 1     |  User 0  |user0@gmail.com\n"
                                        + "Team 2|User 1|      user1@gmail.com    \n";

                // To Enroll page
                clickCourseEnrol(0);
                verifyEnrollPage();

                wdFillString(enrolInfo, students);
                wdClick(enrolButton);
                cout(getElementText(courseErrorMessage));

                wdClick(enrolBackButton);

                // Check number of teams
                assertEquals(2, Integer.parseInt(getCourseTeams(0)));

                // Check number of unregistered students
                assertEquals(3, Integer.parseInt(getCourseUnregisteredStudents(0)));

                TMAPI.cleanupCourse(sc.course.courseId);
        }

        /**
         * Enroll a list of new students (half the from the test scenario)
         */
        public void testEnrollNewStudentsSuccess() throws Exception {
                cout("TestCoordEnrolStudents: Enrolling new students.");
                TMAPI.createCourse(sc.course);
                clickCourseTab();
                waitForElementPresent(inputCourseID);

                int half = sc.students.size() / 2;
                List<Student> ls = sc.students.subList(0, half);
                enrollStudents(ls);

                // Check for number of successful students enrolled
                verifyEnrollment(half, 0);

                wdClick(enrolBackButton);

                // Calculate the number of teams
                Set<String> set = new HashSet<String>();
                for (Student s : ls) {
                        set.add(s.teamName);
                }

                assertEquals(set.size(), Integer.parseInt(getElementText(courseTeams)));
        }

        /**
         * Enroll the entire student list. Making sure that the old students are not
         * mistakenly re-enrolled.
         */
        public void testEnrollExistingStudentsSuccess() throws Exception {
                cout("Test: Enrolling more students (mixed new and old).");

                int left = sc.students.size() - sc.students.size() / 2;
                enrollStudents(sc.students);
                verifyEnrollment(left, 0);
                wdClick(enrolBackButton);

                // Check number of teams
                assertEquals(sc.teams.size(), Integer.parseInt(getElementText(courseTeams)));
        }

        /**
         * Fail to enroll students in without email addresses
         */
        public void testEnrollStudentsNoEmailsFail() throws Exception {
                cout("Test: Enrolling students with missing email addresses.");

                String students = "Team 1|User 6|\n" + "Team 1|User 0|\n" + "Team 1|User 1|";

                clickCourseEnrol(0);
                verifyEnrollPage();

                // enrol page:
                wdFillString(enrolInfo, students);
                wdClick(enrolButton);

                // Make sure the error message is there
                assertTrue(isElementPresent(courseErrorMessage));

                wdClick(enrolBackButton);
        }
}