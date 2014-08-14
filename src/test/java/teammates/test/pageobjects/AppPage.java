package teammates.test.pageobjects;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;
import static org.testng.AssertJUnit.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.params.HttpParams;
import org.cyberneko.html.parsers.DOMParser;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.remote.UselessFileDetector;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import teammates.common.util.Assumption;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.FileHelper;
import teammates.common.util.ThreadHelper;
import teammates.common.util.TimeHelper;
import teammates.common.util.Url;
import teammates.common.util.Utils;
import teammates.test.driver.AssertHelper;
import teammates.test.driver.HtmlHelper;
import teammates.test.driver.TestProperties;

/**
 * An abstract class that represents a browser-loaded page of the app and
 * provides ways to interact with it. Also contains methods to validate some
 * aspects of the page. .e.g, html page source. <br>
 * 
 * Note: We are using the PageObjects pattern here. 
 * https://code.google.com/p/selenium/wiki/PageObjects
 * 
 */
@SuppressWarnings("deprecation")
public abstract class AppPage {
    protected static Logger log = Utils.getLogger();
    /**Home page of the application, as per test.properties file*/
    protected static final String HOMEPAGE = TestProperties.inst().TEAMMATES_URL;
    
    static final long ONE_MINUTE_IN_MILLIS=60000;
    
    /** Browser instance the page is loaded into */
    protected Browser browser;
    
    /** These are elements common to most pages in our app */
    @SuppressWarnings("unused")
    private void ____Common_page_elements___________________________________() {
    }
    @FindBy(id = "statusMessage")
    protected WebElement statusMessage;
    
    @FindBy(xpath = "//*[@id=\"contentLinks\"]/ul[1]/li[1]/a")
    protected WebElement instructorHomeTab;
    
    @FindBy(xpath = "//*[@id=\"contentLinks\"]/ul[1]/li[2]/a")
    protected WebElement instructorCoursesTab;
    
    @FindBy(xpath = "//*[@id=\"contentLinks\"]/ul[1]/li[3]/a")
    protected WebElement instructorEvaluationsTab;
    
    @FindBy(xpath = "//*[@id=\"contentLinks\"]/ul[1]/li[4]/a")
    protected WebElement instructorStudentsTab;
    
    @FindBy(xpath = "//*[@id=\"contentLinks\"]/ul[1]/li[5]/a")
    protected WebElement instructorCommentsTab;
    
    @FindBy(xpath = "//*[@id=\"contentLinks\"]/ul[1]/li[7]/a")
    protected WebElement instructorHelpTab;
    
    @FindBy(xpath = "//*[@id=\"contentLinks\"]/ul[2]/li[1]/a")
    protected WebElement instructorLogoutLink;
    
    @FindBy(id = "studentHomeNavLink")
    protected WebElement studentHomeTab;
    
    @FindBy(id = "studentProfileNavLink")
    protected WebElement studentProfileTab;
    
    @FindBy(id = "studentCommentsNavLink")
    protected WebElement studentCommentsTab;
    
    @FindBy(id = "studentHelpLink")
    protected WebElement studentHelpTab;
    
    @FindBy(xpath = "//*[@id=\"contentLinks\"]/ul[2]/li[1]/a")
    protected WebElement studentLogoutLink;
    
    @SuppressWarnings("unused")
    private void ____creation_and_navigation_______________________________() {
    }
    
    /**
     * Used by subclasses to create a {@code AppPage} object to wrap around the
     * given {@code browser} object. Fails if the page content does not match
     * the page type, as defined by the sub-class.
     */
    public AppPage(Browser browser) {
        this.browser = browser;
        boolean isCorrectPageType = containsExpectedPageContents();
        
        if (isCorrectPageType) { return; }
        
        // To minimize test failures due to eventual consistency, we try to
        //  reload the page and compare once more.
        System.out.println("#### Incorrect page type: going to try reloading the page.");
        
        ThreadHelper.waitFor(2000);
        
        this.reloadPage();
        isCorrectPageType = containsExpectedPageContents();
        
        if (isCorrectPageType) { return; }
        
        System.out.println("######### Not in the correct page! ##########");
        throw new IllegalStateException("Not in the correct page!");
    }
    

    /**
     * Fails if the new page content does not match content expected in a page of
     * the type indicated by the parameter {@code typeOfPage}.
     */
    public static <T extends AppPage> T getNewPageInstance(Browser currentBrowser, Url url, Class<T> typeOfPage){
        currentBrowser.driver.get(url.toString());
        return createNewPage(currentBrowser, typeOfPage);
    }

    /**
     * Fails if the new page content does not match content expected in a page of
     * the type indicated by the parameter {@code typeOfPage}.
     */
    public static <T extends AppPage> T getNewPageInstance(Browser currentBrowser, Class<T> typeOfPage){
        return createNewPage(currentBrowser, typeOfPage);
    }
    
    /**
     * Gives an AppPage instance based on the given Browser.
     */
    public static AppPage getNewPageInstance(Browser currentBrowser){
        return getNewPageInstance(currentBrowser, GenericAppPage.class);
    }

    /**
     * Fails if the new page content does not match content expected in a page of
     * the type indicated by the parameter {@code typeOfPage}.
     */
    public static <T extends AppPage> T getNewPageInstance(String url, Class<T> typeOfPage){
        Browser b = new Browser();
        b.driver.get(url);
        return createNewPage(b, typeOfPage);
    }

    /**
     * Fails if the new page content does not match content expected in a page of
     * the type indicated by the parameter {@code typeOfDestinationPage}.
     */
    public <T extends AppPage> T navigateTo(Url url, Class<T> typeOfDestinationPage){
        return getNewPageInstance(browser, url, typeOfDestinationPage);
    }
    
    /**
     * Simply loads the given URL. 
     */
    public AppPage navigateTo(Url url){
        browser.driver.get(url.toString());
        return this;
    }

    /**
     * Fails if the new page content does not match content expected in a page of
     * the type indicated by the parameter {@code newPageType}.
     */
    public <T extends AppPage> T changePageType(Class<T> newPageType) {
        return createNewPage(browser, newPageType);
    }

    /**
     * Waits until the page is fully loaded. Times out after 15 seconds.
     */
    protected void waitForPageToLoad() {
        browser.selenium.waitForPageToLoad("15000");
    }
    
    protected void waitForElementToBecomeVisible(String elementId) throws Exception {
        int timeOut = 3000;
        while (!browser.driver.findElement(By.id(elementId)).isDisplayed()
                && timeOut > 0) {
            Thread.sleep(100);
            timeOut -= 100;
        }
        return;
    }
    
    protected void waitForElementToAppear(By by) throws Exception {
        int timeOut = 3000;
        while (timeOut > 0) {
            try {
                if (browser.driver.findElement(by).isDisplayed()) {
                    break;
                }
            } catch (NoSuchElementException e) {
                // ignore exception
            }
            Thread.sleep(100);
            timeOut -= 100;
        }
        return;
    }
    
    public void waitForElementVisible(WebElement element){
        WebDriverWait wait = new WebDriverWait(browser.driver, 10);
        wait.until(ExpectedConditions.visibilityOf(element));
    }
    
    /**
     * Waits for element to be invisible or not present, or timeout.
     */
    public void waitForElementToDisappear(By by){
        WebDriverWait wait = new WebDriverWait(browser.driver, 10);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(by));
    }
    
    /**
     * Waits for the element to appear in the page, up to the timeout specified.
     */
    public void waitForElementPresence(By element, int timeOutInSeconds){
        WebDriverWait wait = new WebDriverWait(browser.driver, timeOutInSeconds);
        wait.until(presenceOfElementLocated(element));
    }
    
    /**
     * Switches to the new browser window just opened.
     */
    protected void switchToNewWindow() {
        String curWin = browser.driver.getWindowHandle();
        for (String handle : browser.driver.getWindowHandles()) {
            if (handle.equals(curWin))
                continue;
            browser.selenium.selectWindow(handle);
            browser.selenium.windowFocus();
        }
    }
    
    public void closeCurrentWindowAndSwitchToParentWindow() {
        browser.selenium.close();
        switchToParentWindow();
    }
    
    public void switchToParentWindow() {
        browser.selenium.selectWindow("null");
        browser.selenium.windowFocus();
    }

    public void reloadPage() {
        browser.driver.get(browser.driver.getCurrentUrl());
        waitForPageToLoad();
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
    public AppPage loadCoursesTab() {
        instructorCoursesTab.click();
        waitForPageToLoad();
        return this;
    }
    
    /**
     * Equivalent to clicking the 'Students' tab on the top menu of the page.
     * @return the loaded page.
     */
    public AppPage loadStudentsTab() {
        instructorStudentsTab.click();
        waitForPageToLoad();
        return this;
    }
    
    
    /**
     * Equivalent to clicking the 'Home' tab on the top menu of the page.
     * @return the loaded page.
     */
    public AppPage loadInstructorHomeTab() {
        instructorHomeTab.click();
        waitForPageToLoad();
        return this;
    }
    
    /**
     * Equivalent to clicking the 'Help' tab on the top menu of the page.
     * @return the loaded page.
     */
    public AppPage loadInstructorHelpTab() {
        instructorHelpTab.click();
        waitForPageToLoad();
        return this;
    }
    
    /**
     * Equivalent to clicking the 'Comments' tab on the top menu of the page.
     * @return the loaded page.
     */
    public AppPage loadInstructorCommentsTab() {
        instructorCommentsTab.click();
        waitForPageToLoad();
        return this;
    }

    /**
     * Equivalent to clicking the 'Evaluations' tab on the top menu of the page.
     * @return the loaded page.
     */
    public AppPage loadEvaluationsTab() {
        instructorEvaluationsTab.click();
        waitForPageToLoad();
        return this;
    }
    
    /**
     * Equivalent of clicking the 'Profile' tab on the top menu of the page.
     * @return the loaded page
     */
    public StudentProfilePage loadProfileTab() {
        studentProfileTab.click();
        waitForPageToLoad();
        return changePageType(StudentProfilePage.class);
    }
    
    /**
     * Equivalent of student clicking the 'Home' tab on the top menu of the page.
     * @return the loaded page
     */
    public StudentHomePage loadStudentHomeTab() {
        studentHomeTab.click();
        waitForPageToLoad();
        return changePageType(StudentHomePage.class);
    }
    
    /**
     * Equivalent of student clicking the 'Comments' tab on the top menu of the page.
     * @return the loaded page
     */
    public StudentCommentsPage loadStudentCommentsTab() {
        studentCommentsTab.click();
        waitForPageToLoad();
        return changePageType(StudentCommentsPage.class);
    }

    /**
     * Equivalent to clicking the 'logout' link in the top menu of the page.
     * @return 
     */
    public AppPage logout(){
        logout(browser);
        return this;
    }
    
    /**
     * Equivalent to clicking the 'logout' link in the top menu of the page.
     */
    public static void logout(Browser currentBrowser){
        currentBrowser.driver.get(TestProperties.inst().TEAMMATES_URL + Const.ViewURIs.LOGOUT);
        currentBrowser.isAdminLoggedIn = false;
    }
    
    @SuppressWarnings("unused")
    private void ____accessing_elements___________________________________() {
    }
    
    /**
     * @return the HTML source of the currently loaded page.
     */
    public String getPageSource() {
        return browser.driver.getPageSource();
    }

    
    /**
     * This can be used to save pages which can later be used as the 'expected'
     * in UI test cases. After saving the file, remember to edit it manually and
     *  replace the version number in the page footer with the string 
     * "{$version}". so that the test can insert the correct version number 
     * before comparing the 'expected' with the 'actual.
     *  e.g., replace "V4.55" in the page footer by "V{$version}".
     *  @param filePath If the full path is not given, it will be saved in the
     *  {@code Common.TEST_PAGES_FOLDER} folder. In that case, the parameter
     *  value should start with "/". e.g., "/instructorHomePage.html".
     */
    public void saveCurrentPage(String filePath, String content) throws Exception {
        
        try {
            FileWriter output = new FileWriter(new File(filePath));
            output.write(content);
            output.close();
        } catch (Exception e) {
            throw e;
        }
    }

    public void click(By by) {
        WebElement element = browser.driver.findElement(by);
        element.click();
    }
    
    public String getElementAttribute(By locator, String attrName) {
        return browser.driver.findElement(locator).getAttribute(attrName);
    }
    
    protected void fillTextBox(WebElement textBoxElement, String value) {
        textBoxElement.click();
        textBoxElement.clear();
        textBoxElement.sendKeys(value + Keys.TAB + Keys.TAB + Keys.TAB);
    }
    
    protected void fillFileBox(RemoteWebElement fileBoxElement, String fileName) throws Exception {
        if (fileName.isEmpty()) return;
        fileBoxElement.setFileDetector(new UselessFileDetector());
        String newFilePath = new File(fileName).getAbsolutePath();
        fileBoxElement.sendKeys(newFilePath);
    }

    protected String getTextBoxValue(WebElement textBox) {
        return textBox.getAttribute("value");
    }

    /** 'check' the check box, if it is not already 'checked'.
     * No action taken if it is already 'checked'.
     */
    protected void markCheckBoxAsChecked(WebElement checkBox) {
        if(!checkBox.isSelected()){
            checkBox.click();
        }
    }

    /** 'uncheck' the check box, if it is already 'checked'.
     * No action taken if it is not already 'checked'.
     */
    protected void markCheckBoxAsUnchecked(WebElement checkBox) {
        if(checkBox.isSelected()){
            checkBox.click();
        }
    }

    /** 
     * Selection is based on the value shown to the user. 
     * Since selecting an option by clicking on the option doesn't work sometimes
     * in Firefox, we simulate a user typing the value to select the option
     * instead (i.e., we use the {@code sendKeys()} method). <br>
     * <br>
     * The method will fail with an AssertionError if the selected value is
     * not the one we wanted to select.
     */
    public void selectDropdownByVisibleValue(WebElement element, String value) {
        Select select = new Select(element);
        select.selectByVisibleText(value);
        String selectedVisibleValue = select.getFirstSelectedOption().getText();
        assertEquals(value, selectedVisibleValue);
        element.sendKeys(Keys.RETURN);
    }
    
    /** 
     * Selection is based on the actual value. 
     * Since selecting an option by clicking on the option doesn't work sometimes
     * in Firefox, we simulate a user typing the value to select the option
     * instead (i.e., we use the {@code sendKeys()} method). <br>
     * <br>
     * The method will fail with an AssertionError if the selected value is
     * not the one we wanted to select.
     */
    public void selectDropdownByActualValue(WebElement element, String value) {
        Select select = new Select(element);
        select.selectByValue(value);
        String selectedVisibleValue = select.getFirstSelectedOption().getAttribute("value");
        assertEquals(value, selectedVisibleValue);
        element.sendKeys(Keys.RETURN);
    }

    /**
     * @return the status message in the page. Returns "" if there is no 
     * status message in the page.
     */
    public String getStatus() {
        return statusMessage == null? "" : statusMessage.getText();
    }

    /** 
     * @return the value of the cell located at {@code (row,column)} 
     * from the first table (which is of type {@code class=table}) in the page.
     */
    public String getCellValueFromDataTable(int row, int column) {
        return browser.selenium.getTable("css=table[class~='table']." + row + "." + column);
    }
    
    /** 
     * @return the value of the cell located at {@code (row,column)} 
     * from the nth(0-index-based) table (which is of type {@code class=table}) in the page.
     */
    public String getCellValueFromDataTable(int tableNum, int row, int column) {
        WebElement tableElement = browser.driver.findElements(By.className("table")).get(tableNum);
        WebElement trElement = tableElement.findElements(By.tagName("tr")).get(row);
        WebElement tdElement = trElement.findElements(By.tagName("td")).get(column);
        return tdElement.getText();
    }
    
    /** 
     * @return the number of rows from the nth(0-index-based) table 
     * (which is of type {@code class=table}) in the page.
     */
    public int getNumberOfRowsFromDataTable(int tableNum) {
        WebElement tableElement = browser.driver.findElements(By.className("table")).get(tableNum);
       return tableElement.findElements(By.tagName("tr")).size();
    }

    /**
     * Clicks the element and clicks 'Yes' in the follow up dialog box. 
     * Fails if there is no dialog box.
     * @return the resulting page.
     */
    public AppPage clickAndConfirm(WebElement elementToClick) {
        respondToAlertWithRetry(elementToClick, true);
        waitForPageToLoad();
        return this;
    }
    
    /**
     * Clicks the hidden element and clicks 'Yes' in the follow up dialog box. 
     * Fails if there is no dialog box.
     * @return the resulting page.
     */
    public AppPage clickHiddenElementAndConfirm(String elementId) {
        respondToAlertWithRetryForHiddenElement(elementId, true);
        waitForPageToLoad();
        return this;
    }
    
    /**
     * Clicks the element and clicks 'No' in the follow up dialog box. 
     * Fails if there is no dialog box.
     * @return the resulting page.
     */
    public void clickAndCancel(WebElement elementToClick){
        respondToAlertWithRetry(elementToClick, false);
        waitForPageToLoad();
    }
    
    /**
     * Clicks the hidden element and clicks 'No' in the follow up dialog box. 
     * Fails if there is no dialog box.
     * @return the resulting page.
     */
    public void clickHiddenElementAndCancel(String elementId){
        respondToAlertWithRetryForHiddenElement(elementId, false);
        waitForPageToLoad();
    }
    
    @SuppressWarnings("unused")
    private void ____verification_methods___________________________________() {
    }

    /** @return True if the page contains some basic elements expected in a page of the
     * specific type. e.g., the top heading. 
     */
    protected abstract boolean containsExpectedPageContents() ;

    /**
     * @return True if there is a corresponding element for the given locator.
     */
    public boolean isElementPresent(By by) {
        return browser.driver.findElements(by).size() != 0;
    }
    
    /**
     * @return True if there is a corresponding element for the given id or name.
     */
    public boolean isElementPresent(String elementId) {
        try{
            browser.driver.findElement(By.id(elementId));
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }
    
    public boolean isElementVisible(String elementId) {
        try{
            return browser.driver.findElement(By.id(elementId)).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
    
    public boolean isNamedElementVisible(String elementName) {
        try{
            return browser.driver.findElement(By.name(elementName)).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
    
    public boolean isElementEnabled(String elementId) {
        try{
            return browser.driver.findElement(By.id(elementId)).isEnabled();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
    
    public boolean isNamedElementEnabled(String elementName) {
        try{
            return browser.driver.findElement(By.name(elementName)).isEnabled();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
    
    public boolean isElementSelected(String elementId) {
        try{
            return browser.driver.findElement(By.id(elementId)).isSelected();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public void verifyUnclickable(WebElement element){
        try {
            respondToAlertWithRetry(element, false);
            Assert.fail("This should not give an alert when clicked");
        } catch (NoAlertPresentException e) {
            return;
        }
    }

    /**
     * Compares selected column's rows with patternString to check the order of rows.
     * This can be useful in checking if the table is sorted in a particular order.
     * Separate rows using {*}
     * e.g., {@code "value 1{*}value 2{*}value 3" }
     * The header row will be ignored
     */
    public void verifyTablePattern(int column, String patternString){
        verifyTablePattern(0, column, patternString);
    }
    
    /**
     * Compares selected column's rows with patternString to check the order of rows.
     * This can be useful in checking if the table is sorted in a particular order.
     * Separate rows using {*}
     * e.g., {@code "value 1{*}value 2{*}value 3" }
     * The header row will be ignored
     */
    public void verifyTablePattern(int tableNum, int column, String patternString){
        String[] splitString = patternString.split(java.util.regex.Pattern.quote("{*}"));
        int expectedNumberOfRowsInTable = splitString.length + 1;
        assertEquals(expectedNumberOfRowsInTable, getNumberOfRowsFromDataTable(tableNum));
        for(int row=1;row < splitString.length;row++){
            String tableCellString = this.getCellValueFromDataTable(tableNum, row, column);
            assertEquals(splitString[row - 1], tableCellString);
        }
    }
    
    /**
     * Verifies that the currently loaded page has the same HTML content as 
     * the content given in the file at {@code filePath}. <br>
     * The HTML is checked for logical equivalence, not text equivalence. 
     * @param filePath If this starts with "/" (e.g., "/expected.html"), the 
     * folder is assumed to be {@link Const.TEST_PAGES_FOLDER}. 
     * @return The page (for chaining method calls).
     */
    public AppPage verifyHtml(String filePath) {
        // TODO: improve this method by insert header and footer
        //       to the file specified by filePath
        if(filePath.startsWith("/")){
            filePath = TestProperties.TEST_PAGES_FOLDER + filePath;
        }
        String actual = getPageSource();
        
        try {
            String expected = FileHelper.readFile(filePath);
            HtmlHelper.assertSameHtml(actual, expected);
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        } catch (AssertionError ae) {
            if (!testAndRunGodMode(filePath, actual)) {
                throw ae;
            }
        } 
        
        return this;
    }

    private boolean testAndRunGodMode(String filePath, String content) {        
        
        if (System.getProperty("godmode") != null && System.getProperty("godmode").equals("true")) {
            assert(TestProperties.inst().isDevServer());
            if (areTestAccountsDefaultValues()) {
                Assumption.fail("Please change ALL the default accounts in test.properties in order to use GodMode."
                        + "eg: change test.student1.account from alice.tmms to alice.tmms.example");
            }
            try {
                String processedPageSource = processPageSourceForGodMode(content);                
                saveCurrentPage(filePath, processedPageSource);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        } else {
            return false;
        }
    }

    private String processPageSourceForGodMode(String content) {
        Date now = new Date();
        assertEquals(new SimpleDateFormat("dd MMM yyyy, HH:mm").format(now), TimeHelper.formatTime(now));
        return content
                .replaceAll("<#comment[ ]*</#comment>", "<!---->")
                .replace(Config.APP_URL, "{$app.url}")
                .replaceAll("V[0-9]\\.[0-9]+", "V{\\$version}")
                // photo from instructor
                .replaceAll(Const.ActionURIs.STUDENT_PROFILE_PICTURE + "\\?" + Const.ParamsNames.STUDENT_EMAIL + "=([a-zA-Z0-9]){1,}\\&amp;"
                        + Const.ParamsNames.COURSE_ID + "=([a-zA-Z0-9]){1,}", 
                        Const.ActionURIs.STUDENT_PROFILE_PICTURE + "\\?" + Const.ParamsNames.STUDENT_EMAIL 
                        + "={*}\\&amp;" + Const.ParamsNames.COURSE_ID + "={*}")
                .replaceAll(Const.ActionURIs.STUDENT_PROFILE_PICTURE + "\\?" + Const.ParamsNames.COURSE_ID + "=([a-zA-Z0-9]){1,}\\&amp;"
                        + Const.ParamsNames.STUDENT_EMAIL + "=([a-zA-Z0-9]){1,}", 
                        Const.ActionURIs.STUDENT_PROFILE_PICTURE + "\\?" + Const.ParamsNames.COURSE_ID 
                        + "={*}\\&amp;" + Const.ParamsNames.STUDENT_EMAIL + "={*}")
                .replaceAll(Const.ParamsNames.REGKEY + "=([a-zA-Z0-9]){1,}\\&amp;", Const.ParamsNames.REGKEY + "={*}\\&amp;")
                .replaceAll(Const.ParamsNames.REGKEY + "%3D([a-zA-Z0-9]){1,}\\%", Const.ParamsNames.REGKEY + "%3D{*}\\%")
                //responseid
                .replaceAll("([a-zA-Z0-9-_]){30,}%"
                        + "[\\w+-][\\w+!#$%&'*/=?^_`{}~-]*+(\\.[\\w+!#$%&'*/=?^_`{}~-]+)*+@([A-Za-z0-9-]+\\.)*[A-Za-z]+%"
                        + "[\\w+-][\\w+!#$%&'*/=?^_`{}~-]*+(\\.[\\w+!#$%&'*/=?^_`{}~-]+)*+@([A-Za-z0-9-]+\\.)*[A-Za-z]+", "{*}")
                //questionid
                .replaceAll("([a-zA-Z0-9-_]){62,}","{*}")
                //commentid
                .replaceAll("\\\"([0-9]){16}\\\"", "\\\"{*}\\\"")
                //commentid in url
                .replaceAll("#[0-9]{16}", "#{*}")
                // the test accounts/ email
                .replace(TestProperties.inst().TEST_STUDENT1_ACCOUNT, "{$test.student1}")
                .replace(TestProperties.inst().TEST_STUDENT2_ACCOUNT, "{$test.student2}")
                .replace(TestProperties.inst().TEST_INSTRUCTOR_ACCOUNT, "{$test.instructor}")
                .replace(TestProperties.inst().TEST_ADMIN_ACCOUNT, "{$test.admin}")
                .replace(TestProperties.inst().TEST_UNREG_ACCOUNT, "{$test.unreg}")
                .replace(Config.SUPPORT_EMAIL, "{$support.email}")
                // today's date
                .replace(TimeHelper.formatDate(now), "{*}")
                // now (used in opening time/closing time Grace period)
                .replaceAll(new SimpleDateFormat("dd MMM yyyy, ").format(now) + "[0-9]{2}:[0-9]{2}", "{*}");
    }

    private boolean areTestAccountsDefaultValues() {
        return "alice.tmms".contains(TestProperties.inst().TEST_STUDENT1_ACCOUNT)
                || "charlie.tmms".contains(TestProperties.inst().TEST_STUDENT2_ACCOUNT)  
                || "teammates.unreg".contains(TestProperties.inst().TEST_UNREG_ACCOUNT) 
                || "teammates.coord".contains(TestProperties.inst().TEST_INSTRUCTOR_ACCOUNT)
                || "yourGoogleId".contains(TestProperties.inst().TEST_ADMIN_ACCOUNT);
    }
    
    /**
     * Verifies that element specified in currently loaded page has the same HTML content as 
     * the content given in the file at {@code filePath}. <br>
     * The HTML is checked for logical equivalence, not text equivalence. 
     * @param filePath If this starts with "/" (e.g., "/expected.html"), the 
     * folder is assumed to be {@link Const.TEST_PAGES_FOLDER}. 
     * @return The page (for chaining method calls).
     */
    public AppPage verifyHtmlPart(By by, String filePath) {
        WebElement element = browser.driver.findElement(by);
        if(filePath.startsWith("/")){
            filePath = TestProperties.TEST_PAGES_FOLDER + filePath;
        }
        String actual = element.getAttribute("outerHTML");
        
        try {
            String expected = extractHtmlPartFromFile(by, filePath);
            HtmlHelper.assertSameHtmlPart(actual, expected);            
        } catch(AssertionError ae) { 
            if(!testAndRunGodMode(filePath, actual)) {
                throw ae;
            }
        } catch (Exception e) {
            if(!testAndRunGodMode(filePath, actual)) {
                throw new RuntimeException(e);
            }
            
        }
        return this;
    }

    private String extractHtmlPartFromFile(By by, String filePath)
            throws SAXException, IOException {
        String byId = by.toString().split(":")[1].trim();
        
        DOMParser parser = new DOMParser();
        parser.parse(new InputSource(new BufferedReader(new FileReader(filePath))));
        org.w3c.dom.Document htmlDoc = parser.getDocument();
        org.w3c.dom.Element expectedElement = htmlDoc.getElementById(byId);
        StringBuilder expectedHtml = new StringBuilder();
        HtmlHelper.convertToStandardHtmlRecursively(expectedElement, "", expectedHtml, true);
        
        return expectedHtml.toString().replace("%20", " ")
                .replace("%27", "'")
                .replace("<#document", "")
                .replace("   <html   </html>", "")
                .replace("</#document>", "");
    }
    
    /**
     * Verifies that main content specified id "frameBodyWrapper" in currently 
     * loaded page has the same HTML content as 
     * the content given in the file at {@code filePath}. <br>
     * The HTML is checked for logical equivalence, not text equivalence. 
     * @param filePath If this starts with "/" (e.g., "/expected.html"), the 
     * folder is assumed to be {@link Const.TEST_PAGES_FOLDER}. 
     * @return The page (for chaining method calls).
     */
    public AppPage verifyHtmlMainContent(String filePath) {
        verifyHtmlPart(By.id("frameBodyWrapper"), filePath);
        
        return this;
    }
    
    /**
     * Verifies that the currently loaded page has the same HTML content as 
     * the content given in the file at {@code filePath}. <br>
     * Since the verification is done after making an Ajax Request, the HTML is checked
     * after "waitDuration", for "maxRetryCount" number of times.
     * @param filePath If this starts with "/" (e.g., "/expected.html"), the 
     * folder is assumed to be {@link Const.TEST_PAGES_FOLDER}. 
     * @return The page (for chaining method calls).
     */
    public AppPage verifyHtmlAjax(String filePath) throws Exception {
        int maxRetryCount = 5;
        int waitDuration = 1000;
        
        //Wait for loader gif loader to disappear.
        waitForElementToDisappear(By.cssSelector("img[src='/images/ajax-loader.gif']"));
        waitForElementToDisappear(By.cssSelector("img[src='/images/ajax-preload.gif']"));
        
        if(filePath.startsWith("/")){
            filePath = TestProperties.TEST_PAGES_FOLDER + filePath;
        }
        
        String expectedString = "";
        
        expectedString = extractHtmlPartFromFile(By.id("frameBodyWrapper"), filePath);
        
        for(int i =0; i < maxRetryCount; i++) {
            try {
                String actual = browser.driver.findElement(By.id("frameBodyWrapper")).getAttribute("outerHTML");
                if(HtmlHelper.areSameHtml(actual, expectedString)) {
                    break;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            ThreadHelper.waitFor(waitDuration);   
        }
        
        
        return verifyHtmlMainContent(filePath);
    }
    
    /**
     * Also supports the expression "{*}" which will match any text.
     * e.g. "team 1{*}team 2" will match "team 1 xyz team 2"
     */
    public AppPage verifyContains(String searchString) {
        AssertHelper.assertContainsRegex(searchString, getPageSource());
        return this;
    }
        
    /**
     * Verifies the status message in the page is same as the one specified.
     * @return The page (for chaining method calls).
     */
    public AppPage verifyStatus(String expectedStatus){
        
        try{
            assertEquals(expectedStatus, this.getStatus());
        } catch(Exception e){
            if(!expectedStatus.equals("")){
                this.waitForElementPresence(By.id("statusMessage"), 15);
                if(!statusMessage.isDisplayed()){
                    this.waitForElementVisible(statusMessage);
                }
            }
        }
        assertEquals(expectedStatus, this.getStatus());
        return this;
    }

    /**
     * As of now, this simply verifies that the link is not broken. It does
     * not verify whether the file content is as expected. To be improved.
     */
    public void verifyDownloadLink(Url url) {
        //TODO: implement a better way to download a file and check content 
        // (may be using HtmlUnit as the Webdriver?)
        String beforeReportDownloadUrl = browser.driver.getCurrentUrl();
        browser.driver.get(url.toString());
        String afterReportDownloadUrl = browser.driver.getCurrentUrl();
        assertEquals(beforeReportDownloadUrl, afterReportDownloadUrl);
    }
    
    /**
     * Verify if a file is downloadable based on the given url. If its downloadable,
     * download the file and get the SHA-1 hex of it and verify the hex with the given 
     * expected hash.
     * 
     * Compute the expected hash of a file from http://onlinemd5.com/ (SHA-1)
     */
    public void verifyDownloadableFile(String url, String expectedHash) throws Exception {
        
        if (!url.startsWith("http") ){
            url = HOMEPAGE + url;
        }
        
        URL fileToDownload = new URL(url);
        
        String localDownloadPath = System.getProperty("java.io.tmpdir");
        File downloadedFile = new File(localDownloadPath + fileToDownload.getFile().replaceFirst("/|\\\\", ""));
        
        if (downloadedFile.exists()){ 
            downloadedFile.delete();
        }
        if (downloadedFile.canWrite() == false){ 
            downloadedFile.setWritable(true);
        }
        
        CloseableHttpClient client = HttpClientBuilder.create().build();
        
        HttpGet httpget = new HttpGet(fileToDownload.toURI());
        HttpParams httpRequestParameters = httpget.getParams();
        httpRequestParameters.setParameter(ClientPNames.HANDLE_REDIRECTS, false);
        httpget.setParams(httpRequestParameters);
 
        HttpResponse response = client.execute(httpget);
        FileUtils.copyInputStreamToFile(response.getEntity().getContent(), downloadedFile);
        response.getEntity().getContent().close();
 
        String downloadedFileAbsolutePath = downloadedFile.getAbsolutePath();
        assertEquals(true, new File(downloadedFileAbsolutePath).exists());
        
        String actualHash = DigestUtils.shaHex(new FileInputStream(downloadedFile));
        assertEquals(expectedHash.toLowerCase(), actualHash);
        
        client.close();
    }
    
    public void verifyFieldValue (String fieldId, String expectedValue) {
        assertEquals(expectedValue,
                browser.driver.findElement(By.id(fieldId)).getAttribute("value"));
    }
        
    @SuppressWarnings("unused")
    private void ____private_utility_methods________________________________() {
    }
    
    private static <T extends AppPage> T createNewPage(Browser currentBrowser,    Class<T> typeOfPage) {
        Constructor<T> constructor;
        try {
            constructor = typeOfPage.getConstructor(Browser.class);
            T page = constructor.newInstance(currentBrowser);
            PageFactory.initElements(currentBrowser.driver, page);
            return page;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void respondToAlertWithRetry(WebElement elementToClick, boolean isConfirm) {
        elementToClick.click();    
        //This method might fail at times due to a Selenium bug
        //  See https://code.google.com/p/selenium/issues/detail?id=3544
        //  The delay below is a temporary workaround to minimize the failure rate.
        ThreadHelper.waitFor(250);
        Alert alert = browser.driver.switchTo().alert();
        if(isConfirm){
            alert.accept();
        }else {
            alert.dismiss();
        }
    }
    
    private void respondToAlertWithRetryForHiddenElement(String hiddenElementIdToClick, boolean isConfirm) {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) browser.driver;
        jsExecutor.executeScript("document.getElementById('"+hiddenElementIdToClick+"').click();");
        //This method might fail at times due to a Selenium bug
        //  See https://code.google.com/p/selenium/issues/detail?id=3544
        //  The delay below is a temporary workaround to minimize the failure rate.
        ThreadHelper.waitFor(250);
        Alert alert = browser.driver.switchTo().alert();
        if(isConfirm){
            alert.accept();
        }else {
            alert.dismiss();
        }
    }

}
