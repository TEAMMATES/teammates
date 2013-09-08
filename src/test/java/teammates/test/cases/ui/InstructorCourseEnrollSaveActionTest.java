package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import java.lang.reflect.Method;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;


import teammates.common.datatransfer.DataBundle;
import teammates.common.util.Const;
import teammates.ui.controller.InstructorCourseEnrollSaveAction;

public class InstructorCourseEnrollSaveActionTest extends BaseActionTest {

	DataBundle dataBundle;
	
	@BeforeClass
	public static void classSetUp() throws Exception {
		printTestClassHeader();
		uri = Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_SAVE;
	}

	@BeforeMethod
	public void caseSetUp() throws Exception {
		dataBundle = getTypicalDataBundle();
		restoreTypicalDataInDatastore();
	}
	
	@Test
	public void testAccessControl() throws Exception{
		
		String[] submissionParams = new String[]{
				Const.ParamsNames.COURSE_ID, dataBundle.instructors.get("instructor1OfCourse1").courseId,
				Const.ParamsNames.STUDENTS_ENROLLMENT_INFO, ""
		};
		
		verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
		
	}
	
	@Test
	public void testRemoveHeaderRowFromEnrollLines() throws Exception {
		
		______TS("header row exists");
		String header = "Team\tStudent Name\tEmail\tComment";
		String studentsInfo = "Team1\tJean Wong\tjean@email.com\tExchange student"
							+ Const.EOL + "Team1\tJames Tan\tjames@email.com\t";	
		String enrollLines = header + Const.EOL + studentsInfo;
	
		String result = invokeRemoveHeaderRowIfExist(enrollLines);
		assertEquals(studentsInfo, result);
		
		______TS("header row does not exist");
		studentsInfo = "Team1\tJean Wong\tjean@email.com\tExchange student"
							+ Const.EOL + "Team1\tJames Tan\tjames@email.com\t";	
	
		result = invokeRemoveHeaderRowIfExist(studentsInfo);
		assertEquals(studentsInfo, result);
		
		______TS("header row does not exist but first line contains column names");
		studentsInfo = "Team 1\tSample Name\tsample@email.com"
				+ Const.EOL + "Team1\tJames Tan\tjames@email.com\t";	

		result = invokeRemoveHeaderRowIfExist(studentsInfo);
		assertEquals(studentsInfo, result);
	}
	
	@Test
	public void testExecuteAndPostProcess() throws Exception{
		
		//TODO: implement this
	}
	
	private String invokeRemoveHeaderRowIfExist(String enrollLines) throws Exception {
		Method privateMethod = InstructorCourseEnrollSaveAction.class.getDeclaredMethod("removeHeaderRowIfExist",
							new Class[] { String.class });
		privateMethod.setAccessible(true);
		Object[] params = new Object[] { enrollLines };
		return (String) privateMethod.invoke(new InstructorCourseEnrollSaveAction(), params);
	}

}
