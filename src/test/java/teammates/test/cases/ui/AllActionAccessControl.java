package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;

public class AllActionAccessControl extends BaseActionTest {
    
    private String[] submissionParams = new String[]{};
    private final DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
        uri = Const.ActionURIs.ADMIN_ACCOUNT_DETAILS_PAGE;
        restoreTypicalDataInDatastore();
    }
    
    @Test
    public void AdminAccountDelete() throws Exception {
        uri = Const.ActionURIs.ADMIN_ACCOUNT_DELETE;
        verifyOnlyAdminsCanAccess(submissionParams);
    }
    
    @Test
    public void AdminAccountDetailsPage() throws Exception{
        uri = Const.ActionURIs.ADMIN_ACCOUNT_DETAILS_PAGE;
        verifyOnlyAdminsCanAccess(submissionParams);
    }
    
    @Test
    public void AdminAccountManagementPage() throws Exception{
        uri = Const.ActionURIs.ADMIN_ACCOUNT_MANAGEMENT_PAGE;
        verifyOnlyAdminsCanAccess(submissionParams);
    }
    
    @Test
    public void AdminActivityLogPage() throws Exception{
        uri = Const.ActionURIs.ADMIN_ACTIVITY_LOG_PAGE;
        verifyOnlyAdminsCanAccess(submissionParams);
    }
    
    @Test
    public void AdminExceptionTest() throws Exception{
        uri = Const.ActionURIs.ADMIN_EXCEPTION_TEST;
        verifyOnlyAdminsCanAccess(submissionParams);
    }
    
    @Test
    public void AdminHomePage() throws Exception{
        uri = Const.ActionURIs.ADMIN_HOME_PAGE;
        verifyOnlyAdminsCanAccess(submissionParams);
    }
    
    @Test
    public void AdminInstructorAccountAdd() throws Exception{
        uri = Const.ActionURIs.ADMIN_INSTRUCTORACCOUNT_ADD;
        verifyOnlyAdminsCanAccess(submissionParams);
    }
    
    @Test
    public void AdminSearchPage() throws Exception{
        uri = Const.ActionURIs.ADMIN_SEARCH_PAGE;
        verifyOnlyAdminsCanAccess(submissionParams);
    }
    
    @Test
    public void testAccessControl() throws Exception {
        uri = Const.ActionURIs.INSTRUCTOR_EVAL_STATS_PAGE;
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        EvaluationAttributes accessableEvaluation = dataBundle.evaluations.get("evaluation1InCourse1");
        submissionParams = new String[] { Const.ParamsNames.EVALUATION_NAME, accessableEvaluation.name,
                                          Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId};
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);

        // reset the params
        submissionParams = new String[]{};
    }
    
    @Test
    public void InstructorFeedbackStatsPage() throws Exception{
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACK_STATS_PAGE;
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        FeedbackSessionAttributes accessableFeedbackSession = dataBundle.feedbackSessions.get("session1InCourse1");
        submissionParams = new String[] { Const.ParamsNames.FEEDBACK_SESSION_NAME, accessableFeedbackSession.feedbackSessionName,
                                            Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId};
        
        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        
        // reset the params
        submissionParams = new String[]{};

    }

}
