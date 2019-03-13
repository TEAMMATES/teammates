package teammates.e2e.pageobjects;

/**
 * Page Object Model for not authorized page.
 */
public class NotAuthorizedPage extends AppPage {

    public NotAuthorizedPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("You are not authorized to view this page.");
    }

}
