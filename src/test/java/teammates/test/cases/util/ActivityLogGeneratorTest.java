package teammates.test.cases.util;

import javax.servlet.http.HttpServletRequest;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.appengine.api.log.AppLogLine;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.UserType;
import teammates.common.exception.PageNotFoundException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.ActivityLogEntry;
import teammates.common.util.ActivityLogGenerator;
import teammates.common.util.Const;
import teammates.test.cases.BaseTestCase;
import teammates.test.driver.AssertHelper;
import teammates.test.driver.GaeSimulation;

/**
 * SUT: {@link ActivityLogGenerator}
 */
public class ActivityLogGeneratorTest extends BaseTestCase {
    
    private ActivityLogGenerator logCenter = new ActivityLogGenerator();
    
    private GaeSimulation gaeSimulation = GaeSimulation.inst();
    
    @BeforeTest
    public void testSetup() {
        gaeSimulation.setup();
    }
    
    @Test
    public void testGenerateServletActionFailureLogMessage() {
        ______TS("With google login");
        
        gaeSimulation.loginUser("googleIdABC");
        HttpServletRequest req = gaeSimulation.createWebRequest("/randomPageWithAttack");
        Exception e = new PageNotFoundException("randomPageWithAttack");
        String logMessagePrefix = "TEAMMATESLOG|||Error when getting ActionName for requestUrl : /randomPageWithAttack"
                + "|||Servlet Action Failure|||true|||Unknown|||Unknown|||googleIdABC|||Unknown|||";
        
        String generatedMessage = logCenter.generateServletActionFailureLogMessage(req, e);
        assertTrue(generatedMessage.startsWith(logMessagePrefix));
        int googleIdPosition = generatedMessage.indexOf("googleIdABC");
        assertTrue(googleIdPosition != -1); // google id is in google id field
        assertTrue(generatedMessage.indexOf("googleIdABC", googleIdPosition + 1) != -1); // google id is in log id
        
        gaeSimulation.logoutUser();
        
        ______TS("Without google login (with key)");
        
        req = gaeSimulation.createWebRequest(Const.ActionURIs.STUDENT_COURSE_JOIN,
                Const.ParamsNames.COURSE_ID, "CS2103", Const.ParamsNames.STUDENT_EMAIL, "student@email.com",
                Const.ParamsNames.REGKEY, "KeyABC");
        e = new UnauthorizedAccessException("Unknown Registration Key KeyABC");
        generatedMessage = logCenter.generateServletActionFailureLogMessage(req, e);
        logMessagePrefix = "TEAMMATESLOG|||studentCourseJoin|||Servlet Action Failure|||true"
                    + "|||Unknown|||Unknown|||Unregistered|||Unknown|||";
        
        assertTrue(generatedMessage.startsWith(logMessagePrefix));
        assertTrue(generatedMessage.contains("student@email.com%CS2103")); // log id contain courseId and email
    }
    
    @Test
    public void testGenerateBasicActivityLogMessage() {
        ______TS("Automated task");
        HttpServletRequest req = gaeSimulation.createWebRequest(Const.ActionURIs.AUTOMATED_FEEDBACK_CLOSED_REMINDERS);
        String logMessage = "TEAMMATESLOG|||feedbackSessionClosedReminders|||feedbackSessionClosedReminders|||true|||Auto"
                + "|||Unknown|||Unknown|||Unknown|||auto task|||/auto/feedbackSessionClosedReminders";
        
        AssertHelper.assertLogMessageEquals(logMessage,
                logCenter.generateBasicActivityLogMessage(req, "auto task"));
        
        // other situations tested in testGenerateNormalPageActionLogMessage
    }
    
    @Test
    public void testGenerateNormalPageActionLogMessage() {
        ______TS("Not login");
        
        HttpServletRequest req = gaeSimulation.createWebRequest(Const.ActionURIs.INSTRUCTOR_HOME_PAGE);
        String logMessage = "TEAMMATESLOG|||instructorHomePage|||instructorHomePage|||true|||Unknown|||Unknown"
                            + "|||Unregistered|||Unknown|||Not authorized|||/page/instructorHomePage";
        
        AssertHelper.assertLogMessageEquals(logMessage,
                logCenter.generateNormalPageActionLogMessage(req, null, null, null, "Not authorized"));
        
        ______TS("Not google login but with key (not success)");
        
        req = gaeSimulation.createWebRequest(Const.ActionURIs.STUDENT_COURSE_JOIN,
                        Const.ParamsNames.COURSE_ID, "CS2103", Const.ParamsNames.STUDENT_EMAIL, "student@email.com");
        logMessage = "TEAMMATESLOG|||studentCourseJoin|||studentCourseJoin|||true|||Unknown|||Unknown|||"
                            + "Unregistered|||Unknown|||Not authorized|||/page/studentCourseJoin";
        
        String generatedMessage = logCenter.generateNormalPageActionLogMessage(req, null, null, null, "Not authorized");
        assertTrue(generatedMessage.startsWith(logMessage));
        assertTrue(generatedMessage.contains("student@email.com%CS2103")); // log id contain course id and email
        
        ______TS("Not google login but with key (success)");
        
        req = gaeSimulation.createWebRequest(Const.ActionURIs.STUDENT_COURSE_JOIN);
        logMessage = "TEAMMATESLOG|||studentCourseJoin|||studentCourseJoin|||true|||Unregistered:CS2103|||Joe"
                            + "|||Unregistered|||student@email|||Join Course|||/page/studentCourseJoin";
        StudentAttributes student = new StudentAttributes("unknownGoogleId", "student@email", "Joe",
                                            "comments", "CS2103", "team1", "section1");
        
        AssertHelper.assertLogMessageEquals(logMessage, // auth success will pass unregistered student
                logCenter.generateNormalPageActionLogMessage(req, null, null, student, "Join Course"));
    
        // --------------- Google login ---------------
        
        gaeSimulation.loginUser("googleId");
        ______TS("Google login (No account)");
        
        req = gaeSimulation.createWebRequest(Const.ActionURIs.STUDENT_HOME_PAGE);
        logMessage = "TEAMMATESLOG|||studentHomePage|||studentHomePage|||true|||Unknown|||Unknown"
                + "|||googleId|||Unknown|||Try student home|||/page/studentHomePage";
        UserType userType = new UserType("googleId");
        
        generatedMessage = logCenter.generateNormalPageActionLogMessage(req, userType, null, null, "Try student home");
        AssertHelper.assertLogMessageEquals(logMessage, generatedMessage);
        assertTrue(generatedMessage.contains("googleId")); // log id contain google id
        
        ______TS("Google login (Insturctor)");
        
        String logTemplate = "TEAMMATESLOG|||%1$s|||%1$s|||true|||%2$s|||david"
                + "|||googleId|||david@email.com|||View Result|||/page/%1$s";
        
        req = gaeSimulation.createWebRequest(Const.ActionURIs.INSTRUCTOR_COURSES_PAGE);
        logMessage = String.format(logTemplate, "instructorCoursesPage", "Instructor");
        userType = new UserType("googleId");
        userType.isInstructor = true;
        AccountAttributes acc = new AccountAttributes("googleId", "david", false, "david@email.com", "NUS");
        
        AssertHelper.assertLogMessageEquals(logMessage, // google login will pass userType and account
                logCenter.generateNormalPageActionLogMessage(req, userType, acc, null, "View Result"));

        ______TS("Google login (Insturctor and Student auto detect)");
        
        userType.isStudent = true;
        req = gaeSimulation.createWebRequest(Const.ActionURIs.STUDENT_FEEDBACK_RESULTS_PAGE);
        logMessage = String.format(logTemplate, "studentFeedbackResultsPage", "Student");
        
        AssertHelper.assertLogMessageEquals(logMessage, // google login will pass userType and account
                logCenter.generateNormalPageActionLogMessage(req, userType, acc, null, "View Result"));
        
        req = gaeSimulation.createWebRequest(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE);
        logMessage = String.format(logTemplate, "instructorCourseEditPage", "Instructor");
        
        AssertHelper.assertLogMessageEquals(logMessage, // google login will pass userType and account
                logCenter.generateNormalPageActionLogMessage(req, userType, acc, null, "View Result"));
        
        ______TS("Google login (Admin role auto detect)");
        
        userType.isAdmin = true;
        req = gaeSimulation.createWebRequest(Const.ActionURIs.STUDENT_FEEDBACK_RESULTS_PAGE);
        logMessage = String.format(logTemplate, "studentFeedbackResultsPage", "Student");
        
        AssertHelper.assertLogMessageEquals(logMessage, // google login will pass userType and account
                logCenter.generateNormalPageActionLogMessage(req, userType, acc, null, "View Result"));
        
        req = gaeSimulation.createWebRequest(Const.ActionURIs.INSTRUCTOR_COMMENTS_PAGE);
        logMessage = String.format(logTemplate, "instructorCommentsPage", "Instructor");
        
        AssertHelper.assertLogMessageEquals(logMessage, // google login will pass userType and account
                logCenter.generateNormalPageActionLogMessage(req, userType, acc, null, "View Result"));
        
        req = gaeSimulation.createWebRequest(Const.ActionURIs.ADMIN_ACTIVITY_LOG_PAGE);
        logMessage = "TEAMMATESLOG|||adminActivityLogPage|||adminActivityLogPage|||true|||Admin|||david"
                + "|||googleId|||david@email.com|||View Result|||/admin/adminActivityLogPage";
        
        AssertHelper.assertLogMessageEquals(logMessage, // google login will pass userType and account
                logCenter.generateNormalPageActionLogMessage(req, userType, acc, null, "View Result"));
        
        ______TS("Google login (Admin Masquerade Mode)");
        
        req = gaeSimulation.createWebRequest(Const.ActionURIs.INSTRUCTOR_COMMENTS_PAGE);
        userType = new UserType("googleId");
        userType.isAdmin = true;
        acc = new AccountAttributes("anotherGoogleId", "david", false, "david@email.com", "NUS");
        logMessage = "TEAMMATESLOG|||instructorCommentsPage|||instructorCommentsPage|||true|||Instructor(M)|||david"
                + "|||anotherGoogleId|||david@email.com|||View comments|||/page/instructorCommentsPage";
        
        AssertHelper.assertLogMessageEquals(logMessage, // Masquerade: userType and acc don't have same google id
                logCenter.generateNormalPageActionLogMessage(req, userType, acc, null, "View comments"));
        
        gaeSimulation.logoutUser();
    }
    
    @Test
    public void testGenerateActivityLogFromAppLogLine() {
        String logMessageWithoutTimeTaken = "TEAMMATESLOG|||instructorHome|||Pageload|||true|||Instructor"
                    + "|||UserName|||UserId|||UserEmail|||Message|||URL|||UserId20151019143729608";
        AppLogLine appLog = new AppLogLine();
        appLog.setLogMessage(logMessageWithoutTimeTaken + "|||20"); // with TimeTaken
        ActivityLogEntry entry = logCenter.generateActivityLogFromAppLogLine(appLog);
        assertEquals(logMessageWithoutTimeTaken, entry.generateLogMessage());
        assertEquals(20, entry.getActionTimeTaken());
    }
    
    @AfterTest
    public void testTearDown() {
        gaeSimulation.tearDown();
    }
    
}
