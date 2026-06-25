package teammates.e2e.pageobjects;

/**
 * Represents the admin home page of the website.
 */
public class AdminHomePage extends AppPage {

    public AdminHomePage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("Admin Home Page");
    }
}
