package teammates.e2e.pageobjects;

/**
 * Page Object Model for student help page in development server.
 */
public class StudentHelpPage extends AppPageNew {

    public StudentHelpPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageTitle().contains("Help for Students");
    }

}
