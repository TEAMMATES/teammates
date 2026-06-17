package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.output.NotificationData;
import teammates.ui.request.NotificationUpdateRequest;

/**
 * Action: Updates a new notification banner.
 */
public class UpdateNotificationAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        UUID notificationId = getUuidRequestParamValue(Const.ParamsNames.NOTIFICATION_ID);
        NotificationUpdateRequest updateRequest = getAndValidateRequestBody(NotificationUpdateRequest.class);

        try {
            return new JsonResult(new NotificationData(logic.updateNotification(notificationId, updateRequest)));
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        } catch (EntityDoesNotExistException ednee) {
            throw new EntityNotFoundException(ednee);
        }
    }
}
