package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertEquals;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.util.ThreadHelper;


public class InstructorCourseEditPage extends AppPage {
    
    @FindBy(id = "courseid")
    private WebElement courseIdTextBox;
    
    @FindBy(id = "courseName")
    private WebElement courseNameTextBox;
    
    @FindBy(id = "courseDeleteLink")
    private WebElement deleteCourseLink;
    
    @FindBy(id = "instrEditLink1")
    private WebElement editInstructorLink;
    
    @FindBy(id = "instrDeleteLink1")
    private WebElement deleteInstructorLink;
    
    @FindBy(id = "instrRemindLink3")
    private WebElement inviteInstructorLink;

    @FindBy(id = "instructorid1")
    private WebElement editInstructorIdTextBox;
    
    @FindBy(id = "instructorname1")
    private WebElement editInstructorNameTextBox;
    
    @FindBy(id = "instructoremail1")
    private WebElement editInstructorEmailTextBox;
    
    @FindBy(id = "btnSaveInstructor1")
    private WebElement saveInstructorButton;
    
    @FindBy(id = "btnShowNewInstructorForm")
    private WebElement showNewInstructorFormButton;
    
    @FindBy(id = "instructorname")
    private WebElement instructorNameTextBox;
    
    @FindBy(id = "instructoremail")
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
        return browser.driver.findElement(By.id("courseid")).getAttribute("value");
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
        clickEditInstructorLink();
        
        editInstructorName(name);
        editInstructorEmail(email);
        selectRoleForInstructor(1, "Co-owner");
        
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
    
    public boolean clickEditInstructorLink() {
        editInstructorLink.click();
        waitForElementVisible(saveInstructorButton);
        boolean isEditable = editInstructorNameTextBox.isEnabled()
                            && editInstructorEmailTextBox.isEnabled()
                            && saveInstructorButton.isDisplayed();
        
        return isEditable;
    }
    
    public WebElement displayedToStudentCheckBox(int instrNum) {
        return browser.driver.findElement(By.cssSelector("#instructorTable" + instrNum + " > div:nth-child(4) > label:nth-child(1) > input:nth-child(1)"));
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
        // there is one link before view details link group
        int cssLinkNum = viewLinkNum + 1;
        WebElement viewLink = browser.driver.findElement(By.cssSelector("#accessControlEditDivForInstr" + instrNum +
                " > div:nth-child(1) > div.col-sm-9 > a:nth-child(" + cssLinkNum + ")"));
        
        viewLink.click();
        browser.selenium.waitForPageToLoad("500");
    }
    
    public void closeModal() {
        WebElement closeButton = browser.driver.findElement(By.className("close"));
        
        closeButton.click();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
    }
    
    public WebElement addSessionLevelPrivilegesLink(int instrNum) {
        String idStr = "addSectionLevelForInstructor" + instrNum;
        
        return browser.driver.findElement(By.id(idStr));
    }
    
    public void clickAddSessionLevelPrivilegesLink(int instrNum) {
        this.addSessionLevelPrivilegesLink(instrNum).click();
    }
    
    public WebElement sectionCheckBoxInSectionLevel(int instrNum, int sectionLevelNum, int sectionNum) {
        sectionLevelNum--;
        sectionNum--;
        String cssSelector = "#tuneSectionPermissionsDiv" + sectionLevelNum + "ForInstructor" + instrNum
                + " input[name=sectiongroup" + sectionLevelNum + "section" + sectionNum + "]";
        return browser.driver.findElement(By.cssSelector(cssSelector));
    }
    
    public void clickSectionCheckBoxInSectionLevel(int instrNum, int sectionLevelNum, int sectionNum) {
        this.sectionCheckBoxInSectionLevel(instrNum, sectionLevelNum, sectionNum).click();
    }
    
    public void clickViewStudentCheckBoxInSectionLevel(int instrNum, int sectionLevelNum) {
        // in page, sectionLevel is 0 based
        sectionLevelNum--;
        String cssSelector = "#tuneSectionPermissionsDiv" + sectionLevelNum + "ForInstructor" + instrNum
                + " > div > div.panel-body > div.col-sm-6.border-right-gray > input[type=\"checkbox\"]:nth-child(1)";
        browser.driver.findElement(By.cssSelector(cssSelector)).click();
    }
    
    public void clickViewOthersCommentsCheckBoxInSectionLevel(int instrNum, int sectionLevelNum) {
        sectionLevelNum--;
        String cssSelector = "#tuneSectionPermissionsDiv" + sectionLevelNum + "ForInstructor" + instrNum
                + " > div > div.panel-body > div.col-sm-6.border-right-gray > input[type=\"checkbox\"]:nth-child(5)";
        browser.driver.findElement(By.cssSelector(cssSelector)).click();
    }
    
    public void clickViewSessionResultsCheckBoxInSectionLevel(int instrNum, int sectionLevelNum) {
        sectionLevelNum--;
        String cssSelector = "#tuneSectionPermissionsDiv" + sectionLevelNum + "ForInstructor" + instrNum
                + " > div > div.panel-body > div.col-sm-5.col-sm-offset-1 > input[type=\"checkbox\"]:nth-child(3)";
        browser.driver.findElement(By.cssSelector(cssSelector)).click();
    }
    
    public void clickModifySessionResultCheckBoxInSectionLevel(int instrNum, int sectionLevelNum) {
        sectionLevelNum--;
        String cssSelector = "#tuneSectionPermissionsDiv" + sectionLevelNum + "ForInstructor" + instrNum
                + " > div > div.panel-body > div.col-sm-5.col-sm-offset-1 > input[type=\"checkbox\"]:nth-child(5)";
        browser.driver.findElement(By.cssSelector(cssSelector)).click();
    }
    
    public void clickSessionLevelInSectionLevel(int instrNum, int sectionLevelNum) {
        sectionLevelNum--;
        String linkId = "toggleSessionLevelInSection" + sectionLevelNum + "ForInstructor" + instrNum;
        browser.driver.findElement(By.id(linkId)).click();
    }
    
    public boolean clickShowNewInstructorFormButton() {
        showNewInstructorFormButton.click();
        
        boolean isFormShownCorrectly = instructorNameTextBox.isEnabled()
                && instructorEmailTextBox.isEnabled()
                && addInstructorButton.isDisplayed();

        return isFormShownCorrectly;
    }
    
    public void clickAddInstructorButton() {
        addInstructorButton.click();
        waitForPageToLoad();
    }

    public void clickInviteInstructorLink() {
        inviteInstructorLink.click();
        waitForPageToLoad();
    }
    
    public InstructorCoursesPage clickDeleteCourseLinkAndConfirm() {
        clickAndConfirm(deleteCourseLink);
        waitForPageToLoad();
        return changePageType(InstructorCoursesPage.class);
    }

    public void clickDeleteCourseLinkAndCancel() {
        clickAndCancel(deleteCourseLink);
    }
    
    public void clickDeleteInstructorLinkAndConfirm() {
        clickAndConfirm(deleteInstructorLink);
        waitForPageToLoad();
    }
    
    public void clickDeleteInstructorLinkAndCancel() {
        clickAndCancel(deleteInstructorLink);
    }

}
