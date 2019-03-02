package teammates.e2e.pageobjects;

/**
 * The student help page for the app to interact and validate with.
 */
public class StudentHelpPageNew extends AppPageNew {

    public StudentHelpPageNew(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageTitle().contains("Help for Students");
    }

}
