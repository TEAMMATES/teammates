package teammates.ui.webapi;

import java.util.List;
import java.util.Map;

import teammates.common.util.Templates;
import teammates.ui.output.EmailTemplatesData;

/**
 * Gets the list of email template keys that are configurable by an admin.
 */
public class GetEmailTemplatesAction extends AdminOnlyAction {

    /**
     * The set of template keys that admins are permitted to customise.
     * Add new keys here as more templates are migrated to the database.
     */
    public static final List<String> CONFIGURABLE_TEMPLATE_KEYS =
            List.of("NEW_INSTRUCTOR_ACCOUNT_WELCOME");

    /**
     * Fallback subjects used when no custom template has been saved for a key.
     * These mirror the subjects hardcoded in {@code SqlEmailGenerator}.
     */
    public static final Map<String, String> DEFAULT_SUBJECTS = Map.of(
            "NEW_INSTRUCTOR_ACCOUNT_WELCOME", "Welcome to TEAMMATES!"
    );

    /**
     * Fallback bodies used when no custom template has been saved for a key.
     * Each value is the content of the corresponding static HTML resource file.
     */
    public static final Map<String, String> DEFAULT_BODIES = Map.of(
            "NEW_INSTRUCTOR_ACCOUNT_WELCOME", Templates.EmailTemplates.NEW_INSTRUCTOR_ACCOUNT_WELCOME
    );

    @Override
    public JsonResult execute() {
        return new JsonResult(new EmailTemplatesData(CONFIGURABLE_TEMPLATE_KEYS));
    }
}
