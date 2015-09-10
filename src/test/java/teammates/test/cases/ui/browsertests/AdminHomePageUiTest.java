package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import java.lang.reflect.Constructor;

import org.openqa.selenium.support.PageFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.common.util.Url;
import teammates.test.driver.BackDoor;
import teammates.test.driver.TestProperties;
import teammates.test.pageobjects.AdminHomePage;
import teammates.test.pageobjects.AppPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.DevServerLoginPage;
import teammates.test.pageobjects.FeedbackSubmitPage;
import teammates.test.pageobjects.GoogleLoginPage;
import teammates.test.pageobjects.HomePage;
import teammates.test.pageobjects.InstructorCourseDetailsPage;
import teammates.test.pageobjects.InstructorCourseEditPage;
import teammates.test.pageobjects.InstructorCourseEnrollPage;
import teammates.test.pageobjects.InstructorCourseJoinConfirmationPage;
import teammates.test.pageobjects.InstructorCoursesPage;
import teammates.test.pageobjects.InstructorFeedbackEditPage;
import teammates.test.pageobjects.InstructorFeedbacksPage;
import teammates.test.pageobjects.InstructorHomePage;
import teammates.test.pageobjects.LoginPage;
import teammates.test.pageobjects.StudentCommentsPage;
import teammates.test.pageobjects.StudentCourseDetailsPage;
import teammates.test.pageobjects.StudentFeedbackResultsPage;
import teammates.test.pageobjects.StudentHomePage;
import teammates.test.pageobjects.StudentProfilePage;
import teammates.test.util.Priority;

import com.google.appengine.api.datastore.Text;

/**
 * Covers the home page for admins.
 * SUT: {@link AdminHomePage}
 */
@Priority(6)
public class AdminHomePageUiTest extends BaseUiTestCase{
    private static Browser browser;
    private static AdminHomePage homePage;
    private static InstructorCourseJoinConfirmationPage confirmationPage;
    
    
    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();      
        browser = BrowserPool.getBrowser();
        browser.driver.manage().deleteAllCookies();
    }
    
    @Test
    public void testAll() throws InvalidParametersException, EntityDoesNotExistException, Exception{
        testContent();
        //no links to check
        testCreateInstructorAction();
    }

    private void testContent() {
        
        ______TS("content: typical page");
        
        Url homeUrl = createUrl(Const.ActionURIs.ADMIN_HOME_PAGE);
        homePage = loginAdminToPage(browser, homeUrl, AdminHomePage.class);
        //Full page content check is omitted because this is an internal page. 
    }

    @SuppressWarnings("deprecation")
    private void testCreateInstructorAction() throws Exception {
        
        InstructorAttributes instructor = new InstructorAttributes();
        
        String shortName = "Instrúctör";
        instructor.name =  "AHPUiT Instrúctör";
        instructor.email = "AHPUiT.instr1@gmail.tmt";
        String institute = "TEAMMATES Test Institute 1";
        String demoCourseId = "AHPUiT.instr1.gma-demo";
    
        ______TS("action success : create instructor account and the account is created successfully after user's verification");
        
        BackDoor.deleteAccount(TestProperties.inst().TEST_INSTRUCTOR_ACCOUNT);
        BackDoor.deleteCourse(demoCourseId);
        BackDoor.deleteInstructor(demoCourseId, instructor.email);
        
        homePage.createInstructor(shortName,instructor,institute);
        
        String expectedjoinUrl = Config.APP_URL + Const.ActionURIs.INSTRUCTOR_COURSE_JOIN;
        
        expectedjoinUrl = Url.addParamToUrl(expectedjoinUrl, Const.ParamsNames.REGKEY,
                                            StringHelper.encrypt(BackDoor.getKeyForInstructor(demoCourseId, instructor.email)));
       
        expectedjoinUrl = Url.addParamToUrl(expectedjoinUrl, Const.ParamsNames.INSTRUCTOR_INSTITUTION, institute);
        
        homePage.getStatus().contains("Instructor AHPUiT Instrúctör has been successfully created with join link:\n" 
                                      + expectedjoinUrl);
        homePage.verifyHtml("/adminHomePageCreateInstructorSuccess.html");
        
        homePage.logout();
        //verify the instructor and the demo course have been created
        assertNotNull(BackDoor.getCourse(demoCourseId));
        assertNotNull(BackDoor.getInstructorByEmail(instructor.email, demoCourseId));
        
        //get the joinURL which sent to the requester's email
        String joinActionUrl = TestProperties.inst().TEAMMATES_URL + Const.ActionURIs.INSTRUCTOR_COURSE_JOIN;
      
        String joinLink = Url.addParamToUrl(joinActionUrl, Const.ParamsNames.REGKEY,
                                            StringHelper.encrypt(BackDoor.getKeyForInstructor(demoCourseId, instructor.email)));
       
        joinLink = Url.addParamToUrl(joinLink, Const.ParamsNames.INSTRUCTOR_INSTITUTION, institute);
        
        //simulate the user's verification here because it is added by admin 
        browser.driver.get(joinLink);        
        confirmationPage = createCorrectLoginPageType(browser.driver.getPageSource())
                           .loginAsJoiningInstructor(TestProperties.inst().TEST_INSTRUCTOR_ACCOUNT,
                                                     TestProperties.inst().TEST_INSTRUCTOR_PASSWORD);
        confirmationPage.clickCancelButton();      
        
        browser.driver.get(joinLink);
        confirmationPage = createCorrectLoginPageType(browser.driver.getPageSource())
                           .loginAsJoiningInstructor(TestProperties.inst().TEST_INSTRUCTOR_ACCOUNT,
                                                     TestProperties.inst().TEST_INSTRUCTOR_PASSWORD);  
        confirmationPage.clickConfirmButton();
        
        //check a account has been created for the requester successfully
        assertNotNull(BackDoor.getAccount(TestProperties.inst().TEST_INSTRUCTOR_ACCOUNT));
        
        
        //verify sample course is accessible for newly joined instructor as an instructor
        
        ______TS("new instructor can see sample course in homepage");
        InstructorHomePage instructorHomePage = AppPage.getNewPageInstance(browser, InstructorHomePage.class);
        instructorHomePage.verifyHtmlMainContent("/newlyJoinedInstructorHomePage.html");
        
        ______TS("new instructor can access sample coure enroll page");
        InstructorCourseEnrollPage enrollPage = instructorHomePage.clickCourseErollLink(demoCourseId);
        enrollPage.verifyHtmlMainContent("/newlyJoinedInstructorCourseEnrollPage.html");
        
        ______TS("new instructor can access sample coure details page");
        instructorHomePage = enrollPage.goToPreviousPage(InstructorHomePage.class);
        InstructorCourseDetailsPage detailsPage = instructorHomePage.clickCourseViewLink(demoCourseId);
        detailsPage.verifyHtmlMainContent("/newlyJoinedInstructorCourseDetailsPage.html");
        
        ______TS("new instructor can access sample coure edit page");
        instructorHomePage = detailsPage.goToPreviousPage(InstructorHomePage.class);
        InstructorCourseEditPage editPage = instructorHomePage.clickCourseEditLink(demoCourseId);
        editPage.verifyHtmlMainContent("/newlyJoinedInstructorCourseEditPage.html");
        
        ______TS("new instructor can access sample coure feedback session adding page");
        instructorHomePage = editPage.goToPreviousPage(InstructorHomePage.class);
        InstructorFeedbacksPage feedbacksPage = instructorHomePage.clickCourseAddEvaluationLink(demoCourseId);
        feedbacksPage.verifyHtmlMainContent("/newlyJoinedInstructorFeedbacksPage.html");
        
        ______TS("new instructor can archive sample course");
        instructorHomePage = feedbacksPage.goToPreviousPage(InstructorHomePage.class);
        instructorHomePage.clickArchiveCourseLinkAndConfirm(demoCourseId);
        instructorHomePage.verifyHtmlMainContent("/newlyJoinedInstructorHomePageSampleCourseArchived.html");
        
        ______TS("new instructor can unarchive sample course");
        String url = Url.addParamToUrl(TestProperties.inst().TEAMMATES_URL + Const.ActionURIs.INSTRUCTOR_COURSES_PAGE, 
                                       Const.ParamsNames.USER_ID, 
                                       TestProperties.inst().TEST_INSTRUCTOR_ACCOUNT);
        browser.driver.get(url);
        InstructorCoursesPage coursesPage = AppPage.getNewPageInstance(browser, InstructorCoursesPage.class);
        coursesPage.unarchiveCourse(demoCourseId);
        coursesPage.verifyHtmlMainContent("/newlyJoinedInstructorCoursesPageSampleCourseUnarhived.html");
        
        ______TS("new instructor can access sample course students page");
        coursesPage.loadStudentsTab().verifyHtmlMainContent("/newlyJoinedInstructorStudentListPage.html");
        ______TS("new instructor can access sample course comments page");
        coursesPage.loadInstructorCommentsTab().verifyHtmlMainContent("/newlyJoinedInstructorCommentsPage.html");
        
        ______TS("new instructor can view feedbackSession result of sample course");
        coursesPage.loadInstructorHomeTab();
        instructorHomePage = AppPage.getNewPageInstance(browser, InstructorHomePage.class);
        instructorHomePage.clickFeedbackSessionViewResultsLink("AHPUiT.instr1.gma-demo", "Second team feedback session")
                          .waitForPageToLoad();
        instructorHomePage.verifyHtmlMainContent("/newlyJoinedInstructorFeedbackResultsPage.html");
        
        ______TS("new instructor can edit feedbackSession of sample course");
        instructorHomePage.loadInstructorHomeTab();
        InstructorFeedbackEditPage feedbackEditPage = instructorHomePage.clickFeedbackSessionEditLink("AHPUiT.instr1.gma-demo", 
                                                                                                      "Second team feedback session");
        
        feedbackEditPage.clickEditSessionButton();
        
        FeedbackSessionAttributes feedbackSession = BackDoor.getFeedbackSession("AHPUiT.instr1.gma-demo", 
                                                                                "Second team feedback session");
        feedbackEditPage.editFeedbackSession(feedbackSession.startTime, 
                                             feedbackSession.endTime,
                                             new Text("updated instructions"),
                                             feedbackSession.gracePeriod);
        feedbackEditPage.reloadPage();
        instructorHomePage.verifyHtmlMainContent("/newlyJoinedInstructorFeedbackSessionSuccessEdited.html");
        
        
        
        ______TS("new instructor can click submit button of sample feedbackSession");
        instructorHomePage.loadInstructorHomeTab();
        FeedbackSubmitPage fbsp = instructorHomePage.clickFeedbackSessionSubmitLink("AHPUiT.instr1.gma-demo", 
                                                                                    "Second team feedback session");
        fbsp.verifyHtmlMainContent("/newlyJoinedInstructorFeedbackSubmissionEditPage.html");
        
        ______TS("new instructor can send reminder of sample course");
        instructorHomePage.loadInstructorHomeTab();
        feedbacksPage = instructorHomePage.clickFeedbackSessionRemindLink("AHPUiT.instr1.gma-demo", 
                                                                          "Second team feedback session");
        feedbacksPage.verifyHtmlMainContent("/newlyJoinedInstructorFeedbackSessionRemind.html");
        
        ______TS("new instructor can unpublish feedbackSession of sample course");
        instructorHomePage.loadInstructorHomeTab();
        feedbacksPage = instructorHomePage.clickFeedbackSessionUnpublishLink("AHPUiT.instr1.gma-demo", 
                                                                             "Second team feedback session");
        feedbacksPage.verifyHtmlMainContent("/newlyJoinedInstructorFeedbackSessionUnpublished.html");
        
        ______TS("new instructor can publish feedbackSession of sample course");
        instructorHomePage.loadInstructorHomeTab();
        feedbacksPage = instructorHomePage.clickFeedbackSessionPublishLink("AHPUiT.instr1.gma-demo", 
                                                                           "Second team feedback session");
        feedbacksPage.verifyHtmlMainContent("/newlyJoinedInstructorFeedbackSessionPublished.html");
        
        feedbacksPage.logout();
        
        ______TS("action failure : invalid parameter");
        
        Url homeUrl = createUrl(Const.ActionURIs.ADMIN_HOME_PAGE);
        homePage = loginAdminToPage(browser, homeUrl, AdminHomePage.class);
        
        instructor.email = "AHPUiT.email.tmt";        
        homePage.createInstructor(shortName,instructor,institute).verifyStatus(String.format(FieldValidator.EMAIL_ERROR_MESSAGE, instructor.email, FieldValidator.REASON_INCORRECT_FORMAT));
      
        
        ______TS("action success: course is accessible for newly joined instructor as student");
        //in staging server, the student account uses the hardcoded email above, so this can only be test on dev server
        if(!TestProperties.inst().TEAMMATES_URL.contains("local")){
            
            BackDoor.deleteCourse(demoCourseId);
            BackDoor.deleteAccount(TestProperties.inst().TEST_INSTRUCTOR_ACCOUNT);
            BackDoor.deleteInstructor(demoCourseId, instructor.email);
            return;
        }
        
        //verify sample course is accessible for newly joined instructor as an student
        
        StudentHomePage studentHomePage = HomePage.getNewInstance(browser).clickStudentLogin()
                                                                          .loginAsStudent(TestProperties.inst().TEST_INSTRUCTOR_ACCOUNT, 
                                                                                          TestProperties.inst().TEST_INSTRUCTOR_PASSWORD);
        
        studentHomePage.verifyContains(demoCourseId);
        studentHomePage.clickViewTeam();
        
        StudentCourseDetailsPage courseDetailsPage = AppPage.getNewPageInstance(browser, StudentCourseDetailsPage.class);
        courseDetailsPage.verifyHtmlMainContent("/newlyJoinedInstructorStudentCourseDetailsPage.html");
        
        studentHomePage = courseDetailsPage.goToPreviousPage(StudentHomePage.class);
        studentHomePage.getViewFeedbackButton("First team feedback session").click();
        StudentFeedbackResultsPage sfrp = AppPage.getNewPageInstance(browser, StudentFeedbackResultsPage.class);
        sfrp.verifyHtmlMainContent("/newlyJoinedInstructorStudentFeedbackResultsPage.html");
        
        studentHomePage = sfrp.goToPreviousPage(StudentHomePage.class);
        studentHomePage.getEditFeedbackButton("First team feedback session").click();
        FeedbackSubmitPage fsp = AppPage.getNewPageInstance(browser, FeedbackSubmitPage.class);
        fsp.verifyHtmlMainContent("/newlyJoinedInstructorStudentFeedbackSubmissionEdit.html");
        
        studentHomePage = fsp.loadStudentHomeTab();
        StudentCommentsPage scp = studentHomePage.loadStudentCommentsTab();    
        scp.verifyHtmlMainContent("/newlyJoinedInstructorStudentCommentsPage.html");
        
        studentHomePage = scp.loadStudentHomeTab();
        
        StudentProfilePage spp = studentHomePage.loadProfileTab();
        spp.verifyContains("Student Profile");
        spp.verifyContains("AHPUiT Instrúctör");
        
        studentHomePage.logout();
        
        //login in as instructor again to test sample course deletion
        instructorHomePage = HomePage.getNewInstance(browser).clickInstructorLogin()
                                                             .loginAsInstructor(TestProperties.inst().TEST_INSTRUCTOR_ACCOUNT, 
                                                                                TestProperties.inst().TEST_INSTRUCTOR_PASSWORD);
        
  
        instructorHomePage.clickAndConfirm(instructorHomePage.getDeleteCourseLink(demoCourseId));
        assertTrue(instructorHomePage.getStatus().contains("The course " + demoCourseId + " has been deleted."));
     
        instructorHomePage.logout();
        
        BackDoor.deleteAccount(TestProperties.inst().TEST_INSTRUCTOR_ACCOUNT);
        BackDoor.deleteCourse(demoCourseId);
        BackDoor.deleteInstructor(demoCourseId, instructor.email);
        
   
    }

    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }
    
    private LoginPage createCorrectLoginPageType(String pageSource) {
        if (DevServerLoginPage.containsExpectedPageContents(pageSource)) {
            return (LoginPage) createNewPage(browser, DevServerLoginPage.class);
        } else if (GoogleLoginPage.containsExpectedPageContents(pageSource)) {
            return (LoginPage) createNewPage(browser, GoogleLoginPage.class);
        } else {
            throw new IllegalStateException("Not a valid login page :" + pageSource);
        }
    }
    
    private <T extends AppPage> T createNewPage(Browser browser, Class<T> typeOfPage) {
        Constructor<T> constructor;
        try {
            constructor = typeOfPage.getConstructor(Browser.class);
            T page = constructor.newInstance(browser);
            PageFactory.initElements(browser.driver, page);
            return page;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
