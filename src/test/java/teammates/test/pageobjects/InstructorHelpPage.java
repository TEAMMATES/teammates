package teammates.test.pageobjects;

public class InstructorHelpPage extends AppPage {

    public InstructorHelpPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1 class=\"HelpHeading\">Help for Instructors</h1>");
    }

}
