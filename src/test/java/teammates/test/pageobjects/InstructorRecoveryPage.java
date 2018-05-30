package teammates.test.pageobjects;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/** Represents the "Recovery" page for Instructors. */
public class InstructorRecoveryPage extends AppPage {

    @FindBy (id = "recovery_btn_sortcoursename")
    private WebElement sortByCourseNameIcon;

    @FindBy (id = "recovery_btn_sortcourseid")
    private WebElement sortByCourseIdIcon;

    @FindBy (id = "recovery_btn_sortcreationdate")
    private WebElement sortByCreationDateIcon;

    @FindBy (id = "recovery_btn_sortdeletiondate")
    private WebElement sortByDeletionDateIcon;

    @FindBy(id = "recovery_btn_restoreallcourses")
    private WebElement restoreAllCoursesButton;

    @FindBy(id = "recovery_btn_deleteallcourses")
    private WebElement deleteAllCoursesButton;

    public InstructorRecoveryPage(Browser browser) {
        super(browser);
    }

    /** Used to check if the loaded page is indeed the 'Recovery' page. */
    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Recycle Bin</h1>");
    }

}
