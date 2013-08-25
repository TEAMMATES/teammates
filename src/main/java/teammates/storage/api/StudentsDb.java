package teammates.storage.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.Query;

import teammates.common.datatransfer.EntityAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.common.util.ThreadHelper;
import teammates.common.util.Utils;
import teammates.storage.entity.Student;

import com.google.appengine.api.datastore.KeyFactory;

/**
 * Handles CRUD Operations for student entities.
 * The API uses data transfer classes (i.e. *Attributes) instead of persistable classes.
 * 
 */
public class StudentsDb extends EntitiesDb {

	public static final String ERROR_UPDATE_EMAIL_ALREADY_USED = "Trying to update to an email that is already used by: ";
	
	private static final Logger log = Utils.getLogger();

	/**
	 * Preconditions: <br>
	 * * All parameters are non-null.
	 * 
	 * @return The data for Student with the courseId and email. Returns null if
	 *         there is no such student.
	 */
	public StudentAttributes getStudentForEmail(String courseId, String email) {
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, email);
	
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
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, googleId);
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

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
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, registrationKey);
		StudentAttributes studentAttributes;
		registrationKey = registrationKey.trim();
		String originalKey = registrationKey;
		try {
			//First, try to retrieve the student by assuming the given registrationKey key is encrypted
			registrationKey = StringHelper.decrypt(registrationKey);
			Student student = getPM().getObjectById(Student.class,
					KeyFactory.stringToKey(registrationKey));
			studentAttributes = new StudentAttributes(student); 
		} catch (Exception e) {
			try {
				//Failing that, we try to retrieve assuming the given registrationKey is unencrypted 
				//  (early versions of the system sent unencrypted keys).
				//TODO: This branch can be removed after Dec 2013
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
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, googleId);
		
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
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
		
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
	public List<StudentAttributes> getStudentsForTeam(String teamName, String courseId) {
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, teamName);
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
		
		List<Student> studentList = getStudentEntitiesForTeam(teamName, courseId);
		
		List<StudentAttributes> studentDataList = new ArrayList<StudentAttributes>();
	
		//TODO: See if we can use a generic method to convert a list of entities to a list of attributes.
		//  e.g., convertToAttributes(entityList, new ArrayList<StudentAttributes>())
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
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
		
		List<StudentAttributes> allStudents = getStudentsForCourse(courseId);
		ArrayList<StudentAttributes> unregistered = new ArrayList<StudentAttributes>();
		
		for(StudentAttributes s: allStudents){
			if(s.googleId==null || s.googleId.trim().isEmpty()){
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
			String newComments)
			throws InvalidParametersException, EntityDoesNotExistException {
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, email);
		
		//TODO: Sanitize values and update tests accordingly
		
		verifyStudentExists(courseId, email);
		
		Student student = getStudentEntityForEmail(courseId, email);
		Student studentWithNewEmail = getStudentEntityForEmail(courseId, newEmail);
		
		if (studentWithNewEmail != null && !studentWithNewEmail.equals(student)) {
			String error = ERROR_UPDATE_EMAIL_ALREADY_USED
					+ studentWithNewEmail.getName() + "/" + studentWithNewEmail.getEmail();
			throw new InvalidParametersException(error);
		}

		student.setEmail(newEmail);
		student.setName(newName);
		student.setComments(newComments);
		student.setGoogleId(newGoogleID);
		student.setTeamName(newTeamName);

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
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, email);
	
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
				&& (elapsedTime < Config.PERSISTENCE_CHECK_DURATION)) {
			ThreadHelper.waitBriefly();
			studentCheck = getStudentEntityForEmail(courseId, email);
			elapsedTime += ThreadHelper.WAIT_DURATION;
		}
		if (elapsedTime == Config.PERSISTENCE_CHECK_DURATION) {
			log.severe("Operation did not persist in time: deleteStudent->"
					+ courseId + "/" + email);
		}
		
		//TODO: use the method in the parent class instead.
	}

	/**
	 * Fails silently if no such student. <br>
	 * Preconditions: <br>
	 *  * All parameters are non-null.
	 *  
	 */
	public void deleteStudentsForGoogleId(String googleId) {
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, googleId);

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
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
	
		List<Student> studentList = getStudentEntitiesForCourse(courseId);
	
		getPM().deletePersistentAll(studentList);
		getPM().flush();
	}

	public void verifyStudentExists(String courseId, String email) 
			throws EntityDoesNotExistException {
		
		if (getStudentForEmail(courseId, email) == null) {
			String error = ERROR_UPDATE_NON_EXISTENT_STUDENT +
					courseId + "/" + email;
			throw new EntityDoesNotExistException(error);
		}
		
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

	private List<Student> getStudentEntitiesForTeam(String teamName, String courseId) {
		Query q = getPM().newQuery(Student.class);
		q.declareParameters("String teamNameParam, String courseIDParam");
		q.setFilter("teamName == teamNameParam && courseID == courseIDParam");
		
		@SuppressWarnings("unchecked")
		List<Student> studentList = (List<Student>) q.execute(teamName, courseId);
		
		return studentList;
	}

	private List<Student> getStudentEntities() { 
		
		Query q = getPM().newQuery(Student.class);
		
		@SuppressWarnings("unchecked")
		List<Student> studentList = (List<Student>) q.execute();
		
		return studentList;
	}

	@Override
	protected Object getEntity(EntityAttributes entity) {
		StudentAttributes studentToGet = (StudentAttributes) entity;
		return getStudentForEmail(studentToGet.course, studentToGet.email);
	}
	

}

