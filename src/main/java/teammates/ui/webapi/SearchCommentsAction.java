package teammates.ui.webapi;

import java.util.List;

import teammates.common.datatransfer.FeedbackResponseCommentSearchResultBundle;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.output.CommentSearchResultsData;

/**
 * Action searches for comments. Keyword hits on session, question, or response will also be returned.
 */
class SearchCommentsAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() {
        // Only instructors can search for comments
        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
        }
    }

    @Override
    JsonResult execute() {
        String searchKey = getNonNullRequestParamValue(Const.ParamsNames.SEARCH_KEY);

        List<InstructorAttributes> instructors = logic.getInstructorsForGoogleId(userInfo.id);
        FeedbackResponseCommentSearchResultBundle commentSearchResults =
                logic.searchFeedbackResponseComments(searchKey, instructors);
        CommentSearchResultsData output = new CommentSearchResultsData(commentSearchResults);

        return new JsonResult(output);
    }
}
