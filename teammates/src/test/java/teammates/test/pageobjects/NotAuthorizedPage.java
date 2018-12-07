package teammates.test.pageobjects;

public class NotAuthorizedPage extends AppPage {

    public NotAuthorizedPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("You are not authorized to view this page.");
    }

}
