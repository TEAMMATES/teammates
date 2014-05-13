package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.ui.controller.ActionResult;
import teammates.ui.controller.InstructorFeedbackResultsPageAction;

public class InstructorFeedbackResultsPageActionTest extends BaseActionTest {

    DataBundle dataBundle;
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE;
    }

    @BeforeMethod
    public void caseSetUp() throws Exception {
        dataBundle = getTypicalDataBundle();
        restoreTypicalDataInDatastore();
    }
    
    @Test
    public void testAccessControl() throws Exception{
        
        FeedbackSessionAttributes fs = dataBundle.feedbackSessions.get("session1InCourse1");
        
        String[] submissionParams = new String[]{
                Const.ParamsNames.COURSE_ID, fs.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, fs.feedbackSessionName
        };
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception{
        gaeSimulation.loginAsInstructor(dataBundle.instructors.get("instructor1OfCourse1").googleId);
        FeedbackSessionAttributes session = dataBundle.feedbackSessions.get("session2InCourse1");
        String[] paramsWithoutSortType = {
                Const.ParamsNames.COURSE_ID, session.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.feedbackSessionName
        };
        String[] paramsWithSortTypeTable = {
                Const.ParamsNames.COURSE_ID, session.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "table"
        };
        String[] paramsWithSortTypeGiver = {
                Const.ParamsNames.COURSE_ID, session.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "giver"
        };
        String[] paramsWithSortTypeRecipient = {
                Const.ParamsNames.COURSE_ID, session.courseId,
                Const.ParamsNames.FEEDBACK_SESSION_NAME, session.feedbackSessionName,
                Const.ParamsNames.FEEDBACK_RESULTS_SORTTYPE, "recipient"
        };
        
        ______TS("Success Case 1: no params");
        
        InstructorFeedbackResultsPageAction action = getAction(new String[]{});
        ActionResult result = action.executeAndPostProcess();
        
        assertEquals(Const.ActionURIs.INSTRUCTOR_HOME_PAGE +
                "?error=false&user=idOfInstructor1OfCourse1", 
                result.getDestinationWithParams());
        assertEquals("", result.getStatusMessage());
        assertFalse(result.isError);
        
        action = getAction(new String[]{
                Const.ParamsNames.COURSE_ID, session.courseId
        });
        result = action.executeAndPostProcess();
        
        assertEquals(Const.ActionURIs.INSTRUCTOR_HOME_PAGE +
                "?error=false&user=idOfInstructor1OfCourse1", 
                result.getDestinationWithParams());
        assertEquals("", result.getStatusMessage());
        assertFalse(result.isError);
        
        ______TS("Success Case 2: no sortType param");
        
        action = getAction(paramsWithoutSortType);
        result = action.executeAndPostProcess();
        
        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT +
                "?error=false&user=idOfInstructor1OfCourse1",
                result.getDestinationWithParams());
        assertEquals("", result.getStatusMessage());
        assertFalse(result.isError);
        
        ______TS("Success Case 3: sortType table");
        
        action = getAction(paramsWithSortTypeTable);
        result = action.executeAndPostProcess();
        
        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_TABLE +
                "?error=false&user=idOfInstructor1OfCourse1",
                result.getDestinationWithParams());
        assertEquals("", result.getStatusMessage());
        assertFalse(result.isError);
        
        ______TS("Success Case 4: sortType giver");
        
        action = getAction(paramsWithSortTypeGiver);
        result = action.executeAndPostProcess();
        
        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_GIVER +
                "?error=false&user=idOfInstructor1OfCourse1",
                result.getDestinationWithParams());
        assertEquals("", result.getStatusMessage());
        assertFalse(result.isError);
        
        ______TS("Success Case 5: sortType recipient");
        
        action = getAction(paramsWithSortTypeRecipient);
        result = action.executeAndPostProcess();
        
        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACK_RESULTS_BY_RECIPIENT +
                "?error=false&user=idOfInstructor1OfCourse1",
                result.getDestinationWithParams());
        assertEquals("", result.getStatusMessage());
        assertFalse(result.isError);
    }
    
    private InstructorFeedbackResultsPageAction getAction(String[] params){
        return (InstructorFeedbackResultsPageAction) gaeSimulation.getActionObject(uri, params);
    }

}
