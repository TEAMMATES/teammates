package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import teammates.common.util.Const;
import teammates.common.util.ThreadHelper;
import teammates.test.driver.AssertHelper;

public class InstructorFeedbackResultsPage extends AppPage {

    @FindBy(id = "button_sortFromName")
    public WebElement sortTableGiverButton;
    
    @FindBy(id = "button_sortToName")
    private WebElement sortTableRecipientButton;
    
    @FindBy(id = "button_sortFeedback")
    private WebElement sortTableAnswerButton;
    
    @FindBy(id = "collapse-panels-button")
    public WebElement collapseExpandButton;
    
    @FindBy(id = "show-stats-checkbox")
    public WebElement showStatsCheckbox;
    
    @FindBy(id = "button_add_comment")
    private WebElement showResponseCommentAddFormButton;
    
    @FindBy(id = "showResponseCommentAddForm-0-1-1")
    private WebElement addResponseCommentForm;
    
    
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
    
    public boolean isCorrectPage (String courseId, String feedbackSessionName) {
        boolean isCorrectCourseId = this.getCourseId().equals(courseId);
        boolean isCorrectFeedbackSessionName = this.getFeedbackSessionName().equals(feedbackSessionName);
        return isCorrectCourseId && isCorrectFeedbackSessionName && containsExpectedPageContents();
    }
    
    public void displayByGiverRecipientQuestion() {
        Select select = new Select(browser.driver.findElement(By.name(Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE)));
        select.selectByVisibleText("Group by - Giver > Recipient > Question");
        waitForPageToLoad();
    }
    
    public void displayByRecipientGiverQuestion() {
        Select select = new Select(browser.driver.findElement(By.name(Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE)));
        select.selectByVisibleText("Group by - Recipient > Giver > Question");
        waitForPageToLoad();
    }
    
    public void displayByGiverQuestionRecipient() {
        Select select = new Select(browser.driver.findElement(By.name(Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE)));
        select.selectByVisibleText("Group by - Giver > Question > Recipient");
        waitForPageToLoad();
    }
    
    public void displayByRecipientQuestionGiver() {
        Select select = new Select(browser.driver.findElement(By.name(Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE)));
        select.selectByVisibleText("Group by - Recipient > Question > Giver");
        waitForPageToLoad();
    }

    public void filterResponsesForSection(String section) {
        Select select = new Select(browser.driver.findElement(By.name(Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTION)));
        select.selectByVisibleText(section);
        waitForPageToLoad();
    }

    public void filterResponsesForAllSections() {
        Select select = new Select(browser.driver.findElement(By.name(Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYSECTION)));
        select.selectByVisibleText("All");
        waitForPageToLoad();
    }
    
    public void displayByQuestion() {
        Select select = new Select(browser.driver.findElement(By.name(Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE)));
        select.selectByVisibleText("Group by - Question");
        waitForPageToLoad();
    }
    
    public void clickGroupByTeam() {
        WebElement button = browser.driver.findElement(By.name(Const.ParamsNames.FEEDBACK_RESULTS_GROUPBYTEAM));
        button.click();
        waitForPageToLoad();
    }
    
    public void clickCollapseExpand() {
        collapseExpandButton.click();
    }
    
    public void clickShowStats() {
        showStatsCheckbox.click();
    }
    
    public void fillSearchBox(String s) {
        this.fillTextBox(browser.driver.findElement(By.id("results-search-box")), s);
    }
    
    public AppPage sortTableByGiver() {
        sortTableGiverButton.click();
        return this;
    }
    
    public AppPage sortTableByRecipient() {
        sortTableRecipientButton.click();
        return this;
    }
    
    public AppPage sortTableByAnswer() {
        sortTableAnswerButton.click();
        return this;
    }
    
    public InstructorFeedbackEditPage clickEditLink() {
        WebElement button = browser.driver.findElement(By.linkText("[Edit]"));
        button.click();
        waitForPageToLoad();
        return changePageType(InstructorFeedbackEditPage.class);
    }
    
    public boolean clickQuestionAdditionalInfoButton(int qnNumber, String additionalInfoId){
        WebElement qnAdditionalInfoButton = browser.driver.findElement(By.id("questionAdditionalInfoButton-" + qnNumber + "-" + additionalInfoId));    
        qnAdditionalInfoButton.click();
        // Check if links toggle properly.
        WebElement qnAdditionalInfo = browser.driver.findElement(By.id("questionAdditionalInfo-" + qnNumber + "-" + additionalInfoId));    
        return qnAdditionalInfo.isDisplayed();
    }
    
    public String getQuestionAdditionalInfoButtonText(int qnNumber, String additionalInfoId){
        WebElement qnAdditionalInfoButton = browser.driver.findElement(By.id("questionAdditionalInfoButton-" + qnNumber + "-" + additionalInfoId));    
        return qnAdditionalInfoButton.getText();
    }
    
    public void addFeedbackResponseComment(String commentText) {
        WebDriverWait wait = new WebDriverWait(browser.driver, 30);
        showResponseCommentAddFormButton.click();
        wait.until(ExpectedConditions.elementToBeClickable(addResponseCommentForm.findElement(By.tagName("textarea"))));
        fillTextBox(addResponseCommentForm.findElement(By.tagName("textarea")), commentText);
        addResponseCommentForm.findElement(By.className("col-sm-offset-5")).findElement(By.tagName("a")).click();
        ThreadHelper.waitFor(1000);
    }
    
    public void editFeedbackResponseComment(String commentIdSuffix, String newCommentText) {
        WebElement commentRow = browser.driver.findElement(By.id("responseCommentRow" + commentIdSuffix));
        commentRow.findElements(By.tagName("a")).get(1).click();
        
        WebElement commentEditForm = browser.driver.findElement(By.id("responseCommentEditForm" + commentIdSuffix));
        fillTextBox(commentEditForm.findElement(By.name("responsecommenttext")), newCommentText);
        commentEditForm.findElement(By.className("col-sm-offset-5")).findElement(By.tagName("a")).click();
        ThreadHelper.waitFor(1000);
   
    }
    
    public boolean verifyAllResultsPanelBodyVisibility(boolean visible){
        for(WebElement e : browser.driver.findElements(By.cssSelector(".panel-heading+.panel-body"))){
            if(e.isDisplayed() != visible){
                return false;
            }
        }
        return true;
    }
    
    public boolean verifyAllStatsVisibility(){
        for(WebElement e : browser.driver.findElements(By.className("resultStatistics"))){
            if(e.getCssValue("display").equals("none")){
                return false;
            }
        }
        return true;
    }
    
    public void deleteFeedbackResponseComment(String commentIdSuffix) {
        WebElement commentRow = browser.driver.findElement(By.id("responseCommentRow" + commentIdSuffix));
        commentRow.findElement(By.tagName("form"))
            .findElement(By.tagName("a")).click();
        ThreadHelper.waitFor(1500);
    }
    
    public void verifyCommentRowContent(String commentRowIdSuffix, String commentText, String giverName) {
        WebDriverWait wait = new WebDriverWait(browser.driver, 30);
        WebElement commentRow;
        try{
            commentRow = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("responseCommentRow" + commentRowIdSuffix)));
        } catch (TimeoutException e){
            return;
        }
        try {
            wait.until(ExpectedConditions.textToBePresentInElement(commentRow.findElement(By.id("plainCommentText" + commentRowIdSuffix)), commentText));
        } catch (TimeoutException e){
            fail("Not expected message");
        }
        
        try{
            assertTrue(commentRow.findElement(By.className("text-muted")).getText().contains(giverName));
        } catch (AssertionError e){
            assertTrue(commentRow.findElement(By.className("text-muted")).getText().contains("you"));
        }
    }
    
    public void verifyCommentFormErrorMessage(String commentTableIdSuffix, String errorMessage) {
        WebElement commentRow = browser.driver.findElement(By.id("responseCommentTable" + commentTableIdSuffix));
        assertEquals(errorMessage, commentRow.findElement(By.className("col-sm-offset-5")).findElement(By.tagName("span")).getText());
    }
    
    public void verifyRowMissing(String rowIdSuffix) {
        try {
            waitForElementToDisappear(By.cssSelector("img[src='/images/ajax-loader.gif']"));
            browser.driver.findElement(By.id("responseCommentRow" + rowIdSuffix));
            fail("Row expected to be missing found.");
        } catch (NoSuchElementException e) {
            // row expected to be missing
        }
    }
    
    public void clickAjaxPanel(int index){
        List<WebElement> ajaxPanels = browser.driver.findElements(By.cssSelector(".ajax_submit"));
        ajaxPanels.get(index).click();
    }

    public void clickCollapseSectionButton(int index){
        this.waitForElementPresence(By.id("collapse-panels-button-section-" + index), 10);
        WebElement collapseButton = browser.driver.findElement(By.id("collapse-panels-button-section-" + index));
        collapseButton.click();
    }
    
    public void clickViewPhotoLink(int panelBodyIndex, String urlRegex) throws Exception {
        String idOfPanelBody = "panelBodyCollapse-" + panelBodyIndex;
        WebElement photoCell = browser.driver.findElement(By.id(idOfPanelBody))
                                             .findElements(By.cssSelector(".profile-pic-icon-click"))
                                             .get(0);
        WebElement photoLink = photoCell.findElement(By.tagName("a"));
        Actions actions = new Actions(browser.driver);
        
        actions.click(photoLink).build().perform();
        waitForElementToAppear(By.cssSelector(".popover-content > img"));
        
        AssertHelper.assertContainsRegex(urlRegex, 
                browser.driver.findElement(By.cssSelector(".popover-content > img"))
                              .getAttribute("src"));
    }

    public void hoverClickAndViewStudentPhotoOnHeading(int panelHeadingIndex, String urlRegex) throws Exception {
        String idOfPanelHeading = "panelHeading-" + panelHeadingIndex;
        WebElement photoDiv = browser.driver.findElement(By.id(idOfPanelHeading))
                                            .findElement(By.className("profile-pic-icon-hover"));
        Actions actions = new Actions(browser.driver);
        actions.moveToElement(photoDiv).build().perform();        
        waitForElementToAppear(By.cssSelector(".popover-content"));

        WebElement photoLink = browser.driver.findElement(By.cssSelector(".popover-content > a"));
        actions.click(photoLink).build().perform();
        
        waitForElementToAppear(By.cssSelector(".popover-content > img"));
        
        AssertHelper.assertContainsRegex(urlRegex, 
                browser.driver.findElement(By.cssSelector(".popover-content > img"))
                              .getAttribute("src"));
    }

    public void hoverAndViewStudentPhotoOnBody(int panelBodyIndex, String urlRegex) throws Exception {
        String idOfPanelBody = "panelBodyCollapse-" + panelBodyIndex;
        WebElement photoLink = browser.driver.findElements(By.cssSelector('#'+idOfPanelBody + "> .panel-body > .row"))
                                             .get(0)
                                             .findElements(By.className("profile-pic-icon-hover"))
                                             .get(0);
        Actions actions = new Actions(browser.driver);
        actions.moveToElement(photoLink).build().perform();
        
        waitForElementToAppear(By.cssSelector(".popover-content > img"));
        ThreadHelper.waitFor(500);
        
        AssertHelper.assertContainsRegex(urlRegex, 
                browser.driver.findElement(By.cssSelector(".popover-content > img"))
                              .getAttribute("src"));
    }
}
