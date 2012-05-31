package teammates.testing.testcases;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import teammates.Common;
import teammates.Datastore;
import teammates.TeammatesServlet;
import teammates.testing.config.Config;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

/**
 * Tests API for Course - Add Page
 * 
 * @author Damith C. Rajapakse
 * @author Aldrian Obaja
 */
public class CoordCourseAddApiTest extends BaseTestCase{
	private final static LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
	private final static TeammatesServlet ts = new TeammatesServlet();
	private final String COURSE_ID = "CCAAT.CS2103";
	private final String COURSE_ID_DIFF = "CCAAT.CS1010";
	private final String COURSE_NAME = "CCAAT Software Engineering";
	private final String COURSE_NAME_DIFF = "CCAAT Programming Methodology";
	private final String COURSE_ID_UPPER = "CCAAT.CS1020";
	private final String COURSE_NAME_UPPER = "CCAAT Programming Methodology";
	private final String COURSE_ID_LOWER = COURSE_ID_UPPER.toLowerCase();
	private final String COURSE_NAME_LOWER = COURSE_NAME_UPPER.toLowerCase();
	private final String COURSE_ID_DELETE = "CCAAT.CS2104";
	private final String COURSE_NAME_DELETE = "CCAAT Programming Language Concepts";
	private final String GOOGLE_ID = Config.inst().TEAMMATES_COORD_ID;
	
	@BeforeClass
	public static void setUp() {
		assertTrue(true);
		printTestClassHeader("CoordCourseAddApiTest");
		helper.setUp();
		try{
			ts.init();
			Datastore.initialize();
		}catch(IllegalStateException e){
			System.out.println("Error in initializing local datastore: (this is usually OK)");
			e.printStackTrace();
		} catch (ServletException e) {
			System.out.println("Error in initializing servlet");
			e.printStackTrace();
		}
	}
	
	@Test
	public void testCoordCourseAdd() throws IOException, ServletException {
		String response;
		
		//success
		response = ts.coordinatorAddCourse(COURSE_ID, COURSE_NAME, GOOGLE_ID);
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_ADDED, response);

		//duplicate id and name
		response = ts.coordinatorAddCourse(COURSE_ID, COURSE_NAME, GOOGLE_ID);
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_EXISTS, response);
		
		//different id, same name
		response = ts.coordinatorAddCourse(COURSE_ID_DIFF, COURSE_NAME, GOOGLE_ID);
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_ADDED, response);
		
		//same id, different name
		response = ts.coordinatorAddCourse(COURSE_ID, COURSE_NAME_DIFF, GOOGLE_ID);
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_EXISTS, response);
		
		//different coordinator, same id and name
		response = ts.coordinatorAddCourse(COURSE_ID, COURSE_NAME, "different coordinator");
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_EXISTS, response);
	
		//empty id
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_INVALID, ts.coordinatorAddCourse("", COURSE_NAME, GOOGLE_ID));
		
		//empty name
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_INVALID, ts.coordinatorAddCourse(COURSE_ID, "", GOOGLE_ID));
		
		//long id
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_INVALID, ts.coordinatorAddCourse("CCAAT.LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG", COURSE_NAME, GOOGLE_ID));
		
		//long name
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_INVALID, ts.coordinatorAddCourse(COURSE_ID, "CCAAT LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG", GOOGLE_ID));
	
		//invalid char in id
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_INVALID, ts.coordinatorAddCourse("CCAAT.CS 1010", COURSE_NAME, GOOGLE_ID));
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_INVALID, ts.coordinatorAddCourse("CCAAT.CS~1010", COURSE_NAME, GOOGLE_ID));
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_INVALID, ts.coordinatorAddCourse("CCAAT.CS!1010", COURSE_NAME, GOOGLE_ID));
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_INVALID, ts.coordinatorAddCourse("CCAAT.CS@1010", COURSE_NAME, GOOGLE_ID));
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_INVALID, ts.coordinatorAddCourse("CCAAT.CS#1010", COURSE_NAME, GOOGLE_ID));
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_INVALID, ts.coordinatorAddCourse("CCAAT.CS%1010", COURSE_NAME, GOOGLE_ID));
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_INVALID, ts.coordinatorAddCourse("CCAAT.CS^1010", COURSE_NAME, GOOGLE_ID));
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_INVALID, ts.coordinatorAddCourse("CCAAT.CS&1010", COURSE_NAME, GOOGLE_ID));
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_INVALID, ts.coordinatorAddCourse("CCAAT.CS*1010", COURSE_NAME, GOOGLE_ID));
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_INVALID, ts.coordinatorAddCourse("CCAAT.CS(1010", COURSE_NAME, GOOGLE_ID));
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_INVALID, ts.coordinatorAddCourse("CCAAT.CS)1010", COURSE_NAME, GOOGLE_ID));
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_INVALID, ts.coordinatorAddCourse("CCAAT.CS+1010", COURSE_NAME, GOOGLE_ID));
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_INVALID, ts.coordinatorAddCourse("CCAAT.CS=1010", COURSE_NAME, GOOGLE_ID));
	}
	
	@Test
	public void testCoordCourseAddIDCaseSensitivity() throws IOException, ServletException {
		String response;
		
		// Add course with lowercase courseID
		response = ts.coordinatorAddCourse(COURSE_ID_LOWER, COURSE_NAME_LOWER, GOOGLE_ID);
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_ADDED,response);
		
		// Add course with same ID but in uppercase, with different course name
		response = ts.coordinatorAddCourse(COURSE_ID_UPPER, COURSE_NAME_UPPER, GOOGLE_ID);
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_ADDED,response);
		
		// Add course with same ID but in uppercase, with same course name
		response = ts.coordinatorAddCourse(COURSE_ID_UPPER, COURSE_NAME_UPPER, GOOGLE_ID);
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_EXISTS,response);
	}
	
	@Test
	public void testCoordCourseDelete() throws IOException, ServletException{
		String response;
		
		// Add course first, verifies that it's added
		response = ts.coordinatorAddCourse(COURSE_ID_DELETE, COURSE_NAME_DELETE, GOOGLE_ID);
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_ADDED,response);
		
		// Delete the course
		response = ts.coordinatorDeleteCourse(COURSE_ID_DELETE);
		assertEquals(Common.COORD_DELETE_COURSE_RESPONSE_DELETED,response);
		
		// Delete non-existant course
		response = ts.coordinatorDeleteCourse(COURSE_ID_DELETE+".X");
		assertEquals(Common.COORD_DELETE_COURSE_RESPONSE_DELETED,response);
	}
	
	@AfterClass
	public static void tearDown() {
		ts.destroy();
		helper.tearDown();
		printTestClassFooter("CoordCourseAddApiTest");
	}
}
