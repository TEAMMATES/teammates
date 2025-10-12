package teammates.logic.core;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.storage.api.CoursesDb;
import teammates.storage.api.InstructorsDb;
import teammates.storage.api.StudentsDb;

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

    private AccountsLogic accountsLogic;
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
    private StudentsLogic studentsLogic;
    private DeadlineExtensionsLogic deadlineExtensionsLogic;

    void initLogicDependencies() {
        accountsLogic = AccountsLogic.inst();
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
        studentsLogic = StudentsLogic.inst();
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

}
