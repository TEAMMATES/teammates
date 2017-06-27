package teammates.test.pageobjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * The page object for QUnit test result page.
 */
public class QUnitPage extends AppPage {

    @FindBy(className = "failed")
    private WebElement failedCase;

    @FindBy(className = "total")
    private WebElement totalCase;

    @FindBy(css = ".grand-total > .bl-cl:nth-child(2)")
    private WebElement coverage;

    public QUnitPage(Browser browser) {
        super(browser);
    }

    @Override
    public void waitForPageToLoad() {
        // This is not a web page and thus document.readyState is not relevant here.
        // Instead, wait for the number of test cases to appear.
        waitForElementVisibility(totalCase);
    }

    /**
     * Gets the number of failed test cases.
     */
    public int getFailedCases() {
        return Integer.parseInt(failedCase.getText());
    }

    /**
     * Gets the number of total test cases.
     */
    public int getTotalCases() {
        return Integer.parseInt(totalCase.getText());
    }

    /**
     * Gets the percentage of code coverage of the test run.
     */
    public float getCoverage() {
        return Float.parseFloat(coverage.getText().replace(" %", ""));
    }

    public void waitForCoverageVisibility() {
        waitForElementVisibility(coverage);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("QUnit Testing Result");
    }

}
