package teammates.sqlui.webapi;

import static org.mockito.Mockito.when;
import static teammates.common.util.Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER;

import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.MessageOutput;
import teammates.ui.webapi.DeleteFeedbackQuestionAction;

/**
 * SUT: {@link DeleteFeedbackQuestionAction}.
 */
public class DeleteFeedbackQuestionActionTest extends BaseActionTest<DeleteFeedbackQuestionAction> {

    private final Instructor typicalInstructor = getTypicalInstructor();
    private final Course typicalCourse = typicalInstructor.getCourse();
    private final FeedbackSession typicalFeedbackSession = getTypicalFeedbackSessionForCourse(typicalCourse);
    private final FeedbackQuestion typicalFeedbackQuestion =
            getTypicalFeedbackQuestionForSession(typicalFeedbackSession);

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.QUESTION;
    }

    @Override
    protected String getRequestMethod() {
        return DELETE;
    }

    @Test
    void testExecute_feedbackQuestionExists_success() {
        when(mockLogic.getFeedbackQuestion(typicalFeedbackQuestion.getId())).thenReturn(typicalFeedbackQuestion);

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalFeedbackQuestion.getId().toString(),
        };

        DeleteFeedbackQuestionAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals("Feedback question deleted!", actionOutput.getMessage());
    }

    @Test
    void testExecute_feedbackQuestionDoesNotExist_failSilently() {
        UUID nonexistentQuestionId = UUID.fromString("11110000-0000-0000-0000-000000000000");
        when(mockLogic.getFeedbackQuestion(nonexistentQuestionId)).thenReturn(null);

        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, nonexistentQuestionId.toString(),
        };

        DeleteFeedbackQuestionAction action = getAction(params);
        MessageOutput actionOutput = (MessageOutput) getJsonResult(action).getOutput();

        assertEquals("Feedback question deleted!", actionOutput.getMessage());
    }

    @Test
    void testExecute_missingFeedbackQuestionId_throwsInvalidHttpParameterException() {
        String[] params = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, null,
        };

        verifyHttpParameterFailure(params);
    }

    @Test
    void testSpecificAccessControl_nonExistentFeedbackQuestion_cannotAccess() {
        when(mockLogic.getFeedbackQuestion(typicalFeedbackQuestion.getId())).thenReturn(null);
        String[] submissionParams = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalFeedbackQuestion.getId().toString(),
        };

        verifyCannotAccess(submissionParams);
    }

    @Test
    void testSpecificAccessControl_withModifySessionPrivilege_canAccess() {
        when(mockLogic.getFeedbackQuestion(typicalFeedbackQuestion.getId())).thenReturn(typicalFeedbackQuestion);
        when(mockLogic.getFeedbackSession(typicalFeedbackQuestion.getFeedbackSession().getName(),
                typicalFeedbackQuestion.getCourseId())).thenReturn(typicalFeedbackSession);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), typicalInstructor.getGoogleId()))
                .thenReturn(typicalInstructor);

        String[] submissionParams = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalFeedbackQuestion.getId().toString(),
        };

        loginAsInstructor(typicalInstructor.getGoogleId());
        verifyCanAccess(submissionParams);
    }

    @Test
    void testSpecificAccessControl_withoutModifySessionPrivilege_cannotAccess() {
        // create instructor without modify session privilege
        Instructor instructorWithoutAccess = getTypicalInstructor();
        instructorWithoutAccess.setPrivileges(new InstructorPrivileges(INSTRUCTOR_PERMISSION_ROLE_OBSERVER));

        when(mockLogic.getFeedbackQuestion(typicalFeedbackQuestion.getId())).thenReturn(typicalFeedbackQuestion);
        when(mockLogic.getFeedbackSession(typicalFeedbackQuestion.getFeedbackSession().getName(),
                typicalFeedbackQuestion.getCourseId())).thenReturn(typicalFeedbackSession);
        when(mockLogic.getInstructorByGoogleId(typicalCourse.getId(), instructorWithoutAccess.getGoogleId()))
                .thenReturn(instructorWithoutAccess);

        String[] submissionParams = {
                Const.ParamsNames.FEEDBACK_QUESTION_ID, typicalFeedbackQuestion.getId().toString(),
        };

        loginAsInstructor(instructorWithoutAccess.getGoogleId());
        verifyCannotAccess(submissionParams);
    }

}
