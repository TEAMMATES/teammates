package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.NationalityHelper;
import teammates.ui.webapi.action.GetNationalitiesAction;
import teammates.ui.webapi.action.GetNationalitiesAction.NationalityData;
import teammates.ui.webapi.action.JsonResult;

/**
 * SUT: {@link GetNationalitiesAction}.
 */
public class GetNationalitiesActionTest extends BaseActionTest<GetNationalitiesAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.NATIONALITIES;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    protected void testExecute() throws Exception {

        ______TS("List of nationalities fetched matches the list stored in the server");

        GetNationalitiesAction action = getAction();
        JsonResult result = getJsonResult(action);
        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        NationalityData output = (NationalityData) result.getOutput();

        assertEquals(NationalityHelper.getNationalities().toString(), output.getNationalities().toString());
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        verifyAnyUserCanAccess();
    }
}
