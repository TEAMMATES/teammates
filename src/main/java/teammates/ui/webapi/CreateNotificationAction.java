package teammates.ui.webapi;

import teammates.common.exception.InvalidParametersException;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.output.NotificationData;
import teammates.ui.request.NotificationCreateRequest;

/**
 * Action: Creates a new notification banner.
 */
public class CreateNotificationAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        NotificationCreateRequest createRequest = getAndValidateRequestBody(NotificationCreateRequest.class);

        try {
            return new JsonResult(new NotificationData(logic.createNotification(createRequest)));
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        }
    }
}
