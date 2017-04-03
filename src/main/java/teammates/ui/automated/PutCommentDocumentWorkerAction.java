package teammates.ui.automated;

import teammates.common.datatransfer.attributes.CommentAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const;

/**
 * Task queue worker action: Puts searchable document for comment.
 */
public class PutCommentDocumentWorkerAction extends AutomatedAction {

    @Override
    protected String getActionDescription() {
        return null;
    }

    @Override
    protected String getActionMessage() {
        return null;
    }

    @Override
    public void execute() {
        String commentId = getRequestParamValue(Const.ParamsNames.COMMENT_ID);
        Assumption.assertNotNull(commentId);

        CommentAttributes comment = logic.getComment(Long.valueOf(commentId));
        Assumption.assertNotNull(comment);
        logic.putDocument(comment);
    }
}
