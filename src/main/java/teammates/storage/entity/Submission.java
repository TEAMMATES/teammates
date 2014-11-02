package teammates.storage.entity;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Extension;

import teammates.common.util.Const;

import com.google.appengine.api.datastore.Text;

/**
 * Represents an evaluation submission from one student to
 * another student.
 */
@PersistenceCapable
public class Submission {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;

    @Persistent
    private String courseID;

    @Persistent
    private String evaluationName;

    @Persistent
    @Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
    private String teamName;

    @Persistent
    private String fromStudent;

    @Persistent
    private String toStudent;

    @Persistent
    @Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
    private int points;

    @Persistent
    @Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
    private Text justification;

    @Persistent
    @Extension(vendorName = "datanucleus", key = "gae.unindexed", value = "true")
    private Text commentsToStudent;

    
    public Submission(String reviewerEmail, String revieweeEmail,
            String courseId, String evaluationName, String teamName) {
        // TODO: why do we need the team name here?
        this.setReviewerEmail(reviewerEmail);
        this.setRevieweeEmail(revieweeEmail);
        this.setCourseId(courseId);
        this.setEvaluationName(evaluationName);
        this.setTeamName(teamName);
        this.setJustification(new Text(""));
        this.setCommentsToStudent(new Text(""));
        this.points = Const.POINTS_NOT_SUBMITTED;
    }

    public Long getId() {
        return id;
    }
    
    public String getCourseId() {
        return courseID;
    }

    public void setCourseId(String courseId) {
        this.courseID = courseId.trim();
    }

    public String getEvaluationName() {
        return evaluationName;
    }

    public void setEvaluationName(String evaluationName) {
        this.evaluationName = evaluationName.trim();
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName.trim();
    }

    public String getReviewerEmail() {
        return fromStudent;
    }

    public void setReviewerEmail(String reviewerEmail) {
        this.fromStudent = reviewerEmail.trim();
    }

    public String getRevieweeEmail() {
        return toStudent;
    }

    public void setRevieweeEmail(String revieweeEmail) {
        this.toStudent = revieweeEmail.trim();
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Text getJustification() {
        return justification;
    }

    public void setJustification(Text justification) {
        this.justification = justification;
    }

    public Text getCommentsToStudent() {
        return commentsToStudent;
    }

    public void setCommentsToStudent(Text commentsToStudent) {
        this.commentsToStudent = commentsToStudent;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.courseID + "|");
        sb.append(this.evaluationName + "|");
        sb.append(this.fromStudent + "|");
        sb.append(this.toStudent + "|");
        sb.append(this.points + "|");
        sb.append(this.teamName + "\n");
        return sb.toString();
    }
}
