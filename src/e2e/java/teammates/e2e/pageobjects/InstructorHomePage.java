package teammates.e2e.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class InstructorHomePage extends AppPage {

    @FindBy(id = "sort-by-course-id")
    private WebElement sortByIdBtn;

    @FindBy(id = "sort-by-course-name")
    private WebElement sortByNameBtn;

    @FindBy(id = "sort-by-course-creation-date")
    private WebElement sortByDateBtn;

    @FindBy(id = "remind-modal")
    private WebElement remindModalBtn;

    @FindBy(id = "resend-published-email-modal")
    private WebElement resendPublishedEmailModalLink;

    @FindBy(className = "sort-by-session-name")
    private List<WebElement> sortBySessionNameBtn;

    @FindBy(className = "sort-by-session-start-date")
    private List<WebElement> sortBySessionStartDateBtn;

    @FindBy(className = "sort-by-session-end-date")
    private List<WebElement> sortBySessionEndDateBtn;

    private InstructorCopyFsToModal fsCopyModal;

    public InstructorHomePage(Browser browser) {
        super(browser);
        if (InstructorCopyFsToModal.isPresentOnPage(browser)) {
            this.fsCopyModal = new InstructorCopyFsToModal(browser);
        }
    }

    public InstructorCopyFsToModal getFsCopyModal() {
        return fsCopyModal;
    }

    @Override
    protected boolean containsExpectedPageContents() {
        System.out.println("this is true or false: " + getPageTitle().contains("Home"));
        return getPageTitle().contains("Home");
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

    private void clickElements(List<WebElement> elements) {
        for (WebElement ele : elements) {
            click(ele);
        }
    }

    public void sortTablesByName() {
        clickElements(sortBySessionNameBtn);
    }

    public void sortTablesByStartDate() {
        clickElements(sortBySessionStartDateBtn);
    }

    public void sortTablesByEndDate() {
        clickElements(sortBySessionEndDateBtn);
    }

    public void clickCourseDeleteLink(String courseId) {
        click(getDeleteCourseLink(courseId));
    }

    public InstructorHomePage clickFeedbackSessionUnpublishLink(String courseId, String fsName) {
        clickAndConfirm(getUnpublishLink(courseId, fsName));
        waitForPageToLoad();
//        switchToNewWindow();
        return changePageType(InstructorHomePage.class);
    }

    public InstructorHomePage clickFeedbackSessionPublishLink(String courseId, String fsName) {
        clickAndConfirm(getPublishLink(courseId, fsName));
        return changePageType(InstructorHomePage.class);
    }

    public void clickResendPublishedEmailLink(String courseId, String evalName) {
        click(getResendPublishedEmailLink(courseId, evalName));
        waitForElementVisibility(resendPublishedEmailModalLink);
    }

//    public void cancelResendPublishedEmailForm() {
//        cancelModalForm(resendPublishedEmailModal);
//    }
//
//    public void fillResendPublishedEmailForm() {
//        checkCheckboxesInForm(resendPublishedEmailModal, "usersToEmail");
//    }

    public void submitResendPublishedEmailForm() {
        resendPublishedEmailModalLink.findElement(By.name("form_email_list")).submit();
    }

    public WebElement getViewResponseLink(String courseId, String evalName) {
//        int evaluationRowId = getEvaluationRowId(courseId, evalName);
//        //*[text()='" + courseId + "']
        String xpathExp = "//*[text()='" + evalName + "']//a[@href='#']";

        return browser.driver.findElement(By.xpath(xpathExp));
    }

    public void setViewResponseLinkValue(WebElement element, String newValue) {
        executeScript("arguments[0].href=arguments[1]", element, newValue);
    }

    public void clickViewResponseLink(String courseId, String evalName) {
        click(getViewResponseLink(courseId, evalName));
    }

    public WebElement getViewResultsLink(String courseId, String evalName) {
        return getSessionLinkInRow("session-view-for-test", getEvaluationRowId(courseId, evalName));
    }

    public WebElement getEditLink(String courseId, String evalName) {
        return getSessionLinkInRow("session-edit-for-test", getEvaluationRowId(courseId, evalName));
    }

    public WebElement getSubmitLink(String courseId, String evalName) {
        return getSessionLinkInRow("session-submit-for-test", getEvaluationRowId(courseId, evalName));
    }

    public WebElement getPreviewLink(String courseId, String evalName) {
        return getSessionLinkInRow("session-preview-for-test", getEvaluationRowId(courseId, evalName));
    }

    public WebElement getRemindLink(String courseId, String evalName) {
        return getSessionLinkInRow("session-remind-for-test", getEvaluationRowId(courseId, evalName));
    }

    public WebElement getRemindInnerLink(String courseId, String evalName) {
        return getSessionLinkInRow("session-remind-inner-for-test", getEvaluationRowId(courseId, evalName));
    }

    public WebElement getRemindParticularUsersLink(String courseId, String evalName) {
        return getSessionLinkInRow("session-remind-particular-for-test", getEvaluationRowId(courseId, evalName));
    }

    public void cancelRemindParticularUsersForm() {
        cancelModalForm(remindModalBtn);
    }

    public void cancelModalForm(WebElement modal) {
        clickDismissModalButtonAndWaitForModalHidden(modal.findElement(By.tagName("button")));
    }

    public void fillRemindParticularUsersForm() {
        checkCheckboxesInForm(remindModalBtn, "usersToRemind");
    }

    public void checkCheckboxesInForm(WebElement form, String elementsName) {
        List<WebElement> formElements = form.findElements(By.name(elementsName));
        for (WebElement e : formElements) {
            markCheckBoxAsChecked(e);
        }
    }

    public void submitRemindParticularUsersForm() {
        remindModalBtn.findElement(By.name("form_remind_list")).submit();
    }

    public WebElement getSessionResultsOptionsCaretElement(String courseId, String evalName) {
        int sessionRowId = getEvaluationRowId(courseId, evalName);
        return browser.driver.findElement(
                By.xpath("//tbody/tr[" + (sessionRowId + 1)
                    + "]//button[contains(@class,'session-results-options')]"));
    }

    public void clickSessionResultsOptionsCaretElement(String courseId, String evalName) {
        click(getSessionResultsOptionsCaretElement(courseId, evalName));
    }

    public WebElement getPublishLink(String courseId, String evalName) {
        return getSessionLinkInRow("session-publish-for-test", getEvaluationRowId(courseId, evalName));
    }

    public WebElement getUnpublishLink(String courseId, String evalName) {
        return getSessionLinkInRow("session-unpublish-for-test", getEvaluationRowId(courseId, evalName));
    }

    public void verifyDownloadResultButtonExists(String courseId, String evalName) {
        WebElement sessionRow = waitForElementPresence(By.id("session" + getEvaluationRowId(courseId, evalName)));
        // verifyElementContainsElement(sessionRow, By.className("session-results-download"));
    }

    public void verifyResendPublishedEmailButtonExists(String courseId, String evalName) {
        WebElement sessionRow = waitForElementPresence(By.id("session" + getEvaluationRowId(courseId, evalName)));
        // verifyElementContainsElement(sessionRow, By.className("session-resend-published-email-for-test"));
    }

    public void verifyResendPublishedEmailButtonDoesNotExist(String courseId, String evalName) {
        WebElement sessionRow = waitForElementPresence(By.id("session" + getEvaluationRowId(courseId, evalName)));
        // verifyElementDoesNotContainElement(sessionRow, By.className("session-resend-published-email-for-test"));
    }

    public WebElement getResendPublishedEmailLink(String courseId, String evalName) {
        return getSessionLinkInRow("session-resend-published-email-for-test", getEvaluationRowId(courseId, evalName));
    }

    public WebElement getDeleteEvalLink(String courseId, String evalName) {
        return getSessionLinkInRow("session-delete-for-test", getEvaluationRowId(courseId, evalName));
    }

    public WebElement getDeleteCourseLink(String courseId) {
        return getCourseLinkInRow("course-delete-for-test", getCourseRowId(courseId));
    }

    public InstructorHomePage clickArchiveCourseLinkAndConfirm(String courseId) {
        clickAndConfirm(getCourseLinkInRow("course-archive-for-test", getCourseRowId(courseId)));
        waitForPageToLoad();
        return this;
    }

    public InstructorHomePage clickArchiveCourseLinkAndCancel(String courseId) {
        // clickAndCancel(getCourseLinkInRow("course-archive-for-test", getCourseRowId(courseId)));
        waitForPageToLoad();
        return this;
    }

    public String getArchiveCourseLink(String courseId) {
        return getCourseLinkInRow("course-archive-for-test", getCourseRowId(courseId)).getAttribute("href");
    }

    private WebElement getSessionLinkInRow(String elementClassNamePrefix, int rowId) {
        waitForElementPresence(By.id("session" + rowId));
        waitForElementPresence(By.className(elementClassNamePrefix));
        return browser.driver.findElement(By.id("session" + rowId)).findElement(By.className(elementClassNamePrefix));
    }

    private WebElement getCourseLinkInRow(String elementClassNamePrefix, int rowId) {
        waitForElementPresence(By.id("course-" + rowId));
        waitForElementPresence(By.className(elementClassNamePrefix));
        return browser.driver.findElement(By.id("course-" + rowId)).findElement(By.className(elementClassNamePrefix));
    }

    private int getEvaluationRowId(String courseId, String evalName) {
        int courseRowId = getCourseRowId(courseId);
        if (courseRowId == -1) {
            return -2;
        }
        String template = "//div[@id='course-%d']//tr[@id='session%d']";
        int max = browser.driver.findElements(By.xpath("//div[starts-with(@id, 'course-')]//tr")).size();
        for (int id = 0; id < max; id++) {
            if (getElementText(
                    By.xpath(String.format(template + "//td[1]", courseRowId,
                            id))).equals(evalName)) {
                return id;
            }
        }
        return -1;
    }

    private int getCourseRowId(String courseId) {
        // waitForAjaxLoaderGifToDisappear();
        int id = 0;
        while (isElementPresent(By.id("course-" + id))) {
            if (getElementText(
                    By.xpath("//div[@id='course-" + id
                            + "']//strong"))
                    .startsWith("[" + courseId + "]")) {
                return id;
            }
            id++;
        }
        return -1;
    }

    private String getElementText(By locator) {
        waitForElementPresence(locator);
        return browser.driver.findElement(locator).getText();
    }

    public void changeFsCopyButtonActionLink(String courseId, String feedbackSessionName, String newActionLink) {
        String id = "button_fscopy" + "-" + courseId + "-" + feedbackSessionName;
        By element = By.id(id);
        waitForElementPresence(element);

        executeScript("document.getElementById('" + id + "').setAttribute('data-actionlink', '" + newActionLink + "')");
    }

    public void clickFsCopyButton(String courseId, String feedbackSessionName) {
        By fsCopyButtonElement = By.id("button_fscopy" + "-" + courseId + "-" + feedbackSessionName);

        // give it some time to load as it is loaded via AJAX
        waitForElementPresence(fsCopyButtonElement);

        WebElement fsCopyButton = browser.driver.findElement(fsCopyButtonElement);
        fsCopyButton.click();
    }
}
