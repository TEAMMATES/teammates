package teammates.test.pageobjects;

import teammates.e2e.pageobjects.Browser;

public class StudentHelpPage extends AppPage {

    public StudentHelpPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Help for Students</h1>");
    }

}
