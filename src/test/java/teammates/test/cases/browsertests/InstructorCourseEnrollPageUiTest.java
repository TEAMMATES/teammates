package teammates.test.cases.browsertests;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.test.driver.BackDoor;
import teammates.test.driver.StringHelperExtension;
import teammates.test.pageobjects.InstructorCourseEnrollPage;
import teammates.test.pageobjects.InstructorCourseEnrollResultPage;
import teammates.test.pageobjects.InstructorCoursesDetailsPage;

/**
 * SUT: {@link Const.ActionURIs#INSTRUCTOR_COURSE_ENROLL_PAGE}.
 */
public class InstructorCourseEnrollPageUiTest extends BaseUiTestCase {
    private InstructorCourseEnrollPage enrollPage;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorCourseEnrollPageUiTest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    public void testInstructorCourseEnrollPage() throws Exception {
        testContent();
        testSampleLink();
        testEnrollAction();
    }

    private void testContent() throws Exception {

        ______TS("typical enroll page");

        AppUrl enrollUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_PAGE)
                .withUserId(testData.instructors.get("CCEnrollUiT.teammates.test").googleId)
                .withCourseId(testData.courses.get("CCEnrollUiT.CS2104").getId());

        enrollPage = loginAdminToPage(enrollUrl, InstructorCourseEnrollPage.class);

        // This is the full HTML verification for Instructor Course Enroll Page, the rest can all be verifyMainHtml
        enrollPage.verifyHtml("/instructorCourseEnrollPage.html");
    }

    private void testSampleLink() {

        ______TS("link for the sample spreadsheet");
        enrollPage.clickSpreadsheetLink();
        By expectedOgTitle = By.cssSelector("meta[property='og:title'][content='Course Enroll Sample Spreadsheet']");
        enrollPage.verifyContainsElement(expectedOgTitle);
    }

    private void testEnrollAction() throws Exception {
        /* We test both empty and non-empty courses because the generated
         * enroll result page is slightly different for the two cases.
         */

        String courseId = testData.courses.get("CCEnrollUiT.CS2104").getId();

        ______TS("enroll action: existent course, enroll lines with section field");

        AppUrl enrollUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_PAGE)
                .withUserId(testData.instructors.get("CCEnrollUiT.teammates.test").googleId)
                .withCourseId(testData.courses.get("CCEnrollUiT.CS2104").getId());

        enrollPage = loginAdminToPage(enrollUrl, InstructorCourseEnrollPage.class);

        String enrollString =
                "Section | Team | Name | Email | Comments\n"
                // Modify team for student within section
                + "Section 1| Team 4 | Alice Betsy</textarea><textarea>'\" | alice.b.tmms@gmail.tmt"
                        + " | This comment has been changed\n"
                // Modify section and team
                + "Section 2| Team 2 | Benny Charles| benny.c.tmms@gmail.tmt |\n"
                // A student with no comment
                + "Section 3 | Team 3 |Frank Galoe | frank.g.tmms@gmail.tmt |\n"
                // A new student with name containing accented characters
                + "Section 1 | Team 1|José Gómez | jose.gomez.tmns@gmail.tmt"
                        + " | This student name contains accented characters\n";

        InstructorCourseEnrollResultPage resultsPage = enrollPage.enroll(enrollString);

        // This is the full HTML verification for Instructor Course Enroll Results Page, the rest can all be verifyMainHtml
        resultsPage.verifyHtml("/instructorCourseEnrollPageResult.html");

        // Check 'Edit' link
        enrollPage = resultsPage.clickEditLink();
        enrollPage.verifyContains("Enroll Students for CCEnrollUiT.CS2104");
        assertEquals(enrollString, enrollPage.getEnrollText());

        // Ensure students were actually enrolled
        AppUrl coursesPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE)
                .withUserId(testData.instructors.get("CCEnrollUiT.teammates.test").googleId)
                .withCourseId(courseId);
        InstructorCoursesDetailsPage detailsPage =
                loginAdminToPage(coursesPageUrl, InstructorCoursesDetailsPage.class);
        assertEquals(6, detailsPage.getStudentCountForCourse());

        ______TS("enroll action: empty course, enroll lines with header containing empty columns, no sections");

        // Make the course empty
        BackDoor.deleteCourse(courseId);
        BackDoor.createCourse(testData.courses.get("CCEnrollUiT.CS2104"));
        BackDoor.createInstructor(testData.instructors.get("CCEnrollUiT.teammates.test"));

        enrollUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_PAGE)
            .withUserId(testData.instructors.get("CCEnrollUiT.teammates.test").googleId)
            .withCourseId(testData.courses.get("CCEnrollUiT.CS2104").getId());

        enrollPage = loginAdminToPage(enrollUrl, InstructorCourseEnrollPage.class);

        enrollString =
                "| Name | Email | | Team | Comments\n"
                + "|Alice Betsy</option></td></div>'\" | alice.b.tmms@gmail.tmt ||"
                        + " Team 1</option></td></div>'\" | This comment has been changed\n"
                // A student with no comment
                + "|Frank Galoe | frank.g.tmms@gmail.tmt || Team 1</option></td></div>'\" |\n"
                // A new student with name containing accented characters
                + "|José Gómez | jose.gomez.tmns@gmail.tmt || Team 3 | This student name contains accented characters\n";

        resultsPage = enrollPage.enroll(enrollString);
        resultsPage.verifyHtmlMainContent("/instructorCourseEnrollPageResultForEmptyCourse.html");

        // Check 'Edit' link
        enrollPage = resultsPage.clickEditLink();
        enrollPage.verifyContains("Enroll Students for CCEnrollUiT.CS2104");
        assertEquals(enrollString, enrollPage.getEnrollText());

        // Ensure students were actually enrolled
        coursesPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE)
            .withUserId(testData.instructors.get("CCEnrollUiT.teammates.test").googleId)
            .withCourseId(courseId);
        detailsPage = loginAdminToPage(coursesPageUrl, InstructorCoursesDetailsPage.class);
        assertEquals(3, detailsPage.getStudentCountForCourse());

        ______TS("enroll action: fail to enroll as a team cannot be in 2 different sections");

        enrollUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_PAGE)
            .withUserId(testData.instructors.get("CCEnrollUiT.teammates.test").googleId)
            .withCourseId(testData.courses.get("CCEnrollUiT.CS2104").getId());

        enrollPage = loginAdminToPage(enrollUrl, InstructorCourseEnrollPage.class);

        enrollString = "Section | Team | Name | Email | Comments\n"
                       + "Different Section | Team 1</option></td></div>'\" | Alice Betsy | alice.b.tmms@gmail.tmt |\n";

        enrollPage.enrollUnsuccessfully(enrollString);
        enrollPage.verifyStatus("The team \"Team 1</option></td></div>'\"\" is in multiple sections. "
                                + "The team ID should be unique across the entire course "
                                + "and a team cannot be spread across multiple sections."
                                + "\nPlease use the enroll page to edit multiple students");

        ______TS("enroll action: fail to enroll due to invalid header");

        enrollString = "Section | Team | Name | Email | Comments | Section\n";

        enrollPage.enrollUnsuccessfully(enrollString);
        enrollPage.verifyStatus("The header row contains repeated fields");

        enrollString = "Section | Name | Email\n";

        enrollPage.enrollUnsuccessfully(enrollString);
        enrollPage.verifyStatus("The following required column names are missing in the header row: Team");

        ______TS("enroll action: fail to enroll as there is no input");

        enrollUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_PAGE)
            .withUserId(testData.instructors.get("CCEnrollUiT.teammates.test").googleId)
            .withCourseId(testData.courses.get("CCEnrollUiT.CS2104").getId());

        enrollPage = loginAdminToPage(enrollUrl, InstructorCourseEnrollPage.class);

        enrollString = "";

        enrollPage.enrollUnsuccessfully(enrollString);
        enrollPage.verifyStatus("Please input at least one student detail.");

        ______TS("enroll action: fail to enroll as there is an invalid line");

        enrollString =
                "Team | Name | Email | Comment" + Const.EOL
                // A new student with no email input
                + "Team 3 | Frank Hughe" + Const.EOL
                // A new student with invalid email input
                + "Team 1</option></td></div>'\" | Black Jack | bjack.gmail.tmt | This student email is invalid" + Const.EOL
                // A new student with invalid team name
                + StringHelperExtension.generateStringOfLength(FieldValidator.TEAM_NAME_MAX_LENGTH + 1)
                        + " | Robert Downey | rob@email.tmt | This student team name is too long" + Const.EOL
                // A new student with invalid name
                + "Team 2 | " + StringHelperExtension.generateStringOfLength(FieldValidator.PERSON_NAME_MAX_LENGTH + 1)
                        + " | longname@email.tmt | This student name is too long" + Const.EOL;

        enrollPage.enrollUnsuccessfully(enrollString);
        enrollPage.verifyHtmlMainContent("/instructorCourseEnrollError.html");

        ______TS("enroll action: scripts are successfully sanitized");

        // Enroll a student with a script in the name
        String xssScript = "<script>alert(\"was here\");</script>";
        enrollString = "Team | Name | Email | Comments\n"
                       + "Team GreyHats | Mallory " + xssScript + " | mallory.tmms@gmail.tmt |\n";

        // Check that the script does not appear on the InstructorCourseEnrollResult page
        resultsPage = enrollPage.enroll(enrollString);
        resultsPage.verifyNotContain(xssScript);

        // Check that the script does not appear on the InstructorCourseEnroll page either
        enrollPage = resultsPage.clickEditLink();
        enrollPage.verifyNotContain(xssScript);
    }

}
