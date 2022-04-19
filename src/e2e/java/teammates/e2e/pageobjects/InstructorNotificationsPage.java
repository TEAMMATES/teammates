package teammates.e2e.pageobjects;

/**
 * Page Object Model for instructor notifications page.
 */
public class InstructorNotificationsPage extends UserNotificationsPage {

    public InstructorNotificationsPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("Instructor Notifications");
    }

}
