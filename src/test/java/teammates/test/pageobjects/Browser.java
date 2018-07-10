package teammates.test.pageobjects;

import java.util.Stack;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import teammates.test.driver.TestProperties;

/**
 * A programmatic interface to the Browser used to test the app.
 */
public class Browser {

    /**
     * The {@link WebDriver} object that drives the Browser instance.
     */
    public WebDriver driver;

    /**
     * Indicated to the {@link BrowserPool} that this object is currently being
     * used and not ready to be reused by another test.
     */
    public boolean isInUse;

    public boolean isAdminLoggedIn;

    /**
     * Keeps track of multiple windows opened by the {@link WebDriver}.
     */
    private final Stack<String> windowHandles = new Stack<>();

    public Browser() {
        this.driver = createWebDriver();
        this.driver.manage().window().maximize();
        isInUse = false;
        isAdminLoggedIn = false;
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
     * Closes the current browser window and switches back to the last window
     * used previously.
     */
    public void closeCurrentWindowAndSwitchToParentWindow() {
        driver.close();
        driver.switchTo().window(windowHandles.pop());
    }

    private WebDriver createWebDriver() {
        System.out.print("Initializing Selenium: ");

        String browser = TestProperties.BROWSER;
        if (TestProperties.BROWSER_FIREFOX.equals(browser)) {
            System.out.println("Using Firefox.");
            String firefoxPath = TestProperties.FIREFOX_PATH;
            if (!firefoxPath.isEmpty()) {
                System.out.println("Custom path: " + firefoxPath);
                System.setProperty("webdriver.firefox.bin", firefoxPath);
            }

            // Allow CSV files to be download automatically, without a download popup.
            // This method is used because Selenium cannot directly interact with the download dialog.
            // Taken from http://stackoverflow.com/questions/24852709
            FirefoxProfile profile = new FirefoxProfile();
            profile.setPreference("browser.download.panel.shown", false);
            profile.setPreference("browser.helperApps.neverAsk.openFile", "text/csv,application/vnd.ms-excel");
            profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "text/csv,application/vnd.ms-excel");
            profile.setPreference("browser.download.folderList", 2);
            profile.setPreference("browser.download.dir", System.getProperty("java.io.tmpdir"));
            return new FirefoxDriver(profile);

        } else if (TestProperties.BROWSER_CHROME.equals(browser)) {
            System.out.println("Using Chrome with driver path: " + TestProperties.CHROMEDRIVER_PATH);

            System.setProperty("webdriver.chrome.driver", TestProperties.CHROMEDRIVER_PATH);

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--allow-file-access-from-files");
            return new ChromeDriver(options);
        }
        System.out.println("Using " + browser + " is not supported!");
        return null;

    }

}
