package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertTrue;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class InstructorCommentsPage extends AppPage {

    @FindBy(id = "option-check")
    private WebElement showMoreOptionsCheckbox;

    @FindBy(id = "displayArchivedCourses_check")
    private WebElement isIncludeArchivedCoursesCheckbox;

    public InstructorCommentsPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Comments from Instructors</h1>");
    }

    public void loadResponseComments() {
        String pathToSecondDisplayPanelHeading = "//*[@id=\"panel_display-2\"]/div/div[1]";
        click(browser.driver.findElement(By.xpath(pathToSecondDisplayPanelHeading)));
        waitForPageToLoad();
        String pathToSecondDisplayPanelBodyInnerDiv = "//*[@id=\"panel_display-2\"]/div/div[2]";
        waitForElementVisibility(browser.driver.findElement(By.xpath(pathToSecondDisplayPanelBodyInnerDiv)));
    }

    public void clickSendEmailNotificationButton() {
        String pathToSendEmailNotificationButton = "//*[@id=\"mainContent\"]/div[4]/div[1]/div/a";
        click(browser.driver.findElement(By.xpath(pathToSendEmailNotificationButton)));
        waitForPageToLoad();
    }

    public void clickShowMoreOptions() {
        click(showMoreOptionsCheckbox);
        waitForPageToLoad();
    }

    public void clickIsIncludeArchivedCoursesCheckbox() {
        click(isIncludeArchivedCoursesCheckbox);
        waitForPageToLoad();
    }

    public void clickPreviousCourseLink() {
        click(getPreviousCourseLink());
        waitForPageToLoad();
    }

    public void clickNextCourseLink() {
        click(getNextCourseLink());
        waitForPageToLoad();
    }

    public void showCommentsForAll() {
        click(browser.driver.findElement(By.id("panel_all")));
    }

    public void showCommentsFromAll() {
        click(browser.driver.findElement(By.id("giver_all")));
    }

    public void showCommentsFromAllStatus() {
        click(browser.driver.findElement(By.id("status_all")));
    }

    public void showCommentsForPanel(int panelIdx) {
        click(browser.driver.findElement(By.id("panel_check-" + panelIdx)));
    }

    public void showCommentsFromGiver(String giverIdx) {
        click(browser.driver.findElement(By.id("giver_check-by-" + giverIdx)));
    }

    public void showCommentsForStatus(String status) {
        click(browser.driver.findElement(By.id("status_check-" + status)));
    }

    public WebElement getNextCourseLink() {
        String pathToNextCourseLink = "//*[@id=\"mainContent\"]/ul[1]/li[4]/a";
        return browser.driver.findElement(By.xpath(pathToNextCourseLink));
    }

    public WebElement getPreviousCourseLink() {
        String pathToPreviousCourseLink = "//*[@id=\"mainContent\"]/ul[1]/li[1]/a";
        return browser.driver.findElement(By.xpath(pathToPreviousCourseLink));
    }

    public WebElement getStudentCommentRow(int rowIdx) {
        return browser.driver.findElement(By.id("form_commentedit-" + rowIdx));
    }

    public void clickStudentCommentEditForRow(int i) {
        WebElement editCommentButton = browser.driver.findElement(By.id("commentedit-" + i));
        click(editCommentButton);
    }

    public void clickStudentCommentVisibilityEdit(int row) {
        WebElement visibilityEditButton = browser.driver.findElement(By.id("visibility-options-trigger" + row));
        waitForElementVisibility(visibilityEditButton);
        click(visibilityEditButton);
    }

    public void clickResponseCommentVisibilityEdit(String suffix) {
        WebElement visibilityEditButton =
                browser.driver.findElement(By.id("frComment-visibility-options-trigger-" + suffix));
        waitForElementVisibility(visibilityEditButton);
        click(visibilityEditButton);
    }

    public void clickAllCheckboxes(int row) {
        List<WebElement> answerCheckboxes = browser.driver
                .findElement(By.id("visibility-options" + row))
                .findElements(By.className("answerCheckbox"));
        List<WebElement> checkboxes = answerCheckboxes;
        for (WebElement checkbox : checkboxes) {
            click(checkbox);
        }
    }

    public void clickAllCheckboxes(String suffix) {
        List<WebElement> answerCheckboxes = browser.driver
                .findElement(By.id("visibility-options-" + suffix))
                .findElements(By.className("answerCheckbox"));
        List<WebElement> checkboxes = answerCheckboxes;
        for (WebElement checkbox : checkboxes) {
            click(checkbox);
        }
    }

    public void clickAllGiverCheckboxes(int row) {
        List<WebElement> giverCheckboxes = browser.driver
                                           .findElement(By.id("visibility-options" + row))
                                           .findElements(By.className("giverCheckbox"));
        for (WebElement checkbox : giverCheckboxes) {
            click(checkbox);
        }
    }

    public void fillTextareaToEditStudentCommentForRow(int i, String text) {
        String editorId = "commentText" + i;
        WebElement textarea = browser.driver.findElement(By.id(editorId));
        waitForRichTextEditorToLoad(editorId);
        click(textarea);
        fillRichTextEditor(textarea.getAttribute("id"), text);
    }

    public void saveEditStudentCommentForRow(int i) {
        click(browser.driver.findElement(By.id("commentsave-" + i)));
        waitForPageToLoad();
    }

    public void clickResponseCommentAdd(int sessionIdx, int questionIdx, int responseIdx) {
        waitForPageToLoad();
        WebElement addCommentButton = browser.driver.findElement(
                By.id("button_add_comment-" + sessionIdx + "-" + questionIdx + "-" + responseIdx));
        click(addCommentButton);
        waitForPageToLoad();
    }

    public void fillTextareaToEditResponseComment(int sessionIdx, int questionIdx, int responseIdx, String text) {
        String editorId = "responseCommentAddForm-" + sessionIdx + "-" + questionIdx + "-" + responseIdx;
        WebElement textarea = browser.driver.findElement(By.id(editorId));
        waitForRichTextEditorToLoad(editorId);
        click(textarea);
        fillRichTextEditor(textarea.getAttribute("id"), text);
    }

    public void fillTextareaToEditResponseComment(int sessionIdx, int questionIdx, int responseIdx,
                                                  int commentIdx, String text) {
        String editorId = "responsecommenttext-" + sessionIdx + "-" + questionIdx + "-" + responseIdx + "-" + commentIdx;
        waitForRichTextEditorToLoad(editorId);
        fillRichTextEditor(editorId, text);
    }

    public void addResponseComment(int sessionIdx, int questionIdx, int responseIdx) {
        WebElement saveCommentButton = browser.driver.findElement(
                By.id("button_save_comment_for_add-" + sessionIdx + "-" + questionIdx + "-" + responseIdx));
        click(saveCommentButton);
        waitForPageToLoad();
    }

    public void clickResponseCommentEdit(int sessionIdx, int questionIdx, int responseIdx, int commentIdx) {
        waitForPageToLoad();
        WebElement editCommentButton = browser.driver.findElement(
                By.id("commentedit-" + sessionIdx + "-" + questionIdx + "-" + responseIdx + "-" + commentIdx));
        click(editCommentButton);
        waitForPageToLoad();
    }

    public void saveResponseComment(int sessionIdx, int questionIdx, int responseIdx, int commentIdx) {
        WebElement saveCommentButton = browser.driver.findElement(
                By.id("button_save_comment_for_edit-" + sessionIdx + "-"
                      + questionIdx + "-" + responseIdx + "-" + commentIdx));
        click(saveCommentButton);
        waitForPageToLoad();
    }

    public void clickResponseCommentDelete(int sessionIdx, int questionIdx, int responseIdx, int commentIdx) {
        WebElement deleteCommentButton = browser.driver.findElement(
                By.id("commentdelete-" + sessionIdx + "-" + questionIdx + "-" + responseIdx + "-" + commentIdx));
        click(deleteCommentButton);
        waitForConfirmationModalAndClickOk();
        waitForPageToLoad();
    }

    public void clickDeleteStudentComment(int commentIdx) {
        WebElement deleteLink = browser.driver.findElement(By.id("commentdelete-" + commentIdx));
        click(deleteLink);
    }

    /**
     * Clicks 'Comments for students' panel heading of the comment panel to either expand/collapse the panel body.
     */
    public void clickCommentsForStudentsPanelHeading() {
        WebElement e = browser.driver.findElement(By.cssSelector("div[id='panel_display-1']"));

        click(e.findElement(By.cssSelector(".panel-heading")));
    }

    /**
     * Clicks all the headings of the comment panel to either expand/collapse the panel body.
     */
    public void clickAllCommentsPanelHeading() {
        for (WebElement e : browser.driver.findElements(By.cssSelector("div[id^='panel_display-']"))) {
            click(e.findElement(By.cssSelector(".panel-heading")));
        }
    }

    /**
     * Checks if the body of all the comment panels are visible.
     * @return true if all comment panel body are visible
     */
    public boolean areCommentsVisible() {
        return isAllCommentPanelBodyVisibilityEquals(true);
    }

    /**
     * Checks if the body of all the comment panels are hidden.
     * @return true if all comment panel body are hidden
     */
    public boolean areCommentsHidden() {
        return isAllCommentPanelBodyVisibilityEquals(false);
    }

    /**
     * Checks if the body of all the comment panels are collapsed or expanded.
     * @param isVisible true to check for expanded, false to check for collapsed.
     * @return true if all comment panel body are equals to the visibility being checked.
     */
    private boolean isAllCommentPanelBodyVisibilityEquals(boolean isVisible) {
        By panelCollapseSelector = By.cssSelector(".panel-heading+.panel-collapse");
        List<WebElement> webElements = browser.driver.findElements(panelCollapseSelector);

        for (WebElement e : webElements) {
            if (e.isDisplayed() != isVisible) {
                return false;
            }
        }

        return true;
    }

    public boolean isStudentCommentsPanelBodyVisible() {
        return getStudentCommentsPanelBody().isDisplayed();
    }

    private WebElement getStudentCommentsPanelBody() {
        return browser.driver.findElement(By.cssSelector(".student-comments-panel .panel-body"));
    }

    /**
     * Waits for all the panels to collapse.
     */
    public void waitForPanelsToCollapse() {
        By panelCollapseSelector = By.cssSelector(".panel-heading+.panel-collapse");

        waitForElementsToDisappear(browser.driver.findElements(panelCollapseSelector));
    }

    /**
     * Waits for 'comments for students' panel to collapse.
     */
    public void waitForCommentsForStudentsPanelsToCollapse() {
        By panelCollapseSelector = By.cssSelector("#panel_display-1 .panel-heading+.panel-collapse");
        waitForElementToDisappear(panelCollapseSelector);
    }

    /**
     * Waits for all the panels to expand.
     */
    public void waitForPanelsToExpand() {
        By panelCollapseSelector = By.cssSelector(".panel-heading+.panel-collapse");
        List<WebElement> webElements = browser.driver.findElements(panelCollapseSelector);

        waitForElementsVisibility(webElements);
    }

    /**
     * Waits for CommentsForStudents the panels to expand.
     */
    public void waitForCommentsForStudentsPanelsToExpand() {
        By panelCollapseSelector = By.cssSelector("div[id='panel_display-1']");
        List<WebElement> webElements = browser.driver.findElements(panelCollapseSelector);

        waitForElementsVisibility(webElements);
    }

    public void verifyCommentFormErrorMessage(String commentTableIdSuffix, String errorMessage) {
        int idNumber = commentTableIdSuffix.split("-").length;
        assertTrue(idNumber == 4 || idNumber == 3);
        String commentRowId = (idNumber == 4 ? "responseCommentEditForm-"
                                             : "showResponseCommentAddForm-")
                              + commentTableIdSuffix;
        waitForPageToLoad();
        By errorSpan = By.cssSelector("#" + commentRowId + " .col-sm-offset-5 > span");
        waitForTextContainedInElementPresence(errorSpan, errorMessage);
    }

    public void search(String text) {
        WebElement searchBox = browser.driver.findElement(By.id("searchBox"));
        waitForElementPresence(By.id("searchBox"));
        searchBox.sendKeys(text);
        waitForElementPresence(By.id("buttonSearch"));
        click(browser.driver.findElement(By.id("buttonSearch")));
    }
}
