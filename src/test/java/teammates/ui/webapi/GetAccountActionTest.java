package teammates.ui.webapi;

import java.util.stream.Collectors;

import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.util.Const;
import teammates.ui.output.AccountData;

/**
 * SUT: {@link GetAccountAction}.
 */
@Ignore
public class GetAccountActionTest extends BaseActionTest<GetAccountAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNT;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test
    protected void testExecute() {
        AccountAttributes instructor1OfCourse1 = typicalBundle.accounts.get("instructor1OfCourse1");

        loginAsAdmin();

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();

        ______TS("account not exist");

        String[] nonExistParams = {
                Const.ParamsNames.INSTRUCTOR_ID, "non-exist-account",
        };

        EntityNotFoundException enfe = verifyEntityNotFound(nonExistParams);
        assertEquals("Account does not exist.", enfe.getMessage());

        ______TS("typical success case");

        String[] params = {
                Const.ParamsNames.INSTRUCTOR_ID, instructor1OfCourse1.getGoogleId(),
        };

        GetAccountAction a = getAction(params);
        JsonResult r = getJsonResult(a);

        AccountData response = (AccountData) r.getOutput();

        assertEquals(response.getGoogleId(), instructor1OfCourse1.getGoogleId());
        assertEquals(response.getName(), instructor1OfCourse1.getName());
        assertEquals(response.getEmail(), instructor1OfCourse1.getEmail());
        assertEquals(
                response.getReadNotifications(),
                instructor1OfCourse1.getReadNotifications()
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                        e -> e.getKey(),
                        e -> e.getValue().toEpochMilli()
                    ))
        );

        ______TS("Failure: invalid account not found");

        String[] invalidParams = {
                Const.ParamsNames.INSTRUCTOR_ID, "invalid-google-id",
        };

        enfe = verifyEntityNotFound(invalidParams);
        assertEquals("Account does not exist.", enfe.getMessage());

    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }

}
