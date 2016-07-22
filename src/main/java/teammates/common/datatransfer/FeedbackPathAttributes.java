package teammates.common.datatransfer;

import java.util.List;

import teammates.common.util.Const;
import teammates.common.util.Utils;
import teammates.storage.entity.FeedbackPath;

public class FeedbackPathAttributes extends EntityAttributes {
    
    public static final String FEEDBACK_PARTICIPANT_TYPE_STUDENT = "(Student)";
    public static final String FEEDBACK_PARTICIPANT_TYPE_INSTRUCTOR = "(Instructor)";
    public static final String FEEDBACK_PARTICIPANT_TYPE_TEAM = "(Team)";

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
        return new FeedbackPath(courseId, giver, recipient);
    }

    @Override
    public String getIdentificationString() {
        return courseId + "/" + feedbackPathId + "/" + giver + "/" + recipient;
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
        // TODO: See if anything needs to be sanitized
    }
    
    public boolean isStudentFeedbackPathGiver(StudentAttributes student) {
        String studentFeedbackPathGiver = getStudentEmail(giver);
        String teamFeedbackPathGiver = getTeamName(giver);
        return isFeedbackPathParticipantAStudent(giver) && studentFeedbackPathGiver.equals(student.getEmail())
               || isFeedbackPathParticipantATeam(giver) && teamFeedbackPathGiver.equals(student.getTeam());
    }
    
    public boolean isInstructorFeedbackPathGiver(String instructorEmail) {
        String instructorFeedbackPathGiver = getInstructorEmail(giver);
        return isFeedbackPathParticipantAnInstructor(giver)
                && instructorFeedbackPathGiver.equals(instructorEmail);
    }
    
    public String getGiverId() {
        return getParticipantId(giver);
    }
    
    public String getRecipientId() {
        return getParticipantId(recipient);
    }
    
    private String getParticipantId(String participant) {
        if (isFeedbackPathParticipantAStudent(participant)) {
            return getStudentEmail(participant);
        } else if (isFeedbackPathParticipantAnInstructor(participant)) {
            return getInstructorEmail(participant);
        } else if (isFeedbackPathParticipantATeam(participant)) {
            return getTeamName(participant);
        } else {
            return "";
        }
    }
    
    private String getStudentEmail(String participant) {
        int studentParticipantTypeIndex = getStudentParticipantTypeIndex(participant);
        return participant.substring(0, studentParticipantTypeIndex - 1);
    }
    
    private String getInstructorEmail(String participant) {
        int instructorParticipantTypeIndex = getInstructorParticipantTypeIndex(participant);
        return participant.substring(0, instructorParticipantTypeIndex - 1);
    }
    
    private String getTeamName(String participant) {
        int teamParticipantTypeIndex = getTeamParticipantTypeIndex(participant);
        return participant.substring(0, teamParticipantTypeIndex - 1);
    }
    
    private boolean isFeedbackPathParticipantAStudent(String participant) {
        int studentParticipantTypeIndex = getStudentParticipantTypeIndex(participant);
        return FEEDBACK_PARTICIPANT_TYPE_STUDENT.equals(participant.substring(studentParticipantTypeIndex));
    }
    
    private boolean isFeedbackPathParticipantAnInstructor(String participant) {
        int instructorParticipantTypeIndex = getInstructorParticipantTypeIndex(participant);
        return FEEDBACK_PARTICIPANT_TYPE_INSTRUCTOR.equals(
                participant.substring(instructorParticipantTypeIndex));
    }
    
    private boolean isFeedbackPathParticipantATeam(String participant) {
        int teamParticipantTypeIndex = getTeamParticipantTypeIndex(participant);
        return FEEDBACK_PARTICIPANT_TYPE_TEAM.equals(participant.substring(teamParticipantTypeIndex));
    }
    
    private int getStudentParticipantTypeIndex(String participant) {
        return participant.length() - FEEDBACK_PARTICIPANT_TYPE_STUDENT.length();
    }
    
    private int getInstructorParticipantTypeIndex(String participant) {
        return participant.length() - FEEDBACK_PARTICIPANT_TYPE_INSTRUCTOR.length();
    }
    
    private int getTeamParticipantTypeIndex(String participant) {
        return participant.length() - FEEDBACK_PARTICIPANT_TYPE_TEAM.length();
    }
}
