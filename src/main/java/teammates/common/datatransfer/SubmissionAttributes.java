package teammates.common.datatransfer;

import static teammates.common.util.Const.EOL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.Sanitizer;
import teammates.common.util.StringHelper;
import teammates.common.util.FieldValidator.FieldType;
import teammates.common.util.Utils;
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
    
    @SuppressWarnings("unused")
    private static Logger log = Utils.getLogger();
    
    public SubmissionDetailsBundle details = new SubmissionDetailsBundle();

    public SubmissionAttributes() {

    }

    public SubmissionAttributes(String courseId, String evalName, String teamName,
            String toStudent, String fromStudent) {
        this.course = Sanitizer.sanitizeTitle(courseId);
        this.evaluation = Sanitizer.sanitizeTitle(evalName);
        this.team = Sanitizer.sanitizeTitle(teamName);
        this.reviewee = Sanitizer.sanitizeName(toStudent);
        this.reviewer = Sanitizer.sanitizeName(fromStudent);
        this.justification = Sanitizer.sanitizeTextField(justification);
        this.p2pFeedback = Sanitizer.sanitizeTextField(p2pFeedback);
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
        copy.details.reviewerName = this.details.reviewerName;
        copy.reviewee = this.reviewee;
        copy.details.revieweeName = this.details.revieweeName;
        copy.points = this.points;
        copy.justification = new Text(justification == null ? null
                : justification.getValue());
        copy.p2pFeedback = new Text(p2pFeedback == null ? null
                : p2pFeedback.getValue());
        copy.details.normalizedToStudent = this.details.normalizedToStudent;
        copy.details.normalizedToInstructor = this.details.normalizedToInstructor;
        return copy;
    }
    
    public boolean isSelfEvaluation() {
        return reviewee.equals(reviewer);
    }

    public List<String> getInvalidityInfo() {
        
        Assumption.assertTrue(justification != null);
        //p2pFeedback can be null if p2p feedback is not enabled;
        
        FieldValidator validator = new FieldValidator();
        List<String> errors = new ArrayList<String>();
        String error;
        
        error= validator.getInvalidityInfo(FieldType.COURSE_ID, course);
        if(!error.isEmpty()) { errors.add(error); }
        
        error = validator.getInvalidityInfo(FieldType.EVALUATION_NAME, evaluation);
        if(!error.isEmpty()) { errors.add(error); }
        
        error = validator.getInvalidityInfo(FieldType.TEAM_NAME, team);
        if(!error.isEmpty()) { errors.add(error); }
        
        error = validator.getInvalidityInfo(FieldType.EMAIL, 
                "email address for the student receiving the evaluation", reviewee);
        if(!error.isEmpty()) { errors.add(error); }
        
        error = validator.getInvalidityInfo(FieldType.EMAIL, 
                        "email address for the student giving the evaluation", reviewer);
        if(!error.isEmpty()) { errors.add(error); }
    
        return errors;
    }
    
    
    public static void sortByJustification(List<SubmissionAttributes> submissions) {
        Collections.sort(submissions, new Comparator<SubmissionAttributes>() {
            public int compare(SubmissionAttributes s1, SubmissionAttributes s2) {
                return s1.justification.toString().compareTo(
                        s2.justification.toString());
            }
        });
    }
    
    public static void sortByReviewee(List<SubmissionAttributes> submissions) {
        Collections.sort(submissions, new Comparator<SubmissionAttributes>() {
            public int compare(SubmissionAttributes s1, SubmissionAttributes s2) {
                int result = s1.details.revieweeName.compareTo(s2.details.revieweeName);
                if (result == 0)
                    s1.reviewee.compareTo(s2.reviewee);
                return result;
            }
        });
    }

    public static void sortByPointsAscending(List<SubmissionAttributes> submissions) {
        Collections.sort(submissions, new Comparator<SubmissionAttributes>() {
            public int compare(SubmissionAttributes s1, SubmissionAttributes s2) {
                return Integer.valueOf(s1.points).compareTo(
                        Integer.valueOf(s2.points));
            }
        });
    }
    
    
    public static void sortByNormalizedPointsDescending(List<SubmissionAttributes> submissions){
        Collections.sort(submissions, new Comparator<SubmissionAttributes>(){
            @Override
            public int compare(SubmissionAttributes s1, SubmissionAttributes s2){
                return Integer.valueOf(s2.details.normalizedToInstructor)
                        .compareTo(Integer.valueOf(s1.details.normalizedToInstructor));
            }
        });
    }
    
    public static void putSelfSubmissionFirst(List<SubmissionAttributes> submissions){
        for(int i=0; i<submissions.size(); i++){
            SubmissionAttributes sub = submissions.get(i);
            if(sub.reviewee.equals(sub.reviewer)){
                submissions.remove(sub);
                submissions.add(0,sub);
                break;
            }
        }
    }

    public String toString() {
        return toString(0);
    }

    public String toString(int indent) {
        String indentString = StringHelper.getIndent(indent);
        StringBuilder sb = new StringBuilder();
        sb.append(indentString + "[eval:" + evaluation + "] " + reviewer + "->"
                + reviewee + EOL);
        sb.append(indentString + " points:" + points);
        sb.append(" [normalized-to-student:" + details.normalizedToStudent + "]");
        sb.append(" [normalized-to-instructor:" + details.normalizedToStudent + "]");
        sb.append(EOL + indentString + " justificatoin:"
                + justification.getValue());
        sb.append(EOL + indentString + " p2pFeedback:" + p2pFeedback.getValue());
        return sb.toString();
    }

    @Override
    public String getIdentificationString() {
        return this.course + "/" + this.evaluation
                + " | to: " + this.reviewee + " | from: "
                + this.reviewer;
    }

    @Override
    public String getEntityTypeAsString() {
        return "Submission";
    }
    
    @Override
    public String getBackupIdentifier() {
        return Const.SystemParams.COURSE_BACKUP_LOG_MSG + course;
    }
    
    @Override
    public String getJsonString() {
        return Utils.getTeammatesGson().toJson(this, SubmissionAttributes.class);
    }
    
    @Override
    public void sanitizeForSaving() {
        this.course = Sanitizer.sanitizeForHtml(course); //TODO: rename to courseId 
        this.evaluation = Sanitizer.sanitizeForHtml(evaluation); //TODO: rename to evaluationName 
        this.team = Sanitizer.sanitizeForHtml(team); //TODO: rename to teamName
        this.reviewer = Sanitizer.sanitizeForHtml(reviewer); //TODO: rename to reviewerEmail
        this.reviewee = Sanitizer.sanitizeForHtml(reviewee); //TODO: rename to revieweeEmail
        if(justification != null) {
            this.justification = new Text(Sanitizer.sanitizeForHtml(justification.getValue()));
        }
        if(p2pFeedback != null) {
            this.p2pFeedback = new Text(Sanitizer.sanitizeForHtml(p2pFeedback.getValue()));
        } else {
            this.p2pFeedback = new Text("");
        }
    }

}
