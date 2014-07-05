package teammates.ui.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import teammates.common.datatransfer.CommentSearchResultBundle;
import teammates.common.datatransfer.FeedbackResponseCommentSearchResultBundle;
import teammates.common.datatransfer.InstructorAttributes;
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
        
        List<InstructorAttributes> instructorRoles = logic.getInstructorsForGoogleId(account.googleId);
        Set<String> instructorEmails = new HashSet<String>();
        Set<String> instructorCourseIdList = new HashSet<String>();
        for(InstructorAttributes ins:instructorRoles){
            instructorEmails.add(ins.email);
            instructorCourseIdList.add(ins.courseId);
        }
        instructorEmails.add(account.email);
        
        CommentSearchResultBundle commentSearchResults = logic.searchComment(searchKey, account.googleId, "");
        FeedbackResponseCommentSearchResultBundle frCommentSearchResults = logic.searchFeedbackResponseComments(searchKey, account.googleId, "");
        
        InstructorSearchPageData data = new InstructorSearchPageData(account);
        data.searchKey = searchKey;
        data.commentSearchResultBundle = commentSearchResults;
        data.feedbackResponseCommentSearchResultBundle = frCommentSearchResults;
        data.instructorEmails = instructorEmails;
        data.instructorCourseIdList = instructorCourseIdList;
        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_SEARCH, data);
    }
}
