package teammates.ui.webapi;

import java.time.Instant;
import java.util.UUID;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.SupportRequestAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Logger;
import teammates.ui.output.SupportRequestData;
import teammates.ui.request.InvalidHttpRequestBodyException;
import teammates.ui.request.SupportRequestCreateRequest;

/**
 * Action: Creates a new support request.
 */
public class CreateSupportRequestAction extends Action {
    private static final Logger log = Logger.getLogger();

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        return; // no specific access needed for anything
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        SupportRequestCreateRequest supportRequestRequest = getAndValidateRequestBody(SupportRequestCreateRequest.class);

        Instant createdAt = Instant.ofEpochMilli(supportRequestRequest.getCreatedAt());
        Instant updatedAt = Instant.ofEpochMilli(supportRequestRequest.getUpdatedAt());

        SupportRequestAttributes newSupportRequest = SupportRequestAttributes.builder(UUID.randomUUID().toString())
                .withName(supportRequestRequest.getName())
                .withEmail(supportRequestRequest.getEmail())
                .withCreatedAt(createdAt)
                .withUpdatedAt(updatedAt)
                .withType(supportRequestRequest.getType())
                .withMessage(supportRequestRequest.getMessage())
                .withStatus(supportRequestRequest.getStatus())
                .build();
        
        try {
            return new JsonResult(new SupportRequestData(logic.createSupportRequest(newSupportRequest)));
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        } catch (EntityAlreadyExistsException e) {
            // Should not happen since UUID is usually unique
            log.severe("Unexpected error", e);
            return new JsonResult(e.getMessage(), HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
