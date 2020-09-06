package teammates.e2e.pageobjects;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.remote.UselessFileDetector;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import teammates.common.util.ThreadHelper;
import teammates.common.util.Url;
import teammates.common.util.retry.MaximumRetriesExceededException;
import teammates.common.util.retry.RetryManager;
import teammates.common.util.retry.RetryableTask;
import teammates.e2e.util.TestProperties;
import teammates.test.driver.FileHelper;

/**
 * An abstract class that represents a browser-loaded page of the app and
 * provides ways to interact with it. Also contains methods to validate some
 * aspects of the page. .e.g, html page source. <br>
 *
 * <p>Note: We are using the PageObjects pattern here.
 *
 * @see <a href="https://code.google.com/p/selenium/wiki/PageObjects">https://code.google.com/p/selenium/wiki/PageObjects</a>
 */
public abstract class AppPage {

    private static final String CLEAR_ELEMENT_SCRIPT;
    private static final String SCROLL_ELEMENT_TO_CENTER_AND_CLICK_SCRIPT;
    private static final String ADD_CHANGE_EVENT_HOOK;

    static {
        try {
            ADD_CHANGE_EVENT_HOOK = FileHelper.readFile("src/e2e/resources/scripts/addChangeEventHook.js");
            CLEAR_ELEMENT_SCRIPT = FileHelper.readFile("src/e2e/resources/scripts/clearElementWithoutEvents.js");
            SCROLL_ELEMENT_TO_CENTER_AND_CLICK_SCRIPT = FileHelper
                    .readFile("src/e2e/resources/scripts/scrollElementToCenterAndClick.js");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Browser instance the page is loaded into. */
    protected Browser browser;

    /** Use for retrying due to transient UI issues. */
    protected RetryManager uiRetryManager = new RetryManager((TestProperties.TEST_TIMEOUT + 1) / 2);

    /** Firefox change handler for handling when `change` events are not fired in Firefox. */
    private final FirefoxChangeHandler firefoxChangeHandler;

    @FindBy(linkText = "Profile")
    private WebElement studentProfileTab;

    /**
     * Used by subclasses to create a {@code AppPage} object to wrap around the
     * given {@code browser} object. Fails if the page content does not match
     * the page type, as defined by the sub-class.
     */
    public AppPage(Browser browser) {
        this.browser = browser;
        this.firefoxChangeHandler = new FirefoxChangeHandler(); //legit firefox

        boolean isCorrectPageType = containsExpectedPageContents();

        if (isCorrectPageType) {
            return;
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
     * Fails if the new page content does not match content expected in a page of
     * the type indicated by the parameter {@code typeOfPage}.
     */
    public static <T extends AppPage> T getNewPageInstance(Browser currentBrowser, Url url, Class<T> typeOfPage) {
        currentBrowser.driver.get(url.toAbsoluteString());
        currentBrowser.waitForPageLoad();
        return getNewPageInstance(currentBrowser, typeOfPage);
    }

    /**
     * Fails if the new page content does not match content expected in a page of
     * the type indicated by the parameter {@code typeOfPage}.
     */
    public static <T extends AppPage> T getNewPageInstance(Browser currentBrowser, Class<T> typeOfPage) {
        try {
            Constructor<T> constructor = typeOfPage.getConstructor(Browser.class);
            T page = constructor.newInstance(currentBrowser);
            PageFactory.initElements(currentBrowser.driver, page);
            return page;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gives an AppPage instance based on the given Browser.
     */
    public static AppPage getNewPageInstance(Browser currentBrowser) {
        return getNewPageInstance(currentBrowser, GenericAppPage.class);
    }

    /**
     * Fails if the new page content does not match content expected in a page of
     * the type indicated by the parameter {@code newPageType}.
     */
    public <T extends AppPage> T changePageType(Class<T> newPageType) {
        return getNewPageInstance(browser, newPageType);
    }

    /**
     * Gives a {@link LoginPage} instance based on the given {@link Browser} and test configuration.
     * Fails if the page content does not match the content of the expected login page.
     */
    public static LoginPage createCorrectLoginPageType(Browser browser) {
        Class<? extends LoginPage> cls =
                TestProperties.isDevServer() ? DevServerLoginPage.class : GoogleLoginPage.class;
        return getNewPageInstance(browser, cls);
    }

    public <E> E waitFor(ExpectedCondition<E> expectedCondition) {
        WebDriverWait wait = new WebDriverWait(browser.driver, TestProperties.TEST_TIMEOUT);
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

    public void waitUntilAnimationFinish() {
        WebDriverWait wait = new WebDriverWait(browser.driver, 2);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("ng-animating")));
        ThreadHelper.waitFor(500);
    }

    /**
     * Waits until an element is no longer attached to the DOM or the timeout expires.
     * @param element the WebElement
     * @throws TimeoutException if the timeout defined in
     * {@link TestProperties#TEST_TIMEOUT} expires
     * @see org.openqa.selenium.support.ui.FluentWait#until(java.util.function.Function)
     */
    public void waitForElementStaleness(WebElement element) {
        waitFor(ExpectedConditions.stalenessOf(element));
    }

    /**
     * Waits for a confirmation modal to appear and click the confirm button.
     */
    public void waitForConfirmationModalAndClickOk() {
        waitForModalShown();
        WebElement okayButton = browser.driver.findElement(By.className("modal-btn-ok"));
        waitForElementToBeClickable(okayButton);
        clickDismissModalButtonAndWaitForModalHidden(okayButton);
    }

    /**
     * Waits for a confirmation modal to appear and click the cancel button.
     */
    public void waitForConfirmationModalAndClickCancel() {
        waitForModalShown();
        WebElement cancelButton = browser.driver.findElement(By.className("modal-btn-cancel"));
        waitForElementToBeClickable(cancelButton);
        clickDismissModalButtonAndWaitForModalHidden(cancelButton);
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
        browser.driver.get(browser.driver.getCurrentUrl());
        waitForPageToLoad();
    }

    protected Object executeScript(String script, Object... args) {
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) browser.driver;
        return javascriptExecutor.executeScript(script, args);
    }

    /**
     * Returns the HTML source of the currently loaded page.
     * TODO: remove this method as it does not return necessary html anymore since frontend is generated by angular
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

        // Add event hook before blurring the text box element so we can detect the event.
        firefoxChangeHandler.addChangeEventHook(textBoxElement);

        textBoxElement.sendKeys(Keys.TAB); // blur the element to receive events

        // Although events should not be manually fired, the `change` event does not fire when text input is changed if
        // Firefox is not in focus. Setting profile option `focusmanager.testmode = true` does not help as well.
        // A temporary solution is to fire the `change` event until the buggy behavior is fixed.
        // More details can be found in the umbrella issue of related bugs in the following link:
        // https://github.com/mozilla/geckodriver/issues/906
        // The firing of `change` event is also imperfect because no check for the type of the elements is done before firing
        // the event, for instance a `change` event will be wrongly fired on any content editable element. The firing time of
        // `change` events is also incorrect for several `input` types such as `checkbox` and `date`.
        // See: https://developer.mozilla.org/en-US/docs/Web/Events/change
        firefoxChangeHandler.fireChangeEventIfNotFired(textBoxElement);
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
        browser.driver.switchTo().frame(editor.findElement(By.tagName("iframe")));

        String innerHtml = browser.driver.findElement(By.id("tinymce")).getAttribute("innerHTML");
        // check if editor is empty
        innerHtml = innerHtml.contains("data-mce-bogus") ? "" : innerHtml;
        browser.driver.switchTo().defaultContent();
        return innerHtml;
    }

    /**
     * Write rich text to editor.
     */
    protected void writeToRichTextEditor(WebElement editor, String text) {
        waitForElementPresence(By.tagName("iframe"));
        String id = editor.findElement(By.tagName("textarea")).getAttribute("id");
        executeScript(String.format("tinyMCE.get('%s').setContent('%s');"
                + " tinyMCE.get('%s').save()", id, text, id));
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
        return select.getFirstSelectedOption().getText();
    }

    /**
     * Selects option in dropdown based on visible text.
     */
    protected void selectDropdownOptionByText(WebElement dropdown, String text) {
        Select select = new Select(dropdown);
        select.selectByVisibleText(text);
    }

    /**
     * Returns the value of the cell located at {@code (row, column)}
     *         from the first table (which is of type {@code class=table}) in the page.
     */
    public String getCellValueFromDataTable(int row, int column) {
        return getCellValueFromDataTable(0, row, column);
    }

    /**
     * Returns the value of the cell located at {@code (row, column)}
     *         from the nth(0-index-based) table (which is of type {@code class=table}) in the page.
     */
    public String getCellValueFromDataTable(int tableNum, int row, int column) {
        WebElement tableElement = browser.driver.findElements(By.className("table")).get(tableNum);
        WebElement trElement = tableElement.findElements(By.tagName("tr")).get(row);
        WebElement tdElement = trElement.findElements(By.tagName("td")).get(column);
        return tdElement.getText();
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
     * Equivalent of clicking the 'Profile' tab on the top menu of the page.
     * @return the loaded page
     */
    public StudentProfilePage loadProfileTab() {
        click(studentProfileTab);
        waitForPageToLoad();
        return changePageType(StudentProfilePage.class);
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

    public boolean isElementVisible(String elementId) {
        try {
            return browser.driver.findElement(By.id(elementId)).isDisplayed();
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
            uiRetryManager.runUntilNoRecognizedException(new RetryableTask("Verify status to user") {
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
     * Set browser window to x width and y height.
     */
    protected void setWindowSize(int x, int y) {
        Dimension d = new Dimension(x, y);
        browser.driver.manage().window().setSize(d);
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

    /**
     * Encapsulates methods for handling Firefox {@code change} events. The methods can only handle one {@value CHANGE_EVENT}
     * event at a time and will only do something useful if test browser is Firefox. Note that the class does not check if
     * the {@value CHANGE_EVENT} event should be fired on the element.
     */
    private class FirefoxChangeHandler {

        private static final String CHANGE_EVENT = "change";
        /**
         * The attribute that the hook will modify to indicate if the {@value CHANGE_EVENT} event is detected.
         */
        private static final String HOOK_ATTRIBUTE = "__change__";
        /**
         * The maximum number of seconds required for all hardware (including slow ones) to fire the event.
         */
        private static final int MAXIMUM_SECONDS_REQUIRED_FOR_ALL_CPUS_TO_FIRE_EVENT = 1;

        private final boolean isFirefox;

        FirefoxChangeHandler() {
            isFirefox = TestProperties.BROWSER_FIREFOX.equals(TestProperties.BROWSER);
        }

        /**
         * Returns true if the {@value CHANGE_EVENT} event hook has already been added.
         * Note that there can only be one hook (linked to a particular element) at a time for each page.
         */
        private boolean isChangeEventHookAdded() {
            WebElement bodyElement = browser.driver.findElement(By.tagName("body"));
            return isExpectedCondition(ExpectedConditions.attributeToBeNotEmpty(bodyElement, HOOK_ATTRIBUTE));
        }

        /**
         * Adds a {@value CHANGE_EVENT} event hook for the element.
         * The hook allows detection of the event required for {@link FirefoxChangeHandler#fireChangeEventIfNotFired}.
         *
         * @param element the element for which the hook will track whether the event is fired on the element
         *
         * @throws IllegalStateException if there is already a hook in the document
         */
        private void addChangeEventHook(WebElement element) {
            if (!isFirefox) {
                return;
            }

            checkState(!isChangeEventHookAdded(),
                    "The `%1$s` event hook can only be added once in the document.", CHANGE_EVENT);

            executeScript(ADD_CHANGE_EVENT_HOOK, element, CHANGE_EVENT, HOOK_ATTRIBUTE);
        }

        /**
         * Fires a {@value CHANGE_EVENT} event on the element if not already fired.
         * Requires a hook ({@link FirefoxChangeHandler#addChangeEventHook(WebElement)}) to be added before to detect
         * events.
         * Note that sometimes the {@value CHANGE_EVENT} event may need to be fired multiple times but this method only fires
         * one {@value CHANGE_EVENT} event in place of multiple {@value CHANGE_EVENT} events. This reinforces the notion that
         * events should not be fired manually so this method is to be avoided if possible.
         *
         * @param element the element for which the change event will be fired if it is not fired.
         *
         * @throws IllegalStateException if `change` event hook is not added
         *
         * @see FirefoxChangeHandler#isChangeEventNotFired()
         */
        private void fireChangeEventIfNotFired(WebElement element) {
            if (!isFirefox) {
                return;
            }

            checkState(isChangeEventHookAdded(),
                    "A `%s` hook has to be added previously to detect event firing.", CHANGE_EVENT);

            if (isChangeEventNotFired()) {
                fireChangeEvent(element);
            }

            removeHookAttribute();
        }

        /**
         * Removes the attribute associated with a hook.
         */
        private void removeHookAttribute() {
            executeScript(String.format("document.body.removeAttribute('%s');", HOOK_ATTRIBUTE));
        }

        /**
         * Returns if a {@value CHANGE_EVENT} event has not been fired for the element to which the hook is associated.
         * Note that this only detects the presence of firing of {@value CHANGE_EVENT} events and not does not keep track of
         * how many {@value CHANGE_EVENT} events are fired.
         */
        private boolean isChangeEventNotFired() {
            WebDriverWait wait = new WebDriverWait(browser.driver, MAXIMUM_SECONDS_REQUIRED_FOR_ALL_CPUS_TO_FIRE_EVENT);
            try {
                wait.until(ExpectedConditions.attributeContains(By.tagName("body"), HOOK_ATTRIBUTE, "true"));
                return false;
            } catch (TimeoutException e) {
                return true;
            }
        }

        /**
         * Fires the {@value CHANGE_EVENT} event on the element.
         * Note that this method should not usually be called because events should not be fired manually,
         * and may also result in unexpected <strong>multiple firings</strong> of the event.
         */
        private void fireChangeEvent(WebElement element) {
            if (!isFirefox) {
                return;
            }
            // The `change` event is fired with bubbling enabled to simulate how browsers fire them.
            // See: https://developer.mozilla.org/en-US/docs/Web/Events/change
            executeScript("const event = new Event(arguments[1], {bubbles: true});"
                    + "arguments[0].dispatchEvent(event);", element, CHANGE_EVENT);
        }
    }
}
