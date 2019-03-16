package teammates.e2e.pageobjects;

/**
 * Represents the admin home page of the website.
 * TODO: migrate with admin home page
 */
public class AdminHomePage extends AppPage {

    public AdminHomePage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        // return getPageSource().contains("<h1>Add New Instructor</h1>");
        return true;
    }
}
