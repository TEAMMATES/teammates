package teammates.test.cases.ui.browsertests;



import org.openqa.selenium.By;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.common.util.Url;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.StudentHomePage;
import teammates.test.pageobjects.StudentProfilePage;

public class StudentProfilePageUiTest extends BaseUiTestCase {
    private static Browser browser;
    private static DataBundle testData;
    private StudentProfilePage profilePage;
    
    
    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();
        testData = loadDataBundle("/StudentProfilePageUiTest.json");
        restoreTestDataOnServer(testData);

        browser = BrowserPool.getBrowser();
    }
    
    @Test
    public void allTests() throws Exception {
        testContent();
        testNavLink();
        testActions();
    }

    private void testContent() {
        
        ______TS("typical success case");
        
        profilePage = getProfilePageForStudent("studentWithEmptyProfile");
        profilePage.verifyHtml("/studentProfilePageDefault.html");
        
        ______TS("existing profile values");
        
        profilePage = getProfilePageForStudent("studentWithFilledProfile");
        profilePage.verifyHtmlPart(By.id("editProfileDiv"), "/studentProfileEditDivExistingValues.html");
    }

    private void testActions() throws Exception {
        
        profilePage = getProfilePageForStudent("studentWithFilledProfile");
        ______TS("typical success case");
        
        profilePage.editProfileThourghUi("short.name", "e@email.com", "inst", "Usual Country", 
                "female", "this is enough!$%&*</>");
        profilePage.ensureProfileContains("short.name", "e@email.com", "inst", "Usual Country", 
                "female", "this is enough!$%&*</>");
        profilePage.verifyStatus(Const.StatusMessages.STUDENT_PROFILE_EDITED);
        
        ______TS("invalid data");
        
        StudentProfileAttributes spa = new StudentProfileAttributes("valid.id", "$$short.name", 
                "e@email.com", " inst  ", StringHelper.generateStringOfLength(54), 
                "male", "this is enough!$%&*</>", "");
        profilePage.editProfileThourghUi(spa.shortName, spa.email, spa.institute, 
                spa.country, spa.gender, spa.moreInfo);
        
        profilePage.ensureProfileContains("short.name", "e@email.com", "inst", "Usual Country", 
                "female", "this is enough!$%&*</>");
        
        profilePage.verifyStatus(StringHelper.toString(spa.getInvalidityInfo(), " "));
    }

    private void testNavLink() {
        Url profileUrl = createUrl(Const.ActionURIs.STUDENT_HOME_PAGE)
                .withUserId(testData.accounts.get("studentWithFilledProfile").googleId);
         StudentHomePage shp = loginAdminToPage(browser, profileUrl, StudentHomePage.class);
         
         profilePage = shp.loadProfileTab().changePageType(StudentProfilePage.class);
    }
    
    private StudentProfilePage getProfilePageForStudent(String studentId) {
        Url profileUrl = createUrl(Const.ActionURIs.STUDENT_PROFILE_PAGE)
            .withUserId(testData.accounts.get(studentId).googleId);
        return loginAdminToPage(browser, profileUrl, StudentProfilePage.class);
    }
}
