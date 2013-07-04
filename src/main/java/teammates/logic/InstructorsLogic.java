package teammates.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
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
	
	private static Logger log = Config.getLogger();
	
	private static InstructorsLogic instance = null;
	public static InstructorsLogic inst() {
		if (instance == null)
			instance = new InstructorsLogic();
		return instance;
	}
	
	//TODO: have a deleteStudentCascade here?

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

	public void updateInstructor(InstructorAttributes instructor) 
			throws InvalidParametersException {
		instructorsDb.updateInstructor(instructor);
	}

	public void updateCourseInstructors(
			String courseId, String instructorLines, String courseInstitute) 
					throws InvalidParametersException {
		
		// Prepare the list to be updated
		List<InstructorAttributes> instructorsList = 
				parseInstructorLines(courseId, instructorLines);
	
		// Retrieve the current list of instructors
		// Remove those that are not in the list and persist the new ones
		// Edit the ones that are found in both lists
		List<InstructorAttributes> currentInstructors = getInstructorsForCourse(courseId);
	
		List<InstructorAttributes> toAdd = new ArrayList<InstructorAttributes>();
		List<InstructorAttributes> toRemove = new ArrayList<InstructorAttributes>();
		List<InstructorAttributes> toEdit = new ArrayList<InstructorAttributes>();
	
		// Find new names
		for (InstructorAttributes id : instructorsList) {
			boolean found = false;
			for (InstructorAttributes currentInstructor : currentInstructors) {
				if (id.googleId.equals(currentInstructor.googleId)) {
					toEdit.add(id);
					found = true;
				}
			}
			if (!found) {
				toAdd.add(id);
			}
		}
	
		// Find lost names
		for (InstructorAttributes currentInstructor : currentInstructors) {
			boolean found = false;
			for (InstructorAttributes id : instructorsList) {
				if (id.googleId.equals(currentInstructor.googleId)) {
					found = true;
				}
			}
			if (!found) {
				toRemove.add(currentInstructor);
			}
		}
	
		// Operate on each of the lists respectively
		for (InstructorAttributes add : toAdd) {
			try {
				accountsLogic.createInstructorAccount(add.googleId, courseId,
						add.name, add.email, courseInstitute);
			} catch (EntityAlreadyExistsException e) {
				// This should happens when a row was accidentally entered twice
				// When that happens we continue silently
			}
		}
		for (InstructorAttributes remove : toRemove) {
			deleteInstructor(remove.courseId, remove.googleId);
		}
		for (InstructorAttributes edit : toEdit) {
			updateInstructor(edit);
		}
	
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


	private List<InstructorAttributes> parseInstructorLines(String courseId, String instructorLines) 
			throws InvalidParametersException {
		String[] linesArray = instructorLines.split(Const.EOL);
		
		// check if all non-empty lines are formatted correctly
		List<InstructorAttributes> instructorsList = new ArrayList<InstructorAttributes>();
		for (int i = 0; i < linesArray.length; i++) {
			String information = linesArray[i];
			if (StringHelper.isWhiteSpace(information)) {
				continue;
			}
			instructorsList.add(new InstructorAttributes(courseId, information));
		}
		
		if (instructorsList.size() < 1) {
			throw new InvalidParametersException(ERROR_NO_INSTRUCTOR_LINES);
		}
		
		return instructorsList;
	}

}