package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.util.Const;

public class InstructorCourseEditPage extends AppPage {

    public static final int INSTRUCTOR_TYPE_COOWNER = 0;
    public static final int INSTRUCTOR_TYPE_MANAGER = 1;
    public static final int INSTRUCTOR_TYPE_OBSERVER = 2;
    public static final int INSTRUCTOR_TYPE_TUTOR = 3;
    public static final int INSTRUCTOR_TYPE_CUSTOM = 4;

    public static final int COURSE_MODIFY_COURSE = 0;
    public static final int COURSE_MODIFY_INSTRUCTORS = 1;
    public static final int COURSE_MODIFY_SESSIONS = 2;
    public static final int COURSE_MODIFY_STUDENTS = 3;
    public static final int COURSE_VIEW_STUDENTS = 4;
    public static final int COURSE_GIVE_RESPONSES_IN_SESSION = 5;
    public static final int COURSE_VIEW_RESPONSES_IN_SESSION = 6;
    public static final int COURSE_MODIFY_RESPONSES_IN_SESSION = 7;

    public static final int SECTION_VIEW_STUDENTS = 0;
    public static final int SECTION_GIVE_RESPONSES_IN_SESSION = 1;
    public static final int SECTION_VIEW_RESPONSES_IN_SESSION = 2;
    public static final int SECTION_MODIFY_RESPONSES_IN_SESSION = 3;

    public static final int SESSION_GIVE_RESPONSES = 0;
    public static final int SESSION_VIEW_RESPONSES = 1;
    public static final int SESSION_MODIFY_RESPONSES = 2;

    @FindBy(id = "courseid")
    private WebElement courseIdTextBox;

    @FindBy(id = "coursename")
    private WebElement courseNameTextBox;

    @FindBy(id = "btnSaveCourse")
    private WebElement saveCourseButton;

    @FindBy(id = "courseEditLink")
    private WebElement editCourseLink;

    @FindBy(id = "courseDeleteLink")
    private WebElement deleteCourseLink;

    @FindBy(id = "btnShowNewInstructorForm")
    private WebElement showNewInstructorFormButton;

    @FindBy(id = "instructorname")
    private WebElement newInstructorNameTextBox;

    @FindBy(id = "instructoremail")
    private WebElement newInstructorEmailTextBox;

    @FindBy(xpath = "//form[@name='formAddInstructor']"
            + "//input[@name='instructordisplayname']")
    private WebElement newInstructorDisplayNameTextBox;

    @FindBy(id = "btnAddInstructor")
    private WebElement addInstructorButton;

    public InstructorCourseEditPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Edit Course Details</h1>");
    }

    public String getCourseId() {
        return courseIdTextBox.getAttribute("value");
    }

    public InstructorCourseEditPage verifyIsCorrectPage(String courseId) {
        assertEquals(courseId, getCourseId());
        return this;
    }

    public void addNewInstructor(String name, String email) {
        clickShowNewInstructorFormButton();

        fillNewInstructorName(name);
        fillNewInstructorEmail(email);

        click(addInstructorButton);
        waitForPageToLoad();
    }

    /**
     * Clicks edit button, changes and saves instructor details for given instructor index
     * Instructor email will not be edited when editing a yet-to-join instructor.
     * Instructor display name will not be edited if instructor is not displayed to other students.
     */
    public void editInstructor(int instrNum, String name, String email,
            Boolean isDisplayedToStudents, String displayName, String role) {
        clickEditInstructorLink(instrNum);

        editInstructorName(instrNum, name);
        if (getEmailField(instrNum).getAttribute("readonly") == null) {
            editInstructorEmail(instrNum, email);
        }
        editInstructorDisplayedToStudents(instrNum, isDisplayedToStudents);
        if (isDisplayedToStudents) {
            editInstructorDisplayName(instrNum, displayName);
        }
        selectRoleForInstructor(instrNum, role);

        saveEditInstructor(instrNum);
        waitForPageToLoad();
    }

    public void clickSaveInstructorButton(int instrNum) {
        WebElement button = browser.driver.findElement(By.id("btnSaveInstructor" + instrNum));
        click(button);
        waitForPageToLoad();
    }

    public String editInstructorName(int instrNum, String value) {
        WebElement editPanelNameTextBox = getNameField(instrNum);
        fillTextBox(editPanelNameTextBox, value);
        return getTextBoxValue(editPanelNameTextBox);
    }

    public String editInstructorEmail(int instrNum, String value) {
        WebElement editPanelEmailTextBox = getEmailField(instrNum);
        fillTextBox(editPanelEmailTextBox, value);
        return getTextBoxValue(editPanelEmailTextBox);
    }

    public boolean editInstructorDisplayedToStudents(int instrNum, Boolean isDisplayedToStudents) {
        WebElement editPanelDisplayedToStudentsCheckbox = getDisplayedToStudentCheckBox(instrNum);
        if (isDisplayedToStudents) {
            markCheckBoxAsChecked(editPanelDisplayedToStudentsCheckbox);
        } else {
            markCheckBoxAsUnchecked(editPanelDisplayedToStudentsCheckbox);
        }
        return editPanelDisplayedToStudentsCheckbox.isSelected();
    }

    public String editInstructorDisplayName(int instrNum, String value) {
        WebElement editPanelDisplayNameTextBox = getDisplayNameField(instrNum);
        fillTextBox(editPanelDisplayNameTextBox, value);
        return getTextBoxValue(editPanelDisplayNameTextBox);
    }

    public String fillNewInstructorName(String value) {
        fillTextBox(newInstructorNameTextBox, value);
        return getTextBoxValue(newInstructorNameTextBox);
    }

    public String getNewInstructorName() {
        return getTextBoxValue(newInstructorNameTextBox);
    }

    public String fillNewInstructorEmail(String value) {
        fillTextBox(newInstructorEmailTextBox, value);
        return getTextBoxValue(newInstructorEmailTextBox);
    }

    public String getNewInstructorEmail() {
        return getTextBoxValue(newInstructorEmailTextBox);
    }

    public String fillNewInstructorDisplayName(String value) {
        fillTextBox(newInstructorDisplayNameTextBox, value);
        return getTextBoxValue(newInstructorDisplayNameTextBox);
    }

    public void clickEditInstructorLink(int instrNum) {
        click(getEditInstructorLink(instrNum));
        WebElement saveButton = getSaveInstructorButton(instrNum);
        waitForElementVisibility(saveButton);
    }

    public void clickEditInstructorLinkUnsuccessfully(int instrNum) {
        click(getEditInstructorLink(instrNum));
    }

    public boolean isInstructorEditable(int instrNum) {
        WebElement editInstructorNameTextBox = getNameField(instrNum);
        WebElement editInstructorEmailTextBox = getEmailField(instrNum);

        return editInstructorNameTextBox.isEnabled() && editInstructorEmailTextBox.isEnabled();
    }

    public void clickCancelEditInstructorLink(int instrNum) {
        click(getCancelEditInstructorLink(instrNum));
    }

    public void clickCancelAddInstructorLink() {
        click(getCancelAddInstructorLink());
    }

    public void verifyInstructorEditFormDisabled(int instrNum) {
        waitForElementToDisappear(By.id("btnSaveInstructor" + instrNum));

        WebElement editInstructorNameTextBox = getNameField(instrNum);
        WebElement editInstructorEmailTextBox = getEmailField(instrNum);

        boolean isNotEditable = !editInstructorNameTextBox.isEnabled()
                                && !editInstructorEmailTextBox.isEnabled();

        assertTrue(isNotEditable);
    }

    public boolean verifyAddInstructorFormDisplayed() {
        WebElement newInstructorForm = browser.driver.findElement(By.id("panelAddInstructor"));
        return newInstructorForm.isDisplayed();
    }

    public boolean verifyAddInstructorFormDefaultValues(int newInstructorIndex) {
        String checkbox = browser.driver.findElement(By.name(
                Const.ParamsNames.INSTRUCTOR_IS_DISPLAYED_TO_STUDENT)).getAttribute("value");
        String instructorName = browser.driver.findElement(By.name(
                Const.ParamsNames.INSTRUCTOR_DISPLAY_NAME)).getAttribute("value");
        String instructorRole = browser.driver.findElement(By.id(
                Const.ParamsNames.INSTRUCTOR_ROLE_NAME + "forinstructor"
                        + newInstructorIndex)).getAttribute("value");

        return "true".equals(checkbox) && "Instructor".equals(instructorName)
                && instructorRole.equals(Const.InstructorPermissionRoleNames
                .INSTRUCTOR_PERMISSION_ROLE_COOWNER); // default values taken from courseEditAddInstructorPanel.tag
    }

    /**
     * Verifies that the instructor details fields for the given {@code instrNum} contain the updated values.
     * If {@code newIsDisplayedToStudents} is true, the display name field is checked against {@code newDisplayName}.
     * Otherwise, it is verified that the display name field's placeholder is shown as expected.
     */
    public void verifyInstructorDetails(int instrNum, String newName, String newEmail,
            boolean newIsDisplayedToStudents, String newDisplayName, String newRole) {
        assertEquals(newName, getInstructorName(instrNum));
        assertEquals(newEmail, getInstructorEmail(instrNum));
        assertEquals(newIsDisplayedToStudents, getInstructorDisplayedToStudents(instrNum));
        if (newIsDisplayedToStudents) {
            assertEquals(newDisplayName, getInstructorDisplayName(instrNum));
        } else {
            assertEquals("(This instructor will NOT be displayed to students)",
                    getDisplayNameField(instrNum).getAttribute("placeholder"));
        }
        assertEquals(newRole, getInstructorAccessLevel(instrNum));
    }

    public void saveEditInstructor(int instrNum) {
        click(getSaveInstructorButton(instrNum));
    }

    public void clickDisplayedToStudentCheckBox(int instrNum) {
        click(getDisplayedToStudentCheckBox(instrNum));
    }

    public void selectRoleForInstructor(int instrNum, String role) {
        WebElement roleRadioButton = browser.driver.findElement(By.cssSelector(
                "input[id='instructorroleforinstructor" + instrNum + "'][value='" + role + "']"));
        click(roleRadioButton);
    }

    public void selectRoleForNewInstructor(int newInstructorIndex, String role) {
        WebElement roleRadioButton = browser.driver.findElement(By.cssSelector(
                "input[id='instructorroleforinstructor" + newInstructorIndex + "'][value='" + role + "']"));
        click(roleRadioButton);
    }

    public boolean isRoleSelectedForInstructor(int instrNum, String role) {
        return browser.driver.findElement(By.cssSelector(
                "input[id='instructorroleforinstructor" + instrNum + "'][value='" + role + "']")).isSelected();
    }

    public void clickViewDetailsLinkForInstructor(int instrNum, int viewLinkNum) {
        /*
         *  There are groups of 3 elements:
         *  <input>: radio button
         *  <a>: the details link
         *  <br>: break line
         *  Therefore the formula for the position of the details link of the group i-th (count from 0) is i * 3 + 2
         */
        int cssLinkNum = viewLinkNum * 3 + 2;
        WebElement viewLink =
                browser.driver.findElement(
                        By.cssSelector("#accessControlEditDivForInstr" + instrNum
                                       + " > div.form-group > div.col-sm-9 > a:nth-child(" + cssLinkNum + ")"));

        click(viewLink);
        waitForPageToLoad();
    }

    public void closeModal() {
        WebElement closeButton = browser.driver.findElement(By.className("close"));
        waitForElementToBeClickable(closeButton);
        click(closeButton);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isPrivilegeCheckboxInModalChecked(String privilege) {
        By selector = By.cssSelector("#tunePermissionsDivForInstructorAll input[type='checkbox'][name='"
                                     + privilege + "']");
        WebElement checkbox = browser.driver.findElement(selector);
        return checkbox.isSelected();
    }

    public boolean isPrivilegeCheckboxInPermissionDivChecked(int instructorIndex, String privilege) {
        By selector = By.cssSelector("#tunePermissionsDivForInstructor" + instructorIndex
                                     + " input[type='checkbox'][name='" + privilege + "']");
        WebElement checkbox = browser.driver.findElement(selector);
        return checkbox.isSelected();
    }

    public void clickCourseLevelPrivilegesLink(int instrNum, int linkNum) {
        WebElement coursePanel = getCourseLevelPanel(instrNum);
        click(coursePanel.findElements(By.cssSelector("input[type='checkbox']")).get(linkNum));
    }

    public void clickAddSectionLevelPrivilegesLink(int instrNum) {
        click(getAddSectionLevelPrivilegesLink(instrNum));
    }

    public void clickSectionSelectionCheckBox(int instrNum, int sectionLevelIndex, int sectionNum) {
        click(getSectionSelectionCheckBox(instrNum, sectionLevelIndex, sectionNum));
    }

    public void clickSectionLevelPrivilegeLink(int instrNum, int sectionLevelIndex, int linkNum) {
        click(getSectionLevelPanelCheckBox(instrNum, sectionLevelIndex, linkNum));
    }

    public boolean isSectionLevelPrivilegeLinkClicked(int instrNum, int sectionLevelIndex, int linkNum) {
        return getSectionLevelPanelCheckBox(instrNum, sectionLevelIndex, linkNum).isSelected();
    }

    public void clickSessionLevelPrivilegeLink(int instrNum, int sectionLevelIndex,
                                               int sessionIndex, int linkNum) {
        click(getSessionLevelTableCheckbox(instrNum, sectionLevelIndex, sessionIndex, linkNum));
    }

    public boolean isSessionLevelPrivilegeLinkClicked(int instrNum, int sectionLevelIndex,
                                                      int sessionIndex, int linkNum) {
        return getSessionLevelTableCheckbox(instrNum, sectionLevelIndex, sessionIndex, linkNum).isSelected();
    }

    public void clickSessionLevelInSectionLevel(int instrNum, int sectionLevelIndex) {
        String linkId = "toggleSessionLevelInSection" + sectionLevelIndex + "ForInstructor" + instrNum;
        click(browser.driver.findElement(By.id(linkId)));
    }

    public void clickHideSectionLevelPrivileges(int instrNum, int sectionLevelIndex) {
        click(getSectionLevelPanel(instrNum, sectionLevelIndex).findElement(By.cssSelector(".glyphicon-trash")));
    }

    public boolean isTuneSessionPermissionsDivVisible(int instrNum, int sectionLevelIndex) {
        String sessionPermissionsDivId = "tuneSessionPermissionsDiv" + sectionLevelIndex
                                         + "ForInstructor" + instrNum;
        return isElementVisible(By.id(sessionPermissionsDivId));
    }

    public boolean isTunePermissionsDivVisible(int instrNum) {
        String sessionPermissionsDivId = "tunePermissionsDivForInstructor" + instrNum;
        return isElementVisible(By.id(sessionPermissionsDivId));
    }

    public boolean clickShowNewInstructorFormButton() {
        click(showNewInstructorFormButton);

        return newInstructorNameTextBox.isEnabled()
                && newInstructorEmailTextBox.isEnabled()
                && addInstructorButton.isDisplayed();
    }

    public boolean isInstructorListSortedByName() {
        boolean isSorted = true;
        List<String> instructorNames = new ArrayList<String>();
        List<WebElement> elements = browser.driver.findElements(By.xpath("//*[starts-with(@id, 'instructorname')]"));
        for (int i = 1; i < elements.size(); i++) {
            instructorNames.add(browser.driver.findElement(By.id("instructorname" + i)).getAttribute("value"));
        }
        for (int i = 1; i < instructorNames.size(); i++) {
            if (instructorNames.get(i - 1).compareTo(instructorNames.get(i)) > 0) {
                isSorted = false;
            }
        }
        return isSorted;
    }

    public boolean clickOnNewInstructorAccessLevelViewDetails(String role) {
        WebElement instructorForm = browser.driver.findElement(By.id("formAddInstructor"));

        WebElement viewDetailsLink = instructorForm.findElement(By.xpath(
                                            "//a[contains(@class, 'view-role-details')][@data-role='" + role + "']"));
        click(viewDetailsLink);

        WebElement viewDetailsModal = browser.driver.findElement(By.cssSelector(
                                            "div#tunePermissionsDivForInstructorAll"));
        waitForElementVisibility(viewDetailsModal);

        if ("display: block;".equals(viewDetailsModal.getAttribute("style"))) {
            closeModal();
            return true;
        }
        return false;
    }

    public void clickAddInstructorButton() {
        click(addInstructorButton);
        waitForPageToLoad();
    }

    public void clickInviteInstructorLink(int instrNum) {
        click(getInviteInstructorLink(instrNum));
        waitForConfirmationModalAndClickOk();
        waitForPageToLoad();
    }

    /**
     * Clicks the button to edit the course.
     */
    public void clickEditCourseLink() {
        click(editCourseLink);
        waitForElementVisibility(saveCourseButton);
    }

    /**
     * Clicks the save changes button to save the changes made to the course.
     */
    public void clickSaveCourseButton() {
        click(saveCourseButton);
        waitForPageToLoad();
    }

    public void editCourseName(String value) {
        fillTextBox(courseNameTextBox, value);
    }

    public InstructorCoursesPage clickDeleteCourseLink() {
        click(deleteCourseLink);
        waitForPageToLoad();
        return changePageType(InstructorCoursesPage.class);
    }

    /**
     * Click the delete instructor button at position {@code instrNum} and click "Yes" in the follow up dialog.
     * @param instrNum is the position of the instructor (e.g. 1, 2, 3, ...)
     */
    public void clickDeleteInstructorLinkAndConfirm(int instrNum) {
        WebElement deleteInstructorLink = getDeleteInstructorLink(instrNum);
        clickAndConfirm(deleteInstructorLink);
        waitForPageToLoad();
    }

    /**
     * Click the delete instructor button at position {@code instrNum} and click "No" in the follow up dialog.
     * @param instrNum is the position of the instructor (e.g. 1, 2, 3, ...)
     */
    public void clickDeleteInstructorLinkAndCancel(int instrNum) {
        WebElement deleteInstructorLink = getDeleteInstructorLink(instrNum);
        clickAndCancel(deleteInstructorLink);
    }

    /**
     * Checks if the course edit form is enabled.
     * @return true if the course edit form is enabled
     */
    public boolean isCourseEditFormEnabled() {
        return !courseIdTextBox.isEnabled() && courseNameTextBox.isEnabled() && saveCourseButton.isDisplayed();
    }

    public void changeCourseIdInForm(int instrNum, String newCourseId) {
        String selector = "$('#edit-" + instrNum + " input[name=\"" + Const.ParamsNames.COURSE_ID + "\"]')";
        String action = ".val('" + newCourseId + "')";
        executeScript(selector + action);
    }

    // methods that return WebElements of the page go here

    public WebElement getEditInstructorLink(int instrNum) {
        return browser.driver.findElement(By.id("instrEditLink" + instrNum));
    }

    public WebElement getCancelEditInstructorLink(int instrNum) {
        return browser.driver.findElement(By.id("instrCancelLink" + instrNum));
    }

    public WebElement getCancelAddInstructorLink() {
        return browser.driver.findElement(By.id("cancelAddInstructorLink"));
    }

    private WebElement getInviteInstructorLink(int instrNum) {
        return browser.driver.findElement(By.id("instrRemindLink" + instrNum));
    }

    public WebElement getDeleteInstructorLink(int instrNum) {
        return browser.driver.findElement(By.id("instrDeleteLink" + instrNum));
    }

    public WebElement getSaveInstructorButton(int instrNum) {
        return browser.driver.findElement(By.id("btnSaveInstructor" + instrNum));
    }

    public WebElement getNameField(int instrNum) {
        return browser.driver.findElement(By.id("instructorname" + instrNum));
    }

    public WebElement getEmailField(int instrNum) {
        return browser.driver.findElement(By.id("instructoremail" + instrNum));
    }

    public WebElement getDisplayedToStudentCheckBox(int instrNum) {
        return browser.driver.findElement(By.cssSelector("#instructorTable" + instrNum + " input[name='"
                                                         + Const.ParamsNames.INSTRUCTOR_IS_DISPLAYED_TO_STUDENT
                                                         + "']"));
    }

    public WebElement getDisplayNameField(int instrNum) {
        String displayNameFieldSelector = "#instructorTable" + instrNum + " input[name='"
                + Const.ParamsNames.INSTRUCTOR_DISPLAY_NAME
                + "']";
        return browser.driver.findElement(By.cssSelector(displayNameFieldSelector));
    }

    public String getInstructorName(int instrNum) {
        return browser.driver.findElement(By.id("instructorname" + instrNum)).getAttribute("value");
    }

    public String getInstructorEmail(int instrNum) {
        return browser.driver.findElement(By.id("instructoremail" + instrNum)).getAttribute("value");
    }

    public boolean getInstructorDisplayedToStudents(int instrNum) {
        String isDisplayedToStudentsCheckboxSelector = "#instructorTable" + instrNum + " input[name='"
                + Const.ParamsNames.INSTRUCTOR_IS_DISPLAYED_TO_STUDENT
                + "']";
        return browser.driver.findElement(By.cssSelector(isDisplayedToStudentsCheckboxSelector)).isSelected();
    }

    public String getInstructorDisplayName(int instrNum) {
        String displayNameFieldSelector = "#instructorTable" + instrNum + " input[name='"
                + Const.ParamsNames.INSTRUCTOR_DISPLAY_NAME
                + "']";
        return browser.driver.findElement(By.cssSelector(displayNameFieldSelector)).getAttribute("value");
    }

    public String getInstructorAccessLevel(int instrNum) {
        return browser.driver.findElement(By.cssSelector("#accessControlInfoForInstr" + instrNum + " span")).getText();
    }

    public WebElement getCourseLevelPanel(int instrNum) {
        String permissionDivId = "tunePermissionsDivForInstructor" + instrNum;
        return browser.driver.findElement(By.id(permissionDivId))
                             .findElement(By.cssSelector("div.form-group>div>div.panel"));
    }

    public WebElement getAddSectionLevelPrivilegesLink(int instrNum) {
        return browser.driver.findElement(By.id("addSectionLevelForInstructor" + instrNum));
    }

    public WebElement getSectionLevelPanel(int instrNum, int sectionLevelIndex) {
        String permissionDivId = "tuneSectionPermissionsDiv" + sectionLevelIndex + "ForInstructor" + instrNum;
        return browser.driver.findElement(By.id(permissionDivId));
    }

    private WebElement getSectionLevelPanelBody(int instrNum, int sectionLevelIndex) {
        WebElement sectionPanel = getSectionLevelPanel(instrNum, sectionLevelIndex);
        return sectionPanel.findElement(By.cssSelector("div[class='panel-body']"));
    }

    private WebElement getSectionSelectionCheckBox(int instrNum, int sectionLevelIndex, int sectionNum) {
        WebElement sectionPanel = getSectionLevelPanel(instrNum, sectionLevelIndex);
        String cssSelector = "input[name='" + Const.ParamsNames.INSTRUCTOR_SECTION_GROUP
                             + sectionLevelIndex + Const.ParamsNames.INSTRUCTOR_SECTION + sectionNum + "']";
        return sectionPanel.findElement(By.cssSelector(cssSelector));
    }

    private WebElement getSectionLevelPanelCheckBox(int instrNum, int sectionLevelIndex, int checkBoxIndex) {
        WebElement sectionPanelBody = getSectionLevelPanelBody(instrNum, sectionLevelIndex);
        return sectionPanelBody.findElements(By.cssSelector("input[type='checkbox']")).get(checkBoxIndex);
    }

    private WebElement getSessionLevelTableBody(int instrNum, int sectionLevelIndex) {
        WebElement sectionPanelBody = getSectionLevelPanelBody(instrNum, sectionLevelIndex);
        return sectionPanelBody.findElement(By.cssSelector("table"));
    }

    private WebElement getSessionLevelTableRow(int instrNum, int sectionLevelIndex, int sessionIndex) {
        WebElement sessionLevelTableBody = getSessionLevelTableBody(instrNum, sectionLevelIndex);
        return sessionLevelTableBody.findElements(By.cssSelector("tbody tr")).get(sessionIndex);
    }

    private WebElement getSessionLevelTableCheckbox(int instrNum, int sectionLevelIndex,
                                                 int sessionIndex, int checkBoxIndex) {
        WebElement sessionLevelTableRow = getSessionLevelTableRow(instrNum, sectionLevelIndex, sessionIndex);
        return sessionLevelTableRow.findElements(By.cssSelector("input[type='checkbox']")).get(checkBoxIndex);
    }

}
