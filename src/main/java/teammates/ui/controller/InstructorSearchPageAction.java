package teammates.ui.controller;

import teammates.common.datatransfer.CommentSearchResultBundle;
import teammates.common.datatransfer.FeedbackResponseCommentSearchResultBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorSearchPageAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        new GateKeeper().verifyInstructorPrivileges(account);
        String searchKey = getRequestParamValue(Const.ParamsNames.SEARCH_KEY);
        if(searchKey == null){
            searchKey = "";
        }
        
        CommentSearchResultBundle commentSearchResults = logic.searchComment(searchKey, account.googleId, "");
        FeedbackResponseCommentSearchResultBundle frCommentSearchResults = logic.searchFeedbackResponseComments(searchKey, account.googleId, "");
        
        InstructorSearchPageData data = new InstructorSearchPageData(account);
        data.searchKey = searchKey;
        data.commentSearchResultBundle = commentSearchResults;
        data.feedbackResponseCommentSearchResultBundle = frCommentSearchResults;
        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_SEARCH, data);
    }
}
