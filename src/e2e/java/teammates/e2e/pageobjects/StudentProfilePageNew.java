package teammates.e2e.pageobjects;

/**
 * The student profile page for the app to interact and validate with.
 */
public class StudentProfilePageNew extends AppPageNew {

    public StudentProfilePageNew(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageTitle().contains("Student Profile");
    }

}
