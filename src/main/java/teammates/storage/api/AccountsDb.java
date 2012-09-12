package teammates.storage.api;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;

import teammates.storage.datastore.Datastore;
import teammates.storage.entity.Coordinator;
import teammates.storage.entity.Student;
import teammates.common.Common;
import teammates.common.datatransfer.CoordData;
import teammates.common.datatransfer.StudentData;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.JoinCourseException;

/**
 * Manager for handling basic CRUD Operations only
 * 
 */
public class AccountsDb {

	private static final Logger log = Common.getLogger();

	private PersistenceManager getPM() {
		return Datastore.getPersistenceManager();
	}

	/**
	 * CREATE Coordinator
	 * 
	 * Creates a Coordinator object.
	 * 
	 * @param googleID
	 *            the coordinator's Google ID (Precondition: Must not be null)
	 * 
	 * @param name
	 *            the coordinator's name (Precondition: Must not be null)
	 * 
	 * @param email
	 *            the coordinator's email (Precondition: Must not be null)
	 * 
	 * 
	 */
	public void createCoord(CoordData coordToAdd)
			throws EntityAlreadyExistsException {
		
		if (getCoordEntity(coordToAdd.id) != null) {
			throw new EntityAlreadyExistsException(
					"Coordinator already exists :" + coordToAdd);
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
	 * @param googleID
	 *            the coordinator's Google ID (Precondition: Must not be null)
	 * 
	 * @param name
	 *            the coordinator's name (Precondition: Must not be null)
	 * 
	 * @param email
	 *            the coordinator's email (Precondition: Must not be null)
	 * 
	 * 
	 */
	public void createStudent(StudentData studentToAdd)
			throws EntityAlreadyExistsException {

		if (getStudentEntity(studentToAdd.course, studentToAdd.email) != null) {
			throw new EntityAlreadyExistsException(
					"This student already existis :" + studentToAdd.course + "/" + studentToAdd.email);
		}

		Student newStudent = studentToAdd.toEntity();

		getPM().makePersistent(newStudent);
		getPM().flush();

		// Check insert operation persisted
		int elapsedTime = 0;
		Student studentCheck = getStudentEntity(studentToAdd.course, studentToAdd.email);
		while ((studentCheck == null)
				&& (elapsedTime < Common.PERSISTENCE_CHECK_DURATION)) {
			Common.waitBriefly();
			studentCheck = getStudentEntity(studentToAdd.course, studentToAdd.email);
			elapsedTime += Common.WAIT_DURATION;
		}
		if (elapsedTime == Common.PERSISTENCE_CHECK_DURATION) {
			log.severe("Operation did not persist in time: createStudent->"
					+ studentToAdd.course + "/" + studentToAdd.email);
		}
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

		Coordinator c = getCoordEntity(googleId);

		return c == null ? null : new CoordData(c);
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

		Student s = getStudentEntity(courseId, email);

		return s == null ? null : new StudentData(s);
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
	 * Returns a list of Student objects that matches the specified courseID.
	 * 
	 * @param courseID
	 *            the course ID (Precondition: Must not be null)
	 * 
	 * @return List<Student> the list of students that are in the course
	 */
	public List<StudentData> getStudentListForCourse(String courseID) {
		String query = "select from " + Student.class.getName()
				+ " where courseID == \'" + courseID + "\'";

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
	 * @param courseID
	 *            the course ID (Precondition: Must not be null)
	 * 
	 * @return List<StudentData> the list of unregistered students that are in
	 *         the course
	 */
	public List<StudentData> getUnregisteredStudentListForCourse(String courseID) {
		String query = "select from " + Student.class.getName()
				+ " where courseID == \"" + courseID + "\"" + " && ID == \"\"";

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
					"Invalid key :" + registrationKey);
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
						googleID + " belongs to a different user");
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
	 * @throws EntityDoesNotExistException
	 */
	public void editStudent(String courseID, String email, String newName,
			String newTeamName, String newEmail, String newGoogleID,
			String newComments, Text newProfile)
			throws EntityDoesNotExistException {

		Student student = getStudentEntity(courseID, email);

		if (student == null)
			throw new EntityDoesNotExistException("Student " + email
					+ " does not exist in course " + courseID);

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

		Coordinator coordToDelete = getCoordEntity(coordId);

		if (coordToDelete == null) {
			log.warning("Trying to delete non-existent Coordinator: " + coordId);
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

		Student studentToDelete = getStudentEntity(courseId, email);

		if (studentToDelete == null) {
			log.warning("Trying to delete non-existent Student: " + courseId
					+ "/" + email);
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
			log.warning("Trying to get non-existent Coord : " + googleID);
			return null;
		}

		return coordinatorList.get(0);
	}

}
