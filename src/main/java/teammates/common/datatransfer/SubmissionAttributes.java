package teammates.common.datatransfer;

import static teammates.common.Common.EOL;

import java.util.logging.Logger;

import teammates.common.Assumption;
import teammates.common.Common;
import teammates.common.FieldValidator;
import teammates.common.FieldValidator.FieldType;
import teammates.storage.entity.Submission;

import com.google.appengine.api.datastore.Text;

/**
 * A data transfer object for Submission entities.
 */
public class SubmissionAttributes extends EntityAttributes {
	
	//Note: be careful when changing these variables as their names are used in *.json files.
	public String course; //TODO: rename to courseId 
	public String evaluation; //TODO: rename to evaluationName 
	public String team; //TODO: rename to teamName
	public String reviewer; //TODO: rename to reviewerEmail
	public String reviewee; //TODO: rename to revieweeEmail
	public int points;
	public Text justification;
	public Text p2pFeedback;
	
	private static Logger log = Common.getLogger();
	
	//TODO: these should be extracted into a *Bundle class as they are not attributes of a Student entity
	public transient String reviewerName = null;
	public transient String revieweeName = null;
	public transient int normalizedToStudent = Common.UNINITIALIZED_INT;
	public transient int normalizedToInstructor = Common.UNINITIALIZED_INT;
	
	public SubmissionAttributes() {

	}

	public SubmissionAttributes(String courseId, String evalName, String teamName,
			String toStudent, String fromStudent) {
		//TODO: Need proper sanitization
		this.course = Common.trimIfNotNull(courseId);
		this.evaluation = Common.trimIfNotNull(evalName);
		this.team = Common.trimIfNotNull(teamName);
		this.reviewee = Common.trimIfNotNull(toStudent);
		this.reviewer = Common.trimIfNotNull(fromStudent);
	}

	public SubmissionAttributes(Submission s) {
		this.course = s.getCourseId();
		this.evaluation = s.getEvaluationName();
		this.reviewer = s.getReviewerEmail();
		this.reviewee = s.getRevieweeEmail();
		this.team = s.getTeamName();
		this.points = s.getPoints();
		this.justification = s.getJustification() == null ? new Text("") : s.getJustification();
		this.p2pFeedback = s.getCommentsToStudent() == null ? new Text("N/A") : s.getCommentsToStudent();
	}

	public Submission toEntity() {
		return new Submission(reviewer, reviewee, course, evaluation, team);
	}

	/* Note: using a simple copy method instead of clone(). Reason: seems it is overly
	 * complicated and not well thought out see
	 * http://stackoverflow.com/questions/2326758/how-to-properly-override-clone-method
	 */
	 /**
	 * @return a copy of the object
	 */
	public SubmissionAttributes getCopy() {
		SubmissionAttributes copy = new SubmissionAttributes();
		copy.course = this.course;
		copy.evaluation = this.evaluation;
		copy.team = this.team;
		copy.reviewer = this.reviewer;
		copy.reviewerName = this.reviewerName;
		copy.reviewee = this.reviewee;
		copy.revieweeName = this.revieweeName;
		copy.points = this.points;
		copy.justification = new Text(justification == null ? null
				: justification.getValue());
		copy.p2pFeedback = new Text(p2pFeedback == null ? null
				: p2pFeedback.getValue());
		copy.normalizedToStudent = this.normalizedToStudent;
		copy.normalizedToInstructor = this.normalizedToInstructor;
		return copy;
	}

	public boolean isSelfEvaluation() {
		return reviewee.equals(reviewer);
	}

	public String getInvalidStateInfo() {
		
		Assumption.assertTrue(course != null);
		Assumption.assertTrue(evaluation != null);
		Assumption.assertTrue(team != null);
		Assumption.assertTrue(reviewer != null);
		Assumption.assertTrue(reviewee != null);
		Assumption.assertTrue(justification != null);
		
		//p2pFeedback can be null if p2p feedback is not enabled;
		
		FieldValidator validator = new FieldValidator();
		String errorMessage = 
				validator.getValidityInfo(FieldType.COURSE_ID, course) + EOL+
				validator.getValidityInfo(FieldType.EVALUATION_NAME, evaluation) + EOL +
				(team.isEmpty()? "": validator.getValidityInfo(FieldType.TEAM_NAME, team) + EOL) +
				validator.getValidityInfo(FieldType.EMAIL, 
						"email address for the student receiving the evaluation", reviewee) + EOL+
				validator.getValidityInfo(FieldType.EMAIL, 
						"email address for the student giving the evaluation", reviewer) + EOL;
	
		return errorMessage.trim();
	}

	public String toString() {
		return toString(0);
	}

	public String toString(int indent) {
		String indentString = Common.getIndent(indent);
		StringBuilder sb = new StringBuilder();
		sb.append(indentString + "[eval:" + evaluation + "] " + reviewer + "->"
				+ reviewee + EOL);
		sb.append(indentString + " points:" + points);
		sb.append(" [normalized-to-student:" + normalizedToStudent + "]");
		sb.append(" [normalized-to-instructor:" + normalizedToStudent + "]");
		sb.append(EOL + indentString + " justificatoin:"
				+ justification.getValue());
		sb.append(EOL + indentString + " p2pFeedback:" + p2pFeedback.getValue());
		return sb.toString();
	}

}
