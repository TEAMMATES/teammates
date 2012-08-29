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

import teammates.common.Common;
import teammates.common.datatransfer.EvaluationData;
import teammates.common.datatransfer.StudentData;
import teammates.common.datatransfer.SubmissionData;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.datastore.Datastore;
import teammates.storage.entity.Submission;

public class EvaluationsStorage {
	private static EvaluationsStorage instance = null;
	private static final Logger log = Common.getLogger();

	private static final AccountsDb accountsDb = new AccountsDb();
	private static final EvaluationsDb evaluationsDb = new EvaluationsDb();
	private static final SubmissionsDb submissionsDb = new SubmissionsDb();
	
	

	private PersistenceManager getPM() {
		return Datastore.getPersistenceManager();
	}

	public static EvaluationsStorage inst() {
		if (instance == null)
			instance = new EvaluationsStorage();
		return instance;
	}

	


	/**
	 * Atomically creates an Evaluation Object and a list of Submissions for the evaluation
	 * 
	 * @param e
	 *            An EvaluationData object
	 * 
	 * @author wangsha
	 * @throws EntityAlreadyExistsException, InvalidParametersException
	 */
	public void createEvaluation(EvaluationData e) throws EntityAlreadyExistsException, InvalidParametersException {
		
		try {
			
			evaluationsDb.createEvaluation(e);

			// Build submission objects for each student based on their team number
			List<StudentData> studentDataList = accountsDb.getStudentListForCourse(e.course);

			// This double loop creates 3 submissions for a pair of students: x->x, x->y, y->x
			for (StudentData sx : studentDataList) {
				for (StudentData sy : studentDataList) {
					if (sx.team.equals(sy.team)) {
						submissionsDb.createSubmission(e.course, e.name, sx.team, sx.email, sy.email);
					}
				}
			}
		
		} catch (EntityAlreadyExistsException eaee) {
			log.warning(Common.stackTraceToString(eaee));
			throw eaee;
		} catch (InvalidParametersException ipe) {
			log.warning(Common.stackTraceToString(ipe));
			throw ipe;
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
	 * Adjusts submissions for a student moving from one team to another. 
	 * Deletes existing submissions for original team and creates empty 
	 * submissions for the new team, in all existing submissions,
	 * including CLOSED and PUBLISHED ones.
	 */
	public void adjustSubmissionsForChangingTeam(String courseId, String studentEmail, String originalTeam, String newTeam){
		List<EvaluationData> evaluationDataList = evaluationsDb.getEvaluationsForCourse(courseId);
		for (EvaluationData ed : evaluationDataList) {
			
			deleteSubmissionsForOutgoingMember(courseId, ed.name,
						studentEmail, originalTeam);
			
			addSubmissionsForIncomingMember(courseId, ed.name, studentEmail,
					newTeam);
		}
	}
	
	/**
	 * Adjusts submissions for a student adding a new student to a course. 
	 * Creates empty submissions for the new team, in all existing submissions,
	 * including CLOSED and PUBLISHED ones.
	 */
	public void adjustSubmissionsForNewStudent(String courseId, String studentEmail, String team){
		List<EvaluationData> evaluationDataList = evaluationsDb.getEvaluationsForCourse(courseId);
		for (EvaluationData ed : evaluationDataList) {
			addSubmissionsForIncomingMember(courseId, ed.name, studentEmail, team);
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
		List<SubmissionData> submissionsDataList = submissionsDb.getSubmissionsForCourse(courseId);
		for(SubmissionData s: submissionsDataList){
			if(s.team.equals(team)){
				students.add(s.reviewer);
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
	 * Atomically deletes an Evaluation and its Submission objects.
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: The courseID and evaluationName
	 *            pair must be valid)
	 * 
	 * @param name
	 *            the evaluation name (Pre-condition: The courseID and
	 *            evaluationName pair must be valid)
	 *            
	 */
	public void deleteEvaluation(String courseId, String name) {
		
		if (evaluationsDb.getEvaluation(courseId, name) == null) {
			String errorMessage = "Trying to delete non-existent evaluation : "
					+ courseId + "/" + name;
			log.warning(errorMessage);
		} else {
			// Delete the Evaluation entity
			evaluationsDb.deleteEvaluation(courseId, name);
			
			// Delete Submission entries belonging to this Evaluation
			List<Submission> submissionList = getSubmissionList(courseId, name);
			getPM().deletePersistentAll(submissionList);
			getPM().flush();
		}
	}

	/**
	 * Atomically deletes all Evaluation objects and its Submission objects from a Course.
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: Must be valid)
	 * 
	 */
	public void deleteEvaluations(String courseId) {
		
		evaluationsDb.deleteAllEvaluationsForCourse(courseId);
		submissionsDb.deleteAllSubmissionsForCourse(courseId);
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
	 * Returns if there is an ongoing Evaluation for a particular Course.
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: Must not be null)
	 * 
	 * @return <code>true</code> if there is an ongoing evaluation,
	 *         <code>false</code> otherwise.
	 */
	public boolean isEvaluationOngoing(String courseId) {
		List<EvaluationData> evaluationDataList = evaluationsDb.getEvaluationsForCourse(courseId);

		Calendar now = Calendar.getInstance();
		Calendar start = Calendar.getInstance();
		Calendar deadline = Calendar.getInstance();

		// SGP timezone
		now.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY) + 8);

		for (EvaluationData ed : evaluationDataList) {
			start.setTime(ed.startTime);
			deadline.setTime(ed.endTime);

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

	public List<EvaluationData> getEvaluationsSummaryForCourse(String courseId) {

		List<EvaluationData> evaluationDataList = evaluationsDb.getEvaluationsForCourse(courseId);

		return evaluationDataList;
	}

	public void verifyEvaluationExists(String courseId, String evaluationName)
			throws EntityDoesNotExistException {
		if (evaluationsDb.getEvaluation(courseId, evaluationName) == null) {
			throw new EntityDoesNotExistException("The evaluation "
					+ evaluationName + " does not exist in course " + courseId);
		}
	}
	
	
	public EvaluationsDb getEvaluationsDb() {
		return evaluationsDb;
	}
	
	public SubmissionsDb getSubmissionsDb() {
		return submissionsDb;
	}

}
