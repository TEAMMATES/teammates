package teammates.e2e.pageobjects;

/**
 * Page Object Model for student home page.
 */
public class StudentHomePage extends AppPage {

    public StudentHomePage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageTitle().equals("Student Home");
    }

}
