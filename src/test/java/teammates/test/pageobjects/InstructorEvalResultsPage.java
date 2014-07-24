package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertTrue;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;


public class InstructorEvalResultsPage extends AppPage {
    
    @FindBy (id = "button_sortname")
    private WebElement sortByNameIcon;
    
    @FindBy (id = "button_sortclaimed")
    private WebElement sortByClaimedIcon;
    
    @FindBy (id = "button_sortperceived")
    private WebElement sortByPerceivedIcon;
    
    @FindBy (id = "button_sortdiff")
    private WebElement sortByDiffIcon;
    
    @FindBy (id = "button_sortteamname")
    private WebElement sortByTeamIcon;
    
    @FindBy (id = "radio_summary")
    private WebElement summaryRadioButton;
    
    @FindBy (id = "radio_reviewer")
    private WebElement detailsByReviewerRadioButton;
    
    @FindBy (id = "radio_reviewee")
    private WebElement detailsByRevieweeRadioButton;
    
    @FindBy (id = "button_unpublish")
    private WebElement unpublishButton;
    
    @FindBy (id = "button_publish")
    private WebElement publishButton;
    
    @FindBy (id = "interpret_help_link")
    public WebElement interpretHelpLink;
    

    public InstructorEvalResultsPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Evaluation Results</h1>");
    }
    
    public void verifyIsCorrectPage() {
        assertTrue(containsExpectedPageContents());
    }
    
    public AppPage sortByName() {
        sortByNameIcon.click();
        return this;
    }

    public AppPage sortByClaimed() {
        sortByClaimedIcon.click();
        return this;
    }

    public AppPage sortByPerceived() {
        sortByPerceivedIcon.click();
        return this;
    }
    
    public AppPage sortByDiff() {
        sortByDiffIcon.click();
        return this;
    }

    public AppPage sortByTeam() {
        sortByTeamIcon.click();
        return this;
    }

    public InstructorEvalResultsPage showSummary() {
        summaryRadioButton.click();
        return this;
    }

    public AppPage showDetailsByReviewer() {
        detailsByReviewerRadioButton.click();
        return this;
    }

    public AppPage showDetailsByReviewee() {
        detailsByRevieweeRadioButton.click();
        return this;
    }

    public AppPage unpublishAndCancel() {
        clickAndCancel(unpublishButton);
        return this;
    }

    public InstructorFeedbacksPage unpublishAndConfirm() {
        clickAndConfirm(unpublishButton);
        return changePageType(InstructorFeedbacksPage.class);
    }
    
    public AppPage publishAndCancel() {
        clickAndCancel(publishButton);
        return this;
    }
    
    public InstructorFeedbacksPage publishAndConfirm() {
        clickAndConfirm(publishButton);
        return changePageType(InstructorFeedbacksPage.class);
    }

    public InstructorEvalSubmissionEditPage clickEditLinkForStudent(String studentName) {
        int rowId = getRowIdForReviewee(studentName);
        browser.driver.findElement(By.id("editEvaluationResults" + rowId)).click();
        waitForPageToLoad();
        switchToNewWindow();
        return changePageType(InstructorEvalSubmissionEditPage.class);
    }
    
    public InstructorEvalSubmissionViewPage clickViewLinkForStudent(String studentName) {
        int rowId = getRowIdForReviewee(studentName);
        browser.driver.findElement(By.id("viewEvaluationResults" + rowId)).click();
        waitForPageToLoad();
        switchToNewWindow();
        return changePageType(InstructorEvalSubmissionViewPage.class);
    }

    public InstructorHelpPage clickInterpretHelpLink() {
        interpretHelpLink.click();
        waitForPageToLoad();
        switchToNewWindow();
        return changePageType(InstructorHelpPage.class);
    }

    private int getRowIdForReviewee(String studentName) {
        int studentCount =  browser.driver.findElements(By.className("student_row")).size();
        
        for (int i = 0; i < studentCount; i++) {
            if (browser.driver.findElement(By.id("student" + i)).getText()
                    .contains(studentName)) {
                return i;
            }
        }
        return -1;
    }

}
