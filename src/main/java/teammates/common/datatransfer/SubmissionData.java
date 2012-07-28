package teammates.common.datatransfer;

import static teammates.common.Common.EOL;
import teammates.common.Common;
import teammates.storage.entity.Submission;

import com.google.appengine.api.datastore.Text;

public class SubmissionData {
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
	
	//TODO: to rename to 'normalizedToStudent'
	public transient int normalized = Common.UNINITIALIZED_INT;
	
	public transient int normalizedToCoord = Common.UNINITIALIZED_INT;

	public SubmissionData() {

	}

	public SubmissionData(Submission s) {
		this.course = s.getCourseID();
		this.evaluation = s.getEvaluationName();
		this.reviewer = s.getFromStudent();
		this.reviewee = s.getToStudent();
		this.team = s.getTeamName();
		this.points = s.getPoints();
		this.justification = s.getJustification();
		this.p2pFeedback = s.getCommentsToStudent();
	}

	public Submission toSubmission() {
		return new Submission(reviewer, reviewee, course, evaluation, team,
				points, justification, p2pFeedback);
	}
	
	
	/** using a simple copy method instead of clone().
	 * Reason: seems it is overly complicated and not well thought out
	 *   see http://stackoverflow.com/questions/2326758/how-to-properly-override-clone-method
	 * @return a copy of the object
	 */
	public SubmissionData getCopy(){
		SubmissionData copy = new SubmissionData();
		copy.course = this.course;
		copy.evaluation = this.evaluation;
		copy.team = this.team;
		copy.reviewer = this.reviewer;
		copy.reviewerName = this.reviewerName;
		copy.reviewee = this.reviewee;
		copy.revieweeName = this.revieweeName;
		copy.points = this.points;
		copy.justification = new Text(justification==null?null:justification.getValue());
		copy.p2pFeedback = new Text(p2pFeedback==null?null:p2pFeedback.getValue());
		copy.normalized = this.normalized;
		copy.normalizedToCoord = this.normalizedToCoord;
		return copy;
	}
	
	public boolean isSelfEvaluation(){
		return reviewee.equals(reviewer);
	}
	
	public String toString(){
		return toString(0);
	}
	
	public String toString(int indent){
		String indentString = Common.getIndent(indent);
		StringBuilder sb = new StringBuilder();
		sb.append(indentString+"[eval:"+evaluation+"] "+reviewer+"->"+reviewee+EOL);
		sb.append(indentString+" points:"+points);
		sb.append(" [normalized-to-student:"+normalized+"]");
		sb.append(" [normalized-to-coord:"+normalized+"]");
		sb.append(EOL+indentString+" justificatoin:"+justification.getValue());
		sb.append(EOL+indentString+" p2pFeedback:"+p2pFeedback.getValue());
		return sb.toString();
	}

}
