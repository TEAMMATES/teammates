package teammates.testing.junit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.*;

import teammates.testing.lib.TMAPI;
import teammates.testing.object.*;
import static org.junit.Assert.*;



public class APIServletTest {

	
	@BeforeClass
	public static void setUp() {
		
	}

	@AfterClass
	public static void tearDown() {
		
	}

	@Test
	public void testGetCoursesByCoordId() {
		
		String[] courses = TMAPI.getCourses("nonExistentCoord");
		//testing for non-existent coordinator
		assertEquals("[]",Arrays.toString(courses));
		
		//TODO: cascade delete coordinators and recreate
		String coordId1 = "AST.TGCBCI.coord1";
		String course1OfCoord1 = "AST.TGCBCI.course1OfCoord1";
		String course2OfCoord1 = "AST.TGCBCI.course2OfCoord1";
		TMAPI.createCourse(new Course(course1OfCoord1, "APIServletTest testGetCoursesByCoordId course1OfCoord1"), 
				coordId1);
		TMAPI.createCourse(new Course(course2OfCoord1, "APIServletTest testGetCoursesByCoordId course2OfCoord1"), 
				coordId1);
		
		//add a course that belongs to a different coordinator
		String coordId2 = "AST.TGCBCI.coord2";
		String course1OfCoord2 = "AST.TGCBCI.course1OfCoord2";
		TMAPI.createCourse(new Course(course1OfCoord2, "APIServletTest testGetCoursesByCoordId course1OfCoord2"), 
				coordId2);

		courses = TMAPI.getCourses(coordId1);
		assertEquals("["+course1OfCoord1+", "+course2OfCoord1+"]",Arrays.toString(courses));

		//TODO: delete coordinators
	}
	
	@Test
	public void testDeleteCourseByIdNonCascade() throws InterruptedException{
		//TODO: cascade delete coordinators and recreate
		String coordId1 = "AST.TDCBINC.coord1";
		String course1OfCoord1 = "AST.TDCBINC.course1OfCoord1";
		String course2OfCoord1 = "AST.TDCBINC.course2OfCoord1"; 
		TMAPI.createCourse(new Course(course1OfCoord1, "APIServletTest testDeleteCourseByIdNonCascade course1OfCoord1"), 
				coordId1);
		TMAPI.createCourse(new Course(course2OfCoord1, "APIServletTest testDeleteCourseByIdNonCascade course2OfCoord1"), 
				coordId1);
		
		String[] courses = TMAPI.getCourses(coordId1);
		assertEquals("["+course1OfCoord1+", "+course2OfCoord1+"]",Arrays.toString(courses));
		
		TMAPI.deleteCourseByIdNonCascade(course1OfCoord1);
		courses = TMAPI.getCourses(coordId1);
		assertEquals("["+course2OfCoord1+"]",Arrays.toString(courses));
		
		//trying to delete non-existent course
		TMAPI.deleteCourseByIdNonCascade("AST.TDCBINC.nonexistentcourse");
		courses = TMAPI.getCourses(coordId1);
		assertEquals("["+course2OfCoord1+"]",Arrays.toString(courses));
		
		TMAPI.deleteCourseByIdNonCascade(course2OfCoord1);
		courses = TMAPI.getCourses(coordId1);
		assertEquals("[]",Arrays.toString(courses));
		
	}

	
}
