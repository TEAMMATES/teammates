package teammates.e2e.pageobjects;

/**
 * Page Object Model for student profile page.
 */
public class StudentProfilePage extends AppPage {

    public StudentProfilePage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageTitle().contains("Student Profile");
    }

}
