package teammates.common.datatransfer;

import static teammates.common.Common.EOL;

import java.util.logging.Logger;

import teammates.common.Common;
import teammates.storage.entity.Submission;

import com.google.appengine.api.datastore.Text;

public class SubmissionAttributes extends EntityAttributes {
	/** course ID */
	public String course;

	/** evaluation name */
	public String evaluation;

	/** team name */
	public String team;

	/** reviewer email */
	public String reviewer;

	public transient String reviewerName = null;

	/** reviewee email */
	public String reviewee;

	public transient String revieweeName = null;

	public int points;

	public Text justification;

	public Text p2pFeedback;

	private static Logger log = Common.getLogger();

	public transient int normalizedToStudent = Common.UNINITIALIZED_INT;

	public transient int normalizedToInstructor = Common.UNINITIALIZED_INT;
	
	public static final String ERROR_FIELD_COURSE = "Submission must belong to a valid course\n";
	public static final String ERROR_FIELD_EVALUATION = "Submission must belong to a valid evaluation\n";
	public static final String ERROR_FIELD_REVIEWEE = "Submission reviewee should be a valid email\n";
	public static final String ERROR_FIELD_REVIEWER = "Submission reviewer should be a valid email\n";
	
	public SubmissionAttributes() {

	}

	public SubmissionAttributes(String courseId, String evalName, String teamName,
			String toStudent, String fromStudent) {
		this.course = trimIfNotNull(courseId);
		this.evaluation = trimIfNotNull(evalName);
		this.team = trimIfNotNull(teamName);
		this.reviewee = trimIfNotNull(toStudent);
		this.reviewer = trimIfNotNull(fromStudent);
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

	/**
	 * using a simple copy method instead of clone(). Reason: seems it is overly
	 * complicated and not well thought out see
	 * http://stackoverflow.com/questions
	 * /2326758/how-to-properly-override-clone-method
	 * 
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

	public String getInvalidStateInfo() {
		String errorMessage = "";

		if (!Common.isValidCourseId(course)) {
			errorMessage += ERROR_FIELD_COURSE;
		}

		if (!Common.isValidName(evaluation)) {
			errorMessage += ERROR_FIELD_EVALUATION;
		}

		if (!Common.isValidEmail(reviewee)) {
			errorMessage += ERROR_FIELD_REVIEWEE;
		}
		
		if (!Common.isValidEmail(reviewer)) {
			errorMessage += ERROR_FIELD_REVIEWER;
		}

		return errorMessage;
	}

}
