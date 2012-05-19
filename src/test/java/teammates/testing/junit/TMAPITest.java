package teammates.testing.junit;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import teammates.Common;
import teammates.DataBundle;
import teammates.jdo.*;
import teammates.testing.lib.SharedLib;
import teammates.testing.lib.TMAPI;
import static org.junit.Assert.*;



public class TMAPITest {
	
	//TODO: change this to 'target' directory
	private String TEST_DATA_FOLDER = "src/test/resources/data/";

	
	@BeforeClass
	public static void setUp() {
		
	}

	@AfterClass
	public static void tearDown() {
		
	}

	@Test
	public void testGetCoursesByCoordId() {
		
		String[] courses = TMAPI.getCoursesByCoordId("nonExistentCoord");
		//testing for non-existent coordinator
		assertEquals("[]",Arrays.toString(courses));
		
		//TODO: cascade delete coordinators and recreate
		String coord1Id = "AST.TGCBCI.coord1";
		String course1OfCoord1 = "AST.TGCBCI.course1OfCoord1";
		String course2OfCoord1 = "AST.TGCBCI.course2OfCoord1";
		TMAPI.createCourse(new teammates.testing.object.Course(course1OfCoord1, "APIServletTest testGetCoursesByCoordId course1OfCoord1"), 
				coord1Id);
		TMAPI.createCourse(new teammates.testing.object.Course(course2OfCoord1, "APIServletTest testGetCoursesByCoordId course2OfCoord1"), 
				coord1Id);
		
		//add a course that belongs to a different coordinator
		String coord2Id = "AST.TGCBCI.coord2";
		String course1OfCoord2 = "AST.TGCBCI.course1OfCoord2";
		TMAPI.createCourse(new teammates.testing.object.Course(course1OfCoord2, "APIServletTest testGetCoursesByCoordId course1OfCoord2"), 
				coord2Id);

		courses = TMAPI.getCoursesByCoordId(coord1Id);
		assertEquals("["+course1OfCoord1+", "+course2OfCoord1+"]",Arrays.toString(courses));

		//TODO: delete coordinators
	}
	
	@Test
	public void testDeleteCourseByIdNonCascade() throws InterruptedException{
		//TODO: cascade delete coordinators and recreate
		String coord1Id = "AST.TDCBINC.coord1";
		String course1OfCoord1 = "AST.TDCBINC.course1OfCoord1";
		String course2OfCoord1 = "AST.TDCBINC.course2OfCoord1"; 
		TMAPI.createCourse(new teammates.testing.object.Course(course1OfCoord1, "APIServletTest testDeleteCourseByIdNonCascade course1OfCoord1"), 
				coord1Id);
		TMAPI.createCourse(new teammates.testing.object.Course(course2OfCoord1, "APIServletTest testDeleteCourseByIdNonCascade course2OfCoord1"), 
				coord1Id);
		
		String[] courses = TMAPI.getCoursesByCoordId(coord1Id);
		assertEquals("["+course1OfCoord1+", "+course2OfCoord1+"]",Arrays.toString(courses));
		
		TMAPI.deleteCourseByIdNonCascade(course1OfCoord1);
		courses = TMAPI.getCoursesByCoordId(coord1Id);
		assertEquals("["+course2OfCoord1+"]",Arrays.toString(courses));
		
		//trying to delete non-existent course
		TMAPI.deleteCourseByIdNonCascade("AST.TDCBINC.nonexistentcourse");
		courses = TMAPI.getCoursesByCoordId(coord1Id);
		assertEquals("["+course2OfCoord1+"]",Arrays.toString(courses));
		
		TMAPI.deleteCourseByIdNonCascade(course2OfCoord1);
		courses = TMAPI.getCoursesByCoordId(coord1Id);
		assertEquals("[]",Arrays.toString(courses));
		
	}
	
	@Test
	public void testCoordManipulation() {
		
		String coord1Id = "AST.testCoordManipulation.coord1@somemail.com";
		String coord1Name = "AST TCM Coordinator1";
		String coord1Email = "AST.testCoordManipulation.coord1@gmail.com";
		
		//test for accessing non-existent coord
		assertEquals("null",TMAPI.getCoordAsJason("AST.testCoordManipulation.nonexistentId"));	
				
		//delete coord if already exists
		TMAPI.deleteCoordByIdNonCascading(coord1Id);
		assertEquals("null",TMAPI.getCoordAsJason(coord1Id));
		
		//try to delete again, to ensure it does not crash
		TMAPI.deleteCoordByIdNonCascading(coord1Id);
		
		//test creation, and accessing existing coord
		TMAPI.createCoord(coord1Id, coord1Name, coord1Email);
		assertEquals("{\"googleID\":\""+coord1Id+"\",\"name\":\"" +coord1Name+"\",\"email\":\"" +coord1Email+"\"}",TMAPI.getCoordAsJason(coord1Id));
		
		//creating the same coord, to ensure it does not crash
		TMAPI.createCoord(coord1Id, coord1Name, coord1Email);
		
		//delete existing coord
		TMAPI.deleteCoordByIdNonCascading(coord1Id);
		assertEquals("null",TMAPI.getCoordAsJason(coord1Id));
		
		//TODO: test for coord cascade delete
	}
	
	@Test
	public void testPersistDataBundle(){
		String jsonString = SharedLib.getFileContents(TEST_DATA_FOLDER+"typicalDataBundle.json");
		String status = TMAPI.persistNewDataBundle(jsonString);
		assertEquals(Common.BACKEND_STATUS_SUCCESS, status);
		verifyExistInDatastore(jsonString);
	}

	@Test 
	public void testDataBundle(){
		String jsonString = SharedLib.getFileContents(TEST_DATA_FOLDER+"typicalDataBundle.json");
		Gson gson = Common.getTeammatesGson();
				
		DataBundle data = gson.fromJson(jsonString, DataBundle.class);
		
		Coordinator typicalCoord1 = data.coords.get("typical.coord1");
		assertEquals("typical.coord1",typicalCoord1.getGoogleID());
		assertEquals("Typical Coordinator1",typicalCoord1.getName());
		assertEquals("typical.coord1@gmail.com",typicalCoord1.getEmail());
		
		Coordinator typicalCoord2 = data.coords.get("typical.coord2");
		assertEquals("typical.coord2",typicalCoord2.getGoogleID());
		assertEquals("Typical Coordinator2",typicalCoord2.getName());
		assertEquals("typical.coord2@gmail.com",typicalCoord2.getEmail());
		
		Course course1 = data.courses.get("course1");
		assertEquals("course1-id",course1.getID());
		assertEquals("course 1 name",course1.getName());
		assertEquals("typical.coord1",course1.getCoordinatorID());
		
		Student student1InCourse1 = data.students.get("student1InCourse1");
		assertEquals("student1InCourse1",student1InCourse1.getID());
		assertEquals("student1 In Course1",student1InCourse1.getName());
		assertEquals("Team 1.1",student1InCourse1.getTeamName());
		assertEquals("comment for student1InCourse1",student1InCourse1.getComments());
		assertEquals("profile summary for student1InCourse1",student1InCourse1.getProfileSummary());
		assertEquals("course1",student1InCourse1.getCourseID());
		
		Student student2InCourse2 = data.students.get("student2InCourse2");
		assertEquals("student2InCourse2",student2InCourse2.getID());
		assertEquals("student2 In Course2",student2InCourse2.getName());
		assertEquals("Team 2.1",student2InCourse2.getTeamName());
		
		Evaluation evaluation1 = data.evaluations.get("evalution1InCourse1");
		assertEquals("evaluation1 In Course1", evaluation1.getName());
		assertEquals("course1", evaluation1.getCourseID());
		assertEquals("instructions for evalution1InCourse1", evaluation1.getInstructions());
		assertEquals(10, evaluation1.getGracePeriod());
		assertEquals(true, evaluation1.isCommentsEnabled());
		assertEquals("Sun Apr 01 23:59:00 SGT 2012",evaluation1.getStart().toString());
		assertEquals("Tue Apr 30 23:59:00 SGT 2013",evaluation1.getDeadline().toString());
		assertEquals(true, evaluation1.isActivated());
		assertEquals(false, evaluation1.isPublished());
		assertEquals(2.0, evaluation1.getTimeZone(),0.01);
		
		Evaluation evaluation2 = data.evaluations.get("evalution2InCourse1");
		assertEquals("evaluation2 In Course1", evaluation2.getName());
		assertEquals("course1", evaluation2.getCourseID());
		
		Submission submissionFromS1C1ToS2C1 = data.submissions.get("submissionFromS1C1ToS2C1");
		assertEquals("student1InCourse1@gmail.com", submissionFromS1C1ToS2C1.getFromStudent());
		assertEquals("student2InCourse1@gmail.com", submissionFromS1C1ToS2C1.getToStudent());
		assertEquals("course1", submissionFromS1C1ToS2C1.getCourseID());
		assertEquals("evaluation1 In Course1", submissionFromS1C1ToS2C1.getEvaluationName());
		assertEquals(10, submissionFromS1C1ToS2C1.getPoints());
		assertEquals("Team 1.1", submissionFromS1C1ToS2C1.getTeamName());
		//since justification filed is of Text type, we have to use it's .getValue() method to access the string contained inside it
		assertEquals("justification of student1InCourse1 rating to student2InCourse1", submissionFromS1C1ToS2C1.getJustification().getValue());
		assertEquals("comments from student1InCourse1 to student2InCourse1", submissionFromS1C1ToS2C1.getCommentsToStudent().getValue());
	
		Submission submissionFromS2C1ToS1C1 = data.submissions.get("submissionFromS2C1ToS1C1");
		assertEquals("student2InCourse1@gmail.com", submissionFromS2C1ToS1C1.getFromStudent());
		assertEquals("student1InCourse1@gmail.com", submissionFromS2C1ToS1C1.getToStudent());
		
		TeamFormingSession tfsInCourse1 = data.teamFormingSessions.get("tfsInCourse1");
		assertEquals("course1", tfsInCourse1.getCourseID());
		assertEquals(8.0, tfsInCourse1.getTimeZone(),0.01);
		assertEquals("Sun Apr 01 23:59:00 SGT 2012", tfsInCourse1.getStart().toString());
		assertEquals("Sun Apr 15 23:59:00 SGT 2012", tfsInCourse1.getDeadline().toString());
		assertEquals("instructions for tfsInCourse1", tfsInCourse1.getInstructions());
		assertEquals("profile template for tfsInCourse1", tfsInCourse1.getProfileTemplate());
		assertEquals(10, tfsInCourse1.getGracePeriod());
		assertEquals(false, tfsInCourse1.isActivated());
		
		TeamProfile profileOfTeam1_1 = data.teamProfiles.get("profileOfTeam1.1");
		assertEquals("course1", profileOfTeam1_1.getCourseID());
		assertEquals("course 1 name", profileOfTeam1_1.getCourseName());
		assertEquals("Team 1.1", profileOfTeam1_1.getTeamName());
		assertEquals("team profile of Team 1.1", profileOfTeam1_1.getTeamProfile().getValue());
	}
	
	private void verifyExistInDatastore(String dataBundleJsonString) {
		Gson gson = Common.getTeammatesGson();
		
		DataBundle data = gson.fromJson(dataBundleJsonString, DataBundle.class);
		HashMap<String, Coordinator> coords = data.coords;
		for (Coordinator expectedCoord : coords.values()) {
			String coordJsonString = TMAPI.getCoordAsJason(expectedCoord.getGoogleID());
			Coordinator actualCoord = gson.fromJson(coordJsonString, Coordinator.class);
			assertEquals(gson.toJson(expectedCoord), gson.toJson(actualCoord));
		}
		
		HashMap<String, Course> courses = data.courses;
		for (Course expectedCourse : courses.values()) {
			String courseJsonString = TMAPI.getCourseAsJason(expectedCourse.getID());
			Course actualCourse = gson.fromJson(courseJsonString, Course.class);
			assertEquals(gson.toJson(expectedCourse), gson.toJson(actualCourse));
		}
		
		HashMap<String, Student> students = data.students;
		for (Student expectedStudent : students.values()) {
			String studentJsonString = TMAPI.getStudentAsJason(expectedStudent.getCourseID(), expectedStudent.getEmail());
			Student actualStudent = gson.fromJson(studentJsonString, Student.class);
			assertEquals(gson.toJson(expectedStudent), gson.toJson(actualStudent));
		}
		
		HashMap<String, Evaluation> evaluations = data.evaluations;
		for (Evaluation expectedEvaluation : evaluations.values()) {
			String evaluationJsonString = TMAPI.getEvaluationAsJason(expectedEvaluation.getCourseID(), expectedEvaluation.getName());
			Evaluation actualEvaluation = gson.fromJson(evaluationJsonString, Evaluation.class);
			//equalize id field before comparing (because id field is autogenerated by GAE)
			expectedEvaluation.id = actualEvaluation.id;
			assertEquals(gson.toJson(expectedEvaluation), gson.toJson(actualEvaluation));
		}
		
		HashMap<String, Submission> submissions = data.submissions;
		for (Submission expectedSubmission : submissions.values()) {
			String submissionsJsonString = TMAPI.getSubmissionAsJason(
					expectedSubmission.getCourseID(), 
					expectedSubmission.getEvaluationName(),
					expectedSubmission.getFromStudent(),
					expectedSubmission.getToStudent());
			Submission actualSubmission = gson.fromJson(submissionsJsonString, Submission.class);
			//equalize id field before comparing (because id field is autogenerated by GAE)
			expectedSubmission.id = actualSubmission.id;
			assertEquals(gson.toJson(expectedSubmission), gson.toJson(actualSubmission));
		}
		
		HashMap<String, TeamFormingSession> teamFormingSessions = data.teamFormingSessions;
		for (TeamFormingSession expectedTeamFormingSession : teamFormingSessions.values()) {
			String teamFormingSessionsJsonString = TMAPI.getTfsAsJason(expectedTeamFormingSession.getCourseID());
			TeamFormingSession actualTeamFormingSession = gson.fromJson(teamFormingSessionsJsonString, TeamFormingSession.class);
			//equalize id field before comparing (because id field is autogenerated by GAE)
			expectedTeamFormingSession.id = actualTeamFormingSession.id;
			assertEquals(gson.toJson(expectedTeamFormingSession), gson.toJson(actualTeamFormingSession));
		}
		
		HashMap<String, TeamProfile> teamProfiles = data.teamProfiles;
		for (TeamProfile expectedTeamProfile : teamProfiles.values()) {
			String teamProfileJsonString = TMAPI.getTeamProfileAsJason(expectedTeamProfile.getCourseID(), expectedTeamProfile.getTeamName());
			TeamProfile actualTeamProfile = gson.fromJson(teamProfileJsonString, TeamProfile.class);
			//equalize id field before comparing (because id field is autogenerated by GAE)
			expectedTeamProfile.id = actualTeamProfile.id;
			assertEquals(gson.toJson(expectedTeamProfile), gson.toJson(actualTeamProfile));
		}
		
	}


	
}
