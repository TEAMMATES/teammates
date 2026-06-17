package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.GroupNames;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.output.MessageOutput;

/**
 * Tests for {@link CreateDemoCourseAction}.
 */
public class CreateDemoCourseActionTest extends BaseActionTest<CreateDemoCourseAction, MessageOutput> {

    @Test(groups = GroupNames.ACTION)
    public void createDemoCourseAction_validRequest_returnsDemoCourseSuccessfullyCreatedMessage() {
        var account = given.account("account");
        var avr = given.accountVerificationRequest("avr", ar -> ar.account(account.alias()));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID, avr.id().toString())
                .withParam(Const.ParamsNames.TIMEZONE, "UTC")
                .withAccountAuth(account.id());

        MessageOutput result = execute(request);

        assertEquals("Demo course successfully created", result.getMessage());
    }

    @Test(groups = GroupNames.ACTION)
    public void createDemoCourseAction_avrNotFound_throwsEntityNotFoundException() {
        var adminAccount = given.account("admin", a -> a.admin());
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID, UUID.randomUUID().toString())
                .withAdminAuth(adminAccount.id());

        assertActionThrows(EntityNotFoundException.class, request);
    }

    @Test(groups = GroupNames.ACTION)
    public void createDemoCourseAction_demoCourseAlreadyCreated_throwsInvalidOperationException() {
        var account = given.account("account");
        var avr = given.accountVerificationRequest("avr",
                ar -> ar.account(account.alias()).createdDemoCourseAt(Instant.now()));
        persistGivenData(given);

        RequestContext request = new RequestContext()
                .withParam(Const.ParamsNames.ACCOUNT_VERIFICATION_REQUEST_ID, avr.id().toString())
                .withAccountAuth(account.id());

        assertActionThrows(InvalidOperationException.class, request);
    }
}
