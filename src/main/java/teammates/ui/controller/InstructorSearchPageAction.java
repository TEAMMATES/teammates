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
        
        int numberOfSearchOptions = 0;
        boolean isSearchCommentForStudents = getRequestParamAsBoolean(Const.ParamsNames.SEARCH_COMMENTS_FOR_STUDENTS);
        if(isSearchCommentForStudents){
            numberOfSearchOptions++;
        }
        
        boolean isSearchCommentForResponses = getRequestParamAsBoolean(Const.ParamsNames.SEARCH_COMMENTS_FOR_RESPONSES);
        if(isSearchCommentForResponses){
            numberOfSearchOptions++;
        }
        
        CommentSearchResultBundle commentSearchResults = new CommentSearchResultBundle();
        FeedbackResponseCommentSearchResultBundle frCommentSearchResults = new FeedbackResponseCommentSearchResultBundle();
        int totalResultsSize = 0;
        
        if(!searchKey.isEmpty() && numberOfSearchOptions != 0){
            if(isSearchCommentForStudents){
                commentSearchResults = logic.searchComment(searchKey, account.googleId, "");
            }
            if(isSearchCommentForResponses){
                frCommentSearchResults = logic.searchFeedbackResponseComments(searchKey, account.googleId, "");
            }
            
            totalResultsSize = commentSearchResults.getResultSize() + frCommentSearchResults.getResultSize();
            if(totalResultsSize == 0){
                //TODO: put this status msg into Const
                statusToUser.add("No results found.");
            }
        } else {
            //display search tips and tutorials
            statusToUser.add("Search Tips:<br>"
                    + "<ul>"
                    + "<li>Put more keywords to search for more precise results.</li>"
                    + "<li>Put quotation marks around words <b>\"[any word]\"</b> to search for an exact phrase in an exact order.</li>"
                    + "</ul>");
        }
        
        InstructorSearchPageData data = new InstructorSearchPageData(account);
        data.searchKey = searchKey;
        data.commentSearchResultBundle = commentSearchResults;
        data.feedbackResponseCommentSearchResultBundle = frCommentSearchResults;
        data.totalResultsSize = totalResultsSize;
        //TODO: put the followings into a map
        data.isSearchCommentForStudents = isSearchCommentForStudents;
        data.isSearchCommentForResponses = isSearchCommentForResponses;
        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_SEARCH, data);
    }
}
