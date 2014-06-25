package teammates.test.pageobjects;

public class StudentCommentsPage extends AppPage {

    public StudentCommentsPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h2>Student Comments</h2>");
    }

}
