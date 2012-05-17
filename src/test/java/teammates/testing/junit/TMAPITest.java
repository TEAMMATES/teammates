package teammates.testing.junit;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.*;

import com.google.gson.Gson;
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
		assertEquals("null",TMAPI.getCoordById("AST.testCoordManipulation.nonexistentId"));	
				
		//delete coord if already exists
		TMAPI.deleteCoordByIdNonCascading(coord1Id);
		assertEquals("null",TMAPI.getCoordById(coord1Id));
		
		//try to delete again, to ensure it does not crash
		TMAPI.deleteCoordByIdNonCascading(coord1Id);
		
		//test creation, and accessing existing coord
		TMAPI.createCoord(coord1Id, coord1Name, coord1Email);
		assertEquals("{\"googleID\":\""+coord1Id+"\",\"name\":\"" +coord1Name+"\",\"email\":\"" +coord1Email+"\"}",TMAPI.getCoordById(coord1Id));
		
		//creating the same coord, to ensure it does not crash
		TMAPI.createCoord(coord1Id, coord1Name, coord1Email);
		
		//delete existing coord
		TMAPI.deleteCoordByIdNonCascading(coord1Id);
		assertEquals("null",TMAPI.getCoordById(coord1Id));
		
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
		Gson gson = new Gson();
		DataBundle data = gson.fromJson(jsonString, DataBundle.class);
		assertEquals("typical.coord1",data.coords.get("demo coord").getGoogleID());
		assertEquals("Coordinator",data.coords.get("demo coord").getName());
		assertEquals("teammates.coord@gmail.com",data.coords.get("demo coord").getEmail());
		assertEquals("typical.coord2",data.coords.get("test coord").getGoogleID());
		assertEquals("Demo Coordinator",data.coords.get("test coord").getName());
		assertEquals("teammates.demo.coord@gmail.com",data.coords.get("test coord").getEmail());
		
		assertEquals("course1-id",data.courses.get("course1").getID());
		assertEquals("course 1 name",data.courses.get("course1").getName());
		assertEquals("typical.coord1",data.courses.get("course1").getCoordinatorID());
		
		assertEquals("student1InCourse1",data.students.get("student1InCourse1").getID());
		assertEquals("student1 In Course1",data.students.get("student1InCourse1").getName());
		assertEquals("Team 1.1",data.students.get("student1InCourse1").getTeamName());
		assertEquals("comment for student1InCourse1",data.students.get("student1InCourse1").getComments());
		assertEquals("profile summary for student1InCourse1",data.students.get("student1InCourse1").getProfileSummary());
		assertEquals("course1",data.students.get("student1InCourse1").getCourseID());
		
		assertEquals("student2InCourse2",data.students.get("student2InCourse2").getID());
		assertEquals("student2 In Course2",data.students.get("student2InCourse2").getName());
		assertEquals("Team 2.1",data.students.get("student2InCourse2").getTeamName());
		
		assertEquals("evalution1 In Course1", data.evaluations.get("evalution1InCourse1").getName());
		assertEquals("course1", data.evaluations.get("evalution1InCourse1").getCourseID());
		assertEquals("instructions for evalution1InCourse1", data.evaluations.get("evalution1InCourse1").getInstructions());
		assertEquals(10, data.evaluations.get("evalution1InCourse1").getGracePeriod());
		assertEquals(true, data.evaluations.get("evalution1InCourse1").isCommentsEnabled());
		System.out.println(data.evaluations.get("evalution1InCourse1").getStart());
		
//		Evaluation temp = data.evaluations.get("evalution1InCourse1");
//		temp.setDeadline(new Date());
//		Date myDate = new Date();   
//		System.out.println("JSON : " + gson.toJson(myDate));
//		myDate = gson.fromJson("\"Apr 12, 2012 11:56:04 AM\"", Date.class);
//		System.out.println("Date : " + myDate);
	}
	
	private void verifyExistInDatastore(String dataBundleJsonString) {
		Gson gson = new Gson();
		
		DataBundle data = gson.fromJson(dataBundleJsonString, DataBundle.class);
		HashMap<String, Coordinator> coords = data.coords;
		for (Coordinator expectedCoord : coords.values()) {
			String coordJsonString = TMAPI.getCoordById(expectedCoord.getGoogleID());
			Coordinator actualCoord = gson.fromJson(coordJsonString, Coordinator.class);
			assertEquals(gson.toJson(expectedCoord), gson.toJson(actualCoord));
		}
		
		HashMap<String, Course> courses = data.courses;
		for (Course expectedCourse : courses.values()) {
			String courseJsonString = TMAPI.getCourseById(expectedCourse.getID());
			Course actualCourse = gson.fromJson(courseJsonString, Course.class);
			assertEquals(gson.toJson(expectedCourse), gson.toJson(actualCourse));
		}
		
		HashMap<String, Student> students = data.students;
		for (Student expectedStudent : students.values()) {
			String studentJsonString = TMAPI.getStudentById(expectedStudent.getCourseID(), expectedStudent.getEmail());
			Student actualStudent = gson.fromJson(studentJsonString, Student.class);
			assertEquals(gson.toJson(expectedStudent), gson.toJson(actualStudent));
		}
		
	}


	
}
