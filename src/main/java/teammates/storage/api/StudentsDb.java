package teammates.storage.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import teammates.common.Assumption;
import teammates.common.Common;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.storage.datastore.Datastore;
import teammates.storage.entity.Student;

import com.google.appengine.api.datastore.KeyFactory;

/**
 * Handles CRUD Operations for student entities.
 * The API uses data transfer classes (i.e. *Attributes) instead of presistable classes.
 * 
 */
public class StudentsDb {
	public static final String ERROR_UPDATE_NON_EXISTENT_ACCOUNT = "Trying to update non-existent Account: ";
	public static final String ERROR_UPDATE_NON_EXISTENT_STUDENT = "Trying to update non-existent Student: ";
	public static final String ERROR_CREATE_ACCOUNT_ALREADY_EXISTS = "Trying to create an Account that exists: ";
	public static final String ERROR_CREATE_INSTRUCTOR_ALREADY_EXISTS = "Trying to create a Instructor that exists: ";
	public static final String ERROR_CREATE_STUDENT_ALREADY_EXISTS = "Trying to create a Student that exists: ";
	public static final String ERROR_TRYING_TO_MAKE_NON_EXISTENT_ACCOUNT_AN_INSTRUCTOR = "Trying to make an non-existent account an Instructor :";
	
	private static final Logger log = Common.getLogger();

	/**
	  * Preconditions: 
	 * <br> * {@code studentToAdd} is not null and has valid data.
	 */
	public void createStudent(StudentAttributes studentToAdd)
			throws EntityAlreadyExistsException {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, studentToAdd);
	
		Assumption.assertTrue(
				"Invalid object received as a parameter :" + studentToAdd.getInvalidStateInfo().toString(),
				studentToAdd.isValid());
		
		if (getStudentEntityForEmail(studentToAdd.course, studentToAdd.email) != null) {
			String error = ERROR_CREATE_STUDENT_ALREADY_EXISTS
					+ studentToAdd.course + "/" + studentToAdd.email;
			throw new EntityAlreadyExistsException(error);
		}
	
		Student newStudent = studentToAdd.toEntity();
		getPM().makePersistent(newStudent);
		getPM().flush();
	
		// Wait for the operation to persist
		int elapsedTime = 0;
		Student studentCheck = getStudentEntityForEmail(studentToAdd.course,
				studentToAdd.email);
		while ((studentCheck == null)
				&& (elapsedTime < Common.PERSISTENCE_CHECK_DURATION)) {
			Common.waitBriefly();
			studentCheck = getStudentEntityForEmail(studentToAdd.course,
					studentToAdd.email);
			elapsedTime += Common.WAIT_DURATION;
		}
		if (elapsedTime == Common.PERSISTENCE_CHECK_DURATION) {
			log.severe("Operation did not persist in time: createStudent->"
					+ studentToAdd.course + "/" + studentToAdd.email);
		}
	}

	/**
	 * Preconditions: <br>
	 * * All parameters are non-null.
	 * 
	 * @return The data for Student with the courseId and email. Returns null if
	 *         there is no such student.
	 */
	public StudentAttributes getStudentForEmail(String courseId, String email) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, email);
	
		Student s = getStudentEntityForEmail(courseId, email);
	
		if (s == null) {
			log.info("Trying to get non-existent Student: " + courseId + "/" + email);
			return null;
		}
	
		return new StudentAttributes(s);
	}

	/**
	 * Preconditions: 
	 * <br> * All parameters are non-null.
	 * @return null if no such student is found. 
	 */
	public StudentAttributes getStudentForGoogleId(String courseId, String googleId) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, googleId);
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);

		Query q = getPM().newQuery(Student.class);
		q.declareParameters("String googleIdParam, String courseIdParam");
		q.setFilter("ID == googleIdParam && courseID == courseIdParam");
		
		@SuppressWarnings("unchecked")
		List<Student> studentList = (List<Student>)q.execute(googleId, courseId);
		
		if (studentList.isEmpty() || JDOHelper.isDeleted(studentList.get(0))) {
			return null;
		} else {
			return new StudentAttributes(studentList.get(0));
		}
	}
	
	/**
	 * Works for both encrypted keys and unencrypted keys 
	 *   (sent out before we started encrypting keys). <br>
	 * Preconditions: <br>
	 * * All parameters are non-null.
	 * @return null if no matching student.
	 */
	public StudentAttributes getStudentForRegistrationKey(String registrationKey){
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, registrationKey);
		StudentAttributes studentAttributes;
		registrationKey = registrationKey.trim();
		String originalKey = registrationKey;
		try {
			//First, try to retrieve the student by assuming the given registrationKey key is encrypted
			registrationKey = Common.decrypt(registrationKey);
			Student student = getPM().getObjectById(Student.class,
					KeyFactory.stringToKey(registrationKey));
			studentAttributes = new StudentAttributes(student); 
		} catch (Exception e) {
			try {
				//Failing that, we try to retrieve assuming the given registrationKey is unencrypted 
				//  (early versions of the system sent unencrypted keys).
				Student student = getPM().getObjectById(Student.class,
						KeyFactory.stringToKey(originalKey));
				studentAttributes = new StudentAttributes(student);
			} catch (Exception e2) {
				//Failing both, we assume there is no such student
				studentAttributes = null;
			}
		}
		
		return studentAttributes;
	}


	/**
	 * Preconditions: 
	 * <br> * All parameters are non-null.
	 * @return an empty list if no such students are found.
	 */
	public List<StudentAttributes> getStudentsForGoogleId(String googleId) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, googleId);
		
		List<Student> studentList = getStudentEntitiesForGoogleId(googleId);
	
		List<StudentAttributes> studentDataList = new ArrayList<StudentAttributes>();
		for (Student student : studentList) {
			if (!JDOHelper.isDeleted(student)) {
				studentDataList.add(new StudentAttributes(student));
			}
		}
	
		return studentDataList;
	}

	/**
	 * Preconditions: <br>
	 *  * All parameters are non-null.
	 * @return an empty list if no students in the course.
	 */
	public List<StudentAttributes> getStudentsForCourse(String courseId) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);
		
		List<Student> studentList = getStudentEntitiesForCourse(courseId);
		
		List<StudentAttributes> studentDataList = new ArrayList<StudentAttributes>();
	
		for (Student s : studentList) {
			if (!JDOHelper.isDeleted(s)) {
				studentDataList.add(new StudentAttributes(s));
			}
		}
	
		return studentDataList;
	}
	
	/**
	 * Preconditions: <br>
	 *  * All parameters are non-null.
	 * @return an empty list if no students in the course.
	 */
	public List<StudentAttributes> getUnregisteredStudentsForCourse(String courseId) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);
		
		List<StudentAttributes> allStudents = getStudentsForCourse(courseId);
		ArrayList<StudentAttributes> unregistered = new ArrayList<StudentAttributes>();
		
		for(StudentAttributes s: allStudents){
			if(s.id==null || s.id.trim().isEmpty()){
				unregistered.add(s);
			}
		}
		return unregistered;
	}

	/**
	 * This method is not scalable. Not to be used unless for admin features.
	 * @return the list of all students in the database. 
	 */
	@Deprecated
	public List<StudentAttributes> getAllStudents() { 
		List<StudentAttributes> list = new LinkedList<StudentAttributes>();
		List<Student> entities = getStudentEntities();
		Iterator<Student> it = entities.iterator();
		while(it.hasNext()) {
			list.add(new StudentAttributes(it.next()));
		}
		return list;
	}

	/**
	 * Updates the student identified by {@code courseId} and {@code email}. 
	 * For the remaining parameters, the existing value is preserved 
	 *   if the parameter is null (due to 'keep existing' policy)<br> 
	 * Preconditions: <br>
	 * * {@code courseId} and {@code email} are non-null and correspond to an existing student. <br>
	 */
	public void updateStudent(String courseId, String email, String newName,
			String newTeamName, String newEmail, String newGoogleID,
			String newComments) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, email);
	
		Student student = getStudentEntityForEmail(courseId, email);
	
		//TODO: this should be an exception instead?
		Assumption.assertNotNull(ERROR_UPDATE_NON_EXISTENT_STUDENT + courseId
				+ "/ + email " + Common.getCurrentThreadStack(), student);
	
		//TODO: Enhance to ensure the updated entity is valid. 
		//  e.g. disable keep existing policy here (let the layer above manage it).
		
		student.setEmail(newEmail);
		if (newName != null) {
			student.setName(newName);
		}
	
		if (newComments != null) {
			student.setComments(newComments);
		}
		if (newGoogleID != null) {
			student.setGoogleId(newGoogleID);
		}
		if (newTeamName != null) {
			student.setTeamName(newTeamName);
		}
		
		getPM().close();
	}

	//TODO: add an updateStudent(StudentAttributes) version and make the above private
	
	/**
	 * Fails silently if no such student. <br>
	 * Preconditions: <br>
	 *  * All parameters are non-null.
	 *  
	 */
	public void deleteStudent(String courseId, String email) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, email);
	
		Student studentToDelete = getStudentEntityForEmail(courseId, email);
	
		if (studentToDelete == null) {
			return;
		}
	
		getPM().deletePersistent(studentToDelete);
		getPM().flush();
	
		// Check delete operation persisted
		int elapsedTime = 0;
		Student studentCheck = getStudentEntityForEmail(courseId, email);
		while ((studentCheck != null)
				&& (elapsedTime < Common.PERSISTENCE_CHECK_DURATION)) {
			Common.waitBriefly();
			studentCheck = getStudentEntityForEmail(courseId, email);
			elapsedTime += Common.WAIT_DURATION;
		}
		if (elapsedTime == Common.PERSISTENCE_CHECK_DURATION) {
			log.severe("Operation did not persist in time: deleteStudent->"
					+ courseId + "/" + email);
		}
	}

	/**
	 * Fails silently if no such student. <br>
	 * Preconditions: <br>
	 *  * All parameters are non-null.
	 *  
	 */
	public void deleteStudentsForGoogleId(String googleId) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, googleId);

		List<Student> studentList = getStudentEntitiesForGoogleId(googleId);

		getPM().deletePersistentAll(studentList);
		getPM().flush();
	}

	/**
	 * Fails silently if no such student or no such course. <br>
	 * Preconditions: <br>
	 *  * All parameters are non-null.
	 *  
	 */
	public void deleteStudentsForCourse(String courseId) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);
	
		List<Student> studentList = getStudentEntitiesForCourse(courseId);
	
		getPM().deletePersistentAll(studentList);
		getPM().flush();
	}

	private PersistenceManager getPM() {
		return Datastore.getPersistenceManager();
	}


	private Student getStudentEntityForEmail(String courseId, String email) {
		
		Query q = getPM().newQuery(Student.class);
		q.declareParameters("String courseIdParam, String emailParam");
		q.setFilter("courseID == courseIdParam && email == emailParam");
		
		@SuppressWarnings("unchecked")
		List<Student> studentList = (List<Student>)q.execute(courseId, email);
	
		if (studentList.isEmpty() || JDOHelper.isDeleted(studentList.get(0))) {
			return null;
		}
	
		return studentList.get(0);
	}

	private List<Student> getStudentEntitiesForCourse(String courseId) {
		Query q = getPM().newQuery(Student.class);
		q.declareParameters("String courseIdParam");
		q.setFilter("courseID == courseIdParam");
		
		@SuppressWarnings("unchecked")
		List<Student> studentList = (List<Student>) q.execute(courseId);
		return studentList;
	}

	
	private List<Student> getStudentEntitiesForGoogleId(String googleId) {
		Query q = getPM().newQuery(Student.class);
		q.declareParameters("String googleIdParam");
		q.setFilter("ID == googleIdParam");
		
		@SuppressWarnings("unchecked")
		List<Student> studentList = (List<Student>) q.execute(googleId);
		
		return studentList;
	}


	private List<Student> getStudentEntities() { 
		
		Query q = getPM().newQuery(Student.class);
		
		@SuppressWarnings("unchecked")
		List<Student> studentList = (List<Student>) q.execute();
		
		return studentList;
	}
	

}

