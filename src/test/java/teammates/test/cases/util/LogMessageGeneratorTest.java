package teammates.test.cases.util;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.UserInfo;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.LogMessageGenerator;
import teammates.test.cases.BaseTestCase;
import teammates.test.driver.AssertHelper;

/**
 * SUT: {@link LogMessageGenerator}.
 */
public class LogMessageGeneratorTest extends BaseTestCase {

    private LogMessageGenerator logCenter = new LogMessageGenerator();

    @Test
    public void generateLogMessage_servletActionFailure() {
        ______TS("With google login");

        UserInfo loginUser = new UserInfo("googleIdABC");
        String url = "/randomPage";
        Map<String, String[]> paramMap = new HashMap<>();
        Exception e = new Exception("randomPage");
        String logMessagePrefix = "TEAMMATESLOG|||Error when getting ActionName for requestUrl : /randomPage"
                                  + "|||Servlet Action Failure|||true|||Unregistered|||Unknown|||googleIdABC|||Unknown|||";

        String generatedMessage = logCenter.generateActionFailureLogMessage(url, paramMap, e, loginUser);
        assertTrue(generatedMessage.startsWith(logMessagePrefix));
        AssertHelper.assertLogIdContainsUserId(generatedMessage, "googleIdABC");

        ______TS("Without google login (with key)");

        url = Const.WebPageURIs.JOIN_PAGE;
        paramMap = generateRequestParamsWithRegKey();
        e = new UnauthorizedAccessException("Unknown Registration Key KeyABC");
        generatedMessage = logCenter.generateActionFailureLogMessage(url, paramMap, e, null);
        logMessagePrefix = "TEAMMATESLOG|||join|||Servlet Action Failure|||true"
                           + "|||Unknown|||Unknown|||Unknown|||Unknown|||";

        assertTrue(generatedMessage.startsWith(logMessagePrefix));
        AssertHelper.assertLogIdContainsUserId(generatedMessage, "student@email.com%CS2103");
    }

    @Test
    public void generateLogMessage_basicInformation() {
        ______TS("Automated task");
        String url = Const.CronJobURIs.AUTOMATED_FEEDBACK_CLOSED_REMINDERS;
        Map<String, String[]> paramMap = new HashMap<>();
        String logMessage = "TEAMMATESLOG|||feedbackSessionClosedReminders|||feedbackSessionClosedReminders|||true"
                            + "|||Auto|||Unknown|||Unknown|||Unknown|||auto task|||/auto/feedbackSessionClosedReminders";

        String generatedMessage =
                logCenter.generateBasicActivityLogMessage(url, paramMap, "auto task", null);
        AssertHelper.assertLogMessageEqualsIgnoreLogId(logMessage, generatedMessage);
        AssertHelper.assertLogIdContainsUserId(generatedMessage, "Auto");

        // other situations are tested in generateLogMessage_normalPageAction()
    }

    @Test
    public void generateLogMessage_normalPageAction() {
        ______TS("Not login");

        String url = Const.WebPageURIs.INSTRUCTOR_HOME_PAGE;
        Map<String, String[]> paramMap = new HashMap<>();
        String logMessage = "TEAMMATESLOG|||instructor/home|||instructor/home|||true|||Unknown|||Unknown"
                            + "|||Unknown|||Unknown|||Not authorized|||/web/instructor/home";

        String generatedMessage =
                logCenter.generatePageActionLogMessage(url, paramMap, null, null, null, "Not authorized");
        AssertHelper.assertLogMessageEquals(logMessage, generatedMessage);

        ______TS("Not google login but with key (failure)");

        url = Const.WebPageURIs.JOIN_PAGE;
        paramMap = generateRequestParamsWithRegKey();
        logMessage = "TEAMMATESLOG|||join|||join|||true|||Unknown|||Unknown|||"
                     + "Unknown|||Unknown|||Not authorized|||/web/join";

        generatedMessage =
                logCenter.generatePageActionLogMessage(url, paramMap, null, null, null, "Not authorized");
        AssertHelper.assertLogMessageEqualsForUnregisteredStudentUser(logMessage, generatedMessage,
                "student@email.com", "CS2103");

        ______TS("Not google login but with key (success)");

        url = Const.WebPageURIs.JOIN_PAGE + "?user=test@email.com&course=1";
        logMessage = "TEAMMATESLOG|||join|||join|||true|||Unregistered:CS2103|||Joe"
                     + "|||Unknown|||student@email|||Join Course|||" + url;
        StudentAttributes student = StudentAttributes
                .builder("CS2103", "student@email")
                .withName("Joe")
                .withSectionName("section1")
                .withTeamName("team1")
                .withComment("comments")
                .withGoogleId("unknownGoogleId")
                .build();

        // auth success : unregistered student will be passed
        generatedMessage =
                logCenter.generatePageActionLogMessage(url, paramMap, null, null, student, "Join Course");
        AssertHelper.assertLogMessageEqualsForUnregisteredStudentUser(logMessage, generatedMessage,
                "student@email.com", "CS2103");

        // --------------- Google login ---------------

        ______TS("Google login (No account)");

        url = Const.WebPageURIs.STUDENT_HOME_PAGE + "?course=A&user=test";
        paramMap = new HashMap<>();
        logMessage = "TEAMMATESLOG|||student/home|||student/home|||true|||Unregistered|||Unknown"
                     + "|||googleId|||Unknown|||Try student home|||" + url;
        UserInfo userInfo = new UserInfo("googleId");

        // userInfo and account will be passed for logged-in user
        generatedMessage =
                logCenter.generatePageActionLogMessage(url, paramMap, userInfo, null, null, "Try student home");
        AssertHelper.assertLogMessageEquals(logMessage, generatedMessage);

        ______TS("Google login (Student)");

        String logTemplate = "TEAMMATESLOG|||%1$s|||%1$s|||true|||%2$s|||david"
                             + "|||googleId|||david@email.com|||View Result|||/page/%1$s";

        url = Const.WebPageURIs.STUDENT_HOME_PAGE;
        logMessage = String.format(logTemplate, "studentHomePage", "Student");
        userInfo.isStudent = true;
        AccountAttributes acc = AccountAttributes.builder("googleId")
                .withName("david")
                .withEmail("david@email.com")
                .withInstitute("NUS")
                .withIsInstructor(false)
                .build();
        generatedMessage =
                logCenter.generatePageActionLogMessage(url, paramMap, userInfo, acc, null, "View Result");
        // AssertHelper.assertLogMessageEquals(logMessage, generatedMessage);

        ______TS("Google login (Instructor and Student auto detect)");

        userInfo.isInstructor = true;
        url = Const.WebPageURIs.STUDENT_SESSION_RESULTS_PAGE;
        logMessage = String.format(logTemplate, "studentFeedbackResultsPage", "Student");

        generatedMessage =
                logCenter.generatePageActionLogMessage(url, paramMap, userInfo, acc, null, "View Result");
        // AssertHelper.assertLogMessageEquals(logMessage, generatedMessage);

        url = Const.WebPageURIs.INSTRUCTOR_COURSE_EDIT_PAGE;
        logMessage = String.format(logTemplate, "instructorCourseEditPage", "Instructor");

        generatedMessage =
                logCenter.generatePageActionLogMessage(url, paramMap, userInfo, acc, null, "View Result");
        // AssertHelper.assertLogMessageEquals(logMessage, generatedMessage);

        ______TS("Google login (Admin role auto detect)");

        userInfo.isAdmin = true;
        url = Const.WebPageURIs.STUDENT_SESSION_RESULTS_PAGE;
        logMessage = String.format(logTemplate, "studentFeedbackResultsPage", "Student");

        generatedMessage =
                logCenter.generatePageActionLogMessage(url, paramMap, userInfo, acc, null, "View Result");
        // AssertHelper.assertLogMessageEquals(logMessage, generatedMessage);

        url = Const.WebPageURIs.INSTRUCTOR_COURSES_PAGE;
        logMessage = String.format(logTemplate, "instructor/courses", "Instructor");

        generatedMessage =
                logCenter.generatePageActionLogMessage(url, paramMap, userInfo, acc, null, "View Result");
        // AssertHelper.assertLogMessageEquals(logMessage, generatedMessage);

        ______TS("Google login (Admin Masquerade Mode)");

        url = Const.WebPageURIs.INSTRUCTOR_COURSES_PAGE;
        userInfo.isAdmin = true;
        acc = AccountAttributes.builder("anotherGoogleId")
                .withName("david")
                .withEmail("david@email.com")
                .withInstitute("NUS")
                .withIsInstructor(false)
                .build();
        logMessage = "TEAMMATESLOG|||instructor/courses|||instructor/courses|||true|||Instructor(M)|||david"
                     + "|||anotherGoogleId|||david@email.com|||View comments|||/web/instructor/courses";

        // masquerade: userInfo and account don't have the same google id
        generatedMessage =
                logCenter.generatePageActionLogMessage(url, paramMap, userInfo, acc, null, "View comments");
        AssertHelper.assertLogMessageEqualsInMasqueradeMode(logMessage, generatedMessage, userInfo.id);
    }

    private Map<String, String[]> generateRequestParamsWithRegKey() {
        Map<String, String[]> params = new HashMap<>();
        params.put(Const.ParamsNames.COURSE_ID, new String[] { "CS2103" });
        params.put(Const.ParamsNames.STUDENT_EMAIL, new String[] { "student@email.com" });
        params.put(Const.ParamsNames.REGKEY, new String[] { "KeyABC" });
        return params;
    }
}
