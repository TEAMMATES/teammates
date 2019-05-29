package teammates.test.cases.webapi;

import java.util.List;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.ui.webapi.action.GetFeedbackResponsesAction;
import teammates.ui.webapi.action.Intent;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.FeedbackResponseData;
import teammates.ui.webapi.output.FeedbackResponsesData;

/**
 * SUT: {@link GetFeedbackResponsesAction}.
 */
public class GetFeedbackResponsesActionTest extends BaseActionTest<GetFeedbackResponsesAction> {

    private FeedbackQuestionAttributes qn1InSession1InCourse1;
    private StudentAttributes student1InCourse1;
    private InstructorAttributes instructor1OfCourse1;
    private FeedbackQuestionAttributes qn2InGracePeriodInCourse1;
    private StudentAttributes student1InCourse2;
    private InstructorAttributes instructor1OfCourse2;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.RESPONSES;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        // TODO
        useTypicalDataBundle();
        loginAsStudent(student1InCourse1.getGoogleId());

        ______TS("Not enough parameters");
        verifyHttpParameterFailure(Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSession1InCourse1.getId());

        ______TS("Invalid Intent");
        String[] paramsForInvalidIntent = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };
        GetFeedbackResponsesAction invalidIntentAction = getAction(paramsForInvalidIntent);
        assertThrows(InvalidHttpParameterException.class, () -> {
            getJsonResult(invalidIntentAction);
        });

        ______TS("Typical success case as a student");
        String[] paramsForStudent = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };
        GetFeedbackResponsesAction a = getAction(paramsForStudent);
        JsonResult resultForStudent = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, resultForStudent.getStatusCode());
        FeedbackResponsesData feedbackResponsesDataForStudent = (FeedbackResponsesData) resultForStudent.getOutput();
        List<FeedbackResponseData> responsesForStudent = feedbackResponsesDataForStudent.getResponses();
        assertEquals(1, responsesForStudent.size());

        FeedbackResponseData typicalResponseForStudent = responsesForStudent.get(0);
        FeedbackResponseAttributes expectedForStudent =
                logic.getFeedbackResponsesFromStudentOrTeamForQuestion(qn1InSession1InCourse1,
                        student1InCourse1).get(0);
        assertNotNull(typicalResponseForStudent.getFeedbackResponseId());
        assertEquals(expectedForStudent.getId(), typicalResponseForStudent.getFeedbackResponseId());
        assertEquals(expectedForStudent.getGiver(), typicalResponseForStudent.getGiverIdentifier());
        assertEquals(expectedForStudent.getRecipient(), typicalResponseForStudent.getRecipientIdentifier());
        assertEquals(expectedForStudent.getResponseDetails().getAnswerString(), typicalResponseForStudent
                .getResponseDetails()
                .getAnswerString());
        assertEquals(expectedForStudent.getResponseDetails().questionType, typicalResponseForStudent
                .getResponseDetails().questionType);

        assertEquals(JsonUtils.toJson(expectedForStudent.getResponseDetails()),
                JsonUtils.toJson(typicalResponseForStudent.getResponseDetails()));

        ______TS("Typical success case as a instructor");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        String[] paramsForInstructor = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn2InGracePeriodInCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        GetFeedbackResponsesAction b = getAction(paramsForInstructor);
        JsonResult resultForInstructor = getJsonResult(b);

        assertEquals(HttpStatus.SC_OK, resultForInstructor.getStatusCode());
        FeedbackResponsesData feedbackResponsesDataForInstructor =
                (FeedbackResponsesData) resultForInstructor.getOutput();
        List<FeedbackResponseData> responsesForInstructor = feedbackResponsesDataForInstructor.getResponses();
        assertEquals(1, responsesForInstructor.size());

        FeedbackResponseData typicalResponseForInstructor = responsesForInstructor.get(0);
        FeedbackResponseAttributes expectedForInstructor =
                logic.getFeedbackResponsesFromInstructorForQuestion(qn2InGracePeriodInCourse1, instructor1OfCourse1)
                        .get(0);
        assertNotNull(typicalResponseForInstructor.getFeedbackResponseId());
        assertEquals(expectedForInstructor.getId(), typicalResponseForInstructor.getFeedbackResponseId());
        assertEquals(expectedForInstructor.getGiver(), typicalResponseForInstructor.getGiverIdentifier());
        assertEquals(expectedForInstructor.getRecipient(), typicalResponseForInstructor.getRecipientIdentifier());
        assertEquals(expectedForInstructor.getResponseDetails().getAnswerString(), typicalResponseForInstructor
                .getResponseDetails()
                .getAnswerString());
        assertEquals(expectedForInstructor.getResponseDetails().questionType, typicalResponseForInstructor
                .getResponseDetails().questionType);

        assertEquals(JsonUtils.toJson(expectedForInstructor.getResponseDetails()),
                JsonUtils.toJson(typicalResponseForInstructor.getResponseDetails()));
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        // TODO
        useTypicalDataBundle();

        ______TS("non-existent feedback response");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        String[] nonExistParams = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, "randomNonExistId",
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        assertThrows(EntityNotFoundException.class, () -> getAction(nonExistParams).checkAccessControl());

        ______TS("Not answerable to students");
        loginAsStudent(student1InCourse1.getGoogleId());
        String[] notAnaserableToStudents = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn2InGracePeriodInCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };
        assertThrows(UnauthorizedAccessException.class, () -> getAction(notAnaserableToStudents).checkAccessControl());

        ______TS("Not answerable to instructors");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        String[] notAnswerableToInstructors = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        assertThrows(UnauthorizedAccessException.class,
                () -> getAction(notAnswerableToInstructors).checkAccessControl());

        ______TS("Cannot get responses in preview request");
        String[] inPreviewRequest = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
                Const.ParamsNames.PREVIEWAS, "randomPreviewers",
        };
        assertThrows(UnauthorizedAccessException.class, () -> getAction(inPreviewRequest).checkAccessControl());

        ______TS("student access other student's response from different course");
        loginAsStudent(student1InCourse2.getGoogleId());
        String[] studentAccessOtherStudentsParams = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn1InSession1InCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.STUDENT_SUBMISSION.toString(),
        };
        assertThrows(EntityNotFoundException.class, () -> getAction(studentAccessOtherStudentsParams)
                .checkAccessControl());

        ______TS("instructor access other instructor's response from different course");
        loginAsInstructor(instructor1OfCourse2.getGoogleId());
        String[] instructorAccessOtherInstructorsParams = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn2InGracePeriodInCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_SUBMISSION.toString(),
        };
        assertThrows(UnauthorizedAccessException.class, () -> getAction(instructorAccessOtherInstructorsParams)
                .checkAccessControl());

        ______TS("Unauthorized Intent Full Detail");
        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        String[] unauthorizedIntentFullDetail = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn2InGracePeriodInCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.FULL_DETAIL.toString(),
        };
        assertThrows(InvalidHttpParameterException.class, () -> getAction(unauthorizedIntentFullDetail)
                .checkAccessControl());
        ______TS("Unauthorized Intent Instructor Result");
        String[] unauthorizedIntentInstructorResult = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn2InGracePeriodInCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
        };
        assertThrows(InvalidHttpParameterException.class, () -> getAction(unauthorizedIntentInstructorResult)
                .checkAccessControl());
        ______TS("Unauthorized Intent Student Result");
        String[] unauthorizedIntentStudentResult = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, qn2InGracePeriodInCourse1.getId(),
                Const.ParamsNames.INTENT, Intent.INSTRUCTOR_RESULT.toString(),
        };
        assertThrows(InvalidHttpParameterException.class, () -> getAction(unauthorizedIntentStudentResult)
                .checkAccessControl());

    }

    private void useTypicalDataBundle() {
        removeAndRestoreTypicalDataBundle();
        FeedbackSessionAttributes gracePeriodSession;
        FeedbackSessionAttributes session1InCourse1;

        session1InCourse1 = typicalBundle.feedbackSessions.get("session1InCourse1");
        gracePeriodSession = typicalBundle.feedbackSessions.get("gracePeriodSession");
        instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        int q1 = 1;
        qn1InSession1InCourse1 = logic.getFeedbackQuestion(
                session1InCourse1.getFeedbackSessionName(), session1InCourse1.getCourseId(), q1);
        int q2 = 2;
        qn2InGracePeriodInCourse1 = logic.getFeedbackQuestion(
                gracePeriodSession.getFeedbackSessionName(), gracePeriodSession.getCourseId(), q2);
        student1InCourse2 = typicalBundle.students.get("student1InCourse2");
        instructor1OfCourse2 = typicalBundle.instructors.get("instructor1OfCourse2");

    }

}
