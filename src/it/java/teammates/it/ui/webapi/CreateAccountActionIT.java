package teammates.it.ui.webapi;

import org.junit.jupiter.api.Assertions;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.HibernateUtil;
import teammates.common.util.StringHelperExtension;
import teammates.storage.entity.Account;
import teammates.storage.entity.AccountRequest;
import teammates.storage.entity.Course;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.webapi.CreateAccountAction;

/**
 * SUT: {@link CreateAccountAction}.
 */
public class CreateAccountActionIT extends BaseActionIT<CreateAccountAction> {
    private DataBundle typicalBundle;

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        typicalBundle = persistDataBundle(getTypicalDataBundle());
        HibernateUtil.flushSession();
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNT;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Override
    @Test
    protected void testExecute() throws InvalidParametersException, EntityAlreadyExistsException {
        Account instructor1 = typicalBundle.accounts.get("unregisteredInstructor1");
        loginAsUnregistered(instructor1.getGoogleId());

        AccountRequest accReq = typicalBundle.accountRequests.get("unregisteredInstructor1");
        String email = accReq.getEmail();
        String institute = accReq.getInstitute();
        String name = accReq.getName();

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();

        ______TS("Null parameters");

        String[] nullParams = new String[] { Const.ParamsNames.REGKEY, null, };
        InvalidHttpParameterException ex = verifyHttpParameterFailure(nullParams);
        Assertions.assertEquals("The [key] HTTP parameter is null.", ex.getMessage());

        verifyNoTasksAdded();

        ______TS("Normal case with valid timezone");
        String timezone = "Asia/Singapore";
        AccountRequest accountRequest = logic.getAccountRequest(accReq.getId());

        String[] params = new String[] {
                Const.ParamsNames.REGKEY, accountRequest.getRegistrationKey(),
                Const.ParamsNames.TIMEZONE, timezone,
        };
        CreateAccountAction a = getAction(params);
        getJsonResult(a);

        String courseId = generateNextDemoCourseId(email, FieldValidator.COURSE_ID_MAX_LENGTH);

        Course course = logic.getCourse(courseId);
        Assertions.assertNotNull(course);
        Assertions.assertEquals("Sample Course 101", course.getName());
        Assertions.assertEquals(institute, course.getInstitute());
        Assertions.assertEquals(timezone, course.getTimeZone());

        ZoneId zoneId = ZoneId.of(timezone);
        List<FeedbackSession> feedbackSessionsList = logic.getFeedbackSessionsForCourse(courseId);
        for (FeedbackSession feedbackSession : feedbackSessionsList) {
            LocalTime actualStartTime = LocalTime.ofInstant(feedbackSession.getStartTime(), zoneId);
            LocalTime actualEndTime = LocalTime.ofInstant(feedbackSession.getEndTime(), zoneId);

            Assertions.assertEquals(LocalTime.MIDNIGHT, actualStartTime);
            Assertions.assertEquals(LocalTime.MIDNIGHT, actualEndTime);
        }

        Instructor instructor = logic.getInstructorForEmail(courseId, email);
        Assertions.assertEquals(email, instructor.getEmail());
        Assertions.assertEquals(name, instructor.getName());

        ______TS("Normal case with invalid timezone, timezone should default to UTC");

        Account instructor2 = typicalBundle.accounts.get("unregisteredInstructor2");
        loginAsUnregistered(instructor2.getGoogleId());

        accReq = typicalBundle.accountRequests.get("unregisteredInstructor2");
        email = accReq.getEmail();
        timezone = "InvalidTimezone";

        accountRequest = logic.getAccountRequest(accReq.getId());

        params = new String[] {
                Const.ParamsNames.REGKEY, accountRequest.getRegistrationKey(),
                Const.ParamsNames.TIMEZONE, timezone,
        };

        a = getAction(params);

        getJsonResult(a);

        courseId = generateNextDemoCourseId(email, FieldValidator.COURSE_ID_MAX_LENGTH);
        course = logic.getCourse(courseId);
        Assertions.assertEquals(Const.DEFAULT_TIME_ZONE, course.getTimeZone());

        feedbackSessionsList = logic.getFeedbackSessionsForCourse(courseId);
        zoneId = ZoneId.of(Const.DEFAULT_TIME_ZONE);
        for (FeedbackSession feedbackSession : feedbackSessionsList) {
            LocalTime actualStartTime = LocalTime.ofInstant(feedbackSession.getStartTime(), zoneId);
            LocalTime actualEndTime = LocalTime.ofInstant(feedbackSession.getEndTime(), zoneId);

            Assertions.assertEquals(LocalTime.MIDNIGHT, actualStartTime);
            Assertions.assertEquals(LocalTime.MIDNIGHT, actualEndTime);
        }

        ______TS("Error: registration key already used");
        verifyInvalidOperation(params);
        verifyNoTasksAdded();

        ______TS("Error: account request not found");

        params = new String[] { Const.ParamsNames.REGKEY, "unknownregkey", };
        verifyEntityNotFound(params);
        verifyNoTasksAdded();
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyAnyLoggedInUserCanAccess();
    }

    @Test
    public void testGenerateNextDemoCourseId() {
        testGenerateNextDemoCourseIdForLengthLimit(40);
        testGenerateNextDemoCourseIdForLengthLimit(20);
    }

    private void testGenerateNextDemoCourseIdForLengthLimit(int maximumIdLength) {
        String normalIdSuffix = ".gma-demo";
        String atEmail = "@gmail.tmt";
        int normalIdSuffixLength = normalIdSuffix.length(); // 9
        String strShortWithWordDemo =
                StringHelperExtension.generateStringOfLength((maximumIdLength - normalIdSuffixLength) / 2) + "-demo";
        String strWayShorterThanMaximum =
                StringHelperExtension.generateStringOfLength((maximumIdLength - normalIdSuffixLength) / 2);
        String strOneCharShorterThanMaximum =
                StringHelperExtension.generateStringOfLength(maximumIdLength - normalIdSuffixLength);
        String strOneCharLongerThanMaximum =
                StringHelperExtension.generateStringOfLength(maximumIdLength - normalIdSuffixLength + 1);
        Assertions.assertEquals(strShortWithWordDemo + normalIdSuffix,
                generateNextDemoCourseId(strShortWithWordDemo + atEmail, maximumIdLength));
        Assertions.assertEquals(strShortWithWordDemo + normalIdSuffix + "0",
                generateNextDemoCourseId(strShortWithWordDemo + normalIdSuffix, maximumIdLength));
        Assertions.assertEquals(strShortWithWordDemo + normalIdSuffix + "1",
                generateNextDemoCourseId(strShortWithWordDemo + normalIdSuffix + "0", maximumIdLength));
        Assertions.assertEquals(strWayShorterThanMaximum + normalIdSuffix,
                generateNextDemoCourseId(strWayShorterThanMaximum + atEmail, maximumIdLength));
        Assertions.assertEquals(strOneCharShorterThanMaximum + normalIdSuffix,
                generateNextDemoCourseId(strOneCharShorterThanMaximum + atEmail, maximumIdLength));
        Assertions.assertEquals(strOneCharLongerThanMaximum.substring(1) + normalIdSuffix,
                generateNextDemoCourseId(strOneCharLongerThanMaximum + atEmail, maximumIdLength));
        Assertions.assertEquals(strWayShorterThanMaximum + normalIdSuffix + "0",
                generateNextDemoCourseId(strWayShorterThanMaximum + normalIdSuffix, maximumIdLength));
        Assertions.assertEquals(strWayShorterThanMaximum + normalIdSuffix + "1",
                generateNextDemoCourseId(strWayShorterThanMaximum + normalIdSuffix + "0", maximumIdLength));
        Assertions.assertEquals(strWayShorterThanMaximum + normalIdSuffix + "10",
                generateNextDemoCourseId(strWayShorterThanMaximum + normalIdSuffix + "9", maximumIdLength));
        Assertions.assertEquals(strOneCharShorterThanMaximum.substring(2) + normalIdSuffix + "10",
                generateNextDemoCourseId(strOneCharShorterThanMaximum.substring(1) + normalIdSuffix + "9",
                        maximumIdLength));
    }

    private String generateNextDemoCourseId(String instructorEmailOrProposedCourseId, int maximumIdLength) {
        CreateAccountAction a = new CreateAccountAction();
        return a.generateNextDemoCourseId(instructorEmailOrProposedCourseId, maximumIdLength);
    }

}
