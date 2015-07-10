package teammates.test.driver;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import teammates.common.util.FileHelper;
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
    
    

}