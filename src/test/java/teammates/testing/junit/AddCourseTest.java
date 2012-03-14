package teammates.testing.junit;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import teammates.Datastore;
import teammates.TeammatesServlet;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class AddCourseTest {
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
	private final String COURSE_ID = "CS1010";
	private final String COURSE_NAME = "Software Engineering";
	private final String GOOGLE_ID = "teammates.coord";
	private final String RESPONSE_ADDED = "<status>course added</status>";
	private final String RESPONSE_EXISTS = "<status>course exists</status>";
	
	
	@Before
	public void setUp() {
		helper.setUp();
	}

	@After
	public void tearDown() {
		helper.tearDown();
	}
	
	/**
	 * @rule course id: unique, name: not unique
	 * 
	 * @throws IOException
	 * @throws ServletException
	 */
	@Test
	public void testTeammatesServletAddCourseSuccessful() throws IOException, ServletException {
		Datastore.initialize();
		TeammatesServlet ts = new TeammatesServlet();
		String response;
		
		//normal course
		response = ts.coordinatorAddCourse(COURSE_ID, COURSE_NAME, GOOGLE_ID);
		assertEquals(RESPONSE_ADDED, response);

		//duplicate id
		response = ts.coordinatorAddCourse(COURSE_ID, COURSE_NAME, GOOGLE_ID);
		assertEquals(RESPONSE_EXISTS, response);
		
		//different id
		response = ts.coordinatorAddCourse("different id", COURSE_NAME, GOOGLE_ID);
		assertEquals(RESPONSE_ADDED, response);
		
		//different name
		response = ts.coordinatorAddCourse(COURSE_ID, "different name", GOOGLE_ID);
		assertEquals(RESPONSE_EXISTS, response);
		
		//different coordinator
		response = ts.coordinatorAddCourse(COURSE_ID, COURSE_NAME, "different coordinator");
		assertEquals(RESPONSE_EXISTS, response);
	}
	
	/**
	 * course id
	 * @rule insensitive
	 * @throws IOException
	 * @throws ServletException
	 */
	@Test
	public void testCourseIDCaseSensitivity() throws IOException, ServletException {
		TeammatesServlet ts = new TeammatesServlet();
		String response;
		String COURSE_ID_LOWER = "cs1101";
		String COURSE_ID_UPPER = "CS1101";
		
		response = ts.coordinatorAddCourse(COURSE_ID_LOWER, COURSE_NAME, GOOGLE_ID);
		assertEquals(RESPONSE_ADDED, response);
		
		response = ts.coordinatorAddCourse(COURSE_ID_LOWER, "sensitive course", GOOGLE_ID);
		assertEquals(RESPONSE_EXISTS, response);
		
		//TODO: changed to insensitive
		response = ts.coordinatorAddCourse(COURSE_ID_UPPER, COURSE_NAME, GOOGLE_ID);
//		assertEquals(RESPONSE_EXISTS, response);
		
		//TODO: changed to insensitive
		response = ts.coordinatorAddCourse(COURSE_ID_UPPER, "sensitive course", GOOGLE_ID);
//		assertEquals(RESPONSE_EXISTS, response);
	}
	
	
	/*---------------------------------------------------EXCEPTION TESTING---------------------------------------------------*/
	/**
	 * empty id
	 * @rule compulsory field
	 * @throws Exception
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddCourseWithEmptyIDFailed() throws Exception {
		TeammatesServlet ts = new TeammatesServlet();
		ts.coordinatorAddCourse("", COURSE_NAME, GOOGLE_ID);
	}
	
	//TODO: handle following exceptions!!!
	/**
	 * empty name
	 * @rule compulsory field
	 * @throws Exception
	 */
//	@Test
//	(expected = IllegalArgumentException.class)
	public void testAddCourseWithEmptyNameFailed() throws Exception {
		TeammatesServlet ts = new TeammatesServlet();
		ts.coordinatorAddCourse(COURSE_ID, "", GOOGLE_ID);
	}
	
	/**
	 * long id
	 * @rule size <= 21
	 * @throws Exception
	 */
//	@Test
//	(expected = IllegalArgumentException.class)
	public void testAddCourseWithLongIDFailed() throws Exception {
		TeammatesServlet ts = new TeammatesServlet();
		ts.coordinatorAddCourse("LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG", COURSE_NAME, GOOGLE_ID);
	}
	
	/**
	 * long id 
	 * @rule size <= 38
	 * @throws Exception
	 */
//	@Test
//	(expected = IllegalArgumentException.class)
	public void testAddCourseWithLongNameFailed() throws Exception {
		TeammatesServlet ts = new TeammatesServlet();
		ts.coordinatorAddCourse(COURSE_ID, "LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG LONG", GOOGLE_ID);
	}
	
	/**
	 * invalid id
	 * @rule no white space, only alphabets, numbers, dots, hyphens
	 * @throws (expected) exception
	 * */
//	@Test
//	(expected = IllegalArgumentException.class)
	public void testAddCourseWithInalidIDFailed() throws IOException, Exception {
		TeammatesServlet ts = new TeammatesServlet();
		try {
			ts.coordinatorAddCourse("#CS1010", COURSE_NAME, GOOGLE_ID);
		}
		catch (IOException e) {
			ts.coordinatorAddCourse("CS 1010", COURSE_NAME, GOOGLE_ID);
		}
		finally {
			
		}
	}
}
