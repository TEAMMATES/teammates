package teammates.test.pageobjects;

public class StudentHelpPage extends AppPage {

    public StudentHelpPage(final Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains(
                "Help for Students");
    }

}
