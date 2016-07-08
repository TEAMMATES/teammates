package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertTrue;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
        browser.driver.findElement(By.xpath(pathToSecondDisplayPanelHeading)).click();
        waitForPageToLoad();
        String pathToSecondDisplayPanelBodyInnerDiv = "//*[@id=\"panel_display-2\"]/div/div[2]";
        waitForElementVisibility(browser.driver.findElement(By.xpath(pathToSecondDisplayPanelBodyInnerDiv)));
    }

    public void clickSendEmailNotificationButton() {
        String pathToSendEmailNotificationButton = "//*[@id=\"mainContent\"]/div[4]/div[1]/div/a";
        browser.driver.findElement(By.xpath(pathToSendEmailNotificationButton)).click();
        waitForPageToLoad();
    }
    
    public void clickShowMoreOptions() {
        showMoreOptionsCheckbox.click();
        waitForPageToLoad();
    }
    
    public void clickIsIncludeArchivedCoursesCheckbox() {
        isIncludeArchivedCoursesCheckbox.click();
        waitForPageToLoad();
    }
    
    public void clickPreviousCourseLink() {
        getPreviousCourseLink().click();
        waitForPageToLoad();
    }
    
    public void clickNextCourseLink() {
        getNextCourseLink().click();
        waitForPageToLoad();
    }
    
    public void showCommentsForAll() {
        browser.driver.findElement(By.id("panel_all")).click();
    }
    
    public void showCommentsFromAll() {
        browser.driver.findElement(By.id("giver_all")).click();
    }
    
    public void showCommentsFromAllStatus() {
        browser.driver.findElement(By.id("status_all")).click();
    }
    
    public void showCommentsForPanel(int panelIdx) {
        browser.driver.findElement(By.id("panel_check-" + panelIdx)).click();
    }
    
    public void showCommentsFromGiver(String giverIdx) {
        browser.driver.findElement(By.id("giver_check-by-" + giverIdx)).click();
    }
    
    public void showCommentsForStatus(String status) {
        browser.driver.findElement(By.id("status_check-" + status)).click();
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
        JavascriptExecutor jsExecutor = (JavascriptExecutor) browser.driver;
        jsExecutor.executeScript("document.getElementById('" + "commentedit-" + i + "').click();");
    }
    
    public void clickStudentCommentVisibilityEdit(int row) {
        WebElement visibilityEditButton = browser.driver.findElement(By.id("visibility-options-trigger" + row));
        waitForElementVisibility(visibilityEditButton);
        visibilityEditButton.click();
    }
    
    public void clickResponseCommentVisibilityEdit(String suffix) {
        WebElement visibilityEditButton =
                browser.driver.findElement(By.id("frComment-visibility-options-trigger-" + suffix));
        waitForElementVisibility(visibilityEditButton);
        visibilityEditButton.click();
    }
    
    public void clickAllCheckboxes(int row) {
        List<WebElement> answerCheckboxes = browser.driver
                .findElement(By.id("visibility-options" + row))
                .findElements(By.className("answerCheckbox"));
        List<WebElement> checkboxes = answerCheckboxes;
        for (WebElement checkbox : checkboxes) {
            checkbox.click();
        }
    }
    
    public void clickAllCheckboxes(String suffix) {
        List<WebElement> answerCheckboxes = browser.driver
                .findElement(By.id("visibility-options-" + suffix))
                .findElements(By.className("answerCheckbox"));
        List<WebElement> checkboxes = answerCheckboxes;
        for (WebElement checkbox : checkboxes) {
            checkbox.click();
        }
    }
    
    public void clickAllGiverCheckboxes(int row) {
        List<WebElement> giverCheckboxes = browser.driver
                                           .findElement(By.id("visibility-options" + row))
                                           .findElements(By.className("giverCheckbox"));
        for (WebElement checkbox : giverCheckboxes) {
            checkbox.click();
        }
    }

    public void fillTextareaToEditStudentCommentForRow(int i, String text) {
        WebElement textarea = browser.driver.findElement(By.id("commentText" + i));
        textarea.click();
        textarea.clear();
        textarea.sendKeys(text);
    }
    
    public void saveEditStudentCommentForRow(int i) {
        browser.driver.findElement(By.id("commentsave-" + i)).click();
        waitForPageToLoad();
    }

    public void clickResponseCommentAdd(int sessionIdx, int questionIdx, int responseIdx) {
        waitForPageToLoad();
        browser.driver.findElement(
                By.id("button_add_comment-" + sessionIdx + "-" + questionIdx + "-" + responseIdx)).click();
        waitForPageToLoad();
    }

    public void fillTextareaToEditResponseComment(int sessionIdx, int questionIdx, int responseIdx, String text) {
        WebElement textarea = browser.driver.findElement(
                By.id("responseCommentAddForm-" + sessionIdx + "-" + questionIdx + "-" + responseIdx));
        textarea.click();
        textarea.clear();
        textarea.sendKeys(text);
    }
    
    public void fillTextareaToEditResponseComment(int sessionIdx, int questionIdx, int responseIdx,
                                                  int commentIdx, String text) {
        WebElement textarea = browser.driver.findElement(
                By.id("responsecommenttext-" + sessionIdx + "-" + questionIdx + "-" + responseIdx + "-" + commentIdx));
        textarea.click();
        textarea.clear();
        textarea.sendKeys(text);
    }

    public void addResponseComment(int sessionIdx, int questionIdx, int responseIdx) {
        browser.driver.findElement(
                By.id("button_save_comment_for_add-" + sessionIdx + "-" + questionIdx + "-" + responseIdx)).click();
        waitForPageToLoad();
    }

    public void clickResponseCommentEdit(int sessionIdx, int questionIdx, int responseIdx, int commentIdx) {
        waitForPageToLoad();
        JavascriptExecutor jsExecutor = (JavascriptExecutor) browser.driver;
        jsExecutor.executeScript("document.getElementById('"
                + "commentedit-" + sessionIdx + "-" + questionIdx + "-" + responseIdx + "-" + commentIdx + "').click();");
        waitForPageToLoad();
    }

    public void saveResponseComment(int sessionIdx, int questionIdx, int responseIdx, int commentIdx) {
        browser.driver.findElement(
                By.id("button_save_comment_for_edit-" + sessionIdx + "-"
                      + questionIdx + "-" + responseIdx + "-" + commentIdx)).click();
        waitForPageToLoad();
    }

    public void clickResponseCommentDelete(int sessionIdx, int questionIdx, int responseIdx, int commentIdx) {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) browser.driver;
        jsExecutor.executeScript("document.getElementById('"
                + "commentdelete-" + sessionIdx + "-" + questionIdx + "-" + responseIdx + "-" + commentIdx + "').click();");
        waitForConfirmationModalAndClickOk();
        waitForPageToLoad();
    }
    
    /**
     * Clicks 'Comments for students' panel heading of the comment panel to either expand/collapse the panel body.
     */
    public void clickCommentsForStudentsPanelHeading() {
        WebElement e = browser.driver.findElement(By.cssSelector("div[id='panel_display-1']"));

        e.findElement(By.cssSelector(".panel-heading")).click();
    }
    
    /**
     * Clicks all the headings of the comment panel to either expand/collapse the panel body.
     */
    public void clickAllCommentsPanelHeading() {
        for (WebElement e : browser.driver.findElements(By.cssSelector("div[id^='panel_display-']"))) {
            e.findElement(By.cssSelector(".panel-heading")).click();
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
     * Checks if the body of all the comment panels are hidden
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
        //This click somehow causes an error.
        //searchBox.click();
        this.waitForElementPresence(By.id("searchBox"));
        //searchBox.clear();
        this.waitForElementPresence(By.id("searchBox"));
        searchBox.sendKeys(text);
        this.waitForElementPresence(By.id("buttonSearch"));
        browser.driver.findElement(By.id("buttonSearch")).click();
    }
}
