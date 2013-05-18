package teammates.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.mail.internet.MimeMessage;

import teammates.common.Common;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.EvaluationDetailsBundle;
import teammates.common.datatransfer.EvaluationResultsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentResultBundle;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.datatransfer.TeamResultBundle;
import teammates.common.datatransfer.EvaluationAttributes.EvalStatus;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.EvaluationsDb;

import com.google.appengine.api.datastore.Text;

/**
 * Handles  operations related to evaluation entities.
 * This class does the field validation and sanitization before 
 * passing values to the Storage layer.
 */
public class EvaluationsLogic {
	//The API of this class doesn't have header comments because it sits behind
	//  the API of the logic class. Those who use this class is expected to be
	//  familiar with the its code and Logic's code. Hence, we have minimal
	//  header comments in this class.
	
	//TODO: add sanitization to this class.
	
	@SuppressWarnings("unused")
	private static final Logger log = Common.getLogger();

	private static final EvaluationsDb evaluationsDb = new EvaluationsDb();
	
	//TODO: remove this dependency
	private static final StudentsLogic studentsLogic = StudentsLogic.inst();
	private static final CoursesLogic coursesLogic = CoursesLogic.inst();
	private static final SubmissionsLogic submissionsLogic = SubmissionsLogic.inst();
	private static final InstructorsLogic instructorsLogic = InstructorsLogic.inst();

	private static EvaluationsLogic instance = null;
	public static EvaluationsLogic inst() {
		if (instance == null){
			instance = new EvaluationsLogic();
		}
		return instance;
	}

	/**
	 * Creates an evaluation and empty submissions for it.
	 */
	public void createEvaluationCascade(EvaluationAttributes e)
			throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException {
	
		if (!e.isValid()) {
			throw new InvalidParametersException(e.getInvalidStateInfo());
		}
		
		evaluationsDb.createEvaluation(e);
	
		List<StudentAttributes> studentDataList = studentsLogic.getStudentsForCourse(e.course);
		
		List<SubmissionAttributes> listOfSubmissionsToAdd = new ArrayList<SubmissionAttributes>();
	
		// This double loop creates 3 submissions for a pair of students:
		// x->x, x->y, y->x
		for (StudentAttributes sx : studentDataList) {
			for (StudentAttributes sy : studentDataList) {
				if (sx.team.equals(sy.team)) {
					SubmissionAttributes submissionToAdd = 
							new SubmissionAttributes(e.course, e.name, sx.team, sx.email, sy.email);
					submissionToAdd.p2pFeedback = new Text("");
					submissionToAdd.justification = new Text("");
					listOfSubmissionsToAdd.add(submissionToAdd);
				}
			}
		}
	
		SubmissionsLogic.inst().createSubmissions(listOfSubmissionsToAdd);
	}

	public EvaluationAttributes getEvaluation(String courseId, String evaluationName) {
		return evaluationsDb.getEvaluation(courseId, evaluationName);
	}

	public List<EvaluationAttributes> getEvaluationsForCourse(String courseId) {
		return evaluationsDb.getEvaluationsForCourse(courseId);
	}

	public List<EvaluationAttributes> getReadyEvaluations() {
		
		@SuppressWarnings("deprecation")
		List<EvaluationAttributes> evaluationList = evaluationsDb.getAllEvaluations();
		List<EvaluationAttributes> readyEvaluations = new ArrayList<EvaluationAttributes>();
	
		for (EvaluationAttributes e : evaluationList) {
			if (e.isReadyToActivate()) {
				readyEvaluations.add(e);
			}
		}
		return readyEvaluations;
	}

	public List<EvaluationAttributes> getEvaluationsClosingWithinTimeLimit(int hoursWithinLimit) {
		
		@SuppressWarnings("deprecation")
		List<EvaluationAttributes> evaluationList = evaluationsDb.getAllEvaluations();
		
		List<EvaluationAttributes> dueEvaluationList = new ArrayList<EvaluationAttributes>();
	
		for (EvaluationAttributes e : evaluationList) {
			
			if (e.isClosingWithinTimeLimit(hoursWithinLimit)) {
				dueEvaluationList.add(e);
			}
	
		}
	
		return dueEvaluationList;
	}

	public ArrayList<EvaluationDetailsBundle> getEvaluationsDetailsForInstructor(
			String instructorId) throws EntityDoesNotExistException {
		ArrayList<EvaluationDetailsBundle> evaluationSummaryList = new ArrayList<EvaluationDetailsBundle>();

		List<InstructorAttributes> instructorList = instructorsLogic.getInstructorsForGoogleId(instructorId);
		for (InstructorAttributes id : instructorList) {
			evaluationSummaryList.addAll(getEvaluationDetailsForCourse(id.courseId));
		}
		return evaluationSummaryList;
	}
	
	public ArrayList<EvaluationDetailsBundle> getEvaluationDetailsForCourse(String courseId) throws EntityDoesNotExistException{
		ArrayList<EvaluationDetailsBundle> evaluationSummaryList = new ArrayList<EvaluationDetailsBundle>();

		List<EvaluationAttributes> evaluationsSummaryForCourse = getEvaluationsForCourse(courseId);
		List<StudentAttributes> students = studentsLogic.getStudentsForCourse(courseId);

		for (EvaluationAttributes evaluation : evaluationsSummaryForCourse) {
			EvaluationDetailsBundle edd = getEvaluationDetails(students, evaluation);
			evaluationSummaryList.add(edd);
		}
		
		return evaluationSummaryList;
	}

	public EvaluationResultsBundle getEvaluationResult(String courseId,
			String evaluationName) throws EntityDoesNotExistException {
		
		verifyEvaluationExists(courseId, evaluationName);
	
		//TODO: The datatype below is not a good fit for the data. ArralyList<TeamDetailsBundle>?
		List<TeamDetailsBundle> teams = coursesLogic.getTeamsForCourse(courseId).teams;
		EvaluationResultsBundle returnValue = new EvaluationResultsBundle();
		returnValue.evaluation = getEvaluation(courseId, evaluationName);
		
		HashMap<String, SubmissionAttributes> submissionDataList = 
				submissionsLogic.getSubmissionsForEvaluationAsMap(courseId, evaluationName);
		returnValue.teamResults = new TreeMap<String,TeamResultBundle>();
		
		for (TeamDetailsBundle team : teams) {
			TeamResultBundle teamResultBundle = new TeamResultBundle(team.students);
			
			for (StudentAttributes student : team.students) {
				// TODO: refactor this method. May be have a return value?
				populateSubmissionsAndNames(submissionDataList,
						teamResultBundle,
						teamResultBundle.getStudentResult(student.email));
			}
			
			TeamEvalResult teamResult = calculateTeamResult(teamResultBundle);
			populateTeamResult(teamResultBundle, teamResult);
			returnValue.teamResults.put(team.name, teamResultBundle);
		}
		return returnValue;
	}

	public StudentResultBundle getEvaluationResultForStudent(String courseId,
			String evaluationName, String studentEmail) throws EntityDoesNotExistException {
		
		StudentAttributes student = studentsLogic.getStudentForEmail(courseId, studentEmail);
		
		if (student == null) {
			throw new EntityDoesNotExistException("The student " + studentEmail
					+ " does not exist in course " + courseId);
		}
	
		EvaluationResultsBundle evaluationResults = 
				getEvaluationResult(courseId,	evaluationName);
		TeamResultBundle teamData = evaluationResults.teamResults.get(student.team);
		StudentResultBundle returnValue = null;
	
		returnValue = teamData.getStudentResult(studentEmail);
	
		for (StudentResultBundle srb : teamData.studentResults) {
			returnValue.selfEvaluations.add(teamData.getStudentResult(srb.student.email).getSelfEvaluation());
		}
	
		if (evaluationResults.evaluation.p2pEnabled) {
			returnValue.sortIncomingByFeedbackAscending();
		}
		
		return returnValue;
	}

	public String getEvaluationResultSummaryAsCsv(String courseId, String evalName) 
			throws EntityDoesNotExistException {
		
		EvaluationResultsBundle evaluationResults = getEvaluationResult(courseId, evalName);
		
		String export = "";
		
		export += "Course" + ",," + evaluationResults.evaluation.course + Common.EOL
				+ "Evaluation Name" + ",," + evaluationResults.evaluation.name + Common.EOL
				+ Common.EOL;
		
		export += "Team" + ",," + "Student" + ",," + "Claimed" + ",," + "Perceived" + ",," + "Received" + Common.EOL;
		
		for (TeamResultBundle td : evaluationResults.teamResults.values()) {
			for (StudentResultBundle srb : td.studentResults) {
				String result = "";
				Collections.sort(srb.incoming, new Comparator<SubmissionAttributes>(){
					@Override
					public int compare(SubmissionAttributes s1, SubmissionAttributes s2){
							return Integer.valueOf(s2.normalizedToInstructor).compareTo(s1.normalizedToInstructor);
					}
				});
				for(SubmissionAttributes sub: srb.incoming){
					if(sub.reviewee.equals(sub.reviewer)) continue;
					if(result!="") result+=",";
					result += sub.normalizedToInstructor;
				}
				
				export += srb.student.team + ",," + srb.student.name + ",," + srb.summary.claimedToInstructor + ",," + srb.summary.perceivedToInstructor + ",," + result + Common.EOL;
			}
		}
		
		// Replace all Unset values
		export = export.replaceAll(Integer.toString(Common.UNINITIALIZED_INT), "N/A");
		export = export.replaceAll(Integer.toString(Common.POINTS_NOT_SURE), "Not Sure");
		export = export.replaceAll(Integer.toString(Common.POINTS_NOT_SUBMITTED), "Not Submitted");
		
		return export;
	}

	/**
	 * @return false if the student has any incomplete submissions to any team mates.
	 */
	public boolean isEvaluationCompletedByStudent(EvaluationAttributes evaluation, String email) {
		
		List<SubmissionAttributes> submissionList = 
				SubmissionsLogic.inst().getSubmissionsForEvaluationFromStudent(
						evaluation.course, evaluation.name, email);

		for (SubmissionAttributes sd : submissionList) {
			if (sd.points == Common.POINTS_NOT_SUBMITTED) {
				return false;
			}
		}
		return true;
	}

	public boolean isEvaluationExists(String courseId, String evaluationName) {
		return evaluationsDb.getEvaluation(courseId, evaluationName) != null;
	}
	
	public void updateEvaluation(EvaluationAttributes evaluation) 
			throws InvalidParametersException, EntityDoesNotExistException {
		
		if (!evaluation.isValid()) {
			throw new InvalidParametersException(evaluation.getInvalidStateInfo());
		}
		
		evaluationsDb.updateEvaluation(evaluation);
	}
	
	public void updateStudentEmailForSubmissionsInCourse(String course,
			String originalEmail, String email) {
		SubmissionsLogic.inst().updateStudentEmailForSubmissionsInCourse(course, originalEmail, email);
	}

	public void publishEvaluation(String courseId, String evaluationName) 
			throws EntityDoesNotExistException, InvalidParametersException {
		
		if (!isEvaluationExists(courseId, evaluationName)) {
			throw new EntityDoesNotExistException(
					"Trying to edit non-existent evaluation " 
							+ courseId + "/" + evaluationName);
		}
	
		EvaluationAttributes evaluation = getEvaluation(courseId, evaluationName);
		if (evaluation.getStatus() != EvalStatus.CLOSED) {
			throw new InvalidParametersException(
					Common.ERRORCODE_PUBLISHED_BEFORE_CLOSING,
					"Cannot publish an evaluation unless it is CLOSED");
		}
	
		setEvaluationPublishedStatus(courseId, evaluationName, true);
		sendEvaluationPublishedEmails(courseId, evaluationName);
		
	}

	public void unpublishEvaluation(String courseId, String evaluationName) 
			throws EntityDoesNotExistException, InvalidParametersException {
		
		if (!isEvaluationExists(courseId, evaluationName)) {
			throw new EntityDoesNotExistException(
					"Trying to edit non-existent evaluation " + courseId + "/" + evaluationName);
		}
		
		EvaluationAttributes evaluation = getEvaluation(courseId, evaluationName);
		if (evaluation.getStatus() != EvalStatus.PUBLISHED) {
			throw new InvalidParametersException(
					Common.ERRORCODE_UNPUBLISHED_BEFORE_PUBLISHING,
					"Cannot unpublish an evaluation unless it is PUBLISHED");
		}
	
		setEvaluationPublishedStatus(courseId, evaluationName, false);
		
	}

	public List<MimeMessage> sendReminderForEvaluation(String courseId,
			String evaluationName) throws EntityDoesNotExistException {
		
		if (!isEvaluationExists(courseId, evaluationName)) {
			throw new EntityDoesNotExistException(
					"Trying to edit non-existent evaluation " + courseId + "/" + evaluationName);
		}
	
		EvaluationAttributes evaluation = getEvaluation(courseId, evaluationName);
	
		// Filter out students who have submitted the evaluation
		List<StudentAttributes> studentDataList = studentsLogic.getStudentsForCourse(courseId);
	
		List<StudentAttributes> studentsToRemindList = new ArrayList<StudentAttributes>();
		for (StudentAttributes sd : studentDataList) {
			if (!isEvaluationCompletedByStudent(evaluation,
					sd.email)) {
				studentsToRemindList.add(sd);
			}
		}
	
		CourseAttributes course = coursesLogic.getCourse(courseId);
	
		List<MimeMessage> emails;
	
		Emails emailMgr = new Emails();
		try {
			emails = emailMgr.generateEvaluationReminderEmails(course,
					evaluation, studentsToRemindList);
			emailMgr.sendEmails(emails);
		} catch (Exception e) {
			throw new RuntimeException("Error while sending emails :", e);
		}
	
		return emails;
	}

	public void setEvaluationPublishedStatus(String courseId, String evaluationName, boolean b) 
			throws EntityDoesNotExistException {

		EvaluationAttributes e = evaluationsDb.getEvaluation(courseId, evaluationName);

		if (e == null) {
			throw new EntityDoesNotExistException("Trying to update non-existent Evaluation: "
					+ courseId + " | " + evaluationName );
		}
		
		e.published = b;
		evaluationsDb.updateEvaluation(e);
	}	
	
	/**
	 * Adjusts submissions for a student moving from one team to another.
	 * Deletes existing submissions for original team and creates empty
	 * submissions for the new team, in all existing submissions, including
	 * CLOSED and PUBLISHED ones.
	 */
	public void adjustSubmissionsForChangingTeam(String courseId,
			String studentEmail, String originalTeam, String newTeam) {
		
		List<EvaluationAttributes> evaluationDataList = 
				EvaluationsLogic.inst().getEvaluationsForCourse(courseId);
		
		submissionsLogic.deleteAllSubmissionsForStudent(courseId, studentEmail);
		
		for (EvaluationAttributes ed : evaluationDataList) {
			addSubmissionsForIncomingMember(courseId, ed.name, studentEmail, newTeam);
		}
	}

	/**
	 * Adjusts submissions for a student adding a new student to a course.
	 * Creates empty submissions for the new team, in all existing submissions,
	 * including CLOSED and PUBLISHED ones.
	 * 
	 */
	public void adjustSubmissionsForNewStudent(String courseId,
			String studentEmail, String team) {
		
		List<EvaluationAttributes> evaluationDataList = 
				EvaluationsLogic.inst().getEvaluationsForCourse(courseId);
		
		for (EvaluationAttributes ed : evaluationDataList) {
			addSubmissionsForIncomingMember(courseId, ed.name, studentEmail, team);
		}
	}


	public void deleteEvaluationCascade(String courseId, String evaluationName) {

		evaluationsDb.deleteEvaluation(courseId, evaluationName);
		SubmissionsLogic.inst().deleteAllSubmissionsForEvaluation(courseId, evaluationName);
	}

	
	public void deleteEvaluationsForCourse(String courseId) {
		evaluationsDb.deleteAllEvaluationsForCourse(courseId);
		SubmissionsLogic.inst().deleteAllSubmissionsForCourse(courseId);
	}
	
	private void addSubmissionsForIncomingMember(
			String courseId, String evaluationName, String studentEmail, String newTeam) {
	
		List<String> students = getExistingStudentsInTeam(courseId, newTeam);
	
		// add self evaluation 
		List<SubmissionAttributes> listOfSubmissionsToAdd = new ArrayList<SubmissionAttributes>();
		
		SubmissionAttributes submissionToAdd = new SubmissionAttributes(courseId,
				evaluationName, newTeam, studentEmail, studentEmail);
		submissionToAdd.p2pFeedback = new Text("");
		submissionToAdd.justification = new Text("");
		listOfSubmissionsToAdd.add(submissionToAdd);
		
		//remove self from list
		students.remove(studentEmail);
	
		// add submission to/from peers
		for (String peer : students) {
	
			// To
			submissionToAdd = new SubmissionAttributes(courseId, evaluationName,
					newTeam, peer, studentEmail);
			submissionToAdd.p2pFeedback = new Text("");
			submissionToAdd.justification = new Text("");
			listOfSubmissionsToAdd.add(submissionToAdd);
	
			// From
			submissionToAdd = new SubmissionAttributes(courseId, evaluationName,
					newTeam, studentEmail, peer);
			submissionToAdd.p2pFeedback = new Text("");
			submissionToAdd.justification = new Text("");
			listOfSubmissionsToAdd.add(submissionToAdd);
		}
		
		submissionsLogic.createSubmissions(listOfSubmissionsToAdd);
	}

	private List<String> getExistingStudentsInTeam(String courseId, String team) {
		
		Set<String> students = new HashSet<String>();
		
		List<SubmissionAttributes> submissionsDataList = 
				submissionsLogic.getSubmissionsForCourse(courseId);
		
		for (SubmissionAttributes s : submissionsDataList) {
			if (s.team.equals(team)) {
				students.add(s.reviewer);
			}
		}
		
		return new ArrayList<String>(students);
	}
	
	private EvaluationDetailsBundle getEvaluationDetails(List<StudentAttributes> students, EvaluationAttributes evaluation)
			throws EntityDoesNotExistException {
		EvaluationDetailsBundle edd = new EvaluationDetailsBundle(evaluation);
		edd.stats.expectedTotal = students.size();
		HashMap<String, SubmissionAttributes> submissions = 
				submissionsLogic.getSubmissionsForEvaluationAsMap(evaluation.course, evaluation.name);
		edd.stats.submittedTotal = countSubmittedStudents(submissions.values());
		return edd;
	}
	
	/**
	 * Returns submissions for the evaluation
	 */
	
	
	/**
	 * Returns how many students have submitted at least one submission.
	 */
	private int countSubmittedStudents(Collection<SubmissionAttributes> submissions) {
		int count = 0;
		List<String> emailsOfSubmittedStudents = new ArrayList<String>();
		for (SubmissionAttributes s : submissions) {
			if (s.points != Common.POINTS_NOT_SUBMITTED
					&& !emailsOfSubmittedStudents.contains(s.reviewer)) {
				count++;
				emailsOfSubmittedStudents.add(s.reviewer);
			}
		}
		return count;
	}
	
	private List<MimeMessage> sendEvaluationPublishedEmails(String courseId,
			String evaluationName) throws EntityDoesNotExistException {
		List<MimeMessage> emailsSent;

		CourseAttributes c = coursesLogic.getCourse(courseId);
		EvaluationAttributes e = getEvaluation(courseId, evaluationName);
		List<StudentAttributes> students = studentsLogic.getStudentsForCourse(courseId);

		Emails emailMgr = new Emails();
		try {
			emailsSent = emailMgr.generateEvaluationPublishedEmails(c, e,
					students);
			emailMgr.sendEmails(emailsSent);
		} catch (Exception ex) {
			throw new RuntimeException(
					"Unexpected error while sending emails ", ex);
		}
		return emailsSent;
	}
	
	private void verifyEvaluationExists(String courseId, String evaluationName) 
			throws EntityDoesNotExistException {
		
		if(getEvaluation(courseId, evaluationName)==null){
			throw new EntityDoesNotExistException(
					"The evaluation does not exist :"+courseId + "/"+evaluationName);
		}
		
	}
	
	private TeamEvalResult calculateTeamResult(TeamResultBundle teamResultBundle) {

		int teamSize = teamResultBundle.studentResults.size();
		int[][] claimedFromStudents = new int[teamSize][teamSize];
		teamResultBundle.sortByStudentNameAscending();
		
		int i = 0;
		for (StudentResultBundle studentResult: teamResultBundle.studentResults) {
			studentResult.sortOutgoingByStudentNameAscending();
			for (int j = 0; j < teamSize; j++) {
				SubmissionAttributes submissionData = studentResult.outgoing.get(j);
					claimedFromStudents[i][j] = submissionData.points;
			}
			i++;
		}
		return new TeamEvalResult(claimedFromStudents);
	}

	private void populateTeamResult(TeamResultBundle teamResultBundle, TeamEvalResult teamResult) {
		teamResultBundle.sortByStudentNameAscending();
		int teamSize = teamResultBundle.studentResults.size();
		
		int i = 0;
		for (StudentResultBundle studentResult: teamResultBundle.studentResults) {
			
			studentResult.sortIncomingByStudentNameAscending();
			studentResult.sortOutgoingByStudentNameAscending();
			studentResult.summary.claimedFromStudent = teamResult.claimed[i][i];
			studentResult.summary.claimedToInstructor = teamResult.normalizedClaimed[i][i];
			studentResult.summary.perceivedToStudent = teamResult.denormalizedAveragePerceived[i][i];
			studentResult.summary.perceivedToInstructor = teamResult.normalizedAveragePerceived[i];

			// populate incoming and outgoing
			for (int j = 0; j < teamSize; j++) {
				SubmissionAttributes incomingSub = studentResult.incoming.get(j);
				int normalizedIncoming = teamResult.denormalizedAveragePerceived[i][j];
				incomingSub.normalizedToStudent = normalizedIncoming;
				incomingSub.normalizedToInstructor = teamResult.normalizedPeerContributionRatio[j][i];
				log.finer("Setting normalized incoming of " + studentResult.student.name + " from "
						+ incomingSub.reviewerName + " to "
						+ normalizedIncoming);

				SubmissionAttributes outgoingSub = studentResult.outgoing.get(j);
				int normalizedOutgoing = teamResult.normalizedClaimed[i][j];
				outgoingSub.normalizedToStudent = Common.UNINITIALIZED_INT;
				outgoingSub.normalizedToInstructor = normalizedOutgoing;
				log.finer("Setting normalized outgoing of " + studentResult.student.name + " to "
						+ outgoingSub.revieweeName + " to "
						+ normalizedOutgoing);
			}
			i++;
		}
		
	}

	//TODO: unit test this
	private void populateSubmissionsAndNames(
			HashMap<String, SubmissionAttributes> submissions, 
			TeamResultBundle teamResultBundle,
			StudentResultBundle studentResultBundle) {
		
		for (StudentResultBundle peerResult : teamResultBundle.studentResults) {
			
			StudentAttributes peer = peerResult.student;

			// get incoming submission from peer
			String key = peer.email + "->" + studentResultBundle.student.email;
			SubmissionAttributes submissionFromPeer = submissions.get(key);
			// this workaround is to cater for missing submissions in
			// legacy data.
			if (submissionFromPeer == null) {
				log.warning("Cannot find submission for" + key);
				submissionFromPeer = createEmptySubmission(peer.email,
						studentResultBundle.student.email);
			} else {
				// use a copy to prevent accidental overwriting of data
				submissionFromPeer = submissionFromPeer.getCopy();
			}

			// set names in incoming submission
			submissionFromPeer.revieweeName = studentResultBundle.student.name;
			submissionFromPeer.reviewerName = peer.name;

			// add incoming submission
			studentResultBundle.incoming.add(submissionFromPeer);

			// get outgoing submission to peer
			key = studentResultBundle.student.email + "->" + peer.email;
			SubmissionAttributes submissionToPeer = submissions.get(key);

			// this workaround is to cater for missing submissions in
			// legacy data.
			if (submissionToPeer == null) {
				log.warning("Cannot find submission for" + key);
				submissionToPeer = createEmptySubmission(studentResultBundle.student.email,
						peer.email);
			} else {
				// use a copy to prevent accidental overwriting of data
				submissionToPeer = submissionToPeer.getCopy();
			}

			// set names in outgoing submission
			submissionToPeer.reviewerName = studentResultBundle.student.name;
			submissionToPeer.revieweeName = peer.name;

			// add outgoing submission
			studentResultBundle.outgoing.add(submissionToPeer);

		}
	}
	
	private SubmissionAttributes createEmptySubmission(String reviewer,
			String reviewee) {
		SubmissionAttributes s;
		s = new SubmissionAttributes();
		s.reviewer = reviewer;
		s.reviewee = reviewee;
		s.points = Common.UNINITIALIZED_INT;
		s.justification = new Text("");
		s.p2pFeedback = new Text("");
		s.course = "";
		s.evaluation = "";
		return s;
	}

}
