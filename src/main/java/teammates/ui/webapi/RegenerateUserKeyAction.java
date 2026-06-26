package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.ui.exception.EntityNotFoundException;

/**
 * Regenerates the key for a given user in a course. This will also resend the course registration
 * and feedback session links to the affected user, as any previously sent links will no longer work.
 */
public class RegenerateUserKeyAction extends AdminOnlyAction {

    /** Message indicating that the key regeneration was successful. */
    public static final String SUCCESSFUL_REGENERATION =
            "User's key for this course has been successfully regenerated,";

    /** Message indicating that the key regeneration was successful, and corresponding email was sent. */
    public static final String SUCCESSFUL_REGENERATION_WITH_EMAIL_QUEUED =
            SUCCESSFUL_REGENERATION + " and the email has been sent.";

    @Override
    public JsonResult execute() {
        UUID userId = getUuidRequestParamValue(Const.ParamsNames.USER_ID);

        try {
            logic.regenerateUserLinksAndEnqueueSummaryEmail(userId);
        } catch (EntityDoesNotExistException ex) {
            throw new EntityNotFoundException(ex);
        }

        return new JsonResult(SUCCESSFUL_REGENERATION_WITH_EMAIL_QUEUED);
    }
}
