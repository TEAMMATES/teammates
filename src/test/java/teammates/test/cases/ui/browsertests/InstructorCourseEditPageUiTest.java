package teammates.test.cases.ui.browsertests;

import org.openqa.selenium.By;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.test.driver.BackDoor;
import teammates.test.pageobjects.AppPage;
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
    public static void classSetup() {
        printTestClassHeader();
        testData = loadDataBundle("/InstructorCourseEditPageUiTest.json");
        removeAndRestoreTestDataOnServer(testData);
        browser = BrowserPool.getBrowser();
        
        instructorId = testData.instructors.get("InsCrsEdit.test").googleId;
        courseId = testData.courses.get("InsCrsEdit.CS2104").getId();
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

        testEditCourseAction();
        testDeleteCourseAction();

    }
    
    public void testContent() throws Exception {
        
        ______TS("page load: Helper privileges (custom)");
        
        instructorId = testData.instructors.get("InsCrsEdit.Helper").googleId;
        courseEditPage = getCourseEditPage();
        
        // This is the full HTML verification for Instructor Course Edit Page, the rest can all be verifyMainHtml
        courseEditPage.verifyHtml("/instructorCourseEditHelper.html");
        
        ______TS("page load: Co-owner privileges");
        
        instructorId = testData.instructors.get("InsCrsEdit.test").googleId;
        courseEditPage = getCourseEditPage();
        courseEditPage.verifyHtmlMainContent("/instructorCourseEditCoowner.html");
        
        ______TS("page load: Manager privileges");
        
        instructorId = testData.instructors.get("InsCrsEdit.manager").googleId;
        courseEditPage = getCourseEditPage();
        courseEditPage.verifyHtmlMainContent("/instructorCourseEditManager.html");
        
        ______TS("page load: Observer privileges");
        
        instructorId = testData.instructors.get("InsCrsEdit.observer").googleId;
        courseEditPage = getCourseEditPage();
        courseEditPage.verifyHtmlMainContent("/instructorCourseEditObserver.html");
        
        ______TS("page load: Tutor privileges");
        
        instructorId = testData.instructors.get("InsCrsEdit.tutor").googleId;
        courseEditPage = getCourseEditPage();
        courseEditPage.verifyHtmlMainContent("/instructorCourseEditTutor.html");
        
        ______TS("go back to co-owner privileges");
        
        instructorId = testData.instructors.get("InsCrsEdit.test").googleId;
        courseEditPage = getCourseEditPage();
    }
    
    private void testEditInstructorLink() {
        ______TS("edit instructor link");
        assertTrue(courseEditPage.clickEditInstructorLink(1));
    }

    private void testNewInstructorLink() {
        ______TS("add new instructor link");
        assertTrue(courseEditPage.clickShowNewInstructorFormButton());
        
        assertTrue(courseEditPage.clickOnNewInstructorAccessLevelViewDetails("Co-owner"));
        assertTrue(courseEditPage.clickOnNewInstructorAccessLevelViewDetails("Manager"));
        assertTrue(courseEditPage.clickOnNewInstructorAccessLevelViewDetails("Observer"));
        assertTrue(courseEditPage.clickOnNewInstructorAccessLevelViewDetails("Tutor"));
    }

    private void testInputValidation() {
        
        ______TS("Checking max-length enforcement by the text boxes");
        
        String maxLengthInstructorName = StringHelper.generateStringOfLength(FieldValidator.PERSON_NAME_MAX_LENGTH);
        String longInstructorName = StringHelper.generateStringOfLength(FieldValidator.PERSON_NAME_MAX_LENGTH + 1);
        
        // Add instructor
        assertEquals(maxLengthInstructorName, courseEditPage.fillNewInstructorName(maxLengthInstructorName));
        assertEquals(longInstructorName.substring(0, FieldValidator.PERSON_NAME_MAX_LENGTH),
                     courseEditPage.fillNewInstructorName(longInstructorName));
        // Edit instructor
        assertEquals(maxLengthInstructorName, courseEditPage.editInstructorName(1, maxLengthInstructorName));
        assertEquals(longInstructorName.substring(0, FieldValidator.PERSON_NAME_MAX_LENGTH),
                     courseEditPage.editInstructorName(1, longInstructorName));
        

        String maxLengthEmail = StringHelper.generateStringOfLength(FieldValidator.EMAIL_MAX_LENGTH);
        String longEmail = StringHelper.generateStringOfLength(FieldValidator.EMAIL_MAX_LENGTH + 1);
        
        // Add instructor
        assertEquals(maxLengthEmail, courseEditPage.fillNewInstructorEmail(maxLengthEmail));
        assertEquals(longEmail.substring(0, FieldValidator.EMAIL_MAX_LENGTH),
                     courseEditPage.fillNewInstructorEmail(longEmail));
        // Edit instructor
        assertEquals(maxLengthEmail, courseEditPage.editInstructorEmail(1, maxLengthEmail));
        assertEquals(longEmail.substring(0, FieldValidator.EMAIL_MAX_LENGTH),
                     courseEditPage.editInstructorEmail(1, longEmail));
    }

    private void testInviteInstructorAction() {
        ______TS("success: invite an uregistered instructor");
        int unregisteredInsturctorIndex = 4;
        
        courseEditPage.clickInviteInstructorLink(unregisteredInsturctorIndex);
        courseEditPage.verifyStatus(Const.StatusMessages.COURSE_REMINDER_SENT_TO + "InsCrsEdit.newInstr@gmail.tmt");
    }

    private void testAddInstructorAction() throws Exception {

        ______TS("success: add an instructor with privileges");
        
        courseEditPage.clickShowNewInstructorFormButton();
        courseEditPage.fillNewInstructorName("Teammates Instructor");
        courseEditPage.fillNewInstructorEmail("InsCrsEdit.instructor@gmail.tmt");
        
        int newInstructorIndex = 8;
        
        courseEditPage.selectRoleForInstructor(newInstructorIndex, "Custom");
        courseEditPage.clickCourseLevelPrivilegesLink(newInstructorIndex, 0);
        courseEditPage.clickCourseLevelPrivilegesLink(newInstructorIndex, 3);
        courseEditPage.clickCourseLevelPrivilegesLink(newInstructorIndex, 7);
        
        courseEditPage.clickAddSectionLevelPrivilegesLink(newInstructorIndex);
        courseEditPage.clickSectionSelectionCheckBox(newInstructorIndex, 0, 0);
        courseEditPage.clickSectionLevelPrivilegeLink(newInstructorIndex, 0, 0);
        courseEditPage.clickSectionLevelPrivilegeLink(newInstructorIndex, 0, 5);
        
        courseEditPage.clickAddInstructorButton();
        
        courseEditPage.verifyStatus(String.format(Const.StatusMessages.COURSE_INSTRUCTOR_ADDED, "Teammates Instructor",
                                                  "InsCrsEdit.instructor@gmail.tmt"));
        
        AppUrl courseDetailsLink = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE)
                                    .withCourseId(courseId)
                                    .withUserId(testData.instructors.get("InsCrsEdit.test").googleId);
            
        InstructorCourseDetailsPage courseDetailsPage = AppPage.getNewPageInstance(browser,
                                                                courseDetailsLink, InstructorCourseDetailsPage.class);
        courseDetailsPage.verifyHtmlPart(By.id("instructors"), "/instructorCourseDetailsAddInstructor.html");
        courseEditPage = getCourseEditPage();
        courseEditPage.clickEditInstructorLink(3);
        courseEditPage.verifyHtmlMainContent("/instructorCourseEditAddInstructor.html");
    
        ______TS("failure: add an existing instructor");
        
        courseEditPage.addNewInstructor("Teammates Instructor", "InsCrsEdit.instructor@gmail.tmt");
        courseEditPage.verifyStatus(Const.StatusMessages.COURSE_INSTRUCTOR_EXISTS);
        
        ______TS("failure: add an instructor with an invalid parameter");
        
        String invalidEmail = "InsCrsEdit.email.tmt";
        
        courseEditPage.addNewInstructor("Teammates Instructor", invalidEmail);
        courseEditPage.verifyStatus(new FieldValidator().getInvalidityInfoForEmail(invalidEmail));

        String invalidName = "";
        
        courseEditPage.addNewInstructor(invalidName, "teammates@email.tmt");
        courseEditPage.verifyStatus(new FieldValidator().getInvalidityInfoForPersonName(invalidName));
    }

    private void testEditInstructorAction() throws Exception {
        
        ______TS("failure: ajax error on clicking edit button");
        int editInstructorIndex = 1;
        
        courseEditPage.changeCourseIdInForm(editInstructorIndex, "InvalidCourse");
        courseEditPage.getEditInstructorLink(editInstructorIndex).click();
        courseEditPage.waitForAjaxLoaderGifToDisappear();
        assertTrue(courseEditPage.getEditInstructorLink(editInstructorIndex).getText().contains("Edit failed."));
        courseEditPage.reloadPage();

        ______TS("success: edit an instructor");
        
        courseEditPage.editInstructor(editInstructorIndex, "New name", "new_email@email.tmt",
                                      Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        courseEditPage.verifyStatus(String.format(Const.StatusMessages.COURSE_INSTRUCTOR_EDITED, "New name"));
        
        ______TS("success: edit an instructor (InsCrsEdit.coord)--viewing instructor permission details");
        
        assertTrue(courseEditPage.clickEditInstructorLink(editInstructorIndex));
        
        ______TS("view details: manager");
        
        courseEditPage.clickViewDetailsLinkForInstructor(editInstructorIndex, 2);
        assertFalse(courseEditPage.isPrivilegeCheckboxInModalChecked(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        assertTrue(courseEditPage.isPrivilegeCheckboxInModalChecked(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR));
        assertTrue(courseEditPage.isPrivilegeCheckboxInModalChecked(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));
        assertTrue(courseEditPage.isPrivilegeCheckboxInModalChecked(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
        assertTrue(courseEditPage.isPrivilegeCheckboxInModalChecked(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS));
        assertTrue(courseEditPage.isPrivilegeCheckboxInModalChecked(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS));
        assertTrue(courseEditPage.isPrivilegeCheckboxInModalChecked(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS));
        assertTrue(courseEditPage.isPrivilegeCheckboxInModalChecked(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS));
        assertTrue(courseEditPage.isPrivilegeCheckboxInModalChecked(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));
        assertTrue(courseEditPage.isPrivilegeCheckboxInModalChecked(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
        assertTrue(courseEditPage.isPrivilegeCheckboxInModalChecked(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS));
        courseEditPage.closeModal();
        
        ______TS("view details: observer");
        
        courseEditPage.clickViewDetailsLinkForInstructor(editInstructorIndex, 3);
        assertFalse(courseEditPage.isPrivilegeCheckboxInModalChecked(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        assertFalse(courseEditPage.isPrivilegeCheckboxInModalChecked(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR));
        assertFalse(courseEditPage.isPrivilegeCheckboxInModalChecked(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));
        assertFalse(courseEditPage.isPrivilegeCheckboxInModalChecked(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
        assertTrue(courseEditPage.isPrivilegeCheckboxInModalChecked(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS));
        assertFalse(courseEditPage.isPrivilegeCheckboxInModalChecked(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS));
        assertTrue(courseEditPage.isPrivilegeCheckboxInModalChecked(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS));
        assertFalse(courseEditPage.isPrivilegeCheckboxInModalChecked(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS));
        assertFalse(courseEditPage.isPrivilegeCheckboxInModalChecked(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));
        assertTrue(courseEditPage.isPrivilegeCheckboxInModalChecked(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
        assertFalse(courseEditPage.isPrivilegeCheckboxInModalChecked(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS));
        courseEditPage.closeModal();
        
        ______TS("view details: tutor");
        
        courseEditPage.clickViewDetailsLinkForInstructor(editInstructorIndex, 4);
        assertFalse(courseEditPage.isPrivilegeCheckboxInModalChecked(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        assertFalse(courseEditPage.isPrivilegeCheckboxInModalChecked(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR));
        assertFalse(courseEditPage.isPrivilegeCheckboxInModalChecked(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));
        assertFalse(courseEditPage.isPrivilegeCheckboxInModalChecked(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
        assertTrue(courseEditPage.isPrivilegeCheckboxInModalChecked(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS));
        assertTrue(courseEditPage.isPrivilegeCheckboxInModalChecked(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS));
        assertFalse(courseEditPage.isPrivilegeCheckboxInModalChecked(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS));
        assertFalse(courseEditPage.isPrivilegeCheckboxInModalChecked(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS));
        assertTrue(courseEditPage.isPrivilegeCheckboxInModalChecked(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));
        assertTrue(courseEditPage.isPrivilegeCheckboxInModalChecked(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
        assertFalse(courseEditPage.isPrivilegeCheckboxInModalChecked(
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS));
        courseEditPage.closeModal();
        
        ______TS("view details: co-owner");
        
        courseEditPage.clickViewDetailsLinkForInstructor(editInstructorIndex, 1);
        courseEditPage.verifyHtmlMainContent("/instructorCourseEditEditInstructorPrivilegesModal.html");
        courseEditPage.closeModal();
        
        ______TS("verify that custom has no privileges by default");
        
        int customInstrNum = 5;
        courseEditPage.clickEditInstructorLink(customInstrNum);
        assertFalse(courseEditPage.isCustomCheckboxChecked("canmodifycourse", customInstrNum));
        assertFalse(courseEditPage.isCustomCheckboxChecked("canmodifyinstructor", customInstrNum));
        assertFalse(courseEditPage.isCustomCheckboxChecked("canmodifysession", customInstrNum));
        assertFalse(courseEditPage.isCustomCheckboxChecked("canmodifystudent", customInstrNum));
        assertFalse(courseEditPage.isCustomCheckboxChecked("canviewstudentinsection", customInstrNum));
        assertFalse(courseEditPage.isCustomCheckboxChecked("cangivecommentinsection", customInstrNum));
        assertFalse(courseEditPage.isCustomCheckboxChecked("canviewcommentinsection", customInstrNum));
        assertFalse(courseEditPage.isCustomCheckboxChecked("canmodifycommentinsection", customInstrNum));
        assertFalse(courseEditPage.isCustomCheckboxChecked("cansubmitsessioninsection", customInstrNum));
        assertFalse(courseEditPage.isCustomCheckboxChecked("canviewsessioninsection", customInstrNum));
        assertFalse(courseEditPage.isCustomCheckboxChecked("canmodifysessioncommentinsection", customInstrNum));
        courseEditPage.clickSaveInstructorButton(customInstrNum);
        
        ______TS("success: edit an instructor with privileges");
        
        courseEditPage.clickEditInstructorLink(editInstructorIndex);
        assertTrue(courseEditPage.getDisplayedToStudentCheckBox(editInstructorIndex).isSelected());
        // not displayed to students
        courseEditPage.clickDisplayedToStudentCheckBox(editInstructorIndex);
        // select the role as Custom for instr1
        courseEditPage.selectRoleForInstructor(editInstructorIndex, "Custom");
        
        // deselect some privileges from Co-owner default values
        courseEditPage.clickCourseLevelPrivilegesLink(editInstructorIndex, 0);
        courseEditPage.clickCourseLevelPrivilegesLink(editInstructorIndex, 1);
        courseEditPage.clickCourseLevelPrivilegesLink(editInstructorIndex, 7);
        courseEditPage.clickAddSectionLevelPrivilegesLink(editInstructorIndex);
        courseEditPage.clickSectionSelectionCheckBox(editInstructorIndex, 0, 0);
        courseEditPage.clickSectionSelectionCheckBox(editInstructorIndex, 0, 1);
        courseEditPage.clickSectionLevelPrivilegeLink(editInstructorIndex, 0, 0);
        courseEditPage.clickSectionLevelPrivilegeLink(editInstructorIndex, 0, 2);
        courseEditPage.clickSectionLevelPrivilegeLink(editInstructorIndex, 0, 5);
        courseEditPage.clickSessionLevelInSectionLevel(editInstructorIndex, 0);
        courseEditPage.clickAddSectionLevelPrivilegesLink(editInstructorIndex);
        courseEditPage.clickSectionSelectionCheckBox(editInstructorIndex, 1, 1);
        courseEditPage.clickAddSectionLevelPrivilegesLink(editInstructorIndex);
        courseEditPage.clickSectionSelectionCheckBox(editInstructorIndex, 2, 1);
        courseEditPage.clickSectionSelectionCheckBox(editInstructorIndex, 2, 2);
        courseEditPage.clickSectionLevelPrivilegeLink(editInstructorIndex, 2, 6);
        // after 3 sections added, no more things to add
        assertFalse(courseEditPage.getAddSectionLevelPrivilegesLink(editInstructorIndex).isDisplayed());
        courseEditPage.verifyHtmlMainContent("/instructorCourseEditEditInstructorPrivilegesBeforeSubmit.html");
        
        courseEditPage.clickSaveInstructorButton(editInstructorIndex);
        courseEditPage.verifyHtmlMainContent("/instructorCourseEditEditInstructorPrivilegesSuccessful.html");
        assertTrue(courseEditPage.clickEditInstructorLink(editInstructorIndex));
        courseEditPage.verifyHtmlMainContent(
                            "/instructorCourseEditEditInstructorPrivilegesSuccessfulAndCheckEditAgain.html");
        courseEditPage.clickSaveInstructorButton(editInstructorIndex);
        
        ______TS("failure: edit failed due to invalid parameters");
        String invalidEmail = "InsCrsEdit.email.tmt";
        
        courseEditPage.editInstructor(editInstructorIndex, "New name", invalidEmail,
                                      Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        courseEditPage.verifyStatus(new FieldValidator().getInvalidityInfoForEmail(invalidEmail));
        
        String invalidName = "";
        
        courseEditPage.editInstructor(editInstructorIndex, invalidName, "teammates@email.tmt",
                                      Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        courseEditPage.verifyStatus(new FieldValidator().getInvalidityInfoForPersonName(invalidName));
        
        ______TS("success: test Custom radio button getting other privileges' default values when selected");
        editInstructorIndex = 2;
        courseEditPage.clickEditInstructorLink(editInstructorIndex);
        
        ______TS("tutor->custom");
        
        courseEditPage.selectRoleForInstructor(editInstructorIndex, "Tutor");
        courseEditPage.clickSaveInstructorButton(editInstructorIndex);
        courseEditPage.clickEditInstructorLink(editInstructorIndex);
        courseEditPage.selectRoleForInstructor(editInstructorIndex, "Custom");
        assertFalse(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        assertFalse(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR));
        assertFalse(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));
        assertFalse(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS));
        assertFalse(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS));
        assertFalse(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
        assertFalse(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS));
        
        ______TS("observer->custom");
        
        courseEditPage.selectRoleForInstructor(editInstructorIndex, "Observer");
        courseEditPage.clickSaveInstructorButton(editInstructorIndex);
        courseEditPage.clickEditInstructorLink(editInstructorIndex);
        courseEditPage.selectRoleForInstructor(editInstructorIndex, "Custom");
        assertFalse(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        assertFalse(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR));
        assertFalse(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));
        assertFalse(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS));
        assertFalse(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS));
        assertFalse(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS));
        assertFalse(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
        assertFalse(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS));
        
        ______TS("manager->custom");
        
        courseEditPage.selectRoleForInstructor(editInstructorIndex, "Manager");
        courseEditPage.clickSaveInstructorButton(editInstructorIndex);
        courseEditPage.clickEditInstructorLink(editInstructorIndex);
        courseEditPage.selectRoleForInstructor(editInstructorIndex, "Custom");
        assertFalse(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS));
        
        ______TS("co-owner->custom");
        
        courseEditPage.selectRoleForInstructor(editInstructorIndex, "Co-owner");
        courseEditPage.clickSaveInstructorButton(editInstructorIndex);
        courseEditPage.clickEditInstructorLink(editInstructorIndex);
        courseEditPage.selectRoleForInstructor(editInstructorIndex, "Custom");
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS));
        
        ______TS("verify that session level checkboxes are accessible");
        
        courseEditPage.clickAddSectionLevelPrivilegesLink(editInstructorIndex);
        courseEditPage.clickSessionLevelInSectionLevel(editInstructorIndex, 0);
        assertTrue(courseEditPage.isTuneSessionPermissionsDivVisible(editInstructorIndex, 0));
        
        ______TS("verify checkbox toggling to false");
        
        // course level
        courseEditPage.clickCourseLevelPrivilegesLink(editInstructorIndex, 4);
        assertFalse(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS));
        assertFalse(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS));
        
        courseEditPage.clickCourseLevelPrivilegesLink(editInstructorIndex, 6);
        assertFalse(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS));
        assertFalse(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS));
        
        courseEditPage.clickCourseLevelPrivilegesLink(editInstructorIndex, 9);
        assertFalse(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
        assertFalse(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS));
        
        // section level
        courseEditPage.clickSectionLevelPrivilegeLink(editInstructorIndex, 0, 0);
        assertFalse(courseEditPage.isSectionLevelPrivilegeLinkClicked(editInstructorIndex, 0, 0));
        assertFalse(courseEditPage.isSectionLevelPrivilegeLinkClicked(editInstructorIndex, 0, 1));
        
        courseEditPage.clickSectionLevelPrivilegeLink(editInstructorIndex, 0, 2);
        assertFalse(courseEditPage.isSectionLevelPrivilegeLinkClicked(editInstructorIndex, 0, 2));
        assertFalse(courseEditPage.isSectionLevelPrivilegeLinkClicked(editInstructorIndex, 0, 3));
        
        courseEditPage.clickSectionLevelPrivilegeLink(editInstructorIndex, 0, 5);
        assertFalse(courseEditPage.isSectionLevelPrivilegeLinkClicked(editInstructorIndex, 0, 5));
        assertFalse(courseEditPage.isSectionLevelPrivilegeLinkClicked(editInstructorIndex, 0, 6));
        
        // session level
        courseEditPage.clickSessionLevelPrivilegeLink(editInstructorIndex, 0, 0, 1);
        assertFalse(courseEditPage.isSessionLevelPrivilegeLinkClicked(editInstructorIndex, 0, 0, 1));
        assertFalse(courseEditPage.isSessionLevelPrivilegeLinkClicked(editInstructorIndex, 0, 0, 2));
        
        ______TS("verify checkbox toggling to true");
        
        // course level
        courseEditPage.clickCourseLevelPrivilegesLink(editInstructorIndex, 5);
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS));
        
        courseEditPage.clickCourseLevelPrivilegesLink(editInstructorIndex, 7);
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS));
        
        courseEditPage.clickCourseLevelPrivilegesLink(editInstructorIndex, 10);
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
        assertTrue(courseEditPage.isPrivilegeCheckboxInPermissionDivChecked(editInstructorIndex,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS));
        
        // section level
        courseEditPage.clickSectionLevelPrivilegeLink(editInstructorIndex, 0, 1);
        assertTrue(courseEditPage.isSectionLevelPrivilegeLinkClicked(editInstructorIndex, 0, 0));
        assertTrue(courseEditPage.isSectionLevelPrivilegeLinkClicked(editInstructorIndex, 0, 1));
        
        courseEditPage.clickSectionLevelPrivilegeLink(editInstructorIndex, 0, 3);
        assertTrue(courseEditPage.isSectionLevelPrivilegeLinkClicked(editInstructorIndex, 0, 2));
        assertTrue(courseEditPage.isSectionLevelPrivilegeLinkClicked(editInstructorIndex, 0, 3));
        
        courseEditPage.clickSectionLevelPrivilegeLink(editInstructorIndex, 0, 6);
        assertTrue(courseEditPage.isSectionLevelPrivilegeLinkClicked(editInstructorIndex, 0, 5));
        assertTrue(courseEditPage.isSectionLevelPrivilegeLinkClicked(editInstructorIndex, 0, 6));
        
        // session level
        courseEditPage.clickSessionLevelPrivilegeLink(editInstructorIndex, 0, 0, 2);
        assertTrue(courseEditPage.isSessionLevelPrivilegeLinkClicked(editInstructorIndex, 0, 0, 1));
        assertTrue(courseEditPage.isSessionLevelPrivilegeLinkClicked(editInstructorIndex, 0, 0, 2));
        
        courseEditPage.selectRoleForInstructor(editInstructorIndex, "Co-owner");
    }
    
    private void testDeleteInstructorAction() {
        
        ______TS("delete instructor then cancel");
        
        courseEditPage.clickDeleteInstructorLinkAndCancel(1);
        assertNotNull(BackDoor.getInstructorAsJsonByGoogleId(instructorId, courseId));
        
        ______TS("delete instructor successfully");
        
        courseEditPage.clickDeleteInstructorLinkAndConfirm(1);
        String expectedMsg = "The instructor has been deleted from the course.";
        courseEditPage.verifyStatus(expectedMsg);
        
        ______TS("delete all other instructors");
        
        courseEditPage.clickDeleteInstructorLinkAndConfirm(1);
        courseEditPage.clickDeleteInstructorLinkAndConfirm(1);
        courseEditPage.clickDeleteInstructorLinkAndConfirm(1);
        courseEditPage.clickDeleteInstructorLinkAndConfirm(1);
        courseEditPage.clickDeleteInstructorLinkAndConfirm(1);
        courseEditPage.clickDeleteInstructorLinkAndConfirm(2);
        
        ______TS("test the only registered instructor with the privilege to modify instructors cannot be deleted");
        
        // Create an registered instructor with all privileges except modifying instructors
        InstructorPrivileges privilege =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        privilege.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, false);
        InstructorAttributes instructor =
                new InstructorAttributes("InsCrsEdit.reg", courseId, "Teammates Reg", "InsCrsEdit.reg@gmail.tmt",
                                         Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM,
                                         "Teammates Reg", privilege);
        BackDoor.createInstructor(instructor);
        
        // Create an unregistered instructor with co-owner privilege
        courseEditPage.addNewInstructor("Unreg Instructor", "InstructorCourseEditEmail@gmail.tmt");
        
        // Delete own instructor role
        courseEditPage.clickDeleteInstructorLinkAndConfirm(2);
        assertEquals(Const.StatusMessages.COURSE_INSTRUCTOR_DELETE_NOT_ALLOWED, courseEditPage.getStatus());
        
        // Delete other instructors
        courseEditPage.clickDeleteInstructorLinkAndConfirm(3);
        assertEquals(Const.StatusMessages.COURSE_INSTRUCTOR_DELETED, courseEditPage.getStatus());
        courseEditPage.clickDeleteInstructorLinkAndConfirm(1);
        assertEquals(Const.StatusMessages.COURSE_INSTRUCTOR_DELETED, courseEditPage.getStatus());
        
        ______TS("delete own instructor role and redirect to courses page");
        
        // Create another registered instructor with co-owner privilege
        BackDoor.createInstructor(testData.instructors.get("InsCrsEdit.coord"));
        courseEditPage = getCourseEditPage();
        
        // Delete own instructor role
        courseEditPage.clickDeleteInstructorLinkAndConfirm(2);

        InstructorCoursesPage coursesPage = courseEditPage.changePageType(InstructorCoursesPage.class);
        coursesPage.waitForAjaxLoadCoursesSuccess();
        coursesPage.verifyStatus(Const.StatusMessages.COURSE_INSTRUCTOR_DELETED + "\n"
                                 + Const.StatusMessages.COURSE_EMPTY);
        
        // Restore own instructor role to ensure remaining test cases work properly
        BackDoor.createInstructor(testData.instructors.get("InsCrsEdit.test"));
    }
    
    /**
     * Tests the UI of edit course.
     */
    private void testEditCourseAction() {
        courseEditPage = getCourseEditPage();
        
        ______TS("edit course valid name");
        
        assertFalse(courseEditPage.isCourseEditFormEnabled());
        courseEditPage.clickEditCourseLink();
        assertTrue(courseEditPage.isCourseEditFormEnabled());
        
        courseEditPage.clickSaveCourseButton();
        courseEditPage.changePageType(InstructorCourseEditPage.class);
        assertEquals(Const.StatusMessages.COURSE_EDITED, courseEditPage.getStatus());
        
        ______TS("edit course invalid name");
        assertFalse(courseEditPage.isCourseEditFormEnabled());
        courseEditPage.clickEditCourseLink();
        assertTrue(courseEditPage.isCourseEditFormEnabled());
        courseEditPage.editCourseName("");
        courseEditPage.clickSaveCourseButton();
        courseEditPage.changePageType(InstructorCourseEditPage.class);
        assertEquals(String.format(FieldValidator.COURSE_NAME_ERROR_MESSAGE, "", FieldValidator.REASON_EMPTY),
                     courseEditPage.getStatus());
    }
    
    private void testDeleteCourseAction() {
        // TODO: use navigateTo instead
        courseEditPage = getCourseEditPage();
        
        ______TS("delete course then cancel");
        
        courseEditPage.clickDeleteCourseLinkAndCancel();
        assertNotNull(BackDoor.getCourseAsJson(courseId));
        
        ______TS("delete course then proceed");

        InstructorCoursesPage coursePage = courseEditPage.clickDeleteCourseLinkAndConfirm();
        assertTrue(coursePage.getStatus().contains(String.format(Const.StatusMessages.COURSE_DELETED, courseId)));
    }
    
    private void testUnregisteredInstructorEmailNotEditable() {
        courseEditPage = getCourseEditPage();
        
        ______TS("make a new unregistered instructor and test that its email can't be edited");
        
        courseEditPage.addNewInstructor("Unreg Instructor", "InstructorCourseEditEmail@gmail.tmt");
        int unregInstrNum = 3;
        assertEquals("Unreg Instructor", courseEditPage.getNameField(unregInstrNum).getAttribute("value"));
        assertFalse(courseEditPage.getNameField(unregInstrNum).isEnabled());
        
        assertTrue(courseEditPage.clickEditInstructorLink(unregInstrNum));
        assertEquals("true", courseEditPage.getEmailField(unregInstrNum).getAttribute("readonly"));
        assertTrue(courseEditPage.getNameField(unregInstrNum).isEnabled());
    }
    
    private InstructorCourseEditPage getCourseEditPage() {
        AppUrl courseEditPageLink = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE)
                                    .withUserId(instructorId)
                                    .withCourseId(courseId);
        
        return loginAdminToPage(browser, courseEditPageLink, InstructorCourseEditPage.class);
    }

    @AfterClass
    public static void classTearDown() {
        BrowserPool.release(browser);
    }
    
}
