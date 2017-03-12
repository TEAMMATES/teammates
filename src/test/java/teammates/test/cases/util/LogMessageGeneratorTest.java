package teammates.test.cases.util;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import com.beust.jcommander.internal.Maps;

import teammates.common.datatransfer.UserType;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.PageNotFoundException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.LogMessageGenerator;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.logic.api.EmailGenerator;
import teammates.test.cases.BaseTestCase;
import teammates.test.driver.AssertHelper;

/**
 * SUT: {@link LogMessageGenerator}
 */
public class LogMessageGeneratorTest extends BaseTestCase {

    private LogMessageGenerator logCenter = new LogMessageGenerator();

    @Test
    public void testGenerateSystemErrorReportLogMessage() {
        ______TS("With google login");

        UserType loginUser = new UserType("googleIdABC");
        Map<String, String[]> mockParamMap = Maps.newHashMap();
        String url = Const.ActionURIs.INSTRUCTOR_HOME_PAGE;
        Exception e = new IllegalArgumentException();
        EmailWrapper errorEmail = generateEmailWrapperFromException(e);
        String logMessagePrefix = "TEAMMATESLOG|||instructorHomePage|||System Error Report|||true|||Unregistered"
                + "|||Unknown|||googleIdABC|||Unknown|||";

        String generatedMessage = logCenter.generateSystemErrorLogMessage(url, mockParamMap, errorEmail,
                loginUser);
        assertTrue(generatedMessage.startsWith(logMessagePrefix));
        AssertHelper.assertLogIdContainUserId(generatedMessage, "googleIdABC");

        ______TS("Without google login (with key)");

        url = Const.ActionURIs.STUDENT_COURSE_JOIN;
        mockParamMap = generateRequestParamsWithRegKey();
        e = new IndexOutOfBoundsException();
        errorEmail = generateEmailWrapperFromException(e);

        generatedMessage = logCenter.generateSystemErrorLogMessage(url, mockParamMap,
                errorEmail, null);
        logMessagePrefix = "TEAMMATESLOG|||studentCourseJoin|||System Error Report|||true|||Unknown"
                + "|||Unknown|||Unknown|||Unknown|||";

        assertTrue(generatedMessage.startsWith(logMessagePrefix));
        AssertHelper.assertLogIdContainUserId(generatedMessage, "student@email.com%CS2103");
    }

    @Test
    public void testGenerateServletActionFailureLogMessage() {
        ______TS("With google login");

        UserType loginUser = new UserType("googleIdABC");
        String url = "/randomPage";
        Map<String, String[]> mockParamMap = Maps.newHashMap();
        Exception e = new PageNotFoundException("randomPage");
        String logMessagePrefix = "TEAMMATESLOG|||Error when getting ActionName for requestUrl : /randomPage"
                + "|||Servlet Action Failure|||true|||Unregistered|||Unknown|||googleIdABC|||Unknown|||";

        String generatedMessage = logCenter.generateActionFailureLogMessage(url, mockParamMap, e, loginUser);
        assertTrue(generatedMessage.startsWith(logMessagePrefix));
        AssertHelper.assertLogIdContainUserId(generatedMessage, "googleIdABC");

        ______TS("Without google login (with key)");

        url = Const.ActionURIs.STUDENT_COURSE_JOIN;
        mockParamMap = generateRequestParamsWithRegKey();
        e = new UnauthorizedAccessException("Unknown Registration Key KeyABC");
        generatedMessage = logCenter.generateActionFailureLogMessage(url, mockParamMap, e, null);
        logMessagePrefix = "TEAMMATESLOG|||studentCourseJoin|||Servlet Action Failure|||true"
                + "|||Unknown|||Unknown|||Unknown|||Unknown|||";

        assertTrue(generatedMessage.startsWith(logMessagePrefix));
        AssertHelper.assertLogIdContainUserId(generatedMessage, "student@email.com%CS2103");
    }

    @Test
    public void testGenerateBasicActivityLogMessage() {
        ______TS("Automated task");
        String url = Const.ActionURIs.AUTOMATED_FEEDBACK_CLOSED_REMINDERS;
        Map<String, String[]> mockParamMap = Maps.newHashMap();
        String logMessage = "TEAMMATESLOG|||feedbackSessionClosedReminders|||feedbackSessionClosedReminders|||true"
                + "|||Auto|||Unknown|||Unknown|||Unknown|||auto task|||/auto/feedbackSessionClosedReminders";

        String generatedMessage = logCenter.generateBasicActivityLogMessage(url, mockParamMap, "auto task",
                null);
        AssertHelper.assertLogMessageEqualsWithoutId(logMessage, generatedMessage);
        assertTrue(generatedMessage.contains("Auto" + Const.ActivityLog.FIELD_CONNECTOR)); // log
                                                                                           // id
                                                                                           // contains
                                                                                           // auto

        // other situations are tested in
        // testGenerateNormalPageActionLogMessage()
    }

    @Test
    public void testGenerateNormalPageActionLogMessage() {
        ______TS("Not login");

        String url = Const.ActionURIs.INSTRUCTOR_HOME_PAGE;
        Map<String, String[]> mockParamMap = Maps.newHashMap();
        String logMessage = "TEAMMATESLOG|||instructorHomePage|||instructorHomePage|||true|||Unknown|||Unknown"
                + "|||Unknown|||Unknown|||Not authorized|||/page/instructorHomePage";

        String generatedMessage = logCenter.generatePageActionLogMessage(url, mockParamMap,
                null, null, null, "Not authorized");
        AssertHelper.assertLogMessageEquals(logMessage, generatedMessage);

        ______TS("Not google login but with key (not success)");

        url = Const.ActionURIs.STUDENT_COURSE_JOIN;
        mockParamMap = generateRequestParamsWithRegKey();
        logMessage = "TEAMMATESLOG|||studentCourseJoin|||studentCourseJoin|||true|||Unknown|||Unknown|||"
                + "Unknown|||Unknown|||Not authorized|||/page/studentCourseJoin";

        generatedMessage = logCenter.generatePageActionLogMessage(url, mockParamMap,
                null, null, null, "Not authorized");
        AssertHelper.assertLogMessageEqualsForUnregisteredStudentUser(logMessage, generatedMessage,
                "student@email.com", "CS2103");

        ______TS("Not google login but with key (success)");

        url = Const.ActionURIs.STUDENT_COURSE_JOIN + "?user=test@email.com&course=1";
        logMessage = "TEAMMATESLOG|||studentCourseJoin|||studentCourseJoin|||true|||Unregistered:CS2103|||Joe"
                + "|||Unknown|||student@email|||Join Course|||" + url;
        StudentAttributes student = new StudentAttributes("unknownGoogleId", "student@email", "Joe",
                "comments", "CS2103", "team1", "section1");

        // auth success : unregistered student will be passed
        generatedMessage = logCenter.generatePageActionLogMessage(url, mockParamMap, null, null,
                student, "Join Course");
        AssertHelper.assertLogMessageEqualsForUnregisteredStudentUser(logMessage, generatedMessage,
                "student@email.com", "CS2103");

        // --------------- Google login ---------------

        ______TS("Google login (No account)");

        url = Const.ActionURIs.STUDENT_HOME_PAGE + "?course=A&user=test";
        mockParamMap = Maps.newHashMap();
        logMessage = "TEAMMATESLOG|||studentHomePage|||studentHomePage|||true|||Unregistered|||Unknown"
                + "|||googleId|||Unknown|||Try student home|||" + url;
        UserType userType = new UserType("googleId");

        generatedMessage = logCenter.generatePageActionLogMessage(url, mockParamMap,
                userType, null, null, "Try student home");
        AssertHelper.assertLogMessageEquals(logMessage, generatedMessage);

        ______TS("Google login (Insturctor)");

        String logTemplate = "TEAMMATESLOG|||%1$s|||%1$s|||true|||%2$s|||david"
                + "|||googleId|||david@email.com|||View Result|||/page/%1$s";

        url = Const.ActionURIs.INSTRUCTOR_COURSES_PAGE;
        logMessage = String.format(logTemplate, "instructorCoursesPage", "Instructor");
        userType.isInstructor = true;
        AccountAttributes acc = new AccountAttributes("googleId", "david", false, "david@email.com", "NUS");

        // userType and account will be passed for logged-in user
        generatedMessage = logCenter.generatePageActionLogMessage(url, mockParamMap, userType, acc, null,
                "View Result");
        AssertHelper.assertLogMessageEquals(logMessage, generatedMessage);

        ______TS("Google login (Insturctor and Student auto detect)");

        userType.isStudent = true;
        url = Const.ActionURIs.STUDENT_FEEDBACK_RESULTS_PAGE;
        logMessage = String.format(logTemplate, "studentFeedbackResultsPage", "Student");

        // userType and account will be passed for logged-in user
        generatedMessage = logCenter.generatePageActionLogMessage(url, mockParamMap, userType, acc, null,
                "View Result");
        AssertHelper.assertLogMessageEquals(logMessage, generatedMessage);

        url = Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE;
        logMessage = String.format(logTemplate, "instructorCourseEditPage", "Instructor");

        // userType and account will be passed for logged-in user
        generatedMessage = logCenter.generatePageActionLogMessage(url, mockParamMap, userType, acc, null,
                "View Result");
        AssertHelper.assertLogMessageEquals(logMessage, generatedMessage);

        ______TS("Google login (Admin role auto detect)");

        userType.isAdmin = true;
        url = Const.ActionURIs.STUDENT_FEEDBACK_RESULTS_PAGE;
        logMessage = String.format(logTemplate, "studentFeedbackResultsPage", "Student");

        // userType and account will be passed for logged-in user
        generatedMessage = logCenter.generatePageActionLogMessage(url, mockParamMap, userType, acc, null,
                "View Result");
        AssertHelper.assertLogMessageEquals(logMessage, generatedMessage);

        url = Const.ActionURIs.INSTRUCTOR_COMMENTS_PAGE;
        logMessage = String.format(logTemplate, "instructorCommentsPage", "Instructor");

        // userType and account will be passed for logged-in user
        generatedMessage = logCenter.generatePageActionLogMessage(url, mockParamMap, userType, acc, null,
                "View Result");
        AssertHelper.assertLogMessageEquals(logMessage, generatedMessage);

        url = Const.ActionURIs.ADMIN_ACTIVITY_LOG_PAGE;
        logMessage = "TEAMMATESLOG|||adminActivityLogPage|||adminActivityLogPage|||true|||Admin|||david"
                + "|||googleId|||david@email.com|||View Result|||/admin/adminActivityLogPage";

        // userType and account will be passed for logged-in user
        generatedMessage = logCenter.generatePageActionLogMessage(url, mockParamMap, userType, acc, null,
                "View Result");
        AssertHelper.assertLogMessageEquals(logMessage, generatedMessage);

        ______TS("Google login (Admin Masquerade Mode)");

        url = Const.ActionURIs.INSTRUCTOR_COMMENTS_PAGE;
        userType.isAdmin = true;
        acc = new AccountAttributes("anotherGoogleId", "david", false, "david@email.com", "NUS");
        logMessage = "TEAMMATESLOG|||instructorCommentsPage|||instructorCommentsPage|||true|||Instructor(M)|||david"
                + "|||anotherGoogleId|||david@email.com|||View comments|||/page/instructorCommentsPage";

        // Masquerade: userType and acc don't have the same google id
        generatedMessage = logCenter.generatePageActionLogMessage(url, mockParamMap, userType, acc, null,
                "View comments");
        AssertHelper.assertLogMessageEquals(logMessage, generatedMessage);

    }

    private Map<String, String[]> generateRequestParamsWithRegKey() {
        Map<String, String[]> params = new HashMap<String, String[]>();
        params.put(Const.ParamsNames.COURSE_ID, new String[] { "CS2103" });
        params.put(Const.ParamsNames.STUDENT_EMAIL, new String[] { "student@email.com" });
        params.put(Const.ParamsNames.REGKEY, new String[] { "KeyABC" });
        return params;
    }

    private EmailWrapper generateEmailWrapperFromException(Throwable t) {
        return new EmailGenerator().generateSystemErrorEmail("GET", "MAC OS", "/page/somePage",
                                                             "http://example/", "{}", null, t);
    }

}
