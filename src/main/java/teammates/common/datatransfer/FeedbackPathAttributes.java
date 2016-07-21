package teammates.common.datatransfer;

import java.util.List;

import teammates.common.util.Const;
import teammates.common.util.Utils;
import teammates.storage.entity.FeedbackPath;

public class FeedbackPathAttributes extends EntityAttributes {

    private String feedbackPathId;
    private String courseId;
    private String giver;
    private String recipient;
    
    public FeedbackPathAttributes() {
        // attributes to be set after construction
    }
    
    public FeedbackPathAttributes(String feedbackPathId, String courseId, String giver, String recipient) {
        this.feedbackPathId = feedbackPathId;
        this.courseId = courseId;
        this.giver = giver;
        this.recipient = recipient;
    }
    
    public FeedbackPathAttributes(String courseId, String giver, String recipient) {
        this(null, courseId, giver, recipient);
    }
    
    public FeedbackPathAttributes(FeedbackPath feedbackPath) {
        this(feedbackPath.getFeedbackPathId(), feedbackPath.getCourseId(),
             feedbackPath.getGiver(), feedbackPath.getRecipient());
    }
    
    public String getGiver() {
        return giver;
    }
    
    public String getRecipient() {
        return recipient;
    }
    
    @Override
    public List<String> getInvalidityInfo() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String toString() {
        return "FeedbackPathAttributes ["
                + "courseId=" + courseId
                + ", giver=" + giver
                + ", recipient=" + recipient + "]";
    }

    @Override
    public FeedbackPath toEntity() {
        return new FeedbackPath(feedbackPathId, courseId, giver, recipient);
    }

    @Override
    public String getIdentificationString() {
        return courseId + "/" + feedbackPathId;
    }

    @Override
    public String getEntityTypeAsString() {
        return "FeedbackPath";
    }

    @Override
    public String getBackupIdentifier() {
        return Const.SystemParams.COURSE_BACKUP_LOG_MSG + courseId;
    }

    @Override
    public String getJsonString() {
        return Utils.getTeammatesGson().toJson(this, FeedbackPathAttributes.class);
    }

    @Override
    public void sanitizeForSaving() {
        // TODO Auto-generated method stub
    }
    
    public boolean isStudentFeedbackPathGiver(StudentAttributes student) {
        String[] feedbackPathGiver = giver.split(" ");
        return feedbackPathGiver[0].equals(student.getTeam())
                || feedbackPathGiver[0].equals(student.getEmail())
                && "(Student)".equals(feedbackPathGiver[1]);
    }
    
    public boolean isInstructorFeedbackPathGiver(String instructorEmail) {
        String[] feedbackPathGiver = giver.split(" ");
        return feedbackPathGiver[0].equals(instructorEmail) && "(Instructor)".equals(feedbackPathGiver[1]);
    }
}
