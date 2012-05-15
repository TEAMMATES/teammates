package teammates.testing.testcases;

import static org.junit.Assert.assertEquals;

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

public class CoordCourseAddApiTest extends BaseTestCase{
	private final static LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
	private final String COURSE_ID = "CCAAT.CS1010";
	private final String COURSE_NAME = "CCAAT Software Engineering";
	private final String GOOGLE_ID = Config.inst().TEAMMATES_COORD_ID;
	
	@BeforeClass
	public static void setUp() {
		printTestClassHeader("CoordCourseAddApiTest");
		helper.setUp();
		try{
			Datastore.initialize();
		}catch(IllegalStateException e){
			System.out.println("Error in initializing local datastore :");
			e.printStackTrace();
		}
	}
	

	@Test
	public void testCoordCourseAdd() throws IOException, ServletException {
		TeammatesServlet ts = new TeammatesServlet();
		String response;
		
		//success
		response = ts.coordinatorAddCourse(COURSE_ID, COURSE_NAME, GOOGLE_ID);
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_ADDED, response);

		//duplicate id and name
		response = ts.coordinatorAddCourse(COURSE_ID, COURSE_NAME, GOOGLE_ID);
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_EXISTS, response);
		
		//different id, same name
		response = ts.coordinatorAddCourse("id1010", COURSE_NAME, GOOGLE_ID);
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_ADDED, response);
		
		//same id, different name
		response = ts.coordinatorAddCourse(COURSE_ID, "different name", GOOGLE_ID);
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_EXISTS, response);
		
		//different coordinator, same id and name
		response = ts.coordinatorAddCourse(COURSE_ID, COURSE_NAME, "different coordinator");
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_EXISTS, response);
	
		//empty id
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_INVALID, ts.coordinatorAddCourse("", COURSE_NAME, GOOGLE_ID));
		
		//empty name
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_INVALID, ts.coordinatorAddCourse(COURSE_ID, "", GOOGLE_ID));
		
		//long id
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_INVALID, ts.coordinatorAddCourse("LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG", COURSE_NAME, GOOGLE_ID));
		
		//long name
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_INVALID, ts.coordinatorAddCourse(COURSE_ID, "LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG", GOOGLE_ID));
	
		//invalid char in id
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_INVALID, ts.coordinatorAddCourse("CS 1010", COURSE_NAME, GOOGLE_ID));
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_INVALID, ts.coordinatorAddCourse("CS~1010", COURSE_NAME, GOOGLE_ID));
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_INVALID, ts.coordinatorAddCourse("CS!1010", COURSE_NAME, GOOGLE_ID));
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_INVALID, ts.coordinatorAddCourse("CS@1010", COURSE_NAME, GOOGLE_ID));
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_INVALID, ts.coordinatorAddCourse("CS#1010", COURSE_NAME, GOOGLE_ID));
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_INVALID, ts.coordinatorAddCourse("CS%1010", COURSE_NAME, GOOGLE_ID));
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_INVALID, ts.coordinatorAddCourse("CS^1010", COURSE_NAME, GOOGLE_ID));
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_INVALID, ts.coordinatorAddCourse("CS&1010", COURSE_NAME, GOOGLE_ID));
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_INVALID, ts.coordinatorAddCourse("CS*1010", COURSE_NAME, GOOGLE_ID));
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_INVALID, ts.coordinatorAddCourse("CS(1010", COURSE_NAME, GOOGLE_ID));
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_INVALID, ts.coordinatorAddCourse("CS)1010", COURSE_NAME, GOOGLE_ID));
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_INVALID, ts.coordinatorAddCourse("CS+1010", COURSE_NAME, GOOGLE_ID));
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_INVALID, ts.coordinatorAddCourse("CS=1010", COURSE_NAME, GOOGLE_ID));
		
		ts.destroy();
	}
	
	@Test
	public void testCourseIDCaseSensitivity() throws IOException, ServletException {
		// TODO: finish this method
//		final String COURSE_ID_LOWER = "cs3210";
//		final String COURSE_ID_UPPER = COURSE_ID_LOWER.toUpperCase();
//		final String COURSE_NAME_LOWER = "software engineering";
//		final String COURSE_NAME_UPPER = COURSE_NAME_LOWER.toUpperCase();
//		
//		TeammatesServlet ts = new TeammatesServlet();
//		String response;
//		
//		// Add course
//		ts.coordinatorAddCourse(COURSE_ID_LOWER, COURSE_NAME_LOWER, GOOGLE_ID);
//		
//		// Add course with same ID but in uppercase
//		response = ts.coordinatorAddCourse(COURSE_ID_UPPER, COURSE_NAME_LOWER, GOOGLE_ID);
//		// TODO: Change this part accordingly
//		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_ADDED, response);
//		//assertEquals(Common.COORD_ADD_COURSE_RESPONSE_EXISTS,response);
	}
	
	@AfterClass
	public static void tearDown() {
		helper.tearDown();
		printTestClassFooter("CoordCourseAddApiTest");
	}
}
