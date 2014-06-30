package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;

public class InstructorSearchPageAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String key = getRequestParamValue(Const.ParamsNames.SEARCH_KEY);
        List<CommentAttributes> comments = logic.searchComment(key != null? key: "");
        for(CommentAttributes comment:comments){
            System.out.print(comment.toString());
        }
        
        InstructorSearchPageData data = new InstructorSearchPageData(account);
        data.comments = comments;
        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_SEARCH, data);
    }

}
