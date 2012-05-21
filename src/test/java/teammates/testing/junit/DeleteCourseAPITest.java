package teammates.testing.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import teammates.Courses;
import teammates.Datastore;
import teammates.Evaluations;
import teammates.TeammatesServlet;
import teammates.exception.CourseDoesNotExistException;
import teammates.exception.EntityDoesNotExistException;
import teammates.jdo.Course;
import teammates.jdo.Evaluation;
import teammates.jdo.Student;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class DeleteCourseAPITest {
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
	private PersistenceManager pm;
	private final String COURSE_ID = "CS1102";
	private final String RESPONSE_DELETED = "<status>course deleted</status>";
	private final String RESPONSE_NOT_DELETED = "<status>course not deleted</status>";
	
	@Before
	public void setUp() {
		helper.setUp();
		try{
			Datastore.initialize();
		}catch(Exception e){
			System.out.println("PersistenceManager has been called once.");
		}
		pm = Datastore.getPersistenceManager();
	}

	@After
	public void tearDown() {
		helper.tearDown();
	}
	
	@Test
	public void testCoordDeleteCourseSuccessful() throws EntityDoesNotExistException {
		setupTestData();
		
		testCoursesDeleteCourse();
		
		testCoursesDeleteAllStudents();
		
		testEvaluationsDeleteEvaluations();
		
	}
	
	//Test deleteCoordinatorCourse(courseID) function in Courses.java
	public void testCoursesDeleteCourse() throws EntityDoesNotExistException {
		Courses courses = Courses.inst();
		courses.deleteCoordinatorCourse(COURSE_ID);
	}

	//Test deleteAllStudents(courseID) function in Courses.java
	public void testCoursesDeleteAllStudents() {
		Courses courses = Courses.inst();
		try {
			courses.deleteAllStudents(COURSE_ID);
		} catch (Exception e) {
			assertTrue(false);
		}
		
		assertEquals(0, courses.getStudentList(COURSE_ID).size());
	}
	
	//Test deleteEvaluations(courseID) function in Evaluations.java
	public void testEvaluationsDeleteEvaluations() {
		Evaluations eval = Evaluations.inst();
		try {
			eval.deleteEvaluations(COURSE_ID);
		} catch (Exception e) {
			assertTrue(false);
		}
		assertEquals(0, eval.getEvaluationList(COURSE_ID).size());
	}
	
	//Test coordinatorDeleteCourse(courseID) function in TeammatesServlet.java
	@Test
	public void testTeammatesServletDeleteCourse() {
		setupTestData();
		
		TeammatesServlet ts = new TeammatesServlet();
		String response;
		
		//normal delete
		response = ts.coordinatorDeleteCourse(COURSE_ID);
		assertEquals(RESPONSE_DELETED, response);
		
		//course not exists
		response = ts.coordinatorDeleteCourse("unknown courseID");
		assertEquals(RESPONSE_NOT_DELETED, response);
		
	}
	
	/*---------------------------------------------------EXCEPTION TESTING---------------------------------------------------*/
	@Test (expected = CourseDoesNotExistException.class)
	public void testCoursesDeleteCourseNotExist() throws EntityDoesNotExistException {
		Courses courses = Courses.inst();
		courses.deleteCoordinatorCourse("unknown course");
	}
	

	/*---------------------------------------------------HELPER FUNCTION---------------------------------------------------*/
	private void setupTestData() {
		pm = Datastore.getPersistenceManager();
		//create course
		Course a = new Course(COURSE_ID, "Testing Course", "teammates.coord");
		pm.makePersistent(a);
		//create evaluation
		Evaluation eval = new Evaluation(COURSE_ID, "Testing Course", "instructions", true, new Date(), new Date(), 8, 5);
		pm.makePersistent(eval);
		
		//enroll students
		List<Student> studentList = new ArrayList<Student>();
		studentList.add(new Student("alice.tmms@gmail.com", "Alice", "This is Alice", COURSE_ID, "Team 1"));
		studentList.add(new Student("benny.tmms@gmail.com", "Benny", "This is Benny", COURSE_ID, "Team 1"));
		studentList.add(new Student("charlie.tmms@gmail.com", "Charlie", "This is Charlie", COURSE_ID, "Team 2"));
		studentList.add(new Student("danny.tmms@gmail.com", "Danny", "This is Danny", COURSE_ID, "Team 2"));
		pm.makePersistentAll(studentList);
	}
	
}
