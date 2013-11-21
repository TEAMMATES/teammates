package teammates.logic.core;

import java.util.List;
import java.util.logging.Logger;

import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Utils;
import teammates.storage.api.InstructorsDb;

/**
 * Handles  operations related to instructor roles.
 */
public class InstructorsLogic {
	//The API of this class doesn't have header comments because it sits behind
	//  the API of the logic class. Those who use this class is expected to be
	//  familiar with the its code and Logic's code. Hence, no need for header 
	//  comments.
	
	public static final String ERROR_NO_INSTRUCTOR_LINES = "Course must have at lease one instructor\n";
	
	private static final InstructorsDb instructorsDb = new InstructorsDb();
	private static final AccountsLogic accountsLogic = AccountsLogic.inst();
	
	private static Logger log = Utils.getLogger();
	
	private static InstructorsLogic instance = null;
	public static InstructorsLogic inst() {
		if (instance == null)
			instance = new InstructorsLogic();
		return instance;
	}
	

	public void createInstructor(String googleId, String courseId, String name, String email) 
			throws InvalidParametersException, EntityAlreadyExistsException {
				
		InstructorAttributes instructorToAdd = new InstructorAttributes(googleId, courseId, name, email);
		
		createInstructor(instructorToAdd);
	}

	public void createInstructor(InstructorAttributes instructorToAdd) 
			throws InvalidParametersException, EntityAlreadyExistsException {
		
		log.info("going to create instructor :\n"+instructorToAdd.toString());
		
		instructorsDb.createEntity(instructorToAdd);
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
	
	public void verifyInstructorExists(String instructorId)
			throws EntityDoesNotExistException {
		if (!accountsLogic.isAccountAnInstructor(instructorId)) {
			throw new EntityDoesNotExistException("Instructor does not exist :"
					+ instructorId);
		}
	}

	public void updateInstructor(String courseId, String googleId, String name, String email) 
			throws InvalidParametersException {
		
		InstructorAttributes instructorToUpdate = getInstructorForGoogleId(courseId, googleId);
		instructorToUpdate.name = name;
		instructorToUpdate.email = email;
		
		instructorsDb.updateInstructor(instructorToUpdate);
	}

	public void deleteInstructor(String courseId, String googleId) {
		instructorsDb.deleteInstructor(courseId, googleId);
	}

	public void deleteInstructorsForGoogleId(String googleId) {
		instructorsDb.deleteInstructorsForGoogleId(googleId);
	}

	public void deleteInstructorsForCourse(String courseId) {
		instructorsDb.deleteInstructorsForCourse(courseId);
	}

}