package teammates.e2e.pageobjects;

/**
 * Page Object Model for student profile page in development server.
 */
public class StudentProfilePage extends AppPageNew {

    public StudentProfilePage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageTitle().contains("Student Profile");
    }

}
