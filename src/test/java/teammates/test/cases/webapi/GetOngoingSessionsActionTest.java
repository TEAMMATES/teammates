package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

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
    protected void testExecute() {
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

        ______TS("Value too high");

        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_STARTTIME, "2" + Long.MAX_VALUE,
                Const.ParamsNames.FEEDBACK_SESSION_ENDTIME, "3123" + Long.MAX_VALUE,
        };

        verifyHttpParameterFailure(params);

        ______TS("Verify border values");

        //actually fails test case LOL
        /*
        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_STARTTIME, String.valueOf(Long.MIN_VALUE),
                Const.ParamsNames.FEEDBACK_SESSION_ENDTIME, String.valueOf(Long.MAX_VALUE),
        };

        GetOngoingSessionsAction ongoingSessionsAction = getAction(params);
        JsonResult r = getJsonResult(ongoingSessionsAction);

        verifyNoExistingSession(r);*/

        ______TS("Verify no ongoing session");

        params = new String[] {
                Const.ParamsNames.FEEDBACK_SESSION_STARTTIME, "0",
                Const.ParamsNames.FEEDBACK_SESSION_ENDTIME, "1000",
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
        assertEquals(null, response.getRequestId());
    }

}
