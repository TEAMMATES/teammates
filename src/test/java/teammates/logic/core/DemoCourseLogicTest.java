package teammates.logic.core;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.entity.Account;
import teammates.storage.entity.AccountVerificationRequest;
import teammates.test.GroupNames;

/**
 * Tests for {@link DemoCourseLogic}.
 */
public class DemoCourseLogicTest extends BaseLogicTestcase {

    private final DemoCourseLogic demoCourseLogic = DemoCourseLogic.inst();

    @Test(groups = GroupNames.LOGIC)
    public void createDemoCourse_avrNotFound_throwsEntityDoesNotExistException() {
        UUID nonExistentId = UUID.randomUUID();

        assertThrowsInTransaction(EntityDoesNotExistException.class,
                () -> demoCourseLogic.createDemoCourse(nonExistentId, "UTC", null));
    }

    @Test(groups = GroupNames.LOGIC)
    public void createDemoCourse_demoCourseAlreadyCreated_throwsEntityAlreadyExistsException() {
        var account = given.account("account");
        var avr = given.accountVerificationRequest("avr",
                ar -> ar.account(account.alias()).createdDemoCourseAt(Instant.now()));
        persistGivenData(given);

        assertThrowsInTransaction(EntityAlreadyExistsException.class, () -> {
            Account avrAccount = getEntity(Account.class, account.id());
            demoCourseLogic.createDemoCourse(avr.id(), "UTC", avrAccount);
        });
    }

    @Test(groups = GroupNames.LOGIC)
    public void createDemoCourse_validInputs_marksDemoCourseCreatedInAvr()
            throws EntityDoesNotExistException, EntityAlreadyExistsException, InvalidParametersException {
        var account = given.account("account");
        var avr = given.accountVerificationRequest("avr", ar -> ar.account(account.alias()));
        persistGivenData(given);

        inTransaction(() -> {
            Account avrAccount = getEntity(Account.class, account.id());
            demoCourseLogic.createDemoCourse(avr.id(), "UTC", avrAccount);
        });

        AccountVerificationRequest updatedAvr = getEntityInTransaction(AccountVerificationRequest.class, avr.id());
        assertNotNull(updatedAvr.getCreatedDemoCourseAt());
    }
}
