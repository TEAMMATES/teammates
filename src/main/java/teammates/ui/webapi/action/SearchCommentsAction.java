package teammates.ui.webapi.action;

import java.util.List;

import teammates.common.datatransfer.FeedbackResponseCommentSearchResultBundle;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.webapi.output.CommentSearchResultsData;

/**
 * Action searches for comments. Keyword hits on session, question, or response will also be returned.
 */
public class SearchCommentsAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        // Only instructors can search for comments
        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
        }
    }

    @Override
    public ActionResult execute() {
        String searchKey = getNonNullRequestParamValue(Const.ParamsNames.SEARCH_KEY);

        List<InstructorAttributes> instructors = logic.getInstructorsForGoogleId(userInfo.id);
        FeedbackResponseCommentSearchResultBundle commentSearchResults =
                logic.searchFeedbackResponseComments(searchKey, instructors);
        CommentSearchResultsData output = new CommentSearchResultsData(commentSearchResults);

        return new JsonResult(output);
    }
}
