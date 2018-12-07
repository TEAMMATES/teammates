package teammates.test.pageobjects;

public class StudentCourseDetailsPage extends AppPage {

    public StudentCourseDetailsPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        // Intentional check for opening h1 and not closing h1 because the following content is not static
        return getPageSource().contains("<h1>Team Details for");
    }

}
