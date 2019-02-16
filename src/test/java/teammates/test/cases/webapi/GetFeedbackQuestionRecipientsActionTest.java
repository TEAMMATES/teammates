package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.ui.webapi.action.GetFeedbackQuestionRecipientsAction;
import teammates.ui.webapi.action.Intent;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.FeedbackQuestionRecipientsData;

/**
 * SUT: {@link GetFeedbackQuestionRecipientsAction}.
 */
public class GetFeedbackQuestionRecipientsActionTest extends BaseActionTest<GetFeedbackQuestionRecipientsAction> {

    private FeedbackSessionAttributes firstSession;
    private FeedbackSessionAttributes secondSession;
    private StudentAttributes student1InCourse1;
    private InstructorAttributes instructor1OfCourse1;

    @Override
    @BeforeMethod
    public void beforeTestMethodSetup() {
        super.beforeTestMethodSetup();
        firstSession = typicalBundle.feedbackSessions.get("session1InCourse1");
        secondSession = typicalBundle.feedbackSessions.get("session2InCourse1");
        student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
    }

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
    private String[] generateParameters(FeedbackSessionAttributes session, int questionNumber, Intent intent,
                                        String regKey, String moderatedPerson, String previewPerson) {
        FeedbackQuestionAttributes question = logic.getFeedbackQuestion(session.getFeedbackSessionName(),
                session.getCourseId(), questionNumber);
        return new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, question.getFeedbackQuestionId(),
                Const.ParamsNames.INTENT, intent.toString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_PERSON, moderatedPerson,
                Const.ParamsNames.PREVIEWAS, previewPerson,
                Const.ParamsNames.REGKEY, regKey,
        };
    }

    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {

        ______TS("Student intends to access instructor's question, should not be accessible");
        String[] studentAccessInstructorQuestionParams = generateParameters(firstSession, 3,
                Intent.STUDENT_SUBMISSION, "", "", "");
        verifyCannotAccess(studentAccessInstructorQuestionParams);

        ______TS("Instructor intends to access student's question, should not be accessible");
        String[] instructorAccessStudentQuestionParams = generateParameters(firstSession, 2,
                Intent.INSTRUCTOR_SUBMISSION, "", "", "");
        verifyCannotAccess(instructorAccessStudentQuestionParams);

        ______TS("Instructor access instructor's question, should be accessible");
        String[] instructorSubmissionParams = generateParameters(firstSession, 3,
                Intent.INSTRUCTOR_SUBMISSION, "", "", "");
        verifyOnlyInstructorsOfTheSameCourseCanAccess(instructorSubmissionParams);
        verifyInaccessibleWithoutModifyInstructorPrivilege(instructorSubmissionParams);

        ______TS("Student access student's question, should be accessible");
        String[] studentSubmissionParams = generateParameters(firstSession, 2,
                Intent.STUDENT_SUBMISSION, "", "", "");
        verifyAccessibleForStudentsOfTheSameCourse(studentSubmissionParams);

        ______TS("Student in the same course without logging in, should be accessible");
        StudentAttributes unloggedStudent =
                logic.getStudentForGoogleId(student1InCourse1.getCourse(), student1InCourse1.googleId);
        String[] unregisteredStudentSubmissionParams = generateParameters(firstSession, 2,
                Intent.STUDENT_SUBMISSION, StringHelper.encrypt(unloggedStudent.getKey()),
                "", "");
        verifyAccessibleWithoutLogin(unregisteredStudentSubmissionParams);

        ______TS("Question not intended shown to instructor, moderated instructor should not be accessible");
        String[] invalidModeratedInstructorSubmissionParams = generateParameters(secondSession, 1,
                Intent.INSTRUCTOR_SUBMISSION, "", instructor1OfCourse1.email, "");
        verifyCannotAccess(invalidModeratedInstructorSubmissionParams);

        ______TS("Instructor moderates student's question, should be accessible if he has privilege");
        String[] moderatedStudentSubmissionParams = generateParameters(firstSession, 2,
                Intent.STUDENT_SUBMISSION, "", student1InCourse1.email, "");
        verifyAccessibleForInstructorsOfTheSameCourse(moderatedStudentSubmissionParams);
        verifyInaccessibleWithoutModifyInstructorPrivilege(moderatedStudentSubmissionParams);

        ______TS("Instructor previews student's question, should be accessible if he has privilege");
        String[] previewStudentSubmissionParams = generateParameters(firstSession, 2,
                Intent.STUDENT_SUBMISSION, "", "", student1InCourse1.email);
        verifyAccessibleForInstructorsOfTheSameCourse(previewStudentSubmissionParams);
        verifyInaccessibleWithoutModifyInstructorPrivilege(previewStudentSubmissionParams);

        ______TS("Instructor moderates another instructor's question, "
                + "should be accessible if he has privilege");
        String[] moderatedInstructorSubmissionParams = generateParameters(firstSession, 3,
                Intent.INSTRUCTOR_SUBMISSION, "", instructor1OfCourse1.email, "");
        verifyAccessibleForInstructorsOfTheSameCourse(moderatedInstructorSubmissionParams);
        verifyInaccessibleWithoutModifyInstructorPrivilege(moderatedInstructorSubmissionParams);

        ______TS("Instructor previews another instructor's question,"
                + " should be accessible if he has privilege");
        String[] previewInstructorSubmissionParams = generateParameters(firstSession, 3,
                Intent.INSTRUCTOR_SUBMISSION, "", "", instructor1OfCourse1.email);
        verifyAccessibleForInstructorsOfTheSameCourse(previewInstructorSubmissionParams);
        verifyInaccessibleWithoutModifyInstructorPrivilege(previewInstructorSubmissionParams);
    }
}
