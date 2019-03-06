package teammates.e2e.pageobjects;

/**
 * Page Object Model for student help page.
 */
public class StudentHelpPage extends AppPage {

    public StudentHelpPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageTitle().contains("Help for Students");
    }

}
