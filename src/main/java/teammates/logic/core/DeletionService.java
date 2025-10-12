package teammates.logic.core;

import java.util.List;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.RequestTracer;
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
 * Handles centralized deletion operations for various entities.
 *
 * <p>This service consolidates deletion logic from multiple Logic classes
 * to provide a single source of truth for deletion operations.
 */
public final class DeletionService {

    private static final DeletionService instance = new DeletionService();

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

    private FeedbackSessionsLogic feedbackSessionsLogic;
    private FeedbackResponsesLogic frLogic;
    private InstructorsLogic instructorsLogic;

    private DeletionService() {
        // prevent initialization
    }

    public static DeletionService inst() {
        return instance;
    }

    void initLogicDependencies() {
        feedbackSessionsLogic = FeedbackSessionsLogic.inst();
        frLogic = FeedbackResponsesLogic.inst();
        instructorsLogic = InstructorsLogic.inst();
    }

    /**
     * Deletes students using {@link AttributesDeletionQuery}.
     */
    public void deleteStudents(AttributesDeletionQuery query) {
        studentsDb.deleteStudents(query);
        updateStudentResponsesAfterDeletion(query.getCourseId());
    }

    /**
     * Deletes instructors using {@link AttributesDeletionQuery}.
     */
    public void deleteInstructors(AttributesDeletionQuery query) {
        instructorsDb.deleteInstructors(query);
    }

    /**
     * Deletes a course cascade its students, instructors, sessions, responses, deadline extensions and comments.
     *
     * <p>Fails silently if no such course.
     */
    public void deleteCourseCascade(String courseId) {
        if (coursesDb.getCourse(courseId) == null) {
            return;
        }

        AttributesDeletionQuery query = AttributesDeletionQuery.builder()
                .withCourseId(courseId)
                .build();
        frcDb.deleteFeedbackResponseComments(query);
        frDb.deleteFeedbackResponses(query);
        fqDb.deleteFeedbackQuestions(query);
        fbDb.deleteFeedbackSessions(query);
        deleteStudents(query);
        deleteInstructors(query);
        deadlineExtensionsDb.deleteDeadlineExtensions(query);

        coursesDb.deleteCourse(courseId);
    }

    /**
     * Deletes a student cascade its associated feedback responses, deadline extensions and comments.
     *
     * <p>Fails silently if the student does not exist.
     */
    public void deleteStudentCascade(String courseId, String studentEmail) {
        StudentAttributes student = studentsDb.getStudentForEmail(courseId, studentEmail);
        if (student == null) {
            return;
        }

        frLogic.deleteFeedbackResponsesInvolvedEntityOfCourseCascade(courseId, studentEmail);
        if (studentsDb.getStudentCountForTeam(student.getTeam(), student.getCourse()) == 1) {
            // the student is the only student in the team, delete responses related to the team
            frLogic.deleteFeedbackResponsesInvolvedEntityOfCourseCascade(student.getCourse(), student.getTeam());
        }
        studentsDb.deleteStudent(courseId, studentEmail);
        feedbackSessionsLogic.deleteFeedbackSessionsDeadlinesForStudent(courseId, studentEmail);
        deleteDeadlineExtensions(courseId, studentEmail, false);

        updateStudentResponsesAfterDeletion(courseId);
    }

    private void updateStudentResponsesAfterDeletion(String courseId) {
        frLogic.updateFeedbackResponsesForDeletingStudent(courseId);
    }

    /**
     * Deletes all students associated a googleId and cascade
     * its associated feedback responses, deadline extensions and comments.
     */
    public void deleteStudentsForGoogleIdCascade(String googleId) {
        List<StudentAttributes> students = studentsDb.getStudentsForGoogleId(googleId);

        // Cascade delete students
        for (StudentAttributes student : students) {
            deleteStudentCascade(student.getCourse(), student.getEmail());
        }
    }

    /**
     * Deletes the first {@code batchSize} of the remaining students in the course cascade their
     * associated responses, deadline extensions, and comments.
     */
    public void deleteStudentsInCourseCascade(String courseId, int batchSize) {
        var studentsInCourse = studentsDb.getStudentsForCourse(courseId, batchSize);
        for (var student : studentsInCourse) {
            RequestTracer.checkRemainingTime();
            deleteStudentCascade(courseId, student.getEmail());
        }
    }

    /**
     * Deletes an instructor cascade its associated feedback responses, deadline extensions and comments.
     *
     * <p>Fails silently if the student does not exist.
     */
    public void deleteInstructorCascade(String courseId, String email) {
        InstructorAttributes instructorAttributes = instructorsDb.getInstructorForEmail(courseId, email);
        if (instructorAttributes == null) {
            return;
        }

        frLogic.deleteFeedbackResponsesInvolvedEntityOfCourseCascade(courseId, email);
        instructorsDb.deleteInstructor(courseId, email);
        feedbackSessionsLogic.deleteFeedbackSessionsDeadlinesForInstructor(courseId, email);
        deleteDeadlineExtensions(courseId, email, true);
    }

    /**
     * Deletes all instructors associated with a googleId and cascade delete its associated feedback responses,
     * deadline extensions and comments.
     */
    public void deleteInstructorsForGoogleIdCascade(String googleId) {
        List<InstructorAttributes> instructors = instructorsDb.getInstructorsForGoogleId(googleId, false);

        // cascade delete instructors
        for (InstructorAttributes instructor : instructors) {
            deleteInstructorCascade(instructor.getCourseId(), instructor.getEmail());
        }
    }

    /**
     * Deletes both instructor and student privileges, as well as the account.
     *
     * <ul>
     * <li>Fails silently if no such account.</li>
     * </ul>
     */
    public void deleteAccountCascade(String googleId) {
        // we skip this check for dual db, since all accounts are migrated, but there
        // will still be datastore entities (Student, Course, Instructor)
        // to be deleted by googleId
        // if (accountsDb.getAccount(googleId) == null) {
        // return;
        // }

        // to prevent orphan course
        List<InstructorAttributes> instructorsToDelete =
                instructorsLogic.getInstructorsForGoogleId(googleId, false);
        for (InstructorAttributes instructorToDelete : instructorsToDelete) {
            if (instructorsLogic.getInstructorsForCourse(instructorToDelete.getCourseId()).size() <= 1) {
                // the instructor is the last instructor in the course
                deleteCourseCascade(instructorToDelete.getCourseId());
            }
        }

        instructorsLogic.deleteInstructorsForGoogleIdCascade(googleId);
        deleteStudentsForGoogleIdCascade(googleId);
        accountsDb.deleteAccount(googleId);
    }

    /**
     * Deletes an account.
     *
     * <p>Fails silently if the account doesn't exist.</p>
     */
    public void deleteAccount(String googleId) {
        accountsDb.deleteAccount(googleId);
    }

    /**
     * Deletes the account request associated with the email address and institute.
     *
     * <p>Fails silently if the account request doesn't exist.</p>
     */
    public void deleteAccountRequest(String email, String institute) {
        accountRequestsDb.deleteAccountRequest(email, institute);
    }

    // ==================== Deadline Extensions Deletion ====================

    /**
     * Deletes a deadline extension.
     *
     * <p>Fails silently if the deadline extension doesn't exist.</p>
     */
    public void deleteDeadlineExtension(
            String courseId, String feedbackSessionName, String userEmail, boolean isInstructor) {
        deadlineExtensionsDb.deleteDeadlineExtension(courseId, feedbackSessionName, userEmail, isInstructor);
    }

    /**
     * Deletes all deadline extensions for a user in a course.
     *
     * <p>Fails silently if the deadline extension doesn't exist.</p>
     */
    public void deleteDeadlineExtensions(String courseId, String userEmail, boolean isInstructor) {
        AttributesDeletionQuery query = AttributesDeletionQuery.builder()
                .withCourseId(courseId)
                .withUserEmail(userEmail)
                .withIsInstructor(isInstructor)
                .build();
        deadlineExtensionsDb.deleteDeadlineExtensions(query);
    }

    /**
     * Deletes deadline extensions using {@link AttributesDeletionQuery}.
     */
    public void deleteDeadlineExtensions(AttributesDeletionQuery query) {
        deadlineExtensionsDb.deleteDeadlineExtensions(query);
    }

    // ==================== Feedback Questions Deletion ====================

    /**
     * Deletes a feedback question and cascades to its responses and comments.
     */
    public void deleteFeedbackQuestionCascade(String feedbackQuestionId) {
        // cascade delete responses and comments for question
        AttributesDeletionQuery query = AttributesDeletionQuery.builder()
                .withQuestionId(feedbackQuestionId)
                .build();
        frcDb.deleteFeedbackResponseComments(query);
        frDb.deleteFeedbackResponses(query);

        // delete question
        fqDb.deleteFeedbackQuestion(feedbackQuestionId);
    }

    /**
     * Deletes feedback questions using {@link AttributesDeletionQuery}.
     */
    public void deleteFeedbackQuestions(AttributesDeletionQuery query) {
        fqDb.deleteFeedbackQuestions(query);
    }

    // ==================== Feedback Response Comments Deletion ====================

    /**
     * Deletes a feedback response comment.
     */
    public void deleteFeedbackResponseComment(long commentId) {
        frcDb.deleteFeedbackResponseComment(commentId);
    }

    /**
     * Deletes feedback response comments using {@link AttributesDeletionQuery}.
     */
    public void deleteFeedbackResponseComments(AttributesDeletionQuery query) {
        frcDb.deleteFeedbackResponseComments(query);
    }

    // ==================== Feedback Responses Deletion ====================

    /**
     * Deletes feedback responses using {@link AttributesDeletionQuery}.
     */
    public void deleteFeedbackResponses(AttributesDeletionQuery query) {
        frDb.deleteFeedbackResponses(query);
    }

    /**
     * Deletes a feedback response and cascades its associated comments.
     */
    public void deleteFeedbackResponseCascade(String responseId) {
        frcDb.deleteFeedbackResponseComments(
                AttributesDeletionQuery.builder()
                        .withResponseId(responseId)
                        .build());
        frDb.deleteFeedbackResponse(responseId);
    }

    /**
     * Deletes all feedback responses of a question and cascades its associated comments.
     */
    public void deleteFeedbackResponsesForQuestionCascade(String feedbackQuestionId) {
        AttributesDeletionQuery query = AttributesDeletionQuery.builder()
                .withQuestionId(feedbackQuestionId)
                .build();
        frcDb.deleteFeedbackResponseComments(query);
        frDb.deleteFeedbackResponses(query);
    }

    // ==================== Notifications Deletion ====================

    /**
     * Deletes notification associated with the {@code notificationId}.
     *
     * <p>Fails silently if the notification doesn't exist.</p>
     */
    public void deleteNotification(String notificationId) {
        nfDb.deleteNotification(notificationId);
    }

}
