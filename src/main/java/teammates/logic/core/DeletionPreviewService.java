package teammates.logic.core;

import java.util.List;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.DeletionPreviewData;
import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.storage.api.AccountRequestsDb;
import teammates.storage.api.AccountsDb;
import teammates.storage.api.CoursesDb;
import teammates.storage.api.DeadlineExtensionsDb;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.FeedbackResponseCommentsDb;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.storage.api.InstructorsDb;
import teammates.storage.api.NotificationsDb;
import teammates.storage.api.StudentsDb;

/**
 * Handles preview operations for deletions to show what will be affected.
 *
 * <p>This service provides preview functionality to show users the impact
 * of deletion operations before they are executed.
 */
public final class DeletionPreviewService {

    private static final DeletionPreviewService instance = new DeletionPreviewService();

    private final AccountsDb accountsDb = AccountsDb.inst();
    private final AccountRequestsDb accountRequestsDb = AccountRequestsDb.inst();
    private final CoursesDb coursesDb = CoursesDb.inst();
    private final DeadlineExtensionsDb deadlineExtensionsDb = DeadlineExtensionsDb.inst();
    private final FeedbackQuestionsDb fqDb = FeedbackQuestionsDb.inst();
    private final FeedbackResponseCommentsDb frcDb = FeedbackResponseCommentsDb.inst();
    private final FeedbackResponsesDb frDb = FeedbackResponsesDb.inst();
    private final FeedbackSessionsDb fbDb = FeedbackSessionsDb.inst();
    private final InstructorsDb instructorsDb = InstructorsDb.inst();
    private final NotificationsDb nfDb = NotificationsDb.inst();
    private final StudentsDb studentsDb = StudentsDb.inst();

    private InstructorsLogic instructorsLogic;

    private DeletionPreviewService() {
        // prevent initialization
    }

    public static DeletionPreviewService inst() {
        return instance;
    }

    void initLogicDependencies() {
        instructorsLogic = InstructorsLogic.inst();
    }

    private InstructorsLogic getInstructorsLogic() {
        if (instructorsLogic == null) {
            initLogicDependencies();
        }
        return instructorsLogic;
    }

    /**
     * Previews the deletion of a course and all its associated data.
     *
     * @param courseId the ID of the course to preview deletion
     * @return DeletionPreviewData containing impact statistics
     */
    public DeletionPreviewData previewCourseDeletion(String courseId) {
        DeletionPreviewData preview = new DeletionPreviewData(
                DeletionPreviewData.EntityType.COURSE, courseId);

        if (coursesDb.getCourse(courseId) == null) {
            preview.addWarning("Course does not exist: " + courseId);
            return preview;
        }

        AttributesDeletionQuery query = AttributesDeletionQuery.builder()
                .withCourseId(courseId)
                .build();

        // Count students
        int studentCount = studentsDb.getStudentsForCourse(courseId).size();
        preview.setStudentsAffected(studentCount);
        if (studentCount > 0) {
            preview.addCascadedDeletion(
                    DeletionPreviewData.EntityType.STUDENT,
                    studentCount,
                    "All students in this course will be deleted");
        }

        // Count instructors
        int instructorCount = instructorsDb.getInstructorsForCourse(courseId).size();
        preview.setInstructorsAffected(instructorCount);
        if (instructorCount > 0) {
            preview.addCascadedDeletion(
                    DeletionPreviewData.EntityType.INSTRUCTOR,
                    instructorCount,
                    "All instructors in this course will be deleted");
        }

        // Count feedback sessions
        int sessionCount = fbDb.getFeedbackSessionsCountForCourse(query);
        preview.setFeedbackSessionsAffected(sessionCount);
        if (sessionCount > 0) {
            preview.addCascadedDeletion(
                    DeletionPreviewData.EntityType.FEEDBACK_SESSION,
                    sessionCount,
                    "All feedback sessions in this course will be deleted");
        }

        // Count feedback questions
        int questionCount = fqDb.getFeedbackQuestionsCountForCourse(query);
        preview.setFeedbackQuestionsAffected(questionCount);

        // Count feedback responses
        int responseCount = frDb.getFeedbackResponsesCountForCourse(query);
        preview.setFeedbackResponsesAffected(responseCount);

        // Count feedback response comments
        int commentCount = frcDb.getFeedbackResponseCommentsCountForCourse(query);
        preview.setFeedbackCommentsAffected(commentCount);

        // Count deadline extensions
        int deadlineExtensionCount = deadlineExtensionsDb.getDeadlineExtensionsCountForCourse(query);
        preview.setDeadlineExtensionsAffected(deadlineExtensionCount);

        preview.setCoursesAffected(1);

        // Add warnings if there's significant data
        if (studentCount > 50) {
            preview.addWarning("This course has a large number of students (" + studentCount + ")");
        }
        if (responseCount > 100) {
            preview.addWarning("This course has a large number of feedback responses (" + responseCount + ")");
        }

        return preview;
    }

    /**
     * Previews the deletion of a student and all associated data.
     *
     * @param courseId the course ID
     * @param studentEmail the student's email
     * @return DeletionPreviewData containing impact statistics
     */
    public DeletionPreviewData previewStudentDeletion(String courseId, String studentEmail) {
        DeletionPreviewData preview = new DeletionPreviewData(
                DeletionPreviewData.EntityType.STUDENT,
                courseId + "/" + studentEmail);

        StudentAttributes student = studentsDb.getStudentForEmail(courseId, studentEmail);
        if (student == null) {
            preview.addWarning("Student does not exist: " + studentEmail + " in course " + courseId);
            return preview;
        }

        // Count feedback responses involving this student
        int giverResponseCount = frDb.getFeedbackResponsesFromGiverForCourse(courseId, studentEmail).size();
        int recipientResponseCount = frDb.getFeedbackResponsesForReceiverForCourse(courseId, studentEmail).size();
        int totalResponseCount = giverResponseCount + recipientResponseCount;
        preview.setFeedbackResponsesAffected(totalResponseCount);

        if (totalResponseCount > 0) {
            preview.addCascadedDeletion(
                    DeletionPreviewData.EntityType.FEEDBACK_SESSION,
                    totalResponseCount,
                    "Feedback responses given by or received by this student will be deleted");
        }

        // Count comments on those responses
        AttributesDeletionQuery responseQuery = AttributesDeletionQuery.builder()
                .withCourseId(courseId)
                .withUserEmail(studentEmail)
                .build();
        int commentCount = frcDb.getFeedbackResponseCommentsCountForCourse(responseQuery);
        preview.setFeedbackCommentsAffected(commentCount);

        // Count deadline extensions
        AttributesDeletionQuery deadlineQuery = AttributesDeletionQuery.builder()
                .withCourseId(courseId)
                .withUserEmail(studentEmail)
                .withIsInstructor(false)
                .build();
        int deadlineExtensionCount = deadlineExtensionsDb.getDeadlineExtensionsCountForCourse(deadlineQuery);
        preview.setDeadlineExtensionsAffected(deadlineExtensionCount);

        // Check if student is the only one in their team
        if (studentsDb.getStudentCountForTeam(student.getCourse(), student.getTeam()) == 1) {
            preview.addWarning("This student is the only member of their team. "
                    + "Team-related responses will also be deleted.");
        }

        preview.setStudentsAffected(1);

        return preview;
    }

    /**
     * Previews the deletion of an instructor and all associated data.
     *
     * @param courseId the course ID
     * @param email the instructor's email
     * @return DeletionPreviewData containing impact statistics
     */
    public DeletionPreviewData previewInstructorDeletion(String courseId, String email) {
        DeletionPreviewData preview = new DeletionPreviewData(
                DeletionPreviewData.EntityType.INSTRUCTOR,
                courseId + "/" + email);

        InstructorAttributes instructor = instructorsDb.getInstructorForEmail(courseId, email);
        if (instructor == null) {
            preview.addWarning("Instructor does not exist: " + email + " in course " + courseId);
            return preview;
        }

        // Check if this is the last instructor in the course
        List<InstructorAttributes> allInstructors = instructorsDb.getInstructorsForCourse(courseId);
        boolean isLastInstructor = allInstructors.size() == 1;
        preview.setLastInstructor(isLastInstructor);

        if (isLastInstructor) {
            preview.addWarning("WARNING: This is the last instructor in the course. "
                    + "Deleting this instructor will leave the course without any instructors.");
            preview.setWillOrphanCourse(true);
        }

        // Count feedback responses involving this instructor
        int giverResponseCount = frDb.getFeedbackResponsesFromGiverForCourse(courseId, email).size();
        int recipientResponseCount = frDb.getFeedbackResponsesForReceiverForCourse(courseId, email).size();
        int totalResponseCount = giverResponseCount + recipientResponseCount;
        preview.setFeedbackResponsesAffected(totalResponseCount);

        if (totalResponseCount > 0) {
            preview.addCascadedDeletion(
                    DeletionPreviewData.EntityType.FEEDBACK_SESSION,
                    totalResponseCount,
                    "Feedback responses given by or received by this instructor will be deleted");
        }

        // Count comments
        AttributesDeletionQuery commentQuery = AttributesDeletionQuery.builder()
                .withCourseId(courseId)
                .withUserEmail(email)
                .build();
        int commentCount = frcDb.getFeedbackResponseCommentsCountForCourse(commentQuery);
        preview.setFeedbackCommentsAffected(commentCount);

        // Count deadline extensions
        AttributesDeletionQuery deadlineQuery = AttributesDeletionQuery.builder()
                .withCourseId(courseId)
                .withUserEmail(email)
                .withIsInstructor(true)
                .build();
        int deadlineExtensionCount = deadlineExtensionsDb.getDeadlineExtensionsCountForCourse(deadlineQuery);
        preview.setDeadlineExtensionsAffected(deadlineExtensionCount);

        preview.setInstructorsAffected(1);

        return preview;
    }

    /**
     * Previews the deletion of an account and all associated data.
     *
     * @param googleId the Google ID of the account
     * @return DeletionPreviewData containing impact statistics
     */
    public DeletionPreviewData previewAccountDeletion(String googleId) {
        DeletionPreviewData preview = new DeletionPreviewData(
                DeletionPreviewData.EntityType.ACCOUNT, googleId);

        if (accountsDb.getAccount(googleId) == null) {
            preview.addWarning("Account does not exist: " + googleId);
            return preview;
        }

        // Get all instructors for this account
        List<InstructorAttributes> instructors = getInstructorsLogic().getInstructorsForGoogleId(googleId, false);
        preview.setInstructorsAffected(instructors.size());

        // Check for courses that will be orphaned
        int coursesToDelete = 0;
        for (InstructorAttributes instructor : instructors) {
            List<InstructorAttributes> courseInstructors =
                    getInstructorsLogic().getInstructorsForCourse(instructor.getCourseId());
            if (courseInstructors.size() <= 1) {
                coursesToDelete++;
                preview.addWarning("Course " + instructor.getCourseId()
                        + " will be deleted because this is the last instructor");
            }
        }
        preview.setCoursesAffected(coursesToDelete);

        if (coursesToDelete > 0) {
            preview.addCascadedDeletion(
                    DeletionPreviewData.EntityType.COURSE,
                    coursesToDelete,
                    "Courses where this is the last instructor will be completely deleted");
        }

        // Get all students for this account
        List<StudentAttributes> students = studentsDb.getStudentsForGoogleId(googleId);
        preview.setStudentsAffected(students.size());

        // Estimate total responses (this is an approximation)
        int totalResponses = 0;
        for (StudentAttributes student : students) {
            totalResponses += frDb.getFeedbackResponsesFromGiverForCourse(
                    student.getCourse(), student.getEmail()).size();
            totalResponses += frDb.getFeedbackResponsesForReceiverForCourse(
                    student.getCourse(), student.getEmail()).size();
        }
        for (InstructorAttributes instructor : instructors) {
            totalResponses += frDb.getFeedbackResponsesFromGiverForCourse(
                    instructor.getCourseId(), instructor.getEmail()).size();
            totalResponses += frDb.getFeedbackResponsesForReceiverForCourse(
                    instructor.getCourseId(), instructor.getEmail()).size();
        }
        preview.setFeedbackResponsesAffected(totalResponses);

        preview.setAccountsAffected(1);

        if (instructors.size() > 5 || students.size() > 10) {
            preview.addWarning("This account has significant data associated with it ("
                    + instructors.size() + " instructor roles, "
                    + students.size() + " student enrollments)");
        }

        if (coursesToDelete > 0) {
            preview.addWarning("CRITICAL: " + coursesToDelete
                    + " course(s) will be permanently deleted because this is the last instructor");
        }

        return preview;
    }

    /**
     * Previews the deletion of an account request.
     *
     * @param email the email of the account request
     * @param institute the institute of the account request
     * @return DeletionPreviewData containing impact statistics
     */
    public DeletionPreviewData previewAccountRequestDeletion(String email, String institute) {
        DeletionPreviewData preview = new DeletionPreviewData(
                DeletionPreviewData.EntityType.ACCOUNT_REQUEST,
                email + "/" + institute);

        AccountRequestAttributes accountRequest = accountRequestsDb.getAccountRequest(email, institute);
        if (accountRequest == null) {
            preview.addWarning("Account request does not exist: " + email + " at " + institute);
            return preview;
        }

        preview.setAccountRequestsAffected(1);

        return preview;
    }

    /**
     * Previews the deletion of a notification.
     *
     * @param notificationId the ID of the notification
     * @return DeletionPreviewData containing impact statistics
     */
    public DeletionPreviewData previewNotificationDeletion(String notificationId) {
        DeletionPreviewData preview = new DeletionPreviewData(
                DeletionPreviewData.EntityType.NOTIFICATION, notificationId);

        if (nfDb.getNotification(notificationId) == null) {
            preview.addWarning("Notification does not exist: " + notificationId);
            return preview;
        }

        preview.setNotificationsAffected(1);

        return preview;
    }
}
