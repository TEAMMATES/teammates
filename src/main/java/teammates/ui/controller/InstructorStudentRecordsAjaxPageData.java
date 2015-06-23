package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.SessionResultsBundle;

public class InstructorStudentRecordsAjaxPageData extends PageData {

    public List<SessionResultsBundle> results;

    public InstructorStudentRecordsAjaxPageData(AccountAttributes account) {
        super(account);
    }

}
