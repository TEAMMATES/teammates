package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Text;

import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.Sanitizer;
import teammates.common.util.TimeHelper;
import teammates.common.util.FieldValidator.FieldType;
import teammates.common.util.Utils;
import teammates.storage.entity.Evaluation;

/**
 * Represents a data transfer object for Evaluation entities.
 */
public class EvaluationAttributes extends EntityAttributes implements SessionAttributes{
    
    public enum EvalStatus {
        AWAITING, OPEN, CLOSED, PUBLISHED, DOES_NOT_EXIST
    }
        
    //Note: be careful when changing these variables as their names are used in *.json files.
    public String courseId;
    public String name;
    public Text instructions;
    public Date startTime;
    public Date endTime;
    public double timeZone;
    public int gracePeriod;
    public boolean p2pEnabled;
    public boolean published = false;
    public boolean activated = false;

    private static Logger log = Utils.getLogger();

    public EvaluationAttributes() {

    }
    
    public EvaluationAttributes(String courseId, String name, Text instructions,
            Date startTime, Date endTime, double timeZone, int gracePeriod,
            boolean p2pEnabled, boolean published, boolean activated) {
        this.courseId = Sanitizer.sanitizeTitle(courseId);
        this.name = Sanitizer.sanitizeName(name);
        this.instructions = Sanitizer.sanitizeTextField(instructions);
        this.startTime = startTime;
        this.endTime = endTime;
        this.timeZone = timeZone;
        this.gracePeriod = gracePeriod;
        this.p2pEnabled = p2pEnabled;
        this.published = published;
        this.activated = activated;
    }

    public EvaluationAttributes(Evaluation e) {
        this.courseId = e.getCourseId();
        this.name = e.getName();
        this.instructions = e.getLongInstructions();
        if (this.instructions == null) {
            // for backward compatibility
            this.instructions = new Text(e.getInstructions());
        }
        this.startTime = e.getStart();
        this.endTime = e.getDeadline();
        this.timeZone = e.getTimeZone();
        this.gracePeriod = e.getGracePeriod();
        this.p2pEnabled = e.isCommentsEnabled();
        this.published = e.isPublished();
        this.activated = e.isActivated();
    }

    public Evaluation toEntity() {
        Evaluation evaluation = new Evaluation(courseId, name, instructions,
                p2pEnabled, startTime, endTime, timeZone, gracePeriod);
        evaluation.setActivated(activated);
        evaluation.setPublished(published);
        return evaluation;
    }
    
    public EvalStatus getStatus() {
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        TimeHelper.convertToUserTimeZone(now, timeZone);

        Calendar start = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        start.setTime(startTime);

        Calendar end = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        end.setTime(endTime);
        end.add(Calendar.MINUTE, gracePeriod);

        log.fine(Const.EOL + "Now  : " + TimeHelper.calendarToString(now)
                + Const.EOL + "Start: " + TimeHelper.calendarToString(start)
                + Const.EOL + "End  : " + TimeHelper.calendarToString(end));

        if (published) {
            return EvalStatus.PUBLISHED;
        } else if (now.after(end)) {
            return EvalStatus.CLOSED;
        } else if (now.after(start)) {
            return EvalStatus.OPEN;
        } else {
            return EvalStatus.AWAITING;
        }
    }
    
    /**
     * @return True if the current time is between start time and deadline, but
     *         the evaluation has not been activated yet.
     */
    public boolean isReadyToActivate() {
        Calendar currentTimeInUserTimeZone = TimeHelper.convertToUserTimeZone(
                Calendar.getInstance(TimeZone.getTimeZone("UTC")), timeZone);

        Calendar evalStartTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        evalStartTime.setTime(startTime);

        log.fine("current:"
                + TimeHelper.calendarToString(currentTimeInUserTimeZone)
                + "|start:" + TimeHelper.calendarToString(evalStartTime));

        if (currentTimeInUserTimeZone.before(evalStartTime)) {
            return false;
        } else {
            return (!activated);
        }
    }
    
    /**
     * @return true if the evaluation start time is in the future, after accounting
     * for time zone differences.
     */
    public boolean isOpeningInFuture() {
        Calendar currentTimeInUserTimeZone = TimeHelper.convertToUserTimeZone(
                Calendar.getInstance(TimeZone.getTimeZone("UTC")), timeZone);

        Calendar evalStartTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        evalStartTime.setTime(startTime);

        return currentTimeInUserTimeZone.before(evalStartTime);
    }

    //TODO: unit test this
    public boolean isClosingWithinTimeLimit(int hours) {

        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        // Fix the time zone accordingly
        now.add(Calendar.MILLISECOND,
                (int) (60 * 60 * 1000 * timeZone));
        
        Calendar start = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        start.setTime(startTime);
        
        Calendar deadline = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        deadline.setTime(endTime);

        long nowMillis = now.getTimeInMillis();
        long deadlineMillis = deadline.getTimeInMillis();
        long differenceBetweenDeadlineAndNow = (deadlineMillis - nowMillis)
                / (60 * 60 * 1000);

        // If now and start are almost similar, it means the evaluation 
        // is open for only 24 hours.
        // Hence we do not send a reminder e-mail for the evaluation.
        return now.after(start)
                && (differenceBetweenDeadlineAndNow >= hours - 1 
                && differenceBetweenDeadlineAndNow < hours);
    }


    /**
     * @return true if the evaluation closing time is in the future, after accounting
     * for time zone differences and grace period.
     */
    public boolean isClosingInFuture() {
        Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        TimeHelper.convertToUserTimeZone(now, timeZone);

        Calendar end = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        end.setTime(endTime);
        end.add(Calendar.MINUTE, gracePeriod);
        
        return now.before(end);
    
    }

    @Override
    public boolean isValid() {
        return getInvalidityInfo().isEmpty();
    }

    public List<String> getInvalidityInfo() {
        
        Assumption.assertTrue(startTime!=null);
        Assumption.assertTrue(endTime!=null);
        
        FieldValidator validator = new FieldValidator();
        List<String> errors = new ArrayList<String>();
        String error;
        
        error= validator.getInvalidityInfo(FieldType.COURSE_ID, courseId);
        if(!error.isEmpty()) { errors.add(error); }
        
        error= validator.getInvalidityInfo(FieldType.EVALUATION_NAME, name);
        if(!error.isEmpty()) { errors.add(error); }
        
        error= validator.getInvalidityInfo(FieldType.EVALUATION_INSTRUCTIONS, instructions);
        if(!error.isEmpty()) { errors.add(error); }        
        
        error= validator.getValidityInfoForTimeFrame(FieldType.EVALUATION_TIME_FRAME,
                FieldType.START_TIME, FieldType.END_TIME, startTime, endTime);
        if(!error.isEmpty()) { errors.add(error); }    
        
        error= validator.getValidityInfoForEvalStartTime(startTime, timeZone, activated);
        if(!error.isEmpty()) { errors.add(error); }    
        
        error= validator.getValidityInfoForEvalEndTime(endTime, timeZone, published);
        if(!error.isEmpty()) { errors.add(error); }    

        return errors;
    }

    @Override
    public String toString() {
        return Utils.getTeammatesGson()
                .toJson(this, EvaluationAttributes.class);
    }

    public static List<EvaluationAttributes> toAttributes(
            List<Evaluation> evaluationEntities) {
        
        List<EvaluationAttributes> attributesList = new ArrayList<EvaluationAttributes>();
        for(Evaluation e: evaluationEntities){
            attributesList.add(new EvaluationAttributes(e));
        }
        return attributesList;
    }

    @Override
    public String getIdentificationString() {
        return this.courseId + " | " + this.name;
    }

    @Override
    public String getEntityTypeAsString() {
        return "Evaluation";
    }

    @Override
    public String getBackupIdentifier() {
        return Const.SystemParams.COURSE_BACKUP_LOG_MSG + courseId;
    }
    
    @Override
    public String getJsonString() {
        return Utils.getTeammatesGson().toJson(this, EvaluationAttributes.class);
    }
    
    public boolean isSimilar(EvaluationAttributes e) {
        if(e==null){
            return false;
        }
        return this.courseId.equals(e.courseId)
                && this.name.equals(e.name)
                && this.instructions.equals(e.instructions)
                && this.activated == e.activated
                && this.published == e.published
                && this.p2pEnabled == e.p2pEnabled
                && this.gracePeriod == e.gracePeriod
                && this.timeZone == e.timeZone
                && this.startTime.equals(e.startTime)
                && this.endTime.equals(e.endTime);
    }
    

    /**
     * Sets derived attributes 'activated' and 'published' based on other
     * attributes. <br>
     * * If the opening time is in the future (after accounting for timezone differences),
     *   'activated' is set to false. <br>
     * * If the closing time is in the future (after accounting for timezone differences
     *   and the grace period), published is set to false.<br>
     * * If already closed, 'activated' is set to true. <br>
     */
    public void setDerivedAttributes() {
        // Set derived attributes.
        if(isOpeningInFuture()){
            activated = false;
        }        
        if(isClosingInFuture()){
            published = false;
        }
        
        //If already closed, we want to prevent any activation emails from going out.
        // This is useful when an update changes the state from AWAITING to CLOSED.
        if(!isClosingInFuture()){ 
            activated = true;
        }
        
    }

    public EvaluationAttributes getCopy() {
        EvaluationAttributes copy = new EvaluationAttributes();
        copy.courseId = this.courseId;
        copy.name = this.name;
        copy.instructions = this.instructions; 
        copy.startTime = this.startTime;
        copy.endTime = this.endTime;
        copy.timeZone = this.timeZone;
        copy.gracePeriod = this.gracePeriod;
        copy.p2pEnabled = this.p2pEnabled;
        copy.published = this.published;
        copy.activated = this.activated;
        return copy;
    }

    @Override
    public void sanitizeForSaving() {
        this.courseId = Sanitizer.sanitizeTitle(courseId);
        this.name = Sanitizer.sanitizeName(Sanitizer.sanitizeForHtml(name));
        this.instructions = Sanitizer.sanitizeTextField(this.instructions);
    }
    
    /**
     * Sorts evaluations based courseID (ascending), then by deadline
     * (ascending), then by start time (ascending), then by evaluation name
     * (ascending) The sort by CourseID part is to cater the case when this
     * method is called with combined evaluations from many courses
     * 
     * @param evals
     */
    public static void sortEvaluationsByDeadline(List<EvaluationAttributes> evals) {
        Collections.sort(evals, new Comparator<EvaluationAttributes>() {
            public int compare(EvaluationAttributes eval1, EvaluationAttributes eval2) {
                int result = 0;
                if (result == 0) {
                    result = eval1.courseId.compareTo(eval2.courseId);
                }
                if (result == 0) {
                    result = eval1.endTime.after(eval2.endTime) ? 1
                            : (eval1.endTime.before(eval2.endTime) ? -1 : 0);
                }
                if (result == 0) {
                    result = eval1.startTime.after(eval2.startTime) ? 1
                            : (eval1.startTime.before(eval2.startTime) ? -1 : 0);
                }
                if (result == 0) {
                    result = eval1.name.compareTo(eval2.name);
                }
                return result;
            }
        });
    }
    
    /**
     * Sorts evaluations based courseID (ascending), then by deadline
     * (descending), then by start time (descending), then by evaluation name
     * (ascending) The sort by CourseID part is to cater the case when this
     * method is called with combined evaluations from many courses
     * 
     * @param evals
     */
    public static void sortEvaluationsByDeadlineDescending(List<EvaluationAttributes> evals) {
        Collections.sort(evals, new Comparator<EvaluationAttributes>() {
            public int compare(EvaluationAttributes eval1, EvaluationAttributes eval2) {
                int result = 0;
                if (result == 0) {
                    result = eval1.endTime.after(eval2.endTime) ? -1
                            : (eval1.endTime.before(eval2.endTime) ? 1 : 0);
                }
                if (result == 0) {
                    result = eval1.startTime.after(eval2.startTime) ? -1
                            : (eval1.startTime.before(eval2.startTime) ? 1 : 0);
                }
                if (result == 0) {
                    result = eval1.courseId.compareTo(eval2.courseId);
                }
                if (result == 0) {
                    result = eval1.name.compareTo(eval2.name);
                }
                return result;
            }
        });
    }
    
    @Override
    public Date getSessionStartTime() {
        return this.startTime;
    }

    @Override
    public Date getSessionEndTime() {
        return this.endTime;
    }

    @Override
    public String getSessionName() {
        return this.name;
    }
}
