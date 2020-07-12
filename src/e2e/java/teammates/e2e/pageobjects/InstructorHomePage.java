package teammates.e2e.pageobjects;

/**
 * Represents the instructor home page.
 */
public class InstructorHomePage extends AppPage {
    public InstructorHomePage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageTitle().contains("Home");
    }
}
