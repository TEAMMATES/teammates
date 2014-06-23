package teammates.test.pageobjects;

public class InstructorCommentsPage extends AppPage {

    public InstructorCommentsPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Comments from Instructors</h1>");
    }

}
