package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

import org.openqa.selenium.By;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.common.util.Url;
import teammates.common.util.FieldValidator.FieldType;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorCourseDetailsPage;
import teammates.test.pageobjects.InstructorCourseEditPage;
import teammates.test.pageobjects.InstructorCoursesPage;

/**
 * Tests 'Edit Course Details' functionality for Instructors.
 * SUT {@link InstructorCourseEditPage}. <br>
 */
public class InstructorCourseEditPageUiTest extends BaseUiTestCase {
    private static DataBundle testData;
    private static Browser browser;
    private static InstructorCourseEditPage courseEditPage;
    
    private static String instructorId;
    private static String courseId;
    
    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/InstructorCourseEditPageUiTest.json");
        removeAndRestoreTestDataOnServer(testData);
        browser = BrowserPool.getBrowser();
        
        instructorId = testData.instructors.get("InsCrsEdit.test").googleId;
        courseId = testData.courses.get("InsCrsEdit.CS2104").id;
    }
    
    @Test
    public void allTests() throws Exception {
        testContent();
        
        testEditInstructorLink();
        testNewInstructorLink();
        testInputValidation();
        
        testInviteInstructorAction();
        testAddInstructorAction();
        testEditInstructorAction();
        testDeleteInstructorAction();
        
        testUnregisteredInstructorEmailNotEditable();
        
        testDeleteCourseAction();

    }
    
    public void testContent() throws Exception {
        
        ______TS("page load: Helper privileges");
        
        instructorId = testData.instructors.get("InsCrsEdit.Helper").googleId;
        courseEditPage = getCourseEditPage();
        
        // This is the full HTML verification for Instructor Course Edit Page, the rest can all be verifyMainHtml
        courseEditPage.verifyHtml("/instructorCourseEditHelper.html");
        
        ______TS("page load: Co-owner privileges");
        
        instructorId = testData.instructors.get("InsCrsEdit.test").googleId;
        courseEditPage = getCourseEditPage();
        courseEditPage.verifyHtmlMainContent("/instructorCourseEditCoowner.html" );
    }
    
    private void testEditInstructorLink() {
        ______TS("edit instructor link");
        assertEquals(true, courseEditPage.clickEditInstructorLink(1));
    }

    private void testNewInstructorLink() {
        ______TS("add new instructor link");
        assertEquals(true, courseEditPage.clickShowNewInstructorFormButton());
        
        assertEquals(true, courseEditPage.clickOnAccessLevelViewDetails("Co-owner"));
        assertEquals(true, courseEditPage.clickOnAccessLevelViewDetails("Manager"));
        assertEquals(true, courseEditPage.clickOnAccessLevelViewDetails("Observer"));
        assertEquals(true, courseEditPage.clickOnAccessLevelViewDetails("Tutor"));
    }

    private void testInputValidation() {
        
        ______TS("Checking max-length enforcement by the text boxes");
        String maxLengthInstructorName = StringHelper.generateStringOfLength(FieldValidator.PERSON_NAME_MAX_LENGTH);
        String longInstructorName = StringHelper.generateStringOfLength(FieldValidator.PERSON_NAME_MAX_LENGTH + 1);
        
        // Add instructor
        assertEquals(maxLengthInstructorName, courseEditPage.fillInstructorName(maxLengthInstructorName));
        assertEquals(longInstructorName.substring(0, FieldValidator.PERSON_NAME_MAX_LENGTH), 
                     courseEditPage.fillInstructorName(longInstructorName));
        // Edit instructor
        assertEquals(maxLengthInstructorName, courseEditPage.fillInstructorName(maxLengthInstructorName));
        assertEquals(longInstructorName.substring(0, FieldValidator.PERSON_NAME_MAX_LENGTH), 
                     courseEditPage.fillInstructorName(longInstructorName));
        

        String maxLengthEmail = StringHelper.generateStringOfLength(FieldValidator.EMAIL_MAX_LENGTH);
        String longEmail = StringHelper.generateStringOfLength(FieldValidator.EMAIL_MAX_LENGTH + 1);
        
        // Add instructor
        assertEquals(maxLengthEmail, courseEditPage.fillInstructorEmail(maxLengthEmail));
        assertEquals(longEmail.substring(0, FieldValidator.EMAIL_MAX_LENGTH), 
                     courseEditPage.fillInstructorEmail(longEmail));
        // Edit instructor
        assertEquals(maxLengthEmail, courseEditPage.editInstructorEmail(maxLengthEmail));
        assertEquals(longEmail.substring(0, FieldValidator.EMAIL_MAX_LENGTH), 
                     courseEditPage.editInstructorEmail(longEmail));
    }

    private void testInviteInstructorAction() {
        ______TS("success: invite an uregistered instructor");
        
        courseEditPage.clickInviteInstructorLink();
        courseEditPage.verifyStatus(Const.StatusMessages.COURSE_REMINDER_SENT_TO + "InsCrsEdit.newInstr@gmail.tmt");
    }

    private void testAddInstructorAction() {

        ______TS("success: add an instructor");
        
        courseEditPage.addNewInstructor("Teammates Instructor", "InsCrsEdit.instructor@gmail.tmt");
        courseEditPage.verifyStatus(String.format(Const.StatusMessages.COURSE_INSTRUCTOR_ADDED, "Teammates Instructor",
                                                  "InsCrsEdit.instructor@gmail.tmt"));
        
        Url courseDetailsLink = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE)
                                    .withCourseId(courseId)
                                    .withUserId(testData.instructors.get("InsCrsEdit.test").googleId);
            
        InstructorCourseDetailsPage courseDetailsPage = courseEditPage.navigateTo(
                                                                courseDetailsLink, InstructorCourseDetailsPage.class);
        
        courseDetailsPage.verifyHtmlPart(By.id("instructors"), "/instructorCourseDetailsAddInstructor.html");
        courseEditPage = getCourseEditPage();
    
        ______TS("failure: add an existing instructor");
        
        courseEditPage.addNewInstructor("Teammates Instructor", "InsCrsEdit.instructor@gmail.tmt");
        courseEditPage.verifyStatus(Const.StatusMessages.COURSE_INSTRUCTOR_EXISTS);
        
        ______TS("failure: add an instructor with an invalid parameter");
        String invalidEmail = "InsCrsEdit.email.tmt";
        
        courseEditPage.addNewInstructor("Teammates Instructor", invalidEmail);
        courseEditPage.verifyStatus((new FieldValidator()).getInvalidityInfo(FieldType.EMAIL, invalidEmail));

        String invalidName = "";
        
        courseEditPage.addNewInstructor(invalidName, "teammates@email.tmt");
        courseEditPage.verifyStatus((new FieldValidator()).getInvalidityInfo(FieldType.PERSON_NAME, invalidName));
    }

    private void testEditInstructorAction() {

        ______TS("success: edit an instructor");
        
        courseEditPage.editInstructor(instructorId, "New name", "new_email@email.tmt");
        courseEditPage.verifyStatus(Const.StatusMessages.COURSE_INSTRUCTOR_EDITED);
        
        ______TS("success: edit an instructor (InsCrsEdit.coord)--viewing instructor permission details");
        int instructorIndex = 1;
        
        assertEquals(true, courseEditPage.clickEditInstructorLink(instructorIndex));
        
        // Manager
        courseEditPage.clickViewDetailsLinkForInstructor(instructorIndex, 2);
        assertFalse(courseEditPage.isPrivilegeCheckboxInModalChecked("canmodifycourse"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInModalChecked("canmodifyinstructor"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInModalChecked("canmodifysession"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInModalChecked("canmodifystudent"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInModalChecked("canviewstudentinsection"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInModalChecked("cangivecommentinsection"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInModalChecked("canviewcommentinsection"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInModalChecked("canmodifycommentinsection"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInModalChecked("cansubmitsessioninsection"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInModalChecked("canviewsessioninsection"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInModalChecked("canmodifysessioncommentinsection"));
        courseEditPage.closeModal();
        
        // Observer
        courseEditPage.clickViewDetailsLinkForInstructor(instructorIndex, 3);
        assertFalse(courseEditPage.isPrivilegeCheckboxInModalChecked("canmodifycourse"));
        assertFalse(courseEditPage.isPrivilegeCheckboxInModalChecked("canmodifyinstructor"));
        assertFalse(courseEditPage.isPrivilegeCheckboxInModalChecked("canmodifysession"));
        assertFalse(courseEditPage.isPrivilegeCheckboxInModalChecked("canmodifystudent"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInModalChecked("canviewstudentinsection"));
        assertFalse(courseEditPage.isPrivilegeCheckboxInModalChecked("cangivecommentinsection"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInModalChecked("canviewcommentinsection"));
        assertFalse(courseEditPage.isPrivilegeCheckboxInModalChecked("canmodifycommentinsection"));
        assertFalse(courseEditPage.isPrivilegeCheckboxInModalChecked("cansubmitsessioninsection"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInModalChecked("canviewsessioninsection"));
        assertFalse(courseEditPage.isPrivilegeCheckboxInModalChecked("canmodifysessioncommentinsection"));
        courseEditPage.closeModal();
        
        // Tutor
        courseEditPage.clickViewDetailsLinkForInstructor(instructorIndex, 4);
        assertFalse(courseEditPage.isPrivilegeCheckboxInModalChecked("canmodifycourse"));
        assertFalse(courseEditPage.isPrivilegeCheckboxInModalChecked("canmodifyinstructor"));
        assertFalse(courseEditPage.isPrivilegeCheckboxInModalChecked("canmodifysession"));
        assertFalse(courseEditPage.isPrivilegeCheckboxInModalChecked("canmodifystudent"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInModalChecked("canviewstudentinsection"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInModalChecked("cangivecommentinsection"));
        assertFalse(courseEditPage.isPrivilegeCheckboxInModalChecked("canviewcommentinsection"));
        assertFalse(courseEditPage.isPrivilegeCheckboxInModalChecked("canmodifycommentinsection"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInModalChecked("cansubmitsessioninsection"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInModalChecked("canviewsessioninsection"));
        assertFalse(courseEditPage.isPrivilegeCheckboxInModalChecked("canmodifysessioncommentinsection"));
        courseEditPage.closeModal();
        
        // this should be click co-owner role
        courseEditPage.clickViewDetailsLinkForInstructor(instructorIndex, 1);
        courseEditPage.verifyHtmlMainContent("/instructorCourseEditEditInstructorPrivilegesModal.html");
        courseEditPage.closeModal();
        
        ______TS("success: edit an instructor with privileges");
        //courseEditPage.clickEditInstructorLink(instructorIndex);
        
        assertEquals(true, courseEditPage.displayedToStudentCheckBox(instructorIndex).isSelected());
        // not displayed to students
        courseEditPage.clickDisplayedToStudentCheckBox(instructorIndex);
        // select the role as Custom for instr1
        courseEditPage.selectRoleForInstructor(instructorIndex, "Custom");
        
        // deselect some privileges from Co-owner default values
        courseEditPage.clickCourseLevelPrivilegesLink(instructorIndex, 1);
        courseEditPage.clickCourseLevelPrivilegesLink(instructorIndex, 2);
        courseEditPage.clickCourseLevelPrivilegesLink(instructorIndex, 8);
        courseEditPage.clickAddSessionLevelPrivilegesLink(instructorIndex);
        courseEditPage.clickSectionCheckBoxInSectionLevel(instructorIndex, 1, 2);
        courseEditPage.clickViewStudentCheckBoxInSectionLevel(instructorIndex, 1);
        courseEditPage.clickViewOthersCommentsCheckBoxInSectionLevel(instructorIndex, 1);
        courseEditPage.clickViewSessionResultsCheckBoxInSectionLevel(instructorIndex, 1);
        courseEditPage.clickSessionLevelInSectionLevel(instructorIndex, 1);
        courseEditPage.clickAddSessionLevelPrivilegesLink(instructorIndex);
        courseEditPage.clickAddSessionLevelPrivilegesLink(instructorIndex);
        courseEditPage.clickSectionCheckBoxInSectionLevel(instructorIndex, 3, 2);
        courseEditPage.clickModifySessionResultCheckBoxInSectionLevel(instructorIndex, 3);
        // after 3 sections added, no more things to add
        assertEquals(false, courseEditPage.addSessionLevelPrivilegesLink(instructorIndex).isDisplayed());
        courseEditPage.verifyHtmlMainContent("/instructorCourseEditEditInstructorPrivilegesBeforeSubmit.html");
        courseEditPage.clickSaveInstructorButton(instructorIndex);
        courseEditPage.verifyHtmlMainContent("/instructorCourseEditEditInstructorPrivilegesSuccessful.html");
        assertEquals(true, courseEditPage.clickEditInstructorLink(instructorIndex));
        courseEditPage.verifyHtmlMainContent(
                            "/instructorCourseEditEditInstructorPrivilegesSuccessfulAndCheckEditAgain.html");
        courseEditPage.clickSaveInstructorButton(instructorIndex);
        
        ______TS("failure: edit failed due to invalid parameters");
        String invalidEmail = "InsCrsEdit.email.tmt";
        
        courseEditPage.editInstructor(instructorId, "New name", invalidEmail);
        courseEditPage.verifyStatus((new FieldValidator()).getInvalidityInfo(FieldType.EMAIL, invalidEmail));
        
        String invalidName = "";
        
        courseEditPage.editInstructor(instructorId, invalidName, "teammates@email.tmt");
        courseEditPage.verifyStatus((new FieldValidator()).getInvalidityInfo(FieldType.PERSON_NAME, invalidName));
        
        ______TS("success: test Custom radio button getting other privileges' default values when selected");
        instructorIndex = 2;
        courseEditPage.clickEditInstructorLink(instructorIndex);
        
        // Tutor
        courseEditPage.selectRoleForInstructor(instructorIndex, "Tutor");
        courseEditPage.clickSaveInstructorButton(instructorIndex);
        courseEditPage.clickEditInstructorLink(instructorIndex);
        courseEditPage.selectRoleForInstructor(instructorIndex, "Custom");
        assertFalse(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "canmodifycourse"));
        assertFalse(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "canmodifyinstructor"));
        assertFalse(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "canmodifysession"));
        assertFalse(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "canmodifystudent"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "canviewstudentinsection"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "cangivecommentinsection"));
        assertFalse(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "canviewcommentinsection"));
        assertFalse(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "canmodifycommentinsection"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "cansubmitsessioninsection"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "canviewsessioninsection"));
        assertFalse(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "canmodifysessioncommentinsection"));
        
        // Observer
        courseEditPage.selectRoleForInstructor(instructorIndex, "Observer");
        courseEditPage.clickSaveInstructorButton(instructorIndex);
        courseEditPage.clickEditInstructorLink(instructorIndex);
        courseEditPage.selectRoleForInstructor(instructorIndex, "Custom");
        assertFalse(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "canmodifycourse"));
        assertFalse(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "canmodifyinstructor"));
        assertFalse(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "canmodifysession"));
        assertFalse(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "canmodifystudent"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "canviewstudentinsection"));
        assertFalse(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "cangivecommentinsection"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "canviewcommentinsection"));
        assertFalse(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "canmodifycommentinsection"));
        assertFalse(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "cansubmitsessioninsection"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "canviewsessioninsection"));
        assertFalse(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "canmodifysessioncommentinsection"));
        
        // Manager
        courseEditPage.selectRoleForInstructor(instructorIndex, "Manager");
        courseEditPage.clickSaveInstructorButton(instructorIndex);
        courseEditPage.clickEditInstructorLink(instructorIndex);
        courseEditPage.selectRoleForInstructor(instructorIndex, "Custom");
        assertFalse(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "canmodifycourse"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "canmodifyinstructor"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "canmodifysession"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "canmodifystudent"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "canviewstudentinsection"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "cangivecommentinsection"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "canviewcommentinsection"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "canmodifycommentinsection"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "cansubmitsessioninsection"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "canviewsessioninsection"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "canmodifysessioncommentinsection"));
        
        // Co-owner
        courseEditPage.selectRoleForInstructor(instructorIndex, "Co-owner");
        courseEditPage.clickSaveInstructorButton(instructorIndex);
        courseEditPage.clickEditInstructorLink(instructorIndex);
        courseEditPage.selectRoleForInstructor(instructorIndex, "Custom");
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "canmodifycourse"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "canmodifyinstructor"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "canmodifysession"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "canmodifystudent"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "canviewstudentinsection"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "cangivecommentinsection"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "canviewcommentinsection"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "canmodifycommentinsection"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "cansubmitsessioninsection"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "canviewsessioninsection"));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(instructorIndex, "canmodifysessioncommentinsection"));
        courseEditPage.selectRoleForInstructor(instructorIndex, "Co-owner");
        
    }
    
    private void testDeleteInstructorAction() {
        
        ______TS("delete instructor then cancel");
        courseEditPage.clickDeleteInstructorLinkAndCancel();
        assertNotNull(BackDoor.getInstructorAsJsonByGoogleId(instructorId, courseId));
        
        ______TS("delete instructor successfully");
        courseEditPage.clickDeleteInstructorLinkAndConfirm();
        String expectedMsg = "The instructor has been deleted from the course.";
        courseEditPage.verifyStatus(expectedMsg);
        
        ______TS("failed to delete the last instructor");
        courseEditPage.clickDeleteInstructorLinkAndConfirm();
        courseEditPage.clickDeleteInstructorLinkAndConfirm();
        courseEditPage.clickDeleteInstructorLinkAndConfirm();
        courseEditPage.clickDeleteInstructorLinkAndConfirm();
        courseEditPage.verifyStatus(Const.StatusMessages.COURSE_INSTRUCTOR_DELETE_NOT_ALLOWED);
        
        ______TS("deleted own instructor role and redirect to courses page");
        // Change login id to another instructor
        BackDoor.createInstructor(testData.instructors.get("InsCrsEdit.coord"));
        instructorId = testData.instructors.get("InsCrsEdit.coord").googleId;
        courseEditPage.clickDeleteInstructorLinkAndConfirm();

        InstructorCoursesPage coursesPage = courseEditPage.changePageType(InstructorCoursesPage.class);
        coursesPage.verifyStatus(Const.StatusMessages.COURSE_INSTRUCTOR_DELETED + "\n"
                                 + Const.StatusMessages.COURSE_EMPTY);
        
        // Change back login id to original instructor to ensure remaining test cases work properly
        instructorId = testData.instructors.get("InsCrsEdit.test").googleId;
        BackDoor.createInstructor(testData.instructors.get("InsCrsEdit.test"));
    }
    
    private void testDeleteCourseAction() {
        // TODO: use navigateTo instead
        courseEditPage = getCourseEditPage();
        ______TS("delete course then cancel");
        courseEditPage.clickDeleteCourseLinkAndCancel();
        assertNotNull(BackDoor.getCourseAsJson(courseId));
        
        ______TS("delete course then proceed");
        InstructorCoursesPage coursePage = courseEditPage.clickDeleteCourseLinkAndConfirm();
        coursePage.verifyContains("Add New Course");
    }
    
    private void testUnregisteredInstructorEmailNotEditable() {
        courseEditPage = getCourseEditPage();
        ______TS("make a new unregistered instructor and test that its email can't be edited");
        courseEditPage.addNewInstructor("Unreg Instructor", "InstructorCourseEditEmail@gmail.tmt");
        
        assertEquals("Unreg Instructor", courseEditPage.getNameField(3).getAttribute("value"));
        assertFalse(courseEditPage.getNameField(3).isEnabled());
        
        assertTrue(courseEditPage.clickEditInstructorLink(3));
        assertEquals("true", courseEditPage.getEmailField(3).getAttribute("readonly"));
        assertTrue(courseEditPage.getNameField(3).isEnabled());
    }
    
    private InstructorCourseEditPage getCourseEditPage() {        
        Url courseEditPageLink = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE)
                                    .withUserId(instructorId)
                                    .withCourseId(courseId);
        
        return loginAdminToPage(browser, courseEditPageLink, InstructorCourseEditPage.class);
    }

    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }
    
}