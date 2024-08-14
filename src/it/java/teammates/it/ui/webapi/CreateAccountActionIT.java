package teammates.it.ui.webapi;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import jakarta.transaction.Transactional;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.HibernateUtil;
import teammates.common.util.StringHelperExtension;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.AccountRequest;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.webapi.CreateAccountAction;
import teammates.ui.webapi.InvalidHttpParameterException;

/**
 * SUT: {@link CreateAccountAction}.
 */
@Ignore // TODO: remove ignore once we allow course creation in SQL
public class CreateAccountActionIT extends BaseActionIT<CreateAccountAction> {

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
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
    @Transactional
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
        assertEquals("The [key] HTTP parameter is null.", ex.getMessage());

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
        assertNotNull(course);
        assertEquals("Sample Course 101", course.getName());
        assertEquals(institute, course.getInstitute());
        assertEquals(timezone, course.getTimeZone());

        ZoneId zoneId = ZoneId.of(timezone);
        List<FeedbackSession> feedbackSessionsList = logic.getFeedbackSessionsForCourse(courseId);
        for (FeedbackSession feedbackSession : feedbackSessionsList) {
            LocalTime actualStartTime = LocalTime.ofInstant(feedbackSession.getStartTime(), zoneId);
            LocalTime actualEndTime = LocalTime.ofInstant(feedbackSession.getEndTime(), zoneId);

            assertEquals(LocalTime.MIDNIGHT, actualStartTime);
            assertEquals(LocalTime.MIDNIGHT, actualEndTime);
        }

        Instructor instructor = logic.getInstructorForEmail(courseId, email);
        assertEquals(email, instructor.getEmail());
        assertEquals(name, instructor.getName());

        List<Student> studentList = logic.getStudentsForCourse(courseId);
        List<Instructor> instructorList = logic.getInstructorsByCourse(courseId);
        verifySpecifiedTasksAdded(Const.TaskQueue.SEARCH_INDEXING_QUEUE_NAME,
                studentList.size() + instructorList.size());

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
        assertEquals(Const.DEFAULT_TIME_ZONE, course.getTimeZone());

        feedbackSessionsList = logic.getFeedbackSessionsForCourse(courseId);
        zoneId = ZoneId.of(Const.DEFAULT_TIME_ZONE);
        for (FeedbackSession feedbackSession : feedbackSessionsList) {
            LocalTime actualStartTime = LocalTime.ofInstant(feedbackSession.getStartTime(), zoneId);
            LocalTime actualEndTime = LocalTime.ofInstant(feedbackSession.getEndTime(), zoneId);

            assertEquals(LocalTime.MIDNIGHT, actualStartTime);
            assertEquals(LocalTime.MIDNIGHT, actualEndTime);
        }

        verifySpecifiedTasksAdded(Const.TaskQueue.SEARCH_INDEXING_QUEUE_NAME,
                studentList.size() + instructorList.size());

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
        assertEquals(strShortWithWordDemo + normalIdSuffix,
                generateNextDemoCourseId(strShortWithWordDemo + atEmail, maximumIdLength));
        assertEquals(strShortWithWordDemo + normalIdSuffix + "0",
                generateNextDemoCourseId(strShortWithWordDemo + normalIdSuffix, maximumIdLength));
        assertEquals(strShortWithWordDemo + normalIdSuffix + "1",
                generateNextDemoCourseId(strShortWithWordDemo + normalIdSuffix + "0", maximumIdLength));
        assertEquals(strWayShorterThanMaximum + normalIdSuffix,
                generateNextDemoCourseId(strWayShorterThanMaximum + atEmail, maximumIdLength));
        assertEquals(strOneCharShorterThanMaximum + normalIdSuffix,
                generateNextDemoCourseId(strOneCharShorterThanMaximum + atEmail, maximumIdLength));
        assertEquals(strOneCharLongerThanMaximum.substring(1) + normalIdSuffix,
                generateNextDemoCourseId(strOneCharLongerThanMaximum + atEmail, maximumIdLength));
        assertEquals(strWayShorterThanMaximum + normalIdSuffix + "0",
                generateNextDemoCourseId(strWayShorterThanMaximum + normalIdSuffix, maximumIdLength));
        assertEquals(strWayShorterThanMaximum + normalIdSuffix + "1",
                generateNextDemoCourseId(strWayShorterThanMaximum + normalIdSuffix + "0", maximumIdLength));
        assertEquals(strWayShorterThanMaximum + normalIdSuffix + "10",
                generateNextDemoCourseId(strWayShorterThanMaximum + normalIdSuffix + "9", maximumIdLength));
        assertEquals(strOneCharShorterThanMaximum.substring(2) + normalIdSuffix + "10",
                generateNextDemoCourseId(strOneCharShorterThanMaximum.substring(1) + normalIdSuffix + "9",
                        maximumIdLength));
    }

    private String generateNextDemoCourseId(String instructorEmailOrProposedCourseId, int maximumIdLength) {
        CreateAccountAction a = new CreateAccountAction();
        return a.generateNextDemoCourseId(instructorEmailOrProposedCourseId, maximumIdLength);
    }

}
