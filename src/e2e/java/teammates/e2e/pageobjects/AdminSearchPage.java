package teammates.e2e.pageobjects;

import static org.junit.Assert.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Represents the admin search page of the website.
 */
public class AdminSearchPage extends AppPage {

    @FindBy(id = "filter-query")
    private WebElement filterQueryTextBox;

    @FindBy (id = "search-submit")
    private WebElement searchButton;

    @FindBy(tagName = "tm-regenerate-links-confirm-modal")
    private WebElement regenerateLinksModal;

    @FindBy(className = "snackbar")
    private WebElement successStatusMessage;

    public AdminSearchPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("Admin Search</h1>");
    }

    public void search(String searchQuery) {
        if (searchQuery != null) {
            fillTextBox(filterQueryTextBox, searchQuery);
        }

        click(searchButton);
        waitForPageToLoad();
    }

    public void regenerateLinksForStudent(int i) {
        WebElement regenerateLinksButton = browser.driver.findElement(By.id("regenerate-links-" + i));

        click(regenerateLinksButton);
        waitForPageToLoad();
        click(regenerateLinksModal.findElement(By.className("btn-warning")));
        waitForPageToLoad();
    }

    public void verifyStatusMessage(String message) {
        assertTrue(successStatusMessage.getText().contains(message));
    }

    public String getCourseJoinLinkForStudent(int i) {
        click(browser.driver.findElement(By.id("student-card-" + i)));
        waitForPageToLoad();
        String courseJoinLink = browser.driver.findElement(By.id("course-join-" + i)).getAttribute("value");
        click(browser.driver.findElement(By.id("student-card-" + i)));
        waitForPageToLoad();

        return courseJoinLink;
    }

}
