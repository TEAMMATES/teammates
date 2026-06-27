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
        var deadlineExtension = given.deadlineExtension("deadline-extension");
        persistGivenData(given);

        DeadlineExtension actual = inTransaction(() -> deadlineExtensionsDb.getDeadlineExtension(deadlineExtension.id()));

        assertNotNull(actual);
        assertEquals(deadlineExtension.id(), actual.getId());
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
    public void getDeadlineExtensionByUserAndSession_studentDeadlineExtensionExists_returnsDeadlineExtension() {
        var student = given.student("student");
        var feedbackSession = given.feedbackSession("feedback-session");
        var deadlineExtension = given.deadlineExtension("deadline-extension",
                de -> de.student(student.alias()).feedbackSession(feedbackSession.alias()));
        given.deadlineExtension("another-student-deadline-extension",
                de -> de.student("another-student").feedbackSession(feedbackSession.alias()));
        persistGivenData(given);

        DeadlineExtension actual = inTransaction(() -> deadlineExtensionsDb.getDeadlineExtension(
                student.id(), feedbackSession.id()));

        assertNotNull(actual);
        assertEquals(deadlineExtension.id(), actual.getId());
    }

    @Test(groups = GroupNames.DB)
    public void getDeadlineExtensionByUserAndSession_instructorDeadlineExtensionExists_returnsDeadlineExtension() {
        var instructor = given.instructor("instructor");
        var feedbackSession = given.feedbackSession("feedback-session");
        var deadlineExtension = given.deadlineExtension("deadline-extension",
                de -> de.instructor(instructor.alias()).feedbackSession(feedbackSession.alias()));
        given.deadlineExtension("another-instructor-deadline-extension",
                de -> de.instructor("another-instructor").feedbackSession(feedbackSession.alias()));
        persistGivenData(given);

        DeadlineExtension actual = inTransaction(() -> deadlineExtensionsDb.getDeadlineExtension(
                instructor.id(), feedbackSession.id()));

        assertNotNull(actual);
        assertEquals(deadlineExtension.id(), actual.getId());
    }

    @Test(groups = GroupNames.DB)
    public void getDeadlineExtensionsForUsersAndSessions_matchingExtensionsExist_returnsOnlyMatchingExtensions() {
        var student = given.student("student");
        var anotherStudent = given.student("another-student");
        var feedbackSession = given.feedbackSession("feedback-session");
        var anotherFeedbackSession = given.feedbackSession("another-feedback-session");
        var matchingExtension = given.deadlineExtension("matching-extension",
                de -> de.student(student.alias()).feedbackSession(feedbackSession.alias()));
        var anotherMatchingExtension = given.deadlineExtension("another-matching-extension",
                de -> de.student(anotherStudent.alias()).feedbackSession(anotherFeedbackSession.alias()));
        given.deadlineExtension("unrequested-user-extension",
                de -> de.student("unrequested-student").feedbackSession(feedbackSession.alias()));
        given.deadlineExtension("unrequested-session-extension",
                de -> de.student(student.alias()).feedbackSession("unrequested-feedback-session"));
        persistGivenData(given);

        List<DeadlineExtension> actual = inTransaction(
                () -> deadlineExtensionsDb.getDeadlineExtensionsForUsersAndSessions(
                        List.of(student.id(), anotherStudent.id()),
                        List.of(feedbackSession.id(), anotherFeedbackSession.id())));

        assertEquals(Set.of(matchingExtension.id(), anotherMatchingExtension.id()),
                actual.stream().map(DeadlineExtension::getId).collect(Collectors.toSet()));
    }

    @Test(groups = GroupNames.DB)
    public void persistDeadlineExtension_deadlineExtensionIsNew_deadlineExtensionIsPersisted() {
        var feedbackSessionRef = given.feedbackSession("feedback-session");
        var studentRef = given.student("student");
        persistGivenData(given);
        var deadlineExtensionId = given.uuid("deadline-extension");

        DeadlineExtension actual = inTransaction(() -> {
            FeedbackSession feedbackSession = getEntity(FeedbackSession.class, feedbackSessionRef.id());
            Student student = getEntity(Student.class, studentRef.id());
            DeadlineExtension deadlineExtension = buildDefaultDeadlineExtension(
                    feedbackSession, student, deadlineExtensionId);
            return deadlineExtensionsDb.persistDeadlineExtension(deadlineExtension);
        });

        assertEquals(deadlineExtensionId, actual.getId());
        verifyPresentInDatabase(DeadlineExtension.class, deadlineExtensionId);
    }

    @Test(groups = GroupNames.DB)
    public void removeDeadlineExtension_deadlineExtensionExists_deadlineExtensionIsRemoved() {
        var deadlineExtension = given.deadlineExtension("deadline-extension");
        persistGivenData(given);

        inTransaction(() -> deadlineExtensionsDb.removeDeadlineExtension(
                deadlineExtensionsDb.getDeadlineExtension(deadlineExtension.id())));

        verifyAbsentInDatabase(DeadlineExtension.class, deadlineExtension.id());
    }

    @Test(groups = GroupNames.DB)
    public void getDeadlineExtensionsPossiblyNeedingClosingSoonEmail_extensionsExist_returnsEligibleExtensions() {
        var deadlineExtension = given.deadlineExtension("deadline-extension",
                de -> de.closingSoon().closingSoonEmailSent(false));
        given.deadlineExtension("sent-deadline-extension",
                de -> de.closingSoon().closingSoonEmailSent(true));
        persistGivenData(given);

        List<DeadlineExtension> actual = inTransaction(
                deadlineExtensionsDb::getDeadlineExtensionsPossiblyNeedingClosingSoonEmail);

        assertEquals(Set.of(deadlineExtension.id()),
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
