package teammates.ui.automated;

import teammates.common.datatransfer.attributes.CommentAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Logger;

/**
 * Task queue worker action: Puts searchable document for comment.
 */
public class PutCommentDocumentWorkerAction extends AutomatedAction {

    private static final Logger log = Logger.getLogger();

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
        if (comment == null) {
            log.severe("Comment " + commentId + "was not found");
            return;
        }
        logic.putDocument(comment);
    }
}
