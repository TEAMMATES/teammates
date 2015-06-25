package teammates.test.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;


public class InstructorCourseEnrollPage extends AppPage {
    
    @FindBy(id = "spreadsheet_download")
    protected WebElement spreadsheetLink;
    
    @FindBy(id = "enrollstudents")
    protected WebElement enrollTextBox;
    
    @FindBy(id = "button_enroll")
    protected WebElement enrollButton;

    public InstructorCourseEnrollPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        // Intentional check for opening h1 and not closing h1 because the following content is not static
        return getPageSource().contains("<h1>Enroll Students for");
    }

    public InstructorCourseEnrollPage verifyIsCorrectPage(String courseId){
        getPageSource().contains("Enroll Students for "+courseId);
        return this;
    }

    public String getCourseId() {
        return browser.driver.findElement(By.id("courseid")).getText();
    }
    
    public String getEnrollText() {
        return getTextBoxValue(enrollTextBox);
    }

    public String getSpreadsheetLink() {
        String link = spreadsheetLink.getAttribute("href");
        if (!link.startsWith("http"))
            return link;
        String[] tokens = link.split("/");
        String result = "";
        for (int i = 3; i < tokens.length; i++) {
            result += "/"+ tokens[i] ;
        }
        return result;
    }
    
    public InstructorCourseEnrollResultPage enroll(String enrollString) {
        fillTextBox(enrollTextBox, enrollString);
        enrollButton.click();
        waitForPageToLoad();
        return changePageType(InstructorCourseEnrollResultPage.class);
    }

    public InstructorCourseEnrollPage enrollUnsuccessfully(String enrollString) {
        fillTextBox(enrollTextBox, enrollString);
        enrollButton.click();
        waitForPageToLoad();
        return this;
    }

}
