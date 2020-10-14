package teammates.e2e.pageobjects;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page Object Model for the admin sessions page.
 */
public class AdminSessionsPage extends AppPage {

    @FindBy(id = "ongoing-sessions-table")
    private WebElement ongoingSessionsTable;

    public AdminSessionsPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("Ongoing Sessions");
    }

    public List<WebElement> getOngoingSessionsRows() {
        return ongoingSessionsTable.findElement(By.tagName("tbody")).findElements(By.tagName("tr"));
    }

    public void verifySessionRow(WebElement sessionRow, String[] expectedRowValues) {
        verifyTableRowValues(sessionRow, expectedRowValues);
    }

}
