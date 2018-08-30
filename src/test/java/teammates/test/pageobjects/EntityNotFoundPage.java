package teammates.test.pageobjects;

public class EntityNotFoundPage extends AppPage {

    public EntityNotFoundPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("TEAMMATES could not locate what you were trying to access.");
    }

}
