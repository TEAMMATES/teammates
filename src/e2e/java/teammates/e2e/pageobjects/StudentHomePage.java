package teammates.e2e.pageobjects;

/**
 * Page Object Model for student home page in development server.
 */
public class StudentHomePage extends AppPageNew {

    public StudentHomePage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageTitle().equals("Student Home");
    }

}
