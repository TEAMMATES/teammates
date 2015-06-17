package teammates.ui.template;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentParticipantType;

public class VisibilityCheckboxes {

    private List<Boolean> visibilitySettingsForRecipient;
    private List<Boolean> visibilitySettingsForRecipientTeam;
    private List<Boolean> visibilitySettingsForRecipientSection;
    private List<Boolean> visibilitySettingsForCourseStudents;
    private List<Boolean> visibilitySettingsForInstructors;
    private CommentAttributes comment;
    
    public VisibilityCheckboxes(CommentAttributes comment) {
        this.comment = comment;
        visibilitySettingsForRecipient = getVisibilityForCommentParticipant(CommentParticipantType.PERSON);
        visibilitySettingsForRecipientTeam = getVisibilityForCommentParticipant(CommentParticipantType.TEAM);
        visibilitySettingsForRecipientSection = getVisibilityForCommentParticipant(CommentParticipantType.SECTION);
        visibilitySettingsForCourseStudents = getVisibilityForCommentParticipant(CommentParticipantType.COURSE);
        visibilitySettingsForInstructors = getVisibilityForCommentParticipant(CommentParticipantType.INSTRUCTOR);
    }
    
    public List<Boolean> getVisibilitySettingsForRecipient() {
        return visibilitySettingsForRecipient;
    }

    public List<Boolean> getVisibilitySettingsForRecipientTeam() {
        return visibilitySettingsForRecipientTeam;
    }

    public List<Boolean> getVisibilitySettingsForRecipientSection() {
        return visibilitySettingsForRecipientSection;
    }

    public List<Boolean> getVisibilitySettingsForCourseStudents() {
        return visibilitySettingsForCourseStudents;
    }

    public List<Boolean> getVisibilitySettingsForInstructors() {
        return visibilitySettingsForInstructors;
    }

    private List<Boolean> getVisibilityForCommentParticipant(CommentParticipantType participant) {
        List<Boolean> visibilitySettingsForParticipant = new ArrayList<Boolean>();
        visibilitySettingsForParticipant.add(comment.showCommentTo.contains(participant));
        visibilitySettingsForParticipant.add(comment.showGiverNameTo.contains(participant));
        visibilitySettingsForParticipant.add(comment.showRecipientNameTo.contains(participant));
        return visibilitySettingsForParticipant;
    }
}
