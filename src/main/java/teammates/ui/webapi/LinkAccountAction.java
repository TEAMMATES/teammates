package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.storage.entity.Student;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.request.LinkAccountRequest;

/**
 * Links a logged-in account to the authenticated student from a student session page.
 */
public class LinkAccountAction extends LoggedInAction {

    @Override
    void checkSpecificAccessControl() throws InvalidHttpRequestBodyException, UnauthorizedAccessException {
        LinkAccountRequest requestBody = getAndValidateRequestBody(LinkAccountRequest.class);

        if (requestContext.isAdmin()) {
            return;
        }

        if (requestContext.getAccount() == null
                || !requestContext.getAccount().getId().equals(requestBody.getAccountId())) {
            throw new UnauthorizedAccessException("Not authorized to link this account.");
        }

        Student authenticatedStudent = requestContext.getSessionKeyUser();
        if (authenticatedStudent == null) {
            throw new UnauthorizedAccessException("Not authorized to link this account.");
        }

        UUID userId = requestBody.getUserId();
        if (!authenticatedStudent.getId().equals(userId)) {
            throw new UnauthorizedAccessException("Not authorized to link this account.");
        }
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        Student authenticatedStudent = requestContext.getSessionKeyUser();
        LinkAccountRequest requestBody = getAndValidateRequestBody(LinkAccountRequest.class);
        try {
            logic.joinCourseAndNotify(authenticatedStudent.getId(), logic.getAccount(requestBody.getAccountId()));
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        } catch (EntityAlreadyExistsException e) {
            throw new InvalidOperationException(e);
        }

        return new JsonResult("Account linked successfully.");
    }
}
