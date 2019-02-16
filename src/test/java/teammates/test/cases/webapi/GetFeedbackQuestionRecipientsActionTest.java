package teammates.test.cases.webapi;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.ui.webapi.action.GetFeedbackQuestionRecipientsAction;
import teammates.ui.webapi.action.Intent;

/**
 * SUT: {@link GetFeedbackQuestionRecipientsAction}.
 */
public class GetFeedbackQuestionRecipientsActionTest extends BaseActionTest<GetFeedbackQuestionRecipientsAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.QUESTION_RECIPIENTS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        // TODO
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        FeedbackSessionAttributes firstSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackSessionAttributes secondSession = typicalBundle.feedbackSessions.get("session2InCourse1");
        StudentAttributes student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        FeedbackQuestionAttributes instructorQuestion =
                logic.getFeedbackQuestion(firstSession.getFeedbackSessionName(),
                        firstSession.getCourseId(), 3);
        FeedbackQuestionAttributes studentQuestion =
                logic.getFeedbackQuestion(firstSession.getFeedbackSessionName(),
                        firstSession.getCourseId(), 2);
        FeedbackQuestionAttributes questionNotShownToModeratedInstructor =
                logic.getFeedbackQuestion(secondSession.getFeedbackSessionName(),
                        secondSession.getCourseId(), 1);

        ______TS("Question not intended shown to instructor, moderated instructor should not be accessible");
        String[] invalidModeratedInstructorSubmissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, questionNotShownToModeratedInstructor.getFeedbackQuestionId(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, "idOfInstructor1OfCourse1",
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        verifyCannotAccess(invalidModeratedInstructorSubmissionParams);

        ______TS("Student intends to access instructor's question, should not be accessible");
        String[] studentAccessInstructorQuestion = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, instructorQuestion.getFeedbackQuestionId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };
        verifyCannotAccess(studentAccessInstructorQuestion);

        ______TS("Instructor intends to access student's question, should not be accessible");
        String[] instructorAccessStudentQuestion = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, studentQuestion.getFeedbackQuestionId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        verifyCannotAccess(instructorAccessStudentQuestion);

        ______TS("Instructor access instructor's question, should be accessible");
        String[] instructorSubmissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, instructorQuestion.getFeedbackQuestionId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        verifyOnlyInstructorsOfTheSameCourseCanAccess(instructorSubmissionParams);
        verifyInaccessibleWithoutModifyInstructorPrivilege(instructorSubmissionParams);

        ______TS("Student access student's question, should be accessible");
        String[] studentSubmissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, studentQuestion.getFeedbackQuestionId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };
        verifyAccessibleForStudentsOfTheSameCourse(studentSubmissionParams);

        ______TS("Student in the same course without logging in, should be accessible");
        StudentAttributes unloggedStudent =
                logic.getStudentForGoogleId(student1InCourse1.getCourse(), student1InCourse1.googleId);
        String[] unregisteredStudentSubmissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, studentQuestion.getFeedbackQuestionId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.REGKEY, StringHelper.encrypt(unloggedStudent.getKey()),
        };
        verifyAccessibleWithoutLogin(unregisteredStudentSubmissionParams);

        ______TS("Instructor moderates student's question, should be accessible if he has privilege");
        String[] moderatedStudentSubmissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, studentQuestion.getFeedbackQuestionId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, student1InCourse1.email,
        };
        verifyAccessibleForInstructorsOfTheSameCourse(moderatedStudentSubmissionParams);
        verifyInaccessibleWithoutModifyInstructorPrivilege(moderatedStudentSubmissionParams);

        ______TS("Instructor previews student's question, should be accessible if he has privilege");
        String[] previewStudentSubmissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, studentQuestion.getFeedbackQuestionId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
                Const.ParamsNames.PREVIEWAS, student1InCourse1.email,
        };
        verifyAccessibleForInstructorsOfTheSameCourse(previewStudentSubmissionParams);
        verifyInaccessibleWithoutModifyInstructorPrivilege(previewStudentSubmissionParams);

        ______TS("Instructor moderates another instructor's question, "
                + "should be accessible if he has privilege");
        String[] moderatedInstructorSubmissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, instructorQuestion.getFeedbackQuestionId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, instructor1OfCourse1.email,
        };
        verifyAccessibleForInstructorsOfTheSameCourse(moderatedInstructorSubmissionParams);
        verifyInaccessibleWithoutModifyInstructorPrivilege(moderatedInstructorSubmissionParams);

        ______TS("Instructor previews another instructor's question,"
                + " should be accessible if he has privilege");
        String[] previewInstructorSubmissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, instructorQuestion.getFeedbackQuestionId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.PREVIEWAS, instructor1OfCourse1.email,
        };
        verifyAccessibleForInstructorsOfTheSameCourse(previewInstructorSubmissionParams);
        verifyInaccessibleWithoutModifyInstructorPrivilege(previewInstructorSubmissionParams);
    }
}
