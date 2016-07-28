package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.List;

import teammates.common.util.Const;
import teammates.common.util.Utils;
import teammates.storage.entity.FeedbackPath;

public class FeedbackPathAttributes extends EntityAttributes {
    
    public static final String FEEDBACK_PARTICIPANT_TYPE_STUDENT = "(Student)";
    public static final String FEEDBACK_PARTICIPANT_TYPE_INSTRUCTOR = "(Instructor)";
    public static final String FEEDBACK_PARTICIPANT_TYPE_TEAM = "(Team)";
    public static final String FEEDBACK_PARTICIPANT_CLASS = "Class";

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
        return new ArrayList<String>();
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
        // Nothing to sanitize
    }
    
    public boolean isStudentFeedbackPathGiver(StudentAttributes student) {
        return this.isStudentFeedbackPathParticipant(student, giver);
    }
    
    public boolean isStudentFeedbackPathRecipient(StudentAttributes student) {
        return isStudentFeedbackPathParticipant(student, recipient);
    }
    
    public boolean isInstructorFeedbackPathGiver(String instructorEmail) {
        return isInstructorFeedbackPathParticipant(instructorEmail, giver);
    }
    
    public boolean isInstructorFeedbackPathRecipient(String instructorEmail) {
        return isInstructorFeedbackPathParticipant(instructorEmail, recipient);
    }
    
    public boolean isTeamFeedbackPathGiver(String teamName) {
        return isTeamFeedbackPathParticipant(teamName, giver);
    }
    
    public boolean isTeamFeedbackPathRecipient(String teamName) {
        return isTeamFeedbackPathParticipant(teamName, recipient);
    }
    
    public String getGiverId() {
        return getParticipantId(giver);
    }
    
    public String getRecipientId() {
        return getParticipantId(recipient);
    }
    
    public boolean isFeedbackPathGiverAStudent() {
        return isFeedbackPathParticipantAStudent(giver);
    }
    
    public boolean isFeedbackPathGiverAnInstructor() {
        return isFeedbackPathParticipantAnInstructor(giver);
    }
    
    public boolean isFeedbackPathGiverATeam() {
        return isFeedbackPathParticipantATeam(giver);
    }
    
    public boolean isFeedbackPathRecipientAStudent() {
        return isFeedbackPathParticipantAStudent(recipient);
    }
    
    public boolean isFeedbackPathRecipientAnInstructor() {
        return isFeedbackPathParticipantAnInstructor(recipient);
    }
    
    public boolean isFeedbackPathRecipientATeam() {
        return isFeedbackPathParticipantATeam(recipient);
    }

    public boolean isFeedbackPathRecipientTheClass() {
        return isFeedbackPathParticipantTheClass(recipient);
    }
    
    private String getParticipantId(String participant) {
        if (isFeedbackPathParticipantAStudent(participant)) {
            return getStudentEmail(participant);
        } else if (isFeedbackPathParticipantAnInstructor(participant)) {
            return getInstructorEmail(participant);
        } else if (isFeedbackPathParticipantATeam(participant)) {
            return getTeamName(participant);
        } else if (isFeedbackPathParticipantTheClass(participant)) {
            return Const.GENERAL_QUESTION;
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
        return participant.endsWith(FEEDBACK_PARTICIPANT_TYPE_STUDENT);
    }
    
    private boolean isFeedbackPathParticipantAnInstructor(String participant) {
        return participant.endsWith(FEEDBACK_PARTICIPANT_TYPE_INSTRUCTOR);
    }
    
    private boolean isFeedbackPathParticipantATeam(String participant) {
        return participant.endsWith(FEEDBACK_PARTICIPANT_TYPE_TEAM);
    }
    
    private boolean isFeedbackPathParticipantTheClass(String participant) {
        return participant.equals(FEEDBACK_PARTICIPANT_CLASS);
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
    
    private boolean isStudentFeedbackPathParticipant(StudentAttributes student, String participant) {
        if (isFeedbackPathParticipantAStudent(participant)) {
            String studentFeedbackPathParticipant = getStudentEmail(participant);
            return studentFeedbackPathParticipant.equals(student.getEmail());
        }
        return false;
    }
    
    private boolean isInstructorFeedbackPathParticipant(String instructorEmail, String participant) {
        if (isFeedbackPathParticipantAnInstructor(participant)) {
            String instructorFeedbackPathParticipant = getInstructorEmail(participant);
            return instructorFeedbackPathParticipant.equals(instructorEmail);
        }
        return false;
    }
    
    private boolean isTeamFeedbackPathParticipant(String teamName, String participant) {
        if (isFeedbackPathParticipantATeam(participant)) {
            String teamFeedbackPathParticipant = getTeamName(participant);
            return teamFeedbackPathParticipant.equals(teamName);
        }
        return false;
    }
}
