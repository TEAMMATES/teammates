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
import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.storage.datastore.Datastore;
import teammates.storage.entity.Account;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;

import com.google.appengine.api.datastore.KeyFactory;

/**
 * Handles CRUD Operations for accounts and Role classes (i.e. Student and Instructor).
 * The API uses data transfer classes (i.e. *Attributes) instead of presistable classes.
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

	@SuppressWarnings("unused")
	private void ____ACCOUNT_related_______________________________________() {
	}
	
	/**
	 * Preconditions: 
	 * <br> * {@code accountToAdd} is not null and has valid data.
	 */
	public void createAccount(AccountAttributes accountToAdd) {
		
		//TODO: why doesn't this throw EntityAlreadyExistsException?
		
		Assumption.assertNotNull(
				Common.ERROR_DBLEVEL_NULL_INPUT, accountToAdd);
		Assumption.assertTrue(
				"Invalid object received as a parameter :" + accountToAdd.getInvalidStateInfo().toString(),
				accountToAdd.isValid());
		
		Account newAccount = accountToAdd.toEntity();
		getPM().makePersistent(newAccount);
		getPM().flush();

		// Wait for the operation to persist
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
	 * Preconditions: 
	 * <br> * {@code accountsToAdd} is not null and 
	 * contains {@link AccountAttributes} objects with valid data.
	 */
	public void createAccounts(List<AccountAttributes> accountsToAdd) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, accountsToAdd);
		
		List<Account> accounts = new ArrayList<Account>();
		for (AccountAttributes ad : accountsToAdd) {
			Assumption.assertTrue(
					"Invalid object received as a parameter" + ad.getInvalidStateInfo().toString(),
					ad.isValid());
			accounts.add(ad.toEntity());
		}
		
		getPM().makePersistentAll(accounts);
		getPM().flush();
	}
	
	/**
	 * Preconditions: 
	 * <br> * All parameters are non-null. 
	 */
	public AccountAttributes getAccount(String googleId) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, googleId);
	
		Account a = getAccountEntity(googleId);
	
		if (a == null) {
			log.info("Trying to get non-existent Account: " + googleId);
			return null;
		}
	
		return new AccountAttributes(a);
	}


	/**
	 * @return {@link AccountAttribute} objects for all accounts with instructor privileges.
	 *   Returns an empty list if no such accounts are found.
	 */
	public List<AccountAttributes> getInstructorAccounts() {
		Query q = getPM().newQuery(Account.class);
		q.setFilter("isInstructor == true");
		
		@SuppressWarnings("unchecked")
		List<Account> accountsList = (List<Account>) q.execute();
		
		List<AccountAttributes> instructorsAccountData = new ArrayList<AccountAttributes>();
		
		for (Account a : accountsList) {
			instructorsAccountData.add(new AccountAttributes(a));
		}
		
		return instructorsAccountData;
	}

	/**
	 * Preconditions: 
	 * <br> * {@code accountToAdd} is not null and has valid data.
	 * <br> * {@code accountToAdd} corresponds to a valid account.
	 */
	public void updateAccount(AccountAttributes a) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, a);
		Assumption.assertTrue(
				"Invalid object received as a parameter" + a.getInvalidStateInfo().toString(),
				a.isValid());
		
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
	 * Note: This deletes the {@link Account} object only. It does not delete
	 *   data that belongs to this account (e.g., courses).
	 *   <br> Fails silently if there is no such account.
	 * <br> Preconditions: 
	 * <br> * {@code googleId} is not null.
	 */
	public void deleteAccount(String googleId) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, googleId);
	
		Account accountToDelete = getAccountEntity(googleId);
	
		if (accountToDelete == null) {
			return;
		}
	
		getPM().deletePersistent(accountToDelete);
		getPM().flush();
	
		// Wait for the operation to persist
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
		
		//TODO: the above piece of code is duplicated in many places. 
		//  Eliminate using anonymous classes? e.g., similar to the way sorting works
	}

	@SuppressWarnings("unused")
	private void ____STUDENT_related_______________________________________() {
	}
	
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
	
		Assumption.assertNotNull(ERROR_UPDATE_NON_EXISTENT_STUDENT + courseId
				+ "/ + email " + Common.getCurrentThreadStack(), student);
	
		//TODO: Enhance to ensure the updated entity is valid.
		
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

	@SuppressWarnings("unused")
	private void ____INSTRUCTOR_related____________________________________() {
	}

	/**
	  * Preconditions: 
	 * <br> * {@code instructorToAdd} is not null and has valid data.
	 */
	public void createInstructor(InstructorAttributes instructorToAdd)
			throws EntityAlreadyExistsException {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, instructorToAdd);

		Assumption.assertTrue(
				"Invalid object received as a parameter :" + Common.toString(instructorToAdd.getInvalidStateInfo()),
				instructorToAdd.isValid());

		if (getInstructorEntityForGoogleId(instructorToAdd.courseId, instructorToAdd.googleId) != null) {
			String error = ERROR_CREATE_INSTRUCTOR_ALREADY_EXISTS
					+ instructorToAdd.googleId + ", " + instructorToAdd.courseId;
			throw new EntityAlreadyExistsException(error);
		}

		Instructor newInstructor = instructorToAdd.toEntity();
		getPM().makePersistent(newInstructor);
		getPM().flush();

		// Wait for the operation to persist
		int elapsedTime = 0;
		Instructor instructorCheck = getInstructorEntityForGoogleId(instructorToAdd.courseId, instructorToAdd.googleId);
		while ((instructorCheck == null)
				&& (elapsedTime < Common.PERSISTENCE_CHECK_DURATION)) {
			Common.waitBriefly();
			instructorCheck = getInstructorEntityForGoogleId(instructorToAdd.courseId, instructorToAdd.googleId);
			elapsedTime += Common.WAIT_DURATION;
		}
		if (elapsedTime == Common.PERSISTENCE_CHECK_DURATION) {
			log.severe("Operation did not persist in time: createInstructor->"
					+ instructorToAdd.googleId);
		}
	}
	
	/**
	 * Preconditions: <br>
	 *  * All parameters are non-null.
	 * @return empty list if no matching objects. 
	 */
	public InstructorAttributes getInstructorForEmail(String courseId, String email) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, email);
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);
	
		Instructor i = getInstructorEntityForEmail(courseId, email);
	
		if (i == null) {
			log.info("Trying to get non-existent Instructor: " + courseId +"/"+ email );
			return null;
		}
	
		return new InstructorAttributes(i);
	}

	
	/**
	 * Preconditions: <br>
	 *  * All parameters are non-null.
	 * @return empty list if no matching objects. 
	 */
	public InstructorAttributes getInstructorForGoogleId(String courseId, String googleId) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, googleId);
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);
	
		Instructor i = getInstructorEntityForGoogleId(courseId, googleId);
	
		if (i == null) {
			log.info("Trying to get non-existent Instructor: " + googleId);
			return null;
		}
	
		return new InstructorAttributes(i);
	}

	/**
	 * Preconditions: <br>
	 *  * All parameters are non-null.
	 * @return empty list if no matching objects. 
	 */
	public List<InstructorAttributes> getInstructorsForGoogleId(String googleId) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, googleId);
		
		List<Instructor> instructorList = getInstructorEntitiesForGoogleId(googleId);
		
		List<InstructorAttributes> instructorDataList = new ArrayList<InstructorAttributes>();
		for (Instructor i : instructorList) {
			instructorDataList.add(new InstructorAttributes(i));
		}
		
		return instructorDataList;
	}
	
	/**
	 * Preconditions: <br>
	 *  * All parameters are non-null.
	 * @return empty list if no matching objects. 
	 */
	public List<InstructorAttributes> getInstructorsForCourse(String courseId) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);
		
		List<Instructor> instructorList = getInstructorEntitiesForCourse(courseId);
		
		List<InstructorAttributes> instructorDataList = new ArrayList<InstructorAttributes>();
		for (Instructor i : instructorList) {
			instructorDataList.add(new InstructorAttributes(i));
		}
		
		return instructorDataList;
	}
	
	/**
	 * Not scalable. Don't use unless for admin features.
	 * @return {@code InstructorAttributes} objects for all instructor 
	 * roles in the system.
	 */
	@Deprecated
	public List<InstructorAttributes> getAllInstructors() {
		List<InstructorAttributes> list = new LinkedList<InstructorAttributes>();
		List<Instructor> entities = getInstructorEntities();
		Iterator<Instructor> it = entities.iterator();
		while(it.hasNext()) {
			list.add(new InstructorAttributes(it.next()));
		}	
		return list;
	}
	

	/**
	 * Updates the instructor. Cannot modify Course ID or email.
	 * Updates only name and email.<br>
	 * Does not follow the 'keep existing' policy <br> 
	 * Preconditions: <br>
	 * * {@code courseId} and {@code email} are non-null and correspond to an existing student. <br>
	 */
	public void updateInstructor(InstructorAttributes instructorAttributesToUpdate) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, instructorAttributesToUpdate);
		
		Assumption.assertTrue(
				"Invalid object received as a parameter :" + Common.toString(instructorAttributesToUpdate.getInvalidStateInfo()), 
				instructorAttributesToUpdate.isValid());
		
		Instructor instructorToUpdate = getInstructorEntityForGoogleId(instructorAttributesToUpdate.courseId, instructorAttributesToUpdate.googleId);
		
		Assumption.assertNotNull(ERROR_UPDATE_NON_EXISTENT_ACCOUNT + instructorAttributesToUpdate.googleId
				+ Common.getCurrentThreadStack(), instructorToUpdate);
		
		instructorToUpdate.setName(instructorAttributesToUpdate.name);
		instructorToUpdate.setEmail(instructorAttributesToUpdate.email);
		
		//TODO: update institute name
		//TODO: make courseId+email the non-modifiable values
		
		getPM().close();
	}
	
	/**
	 * Fails silently if no such instructor. <br>
	 * Preconditions: <br>
	 *  * All parameters are non-null.
	 */
	public void deleteInstructor(String courseId, String googleId) {
		//TODO: in future, courseId+email should be the key, not courseId+googleId

		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, googleId);
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);

		Instructor instructorToDelete = getInstructorEntityForGoogleId(courseId, googleId);

		if (instructorToDelete == null) {
			return;
		}

		getPM().deletePersistent(instructorToDelete);
		getPM().flush();

		// Check delete operation persisted
		int elapsedTime = 0;
		Instructor instructorCheck = getInstructorEntityForGoogleId(courseId, googleId);
		while ((instructorCheck != null)
				&& (elapsedTime < Common.PERSISTENCE_CHECK_DURATION)) {
			Common.waitBriefly();
			instructorCheck = getInstructorEntityForGoogleId(courseId, googleId);
			elapsedTime += Common.WAIT_DURATION;
		}
		if (elapsedTime == Common.PERSISTENCE_CHECK_DURATION) {
			log.severe("Operation did not persist in time: deleteInstructor->"
					+ googleId);
		}
	}
	
	/**
	 * Fails silently if no such instructor. <br>
	 * Preconditions: <br>
	 *  * All parameters are non-null.
	 */
	public void deleteInstructorsForGoogleId(String googleId) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, googleId);

		List<Instructor> instructorList = getInstructorEntitiesForGoogleId(googleId);

		getPM().deletePersistentAll(instructorList);
		getPM().flush();
	}
	
	/**
	 * Fails silently if no such instructor. <br>
	 * Preconditions: <br>
	 *  * All parameters are non-null.
	 */
	public void deleteInstructorsForCourse(String courseId) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);

		List<Instructor> instructorList = getInstructorEntitiesForCourse(courseId);

		getPM().deletePersistentAll(instructorList);
		getPM().flush();
	}
	
	@SuppressWarnings("unused")
	private void ____PRIVATE_methods____________________________________() {
	}

	
	private PersistenceManager getPM() {
		return Datastore.getPersistenceManager();
	}


	private Account getAccountEntity(String googleId) {
		
		Query q = getPM().newQuery(Account.class);
		q.declareParameters("String googleIdParam");
		q.setFilter("googleId == googleIdParam");
		
		@SuppressWarnings("unchecked")
		List<Account> accountsList = (List<Account>) q.execute(googleId);
		
		if (accountsList.isEmpty() || JDOHelper.isDeleted(accountsList.get(0))) {
			return null;
		}
	
		return accountsList.get(0);
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

	private Instructor getInstructorEntityForGoogleId(String courseId, String googleId) {
		
		Query q = getPM().newQuery(Instructor.class);
		q.declareParameters("String googleIdParam, String courseIdParam");
		q.setFilter("googleId == googleIdParam && courseId == courseIdParam");
		
		@SuppressWarnings("unchecked")
		List<Instructor> instructorList = (List<Instructor>) q.execute(googleId, courseId);
		
		if (instructorList.isEmpty()
				|| JDOHelper.isDeleted(instructorList.get(0))) {
			return null;
		}

		return instructorList.get(0);
	}
	
	private Instructor getInstructorEntityForEmail(String courseId, String email) {
		
		Query q = getPM().newQuery(Instructor.class);
		q.declareParameters("String courseIdParam, String emailParam");
		q.setFilter("courseId == courseIdParam && email == emailParam");
		
		@SuppressWarnings("unchecked")
		List<Instructor> instructorList = (List<Instructor>) q.execute(courseId, email);
		
		if (instructorList.isEmpty()
				|| JDOHelper.isDeleted(instructorList.get(0))) {
			return null;
		}

		return instructorList.get(0);
	}
	
	private List<Instructor> getInstructorEntitiesForGoogleId(String googleId) {
		
		Query q = getPM().newQuery(Instructor.class);
		q.declareParameters("String googleIdParam");
		q.setFilter("googleId == googleIdParam");
		
		@SuppressWarnings("unchecked")
		List<Instructor> instructorList = (List<Instructor>) q.execute(googleId);
		
		return instructorList;
	}

	private List<Instructor> getInstructorEntitiesForCourse(String courseId) {
		
		Query q = getPM().newQuery(Instructor.class);
		q.declareParameters("String courseIdParam");
		q.setFilter("courseId == courseIdParam");
		
		@SuppressWarnings("unchecked")
		List<Instructor> instructorList = (List<Instructor>) q.execute(courseId);
		
		return instructorList;
	}

	private List<Instructor> getInstructorEntities() {
		String query = "select from " + Instructor.class.getName();
			
		@SuppressWarnings("unchecked")
		List<Instructor> instructorList = (List<Instructor>) getPM()
				.newQuery(query).execute();
	
		return instructorList;
	}
	

}

