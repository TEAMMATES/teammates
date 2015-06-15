package teammates.test.pageobjects;

public class StudentCourseDetailsPage extends AppPage {

    public StudentCourseDetailsPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("Team Details for");
    }

}
