package teammates.logic;

import java.util.List;
import java.util.logging.Logger;

import teammates.common.Common;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.InstructorsDb;

/**
 * Handles  operations related to insturctor roles.
 * This class does the field validation and sanitization before 
 * passing values to the Storage layer.
 */
public class InstructorsLogic {
	//The API of this class doesn't have header comments because it sits behind
	//  the API of the logic class. Those who use this class is expected to be
	//  familiar with the its code and Logic's code. Hence, no need for header 
	//  comments.
	
	//TODO: add sanitization to this class.
	
	private static InstructorsLogic instance = null;
	private static final InstructorsDb instructorsDb = new InstructorsDb();
	
	@SuppressWarnings("unused")
	private static Logger log = Common.getLogger();
	
	public static InstructorsLogic inst() {
		if (instance == null)
			instance = new InstructorsLogic();
		return instance;
	}
	
	//TODO: have a deleteStudentCascade here?

	public void createInstructor(String googleId, String courseId, String name, String email) 
			throws InvalidParametersException, EntityAlreadyExistsException {
		
		googleId = AccountsLogic.sanitizeGoogleId(googleId);
		
		InstructorAttributes instructorToAdd = new InstructorAttributes(googleId, courseId, name, email);
		if (!instructorToAdd.isValid()) {
			throw new InvalidParametersException(instructorToAdd.getInvalidStateInfo());
		}
	
		instructorsDb.createInstructor(instructorToAdd);
	}

	public InstructorAttributes getInstructorForEmail(String courseId, String email) {
		return instructorsDb.getInstructorForEmail(courseId, email);
	}

	public InstructorAttributes getInstructorForGoogleId(String courseId, String googleId) {
		return instructorsDb.getInstructorForGoogleId(courseId, googleId);
	}

	public List<InstructorAttributes> getInstructorsForCourse(String courseId) {
		return instructorsDb.getInstructorsForCourse(courseId);
	}

	public List<InstructorAttributes> getInstructorsForGoogleId(String googleId) {
		return instructorsDb.getInstructorsForGoogleId(googleId);
	}

	/**
	 * @deprecated Not scalable. Use only for admin features.
	 */
	@Deprecated 
	public List<InstructorAttributes> getAllInstructors() {
		return instructorsDb.getAllInstructors();
	}


	public boolean isInstructorOfCourse(String instructorId, String courseId) {
		return instructorsDb.getInstructorForGoogleId(courseId, instructorId)!=null;
	}

	public void updateInstructor(InstructorAttributes instructor) 
			throws InvalidParametersException {
		if (!instructor.isValid()) {
			throw new InvalidParametersException(instructor.getInvalidStateInfo());
		}
		instructorsDb.updateInstructor(instructor);
	}

	public void deleteInstructor(String courseId, String googleId) {
		instructorsDb.deleteInstructor(courseId, googleId);
	}

	public void deleteInstructorsForGoogleId(String googleId) {
		instructorsDb.deleteInstructorsForGoogleId(googleId);
	}



}