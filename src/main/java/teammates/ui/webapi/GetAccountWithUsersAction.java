package teammates.ui.webapi;

import java.util.List;
import java.util.stream.Collectors;

import teammates.common.util.Const;
import teammates.storage.entity.Account;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.output.AccountWithUsersData;
import teammates.ui.output.InstructorData;
import teammates.ui.output.StudentData;

/**
 * Gets account's information with associated users information.
 */
public class GetAccountWithUsersAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        String googleId = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_ID);

        Account account = logic.getAccountForGoogleId(googleId);

        if (account == null) {
            throw new EntityNotFoundException("Account does not exist.");
        }

        List<InstructorData> instructorDataList = logic.getInstructorsByAccountId(account.getId())
                .stream()
                .map(InstructorData::new)
                .collect(Collectors.toList());

        List<StudentData> studentDataList = logic.getStudentsByAccountId(account.getId())
                .stream()
                .map(student -> {
                    StudentData studentData = new StudentData(student);
                    return studentData;
                })
                .collect(Collectors.toList());

        AccountWithUsersData output = new AccountWithUsersData(account, instructorDataList, studentDataList);
        return new JsonResult(output);
    }

}
