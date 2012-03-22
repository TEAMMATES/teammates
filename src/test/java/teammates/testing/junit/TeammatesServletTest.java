package teammates.testing.junit;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mockit.Mocked;
import mockit.NonStrictExpectations;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import teammates.Courses;
import teammates.TeammatesServlet;
import teammates.exception.CourseExistsException;
import teammates.exception.CourseInputInvalidException;

import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;

/**
 * Test client-side request to TeammatesServlet.java
 * 
 * @Mocked: HttpServletRequest, HttpServletResponse
 * */
public class TeammatesServletTest {

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalUserServiceTestConfig()).setEnvIsAdmin(true).setEnvIsLoggedIn(true);
	@Mocked
	HttpServletRequest req;
	@Mocked(methods = { "getWriter()", "new PrintWriter(\"test.txt\")" })
	HttpServletResponse resp;

	@Before
	public void setUp() {
		helper.setUp();
		helper.setEnvEmail("teammates.coord@gmail.com");
		helper.setEnvAuthDomain("59670");
	}

	@After
	public void tearDown() {
		helper.tearDown();
	}

	@Test
	public void testCoordAddCourse() throws IOException, ServletException, CourseExistsException, CourseInputInvalidException {

		new NonStrictExpectations() {
			@Mocked
			Courses courses;
			{
				req.getParameter("operation");
				result = "coordinator_addcourse";
				Courses.inst();
				result = courses;
				courses.addCourse(anyString, anyString, anyString);
				resp.getWriter();
				result = new PrintWriter("test.txt");
			}
		};

		TeammatesServlet ts = new TeammatesServlet();
		ts.doPost(req, resp);
		ts.getResponse().getWriter().flush();
		assertTrue(FileUtils.readFileToString(new File("test.txt"), "UTF-8").contains("<status>course added</status>"));
	}

	@Test
	public void testCoordAddExistingCourse() throws IOException, ServletException, CourseExistsException, CourseInputInvalidException {

		new NonStrictExpectations() {
			@Mocked
			Courses courses;
			{
				req.getParameter("operation");
				result = "coordinator_addcourse";
				Courses.inst();
				result = courses;
				courses.addCourse(anyString, anyString, anyString);
				result = new CourseExistsException();
				resp.getWriter();
				result = new PrintWriter("test.txt");
			}
		};

		TeammatesServlet ts = new TeammatesServlet();
		ts.doPost(req, resp);
		ts.getResponse().getWriter().flush();
		assertTrue(FileUtils.readFileToString(new File("test.txt"), "UTF-8").contains("<status>course exists</status>"));
	}
}
