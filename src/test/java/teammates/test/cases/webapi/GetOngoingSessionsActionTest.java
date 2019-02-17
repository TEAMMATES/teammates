package teammates.test.cases.webapi;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.webapi.action.GetOngoingSessionsAction;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.OngoingSessionsData;

/**
 * SUT: {@link GetOngoingSessionsAction}.
 */
public class GetOngoingSessionsActionTest extends BaseActionTest<GetOngoingSessionsAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SESSIONS_ONGOING;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    protected void testExecute() throws Exception {
        ______TS("Verify no ongoing session");

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_STARTTIME, "0",
                Const.ParamsNames.FEEDBACK_SESSION_ENDTIME, "1000",
        };

        GetOngoingSessionsAction getOngoingSessionsAction = getAction(params);
        JsonResult r = getJsonResult(getOngoingSessionsAction);

        verifyNoExistingSession(r);

        ______TS("Typical use case");

        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String courseId = instructor1OfCourse1.courseId;

        Instant startTime = Instant.now();
        Instant endTime = Instant.now().plus(5, ChronoUnit.DAYS);

        logic.createFeedbackSession(
                FeedbackSessionAttributes.builder("new-session", courseId, instructor1OfCourse1.email)
                        .withStartTime(startTime)
                        .withEndTime(endTime)
                        .withCreatedTime(startTime.minusSeconds(1))
                        .withSessionVisibleFromTime(startTime)
                        .withResultsVisibleFromTime(endTime)
                        .build());

        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_STARTTIME, String.valueOf(startTime.toEpochMilli()),
                Const.ParamsNames.FEEDBACK_SESSION_ENDTIME, String.valueOf(endTime.toEpochMilli()),
        };

        getOngoingSessionsAction = getAction(params);
        r = getJsonResult(getOngoingSessionsAction);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());
        OngoingSessionsData response = (OngoingSessionsData) r.getOutput();

        assertEquals(0, response.getTotalAwaitingSessions());
        assertEquals(1, response.getTotalOpenSessions());
        assertEquals(0, response.getTotalClosedSessions());
        assertEquals(1, response.getTotalOngoingSessions());
        assertEquals(1, response.getTotalInstitutes());
        assertEquals(1, response.getSessions().size());
    }

    @Test
    public void testExecute_notEnoughParameters_shouldFail() {
        loginAsAdmin();

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();

        String[] params = {
                Const.ParamsNames.FEEDBACK_SESSION_STARTTIME, "10",
        };
        verifyHttpParameterFailure(params);

        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_ENDTIME, "10",
        };
        verifyHttpParameterFailure(params);
    }

    @Test
    public void textExecute_boundaryValues_shouldFail() {
        ______TS("Value too high");

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_STARTTIME, "2" + Long.MAX_VALUE,
                Const.ParamsNames.FEEDBACK_SESSION_ENDTIME, "3123" + Long.MAX_VALUE,
        };

        verifyHttpParameterFailure(params);

        ______TS("Verify border values");

        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_STARTTIME, String.valueOf(Long.MIN_VALUE),
                Const.ParamsNames.FEEDBACK_SESSION_ENDTIME, String.valueOf(Long.MAX_VALUE),
        };

        verifyHttpParameterFailure(params);
    }

    @Test
    public void testExecute_appropriateBoundaryValues_shouldSucceed() {
        Instant minValuePlus30 = Instant.ofEpochMilli(Long.MIN_VALUE).plus(30, ChronoUnit.DAYS);
        Instant maxValueMinus30 = Instant.ofEpochMilli(Long.MAX_VALUE).minus(30, ChronoUnit.DAYS);

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_STARTTIME, String.valueOf(minValuePlus30.toEpochMilli()),
                Const.ParamsNames.FEEDBACK_SESSION_ENDTIME, String.valueOf(maxValueMinus30.toEpochMilli()),
        };

        GetOngoingSessionsAction getOngoingSessionsAction = getAction(params);
        JsonResult r = getJsonResult(getOngoingSessionsAction);

        verifyNoExistingSession(r);
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

    private void verifyNoExistingSession(JsonResult r) {
        assertEquals(HttpStatus.SC_OK, r.getStatusCode());
        OngoingSessionsData response = (OngoingSessionsData) r.getOutput();

        assertEquals(0, response.getTotalAwaitingSessions());
        assertEquals(0, response.getTotalOpenSessions());
        assertEquals(0, response.getTotalClosedSessions());
        assertEquals(0, response.getTotalOngoingSessions());
        assertEquals(0, response.getTotalInstitutes());
        assertEquals(0, response.getSessions().size());
    }

}
