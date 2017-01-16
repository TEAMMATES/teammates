package teammates.test.cases.ui.browsertests;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.test.driver.BackDoor;
import teammates.test.driver.TestProperties;
import teammates.test.pageobjects.AppPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.HomePage;
import teammates.test.pageobjects.LoginPage;
import teammates.test.pageobjects.StudentCourseJoinConfirmationPage;
import teammates.test.pageobjects.StudentFeedbackResultsPage;

/**
 * Tests 'Feedback Results' view of students.
 * SUT: {@link StudentFeedbackResultsPage}.
 */
public class StudentFeedbackResultsPageUiTest extends BaseUiTestCase {
    private static DataBundle testData;
    private static Browser browser;

    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
        testData = loadDataBundle("/StudentFeedbackResultsPageUiTest.json");
        removeAndRestoreDataBundle(testData);
        browser = BrowserPool.getBrowser();
    }

    @Test
    public void testAll() throws Exception {

        ______TS("unreg student");

        logout(browser);
        
        // Open Session
        StudentAttributes unreg = testData.students.get("DropOut");
        StudentFeedbackResultsPage resultsPage =
                loginToStudentFeedbackResultsPage(unreg, "Open Session", StudentFeedbackResultsPage.class);
        resultsPage.verifyHtmlMainContent("/unregisteredStudentFeedbackResultsPageOpen.html");

        // Mcq Session
        resultsPage = loginToStudentFeedbackResultsPage(unreg, "MCQ Session", StudentFeedbackResultsPage.class);

        // This is the full HTML verification for Unregistered Student Feedback Results Page,
        // the rest can all be verifyMainHtml
        resultsPage.verifyHtml("/unregisteredStudentFeedbackResultsPageMCQ.html");

        ______TS("no responses");

        resultsPage = loginToStudentFeedbackResultsPage("Alice", "Empty Session");

        // This is the full HTML verification for Registered Student Feedback Results Page,
        // the rest can all be verifyMainHtml
        resultsPage.verifyHtml("/studentFeedbackResultsPageEmpty.html");

        ______TS("standard session results");

        resultsPage = loginToStudentFeedbackResultsPage("Alice", "Open Session");
        resultsPage.verifyHtmlMainContent("/studentFeedbackResultsPageOpen.html");

        ______TS("team-to-team session results");

        resultsPage = loginToStudentFeedbackResultsPage("Benny", "Open Session");
        resultsPage.verifyHtmlMainContent("/studentFeedbackResultsPageTeamToTeam.html");

        ______TS("MCQ session results");

        resultsPage = loginToStudentFeedbackResultsPage("Alice", "MCQ Session");
        resultsPage.verifyHtmlMainContent("/studentFeedbackResultsPageMCQ.html");

        assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(4, ""));
        assertTrue(resultsPage.clickQuestionAdditionalInfoButton(4, ""));
        assertEquals("[less]", resultsPage.getQuestionAdditionalInfoButtonText(4, ""));
        assertFalse(resultsPage.clickQuestionAdditionalInfoButton(4, ""));
        assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(4, ""));

        assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(5, ""));
        assertTrue(resultsPage.clickQuestionAdditionalInfoButton(5, ""));
        assertEquals("[less]", resultsPage.getQuestionAdditionalInfoButtonText(5, ""));
        assertFalse(resultsPage.clickQuestionAdditionalInfoButton(5, ""));
        assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(5, ""));

        ______TS("MSQ session results");

        resultsPage = loginToStudentFeedbackResultsPage("Alice", "MSQ Session");
        resultsPage.verifyHtmlMainContent("/studentFeedbackResultsPageMSQ.html");

        assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(4, ""));
        assertTrue(resultsPage.clickQuestionAdditionalInfoButton(4, ""));
        assertEquals("[less]", resultsPage.getQuestionAdditionalInfoButtonText(4, ""));
        assertFalse(resultsPage.clickQuestionAdditionalInfoButton(4, ""));
        assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(4, ""));

        assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(5, ""));
        assertTrue(resultsPage.clickQuestionAdditionalInfoButton(5, ""));
        assertEquals("[less]", resultsPage.getQuestionAdditionalInfoButtonText(5, ""));
        assertFalse(resultsPage.clickQuestionAdditionalInfoButton(5, ""));
        assertEquals("[more]", resultsPage.getQuestionAdditionalInfoButtonText(5, ""));

        ______TS("NUMSCALE session results");

        resultsPage = loginToStudentFeedbackResultsPage("Alice", "NUMSCALE Session");
        resultsPage.verifyHtmlMainContent("/studentFeedbackResultsPageNUMSCALE.html");

        ______TS("CONSTSUM session results");

        resultsPage = loginToStudentFeedbackResultsPage("Alice", "CONSTSUM Session");
        resultsPage.verifyHtmlMainContent("/studentFeedbackResultsPageCONSTSUM.html");


        ______TS("CONTRIB session results");

        resultsPage = loginToStudentFeedbackResultsPage("Alice", "CONTRIB Session");
        resultsPage.verifyHtmlMainContent("/studentFeedbackResultsPageCONTRIB.html");

        ______TS("unreg student logged in as a student in another course: registered after logging out");
        
        String student1Username = TestProperties.TEST_STUDENT1_ACCOUNT;
        String student1Password = TestProperties.TEST_STUDENT1_PASSWORD;
        
        logout(browser);
        LoginPage loginPage = AppPage.getNewPageInstance(browser, HomePage.class).clickStudentLogin();
        loginPage.loginAsStudent(student1Username, student1Password);

        StudentCourseJoinConfirmationPage confirmationPage =
                loginToStudentFeedbackResultsPage(unreg, "Open Session", StudentCourseJoinConfirmationPage.class);
        confirmationPage.verifyHtmlMainContent("/studentCourseJoinConfirmationLoggedInHTML.html");
        loginPage = confirmationPage.clickCancelButton();
        loginPage.loginAsStudent(student1Username, student1Password, StudentFeedbackResultsPage.class);

        resultsPage.verifyHtmlMainContent("/studentFeedbackResultsPageNewlyRegistered.html");
        
        BackDoor.editStudent(unreg.email, unreg); // clear the googleId
        
        ______TS("unreg student logged in as a student in another course: registered without logging out");
        
        logout(browser);
        loginPage = AppPage.getNewPageInstance(browser, HomePage.class).clickStudentLogin();
        loginPage.loginAsStudent(student1Username, student1Password);

        confirmationPage =
                loginToStudentFeedbackResultsPage(unreg, "Open Session", StudentCourseJoinConfirmationPage.class);
        confirmationPage.verifyHtmlMainContent("/studentCourseJoinConfirmationLoggedInHTML.html");
        resultsPage = confirmationPage.clickConfirmButton(StudentFeedbackResultsPage.class);

        resultsPage.verifyHtmlMainContent("/studentFeedbackResultsPageNewlyRegistered.html");
        
        BackDoor.deleteStudent(unreg.course, unreg.email);
    }

    @AfterClass
    public static void classTearDown() {
        BrowserPool.release(browser);
    }

    private StudentFeedbackResultsPage loginToStudentFeedbackResultsPage(String studentName, String fsName) {
        AppUrl editUrl = createUrl(Const.ActionURIs.STUDENT_FEEDBACK_RESULTS_PAGE)
                                        .withUserId(testData.students.get(studentName).googleId)
                                        .withCourseId(testData.feedbackSessions.get(fsName).getCourseId())
                                        .withSessionName(testData.feedbackSessions.get(fsName).getFeedbackSessionName());
        return loginAdminToPage(browser, editUrl, StudentFeedbackResultsPage.class);
    }

    private <T extends AppPage> T loginToStudentFeedbackResultsPage(StudentAttributes s, String fsDataId,
                                                                    Class<T> typeOfPage) {
        AppUrl submitUrl = createUrl(Const.ActionURIs.STUDENT_FEEDBACK_RESULTS_PAGE)
                                            .withCourseId(s.course)
                                            .withStudentEmail(s.email)
                                            .withSessionName(testData.feedbackSessions.get(fsDataId)
                                                                                      .getFeedbackSessionName())
                                            .withRegistrationKey(BackDoor.getEncryptedKeyForStudent(s.course, s.email));
        return AppPage.getNewPageInstance(browser, submitUrl, typeOfPage);
    }
}
