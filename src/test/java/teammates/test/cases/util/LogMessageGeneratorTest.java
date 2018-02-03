package teammates.test.cases.util;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import teammates.common.datatransfer.UserType;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.PageNotFoundException;
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

        UserType loginUser = new UserType("googleIdABC");
        String url = "/randomPage";
        Map<String, String[]> paramMap = new HashMap<>();
        Exception e = new PageNotFoundException("randomPage");
        String logMessagePrefix = "TEAMMATESLOG|||Error when getting ActionName for requestUrl : /randomPage"
                                  + "|||Servlet Action Failure|||true|||Unregistered|||Unknown|||googleIdABC|||Unknown|||";

        String generatedMessage = logCenter.generateActionFailureLogMessage(url, paramMap, e, loginUser);
        assertTrue(generatedMessage.startsWith(logMessagePrefix));
        AssertHelper.assertLogIdContainsUserId(generatedMessage, "googleIdABC");

        ______TS("Without google login (with key)");

        url = Const.ActionURIs.STUDENT_COURSE_JOIN;
        paramMap = generateRequestParamsWithRegKey();
        e = new UnauthorizedAccessException("Unknown Registration Key KeyABC");
        generatedMessage = logCenter.generateActionFailureLogMessage(url, paramMap, e, null);
        logMessagePrefix = "TEAMMATESLOG|||studentCourseJoin|||Servlet Action Failure|||true"
                           + "|||Unknown|||Unknown|||Unknown|||Unknown|||";

        assertTrue(generatedMessage.startsWith(logMessagePrefix));
        AssertHelper.assertLogIdContainsUserId(generatedMessage, "student@email.com%CS2103");
    }

    @Test
    public void generateLogMessage_basicInformation() {
        ______TS("Automated task");
        String url = Const.ActionURIs.AUTOMATED_FEEDBACK_CLOSED_REMINDERS;
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

        String url = Const.ActionURIs.INSTRUCTOR_HOME_PAGE;
        Map<String, String[]> paramMap = new HashMap<>();
        String logMessage = "TEAMMATESLOG|||instructorHomePage|||instructorHomePage|||true|||Unknown|||Unknown"
                            + "|||Unknown|||Unknown|||Not authorized|||/page/instructorHomePage";

        String generatedMessage =
                logCenter.generatePageActionLogMessage(url, paramMap, null, null, null, "Not authorized");
        AssertHelper.assertLogMessageEquals(logMessage, generatedMessage);

        ______TS("Not google login but with key (failure)");

        url = Const.ActionURIs.STUDENT_COURSE_JOIN;
        paramMap = generateRequestParamsWithRegKey();
        logMessage = "TEAMMATESLOG|||studentCourseJoin|||studentCourseJoin|||true|||Unknown|||Unknown|||"
                     + "Unknown|||Unknown|||Not authorized|||/page/studentCourseJoin";

        generatedMessage =
                logCenter.generatePageActionLogMessage(url, paramMap, null, null, null, "Not authorized");
        AssertHelper.assertLogMessageEqualsForUnregisteredStudentUser(logMessage, generatedMessage,
                "student@email.com", "CS2103");

        ______TS("Not google login but with key (success)");

        url = Const.ActionURIs.STUDENT_COURSE_JOIN + "?user=test@email.com&course=1";
        logMessage = "TEAMMATESLOG|||studentCourseJoin|||studentCourseJoin|||true|||Unregistered:CS2103|||Joe"
                     + "|||Unknown|||student@email|||Join Course|||" + url;
        StudentAttributes student = StudentAttributes
                .builder("CS2103", "Joe", "student@email")
                .withSection("section1")
                .withTeam("team1")
                .withComments("comments")
                .withGoogleId("unknownGoogleId")
                .build();

        // auth success : unregistered student will be passed
        generatedMessage =
                logCenter.generatePageActionLogMessage(url, paramMap, null, null, student, "Join Course");
        AssertHelper.assertLogMessageEqualsForUnregisteredStudentUser(logMessage, generatedMessage,
                "student@email.com", "CS2103");

        // --------------- Google login ---------------

        ______TS("Google login (No account)");

        url = Const.ActionURIs.STUDENT_HOME_PAGE + "?course=A&user=test";
        paramMap = new HashMap<>();
        logMessage = "TEAMMATESLOG|||studentHomePage|||studentHomePage|||true|||Unregistered|||Unknown"
                     + "|||googleId|||Unknown|||Try student home|||" + url;
        UserType userType = new UserType("googleId");

        // userType and account will be passed for logged-in user
        generatedMessage =
                logCenter.generatePageActionLogMessage(url, paramMap, userType, null, null, "Try student home");
        AssertHelper.assertLogMessageEquals(logMessage, generatedMessage);

        ______TS("Google login (Student)");

        String logTemplate = "TEAMMATESLOG|||%1$s|||%1$s|||true|||%2$s|||david"
                             + "|||googleId|||david@email.com|||View Result|||/page/%1$s";

        url = Const.ActionURIs.STUDENT_HOME_PAGE;
        logMessage = String.format(logTemplate, "studentHomePage", "Student");
        userType.isStudent = true;
        AccountAttributes acc = AccountAttributes.builder()
                .withGoogleId("googleId")
                .withName("david")
                .withEmail("david@email.com")
                .withInstitute("NUS")
                .withIsInstructor(false)
                .withDefaultStudentProfileAttributes("idOfNewStudent")
                .build();
        generatedMessage =
                logCenter.generatePageActionLogMessage(url, paramMap, userType, acc, null, "View Result");
        AssertHelper.assertLogMessageEquals(logMessage, generatedMessage);

        ______TS("Google login (Instructor and Student auto detect)");

        userType.isInstructor = true;
        url = Const.ActionURIs.STUDENT_FEEDBACK_RESULTS_PAGE;
        logMessage = String.format(logTemplate, "studentFeedbackResultsPage", "Student");

        generatedMessage =
                logCenter.generatePageActionLogMessage(url, paramMap, userType, acc, null, "View Result");
        AssertHelper.assertLogMessageEquals(logMessage, generatedMessage);

        url = Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE;
        logMessage = String.format(logTemplate, "instructorCourseEditPage", "Instructor");

        generatedMessage =
                logCenter.generatePageActionLogMessage(url, paramMap, userType, acc, null, "View Result");
        AssertHelper.assertLogMessageEquals(logMessage, generatedMessage);

        ______TS("Google login (Admin role auto detect)");

        userType.isAdmin = true;
        url = Const.ActionURIs.STUDENT_FEEDBACK_RESULTS_PAGE;
        logMessage = String.format(logTemplate, "studentFeedbackResultsPage", "Student");

        generatedMessage =
                logCenter.generatePageActionLogMessage(url, paramMap, userType, acc, null, "View Result");
        AssertHelper.assertLogMessageEquals(logMessage, generatedMessage);

        url = Const.ActionURIs.INSTRUCTOR_COURSES_PAGE;
        logMessage = String.format(logTemplate, "instructorCoursesPage", "Instructor");

        generatedMessage =
                logCenter.generatePageActionLogMessage(url, paramMap, userType, acc, null, "View Result");
        AssertHelper.assertLogMessageEquals(logMessage, generatedMessage);

        url = Const.ActionURIs.ADMIN_ACTIVITY_LOG_PAGE;
        logMessage = "TEAMMATESLOG|||adminActivityLogPage|||adminActivityLogPage|||true|||Admin|||david"
                     + "|||googleId|||david@email.com|||View Result|||/admin/adminActivityLogPage";

        generatedMessage = logCenter.generatePageActionLogMessage(url, paramMap, userType, acc, null,
                "View Result");
        AssertHelper.assertLogMessageEquals(logMessage, generatedMessage);

        ______TS("Google login (Admin Masquerade Mode)");

        url = Const.ActionURIs.INSTRUCTOR_COURSES_PAGE;
        userType.isAdmin = true;
        acc = AccountAttributes.builder()
                .withGoogleId("anotherGoogleId")
                .withName("david")
                .withEmail("david@email.com")
                .withInstitute("NUS")
                .withIsInstructor(false)
                .withDefaultStudentProfileAttributes("anotherGoogleId")
                .build();
        logMessage = "TEAMMATESLOG|||instructorCoursesPage|||instructorCoursesPage|||true|||Instructor(M)|||david"
                     + "|||anotherGoogleId|||david@email.com|||View comments|||/page/instructorCoursesPage";

        // masquerade: userType and account don't have the same google id
        generatedMessage =
                logCenter.generatePageActionLogMessage(url, paramMap, userType, acc, null, "View comments");
        AssertHelper.assertLogMessageEqualsInMasqueradeMode(logMessage, generatedMessage, userType.id);
    }

    private Map<String, String[]> generateRequestParamsWithRegKey() {
        Map<String, String[]> params = new HashMap<>();
        params.put(Const.ParamsNames.COURSE_ID, new String[] { "CS2103" });
        params.put(Const.ParamsNames.STUDENT_EMAIL, new String[] { "student@email.com" });
        params.put(Const.ParamsNames.REGKEY, new String[] { "KeyABC" });
        return params;
    }
}
