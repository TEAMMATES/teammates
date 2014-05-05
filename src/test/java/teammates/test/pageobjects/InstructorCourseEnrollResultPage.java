package teammates.test.pageobjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class InstructorCourseEnrollResultPage extends AppPage {
    
    @FindBy(id = "edit_enroll")
    WebElement editLink;

    public InstructorCourseEnrollResultPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Enrollment Results for");
    }
    
    public InstructorCourseEnrollPage clickEditLink(){
        editLink.click();
        waitForPageToLoad();
        return changePageType(InstructorCourseEnrollPage.class);
    }

}
