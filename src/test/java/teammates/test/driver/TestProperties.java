package teammates.test.driver;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import teammates.common.util.Assumption;
import teammates.common.util.Config;
import teammates.common.util.FileHelper;
import teammates.common.util.StringHelper;
import teammates.common.util.Url;

/** 
 * Represents properties in test.properties file
 */
public class TestProperties {
    
    public String TEAMMATES_REMOTEAPI_APP_DOMAIN;
    public int TEAMMATES_REMOTEAPI_APP_PORT;
    
    public String TEAMMATES_URL;
    public String TEAMMATES_VERSION;

    public String TEAMMATES_URL_IN_EMAILS;

    public String TEST_INSTRUCTOR_ACCOUNT;
    public String TEST_INSTRUCTOR_PASSWORD;

    public String TEST_STUDENT1_ACCOUNT;
    public String TEST_STUDENT1_PASSWORD;
    
    public String TEST_STUDENT2_ACCOUNT;
    public String TEST_STUDENT2_PASSWORD;
    
    public String TEST_ADMIN_ACCOUNT;
    public String TEST_ADMIN_PASSWORD;
    
    public String TEST_UNREG_ACCOUNT;
    public String TEST_UNREG_PASSWORD;

    public String BACKDOOR_KEY;

    public String BROWSER;
    public String FIREFOX_PATH;
    public String SELENIUMRC_HOST;
    public int SELENIUMRC_PORT;
    
    public int TEST_TIMEOUT;
    public String TEST_TIMEOUT_PAGELOAD;
    
    private static TestProperties instance;
    private Properties prop;
    public static final String TEST_PAGES_FOLDER = "src/test/resources/pages";
    /// TODO: create a subclass (e.g., TestDriverCo) and move all internal utility
    // functions to that sub class. It should be in util package.
    public static final String TEST_DATA_FOLDER = "src/test/resources/data";
    
    private TestProperties() {
        prop = new Properties();
        try {
            
            prop.load(new FileInputStream("src/test/resources/test.properties"));
            
            TEAMMATES_URL = Url.trimTrailingSlash(prop
                    .getProperty("test.app.url"));

            String remoteApiDomain = TEAMMATES_URL.substring(TEAMMATES_URL
                    .indexOf("://") + 3); // remove "http\://" and "https\://"
            TEAMMATES_REMOTEAPI_APP_DOMAIN = remoteApiDomain.split(":")[0];
            TEAMMATES_REMOTEAPI_APP_PORT = remoteApiDomain.contains(":") ? 
                    Integer.parseInt(remoteApiDomain.split(":")[1]) : 443;
        
            TEAMMATES_VERSION = extractVersionNumber(FileHelper.readFile("src/main/webapp/WEB-INF/appengine-web.xml"));
            
            TEAMMATES_URL_IN_EMAILS = Url.trimTrailingSlash(prop.getProperty("test.app.urlInEmails"));
            
            TEST_ADMIN_ACCOUNT = prop.getProperty("test.admin.account");
            TEST_ADMIN_PASSWORD = prop.getProperty("test.admin.password");
            
            TEST_INSTRUCTOR_ACCOUNT = prop.getProperty("test.instructor.account");
            TEST_INSTRUCTOR_PASSWORD = prop.getProperty("test.instructor.password");
            
            TEST_STUDENT1_ACCOUNT = prop.getProperty("test.student1.account");
            TEST_STUDENT1_PASSWORD = prop.getProperty("test.student1.password");
            
            TEST_STUDENT2_ACCOUNT = prop.getProperty("test.student2.account");
            TEST_STUDENT2_PASSWORD = prop.getProperty("test.student2.password");
            
            TEST_UNREG_ACCOUNT = prop.getProperty("test.unreg.account");
            TEST_UNREG_PASSWORD = prop.getProperty("test.unreg.password");
            
            BACKDOOR_KEY = prop.getProperty("test.backdoor.key");
            
            BROWSER = prop.getProperty("test.selenium.browser");
            FIREFOX_PATH = prop.getProperty("test.firefox.path");
            SELENIUMRC_HOST = prop.getProperty("test.selenium.host");
            SELENIUMRC_PORT = Integer.parseInt(prop.getProperty("test.selenium.port"));
            
            TEST_TIMEOUT = Integer.parseInt(prop.getProperty("test.timeout"));
            TEST_TIMEOUT_PAGELOAD = prop.getProperty("test.timeout") + "000";

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public static TestProperties inst() {
        if (instance == null)
            instance = new TestProperties();
        return instance;
    }


    public static String getChromeDriverPath() {
        String os = System.getProperty("os.name");
        if (os.startsWith("Windows")) {
            return "./src/test/resources/lib/selenium/chromedriver.exe";
        } else if (os.startsWith("Mac OS")) {
            return "./src/test/resources/lib/selenium/chromedriver_osx";
        }
        return "";
    }
    
    public static String getIEDriverPath() {
            return "./src/test/resources/lib/selenium/IEDriverServer.exe";
    }
    
    public boolean isDevServer(){
        return TEAMMATES_URL.contains("localhost");
    }

    public static String extractVersionNumber(String inputString) {
        String startTag = "<version>";
        String endTag = "</version>";
        int startPos = inputString.indexOf(startTag)+startTag.length();
        int endPos = inputString.indexOf(endTag);
        
        return inputString.substring(startPos, endPos).replace("-", ".").trim();
    }
    
    public void verifyReadyForGodMode() {
        if (!inst().isDevServer()) {
            Assumption.fail("God mode regeneration works only in dev server.");
        }
        if (areTestAccountsDefaultValues()) {
            Assumption.fail("Please change ALL the default accounts in test.properties in order to use God mode,"
                            + "e.g change test.student1.account from alice.tmms to alice.tmms.example");
        }
        if (!areAllTestAccountsDifferent()) {
            Assumption.fail("ALL test accounts used must be different, including after truncation, "
                            + "e.g veryveryveryveryverylong and veryveryveryveryverylengthy are not "
                            + "accepted as two different accounts.");
        }
        if (!areAppUrlsDifferent()) {
            Assumption.fail("App URLs defined in test.properties and build.properties must be different, "
                            + "and neither one can be a substring of the other, e.g localhost:8888 and "
                            + "localhost:88889 are not accepted as two different app URLs.");
        }
    }

    private boolean areTestAccountsDefaultValues() {
        /*
         * TODO make this check much, much stricter. If TEST_STUDENT1_ACCOUNT is charlie.tmms and
         * TEST_STUDENT2_ACCOUNT is alice.tmms, etc, this check will pass BUT the unintended replacement
         * will still happen.
         * Also consider adding checks for the accounts used in other UI tests such as
         * ISR.teammates.test, instructorWith2Courses, etc. If this is done the method might need a new name.
         */
        return "alice.tmms".contains(inst().TEST_STUDENT1_ACCOUNT)
                || "charlie.tmms".contains(inst().TEST_STUDENT2_ACCOUNT)  
                || "teammates.unreg".contains(inst().TEST_UNREG_ACCOUNT) 
                || "teammates.coord".contains(inst().TEST_INSTRUCTOR_ACCOUNT)
                || "yourGoogleId".contains(inst().TEST_ADMIN_ACCOUNT);
    }

    private boolean areAllTestAccountsDifferent() {
        List<String> testAccounts = new ArrayList<String>();
        List<String> injectedAccounts = Arrays.asList(inst().TEST_STUDENT1_ACCOUNT, inst().TEST_STUDENT2_ACCOUNT,
                                                      inst().TEST_UNREG_ACCOUNT, inst().TEST_INSTRUCTOR_ACCOUNT,
                                                      inst().TEST_ADMIN_ACCOUNT);
        for (String account : injectedAccounts) {
            if (testAccounts.contains(account)) {
                return false;
            }
            testAccounts.add(account);
            String truncatedAccount = StringHelper.truncateLongId(account);
            if (!truncatedAccount.equals(account)) {
                if (testAccounts.contains(truncatedAccount)) {
                    return false;
                }
                testAccounts.add(truncatedAccount);
            }
        }
        return true;
    }

    private boolean areAppUrlsDifferent() {
        return !Config.APP_URL.contains(inst().TEAMMATES_URL) && !inst().TEAMMATES_URL.contains(Config.APP_URL);
    }

}