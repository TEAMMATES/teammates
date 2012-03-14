package teammates.testing.junit.sample;

import static org.junit.Assert.*;

import java.util.List;

import javax.jdo.PersistenceManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import teammates.Courses;
import teammates.Datastore;
import teammates.jdo.Course;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class FirstTest {
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
	public void simpleJdo() {
		Course a = new Course("cs1010", "test", "teammates.coord");
		Course b = new Course("CS1101", "Programming Methodology", "teammates.coord");

		Datastore.initialize();
		PersistenceManager pm = Datastore.getPersistenceManager();
		pm.makePersistent(a);
		pm.makePersistent(b);
		
		Courses courses = Courses.inst();
		List<Course> courseList = courses.getCoordinatorCourseList("teammates.coord");
		assertEquals(2, courseList.size());
		
	}
}
