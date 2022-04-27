package teammates.e2e.pageobjects;

/**
 * Page Object Model for student notifications page.
 */
public class StudentNotificationsPage extends UserNotificationsPage {

    public StudentNotificationsPage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("Student Notifications");
    }

}
