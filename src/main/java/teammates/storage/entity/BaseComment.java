package teammates.storage.entity;

import java.util.Date;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.CommentSendingState;

public interface BaseComment {

    public void setSendingState(CommentSendingState newState);

    public void setGiverEmail(String updatedEmail);

    public void setCommentText(Text commentText);

    public void setLastEditorEmail(String giverEmail);

    public void setLastEditedAt(Date createdAt);

}
