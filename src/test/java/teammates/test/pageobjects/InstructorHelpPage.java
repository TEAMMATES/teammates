package teammates.test.pageobjects;


public class InstructorHelpPage extends AppPage {

    public InstructorHelpPage(final Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains(
                "TEAMMATES Online Peer Feedback System for Student Team Projects - Instructor Help");
    }

}
