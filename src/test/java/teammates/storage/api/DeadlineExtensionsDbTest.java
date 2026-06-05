package teammates.storage.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import teammates.storage.entity.DeadlineExtension;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Student;
import teammates.test.GroupNames;

/**
 * Tests for {@link DeadlineExtensionsDb}.
 */
public class DeadlineExtensionsDbTest extends BaseDbTestcase {
    private final DeadlineExtensionsDb deadlineExtensionsDb = DeadlineExtensionsDb.inst();

    @Test(groups = GroupNames.DB)
    public void getDeadlineExtension_deadlineExtensionExists_returnsDeadlineExtension() {
        UUID deadlineExtensionId = given.deadlineExtension("deadline-extension");
        persistGivenData(given);

        DeadlineExtension actual = inTransaction(() -> deadlineExtensionsDb.getDeadlineExtension(deadlineExtensionId));

        assertNotNull(actual);
        assertEquals(deadlineExtensionId, actual.getId());
    }

    @Test(groups = GroupNames.DB)
    public void getDeadlineExtension_deadlineExtensionDoesNotExist_returnsNull() {
        given.deadlineExtension("different-deadline-extension");
        persistGivenData(given);

        DeadlineExtension actual = inTransaction(
                () -> deadlineExtensionsDb.getDeadlineExtension(given.uuid("non-existent-deadline-extension")));

        assertNull(actual);
    }

    @Test(groups = GroupNames.DB)
    public void getDeadlineExtensionByUserAndSession_deadlineExtensionExists_returnsDeadlineExtension() {
        UUID studentId = given.student("student");
        UUID feedbackSessionId = given.feedbackSession("feedback-session");
        UUID deadlineExtensionId = given.deadlineExtension("deadline-extension",
                de -> de.student("student").feedbackSession("feedback-session"));
        given.deadlineExtension("another-student-deadline-extension",
                de -> de.student("another-student").feedbackSession("feedback-session"));
        persistGivenData(given);

        DeadlineExtension actual = inTransaction(() -> deadlineExtensionsDb.getDeadlineExtension(
                studentId, feedbackSessionId));

        assertNotNull(actual);
        assertEquals(deadlineExtensionId, actual.getId());
    }

    @Test(groups = GroupNames.DB)
    public void persistDeadlineExtension_deadlineExtensionIsNew_deadlineExtensionIsPersisted() {
        UUID feedbackSessionId = given.feedbackSession("feedback-session");
        UUID studentId = given.student("student");
        persistGivenData(given);
        UUID deadlineExtensionId = given.uuid("deadline-extension");

        DeadlineExtension actual = inTransaction(() -> {
            FeedbackSession feedbackSession = getEntity(FeedbackSession.class, feedbackSessionId);
            Student student = getEntity(Student.class, studentId);
            DeadlineExtension deadlineExtension = buildDefaultDeadlineExtension(
                    feedbackSession, student, deadlineExtensionId);
            return deadlineExtensionsDb.persistDeadlineExtension(deadlineExtension);
        });

        assertEquals(deadlineExtensionId, actual.getId());
        verifyPresentInDatabase(DeadlineExtension.class, deadlineExtensionId);
    }

    @Test(groups = GroupNames.DB)
    public void removeDeadlineExtension_deadlineExtensionExists_deadlineExtensionIsRemoved() {
        UUID deadlineExtensionId = given.deadlineExtension("deadline-extension");
        persistGivenData(given);

        inTransaction(() -> deadlineExtensionsDb.removeDeadlineExtension(
                deadlineExtensionsDb.getDeadlineExtension(deadlineExtensionId)));

        verifyAbsentInDatabase(DeadlineExtension.class, deadlineExtensionId);
    }

    @Test(groups = GroupNames.DB)
    public void getDeadlineExtensionsPossiblyNeedingClosingSoonEmail_extensionsExist_returnsEligibleExtensions() {
        UUID deadlineExtensionId = given.deadlineExtension("deadline-extension",
                de -> de.closingSoon().closingSoonEmailSent(false));
        given.deadlineExtension("sent-deadline-extension",
                de -> de.closingSoon().closingSoonEmailSent(true));
        persistGivenData(given);

        List<DeadlineExtension> actual = inTransaction(
                deadlineExtensionsDb::getDeadlineExtensionsPossiblyNeedingClosingSoonEmail);

        assertEquals(Set.of(deadlineExtensionId),
                actual.stream().map(DeadlineExtension::getId).collect(Collectors.toSet()));
    }

    private static DeadlineExtension buildDefaultDeadlineExtension(
            FeedbackSession feedbackSession, Student student, UUID deadlineExtensionId) {
        assertNotNull(feedbackSession);
        assertNotNull(student);
        DeadlineExtension deadlineExtension = new DeadlineExtension(
                student, feedbackSession.getEndTime().plus(1, ChronoUnit.HOURS));
        deadlineExtension.setId(deadlineExtensionId);
        feedbackSession.addDeadlineExtension(deadlineExtension);
        return deadlineExtension;
    }
}
