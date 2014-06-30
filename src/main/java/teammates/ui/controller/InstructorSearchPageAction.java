package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;

public class InstructorSearchPageAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String key = getRequestParamValue(Const.ParamsNames.SEARCH_KEY);
        if(key == null) key = "";

        List<InstructorAttributes> instructorRoles = logic.getInstructorsForGoogleId(account.googleId);
        List<CommentAttributes> comments = logic.searchComment(key, instructorRoles);
        
        for(CommentAttributes comment:comments){
            System.out.print(comment.toString());
        }
        
        InstructorSearchPageData data = new InstructorSearchPageData(account);
        data.comments = comments;
        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_SEARCH, data);
    }

}
