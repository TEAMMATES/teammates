package teammates;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import teammates.exception.TeamFormingSessionExistsException;
import teammates.exception.TeamProfileExistsException;
import teammates.jdo.*;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public class TeamForming {
	private static TeamForming instance = null;
	private static final Logger log = Logger.getLogger(Evaluations.class
			.getName());

	/**
	 * Constructs an Accounts object. Obtains an instance of PersistenceManager
	 * class to handle datastore transactions.
	 */
	private TeamForming() {
	}

	private PersistenceManager getPM() {
		return Datastore.getPersistenceManager();
	}

	public static TeamForming inst() {
		if (instance == null)
			instance = new TeamForming();
		return instance;
	}
	
	/**
	 * Adds a team forming session to the specified course.
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: Must be valid)
	 * 
	 * @param name
	 *            the evaluation name (Pre-condition: Must not be null)
	 * 
	 * @param instructions
	 *            the instructions for the evaluation (Pre-condition: Must not
	 *            be null)
	 * 
	 * @param commentsEnabled
	 *            if students are allowed to make comments (Pre-condition: Must
	 *            not be null)
	 * 
	 * @param start
	 *            the start date/time of the evaluation (Pre-condition: Must not
	 *            be null)
	 * 
	 * @param deadline
	 *            the deadline of the evaluation (Pre-condition: Must not be
	 *            null)
	 * 
	 * @param gracePeriod
	 *            the amount of time after the deadline within which submissions
	 *            will still be accepted (Pre-condition: Must not be null)
	 * 
	 * @throws EvaluationExistsException
	 *             if an evaluation with the specified name exists for the
	 *             course
	 */
	public void createTeamFormingSession(String courseID, Date start, Date deadline, double timeZone, int gracePeriod,
			String instructions, String profileTemplate)
			throws TeamFormingSessionExistsException {
		if (getTeamFormingSession(courseID, deadline) != null) {
			throw new TeamFormingSessionExistsException();
		}

		TeamFormingSession teamFormingSession = new TeamFormingSession(courseID, start, deadline, timeZone, gracePeriod,
				instructions, profileTemplate);

		try {
			getPM().makePersistent(teamFormingSession);
		} finally {
		}

		// pending
		// Build TeamProfile objects for each team based on the courseID
		//createTeamProfile(courseID, teamName);
	}
	
	/**
	 * Adds a team profile
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: Must be valid)
	 * 
	 * @param course name
	 *            the course name (Pre-condition: Must not be null)
	 * 
	 * @param team name
	 *            the team name (Pre-condition: Must not be null)
	 * 
	 * @param team profile
	 *            the team profile (Pre-condition: Must not be null)
	 */
	public void createTeamProfile(String courseId, String courseName, String teamName, 
			String teamProfile) throws TeamProfileExistsException{
		if (getTeamProfile(courseId, teamName) != null) {
			throw new TeamProfileExistsException();
		}
		TeamProfile newTeamProfile = new TeamProfile(courseId, courseName, teamName, teamProfile);

		try {
			getPM().makePersistent(newTeamProfile);
		} finally {
		}
	}
	
	/**
	 * Edits a student object
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: Must be valid)
	 * 
	 * @param team name
	 *            the team name (Pre-condition: Must not be null)
	 * 
	 * @param new team name
	 *            the updated team name (Pre-condition: Must not be null)
	 */
	public void editStudentTeam(String courseId, String teamName, String newTeamName){
		List<Student> studentList = getStudentsOfCourseTeam(courseId, teamName);
		for(int loop=0; loop<studentList.size(); loop++){
			if(studentList.get(loop).getTeamName().equals(teamName))
				studentList.get(loop).setTeamName(newTeamName);
		}
	}
	
	/**
	 * Edits a team profile
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: Must be valid)
	 * 
	 * @param course name
	 *            the course name (Pre-condition: Must not be null)
	 * 
	 * @param team name
	 *            the team name (Pre-condition: Must not be null)
	 * 
	 * @param team profile
	 *            the team profile (Pre-condition: Must not be null)
	 */
	public boolean editTeamProfile(String courseId, String courseName, String teamName, 
			String newTeamName, String newTeamProfile){
		int newTeamNameExists = 0;
		
		TeamProfile tProfile = getTeamProfile(courseId, teamName);
		List<String> teamList = getTeamsOfCourse(courseId);
		
		
		for(int i=0;i<teamList.size(); i++)
		{
			if(teamList.get(i).equals(newTeamName))
				newTeamNameExists = 1;
		}
		
		if (tProfile != null && newTeamNameExists==0) {			
			Transaction tx = getPM().currentTransaction();
			try {
				tx.begin();
				tProfile.setTeamProfile(newTeamProfile);
				tProfile.setTeamName(newTeamName);

				getPM().flush();

				tx.commit();
			} finally {
				if (tx.isActive()) {
					tx.rollback();
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Returns a String array of teams.
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: Must not be null)
	 * 
	 * @return the list of teams that are formed under a course
	 */
	public List<String> getTeamsOfCourse(String courseId){
		String query = "select teamName from " + Student.class.getName()
				+ " where courseID == '" + courseId + "'";
		
		try{
			@SuppressWarnings("unchecked")
			List<String> teams = (List<String>) getPM().newQuery(
					query).execute();
			if(teams.isEmpty())
				return null;
			return teams;
		}
		catch(Exception e){
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	/**
	 * Returns a Student object.
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: Must not be null)
	 * 
	 * @param team name
	 *            the team name (Pre-condition: Must not be null)
	 * 
	 * @return the students enrolled in courseid and team name
	 */
	public List<Student> getStudentsOfCourseTeam(String courseID, String teamName) {
		String query = "select from " + Student.class.getName()
				+ " where courseID == '" + courseID + "' && teamName == '" + teamName + "'";
		List<Student> studentList = new ArrayList<Student>();

		@SuppressWarnings("unchecked")
		List<Student> tempStudentList = (List<Student>) getPM()
		.newQuery(query).execute();
		studentList.addAll(tempStudentList);

		return studentList;
	}
	
	/**
	 * Returns an TeamProfile object.
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: Must not be null)
	 * 
	 * @param team name
	 *            the team name (Pre-condition: Must not be null)
	 * 
	 * @return the team profile of the specified courseid and team name
	 */
	public TeamProfile getTeamProfile(String courseID, String teamName) {
		String query = "select from " + TeamProfile.class.getName()
				+ " where courseID == '" + courseID + "' && teamName == '" + teamName + "'";
		
		try{
			@SuppressWarnings("unchecked")
			List<TeamProfile> tProfile = (List<TeamProfile>) getPM().newQuery(
					query).execute();
			if(tProfile.isEmpty())
				return null;

			return tProfile.get(0);
		}
		catch(Exception e){
			System.out.println(e.getMessage());
			return null;
		}
	}	
	
	public List<TeamProfile> getTeamProfiles(String courseID) {
		String query = "select from " + TeamProfile.class.getName()
				+ " where courseID == '" + courseID + "'";
		
		try{
			@SuppressWarnings("unchecked")
			List<TeamProfile> tProfile = (List<TeamProfile>) getPM().newQuery(
					query).execute();
			if(tProfile.isEmpty())
				return null;

			return tProfile;
		}
		catch(Exception e){
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	/**
	 * Returns an TeamFormingSession object.
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: Must not be null)
	 * 
	 * @param name
	 *            the deadline(endTime) (Pre-condition: Must not be null)
	 * 
	 * @return the team forming session of the specified course and deadline
	 */
	public TeamFormingSession getTeamFormingSession(String courseID, Date deadline) {
//		String query = "select from " + TeamFormingSession.class.getName()
//				+ " where endTime == '" + deadline + "' && courseID == '" + courseID
//				+ "'";
		String query = "select from " + TeamFormingSession.class.getName()
				+ " where courseID == '" + courseID + "'";
		
		//System.out.println("query is: "+query);
		try{
			@SuppressWarnings("unchecked")
			List<TeamFormingSession> teamFormingSessionList = (List<TeamFormingSession>) getPM().newQuery(
					query).execute();
			if(teamFormingSessionList.isEmpty())
				return null;

			return teamFormingSessionList.get(0);
		}
		catch(Exception e){
			System.out.println(e.getMessage());
			return null;
		}
	}	
	
	/**
	 * Returns the Team Forming Session objects belonging to some Course objects.
	 * 
	 * @param courseList
	 *            a list of courses (Pre-condition: Must not be null)
	 * 
	 * @return the list of team forming sessions belonging to the list of courses
	 */
	public List<TeamFormingSession> getTeamFormingSessionList(List<Course> courseList) {
		List<TeamFormingSession> teamFormingSessionList = new ArrayList<TeamFormingSession>();

		for (Course c : courseList) {
			String query = "select from " + TeamFormingSession.class.getName()
					+ " where courseID == '" + c.getID() + "'";

			@SuppressWarnings("unchecked")
			List<TeamFormingSession> tempTeamFormingSessionList = (List<TeamFormingSession>) getPM()
					.newQuery(query).execute();
			teamFormingSessionList.addAll(tempTeamFormingSessionList);
		}

		return teamFormingSessionList;
	}
	
	/**
	 * Checks every team forming session and activate it if the current time
	 * is later than start time.
	 * 
	 * @return list of team forming sessions that were activated in the function call
	 */
	public List<TeamFormingSession> activateTeamFormingSessions() {
		List<TeamFormingSession> teamFormingSessionList = getAllTeamFormingSessions();
		List<TeamFormingSession> activatedTeamFormingSessionList = new ArrayList<TeamFormingSession>();

		Calendar c1 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		Calendar c2 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

		for (TeamFormingSession t : teamFormingSessionList) {
			// Fix the time zone accordingly
			c1.add(Calendar.MILLISECOND,
					(int) (60 * 60 * 1000 * t.getTimeZone()));

			c2.setTime(t.getStart());

			if (c1.after(c2) || c1.equals(c2)) {
				if (t.isActivated() == false) {
					t.setActivated(true);
					activatedTeamFormingSessionList.add(t);
				}
			}
			
			// Revert time zone change
			c1.add(Calendar.MILLISECOND,
					(int) (-60 * 60 * 1000 * t.getTimeZone()));
		}

		return activatedTeamFormingSessionList;
	}
	
	/**
	 * Returns all Team Forming Sessions objects.
	 * 
	 * @return the list of all evaluations
	 */
	public List<TeamFormingSession> getAllTeamFormingSessions() {
		String query = "select from " + TeamFormingSession.class.getName();

		@SuppressWarnings("unchecked")
		List<TeamFormingSession> teamFormingSessionList = (List<TeamFormingSession>) getPM().newQuery(
				query).execute();

		return teamFormingSessionList;
	}
	
	/**
	 * Adds to TaskQueue emails to inform students of an Team Forming Session.
	 * 
	 * @param studentList
	 *            the list of students to be informed
	 * @param courseID
	 *            the course ID
	 * @param deadline
	 *            the deadline of the team forming session
	 */
	public void informStudentsOfTeamFormingSessionOpening(List<Student> studentList,
			String courseID, Date deadline) {
		Queue queue = QueueFactory.getQueue("email-queue");
		List<TaskOptions> taskOptionsList = new ArrayList<TaskOptions>();

		for (Student s : studentList) {
			// There is a limit of 100 tasks per batch addition to Queue in
			// Google App
			// Engine
			if (taskOptionsList.size() == 100) {
				queue.add(taskOptionsList);
				taskOptionsList = new ArrayList<TaskOptions>();
			}

			taskOptionsList.add(TaskOptions.Builder.withUrl("/email")
					.param("operation", "informstudentsofevaluationopening")
					.param("email", s.getEmail()).param("name", s.getName())
					.param("courseid", courseID)
					.param("deadline", deadline.toString()));
		}

		if (!taskOptionsList.isEmpty()) {
			queue.add(taskOptionsList);
		}
	}
	
	/**
	 * Deletes a Team Forming Session and its Submission objects.
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: The courseID and deadline
	 *            pair must be valid)
	 * 
	 * @param deadline
	 *            the team forming session deadline (Pre-condition: The courseID and
	 *            deadline pair must be valid)
	 */
	public void deleteTeamFormingSession(String courseID, Date deadline) {
		//System.out.println(deadline +" and "+courseID);
		TeamFormingSession teamFormingSession = getTeamFormingSession(courseID, deadline);
		
		if(teamFormingSession ==  null)
			System.out.println("No session found!");
		
		try {
			getPM().deletePersistent(teamFormingSession);
		} finally {
		}

		//pending
		// Delete submission entries
//		List<Submission> submissionList = getSubmissionList(courseID, deadline);
//		getPM().deletePersistentAll(submissionList);
	}
	
	/**
	 * Deletes all Team Forming Session objects from a Course.
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: Must be valid)
	 * 
	 */
	public void deleteTeamFormingSession(String courseID) {
		List<TeamFormingSession> teamFormingSessionList = getTeamFormingSessionList(courseID);
		//pending
		//List<Submission> submissionList = getSubmissionList(courseID);

		try {
			getPM().deletePersistentAll(teamFormingSessionList);
			deleteTeamProfiles(courseID);
			//pending
			//getPM().deletePersistentAll(submissionList);
		} finally {
		}
	}
	
	/**
	 * Deletes all TeamProfile objects from a Course.
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: Must be valid)
	 * 
	 */
	public void deleteTeamProfiles(String courseID){
		List<TeamProfile> teamProfileList = getTeamProfiles(courseID);
		//pending
		//List<Submission> submissionList = getSubmissionList(courseID);

		try {
			getPM().deletePersistentAll(teamProfileList);
			//pending
			//getPM().deletePersistentAll(submissionList);
		} finally {
		}		
	}
	
	/**
	 * Returns the Team Forming Session objects belonging to a Course.
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: Must not be null)
	 * 
	 * @return the list of evaluations belonging to the specified course
	 */
	public List<TeamFormingSession> getTeamFormingSessionList(String courseID) {
		String query = "select from " + TeamFormingSession.class.getName()
				+ " where courseID == '" + courseID + "'";

		@SuppressWarnings("unchecked")
		List<TeamFormingSession> teamFormingSessionList = (List<TeamFormingSession>) getPM().newQuery(
				query).execute();
		return teamFormingSessionList;
	}
	
	/**
	 * Adds to TaskQueue emails to remind students of an Evaluation.
	 * 
	 * @param studentList
	 *            the list of students to remind (Pre-condition: Must be valid)
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: Must not be null)
	 * 
	 * @param deadline
	 *            the evaluation deadline (Pre-condition: Must not be null)
	 */
	public void remindStudents(List<Student> studentList, String courseID, Date deadline) {
		Queue queue = QueueFactory.getQueue("email-queue");
		List<TaskOptions> taskOptionsList = new ArrayList<TaskOptions>();

		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HHmm");

		for (Student s : studentList) {
			// There is a limit of 100 tasks per batch addition to Queue in
			// Google App Engine
			if (taskOptionsList.size() == 100) {
				queue.add(taskOptionsList);
				taskOptionsList = new ArrayList<TaskOptions>();
			}

			taskOptionsList.add(TaskOptions.Builder.withUrl("/email")
					.param("operation", "remindstudents")
					.param("email", s.getEmail()).param("name", s.getName())
					.param("courseid", courseID)
					.param("deadline", df.format(deadline)));
		}

		if (!taskOptionsList.isEmpty()) {
			queue.add(taskOptionsList);
		}
	}
	
	/**
	 * Edits an Team Forming Session object with the new values and returns true if there
	 * are changes, false otherwise.
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: The courseID and deadline
	 *            pair must be valid)
	 * 
	 * @param newInstructions
	 *            new instructions for the evaluation (Pre-condition: Must not
	 *            be null)
	 * 
	 * @param newStart
	 *            new start date for the evaluation (Pre-condition: Must not be
	 *            null)
	 * 
	 * @param newDeadline
	 *            new deadline for the evaluation (Pre-condition: Must not be
	 *            null)
	 * 
	 * @param newGracePeriod
	 *            new grace period for the evaluation (Pre-condition: Must not
	 *            be null)
	 * 
	 * @return <code>true</code> if there are changes, <code>false</code>
	 *         otherwise
	 */
	public boolean editTeamFormingSession(String courseID, Date newStart,
			Date newDeadline, int newGracePeriod, String newInstructions, String newProfileTemplate) {
		TeamFormingSession teamFormingSession = getTeamFormingSession(courseID, null);
		Transaction tx = getPM().currentTransaction();
		try {
			tx.begin();

			teamFormingSession.setInstructions(newInstructions);
			teamFormingSession.setStart(newStart);
			teamFormingSession.setDeadline(newDeadline);
			teamFormingSession.setGracePeriod(newGracePeriod);
			teamFormingSession.setProfileTemplate(newProfileTemplate);

			getPM().flush();

			tx.commit();
		} finally {
			if (tx.isActive()) {
				tx.rollback();
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Adds to TaskQueue emails to inform students of changes to an Team Forming Session
	 * object.
	 * 
	 * @param studentList
	 *            the list of students to inform (Pre-condition: The parameters
	 *            must be valid)
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: The parameters must be valid)
	 */
	public void informStudentsOfChanges(List<Student> studentList,
			String courseID, Date endTime) {
		Queue queue = QueueFactory.getQueue("email-queue");
		List<TaskOptions> taskOptionsList = new ArrayList<TaskOptions>();

		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HHmm");

		TeamFormingSession teamFormingSession = getTeamFormingSession(courseID, endTime);

		Date start = teamFormingSession.getStart();
		Date deadline = teamFormingSession.getDeadline();
		String instructions = teamFormingSession.getInstructions();
		String profileTemplate = teamFormingSession.getProfileTemplate();

		for (Student s : studentList) {
			// There is a limit of 100 tasks per batch addition to Queue in
			// Google App Engine
			if (taskOptionsList.size() == 100) {
				queue.add(taskOptionsList);
				taskOptionsList = new ArrayList<TaskOptions>();
			}

			taskOptionsList.add(TaskOptions.Builder.withUrl("/email")
					.param("operation", "informstudentsofevaluationchanges")
					.param("email", s.getEmail()).param("name", s.getName())
					.param("courseid", courseID)
					.param("start", df.format(start))
					.param("deadline", df.format(deadline))
					.param("instr", instructions)
					.param("profileTemplate", profileTemplate));
		}

		if (!taskOptionsList.isEmpty()) {
			queue.add(taskOptionsList);
		}
	}
}
