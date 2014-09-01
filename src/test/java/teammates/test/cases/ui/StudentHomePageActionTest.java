package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.EvaluationAttributes.EvalStatus;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.EvaluationsLogic;
import teammates.storage.api.AccountsDb;
import teammates.test.cases.common.EvaluationAttributesTest;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.controller.StudentHomePageAction;
import teammates.ui.controller.StudentHomePageData;

public class StudentHomePageActionTest extends BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
		removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.STUDENT_HOME_PAGE;
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception{
        String unregUserId = "unreg.user";
        StudentAttributes student1InCourse1 = dataBundle.students.get("student1InCourse1");
        String studentId = student1InCourse1.googleId;
        String adminUserId = "admin.user";
        
        String[] submissionParams = new String[]{};
        
        ______TS("unregistered student");
        
        gaeSimulation.loginUser(unregUserId);
        StudentHomePageAction a = getAction(submissionParams);
        ShowPageResult r = getShowPageResult(a);
        AssertHelper.assertContainsRegex("/jsp/studentHome.jsp?error=false&user=unreg.user", r.getDestinationWithParams());
        assertEquals(false, r.isError);
        AssertHelper.assertContainsRegex("Welcome stranger :-){*}use the new Gmail address.",r.getStatusMessage());
        
        StudentHomePageData data = (StudentHomePageData)r.data;
        assertEquals(0, data.courses.size());
        assertEquals(0, data.evalSubmissionStatusMap.keySet().size());
        
        String expectedLogMessage = "TEAMMATESLOG|||studentHomePage|||studentHomePage" +
                "|||true|||Student|||null|||unreg.user|||null" +
                "|||Servlet Action Failure :Student with Google ID unreg.user does not exist|||/page/studentHomePage" ;
        assertEquals(expectedLogMessage, a.getLogMessage());
        
        ______TS("registered student with no courses");
        
        //Note: this can happen only if the course was deleted after the student joined it.
        // The 'welcome stranger' response is not really appropriate for this situation, but 
        //   we keep it because the situation is rare and not worth extra coding.
        
        //create a student account without courses
        AccountAttributes studentWithoutCourses = new AccountAttributes();
        studentWithoutCourses.googleId = "googleId.without.courses";
        studentWithoutCourses.name = "Student Without Courses";
        studentWithoutCourses.email = "googleId.without.courses@email.tmt";
        studentWithoutCourses.institute = "TEAMMATES Test Institute 5";
        studentWithoutCourses.isInstructor = false;
        studentWithoutCourses.studentProfile = new StudentProfileAttributes();
        studentWithoutCourses.studentProfile.googleId = studentWithoutCourses.googleId;
        AccountsDb accountsDb = new AccountsDb();
        accountsDb.createAccount(studentWithoutCourses);
        assertNotNull(accountsDb.getAccount(studentWithoutCourses.googleId));
        
        gaeSimulation.loginUser(studentWithoutCourses.googleId);
        a = getAction(submissionParams);
        r = getShowPageResult(a);
        AssertHelper.assertContainsRegex("/jsp/studentHome.jsp?error=false&user="+studentWithoutCourses.googleId, r.getDestinationWithParams());
        assertEquals(false, r.isError);
        AssertHelper.assertContainsRegex("Welcome stranger :-){*}use the new Gmail address.",r.getStatusMessage());
        
        data = (StudentHomePageData)r.data;
        assertEquals(0, data.courses.size());
        assertEquals(0, data.evalSubmissionStatusMap.keySet().size());
        
        expectedLogMessage = "TEAMMATESLOG|||studentHomePage|||studentHomePage|||true" +
                "|||Student|||Student Without Courses|||googleId.without.courses" +
                "|||googleId.without.courses@email.tmt|||Servlet Action Failure :Student with Google ID googleId.without.courses does not exist|||/page/studentHomePage" ;
        assertEquals(expectedLogMessage, a.getLogMessage());
        
        
        ______TS("typical user, masquerade mode");
        
        gaeSimulation.loginAsAdmin(adminUserId);
        studentId = dataBundle.students.get("student2InCourse2").googleId;
        
        //create a CLOSED evaluation
        EvaluationAttributes eval = EvaluationAttributesTest.generateValidEvaluationAttributesObject();
        String IdOfCourse2 = dataBundle.courses.get("typicalCourse2").id;
        eval.courseId = IdOfCourse2;
        eval.name = "Closed eval";
        eval.startTime = TimeHelper.getDateOffsetToCurrentTime(-2);
        eval.endTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        eval.setDerivedAttributes();
        assertEquals(EvalStatus.CLOSED, eval.getStatus());
        EvaluationsLogic evaluationsLogic = new EvaluationsLogic();
        evaluationsLogic.createEvaluationCascade(eval);
        
        //create a PUBLISHED evaluation
        eval.name = "published eval";
        eval.startTime = TimeHelper.getDateOffsetToCurrentTime(-2);
        eval.endTime = TimeHelper.getDateOffsetToCurrentTime(-1);
        eval.published = true;
        eval.setDerivedAttributes();
        assertEquals(EvalStatus.PUBLISHED, eval.getStatus());
        evaluationsLogic.createEvaluationCascade(eval);
        
        //access page in masquerade mode
        a = getAction(addUserIdToParams(studentId, submissionParams));
        r = getShowPageResult(a);
        
        assertEquals("/jsp/studentHome.jsp?error=false&user="+studentId, r.getDestinationWithParams());
        assertEquals(false, r.isError);
        assertEquals("",r.getStatusMessage());
        
        data = (StudentHomePageData)r.data;
        assertEquals(2, data.courses.size());
        assertEquals(5, data.evalSubmissionStatusMap.keySet().size());
        assertEquals(
                "{idOfTypicalCourse2%published eval=Published, " +
                "idOfTypicalCourse2%Closed eval=Closed, " +
                "idOfTypicalCourse1%evaluation2 In Course1=Pending, " +
                "idOfTypicalCourse1%evaluation1 In Course1=Submitted, " +
                "idOfTypicalCourse2%evaluation1 In Course2=Pending}", 
                data.evalSubmissionStatusMap.toString());
        
        
        expectedLogMessage = "TEAMMATESLOG|||studentHomePage|||studentHomePage|||true" +
                "|||Student(M)|||Student in two courses|||student2InCourse1|||student2InCourse1@gmail.tmt" +
                "|||studentHome Page Load<br>Total courses: 2|||/page/studentHomePage" ;
        assertEquals(expectedLogMessage, a.getLogMessage());
        
        
        ______TS("New student with no existing course, course join affected by eventual consistency");
        submissionParams = new String[]{Const.ParamsNames.CHECK_PERSISTENCE_COURSE, "idOfTypicalCourse1"};
        studentId = "newStudent";
        gaeSimulation.loginUser(studentId);
        a = getAction(submissionParams);
        r = getShowPageResult(a);
        data = (StudentHomePageData)r.data;
        assertEquals(1, data.courses.size());
        assertEquals("idOfTypicalCourse1", data.courses.get(0).course.id);
        assertEquals(2, data.evalSubmissionStatusMap.keySet().size());
        assertEquals(
                "{idOfTypicalCourse1%evaluation2 In Course1=Pending, " +
                "idOfTypicalCourse1%evaluation1 In Course1=Pending}", 
                data.evalSubmissionStatusMap.toString());
        
        
        ______TS("Registered student with existing courses, course join affected by eventual consistency");
        submissionParams = new String[]{Const.ParamsNames.CHECK_PERSISTENCE_COURSE, "idOfTypicalCourse2"};
        student1InCourse1 = dataBundle.students.get("student1InCourse1");
        studentId = student1InCourse1.googleId;
        gaeSimulation.loginUser(studentId);
        a = getAction(submissionParams);
        r = getShowPageResult(a);
        data = (StudentHomePageData)r.data;
        assertEquals(2, data.courses.size());
        assertEquals("idOfTypicalCourse2", data.courses.get(1).course.id);
        assertEquals(5, data.evalSubmissionStatusMap.keySet().size());
        assertEquals(
                "{idOfTypicalCourse2%published eval=Published, " +
                "idOfTypicalCourse2%Closed eval=Closed, " +
                "idOfTypicalCourse1%evaluation2 In Course1=Pending, " +
                "idOfTypicalCourse1%evaluation1 In Course1=Submitted, " +
                "idOfTypicalCourse2%evaluation1 In Course2=Pending}", 
                data.evalSubmissionStatusMap.toString());
        
        
        ______TS("Just joined course, course join not affected by eventual consistency and appears in list");
        submissionParams = new String[]{Const.ParamsNames.CHECK_PERSISTENCE_COURSE, "idOfTypicalCourse1"};
        student1InCourse1 = dataBundle.students.get("student1InCourse1");
        studentId = student1InCourse1.googleId;
        gaeSimulation.loginUser(studentId);
        a = getAction(submissionParams);
        r = getShowPageResult(a);
        data = (StudentHomePageData)r.data;
        assertEquals(1, data.courses.size());
        
        
        ______TS("Evaluation submission affected by eventual consistency");
        submissionParams = new String[]{Const.ParamsNames.CHECK_PERSISTENCE_EVALUATION, "idOfTypicalCourse1evaluation2 In Course1"};
        student1InCourse1 = dataBundle.students.get("student1InCourse1");
        studentId = student1InCourse1.googleId;
        gaeSimulation.loginUser(studentId);
        a = getAction(submissionParams);
        r = getShowPageResult(a);
        data = (StudentHomePageData)r.data;
        assertEquals(
                "{idOfTypicalCourse1%evaluation2 In Course1=Submitted, " +
                "idOfTypicalCourse1%evaluation1 In Course1=Submitted}",
                data.evalSubmissionStatusMap.toString());
        
        // delete additional sessions that were created
        CoursesLogic.inst().deleteCourseCascade("typicalCourse2");
        
    }

    private StudentHomePageAction getAction(String... params) throws Exception{
            return (StudentHomePageAction) (gaeSimulation.getActionObject(uri, params));
    }
    
}
