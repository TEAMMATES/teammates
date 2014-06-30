package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentAttributes;

public class InstructorSearchPageData extends PageData {

    public List<CommentAttributes> comments;
    
    public InstructorSearchPageData(AccountAttributes account) {
        super(account);
    }

}
