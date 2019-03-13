package teammates.e2e.pageobjects;

/**
 * Page Object Model for entity not found page.
 */
public class EntityNotFoundPage extends AppPage {

    public EntityNotFoundPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("TEAMMATES could not locate what you were trying to access.");
    }

}
