package teammates.e2e.pageobjects;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Page Object Model for student home page.
 */
public class StudentHomePage extends AppPage {

    public StudentHomePage(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        return "Student Home".equals(getPageTitle());
    }

    private List<WebElement> getStudentHomeCoursePanels() {
        return browser.driver.findElements(By.cssSelector("div.card.bg-light"));
    }

    public int getStudentHomeCoursePanelIndex(String courseName) {
        List<WebElement> coursePanels = getStudentHomeCoursePanels();
        int coursePanelIndex = -1;
        for (int i = 0; i < coursePanels.size(); i++) {
            if (coursePanels.get(i).getText().contains(courseName)) {
                coursePanelIndex = i;
            }
        }
        assertTrue(coursePanelIndex >= 0);
        return coursePanelIndex;
    }

    public void verifyVisibleFeedbackSessionToStudents(String feedbackSessionName, int index) {
        assertTrue(getStudentHomeCoursePanels().get(index)
                .findElement(By.cssSelector("div.table-responsive table.table tbody")).getText()
                .contains(feedbackSessionName));
    }

    public void clickCloseNotificationBannerButton() {
        WebElement closeNotifButton = browser.driver.findElement(By.id("btn-close-notif"));
        waitForElementToBeClickable(closeNotifButton);
        click(closeNotifButton);
        waitUntilAnimationFinish();
    }

    public void clickMarkAsReadButton() {
        WebElement markNotifAsReadButton = browser.driver.findElement(By.id("btn-mark-as-read"));
        waitForElementToBeClickable(markNotifAsReadButton);
        click(markNotifAsReadButton);
        waitUntilAnimationFinish();
    }

    public String getNotificationId() {
        WebElement notificationBanner = browser.driver.findElement(By.id("notification-banner"));
        return notificationBanner.getAttribute("data-testid");
    }

}
