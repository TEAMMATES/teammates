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

import teammates.Datastore;
import teammates.TeammatesServlet;
import teammates.exception.EntityDoesNotExistException;
import teammates.exception.TeammatesException;
import teammates.manager.Courses;
import teammates.manager.Evaluations;
import teammates.persistent.Course;
import teammates.persistent.Evaluation;
import teammates.persistent.Student;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class DeleteCourseAPITest {
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
	private PersistenceManager pm;
	private TeammatesServlet ts;
	private final String COURSE_ID = "CS1102";
	private final String RESPONSE_DELETED = "<status>course deleted</status>";
	@Before
	public void setUp() {
		helper.setUp();
		try{
			Datastore.initialize();
		}catch(Exception e){
			System.out.println("PersistenceManager has been called once.");
		}
		
		ts = new TeammatesServlet();
		pm = Datastore.getPersistenceManager();
	}

	@After
	public void tearDown() {
		helper.tearDown();
	}
	
	@Test
	public void testCoordDeleteCourseSuccessful() throws EntityDoesNotExistException, TeammatesException {
		setupTestData();
		
		testCoursesDeleteCourse();
		
		testCoursesDeleteAllStudents();
		
		testEvaluationsDeleteEvaluations();
		
	}
	
	//Test deleteCoordinatorCourse(courseID) function in Courses.java
	// FIXME: No check/assertion/verification is done?
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
	public void testTeammatesServletDeleteCourse() throws TeammatesException {
		setupTestData();
		String response;
		
		//normal delete
		response = ts.coordinatorDeleteCourse(COURSE_ID);
		assertEquals(RESPONSE_DELETED, response);
		
		//course not exists, but the operation is still considered successful
		response = ts.coordinatorDeleteCourse("unknown courseID");
		assertEquals(RESPONSE_DELETED, response);
		
	}

	

	/*---------------------------------------------------HELPER FUNCTION---------------------------------------------------*/
	private void setupTestData() throws TeammatesException {
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
