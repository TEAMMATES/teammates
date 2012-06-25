package teammates.testing.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashMap;

import org.junit.Test;

import com.google.gson.Gson;

import teammates.BackDoorLogic;
import teammates.api.Common;
import teammates.api.EntityAlreadyExistsException;
import teammates.api.InvalidParametersException;
import teammates.datatransfer.CoordData;
import teammates.datatransfer.CourseData;
import teammates.datatransfer.DataBundle;
import teammates.datatransfer.EvaluationData;
import teammates.datatransfer.StudentActionData;
import teammates.datatransfer.StudentData;
import teammates.datatransfer.SubmissionData;
import teammates.datatransfer.TeamProfileData;
import teammates.datatransfer.TfsData;

public class BackDoorLogicTest extends BaseTestCase{
	Gson gson = Common.getTeammatesGson();
	
	@Test
	public void testPersistDataBundle() throws Exception {
		printTestCaseHeader();
		BackDoorLogic logic = new BackDoorLogic();
		String jsonString = "";
			try {
				jsonString = Common.readFile(Common.TEST_DATA_FOLDER
						+ "/typicalDataBundle.json");
			} catch (Exception e) {
				e.printStackTrace();
			}
		DataBundle dataBundle = gson.fromJson(jsonString, DataBundle.class);
		// clean up the datastore first, to avoid clashes with existing data
		HashMap<String, CoordData> coords = dataBundle.coords;
		for (CoordData coord : coords.values()) {
			logic.deleteCoord(coord.id);
		}

		// try with empty dataBundle
		String status = logic.persistNewDataBundle(new DataBundle());
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);

		// try with typical dataBundle
		logic.persistNewDataBundle(dataBundle);
		verifyPresentInDatastore(jsonString);

		// try again, should throw exception
		try {
			logic.persistNewDataBundle(dataBundle);
			fail();
		} catch (EntityAlreadyExistsException e) {
		}

		// try with null
		DataBundle nullDataBundle = null;
		try {
			logic.persistNewDataBundle(nullDataBundle);
			fail();
		} catch (InvalidParametersException e) {
			assertEquals(Common.ERRORCODE_NULL_PARAMETER, e.errorCode);
		}

		// try with invalid parameters in an entity
		CourseData invalidCourse = new CourseData();
		dataBundle = new DataBundle();
		dataBundle.courses.put("invalid", invalidCourse);
		try {
			logic.persistNewDataBundle(dataBundle);
			fail();
		} catch (InvalidParametersException e) {
			assertEquals(Common.ERRORCODE_NULL_PARAMETER, e.errorCode);
		}

		// Not checking for invalid values in other entities because they
		// should be checked at lower level methods
	}
	
	private void verifyPresentInDatastore(String dataBundleJsonString)
			throws Exception {

		DataBundle data = gson.fromJson(dataBundleJsonString, DataBundle.class);
		HashMap<String, CoordData> coords = data.coords;
		for (CoordData expectedCoord : coords.values()) {
			LogicTest.verifyPresentInDatastore(expectedCoord);
		}

		HashMap<String, CourseData> courses = data.courses;
		for (CourseData expectedCourse : courses.values()) {
			LogicTest.verifyPresentInDatastore(expectedCourse);
		}

		HashMap<String, StudentData> students = data.students;
		for (StudentData expectedStudent : students.values()) {
			LogicTest.verifyPresentInDatastore(expectedStudent);
		}

		HashMap<String, EvaluationData> evaluations = data.evaluations;
		for (EvaluationData expectedEvaluation : evaluations.values()) {
			LogicTest.verifyPresentInDatastore(expectedEvaluation);
		}

		HashMap<String, SubmissionData> submissions = data.submissions;
		for (SubmissionData expectedSubmission : submissions.values()) {
			LogicTest.verifyPresentInDatastore(expectedSubmission);
		}

	}


}
