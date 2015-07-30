package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackNumericalScaleQuestionDetails;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackQuestionType;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.NullPostParameterException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;
import teammates.common.util.Url;
import teammates.logic.core.StudentsLogic;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.ui.controller.ActionResult;
import teammates.ui.controller.RedirectResult;
import teammates.ui.controller.StudentFeedbackSubmissionEditSaveAction;

public class StudentFeedbackSubmissionEditSaveActionTest extends BaseActionTest {
    private final DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
		removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_SAVE;
    }

    @Test
    public void testExecuteAndPostProcess() throws Exception {
        ______TS("edit existing answer");

        FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
        FeedbackQuestionAttributes fq = fqDb.getFeedbackQuestion("First feedback session", "idOfTypicalCourse1", 1);
        assertNotNull("Feedback question not found in database", fq);

        FeedbackResponsesDb frDb = new FeedbackResponsesDb();
        FeedbackResponseAttributes fr = dataBundle.feedbackResponses.get("response1ForQ1S1C1");
        // necessary to get the correct responseId
        fr = frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail);
        assertNotNull("Feedback response not found in database", fr);

        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        gaeSimulation.loginAsStudent(student1InCourse1.googleId);

        String[] submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "Edited" + fr.getResponseDetails().getAnswerString()
        };

        StudentFeedbackSubmissionEditSaveAction a = getAction(submissionParams);
        RedirectResult r = (RedirectResult) a.executeAndPostProcess();

        assertFalse(r.isError);
        assertEquals("All responses submitted succesfully!", r.getStatusMessage());
        assertEquals("/page/studentHomePage?error=" + r.isError +"&user=student1InCourse1",
                     r.getDestinationWithParams());
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail));

        ______TS("deleted response");

        submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", ""
        };

        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();

        assertFalse(r.isError);
        assertEquals("All responses submitted succesfully!", r.getStatusMessage());
        assertEquals("/page/studentHomePage?error=" + r.isError +"&user=student1InCourse1",
                     r.getDestinationWithParams());
        assertNull(frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail));

        ______TS("skipped question");

        submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", ""
        };

        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();

        assertFalse(r.isError);
        assertEquals("All responses submitted succesfully!", r.getStatusMessage());
        assertEquals("/page/studentHomePage?error=" + r.isError +"&user=student1InCourse1",
                     r.getDestinationWithParams());
        assertNull(frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail));

        ______TS("new response");

        submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "New " + fr.getResponseDetails().getAnswerString()
        };

        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();

        assertFalse(r.isError);
        assertEquals("All responses submitted succesfully!", r.getStatusMessage());
        assertEquals("/page/studentHomePage?error=" + r.isError +"&user=student1InCourse1",
                     r.getDestinationWithParams());
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail));

        ______TS("edit response, did not specify recipient");

        fq = fqDb.getFeedbackQuestion("First feedback session", "idOfTypicalCourse1", 2);
        assertNotNull("Feedback question not found in database", fq);

        fr = dataBundle.feedbackResponses.get("response2ForQ2S1C1");
        // necessary to get the correct responseId
        fr = frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail);
        assertNotNull("Feedback response not found in database", fr);

        submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-2", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-2-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-2", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-2-0", "",
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-2", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-2-0", "Edited" + fr.getResponseDetails().getAnswerString()
        };

        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();

        assertTrue(r.isError);
        assertEquals("You did not specify a recipient for your response in question 2.", r.getStatusMessage());
        assertEquals("/page/studentFeedbackSubmissionEditPage?error=" + r.isError + "&user=student1InCourse1"
                     + "&courseid=idOfTypicalCourse1" + "&fsname=First+feedback+session",
                     r.getDestinationWithParams());

        ______TS("edit response, empty answer");

        submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-2", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-2-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-2", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-2-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-2", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-2-0", ""
        };

        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();

        assertFalse(r.isError);
        assertEquals("All responses submitted succesfully!", r.getStatusMessage());
        assertEquals("/page/studentHomePage?error=" + r.isError + "&user=student1InCourse1",
                     r.getDestinationWithParams());
        assertNull(frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail));


        ______TS("new response, did not specify recipient");

        submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-2", "1",
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-2", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-2-0", "",
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-2", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-2-0", "Edited" + fr.getResponseDetails().getAnswerString()
        };

        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();

        assertTrue(r.isError);
        assertEquals("You did not specify a recipient for your response in question 2.", r.getStatusMessage());
        assertEquals("/page/studentFeedbackSubmissionEditPage?error=" + r.isError + "&user=student1InCourse1"
                     + "&courseid=idOfTypicalCourse1" + "&fsname=First+feedback+session",
                     r.getDestinationWithParams());
        assertNull(frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail));

        ______TS("mcq");

        DataBundle dataBundle = loadDataBundle("/FeedbackSessionQuestionTypeTest.json");
        removeAndRestoreDatastoreFromJson("/FeedbackSessionQuestionTypeTest.json");

        fq = fqDb.getFeedbackQuestion("MCQ Session", "FSQTT.idOfTypicalCourse1", 1);
        assertNotNull("Feedback question not found in database", fq);

        fr = dataBundle.feedbackResponses.get("response1ForQ1S1C1");
        // necessary to get the correct responseId
        fr = frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail);
        assertNotNull("Feedback response not found in database", fr);

        student1InCourse1 = dataBundle.students.get("student1InCourse1");
        gaeSimulation.loginAsStudent(student1InCourse1.googleId);

        submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "It's perfect"
        };

        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();

        assertFalse(r.isError);
        assertEquals("All responses submitted succesfully!", r.getStatusMessage());
        assertEquals("/page/studentHomePage?error=" + r.isError + "&user=FSQTT.student1InCourse1",
                     r.getDestinationWithParams());
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail));

        ______TS("mcq, question skipped");

        submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString()
        };

        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();

        assertFalse(r.isError);
        assertEquals("All responses submitted succesfully!", r.getStatusMessage());
        assertEquals("/page/studentHomePage?error=" + r.isError + "&user=FSQTT.student1InCourse1",
                     r.getDestinationWithParams());
        assertNull(frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail));
        
        ______TS("mcq with other option enabled");
        
        fq = fqDb.getFeedbackQuestion("MCQ Session", "FSQTT.idOfTypicalCourse1", 3);
        assertNotNull("Feedback question not found in database", fq);
    
        fr = dataBundle.feedbackResponses.get("response1ForQ3S1C1");
        fr = frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail); //necessary to get the correct responseId
        assertNotNull("Feedback response not found in database", fr);

        submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-2", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-2-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-2", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-2-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-2", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-2-0", "Tutorial",
                Const.ParamsNames.FEEDBACK_QUESTION_MCQ_ISOTHEROPTIONANSWER + "-2-0", "1"
        };
        
        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();
        
        assertFalse(r.isError);
        assertEquals("All responses submitted succesfully!", r.getStatusMessage());
        assertEquals("/page/studentHomePage?error=" + r.isError +"&user=FSQTT.student1InCourse1",
                        r.getDestinationWithParams());
        FeedbackResponseAttributes finalFr = frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail);
        assertNotNull(finalFr);
        assertEquals("Tutorial", finalFr.getResponseDetails().getAnswerString());
        

        ______TS("msq");

        fq = fqDb.getFeedbackQuestion("MSQ Session", "FSQTT.idOfTypicalCourse1", 1);
        assertNotNull("Feedback question not found in database", fq);

        fr = dataBundle.feedbackResponses.get("response1ForQ1S2C1");
        // necessary to get the correct responseId
        fr = frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail);
        assertNotNull("Feedback response not found in database", fr);

        student1InCourse1 = dataBundle.students.get("student1InCourse1");
        gaeSimulation.loginAsStudent(student1InCourse1.googleId);

        submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "It's perfect"
        };

        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();

        assertFalse(r.isError);
        assertEquals("All responses submitted succesfully!", r.getStatusMessage());
        assertEquals("/page/studentHomePage?error=" + r.isError +"&user=FSQTT.student1InCourse1",
                     r.getDestinationWithParams());
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail));

        ______TS("msq, question skipped");

        submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString()
        };

        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();

        assertFalse(r.isError);
        assertEquals("All responses submitted succesfully!", r.getStatusMessage());
        assertEquals("/page/studentHomePage?error=" + r.isError + "&user=FSQTT.student1InCourse1",
                     r.getDestinationWithParams());
        assertNull(frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail));
        
        ______TS("msq with other option enabled, student selects predefined options and other option");
        
        fq = fqDb.getFeedbackQuestion("MSQ Session", "FSQTT.idOfTypicalCourse1", 3);
        assertNotNull("Feedback question not found in database", fq);
    
        fr = dataBundle.feedbackResponses.get("response1ForQ3S2C1");
        fr = frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail); //necessary to get the correct responseId
        assertNotNull("Feedback response not found in database", fr);

        submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-3", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-3-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-3", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-3-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-3", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-3-0", "Pizza, Pasta, Chicken rice, Hotdog",
                Const.ParamsNames.FEEDBACK_QUESTION_MSQ_ISOTHEROPTIONANSWER + "-3-0", "1"
        };
        
        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();
        
        assertFalse(r.isError);
        assertEquals("All responses submitted succesfully!", r.getStatusMessage());
        assertEquals("/page/studentHomePage?error=" + r.isError +"&user=FSQTT.student1InCourse1",
                        r.getDestinationWithParams());
        finalFr = frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail);
        assertNotNull(finalFr);
        assertEquals("Pizza, Pasta, Chicken rice, Hotdog", finalFr.getResponseDetails().getAnswerString());
        
        ______TS("msq with other option enabled, student selects predefined options only, not other option");
    
        fr = dataBundle.feedbackResponses.get("response2ForQ3S2C1");
        fr = frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail); //necessary to get the correct responseId
        assertNotNull("Feedback response not found in database", fr);

        submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-3", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-3-1", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-3", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-3-1", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-3", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-3-1", "Pasta",
                Const.ParamsNames.FEEDBACK_QUESTION_MSQ_ISOTHEROPTIONANSWER + "-3-1", "0"
        };
        
        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();
        
        assertFalse(r.isError);
        assertEquals("All responses submitted succesfully!", r.getStatusMessage());
        assertEquals("/page/studentHomePage?error=" + r.isError +"&user=FSQTT.student1InCourse1",
                        r.getDestinationWithParams());
        finalFr = frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail);
        assertNotNull(finalFr);
        assertEquals("Pasta", finalFr.getResponseDetails().getAnswerString());
        
        ______TS("msq with other option enabled, student selects other option only, not any predefined option");
        
        fr = dataBundle.feedbackResponses.get("response3ForQ3S2C1");
        fr = frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail); //necessary to get the correct responseId
        assertNotNull("Feedback response not found in database", fr);

        submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-3", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-3-2", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-3", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-3-2", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-3", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-3-2", "Hotdog",
                Const.ParamsNames.FEEDBACK_QUESTION_MSQ_ISOTHEROPTIONANSWER + "-3-2", "1"
        };
        
        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();
        
        assertFalse(r.isError);
        assertEquals("All responses submitted succesfully!", r.getStatusMessage());
        assertEquals("/page/studentHomePage?error=" + r.isError +"&user=FSQTT.student1InCourse1",
                        r.getDestinationWithParams());
        finalFr = frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail);
        assertNotNull(finalFr);
        assertEquals("Hotdog", finalFr.getResponseDetails().getAnswerString());

        ______TS("numscale");

        fq = fqDb.getFeedbackQuestion("NUMSCALE Session", "FSQTT.idOfTypicalCourse1", 1);
        assertNotNull("Feedback question not found in database", fq);
        FeedbackNumericalScaleQuestionDetails fqd =
                (FeedbackNumericalScaleQuestionDetails) fq.getQuestionDetails();

        fr = dataBundle.feedbackResponses.get("response1ForQ1S3C1");
        // necessary to get the correct responseId
        fr = frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail);
        assertNotNull("Feedback response not found in database", fr);

        student1InCourse1 = dataBundle.students.get("student1InCourse1");
        gaeSimulation.loginAsStudent(student1InCourse1.googleId);

        submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "1",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MIN + "-1-0", Integer.toString(fqd.minScale),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MAX + "-1-0", Integer.toString(fqd.maxScale),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_STEP + "-1-0", StringHelper.toDecimalFormatString(fqd.step)
        };

        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();

        assertEquals("All responses submitted succesfully!", r.getStatusMessage());
        assertFalse(r.isError);
        assertEquals("/page/studentHomePage?error=" + r.isError + "&user=FSQTT.student1InCourse1",
                     r.getDestinationWithParams());
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail));

        ______TS("numscale, question skipped");

        submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MIN + "-1-0", Integer.toString(fqd.minScale),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MAX + "-1-0", Integer.toString(fqd.maxScale),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_STEP + "-1-0", StringHelper.toDecimalFormatString(fqd.step)
        };

        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();

        assertFalse(r.isError);
        assertEquals("All responses submitted succesfully!", r.getStatusMessage());
        assertEquals("/page/studentHomePage?error=" + r.isError + "&user=FSQTT.student1InCourse1",
                     r.getDestinationWithParams());
        assertNull(frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail));

        ______TS("Successful case: const sum: typical case");

        fq = fqDb.getFeedbackQuestion("CONSTSUM Session", "FSQTT.idOfTypicalCourse1", 1);
        assertNotNull("Feedback question not found in database", fq);

        fr = dataBundle.feedbackResponses.get("response1ForQ1S4C1");
        // necessary to get the correct responseId
        fr = frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail);
        assertNotNull("Feedback response not found in database", fr);

        FeedbackResponseAttributes fr2 = dataBundle.feedbackResponses.get("response2ForQ1S4C1");
        // necessary to get the correct responseId
        fr2 = frDb.getFeedbackResponse(fq.getId(), fr2.giverEmail, fr2.recipientEmail);
        assertNotNull("Feedback response not found in database", fr2);

        student1InCourse1 = dataBundle.students.get("student1InCourse1");
        gaeSimulation.loginAsStudent(student1InCourse1.googleId);

        submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "50",
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "50",
        };

        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();

        assertEquals("All responses submitted succesfully!", r.getStatusMessage());
        assertFalse(r.isError);
        assertEquals("/page/studentHomePage?error=" + r.isError + "&user=FSQTT.student1InCourse1",
                     r.getDestinationWithParams());
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail));

        ______TS("Successful case: const sum: question skipped");

        submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", ""
        };

        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();

        assertFalse(r.isError);
        assertEquals("All responses submitted succesfully!", r.getStatusMessage());
        assertEquals("/page/studentHomePage?error=false&user=FSQTT.student1InCourse1",
                     r.getDestinationWithParams());
        assertNull(frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail));

        ______TS("Successful case: contrib qn: typical case");

        fq = fqDb.getFeedbackQuestion("CONTRIB Session", "FSQTT.idOfTypicalCourse1", 1);
        assertNotNull("Feedback question not found in database", fq);

        fr = dataBundle.feedbackResponses.get("response1ForQ1S5C1");
        // necessary to get the correct responseId
        fr = frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail);
        assertNotNull("Feedback response not found in database", fr);

        student1InCourse1 = dataBundle.students.get("student1InCourse1");
        gaeSimulation.loginAsStudent(student1InCourse1.googleId);

        submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "150",
        };

        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();

        assertEquals("All responses submitted succesfully!", r.getStatusMessage());
        assertFalse(r.isError);
        assertEquals("/page/studentHomePage?error=" + r.isError + "&user=FSQTT.student1InCourse1",
                     r.getDestinationWithParams());
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail));

        ______TS("Successful case: contrib qn: question skipped");

        submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", ""
        };

        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();

        assertFalse(r.isError);
        assertEquals("All responses submitted succesfully!", r.getStatusMessage());
        assertEquals("/page/studentHomePage?error=false&user=FSQTT.student1InCourse1",
                     r.getDestinationWithParams());
        assertNull(frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail));

        ______TS("Unsuccessful case: test empty feedback session name parameter");

        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, dataBundle.feedbackResponses.get("response1ForQ1S1C1").courseId
        };

        try {
            a = getAction(submissionParams);
            r = (RedirectResult) a.executeAndPostProcess();
            signalFailureToDetectException("Did not detect that parameters are null.");
        } catch (NullPostParameterException e) {
            assertEquals(String.format(Const.StatusCodes.NULL_POST_PARAMETER,
                                       Const.ParamsNames.FEEDBACK_SESSION_NAME), e.getMessage());
        }

        ______TS("Unsuccessful case: test empty course id parameter");

        submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_SESSION_NAME, dataBundle.feedbackResponses.get("response1ForQ1S1C1").feedbackSessionName
        };

        try {
            a = getAction(submissionParams);
            r = (RedirectResult) a.executeAndPostProcess();
            signalFailureToDetectException("Did not detect that parameters are null.");
        } catch (NullPostParameterException e) {
            assertEquals(String.format(Const.StatusCodes.NULL_POST_PARAMETER,
                                       Const.ParamsNames.COURSE_ID), e.getMessage());
        }

        ______TS("Unsuccessful case: modified recipient list to invalid recipient");

        submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", "invalid recipient",
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", ""
        };

        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();

        assertTrue(r.isError);
        assertEquals("/page/studentFeedbackSubmissionEditPage?error=true&user=FSQTT.student1InCourse1"
                     + "&courseid=FSQTT.idOfTypicalCourse1&fsname=CONTRIB+Session",
                     r.getDestinationWithParams());
        assertNull(frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, "invalid recipient"));
        

        ______TS("Unsuccessful case: modified question type to another type");
        // Response is supposed to be CONTRIB, but submit as RUBRIC
        assertEquals(fq.questionType, FeedbackQuestionType.CONTRIB);
        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", "RUBRIC",
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", ""
        };
        
        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();

        assertTrue(r.isError);  
        assertEquals("/page/studentFeedbackSubmissionEditPage?error=true&user=FSQTT.student1InCourse1&courseid=FSQTT.idOfTypicalCourse1&fsname=CONTRIB+Session",
                                r.getDestinationWithParams());
        assertEquals(String.format(Const.StatusMessages.FEEDBACK_RESPONSES_WRONG_QUESTION_TYPE, "1"), r.getStatusMessage());
        assertNull(frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail));
        
        ______TS("Unsuccessful case: try to delete response not belonging to the student");
        
        fq = fqDb.getFeedbackQuestion("MCQ Session", "FSQTT.idOfTypicalCourse1", 1);
        assertNotNull("Feedback question not found in database", fq);
        
        FeedbackResponseAttributes otherFr = dataBundle.feedbackResponses.get("response2ForQ1S1C1");
        List<FeedbackResponseAttributes> responsesToAdd = new ArrayList<FeedbackResponseAttributes>();
        responsesToAdd.add(fr);
        frDb.createFeedbackResponses(responsesToAdd);
        
        otherFr = frDb.getFeedbackResponse(fq.getId(), otherFr.giverEmail, otherFr.recipientEmail); //necessary to get the correct responseId
        assertNotNull("Feedback response not found in database", fr);
        
        submissionParams = new String[] {
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", otherFr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, otherFr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, otherFr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", otherFr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", otherFr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", otherFr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", ""
        };
        
        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();
        
        assertTrue(r.isError);        

        assertEquals("/page/studentFeedbackSubmissionEditPage?error=true&user=FSQTT.student1InCourse1&courseid=FSQTT.idOfTypicalCourse1&fsname=MCQ+Session",
                        r.getDestinationWithParams());
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), otherFr.giverEmail, otherFr.recipientEmail));
        
        gaeSimulation.logoutUser();

        ______TS("Unregistered student with valid submission of response remains at submission page");

        StudentAttributes unregisteredStudent = dataBundle.students.get("unregisteredStudentInCourse1");

        fq = fqDb.getFeedbackQuestion("Unregistered Student Session", "FSQTT.idOfTypicalCourse2", 1);
        assertNotNull("Feedback question not found in database", fq);
        fqd = (FeedbackNumericalScaleQuestionDetails) fq.getQuestionDetails();

        fr = dataBundle.feedbackResponses.get("response1ForQ1S1C2");
        fr = frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail);
        // ensure correct response id is retrieved
        assertNotNull("Feedback response not found in database", fr);

        FeedbackSessionsDb fsDb = new FeedbackSessionsDb();
        FeedbackSessionAttributes fsa = dataBundle.feedbackSessions.get("unregisteredStudentSession");
        fsa = fsDb.getFeedbackSession(unregisteredStudent.course, fsa.feedbackSessionName);
        assertNotNull("Feedback session not found in database", fsa);

        // Setting uri for unregistered student which contains the key of the student
        String studentKey = StudentsLogic.inst().getEncryptedKeyForStudent(unregisteredStudent.course,
                                                                           unregisteredStudent.email);
        uri = new Url(Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_SAVE)
                                          .withCourseId(unregisteredStudent.course)
                                          .withSessionName(fsa.feedbackSessionName)
                                          .withRegistrationKey(studentKey)
                                          .withStudentEmail(unregisteredStudent.email)
                                          .toString();

        // Valid response from unregistered student
        String[] validSubmissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", fr.getResponseDetails().getAnswerString(),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MIN + "-1-0", Integer.toString(fqd.minScale),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MAX + "-1-0", Integer.toString(fqd.maxScale),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_STEP + "-1-0", StringHelper.toDecimalFormatString(fqd.step)
        };

        StudentFeedbackSubmissionEditSaveAction submissionAction = getAction(validSubmissionParams);
        RedirectResult redirectResult = getRedirectResult(submissionAction);

        assertFalse(redirectResult.isError);
        assertEquals(Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE
                     + "?studentemail=unregisteredStudentInCourse2%40gmail.tmt&error="
                     + redirectResult.isError + "&courseid="+unregisteredStudent.course
                     + "&fsname=Unregistered+Student+Session&key=" + studentKey,
                     redirectResult.getDestinationWithParams());
        assertEquals("All responses submitted succesfully!", redirectResult.getStatusMessage());
        gaeSimulation.logoutUser();

        ______TS("Unregistered student with invalid submission of response remains at submission page");

        // Setting uri for unregistered student which contains the key of the student
        studentKey = StudentsLogic.inst().getEncryptedKeyForStudent(unregisteredStudent.course, unregisteredStudent.email);
        uri = new Url(Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_SAVE)
                                          .withCourseId(unregisteredStudent.course)
                                          .withSessionName(fsa.feedbackSessionName)
                                          .withRegistrationKey(studentKey)
                                          .withStudentEmail(unregisteredStudent.email)
                                          .toString();

        // Invalid response from unregistered student
        String[] invalidSubmissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "100",
                Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MIN + "-1-0", Integer.toString(fqd.minScale),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MAX + "-1-0", Integer.toString(fqd.maxScale),
                Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_STEP + "-1-0", StringHelper.toDecimalFormatString(fqd.step)
        };

        submissionAction = getAction(invalidSubmissionParams);
        redirectResult = getRedirectResult(submissionAction);

        assertTrue(redirectResult.isError);
        assertEquals(Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE
                     + "?studentemail=unregisteredStudentInCourse2%40gmail.tmt&error="
                     + redirectResult.isError + "&courseid="+unregisteredStudent.course
                     + "&fsname=Unregistered+Student+Session&key=" + studentKey,
                     redirectResult.getDestinationWithParams());
        assertEquals("100 is out of the range for Numerical-scale question.(min=1, max=5)", redirectResult.getStatusMessage());
        gaeSimulation.logoutUser();

        // reset uri to normal submission page uri as it might be used by other testing methods
        uri = Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_SAVE;
    }

    @Test
    public void testGracePeriodExecuteAndPostProcess() throws Exception {
        FeedbackSessionsDb feedbackSessionDb = new FeedbackSessionsDb();
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("gracePeriodSession");
        StudentAttributes studentInGracePeriod = dataBundle.students.get("student1InCourse1");
        gaeSimulation.loginAsStudent(studentInGracePeriod.googleId);

        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName
        };

        ______TS("opened");

        fs.endTime = TimeHelper.getDateOffsetToCurrentTime(1);
        feedbackSessionDb.updateFeedbackSession(fs);

        assertTrue(fs.isOpened());
        assertFalse(fs.isInGracePeriod());

        StudentFeedbackSubmissionEditSaveAction a = getAction(submissionParams);
        ActionResult r = a.executeAndPostProcess();

        assertEquals(Const.ActionURIs.STUDENT_HOME_PAGE + "?error=false&user=student1InCourse1",
                     r.getDestinationWithParams());
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, r.getStatusMessage());
        assertFalse(r.isError);

        ______TS("during grace period");

        fs.endTime = TimeHelper.getDateOffsetToCurrentTime(0);
        feedbackSessionDb.updateFeedbackSession(fs);

        assertFalse(fs.isOpened());
        assertTrue(fs.isInGracePeriod());

        a = getAction(submissionParams);
        r = a.executeAndPostProcess();
        assertEquals(Const.ActionURIs.STUDENT_HOME_PAGE + "?error=false&user=student1InCourse1",
                     r.getDestinationWithParams());
        assertEquals(Const.StatusMessages.FEEDBACK_RESPONSES_SAVED, r.getStatusMessage());
        assertFalse(r.isError);

        ______TS("after grace period");

        fs.endTime = TimeHelper.getDateOffsetToCurrentTime(-10);
        feedbackSessionDb.updateFeedbackSession(fs);

        assertFalse(fs.isOpened());
        assertFalse(fs.isInGracePeriod());

        a = getAction(submissionParams);
        r = a.executeAndPostProcess();

        assertEquals(Const.StatusMessages.FEEDBACK_SUBMISSIONS_NOT_OPEN, r.getStatusMessage());
    }

    private StudentFeedbackSubmissionEditSaveAction getAction(String... params) throws Exception {
        return (StudentFeedbackSubmissionEditSaveAction) (gaeSimulation.getActionObject(uri, params));
    }
}
