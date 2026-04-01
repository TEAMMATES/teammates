package teammates.ui.webapi;

import java.util.List;

import teammates.ui.output.EmailTemplatesData;

/**
 * Gets the list of email template keys that are configurable by an admin.
 */
public class GetEmailTemplatesAction extends AdminOnlyAction {

    /**
     * The set of template keys that admins are permitted to customise.
     * Add new keys here as more templates are migrated to the database.
     */
    static final List<String> CONFIGURABLE_TEMPLATE_KEYS =
            List.of("NEW_INSTRUCTOR_ACCOUNT_WELCOME");

    @Override
    public JsonResult execute() {
        return new JsonResult(new EmailTemplatesData(CONFIGURABLE_TEMPLATE_KEYS));
    }
}
