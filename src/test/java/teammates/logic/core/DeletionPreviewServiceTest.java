package teammates.logic.core;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DeletionPreviewData;
import teammates.test.BaseTestCase;

/**
 * SUT: {@link DeletionPreviewService}.
 */
public class DeletionPreviewServiceTest extends BaseTestCase {

    private DeletionPreviewService deletionPreviewService = DeletionPreviewService.inst();

    @BeforeMethod
    public void setUp() {
        deletionPreviewService.initLogicDependencies();
    }

    @Test
    public void testPreviewCourseDeletion_nonExistentCourse_shouldReturnWarning() {
        String courseId = "non-existent-course";

        DeletionPreviewData preview = deletionPreviewService.previewCourseDeletion(courseId);

        assertEquals(DeletionPreviewData.EntityType.COURSE, preview.getEntityType());
        assertEquals(courseId, preview.getEntityIdentifier());
        assertTrue(preview.hasWarnings());
        assertTrue(preview.getWarnings().get(0).contains("Course does not exist"));
        assertEquals(0, preview.getTotalEntitiesAffected());
    }

    @Test
    public void testPreviewStudentDeletion_nonExistentStudent_shouldReturnWarning() {
        String courseId = "test-course";
        String studentEmail = "nonexistent@example.com";

        DeletionPreviewData preview = deletionPreviewService.previewStudentDeletion(courseId, studentEmail);

        assertEquals(DeletionPreviewData.EntityType.STUDENT, preview.getEntityType());
        assertTrue(preview.hasWarnings());
        assertTrue(preview.getWarnings().get(0).contains("Student does not exist"));
    }

    @Test
    public void testPreviewInstructorDeletion_nonExistentInstructor_shouldReturnWarning() {
        String courseId = "test-course";
        String instructorEmail = "nonexistent@example.com";

        DeletionPreviewData preview = deletionPreviewService.previewInstructorDeletion(courseId, instructorEmail);

        assertEquals(DeletionPreviewData.EntityType.INSTRUCTOR, preview.getEntityType());
        assertTrue(preview.hasWarnings());
        assertTrue(preview.getWarnings().get(0).contains("Instructor does not exist"));
    }

    @Test
    public void testPreviewAccountDeletion_nonExistentAccount_shouldReturnWarning() {
        String googleId = "nonexistent-google-id";

        DeletionPreviewData preview = deletionPreviewService.previewAccountDeletion(googleId);

        assertEquals(DeletionPreviewData.EntityType.ACCOUNT, preview.getEntityType());
        assertTrue(preview.hasWarnings());
        assertTrue(preview.getWarnings().get(0).contains("Account does not exist"));
    }

    @Test
    public void testPreviewAccountRequestDeletion_nonExistentRequest_shouldReturnWarning() {
        String email = "nonexistent@example.com";
        String institute = "Test Institute";

        DeletionPreviewData preview = deletionPreviewService.previewAccountRequestDeletion(email, institute);

        assertEquals(DeletionPreviewData.EntityType.ACCOUNT_REQUEST, preview.getEntityType());
        assertTrue(preview.hasWarnings());
        assertTrue(preview.getWarnings().get(0).contains("Account request does not exist"));
    }

    @Test
    public void testPreviewNotificationDeletion_nonExistentNotification_shouldReturnWarning() {
        String notificationId = "nonexistent-notification-id";

        DeletionPreviewData preview = deletionPreviewService.previewNotificationDeletion(notificationId);

        assertEquals(DeletionPreviewData.EntityType.NOTIFICATION, preview.getEntityType());
        assertTrue(preview.hasWarnings());
        assertTrue(preview.getWarnings().get(0).contains("Notification does not exist"));
    }
}
