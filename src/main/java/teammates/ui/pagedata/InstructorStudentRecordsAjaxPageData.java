package teammates.ui.pagedata;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.ui.template.FeedbackResultsTable;

public class InstructorStudentRecordsAjaxPageData extends PageData {

    private List<FeedbackResultsTable> resultsTables;

    public InstructorStudentRecordsAjaxPageData(AccountAttributes account, StudentAttributes student, String sessionToken,
                                                List<FeedbackSessionResultsBundle> results) {
        super(account, student, sessionToken);
        this.resultsTables = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            FeedbackSessionResultsBundle result = results.get(i);
            String studentName = result.appendTeamNameToName(student.name, student.team);
            this.resultsTables.add(new FeedbackResultsTable(i, studentName, result));
        }
    }

    public List<FeedbackResultsTable> getResultsTables() {
        return resultsTables;
    }

}
