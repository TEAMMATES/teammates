package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertTrue;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class AdminActivityLogPage extends AppPage {

    public AdminActivityLogPage(Browser browser) {
        super(browser);
  
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Admin Activity Log</h1>");
    }
    
    public void verifyIsCorrectPage() {
        assertTrue(containsExpectedPageContents());
    }
    
    public String getPersonInfoOfFirstEntry() {
        
        WebElement table = browser.driver.findElement(By.id("logsTable"));
        WebElement tableRow = table.findElements(By.tagName("tr")).get(1);
        WebElement element = tableRow.findElement(By.tagName("small"));
        WebElement hiddenInput = element.findElement(By.name("filterQuery"));
        return hiddenInput.getAttribute("value");
    }
    
    public void clickViewActionsButtonOfFirstEntry() {
        
        WebElement table = browser.driver.findElement(By.id("logsTable"));
        WebElement tableRow = table.findElements(By.tagName("tr")).get(1);
        WebElement element = tableRow.findElement(By.tagName("button"));
        click(element);
    }
    
    public String getFilterBoxString() {
        
        WebElement element = browser.driver.findElement(By.id("filterQuery"));
        return element.getAttribute("value");
    }
    
    public void fillQueryBoxWithText(String query) {
        
        WebElement element = browser.driver.findElement(By.id("filterQuery"));
        fillTextBox(element, query);
    }
    
    public void clickSearchSubmitButton() {
        
        WebElement button = browser.driver.findElement(By.name("search_submit"));
        click(button);
    }
    
    public boolean isLogsTableVisible() {
        WebElement table = getLogsTable();
        if (table == null) {
            return false;
        }
        return table.isDisplayed();
    }
    
    public WebElement getLogsTable() {
        List<WebElement> list = browser.driver.findElements(By.id("logsTable"));
        if (list.isEmpty()) {
            return null;
        }
        
        return browser.driver.findElement(By.id("logsTable"));
    }
    
    public int getNumberOfTableHeaders() {
        if (isLogsTableVisible()) {
            List<WebElement> headerList = browser.driver.findElements(By.cssSelector("#logsTable > thead > tr > th"));
            return headerList.size();
        }
        return 0;
    }
    
    public String getQueryMessage() {
        
        WebElement alert = browser.driver.findElement(By.id("queryMessage"));
        return alert.getText();
    }
    
    public void clickReferenceButton() {
        WebElement button = browser.driver.findElement(By.id("referenceText"));
        click(button);
    }
    
    public void clickUserTimezoneAtFirstRow() {
        WebElement button = browser.driver.findElement(By.cssSelector("#first-row > td > a"));
        click(button);
    }
    
    public boolean isFilterReferenceVisible() {
        WebElement reference = browser.driver.findElement(By.id("filterReference"));
        return reference.isDisplayed();
    }
    
    public By getFirstActivityLogRow() {
        return By.id("first-row");
    }
    
    public boolean isUserTimezoneAtFirstRowClicked() {
        List<WebElement> elements = browser.driver.findElements(By.cssSelector("#first-row > td > mark"));
        return !elements.isEmpty();
    }

    public Date getDateOfEarliestLog() throws ParseException {
        String dateFormat = "dd-MM-yyyy HH:mm:ss";
        DateFormat sdf = new SimpleDateFormat(dateFormat);
        String dateTimeString = getLogsTable().findElement(By.cssSelector("tr:last-child > td > a")).getText();
        
        return sdf.parse(dateTimeString);
        
    }
}
