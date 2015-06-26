package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.common.util.Url;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorCourseEnrollPage;
import teammates.test.pageobjects.InstructorCourseEnrollResultPage;
import teammates.test.pageobjects.InstructorCoursesDetailsPage;


/**
 * Covers 'enroll' view for instructors.
 * SUT: {@link InstructorCourseEnrollPage}.
 */
public class InstructorCourseEnrollPageUiTest extends BaseUiTestCase {
    private static DataBundle testData;
    private static Browser browser;
    private InstructorCourseEnrollPage enrollPage;
    
    private static String enrollString = "";
    private Url enrollUrl;

    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/InstructorCourseEnrollPageUiTest.json");
        removeAndRestoreTestDataOnServer(testData);
        
        browser = BrowserPool.getBrowser();
    }
    
    @Test
    public void testInstructorCourseEnrollPage() throws Exception{
        testContent();
        testSampleLink();
        testEnrollAction();
    }

    private void testContent() {
        
        ______TS("typical enroll page");
        
        enrollUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_PAGE)
        .withUserId(testData.instructors.get("CCEnrollUiT.teammates.test").googleId)
        .withCourseId(testData.courses.get("CCEnrollUiT.CS2104").id);
        
        enrollPage = loginAdminToPage(browser, enrollUrl, InstructorCourseEnrollPage.class);

        // This is the full HTML verification for Instructor Course Enroll Page, the rest can all be verifyMainHtml
        enrollPage.verifyHtml("/InstructorCourseEnrollPage.html");
    }

    private void testSampleLink() throws Exception {
        
        ______TS("link for the sample spreadsheet");
        String expectedShaHexForWindows = "98df8d0e8285a8192ed88183380947ca1c36ca68";
        String expectedShaHexForUnix = "e02099ef19b16a5d30e8d09e6d22f179fa123272";

        
        try{
            enrollPage.verifyDownloadableFile(enrollPage.getSpreadsheetLink(), expectedShaHexForWindows);
        } catch (AssertionError e){
            enrollPage.verifyDownloadableFile(enrollPage.getSpreadsheetLink(), expectedShaHexForUnix);
        }
    }

    private void testEnrollAction() throws Exception {
        /* We test both empty and non-empty courses because the generated
         * enroll result page is slightly different for the two cases.
         */
        
        String courseId = testData.courses.get("CCEnrollUiT.CS2104").id;

        ______TS("enroll action: existent course, enroll lines with section field");

        enrollUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_PAGE)
            .withUserId(testData.instructors.get("CCEnrollUiT.teammates.test").googleId)
            .withCourseId(testData.courses.get("CCEnrollUiT.CS2104").id);

        enrollPage = loginAdminToPage(browser, enrollUrl, InstructorCourseEnrollPage.class);

        enrollString = "Section | Team | Name | Email | Comments\n";
        // Modify team for student within section
        enrollString += "Section 1| Team 4 | Alice Betsy | alice.b.tmms@gmail.tmt | This comment has been changed\n";
        // Modify section and team
        enrollString += "Section 2| Team 2 | Benny Charles| benny.c.tmms@gmail.tmt |\n";
        // A student with no comment
        enrollString += "Section 3 | Team 3 |Frank Galoe | frank.g.tmms@gmail.tmt |\n";
        // A new student with name containing accented characters
        enrollString += "Section 1 | Team 1|José Gómez | jose.gomez.tmns@gmail.tmt | This student name contains accented characters\n";

        InstructorCourseEnrollResultPage resultsPage = enrollPage.enroll(enrollString);

        // This is the full HTML verification for Instructor Course Enroll Results Page, the rest can all be verifyMainHtml
        resultsPage.verifyHtml("/InstructorCourseEnrollPageResult.html");

        // Check 'Edit' link
        enrollPage = resultsPage.clickEditLink();
        enrollPage.verifyContains("Enroll Students for CCEnrollUiT.CS2104");
        assertEquals(enrollString, enrollPage.getEnrollText());
        
        // Ensure students were actually enrolled
        Url coursesPageUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE)
            .withUserId(testData.instructors.get("CCEnrollUiT.teammates.test").googleId)
            .withCourseId(courseId);
        InstructorCoursesDetailsPage detailsPage = loginAdminToPage(browser, coursesPageUrl, InstructorCoursesDetailsPage.class);
        assertEquals(6, detailsPage.getStudentCountForCourse("CCEnrollUiT.CS2104"));

        ______TS("enroll action: empty course, enroll lines with header containing empty columns, no sections");
        
        // Make the course empty
        BackDoor.deleteCourse(courseId);
        BackDoor.createCourse(testData.courses.get("CCEnrollUiT.CS2104"));
        BackDoor.createInstructor(testData.instructors.get("CCEnrollUiT.teammates.test"));
        
        enrollUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_PAGE)
            .withUserId(testData.instructors.get("CCEnrollUiT.teammates.test").googleId)
            .withCourseId(testData.courses.get("CCEnrollUiT.CS2104").id);
        
        enrollPage = loginAdminToPage(browser, enrollUrl, InstructorCourseEnrollPage.class);
        
        enrollString = "| Name | Email | | Team | Comments\n";
        enrollString += "|Alice Betsy | alice.b.tmms@gmail.tmt || Team 1 | This comment has been changed\n";
        // A student with no comment
        enrollString += "|Frank Galoe | frank.g.tmms@gmail.tmt || Team 1 |\n";
        // A new student with name containing accented characters
        enrollString += "|José Gómez | jose.gomez.tmns@gmail.tmt || Team 3 | This student name contains accented characters\n";
                
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
        detailsPage = loginAdminToPage(browser, coursesPageUrl, InstructorCoursesDetailsPage.class);
        assertEquals(3, detailsPage.getStudentCountForCourse("CCEnrollUiT.CS2104"));

        ______TS("enroll action: fail to enroll as a team cannot be in 2 different sections");

        enrollUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_PAGE)
            .withUserId(testData.instructors.get("CCEnrollUiT.teammates.test").googleId)
            .withCourseId(testData.courses.get("CCEnrollUiT.CS2104").id);
        
        enrollPage = loginAdminToPage(browser, enrollUrl, InstructorCourseEnrollPage.class);

        enrollString = "Section | Team | Name | Email | Comments\n";
        enrollString += "Different Section | Team 1 | Alice Betsy | alice.b.tmms@gmail.tmt |\n";

        enrollPage.enrollUnsuccessfully(enrollString);
        enrollPage.verifyStatus("The team \"Team 1\" is in multiple sections. The team ID should be unique across the entire course and a team cannot be spread across multiple sections."
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
            .withCourseId(testData.courses.get("CCEnrollUiT.CS2104").id);
        
        enrollPage = loginAdminToPage(browser, enrollUrl, InstructorCourseEnrollPage.class);
        
        enrollString = "";

        enrollPage.enrollUnsuccessfully(enrollString);
        enrollPage.verifyStatus("Please input at least one student detail.");
        
        ______TS("enroll action: fail to enroll as there is an invalid line");
        
        enrollString = "Team | Name | Email | Comment\n";
        // A new student with no email input
        enrollString += "Team 3 | Frank Hughe\n";
        // A new student with invalid email input
        enrollString += "Team 1 | Black Jack | bjack.gmail.tmt | This student email is invalid\n";
        // A new student with invalid team name
        enrollString += StringHelper.generateStringOfLength(FieldValidator.TEAM_NAME_MAX_LENGTH + 1)
                        + " | Robert Downey | rob@email.tmt | This student team name is too long\n";
        // A new student with invalid name
        enrollString += "Team 2 | " + StringHelper.generateStringOfLength(FieldValidator.PERSON_NAME_MAX_LENGTH + 1)
                        + " | longname@email.tmt | This student name is too long\n";
                        
        enrollPage.enrollUnsuccessfully(enrollString);
        enrollPage.verifyHtmlMainContent("/instructorCourseEnrollError.html");
    }

    @AfterClass
        public static void classTearDown() throws Exception {
            BrowserPool.release(browser);
        }
}