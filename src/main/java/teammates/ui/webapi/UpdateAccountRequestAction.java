package teammates.ui.webapi;

import java.time.Instant;

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
            throw new EntityNotFoundException("Account request with email: " + email
                    + " and institute: " + institute + " does not exist.");
        }
        if (!accountRequest.getStatus().equals(AccountRequestStatus.SUBMITTED)) {
            throw new InvalidOperationException("Only account requests with status SUBMITTED can be edited.");
        }

        AccountRequestUpdateRequest updateRequest = getAndValidateRequestBody(AccountRequestUpdateRequest.class);
        UpdateOptions newAccountRequest = AccountRequestAttributes.updateOptionsBuilder(email, institute)
                .withName(updateRequest.getInstructorName())
                .withInstitute(updateRequest.getInstructorInstitute())
                .withEmail(updateRequest.getInstructorEmail())
                .withLastProcessedAt(Instant.now())
                .build();

        try {
            AccountRequestAttributes updatedAccountRequest = logic.updateAccountRequest(newAccountRequest);
            taskQueuer.scheduleAccountRequestForSearchIndexing(updatedAccountRequest.getEmail(),
                    updatedAccountRequest.getInstitute());

            return new JsonResult(new AccountRequestData(updatedAccountRequest));
        } catch (InvalidParametersException ipe) {
            throw new InvalidHttpRequestBodyException(ipe);
        } catch (EntityDoesNotExistException ednee) {
            throw new EntityNotFoundException("Account request with email: " + email
                    + " and institute: " + institute + " does not exist.", ednee);
        } catch (EntityAlreadyExistsException eaee) {
            AccountRequestAttributes existingAccountRequest =
                    logic.getAccountRequest(updateRequest.getInstructorEmail(), updateRequest.getInstructorInstitute());
            throw new InvalidOperationException("There’s an existing account request with the email address and institute"
                    + " you want to update to, and its status is " + existingAccountRequest.getStatus() + ".", eaee);
        }
    }

}
