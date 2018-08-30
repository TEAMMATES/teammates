package teammates.test.pageobjects;

import teammates.e2e.pageobjects.Browser;

public class FeedbackSessionNotVisiblePage extends AppPage {

    public FeedbackSessionNotVisiblePage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("Sorry, this session is currently not open for submission.");
    }

}
