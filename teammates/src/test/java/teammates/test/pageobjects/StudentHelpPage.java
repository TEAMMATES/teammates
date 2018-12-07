package teammates.test.pageobjects;

public class StudentHelpPage extends AppPage {

    public StudentHelpPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Help for Students</h1>");
    }

}
