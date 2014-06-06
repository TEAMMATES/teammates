package teammates.test.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class StudentEvalResultsPage extends AppPage {

    public StudentEvalResultsPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Evaluation Results</h1>");
    }
    
    public void clickResultInterpretLink(){
        
        WebElement interpretLink = browser.driver.findElement(By.linkText("How do I interpret these results?"));
        interpretLink.click();
    }
    
    public void clickcalculationDetaislLink(){
        
        WebElement DetailsLink = browser.driver.findElement(By.linkText("here"));
        DetailsLink.click();
    }
}
