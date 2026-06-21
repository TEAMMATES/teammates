package teammates.logic.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.Logic;
import teammates.storage.entity.Account;
import teammates.storage.entity.AccountVerificationRequest;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.test.GroupNames;

/**
 * Tests for {@link DemoCourseLogic}.
 */
public class DemoCourseLogicTest extends BaseLogicTest {

    private final DemoCourseLogic demoCourseLogic = DemoCourseLogic.inst();
    private final Logic logic = Logic.inst();

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
    public void createDemoCourse_validInputs_marksDemoCourseCreatedInAvr() {
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

    @Test(groups = GroupNames.LOGIC)
    public void createDemoCourse_validInputs_courseHasCorrectProperties() {
        var account = given.account("account");
        var avr = given.accountVerificationRequest("avr",
                ar -> ar.account(account.alias()).email("instructor@example.com"));
        persistGivenData(given);

        inTransaction(() -> {
            Account avrAccount = getEntity(Account.class, account.id());
            demoCourseLogic.createDemoCourse(avr.id(), "America/New_York", avrAccount);
        });

        List<Instructor> instructors = inTransaction(() -> logic.getInstructorsByAccountId(account.id()));
        assertEquals(1, instructors.size());
        assertEquals("instructor@example.com", instructors.get(0).getEmail());

        Course course = inTransaction(() -> logic.getCourse(instructors.get(0).getCourseId()));
        assertEquals("Sample Course 101", course.getName());
        assertEquals("America/New_York", course.getTimeZone());
    }

    @Test(groups = GroupNames.LOGIC)
    public void createDemoCourse_invalidTimezone_courseUsesDefaultTimezone() {
        var account = given.account("account");
        var avr = given.accountVerificationRequest("avr", ar -> ar.account(account.alias()));
        persistGivenData(given);

        inTransaction(() -> {
            Account avrAccount = getEntity(Account.class, account.id());
            demoCourseLogic.createDemoCourse(avr.id(), "NOT_A_VALID_TIMEZONE", avrAccount);
        });

        List<Instructor> instructors = inTransaction(() -> logic.getInstructorsByAccountId(account.id()));
        Course course = inTransaction(() -> logic.getCourse(instructors.get(0).getCourseId()));
        assertEquals(Const.DEFAULT_TIME_ZONE, course.getTimeZone());
    }
}
