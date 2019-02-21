package teammates.e2e.pageobjects;

public class StudentHelpPageNew extends AppPageNew {

    public StudentHelpPageNew(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("<h1>Help for Students</h1>");
    }

}
