package teammates.e2e.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Page Object Model for account request form page.
 */
public class RequestPage extends AppPage {

    @FindBy(id = "name")
    private WebElement nameBox;

    @FindBy(id = "institution")
    private WebElement institutionBox;

    @FindBy(id = "country")
    private WebElement countryBox;

    @FindBy(id = "email")
    private WebElement emailBox;

    @FindBy(id = "comments")
    private WebElement commentsBox;

    @FindBy(id = "submit-button")
    private WebElement submitButton;

    public RequestPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageTitle().contains("Create Courses with TEAMMATES");
    }

    public void fillForm(String name, String institution, String country, String email, String comments) {
        fillTextBox(nameBox, name);
        fillTextBox(institutionBox, institution);
        selectCountry(country);
        fillTextBox(emailBox, email);
        fillTextBox(commentsBox, comments);
    }

    private void selectCountry(String countryName) {
        click(countryBox);
        countryBox.sendKeys(countryName);
        By optionLocator = By.xpath(
                "//*[@data-testid='searchable-combobox-option' and normalize-space()='" + countryName + "']");
        click(waitFor(ExpectedConditions.elementToBeClickable(optionLocator)));
    }

    public void clickSubmitFormButton() {
        click(submitButton);
        waitForPageToLoad();
    }

    public void verifySubmittedInfo(String name, String institution, String country, String email, String comments) {
        WebElement table = browser.driver.findElement(By.className("table"));
        String[][] expected = {
                { name },
                { institution },
                { country },
                { email },
                { comments },
        };
        verifyTableBodyValues(table, expected);
    }
}
