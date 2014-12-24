package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.NullPostParameterException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.storage.api.FeedbackQuestionsDb;
import teammates.storage.api.FeedbackResponsesDb;
import teammates.ui.controller.InstructorEditStudentFeedbackSaveAction;
import teammates.ui.controller.RedirectResult;

import com.google.gson.GsonBuilder;


public class InstructorEditStudentFeedbackSaveActionTest extends BaseActionTest {

    private static DataBundle dataBundle;
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        dataBundle = loadDataBundle("/InstructorEditStudentFeedbackPageTest.json");
        removeAndRestoreDatastoreFromJson("/InstructorEditStudentFeedbackPageTest.json");
        
        uri = Const.ActionURIs.INSTRUCTOR_EDIT_STUDENT_FEEDBACK_SAVE;
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception{
        ______TS("edit existing answer");
        
        FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
        FeedbackQuestionAttributes fq = fqDb.getFeedbackQuestion("First feedback session", "CourseA", 1);
        assertNotNull("Feedback question not found in database", fq);
        
        FeedbackResponsesDb frDb = new FeedbackResponsesDb();
        FeedbackResponseAttributes fr = dataBundle.feedbackResponses.get("response1ForQ1");
        fr = frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail); //necessary to get the correct responseId
        assertNotNull("Feedback response not found in database", fr);
        
        
        InstructorAttributes instructor = dataBundle.instructors.get("CourseAinstr");
        gaeSimulation.loginAsInstructor(instructor.googleId);
        
        
        String moderatedStudentEmail = "student1InCourseA@gmail.tmt";
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "Edited" + fr.getResponseDetails().getAnswerString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT, moderatedStudentEmail
        };
        
        InstructorEditStudentFeedbackSaveAction a = getAction(submissionParams);
        RedirectResult r = (RedirectResult) a.executeAndPostProcess();
        
        assertFalse(r.isError);
        assertEquals("All responses submitted succesfully!", r.getStatusMessage());
        assertEquals("/page/instructorEditStudentFeedbackPage" + 
                     "?error=false&moderatedstudent=student1InCourseA%40gmail.tmt" + 
                     "&user=CourseAinstr&courseid=CourseA" + 
                     "&fsname=First+feedback+session",
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
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "",
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT, moderatedStudentEmail
        };
        
        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();
        
        assertFalse(r.isError);
        assertEquals("All responses submitted succesfully!", r.getStatusMessage());
        assertEquals("/page/instructorEditStudentFeedbackPage" + 
                     "?error=false&moderatedstudent=student1InCourseA%40gmail.tmt" + 
                     "&user=CourseAinstr&courseid=CourseA" + 
                     "&fsname=First+feedback+session",
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
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "" ,
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT, moderatedStudentEmail
        };
        
        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();
        
        assertFalse(r.isError);
        assertEquals("All responses submitted succesfully!", r.getStatusMessage());
        assertEquals("/page/instructorEditStudentFeedbackPage" + 
                     "?error=false&moderatedstudent=student1InCourseA%40gmail.tmt" + 
                     "&user=CourseAinstr&courseid=CourseA" + 
                     "&fsname=First+feedback+session",
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
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "New " + fr.getResponseDetails().getAnswerString(), 
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT, moderatedStudentEmail
        };
        
        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();
        
        assertFalse(r.isError);
        assertEquals("All responses submitted succesfully!", r.getStatusMessage());
        assertEquals("/page/instructorEditStudentFeedbackPage" + 
                     "?error=false&moderatedstudent=student1InCourseA%40gmail.tmt" + 
                     "&user=CourseAinstr&courseid=CourseA" + 
                     "&fsname=First+feedback+session",
                        r.getDestinationWithParams());
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail));
        
        ______TS("Unsuccessful case: test empty feedback session name parameter");
        submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, dataBundle.feedbackResponses.get("response1ForQ1").courseId,
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT, moderatedStudentEmail
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
                Const.ParamsNames.FEEDBACK_SESSION_NAME, dataBundle.feedbackResponses.get("response1ForQ1").feedbackSessionName, 
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT, moderatedStudentEmail
        };
        
        try {
            a = getAction(submissionParams);
            r = (RedirectResult) a.executeAndPostProcess();
            signalFailureToDetectException("Did not detect that parameters are null.");
        } catch (NullPostParameterException e) {
            assertEquals(String.format(Const.StatusCodes.NULL_POST_PARAMETER, 
                    Const.ParamsNames.COURSE_ID), e.getMessage());
        }
        
        ______TS("Unsuccessful case: test no moderated student parameter");
        submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-2", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-2-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-2", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-2-0", "",
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-2", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-2-0", "Edited" + fr.getResponseDetails().getAnswerString(),
        };
        
        try {
            a = getAction(submissionParams);
            r = (RedirectResult) a.executeAndPostProcess();
            signalFailureToDetectException("Did not detect that parameters are null.");
        } catch (NullPostParameterException e) {
            assertEquals(String.format(Const.StatusCodes.NULL_POST_PARAMETER, 
                    Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT), e.getMessage());
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
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "", 
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT, moderatedStudentEmail
        };
        
        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();
        
        assertTrue(r.isError);        
        assertEquals("/page/instructorEditStudentFeedbackPage" + 
                     "?error=true&moderatedstudent=student1InCourseA%40gmail.tmt" + 
                     "&user=CourseAinstr&courseid=CourseA" + 
                     "&fsname=First+feedback+session",
                     r.getDestinationWithParams());
        assertNull(frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, "invalid recipient"));
        
        ______TS("Unsuccessful case: insufficient privileges");
        
        InstructorAttributes instructorHelper = dataBundle.instructors.get("CourseAhelper1");
        gaeSimulation.loginAsInstructor(instructorHelper.googleId);
        
        submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "Edited" + fr.getResponseDetails().getAnswerString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT, moderatedStudentEmail
        };
        
        try{
            a = getAction(submissionParams);
            r = (RedirectResult) a.executeAndPostProcess();
        } catch (UnauthorizedAccessException e) {
            assertEquals("Feedback session [First feedback session] is not accessible to instructor [" + 
                    instructorHelper.email + "] for privilege [canmodifysessioncommentinsection] on section [Section 1]", e.getMessage());
        }
        
        ______TS("Unsuccessful case: sufficient privileges only for a section, but attempted to modify another section");
        
        instructorHelper = dataBundle.instructors.get("CourseAhelper1");
        gaeSimulation.loginAsInstructor(instructorHelper.googleId);
        
        
        submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "Edited" + fr.getResponseDetails().getAnswerString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT, moderatedStudentEmail
        };
        
        
        try{
            a = getAction(submissionParams);
            r = (RedirectResult) a.executeAndPostProcess();
        } catch (UnauthorizedAccessException e) {
            assertEquals("Feedback session [First feedback session] is not accessible to instructor [" + 
                    instructorHelper.email + "] for privilege [canmodifysessioncommentinsection] on section [Section 1]", e.getMessage());
        }
       
        
        ______TS("Successful case: sufficient privileges only for a section");
        moderatedStudentEmail = "student2InCourseA@gmail.tmt";
        instructorHelper = dataBundle.instructors.get("CourseAhelper1");
        gaeSimulation.loginAsInstructor(instructorHelper.googleId);
        
        frDb = new FeedbackResponsesDb();
        fr = dataBundle.feedbackResponses.get("response2ForQ1");
        fr = frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail); //necessary to get the correct responseId
        assertNotNull("Feedback response not found in database", fr);
        
        submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "Edited" + fr.getResponseDetails().getAnswerString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT, moderatedStudentEmail
        };
        
        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();
        
        assertFalse(r.isError);
        assertEquals("All responses submitted succesfully!", r.getStatusMessage());
        assertEquals("/page/instructorEditStudentFeedbackPage" + 
                     "?error=false&moderatedstudent=student2InCourseA%40gmail.tmt" + 
                     "&user=CourseAhelper1&courseid=CourseA" + 
                     "&fsname=First+feedback+session",
                     r.getDestinationWithParams());
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail));
        
        
        ______TS("Successful case: sufficient privileges only for a section");
        moderatedStudentEmail = "student2InCourseA@gmail.tmt";
        instructorHelper = dataBundle.instructors.get("CourseAhelper1");
        gaeSimulation.loginAsInstructor(instructorHelper.googleId);
        
        frDb = new FeedbackResponsesDb();
        fr = dataBundle.feedbackResponses.get("response2ForQ1");
        fr = frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail); //necessary to get the correct responseId
        assertNotNull("Feedback response not found in database", fr);
        
        submissionParams = new String[]{
                Const.ParamsNames.FEEDBACK_QUESTION_RESPONSETOTAL + "-1", "1",
                Const.ParamsNames.FEEDBACK_RESPONSE_ID + "-1-0", fr.getId(),
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fr.feedbackSessionName,
                Const.ParamsNames.COURSE_ID, fr.courseId,
                Const.ParamsNames.FEEDBACK_QUESTION_ID + "-1", fr.feedbackQuestionId,
                Const.ParamsNames.FEEDBACK_RESPONSE_RECIPIENT + "-1-0", fr.recipientEmail,
                Const.ParamsNames.FEEDBACK_QUESTION_TYPE + "-1", fr.feedbackQuestionType.toString(),
                Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-1-0", "Edited" + fr.getResponseDetails().getAnswerString(),
                Const.ParamsNames.FEEDBACK_SESSION_MODERATED_STUDENT, moderatedStudentEmail
        };
        
        a = getAction(submissionParams);
        r = (RedirectResult) a.executeAndPostProcess();
        
        assertFalse(r.isError);
        assertEquals("All responses submitted succesfully!", r.getStatusMessage());
        assertEquals("/page/instructorEditStudentFeedbackPage" + 
                     "?error=false&moderatedstudent=student2InCourseA%40gmail.tmt" + 
                     "&user=CourseAhelper1&courseid=CourseA" + 
                     "&fsname=First+feedback+session",
                     r.getDestinationWithParams());
        assertNotNull(frDb.getFeedbackResponse(fq.getId(), fr.giverEmail, fr.recipientEmail));
        
    }
    
    
    
    private InstructorEditStudentFeedbackSaveAction getAction(String... params) throws Exception{
        return (InstructorEditStudentFeedbackSaveAction) (gaeSimulation.getActionObject(uri, params));
    }
}
