package teammates.test.cases.ui.browsertests;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.ThreadHelper;
import teammates.test.driver.BackDoor;
import teammates.test.driver.TestProperties;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;
import teammates.test.pageobjects.InstructorCourseEnrollPage;
import teammates.test.util.Priority;

/**
 * Covers Ui aspect of submission adjustment for evaluations and feedbacks
 */
@Priority(1)
public class InstructorSubmissionAdjustmentUiTest extends BaseUiTestCase {
    private static DataBundle testData;
    private static Browser browser;
    private static InstructorCourseEnrollPage enrollPage;
    
    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
        testData = loadDataBundle("/InstructorSubmissionAdjustmentUiTest.json");
        
        // use the instructor account injected for this test
        
        testData.accounts.get("instructor1OfCourse1").googleId = TestProperties.TEST_INSTRUCTOR_ACCOUNT;
        testData.accounts.get("instructor1OfCourse1").email = TestProperties.TEST_INSTRUCTOR_ACCOUNT + "@gmail.com";
        
        removeAndRestoreDataBundle(testData);
        
        browser = BrowserPool.getBrowser();
    }
    
    @AfterClass
    public static void classTearDown() {
        BrowserPool.release(browser);
    }
    
    @Test
    public void testAdjustmentOfSubsmission() {
        
        //load the enrollPage
        loadEnrollmentPage();
        
        ______TS("typical case: enroll new student to existing course");
        StudentAttributes newStudent = new StudentAttributes();
        newStudent.section = "None";
        newStudent.team = "Team 1.1</td></div>'\"";
        newStudent.course = "idOfTypicalCourse1";
        newStudent.email = "random@g.tmt";
        newStudent.name = "someName";
        newStudent.comments = "comments";
        
        String enrollString = "Section | Team | Name | Email | Comment" + Const.EOL
                            + newStudent.toEnrollmentString();
        
        enrollPage.enroll(enrollString);
        
        //Wait briefly to allow task queue to successfully execute tasks
        ThreadHelper.waitFor(2000);
        
        
        ______TS("typical case : existing student changes team");
        loadEnrollmentPage();
        
        FeedbackSessionAttributes session = testData.feedbackSessions.get("session2InCourse1");
        StudentAttributes student = testData.students.get("student1InCourse1");
        
        //Verify pre-existing submissions and responses
        List<FeedbackResponseAttributes> oldResponsesForSession =
                getAllResponsesForStudentForSession(student, session.getFeedbackSessionName());
        assertFalse(oldResponsesForSession.isEmpty());
        
        String newTeam = "Team 1.2";
        student.team = newTeam;
        
        enrollString = "Section | Team | Name | Email | Comment" + Const.EOL
                     + student.toEnrollmentString();
        enrollPage.enroll(enrollString);
        
        
        int numberOfNewResponses =
                getAllResponsesForStudentForSession(student, session.getFeedbackSessionName()).size();
        assertEquals(0, numberOfNewResponses);
        
    }
    
    private void loadEnrollmentPage() {
        AppUrl enrollUrl = createUrl(Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_PAGE)
                            .withUserId(testData.instructors.get("instructor1OfCourse1").googleId)
                            .withCourseId(testData.courses.get("typicalCourse1").getId());
                
        enrollPage = loginAdminToPage(browser, enrollUrl, InstructorCourseEnrollPage.class);
    }
    
    private List<FeedbackResponseAttributes> getAllTeamResponsesForStudent(StudentAttributes student) {
        List<FeedbackResponseAttributes> returnList = new ArrayList<FeedbackResponseAttributes>();
        
        List<FeedbackResponseAttributes> studentReceiverResponses = BackDoor
                .getFeedbackResponsesForReceiverForCourse(student.course, student.email);
        
        for (FeedbackResponseAttributes response : studentReceiverResponses) {
            FeedbackQuestionAttributes question = BackDoor
                    .getFeedbackQuestion(response.feedbackQuestionId);
            if (question.recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS) {
                returnList.add(response);
            }
        }
        
        List<FeedbackResponseAttributes> studentGiverResponses = BackDoor
                .getFeedbackResponsesFromGiverForCourse(student.course, student.email);
        
        for (FeedbackResponseAttributes response : studentGiverResponses) {
            FeedbackQuestionAttributes question = BackDoor
                    .getFeedbackQuestion(response.feedbackQuestionId);
            if (question.giverType == FeedbackParticipantType.TEAMS
                    || question.recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS) {
                returnList.add(response);
            }
        }
        
        return returnList;
    }
    
    private List<FeedbackResponseAttributes> getAllResponsesForStudentForSession(StudentAttributes student,
            String feedbackSessionName) {
        List<FeedbackResponseAttributes> returnList = new ArrayList<FeedbackResponseAttributes>();
        
        List<FeedbackResponseAttributes> allResponseOfStudent = getAllTeamResponsesForStudent(student);
        
        for (FeedbackResponseAttributes responseAttributes : allResponseOfStudent) {
            if (responseAttributes.feedbackSessionName.equals(feedbackSessionName)) {
                returnList.add(responseAttributes);
            }
        }
        
        return returnList;
    }
}
