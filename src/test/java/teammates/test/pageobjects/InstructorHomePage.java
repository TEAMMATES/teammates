package teammates.test.pageobjects;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.util.ThreadHelper;

public class InstructorHomePage extends AppPage {

    @FindBy(id = "searchBox")
    private WebElement searchBox;

    @FindBy(id = "buttonSearch")
    private WebElement searchButton;

    @FindBy(id = "sortById")
    private WebElement sortByIdButton;

    @FindBy(id = "sortByName")
    private WebElement sortByNameButton;

    @FindBy(id = "sortByDate")
    private WebElement sortByDateButton;

    @FindBy(id = "remindModal")
    private WebElement remindModal;

    @FindBy(id = "resendPublishedEmailModal")
    private WebElement resendPublishedEmailModal;

    @FindBy(className = "button_sortname")
    private List<WebElement> tablesSortByName;

    @FindBy(className = "button_sortstartdate")
    private List<WebElement> tablesSortByStartDate;

    @FindBy(className = "button_sortenddate")
    private List<WebElement> tablesSortByEndDate;

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
        return containsExpectedPageContents(getPageSource());
    }

    public static boolean containsExpectedPageContents(String pageSource) {
        return pageSource.contains("<h1>Home</h1>");
    }

    public void clickSortByIdButton() {
        click(sortByIdButton);
        waitForPageToLoad();
    }

    public void clickSortByNameButton() {
        click(sortByNameButton);
        waitForPageToLoad();
    }

    public void clickSortByDateButton() {
        click(sortByDateButton);
        waitForPageToLoad();
    }

    private void clickElements(List<WebElement> elements) {
        for (WebElement ele : elements) {
            click(ele);
        }
    }

    public void sortTablesByName() {
        clickElements(tablesSortByName);
    }

    public void sortTablesByStartDate() {
        clickElements(tablesSortByStartDate);
    }

    public void sortTablesByEndDate() {
        clickElements(tablesSortByEndDate);
    }

    public InstructorCourseEnrollPage clickCourseEnrollLink(String courseId) {
        click(getCourseLinkInRow("course-enroll-for-test", getCourseRowId(courseId)));
        waitForPageToLoad();
        return changePageType(InstructorCourseEnrollPage.class);
    }

    public InstructorCourseDetailsPage clickCourseViewLink(String courseId) {
        click(getCourseLinkInRow("course-view-for-test", getCourseRowId(courseId)));
        waitForPageToLoad();
        return changePageType(InstructorCourseDetailsPage.class);
    }

    public InstructorCourseEditPage clickCourseEditLink(String courseId) {
        click(getCourseLinkInRow("course-edit-for-test", getCourseRowId(courseId)));
        waitForPageToLoad();
        return changePageType(InstructorCourseEditPage.class);
    }

    public void clickCourseDeleteLink(String courseId) {
        click(getDeleteCourseLink(courseId));
    }

    //TODO: rename course-add-eval-for-test
    public InstructorFeedbackSessionsPage clickCourseAddEvaluationLink(String courseId) {
        click(getCourseLinkInRow("course-add-eval-for-test", getCourseRowId(courseId)));
        waitForPageToLoad();
        ThreadHelper.waitBriefly();
        return changePageType(InstructorFeedbackSessionsPage.class);
    }

    public InstructorFeedbackResultsPage clickFeedbackSessionViewResultsLink(String courseId, String fsName) {
        click(getViewResultsLink(courseId, fsName));
        waitForPageToLoad();
        return changePageType(InstructorFeedbackResultsPage.class);
    }

    public InstructorFeedbackEditPage clickFeedbackSessionEditLink(String courseId, String fsName) {
        click(getEditLink(courseId, fsName));
        waitForPageToLoad();
        return changePageType(InstructorFeedbackEditPage.class);
    }

    public InstructorFeedbackSessionsPage clickFeedbackSessionDeleteLink(String courseId, String fsName) {
        clickAndConfirm(getDeleteEvalLink(courseId, fsName));
        waitForPageToLoad();
        switchToNewWindow();
        return changePageType(InstructorFeedbackSessionsPage.class);
    }

    public FeedbackSubmitPage clickFeedbackSessionSubmitLink(String courseId, String fsName) {
        click(getSubmitLink(courseId, fsName));
        waitForPageToLoad();
        switchToNewWindow();
        return changePageType(FeedbackSubmitPage.class);
    }

    public InstructorHomePage clickFeedbackSessionRemindLink(String courseId, String fsName) {
        clickAndConfirm(getRemindLink(courseId, fsName));
        waitForPageToLoad();
        switchToNewWindow();
        return changePageType(InstructorHomePage.class);
    }

    public InstructorHomePage clickFeedbackSessionUnpublishLink(String courseId, String fsName) {
        clickAndConfirm(getUnpublishLink(courseId, fsName));
        waitForPageToLoad();
        switchToNewWindow();
        return changePageType(InstructorHomePage.class);
    }

    public InstructorHomePage clickFeedbackSessionPublishLink(String courseId, String fsName) {
        clickAndConfirm(getPublishLink(courseId, fsName));
        return changePageType(InstructorHomePage.class);
    }

    public void clickResendPublishedEmailLink(String courseId, String evalName) {
        click(getResendPublishedEmailLink(courseId, evalName));
        waitForElementVisibility(resendPublishedEmailModal);
    }

    public void cancelResendPublishedEmailForm() {
        cancelModalForm(resendPublishedEmailModal);
    }

    public void fillResendPublishedEmailForm() {
        checkCheckboxesInForm(resendPublishedEmailModal, "usersToEmail");
    }

    public void submitResendPublishedEmailForm() {
        resendPublishedEmailModal.findElement(By.name("form_email_list")).submit();
    }

    public InstructorStudentListPage searchForStudent(String studentName) {
        searchBox.clear();
        searchBox.sendKeys(studentName);
        click(searchButton);
        waitForPageToLoad();
        return changePageType(InstructorStudentListPage.class);
    }

    public WebElement getViewResponseLink(String courseId, String evalName) {
        int evaluationRowId = getEvaluationRowId(courseId, evalName);
        String xpathExp = "//tr[@id='session" + evaluationRowId + "']/td[contains(@class,'session-response-for-test')]/a";

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

    public WebElement getRemindOptionsLink(String courseId, String evalName) {
        return getSessionLinkInRow("session-remind-options-for-test", getEvaluationRowId(courseId, evalName));
    }

    public void clickRemindOptionsLink(String courseId, String evalName) {
        click(getRemindOptionsLink(courseId, evalName));
    }

    public WebElement getRemindInnerLink(String courseId, String evalName) {
        return getSessionLinkInRow("session-remind-inner-for-test", getEvaluationRowId(courseId, evalName));
    }

    public WebElement getRemindParticularUsersLink(String courseId, String evalName) {
        return getSessionLinkInRow("session-remind-particular-for-test", getEvaluationRowId(courseId, evalName));
    }

    public void clickRemindParticularUsersLink(String courseId, String evalName) {
        click(getRemindParticularUsersLink(courseId, evalName));
        waitForElementVisibility(remindModal);
    }

    public void cancelRemindParticularUsersForm() {
        cancelModalForm(remindModal);
    }

    public void fillRemindParticularUsersForm() {
        checkCheckboxesInForm(remindModal, "usersToRemind");
    }

    public void submitRemindParticularUsersForm() {
        remindModal.findElement(By.name("form_remind_list")).submit();
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

    public void verifyResendPublishedEmailButtonExists(String courseId, String evalName) {
        WebElement sessionRow = waitForElementPresence(By.id("session" + getEvaluationRowId(courseId, evalName)));
        verifyElementContainsElement(sessionRow, By.className("session-resend-published-email-for-test"));
    }

    public void verifyResendPublishedEmailButtonDoesNotExist(String courseId, String evalName) {
        WebElement sessionRow = waitForElementPresence(By.id("session" + getEvaluationRowId(courseId, evalName)));
        verifyElementDoesNotContainElement(sessionRow, By.className("session-resend-published-email-for-test"));
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
        clickAndCancel(getCourseLinkInRow("course-archive-for-test", getCourseRowId(courseId)));
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
        waitForAjaxLoaderGifToDisappear();
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
