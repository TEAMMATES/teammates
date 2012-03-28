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

public class EnrolStudentsAPITest {
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
	@Before
	public void setUp() {
		helper.setUp();
	}

	@After
	public void tearDown() {
		helper.tearDown();
	}
	@Test
	public void testTeammatesServletEntolStudentsSuccessful() throws IOException, ServletException {
		Datastore.initialize();
		TeammatesServlet ts = new TeammatesServlet();
		String information = "Team A\tStudent A\tstudenta@gmail.com";
		String courseID = "SC2103";
		String reponse = ts.coordinatorEnrolStudents(information, courseID);
		String expectedResult =
				"<enrollmentreports>" +
						"<enrollmentreport>" +
						"<name><![CDATA[Student A]]></name>" +
						"<email><![CDATA[studenta@gmail.com]]></email>" +
						"<status><![CDATA[ADDED]]></status>" +
						"<nameedited>false</nameedited>" +
						"<teamnameedited>false</teamnameedited>" +
						"<commentsedited>false</commentsedited>" +
						"</enrollmentreport>" +
						"<enrollmentreport>" +
						"<name><![CDATA[Student A]]></name>" +
						"<email><![CDATA[studenta@gmail.com]]></email>" +
						"<status><![CDATA[REMAINED]]></status>" +
						"<nameedited>false</nameedited>" +
						"<teamnameedited>false</teamnameedited>" +
						"<commentsedited>false</commentsedited>" +
						"</enrollmentreport>" +
				"</enrollmentreports>";
		assertEquals(reponse, expectedResult);
	}
}
