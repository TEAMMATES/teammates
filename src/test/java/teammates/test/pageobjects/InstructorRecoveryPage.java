package teammates.test.pageobjects;

/** Represents the "Recovery" page for Instructors. */
public class InstructorRecoveryPage extends AppPage {

    public InstructorRecoveryPage(Browser browser) {
        super(browser);
    }

    /** Used to check if the loaded page is indeed the 'Recovery' page. */
    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Recycle Bin</h1>");
    }

}
