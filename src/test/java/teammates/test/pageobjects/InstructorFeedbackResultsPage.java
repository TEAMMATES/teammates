package teammates.test.pageobjects;

import static com.google.common.base.Preconditions.checkState;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import teammates.common.util.Const;
import teammates.common.util.ThreadHelper;
import teammates.common.util.retry.MaximumRetriesExceededException;
import teammates.common.util.retry.RetryableTask;

public class InstructorFeedbackResultsPage extends AppPage {

    @FindBy(id = "collapse-panels-button")
    public WebElement collapseExpandButton;

    @FindBy(id = "collapse-panels-button-team-0")
    public WebElement instructorPanelCollapseStudentsButton;

    @FindBy(id = "collapse-panels-button-section-0")
    public WebElement sectionCollapseStudentsButton;

    @FindBy(id = "show-stats-checkbox")
    public WebElement showStatsCheckbox;

    @FindBy(id = "indicate-missing-responses-checkbox")
    public WebElement indicateMissingResponsesCheckbox;

    @FindBy(className = "remind-btn-no-response")
    public WebElement remindAllButton;

    @FindBy(id = "remindModal")
    private WebElement remindModal;

    public InstructorFeedbackResultsPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Session Results</h1>");
    }

    public String getCourseId() {
        return browser.driver.findElement(By.name("courseid")).getAttribute("value");
    }

    public String getFeedbackSessionName() {
        return browser.driver.findElement(By.name("fsname")).getAttribute("value");
    }

    /**
     * Waits until the page structure is loaded.
     * Does not wait for all the content that are loaded by ajax to load.
     */
    public void waitForPageStructureToLoad() {
        super.waitForPageToLoad();
    }

    public boolean isCorrectPage(String courseId, String feedbackSessionName) {
        boolean isCorrectCourseId = this.getCourseId().equals(courseId);
        boolean isCorrectFeedbackSessionName = this.getFeedbackSessionName().equals(feedbackSessionName);
        return isCorrectCourseId && isCorrectFeedbackSessionName && containsExpectedPageContents();
    }

    public void displayEditSettingsWindow() {
        WebElement editBtn = browser.driver.findElement(By.id("editBtn"));
        click(editBtn);
        waitForElementVisibility(By.id("editModal"));
    }

    public void submitEditForm() {
        WebElement submitBtn = browser.driver.findElement(By.id("submitBtn"));
        submitBtn.submit();
    }

    public void displayByGiverRecipientQuestion() {
        displayEditSettingsWindow();

        Select select = new Select(browser.driver.findElement(By.name(Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE)));
        select.selectByVisibleText("Group by - Giver > Recipient > Question");

        submitEditForm();
    }

    public void displayByRecipientGiverQuestion() {
        displayEditSettingsWindow();

        Select select = new Select(browser.driver.findElement(By.name(Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE)));
        select.selectByVisibleText("Group by - Recipient > Giver > Question");

        submitEditForm();
    }

    public void displayByGiverQuestionRecipient() {
        displayEditSettingsWindow();

        Select select = new Select(browser.driver.findElement(By.name(Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE)));
        select.selectByVisibleText("Group by - Giver > Question > Recipient");

        submitEditForm();
    }

    public void displayByRecipientQuestionGiver() {
        displayEditSettingsWindow();

        Select select = new Select(browser.driver.findElement(By.name(Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE)));
        select.selectByVisibleText("Group by - Recipient > Question > Giver");

        submitEditForm();
    }

    public void filterResponsesForSection(String section) {
        displayEditSettingsWindow();

        Select select = new Select(browser.driver.findElements(By.name(Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTION))
                                                 .get(1));
        select.selectByVisibleText(section);

        submitEditForm();
    }

    public void filterResponsesForAllSections() {
        displayEditSettingsWindow();

        Select select = new Select(browser.driver.findElements(By.name(Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTION))
                                                 .get(1));
        select.selectByVisibleText("All");

        submitEditForm();
    }

    public void displayByQuestion() {
        displayEditSettingsWindow();

        Select select = new Select(browser.driver.findElement(By.name(Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE)));
        select.selectByVisibleText("Group by - Question");

        submitEditForm();
    }

    public void clickGroupByTeam() {
        displayEditSettingsWindow();

        WebElement button = browser.driver.findElement(By.name(Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYTEAM));
        click(button);

        submitEditForm();
    }

    public void clickCollapseExpandButtonAndWaitForPanelsToExpand() {
        click(collapseExpandButton);
        waitForPanelsToExpand();
    }

    public void expandPanels() {
        if (isElementPresent("collapse-panels-button")) {
            clickCollapseExpandButtonAndWaitForPanelsToExpand();
        }
    }

    public void clickCollapseExpandButtonAndWaitForPanelsToCollapse() {
        click(collapseExpandButton);
        waitForPanelsToCollapse();
    }

    public void clickShowStats() {
        displayEditSettingsWindow();
        click(showStatsCheckbox);
        submitEditForm();
    }

    public void clickIndicateMissingResponses() {
        displayEditSettingsWindow();
        click(indicateMissingResponsesCheckbox);
        submitEditForm();
    }

    public void clickRemindAllButtonAndWaitForFormToLoad() {
        checkState(isElementInvisibleOrStale(By.className("modal")),
                "The Remind All button is not clickable when a modal is opened");

        click(remindAllButton);
        waitForElementVisibility(remindModal);
        WebElement remindButton = browser.driver.findElement(By.className("remind-particular-button"));
        waitForElementToBeClickable(remindButton);
    }

    public void cancelRemindAllForm() {
        WebElement remindModal = browser.driver.findElement(By.id("remindModal"));
        clickDismissModalButtonAndWaitForModalHidden(remindModal.findElement(By.tagName("button")));
    }

    public void deselectUsersInRemindAllForm() {
        WebElement remindModal = browser.driver.findElement(By.id("remindModal"));
        List<WebElement> usersToRemind = remindModal.findElements(By.name("usersToRemind"));
        for (WebElement e : usersToRemind) {
            markCheckBoxAsUnchecked(e);
        }
    }

    public void clickRemindButtonInModal() {
        WebElement remindModal = browser.driver.findElement(By.id("remindModal"));

        clickDismissModalButtonAndWaitForModalHidden(remindModal.findElement(By.className("remind-particular-button")));
    }

    public InstructorFeedbackEditPage clickEditLink() {
        WebElement button = browser.driver.findElement(By.linkText("[Edit]"));
        click(button);

        InstructorFeedbackEditPage editPage = changePageType(InstructorFeedbackEditPage.class);
        editPage.waitForPageToLoad();
        return editPage;
    }

    public void clickQuestionAdditionalInfoButton(int qnNumber, String additionalInfoId) {
        click(By.id("questionAdditionalInfoButton-" + qnNumber + "-" + additionalInfoId));
    }

    public boolean isQuestionAdditionalInfoVisible(int qnNumber, String additionalInfoId) {
        return isElementVisible("questionAdditionalInfo-" + qnNumber + "-" + additionalInfoId);
    }

    public String getQuestionAdditionalInfoButtonText(int qnNumber, String additionalInfoId) {
        WebElement qnAdditionalInfoButton = browser.driver.findElement(By.id("questionAdditionalInfoButton-"
                                                                             + qnNumber + "-" + additionalInfoId));
        return qnAdditionalInfoButton.getText();
    }

    public void addFeedbackResponseComment(String addResponseCommentId, String commentText) {
        WebElement addResponseCommentForm = browser.driver.findElement(By.id(addResponseCommentId));
        WebElement parentContainer = addResponseCommentForm.findElement(By.xpath("../.."));
        WebElement showResponseCommentAddFormButton = parentContainer.findElement(By.id("button_add_comment"));
        click(showResponseCommentAddFormButton);
        WebElement editorElement = waitForElementPresence(By.cssSelector("#" + addResponseCommentId + " .mce-content-body"));
        waitForRichTextEditorToLoad(editorElement.getAttribute("id"));
        fillRichTextEditor(editorElement.getAttribute("id"), commentText);
        WebElement saveButton = addResponseCommentForm
                .findElement(By.className("col-sm-offset-5"))
                .findElement(By.tagName("a"));
        click(saveButton);
        if (commentText.isEmpty()) {
            // empty comment: wait until the textarea is clickable again
            waitForElementToBeClickable(editorElement);
        } else {
            // non-empty comment: wait until the add comment form disappears
            waitForElementToDisappear(By.id(addResponseCommentId));
        }
    }

    public void editFeedbackResponseComment(String commentIdSuffix, String newCommentText) {
        WebElement commentRow = browser.driver.findElement(By.id("responseCommentRow" + commentIdSuffix));
        click(commentRow.findElements(By.tagName("a")).get(1));

        WebElement commentEditForm = browser.driver.findElement(By.id("responseCommentEditForm" + commentIdSuffix));
        fillRichTextEditor("responsecommenttext" + commentIdSuffix, newCommentText);
        click(commentEditForm.findElement(By.className("col-sm-offset-5")).findElement(By.tagName("a")));
        ThreadHelper.waitFor(1000);
    }

    public void clickVisibilityOptionForResponseCommentAndSave(String idString, int numOfTheCheckbox) {
        String idSuffix = idString.substring(18);
        WebElement commentRow = browser.driver.findElement(By.id(idString));
        click(commentRow.findElements(By.tagName("a")).get(1));
        WebElement commentEditForm = browser.driver.findElement(By.id("responseCommentEditForm" + idSuffix));
        click(commentRow.findElement(By.id("frComment-visibility-options-trigger" + idSuffix)));
        click(commentRow.findElements(By.cssSelector("input[type='checkbox']")).get(numOfTheCheckbox));
        click(commentEditForm.findElement(By.className("col-sm-offset-5")).findElement(By.tagName("a")));
        ThreadHelper.waitFor(1000);
    }

    public void clickCommentModalButton(String commentId) {
        WebElement commentModal = browser.driver.findElement(By.id("commentModal" + commentId));
        WebElement parentTable = commentModal.findElement(By.xpath("../.."));
        WebElement commentButton = parentTable.findElement(By.className("comment-button"));
        click(commentButton);
    }

    public void addFeedbackResponseCommentInCommentModal(String commentId, String commentText) {
        WebElement addResponseCommentForm = browser.driver.findElement(By.id(commentId));
        WebElement editorElement = waitForElementPresence(By.cssSelector("#" + commentId + " .mce-content-body"));
        waitForRichTextEditorToLoad(editorElement.getAttribute("id"));
        fillRichTextEditor(editorElement.getAttribute("id"), commentText);
        WebElement saveButton = addResponseCommentForm
                .findElement(By.className("col-sm-offset-5"))
                .findElement(By.tagName("a"));
        click(saveButton);
        if (commentText.isEmpty()) {
            // empty comment: wait until the textarea is clickable again
            waitForElementToBeClickable(editorElement);
        }
    }

    public void closeCommentModal(String commentId) {
        WebElement commentModal = browser.driver.findElement(By.id("commentModal" + commentId));
        WebElement modalFooter = commentModal.findElement(By.className("modal-footer"));
        WebElement closeButton = modalFooter.findElement(By.className("commentModalClose"));

        clickDismissModalButtonAndWaitForModalHidden(closeButton);
    }

    /**
     * Makes sure the result panels are indeed all visible.
     */
    public void verifyResultsVisible() {
        assertTrue(isAllResultsPanelBodyVisibilityEquals(true));
    }

    /**
     * Makes sure the result panels are indeed all hidden.
     */
    public void verifyResultsHidden() {
        assertTrue(isAllResultsPanelBodyVisibilityEquals(false));
    }

    /**
     * Checks if the body of all the results panels are collapsed or expanded.
     * @param isVisible true to check for expanded, false to check for collapsed.
     * @return true if all results panel body are equals to the visibility being checked.
     */
    private boolean isAllResultsPanelBodyVisibilityEquals(boolean isVisible) {
        By panelCollapseSelector = By.cssSelector(".panel-heading+.panel-collapse");
        List<WebElement> webElements = browser.driver.findElements(panelCollapseSelector);
        int numOfQns = webElements.size();

        assertTrue(numOfQns > 0);

        for (WebElement e : webElements) {
            if (e.isDisplayed() != isVisible) {
                return false;
            }
        }

        return true;
    }

    /**
     * Waits for all the panels to collapse.
     */
    public void waitForPanelsToCollapse() {
        By panelCollapseSelector = By.cssSelector("div[id^='panelBodyCollapse-']");

        waitForElementsToDisappear(browser.driver.findElements(panelCollapseSelector));
    }

    public void waitForPanelsToExpand() {
        By panelCollapseSelector = By.cssSelector(".panel-heading+.panel-collapse");
        List<WebElement> webElements = browser.driver.findElements(panelCollapseSelector);
        for (WebElement element : webElements) {
            try {
                waitForElementVisibility(element);
            } catch (StaleElementReferenceException e) {
                // Case when element has been removed after JS processing
            }
        }
    }

    /**
     * Waits for Ajax loaded panel to be expanded. The panel is expanded when Ajax loading finishes,
     * which is identified by the removal of Ajax class in the element.
     *
     * @param panelId the Id of panel element
     * @param ajaxClass the class removed from {@code panelElement} when Ajax loading finished
     */
    public void waitForAjaxLoadedPanelToExpand(String panelId, String ajaxClass) {
        WebElement panelElement = browser.driver.findElement(By.id(panelId));
        waitFor(ExpectedConditions.not(ExpectedConditions.attributeContains(panelElement, "class", ajaxClass)));
    }

    public boolean verifyAllStatsVisibility() {
        for (WebElement e : browser.driver.findElements(By.className("resultStatistics"))) {
            if ("none".equals(e.getCssValue("display"))) {
                return false;
            }
        }
        return true;
    }

    public boolean verifyMissingResponsesVisibility() {
        List<WebElement> pendingResponses = browser.driver.findElements(By.className("pending_response_row"));
        return pendingResponses.isEmpty();
    }

    public void deleteFeedbackResponseCommentInModal(String commentIdSuffix) {
        deleteFeedbackResponseComment(commentIdSuffix, true);
    }

    public void deleteFeedbackResponseCommentInline(String commentIdSuffix) {
        deleteFeedbackResponseComment(commentIdSuffix, false);
    }

    private void deleteFeedbackResponseComment(String commentIdSuffix, boolean hasParentCommentModal) {
        WebElement commentRow = browser.driver.findElement(By.id("responseCommentRow" + commentIdSuffix));
        final WebElement deleteCommentButton =
                commentRow.findElement(By.tagName("form")).findElement(By.id("commentdelete" + commentIdSuffix));

        WebElement modalBackdrop = null;
        if (hasParentCommentModal) {
            modalBackdrop = browser.driver.findElement(By.className("modal-backdrop"));
        }

        click(deleteCommentButton);

        if (hasParentCommentModal) {
            waitForModalHidden(modalBackdrop);
        }

        waitForConfirmationModalAndClickOk();
        ThreadHelper.waitFor(1500);
    }

    public void verifyCommentRowContent(String commentRowIdSuffix, String commentText, String giverName) {
        By commentRowSelector = By.id("responseCommentRow" + commentRowIdSuffix);
        WebElement commentRow = waitForElementPresence(commentRowSelector);
        waitForTextContainedInElementPresence(By.id("plainCommentText" + commentRowIdSuffix), commentText);
        assertTrue(commentRow.findElement(By.className("text-muted")).getText().contains(giverName)
                   || commentRow.findElement(By.className("text-muted")).getText().contains("you"));
    }

    public void verifyCommentFormErrorMessage(String commentTableIdSuffix, String errorMessage) {
        WebElement errorMessageSpan = waitForElementPresence(By.cssSelector("#errorMessage"));
        assertEquals(errorMessage, errorMessageSpan.getText());
    }

    public void verifyRowMissing(String rowIdSuffix) {
        try {
            waitForAjaxLoaderGifToDisappear();
            browser.driver.findElement(By.id("responseCommentRow" + rowIdSuffix));
            fail("Row expected to be missing found.");
        } catch (NoSuchElementException expected) {
            // row expected to be missing
            return;
        }
    }

    public void clickViewPhotoLink(String panelBodyIndex, String urlRegex) throws MaximumRetriesExceededException {
        String panelBodySelector = "#panelBodyCollapse-" + panelBodyIndex;
        String popoverSelector = panelBodySelector + " .popover-content";
        String clickSelector = panelBodySelector + " .profile-pic-icon-click a";

        moveToElementAndClickAfterWaitForPresence(By.cssSelector(clickSelector));

        verifyPopoverImageUrlWithClickRetry(popoverSelector, clickSelector, urlRegex, "Click and verify photo");
    }

    public void hoverClickAndViewStudentPhotoOnHeading(String panelHeadingIndex, String urlRegex)
            throws MaximumRetriesExceededException {
        String headingSelector = "#panelHeading-" + panelHeadingIndex;
        String popoverSelector = headingSelector + " .popover-content";
        String hoverSelector = headingSelector + " .profile-pic-icon-hover";

        moveToElement(By.cssSelector(hoverSelector));
        click(waitForElementPresence(By.cssSelector(popoverSelector + " > a")));

        verifyPopoverImageUrlWithHoverRetry(popoverSelector, hoverSelector, urlRegex,
                "Hover and verify student photo on heading");
    }

    public void hoverAndViewStudentPhotoOnBody(String panelBodyIndex, String urlRegex)
            throws MaximumRetriesExceededException {
        String bodyRowSelector = "#panelBodyCollapse-" + panelBodyIndex + " > .panel-body > .row";
        String popoverSelector = bodyRowSelector + " .popover-content";
        String hoverSelector = bodyRowSelector + " .profile-pic-icon-hover";

        moveToElement(By.cssSelector(hoverSelector));

        verifyPopoverImageUrlWithHoverRetry(popoverSelector, hoverSelector, urlRegex,
                "Hover and verify student photo on body");
    }

    public void hoverClickAndViewPhotoOnTableCell(int questionBodyIndex, int tableRow, int tableCol, String urlRegex)
            throws MaximumRetriesExceededException {
        String cellSelector = "#questionBody-" + questionBodyIndex + " .data-table tbody"
                              + " tr:nth-child(" + (tableRow + 1) + ")"
                              + " td:nth-child(" + (tableCol + 1) + ")";
        String popoverSelector = cellSelector + " .popover-content";
        String hoverSelector = cellSelector + " .profile-pic-icon-hover";

        moveToElement(By.cssSelector(hoverSelector));
        click(waitForElementPresence(By.cssSelector(popoverSelector + " > a")));

        verifyPopoverImageUrlWithHoverRetry(popoverSelector, hoverSelector, urlRegex,
                "Hover and verify photo on table cell");
    }

    /**
     * Popovers triggered by hover actions sometimes fail to appear, resulting in a {@link WebDriverException}.
     * Popover image verifications that depend on hover actions should therefore be retried several times,
     * with the hover action triggered before each retry.
     */
    private void verifyPopoverImageUrlWithHoverRetry(
            final String popoverSelector, final String hoverSelector, final String urlRegex, String taskName)
            throws MaximumRetriesExceededException {
        uiRetryManager.runUntilNoRecognizedException(new RetryableTask(taskName) {
            @Override
            public void run() {
                verifyPopoverImageUrl(popoverSelector, urlRegex);
            }

            @Override
            public void beforeRetry() {
                moveToElement(By.cssSelector(hoverSelector));
            }
        }, WebDriverException.class);
    }

    /**
     * Similar to {@link #verifyPopoverImageUrlWithHoverRetry}, but for click actions.
     */
    private void verifyPopoverImageUrlWithClickRetry(
            final String popoverSelector, final String clickSelector, final String urlRegex, String taskName)
            throws MaximumRetriesExceededException {
        uiRetryManager.runUntilNoRecognizedException(new RetryableTask(taskName) {
            @Override
            public void run() {
                verifyPopoverImageUrl(popoverSelector, urlRegex);
            }

            @Override
            public void beforeRetry() {
                moveToElementAndClickAfterWaitForPresence(By.cssSelector(clickSelector));
            }
        }, WebDriverException.class);
    }

    private void verifyPopoverImageUrl(String popoverSelector, String urlRegex) {
        String imgSrc = getElementSrcWithRetryAfterWaitForPresence(By.cssSelector(popoverSelector + " > img"));
        verifyImageUrl(urlRegex, imgSrc);
    }

    public void hoverClickAndViewGiverPhotoOnTableCell(int questionBodyIndex, int tableRow, String urlRegex)
            throws MaximumRetriesExceededException {
        hoverClickAndViewPhotoOnTableCell(questionBodyIndex, tableRow, 1, urlRegex);
    }

    public void hoverClickAndViewRecipientPhotoOnTableCell(int questionBodyIndex, int tableRow, String urlRegex)
            throws MaximumRetriesExceededException {
        hoverClickAndViewPhotoOnTableCell(questionBodyIndex, tableRow, 3, urlRegex);
    }

    public void removeNavBar() {
        executeScript("document.getElementsByClassName('navbar-fixed-top')[0].parentNode.removeChild("
                      + "document.getElementsByClassName('navbar-fixed-top')[0])");
    }

    public void verifyModerateResponseButtonBelongsTo(WebElement btn, String email) {
        assertEquals(email, btn.findElement(By.xpath("input[5]")).getAttribute("value"));
    }

    public WebElement getModerateResponseButtonInQuestionView(int qnNo, int responseNo) {
        return browser.driver.findElement(By.id("questionBody-" + (qnNo - 1)))
                             .findElement(By.className("table-responsive"))
                             .findElement(By.xpath("table/tbody/tr[" + responseNo + "]/td[6]/form"));
    }

    public void clickInstructorPanelCollapseStudentsButton() {
        click(instructorPanelCollapseStudentsButton);
    }

    public void clickSectionCollapseStudentsButton() {
        click(sectionCollapseStudentsButton);
    }

    public void waitForInstructorPanelStudentPanelsToCollapse() {
        List<WebElement> studentPanels = browser.driver.findElements(
                By.cssSelector("#panelBodyCollapse-0-1 .panel-collapse"));
        waitForElementsToDisappear(studentPanels);
    }

    public void waitForSectionStudentPanelsToCollapse() {
        List<WebElement> studentPanels = browser.driver.findElements(
                By.cssSelector("#panelBodyCollapse-section-0-1 .panel-collapse"));
        waitForElementsToDisappear(studentPanels);
    }

    public void verifyPanelForParticipantIsDisplayed(String participantIdentifier) {
        WebElement panel = browser.driver.findElement(
                By.xpath("//div[contains(@class, 'panel-primary') or contains(@class, 'panel-default')]"
                            + "[contains(.,'[" + participantIdentifier + "]')]"));
        assertTrue(panel.isDisplayed());
    }

    public void verifySpecifiedPanelIdsAreCollapsed(String[] ids) {
        for (String id : ids) {
            WebElement panel = browser.driver.findElement(By.id("panelBodyCollapse-" + id));
            assertFalse(panel.isDisplayed());
        }
    }

    public boolean isSectionPanelExist(String section) {
        List<WebElement> panelsWithHeading =
                browser.driver.findElements(By.cssSelector("div[id^='panelHeading-'] .panel-heading-text"));
        for (WebElement panel : panelsWithHeading) {
            String panelSectionName = panel.getText();
            if (panelSectionName.equals(section)) {
                return true;
            }
        }
        return false;
    }

    public void changeFsNameInAjaxLoadResponsesForm(int indexOfForm, String newFsName) {
        executeScript("$('.ajax_submit:eq(" + indexOfForm + ") [name=\"fsname\"]').val('" + newFsName + "')");
    }

    public void changeFsNameInNoResponsePanelForm(String newFsName) {
        executeScript("$('.ajax-response-submit [name=\"fsname\"]').val('" + newFsName + "')");
    }

    public void waitForAjaxError(int indexOfForm) {
        By ajaxErrorSelector = By.cssSelector(".ajax_submit:nth-of-type(" + indexOfForm
                                        + ") .ajax-error");
        waitForElementPresence(ajaxErrorSelector);

        waitForTextContainedInElementPresence(ajaxErrorSelector, "[ Failed to load. Click here to retry. ]");
    }

    public void waitForAjaxErrorOnNoResponsePanel() {
        By ajaxErrorSelector = By.cssSelector(".ajax-response-submit .ajax-error");
        waitForElementPresence(ajaxErrorSelector);

        waitForTextContainedInElementPresence(ajaxErrorSelector, "[ Failed to load. Click here to retry. ]");
    }

    private void moveToElement(By by) {
        moveToElement(browser.driver.findElement(by));
    }

    private void moveToElement(WebElement element) {
        new Actions(browser.driver).moveToElement(element).perform();
    }

    private void moveToElementAndClickAfterWaitForPresence(By by) {
        WebElement element = waitForElementPresence(by);
        moveToElement(element);
        click(element);
    }

    private String getElementSrcWithRetryAfterWaitForPresence(By by) {
        try {
            waitForAjaxLoaderGifToDisappear();
            return waitForElementPresence(by).getAttribute("src");
        } catch (StaleElementReferenceException e) {
            // Element changed (e.g. loading gif changed to actual image)
            return waitForElementPresence(by).getAttribute("src");
        }
    }

    /**
     * Expands a particular question panel, causing its results to load.
     */
    public void loadResultQuestionPanel(int questionNumber) {
        String panelId = "panelHeading-" + questionNumber;
        clickPanelAndWaitForExpansion(panelId);
    }

    /**
     * Expands a particular section panel, causing its results to load.
     */
    public void loadResultSectionPanel(int panelNumber, int sectionNumber) {
        String panelId = "panelHeading-section-" + panelNumber + "-" + sectionNumber;
        clickPanelAndWaitForExpansion(panelId);
    }

    /**
     * Expands a particular large scale results panel, causing its results to load.
     */
    public void loadResultLargeScalePanel(int panelNumber) {
        String panelId = "panelHeading-" + panelNumber;
        clickLargeScalePanelAndWaitForExpansion(panelId);
    }

    private void clickPanelAndWaitForExpansion(String panelId) {
        clickElementById(panelId);
        waitForAjaxLoadedPanelToExpand(panelId, "ajax_auto");
    }

    private void clickLargeScalePanelAndWaitForExpansion(String panelId) {
        clickElementById(panelId);
        waitForAjaxLoadedPanelToExpand(panelId, "ajax_submit");
    }

}
