package teammates.ui.controller;

import teammates.common.util.Const;

public class StudentCommentsPageAction extends Action {

    @Override
    protected ActionResult execute() {
        StudentCommentsPageData pageData = new StudentCommentsPageData(account);
        return createShowPageResult(Const.ViewURIs.STUDENT_COMMENTS, pageData);
    }

}
