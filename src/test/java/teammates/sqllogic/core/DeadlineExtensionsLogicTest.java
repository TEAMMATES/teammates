package teammates.sqllogic.core;

import static org.mockito.Mockito.mock;
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
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.User;
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
        session.setId(UUID.randomUUID());
        Student student = getTypicalStudent();
        student.setId(UUID.randomUUID());
        Instant extendedDeadline = Instant.now().plusSeconds(86400);

        DeadlineExtension de = new DeadlineExtension(student, session, extendedDeadline);

        when(deDb.getDeadlineExtension(student.getId(), session.getId())).thenReturn(de);

        Instant result = deLogic.getDeadlineForUser(session, student);

        assertEquals(extendedDeadline, result);
    }

    @Test
    public void testGetDeadlineForUser_noExtension_returnsSessionEndTime() {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        session.setId(UUID.randomUUID());
        Instant sessionEndTime = session.getEndTime();
        Student student = getTypicalStudent();
        student.setId(UUID.randomUUID());

        when(deDb.getDeadlineExtension(student.getId(), session.getId())).thenReturn(null);

        Instant result = deLogic.getDeadlineForUser(session, student);

        assertEquals(sessionEndTime, result);
    }

    @Test
    public void testGetExtendedDeadlineForUser_hasExtension_returnsExtendedDeadline() {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        session.setId(UUID.randomUUID());
        Student student = getTypicalStudent();
        student.setId(UUID.randomUUID());
        Instant extendedDeadline = Instant.now().plusSeconds(86400);

        DeadlineExtension de = new DeadlineExtension(student, session, extendedDeadline);

        when(deDb.getDeadlineExtension(student.getId(), session.getId())).thenReturn(de);

        Instant result = deLogic.getExtendedDeadlineForUser(session, student);

        assertEquals(extendedDeadline, result);
    }

    @Test
    public void testGetExtendedDeadlineForUser_noExtension_returnsNull() {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        session.setId(UUID.randomUUID());
        Student student = getTypicalStudent();
        student.setId(UUID.randomUUID());

        when(deDb.getDeadlineExtension(student.getId(), session.getId())).thenReturn(null);

        Instant result = deLogic.getExtendedDeadlineForUser(session, student);

        assertNull(result);
    }

    @Test
    public void testGetDeadlineExtensionEntityForUser_extensionExists_returnsExtension() {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        session.setId(UUID.randomUUID());
        Student student = getTypicalStudent();
        student.setId(UUID.randomUUID());
        Instant extendedDeadline = Instant.now().plusSeconds(86400);

        DeadlineExtension de = new DeadlineExtension(student, session, extendedDeadline);

        when(deDb.getDeadlineExtension(student.getId(), session.getId())).thenReturn(de);

        DeadlineExtension result = deLogic.getDeadlineExtensionEntityForUser(session, student);

        assertEquals(de, result);
        assertEquals(student, result.getUser());
        assertEquals(session, result.getFeedbackSession());
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

        assertEquals(de, result);
        verify(deDb, times(1)).createDeadlineExtension(de);
    }

    @Test
    public void testDeleteDeadlineExtension_extensionExists_success() {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        Student student = getTypicalStudent();
        Instant extendedDeadline = Instant.now().plusSeconds(86400);

        DeadlineExtension de = new DeadlineExtension(student, session, extendedDeadline);

        deLogic.deleteDeadlineExtension(de);

        verify(deDb, times(1)).deleteDeadlineExtension(de);
    }

    @Test
    public void testUpdateDeadlineExtension_validUpdate_success()
            throws InvalidParametersException, EntityDoesNotExistException {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        Student student = getTypicalStudent();
        Instant extendedDeadline = Instant.now().plusSeconds(86400);

        DeadlineExtension de = new DeadlineExtension(student, session, extendedDeadline);

        when(deDb.updateDeadlineExtension(de)).thenReturn(de);

        DeadlineExtension result = deLogic.updateDeadlineExtension(de);

        assertEquals(de, result);
        verify(deDb, times(1)).updateDeadlineExtension(de);
    }

    @Test
    public void testGetDeadlineExtensionsPossiblyNeedingClosingSoonEmail_extensionsExist_success() {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        Student student = getTypicalStudent();
        Instant extendedDeadline = Instant.now().plusSeconds(3600); // Closing soon

        DeadlineExtension de = new DeadlineExtension(student, session, extendedDeadline);
        List<DeadlineExtension> extensions = List.of(de);

        when(deDb.getDeadlineExtensionsPossiblyNeedingClosingSoonEmail()).thenReturn(extensions);

        List<DeadlineExtension> result = deLogic.getDeadlineExtensionsPossiblyNeedingClosingSoonEmail();

        assertEquals(1, result.size());
        assertEquals(de, result.get(0));
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
        Student student = getTypicalStudent();
        student.setCourse(course);

        FeedbackSession session1 = getTypicalFeedbackSessionForCourse(course);
        FeedbackSession session2 = getTypicalFeedbackSessionForCourse(course);

        DeadlineExtension de1 = new DeadlineExtension(student, session1, Instant.now().plusSeconds(86400));
        DeadlineExtension de2 = new DeadlineExtension(student, session2, Instant.now().plusSeconds(86400));

        session1.setDeadlineExtensions(new ArrayList<>(List.of(de1)));
        session2.setDeadlineExtensions(new ArrayList<>(List.of(de2)));

        when(fsLogic.getFeedbackSessionsForCourse(course.getId())).thenReturn(List.of(session1, session2));

        deLogic.deleteDeadlineExtensionsForUser(student);

        verify(fsLogic, times(1)).getFeedbackSessionsForCourse(course.getId());
        verify(deDb, times(1)).deleteDeadlineExtension(de1);
        verify(deDb, times(1)).deleteDeadlineExtension(de2);
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
        verify(deDb, times(0)).deleteDeadlineExtension(org.mockito.ArgumentMatchers.any());
    }

    @Test
    public void testCreateDeadlineExtension_forInstructor_success()
            throws InvalidParametersException, EntityAlreadyExistsException {
        Course course = getTypicalCourse();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(course);
        User instructor = getTypicalInstructor();
        Instant extendedDeadline = Instant.now().plusSeconds(86400);

        DeadlineExtension de = new DeadlineExtension(instructor, session, extendedDeadline);

        when(deDb.createDeadlineExtension(de)).thenReturn(de);

        DeadlineExtension result = deLogic.createDeadlineExtension(de);

        assertEquals(de, result);
        assertEquals(instructor, result.getUser());
    }
}
