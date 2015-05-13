package teammates.common.datatransfer;

import java.util.Date;

import teammates.common.util.Const;
import teammates.common.util.TimeHelper;

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

    public BaseCommentAttributes(String courseId, String giverEmail,
            Date createdAt, Text commentText) {
        this.courseId = courseId;
        this.giverEmail = giverEmail;
        this.commentText = commentText;
        this.createdAt = createdAt;
        this.lastEditorEmail = giverEmail;
        this.lastEditedAt = createdAt;
    }

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

    protected String getEditedAtText(Boolean isGiverAnonymous,
            String displayGiverAs,
            String displayTimeAs) {
        if (this.lastEditedAt != null
                && (!this.lastEditedAt.equals(this.createdAt))) {
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
