package teammates.testing.testcases;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import teammates.Common;
import teammates.Datastore;
import teammates.TeammatesServlet;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class CoordCourseAddApiTest {
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
	private final String COURSE_ID = "CS1010";
	private final String COURSE_NAME = "Software Engineering";
	private final String GOOGLE_ID = "teammates.coord";

	
	
	@Before
	public void setUp() {
		helper.setUp();
		try{
			Datastore.initialize();
		}catch(Exception e){
			System.out.println("PersistenceManager has been called once.");
		}
	}

	@After
	public void tearDown() {
		helper.tearDown();
	}
	

	@Test
	public void testCoordCourseAdd() throws IOException, ServletException {
		TeammatesServlet ts = new TeammatesServlet();
		String response;
		
		//success
		response = ts.coordinatorAddCourse(COURSE_ID, COURSE_NAME, GOOGLE_ID);
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_ADDED, response);

		//duplicate id
		response = ts.coordinatorAddCourse(COURSE_ID, COURSE_NAME, GOOGLE_ID);
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_EXISTS, response);
		
		//different id
		response = ts.coordinatorAddCourse("id1010", COURSE_NAME, GOOGLE_ID);
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_ADDED, response);
		
		//different name
		response = ts.coordinatorAddCourse(COURSE_ID, "different name", GOOGLE_ID);
		assertEquals(Common.COORD_ADD_COURSE_RESPONSE_EXISTS, response);
		
		//different coordinator
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
	}
	
	@Test
	public void testCourseIDCaseSensitivity() throws IOException, ServletException {
		//TODO:implement this when the feature is implemented
	}
}
