package teammates.e2e.pageobjects;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page Object Model for instructor home page.
 */
public class InstructorHomePage extends AppPage {

    @FindBy(id = "sort-by-course-id")
    private WebElement sortByIdBtn;

    @FindBy(id = "sort-by-course-name")
    private WebElement sortByNameBtn;

    @FindBy(id = "sort-by-course-creation-date")
    private WebElement sortByDateBtn;

    @FindBy(className = "sort-session-name")
    private List<WebElement> sortSessionNameBtn;

    @FindBy(className = "sort-session-start-date")
    private List<WebElement> sortSessionStartDateBtn;

    @FindBy(className = "sort-session-end-date")
    private List<WebElement> sortSessionEndDateBtn;

    @FindBy(linkText = "Home")
    private WebElement homeBtn;

    @FindBy(tagName = "tm-send-reminders-to-student-modal")
    private WebElement remindModal;

    @FindBy(tagName = "tm-resend-results-link-to-student-modal")
    private WebElement resendPublishedEmailModal;

    @FindBy(tagName = "tm-copy-session-modal")
    private WebElement copySessionModal;

    public InstructorHomePage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageTitle().contains("Home");
    }

    public void clickAndConfirmRemindStudentsWithUsers(int rowId) {
        clickFsRemindStudentsBtn(rowId);
        fillRemindUsersForm();
        clickModalSubmitBtn();
    }

    public void clickAndConfirmResendPublishedWithUsers(int rowId) {
        clickResendPublishedEmail(rowId);
        fillResendEmailUsersForm();
        clickModalSubmitBtn();
    }

    public void clickFsCopyButton(int rowId) {
        click(getFsCopyBtn(rowId));
        waitForElementVisibility(copySessionModal);
    }

    public void clickFsShowLink(int rowId) {
        click(getFsShowLink(rowId));
        waitForPageToLoad();
    }

    public void clickHomeBtn() {
        click(homeBtn);
        waitForPageToLoad();
    }

    public void clickModalSubmitBtn() {
        click(getModalSubmitBtn());
    }

    public void clickSortByIdButton() {
        click(sortByIdBtn);
        waitForPageToLoad();
    }

    public void clickSortByNameButton() {
        click(sortByNameBtn);
        waitForPageToLoad();
    }

    public void clickSortByDateButton() {
        click(sortByDateBtn);
        waitForPageToLoad();
    }

    public void loadInstructorCoursePanel(int panelId) {
        click(By.id("panel-head-" + panelId));
        waitForPageToLoad();
    }

    public void sortTablesByName() {
        clickElements(sortSessionNameBtn);
        clickElements(sortSessionNameBtn);
    }

    public void sortTablesByStartDate() {
        clickElements(sortSessionStartDateBtn);
        clickElements(sortSessionStartDateBtn);
    }

    public void sortTablesByEndDate() {
        clickElements(sortSessionEndDateBtn);
        clickElements(sortSessionEndDateBtn);
    }

    public WebElement getCourseArchiveBtn(int panelId) {
        return getCourseBtnInPanel("course-archive-btn", panelId);
    }

    public WebElement getCourseDeleteBtn(int panelId) {
        return getCourseBtnInPanel("course-delete-btn", panelId);
    }

    public WebElement getFsCopyBtn(int rowId) {
        return getSessionBtnInRow("session-copy-btn", rowId);
    }

    public WebElement getFsDeleteBtn(int rowId) {
        return getSessionBtnInRow("session-delete-btn", rowId);
    }

    public WebElement getFsPublishBtn(int rowId) {
        return getSessionBtnInRow("session-publish-result-btn", rowId);
    }

    public WebElement getFsRemindStudentsBtn(int rowId) {
        return getSessionBtnInRow("session-remind-students-btn", rowId);
    }

    public WebElement getFsResendPublishedEmail(int rowId) {
        return getSessionBtnInRow("session-resend-result-btn", rowId);
    }

    public WebElement getFsUnpublishBtn(int rowId) {
        return getSessionBtnInRow("session-unpublish-result-btn", rowId);
    }

    public WebElement getModalSubmitBtn() {
        return browser.driver.findElement(By.className("modal-btn-ok"));
    }

    public String getFsViewResponseText(int rowId) {
        return browser.driver.findElement(By.cssSelector(".session-" + rowId + "> td:nth-child(6)")).getText();
    }

    public boolean isCoursePanelExpanded(int courseId) {
        WebElement coursePanel = browser.driver.findElement(By.id("course-" + courseId));
        return coursePanel.findElements(By.className("card-body")).size() != 0;
    }

    public void verifyResendPublishedEmailButtonExists(int rowId) {
        clickFsResultsBtn(rowId);
        WebElement sessionRow = getSessionElementInRow(".dropdown-menu.show", rowId);
        verifyElementContainsElement(sessionRow, By.className("session-resend-result-btn"));
    }

    public void verifyResendPublishedEmailButtonDoesNotExist(int rowId) {
        clickFsResultsBtn(rowId);
        WebElement sessionRow = getSessionElementInRow(".dropdown-menu.show", rowId);
        verifyElementDoesNotContainElement(sessionRow, By.className("session-resend-result-btn"));
    }

    /**
     * Populates the fields of the form by using the provided name, and selecting the last course shown.
     * @param newFsName feedback session name of the new session
     */
    public void fillFormWithLastCourseSelected(String newFsName) {
        WebElement fscopyModalBody = browser.driver.findElement(By.className("modal-body"));
        List<WebElement> coursesCheckBoxes = fscopyModalBody.findElements(By.name("copySessionChooseCourse"));
        for (WebElement e : coursesCheckBoxes) {
            markCheckBoxAsChecked(e);
        }

        WebElement fsNameInput = fscopyModalBody.findElement(By.id("copied-fsname"));
        fillTextBox(fsNameInput, newFsName);
    }

    private WebElement getCourseBtnInPanel(String elementClassNamePrefix, int panelId) {
        return browser.driver.findElement(By.id("panel-head-" + panelId)).findElement(By.className(elementClassNamePrefix));
    }

    private WebElement getFsShowLink(int rowId) {
        return browser.driver.findElement(By.className("session-" + rowId)).findElement(By.linkText("Show"));
    }

    private WebElement getFsResultsBtn(int rowId) {
        return getSessionBtnInRow("session-results-btn", rowId);
    }

    private WebElement getSessionBtnInRow(String elementClassNamePrefix, int rowId) {
        return browser.driver.findElement(By.className("session-" + rowId))
                .findElement(By.className(elementClassNamePrefix));
    }

    private WebElement getSessionElementInRow(String elementCssSelectPrefix, int rowId) {
        return browser.driver.findElement(By.className("session-" + rowId))
                .findElement(By.cssSelector(elementCssSelectPrefix));
    }

    private void checkCheckboxesInForm(WebElement form, String elementsName) {
        List<WebElement> formElements = form.findElements(By.name(elementsName));
        for (WebElement e : formElements) {
            markCheckBoxAsChecked(e);
        }
    }

    private void clickElements(List<WebElement> elements) {
        for (WebElement ele : elements) {
            click(ele);
        }
    }

    private void clickFsRemindStudentsBtn(int rowId) {
        click(getFsRemindStudentsBtn(rowId));
        waitForElementVisibility(remindModal);
    }

    private void clickFsResultsBtn(int rowId) {
        click(getFsResultsBtn(rowId));
        waitForElementVisibility(By.cssSelector(".dropdown-menu.show"));
    }

    private void clickResendPublishedEmail(int rowId) {
        click(getFsResendPublishedEmail(rowId));
        waitForElementVisibility(resendPublishedEmailModal);
    }

    private void fillRemindUsersForm() {
        checkCheckboxesInForm(remindModal, "usersToRemind");
    }

    private void fillResendEmailUsersForm() {
        checkCheckboxesInForm(resendPublishedEmailModal, "usersToResendPublishedEmail");
    }

}
