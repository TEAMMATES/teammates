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
import teammates.common.datatransfer.AccountData;
import teammates.common.datatransfer.InstructorData;
import teammates.common.datatransfer.StudentData;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.JoinCourseException;
import teammates.storage.datastore.Datastore;
import teammates.storage.entity.Account;
import teammates.storage.entity.Coordinator;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;

/**
 * Manager for handling basic CRUD Operations only
 * 
 */
public class AccountsDb {
	public static final String ERROR_UPDATE_NON_EXISTENT_ACCOUNT = "Trying to update non-existent Account: ";
	public static final String ERROR_UPDATE_NON_EXISTENT_STUDENT = "Trying to update non-existent Student: ";
	public static final String ERROR_CREATE_ACCOUNT_ALREADY_EXISTS = "Trying to create an Account that exists: ";
	public static final String ERROR_CREATE_INSTRUCTOR_ALREADY_EXISTS = "Trying to create a Instructor that exists: ";
	public static final String ERROR_CREATE_STUDENT_ALREADY_EXISTS = "Trying to create a Student that exists: ";
	public static final String ERROR_TRYING_TO_MAKE_NON_EXISTENT_ACCOUNT_AN_INSTRUCTOR = "Trying to make an non-existent account an Instructor :";
	
	private static final Logger log = Common.getLogger();

	private PersistenceManager getPM() {
		return Datastore.getPersistenceManager();
	}
	
	//=====================================================================
	
	/**
	 * CREATE Account
	 * 
	 * Creates an Account which is to be referenced by Instructor or Student
	 * 
	 * [Can an Account be neither student nor instructor?]
	 * 
	 * @param accountToAdd
	 */
	public void createAccount(AccountData accountToAdd) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, accountToAdd);
		
		Assumption.assertTrue(accountToAdd.getInvalidStateInfo(),
				accountToAdd.isValid());
		
		Account newAccount = accountToAdd.toEntity();
		getPM().makePersistent(newAccount);
		getPM().flush();

		// Check insert operation persisted
		int elapsedTime = 0;
		Account accountCheck = getAccountEntity(accountToAdd.googleId);
		while ((accountCheck == null)
				&& (elapsedTime < Common.PERSISTENCE_CHECK_DURATION)) {
			Common.waitBriefly();
			accountCheck = getAccountEntity(accountToAdd.googleId);
			elapsedTime += Common.WAIT_DURATION;
		}
		if (elapsedTime == Common.PERSISTENCE_CHECK_DURATION) {
			log.severe("Operation did not persist in time: createAccount->"
					+ accountToAdd.googleId);
		}
	}
	
	/**
	 * CREATE List<Account>
	 * 
	 * @param List<AccountData>
	 */
	public void createAccounts(List<AccountData> accountsToAdd) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, accountsToAdd);
		
		List<Account> accounts = new ArrayList<Account>();
		for (AccountData ad : accountsToAdd) {
			accounts.add(ad.toEntity());
		}
		
		getPM().makePersistentAll(accounts);
		getPM().flush();
	}

	/**
	 * CREATE Instructor
	 * 
	 * Creates a Instructor object.
	 * Instructor represents the relation between an Account and a Course
	 * 
	 * @throws EntityAlreadyExistsException
	 * 
	 */
	public void createInstructor(InstructorData instructorToAdd)
			throws EntityAlreadyExistsException {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, instructorToAdd);

		Assumption.assertTrue(instructorToAdd.getInvalidStateInfo(),
				instructorToAdd.isValid());

		if (getInstructorEntity(instructorToAdd.googleId, instructorToAdd.courseId) != null) {
			String error = ERROR_CREATE_INSTRUCTOR_ALREADY_EXISTS
					+ instructorToAdd.googleId + ", " + instructorToAdd.courseId;

			log.warning(error + "\n" + Common.getCurrentThreadStack());

			throw new EntityAlreadyExistsException(error);
		}

		Instructor newInstructor = instructorToAdd.toEntity();
		getPM().makePersistent(newInstructor);
		getPM().flush();

		// Check insert operation persisted
		int elapsedTime = 0;
		Instructor instructorCheck = getInstructorEntity(instructorToAdd.googleId, instructorToAdd.courseId);
		while ((instructorCheck == null)
				&& (elapsedTime < Common.PERSISTENCE_CHECK_DURATION)) {
			Common.waitBriefly();
			instructorCheck = getInstructorEntity(instructorToAdd.googleId, instructorToAdd.courseId);
			elapsedTime += Common.WAIT_DURATION;
		}
		if (elapsedTime == Common.PERSISTENCE_CHECK_DURATION) {
			log.severe("Operation did not persist in time: createInstructor->"
					+ instructorToAdd.googleId);
		}
	}

	/**
	 * CREATE Student
	 * 
	 * Creates a Student object.
	 * Also a relation between an Account and a Course
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
	 * Checks if there exists a INSTRUCTOR with this googleId
	 * 
	 * @param googleID
	 *            the instructor's Google ID (Precondition: Must not be null)
	 * 
	 * @return boolean
	 */
	public boolean isInstructor(String googleId) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, googleId);
		
		Account a = getAccountEntity(googleId);
		return a == null ? false : a.isInstructor();
	}
	
	/**
	 * RETRIEVE List<InstructorData>
	 * 
	 *  Returns list of InstructorData which tells the COURSES instructed by this instructor
	 * 
	 * @param googleId
	 * @return List<InstructorData>
	 */
	public List<InstructorData> getInstructorsByGoogleId(String googleId) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, googleId);
		
		List<Instructor> instructorList = getInstructorEntitiesByGoogleId(googleId);
		
		List<InstructorData> instructorDataList = new ArrayList<InstructorData>();
		for (Instructor i : instructorList) {
			instructorDataList.add(new InstructorData(i));
		}
		
		return instructorDataList;
	}
	
	/**
	 * RETRIEVE List<InstructorData>
	 * 
	 *  Returns list of InstructorData which tells the INSTRUCTORS for a given course
	 * 
	 * @param googleId
	 * @return List<InstructorData>
	 */
	public List<InstructorData> getInstructorsByCourseId(String courseId) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);
		
		List<Instructor> instructorList = getInstructorEntitiesByCourseId(courseId);
		
		List<InstructorData> instructorDataList = new ArrayList<InstructorData>();
		for (Instructor i : instructorList) {
			instructorDataList.add(new InstructorData(i));
		}
		
		return instructorDataList;
	}
	
	private List<Instructor> getInstructorEntitiesByGoogleId(String googleId) {
		String query = "select from " + Instructor.class.getName()
				+ " where googleId == '" + googleId + "'";
		
		@SuppressWarnings("unchecked")
		List<Instructor> instructorList = (List<Instructor>) getPM().newQuery(query)
				.execute();
		
		return instructorList;
	}
	
	private List<Instructor> getInstructorEntitiesByCourseId(String courseId) {
		String query = "select from " + Instructor.class.getName()
				+ " where courseId == '" + courseId + "'";
		
		@SuppressWarnings("unchecked")
		List<Instructor> instructorList = (List<Instructor>) getPM().newQuery(query)
				.execute();
		
		return instructorList;
	}
	
	/**
	 * RETRIEVE boolean
	 * 
	 * Checks if there is an Account that is already with the specified googleId
	 * 
	 * @return
	 */
	public boolean isAccountExists(String googleId) {
		return getAccountEntity(googleId) != null;
	}

	/**
	 * RETREIVE boolean
	 * 
	 * Checks if the googleId is an INSTRUCTOR of the specified COURSE
	 * 
	 * @param googleID
	 *            the instructor's Google ID (Precondition: Must not be null)
	 * 
	 * @return boolean
	 */
	public boolean isInstructorOfCourse(String googleId, String courseId) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, googleId);
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);
		
		Instructor i = getInstructorEntity(googleId, courseId);
		return i != null;
	}

	/**
	 * RETREIVE boolean
	 * 
	 * Checks if there exists a STUDENT in this course with this email
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
	 * RETREIVE boolean
	 * 
	 * Checks if the googleId is a STUDENT of the specified COURSE
	 * 
	 * @param googleID
	 *            the instructor's Google ID (Precondition: Must not be null)
	 * 
	 * @return boolean
	 */
	public boolean isStudentOfCourse(String googleId, String courseId) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, googleId);
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);
		
		String query = "select from " + Student.class.getName()
				+ " where ID == '" + googleId + "' && courseID == '" + courseId + "'";

		@SuppressWarnings("unchecked")
		List<Student> studentList = (List<Student>) getPM()
				.newQuery(query).execute();

		if (studentList.isEmpty()
				|| JDOHelper.isDeleted(studentList.get(0))) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * RETREIVE Account
	 * 
	 * Returns a AccountData object.
	 * 
	 * @param googleID
	 */
	public AccountData getAccount(String googleId) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, googleId);

		Account a = getAccountEntity(googleId);

		if (a == null) {
			log.warning("Trying to get non-existent Account: " + googleId
					+ Common.getCurrentThreadStack());
			return null;
		}

		return new AccountData(a);
	}

	/**
	 * RETREIVE Instructor
	 * 
	 * Returns a InstructorData object.
	 * 
	 * @param googleID
	 *            the instructor's Google ID (Precondition: Must not be null)
	 * 
	 * @return the InstructorData of Instructor with the specified Google ID, or
	 *         null if not found
	 */
	public InstructorData getInstructor(String googleId, String courseId) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, googleId);
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);

		Instructor c = getInstructorEntity(googleId, courseId);

		if (c == null) {
			log.warning("Trying to get non-existent Instructor: " + googleId
					+ Common.getCurrentThreadStack());
			return null;
		}

		return new InstructorData(c);
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
		
		List<Student> studentList = getStudentEntitiesByGoogleId(googleId);

		List<StudentData> studentDataList = new ArrayList<StudentData>();
		for (Student student : studentList) {
			if (!JDOHelper.isDeleted(student)) {
				studentDataList.add(new StudentData(student));
			}
		}

		return studentDataList;
	}
	
	private List<Student> getStudentEntitiesByGoogleId(String googleId) {
		String query = "select from " + Student.class.getName()
				+ " where ID == '" + googleId + "'";
		
		@SuppressWarnings("unchecked")
		List<Student> studentList = (List<Student>) getPM().newQuery(query)
				.execute();
		
		return studentList;
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
	 * Called when an Account for a student person is also made an Instructor of another course
	 * 
	 * This method should only be called if the Account exists
	 * 
	 * @param googleId
	 */
	public void makeAccountInstructor(String googleId) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, googleId);
		Account a = getAccountEntity(googleId);
		
		Assumption.assertNotNull(ERROR_TRYING_TO_MAKE_NON_EXISTENT_ACCOUNT_AN_INSTRUCTOR, a);
		
		a.setIsInstructor(true);
		getPM().close();
	}
	
	/**
	 * To be use for a user to update his/her information.
	 * 
	 * @param AccountData a
	 */
	public void updateAccount(AccountData a) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, a);
		Account accountToUpdate = getAccountEntity(a.googleId);
		
		Assumption.assertNotNull(ERROR_UPDATE_NON_EXISTENT_ACCOUNT + a.googleId
				+ Common.getCurrentThreadStack(), accountToUpdate);
		
		accountToUpdate.setName(a.name);
		accountToUpdate.setEmail(a.email);
		accountToUpdate.setIsInstructor(a.isInstructor);
		accountToUpdate.setInstitute(a.institute);
		
		getPM().close();
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

		Assumption.assertNotNull(ERROR_UPDATE_NON_EXISTENT_STUDENT + courseId
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
	 * UPDATE Instructor
	 * 
	 * Allow instructors to modify their NAME and EMAIL Fields
	 * Cannot modify GoogleId and Course Id
	 * 
	 * @param InstructorData id
	 */
	public void updateInstructor(InstructorData id) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, id);
		
		Assumption.assertTrue(id.getInvalidStateInfo(), id.isValid());
		
		Instructor instructorToUpdate = getInstructorEntity(id.googleId, id.courseId);
		
		Assumption.assertNotNull(ERROR_UPDATE_NON_EXISTENT_ACCOUNT + id.googleId
				+ Common.getCurrentThreadStack(), instructorToUpdate);
		
		instructorToUpdate.setName(id.name);
		instructorToUpdate.setEmail(id.email);
		
		getPM().close();
	}
	
	/**
	 * DELETE Account
	 * 
	 * Delete a particular User from system
	 * 
	 * @param instructorId
	 * @param courseId
	 */
	public void deleteAccount(String googleId) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, googleId);

		Account accountToDelete = getAccountEntity(googleId);

		if (accountToDelete == null) {
			return;
		}

		getPM().deletePersistent(accountToDelete);
		getPM().flush();

		// Check delete operation persisted
		int elapsedTime = 0;
		Account accountCheck = getAccountEntity(googleId);
		while ((accountCheck != null)
				&& (elapsedTime < Common.PERSISTENCE_CHECK_DURATION)) {
			Common.waitBriefly();
			accountCheck = getAccountEntity(googleId);
			elapsedTime += Common.WAIT_DURATION;
		}
		if (elapsedTime == Common.PERSISTENCE_CHECK_DURATION) {
			log.severe("Operation did not persist in time: deleteAccount->"
					+ googleId);
		}
	}

	/**
	 * DELETE Instructor
	 * 
	 * Delete a specific relation
	 * 
	 * @param instructorId
	 * @param courseId
	 */
	public void deleteInstructor(String instructorId, String courseId) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, instructorId);
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);

		Instructor instructorToDelete = getInstructorEntity(instructorId, courseId);

		if (instructorToDelete == null) {
			return;
		}

		getPM().deletePersistent(instructorToDelete);
		getPM().flush();

		// Check delete operation persisted
		int elapsedTime = 0;
		Instructor instructorCheck = getInstructorEntity(instructorId, courseId);
		while ((instructorCheck != null)
				&& (elapsedTime < Common.PERSISTENCE_CHECK_DURATION)) {
			Common.waitBriefly();
			instructorCheck = getInstructorEntity(instructorId, courseId);
			elapsedTime += Common.WAIT_DURATION;
		}
		if (elapsedTime == Common.PERSISTENCE_CHECK_DURATION) {
			log.severe("Operation did not persist in time: deleteInstructor->"
					+ instructorId);
		}
	}
	
	/**
	 * DELETE List<Instructor>
	 * 
	 * Delete all relations for this INSTRUCTOR
	 * 
	 * @param googleId
	 */
	public void deleteInstructorsByGoogleId(String googleId) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, googleId);

		List<Instructor> instructorList = getInstructorEntitiesByGoogleId(googleId);
		
		if (instructorList.size() > 0) {
			getPM().deletePersistentAll(instructorList);
			getPM().flush();
		}
	}
	
	/**
	 * DELETE List<Instructor>
	 * 
	 * Delete all relations for this COURSE
	 * 
	 * @param courseId
	 */
	public void deleteInstructorsByCourseId(String courseId) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);

		List<Instructor> instructorList = getInstructorEntitiesByCourseId(courseId);
		
		if (instructorList.size() > 0) {
			getPM().deletePersistentAll(instructorList);
			getPM().flush();
		}
	}
	
	/**
	 * DELETE List<Student>
	 * 
	 * Delete all relations for this INSTRUCTOR
	 * 
	 * @param googleId
	 */
	public void deleteStudentsByGoogleId(String googleId) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, googleId);

		List<Student> studentList = getStudentEntitiesByGoogleId(googleId);
		
		if (studentList.size() > 0) {
			getPM().deletePersistentAll(studentList);
			getPM().flush();
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

	//=================================================================================
	
	/**
	 * Returns the actual Account Entity
	 * 
	 * @param googleId
	 * 
	 * @return Account for this user
	 */
	private Account getAccountEntity(String googleId) {
		String query = "select from " + Account.class.getName()
				+ " where googleId == \"" + googleId + "\"";

		@SuppressWarnings("unchecked")
		List<Account> accountsList = (List<Account>) getPM().newQuery(query)
				.execute();

		if (accountsList.isEmpty() || JDOHelper.isDeleted(accountsList.get(0))) {
			return null;
		}

		return accountsList.get(0);
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
	 * Returns the actual Instructor Entity
	 * 
	 * @param googleID
	 *            the instructor's Google ID (Precondition: Must not be null)
	 * 
	 * @return Instructor
	 */
	private Instructor getInstructorEntity(String googleID, String courseId) {
		String query = "select from " + Instructor.class.getName()
				+ " where googleId == '" + googleID + "' && courseId == '" + courseId + "'";

		@SuppressWarnings("unchecked")
		List<Instructor> instructorList = (List<Instructor>) getPM()
				.newQuery(query).execute();

		if (instructorList.isEmpty()
				|| JDOHelper.isDeleted(instructorList.get(0))) {
			return null;
		}

		return instructorList.get(0);
	}
	

	/**
	 * Returns the list of all instructors
	 * @return
	 */
	public List<InstructorData> getInstructors() {
		List<InstructorData> list = new LinkedList<InstructorData>();
		List<Coordinator> entities = getInstructorEntities();
		Iterator<Coordinator> it = entities.iterator();
		while(it.hasNext()) {
			list.add(new InstructorData(it.next()));
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
	 * Returns the list of instructor entities
	 */
	private List<Coordinator> getInstructorEntities() {
		String query = "select from " + Coordinator.class.getName();
			

		@SuppressWarnings("unchecked")
		List<Coordinator> instructorList = (List<Coordinator>) getPM()
				.newQuery(query).execute();
	
		return instructorList;
	}

	public void persistInstructorsFromCourses(List<InstructorData> instructorsToAdd) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, instructorsToAdd);
		
		List<Instructor> instructors = new ArrayList<Instructor>();
		for (InstructorData id : instructorsToAdd) {
			instructors.add(id.toEntity());
		}
		
		getPM().makePersistentAll(instructors);
		getPM().flush();
	}
	
	public void createAccountsForCoordinators() {
		List<Coordinator> coordinators = getInstructorEntities();
		
		List<Account> accountsToAdd = new ArrayList<Account>();
		for (Coordinator c : coordinators) {
			accountsToAdd.add(new Account(c.getGoogleID(), c.getName(), true, c.getEmail(), ""));
		}
		
		getPM().makePersistentAll(accountsToAdd);
		getPM().flush();
	}

	public void appendNameEmailForInstructors() {
		String query = "select from " + Account.class.getName()
				+ " where isInstructor == true";

		@SuppressWarnings("unchecked")
		List<Account> instructorAccounts = (List<Account>) getPM()
				.newQuery(query).execute();
		
		log.warning("SIZE OF OPERATION: " + instructorAccounts.size());
		
		for (Account a : instructorAccounts) {
			log.warning("Operating: " + a.getGoogleId());
			String instructorQuery = "select from " + Instructor.class.getName()
					+ " where googleId == '" + a.getGoogleId() + "'";
			
			@SuppressWarnings("unchecked")
			List<Instructor> instructorsOfThisAccount = (List<Instructor>) getPM()
					.newQuery(instructorQuery).execute();
			for (Instructor i : instructorsOfThisAccount) {
				log.warning("Changing from: " + i.getName());
				i.setName(a.getName());
				i.setEmail(a.getEmail());
				log.warning(" to: " + i.getName());
			}
			getPM().close();
		}
		
		getPM().close();
	}

}
