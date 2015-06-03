package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertFalse;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.ui.controller.InstructorFeedbackQuestionVisibilityMessageAction;
import teammates.ui.controller.ActionResult;

public class InstructorFeedbackQuestionVisibilityMessageActionTest extends BaseActionTest {
    private final DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_VISIBILITY_MESSAGE;
    }

    @Test
    public void testExecuteAndPostProcess() throws Exception {
        String instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1").googleId;

        gaeSimulation.loginAsInstructor(instructor1OfCourse1);

        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
        FeedbackQuestionAttributes fq = FeedbackQuestionsLogic
                                            .inst()
                                            .getFeedbackQuestion(fs.feedbackSessionName, fs.courseId, 1);

        ______TS("Typical Case - max -> constructed params");

        String[] typicalParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
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
        ActionResult r = (ActionResult) a.executeAndPostProcess();

        assertFalse(r.isError);

        ______TS("Custom Case Students - constructed params");

        String[] customParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
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
        r = (ActionResult) a.executeAndPostProcess();

        assertFalse(r.isError);

        ______TS("Custom Case Teams - data bundle params");

        fs = dataBundle.feedbackSessions.get("session2InCourse1");
        fq = FeedbackQuestionsLogic.inst().getFeedbackQuestion(fs.feedbackSessionName, fs.courseId, 1);

        customParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
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
        r = (ActionResult) a.executeAndPostProcess();

        assertFalse(r.isError);

        ______TS("Custom Case Instructor - data bundle params");

        fs = dataBundle.feedbackSessions.get("gracePeriodSession");
        fq = FeedbackQuestionsLogic.inst().getFeedbackQuestion(fs.feedbackSessionName, fs.courseId, 1);

        customParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
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
        r = (ActionResult) a.executeAndPostProcess();

        assertFalse(r.isError);

        ______TS("Private case, empty participant list - data bundle params");

        String instructor1OfCourse2 = dataBundle.instructors.get("instructor1OfCourse2").googleId;

        gaeSimulation.logoutUser(); // log out of instructor 1
        gaeSimulation.loginAsInstructor(instructor1OfCourse2);

        fs = dataBundle.feedbackSessions.get("session1InCourse2");
        fq = FeedbackQuestionsLogic.inst().getFeedbackQuestion(fs.feedbackSessionName, fs.courseId, 1);

        String[] privateParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
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

        a = getAction(privateParams);
        r = (ActionResult) a.executeAndPostProcess();

        assertFalse(r.isError);

        ______TS("Private case, null participant list - constructed params");

        privateParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
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

        a = getAction(privateParams);
        r = (ActionResult) a.executeAndPostProcess();

        assertFalse(r.isError);
    }

    private InstructorFeedbackQuestionVisibilityMessageAction getAction(String... params) throws Exception {
        return (InstructorFeedbackQuestionVisibilityMessageAction) (gaeSimulation.getActionObject(uri, params));
    }
}