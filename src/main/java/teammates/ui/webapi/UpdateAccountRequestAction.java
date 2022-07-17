package teammates.ui.webapi;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.datatransfer.attributes.AccountRequestAttributes.UpdateOptions;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.ui.output.AccountRequestData;
import teammates.ui.request.AccountRequestUpdateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Edits the name, institute, email of an account request.
 */
class UpdateAccountRequestAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        String email = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
        String institute = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_INSTITUTION);

        AccountRequestAttributes accountRequest = logic.getAccountRequest(email, institute);
        if (accountRequest == null) {
            throw new EntityNotFoundException("Account request for instructor with email: " + email
                    + " and institute: " + institute + " does not exist.");
        }
        if (!accountRequest.getStatus().equals(AccountRequestStatus.SUBMITTED)) {
            throw new InvalidOperationException("Only account requests with status SUBMITTED can be edited.");
        }

        boolean isForceUpdate = Boolean.parseBoolean(getNonNullRequestParamValue(Const.ParamsNames.IS_FORCE_UPDATE));

        AccountRequestUpdateRequest updateRequest = getAndValidateRequestBody(AccountRequestUpdateRequest.class);
        UpdateOptions newAccountRequest = AccountRequestAttributes.updateOptionsBuilder(email, institute)
                .withName(updateRequest.getInstructorName())
                .withInstitute(updateRequest.getInstructorInstitute())
                .withEmail(updateRequest.getInstructorEmail())
                .build();

        try {
            AccountRequestAttributes updatedAccountRequest = logic.updateAccountRequest(newAccountRequest, isForceUpdate);
            taskQueuer.scheduleAccountRequestForSearchIndexing(updatedAccountRequest.getEmail(),
                    updatedAccountRequest.getInstitute()); // TODO: do not delete old document?

            return new JsonResult(new AccountRequestData(updatedAccountRequest));
        } catch (InvalidParametersException ipe) {
            throw new InvalidHttpRequestBodyException(ipe);
        } catch (EntityDoesNotExistException ednee) {
            throw new EntityNotFoundException("Account request for instructor with email: " + email
                    + " and institute: " + institute + " does not exist.");
        } catch (EntityAlreadyExistsException eaee) {
            AccountRequestAttributes existingAccountRequest =
                    logic.getAccountRequest(updateRequest.getInstructorEmail(), updateRequest.getInstructorInstitute());
            throw new InvalidOperationException(generateExistingAccountRequestErrorMessage(
                    existingAccountRequest.getStatus()), eaee);
        }
    }

    private String generateExistingAccountRequestErrorMessage(AccountRequestStatus status) {
        String basicMessage = "There’s an existing account request with the email address and institute"
                + " you want to update to and its status is " + status + ".\n";
        StringBuilder sb = new StringBuilder(basicMessage);
        switch (status) {
        case SUBMITTED:
            sb.append("You can locate that account request on the requests page and process it instead.");
            break;
        case APPROVED:
        case REGISTERED:
            sb.append("You can search for that account request to see more information.");
            break;
        case REJECTED:
            sb.append("You can choose to force update this account request, which will delete that rejected request.");
            break;
        }
        return sb.toString();
    }

}
