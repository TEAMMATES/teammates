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

import teammates.exception.EvaluationExistsException;
import teammates.jdo.Coordinator;
import teammates.jdo.Course;
import teammates.jdo.Evaluation;
import teammates.jdo.Student;
import teammates.jdo.Submission;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

public class Evaluations {
	private static Evaluations instance = null;
	private static final Logger log = Logger.getLogger(Evaluations.class
			.getName());

	/**
	 * Constructs an Accounts object. Obtains an instance of PersistenceManager
	 * class to handle datastore transactions.
	 */
	private Evaluations() {
	}

	private PersistenceManager getPM() {
		return Datastore.getPersistenceManager();
	}

	public static Evaluations inst() {
		if (instance == null)
			instance = new Evaluations();
		return instance;
	}

	/**
	 * Checks every evaluation and activate the evaluation if the current time
	 * is later than start time.
	 * 
	 * @return list of evaluations that were activated in the function call
	 */
	public List<Evaluation> activateEvaluations() {
		List<Evaluation> evaluationList = getAllEvaluations();
		List<Evaluation> activatedEvaluationList = new ArrayList<Evaluation>();

		Calendar c1 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		Calendar c2 = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

		for (Evaluation e : evaluationList) {
			// Fix the time zone accordingly
			c1.add(Calendar.MILLISECOND,
					(int) (60 * 60 * 1000 * e.getTimeZone()));

			c2.setTime(e.getStart());

			if (c1.after(c2) || c1.equals(c2)) {
				if (e.isActivated() == false) {
					e.setActivated(true);
					activatedEvaluationList.add(e);
				}
			}

			// Revert time zone change
			c1.add(Calendar.MILLISECOND,
					(int) (-60 * 60 * 1000 * e.getTimeZone()));
		}

		return activatedEvaluationList;

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
	 * @throws EvaluationExistsException
	 *             if an evaluation with the specified name exists for the
	 *             course
	 */

	public void addEvaluation(String courseID, String name,
			String instructions, boolean commentsEnabled, Date start,
			Date deadline, double timeZone, int gracePeriod)
			throws EvaluationExistsException {
		if (getEvaluation(courseID, name) != null) {
			throw new EvaluationExistsException();
		}

		Evaluation evaluation = new Evaluation(courseID, name, instructions,
				commentsEnabled, start, deadline, timeZone, gracePeriod);

		try {
			getPM().makePersistent(evaluation);
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
	 */
	public boolean addEvaluation(Evaluation e) {
		try {
			getPM().makePersistent(e);

			// Build submission objects for each student based on their team
			// number
			createSubmissions(e.getCourseID(), e.getName());
			System.out.println("create evaluation");
			return true;
		} catch (Exception exp) {
			exp.printStackTrace();
			return false;
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

		boolean debug = false;
		for (Submission s : submissionList) {
			// Exclude unsure and unfilled entries
			if (s.getPoints() == -999) {
				return 1;
			} else if (s.getPoints() != -101) {
				
				totalPoints = totalPoints + s.getPoints();
				numberOfStudents++;
			}
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
	public boolean createSubmissions(String courseID, String evaluationName) {
		Courses courses = Courses.inst();
		List<Student> studentList = courses.getStudentList(courseID);

		List<Submission> submissionList = new ArrayList<Submission>();
		Submission submission = null;

		for (Student sx : studentList) {
			for (Student sy : studentList) {
				if (sx.getTeamName().equals(sy.getTeamName())) {
					submission = new Submission(sx.getEmail(), sy.getEmail(),
							courseID, evaluationName, sx.getTeamName());
					submissionList.add(submission);
				}

			}

		}

		try {
			getPM().makePersistentAll(submissionList);
			return true;
		} catch (Exception e) {
			return false;
		}
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
	 */
	public void deleteEvaluation(String courseID, String name) {
		Evaluation evaluation = getEvaluation(courseID, name);

		try {
			getPM().deletePersistent(evaluation);
		} finally {
		}

		// Delete submission entries
		List<Submission> submissionList = getSubmissionList(courseID, name);
		getPM().deletePersistentAll(submissionList);
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

		try {
			getPM().deletePersistentAll(evaluationList);
			getPM().deletePersistentAll(submissionList);
		} finally {
		}
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
		System.out.println("awaiting submission pass");
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

		@SuppressWarnings("unchecked")
		List<Evaluation> evaluationList = (List<Evaluation>) getPM().newQuery(
				query).execute();

		if (evaluationList.isEmpty())
			return null;

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
	public List<Evaluation> getEvaluationList(int hours) {
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
	 * Returns the number of completed entries of a particular Evaluation.
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: Must not be null)
	 * 
	 * @param evaluationName
	 *            the evaluation name (Pre-condition: Must not be null)
	 * 
	 * @return the number of completed evaluations for the particular course
	 */
	public int getNumberOfCompletedEvaluations(String courseID,
			String evaluationName) {
		String query = "select from " + Submission.class.getName()
				+ " where courseID == '" + courseID
				+ "' && evaluationName == '" + evaluationName + "'";

		@SuppressWarnings("unchecked")
		List<Submission> submissionList = (List<Submission>) getPM().newQuery(
				query).execute();

		int count = 0;
		List<String> studentEmailList = new ArrayList<String>();

		for (Submission s : submissionList) {
			if (s.getPoints() != -999
					&& !studentEmailList.contains(s.getFromStudent())) {
				count++;
				studentEmailList.add(s.getFromStudent());
			}
		}

		return count;
	}

	/**
	 * Returns the number of entries of a particular Evaluation.
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: Must not be null)
	 * 
	 * @param evaluationName
	 *            the evaluation name (Pre-condition: Must not be null)
	 * 
	 * @return the total number of entries for a particular evaluation
	 */
	public int getNumberOfEvaluations(String courseID, String evaluationName) {
		String query = "select from " + Submission.class.getName()
				+ " where courseID == '" + courseID
				+ "' && evaluationName == '" + evaluationName + "'";

		@SuppressWarnings("unchecked")
		List<Submission> submissionList = (List<Submission>) getPM().newQuery(
				query).execute();
		int count = 0;
		List<String> studentEmailList = new ArrayList<String>();

		for (Submission s : submissionList) {
			if (!studentEmailList.contains(s.getFromStudent())) {
				count++;
				studentEmailList.add(s.getFromStudent());
			}
		}

		return count;
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

		return submissionList.get(0);
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
	 * @param fromStudent
	 *            the email of the sending student (Pre-condition: The
	 *            parameters must be valid)
	 * 
	 * @return the submissions of the specified student pertaining to the
	 *         specified evaluation
	 */
	public List<Submission> getSubmissionFromStudentList(String courseID,
			String evaluationName, String fromStudent) {

		String query = "select from " + Submission.class.getName()
				+ " where courseID == '" + courseID
				+ "' && evaluationName == '" + evaluationName
				+ "' && fromStudent == '" + fromStudent + "'";

		log.log(Level.WARNING, query);
		@SuppressWarnings("unchecked")
		List<Submission> submissionList = (List<Submission>) getPM().newQuery(
				query).execute();
		log.log(Level.WARNING, submissionList.toString());
		return submissionList;
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
					.param("evaluationname", evaluationName));
		}

		if (!taskOptionsList.isEmpty()) {
			queue.add(taskOptionsList);
		}
	}

	/**
	 * Adds to TaskQueue emails to inform students of publishing of results for
	 * an Evaluation.
	 * 
	 * @param studentList
	 *            the list of students to be informed
	 * 
	 * @param courseID
	 *            the course ID
	 * 
	 * @param evaluationName
	 *            the evaluation name
	 */
	private void informStudentsOfPublishingOfEvaluationResults(
			List<Student> studentList, String courseID, String name) {

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
					.param("operation", "informstudentspublishedevaluation")
					.param("email", s.getEmail()).param("name", s.getName())
					.param("courseid", courseID).param("evaluationname", name));
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
	public boolean isEvaluationSubmitted(Evaluation evaluation, String email) {
		List<Submission> submissionList = getSubmissionFromStudentList(
				evaluation.getCourseID(), evaluation.getName(), email);
		if (submissionList.size() == 0)
			return false;
		if (submissionList.get(0).getPoints() != -999) {
			return true;
		}
		return false;
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
	public boolean publishEvaluation(String courseID, String name,
			List<Student> studentList) {
		Evaluation evaluation = getEvaluation(courseID, name);

		evaluation.setPublished(true);

		informStudentsOfPublishingOfEvaluationResults(studentList, courseID,
				name);

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

}
