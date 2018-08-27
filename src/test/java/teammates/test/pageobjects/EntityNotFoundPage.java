package teammates.test.pageobjects;

import teammates.e2e.pageobjects.Browser;

public class EntityNotFoundPage extends AppPage {

    public EntityNotFoundPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("TEAMMATES could not locate what you were trying to access.");
    }

}
