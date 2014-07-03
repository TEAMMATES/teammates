package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentSearchResultBundle;

public class InstructorSearchPageData extends PageData {

    public CommentSearchResultBundle commentSearchResultBundle;
    
    public InstructorSearchPageData(AccountAttributes account) {
        super(account);
    }

}
