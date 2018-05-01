package teammates.test.cases.action;

import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.ui.controller.AjaxResult;
import teammates.ui.controller.InstructorFeedbackQuestionVisibilityMessageAction;

/**
 * SUT: {@link InstructorFeedbackQuestionVisibilityMessageAction}.
 */
public class InstructorFeedbackQuestionVisibilityMessageActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_VISIBILITY_MESSAGE;
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {
        String instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1").googleId;

        gaeSimulation.loginAsInstructor(instructor1OfCourse1);

        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestionAttributes fq = FeedbackQuestionsLogic
                                            .inst()
                                            .getFeedbackQuestion(fs.getFeedbackSessionName(), fs.getCourseId(), 1);

        ______TS("Typical Case - max -> constructed params");

        String[] typicalParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "0",
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "TEXT",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "question",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "max",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId()
        };

        InstructorFeedbackQuestionVisibilityMessageAction a = getAction(typicalParams);
        AjaxResult r = getAjaxResult(a);

        assertFalse(r.isError);

        ______TS("Custom Case Students - constructed params");

        String[] customParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "1",
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "TEXT",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "question",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "custom",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId()
        };

        a = getAction(customParams);
        r = getAjaxResult(a);

        assertFalse(r.isError);

        ______TS("Custom Case Teams - data bundle params");

        fs = typicalBundle.feedbackSessions.get("session2InCourse1");
        fq = FeedbackQuestionsLogic.inst().getFeedbackQuestion(fs.getFeedbackSessionName(), fs.getCourseId(), 1);

        customParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, FeedbackParticipantType.TEAMS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, FeedbackParticipantType.TEAMS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "1",
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "TEXT",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "question",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "custom",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId()
        };

        a = getAction(customParams);
        r = getAjaxResult(a);

        assertFalse(r.isError);

        ______TS("Custom Case Instructor - data bundle params");

        fs = typicalBundle.feedbackSessions.get("gracePeriodSession");
        fq = FeedbackQuestionsLogic.inst().getFeedbackQuestion(fs.getFeedbackSessionName(), fs.getCourseId(), 1);

        customParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "TEXT",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "question",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "custom",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES, "1",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId()
        };

        a = getAction(customParams);
        r = getAjaxResult(a);

        assertFalse(r.isError);

        ______TS("Empty participant list - data bundle params");

        String instructor1OfCourse2 = typicalBundle.instructors.get("instructor1OfCourse2").googleId;

        gaeSimulation.logoutUser(); // log out of instructor 1
        gaeSimulation.loginAsInstructor(instructor1OfCourse2);

        fs = typicalBundle.feedbackSessions.get("session1InCourse2");
        fq = FeedbackQuestionsLogic.inst().getFeedbackQuestion(fs.getFeedbackSessionName(), fs.getCourseId(), 1);

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "1",
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "TEXT",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "question",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "custom",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES, "10",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, "",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, "",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, "",
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId()
        };
        a = getAction(params);
        r = getAjaxResult(a);

        assertFalse(r.isError);

        ______TS("Null participant list - constructed params");

        params = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "1",
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "TEXT",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "question",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "custom",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES, "10",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, null,
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, null,
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, null,
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId()
        };

        a = getAction(params);
        r = getAjaxResult(a);

        assertFalse(r.isError);
    }

    @Override
    protected InstructorFeedbackQuestionVisibilityMessageAction getAction(String... params) {
        return (InstructorFeedbackQuestionVisibilityMessageAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        //TODO: implement this
    }
}
