package teammates.test.cases.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorFeedbackQuestionAddAction;
import teammates.ui.controller.RedirectResult;

/**
 * SUT: {@link InstructorFeedbackQuestionAddAction}.
 */
public class InstructorFeedbackQuestionAddActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_ADD;
    }

    @AfterClass
    public void classTearDown() {
        // delete entire session to clean the database
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");
        FeedbackSessionsLogic.inst().deleteFeedbackSessionCascade(fs.getFeedbackSessionName(), fs.getCourseId());
    }

    @Test
    public void testExecuteAndPostProcessMsq() {
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);

        ______TS("Typical case");

        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "1",
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "MSQ",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "What do you like best about the class?",
                Const.ParamsNames.FEEDBACK_QUESTION_DESCRIPTION, "more details",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, "5",
                Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-0", "The Content",
                // This option is deleted during creation, don't pass parameter
                // Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-1", "The Teacher",
                Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-2", "", // empty option
                // empty option with extra whitespace
                Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-3", "          ",
                Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-4", "The Atmosphere",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "custom",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
                Const.ParamsNames.FEEDBACK_QUESTION_MSQ_GENERATED_OPTIONS, FeedbackParticipantType.NONE.toString()

        };

        InstructorFeedbackQuestionAddAction action = getAction(params);
        RedirectResult result = getRedirectResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE,
                        instructor1ofCourse1.courseId,
                        "First+feedback+session",
                        instructor1ofCourse1.googleId,
                        false),
                result.getDestinationWithParams());

        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, result.getStatusMessage());

        String expectedLogMessage = "TEAMMATESLOG|||instructorFeedbackQuestionAdd"
                                    + "|||instructorFeedbackQuestionAdd|||true|||"
                                    + "Instructor|||Instructor 1 of Course 1|||"
                                    + "idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                                    + "Created Feedback Question for Feedback Session:<span class=\"bold\">"
                                    + "(First feedback session)</span> "
                                    + "for Course <span class=\"bold\">[idOfTypicalCourse1]</span>"
                                    + " created.<br><span class=\"bold\">Multiple-choice (multiple answers) "
                                    + "question:</span> What do you like best about the class?"
                                    + "|||/page/instructorFeedbackQuestionAdd";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());

        ______TS("Generated options");

        params = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "1",
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "MSQ",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "Who do you like in the class?",
                Const.ParamsNames.FEEDBACK_QUESTION_DESCRIPTION, "more details",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "custom",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
                Const.ParamsNames.FEEDBACK_QUESTION_MSQ_GENERATED_OPTIONS, FeedbackParticipantType.STUDENTS.toString()

        };

        action = getAction(params);
        result = getRedirectResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE,
                        instructor1ofCourse1.courseId,
                        "First+feedback+session",
                        instructor1ofCourse1.googleId,
                        false),
                result.getDestinationWithParams());

        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, result.getStatusMessage());

        expectedLogMessage = "TEAMMATESLOG|||instructorFeedbackQuestionAdd|||"
                             + "instructorFeedbackQuestionAdd|||true|||"
                             + "Instructor|||Instructor 1 of Course 1|||"
                             + "idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                             + "Created Feedback Question for Feedback Session:<span class=\"bold\">"
                             + "(First feedback session)</span> for Course "
                             + "<span class=\"bold\">[idOfTypicalCourse1]</span>"
                             + " created.<br><span class=\"bold\">Multiple-choice (multiple answers) question:</span> "
                             + "Who do you like in the class?|||/page/instructorFeedbackQuestionAdd";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());

        ______TS("Enable other option");

        params = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "3",
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "MSQ",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "Choose all the food you like",
                Const.ParamsNames.FEEDBACK_QUESTION_DESCRIPTION, "more details",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, "3",
                Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-0", "Pizza",
                Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-1", "Pasta",
                Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-2", "Chicken rice",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "custom",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
                Const.ParamsNames.FEEDBACK_QUESTION_MSQ_GENERATED_OPTIONS, FeedbackParticipantType.NONE.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_MSQOTHEROPTIONFLAG, "on"
        };

        action = getAction(params);
        result = getRedirectResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE,
                        instructor1ofCourse1.courseId,
                        "First+feedback+session",
                        instructor1ofCourse1.googleId,
                        false),
                result.getDestinationWithParams());

        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, result.getStatusMessage());

        expectedLogMessage = "TEAMMATESLOG|||instructorFeedbackQuestionAdd|||"
                             + "instructorFeedbackQuestionAdd|||true|||"
                             + "Instructor|||Instructor 1 of Course 1|||"
                             + "idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                             + "Created Feedback Question for Feedback Session:<span class=\"bold\">"
                             + "(First feedback session)</span> for Course "
                             + "<span class=\"bold\">[idOfTypicalCourse1]</span>"
                             + " created.<br><span class=\"bold\">Multiple-choice (multiple answers) question:</span> "
                             + "Choose all the food you like|||/page/instructorFeedbackQuestionAdd";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());
    }

    @Test
    public void testExecuteAndPostProcessMsqWeights() {
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");

        String[] requiredParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "MSQ",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "What do you like best about the class?",
                Const.ParamsNames.FEEDBACK_QUESTION_DESCRIPTION, "more details",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "custom",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
                Const.ParamsNames.FEEDBACK_QUESTION_MSQ_GENERATED_OPTIONS, FeedbackParticipantType.NONE.toString()
        };
        ______TS("MSQ options with weights assigned");

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "1",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, "3",
                Const.ParamsNames.FEEDBACK_QUESTION_MSQ_HAS_WEIGHTS_ASSIGNED, "on",
                Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-0", "The Content",
                Const.ParamsNames.FEEDBACK_QUESTION_MSQ_WEIGHT + "-0", "1",
                Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-1", "The Teacher",
                Const.ParamsNames.FEEDBACK_QUESTION_MSQ_WEIGHT + "-1", "2",
                Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-2", "The Team members",
                Const.ParamsNames.FEEDBACK_QUESTION_MSQ_WEIGHT + "-2", "3",
        };
        List<String> requestedParams = new ArrayList<>(Arrays.asList(requiredParams));
        Collections.addAll(requestedParams, params);
        InstructorFeedbackQuestionAddAction action = getAction(requestedParams.toArray(new String[0]));
        RedirectResult result = getRedirectResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE,
                        instructor1ofCourse1.courseId,
                        "First+feedback+session",
                        instructor1ofCourse1.googleId,
                        false),
                result.getDestinationWithParams());

        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, result.getStatusMessage());

        String expectedLogMessage = "TEAMMATESLOG|||instructorFeedbackQuestionAdd|||"
                + "instructorFeedbackQuestionAdd|||true|||"
                + "Instructor|||Instructor 1 of Course 1|||"
                + "idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                + "Created Feedback Question for Feedback Session:<span class=\"bold\">"
                + "(First feedback session)</span> for Course "
                + "<span class=\"bold\">[idOfTypicalCourse1]</span>"
                + " created.<br><span class=\"bold\">Multiple-choice (multiple answers) "
                + "question:</span> What do you like best about the class?"
                + "|||/page/instructorFeedbackQuestionAdd";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());

        ______TS("MSQ: Enabled other option with weights assigned");

        params = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_MSQ_HAS_WEIGHTS_ASSIGNED, "on",
                Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-0", "The content",
                Const.ParamsNames.FEEDBACK_QUESTION_MSQ_WEIGHT + "-0", "1",
                Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-1", "Teaching style",
                Const.ParamsNames.FEEDBACK_QUESTION_MSQ_WEIGHT + "-1", "2",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "custom",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_MSQOTHEROPTIONFLAG, "on",
                Const.ParamsNames.FEEDBACK_QUESTION_MSQ_OTHER_WEIGHT, "3"
        };
        requestedParams = new ArrayList<>(Arrays.asList(requiredParams));
        Collections.addAll(requestedParams, params);
        action = getAction(requestedParams.toArray(new String[0]));
        result = getRedirectResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE,
                        instructor1ofCourse1.courseId,
                        "First+feedback+session",
                        instructor1ofCourse1.googleId,
                        false),
                result.getDestinationWithParams());

        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, result.getStatusMessage());

        expectedLogMessage = "TEAMMATESLOG|||instructorFeedbackQuestionAdd|||"
                + "instructorFeedbackQuestionAdd|||true|||"
                + "Instructor|||Instructor 1 of Course 1|||"
                + "idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                + "Created Feedback Question for Feedback Session:<span class=\"bold\">"
                + "(First feedback session)</span> for Course "
                + "<span class=\"bold\">[idOfTypicalCourse1]</span>"
                + " created.<br><span class=\"bold\">Multiple-choice (multiple answers) question:</span> "
                + "What do you like best about the class?|||/page/instructorFeedbackQuestionAdd";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());

        ______TS("MSQ: Failure case: Number of choices is greater than number of corrosponding weights");

        params = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "3",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, "3",
                Const.ParamsNames.FEEDBACK_QUESTION_MSQ_HAS_WEIGHTS_ASSIGNED, "on",
                Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-0", "The Content",
                Const.ParamsNames.FEEDBACK_QUESTION_MSQ_WEIGHT + "-0", "1",
                Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-1", "The Teacher",
                Const.ParamsNames.FEEDBACK_QUESTION_MSQ_WEIGHT + "-1", "2",
                Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-2", "The Team members",
                // This weight is removed, so that choice-2 does not have a assigned weight.
                // Const.ParamsNames.FEEDBACK_QUESTION_MSQ_WEIGHT + "-2", "3",
        };
        requestedParams = new ArrayList<>(Arrays.asList(requiredParams));
        Collections.addAll(requestedParams, params);
        action = getAction(requestedParams.toArray(new String[0]));
        result = getRedirectResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE,
                        instructor1ofCourse1.courseId,
                        "First+feedback+session",
                        instructor1ofCourse1.googleId,
                        true),
                result.getDestinationWithParams());

        assertEquals(Const.FeedbackQuestion.MSQ_ERROR_INVALID_WEIGHT, result.getStatusMessage());

        expectedLogMessage = "TEAMMATESLOG|||instructorFeedbackQuestionAdd|||"
                + "instructorFeedbackQuestionAdd|||true|||"
                + "Instructor|||Instructor 1 of Course 1|||"
                + "idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                + "Unknown|||/page/instructorFeedbackQuestionAdd";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());
    }

    @Test
    public void testExecuteAndPostProcessMcq() {
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);

        ______TS("Typical case");

        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "1",
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "MCQ",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "What do you like best about the class?",
                Const.ParamsNames.FEEDBACK_QUESTION_DESCRIPTION, "more details",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, "5",
                Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-0", "The Content",
                // This option is deleted during creation, don't pass parameter
                // Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-1", "The Teacher",
                Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-2", "", // empty option
                // empty option with extra whitespace
                Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-3", "          ",
                Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-4", "The Atmosphere",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "custom",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
                Const.ParamsNames.FEEDBACK_QUESTION_MCQ_GENERATED_OPTIONS, FeedbackParticipantType.NONE.toString()
        };

        InstructorFeedbackQuestionAddAction action = getAction(params);
        RedirectResult result = getRedirectResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE,
                        instructor1ofCourse1.courseId,
                        "First+feedback+session",
                        instructor1ofCourse1.googleId,
                        false),
                result.getDestinationWithParams());

        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, result.getStatusMessage());

        String expectedLogMessage = "TEAMMATESLOG|||instructorFeedbackQuestionAdd|||"
                                    + "instructorFeedbackQuestionAdd|||true|||"
                                    + "Instructor|||Instructor 1 of Course 1|||"
                                    + "idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                                    + "Created Feedback Question for Feedback Session:<span class=\"bold\">"
                                    + "(First feedback session)</span> for Course "
                                    + "<span class=\"bold\">[idOfTypicalCourse1]</span>"
                                    + " created.<br><span class=\"bold\">Multiple-choice (single answer) "
                                    + "question:</span> What do you like best about the class?"
                                    + "|||/page/instructorFeedbackQuestionAdd";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());

        ______TS("Generated options");

        params = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "MCQ",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "Who do you like best in the class?",
                Const.ParamsNames.FEEDBACK_QUESTION_DESCRIPTION, "more details",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, "2", // this field defaults to 2
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "custom",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
                Const.ParamsNames.FEEDBACK_QUESTION_MCQ_GENERATED_OPTIONS, FeedbackParticipantType.STUDENTS.toString()
        };

        action = getAction(params);
        result = getRedirectResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE,
                        instructor1ofCourse1.courseId,
                        "First+feedback+session",
                        instructor1ofCourse1.googleId,
                        false),
                result.getDestinationWithParams());

        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, result.getStatusMessage());

        expectedLogMessage = "TEAMMATESLOG|||instructorFeedbackQuestionAdd|||"
                             + "instructorFeedbackQuestionAdd|||true|||"
                             + "Instructor|||Instructor 1 of Course 1|||"
                             + "idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                             + "Created Feedback Question for Feedback Session:<span class=\"bold\">"
                             + "(First feedback session)</span> for Course "
                             + "<span class=\"bold\">[idOfTypicalCourse1]</span>"
                             + " created.<br><span class=\"bold\">Multiple-choice (single answer) question:</span> "
                             + "Who do you like best in the class?|||/page/instructorFeedbackQuestionAdd";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());

        ______TS("Enable other option");

        params = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "3",
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "MCQ",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "What can be improved for this class?",
                Const.ParamsNames.FEEDBACK_QUESTION_DESCRIPTION, "more details",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, "4",
                Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-0", "The content",
                Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-1", "Teaching style",
                Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-2", "Tutorial questions",
                Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-3", "Assignments",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "custom",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
                Const.ParamsNames.FEEDBACK_QUESTION_MCQ_GENERATED_OPTIONS, FeedbackParticipantType.NONE.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_MCQOTHEROPTIONFLAG, "on"
        };

        action = getAction(params);
        result = getRedirectResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE,
                        instructor1ofCourse1.courseId,
                        "First+feedback+session",
                        instructor1ofCourse1.googleId,
                        false),
                result.getDestinationWithParams());

        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, result.getStatusMessage());

        expectedLogMessage = "TEAMMATESLOG|||instructorFeedbackQuestionAdd|||"
                             + "instructorFeedbackQuestionAdd|||true|||"
                             + "Instructor|||Instructor 1 of Course 1|||"
                             + "idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                             + "Created Feedback Question for Feedback Session:<span class=\"bold\">"
                             + "(First feedback session)</span> for Course "
                             + "<span class=\"bold\">[idOfTypicalCourse1]</span>"
                             + " created.<br><span class=\"bold\">Multiple-choice (single answer) question:</span> "
                             + "What can be improved for this class?|||/page/instructorFeedbackQuestionAdd";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());

    }

    @Test
    public void testExecuteAndPostProcessMcqWeights() {
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);
        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");

        String[] requiredParams = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "MCQ",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "What do you like best about the class?",
                Const.ParamsNames.FEEDBACK_QUESTION_DESCRIPTION, "more details",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "custom",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
                Const.ParamsNames.FEEDBACK_QUESTION_MCQ_GENERATED_OPTIONS, FeedbackParticipantType.NONE.toString()
        };
        ______TS("MCQ options with weights assigned");

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "1",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, "3",
                Const.ParamsNames.FEEDBACK_QUESTION_MCQ_HAS_WEIGHTS_ASSIGNED, "on",
                Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-0", "The Content",
                Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-0", "1",
                Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-1", "The Teacher",
                Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-1", "2",
                Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-2", "The Team members",
                Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-2", "0",
        };
        List<String> requestedParams = new ArrayList<>(Arrays.asList(requiredParams));
        Collections.addAll(requestedParams, params);
        InstructorFeedbackQuestionAddAction action = getAction(requestedParams.toArray(new String[0]));
        RedirectResult result = getRedirectResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE,
                        instructor1ofCourse1.courseId,
                        "First+feedback+session",
                        instructor1ofCourse1.googleId,
                        false),
                result.getDestinationWithParams());

        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, result.getStatusMessage());

        String expectedLogMessage = "TEAMMATESLOG|||instructorFeedbackQuestionAdd|||"
                                    + "instructorFeedbackQuestionAdd|||true|||"
                                    + "Instructor|||Instructor 1 of Course 1|||"
                                    + "idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                                    + "Created Feedback Question for Feedback Session:<span class=\"bold\">"
                                    + "(First feedback session)</span> for Course "
                                    + "<span class=\"bold\">[idOfTypicalCourse1]</span>"
                                    + " created.<br><span class=\"bold\">Multiple-choice (single answer) "
                                    + "question:</span> What do you like best about the class?"
                                    + "|||/page/instructorFeedbackQuestionAdd";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());

        ______TS("Enabled other option with weights assigned");

        params = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_MCQ_HAS_WEIGHTS_ASSIGNED, "on",
                Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-0", "The content",
                Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-0", "1",
                Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-1", "Teaching style",
                Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-1", "2",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "custom",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_MCQOTHEROPTIONFLAG, "on",
                Const.ParamsNames.FEEDBACK_QUESTION_MCQ_OTHER_WEIGHT, "3"
        };
        requestedParams = new ArrayList<>(Arrays.asList(requiredParams));
        Collections.addAll(requestedParams, params);
        action = getAction(requestedParams.toArray(new String[0]));
        result = getRedirectResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE,
                        instructor1ofCourse1.courseId,
                        "First+feedback+session",
                        instructor1ofCourse1.googleId,
                        false),
                result.getDestinationWithParams());

        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, result.getStatusMessage());

        expectedLogMessage = "TEAMMATESLOG|||instructorFeedbackQuestionAdd|||"
                             + "instructorFeedbackQuestionAdd|||true|||"
                             + "Instructor|||Instructor 1 of Course 1|||"
                             + "idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                             + "Created Feedback Question for Feedback Session:<span class=\"bold\">"
                             + "(First feedback session)</span> for Course "
                             + "<span class=\"bold\">[idOfTypicalCourse1]</span>"
                             + " created.<br><span class=\"bold\">Multiple-choice (single answer) question:</span> "
                             + "What do you like best about the class?|||/page/instructorFeedbackQuestionAdd";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());

        ______TS("Failure case: Number of choices is greater than number of corrosponding weights");

        params = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "3",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, "3",
                Const.ParamsNames.FEEDBACK_QUESTION_MCQ_HAS_WEIGHTS_ASSIGNED, "on",
                Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-0", "The Content",
                Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-0", "1",
                Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-1", "The Teacher",
                Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-1", "2",
                Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-2", "The Team members",
                // This weight is removed, so that choice-2 does not have a assigned weight.
                // Const.ParamsNames.FEEDBACK_QUESTION_MCQ_WEIGHT + "-2", "3",
        };
        requestedParams = new ArrayList<>(Arrays.asList(requiredParams));
        Collections.addAll(requestedParams, params);
        action = getAction(requestedParams.toArray(new String[0]));
        result = getRedirectResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE,
                        instructor1ofCourse1.courseId,
                        "First+feedback+session",
                        instructor1ofCourse1.googleId,
                        true),
                result.getDestinationWithParams());

        assertEquals(Const.FeedbackQuestion.MCQ_ERROR_INVALID_WEIGHT, result.getStatusMessage());

        expectedLogMessage = "TEAMMATESLOG|||instructorFeedbackQuestionAdd|||"
                                    + "instructorFeedbackQuestionAdd|||true|||"
                                    + "Instructor|||Instructor 1 of Course 1|||"
                                    + "idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                                    + "Unknown|||/page/instructorFeedbackQuestionAdd";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());
    }

    @Test
    public void testExecuteAndPostProcessNumScale() {
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);

        ______TS("Typical case");

        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "1",
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "NUMSCALE",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "Rate the class?",
                Const.ParamsNames.FEEDBACK_QUESTION_DESCRIPTION, "more details",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MIN, "1",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MAX, "5",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_STEP, "0.5",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "custom",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
        };

        InstructorFeedbackQuestionAddAction action = getAction(params);
        RedirectResult result = getRedirectResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE,
                        instructor1ofCourse1.courseId,
                        "First+feedback+session",
                        instructor1ofCourse1.googleId,
                        false),
                result.getDestinationWithParams());

        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, result.getStatusMessage());

        String expectedLogMessage = "TEAMMATESLOG|||instructorFeedbackQuestionAdd|||"
                                    + "instructorFeedbackQuestionAdd|||true|||"
                                    + "Instructor|||Instructor 1 of Course 1|||"
                                    + "idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                                    + "Created Feedback Question for Feedback Session:<span class=\"bold\">"
                                    + "(First feedback session)</span> for Course "
                                    + "<span class=\"bold\">[idOfTypicalCourse1]</span>"
                                    + " created.<br><span class=\"bold\">Numerical-scale question:</span> "
                                    + "Rate the class?|||/page/instructorFeedbackQuestionAdd";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());
    }

    @Test
    public void testExecuteAndPostProcessConstSumOption() {
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);

        ______TS("Typical case");

        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, FeedbackParticipantType.SELF.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "1",
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "CONSTSUM",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "Split points among the options.",
                Const.ParamsNames.FEEDBACK_QUESTION_DESCRIPTION, "more details",
                Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS, "30",
                Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHOPTION, "100",
                Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHRECIPIENT, "50",
                Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION, "true",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, "3",
                Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMOPTION + "-1", "Option 1",
                Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMOPTION + "-2", "Option 2",
                Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMOPTION + "-3", "Option 3",
                Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS, "false",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "max",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES, "1",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
        };

        InstructorFeedbackQuestionAddAction action = getAction(params);
        RedirectResult result = getRedirectResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE,
                        instructor1ofCourse1.courseId,
                        "First+feedback+session",
                        instructor1ofCourse1.googleId,
                        false),
                result.getDestinationWithParams());

        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, result.getStatusMessage());

        String expectedLogMessage = "TEAMMATESLOG|||instructorFeedbackQuestionAdd|||"
                                    + "instructorFeedbackQuestionAdd|||true|||"
                                    + "Instructor|||Instructor 1 of Course 1|||"
                                    + "idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                                    + "Created Feedback Question for Feedback Session:<span class=\"bold\">"
                                    + "(First feedback session)</span> for Course "
                                    + "<span class=\"bold\">[idOfTypicalCourse1]</span>"
                                    + " created.<br><span class=\"bold\">Distribute points (among options) "
                                    + "question:</span> Split points among the options."
                                    + "|||/page/instructorFeedbackQuestionAdd";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());
    }

    @Test
    public void testExecuteAndPostProcessConstSumRecipient() {
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);

        ______TS("Typical case");

        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "1",
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "CONSTSUM",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "Split points among students.",
                Const.ParamsNames.FEEDBACK_QUESTION_DESCRIPTION, "more details",
                Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS, "30",
                Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHOPTION, "50",
                Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHRECIPIENT, "100",
                Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION, "true",
                Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS, "true",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, "2", // default value.
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "custom",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
        };

        InstructorFeedbackQuestionAddAction action = getAction(params);
        RedirectResult result = getRedirectResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE,
                        instructor1ofCourse1.courseId,
                        "First+feedback+session",
                        instructor1ofCourse1.googleId,
                        false),
                result.getDestinationWithParams());

        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, result.getStatusMessage());

        String expectedLogMessage = "TEAMMATESLOG|||instructorFeedbackQuestionAdd|||"
                                    + "instructorFeedbackQuestionAdd|||true|||"
                                    + "Instructor|||Instructor 1 of Course 1|||"
                                    + "idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                                    + "Created Feedback Question for Feedback Session:<span class=\"bold\">"
                                    + "(First feedback session)</span> for Course "
                                    + "<span class=\"bold\">[idOfTypicalCourse1]</span>"
                                    + " created.<br><span class=\"bold\">Distribute points "
                                    + "(among recipients) question:</span> Split points among students."
                                    + "|||/page/instructorFeedbackQuestionAdd";

        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());
    }

    @Test
    public void testExecuteAndPostProcessContributionQuestion() {
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);

        ______TS("Typical case");

        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");

        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE,
                FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "1",
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "CONTRIB",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT,
                "How much has each team member including yourself, contributed to the project?",
                Const.ParamsNames.FEEDBACK_QUESTION_DESCRIPTION, "more details",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "max",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES, "1",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
        };

        InstructorFeedbackQuestionAddAction action = getAction(params);
        RedirectResult result = getRedirectResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE,
                        instructor1ofCourse1.courseId,
                        "First+feedback+session",
                        instructor1ofCourse1.googleId,
                        false),
                result.getDestinationWithParams());

        String expectedLogMessage = "TEAMMATESLOG|||instructorFeedbackQuestionAdd|||"
                                    + "instructorFeedbackQuestionAdd|||true|||"
                                    + "Instructor|||Instructor 1 of Course 1|||"
                                    + "idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                                    + "Created Feedback Question for Feedback Session:<span class=\"bold\">"
                                    + "(First feedback session)</span> for Course "
                                    + "<span class=\"bold\">[idOfTypicalCourse1]</span>"
                                    + " created.<br><span class=\"bold\">Team contribution question:</span> "
                                    + "How much has each team member including yourself, contributed to the project?"
                                    + "|||/page/instructorFeedbackQuestionAdd";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());

        ______TS("Invalid giver case");

        params = new String[] {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE,
                FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "1",
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "CONTRIB",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT,
                "How much has each team member including yourself, contributed to the project?",
                Const.ParamsNames.FEEDBACK_QUESTION_DESCRIPTION, "more details",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "max",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES, "1",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
        };

        action = getAction(params);
        result = getRedirectResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE,
                        instructor1ofCourse1.courseId,
                        "First+feedback+session",
                        instructor1ofCourse1.googleId,
                        true),
                result.getDestinationWithParams());

        expectedLogMessage = "TEAMMATESLOG|||instructorFeedbackQuestionAdd|||"
                             + "instructorFeedbackQuestionAdd|||true|||"
                             + "Instructor|||Instructor 1 of Course 1|||"
                             + "idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                             + "Created Feedback Question for Feedback Session:<span class=\"bold\">"
                             + "(First feedback session)</span> for Course "
                             + "<span class=\"bold\">[idOfTypicalCourse1]</span>"
                             + " created.<br><span class=\"bold\">Team contribution question:</span> "
                             + "How much has each team member including yourself, contributed to the project?"
                             + "|||/page/instructorFeedbackQuestionAdd";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());

        assertEquals(Const.FeedbackQuestion.CONTRIB_ERROR_INVALID_FEEDBACK_PATH
                     + "<br>" + Const.StatusMessages.FEEDBACK_QUESTION_ADDED,
                     result.getStatusMessage());
    }

    @Test
    public void testExecuteAndPostProcessRubricQuestion() {
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);

        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");
        String[] requiredParams = {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName(),
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE,
                FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "RUBRIC",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT,
                "Please choose the most appropriate choices for the sub-questions below.",
                Const.ParamsNames.FEEDBACK_QUESTION_DESCRIPTION, "more details",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_COLS, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_NUM_ROWS, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_SUBQUESTION + "-0", "SubQn-1",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE + "-0", "Choice-1",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_SUBQUESTION + "-1", "SubQn-2",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_CHOICE + "-1", "Choice-2",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "max",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES, "1",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
        };

        ______TS("Typical case with attached weights");

        String[] params = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "1",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHTS_ASSIGNED, "on",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT + "-0", "-1",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT + "-1", "2",
        };

        List<String> requestedParams = new ArrayList<>(Arrays.asList(requiredParams));
        Collections.addAll(requestedParams, params);

        InstructorFeedbackQuestionAddAction action = getAction(requestedParams.toArray(new String[0]));
        RedirectResult result = getRedirectResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE,
                        instructor1ofCourse1.courseId,
                        "First+feedback+session",
                        instructor1ofCourse1.googleId,
                        false),
                result.getDestinationWithParams());

        String expectedLogMessage = "TEAMMATESLOG|||instructorFeedbackQuestionAdd|||"
                                    + "instructorFeedbackQuestionAdd|||true|||"
                                    + "Instructor|||Instructor 1 of Course 1|||"
                                    + "idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                                    + "Created Feedback Question for Feedback Session:<span class=\"bold\">"
                                    + "(First feedback session)</span> for Course "
                                    + "<span class=\"bold\">[idOfTypicalCourse1]</span>"
                                    + " created.<br><span class=\"bold\">Rubric question:</span> "
                                    + "Please choose the most appropriate choices for the sub-questions below."
                                    + "|||/page/instructorFeedbackQuestionAdd";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());

        ______TS("Rubric choices without weights attached");

        params = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "2",
        };

        requestedParams = new ArrayList<>(Arrays.asList(requiredParams));
        Collections.addAll(requestedParams, params);

        action = getAction(requestedParams.toArray(new String[0]));
        result = getRedirectResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE,
                        instructor1ofCourse1.courseId,
                        "First+feedback+session",
                        instructor1ofCourse1.googleId,
                        false),
                result.getDestinationWithParams());

        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, result.getStatusMessage());

        expectedLogMessage = "TEAMMATESLOG|||instructorFeedbackQuestionAdd|||"
                                    + "instructorFeedbackQuestionAdd|||true|||"
                                    + "Instructor|||Instructor 1 of Course 1|||"
                                    + "idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                                    + "Created Feedback Question for Feedback Session:<span class=\"bold\">"
                                    + "(First feedback session)</span> for Course "
                                    + "<span class=\"bold\">[idOfTypicalCourse1]</span>"
                                    + " created.<br><span class=\"bold\">Rubric question:</span> "
                                    + "Please choose the most appropriate choices for the sub-questions below."
                                    + "|||/page/instructorFeedbackQuestionAdd";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());

        ______TS("Number of weights is less than number of choices");

        params = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "3",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHTS_ASSIGNED, "on",
                // This weight is removed so that rubricChoice-0 does not have a attached weight
                // Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT + "-0", "-1",
                Const.ParamsNames.FEEDBACK_QUESTION_RUBRIC_WEIGHT + "-1", "2",
        };
        requestedParams = new ArrayList<>(Arrays.asList(requiredParams));
        Collections.addAll(requestedParams, params);

        action = getAction(requestedParams.toArray(new String[0]));
        result = getRedirectResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE,
                        instructor1ofCourse1.courseId,
                        "First+feedback+session",
                        instructor1ofCourse1.googleId,
                        true),
                result.getDestinationWithParams());

        assertEquals(Const.FeedbackQuestion.RUBRIC_ERROR_INVALID_WEIGHT, result.getStatusMessage());

        expectedLogMessage = "TEAMMATESLOG|||instructorFeedbackQuestionAdd|||"
                                    + "instructorFeedbackQuestionAdd|||true|||"
                                    + "Instructor|||Instructor 1 of Course 1|||"
                                    + "idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                                    + "Unknown|||/page/instructorFeedbackQuestionAdd";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);

        ______TS("Not enough parameters");

        verifyAssumptionFailure();

        FeedbackSessionAttributes fs = typicalBundle.feedbackSessions.get("session1InCourse1");

        String[] params = {
                Const.ParamsNames.COURSE_ID, fs.getCourseId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.getFeedbackSessionName()
        };

        verifyAssumptionFailure(params);

        ______TS("Empty questionText");

        params = createParamsForTypicalFeedbackQuestion(fs.getCourseId(), fs.getFeedbackSessionName());
        modifyParamValue(params, Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "");
        verifyAssumptionFailure(params);

        ______TS("Invalid questionNumber");

        params = createParamsForTypicalFeedbackQuestion(fs.getCourseId(), fs.getFeedbackSessionName());
        // change questionNumber to invalid number
        modifyParamValue(params, Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "0");
        verifyAssumptionFailure(params);

        params = createParamsForTypicalFeedbackQuestion(fs.getCourseId(), fs.getFeedbackSessionName());
        // change questionNumber to invalid number
        modifyParamValue(params, Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "-1");
        verifyAssumptionFailure(params);

        params = createParamsForTypicalFeedbackQuestion(fs.getCourseId(), fs.getFeedbackSessionName());
        // change questionNumber to invalid "number"
        modifyParamValue(params, Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "ABC");

        try {
            InstructorFeedbackQuestionAddAction c = getAction(params);
            c.executeAndPostProcess();
            signalFailureToDetectException();
        } catch (NumberFormatException e) {
            ignoreExpectedException();
        }

        ______TS("Non-existent Enumeration");

        params = createParamsForTypicalFeedbackQuestion(instructor1ofCourse1.courseId, fs.getFeedbackSessionName());
        modifyParamValue(params, Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, "NON_EXISTENT_ENUMERATION");

        try {
            InstructorFeedbackQuestionAddAction c = getAction(params);
            c.executeAndPostProcess();
            signalFailureToDetectException();
        } catch (IllegalArgumentException e) {
            ignoreExpectedException();
        }

        params = createParamsForTypicalFeedbackQuestion(instructor1ofCourse1.courseId, fs.getFeedbackSessionName());
        modifyParamValue(params, Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, "NON_EXISTENT_ENUMERATION");

        try {
            InstructorFeedbackQuestionAddAction c = getAction(params);
            c.executeAndPostProcess();
            signalFailureToDetectException();
        } catch (IllegalArgumentException e) {
            ignoreExpectedException();
        }

        params = createParamsForTypicalFeedbackQuestion(instructor1ofCourse1.courseId, fs.getFeedbackSessionName());
        modifyParamValue(params, Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "NON_EXISTENT_ENUMERATION");

        try {
            InstructorFeedbackQuestionAddAction c = getAction(params);
            c.executeAndPostProcess();
            signalFailureToDetectException();
        } catch (IllegalArgumentException e) {
            ignoreExpectedException();
        }

        ______TS("Typical case");

        params = createParamsForTypicalFeedbackQuestion(fs.getCourseId(), fs.getFeedbackSessionName());
        // change number of feedback to give to unlimited
        modifyParamValue(params, Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "max");
        InstructorFeedbackQuestionAddAction action = getAction(params);
        RedirectResult result = getRedirectResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE,
                        instructor1ofCourse1.courseId,
                        "First+feedback+session",
                        instructor1ofCourse1.googleId,
                        false),
                result.getDestinationWithParams());

        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, result.getStatusMessage());

        String expectedLogMessage = "TEAMMATESLOG|||instructorFeedbackQuestionAdd|||"
                                    + "instructorFeedbackQuestionAdd|||true|||"
                                    + "Instructor|||Instructor 1 of Course 1|||"
                                    + "idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                                    + "Created Feedback Question for Feedback Session:<span class=\"bold\">"
                                    + "(First feedback session)</span> for Course "
                                    + "<span class=\"bold\">[idOfTypicalCourse1]</span>"
                                    + " created.<br><span class=\"bold\">Essay question:</span> "
                                    + "question|||/page/instructorFeedbackQuestionAdd";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());

        ______TS("Custom number of students to give feedback to");

        params = createParamsForTypicalFeedbackQuestion(fs.getCourseId(), fs.getFeedbackSessionName());
        action = getAction(params);
        result = getRedirectResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE,
                        instructor1ofCourse1.courseId,
                        "First+feedback+session",
                        instructor1ofCourse1.googleId,
                        false),
                result.getDestinationWithParams());

        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, result.getStatusMessage());

        expectedLogMessage = "TEAMMATESLOG|||instructorFeedbackQuestionAdd|||"
                             + "instructorFeedbackQuestionAdd|||true|||"
                             + "Instructor|||Instructor 1 of Course 1|||"
                             + "idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                             + "Created Feedback Question for Feedback Session:<span class=\"bold\">"
                             + "(First feedback session)</span> for Course "
                             + "<span class=\"bold\">[idOfTypicalCourse1]</span>"
                             + " created.<br><span class=\"bold\">Essay question:</span> "
                             + "question|||/page/instructorFeedbackQuestionAdd";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());

        ______TS("Custom number of teams to give feedback to");

        params = createParamsForTypicalFeedbackQuestion(fs.getCourseId(), fs.getFeedbackSessionName());
        modifyParamValue(params, Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, "TEAMS");

        action = getAction(params);
        result = getRedirectResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE,
                        instructor1ofCourse1.courseId,
                        "First+feedback+session",
                        instructor1ofCourse1.googleId,
                        false),
                result.getDestinationWithParams());

        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, result.getStatusMessage());

        expectedLogMessage = "TEAMMATESLOG|||instructorFeedbackQuestionAdd|||"
                             + "instructorFeedbackQuestionAdd|||true|||"
                             + "Instructor|||Instructor 1 of Course 1|||"
                             + "idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                             + "Created Feedback Question for Feedback Session:<span class=\"bold\">"
                             + "(First feedback session)</span> for Course "
                             + "<span class=\"bold\">[idOfTypicalCourse1]</span>"
                             + " created.<br><span class=\"bold\">Essay question:</span> "
                             + "question|||/page/instructorFeedbackQuestionAdd";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());

        ______TS("Remnant custom number of entities when recipient is changed to non-student and non-team");

        params = createParamsForTypicalFeedbackQuestion(fs.getCourseId(), fs.getFeedbackSessionName());
        modifyParamValue(params, Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, "INSTRUCTORS");

        action = getAction(params);
        result = getRedirectResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE,
                        instructor1ofCourse1.courseId,
                        "First+feedback+session",
                        instructor1ofCourse1.googleId,
                        false),
                result.getDestinationWithParams());

        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, result.getStatusMessage());

        expectedLogMessage = "TEAMMATESLOG|||instructorFeedbackQuestionAdd|||"
                             + "instructorFeedbackQuestionAdd|||true|||"
                             + "Instructor|||Instructor 1 of Course 1|||"
                             + "idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                             + "Created Feedback Question for Feedback Session:<span class=\"bold\">"
                             + "(First feedback session)</span> for Course "
                             + "<span class=\"bold\">[idOfTypicalCourse1]</span>"
                             + " created.<br><span class=\"bold\">Essay question:</span> "
                             + "question|||/page/instructorFeedbackQuestionAdd";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());

        ______TS("Question text requires sanitization");

        params = createParamsForTypicalFeedbackQuestion(fs.getCourseId(), fs.getFeedbackSessionName());
        modifyParamValue(params, Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "attempted html injection '\"/>");

        action = getAction(params);
        result = getRedirectResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE,
                        instructor1ofCourse1.courseId,
                        "First+feedback+session",
                        instructor1ofCourse1.googleId,
                        false),
                result.getDestinationWithParams());

        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, result.getStatusMessage());

        expectedLogMessage = "TEAMMATESLOG|||instructorFeedbackQuestionAdd|||"
                + "instructorFeedbackQuestionAdd|||true|||"
                + "Instructor|||Instructor 1 of Course 1|||"
                + "idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                + "Created Feedback Question for Feedback Session:<span class=\"bold\">"
                + "(First feedback session)</span> for Course "
                + "<span class=\"bold\">[idOfTypicalCourse1]</span>"
                + " created.<br><span class=\"bold\">Essay question:</span> "
                + "attempted html injection &#39;&quot;&#x2f;&gt;|||/page/instructorFeedbackQuestionAdd";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());

        ______TS("Failure: Empty or null participant lists");

        params = createParamsForTypicalFeedbackQuestion(fs.getCourseId(), fs.getFeedbackSessionName());
        modifyParamValue(params, Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, "");

        // Purposely not using modifyParamVale because we're removing showRecipientTo
        params[22] = Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE;
        params[23] = "edit";

        params = Arrays.copyOf(params, 24);

        action = getAction(params);
        result = getRedirectResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE,
                        instructor1ofCourse1.courseId,
                        "First+feedback+session",
                        instructor1ofCourse1.googleId,
                        false),
                result.getDestinationWithParams());

        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, result.getStatusMessage());

        expectedLogMessage = "TEAMMATESLOG|||instructorFeedbackQuestionAdd|||"
                             + "instructorFeedbackQuestionAdd|||true|||"
                             + "Instructor|||Instructor 1 of Course 1|||"
                             + "idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                             + "Created Feedback Question for Feedback Session:<span class=\"bold\">"
                             + "(First feedback session)</span> for Course "
                             + "<span class=\"bold\">[idOfTypicalCourse1]</span>"
                             + " created.<br><span class=\"bold\">Essay question:</span> "
                             + "question|||/page/instructorFeedbackQuestionAdd";

        ______TS("Failure: Invalid Parameter");

        params = createParamsForTypicalFeedbackQuestion(instructor1ofCourse1.courseId, fs.getFeedbackSessionName());
        modifyParamValue(params, Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, "NONE");
        action = getAction(params);
        result = getRedirectResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE,
                        instructor1ofCourse1.courseId,
                        "First+feedback+session",
                        instructor1ofCourse1.googleId,
                        true),
                result.getDestinationWithParams());

        assertEquals("NONE is not a valid feedback giver.", result.getStatusMessage());

        expectedLogMessage =
                "TEAMMATESLOG|||instructorFeedbackQuestionAdd|||"
                + "instructorFeedbackQuestionAdd|||true|||"
                + "Instructor|||Instructor 1 of Course 1|||"
                + "idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE, "NONE", FieldValidator.GIVER_TYPE_NAME)
                + "|||/page/instructorFeedbackQuestionAdd";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, action.getLogMessage());

        ______TS("Masquerade mode");

        String adminUserId = "admin.user";
        gaeSimulation.loginAsAdmin(adminUserId);

        params = createParamsForTypicalFeedbackQuestion(instructor1ofCourse1.courseId, fs.getFeedbackSessionName());
        params = addUserIdToParams(instructor1ofCourse1.googleId, params);

        action = getAction(params);
        result = getRedirectResult(action);

        assertEquals(
                getPageResultDestination(
                        Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE,
                        instructor1ofCourse1.courseId,
                        "First+feedback+session",
                        instructor1ofCourse1.googleId,
                        false),
                result.getDestinationWithParams());

        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, result.getStatusMessage());

        expectedLogMessage = "TEAMMATESLOG|||instructorFeedbackQuestionAdd|||"
                             + "instructorFeedbackQuestionAdd|||true|||"
                             + "Instructor(M)|||Instructor 1 of Course 1|||"
                             + "idOfInstructor1OfCourse1|||instr1@course1.tmt|||"
                             + "Created Feedback Question for Feedback Session:<span class=\"bold\">"
                             + "(First feedback session)</span> for Course "
                             + "<span class=\"bold\">[idOfTypicalCourse1]</span>"
                             + " created.<br><span class=\"bold\">Essay question:</span> "
                             + "question|||/page/instructorFeedbackQuestionAdd";
        AssertHelper.assertLogMessageEqualsInMasqueradeMode(expectedLogMessage, action.getLogMessage(), adminUserId);
    }

    @Override
    protected InstructorFeedbackQuestionAddAction getAction(String... params) {
        return (InstructorFeedbackQuestionAddAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    protected String getPageResultDestination(
            String parentUri, String courseId, String fsname, String userId, boolean isError) {
        String pageDestination = parentUri;
        pageDestination = addParamToUrl(pageDestination, Const.ParamsNames.COURSE_ID, courseId);
        pageDestination = addParamToUrl(pageDestination, Const.ParamsNames.FEEDBACK_SESSION_NAME, fsname);
        pageDestination = addParamToUrl(pageDestination, Const.ParamsNames.USER_ID, userId);
        pageDestination = addParamToUrl(pageDestination, Const.ParamsNames.ERROR, Boolean.toString(isError));
        return pageDestination;
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        FeedbackSessionAttributes fs =
                typicalBundle.feedbackSessions.get("empty.session");

        String[] submissionParams =
                createParamsForTypicalFeedbackQuestion(fs.getCourseId(), fs.getFeedbackSessionName());
        // set question number to be the last
        submissionParams[9] = "5";
        verifyUnaccessibleWithoutModifySessionPrivilege(submissionParams);
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);

        // remove the session as removing questions is difficult
        FeedbackSessionsLogic.inst().deleteFeedbackSessionCascade(fs.getFeedbackSessionName(), fs.getCourseId());
    }
}
