package teammates.storage.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import teammates.common.Assumption;
import teammates.common.Common;
import teammates.common.datatransfer.CoordData;
import teammates.common.datatransfer.StudentData;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.JoinCourseException;
import teammates.storage.datastore.Datastore;
import teammates.storage.entity.Coordinator;
import teammates.storage.entity.Student;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;

/**
 * Manager for handling basic CRUD Operations only
 * 
 */
public class AccountsDb {

	public static final String ERROR_UPDATE_NON_EXISTENT = "Trying to update non-existent Student: ";
	public static final String ERROR_CREATE_COORD_ALREADY_EXISTS = "Trying to create a Coordinatior that exists: ";
	public static final String ERROR_CREATE_STUDENT_ALREADY_EXISTS = "Trying to create a Student that exists: ";
	
	private static final Logger log = Common.getLogger();

	private PersistenceManager getPM() {
		return Datastore.getPersistenceManager();
	}

	/**
	 * CREATE Coordinator
	 * 
	 * Creates a Coordinator object.
	 * 
	 * @throws EntityAlreadyExistsException
	 * 
	 */
	public void createCoord(CoordData coordToAdd)
			throws EntityAlreadyExistsException {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, coordToAdd);

		Assumption.assertTrue(coordToAdd.getInvalidStateInfo(),
				coordToAdd.isValid());

		if (getCoordEntity(coordToAdd.id) != null) {
			String error = ERROR_CREATE_COORD_ALREADY_EXISTS
					+ coordToAdd;

			log.warning(error + "\n" + Common.getCurrentThreadStack());

			throw new EntityAlreadyExistsException(error);
		}

		Coordinator newCoordinator = coordToAdd.toEntity();
		getPM().makePersistent(newCoordinator);
		getPM().flush();

		// Check insert operation persisted
		int elapsedTime = 0;
		Coordinator coordinatorCheck = getCoordEntity(coordToAdd.id);
		while ((coordinatorCheck == null)
				&& (elapsedTime < Common.PERSISTENCE_CHECK_DURATION)) {
			Common.waitBriefly();
			coordinatorCheck = getCoordEntity(coordToAdd.id);
			elapsedTime += Common.WAIT_DURATION;
		}
		if (elapsedTime == Common.PERSISTENCE_CHECK_DURATION) {
			log.severe("Operation did not persist in time: createCoord->"
					+ coordToAdd.id);
		}
	}

	/**
	 * CREATE Student
	 * 
	 * Creates a Student object.
	 * 
	 * @throws EntityAlreadyExistsException
	 * 
	 */
	public void createStudent(StudentData studentToAdd)
			throws EntityAlreadyExistsException {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, studentToAdd);

		Assumption.assertTrue(studentToAdd.getInvalidStateInfo(),
				studentToAdd.isValid());
		
		if (getStudentEntity(studentToAdd.course, studentToAdd.email) != null) {
			String error = ERROR_CREATE_STUDENT_ALREADY_EXISTS
					+ studentToAdd.course + "/" + studentToAdd.email;

			log.warning(error + "\n" + Common.getCurrentThreadStack());

			throw new EntityAlreadyExistsException(error);
		}

		Student newStudent = studentToAdd.toEntity();
		getPM().makePersistent(newStudent);
		getPM().flush();

		// Check insert operation persisted
		int elapsedTime = 0;
		Student studentCheck = getStudentEntity(studentToAdd.course,
				studentToAdd.email);
		while ((studentCheck == null)
				&& (elapsedTime < Common.PERSISTENCE_CHECK_DURATION)) {
			Common.waitBriefly();
			studentCheck = getStudentEntity(studentToAdd.course,
					studentToAdd.email);
			elapsedTime += Common.WAIT_DURATION;
		}
		if (elapsedTime == Common.PERSISTENCE_CHECK_DURATION) {
			log.severe("Operation did not persist in time: createStudent->"
					+ studentToAdd.course + "/" + studentToAdd.email);
		}
	}

	/**
	 * RETREIVE boolean
	 * 
	 * Checks if there exists a coord with this googleId
	 * 
	 * @param googleID
	 *            the coordinator's Google ID (Precondition: Must not be null)
	 * 
	 * @return boolean
	 */
	public boolean isCoord(String googleId) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, googleId);
		
		Coordinator c = getCoordEntity(googleId);
		return c != null;
	}

	/**
	 * RETREIVE boolean
	 * 
	 * Checks if there exists a student in this course with this email
	 * 
	 * @param courseId
	 *            the courseId for this Student entry
	 * 
	 * @param email
	 *            for identifying the student in the course
	 * 
	 * @return boolean
	 */
	public boolean isStudentExists(String courseId, String email) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, email);
		
		Student s = getStudentEntity(courseId, email);
		return s != null;
	}

	/**
	 * RETREIVE Coordinator
	 * 
	 * Returns a CoordData object.
	 * 
	 * @param googleID
	 *            the coordinator's Google ID (Precondition: Must not be null)
	 * 
	 * @return the CoordData of Coordinator with the specified Google ID, or
	 *         null if not found
	 */
	public CoordData getCoord(String googleId) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, googleId);

		Coordinator c = getCoordEntity(googleId);

		if (c == null) {
			log.warning("Trying to get non-existent Coordinator: " + googleId
					+ Common.getCurrentThreadStack());
			return null;
		}

		return new CoordData(c);
	}

	/**
	 * RETRIEVE Student
	 * 
	 * Returns a StudentData object from a unique Student entry with the
	 * key(courseId, email)
	 * 
	 * @param courseId
	 * 
	 * @param email
	 * 
	 * @return the StudentData of Student with the courseId and email
	 */
	public StudentData getStudent(String courseId, String email) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, email);

		Student s = getStudentEntity(courseId, email);

		if (s == null) {
			log.warning("Trying to get non-existent Student: " + courseId + "/"
					+ email + Common.getCurrentThreadStack());
			return null;
		}

		return new StudentData(s);
	}

	/**
	 * RETREIVE List<Student>
	 * 
	 * Returns a List of StudentData objects with this googleId
	 * 
	 * @param googleId
	 * @return List<StudentData> Each element in list are StudentData of
	 *         returned Students
	 */
	public List<StudentData> getStudentsWithGoogleId(String googleId) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, googleId);
		
		String query = "select from " + Student.class.getName()
				+ " where ID == \"" + googleId + "\"";

		@SuppressWarnings("unchecked")
		List<Student> studentList = (List<Student>) getPM().newQuery(query)
				.execute();

		List<StudentData> studentDataList = new ArrayList<StudentData>();
		for (Student student : studentList) {
			if (!JDOHelper.isDeleted(student)) {
				studentDataList.add(new StudentData(student));
			}
		}

		return studentDataList;
	}

	/**
	 * RETRIEVE List<Student>
	 * 
	 * Returns a list of Student objects that matches the specified courseID.
	 * 
	 * @param courseId
	 *            the course ID (Precondition: Must not be null)
	 * 
	 * @return List<Student> the list of students that are in the course
	 */
	public List<StudentData> getStudentListForCourse(String courseId) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);
		
		String query = "select from " + Student.class.getName()
				+ " where courseID == \'" + courseId + "\'";

		@SuppressWarnings("unchecked")
		List<Student> studentList = (List<Student>) getPM().newQuery(query)
				.execute();

		List<StudentData> studentDataList = new ArrayList<StudentData>();

		for (Student s : studentList) {
			if (!JDOHelper.isDeleted(s)) {
				studentDataList.add(new StudentData(s));
			}
		}

		return studentDataList;
	}

	/**
	 * RETRIEVE List<Student>
	 * 
	 * Returns a list of Student objects that matches the specified courseID and
	 * which do not have a Google ID associated with it
	 * 
	 * @param courseId
	 *            the course ID (Precondition: Must not be null)
	 * 
	 * @return List<StudentData> the list of unregistered students that are in
	 *         the course
	 */
	public List<StudentData> getUnregisteredStudentListForCourse(String courseId) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);
		
		String query = "select from " + Student.class.getName()
				+ " where courseID == \"" + courseId + "\"" + " && ID == \"\"";

		@SuppressWarnings("unchecked")
		List<Student> studentList = (List<Student>) getPM().newQuery(query)
				.execute();

		List<StudentData> studentDataList = new ArrayList<StudentData>();

		for (Student s : studentList) {
			studentDataList.add(new StudentData(s));
		}

		return studentDataList;
	}

	/**
	 * UPDATE Student.ID
	 * 
	 * Sets the ID of a particular Student object having the specified
	 * registration key.
	 * 
	 * @param registrationKey
	 *            the registration key of the student (Precondition: Must not be
	 *            null)
	 * 
	 * @param googleID
	 *            the Google ID of the student (Precondition: Must not be null)
	 * 
	 * @throws JoinCourseException
	 *             if the registration key does not exist if the student has
	 *             already registered in the course if the registration key has
	 *             been used by another student
	 */
	public void joinCourse(String registrationKey, String googleID)
			throws JoinCourseException {

		registrationKey = registrationKey.trim();
		googleID = googleID.trim();

		Student student = null;

		try {
			student = getPM().getObjectById(Student.class,
					KeyFactory.stringToKey(registrationKey));
		} catch (Exception e) {
			// No Student entry was found with this key
			throw new JoinCourseException(Common.ERRORCODE_INVALID_KEY,
					"You have entered an invalid key: " + registrationKey);
		}

		// If ID field is not empty -> check if this is user's googleId?
		if (student.getID() != null && !student.getID().equals("")) {

			if (student.getID().equals(googleID)) {
				// Belongs to the student and the student is already registered
				// to course
				throw new JoinCourseException(Common.ERRORCODE_ALREADY_JOINED,
						googleID + " has already joined this course");
			} else {
				// Does not belong to this student but belongs to another
				// student that is already registered
				throw new JoinCourseException(
						Common.ERRORCODE_KEY_BELONGS_TO_DIFFERENT_USER,
						registrationKey + " belongs to a different user");
			}
		}

		// A Student entry found with this key and ID is unregistered, register
		// him
		student.setID(googleID);

		// TODO: using this to help unit testing, might not work in live server
		getPM().close();
	}

	/**
	 * UPDATE Student
	 * 
	 * Updates Student object
	 * 
	 * @param courseId
	 *            , email and params to change
	 * 
	 */
	public void editStudent(String courseId, String email, String newName,
			String newTeamName, String newEmail, String newGoogleID,
			String newComments, Text newProfile) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, email);

		Student student = getStudentEntity(courseId, email);

		Assumption.assertNotNull(ERROR_UPDATE_NON_EXISTENT + courseId
				+ "/ + email " + Common.getCurrentThreadStack(), student);

		student.setEmail(newEmail);
		if (newName != null) {
			student.setName(newName);
		}

		if (newComments != null) {
			student.setComments(newComments);
		}
		if (newGoogleID != null) {
			student.setID(newGoogleID);
		}
		if (newTeamName != null) {
			student.setTeamName(newTeamName);
		}
		if (newProfile != null) {
			student.setProfileDetail(newProfile);
		}

		getPM().close();
	}

	/**
	 * DELETE Coordinator
	 * 
	 * @param coordId
	 */
	public void deleteCoord(String coordId) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, coordId);

		Coordinator coordToDelete = getCoordEntity(coordId);

		if (coordToDelete == null) {
			return;
		}

		getPM().deletePersistent(coordToDelete);
		getPM().flush();

		// Check delete operation persisted
		int elapsedTime = 0;
		Coordinator coordinatorCheck = getCoordEntity(coordId);
		while ((coordinatorCheck != null)
				&& (elapsedTime < Common.PERSISTENCE_CHECK_DURATION)) {
			Common.waitBriefly();
			coordinatorCheck = getCoordEntity(coordId);
			elapsedTime += Common.WAIT_DURATION;
		}
		if (elapsedTime == Common.PERSISTENCE_CHECK_DURATION) {
			log.severe("Operation did not persist in time: deleteCoord->"
					+ coordId);
		}

	}

	/**
	 * DELETE List<Student>
	 * 
	 * Deletes the Student objects in a particular Course.
	 * 
	 * @param courseId
	 *            the course Id (Precondition: Must not be null)
	 */
	public void deleteAllStudentsInCourse(String courseId) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);

		String query = "select from " + Student.class.getName()
				+ " where courseID == \'" + courseId + "\'";

		@SuppressWarnings("unchecked")
		List<Student> studentList = (List<Student>) getPM().newQuery(query)
				.execute();

		log.info("Deleting " + studentList.size()
				+ " students from the course " + courseId);

		getPM().deletePersistentAll(studentList);
		getPM().flush();
	}

	/**
	 * DELETE Student
	 * 
	 * Deletes a Student object from a specific Course.
	 * 
	 * @param courseId
	 *            the course Id (Precondition: Must not be null)
	 * 
	 * @param email
	 *            the email of the student (Precondition: Must not be null)
	 * 
	 */
	public void deleteStudent(String courseId, String email) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, email);

		Student studentToDelete = getStudentEntity(courseId, email);

		if (studentToDelete == null) {
			return;
		}

		getPM().deletePersistent(studentToDelete);
		getPM().flush();

		// Check delete operation persisted
		int elapsedTime = 0;
		Student studentCheck = getStudentEntity(courseId, email);
		while ((studentCheck != null)
				&& (elapsedTime < Common.PERSISTENCE_CHECK_DURATION)) {
			Common.waitBriefly();
			studentCheck = getStudentEntity(courseId, email);
			elapsedTime += Common.WAIT_DURATION;
		}
		if (elapsedTime == Common.PERSISTENCE_CHECK_DURATION) {
			log.severe("Operation did not persist in time: deleteStudent->"
					+ courseId + "/" + email);
		}
	}

	/**
	 * Returns the actual Student Entity
	 * 
	 * @param courseID
	 *            the course ID (Precondition: Must not be null)
	 * 
	 * @param email
	 *            the email of the student (Precondition: Must not be null)
	 * 
	 * @return Student the student who has the specified email in the specified
	 *         course
	 */
	private Student getStudentEntity(String courseId, String email) {
		String query = "select from " + Student.class.getName()
				+ " where courseID == \"" + courseId + "\" && email == \""
				+ email + "\"";

		@SuppressWarnings("unchecked")
		List<Student> studentList = (List<Student>) getPM().newQuery(query)
				.execute();

		if (studentList.isEmpty() || JDOHelper.isDeleted(studentList.get(0))) {
			return null;
		}

		return studentList.get(0);
	}

	/**
	 * Returns the actual Coordinator Entity
	 * 
	 * @param googleID
	 *            the coordinator's Google ID (Precondition: Must not be null)
	 * 
	 * @return Coordinator
	 */
	private Coordinator getCoordEntity(String googleID) {
		String query = "select from " + Coordinator.class.getName()
				+ " where googleID == '" + googleID + "'";

		@SuppressWarnings("unchecked")
		List<Coordinator> coordinatorList = (List<Coordinator>) getPM()
				.newQuery(query).execute();

		if (coordinatorList.isEmpty()
				|| JDOHelper.isDeleted(coordinatorList.get(0))) {
			return null;
		}

		return coordinatorList.get(0);
	}
	

	/**
	 * Returns the list of all coordinators
	 * @return
	 */
	public List<CoordData> getCoordinators() {
		List<CoordData> list = new LinkedList<CoordData>();
		List<Coordinator> entities = getCoordinatorEntities();
		Iterator<Coordinator> it = entities.iterator();
		while(it.hasNext()) {
			list.add(new CoordData(it.next()));
		}
		
		return list;
	}
	/**
	 * Returns the list of student entities
	 */
	@SuppressWarnings("unchecked")
	private List<Student> getStudentEntities() { 
		String query = "select from " + Student.class.getName();
		return (List<Student>) getPM().newQuery(query).execute();
	}

	/**
	 * @return the list of all students
	 */
	public List<StudentData> getStudents() { 
		List<StudentData> list = new LinkedList<StudentData>();
		List<Student> entities = getStudentEntities();
		Iterator<Student> it = entities.iterator();
		while(it.hasNext()) {
			list.add(new StudentData(it.next()));
		}
		return list;
	}
	
	
	/**
	 * Returns the list of coordinator entities
	 */
	private List<Coordinator> getCoordinatorEntities() {
		String query = "select from " + Coordinator.class.getName();
			

		@SuppressWarnings("unchecked")
		List<Coordinator> coordinatorList = (List<Coordinator>) getPM()
				.newQuery(query).execute();
	
		return coordinatorList;
	}
 

}
