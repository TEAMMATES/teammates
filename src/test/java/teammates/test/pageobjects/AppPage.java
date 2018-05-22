package teammates.test.pageobjects;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.remote.UselessFileDetector;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Preconditions;
import com.google.common.collect.ObjectArrays;

import teammates.common.util.Const;
import teammates.common.util.ThreadHelper;
import teammates.common.util.Url;
import teammates.common.util.retry.MaximumRetriesExceededException;
import teammates.common.util.retry.RetryManager;
import teammates.common.util.retry.RetryableTask;
import teammates.common.util.retry.RetryableTaskReturnsThrows;
import teammates.test.driver.AssertHelper;
import teammates.test.driver.FileHelper;
import teammates.test.driver.HtmlHelper;
import teammates.test.driver.TestProperties;

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
    private static final By MAIN_CONTENT = By.id("mainContent");
    private static final int VERIFICATION_RETRY_COUNT = 5;
    private static final int VERIFICATION_RETRY_DELAY_IN_MS = 1000;

    /** Browser instance the page is loaded into. */
    protected Browser browser;

    /** Use for retrying due to persistence delays. */
    protected RetryManager persistenceRetryManager = new RetryManager(TestProperties.PERSISTENCE_RETRY_PERIOD_IN_S / 2);

    /** Use for retrying due to transient UI issues. */
    protected RetryManager uiRetryManager = new RetryManager((TestProperties.TEST_TIMEOUT + 1) / 2);

    /** Firefox change handler for handling when `change` events are not fired in Firefox. */
    private final FirefoxChangeHandler firefoxChangeHandler;

    /** Handler for tracking the state of a JQuery AJAX request. */
    private final JQueryAjaxHandler jQueryAjaxHandler;

    // These are elements common to most pages in our app
    @FindBy(id = "statusMessagesToUser")
    private WebElement statusMessage;

    @FindBy(xpath = "//*[@id=\"contentLinks\"]/ul[1]/li[1]/a")
    private WebElement instructorHomeTab;

    @FindBy(xpath = "//*[@id=\"contentLinks\"]/ul[1]/li[2]/a")
    private WebElement instructorCoursesTab;

    @FindBy(xpath = "//*[@id=\"contentLinks\"]/ul[1]/li[4]/a")
    private WebElement instructorStudentsTab;

    @FindBy(xpath = "//*[@id=\"contentLinks\"]/ul[1]/li[6]/a")
    private WebElement instructorHelpTab;

    @FindBy(id = "studentHomeNavLink")
    private WebElement studentHomeTab;

    @FindBy(id = "studentProfileNavLink")
    private WebElement studentProfileTab;

    @FindBy(id = "studentHelpLink")
    private WebElement studentHelpTab;

    @FindBy(id = "btnLogout")
    private WebElement logoutButton;

    /**
     * Used by subclasses to create a {@code AppPage} object to wrap around the
     * given {@code browser} object. Fails if the page content does not match
     * the page type, as defined by the sub-class.
     */
    public AppPage(Browser browser) {
        this.browser = browser;
        this.firefoxChangeHandler = new FirefoxChangeHandler();
        jQueryAjaxHandler = new JQueryAjaxHandler();

        boolean isCorrectPageType = containsExpectedPageContents();

        if (isCorrectPageType) {
            return;
        }

        // To minimize test failures due to eventual consistency, we try to
        //  reload the page and compare once more.
        System.out.println("#### Incorrect page type: going to try reloading the page.");

        ThreadHelper.waitFor(2000);

        this.reloadPage();
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

    JQueryAjaxHandler getjQueryAjaxHandler() {
        return jQueryAjaxHandler;
    }

    /**
     * Simply loads the given URL.
     */
    public AppPage navigateTo(Url url) {
        browser.driver.get(url.toAbsoluteString());
        return this;
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

    /**
     * Checks whether the URL currently loaded in the browser corresponds to the given page {@code uri}.
     */
    public boolean isPageUri(String uri) {
        Url currentPageUrl;
        try {
            currentPageUrl = new Url(browser.driver.getCurrentUrl());
        } catch (AssertionError e) { // due to MalformedURLException
            return false;
        }
        return currentPageUrl.getRelativeUrl().equals(uri);
    }

    public <E> E waitFor(ExpectedCondition<E> expectedCondition) {
        WebDriverWait wait = new WebDriverWait(browser.driver, TestProperties.TEST_TIMEOUT);
        return wait.until(expectedCondition);
    }

    /**
     * Waits until the page is fully loaded.
     */
    public void waitForPageToLoad() {
        waitFor(driver -> {
            // Check https://developer.mozilla.org/en/docs/web/api/document/readystate
            // to understand more on a web document's readyState
            final JavascriptExecutor javascriptExecutor = (JavascriptExecutor) Preconditions.checkNotNull(driver);
            return "complete".equals(javascriptExecutor.executeScript("return document.readyState"));
        });
    }

    /**
     * Waits until TinyMCE editor is fully loaded.
     */
    public void waitForRichTextEditorToLoad(final String id) {
        waitFor(driver -> {
            String script = "return tinymce.get('" + id + "') !== null";
            final JavascriptExecutor javascriptExecutor = (JavascriptExecutor) Preconditions.checkNotNull(driver);
            return (Boolean) javascriptExecutor.executeScript(script);
        });
    }

    /**
     * Waits until the element is not covered by any other element.
     */
    public void waitForElementNotCovered(final WebElement element) {
        waitFor(d -> !isElementCovered(element));
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

    public void waitForElementsVisibility(List<WebElement> elements) {
        waitFor(ExpectedConditions.visibilityOfAllElements(elements));
    }

    /**
     * {@code locator} is mapped to an actual {@link WebElement}.
     * @param locator used to find the element
     * @see AppPage#waitForElementStaleness(WebElement)
     */
    public void waitForElementStaleness(By locator) {
        waitForElementStaleness(browser.driver.findElement(locator));
    }

    /**
     * Waits until an element is no longer attached to the DOM or the timeout expires.
     * @param element the WebElement
     * @throws org.openqa.selenium.TimeoutException if the timeout defined in
     * {@link TestProperties#TEST_TIMEOUT} expires
     * @see org.openqa.selenium.support.ui.FluentWait#until(com.google.common.base.Function)
     */
    public void waitForElementStaleness(WebElement element) {
        waitFor(ExpectedConditions.stalenessOf(element));
    }

    /**
     * Waits until an element belongs to the class or the timeout expires.
     * @param element the WebElement
     * @param elementClass the class that the element must belong to
     * @throws org.openqa.selenium.TimeoutException if the timeout defined in
     * {@link TestProperties#TEST_TIMEOUT} expires
     * @see org.openqa.selenium.support.ui.FluentWait#until(com.google.common.base.Function)
     */
    void waitForElementToBeMemberOfClass(WebElement element, String elementClass) {
        waitFor(driver -> {
            String classAttribute = element.getAttribute("class");
            List<String> classes = Arrays.asList(classAttribute.split(" "));

            return classes.contains(elementClass);
        });
    }

    /**
     * Waits for element to be invisible or not present, or timeout.
     */
    public void waitForElementToDisappear(By by) {
        waitFor(ExpectedConditions.invisibilityOfElementLocated(by));
    }

    /**
     * Waits for a list of elements to be invisible or not present, or timeout.
     */
    public void waitForElementsToDisappear(List<WebElement> elements) {
        waitFor(ExpectedConditions.invisibilityOfAllElements(elements));
    }

    /**
     * Waits until scrolling on the page is complete. Note: The detection of whether the page is scrolling is coupled to our
     * own implementation of scrolling {@code (scrollTo.js)} and is not a true detection.
     * @throws org.openqa.selenium.TimeoutException if the timeout defined in {@link TestProperties#TEST_TIMEOUT} expires
     */
    void waitForScrollingToComplete() {
        // Note: The implementation compares previous and current scroll positions so the polling interval should not be
        // changed or set to too low a value or the comparisons may unexpectedly be equal.
        waitFor(new ExpectedCondition<Boolean>() {
            private static final String SCROLL_POSITION_PROPERTY = "scrollTop";

            private boolean isFirstEvaluation = true;

            private final WebElement htmlElement = browser.driver.findElement(By.tagName("html"));
            private final WebElement bodyElement = browser.driver.findElement(By.tagName("body"));

            private String prevHtmlScrollPosition;
            private String prevBodyScrollPosition;

            private boolean isPreviouslyEqual;

            @Override
            public Boolean apply(WebDriver input) {
                // The first evaluation has no previous scrolling positions to compare to
                if (isFirstEvaluation) {
                    prevHtmlScrollPosition = htmlElement.getAttribute(SCROLL_POSITION_PROPERTY);
                    prevBodyScrollPosition = bodyElement.getAttribute(SCROLL_POSITION_PROPERTY);

                    isFirstEvaluation = false;
                    return false;
                }

                return isCurrentScrollingPositionSameAsPrevious();
            }

            private Boolean isCurrentScrollingPositionSameAsPrevious() {
                final String currentHtmlScrollPosition = htmlElement.getAttribute(SCROLL_POSITION_PROPERTY);
                final String currentBodyScrollPosition = bodyElement.getAttribute(SCROLL_POSITION_PROPERTY);

                if (currentHtmlScrollPosition.equals(prevHtmlScrollPosition)
                        && currentBodyScrollPosition.equals(prevBodyScrollPosition)) {
                    // Because we are not truly detecting if the page has stopped scrolling,
                    // we make sure the scroll positions is the same for one more iteration
                    if (isPreviouslyEqual) {
                        return true;
                    } else {
                        isPreviouslyEqual = true;
                        return false;
                    }
                }

                prevHtmlScrollPosition = currentHtmlScrollPosition;
                prevBodyScrollPosition = currentBodyScrollPosition;

                return false;
            }
        });
    }

    /**
     * Waits for an alert modal to appear and dismisses it.
     */
    public void waitForAndDismissAlertModal() {
        waitForConfirmationModalAndClickOk();
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
     * Waits for a confirmation modal to appear and click the No button.
     */
    public void clickNoOnModal() {
        waitForModalShown();
        WebElement noButton = browser.driver.findElement(By.cssSelector("[data-bb-handler='no']"));
        waitForElementToBeClickable(noButton);
        clickDismissModalButtonAndWaitForModalHidden(noButton);
    }

    public void cancelModalForm(WebElement modal) {
        clickDismissModalButtonAndWaitForModalHidden(modal.findElement(By.tagName("button")));
    }

    public void checkCheckboxesInForm(WebElement form, String elementsName) {
        List<WebElement> formElements = form.findElements(By.name(elementsName));
        for (WebElement e : formElements) {
            markCheckBoxAsChecked(e);
        }
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

    /**
     * Waits for text contained in the element to appear in the page, or timeout.
     */
    public void waitForTextContainedInElementPresence(By by, String text) {
        waitFor(ExpectedConditions.textToBePresentInElementLocated(by, text));
    }

    /**
     * Waits for text contained in the element to disappear from the page, or timeout.
     */
    public void waitForTextContainedInElementAbsence(By by, String text) {
        waitFor(ExpectedConditions.not(ExpectedConditions.textToBePresentInElementLocated(by, text)));
    }

    /**
     * Switches to the new browser window just opened.
     */
    protected void switchToNewWindow() {
        browser.switchToNewWindow();
    }

    public void closeCurrentWindowAndSwitchToParentWindow() {
        browser.closeCurrentWindowAndSwitchToParentWindow();
    }

    public void reloadPage() {
        browser.driver.get(browser.driver.getCurrentUrl());
        waitForPageToLoad();
    }

    protected Object executeScript(String script, Object... args) {
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) browser.driver;
        return javascriptExecutor.executeScript(script, args);
    }

    /** Equivalent to pressing the 'back' button of the browser. <br>
     * Fails if the page content does not match content expected in a page of
     * the type indicated by the parameter {@code typeOfPreviousPage}.
     */
    public <T extends AppPage> T goToPreviousPage(Class<T> typeOfPreviousPage) {
        browser.driver.navigate().back();
        waitForPageToLoad();
        return changePageType(typeOfPreviousPage);
    }

    /**
     * Equivalent to clicking the 'Courses' tab on the top menu of the page.
     * @return the loaded page.
     */
    public InstructorCoursesPage loadCoursesTab() {
        click(instructorCoursesTab);
        waitForPageToLoad();
        return changePageType(InstructorCoursesPage.class);
    }

    /**
     * Equivalent to clicking the 'Students' tab on the top menu of the page.
     * @return the loaded page.
     */
    public InstructorStudentListPage loadStudentsTab() {
        click(instructorStudentsTab);
        waitForPageToLoad();
        return changePageType(InstructorStudentListPage.class);
    }

    /**
     * Equivalent to clicking the 'Home' tab on the top menu of the page.
     * @return the loaded page.
     */
    public InstructorHomePage loadInstructorHomeTab() {
        click(instructorHomeTab);
        waitForPageToLoad();
        return changePageType(InstructorHomePage.class);
    }

    /**
     * Equivalent to clicking the 'Help' tab on the top menu of the page.
     * @return the loaded page.
     */
    public InstructorHelpPage loadInstructorHelpTab() {
        click(instructorHelpTab);
        waitForPageToLoad();
        switchToNewWindow();
        return changePageType(InstructorHelpPage.class);
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
     * Equivalent of student clicking the 'Home' tab on the top menu of the page.
     * @return the loaded page
     */
    public StudentHomePage loadStudentHomeTab() {
        click(studentHomeTab);
        waitForPageToLoad();
        return changePageType(StudentHomePage.class);
    }

    /**
     * Equivalent of clicking the 'Help' tab on the top menu of the page.
     * @return the loaded page
     */
    public StudentHelpPage loadStudentHelpTab() {
        click(studentHelpTab);
        waitForPageToLoad();
        switchToNewWindow();
        return changePageType(StudentHelpPage.class);
    }

    /**
     * Click the 'logout' link in the top menu of the page.
     */
    public AppPage logout() {
        click(logoutButton);
        waitForPageToLoad();
        return this;
    }

    /**
     * Returns the HTML source of the currently loaded page.
     */
    public String getPageSource() {
        return browser.driver.getPageSource();
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
        final Map<String, String> errors = (Map<String, String>) result.get("errors");
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
        final Map<String, Object> result = (Map<String, Object>) executeScript(
                "const element = arguments[0];"
                        + "if (element.nodeName === 'INPUT' || element.nodeName === 'TEXTAREA') {"
                        + "   if (element.readOnly) {"
                        + "       return { "
                        + "           errors: {"
                        + "               detail: 'You may only edit editable elements'"
                        + "           }"
                        + "       };"
                        + "   }"
                        + "   if (element.disabled) {"
                        + "       return { "
                        + "           errors: {"
                        + "               detail: 'You may only interact with enabled elements'"
                        + "           }"
                        + "       };"
                        + "   }"
                        + "   element.value='';"
                        + "} else if (element.isContentEditable) {"
                        + "   while (element.firstChild) {"
                        + "       element.removeChild(element.firstChild);"
                        + "   }"
                        + "}"
                        + "return { "
                        + "   data: {"
                        + "       detail: 'Success'"
                        + "   }"
                        + "};", element);
        return result;
    }

    public String getElementAttribute(By locator, String attrName) {
        return browser.driver.findElement(locator).getAttribute(attrName);
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

    protected void fillRichTextEditor(String id, String content) {
        String preparedContent = content.replace("\n", "<br>");

        clearAndSetNewValueForTinyMce(id, preparedContent);
    }

    /**
     * Simulates the clearing and setting of value for a TinyMCE Editor. This method is a legacy helper for filling rich text
     * editors and should not be used unless necessary.
     */
    private void clearAndSetNewValueForTinyMce(String id, String preparedContent) {
        executeScript(
                // clear content programmatically; one implication is that an undo level is not added to the TinyMCE editor
                "tinyMCE.get(arguments[0]).setContent('');"
                        // insert like a user does (one implication is that an undo level is added);
                        // this may result in some events (e.g. `Change`) firing immediately
                        + "tinyMCE.get(arguments[0]).insertContent(arguments[1]);"
                        + "tinyMCE.get(arguments[0]).focus();", // for consistent HTML verification across browsers
                id, preparedContent);
    }

    protected String getRichTextEditorContent(String id) {
        return (String) executeScript("  if (typeof tinyMCE !== 'undefined') {"
                                      + "    return tinyMCE.get('" + id + "').getContent();"
                                      + "}");
    }

    protected void fillFileBox(RemoteWebElement fileBoxElement, String fileName) {
        if (fileName.isEmpty()) {
            fileBoxElement.clear();
        } else {
            fileBoxElement.setFileDetector(new UselessFileDetector());
            String newFilePath = new File(fileName).getAbsolutePath();
            fileBoxElement.sendKeys(newFilePath);
        }
    }

    protected String getTextBoxValue(WebElement textBox) {
        return textBox.getAttribute("value");
    }

    protected boolean checkEmptyTextBoxValue(WebElement textBox) {
        String textInsideInputBox = textBox.getAttribute("value");
        return textInsideInputBox.isEmpty();
    }

    /**
     * 'check' the check box, if it is not already 'checked'.
     * No action taken if it is already 'checked'.
     */
    protected void markCheckBoxAsChecked(WebElement checkBox) {
        waitForElementVisibility(checkBox);
        if (!checkBox.isSelected()) {
            click(checkBox);
        }
    }

    /**
     * 'uncheck' the check box, if it is already 'checked'.
     * No action taken if it is not already 'checked'.
     */
    protected void markCheckBoxAsUnchecked(WebElement checkBox) {
        if (checkBox.isSelected()) {
            click(checkBox);
        }
    }

    /**
     * 'check' the radio button, if it is not already 'checked'.
     * No action taken if it is already 'checked'.
     */
    protected void markRadioButtonAsChecked(WebElement radioButton) {
        waitForElementVisibility(radioButton);
        if (!radioButton.isSelected()) {
            click(radioButton);
        }
    }

    /**
     * Selects the option by visible text and returns whether the dropdown value has changed.
     *
     * @throws AssertionError if the selected option is not the one we wanted to select
     *
     * @see Select#selectByVisibleText(String)
     */
    boolean selectDropdownByVisibleValue(WebElement element, String text) {
        Select select = new Select(element);

        WebElement originalSelectedOption = select.getFirstSelectedOption();

        select.selectByVisibleText(text);

        WebElement newSelectedOption = select.getFirstSelectedOption();

        assertEquals(text, newSelectedOption.getText().trim());

        return !newSelectedOption.equals(originalSelectedOption);
    }

    /**
     * Selects the option by visible text and waits for the associated AJAX request to complete.
     *
     * @see AppPage#selectDropdownByVisibleValue(WebElement, String)
     */
    void selectDropdownByVisibleValueAndWaitForAjaxRequestComplete(WebElement element, String text) {
        jQueryAjaxHandler.registerHandlers();

        if (selectDropdownByVisibleValue(element, text)) {
            jQueryAjaxHandler.waitForRequestComplete();
        } else {
            // No AJAX request will be made if the value did not change
            jQueryAjaxHandler.unregisterHandlers();
        }
    }

    /**
     * Selects the option by value and returns whether the dropdown value has changed.
     *
     * @throws AssertionError if the selected option is not the one we wanted to select
     *
     * @see Select#selectByValue(String)
     */
    boolean selectDropdownByActualValue(WebElement element, String value) {
        Select select = new Select(element);

        WebElement originalSelectedOption = select.getFirstSelectedOption();

        select.selectByValue(value);

        WebElement newSelectedOption = select.getFirstSelectedOption();

        assertEquals(value, newSelectedOption.getAttribute("value"));

        return !newSelectedOption.equals(originalSelectedOption);
    }

    /**
     * Selects the option by value and waits for the associated AJAX request to complete.
     *
     * @see AppPage#selectDropdownByActualValue(WebElement, String)
     */
    void selectDropdownByActualValueAndWaitForAjaxRequestComplete(WebElement element, String value) {
        jQueryAjaxHandler.registerHandlers();

        if (selectDropdownByActualValue(element, value)) {
            jQueryAjaxHandler.waitForRequestComplete();
        } else {
            // No AJAX request will be made if the value did not change
            jQueryAjaxHandler.unregisterHandlers();
        }
    }

    public String getDropdownSelectedValue(WebElement element) {
        Select select = new Select(element);
        return select.getFirstSelectedOption().getAttribute("value");
    }

    /**
     * Returns a list containing the texts of the user status messages in the page.
     * @see WebElement#getText()
     */
    public List<String> getTextsForAllStatusMessagesToUser() {
        List<WebElement> statusMessagesToUser = statusMessage.findElements(By.tagName("div"));
        List<String> statusMessageTexts = new ArrayList<String>();
        for (WebElement statusMessage : statusMessagesToUser) {
            statusMessageTexts.add(statusMessage.getText());
        }
        return statusMessageTexts;
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
     * Returns the value of the header located at {@code (row, column)}
     *         from the nth(0-index-based) table (which is of type {@code class=table}) in the page.
     */
    public String getHeaderValueFromDataTable(int tableNum, int row, int column) {
        WebElement tableElement = browser.driver.findElements(By.className("table")).get(tableNum);
        WebElement trElement = tableElement.findElements(By.tagName("tr")).get(row);
        WebElement tdElement = trElement.findElements(By.tagName("th")).get(column);
        return tdElement.getText();
    }

    /**
     * Returns the number of rows from the nth(0-index-based) table
     *         (which is of type {@code class=table}) in the page.
     */
    public int getNumberOfRowsFromDataTable(int tableNum) {
        WebElement tableElement = browser.driver.findElements(By.className("table")).get(tableNum);
        return tableElement.findElements(By.tagName("tr")).size();
    }

    /**
     * Returns the number of columns from the header in the table
     *         (which is of type {@code class=table}) in the page.
     */
    public int getNumberOfColumnsFromDataTable(int tableNum) {
        WebElement tableElement = browser.driver.findElements(By.className("table")).get(tableNum);
        WebElement trElement = tableElement.findElement(By.tagName("tr"));
        return trElement.findElements(By.tagName("th")).size();
    }

    /**
     * Returns the id of the table
     *         (which is of type {@code class=table}) in the page.
     */
    public String getDataTableId(int tableNum) {
        WebElement tableElement = browser.driver.findElements(By.className("table")).get(tableNum);
        return tableElement.getAttribute("id");
    }

    public void clickElementById(String elementId) {
        WebElement element = browser.driver.findElement(By.id(elementId));
        click(element);
    }

    /**
     * Clicks the element and clicks 'Yes' in the follow up dialog box.
     * Fails if there is no dialog box.
     * @return the resulting page.
     */
    public AppPage clickAndConfirm(WebElement elementToClick) {
        click(elementToClick);
        waitForConfirmationModalAndClickOk();
        waitForPageToLoad();
        return this;
    }

    /**
     * Clicks the element and clicks 'Yes' in the follow up dialog box and will not wait for modal to disappear
     * Fails if there is no dialog box.
     */
    public void clickAndConfirmWithoutWaitingForModalDisappearance(WebElement elementToClick) {
        click(elementToClick);
        waitForModalShown();
        WebElement okayButton = browser.driver.findElement(By.className("modal-btn-ok"));
        waitForElementToBeClickable(okayButton);
        click(okayButton);
    }

    /**
     * Clicks the element and clicks 'No' in the follow up dialog box.
     * Fails if there is no dialog box.
     */
    public void clickAndCancel(WebElement elementToClick) {
        click(elementToClick);
        waitForConfirmationModalAndClickCancel();
        waitForPageToLoad();
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
     * Returns true if the element is invisible or stale as defined in the WebDriver specification.
     * @param locator used to find the element
     */
    public boolean isElementInvisibleOrStale(By locator) {
        return isExpectedCondition(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    /**
     * Returns true if there exists an element with the given id and class name.
     *
     * @param elementId
     *            Id of the element
     * @param targetClass
     *            className
     */
    public boolean isElementHasClass(String elementId, String targetClass) {
        List<WebElement> elementsMatched =
                browser.driver.findElements(By.cssSelector("#" + elementId + "." + targetClass));
        return !elementsMatched.isEmpty();
    }

    public boolean isNamedElementVisible(String elementName) {
        try {
            return browser.driver.findElement(By.name(elementName)).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public boolean isElementEnabled(String elementId) {
        try {
            return browser.driver.findElement(By.id(elementId)).isEnabled();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public boolean isNamedElementEnabled(String elementName) {
        try {
            return browser.driver.findElement(By.name(elementName)).isEnabled();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public boolean isElementSelected(String elementId) {
        try {
            return browser.driver.findElement(By.id(elementId)).isSelected();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Checks if the midpoint of an element is covered by any other element.
     * @return true if element is covered, false otherwise.
     */
    public boolean isElementCovered(WebElement element) {
        int x = element.getLocation().x + element.getSize().width / 2;
        int y = element.getLocation().y + element.getSize().height / 2;
        WebElement topElem = (WebElement) executeScript("return document.elementFromPoint(" + x + "," + y + ");");
        return !topElem.equals(element);
    }

    public void verifyUnclickable(WebElement element) {
        if (element.getTagName().equals("a")) {
            assertTrue(element.getAttribute("class").contains("disabled"));
        } else {
            assertNotNull(element.getAttribute("disabled"));
        }
    }

    /**
     * Compares selected column's rows with patternString to check the order of rows.
     * This can be useful in checking if the table is sorted in a particular order.
     * Separate rows using {*}
     * e.g., {@code "value 1{*}value 2{*}value 3" }
     * The header row will be ignored
     */
    public void verifyTablePattern(int column, String patternString) {
        verifyTablePattern(0, column, patternString);
    }

    /**
     * Compares selected column's rows with patternString to check the order of rows.
     * This can be useful in checking if the table is sorted in a particular order.
     * Separate rows using {*}
     * e.g., {@code "value 1{*}value 2{*}value 3" }
     * The header row will be ignored
     */
    public void verifyTablePattern(int tableNum, int column, String patternString) {
        String[] splitString = patternString.split(Pattern.quote("{*}"));
        int expectedNumberOfRowsInTable = splitString.length + 1;
        assertEquals(expectedNumberOfRowsInTable, getNumberOfRowsFromDataTable(tableNum));
        for (int row = 1; row < splitString.length; row++) {
            String tableCellString = this.getCellValueFromDataTable(tableNum, row, column);
            assertEquals(splitString[row - 1], tableCellString);
        }
    }

    /**
     * Verifies that the currently loaded page has the same HTML content as
     * the content given in the file at {@code filePath}. <br>
     * The HTML is checked for logical equivalence, not text equivalence.
     * @param filePath
     *         If this starts with "/" (e.g., "/expected.html"), the
     *         folder is assumed to be {@link TestProperties#TEST_PAGES_FOLDER}.
     * @return The page (for chaining method calls).
     */
    public AppPage verifyHtml(String filePath) throws IOException {
        return verifyHtmlPart(null, filePath);
    }

    /**
     * Verifies that element specified in currently loaded page has the same HTML content as
     * the content given in the file at {@code filePath}. <br>
     * The HTML is checked for logical equivalence, not text equivalence.
     * @param filePathParam
     *         If this starts with "/" (e.g., "/expected.html"), the
     *         folder is assumed to be {@link TestProperties#TEST_PAGES_FOLDER}.
     * @return The page (for chaining method calls).
     */
    public AppPage verifyHtmlPart(By by, String filePathParam) throws IOException {
        String filePath = (filePathParam.startsWith("/") ? TestProperties.TEST_PAGES_FOLDER : "") + filePathParam;
        boolean isPart = by != null;
        String actual = getPageSource(by);
        try {
            String expected = FileHelper.readFile(filePath);
            expected = HtmlHelper.injectTestProperties(expected);

            // The check is done multiple times with waiting times in between to account for
            // certain elements to finish loading (e.g ajax load, panel collapsing/expanding).
            for (int i = 0; i < VERIFICATION_RETRY_COUNT; i++) {
                if (i == VERIFICATION_RETRY_COUNT - 1) {
                    // Last retry count: do one last attempt and if it still fails,
                    // throw assertion error and show the differences
                    HtmlHelper.assertSameHtml(expected, actual, isPart);
                    break;
                }
                if (HtmlHelper.areSameHtml(expected, actual, isPart)) {
                    break;
                }
                ThreadHelper.waitFor(VERIFICATION_RETRY_DELAY_IN_MS);
                actual = getPageSource(by);
            }

        } catch (IOException | AssertionError e) {
            if (!testAndRunGodMode(filePath, actual, isPart)) {
                throw e;
            }
        }

        return this;
    }

    private String getPageSource(By by) {
        waitForAjaxLoaderGifToDisappear();
        String actual = by == null ? browser.driver.findElement(By.tagName("html")).getAttribute("innerHTML")
                                   : browser.driver.findElement(by).getAttribute("outerHTML");
        return HtmlHelper.processPageSourceForHtmlComparison(actual);
    }

    private boolean testAndRunGodMode(String filePath, String content, boolean isPart) throws IOException {
        return TestProperties.IS_GODMODE_ENABLED && regenerateHtmlFile(filePath, content, isPart);
    }

    private boolean regenerateHtmlFile(String filePath, String content, boolean isPart) throws IOException {
        if (content == null || content.isEmpty()) {
            return false;
        }

        TestProperties.verifyReadyForGodMode();
        String processedPageSource = HtmlHelper.processPageSourceForExpectedHtmlRegeneration(content, isPart);
        FileHelper.saveFile(filePath, processedPageSource);
        return true;
    }

    /**
     * Verifies that main content specified id "mainContent" in currently
     * loaded page has the same HTML content as
     * the content given in the file at {@code filePath}. <br>
     * The HTML is checked for logical equivalence, not text equivalence.
     * @param filePath
     *         If this starts with "/" (e.g., "/expected.html"), the
     *         folder is assumed to be {@link TestProperties#TEST_PAGES_FOLDER}.
     * @return The page (for chaining method calls).
     */
    public AppPage verifyHtmlMainContent(String filePath) throws IOException {
        return verifyHtmlPart(MAIN_CONTENT, filePath);
    }

    public AppPage verifyHtmlMainContentWithReloadRetry(final String filePath)
            throws IOException, MaximumRetriesExceededException {
        return persistenceRetryManager.runUntilNoRecognizedException(new RetryableTaskReturnsThrows<AppPage, IOException>(
                "HTML verification") {
            @Override
            public AppPage run() throws IOException {
                return verifyHtmlPart(MAIN_CONTENT, filePath);
            }

            @Override
            public void beforeRetry() {
                reloadPage();
            }
        }, AssertionError.class);
    }

    /**
     * Verifies that the title of the loaded page is the same as {@code expectedTitle}.
     */
    public void verifyTitle(String expectedTitle) {
        assertEquals(expectedTitle, browser.driver.getTitle());
    }

    /**
     * Also supports the expression "{*}" which will match any text.
     * e.g. "team 1{*}team 2" will match "team 1 xyz team 2"
     */
    public AppPage verifyContains(String searchString) {
        AssertHelper.assertContainsRegex(searchString, getPageSource());
        return this;
    }

    public void verifyContainsElement(By childBy) {
        assertFalse(browser.driver.findElements(childBy).isEmpty());
    }

    public void verifyElementContainsElement(WebElement parentElement, By childBy) {
        assertFalse(parentElement.findElements(childBy).isEmpty());
    }

    public void verifyElementDoesNotContainElement(WebElement parentElement, By childBy) {
        assertTrue(parentElement.findElements(childBy).isEmpty());
    }

    /**
     * Waits and verifies that the texts of user status messages in the page are equal to the expected texts.
     * The check is done multiple times with waiting times in between to account for
     * timing issues due to page load, inconsistencies in Selenium API, etc.
     */
    public void waitForTextsForAllStatusMessagesToUserEquals(String firstExpectedText, String... remainingExpectedTexts) {
        List<String> expectedTexts = Arrays.asList(ObjectArrays.concat(firstExpectedText, remainingExpectedTexts));
        try {
            uiRetryManager.runUntilNoRecognizedException(new RetryableTask("Verify status to user") {
                @Override
                public void run() {
                    // Scroll to status message because it must be visible in order to get its text
                    new Actions(browser.driver).moveToElement(statusMessage).perform();
                    waitForElementVisibility(statusMessage);

                    assertEquals(expectedTexts, getTextsForAllStatusMessagesToUser());
                }
            }, WebDriverException.class, AssertionError.class);
        } catch (MaximumRetriesExceededException e) {
            assertEquals(expectedTexts, getTextsForAllStatusMessagesToUser());
        }
    }

    /**
     * As of now, this simply verifies that the link is not broken. It does
     * not verify whether the file content is as expected. To be improved.
     */
    public void verifyDownloadLink(Url url) {
        //TODO: implement a better way to download a file and check content
        // (may be using HtmlUnit as the Webdriver?)
        String beforeReportDownloadUrl = browser.driver.getCurrentUrl();
        browser.driver.get(url.toAbsoluteString());
        String afterReportDownloadUrl = browser.driver.getCurrentUrl();
        assertEquals(beforeReportDownloadUrl, afterReportDownloadUrl);
    }

    public void verifyFieldValue(String fieldId, String expectedValue) {
        assertEquals(expectedValue,
                browser.driver.findElement(By.id(fieldId)).getAttribute("value"));
    }

    /**
     * Verifies that the page source does not contain the given searchString.
     *
     * @param searchString the substring that we want to omit from the page source
     * @return the AppPage
     */
    public AppPage verifyNotContain(String searchString) {
        String pageSource = getPageSource();
        assertFalse(pageSource.contains(searchString));
        return this;
    }

    public void waitForAjaxLoaderGifToDisappear() {
        try {
            waitForElementToDisappear(By.xpath("//img[@src='/images/ajax-loader.gif' or @src='/images/ajax-preload.gif']"));
        } catch (NoSuchElementException alreadydisappears) {
            // ok to ignore
            return;
        }
    }

    public void verifyImageUrl(String urlRegex, String imgSrc) {
        if (Const.SystemParams.DEFAULT_PROFILE_PICTURE_PATH.equals(urlRegex)) {
            verifyDefaultImageUrl(imgSrc);
        } else {
            AssertHelper.assertContainsRegex(urlRegex, imgSrc);
        }
    }

    public void verifyDefaultImageUrl(String imgSrc) {
        openNewWindow(imgSrc);
        switchToNewWindow();
        assertEquals(TestProperties.TEAMMATES_URL + Const.SystemParams.DEFAULT_PROFILE_PICTURE_PATH,
                browser.driver.getCurrentUrl());
        browser.closeCurrentWindowAndSwitchToParentWindow();
    }

    /**
     * Returns if the input element is valid (satisfies constraint validation). Note: This method will return false if the
     * input element is not a candidate for constraint validation (e.g. when input element is disabled).
     */
    public boolean isInputElementValid(WebElement inputElement) {
        checkArgument(inputElement.getAttribute("nodeName").equals("INPUT"));

        return (boolean) executeScript("return arguments[0].willValidate && arguments[0].checkValidity();", inputElement);
    }

    public void changeToMobileView() {
        browser.driver.manage().window().setSize(new Dimension(360, 640));
    }

    public void changeToDesktopView() {
        browser.driver.manage().window().maximize();
    }

    /**
     * Returns true if the element is in the user's visible area of a web page.
     */
    public boolean isElementInViewport(String id) {
        String script = "return isWithinView(document.getElementById('" + id + "'));";
        return (boolean) executeScript(script);
    }

    private void openNewWindow(String url) {
        executeScript("$(window.open('" + url + "'))");
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
     * <p>As compared to {@link Actions#moveToElement(WebElement)}, this method is more reliable as the element will not get
     * blocked by elements such as the header.
     *
     * <p>Furthermore, {@link Actions#moveToElement(WebElement)} is currently not working in Geckodriver.
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
        executeScript("const elementRect = arguments[0].getBoundingClientRect();"
                + "const elementAbsoluteTop = elementRect.top + window.pageYOffset;"
                + "const center = elementAbsoluteTop - (window.innerHeight / 2);"
                + "window.scrollTo(0, center);", element);
        element.click();
    }

    /**
     * Helper methods for detecting the state of a single JQuery AJAX request in the page. If more than one AJAX request is
     * made at the same time, the behavior is undefined.
     *
     * <p><b>Note:</b> If {@code $.ajax()} or {@code $.ajaxSetup()} is called with the {@code global} option set to
     * {@code false},the methods cannot work correctly.
     */
    class JQueryAjaxHandler {
        /**
         * The attribute that tracks if an AJAX request is started and not yet complete,
         * i.e. {@code ajaxStart} is triggered and {@code ajaxStop} is not yet triggered.
         */
        private static final String START_ATTRIBUTE = "__ajaxStart__";
        /**
         * The attribute that tracks if an AJAX request is complete,
         * i.e. {@code ajaxStop} is triggered.
         */
        private static final String STOP_ATTRIBUTE = "__ajaxStop__";

        /**
         * The attribute that tracks if an AJAX request is started, and may or may not be complete,
         * i.e. {@code ajaxStart} is triggered.
         */
        private static final String START_OCCURRED_ATTRIBUTE = "__ajaxStartOccurred__";

        /**
         * Registers `ajaxStart` and `ajaxStop` handlers to track the state of an AJAX request.
         *
         * @throws IllegalStateException if the handlers are already registered in the document
         */
        void registerHandlers() {
            checkState(!hasHandlers(), "`ajaxStart` and `ajaxStop` handlers need only be added once to the document.");

            executeScript("const seleniumArguments = arguments;"
                            + "$(document).ajaxStart(function() {"
                            + "    document.body.setAttribute(seleniumArguments[0], true);"
                            + "    document.body.setAttribute(seleniumArguments[1], false);"
                            + "    document.body.setAttribute(seleniumArguments[2], true);"
                            + "});"
                            + "$(document).ajaxStop(function() {"
                            + "    document.body.setAttribute(seleniumArguments[0], false);"
                            + "    document.body.setAttribute(seleniumArguments[1], true);"
                            + "});"
                            + "document.body.setAttribute(seleniumArguments[0], false);"
                            + "document.body.setAttribute(seleniumArguments[1], false);"
                            + "document.body.setAttribute(seleniumArguments[2], false);",
                    START_ATTRIBUTE, STOP_ATTRIBUTE, START_OCCURRED_ATTRIBUTE);
        }

        /**
         * Unregisters `ajaxStart` and `ajaxStop` handlers.
         *
         * @throws IllegalStateException if there are no registered handlers to unregister
         */
        private void unregisterHandlers() {
            checkState(hasHandlers(), "`ajaxStart` and `ajaxStop` handlers are not registered. Cannot unregister!");

            executeScript("$(document).off('ajaxStart');"
                            + "$(document).off('ajaxStop');"
                            + "document.body.removeAttribute(arguments[0]);"
                            + "document.body.removeAttribute(arguments[1]);"
                            + "document.body.removeAttribute(arguments[2]);",
                    START_ATTRIBUTE, STOP_ATTRIBUTE, START_OCCURRED_ATTRIBUTE);
        }

        /**
         * Returns true if `ajaxStart` and `ajaxStop` handlers exist.
         */
        private boolean hasHandlers() {
            WebElement bodyElement = browser.driver.findElement(By.tagName("body"));

            return isExpectedCondition(ExpectedConditions.and(
                    ExpectedConditions.attributeToBeNotEmpty(bodyElement, START_ATTRIBUTE),
                    ExpectedConditions.attributeToBeNotEmpty(bodyElement, STOP_ATTRIBUTE),
                    ExpectedConditions.attributeToBeNotEmpty(bodyElement, START_OCCURRED_ATTRIBUTE)));
        }

        /**
         * Waits for an AJAX request to complete and automatically unregisters the handlers.
         * <b>Note:</b> The behavior is undefined if more than one AJAX request was made after the the registration of the
         * handlers.
         */
        void waitForRequestComplete() {
            checkState(hasHandlers(),
                    "`ajaxStart` and `ajaxStop` handlers are not registered. Cannot detect if AJAX request is complete!");

            WebElement bodyElement = browser.driver.findElement(By.tagName("body"));

            waitFor(ExpectedConditions.and(
                    // Make sure that only a single AJAX request has previously occurred, this will be false if an AJAX
                    // request was made while there are other outstanding AJAX requests.
                    ExpectedConditions.attributeContains(bodyElement, START_OCCURRED_ATTRIBUTE, "true"),
                    ExpectedConditions.attributeContains(bodyElement, START_ATTRIBUTE, "false"),
                    ExpectedConditions.attributeContains(bodyElement, STOP_ATTRIBUTE, "true")));

            // Any AJAX requests made while executing the following script will result in undefined behavior.
            executeScript("document.body.setAttribute(arguments[0], false);"
                            + "document.body.setAttribute(arguments[1], false);"
                            + "document.body.setAttribute(arguments[2], false);",
                    START_ATTRIBUTE, STOP_ATTRIBUTE, START_OCCURRED_ATTRIBUTE);

            unregisterHandlers();
        }
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

            executeScript(
                    "const seleniumArguments = arguments;"
                    + "seleniumArguments[0].addEventListener(seleniumArguments[1], function onchange() {"
                    + "    this.removeEventListener(seleniumArguments[1], onchange);"
                    + "    document.body.setAttribute(seleniumArguments[2], true);"
                    + "});"
                    + "document.body.setAttribute(seleniumArguments[2], false);",
                    element, CHANGE_EVENT, HOOK_ATTRIBUTE);
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
