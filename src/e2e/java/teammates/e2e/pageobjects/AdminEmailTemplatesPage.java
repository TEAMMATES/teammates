package teammates.e2e.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page Object Model for the admin email templates page.
 */
public class AdminEmailTemplatesPage extends AppPage {

    @FindBy(id = "template-selector")
    private WebElement templateSelector;

    @FindBy(id = "template-subject")
    private WebElement subjectInput;

    @FindBy(id = "template-body")
    private WebElement bodyTextarea;

    public AdminEmailTemplatesPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("Customize the emails that TEAMMATES sends");
    }

    /**
     * Selects a template by its registry key from the dropdown, then waits for
     * Angular's HTTP call and re-render to complete.
     */
    public void selectTemplate(String templateKey) {
        selectDropdownOptionByText(templateSelector, templateKey);
        waitForPageToLoad();
    }

    /**
     * Returns the current value displayed in the subject input.
     */
    public String getSubject() {
        return subjectInput.getAttribute("value");
    }

    /**
     * Replaces the subject input with the given value.
     */
    public void setSubject(String subject) {
        fillTextBox(subjectInput, subject);
    }

    /**
     * Returns the current value of the body textarea.
     */
    public String getBody() {
        return bodyTextarea.getAttribute("value");
    }

    /**
     * Replaces the body textarea with the given value.
     */
    public void setBody(String body) {
        fillTextBox(bodyTextarea, body);
    }

    /**
     * Returns true if the green "Custom Template Active" badge is visible.
     */
    public boolean isCustomBadgeVisible() {
        return !browser.driver.findElements(By.cssSelector("span.badge-success")).isEmpty();
    }

    /**
     * Returns true if the grey "Using Default Template" badge is visible.
     */
    public boolean isDefaultBadgeVisible() {
        return !browser.driver.findElements(By.cssSelector("span.badge-secondary")).isEmpty();
    }

    /**
     * Clicks "Save Template" and waits for Angular to stabilise, preserving
     * the toast so the caller can immediately call {@link #verifyStatusMessage}.
     */
    public void saveTemplate() {
        click(By.cssSelector("button.btn-primary"));
        waitForPageToLoad(true);
    }

    /**
     * Clicks "Revert to Default" and waits for Angular to stabilise, preserving
     * the toast so the caller can immediately call {@link #verifyStatusMessage}.
     */
    public void revertToDefault() {
        click(By.cssSelector("button.btn-outline-danger"));
        waitForPageToLoad(true);
    }
}
