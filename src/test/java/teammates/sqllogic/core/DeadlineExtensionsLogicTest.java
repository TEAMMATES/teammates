package teammates.sqllogic.core;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.sqlapi.DeadlineExtensionsDb;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.DeadlineExtension;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link DeadlineExtensionsLogic}.
 */
public class DeadlineExtensionsLogicTest extends BaseTestCase {

    private final DeadlineExtensionsLogic deLogic = DeadlineExtensionsLogic.inst();

    private DeadlineExtensionsDb deDb;
    private FeedbackSessionsLogic fsLogic;

    @BeforeMethod
    public void setUpMethod() {
        deDb = mock(DeadlineExtensionsDb.class);
        fsLogic = mock(FeedbackSessionsLogic.class);
        deLogic.initLogicDependencies(deDb, fsLogic);
    }

    @Test
    public void testGetDeadlineForUser_hasExtension_returnsExtendedDeadline() {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        UUID sessionId = UUID.randomUUID();
        session.setId(sessionId);
        Instant sessionEndTime = session.getEndTime();
        Student student = getTypicalStudent();
        UUID studentId = UUID.randomUUID();
        student.setId(studentId);
        Instant extendedDeadline = sessionEndTime.plusSeconds(86400);
        assertTrue("Extended deadline should be after session end time",
                extendedDeadline.isAfter(sessionEndTime));

        DeadlineExtension de = new DeadlineExtension(student, session, extendedDeadline);

        when(deDb.getDeadlineExtension(studentId, sessionId)).thenReturn(de);

        Instant result = deLogic.getDeadlineForUser(session, student);

        assertNotNull(result);
        assertEquals(extendedDeadline, result);
        assertTrue(result.isAfter(sessionEndTime));
        verify(deDb, times(1)).getDeadlineExtension(studentId, sessionId);
    }

    @Test
    public void testGetDeadlineForUser_noExtension_returnsSessionEndTime() {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        UUID sessionId = UUID.randomUUID();
        session.setId(sessionId);
        Instant sessionEndTime = session.getEndTime();
        Student student = getTypicalStudent();
        UUID studentId = UUID.randomUUID();
        student.setId(studentId);

        when(deDb.getDeadlineExtension(studentId, sessionId)).thenReturn(null);

        Instant result = deLogic.getDeadlineForUser(session, student);

        assertNotNull(result);
        assertEquals(sessionEndTime, result);
        verify(deDb, times(1)).getDeadlineExtension(studentId, sessionId);
    }

    @Test
    public void testGetDeadlineForUser_extensionBeforeSessionEnd_returnsExtendedDeadline() {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        UUID sessionId = UUID.randomUUID();
        session.setId(sessionId);
        Student student = getTypicalStudent();
        UUID studentId = UUID.randomUUID();
        student.setId(studentId);
        // Extension that is before session end (shouldn't happen in practice but test edge case)
        Instant extendedDeadline = session.getEndTime().minusSeconds(3600);

        DeadlineExtension de = new DeadlineExtension(student, session, extendedDeadline);

        when(deDb.getDeadlineExtension(studentId, sessionId)).thenReturn(de);

        Instant result = deLogic.getDeadlineForUser(session, student);

        assertEquals(extendedDeadline, result);
        assertTrue(result.isBefore(session.getEndTime()));
    }

    @Test
    public void testGetExtendedDeadlineForUser_hasExtension_returnsExtendedDeadline() {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        UUID sessionId = UUID.randomUUID();
        session.setId(sessionId);
        Student student = getTypicalStudent();
        UUID studentId = UUID.randomUUID();
        student.setId(studentId);
        Instant extendedDeadline = Instant.now().plusSeconds(86400);

        DeadlineExtension de = new DeadlineExtension(student, session, extendedDeadline);

        when(deDb.getDeadlineExtension(studentId, sessionId)).thenReturn(de);

        Instant result = deLogic.getExtendedDeadlineForUser(session, student);

        assertNotNull(result);
        assertEquals(extendedDeadline, result);
        verify(deDb, times(1)).getDeadlineExtension(studentId, sessionId);
    }

    @Test
    public void testGetExtendedDeadlineForUser_noExtension_returnsNull() {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        UUID sessionId = UUID.randomUUID();
        session.setId(sessionId);
        Student student = getTypicalStudent();
        UUID studentId = UUID.randomUUID();
        student.setId(studentId);

        when(deDb.getDeadlineExtension(studentId, sessionId)).thenReturn(null);

        Instant result = deLogic.getExtendedDeadlineForUser(session, student);

        assertNull(result);
        verify(deDb, times(1)).getDeadlineExtension(studentId, sessionId);
    }

    @Test
    public void testGetDeadlineExtensionEntityForUser_extensionExists_returnsExtension() {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        UUID sessionId = UUID.randomUUID();
        session.setId(sessionId);
        Student student = getTypicalStudent();
        UUID studentId = UUID.randomUUID();
        student.setId(studentId);
        Instant extendedDeadline = Instant.now().plusSeconds(86400);

        DeadlineExtension de = new DeadlineExtension(student, session, extendedDeadline);
        de.setId(UUID.randomUUID());

        when(deDb.getDeadlineExtension(studentId, sessionId)).thenReturn(de);

        DeadlineExtension result = deLogic.getDeadlineExtensionEntityForUser(session, student);

        assertNotNull(result);
        assertEquals(de, result);
        assertEquals(extendedDeadline, result.getEndTime());
        assertTrue(result.getUser() instanceof Student);
        verify(deDb, times(1)).getDeadlineExtension(studentId, sessionId);
    }

    @Test
    public void testGetDeadlineExtensionEntityForUser_noExtension_returnsNull() {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        session.setId(UUID.randomUUID());
        Student student = getTypicalStudent();
        student.setId(UUID.randomUUID());

        when(deDb.getDeadlineExtension(student.getId(), session.getId())).thenReturn(null);

        DeadlineExtension result = deLogic.getDeadlineExtensionEntityForUser(session, student);

        assertNull(result);
    }

    @Test
    public void testCreateDeadlineExtension_validExtension_success()
            throws InvalidParametersException, EntityAlreadyExistsException {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        Student student = getTypicalStudent();
        Instant extendedDeadline = Instant.now().plusSeconds(86400);

        DeadlineExtension de = new DeadlineExtension(student, session, extendedDeadline);

        when(deDb.createDeadlineExtension(de)).thenReturn(de);

        DeadlineExtension result = deLogic.createDeadlineExtension(de);

        assertNotNull(result);
        assertEquals(de, result);
        assertEquals(extendedDeadline, result.getEndTime());
        assertTrue(result.getUser() instanceof Student);
        verify(deDb, times(1)).createDeadlineExtension(de);
    }

    @Test
    public void testCreateDeadlineExtension_nullExtension_throwsException()
            throws InvalidParametersException, EntityAlreadyExistsException {
        assertThrows(AssertionError.class, () -> deLogic.createDeadlineExtension(null));
        verify(deDb, never()).createDeadlineExtension(any());
    }

    @Test
    public void testDeleteDeadlineExtension_extensionExists_success() {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        Student student = getTypicalStudent();
        Instant extendedDeadline = Instant.now().plusSeconds(86400);

        DeadlineExtension de = new DeadlineExtension(student, session, extendedDeadline);
        de.setId(UUID.randomUUID());

        deLogic.deleteDeadlineExtension(de);

        verify(deDb, times(1)).deleteDeadlineExtension(de);
    }

    @Test
    public void testUpdateDeadlineExtension_validUpdate_success()
            throws InvalidParametersException, EntityDoesNotExistException {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        Student student = getTypicalStudent();
        Instant originalDeadline = Instant.now().plusSeconds(86400);
        Instant updatedDeadline = Instant.now().plusSeconds(172800); // 2 days later

        DeadlineExtension de = new DeadlineExtension(student, session, originalDeadline);
        de.setId(UUID.randomUUID());
        de.setEndTime(updatedDeadline);

        when(deDb.updateDeadlineExtension(de)).thenReturn(de);

        DeadlineExtension result = deLogic.updateDeadlineExtension(de);

        assertNotNull(result);
        assertEquals(de, result);
        assertEquals(updatedDeadline, result.getEndTime());
        verify(deDb, times(1)).updateDeadlineExtension(de);
    }

    @Test
    public void testGetDeadlineExtensionsPossiblyNeedingClosingSoonEmail_extensionsExist_success() {
        Course course = getTypicalCourse();
        FeedbackSession session1 = getTypicalFeedbackSessionForCourse(course);
        FeedbackSession session2 = getTypicalFeedbackSessionForCourse(course);
        Student student1 = getTypicalStudent();
        Student student2 = getTypicalStudent();
        Instant extendedDeadline1 = Instant.now().plusSeconds(3600); // Closing soon
        Instant extendedDeadline2 = Instant.now().plusSeconds(7200); // Closing soon

        DeadlineExtension de1 = new DeadlineExtension(student1, session1, extendedDeadline1);
        DeadlineExtension de2 = new DeadlineExtension(student2, session2, extendedDeadline2);
        List<DeadlineExtension> extensions = List.of(de1, de2);

        when(deDb.getDeadlineExtensionsPossiblyNeedingClosingSoonEmail()).thenReturn(extensions);

        List<DeadlineExtension> result = deLogic.getDeadlineExtensionsPossiblyNeedingClosingSoonEmail();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(de1, result.get(0));
        assertEquals(de2, result.get(1));
        verify(deDb, times(1)).getDeadlineExtensionsPossiblyNeedingClosingSoonEmail();
    }

    @Test
    public void testGetDeadlineExtensionsPossiblyNeedingClosingSoonEmail_noExtensions_returnsEmptyList() {
        when(deDb.getDeadlineExtensionsPossiblyNeedingClosingSoonEmail()).thenReturn(new ArrayList<>());

        List<DeadlineExtension> result = deLogic.getDeadlineExtensionsPossiblyNeedingClosingSoonEmail();

        assertTrue(result.isEmpty());
    }

    @Test
    public void testDeleteDeadlineExtensionsForUser_userHasExtensions_deletesExtensions() {
        Course course = getTypicalCourse();
        String courseId = course.getId();
        Student student = getTypicalStudent();
        student.setCourse(course);

        FeedbackSession session1 = getTypicalFeedbackSessionForCourse(course);
        FeedbackSession session2 = getTypicalFeedbackSessionForCourse(course);

        DeadlineExtension de1 = new DeadlineExtension(student, session1, Instant.now().plusSeconds(86400));
        DeadlineExtension de2 = new DeadlineExtension(student, session2, Instant.now().plusSeconds(86400));
        // Add extension for different user that should not be deleted
        Student otherStudent = getTypicalStudent();
        DeadlineExtension de3 = new DeadlineExtension(otherStudent, session1, Instant.now().plusSeconds(86400));

        session1.setDeadlineExtensions(new ArrayList<>(List.of(de1, de3)));
        session2.setDeadlineExtensions(new ArrayList<>(List.of(de2)));

        when(fsLogic.getFeedbackSessionsForCourse(courseId)).thenReturn(List.of(session1, session2));

        deLogic.deleteDeadlineExtensionsForUser(student);

        verify(fsLogic, times(1)).getFeedbackSessionsForCourse(courseId);
        // Only extensions for the specified user should be deleted
        verify(deDb, times(1)).deleteDeadlineExtension(de1);
        verify(deDb, times(1)).deleteDeadlineExtension(de2);
        verify(deDb, never()).deleteDeadlineExtension(de3);
        verify(deDb, times(2)).deleteDeadlineExtension(any(DeadlineExtension.class));
    }

    @Test
    public void testDeleteDeadlineExtensionsForUser_userHasNoExtensions_noDeletes() {
        Course course = getTypicalCourse();
        Student student = getTypicalStudent();
        student.setCourse(course);

        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        session.setDeadlineExtensions(new ArrayList<>());

        when(fsLogic.getFeedbackSessionsForCourse(course.getId())).thenReturn(List.of(session));

        deLogic.deleteDeadlineExtensionsForUser(student);

        verify(fsLogic, times(1)).getFeedbackSessionsForCourse(course.getId());
        verify(deDb, times(0)).deleteDeadlineExtension(any());
    }

    @Test
    public void testCreateDeadlineExtension_forInstructor_success()
            throws InvalidParametersException, EntityAlreadyExistsException {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        Instructor instructor = getTypicalInstructor();
        Instant extendedDeadline = Instant.now().plusSeconds(86400);

        DeadlineExtension de = new DeadlineExtension(instructor, session, extendedDeadline);

        when(deDb.createDeadlineExtension(de)).thenReturn(de);

        DeadlineExtension result = deLogic.createDeadlineExtension(de);

        assertNotNull(result);
        assertEquals(de, result);
        assertTrue(result.getUser() instanceof Instructor);
        assertEquals(extendedDeadline, result.getEndTime());
        verify(deDb, times(1)).createDeadlineExtension(de);
    }

    @Test
    public void testGetDeadlineForUser_differentUsers_differentDeadlines() {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        UUID sessionId = UUID.randomUUID();
        session.setId(sessionId);

        Student student1 = getTypicalStudent();
        UUID student1Id = UUID.randomUUID();
        student1.setId(student1Id);
        Instant extendedDeadline1 = Instant.now().plusSeconds(86400);
        DeadlineExtension de1 = new DeadlineExtension(student1, session, extendedDeadline1);

        Student student2 = getTypicalStudent();
        UUID student2Id = UUID.randomUUID();
        student2.setId(student2Id);
        Instant extendedDeadline2 = Instant.now().plusSeconds(172800);
        DeadlineExtension de2 = new DeadlineExtension(student2, session, extendedDeadline2);

        when(deDb.getDeadlineExtension(student1Id, sessionId)).thenReturn(de1);
        when(deDb.getDeadlineExtension(student2Id, sessionId)).thenReturn(de2);

        Instant result1 = deLogic.getDeadlineForUser(session, student1);
        Instant result2 = deLogic.getDeadlineForUser(session, student2);

        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(extendedDeadline1, result1);
        assertEquals(extendedDeadline2, result2);
        assertTrue(result2.isAfter(result1));
    }
}
