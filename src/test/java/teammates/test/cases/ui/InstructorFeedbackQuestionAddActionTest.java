package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import java.util.Arrays;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.ui.controller.Action;
import teammates.ui.controller.InstructorFeedbackQuestionAddAction;
import teammates.ui.controller.RedirectResult;

public class InstructorFeedbackQuestionAddActionTest extends BaseActionTest {

    private static final DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
		removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_ADD;
    }
    
    @AfterClass
    public static void classTearDown() {
        // delete entire session to clean the database
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
        FeedbackSessionsLogic.inst().deleteFeedbackSessionCascade(fs.feedbackSessionName, fs.courseId);
    }
    
    @Test
    public void testExecuteAndPostProcessMsq() throws Exception{
        InstructorAttributes instructor1ofCourse1 =
                dataBundle.instructors.get("instructor1OfCourse1");

        gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);
                
        ______TS("Typical case");

        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
        String[] params = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "1",
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "MSQ",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "What do you like best about the class?",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, "5",
                Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-0", "The Content",
                //Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-1", "The Teacher",   // This option is deleted during creation, don't pass parameter
                Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-2", "", // empty option
                Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-3", "          ", // empty option with extra whitespace
                Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE + "-4", "The Atmosphere",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "custom",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
                Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS, FeedbackParticipantType.NONE.toString()
        };
        
        InstructorFeedbackQuestionAddAction action = getAction(params);
        RedirectResult result = (RedirectResult) action.executeAndPostProcess();
        
        assertEquals(
                Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE
                        + "?courseid="
                        + instructor1ofCourse1.courseId
                        + "&fsname=First+feedback+session"
                        + "&user="
                        + instructor1ofCourse1.googleId
                        + "&error=false",
                result.getDestinationWithParams());
        
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, result.getStatusMessage());

        String expectedLogMessage =
                "TEAMMATESLOG|||instructorFeedbackQuestionAdd|||instructorFeedbackQuestionAdd|||true|||"
                        + "Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.com|||"
                        + "Created Feedback Question for Feedback Session:<span class=\"bold\">"
                        + "(First feedback session)</span> for Course <span class=\"bold\">[idOfTypicalCourse1]</span>"
                        + " created.<br><span class=\"bold\">Multiple-choice (multiple answers) question:</span> What do you like best about the class?"
                        + "|||/page/instructorFeedbackQuestionAdd";
        assertEquals(expectedLogMessage, action.getLogMessage());
        
        ______TS("Generated options");

        params = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "1",
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "MSQ",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "Who do you like in the class?",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "custom",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
                Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS, FeedbackParticipantType.STUDENTS.toString()
        };
        
        action = getAction(params);
        result = (RedirectResult) action.executeAndPostProcess();
        
        assertEquals(
                Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE
                        + "?courseid="
                        + instructor1ofCourse1.courseId
                        + "&fsname=First+feedback+session"
                        + "&user="
                        + instructor1ofCourse1.googleId
                        + "&error=false",
                result.getDestinationWithParams());
        
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, result.getStatusMessage());

        expectedLogMessage =
                "TEAMMATESLOG|||instructorFeedbackQuestionAdd|||instructorFeedbackQuestionAdd|||true|||"
                        + "Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.com|||"
                        + "Created Feedback Question for Feedback Session:<span class=\"bold\">"
                        + "(First feedback session)</span> for Course <span class=\"bold\">[idOfTypicalCourse1]</span>"
                        + " created.<br><span class=\"bold\">Multiple-choice (multiple answers) question:</span> Who do you like in the class?"
                        + "|||/page/instructorFeedbackQuestionAdd";
        assertEquals(expectedLogMessage, action.getLogMessage());
    }

    @Test
    public void testExecuteAndPostProcessMcq() throws Exception{
        InstructorAttributes instructor1ofCourse1 =
                dataBundle.instructors.get("instructor1OfCourse1");

        gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);
                
        ______TS("Typical case");

        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
        String[] params = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "1",
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "MCQ",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "What do you like best about the class?",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, "5",
                Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-0", "The Content",
                //Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-1", "The Teacher",   // This option is deleted during creation, don't pass parameter
                Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-2", "", // empty option
                Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-3", "          ", // empty option with extra whitespace
                Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-4", "The Atmosphere",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "custom",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
                Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS, FeedbackParticipantType.NONE.toString()
        };
        
        InstructorFeedbackQuestionAddAction action = getAction(params);
        RedirectResult result = (RedirectResult) action.executeAndPostProcess();
        
        assertEquals(
                Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE
                        + "?courseid="
                        + instructor1ofCourse1.courseId
                        + "&fsname=First+feedback+session"
                        + "&user="
                        + instructor1ofCourse1.googleId
                        + "&error=false",
                result.getDestinationWithParams());
        
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, result.getStatusMessage());

        String expectedLogMessage =
                "TEAMMATESLOG|||instructorFeedbackQuestionAdd|||instructorFeedbackQuestionAdd|||true|||"
                        + "Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.com|||"
                        + "Created Feedback Question for Feedback Session:<span class=\"bold\">"
                        + "(First feedback session)</span> for Course <span class=\"bold\">[idOfTypicalCourse1]</span>"
                        + " created.<br><span class=\"bold\">Multiple-choice (single answer) question:</span> What do you like best about the class?"
                        + "|||/page/instructorFeedbackQuestionAdd";
        assertEquals(expectedLogMessage, action.getLogMessage());
        
        ______TS("Generated options");

        params = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "MCQ",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "Who do you like best in the class?",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, "2", // this field defaults to 2
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "custom",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
                Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS, FeedbackParticipantType.STUDENTS.toString()
        };
        
        action = getAction(params);
        result = (RedirectResult) action.executeAndPostProcess();
        
        assertEquals(
                Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE
                        + "?courseid="
                        + instructor1ofCourse1.courseId
                        + "&fsname=First+feedback+session"
                        + "&user="
                        + instructor1ofCourse1.googleId
                        + "&error=false",
                result.getDestinationWithParams());
        
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, result.getStatusMessage());

        expectedLogMessage =
                "TEAMMATESLOG|||instructorFeedbackQuestionAdd|||instructorFeedbackQuestionAdd|||true|||"
                        + "Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.com|||"
                        + "Created Feedback Question for Feedback Session:<span class=\"bold\">"
                        + "(First feedback session)</span> for Course <span class=\"bold\">[idOfTypicalCourse1]</span>"
                        + " created.<br><span class=\"bold\">Multiple-choice (single answer) question:</span> Who do you like best in the class?"
                        + "|||/page/instructorFeedbackQuestionAdd";
        assertEquals(expectedLogMessage, action.getLogMessage());
    }

    @Test
    public void testExecuteAndPostProcessNumScale() throws Exception{
        InstructorAttributes instructor1ofCourse1 =
                dataBundle.instructors.get("instructor1OfCourse1");

        gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);
                
        ______TS("Typical case");

        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
        String[] params = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "1",
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "NUMSCALE",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "Rate the class?",
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
        RedirectResult result = (RedirectResult) action.executeAndPostProcess();
        
        assertEquals(
                Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE
                        + "?courseid="
                        + instructor1ofCourse1.courseId
                        + "&fsname=First+feedback+session"
                        + "&user="
                        + instructor1ofCourse1.googleId
                        + "&error=false",
                result.getDestinationWithParams());
        
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, result.getStatusMessage());
        
        String expectedLogMessage =
                "TEAMMATESLOG|||instructorFeedbackQuestionAdd|||instructorFeedbackQuestionAdd|||true|||"
                        + "Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.com|||"
                        + "Created Feedback Question for Feedback Session:<span class=\"bold\">"
                        + "(First feedback session)</span> for Course <span class=\"bold\">[idOfTypicalCourse1]</span>"
                        + " created.<br><span class=\"bold\">Numerical-scale question:</span> Rate the class?"
                        + "|||/page/instructorFeedbackQuestionAdd";
        assertEquals(expectedLogMessage, action.getLogMessage());
    }
    
    @Test
    public void testExecuteAndPostProcessConstSumOption() throws Exception{
        InstructorAttributes instructor1ofCourse1 =
                dataBundle.instructors.get("instructor1OfCourse1");

        gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);
                
        ______TS("Typical case");

        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
        String[] params = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, FeedbackParticipantType.SELF.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "1",
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "CONSTSUM",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "Split points among the options.",
                Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS, "100",
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
        RedirectResult result = (RedirectResult) action.executeAndPostProcess();
        
        assertEquals(
                Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE
                        + "?courseid="
                        + instructor1ofCourse1.courseId
                        + "&fsname=First+feedback+session"
                        + "&user="
                        + instructor1ofCourse1.googleId
                        + "&error=false",
                result.getDestinationWithParams());
        
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, result.getStatusMessage());
        
        String expectedLogMessage =
                "TEAMMATESLOG|||instructorFeedbackQuestionAdd|||instructorFeedbackQuestionAdd|||true|||"
                        + "Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.com|||"
                        + "Created Feedback Question for Feedback Session:<span class=\"bold\">"
                        + "(First feedback session)</span> for Course <span class=\"bold\">[idOfTypicalCourse1]</span>"
                        + " created.<br><span class=\"bold\">Distribute points (among options) question:</span> Split points among the options."
                        + "|||/page/instructorFeedbackQuestionAdd";
        assertEquals(expectedLogMessage, action.getLogMessage());
    }
    
    @Test
    public void testExecuteAndPostProcessConstSumRecipient() throws Exception{
        InstructorAttributes instructor1ofCourse1 =
                dataBundle.instructors.get("instructor1OfCourse1");

        gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);
                
        ______TS("Typical case");

        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
        String[] params = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "1",
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "CONSTSUM",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "Split points among students.",
                Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTS, "100",
                Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION, "true",
                Const.ParamsNames.FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS, "true",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED, "2",//default value.
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "custom",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES, "2",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.RECEIVER.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
        };
        
        InstructorFeedbackQuestionAddAction action = getAction(params);
        RedirectResult result = (RedirectResult) action.executeAndPostProcess();
        
        assertEquals(
                Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE
                        + "?courseid="
                        + instructor1ofCourse1.courseId
                        + "&fsname=First+feedback+session"
                        + "&user="
                        + instructor1ofCourse1.googleId
                        + "&error=false",
                result.getDestinationWithParams());
        
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, result.getStatusMessage());

        String expectedLogMessage =
                "TEAMMATESLOG|||instructorFeedbackQuestionAdd|||instructorFeedbackQuestionAdd|||true|||"
                        + "Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.com|||"
                        + "Created Feedback Question for Feedback Session:<span class=\"bold\">"
                        + "(First feedback session)</span> for Course <span class=\"bold\">[idOfTypicalCourse1]</span>"
                        + " created.<br><span class=\"bold\">Distribute points (among recipients) question:</span> Split points among students."
                        + "|||/page/instructorFeedbackQuestionAdd";
        assertEquals(expectedLogMessage, action.getLogMessage());
    }
    
    @Test
    public void testExecuteAndPostProcessContributionQuestion() throws Exception{
        InstructorAttributes instructor1ofCourse1 =
                dataBundle.instructors.get("instructor1OfCourse1");

        gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);
                
        ______TS("Typical case");

        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
        String[] params = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, FeedbackParticipantType.STUDENTS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "1",
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "CONTRIB",
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "How much has each team member including yourself, contributed to the project?",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "max",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIES, "1",
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRESPONSESTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWGIVERTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_SHOWRECIPIENTTO, FeedbackParticipantType.INSTRUCTORS.toString(),
                Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE, "edit",
        };
        
        InstructorFeedbackQuestionAddAction action = getAction(params);
        RedirectResult result = (RedirectResult) action.executeAndPostProcess();
        
        assertEquals(
                Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE
                        + "?courseid="
                        + instructor1ofCourse1.courseId
                        + "&fsname=First+feedback+session"
                        + "&user="
                        + instructor1ofCourse1.googleId
                        + "&error=false",
                result.getDestinationWithParams());

        String expectedLogMessage =
                "TEAMMATESLOG|||instructorFeedbackQuestionAdd|||instructorFeedbackQuestionAdd|||true|||"
                        + "Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.com|||"
                        + "Created Feedback Question for Feedback Session:<span class=\"bold\">"
                        + "(First feedback session)</span> for Course <span class=\"bold\">[idOfTypicalCourse1]</span>"
                        + " created.<br><span class=\"bold\">Team contribution question:</span> How much has each team member including yourself, contributed to the project?"
                        + "|||/page/instructorFeedbackQuestionAdd";
        assertEquals(expectedLogMessage, action.getLogMessage());
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception{
        InstructorAttributes instructor1ofCourse1 =
                dataBundle.instructors.get("instructor1OfCourse1");

        gaeSimulation.loginAsInstructor(instructor1ofCourse1.googleId);

        ______TS("Not enough parameters");

        verifyAssumptionFailure();
        
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
        String[] params = { Const.ParamsNames.COURSE_ID, fs.courseId,
                            Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName};
        verifyAssumptionFailure(params);

        ______TS("Empty questionText");
        
        params = createParamsForTypicalFeedbackQuestion(fs.courseId, fs.feedbackSessionName);
        //params[13] = ""; //change questionText to empty string
        modifyParamValue(params, Const.ParamsNames.FEEDBACK_QUESTION_TEXT, "");
        verifyAssumptionFailure(params);
        
        ______TS("Invalid questionNumber");
        
        params = createParamsForTypicalFeedbackQuestion(fs.courseId, fs.feedbackSessionName);
        modifyParamValue(params, Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "0");//change questionNumber to invalid number
        verifyAssumptionFailure(params);
        
        params = createParamsForTypicalFeedbackQuestion(fs.courseId, fs.feedbackSessionName);
        modifyParamValue(params, Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "-1");//change questionNumber to invalid number
        verifyAssumptionFailure(params);
        
        params = createParamsForTypicalFeedbackQuestion(fs.courseId, fs.feedbackSessionName);
        modifyParamValue(params, Const.ParamsNames.FEEDBACK_QUESTION_NUMBER, "ABC");//change questionNumber to invalid number
        try {
            Action c = gaeSimulation.getActionObject(uri, params);
            c.executeAndPostProcess();
            signalFailureToDetectException();
        } catch (NumberFormatException e) {
            ignoreExpectedException();
        }
        
        ______TS("Non-existent Enumeration");

        params = createParamsForTypicalFeedbackQuestion(instructor1ofCourse1.courseId,  fs.feedbackSessionName);
        modifyParamValue(params, Const.ParamsNames.FEEDBACK_QUESTION_GIVERTYPE, "NON_EXISTENT_ENUMERATION");
        try {
            Action c = gaeSimulation.getActionObject(uri, params);
            c.executeAndPostProcess();
            signalFailureToDetectException();
        } catch (IllegalArgumentException e) {
            ignoreExpectedException();
        }
        
        params = createParamsForTypicalFeedbackQuestion(instructor1ofCourse1.courseId,  fs.feedbackSessionName);
        modifyParamValue(params, Const.ParamsNames.FEEDBACK_QUESTION_RECIPIENTTYPE, "NON_EXISTENT_ENUMERATION");
        try {
            Action c = gaeSimulation.getActionObject(uri, params);
            c.executeAndPostProcess();
            signalFailureToDetectException();
        } catch (IllegalArgumentException e) {
            ignoreExpectedException();
        }
        
        params = createParamsForTypicalFeedbackQuestion(instructor1ofCourse1.courseId,  fs.feedbackSessionName);
        modifyParamValue(params, Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "NON_EXISTENT_ENUMERATION");
        try {
            Action c = gaeSimulation.getActionObject(uri, params);
            c.executeAndPostProcess();
            signalFailureToDetectException();
        } catch (IllegalArgumentException e) {
            ignoreExpectedException();
        }
        
        ______TS("Typical case");

        params = createParamsForTypicalFeedbackQuestion(fs.courseId, fs.feedbackSessionName);
        modifyParamValue(params, Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE, "max");//change number of feedback to give to unlimited
        InstructorFeedbackQuestionAddAction action = getAction(params);
        RedirectResult result = (RedirectResult) action.executeAndPostProcess();
        
        assertEquals(
                Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE
                        + "?courseid="
                        + instructor1ofCourse1.courseId
                        + "&fsname=First+feedback+session"
                        + "&user="
                        + instructor1ofCourse1.googleId
                        + "&error=false",
                result.getDestinationWithParams());
        
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, result.getStatusMessage());

        String expectedLogMessage =
                "TEAMMATESLOG|||instructorFeedbackQuestionAdd|||instructorFeedbackQuestionAdd|||true|||"
                        + "Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.com|||"
                        + "Created Feedback Question for Feedback Session:<span class=\"bold\">"
                        + "(First feedback session)</span> for Course <span class=\"bold\">[idOfTypicalCourse1]</span>"
                        + " created.<br><span class=\"bold\">Essay question:</span> question|||/page/instructorFeedbackQuestionAdd";
        assertEquals(expectedLogMessage, action.getLogMessage());
        
        ______TS("Custom number of students to give feedback to");

        params = createParamsForTypicalFeedbackQuestion(fs.courseId, fs.feedbackSessionName);
        action = getAction(params);
        result = (RedirectResult) action.executeAndPostProcess();
        
        assertEquals(
                Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE
                        + "?courseid="
                        + instructor1ofCourse1.courseId
                        + "&fsname=First+feedback+session"
                        + "&user="
                        + instructor1ofCourse1.googleId
                        + "&error=false",
                result.getDestinationWithParams());
        
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, result.getStatusMessage());

        expectedLogMessage =
                "TEAMMATESLOG|||instructorFeedbackQuestionAdd|||instructorFeedbackQuestionAdd|||true|||"
                        + "Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.com|||"
                        + "Created Feedback Question for Feedback Session:<span class=\"bold\">"
                        + "(First feedback session)</span> for Course <span class=\"bold\">[idOfTypicalCourse1]</span>"
                        + " created.<br><span class=\"bold\">Essay question:</span> question|||/page/instructorFeedbackQuestionAdd";
        assertEquals(expectedLogMessage, action.getLogMessage());
        
        ______TS("Custom number of teams to give feedback to");

        params = createParamsForTypicalFeedbackQuestion(fs.courseId, fs.feedbackSessionName);
        params[7] = "TEAMS"; //change recipientType to TEAMS
        
        action = getAction(params);
        result = (RedirectResult) action.executeAndPostProcess();
        
        assertEquals(
                Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE
                        + "?courseid="
                        + instructor1ofCourse1.courseId
                        + "&fsname=First+feedback+session"
                        + "&user="
                        + instructor1ofCourse1.googleId
                        + "&error=false",
                result.getDestinationWithParams());
        
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, result.getStatusMessage());
        
        expectedLogMessage =
                "TEAMMATESLOG|||instructorFeedbackQuestionAdd|||instructorFeedbackQuestionAdd|||true|||"
                        + "Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.com|||"
                        + "Created Feedback Question for Feedback Session:<span class=\"bold\">"
                        + "(First feedback session)</span> for Course <span class=\"bold\">[idOfTypicalCourse1]</span>"
                        + " created.<br><span class=\"bold\">Essay question:</span> question|||/page/instructorFeedbackQuestionAdd";
        assertEquals(expectedLogMessage, action.getLogMessage());
        
        ______TS("Remnant custom number of entities when recipient is changed to non-student and non-team");
        
        params = createParamsForTypicalFeedbackQuestion(fs.courseId, fs.feedbackSessionName);
        params[7] = "INSTRUCTORS"; //change recipientType to INSTRUCTORS
        action = getAction(params);
        result = (RedirectResult) action.executeAndPostProcess();
        
        assertEquals(
                Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE
                        + "?courseid="
                        + instructor1ofCourse1.courseId
                        + "&fsname=First+feedback+session"
                        + "&user="
                        + instructor1ofCourse1.googleId
                        + "&error=false",
                result.getDestinationWithParams());
        
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, result.getStatusMessage());
        
        expectedLogMessage =
                "TEAMMATESLOG|||instructorFeedbackQuestionAdd|||instructorFeedbackQuestionAdd|||true|||"
                        + "Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.com|||"
                        + "Created Feedback Question for Feedback Session:<span class=\"bold\">"
                        + "(First feedback session)</span> for Course <span class=\"bold\">[idOfTypicalCourse1]</span>"
                        + " created.<br><span class=\"bold\">Essay question:</span> question|||/page/instructorFeedbackQuestionAdd";
        assertEquals(expectedLogMessage, action.getLogMessage());
        
        ______TS("Failure: Empty or null participant lists");
        
        params = createParamsForTypicalFeedbackQuestion(fs.courseId, fs.feedbackSessionName);
        params[21] = ""; //change showGiverTo to empty
        
        // remove showRecipientTo
        params[22] = Const.ParamsNames.FEEDBACK_QUESTION_EDITTYPE;
        params[23] = "edit";
        params = Arrays.copyOf(params, 24);        
        
        action = getAction(params);
        result = (RedirectResult) action.executeAndPostProcess();
        
        assertEquals(
                Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE
                        + "?courseid="
                        + instructor1ofCourse1.courseId
                        + "&fsname=First+feedback+session"
                        + "&user="
                        + instructor1ofCourse1.googleId
                        + "&error=false",
                result.getDestinationWithParams());

        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, result.getStatusMessage());
        
        expectedLogMessage =
                "TEAMMATESLOG|||instructorFeedbackQuestionAdd|||instructorFeedbackQuestionAdd|||true|||"
                        + "Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.com|||"
                        + "Created Feedback Question for Feedback Session:<span class=\"bold\">"
                        + "(First feedback session)</span> for Course <span class=\"bold\">[idOfTypicalCourse1]</span>"
                        + " created.<br><span class=\"bold\">Essay question:</span> question|||/page/instructorFeedbackQuestionAdd";
        
        ______TS("Failure: Invalid Parameter");

        params = createParamsForTypicalFeedbackQuestion(instructor1ofCourse1.courseId,  fs.feedbackSessionName);
        params[5] = "NONE"; // Change giverType to NONE
        action = getAction(params);
        result = (RedirectResult) action.executeAndPostProcess();
        
        assertEquals(
                Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE
                        + "?courseid="
                        + instructor1ofCourse1.courseId
                        + "&fsname=First+feedback+session"
                        + "&user="
                        + instructor1ofCourse1.googleId
                        + "&error=true",
                result.getDestinationWithParams());
        
        assertEquals("NONE is not a valid feedback giver..", result.getStatusMessage());

        expectedLogMessage =
                "TEAMMATESLOG|||instructorFeedbackQuestionAdd|||instructorFeedbackQuestionAdd|||true|||"
                        + "Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.com|||"
                        + String.format(FieldValidator.PARTICIPANT_TYPE_ERROR_MESSAGE, "NONE", FieldValidator.GIVER_TYPE_NAME) 
                        + "|||/page/instructorFeedbackQuestionAdd";
        assertEquals(expectedLogMessage, action.getLogMessage());
                
        ______TS("Masquerade mode");
        
        gaeSimulation.loginAsAdmin("admin.user");

        params = createParamsForTypicalFeedbackQuestion(instructor1ofCourse1.courseId,  fs.feedbackSessionName);
        params = addUserIdToParams(instructor1ofCourse1.googleId, params);
        
        action = getAction(params);
        result = (RedirectResult) action.executeAndPostProcess();
        
        assertEquals(
                Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE
                        + "?courseid="
                        + instructor1ofCourse1.courseId
                        + "&fsname=First+feedback+session"
                        + "&user="
                        + instructor1ofCourse1.googleId
                        + "&error=false",
                result.getDestinationWithParams());
        
        assertEquals(Const.StatusMessages.FEEDBACK_QUESTION_ADDED, result.getStatusMessage());
        
        expectedLogMessage =
                "TEAMMATESLOG|||instructorFeedbackQuestionAdd|||instructorFeedbackQuestionAdd|||true|||"
                        + "Instructor(M)|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||instr1@course1.com|||"
                        + "Created Feedback Question for Feedback Session:<span class=\"bold\">"
                        + "(First feedback session)</span> for Course <span class=\"bold\">[idOfTypicalCourse1]</span>"
                        + " created.<br><span class=\"bold\">Essay question:</span> question|||/page/instructorFeedbackQuestionAdd";
        assertEquals(expectedLogMessage, action.getLogMessage());
    }
    
    private InstructorFeedbackQuestionAddAction getAction (String... params) throws Exception {
        return (InstructorFeedbackQuestionAddAction) gaeSimulation.getActionObject(uri, params);
    }
}
