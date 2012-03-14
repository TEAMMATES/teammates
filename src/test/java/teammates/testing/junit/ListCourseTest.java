package teammates.testing.junit;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import teammates.Courses;
import teammates.Datastore;
import teammates.TeammatesServlet;
import teammates.jdo.Course;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class ListCourseTest {

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
	private PersistenceManager pm;
	private List<Course> inputList = new ArrayList<Course>();
	

	@Before
	public void setUp() {
		helper.setUp();
		
		Datastore.initialize();
		pm = Datastore.getPersistenceManager();
		
		//initial data
		Course a = new Course("MA1010", "test", "teammates.coord");
		Course b = new Course("CS1101", "Programming Methodology", "teammates.coord");
		Course c = new Course("CS1231", "Programming Methodology", "teammates.coord");
		
		inputList.add(a);
		inputList.add(b);
		inputList.add(c);
		
		pm.makePersistentAll(inputList);
	}

	@After
	public void tearDown() {
		helper.tearDown();
	}

	@Test
	public void testCoordGetCourseList() throws IOException, ServletException {
		testCoursesCoordGetCourseList();
		testTeammatesServletCoordGetCourseList();
	}
	
	public void testCoursesCoordGetCourseList() throws IOException {
		Courses courses = Courses.inst();
		List<Course> courseList = courses.getCoordinatorCourseList("teammates.coord");
		assertEquals(inputList.size(), courseList.size());
		
		for(int i = 0; i < courseList.size(); i++) {
			
			Course expected = inputList.get(inputList.indexOf(courseList.get(i)));
			Course actual = courseList.get(i);
			assertEquals(expected.getID(), actual.getID());
			assertEquals(expected.getName(), actual.getName());
			assertEquals(expected.getCoordinatorID(), actual.getCoordinatorID());
			
		}
	}
	
	public void testTeammatesServletCoordGetCourseList() throws IOException, ServletException {
		TeammatesServlet ts = new TeammatesServlet();
		String result = ts.coordinatorGetCourseList("teammates.coord");
		String expected = 
				"<courses>" +
				"<coursesummary>" +
					"<courseid><![CDATA[CS1101]]></courseid>" +
					"<coursename><![CDATA[Programming Methodology]]></coursename>" +
					"<coursestatus>false</coursestatus>" +
					"<coursenumberofteams>0</coursenumberofteams>" +
					"<coursetotalstudents>0</coursetotalstudents>" +
					"<courseunregistered>0</courseunregistered>" +
				"</coursesummary>" +
				"<coursesummary>" +
					"<courseid><![CDATA[CS1231]]></courseid>" +
					"<coursename><![CDATA[Programming Methodology]]></coursename>" +
					"<coursestatus>false</coursestatus>" +
					"<coursenumberofteams>0</coursenumberofteams>" +
					"<coursetotalstudents>0</coursetotalstudents>" +
					"<courseunregistered>0</courseunregistered>" +
				"</coursesummary>" +
				"<coursesummary>" +
					"<courseid><![CDATA[MA1010]]></courseid>" +
					"<coursename><![CDATA[test]]></coursename>" +
					"<coursestatus>false</coursestatus>" +
					"<coursenumberofteams>0</coursenumberofteams>" +
					"<coursetotalstudents>0</coursetotalstudents>" +
					"<courseunregistered>0</courseunregistered>" +
				"</coursesummary>" +
				"</courses>";
		assertEquals(expected, result);
	}

}
