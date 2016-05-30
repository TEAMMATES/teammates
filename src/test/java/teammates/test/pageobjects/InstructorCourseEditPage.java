package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertEquals;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.util.Const;

public class InstructorCourseEditPage extends AppPage {
    
    @FindBy(id = Const.ParamsNames.COURSE_ID)
    private WebElement courseIdTextBox;
    
    @FindBy(id = Const.ParamsNames.COURSE_NAME)
    private WebElement courseNameTextBox;
    
    @FindBy(id = "btnSaveCourse")
    private WebElement saveCourseButton;
    
    @FindBy(id = "courseEditLink")
    private WebElement editCourseLink;
    
    @FindBy(id = "courseDeleteLink")
    private WebElement deleteCourseLink;
    
    @FindBy(id = "instrEditLink1")
    private WebElement editInstructorLink;
    
    @FindBy(id = "instrRemindLink4")
    private WebElement inviteInstructorLink;
    
    @FindBy(id = Const.ParamsNames.INSTRUCTOR_NAME + "1")
    private WebElement editInstructorNameTextBox;
    
    @FindBy(id = Const.ParamsNames.INSTRUCTOR_EMAIL + "1")
    private WebElement editInstructorEmailTextBox;
    
    @FindBy(id = "btnSaveInstructor1")
    private WebElement saveInstructorButton;
    
    @FindBy(id = "btnShowNewInstructorForm")
    private WebElement showNewInstructorFormButton;
    
    @FindBy(id = Const.ParamsNames.INSTRUCTOR_NAME)
    private WebElement instructorNameTextBox;
    
    @FindBy(id = Const.ParamsNames.INSTRUCTOR_EMAIL)
    private WebElement instructorEmailTextBox;
    
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
        return browser.driver.findElement(By.id(Const.ParamsNames.COURSE_ID)).getAttribute("value");
    }

    public InstructorCourseEditPage verifyIsCorrectPage(String courseId) {
        assertEquals(courseId, this.getCourseId());
        return this;
    }
    
    public void addNewInstructor(String name, String email) {
        clickShowNewInstructorFormButton();
        
        fillInstructorName(name);
        fillInstructorEmail(email);
        
        addInstructorButton.click();
        waitForPageToLoad();
    }

    public void editInstructor(String id, String name, String email) {
        clickEditInstructorLink(1);
        
        editInstructorName(name);
        editInstructorEmail(email);
        selectRoleForInstructor(1, Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        
        saveInstructorButton.click();
        waitForPageToLoad();
    }
    
    public void clickSaveInstructorButton(int instrNum) {
        WebElement button = browser.driver.findElement(By.id("btnSaveInstructor" + instrNum));
        button.click();
        waitForPageToLoad();
    }
    
    public String editInstructorName(String value) {
        fillTextBox(editInstructorNameTextBox, value);
        return getTextBoxValue(editInstructorNameTextBox);
    }
    
    public String editInstructorEmail(String value) {
        fillTextBox(editInstructorEmailTextBox, value);
        return getTextBoxValue(editInstructorEmailTextBox);
    }
    
    public String fillInstructorName(String value) {
        fillTextBox(instructorNameTextBox, value);
        return getTextBoxValue(instructorNameTextBox);
    }
    
    public String fillInstructorEmail(String value) {
        fillTextBox(instructorEmailTextBox, value);
        return getTextBoxValue(instructorEmailTextBox);
    }
    
    public boolean clickEditInstructorLink(int instrNum) {
        boolean isEditable;
        if (instrNum == 1) {
            editInstructorLink.click();
            waitForElementVisibility(saveInstructorButton);
            isEditable = editInstructorNameTextBox.isEnabled()
                        && editInstructorEmailTextBox.isEnabled()
                        && saveInstructorButton.isDisplayed();
        } else {
            String instructorNum = String.valueOf(instrNum);
            WebElement editLink = browser.driver.findElement(By.id("instrEditLink" + instructorNum));
            editLink.click();
            
            WebElement saveButton = browser.driver.findElement(By.id("btnSaveInstructor" + instructorNum));
            waitForElementVisibility(saveButton);
            
            WebElement editInstructorNameTextBox = browser.driver.findElement(By.id(Const.ParamsNames.INSTRUCTOR_NAME
                                                                                    + instructorNum));
            WebElement editInstructorEmailTextBox = browser.driver.findElement(By.id(Const.ParamsNames.INSTRUCTOR_EMAIL
                                                                                     + instructorNum));
            
            isEditable = editInstructorNameTextBox.isEnabled()
                        && editInstructorEmailTextBox.isEnabled()
                        && saveButton.isDisplayed();
        }
        
        return isEditable;
    }
    
    public WebElement displayedToStudentCheckBox(int instrNum) {
        return browser.driver.findElement(By.cssSelector("#instructorTable" + instrNum + " input[name='"
                                                         + Const.ParamsNames.INSTRUCTOR_IS_DISPLAYED_TO_STUDENT
                                                         + "']"));
    }
    
    public void clickDisplayedToStudentCheckBox(int instrNum) {
        this.displayedToStudentCheckBox(instrNum).click();
    }
    
    public void selectRoleForInstructor(int instrNum, String role) {
        WebElement roleRadioButton = browser.driver.findElement(By.cssSelector(
                "input[id='instructorroleforinstructor" + instrNum + "'][value='" + role + "']"));
        roleRadioButton.click();
    }
    
    public void clickViewDetailsLinkForInstructor(int instrNum, int viewLinkNum) {
        /*
         *  There are groups of 3 elements:
         *  <input>: radio button
         *  <a>: the details link
         *  <br>: break line
         *  Therefore the formula for the position of the details link of the group i-th (count from 1) is i * 3 - 1
         */
        int cssLinkNum = viewLinkNum * 3 - 1;
        WebElement viewLink =
                browser.driver.findElement(
                        By.cssSelector("#accessControlEditDivForInstr" + instrNum
                                       + " > div.form-group > div.col-sm-9 > a:nth-child(" + cssLinkNum + ")"));
        
        viewLink.click();
        waitForPageToLoad();
    }
    
    public void closeModal() {
        WebElement closeButton = browser.driver.findElement(By.className("close"));
        waitForElementToBeClickable(closeButton);
        closeButton.click();
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
    
    public WebElement courseLevelPanel(int instrNum) {
        String permissionDivId = "tunePermissionsDivForInstructor" + instrNum;
        return browser.driver.findElement(By.id(permissionDivId)).findElement(By.cssSelector("div.form-group>div>div.panel"));
    }
    
    public void clickCourseLevelPrivilegesLink(int instrNum, int linkNum) {
        WebElement coursePanel = this.courseLevelPanel(instrNum);
        coursePanel.findElements(By.cssSelector("input[type='checkbox']")).get(linkNum - 1).click();
    }
    
    public WebElement addSectionLevelPrivilegesLink(int instrNum) {
        String idStr = "addSectionLevelForInstructor" + instrNum;
        
        return browser.driver.findElement(By.id(idStr));
    }
    
    public void clickAddSectionLevelPrivilegesLink(int instrNum) {
        this.addSectionLevelPrivilegesLink(instrNum).click();
    }
    
    public WebElement sectionLevelPanel(int instrNum, int sectionLevelNum) {
        String permissionDivId = "tuneSectionPermissionsDiv" + sectionLevelNum + "ForInstructor" + instrNum;
        return browser.driver.findElement(By.id(permissionDivId));
    }
    
    private WebElement sectionCheckBoxInSectionLevel(int instrNum, int sectionLevelNum, int sectionNum) {
        WebElement sectionPanel = this.sectionLevelPanel(instrNum, sectionLevelNum);
        String cssSelector = "input[name='" + Const.ParamsNames.INSTRUCTOR_SECTION_GROUP
                             + sectionLevelNum + Const.ParamsNames.INSTRUCTOR_SECTION + sectionNum + "']";
        return sectionPanel.findElement(By.cssSelector(cssSelector));
    }
    
    public void clickSectionCheckBoxInSectionLevel(int instrNum, int sectionLevelNum, int sectionNum) {
        this.sectionCheckBoxInSectionLevel(instrNum, sectionLevelNum - 1, sectionNum - 1).click();
    }
    
    private WebElement sectionLevelPanelCheckBox(int instrNum, int sectionLevelNum, String checkBoxName) {
        WebElement sectionPanel = this.sectionLevelPanel(instrNum, sectionLevelNum);
        String cssSelector = "input[type='checkbox'][name='" + checkBoxName
                             + Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + sectionLevelNum + "']";
        return sectionPanel.findElement(By.cssSelector(cssSelector));
    }
    
    public void clickViewStudentCheckBoxInSectionLevel(int instrNum, int sectionLevelNum) {
        this.sectionLevelPanelCheckBox(instrNum, sectionLevelNum,
                                       Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS).click();
    }
    
    public void clickViewOthersCommentsCheckBoxInSectionLevel(int instrNum, int sectionLevelNum) {
        this.sectionLevelPanelCheckBox(instrNum, sectionLevelNum,
                                       Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS).click();
    }
    
    public void clickViewSessionResultsCheckBoxInSectionLevel(int instrNum, int sectionLevelNum) {
        this.sectionLevelPanelCheckBox(instrNum, sectionLevelNum,
                                       Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS).click();
    }
    
    public void clickModifySessionResultCheckBoxInSectionLevel(int instrNum, int sectionLevelNum) {
        this.sectionLevelPanelCheckBox(instrNum, sectionLevelNum,
                                       Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS).click();
    }
    
    public void clickSessionLevelInSectionLevel(int instrNum, int sectionLevelNum) {
        String linkId = "toggleSessionLevelInSection" + sectionLevelNum + "ForInstructor" + instrNum;
        browser.driver.findElement(By.id(linkId)).click();
    }
    
    public boolean isTuneSessionPermissionsDivVisible(int instrNum, int sectionLevelNum) {
        By sessionPermissionsDiv = By.id("tuneSessionPermissionsDiv" + sectionLevelNum + "ForInstructor" + instrNum);
        return isElementVisible(sessionPermissionsDiv);
    }
    
    public boolean clickShowNewInstructorFormButton() {
        showNewInstructorFormButton.click();
        
        boolean isFormShownCorrectly = instructorNameTextBox.isEnabled()
                && instructorEmailTextBox.isEnabled()
                && addInstructorButton.isDisplayed();

        return isFormShownCorrectly;
    }
    
    public boolean clickOnAccessLevelViewDetails(String role) {
        WebElement viewDetailsLink = browser.driver.findElement(By.cssSelector(
                                            "a[onclick=\"showInstructorRoleModal('" + role + "')\"]"));
        viewDetailsLink.click();
        
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
        addInstructorButton.click();
        waitForPageToLoad();
    }

    public void clickInviteInstructorLink() {
        inviteInstructorLink.click();
        waitForPageToLoad();
    }
    
    /**
     * Clicks the button to edit the course.
     */
    public void clickEditCourseLink() {
        editCourseLink.click();
        waitForElementVisibility(saveCourseButton);
    }
    
    /**
     * Clicks the save changes button to save the changes made to the course.
     */
    public void clickSaveCourseButton() {
        saveCourseButton.click();
        waitForPageToLoad();
    }
    
    public void editCourseName(String value) {
        fillTextBox(courseNameTextBox, value);
    }
    
    public InstructorCoursesPage clickDeleteCourseLinkAndConfirm() {
        clickAndConfirm(deleteCourseLink);
        waitForPageToLoad();
        return changePageType(InstructorCoursesPage.class);
    }

    public void clickDeleteCourseLinkAndCancel() {
        clickAndCancel(deleteCourseLink);
    }
    
    /**
     * Click the delete instructor button at position {@code instrNum} and click "Yes" in the follow up dialog
     * @param instrNum is the position of the instructor (e.g. 1, 2, 3, ...)
     */
    public void clickDeleteInstructorLinkAndConfirm(int instrNum) {
        WebElement deleteInstructorLink = browser.driver.findElement(By.id("instrDeleteLink" + instrNum));
        clickAndConfirm(deleteInstructorLink);
        waitForPageToLoad();
    }
    
    /**
     * Click the delete instructor button at position {@code instrNum} and click "No" in the follow up dialog
     * @param instrNum is the position of the instructor (e.g. 1, 2, 3, ...)
     */
    public void clickDeleteInstructorLinkAndCancel(int instrNum) {
        WebElement deleteInstructorLink = browser.driver.findElement(By.id("instrDeleteLink" + instrNum));
        clickAndCancel(deleteInstructorLink);
    }

    public WebElement getNameField(int instrNum) {
        return browser.driver.findElement(By.id(Const.ParamsNames.INSTRUCTOR_NAME + instrNum));
    }
    
    public WebElement getEmailField(int instrNum) {
        return browser.driver.findElement(By.id(Const.ParamsNames.INSTRUCTOR_EMAIL + instrNum));
    }

    public boolean isCustomCheckboxChecked(String privilege, int instrNum) {
        By selector = By.cssSelector("#tunePermissionsDivForInstructor" + instrNum + " input[type='checkbox'][name='"
                                     + privilege + "']");
        WebElement checkbox = browser.driver.findElement(selector);
        return checkbox.isSelected();
    }
    
    /**
     * Checks if the course edit form is enabled.
     * @return true if the course edit form is enabled
     */
    public boolean isCourseEditFormEnabled() {
        return !courseIdTextBox.isEnabled() && courseNameTextBox.isEnabled() && saveCourseButton.isDisplayed();
    }
    
    public void changeCourseIdInForm(int instrNum, String newCourseId) {
        String selector = "$('#edit-" + instrNum + "').find('[name=\"" + Const.ParamsNames.COURSE_ID + "\"]')";
        String action = ".val('" + newCourseId + "')";
        ((JavascriptExecutor) browser.driver).executeScript(selector + action);
    }
    
    public WebElement getFirstEditInstructorLink() {
        return editInstructorLink;
    }

}
