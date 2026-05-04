package teammates.storage.sqlapi;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;

import java.time.Instant;
import java.util.UUID;

import org.mockito.MockedStatic;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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
    public void testCreateDeadlineExtension_success() {
        DeadlineExtension de = getValidDeadlineExtension();

        deadlineExtensionsDb.createDeadlineExtension(de);

        mockHibernateUtil.verify(() -> HibernateUtil.persist(de), times(1));
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
    public void testDeleteDeadlineExtension_success() {
        DeadlineExtension de = getValidDeadlineExtension();

        deadlineExtensionsDb.deleteDeadlineExtension(de);

        mockHibernateUtil.verify(() -> HibernateUtil.remove(de), times(1));
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
}
