package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.EvaluationAttributes.EvalStatus;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.logic.core.EvaluationsLogic;
import teammates.test.cases.common.EvaluationAttributesTest;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorHomePageAction;
import teammates.ui.controller.InstructorHomePageData;
import teammates.ui.controller.ShowPageResult;

public class InstructorHomePageActionTest extends BaseActionTest {

	DataBundle dataBundle;
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		uri = Const.ActionURIs.INSTRUCTOR_HOME_PAGE;
	}

	@BeforeMethod
	public void methodSetUp() throws Exception {
		dataBundle = getTypicalDataBundle();
		restoreTypicalDataInDatastore();
	}
	
	@Test
	public void testAccessControl() throws Exception{
		
		String[] submissionParams = new String[]{};
		verifyOnlyInstructorsCanAccess(submissionParams);
		
	}
	
	@Test
	public void testExecuteAndPostProcess() throws Exception{
		
		String[] submissionParams = new String[]{};
		
		______TS("insctructor with no courses");
		
		gaeSimulation.loginAsInstructor(dataBundle.accounts.get("instructorWithoutCourses").googleId);
		InstructorHomePageAction a = getAction(submissionParams);
		ShowPageResult r = getShowPageResult(a);
		AssertHelper.assertContainsRegex("/jsp/instructorHome.jsp?message=New+to+TEAMMATES%3F+You+may+wish+to+have+a+look+at+our+"
				+ "%3Ca+href%3D%27%2FinstructorHelp.html%23gs%27+target%3D%27_blank%27%3EGetting+Started+Guide%3C%2Fa%3E.%3Cbr%3EA+%3Ca+"
				+ "href%3D%27https%3A%2F%2Fyoutube.googleapis.com%2Fv%2FwCxBOUEiD6Q%26hd%3D1%26autoplay%3D1%26rel%3D0%27+target%3D%27_blank%27%3E"
				+ "video+tour%3C%2Fa%3E+is+also+available+in+our+%3Ca+href%3D%27%2Findex.html%27+target%3D%27_blank%27%3Ehome+page%3C%2Fa%3E.&"
				+ "error=false&user=instructorWithoutCourses", r.getDestinationWithParams());
		assertEquals(false, r.isError);
		assertEquals(Const.StatusMessages.HINT_FOR_NEW_INSTRUCTOR, r.getStatusMessage());
		
		InstructorHomePageData data = (InstructorHomePageData)r.data;
		assertEquals(0, data.courses.size());
		
		String expectedLogMessage = "TEAMMATESLOG|||instructorHomePage|||instructorHomePage|||true" +
				"|||Instructor|||Instructor Without Courses|||instructorWithoutCourses" +
				"|||iwc@yahoo.com|||instructorHome Page Load<br>Total Courses: 0|||/page/instructorHomePage" ;
		assertEquals(expectedLogMessage, a.getLogMessage());
		
		______TS("instructor with multiple courses, masquerade mode");
		
		gaeSimulation.loginAsAdmin("admin.user");
		
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
		String instructorWithMultipleCourses = dataBundle.accounts.get("instructor3").googleId;
		a = getAction(addUserIdToParams(instructorWithMultipleCourses, submissionParams));
		r = getShowPageResult(a);
		
		assertEquals("/jsp/instructorHome.jsp?error=false&user="+instructorWithMultipleCourses, r.getDestinationWithParams());
		assertEquals(false, r.isError);
		assertEquals("",r.getStatusMessage());
		
		data = (InstructorHomePageData)r.data;
		assertEquals(2, data.courses.size());
		
		
		expectedLogMessage = "TEAMMATESLOG|||instructorHomePage|||instructorHomePage|||true" +
				"|||Instructor(M)|||Instructor 3 of Course 1 and 2|||idOfInstructor3" +
				"|||instr3@course1n2.com|||instructorHome Page Load<br>Total Courses: 2|||/page/instructorHomePage" ;
		assertEquals(expectedLogMessage, a.getLogMessage());
		
	}

	@Test
	public void testIsNewInstructor() {
		//TODO: to be implemented
	}
	
	private InstructorHomePageAction getAction(String... params) throws Exception{
			return (InstructorHomePageAction) (gaeSimulation.getActionObject(uri, params));
	}
	
}
