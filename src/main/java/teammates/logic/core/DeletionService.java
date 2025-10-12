package teammates.logic.core;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.RequestTracer;
import teammates.storage.api.*;
import teammates.storage.entity.Account;

import java.util.List;

/**
 * Integrate centralized deletion service across the system
 */
public class DeletionService {

    private static final DeletionService instance = new DeletionService();

    //prevent initialising Deletion Service
    private DeletionService() {}

    public static DeletionService inst() {
        return instance;
    }

    private AccountsDb accountsDb;
    private AccountRequestsDb accountRequestsDb;
    private CoursesDb coursesDb;
    private DeadlineExtensionsLogic deLogic;
    private FeedbackSessionsLogic feedbackSessionsLogic;
    private FeedbackQuestionsLogic fqLogic;
    private FeedbackResponsesLogic frLogic;
    private FeedbackSessionsLogic fsLogic;
    private FeedbackResponseCommentsLogic frcLogic;
    private InstructorsDb instructorsDb;
    private InstructorsLogic instructorsLogic;
    private StudentsDb studentsDb;
    private DeadlineExtensionsLogic deadlineExtensionsLogic;

    void initLogicDependencies() {
        accountsDb = AccountsDb.inst();
        accountRequestsDb = AccountRequestsDb.inst();
        coursesDb = CoursesDb.inst();
        deLogic = DeadlineExtensionsLogic.inst();
        feedbackSessionsLogic = FeedbackSessionsLogic.inst();
        fqLogic = FeedbackQuestionsLogic.inst();
        frLogic = FeedbackResponsesLogic.inst();
        fsLogic = FeedbackSessionsLogic.inst();
        frcLogic = FeedbackResponseCommentsLogic.inst();
        instructorsDb = InstructorsDb.inst();
        instructorsLogic = InstructorsLogic.inst();
        studentsDb = StudentsDb.inst();
        deadlineExtensionsLogic = DeadlineExtensionsLogic.inst();
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
        frcLogic.deleteFeedbackResponseComments(query);
        frLogic.deleteFeedbackResponses(query);
        fqLogic.deleteFeedbackQuestions(query);
        feedbackSessionsLogic.deleteFeedbackSessions(query);
        deleteStudents(query);
        deleteInstructors(query);
        deadlineExtensionsLogic.deleteDeadlineExtensions(query);

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
        fsLogic.deleteFeedbackSessionsDeadlinesForStudent(courseId, studentEmail);
        deLogic.deleteDeadlineExtensions(courseId, studentEmail, false);

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
        fsLogic.deleteFeedbackSessionsDeadlinesForInstructor(courseId, email);
        deLogic.deleteDeadlineExtensions(courseId, email, true);
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
     * Deletes the account request associated with the email address and institute.
     *
     * <p>Fails silently if the account request doesn't exist.</p>
     */
    public void deleteAccountRequest(String email, String institute) {
        accountRequestsDb.deleteAccountRequest(email, institute);
    }



}
