package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.NullPostParameterException;
import teammates.common.util.Const;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.ui.controller.InstructorFeedbackQuestionSubmissionEditSaveAction;
import teammates.ui.controller.ShowPageResult;

public class InstructorFeedbackQuestionSubmissionEditSaveActionTest extends
        BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
		removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_SUBMISSION_EDIT_SAVE;
    }

    @Test
    public void testExecuteAndPostProcess() throws Exception {
        InstructorAttributes instructor = dataBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
        
        FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
        FeedbackQuestionAttributes fq = fqDb.getFeedbackQuestion(fs.feedbackSessionName, fs.courseId, 3);
        
        FeedbackResponsesDb frDb = new FeedbackResponsesDb();
        FeedbackResponseAttributes fr = frDb.getFeedbackResponse(fq.getId(), instructor.email, "%GENERAL%");
        assertNotNull(fr);
        
        gaeSimulation.loginAsInstructor(instructor.googleId);
        
        ______TS("not enough parameters");
        
        verifyAssumptionFailure();
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName
        };
        verifyAssumptionFailure(submissionParams);
        
        ______TS("edit existing answer");
        
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), instructor.email, fr.recipientEmail));
        
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId(),
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL, "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT+"-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, fq.questionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT+"-1-0", "Qn Answer",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID+"-1-0", fr.getId()
        };
        
        InstructorFeedbackQuestionSubmissionEditSaveAction a = getAction(submissionParams);
        ShowPageResult r = (ShowPageResult) a.executeAndPostProcess();
        
        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_QUESTION_SUBMISSION_EDIT, r.destination);
        assertFalse(r.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED,    r.getStatusMessage());
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), instructor.email, fr.recipientEmail));
        
        ______TS("edit existing answer - empty receipient email - response will be deleted");
        
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), instructor.email, fr.recipientEmail));
        
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId(),
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL, "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT+"-1-0", "",
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, fq.questionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT+"-1-0", "Qn Answer",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID+"-1-0", fr.getId()
        };
        
        a = getAction(submissionParams);
        r = (ShowPageResult) a.executeAndPostProcess();
        
        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_QUESTION_SUBMISSION_EDIT, r.destination);
        assertFalse(r.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED,    r.getStatusMessage());
        assertNull(frDb.getFeedbackResponse(fq.getId(), instructor.email, fr.recipientEmail));
        
        ______TS("re-add response");
        
        instructor = dataBundle.instructors.get("instructor1OfCourse1");
        fs = dataBundle.feedbackSessions.get("session1InCourse1");
        fr = dataBundle.feedbackResponses.get("response1ForQ3S1C1");
        fr.feedbackQuestionId = fq.getId();
        frDb.createEntity(fr);
        
        fq = fqDb.getFeedbackQuestion(fs.feedbackSessionName, fs.courseId, 3);
        fr = frDb.getFeedbackResponse(fq.getId(), instructor.email, "%GENERAL%");
        assertNotNull(fr);
        
        gaeSimulation.loginAsInstructor(instructor.googleId);
        
        
        ______TS("edit existing answer - empty answer string - response will be deleted");
        
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), instructor.email, fr.recipientEmail));
        
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId(),
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL, "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT+"-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, fq.questionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT+"-1-0", "",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID+"-1-0", fr.getId()
        };
        
        a = getAction(submissionParams);
        r = (ShowPageResult) a.executeAndPostProcess();
        
        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_QUESTION_SUBMISSION_EDIT, r.destination);
        assertFalse(r.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED,    r.getStatusMessage());
        assertNull(frDb.getFeedbackResponse(fq.getId(), instructor.email, fr.recipientEmail));
        
        ______TS("re-add response");
        
        instructor = dataBundle.instructors.get("instructor1OfCourse1");
        fs = dataBundle.feedbackSessions.get("session1InCourse1");
        fr = dataBundle.feedbackResponses.get("response1ForQ3S1C1");
        fr.feedbackQuestionId = fq.getId();
        frDb.createEntity(fr);
        
        fq = fqDb.getFeedbackQuestion(fs.feedbackSessionName, fs.courseId, 3);
        fr = frDb.getFeedbackResponse(fq.getId(), instructor.email, "%GENERAL%");
        assertNotNull(fr);
        
        gaeSimulation.loginAsInstructor(instructor.googleId);
        
        ______TS("edit existing answer - invalid parameters");
        
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), instructor.email, fr.recipientEmail));
        
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId(),
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL, "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT+"-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, fq.questionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT+"-1-0", "Qn Answer",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID+"-1-0", fr.getId()
        };
        
        a = getAction(submissionParams);
        r = (ShowPageResult) a.executeAndPostProcess();
        
        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_QUESTION_SUBMISSION_EDIT, r.destination);
        assertFalse(r.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED,    r.getStatusMessage());
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), instructor.email, fr.recipientEmail));
        
        ______TS("add answer that already exists");
        
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), instructor.email, fr.recipientEmail));
        
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId(),
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL, "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT+"-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, fq.questionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT+"-1-0", "Qn Answer"
        };
        
        a = getAction(submissionParams);
        r = (ShowPageResult) a.executeAndPostProcess();
        
        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_QUESTION_SUBMISSION_EDIT, r.destination);
        assertFalse(r.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED,    r.getStatusMessage());
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), instructor.email, fr.recipientEmail));
        
        ______TS("edit answer - unsupported question type");
        
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), instructor.email, fr.recipientEmail));
        
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId(),
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL, "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT+"-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, "INVALID",
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT+"-1-0", "Qn Answer",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID+"-1-0", fr.getId()
        };
        
        try{
            a = getAction(submissionParams);
            r = (ShowPageResult) a.executeAndPostProcess();
            signalFailureToDetectException();
        } catch (IllegalArgumentException e){
            ignoreExpectedException();
        }
        
        //TODO: test/validate when response questionType does not match questionDetails questionType
        /*
        ______TS("edit answer - unsupported question type");
        
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), instructor.email, fr.recipientEmail));
        
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId(),
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL, "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT+"-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, FeedbackQuestionType.MCQ.toString(),//Submit mcq response for text question
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT+"-1-0", "Qn Answer",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID+"-1-0", fr.getId()
        };
        //TODO: this should fail but does not.
        a = getAction(submissionParams);
        r = (ShowPageResult) a.executeAndPostProcess();
        */
        
        ______TS("delete answer");
        
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId(),
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL, "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT+"-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, fq.questionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT+"-1-0", "",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID+"-1-0", fr.getId()
        };
        
        a = getAction(submissionParams);
        r = (ShowPageResult) a.executeAndPostProcess();
        
        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_QUESTION_SUBMISSION_EDIT, r.destination);
        assertFalse(r.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED,    r.getStatusMessage());
        assertNull(frDb.getFeedbackResponse(fq.getId(), instructor.email, fr.recipientEmail));
        
        ______TS("skip question");
        
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId(),
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL, "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT+"-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, fq.questionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT+"-1-0", ""
        };
        
        a = getAction(submissionParams);
        r = (ShowPageResult) a.executeAndPostProcess();
        
        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_QUESTION_SUBMISSION_EDIT, r.destination);
        assertFalse(r.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED,    r.getStatusMessage());
        assertNull(frDb.getFeedbackResponse(fq.getId(), instructor.email, fr.recipientEmail));
                
        ______TS("new response");
        
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId(),
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL, "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT+"-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, fq.questionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT+"-1-0", "response"
        };
        
        a = getAction(submissionParams);
        r = (ShowPageResult) a.executeAndPostProcess();
        
        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_QUESTION_SUBMISSION_EDIT, r.destination);
        assertFalse(r.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED,    r.getStatusMessage());
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), instructor.email, fr.recipientEmail));
    
        ______TS("grace period session edit answer");
        
        instructor = dataBundle.instructors.get("instructor1OfCourse1");
        fs = dataBundle.feedbackSessions.get("gracePeriodSession");
        
        fq = fqDb.getFeedbackQuestion(fs.feedbackSessionName, fs.courseId, 2);
        
        fr = frDb.getFeedbackResponse(fq.getId(), instructor.email, instructor.email);
        assertNotNull(fr);
        
        gaeSimulation.loginAsInstructor(instructor.googleId);
        
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), instructor.email, fr.recipientEmail));
        
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId(),
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL, "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT+"-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, fq.questionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT+"-1-0", "Qn Answer",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID+"-1-0", fr.getId()
        };
        
        a = getAction(submissionParams);
        r = (ShowPageResult) a.executeAndPostProcess();
        
        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_QUESTION_SUBMISSION_EDIT, r.destination);
        assertFalse(r.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED,    r.getStatusMessage());
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), instructor.email, fr.recipientEmail));
        
        ______TS("closed session edit answer");
        
        instructor = dataBundle.instructors.get("instructor1OfCourse1");
        fs = dataBundle.feedbackSessions.get("closedSession");
        
        fq = fqDb.getFeedbackQuestion(fs.feedbackSessionName, fs.courseId, 1);
        
        fr = frDb.getFeedbackResponse(fq.getId(), instructor.email, instructor.email);
        assertNotNull(fr);
        
        gaeSimulation.loginAsInstructor(instructor.googleId);
        
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), instructor.email, fr.recipientEmail));
        
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId(),
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL, "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT+"-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, fq.questionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT+"-1-0", "Qn Answer",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID+"-1-0", fr.getId()
        };
        
        a = getAction(submissionParams);
        r = (ShowPageResult) a.executeAndPostProcess();
        
        assertEquals(Const.StatusMessages.FEEDBACK_SUBMISSION_EXCEEDED_DEADLINE,
                r.getStatusMessage());
        
        ______TS("private session edit answer");
        
        instructor = dataBundle.instructors.get("instructor1OfCourse2");
        fs = dataBundle.feedbackSessions.get("session1InCourse2");
        
        fq = fqDb.getFeedbackQuestion(fs.feedbackSessionName, fs.courseId, 1);
        fr = frDb.getFeedbackResponse(fq.getId(), instructor.email, "student1InCourse2@gmail.tmt");
        assertNotNull(fr);
        
        gaeSimulation.loginAsInstructor(instructor.googleId);
        
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), instructor.email, fr.recipientEmail));
        
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId(),
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL, "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT+"-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, fq.questionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT+"-1-0", "Qn Answer",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID+"-1-0", fr.getId()
        };
        
        a = getAction(submissionParams);
        r = (ShowPageResult) a.executeAndPostProcess();
        
        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_QUESTION_SUBMISSION_EDIT, r.destination);
        assertFalse(r.isError);
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED,    r.getStatusMessage());
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), instructor.email, fr.recipientEmail));
       
        
        ______TS("Unsuccessful case: test null course id parameter");
        submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId(),
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL, "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT+"-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, fq.questionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT+"-1-0", "Qn Answer",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID+"-1-0", fr.getId()
        };
        
        try {
            a = getAction(submissionParams);
            r = (ShowPageResult) a.executeAndPostProcess();
            signalFailureToDetectException("Did not detect that parameters are null.");
        } catch (NullPostParameterException e) {
            assertEquals(String.format(Const.StatusCodes.NULL_POST_PARAMETER, 
                    Const.ParamsNames.COURSE_ID), e.getMessage());
        }
        
        
        ______TS("Unsuccessful case: test null feedback session name parameter");
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID, fq.getId(),
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL, "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT+"-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE, fq.questionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT+"-1-0", "Qn Answer",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID+"-1-0", fr.getId()
        };
        
        try {
            a = getAction(submissionParams);
            r = (ShowPageResult) a.executeAndPostProcess();
            signalFailureToDetectException("Did not detect that parameters are null.");
        } catch (NullPostParameterException e) {
            assertEquals(String.format(Const.StatusCodes.NULL_POST_PARAMETER, 
                    Const.ParamsNames.FEEDBACK_SESSION_NAME), e.getMessage());
        }
    }
    
    private InstructorFeedbackQuestionSubmissionEditSaveAction getAction(String... params) throws Exception{
        return (InstructorFeedbackQuestionSubmissionEditSaveAction) (gaeSimulation.getActionObject(uri, params));
    }
}
