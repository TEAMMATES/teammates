package teammates.e2e.pageobjects;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.remote.UselessFileDetector;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.util.TimeHelper;
import teammates.e2e.util.MaximumRetriesExceededException;
import teammates.e2e.util.RetryManager;
import teammates.e2e.util.Retryable;
import teammates.e2e.util.TestProperties;
import teammates.test.FileHelper;
import teammates.test.ThreadHelper;

/**
 * An abstract class that represents a browser-loaded page of the app and
 * provides ways to interact with it. Also contains methods to validate some
 * aspects of the page, e.g. HTML page source.
 *
 * <p>Note: We are using the Page Object pattern here.
 *
 * @see <a href="https://martinfowler.com/bliki/PageObject.html">https://martinfowler.com/bliki/PageObject.html</a>
 */
public abstract class AppPage {

    private static final String CLEAR_ELEMENT_SCRIPT;
    private static final String SCROLL_ELEMENT_TO_CENTER_AND_CLICK_SCRIPT;
    private static final String READ_TINYMCE_CONTENT_SCRIPT;
    private static final String WRITE_TO_TINYMCE_SCRIPT;

    static {
        try {
            CLEAR_ELEMENT_SCRIPT = FileHelper.readFile("src/e2e/resources/scripts/clearElementWithoutEvents.js");
            SCROLL_ELEMENT_TO_CENTER_AND_CLICK_SCRIPT = FileHelper
                    .readFile("src/e2e/resources/scripts/scrollElementToCenterAndClick.js");
            READ_TINYMCE_CONTENT_SCRIPT = FileHelper.readFile("src/e2e/resources/scripts/readTinyMCEContent.js");
            WRITE_TO_TINYMCE_SCRIPT = FileHelper.readFile("src/e2e/resources/scripts/writeToTinyMCE.js");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Browser instance the page is loaded into. */
    protected Browser browser;

    /** Use for retrying due to transient UI issues. */
    protected RetryManager uiRetryManager = new RetryManager((TestProperties.TEST_TIMEOUT + 1) / 2);

    /**
     * Used by subclasses to create a {@code AppPage} object to wrap around the
     * given {@code browser} object. Fails if the page content does not match
     * the page type, as defined by the sub-class.
     */
    public AppPage(Browser browser) {
        this.browser = browser;

        boolean isCorrectPageType;

        try {
            isCorrectPageType = containsExpectedPageContents();

            if (isCorrectPageType) {
                return;
            }
        } catch (Exception e) {
            // ignore and try again
        }

        // To minimize test failures due to eventual consistency, we try to
        //  reload the page and compare once more.
        System.out.println("#### Incorrect page type: going to try reloading the page.");

        ThreadHelper.waitFor(2000);

        reloadPage();

        isCorrectPageType = containsExpectedPageContents();

        if (isCorrectPageType) {
            return;
        }

        System.out.println("######### Not in the correct page! ##########");
        throw new IllegalStateException("Not in the correct page!");
    }

    /**
     * Gets a new page object representation of the currently open web page in the browser.
     *
     * <p>Fails if the new page content does not match content expected in a page of
     * the type indicated by the parameter {@code typeOfPage}.
     */
    public static <T extends AppPage> T getNewPageInstance(Browser currentBrowser, Class<T> typeOfPage) {
        waitUntilAnimationFinish(currentBrowser);
        try {
            Constructor<T> constructor = typeOfPage.getConstructor(Browser.class);
            T page = constructor.newInstance(currentBrowser);
            PageFactory.initElements(currentBrowser.driver, page);
            page.waitForPageToLoad();
            return page;
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof IllegalStateException) {
                throw (IllegalStateException) e.getCause();
            }
            throw new RuntimeException(e);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Fails if the new page content does not match content expected in a page of
     * the type indicated by the parameter {@code newPageType}.
     */
    public <T extends AppPage> T changePageType(Class<T> newPageType) {
        return getNewPageInstance(browser, newPageType);
    }

    public <E> E waitFor(ExpectedCondition<E> expectedCondition) {
        WebDriverWait wait = new WebDriverWait(browser.driver, Duration.ofSeconds(TestProperties.TEST_TIMEOUT));
        return wait.until(expectedCondition);
    }

    /**
     * Waits until the page is fully loaded.
     */
    public void waitForPageToLoad() {
        waitForPageToLoad(false);
    }

    /**
     * Waits until the page is fully loaded.
     *
     * @param excludeToast Set this to true if toast message's disappearance should not be counted
     *         as criteria for page load's completion.
     */
    public void waitForPageToLoad(boolean excludeToast) {
        browser.waitForPageLoad(excludeToast);
    }

    public void waitForElementVisibility(WebElement element) {
        waitFor(ExpectedConditions.visibilityOf(element));
    }

    public void waitForElementVisibility(By by) {
        waitFor(ExpectedConditions.visibilityOfElementLocated(by));
    }

    public void waitForElementToBeClickable(WebElement element) {
        waitFor(ExpectedConditions.elementToBeClickable(element));
    }

    public static void waitUntilAnimationFinish(Browser browser) {
        WebDriverWait wait = new WebDriverWait(browser.driver, Duration.ofSeconds(TestProperties.TEST_TIMEOUT));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("ng-animating")));
        ThreadHelper.waitFor(1000);
    }

    public void waitUntilAnimationFinish() {
        waitUntilAnimationFinish(browser);
    }

    /**
     * Waits until an element is no longer attached to the DOM or the timeout expires.
     * @param element the WebElement that expires after {@link TestProperties#TEST_TIMEOUT}
     * @see org.openqa.selenium.support.ui.FluentWait#until(java.util.function.Function)
     */
    public void waitForElementStaleness(WebElement element) {
        waitFor(ExpectedConditions.stalenessOf(element));
    }

    public void verifyUnclickable(WebElement element) {
        if ("a".equals(element.getTagName())) {
            assertTrue(element.getAttribute("class").contains("disabled"));
        } else {
            assertNotNull(element.getAttribute("disabled"));
        }
    }

    /**
     * Waits for a confirmation modal to appear and click the confirm button.
     */
    public void waitForConfirmationModalAndClickOk() {
        waitForModalShown();
        waitForElementVisibility(By.className("modal-btn-ok"));
        WebElement okayButton = browser.driver.findElement(By.className("modal-btn-ok"));
        waitForElementToBeClickable(okayButton);
        clickDismissModalButtonAndWaitForModalHidden(okayButton);
    }

    private void waitForModalShown() {
        // Possible exploration: Change to listening to modal shown event as
        // this is based on the implementation detail assumption that once modal-backdrop is added the modal is shown
        waitForElementVisibility(By.className("modal-backdrop"));
    }

    void waitForModalHidden(WebElement modalBackdrop) {
        // Possible exploration: Change to listening to modal hidden event as
        // this is based on the implementation detail assumption that once modal-backdrop is removed the modal is hidden
        waitForElementStaleness(modalBackdrop);
    }

    /**
     * Waits for the element to appear in the page, up to the timeout specified.
     */
    public WebElement waitForElementPresence(By by) {
        return waitFor(ExpectedConditions.presenceOfElementLocated(by));
    }

    public void reloadPage() {
        browser.goToUrl(browser.driver.getCurrentUrl());
        waitForPageToLoad();
    }

    protected Object executeScript(String script, Object... args) {
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) browser.driver;
        return javascriptExecutor.executeScript(script, args);
    }

    /**
     * Returns the HTML source of the currently loaded page.
     */
    public String getPageSource() {
        return browser.driver.getPageSource();
    }

    public String getTitle() {
        return browser.driver.getTitle();
    }

    public String getPageTitle() {
        return waitForElementPresence(By.tagName("h1")).getText();
    }

    public void click(By by) {
        WebElement element = browser.driver.findElement(by);
        click(element);
    }

    protected void click(WebElement element) {
        executeScript("arguments[0].click();", element);
    }

    /**
     * Simulates the clearing and sending of keys to an element.
     *
     * <p><b>Note:</b> This method is not the same as using {@link WebElement#clear} followed by {@link WebElement#sendKeys}.
     * It avoids double firing of the {@code change} event which may occur when {@link WebElement#clear} is followed by
     * {@link WebElement#sendKeys}.
     *
     * @see AppPage#clearWithoutEvents(WebElement)
     */
    private void clearAndSendKeys(WebElement element, CharSequence... keysToSend) {
        Map<String, Object> result = clearWithoutEvents(element);
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) result.get("errors");
        if (errors != null) {
            throw new InvalidElementStateException(errors.get("detail"));
        }

        element.sendKeys(keysToSend);
    }

    /**
     * Clears any kind of editable element, but without firing the {@code change} event (unlike {@link WebElement#clear()}).
     * Avoid using this method if {@link WebElement#clear()} meets the requirements as this method depends on implementation
     * details.
     */
    private Map<String, Object> clearWithoutEvents(WebElement element) {
        // This method is a close mirror of HtmlUnitWebElement#clear(), except that events are not handled. Note that
        // HtmlUnitWebElement is mirrored as opposed to RemoteWebElement (which is used with actual browsers) for convenience
        // and the implementation can differ.
        checkNotNull(element);

        // Adapted from ExpectedConditions#stalenessOf which forces a staleness check. This allows a meaningful
        // StaleElementReferenceException to be thrown rather than just getting a boolean from ExpectedConditions.
        element.isEnabled();

        // Fail safe in case the implementation of staleness checks is changed
        if (isExpectedCondition(ExpectedConditions.stalenessOf(element))) {
            throw new AssertionError(
                    "Element is stale but should have been caught earlier by element.isEnabled().");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) executeScript(CLEAR_ELEMENT_SCRIPT, element);
        return result;
    }

    protected void fillTextBox(WebElement textBoxElement, String value) {
        try {
            scrollElementToCenterAndClick(textBoxElement);
        } catch (WebDriverException e) {
            // It is important that a text box element is clickable before we fill it but due to legacy reasons we continue
            // attempting to fill the text box element even if it's not clickable (which may lead to an unexpected failure
            // later on)
            System.out.println("Unexpectedly not able to click on the text box element because of: ");
            System.out.println(e);
        }

        // If the intended value is empty `clear` works well enough for us
        if (value.isEmpty()) {
            textBoxElement.clear();
            return;
        }

        // Otherwise we need to do special handling of entering input because `clear` and `sendKeys` work differently.
        // See documentation for `clearAndSendKeys` for more details.
        clearAndSendKeys(textBoxElement, value);

        textBoxElement.sendKeys(Keys.TAB); // blur the element to receive events
    }

    protected void fillDatePicker(WebElement dateBox, Instant startInstant, String timeZone) {
        WebElement buttonToOpenPicker = dateBox.findElement(By.tagName("button"));
        click(buttonToOpenPicker);

        WebElement datePicker = dateBox.findElement(By.tagName("ngb-datepicker"));
        WebElement monthAndYearPicker = datePicker.findElement(By.tagName("ngb-datepicker-navigation-select"));
        WebElement monthPicker = monthAndYearPicker.findElement(By.cssSelector("[title='Select month']"));
        WebElement yearPicker = monthAndYearPicker.findElement(By.cssSelector("[title='Select year']"));
        WebElement dayPicker = datePicker.findElement(By.cssSelector("ngb-datepicker-month"));

        String year = getYearString(startInstant, timeZone);
        String month = getMonthString(startInstant, timeZone);
        String date = getFullDateString(startInstant, timeZone);

        selectDropdownOptionByText(yearPicker, year);
        selectDropdownOptionByText(monthPicker, month);
        dayPicker.findElement(By.cssSelector(String.format("[aria-label='%s']", date))).click();
    }

    protected void fillFileBox(RemoteWebElement fileBoxElement, String fileName) {
        if (fileName.isEmpty()) {
            fileBoxElement.clear();
        } else {
            fileBoxElement.setFileDetector(new UselessFileDetector());
            String filePath = new File(fileName).getAbsolutePath();
            fileBoxElement.sendKeys(filePath);
        }
    }

    /**
     * Get rich text from editor.
     */
    protected String getEditorRichText(WebElement editor) {
        waitForElementPresence(By.tagName("iframe"));
        String id = editor.findElement(By.tagName("textarea")).getAttribute("id");
        return (String) ((JavascriptExecutor) browser.driver)
                .executeAsyncScript(READ_TINYMCE_CONTENT_SCRIPT, id);
    }

    /**
     * Write rich text to editor.
     */
    protected void writeToRichTextEditor(WebElement editor, String text) {
        waitForElementPresence(By.tagName("iframe"));
        String id = editor.findElement(By.tagName("textarea")).getAttribute("id");
        ((JavascriptExecutor) browser.driver).executeAsyncScript(WRITE_TO_TINYMCE_SCRIPT, id, text);
    }

    /**
     * Select the option, if it is not already selected.
     * No action taken if it is already selected.
     */
    protected void markOptionAsSelected(WebElement option) {
        waitForElementVisibility(option);
        if (!option.isSelected()) {
            click(option);
        }
    }

    /**
     * Unselect the option, if it is not already unselected.
     * No action taken if it is already unselected'.
     */
    protected void markOptionAsUnselected(WebElement option) {
        waitForElementVisibility(option);
        if (option.isSelected()) {
            click(option);
        }
    }

    /**
     * Returns the text of the option selected in the dropdown.
     */
    protected String getSelectedDropdownOptionText(WebElement dropdown) {
        Select select = new Select(dropdown);
        try {
            uiRetryManager.runUntilNoRecognizedException(new Retryable("Wait for dropdown text to load") {
                @Override
                public void run() {
                    String txt = select.getFirstSelectedOption().getText();
                    assertNotEquals("", txt);
                }
            }, WebDriverException.class, AssertionError.class);
            return select.getFirstSelectedOption().getText();
        } catch (MaximumRetriesExceededException e) {
            return select.getFirstSelectedOption().getText();
        }
    }

    /**
     * Selects option in dropdown based on visible text.
     */
    protected void selectDropdownOptionByText(WebElement dropdown, String text) {
        Select select = new Select(dropdown);
        select.selectByVisibleText(text);
    }

    /**
     * Selects option in dropdown based on value.
     */
    protected void selectDropdownOptionByValue(WebElement dropdown, String value) {
        Select select = new Select(dropdown);
        select.selectByValue(value);
    }

    /**
     * Asserts that all values in the body of the given table are equal to the expectedTableBodyValues.
     */
    protected void verifyTableBodyValues(WebElement table, String[][] expectedTableBodyValues) {
        List<WebElement> rows = table.findElement(By.tagName("tbody")).findElements(By.tagName("tr"));
        assertTrue(expectedTableBodyValues.length <= rows.size());
        for (int rowIndex = 0; rowIndex < expectedTableBodyValues.length; rowIndex++) {
            verifyTableRowValues(rows.get(rowIndex), expectedTableBodyValues[rowIndex]);
        }
    }

    /**
     * Asserts that all values in the given table row are equal to the expectedRowValues.
     */
    protected void verifyTableRowValues(WebElement row, String[] expectedRowValues) {
        List<WebElement> cells = row.findElements(By.tagName("td"));
        assertTrue(expectedRowValues.length <= cells.size());
        for (int cellIndex = 0; cellIndex < expectedRowValues.length; cellIndex++) {
            assertEquals(expectedRowValues[cellIndex], cells.get(cellIndex).getText());
        }
    }

    public void verifyBannerContent(NotificationAttributes expected) {
        WebElement banner = browser.driver.findElement(By.className("banner"));
        String title = banner.findElement(By.tagName("h5")).getText();
        String message = banner.findElement(By.className("banner-text")).getAttribute("innerHTML");
        assertEquals(expected.getTitle(), title);
        assertEquals(expected.getMessage(), message);
    }

    public boolean isBannerVisible() {
        return isElementVisible(By.className("banner"));
    }

    /**
     * Clicks the element and clicks 'Yes' in the follow up dialog box.
     * Fails if there is no dialog box.
     * @return the resulting page.
     */
    public AppPage clickAndConfirm(WebElement elementToClick) {
        click(elementToClick);
        waitForConfirmationModalAndClickOk();
        return this;
    }

    /**
     * Returns True if the page contains some basic elements expected in a page of the
     *         specific type. e.g., the top heading.
     */
    protected abstract boolean containsExpectedPageContents();

    /**
     * Returns True if there is a corresponding element for the given locator.
     */
    public boolean isElementPresent(By by) {
        return browser.driver.findElements(by).size() != 0;
    }

    /**
     * Returns True if there is a corresponding element for the given id or name.
     */
    public boolean isElementPresent(String elementId) {
        try {
            browser.driver.findElement(By.id(elementId));
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public boolean isElementVisible(By by) {
        try {
            return browser.driver.findElement(by).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Returns true if the expected condition is evaluated to true immediately.
     * @see ExpectedConditions
     */
    private boolean isExpectedCondition(ExpectedCondition<?> expectedCondition) {
        Object value = expectedCondition.apply(browser.driver);
        if (value == null) {
            return false;
        }

        if (value.getClass() == Boolean.class) {
            return (boolean) value;
        } else {
            return true;
        }
    }

    /**
     * Clicks a button (can be inside or outside the modal) that dismisses the modal and waits for the modal to be hidden.
     * The caller must ensure the button is in the modal or a timeout will occur while waiting for the modal to be hidden.
     * @param dismissModalButton a button that dismisses the modal
     */
    public void clickDismissModalButtonAndWaitForModalHidden(WebElement dismissModalButton) {
        // Note: Should first check if the button can actually dismiss the modal otherwise the state will be consistent.
        // However, it is too difficult to check.

        WebElement modalBackdrop = browser.driver.findElement(By.className("modal-backdrop"));

        click(dismissModalButton);
        waitForModalHidden(modalBackdrop);
    }

    /**
     * Scrolls element to center and clicks on it.
     *
     * <p>As compared to {@link org.openqa.selenium.interactions.Actions#moveToElement(WebElement)}, this method is
     * more reliable as the element will not get blocked by elements such as the header.
     *
     * <p>Furthermore, {@link org.openqa.selenium.interactions.Actions#moveToElement(WebElement)} is currently not
     * working in Geckodriver.
     *
     * <p><b>Note:</b> A "scroll into view" Actions primitive is in progress and may allow scrolling element to center.
     * Tracking issue:
     * <a href="https://github.com/w3c/webdriver/issues/1005">Missing "scroll into view" Actions primitive</a>.
     *
     * <p>Also note that there are some other caveats, for example
     * {@code new Actions(browser.driver).moveToElement(...).click(...).perform()} does not behave consistently across
     * browsers.
     * <ul>
     * <li>In FirefoxDriver, the element is scrolled to and then a click is attempted on the element.
     * <li>In ChromeDriver, the mouse is scrolled to the element and then a click is attempted on the mouse coordinate,
     * which means another element can actually be clicked (such as the header or a blocking pop-up).
     * </ul>
     *
     * <p>ChromeDriver also automatically scrolls to an element when clicking an element if it is not in the viewport.
     */
    void scrollElementToCenterAndClick(WebElement element) {
        // TODO: migrate to `scrollIntoView` when Geckodriver is adopted
        scrollElementToCenter(element);
        element.click();
    }

    /**
     * Scrolls element to center.
     */
    void scrollElementToCenter(WebElement element) {
        executeScript(SCROLL_ELEMENT_TO_CENTER_AND_CLICK_SCRIPT, element);
    }

    /**
     * Asserts message in toast is equal to the expected message.
     */
    public void verifyStatusMessage(String expectedMessage) {
        verifyStatusMessageWithLinks(expectedMessage, new String[] {});
    }

    /**
     * Asserts message in toast is equal to the expected message and contains the expected links.
     */
    public void verifyStatusMessageWithLinks(String expectedMessage, String[] expectedLinks) {
        WebElement[] statusMessage = new WebElement[1];
        try {
            uiRetryManager.runUntilNoRecognizedException(new Retryable("Verify status to user") {
                @Override
                public void run() {
                    statusMessage[0] = waitForElementPresence(By.className("toast-body"));
                    assertEquals(expectedMessage, statusMessage[0].getText());
                }
            }, WebDriverException.class, AssertionError.class);
        } catch (MaximumRetriesExceededException e) {
            statusMessage[0] = waitForElementPresence(By.className("toast-body"));
            assertEquals(expectedMessage, statusMessage[0].getText());
        } finally {
            if (expectedLinks.length > 0) {
                List<WebElement> actualLinks = statusMessage[0].findElements(By.tagName("a"));
                for (int i = 0; i < expectedLinks.length; i++) {
                    assertTrue(actualLinks.get(i).getAttribute("href").contains(expectedLinks[i]));
                }
            }
        }
    }

    /**
     * Switches to the new browser window just opened.
     */
    protected void switchToNewWindow() {
        browser.switchToNewWindow();
    }

    /**
     * Closes current window and switches back to parent window.
     */
    public void closeCurrentWindowAndSwitchToParentWindow() {
        browser.closeCurrentWindowAndSwitchToParentWindow();
    }

    String getDisplayGiverName(FeedbackParticipantType type) {
        switch (type) {
        case SELF:
            return "Feedback session creator (i.e., me)";
        case STUDENTS:
            return "Students in this course";
        case INSTRUCTORS:
            return "Instructors in this course";
        case TEAMS:
            return "Teams in this course";
        default:
            throw new IllegalArgumentException("Unknown FeedbackParticipantType: " + type);
        }
    }

    String getDisplayRecipientName(FeedbackParticipantType type) {
        switch (type) {
        case SELF:
            return "Giver (Self feedback)";
        case STUDENTS_IN_SAME_SECTION:
            return "Other students in the same section";
        case STUDENTS:
            return "Students in the course";
        case STUDENTS_EXCLUDING_SELF:
            return "Other students in the course";
        case INSTRUCTORS:
            return "Instructors in the course";
        case TEAMS_IN_SAME_SECTION:
            return "Other teams in the same section";
        case TEAMS:
            return "Teams in the course";
        case TEAMS_EXCLUDING_SELF:
            return "Other teams in the course";
        case OWN_TEAM:
            return "Giver's team";
        case OWN_TEAM_MEMBERS:
            return "Giver's team members";
        case OWN_TEAM_MEMBERS_INCLUDING_SELF:
            return "Giver's team members and Giver";
        case NONE:
            return "Nobody specific (For general class feedback)";
        default:
            throw new IllegalArgumentException("Unknown FeedbackParticipantType: " + type);
        }
    }

    String getDisplayedDateTime(Instant instant, String timeZone, String pattern) {
        ZonedDateTime zonedDateTime = TimeHelper.getMidnightAdjustedInstantBasedOnZone(instant, timeZone, false)
                .atZone(ZoneId.of(timeZone));
        return DateTimeFormatter.ofPattern(pattern).format(zonedDateTime);
    }

    private String getFullDateString(Instant instant, String timeZone) {
        return getDisplayedDateTime(instant, timeZone, "EEEE, MMMM d, yyyy");
    }

    private String getYearString(Instant instant, String timeZone) {
        return getDisplayedDateTime(instant, timeZone, "yyyy");
    }

    private String getMonthString(Instant instant, String timeZone) {
        return getDisplayedDateTime(instant, timeZone, "MMM");
    }
}
