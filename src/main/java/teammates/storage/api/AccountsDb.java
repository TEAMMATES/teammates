package teammates.storage.api;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import teammates.storage.datastore.Datastore;
import teammates.storage.entity.Coordinator;
import teammates.storage.entity.Student;
import teammates.common.Common;
import teammates.common.datatransfer.CoordData;
import teammates.common.datatransfer.StudentData;
import teammates.common.exception.EntityAlreadyExistsException;

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
	 * Adds a Coordinator object.
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
	public void addCoord(String googleID, String name, String email)
			throws EntityAlreadyExistsException {
		if (getCoord(googleID) != null) {
			throw new EntityAlreadyExistsException("Coordinator already exists :" + googleID);
		}
		Coordinator coordinator = new Coordinator(googleID, name, email);
		getPM().makePersistent(coordinator);
		getPM().flush();
		
		// Check insert operation persisted
		CoordData coordinatorCheck = getCoord(googleID);
		int elapsedTime = 0;
		while ((coordinatorCheck == null) && (elapsedTime < Common.PERSISTENCE_CHECK_DURATION)){
			Common.waitBriefly();
			coordinatorCheck = getCoord(googleID);
			elapsedTime += Common.WAIT_DURATION;
		}
		if(elapsedTime==Common.PERSISTENCE_CHECK_DURATION){
			log.severe("Operation did not persist in time: addCoord->"+googleID);
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
	 * @return the CoordData of Coordinator with the specified Google ID, or null if not
	 *         found
	 */
	public CoordData getCoord(String googleID) {
		String query = "select from " + Coordinator.class.getName()
				+ " where googleID == '" + googleID + "'";

		@SuppressWarnings("unchecked")
		List<Coordinator> coordinatorList = (List<Coordinator>) getPM()
				.newQuery(query).execute();

		if (coordinatorList.isEmpty()) {
			log.warning("Trying to get non-existent Coord : " + googleID);
			return null;
		}

		return new CoordData(coordinatorList.get(0));
	}
	
	
	
	
	
	/**
	 * RETRIEVE Student
	 * 
	 * Returns a StudentData object
	 * @param courseId
	 * @param email
	 * @return the StudentData of Student with the courseId and email
	 */
	public StudentData getStudent(String courseId, String email) {
		String query = "select from " + Student.class.getName()
				+ " where (email == '" + email + "')"
				+ " && (courseID == '" + courseId + "')";

		@SuppressWarnings("unchecked")
		List<Student> studentList = (List<Student>) getPM()
				.newQuery(query).execute();

		if (studentList.isEmpty()) {
			log.fine("Trying to get non-existent Student : " + courseId
					+ "/" + email);
			return null;
		}
		Student student = studentList.get(0);
		return new StudentData(student);
	}
	
	
	
	
	
	/**
	 * RETREIVE List<Student>
	 * 
	 * Returns a List of StudentData objects
	 * @param googleId
	 * @return List<StudentData> Each element in list are StudentData of returned Students
	 */
	public List<StudentData> getStudentsWithID(String googleId) {
		String query = "select from " + Student.class.getName() + " where ID == \"" + googleId + "\"";

		@SuppressWarnings("unchecked")
		List<Student> studentList = (List<Student>) getPM().newQuery(query).execute();

		List<StudentData> studentDataList = new ArrayList<StudentData>();
		for (Student student : studentList) {
			studentDataList.add(new StudentData(student));
		}
		
		return studentDataList;
	}
	
	
	
	
	
	/**
	 * RETRIEVE List<Coordinator>
	 * 
	 * Returns a List of CoordData objects. (Unused)
	 * 
	 * @return List<CoordData> All CoordData objects of all coordinators
	 */
	public List<CoordData> getCoordList() {
		String query = "select from " + Coordinator.class.getName();

		@SuppressWarnings("unchecked")
		List<Coordinator> coordinatorList = (List<Coordinator>) getPM()
				.newQuery(query).execute();
		
		List<CoordData> coordinatorDataList = new ArrayList<CoordData>();
		for (Coordinator coord : coordinatorList) {
			coordinatorDataList.add(new CoordData(coord));
		}

		return coordinatorDataList;
	}
	
	
	
	
	
	/**
	 * DELETE Coordinator
	 * @param coordId
	 */
	public void deleteCoord(String coordId) {
		String query = "select from " + Coordinator.class.getName()
				+ " where googleID == '" + coordId + "'";

		@SuppressWarnings("unchecked")
		List<Coordinator> coordinatorList = (List<Coordinator>) getPM()
				.newQuery(query).execute();

		if (coordinatorList.isEmpty()) {
			log.warning("Trying to delete non-existent Coord : " + coordId);
			return;
		}
		
		Coordinator coord = coordinatorList.get(0);
		
		getPM().deletePersistent(coord);
		getPM().flush();
		
		// Check delete operation persisted
		int elapsedTime = 0;
		CoordData coordinatorCheck = getCoord(coordId);
		while ((coordinatorCheck != null) && (elapsedTime < Common.PERSISTENCE_CHECK_DURATION)){
			Common.waitBriefly();
			coordinatorCheck = getCoord(coordId);
			elapsedTime += Common.WAIT_DURATION;
		}
		if(elapsedTime==Common.PERSISTENCE_CHECK_DURATION){
			log.severe("Operation did not persist in time: deleteCoord->"+coordId);
		}
		
	}
}
