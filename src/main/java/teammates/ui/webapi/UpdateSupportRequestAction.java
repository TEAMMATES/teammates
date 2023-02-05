package teammates.ui.webapi;

import java.time.Instant;

import teammates.common.datatransfer.attributes.SupportRequestAttributes;
import teammates.common.datatransfer.attributes.SupportRequestAttributes.UpdateOptions;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.ui.output.SupportRequestData;
import teammates.ui.request.InvalidHttpRequestBodyException;
import teammates.ui.request.SupportRequestUpdateRequest;

/**
 * Action: Updates a support request.
 */
public class UpdateSupportRequestAction extends AdminOnlyAction {
    
    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        String id = getNonNullRequestParamValue(Const.ParamsNames.SUPPORT_REQUEST_ID);
        SupportRequestUpdateRequest supportRequestRequest = getAndValidateRequestBody(SupportRequestUpdateRequest.class);

        Instant createdAt = Instant.ofEpochMilli(supportRequestRequest.getCreatedAt());
        Instant updatedAt = Instant.ofEpochMilli(supportRequestRequest.getUpdatedAt());

        UpdateOptions newSupportRequest = SupportRequestAttributes.updateOptionsBuilder(id)
                .withName(supportRequestRequest.getName())
                .withEmail(supportRequestRequest.getEmail())
                .withCreatedAt(createdAt)
                .withUpdatedAt(updatedAt)
                .withType(supportRequestRequest.getType())
                .withMessage(supportRequestRequest.getMessage())
                .withStatus(supportRequestRequest.getStatus())
                .build();

        try {
            return new JsonResult(new SupportRequestData(logic.updateSupportRequest(newSupportRequest)));
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        } catch (EntityDoesNotExistException ednee) {
            throw new EntityNotFoundException(ednee);
        }
    }
}
