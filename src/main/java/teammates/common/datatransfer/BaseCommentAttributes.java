package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.Sanitizer;
import teammates.common.util.TimeHelper;
import teammates.common.util.FieldValidator.FieldType;

import com.google.appengine.api.datastore.Text;

public abstract class BaseCommentAttributes extends EntityAttributes implements
        Comparable<BaseCommentAttributes> {

    public String courseId;
    public String giverEmail;
    public CommentSendingState sendingState = CommentSendingState.SENT;
    public Text commentText;
    public Date createdAt;
    public String lastEditorEmail;
    public Date lastEditedAt;

    public BaseCommentAttributes() {

    }

    public BaseCommentAttributes(String courseId, String giverEmail, Date createdAt, Text commentText) {
        this.courseId = courseId;
        this.giverEmail = giverEmail;
        this.commentText = commentText;
        this.createdAt = createdAt;
        this.lastEditorEmail = giverEmail;
        this.lastEditedAt = createdAt;
    }

    public abstract Long getId();

    @Override
    public String getIdentificationString() {
        return toString();
    }

    @Override
    public String getBackupIdentifier() {
        return Const.SystemParams.COURSE_BACKUP_LOG_MSG + courseId;
    }

    @Override
    public int compareTo(BaseCommentAttributes o) {
        if (o == null) {
            return 1;
        }
        return o.createdAt.compareTo(createdAt);
    }

    @Override
    public List<String> getInvalidityInfo() {
        FieldValidator validator = new FieldValidator();
        List<String> errors = new ArrayList<String>();
        String error;

        error = validator.getInvalidityInfo(FieldType.COURSE_ID, courseId);
        if (!error.isEmpty()) {
            errors.add(error);
        }

        error = validator.getInvalidityInfo(FieldType.EMAIL, giverEmail);
        if (!error.isEmpty()) {
            errors.add(error);
        }

        return errors;
    }

    @Override
    public void sanitizeForSaving() {
        this.courseId = Sanitizer.sanitizeForHtml(this.courseId.trim());
        this.commentText = Sanitizer.sanitizeTextField(this.commentText);
        this.giverEmail = Sanitizer.sanitizeForHtml(giverEmail);
    }

    protected String getEditedAtText(Boolean isGiverAnonymous, String displayGiverAs, String displayTimeAs) {
        if (this.lastEditedAt != null && (!this.lastEditedAt.equals(this.createdAt))) {
            return "(last edited " +
                    (isGiverAnonymous ? "" : "by " + displayGiverAs + " ") +
                    "at " + displayTimeAs + ")";
        } else {
            return "";
        }
    }

    public String getEditedAtTextForInstructor(Boolean isGiverAnonymous) {
        return getEditedAtText(isGiverAnonymous, this.lastEditorEmail,
                               TimeHelper.formatTime(this.lastEditedAt));
    }

    public String getEditedAtTextForStudent(Boolean isGiverAnonymous,
                                    String displayGiverAs) {
        return getEditedAtText(isGiverAnonymous, displayGiverAs,
                               TimeHelper.formatDate(this.lastEditedAt));
    }

}
