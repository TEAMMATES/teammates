package teammates.storage.sqlapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static teammates.common.util.Const.ERROR_CREATE_ENTITY_ALREADY_EXISTS;
import static teammates.common.util.Const.ERROR_UPDATE_NON_EXISTENT;

import java.time.Instant;
import java.util.UUID;

import org.mockito.MockedStatic;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.DeadlineExtension;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Student;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link DeadlineExtensionsDb}.
 */
public class DeadlineExtensionsDbTest extends BaseTestCase {

    private DeadlineExtensionsDb deadlineExtensionsDb;
    private MockedStatic<HibernateUtil> mockHibernateUtil;

    @BeforeMethod
    public void setUpMethod() {
        mockHibernateUtil = mockStatic(HibernateUtil.class);
        deadlineExtensionsDb = spy(DeadlineExtensionsDb.class);
    }

    @AfterMethod
    public void teardownMethod() {
        mockHibernateUtil.close();
    }

    @Test
    public void testCreateDeadlineExtension_success()
            throws InvalidParametersException, EntityAlreadyExistsException {
        DeadlineExtension de = getValidDeadlineExtension();

        doReturn(null).when(deadlineExtensionsDb).getDeadlineExtension(de.getId());

        deadlineExtensionsDb.createDeadlineExtension(de);

        mockHibernateUtil.verify(() -> HibernateUtil.persist(de), times(1));
    }

    @Test
    public void testCreateDeadlineExtension_deadlineExtensionAlreadyExists_throwsEntityAlreadyExistsException() {
        DeadlineExtension de = getValidDeadlineExtension();

        doReturn(de).when(deadlineExtensionsDb).getDeadlineExtension(de.getId());

        EntityAlreadyExistsException eaee = assertThrows(EntityAlreadyExistsException.class,
                () -> deadlineExtensionsDb.createDeadlineExtension(de));

        assertEquals(String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, de.toString()), eaee.getMessage());
        mockHibernateUtil.verify(() -> HibernateUtil.persist(de), never());
    }

    @Test
    public void testCreateDeadlineExtension_invalidDeadlineExtension_throwsInvalidParametersException() {
        DeadlineExtension de = getInvalidDeadlineExtension();

        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> deadlineExtensionsDb.createDeadlineExtension(de));

        assertTrue(ipe.getMessage().contains("extended deadlines"));
        mockHibernateUtil.verify(() -> HibernateUtil.persist(de), never());
    }

    @Test
    public void testGetDeadlineExtension_success() {
        DeadlineExtension de = getValidDeadlineExtension();
        UUID id = de.getId();

        mockHibernateUtil.when(() -> HibernateUtil.get(DeadlineExtension.class, id)).thenReturn(de);

        DeadlineExtension result = deadlineExtensionsDb.getDeadlineExtension(id);

        mockHibernateUtil.verify(() -> HibernateUtil.get(DeadlineExtension.class, id), times(1));
        assertEquals(de, result);
    }

    @Test
    public void testGetDeadlineExtension_deadlineExtensionDoesNotExist_returnsNull() {
        UUID id = UUID.randomUUID();

        mockHibernateUtil.when(() -> HibernateUtil.get(DeadlineExtension.class, id)).thenReturn(null);

        DeadlineExtension result = deadlineExtensionsDb.getDeadlineExtension(id);

        mockHibernateUtil.verify(() -> HibernateUtil.get(DeadlineExtension.class, id), times(1));
        assertNull(result);
    }

    @Test
    public void testUpdateDeadlineExtension_success()
            throws InvalidParametersException, EntityDoesNotExistException {
        DeadlineExtension de = getValidDeadlineExtension();

        doReturn(de).when(deadlineExtensionsDb).getDeadlineExtension(de.getId());
        mockHibernateUtil.when(() -> HibernateUtil.merge(de)).thenReturn(de);

        DeadlineExtension result = deadlineExtensionsDb.updateDeadlineExtension(de);

        mockHibernateUtil.verify(() -> HibernateUtil.merge(de), times(1));
        assertEquals(de, result);
    }

    @Test
    public void testUpdateDeadlineExtension_invalidDeadlineExtension_throwsInvalidParametersException() {
        DeadlineExtension de = getInvalidDeadlineExtension();

        InvalidParametersException ipe = assertThrows(InvalidParametersException.class,
                () -> deadlineExtensionsDb.updateDeadlineExtension(de));

        assertTrue(ipe.getMessage().contains("extended deadlines"));
        mockHibernateUtil.verify(() -> HibernateUtil.merge(de), never());
    }

    @Test
    public void testUpdateDeadlineExtension_deadlineExtensionDoesNotExist_throwsEntityDoesNotExistException() {
        DeadlineExtension de = getValidDeadlineExtension();

        doReturn(null).when(deadlineExtensionsDb).getDeadlineExtension(de.getId());

        EntityDoesNotExistException ednee = assertThrows(EntityDoesNotExistException.class,
                () -> deadlineExtensionsDb.updateDeadlineExtension(de));

        assertEquals(ERROR_UPDATE_NON_EXISTENT, ednee.getMessage());
        mockHibernateUtil.verify(() -> HibernateUtil.merge(de), never());
    }

    @Test
    public void testDeleteDeadlineExtension_success() {
        DeadlineExtension de = getValidDeadlineExtension();

        deadlineExtensionsDb.deleteDeadlineExtension(de);

        mockHibernateUtil.verify(() -> HibernateUtil.remove(de), times(1));
    }

    @Test
    public void testDeleteDeadlineExtension_nullDeadlineExtension_nothingHappens() {
        deadlineExtensionsDb.deleteDeadlineExtension(null);

        mockHibernateUtil.verify(() -> HibernateUtil.remove(any()), never());
    }

    /**
     * Creates a valid DeadlineExtension with endTime after the feedback session's end time.
     */
    private DeadlineExtension getValidDeadlineExtension() {
        Student student = getTypicalStudent();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(getTypicalCourse());

        // Set extension endTime to be AFTER the session end time
        Instant extensionEndTime = Instant.now().plusSeconds(8 * 24 * 60 * 60);
        DeadlineExtension de = new DeadlineExtension(student, session, extensionEndTime);
        session.getDeadlineExtensions().add(de);

        return de;
    }

    /**
     * Creates an invalid DeadlineExtension with endTime before or equal to the feedback session's end time.
     */
    private DeadlineExtension getInvalidDeadlineExtension() {
        Student student = getTypicalStudent();
        FeedbackSession session = getTypicalFeedbackSessionForCourse(getTypicalCourse());

        // Set extension endTime to be BEFORE the session end time (violates validation rule)
        Instant extensionEndTime = Instant.now().plusSeconds(6 * 24 * 60 * 60);
        DeadlineExtension de = new DeadlineExtension(student, session, extensionEndTime);
        session.getDeadlineExtensions().add(de);

        return de;
    }
}
