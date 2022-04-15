package teammates.e2e.pageobjects;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import teammates.common.datatransfer.attributes.NotificationAttributes;

/**
 * Page Object Model for page that can display notification banners.
 */
public class PageWithNotificationBanner extends AppPage {
    @FindBy(tagName = "tm-notification-banner")
    private WebElement bannerContainer;

    @FindBy(id = "btn-close-notif")
    private WebElement closeNotifButton;

    @FindBy(id = "btn-mark-as-read")
    private WebElement markNotifAsReadButton;

    public PageWithNotificationBanner(Browser browser) {
        super(browser);
    }

    @Override
    protected boolean containsExpectedPageContents() {
        // Notification Banner can appear on any page except for the past notification page
        String pageTitle = getPageTitle();
        return !"Instructor Notifications".equals(pageTitle) || !"Student Notifications".equals(pageTitle);
    }

    public void verifyNotificationBannerIsNotVisible() {
        List<WebElement> bannerContent = bannerContainer.findElements(By.id("banner-contents"));
        assertEquals(0, bannerContent.size());
    }

    public void verifyNotificationBannerIsVisible(NotificationAttributes notification) {
        List<WebElement> bannerContent = bannerContainer.findElements(By.id("banner-contents"));
        String title = bannerContainer.findElement(By.tagName("h5")).getText();
        String message = bannerContainer.findElement(By.className("banner-text")).getText();

        assertEquals(1, bannerContent.size());
        assertEquals(notification.getTitle(), title);
        assertEquals(removeParagraphTag(notification.getMessage()), message);
    }

    public String removeParagraphTag(String message) {
        return message.replace("<p>", "").replace("</p>", "");
    }

    public void clickCloseNotificationBannerButton() {
        waitForElementToBeClickable(closeNotifButton);
        click(closeNotifButton);
        waitUntilAnimationFinish();
    }

    public void clickMarkAsReadButton() {
        waitForElementToBeClickable(markNotifAsReadButton);
        click(markNotifAsReadButton);
        waitUntilAnimationFinish();
    }

    public void clickHomePageNavLink() {
        waitForPageToLoad();
        click(By.linkText("Home"));
        waitUntilAnimationFinish();
    }

    public void clickNotificationPageNavLink() {
        waitForPageToLoad();
        click(By.linkText("Notifications"));
        waitUntilAnimationFinish();
    }

    public void clickHelpPageNavLink() {
        waitForPageToLoad();
        click(By.linkText("Help"));
        waitUntilAnimationFinish();
    }
}
