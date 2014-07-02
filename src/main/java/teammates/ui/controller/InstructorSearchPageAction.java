package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentSearchBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorSearchPageAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        new GateKeeper().verifyInstructorPrivileges(account);
        
        String key = getRequestParamValue(Const.ParamsNames.SEARCH_KEY);
        if(key == null) key = "";

        List<CommentSearchBundle> commentSearchResults = logic.searchComment(key, account.googleId);
        
        List<CommentAttributes> comments = new ArrayList<CommentAttributes>();
        for(CommentSearchBundle bundle:commentSearchResults){
            comments.add(bundle.comment);
        }
        
        InstructorSearchPageData data = new InstructorSearchPageData(account);
        data.comments = comments;
        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_SEARCH, data);
    }

}
