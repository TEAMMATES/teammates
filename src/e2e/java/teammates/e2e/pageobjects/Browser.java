package teammates.e2e.pageobjects;

import java.io.IOException;
import java.util.Stack;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.ProfilesIni;
import org.openqa.selenium.support.ui.WebDriverWait;

import teammates.e2e.util.TestProperties;
import teammates.test.driver.FileHelper;

/**
 * A programmatic interface to the Browser used to test the app.
 */
public class Browser {

    private static final String PAGE_LOAD_SCRIPT;

    static {
        try {
            PAGE_LOAD_SCRIPT = FileHelper.readFile("src/e2e/resources/scripts/waitForPageLoad.js");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * The {@link WebDriver} object that drives the Browser instance.
     */
    // TODO change this to private once all legacy UI tests are migrated
    public WebDriver driver;

    /**
     * Indicates whether the app is being used by an admin.
     */
    public boolean isAdminLoggedIn;

    /**
     * Indicates to the {@link BrowserPool} that this object is currently being used
     * and not ready to be reused by another test.
     */
    boolean isInUse;

    /**
     * Keeps track of multiple windows opened by the {@link WebDriver}.
     */
    private final Stack<String> windowHandles = new Stack<>();

    public Browser() {
        this.driver = createWebDriver();
        this.driver.manage().window().maximize();
        this.isInUse = false;
        this.isAdminLoggedIn = false;
    }

    /**
     * Switches to new browser window for browsing.
     */
    public void switchToNewWindow() {
        String curWin = driver.getWindowHandle();
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(curWin) && !windowHandles.contains(curWin)) {
                windowHandles.push(curWin);
                driver.switchTo().window(handle);
                break;
            }
        }
    }

    /**
     * Waits for the page to load. This includes all AJAX requests and Angular animations in the page.
     */
    public void waitForPageLoad() {
        waitForPageLoad(false);
    }

    /**
     * Waits for the page to load. This includes all AJAX requests and Angular animations in the page.
     *
     * @param excludeToast Set this to true if toast message's disappearance should not be counted
     *         as criteria for page load's completion.
     */
    public void waitForPageLoad(boolean excludeToast) {
        WebDriverWait wait = new WebDriverWait(driver, TestProperties.TEST_TIMEOUT);
        wait.until(driver -> {
            return "complete".equals(
                    ((JavascriptExecutor) driver).executeAsyncScript(PAGE_LOAD_SCRIPT, excludeToast ? 1 : 0)
            );
        });
    }

    /**
     * Waits for the page to load by only looking at the page's readyState.
     */
    public void waitForPageReadyState() {
        WebDriverWait wait = new WebDriverWait(driver, TestProperties.TEST_TIMEOUT);
        wait.until(driver -> {
            return "complete".equals(((JavascriptExecutor) driver).executeScript("return document.readyState"));
        });
    }

    /**
     * Closes the current browser window and switches back to the last window used previously.
     */
    public void closeCurrentWindowAndSwitchToParentWindow() {
        driver.close();
        driver.switchTo().window(windowHandles.pop());
    }

    private WebDriver createWebDriver() {
        System.out.print("Initializing Selenium: ");

        String browser = TestProperties.BROWSER;
        if (TestProperties.BROWSER_FIREFOX.equals(browser)) {
            System.out.println("Using Firefox with driver path: " + TestProperties.GECKODRIVER_PATH);
            String firefoxPath = TestProperties.FIREFOX_PATH;
            if (!firefoxPath.isEmpty()) {
                System.out.println("Custom path: " + firefoxPath);
                System.setProperty("webdriver.firefox.bin", firefoxPath);
            }
            System.setProperty("webdriver.gecko.driver", TestProperties.GECKODRIVER_PATH);

            FirefoxProfile profile;
            if (TestProperties.isDevServer()) {
                profile = new FirefoxProfile();
            } else {
                // Get user data from browser to bypass google blocking automated log in.
                // Log in manually to teammates to use that log in data for e2e tests.
                ProfilesIni profileIni = new ProfilesIni();
                profile = profileIni.getProfile(TestProperties.FIREFOX_PROFILE_NAME);
            }

            // Allow CSV files to be download automatically, without a download popup.
            // This method is used because Selenium cannot directly interact with the download dialog.
            // Taken from http://stackoverflow.com/questions/24852709
            profile.setPreference("browser.download.panel.shown", false);
            profile.setPreference("browser.helperApps.neverAsk.openFile", "text/csv,application/vnd.ms-excel");
            profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "text/csv,application/vnd.ms-excel");
            profile.setPreference("browser.download.folderList", 2);
            profile.setPreference("browser.download.dir", System.getProperty("java.io.tmpdir"));

            FirefoxOptions options = new FirefoxOptions().setProfile(profile);
            return new FirefoxDriver(options);
        }

        if (TestProperties.BROWSER_CHROME.equals(browser)) {
            System.out.println("Using Chrome with driver path: " + TestProperties.CHROMEDRIVER_PATH);
            System.setProperty("webdriver.chrome.driver", TestProperties.CHROMEDRIVER_PATH);

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--allow-file-access-from-files");
            if (!TestProperties.isDevServer()) {
                // Get user data from browser to bypass google blocking automated log in.
                // Log in manually to teammates to use that log in data for e2e tests.
                options.addArguments("user-data-dir=" + TestProperties.CHROME_USER_DATA_PATH);
            }

            return new ChromeDriver(options);
        }

        throw new RuntimeException("Using " + browser + " is not supported!");
    }

}
