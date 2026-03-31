package teammates.ui.webapi;

import java.util.List;
import java.util.stream.Collectors;

import teammates.common.util.Templates.EmailTemplates;

/**
 * Registry of admin-configurable email templates.
 *
 * <p>Each constant bundles the template's default subject, default body, and the
 * set of body placeholders that must be present before a custom template can be saved.
 *
 * <p>Adding a new configurable template requires a single entry here; the action
 * layer derives all registry data (allowed keys, fallback values, required placeholders)
 * from this enum, eliminating parallel data structures.
 */
public enum ConfigurableEmailTemplate {

    // CHECKSTYLE.OFF:JavadocVariable enum names are self-documenting

    NEW_INSTRUCTOR_ACCOUNT_WELCOME(
            "TEAMMATES: Welcome to TEAMMATES! ${userName}",
            EmailTemplates.NEW_INSTRUCTOR_ACCOUNT_WELCOME,
            List.of("${joinUrl}"));

    // CHECKSTYLE.ON:JavadocVariable

    private final String defaultSubject;
    private final String defaultBody;
    private final List<String> requiredBodyPlaceholders;

    ConfigurableEmailTemplate(String defaultSubject, String defaultBody,
            List<String> requiredBodyPlaceholders) {
        this.defaultSubject = defaultSubject;
        this.defaultBody = defaultBody;
        this.requiredBodyPlaceholders = requiredBodyPlaceholders;
    }

    /**
     * Gets the default subject for this email template.
     */
    public String getDefaultSubject() {
        return defaultSubject;
    }

    /**
     * Gets the default body for this email template.
     */
    public String getDefaultBody() {
        return defaultBody;
    }

    /**
     * Returns the required body placeholders that are absent from {@code body},
     * or an empty list if all required placeholders are present.
     */
    public List<String> getMissingPlaceholders(String body) {
        return requiredBodyPlaceholders.stream()
                .filter(placeholder -> !body.contains(placeholder))
                .collect(Collectors.toList());
    }

}
