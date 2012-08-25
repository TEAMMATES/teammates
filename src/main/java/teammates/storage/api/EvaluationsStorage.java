package teammates.storage.api;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import teammates.common.Common;
import teammates.common.datatransfer.EvaluationData;
import teammates.common.datatransfer.StudentData;
import teammates.common.datatransfer.SubmissionData;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.datastore.Datastore;
import teammates.storage.entity.Course;
import teammates.storage.entity.Evaluation;
import teammates.storage.entity.Student;
import teammates.storage.entity.Submission;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public class EvaluationsStorage {
	private static EvaluationsStorage instance = null;
	private static final Logger log = Logger.getLogger(EvaluationsStorage.class
			.getName());

	private static final AccountsDb accountsDb = new AccountsDb();
	
	/**
	 * Constructs an Accounts object. Obtains an instance of PersistenceManager
	 * class to handle datastore transactions.
	 */
	private EvaluationsStorage() {
	}

	private PersistenceManager getPM() {
		return Datastore.getPersistenceManager();
	}

	public static EvaluationsStorage inst() {
		if (instance == null)
			instance = new EvaluationsStorage();
		return instance;
	}

	public List<Evaluation> getReadyEvaluations() {
		// TODO: very inefficient to go through all evaluations
		List<Evaluation> evaluationList = getAllEvaluations();
		List<Evaluation> readyEvaluations = new ArrayList<Evaluation>();

		for (Evaluation e : evaluationList) {
			if (e.isReady()) {
				readyEvaluations.add(e);
			}
		}
		return readyEvaluations;
	}

	/**
	 * Adds an evaluation to the specified course.
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
	 * @throws InvalidParametersException
	 */

	public void addEvaluation(String courseID, String name,
			String instructions, boolean commentsEnabled, Date start,
			Date deadline, double timeZone, int gracePeriod)
			throws EntityAlreadyExistsException, InvalidParametersException {
		if (getEvaluation(courseID, name) != null) {
			throw new EntityAlreadyExistsException("An evaluation by the name "
					+ name + " already exists under course " + courseID);
		}

		Evaluation evaluation = new Evaluation(courseID, name, instructions,
				commentsEnabled, start, deadline, timeZone, gracePeriod);

		try {
			getPM().makePersistent(evaluation);
			getPM().flush();
		} finally {
		}

		// Build submission objects for each student based on their team number
		createSubmissions(courseID, name);
	}

	/**
	 * Add evaluation object
	 * 
	 * @param e
	 *            An evaluation object
	 * 
	 * @author wangsha
	 * @throws EntityAlreadyExistsException
	 */
	public void addEvaluation(Evaluation e) throws EntityAlreadyExistsException {
		String courseID = e.getCourseID();
		String evaluationName = e.getName();
		if (getEvaluation(courseID, evaluationName) != null) {
			throw new EntityAlreadyExistsException("The course " + courseID
					+ " already has an evaluation by this name: "
					+ evaluationName);
		}
		try {
			getPM().makePersistent(e);
			getPM().flush();

			// Build submission objects for each student based on their team
			// number
			createSubmissions(courseID, evaluationName);
		} catch (Exception exp) {
			exp.printStackTrace();
		}

		int elapsedTime = 0;
		Evaluation created = getEvaluation(courseID, evaluationName);
		while ((created == null) && (elapsedTime < Common.PERSISTENCE_CHECK_DURATION)) {
			Common.waitBriefly();
			created = getEvaluation(courseID, evaluationName);
			elapsedTime += Common.WAIT_DURATION;
		}
		if (elapsedTime == Common.PERSISTENCE_CHECK_DURATION) {
			log.severe("Operation did not persist in time: addEvaluation->"
					+ courseID + "/" + evaluationName);
		}
	}

	/**
	 * Calculates the bump ratio of points for a specific student.
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: courseID, evaluationName and
	 *            fromStudent must be valid)
	 * 
	 * @param evaluationName
	 *            the evaluation name (Pre-condition: courseID, evaluationName
	 *            and fromStudent must be valid)
	 * 
	 * @param fromStudent
	 *            the email of the student giving the feedback (Pre-condition:
	 *            courseID, evaluationName and fromStudent must be valid)
	 * 
	 * @return the ratio to increase/decrease points given by the student
	 */
	public float calculatePointsBumpRatio(String courseID,
			String evaluationName, String fromStudent,
			List<Submission> submissionList) {

		int totalPoints = 0;
		int numberOfStudents = 0;

		for (Submission s : submissionList) {
			// Exclude unsure and unfilled entries
			if (s.getPoints() == -999) {
				return 1;

			} else if (s.getPoints() != -101) {

				totalPoints = totalPoints + s.getPoints();
				numberOfStudents++;
			}
		}
		// special case all the students who submit the evaluation give 0 to
		// everyone
		if (totalPoints == 0) {
			for (Submission s : submissionList) {
				if (s.getPoints() != -101) {
					s.setPoints(100);
					log.fine("MSG:" + s.getFromStudent() + "|"
							+ s.getToStudent() + "|" + s.getPoints());
				}

			}
			return 1;
		}
		return (float) ((numberOfStudents * 100.0) / (float) totalPoints);
	}

	/**
	 * Creates Submission objects for a particular Evaluation.
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: The courseID and evaluationName
	 *            pair must be valid)
	 * 
	 * @param evaluationName
	 *            the evaluation name (Pre-condition: The courseID and
	 *            evaluationName pair must be valid)
	 */
	public void createSubmissions(String courseID, String evaluationName) {
		List<Submission> submissionList = createSubmissionsForEval(courseID,
				evaluationName);

		try {
			getPM().makePersistentAll(submissionList);
			getPM().flush();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		//check for persistence
		int elapsedTime = 0;
		if (submissionList.size() == 0) {
			return;
		}
		Submission lastSubmission = submissionList
				.get(submissionList.size() - 1);
		Submission created = getSubmission(courseID, evaluationName,
				lastSubmission.getFromStudent(), lastSubmission.getToStudent());
		while ((created == null) && (elapsedTime < Common.PERSISTENCE_CHECK_DURATION)) {
			Common.waitBriefly();
			created = getSubmission(courseID, evaluationName,
					lastSubmission.getFromStudent(), lastSubmission.getToStudent());
			elapsedTime += Common.WAIT_DURATION;
		}
		if (elapsedTime == Common.PERSISTENCE_CHECK_DURATION) {
			log.severe("Operation did not persist in time: createSubmissions->"
					+ courseID + "/" + evaluationName);
		}

	}

	private List<Submission> createSubmissionsForEval(String courseID,
			String evaluationName) {
		
		List<StudentData> studentDataList = accountsDb.getStudentListForCourse(courseID);

		List<Submission> submissionList = new ArrayList<Submission>();
		Submission submission = null;

		for (StudentData sx : studentDataList) {
			for (StudentData sy : studentDataList) {
				if (sx.team.equals(sy.team)) {
					submission = new Submission(sx.email, sy.email,
							courseID, evaluationName, sx.team);
					submissionList.add(submission);
				}

			}

		}
		return submissionList;
	}
	
	/**
	 * Adjusts submissions for a student moving from one team to another. 
	 * Deletes existing submissions for original team and creates empty 
	 * submissions for the new team, in all existing submissions,
	 * including CLOSED and PUBLISHED ones.
	 */
	public void adjustSubmissionsForChangingTeam(String courseId, String studentEmail, String originalTeam, String newTeam){
		List<Evaluation> evaluationList = getEvaluationList(courseId);
		for (Evaluation e : evaluationList) {
			
			deleteSubmissionsForOutgoingMember(courseId, e.getName(),
						studentEmail, originalTeam);
			
			addSubmissionsForIncomingMember(courseId, e.getName(), studentEmail,
					newTeam);
		}
	}
	
	/**
	 * Adjusts submissions for a student adding a new student to a course. 
	 * Creates empty submissions for the new team, in all existing submissions,
	 * including CLOSED and PUBLISHED ones.
	 */
	public void adjustSubmissionsForNewStudent(String courseId, String studentEmail, String team){
		List<Evaluation> evaluationList = getEvaluationList(courseId);
		for (Evaluation e : evaluationList) {
			addSubmissionsForIncomingMember(courseId, e.getName(), studentEmail, team);
		}
	}


	private void addSubmissionsForIncomingMember(String courseId,
			String evaluationName, String studentEmail, String newTeam) {
		
		List<String> students = getExistingStudentsInTeam(courseId, newTeam);
		
		//add self evaluation are remove self from list
		Submission selfSubmission = new Submission(studentEmail, studentEmail,
				courseId, evaluationName, newTeam);
		addSubmission(selfSubmission);
		students.remove(studentEmail);
		
		//add submission to/from peers
		for (String peer : students) {
			
			Submission incomingSubmission = new Submission(peer, studentEmail,
					courseId, evaluationName, newTeam);
			addSubmission(incomingSubmission);
			
			Submission outgoingSubmission = new Submission(studentEmail, peer,
					courseId, evaluationName, newTeam);
			addSubmission(outgoingSubmission);
		}
	}

	private void addSubmission(Submission submission) {
		getPM().makePersistent(submission);
		getPM().flush();
		log.warning("Adding new submission: "+submission.toString());
	}

	private List<String> getExistingStudentsInTeam(String courseId, String team) {
		Set<String> students = new HashSet<String>();
		List<Submission> submissions = getSubmissionList(courseId);
		for(Submission s: submissions){
			if(s.getTeamName().equals(team)){
			students.add(s.getFromStudent());
			}
		}
		return new ArrayList<String>(students);
	}

	private void deleteSubmissionsForOutgoingMember(String courseId,
			String evaluationName, String studentEmail, String originalTeam) {

		List<Submission> submissions = getSubmissionFromStudentList(courseId,
				evaluationName, studentEmail);
		deleteSubmissionsIfSameTeam(submissions, originalTeam);

		submissions = getSubmissionToStudentList(courseId, evaluationName,
				studentEmail);
		deleteSubmissionsIfSameTeam(submissions, originalTeam);

	}

	private void deleteSubmissionsIfSameTeam(List<Submission> submissions,
			String originalTeam) {
		for (Submission s : submissions) {
			if (s.getTeamName().equals(originalTeam)) {
				log.warning("Deleting outdated submission: "+s.toString());
				getPM().deletePersistent(s);
			} else {
				log.severe("Unexpected submission found when deleting outgoing submissions for "
						+ s.toString());
			}
		}
		getPM().flush();
	}


	/**
	 * Deletes an Evaluation and its Submission objects.
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: The courseID and evaluationName
	 *            pair must be valid)
	 * 
	 * @param name
	 *            the evaluation name (Pre-condition: The courseID and
	 *            evaluationName pair must be valid)
	 * @throws EntityDoesNotExistException
	 */
	public void deleteEvaluation(String courseID, String name) {
		Evaluation evaluation = getEvaluation(courseID, name);
		if (evaluation == null) {
			String errorMessage = "Trying to delete non-existent evaluation : "
					+ courseID + "/" + name;
			log.warning(errorMessage);
		} else {
			getPM().deletePersistent(evaluation);
			// Delete submission entries
			List<Submission> submissionList = getSubmissionList(courseID, name);
			getPM().deletePersistentAll(submissionList);
			getPM().flush();
		}

		int elapsedTime = 0;
		Evaluation created = getEvaluation(courseID, name);
		while ((created != null)
				&& (elapsedTime < Common.PERSISTENCE_CHECK_DURATION)) {
			Common.waitBriefly();
			created = getEvaluation(courseID, name);
			elapsedTime += Common.WAIT_DURATION;
		}
		if (elapsedTime == Common.PERSISTENCE_CHECK_DURATION) {
			log.severe("Operation did not persist in time: addEvaluation->"
					+ courseID + "/" + name);
		}

	}

	/**
	 * Deletes all Evaluation objects and its Submission objects from a Course.
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: Must be valid)
	 * 
	 */
	public void deleteEvaluations(String courseID) {
		List<Evaluation> evaluationList = getEvaluationList(courseID);
		List<Submission> submissionList = getSubmissionList(courseID);
		getPM().deletePersistentAll(evaluationList);
		getPM().deletePersistentAll(submissionList);
		getPM().flush();
	}

	/**
	 * Edits an Evaluation object with the new values and returns true if there
	 * are changes, false otherwise.
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: The courseID and evaluationName
	 *            pair must be valid)
	 * 
	 * @param name
	 *            the evaluation name (Pre-condition: The courseID and
	 *            evaluationName pair must be valid)
	 * 
	 * @param newInstructions
	 *            new instructions for the evaluation (Pre-condition: Must not
	 *            be null)
	 * 
	 * @param newCommentsEnabled
	 *            new status for comments (Pre-condition: Must not be null)
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
	public boolean editEvaluation(String courseID, String name,
			String newInstructions, boolean newCommentsEnabled, Date newStart,
			Date newDeadline, int newGracePeriod) {
		Evaluation evaluation = getEvaluation(courseID, name);
		Transaction tx = getPM().currentTransaction();
		try {
			tx.begin();

			evaluation.setInstructions(newInstructions);
			evaluation.setStart(newStart);
			evaluation.setDeadline(newDeadline);
			evaluation.setGracePeriod(newGracePeriod);
			evaluation.setCommentsEnabled(newCommentsEnabled);

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

	public boolean editEvaluation(String courseID, String name,
			String newInstructions, boolean newCommentsEnabled, Date newStart,
			Date newDeadline, int newGracePeriod, boolean newIsActive,
			boolean newIsPublished, double newTimeZone)
			throws EntityDoesNotExistException {
		Evaluation evaluation = getEvaluation(courseID, name);

		Transaction tx = getPM().currentTransaction();
		try {
			tx.begin();

			evaluation.setInstructions(newInstructions);
			evaluation.setStart(newStart);
			evaluation.setDeadline(newDeadline);
			evaluation.setGracePeriod(newGracePeriod);
			evaluation.setCommentsEnabled(newCommentsEnabled);
			evaluation.setActivated(newIsActive);
			evaluation.setPublished(newIsPublished);
			evaluation.setTimeZone(newTimeZone);

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
	 * Closes an Evaluation. (For testing purpose only)
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: The courseID and evaluationName
	 *            pair must be valid)
	 * 
	 * @param name
	 *            the evaluation name (Pre-condition: The courseID and
	 *            evaluationName pair must be valid)
	 */
	public boolean closeEvaluation(String courseID, String name) {
		Evaluation evaluation = getEvaluation(courseID, name);

		int oneday = 24 * 60 * 60 * 1000;
		Date start = new Date(System.currentTimeMillis() - 2 * oneday);
		Date end = new Date(System.currentTimeMillis() - oneday);

		evaluation.setStart(start);
		evaluation.setDeadline(end);
		// evaluation.setActivated(false);

		return true;
	}

	/**
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: The courseID and evaluationName
	 *            pair must be valid)
	 * 
	 * @param name
	 *            the evaluation name (Pre-condition: The courseID and
	 *            evaluationName pair must be valid)
	 */
	public boolean openEvaluation(String courseID, String name) {

		Transaction trans = getPM().currentTransaction();
		try {
			trans.begin();

			Evaluation evaluation = getEvaluation(courseID, name);

			if (evaluation == null)
				return false;

			long oneday = 24 * 60 * 60 * 1000;
			Date start = new Date(System.currentTimeMillis() - oneday);
			Date end = new Date(System.currentTimeMillis() + oneday);
			evaluation.setStart(start);
			evaluation.setDeadline(end);
			evaluation.setActivated(true);

			getPM().flush();
			trans.commit();

		} finally {
			if (trans.isActive()) {
				trans.rollback();
				return false;
			}
		}
		return true;
	}

	/**
	 * Await an Evaluation. (for testing purpose only) Edits the Submission
	 * objects that pertain to a specified Student and his email.
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: The courseID and evaluationName
	 *            pair must be valid)
	 * @param name
	 *            the evaluation name (Pre-condition: The courseID and
	 *            evaluationName pair must be valid)
	 */
	public boolean awaitEvaluation(String courseID, String name) {

		Transaction trans = getPM().currentTransaction();
		try {

			trans.begin();

			Evaluation evaluation = getEvaluation(courseID, name);

			int oneday = 24 * 60 * 60 * 1000;
			Date start = new Date(System.currentTimeMillis() + oneday);
			Date end = new Date(System.currentTimeMillis() + oneday * 2);
			evaluation.setActivated(false);
			evaluation.setStart(start);
			evaluation.setDeadline(end);

			getPM().flush();
			trans.commit();

		} finally {
			if (trans.isActive()) {
				trans.rollback();
				return false;
			}
		}
		log.fine("awaiting submission pass");
		return true;
	}

	/**
	 * Edits the Submission objects that pertain to a specified Student and his
	 * email.
	 * 
	 * @param email
	 *            the email of the student (Pre-condition: The courseID and
	 *            email pair must be valid)
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: The courseID and email pair must
	 *            be valid)
	 * 
	 * @param newEmail
	 *            the new email of the student (Pre-condition: Must not be null)
	 */
	public void editSubmissions(String courseID, String email, String newEmail) {

		List<Submission> submissionList = getSubmissionList(courseID);

		for (Submission s : submissionList) {
			if (s.getFromStudent().equals(email)) {
				s.setFromStudent(newEmail);
			}
			if (s.getToStudent().equals(email)) {
				s.setToStudent(newEmail);
			}

		}
		getPM().close();
	}

	/**
	 * Edits a list of Submission objects.
	 * 
	 * @param submissionList
	 *            the list of submissions to be edited (Pre-condition: The
	 *            submission list must be valid)
	 */
	public void editSubmissions(List<Submission> submissionList) {
		Submission submission = null;

		for (Submission s : submissionList) {
			submission = getSubmission(s.getCourseID(), s.getEvaluationName(),
					s.getTeamName(), s.getToStudent(), s.getFromStudent());

			submission.setPoints(s.getPoints());
			submission.setJustification(s.getJustification());
			submission.setCommentsToStudent(s.getCommentsToStudent());
		}
		// closing PM because otherwise the data is not updated during offline
		// unit testing
		getPM().close();

	}

	/**
	 * Returns all Evaluation objects.
	 * 
	 * @return the list of all evaluations
	 */
	public List<Evaluation> getAllEvaluations() {
		String query = "select from " + Evaluation.class.getName();

		@SuppressWarnings("unchecked")
		List<Evaluation> evaluationList = (List<Evaluation>) getPM().newQuery(
				query).execute();

		return evaluationList;
	}

	/**
	 * Returns an Evaluation object.
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: Must not be null)
	 * 
	 * @param name
	 *            the evaluation name (Pre-condition: Must not be null)
	 * 
	 * @return the evaluation of the specified course and name
	 */
	public Evaluation getEvaluation(String courseID, String name) {
		String query = "select from " + Evaluation.class.getName()
				+ " where name == '" + name + "' && courseID == '" + courseID
				+ "'";
		log.fine(query);
		@SuppressWarnings("unchecked")
		List<Evaluation> evaluationList = (List<Evaluation>) getPM().newQuery(
				query).execute();

		if (evaluationList.isEmpty()) {
			log.fine("Trying to get non-existent Evaluation : " + courseID
					+ "/" + name);
			return null;
		}

		return evaluationList.get(0);
	}

	/**
	 * Returns all Evaluation objects that are due in the specified number of
	 * hours.
	 * 
	 * @param hours
	 *            the number of hours in which the evaluations are due
	 * 
	 * @return the list of all existing evaluations
	 */
	public List<Evaluation> getEvaluationsClosingWithinTimeLimit(int hours) {
		String query = "select from " + Evaluation.class.getName();

		@SuppressWarnings("unchecked")
		List<Evaluation> evaluationList = (List<Evaluation>) getPM().newQuery(
				query).execute();
		Calendar now = Calendar.getInstance();
		Calendar start = Calendar.getInstance();
		Calendar deadline = Calendar.getInstance();

		long nowMillis;
		long deadlineMillis;

		long differenceBetweenDeadlineAndNow;

		List<Evaluation> dueEvaluationList = new ArrayList<Evaluation>();

		for (Evaluation e : evaluationList) {
			// Fix the time zone accordingly
			now.add(Calendar.MILLISECOND,
					(int) (60 * 60 * 1000 * e.getTimeZone()));
			start.setTime(e.getStart());
			deadline.setTime(e.getDeadline());

			nowMillis = now.getTimeInMillis();
			deadlineMillis = deadline.getTimeInMillis();

			differenceBetweenDeadlineAndNow = (deadlineMillis - nowMillis)
					/ (60 * 60 * 1000);

			// If now and start are almost similar, it means the evaluation is
			// open
			// for only 24 hours
			// hence we do not send a reminder e-mail for the evaluation
			if (now.after(start)
					&& (differenceBetweenDeadlineAndNow >= hours - 1 && differenceBetweenDeadlineAndNow < hours))

			{
				dueEvaluationList.add(e);
			}

			now.add(Calendar.MILLISECOND,
					(int) (-60 * 60 * 1000 * e.getTimeZone()));
		}

		return dueEvaluationList;
	}

	/**
	 * Returns the Evaluation objects belonging to some Course objects.
	 * 
	 * @param courseList
	 *            a list of courses (Pre-condition: Must not be null)
	 * 
	 * @return the list of evaluations belonging to the list of courses
	 */
	public List<Evaluation> getEvaluationList(List<Course> courseList) {
		List<Evaluation> evaluationList = new ArrayList<Evaluation>();

		for (Course c : courseList) {
			String query = "select from " + Evaluation.class.getName()
					+ " where courseID == '" + c.getID() + "'";

			@SuppressWarnings("unchecked")
			List<Evaluation> tempEvaluationList = (List<Evaluation>) getPM()
					.newQuery(query).execute();
			evaluationList.addAll(tempEvaluationList);
		}

		return evaluationList;
	}

	/**
	 * Returns the Evaluation objects belonging to a Course.
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: Must not be null)
	 * 
	 * @return the list of evaluations belonging to the specified course
	 */
	public List<Evaluation> getEvaluationList(String courseID) {
		String query = "select from " + Evaluation.class.getName()
				+ " where courseID == '" + courseID + "'";

		@SuppressWarnings("unchecked")
		List<Evaluation> evaluationList = (List<Evaluation>) getPM().newQuery(
				query).execute();
		return evaluationList;
	}


	/**
	 * Returns a Submission object.
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: The parameters must be valid)
	 * 
	 * @param evaluationName
	 *            the evaluation name (Pre-condition: The parameters must be
	 *            valid)
	 * 
	 * @param teamName
	 *            the team name (Pre-condition: The parameters must be valid)
	 * 
	 * @param toStudent
	 *            the email of the target student (Pre-condition: The parameters
	 *            must be valid)
	 * 
	 * @param fromStudent
	 *            the email of the sending student (Pre-condition: The
	 *            parameters must be valid)
	 * 
	 * @return the submission entry of the specified fromStudent to the
	 *         specified toStudent
	 */
	private Submission getSubmission(String courseID, String evaluationName,
			String teamName, String toStudent, String fromStudent) {

		String query = "select from " + Submission.class.getName()
				+ " where courseID == '" + courseID + "'"
				+ "&& evaluationName == '" + evaluationName
				+ "' && teamName == '" + teamName + "'" + "&& fromStudent == '"
				+ fromStudent + "' && toStudent == '" + toStudent + "'";

		@SuppressWarnings("unchecked")
		List<Submission> submissionList = (List<Submission>) getPM().newQuery(
				query).execute();

		// TODO: need to handle entity-not-found
		return submissionList.size() == 0 ? null : submissionList.get(0);
	}

	/**
	 * Returns the Submission objects of an Evaluation from a specific Student.
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: The parameters must be valid)
	 * 
	 * @param evaluationName
	 *            the evaluation name (Pre-condition: The parameters must be
	 *            valid)
	 * 
	 * @param reviewerEmail
	 *            the email of the sending student (Pre-condition: The
	 *            parameters must be valid)
	 * 
	 * @return the submissions of the specified student pertaining to the
	 *         specified evaluation
	 */
	public List<Submission> getSubmissionFromStudentList(String courseID,
			String evaluationName, String reviewerEmail) {

		String query = "select from " + Submission.class.getName()
				+ " where courseID == '" + courseID
				+ "' && evaluationName == '" + evaluationName
				+ "' && fromStudent == '" + reviewerEmail + "'";

		log.info(query);
		@SuppressWarnings("unchecked")
		List<Submission> submissionList = (List<Submission>) getPM().newQuery(
				query).execute();
		return submissionList;
	}

	public Submission getSubmission(String courseId, String evaluationName,
			String reviewerEmail, String revieweeEmail) {
		List<Submission> allSubmissionsFromReviewer = getSubmissionFromStudentList(
				courseId, evaluationName, reviewerEmail);
		Submission target = null;
		for (Submission submission : allSubmissionsFromReviewer) {
			if (submission.getToStudent().equals(revieweeEmail)) {
				target = submission;
				break;
			}
		}
		if (target == null) {
			log.fine("Trying to get non-existent Submission : " + courseId
					+ "/" + evaluationName + "/" + reviewerEmail + "/"
					+ revieweeEmail);
		}
		return target;
	}

	/**
	 * Returns the Submission objects of a Course.
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: Must be valid)
	 * 
	 * @return the submissions pertaining to the specified course
	 */
	public List<Submission> getSubmissionList(String courseID) {
		String query = "select from " + Submission.class.getName()
				+ " where courseID == '" + courseID + "'";

		@SuppressWarnings("unchecked")
		List<Submission> submissionList = (List<Submission>) getPM().newQuery(
				query).execute();

		return submissionList;
	}
	
	public List<SubmissionData> getSubmissionsForCourse(String courseID) {
		
		List<Submission> submissionList = getSubmissionList(courseID);
		List<SubmissionData> submissionDataList = new ArrayList<SubmissionData>();
		for (Submission s : submissionList) {
			submissionDataList.add(new SubmissionData(s));
		}
		return submissionDataList;
	}

	/**
	 * Returns the Submission objects of an Evaluation.
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: The courseID and evaluationName
	 *            pair must be valid)
	 * 
	 * @param evaluationName
	 *            the evaluation name (Pre-condition: The courseID and
	 *            evaluationName pair must be valid)
	 * 
	 * @return the submissions pertaining to an evaluation
	 */
	public List<Submission> getSubmissionList(String courseID,
			String evaluationName) {
		String sQuery = "select from " + Submission.class.getName()
				+ " where courseID == '" + courseID
				+ "' && evaluationName == '" + evaluationName + "'";

		@SuppressWarnings("unchecked")
		List<Submission> submissionList = (List<Submission>) getPM().newQuery(
				sQuery).execute();
		return submissionList;
	}

	/**
	 * Returns the Submission objects of an Evaluation to a specific Student.
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: The parameters must be valid)
	 * 
	 * @param evaluationName
	 *            the evaluation name (Pre-condition: The parameters must be
	 *            valid)
	 * 
	 * @param toStudent
	 *            the email of the target student (Pre-condition: The parameters
	 *            must be valid)
	 * 
	 * @return the submissions to the target student
	 */
	public List<Submission> getSubmissionToStudentList(String courseID,
			String evaluationName, String toStudent) {
		String query = "select from " + Submission.class.getName()
				+ " where courseID == '" + courseID
				+ "' && evaluationName == '" + evaluationName
				+ "' && toStudent == '" + toStudent + "'";

		@SuppressWarnings("unchecked")
		List<Submission> submissionList = (List<Submission>) getPM().newQuery(
				query).execute();

		return submissionList;
	}

	/**
	 * Adds to TaskQueue emails to inform students of changes to an Evaluation
	 * object.
	 * 
	 * @param studentList
	 *            the list of students to inform (Pre-condition: The parameters
	 *            must be valid)
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: The parameters must be valid)
	 * 
	 * @param evaluationName
	 *            the evaluation name (Pre-condition: The parameters must be
	 *            valid)
	 */
	public void informStudentsOfChanges(List<Student> studentList,
			String courseID, String evaluationName) {
		Queue queue = QueueFactory.getQueue("email-queue");
		List<TaskOptions> taskOptionsList = new ArrayList<TaskOptions>();

		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HHmm");

		Evaluation evaluation = getEvaluation(courseID, evaluationName);

		Date start = evaluation.getStart();
		Date deadline = evaluation.getDeadline();
		String instructions = evaluation.getInstructions();

		for (Student s : studentList) {
			// There is a limit of 100 tasks per batch addition to Queue in
			// Google App
			// Engine
			if (taskOptionsList.size() == 100) {
				queue.add(taskOptionsList);
				taskOptionsList = new ArrayList<TaskOptions>();
			}

			taskOptionsList.add(TaskOptions.Builder.withUrl("/email")
					.param("operation", "informstudentsofevaluationchanges")
					.param("email", s.getEmail()).param("name", s.getName())
					.param("courseid", courseID)
					.param("evaluationname", evaluationName)
					.param("start", df.format(start))
					.param("deadline", df.format(deadline))
					.param("instr", instructions));
		}

		if (!taskOptionsList.isEmpty()) {
			queue.add(taskOptionsList);
		}
	}

	/**
	 * Adds to TaskQueue emails to inform students of an Evaluation.
	 * 
	 * @param studentList
	 *            the list of students to be informed
	 * @param courseID
	 *            the course ID
	 * @param evaluationName
	 *            the evaluation name
	 */
	public void informStudentsOfEvaluationOpening(List<Student> studentList,
			String courseID, String evaluationName) {
		Queue queue = QueueFactory.getQueue("email-queue");
		List<TaskOptions> taskOptionsList = new ArrayList<TaskOptions>();
		Evaluation evaluation = getEvaluation(courseID, evaluationName);
		Date deadline = evaluation.getDeadline();
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HHmm");
		for (Student s : studentList) {
			// There is a limit of 100 tasks per batch addition to Queue in
			// Google App Engine
			if (taskOptionsList.size() == 100) {
				queue.add(taskOptionsList);
				taskOptionsList = new ArrayList<TaskOptions>();
			}

			taskOptionsList.add(TaskOptions.Builder.withUrl("/email")
					.param("operation", "informstudentsofevaluationopening")
					.param("email", s.getEmail()).param("name", s.getName())
					.param("courseid", courseID)
					.param("evaluationname", evaluationName)
					.param("deadline", df.format(deadline)));
		}

		if (!taskOptionsList.isEmpty()) {
			queue.add(taskOptionsList);
		}
	}

	/**
	 * Returns if there is an ongoing Evaluation for a particular Course.
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: Must not be null)
	 * 
	 * @return <code>true</code> if there is an ongoing evaluation,
	 *         <code>false</code> otherwise.
	 */
	public boolean isEvaluationOngoing(String courseID) {
		List<Evaluation> evaluationList = getEvaluationList(courseID);

		Calendar now = Calendar.getInstance();
		Calendar start = Calendar.getInstance();
		Calendar deadline = Calendar.getInstance();

		// SGP timezone
		now.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY) + 8);

		for (Evaluation e : evaluationList) {
			start.setTime(e.getStart());
			deadline.setTime(e.getDeadline());

			if (now.after(start) && now.before(deadline)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Checks if a Student has done his submitted his entry for a particular
	 * Evaluation.
	 * 
	 * @param evaluation
	 *            the evaluation (Pre-condition: The evaluation and email pair
	 *            must be valid)
	 * 
	 * @param email
	 *            the email of the student (Pre-condition: The evaluation and
	 *            email pair must be valid)
	 * 
	 * @return <code>true</code> if the student has submitted the evaluation,
	 *         <code>false</code> otherwise.
	 */
	public boolean isEvaluationSubmitted(EvaluationData evaluation, String email) {
		List<Submission> submissionList = getSubmissionFromStudentList(
				evaluation.course, evaluation.name, email);
		for (Submission s : submissionList) {
			if (s.getPoints() == -999) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Publishes an Evaluation.
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: The courseID and name pair must
	 *            be valid)
	 * 
	 * @param name
	 *            the evaluation name (Pre-condition: The courseID and name pair
	 *            must be valid)
	 */
	public boolean publishEvaluation(String courseID, String name) {
		Evaluation evaluation = getEvaluation(courseID, name);

		evaluation.setPublished(true);
		getPM().close();
		return true;
	}

	/**
	 * Unpublish an Evaluation
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: The courseID and name pair must
	 *            be valid)
	 * 
	 * @param name
	 *            the evaluation name (Pre-condition: The courseID and name pair
	 *            must be valid)
	 */
	public boolean unpublishEvaluation(String courseID, String name) {
		Evaluation evaluation = getEvaluation(courseID, name);

		evaluation.setPublished(false);
		getPM().close();

		return true;
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
	 * @param evaluationName
	 *            the evaluation name (Pre-condition: Must not be null)
	 * 
	 * @param deadline
	 *            the evaluation deadline (Pre-condition: Must not be null)
	 */
	public void remindStudents(List<Student> studentList, String courseID,
			String evaluationName, Date deadline) {
		Queue queue = QueueFactory.getQueue("email-queue");
		List<TaskOptions> taskOptionsList = new ArrayList<TaskOptions>();

		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HHmm");

		for (Student s : studentList) {
			// There is a limit of 100 tasks per batch addition to Queue in
			// Google App
			// Engine
			if (taskOptionsList.size() == 100) {
				queue.add(taskOptionsList);
				taskOptionsList = new ArrayList<TaskOptions>();
			}

			taskOptionsList.add(TaskOptions.Builder.withUrl("/email")
					.param("operation", "remindstudents")
					.param("email", s.getEmail()).param("name", s.getName())
					.param("courseid", courseID)
					.param("evaluationname", evaluationName)
					.param("deadline", df.format(deadline)));
		}

		if (!taskOptionsList.isEmpty()) {
			queue.add(taskOptionsList);
		}
	}

	public void deleteSubmissionsForStudent(String courseId, String studentEmail) {
		String query1 = "select from " + Submission.class.getName()
				+ " where courseID == '" + courseId + "' && toStudent=='"
				+ studentEmail + "'";
		@SuppressWarnings("unchecked")
		List<Submission> submissionList1 = (List<Submission>) getPM().newQuery(
				query1).execute();
		getPM().deletePersistentAll(submissionList1);

		String query2 = "select from " + Submission.class.getName()
				+ " where courseID == '" + courseId + "' && fromStudent=='"
				+ studentEmail + "'";
		@SuppressWarnings("unchecked")
		List<Submission> submissionList2 = (List<Submission>) getPM().newQuery(
				query2).execute();
		getPM().deletePersistentAll(submissionList2);
		getPM().flush();

	}

	public ArrayList<EvaluationData> getEvaluationsSummaryForCourse(
			String courseId) {

		ArrayList<EvaluationData> evaluationsSummaryList = new ArrayList<EvaluationData>();
		List<Evaluation> evaluationList = getEvaluationList(courseId);

		for (Evaluation e : evaluationList) {

			EvaluationData ed = new EvaluationData();

			ed.course = e.getCourseID();
			ed.name = e.getName();
			ed.instructions = e.getInstructions();
			ed.p2pEnabled = e.isCommentsEnabled();
			ed.startTime = e.getStart();
			ed.endTime = e.getDeadline();
			ed.timeZone = e.getTimeZone();
			ed.gracePeriod = e.getGracePeriod();
			ed.published = e.isPublished();
			ed.activated = e.isActivated();
			evaluationsSummaryList.add(ed);
		}

		return evaluationsSummaryList;
	}

	public void verifyEvaluationExists(String courseId, String evaluationName)
			throws EntityDoesNotExistException {
		if (getEvaluation(courseId, evaluationName) == null) {
			throw new EntityDoesNotExistException("The evaluation "
					+ evaluationName + " does not exist in course " + courseId);
		}
	}

}
