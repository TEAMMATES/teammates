package teammates.ui.webapi;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.util.Const;
import teammates.storage.entity.AccountRequest;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.output.AccountRequestData;
import teammates.ui.output.AccountRequestsData;

/**
 * Action: Gets pending account requests.
 */
public class GetAccountRequestsAction extends AdminOnlyAction {
    @Override
    public JsonResult execute() {
        String accountRequestStatus = getNonNullRequestParamValue(Const.ParamsNames.ACCOUNT_REQUEST_STATUS);
        String pending = AccountRequestStatus.PENDING.name(); // 'PENDING'
        String all = "ALL";
        if (!pending.equalsIgnoreCase(accountRequestStatus) && !all.equalsIgnoreCase(accountRequestStatus)) {
            throw new InvalidHttpParameterException("Only 'pending' or 'all' is allowed for account request status.");
        }

        List<AccountRequest> accountRequests;
        if (all.equalsIgnoreCase(accountRequestStatus)) {
            accountRequests = logic.getAllAccountRequests();
        } else {
            accountRequests = logic.getPendingAccountRequests();
        }

        // Calculate frequency maps for the current list
        Map<String, Long> emailFreq = accountRequests.stream()
                .collect(Collectors.groupingBy(AccountRequest::getEmail, Collectors.counting()));
        Map<String, Long> instituteFreq = accountRequests.stream()
                .collect(Collectors.groupingBy(AccountRequest::getInstitute, Collectors.counting()));

        List<AccountRequestData> accountRequestDatas = accountRequests
                .stream()
                .map(ar -> {
                    AccountRequestData data = new AccountRequestData(ar);
                    data.setIsDuplicateEmail(emailFreq.getOrDefault(ar.getEmail(), 0L) > 1);
                    data.setSameInstituteCount(instituteFreq.getOrDefault(ar.getInstitute(), 0L).intValue());
                    
                    // existing instructor check (email + institute)
                    boolean hasInstructor = logic.getInstructorForEmailAndInstitute(ar.getEmail(), ar.getInstitute()) != null;
                    data.setHasExistingInstructor(hasInstructor);
                    
                    return data;
                })
                .collect(Collectors.toList());

        AccountRequestsData output = new AccountRequestsData();
        output.setAccountRequests(accountRequestDatas);
        return new JsonResult(output);
    }
}
