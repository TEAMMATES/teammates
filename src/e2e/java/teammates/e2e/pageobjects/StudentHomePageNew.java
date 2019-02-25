package teammates.e2e.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class StudentHomePageNew extends AppPageNew {

    public StudentHomePageNew(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return containsExpectedPageContents(getPageSource());
    }

    public static boolean containsExpectedPageContents(String pageSource) {
        // TODO: method to be removed as getPageSource() only returns the header segment and ignores the body.
        return pageSource.contains("<title>TEAMMATES - Online Peer Feedback/Evaluation System for Student Team Projects</title>");
        // return pageSource.contains("<h1>Student Home</h1>");
    }

    public void clickViewTeam() {

        List<WebElement> viewTeamLinks = browser.driver.findElements(By.linkText("View Team"));

        click(viewTeamLinks.get(0));
    }

    public WebElement getViewFeedbackButton(String feedbackName) {

        int rowId = getEvalRowId(feedbackName);
        return browser.driver.findElement(By.id("viewFeedbackResults" + rowId));
    }

    public void clickViewFeedbackButton(String feedbackName) {
        click(getViewFeedbackButton(feedbackName));
    }

    public WebElement getEditFeedbackButton(String feedbackName) {

        int rowId = getEvalRowId(feedbackName);
        return browser.driver.findElement(By.id("editFeedbackResponses" + rowId));
    }

    public void clickEditFeedbackButton(String feedbackName) {
        click(getEditFeedbackButton(feedbackName));
    }

    public WebElement getSubmitFeedbackButton(String feedbackName) {

        int rowId = getEvalRowId(feedbackName);
        return browser.driver.findElement(By.id("submitFeedback" + rowId));
    }

    public void clickSubmitFeedbackButton(String feedbackName) {
        click(getSubmitFeedbackButton(feedbackName));
    }

    private int getEvalRowId(String name) {

        int id = 0;
        while (isElementPresent(By.id("evaluation" + id))) {

            WebElement element = browser.driver.findElement(By.id("evaluation" + id));
            WebElement text = element.findElement(By.tagName("td"));

            if (text.getText().contains(name)) {
                return id;
            }

            id++;
        }
        return -1;
    }

}
