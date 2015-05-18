package teammates.test.cases.logic;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.appengine.api.urlfetch.URLFetchServicePb.URLFetchRequest;
import com.google.gson.Gson;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentEnrollDetails;
import teammates.common.datatransfer.StudentAttributes.UpdateStatus;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Utils;
import teammates.common.util.Const.ParamsNames;
import teammates.logic.api.Logic;
import teammates.logic.automated.FeedbackSubmissionAdjustmentAction;
import teammates.logic.backdoor.BackDoorLogic;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.logic.core.FeedbackResponsesLogic;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.logic.core.StudentsLogic;
import teammates.test.cases.BaseComponentUsingTaskQueueTestCase;
import teammates.test.cases.BaseTaskQueueCallback;
import teammates.test.driver.BackDoor;
import teammates.test.util.TestHelper;

public class SubmissionsAdjustmentTest extends
        BaseComponentUsingTaskQueueTestCase {
    
    protected static StudentsLogic studentsLogic = StudentsLogic.inst();
    protected static FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
    protected static FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    protected static AccountsLogic accountsLogic = AccountsLogic.inst();
    protected static CoursesLogic coursesLogic = CoursesLogic.inst();
    private static DataBundle dataBundle = getTypicalDataBundle();
    private static DataBundle dataBundle2;
    
    
    @SuppressWarnings("serial")
    public static class SubmissionsAdjustmentTaskQueueCallback 
                extends BaseTaskQueueCallback {
        
        @Override
        public int execute(URLFetchRequest request) {
            HashMap<String, String> paramMap = HttpRequestHelper.getParamMap(request);
            
            assertTrue(paramMap.containsKey(ParamsNames.COURSE_ID));
            assertNotNull(paramMap.get(ParamsNames.COURSE_ID));
            
            assertTrue(paramMap.containsKey(ParamsNames.ENROLLMENT_DETAILS));
            assertNotNull(paramMap.get(ParamsNames.ENROLLMENT_DETAILS));
            
            assertTrue(paramMap.containsKey(ParamsNames.FEEDBACK_SESSION_NAME));
            assertNotNull(paramMap.get(ParamsNames.FEEDBACK_SESSION_NAME));
            
            SubmissionsAdjustmentTaskQueueCallback.taskCount++;
            return Const.StatusCodes.TASK_QUEUE_RESPONSE_OK;
        }
    }
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        gaeSimulation.tearDown();
        gaeSimulation.setupWithTaskQueueCallbackClass(
                SubmissionsAdjustmentTaskQueueCallback.class);
        gaeSimulation.resetDatastore();
        removeAndRestoreTypicalDataInDatastore();
        
        dataBundle2 = loadDataBundle("/FeedbackSubmissionAdjustmentTest.json");
        removeAndRestoreDatastoreFromJson("/FeedbackSubmissionAdjustmentTest.json");
    }
    
    @AfterClass
    public static void classTearDown() throws Exception {
        printTestClassFooter();
    }
    
    @Test
    public void testEnrollStudentsWithScheduledSubmissionAdjustment() throws Exception{
        CourseAttributes course1 = dataBundle.courses.get("typicalCourse1");        
        
        ______TS("enrolling students to a non-existent course");
        SubmissionsAdjustmentTaskQueueCallback.resetTaskCount();
        if(!SubmissionsAdjustmentTaskQueueCallback.verifyTaskCount(0)){
            assertEquals(SubmissionsAdjustmentTaskQueueCallback.taskCount, 0);
        }
        
        String newStudentLine = "Section 1 | Team 1.3|n|s@g|c";
        String nonExistentCourseId = "courseDoesNotExist";
        String enrollLines = newStudentLine + Const.EOL;
        
        List<StudentAttributes> studentsInfo = new ArrayList<StudentAttributes>();
        try {
            studentsInfo = studentsLogic
                    .enrollStudentsWithoutDocument(enrollLines, nonExistentCourseId);
            signalFailureToDetectException(" - EntityDoesNotExistException");
        } catch (EntityDoesNotExistException e) {
            ignoreExpectedException();
        }
        
        //Verify no tasks sent to the task queue
        if(!SubmissionsAdjustmentTaskQueueCallback.verifyTaskCount(0)){
           assertEquals(SubmissionsAdjustmentTaskQueueCallback.taskCount, 0); 
        }
        
        ______TS("try to enroll with empty input enroll lines");
        SubmissionsAdjustmentTaskQueueCallback.resetTaskCount();
        enrollLines = "";
        
        try {
            studentsInfo = studentsLogic
                    .enrollStudentsWithoutDocument(enrollLines, course1.id);
            signalFailureToDetectException("Failure cause : Invalid enrollment executed without exceptions");
        } catch (EnrollException e) {
            String errorMessage = e.getLocalizedMessage();
            assertEquals(Const.StatusMessages.ENROLL_LINE_EMPTY, errorMessage);
        }
        
        //Verify no tasks sent to the task queue
        if(!SubmissionsAdjustmentTaskQueueCallback.verifyTaskCount(0)){
            assertEquals(SubmissionsAdjustmentTaskQueueCallback.taskCount, 0);
        }
        
        ______TS("enroll new students to existing course" +
                "(to check the cascade logic of the SUT)");

        //enroll string can also contain whitespace lines
        enrollLines = "Section | Team | Name | Email | Comment" + Const.EOL;
        enrollLines += newStudentLine + Const.EOL + "\t";
        
        int counter = 0;
        while(counter != 10){
            SubmissionsAdjustmentTaskQueueCallback.resetTaskCount();
            studentsInfo = studentsLogic.enrollStudentsWithoutDocument(enrollLines, course1.id);
        
            //Check whether students are present in database
            assertNotNull(studentsLogic.getStudentForEmail(course1.id, "s@g"));

            //Verify no tasks sent to the task queue
            if(SubmissionsAdjustmentTaskQueueCallback.verifyTaskCount(
                    fsLogic.getFeedbackSessionsForCourse(course1.id).size())){
                break;
            }
            counter++;
        }
        
        assertEquals(SubmissionsAdjustmentTaskQueueCallback.taskCount,
                    fsLogic.getFeedbackSessionsForCourse(course1.id).size());     
        
        
        ______TS("change an existing students email and verify update "
                + "of responses");
        SubmissionsAdjustmentTaskQueueCallback.resetTaskCount();
        
        String oldEmail = studentsInfo.get(0).email;
        StudentAttributes updatedAttributes = new StudentAttributes();
        updatedAttributes.email = "newEmail@g";
        updatedAttributes.course = course1.id;

        studentsLogic.updateStudentCascadeWithoutDocument(oldEmail, updatedAttributes);

        TestHelper.verifyPresentInDatastore(updatedAttributes);

        //Verify no tasks sent to task queue 
        if(!SubmissionsAdjustmentTaskQueueCallback.verifyTaskCount(0)){
            assertEquals(SubmissionsAdjustmentTaskQueueCallback.taskCount, 0);
        }
        
        //Verify that no response exists for old email
        verifyResponsesDoNotExistForEmailInCourse(oldEmail, course1.id);
        
        ______TS("change team of existing student and verify deletion of all his responses");
        StudentAttributes studentInTeam1 = dataBundle.students.get("student2InCourse1");
        
        //verify he has existing team feedback responses in the system
        List<FeedbackResponseAttributes> student1responses = getAllTeamResponsesForStudent(studentInTeam1);
        assertTrue(student1responses.size() != 0);
        
        studentInTeam1.section = "Section 2";
        studentInTeam1.team = "Team 1.2";
        enrollLines = "Section | Team | Name | Email | Comment";
        enrollLines += studentInTeam1.toEnrollmentString();
        
        counter = 0;
        while(counter != 10){
            SubmissionsAdjustmentTaskQueueCallback.resetTaskCount();
            studentsInfo = studentsLogic.enrollStudentsWithoutDocument(enrollLines, studentInTeam1.course);
            
            //Verify scheduling of adjustment of responses
            if(SubmissionsAdjustmentTaskQueueCallback.verifyTaskCount(
                    fsLogic.getFeedbackSessionsForCourse(studentInTeam1.course).size())){
                break;
            }
            counter++;
        }
        if(counter == 10){
            assertEquals(SubmissionsAdjustmentTaskQueueCallback.taskCount,
                        fsLogic.getFeedbackSessionsForCourse(studentInTeam1.course).size());
        }
       
        
        ______TS("error during enrollment");
        //Reset task count in TaskQueue callback
        SubmissionsAdjustmentTaskQueueCallback.resetTaskCount();
        
        String invalidEnrollLine = "Team | Name | Email | Comment" + Const.EOL;
        String invalidStudentId = "t1|n6|e6@g@";
        invalidEnrollLine += invalidStudentId + Const.EOL;
        try {
            studentsInfo = studentsLogic
                    .enrollStudentsWithoutDocument(invalidEnrollLine, course1.id);
            assertTrue(false);
        } catch (EnrollException e) {
            String actualErrorMessage = e.getLocalizedMessage();

            String errorReason = String.format(FieldValidator.EMAIL_ERROR_MESSAGE, "e6@g@",
                    FieldValidator.REASON_INCORRECT_FORMAT);
            String expectedMessage = String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM,
                    invalidStudentId, errorReason);
            
            assertEquals(expectedMessage, actualErrorMessage);
        }

        //Verify no task sent to the task queue
        if(!SubmissionsAdjustmentTaskQueueCallback.verifyTaskCount(0)){
            assertEquals(SubmissionsAdjustmentTaskQueueCallback.taskCount, 0);
        }
    }
    
    @Test
    private void testAdjustmentOfResponses() throws Exception {
                
        ______TS("typical case : existing student changes team");
        FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("session2InCourse1");
        StudentAttributes student = dataBundle.students.get("student1InCourse1");
        
        //Verify pre-existing submissions and responses
        int oldNumberOfResponsesForSession = getAllResponsesForStudentForSession
                (student, session.feedbackSessionName).size();
        assertTrue(oldNumberOfResponsesForSession != 0);
        
        String oldTeam = student.team;
        String oldSection = student.section;
        String newTeam = "Team 1.2";
        String newSection = "Section 2";
        student.team = newTeam;
        student.section = newSection;
        
        
        StudentEnrollDetails enrollDetails = new StudentEnrollDetails
                (UpdateStatus.MODIFIED, student.course, student.email, oldTeam, newTeam, oldSection, newSection);
        ArrayList<StudentEnrollDetails> enrollList = new ArrayList<StudentEnrollDetails>();
        enrollList.add(enrollDetails);
        Gson gsonBuilder = Utils.getTeammatesGson();
        String enrollString = gsonBuilder.toJson(enrollList);

        //Prepare parameter map
        HashMap<String, String> paramMap = new HashMap<String,String>();
        paramMap.put(ParamsNames.COURSE_ID, student.course);
        paramMap.put(ParamsNames.FEEDBACK_SESSION_NAME, session.feedbackSessionName);
        paramMap.put(ParamsNames.ENROLLMENT_DETAILS, enrollString);
        
        studentsLogic.updateStudentCascadeWithSubmissionAdjustmentScheduled(student.email, student, false);
        FeedbackSubmissionAdjustmentAction responseAdjustmentAction = new FeedbackSubmissionAdjustmentAction(paramMap);
        assertTrue(responseAdjustmentAction.execute());
        
        int numberOfNewResponses = getAllResponsesForStudentForSession
                (student, session.feedbackSessionName).size();
        assertEquals(0, numberOfNewResponses);        
        
        
        ______TS("Test that moving a team causes the responses to be updated");
        student = dataBundle2.students.get("student1InCourse1");
        StudentAttributes otherStudent = dataBundle2.students.get("student2InCourse1");
        session = dataBundle2.feedbackSessions.get("session1InCourse1");
        
        FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
        List<FeedbackResponseAttributes> responses = frLogic.getFeedbackResponsesFromGiverForCourse(student.course, student.email);
        assertNotNull(responses);
        
        List<FeedbackResponseAttributes> responsesToTeam = new ArrayList<FeedbackResponseAttributes>();
        for (FeedbackResponseAttributes response : responses) {
            if (response.recipientEmail.equals(student.team)) {
                responsesToTeam.add(response);
            }
        }
        responsesToTeam = getResponsesFromGiverToTeamAndSection(student, frLogic, student.team, student.section);
        assertEquals(1, responsesToTeam.size());
        
        String originalTeam = student.team;
        String renamedTeam = student.team + "_renamed";
        student.team = renamedTeam;
        otherStudent.team = renamedTeam;
        
        enrollList = new ArrayList<StudentEnrollDetails>();
        enrollDetails = new StudentEnrollDetails
                (UpdateStatus.MODIFIED, student.course, student.email, originalTeam, renamedTeam, student.section, student.section);
        enrollList.add(enrollDetails);
        StudentEnrollDetails enrollDetails2 = new StudentEnrollDetails
                (UpdateStatus.MODIFIED, otherStudent.course, otherStudent.email, originalTeam, renamedTeam, otherStudent.section, otherStudent.section);
        enrollList.add(enrollDetails2);
        gsonBuilder = Utils.getTeammatesGson();
        enrollString = gsonBuilder.toJson(enrollList);

        paramMap = prepareParamMapForEnrollment(session, student, enrollString);
        
        studentsLogic.updateStudentCascadeWithSubmissionAdjustmentScheduled(student.email, student, false);
        updateStudentAndAdjustResponses(otherStudent, paramMap);
        
        responsesToTeam = getResponsesFromGiverToTeamAndSection(student, frLogic, renamedTeam, student.section);
        assertEquals(1, responsesToTeam.size());
        
        ______TS("Test that if only one student moves, the response to the team does not change");
        
        // rename from renamedTeam to originalTeam
        student.team = originalTeam;
        
        enrollList = new ArrayList<StudentEnrollDetails>();
        enrollDetails = new StudentEnrollDetails
                (UpdateStatus.MODIFIED, student.course, student.email, renamedTeam, originalTeam, student.section, student.section);
        enrollList.add(enrollDetails);
        enrollString = gsonBuilder.toJson(enrollList);

        paramMap = prepareParamMapForEnrollment(session, student, enrollString);
        updateStudentAndAdjustResponses(student, paramMap);
        
        
        responsesToTeam = getResponsesFromGiverToTeamAndSection(student, frLogic, student.team, student.section);
        assertEquals(0, responsesToTeam.size());
        
        ______TS("Move the other student to the team, response should move");
        // rename from renamedTeam to originalTeam
        otherStudent.team = originalTeam;
        
        enrollList = new ArrayList<StudentEnrollDetails>();
        enrollDetails = new StudentEnrollDetails
                (UpdateStatus.MODIFIED, otherStudent.course, otherStudent.email, renamedTeam, originalTeam, student.section, student.section);
        enrollList.add(enrollDetails);
        enrollString = gsonBuilder.toJson(enrollList);

        paramMap = prepareParamMapForEnrollment(session, student, enrollString);
        updateStudentAndAdjustResponses(otherStudent, paramMap);
        
        responsesToTeam = getResponsesFromGiverToTeamAndSection(student, frLogic, originalTeam, student.section);
        assertEquals(1, responsesToTeam.size());

        ______TS("Test that a change in section causes response to move");
        String originalSection = student.section;
        String renamedSection = student.section + "_renamed";
        student.section = renamedSection;
        otherStudent.section = renamedSection;
        
        enrollList = new ArrayList<StudentEnrollDetails>();
        enrollDetails = new StudentEnrollDetails
                (UpdateStatus.MODIFIED, student.course, student.email, student.team, student.team, originalSection, student.section);
        enrollList.add(enrollDetails);
        enrollDetails2 = new StudentEnrollDetails
                (UpdateStatus.MODIFIED, otherStudent.course, otherStudent.email, otherStudent.team, otherStudent.team, originalSection, otherStudent.section);
        enrollList.add(enrollDetails2);
        enrollString = gsonBuilder.toJson(enrollList);
        
        // Prepare parameter map
        paramMap = prepareParamMapForEnrollment(session, student, enrollString);
        updateStudentAndAdjustResponses(student, paramMap);
        
        responsesToTeam = getResponsesFromGiverToTeamAndSection(student,
                frLogic, originalTeam, student.section);
        assertEquals(1, responsesToTeam.size());
        
        ______TS("Test that if the team is move into 2 different teams, the responses does not move");
        
        String renamedTeam2 = originalTeam + "_renamed2";
        
        student.team = renamedTeam2;
        otherStudent.team = renamedTeam;
        
        enrollList = new ArrayList<StudentEnrollDetails>();
        enrollDetails = new StudentEnrollDetails
                (UpdateStatus.MODIFIED, student.course, student.email, originalTeam, renamedTeam, student.section, student.section);
        enrollList.add(enrollDetails);
        enrollDetails2 = new StudentEnrollDetails
                (UpdateStatus.MODIFIED, otherStudent.course, otherStudent.email, originalTeam, renamedTeam2, student.section, student.section);
        enrollList.add(enrollDetails2);
        enrollString = gsonBuilder.toJson(enrollList);

        paramMap = prepareParamMapForEnrollment(session, student, enrollString);
        updateStudentAndAdjustResponses(student, paramMap);
        
        responsesToTeam = getResponsesFromGiverToTeamAndSection(student,
                frLogic, originalTeam, student.section);
        assertEquals(1, responsesToTeam.size());
        
    }

    private List<FeedbackResponseAttributes> getResponsesFromGiverToTeamAndSection(
            StudentAttributes student, FeedbackResponsesLogic frLogic,
            String team, String section) {
        
        List<FeedbackResponseAttributes> responses;
        
        responses = frLogic.getFeedbackResponsesFromGiverForCourse(student.course, student.email);
        assertNotNull(responses);
        
        List<FeedbackResponseAttributes> responsesToTeam = new ArrayList<FeedbackResponseAttributes>();
        for (FeedbackResponseAttributes response : responses) {
            if (response.recipientEmail.equals(team) && 
                response.recipientSection.equals(section)) {
                
                responsesToTeam.add(response);
            }
        }
        
        return responsesToTeam;
    }

    private void updateStudentAndAdjustResponses(StudentAttributes student,
            HashMap<String, String> paramMap)
            throws EntityDoesNotExistException, InvalidParametersException {
        FeedbackSubmissionAdjustmentAction responseAdjustmentAction;
        studentsLogic.updateStudentCascadeWithSubmissionAdjustmentScheduled(student.email, student, false);
        responseAdjustmentAction = new FeedbackSubmissionAdjustmentAction(paramMap);
        assertTrue(responseAdjustmentAction.execute());
    }

    private HashMap<String, String> prepareParamMapForEnrollment(
            FeedbackSessionAttributes session, StudentAttributes student,
            String enrollString) {
        HashMap<String, String> paramMap;
        paramMap = new HashMap<String,String>();
        paramMap.put(ParamsNames.COURSE_ID, student.course);
        paramMap.put(ParamsNames.FEEDBACK_SESSION_NAME, session.feedbackSessionName);
        paramMap.put(ParamsNames.ENROLLMENT_DETAILS, enrollString);
        return paramMap;
    }

    private List<FeedbackResponseAttributes> getAllTeamResponsesForStudent(StudentAttributes student) {
        List<FeedbackResponseAttributes> returnList = new ArrayList<FeedbackResponseAttributes>();
        
        List<FeedbackResponseAttributes> studentReceiverResponses = FeedbackResponsesLogic.inst()
                .getFeedbackResponsesForReceiverForCourse(student.course, student.email);
        
        for (FeedbackResponseAttributes response : studentReceiverResponses) {
            FeedbackQuestionAttributes question = FeedbackQuestionsLogic.inst()
                    .getFeedbackQuestion(response.feedbackQuestionId);
            if (question.recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS) {
                returnList.add(response);
            }
        }
        
        List<FeedbackResponseAttributes> studentGiverResponses = FeedbackResponsesLogic.inst()
                .getFeedbackResponsesFromGiverForCourse(student.course, student.email);
        
        for (FeedbackResponseAttributes response : studentGiverResponses) {
            FeedbackQuestionAttributes question = FeedbackQuestionsLogic.inst()
                    .getFeedbackQuestion(response.feedbackQuestionId);
            if (question.giverType == FeedbackParticipantType.TEAMS || 
                question.recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS) {
                returnList.add(response);
            }
        }
        
        return returnList;
    }
    
    private List<FeedbackResponseAttributes> getAllResponsesForStudentForSession(StudentAttributes student,
            String feedbackSessionName) {
        List<FeedbackResponseAttributes> returnList = new ArrayList<FeedbackResponseAttributes>();
        
        List<FeedbackResponseAttributes> allResponseOfStudent = getAllTeamResponsesForStudent(student);
        
        for (FeedbackResponseAttributes responseAttributes : allResponseOfStudent) {
            if (responseAttributes.feedbackSessionName.equals(feedbackSessionName)) {
                returnList.add(responseAttributes);
            }
        }
        
        return returnList;
    }
    
    private void verifyResponsesDoNotExistForEmailInCourse(String email,
            String courseId) {
        List<FeedbackSessionAttributes> allSessions = fsLogic
                .getFeedbackSessionsForCourse(courseId); 
        
        for (FeedbackSessionAttributes eachSession : allSessions) {
            List<FeedbackResponseAttributes> allResponses = frLogic
                    .getFeedbackResponsesForSession(eachSession.feedbackSessionName, courseId);
            
            for (FeedbackResponseAttributes eachResponse : allResponses) {
                if (eachResponse.recipientEmail.equals(email) ||
                    eachResponse.giverEmail.equals(email)) {
                    fail("Cause : Feedback response for "
                         + email + " found on system");
                }
            }
        }
    }
    
}
