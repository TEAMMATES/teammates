package teammates.test.pageobjects;

import static org.testng.AssertJUnit.assertTrue;

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
    
    public String getPersonInfoOfFirstEntry(){
        
        WebElement table = browser.driver.findElement(By.id("logsTable"));
        WebElement tableRow = table.findElements(By.tagName("tr")).get(1);
        WebElement element = tableRow.findElement(By.tagName("small"));
        WebElement hiddenInput = element.findElement(By.name("filterQuery"));
        return hiddenInput.getAttribute("value");
    }
    
    public void clickViewActionsButtonOfSecondEntry() {
        WebElement table = browser.driver.findElement(By.id("logsTable"));
        List<WebElement> tableEntries = table.findElements(By.tagName("tr"));
        WebElement tableRow;
        if (tableEntries.size() == 1) {
            tableRow = tableEntries.get(0);
        } else {
            tableRow = tableEntries.get(1);
        }
        WebElement element = tableRow.findElement(By.tagName("button"));
        element.click();
    }
    
    public String getFilterBoxString(){
        
        WebElement element = browser.driver.findElement(By.id("filterQuery"));
        return element.getAttribute("value");
    }
    
    public void fillQueryBoxWithText(String query){
        
        WebElement element = browser.driver.findElement(By.id("filterQuery"));
        fillTextBox(element, query);
    }
    
    public void clickSearchSubmitButton(){
        
        WebElement button = browser.driver.findElement(By.name("search_submit"));
        button.click();      
    }
    
    public String getQueryMessage(){
        
        WebElement alert = browser.driver.findElement(By.id("queryMessage"));
        return alert.getText();
    }
}
