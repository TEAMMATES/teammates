package teammates.e2e.pageobjects;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class Test {
    public static void main(String[] args) {
        String downloadPath = "";
        try {
            downloadPath = new File("src/e2e/resources/downloads").getCanonicalPath();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            System.out.println("Read from file");
        }
        Map<String, Object> chromePrefs = new HashMap<>();
        chromePrefs.put("download.default_directory", downloadPath);
        chromePrefs.put("download.prompt_for_download", false);
        System.setProperty("webdriver.chrome.driver", "/mnt/c/Users/Fergu/Downloads/chromedriver");

        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs", chromePrefs);
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--user-data-dir=" + "./build");
        options.addArguments("--allow-file-access-from-files");
        options.setBinary("/mnt/c/Program Files (x86)/Google/Chrome/Application/chrome.exe");
        options.addArguments("--disable-extensions");
        ChromeDriver newDriver = new ChromeDriver(options);
        newDriver.manage().timeouts().pageLoadTimeout(40, TimeUnit.SECONDS);
        newDriver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        newDriver.get("http://www.google.com");
        System.out.println("hello123");
        newDriver.close();
    }
}
