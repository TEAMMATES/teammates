package teammates.test.cases.browsertests;

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

    private void testEnrollAction() throws Exception {
        /* We test both empty and non-empty courses because the generated
         * enroll result page is slightly different for the two cases.
         */

        String courseId = testData.courses.get("CCEnrollUiT.CS2104").getId();

        ______TS("enroll action: existent course, modify and add new students");

        AppUrl enrollUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_PAGE)
                .withUserId(testData.instructors.get("CCEnrollUiT.teammates.test").googleId)
                .withCourseId(testData.courses.get("CCEnrollUiT.CS2104").getId());

        enrollPage = loginAdminToPage(enrollUrl, InstructorCourseEnrollPage.class);

        String enrollString =
                // Modify team for student within section
                "Section 1\tTeam 4\tAlice Betsy\talice.b.tmms@gmail.tmt"
                + "\tThis comment has been changed\t"
                // Modify section and team
                + "Section 2\tTeam 2\tBenny Charles\tbenny.c.tmms@gmail.tmt\t\t"
                + "Section 2\tTeam 2\tCharlie Davis\tcharlie.d.tmms@gmail.tmt"
                + "\tThis student's name is Charlie Davis\t"
                + "Section 2\tTeam 2\tDanny Engrid\tdanny.e.tmms@gmail.tmt"
                + "\tThis student's name is Danny Engrid\t"
                // A student with no comment
                + "Section 3\tTeam 3\tFrank Galoe\tfrank.g.tmms@gmail.tmt\t\t"
                // A new student with name containing accented characters
                + "Section 1\tTeam 1\tJosé Gómez\tjose.gomez.tmns@gmail.tmt"
                + "\tThis student name contains accented characters\t";

        String expectedEnrollText =
                "Section|Team|Name|Email|Comments\n"
                + "Section 1|Team 4|Alice Betsy|alice.b.tmms@gmail.tmt|"
                    + "This comment has been changed\n"
                + "Section 2|Team 2|Benny Charles|benny.c.tmms@gmail.tmt|\n"
                + "Section 2|Team 2|Charlie Davis|charlie.d.tmms@gmail.tmt|"
                    + "This student's name is Charlie Davis\n"
                + "Section 2|Team 2|Danny Engrid|danny.e.tmms@gmail.tmt|"
                    + "This student's name is Danny Engrid\n"
                + "Section 3|Team 3|Frank Galoe|frank.g.tmms@gmail.tmt|\n"
                + "Section 1|Team 1|José Gómez|jose.gomez.tmns@gmail.tmt|"
                + "This student name contains accented characters";

        InstructorCourseEnrollResultPage resultsPage = enrollPage.enroll(enrollString);

        // This is the full HTML verification for Instructor Course Enroll Results Page, the rest can all be verifyMainHtml
        resultsPage.verifyHtml("/instructorCourseEnrollPageResultAllFields.html");

        // Check 'Edit' link
        enrollPage = resultsPage.clickEditLink();
        enrollPage.verifyContains("Enroll Students for CCEnrollUiT.CS2104");
        assertEquals(expectedEnrollText, enrollPage.getEnrollText());

        // Ensure students were actually enrolled
        AppUrl coursesPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE)
                .withUserId(testData.instructors.get("CCEnrollUiT.teammates.test").googleId)
                .withCourseId(courseId);
        InstructorCoursesDetailsPage detailsPage =
                loginAdminToPage(coursesPageUrl, InstructorCoursesDetailsPage.class);
        assertEquals(6, detailsPage.getStudentCountForCourse());

        ______TS("enroll action: delete existing course, create a new course and "
                + "enroll new students");

        // Make the course empty
        BackDoor.deleteCourse(courseId);
        BackDoor.createCourse(testData.courses.get("CCEnrollUiT.CS2104"));
        BackDoor.createInstructor(testData.instructors.get("CCEnrollUiT.teammates.test"));

        enrollUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_PAGE)
            .withUserId(testData.instructors.get("CCEnrollUiT.teammates.test").googleId)
            .withCourseId(testData.courses.get("CCEnrollUiT.CS2104").getId());

        enrollPage = loginAdminToPage(enrollUrl, InstructorCourseEnrollPage.class);

        enrollString =
                "Section 1\tTeam 1\tAlice Betsy\talice.b.tmms@gmail.tmt\t"
                + "This comment has been changed\t"
                // A student with no comment
                + "Section 1\tTeam 1\tFrank Galoe\tfrank.g.tmms@gmail.tmt\t\t"
                // A new student with name containing accented characters
                + "Section 3\tTeam 3\tJosé Gómez\tjose.gomez.tmns@gmail.tmt\t"
                + "This student name contains accented characters\n";

        expectedEnrollText =
                "Section|Team|Name|Email|Comments\n"
                + "Section 1|Team 1|Alice Betsy|alice.b.tmms@gmail.tmt|"
                + "This comment has been changed\n"
                + "Section 1|Team 1|Frank Galoe|frank.g.tmms@gmail.tmt|\n"
                + "Section 3|Team 3|José Gómez|jose.gomez.tmns@gmail.tmt|"
                + "This student name contains accented characters";

        resultsPage = enrollPage.enroll(enrollString);
        resultsPage.verifyHtmlMainContent("/instructorCourseEnrollPageResultForEmptyCourse.html");

        // Check 'Edit' link
        enrollPage = resultsPage.clickEditLink();
        enrollPage.verifyContains("Enroll Students for CCEnrollUiT.CS2104");
        assertEquals(expectedEnrollText, enrollPage.getEnrollText());

        // Ensure students were actually enrolled
        coursesPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE)
            .withUserId(testData.instructors.get("CCEnrollUiT.teammates.test").googleId)
            .withCourseId(courseId);
        detailsPage = loginAdminToPage(coursesPageUrl, InstructorCoursesDetailsPage.class);
        assertEquals(3, detailsPage.getStudentCountForCourse());

        ______TS("enroll action: existent course, add new students with no sections");

        enrollUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_PAGE)
                .withUserId(testData.instructors.get("CCEnrollUiT.teammates.test").googleId)
                .withCourseId(testData.courses.get("CCEnrollUiT.CS2104").getId());

        enrollPage = loginAdminToPage(enrollUrl, InstructorCourseEnrollPage.class);

        enrollString =
                "\tTeam 10\tNo Section 1\tno.section.one@gmail.tmt\t"
                + "This student has no section\t"
                + "\tTeam 11\tNo Section 2\tno.section.two@gmail.tmt\t\n";

        expectedEnrollText =
                "Section|Team|Name|Email|Comments\n"
                + "|Team 10|No Section 1|no.section.one@gmail.tmt|"
                + "This student has no section\n"
                + "|Team 11|No Section 2|no.section.two@gmail.tmt|";

        resultsPage = enrollPage.enroll(enrollString);
        resultsPage.verifyHtmlMainContent("/instructorCourseEnrollPageResultEmptySection.html");

        // Check 'Edit' link
        enrollPage = resultsPage.clickEditLink();
        enrollPage.verifyContains("Enroll Students for CCEnrollUiT.CS2104");
        assertEquals(expectedEnrollText, enrollPage.getEnrollText());

        // Ensure students were actually enrolled
        coursesPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE)
                .withUserId(testData.instructors.get("CCEnrollUiT.teammates.test").googleId)
                .withCourseId(courseId);
        detailsPage = loginAdminToPage(coursesPageUrl, InstructorCoursesDetailsPage.class);
        assertEquals(5, detailsPage.getStudentCountForCourse());

        ______TS("enroll action: fail to enroll as a team cannot be in 2 different sections");

        enrollUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_PAGE)
            .withUserId(testData.instructors.get("CCEnrollUiT.teammates.test").googleId)
            .withCourseId(testData.courses.get("CCEnrollUiT.CS2104").getId());

        enrollPage = loginAdminToPage(enrollUrl, InstructorCourseEnrollPage.class);

        enrollString = "Different Section\tTeam 1\tAlice Betsy\talice.b.tmms@gmail.tmt\t\n";

        enrollPage.enrollUnsuccessfully(enrollString);
        enrollPage.waitForTextsForAllStatusMessagesToUserEquals(
                "The team \"Team 1\" is in multiple sections. "
                        + "The team ID should be unique across the entire course "
                        + "and a team cannot be spread across multiple sections."
                        + "\nPlease use the enroll page to edit multiple students");

        ______TS("enroll action: fail to enroll as there is no input");

        enrollUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_PAGE)
            .withUserId(testData.instructors.get("CCEnrollUiT.teammates.test").googleId)
            .withCourseId(testData.courses.get("CCEnrollUiT.CS2104").getId());

        enrollPage = loginAdminToPage(enrollUrl, InstructorCourseEnrollPage.class);

        enrollString = "";

        enrollPage.enrollUnsuccessfully(enrollString);
        enrollPage.waitForTextsForAllStatusMessagesToUserEquals("Please input at least one student detail.");

        ______TS("enroll action: fail to enroll as there is an invalid line");

        enrollUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_PAGE)
                .withUserId(testData.instructors.get("CCEnrollUiT.teammates.test").googleId)
                .withCourseId(testData.courses.get("CCEnrollUiT.CS2104").getId());

        enrollPage = loginAdminToPage(enrollUrl, InstructorCourseEnrollPage.class);

        enrollString =
                // A new student with no email input
                "Section 3\tTeam 3\tFrank Hughe\t\t\t"
                // A new student with invalid email input
                + "Section 1\tTeam 1\tBlack Jack\tbjack.gmail.tmt\tThis student email is invalid\t"
                // A new student with invalid team name
                + "Section 1\t" + StringHelperExtension.generateStringOfLength(FieldValidator.TEAM_NAME_MAX_LENGTH + 1)
                        + "\tRobert Downey\trob@email.tmt\tThis student team name is too long\t"
                // A new student with invalid name
                + "Section 2\tTeam 2\t"
                        + StringHelperExtension.generateStringOfLength(FieldValidator.PERSON_NAME_MAX_LENGTH + 1)
                        + "\tlongname@email.tmt\tThis student name is too long\n";

        enrollPage.enrollUnsuccessfully(enrollString);
        enrollPage.verifyHtmlMainContent("/instructorCourseEnrollError.html");

        ______TS("enroll action: scripts are successfully sanitized");

        enrollUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_PAGE)
                .withUserId(testData.instructors.get("CCEnrollUiT.teammates.test").googleId)
                .withCourseId(testData.courses.get("CCEnrollUiT.CS2104").getId());

        enrollPage = loginAdminToPage(enrollUrl, InstructorCourseEnrollPage.class);

        // Enroll a student with a script in the name
        String xssScript = "<script>alert(\"was here\");</script>";
        enrollString = "Section 5\tTeam GreyHats\tMallory " + xssScript + "\tmallory.tmms@gmail.tmt\t\n";

        // Check that the script does not appear on the InstructorCourseEnrollResult page
        resultsPage = enrollPage.enroll(enrollString);
        resultsPage.verifyNotContain(xssScript);

        // Check that the script does not appear on the InstructorCourseEnroll page either
        enrollPage = resultsPage.clickEditLink();
        enrollPage.verifyNotContain(xssScript);
    }

}
