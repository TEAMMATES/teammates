package teammates.test.pageobjects;

public class NotFoundPage extends AppPage {

    public NotFoundPage(final Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("The page you are looking for is not there.");
    }

}
