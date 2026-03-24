package teammates.ui.output;

import java.util.List;

/**
 * Output format for the list of configurable email template keys.
 */
public class EmailTemplatesData extends ApiOutput {
    private final List<String> templateKeys;

    public EmailTemplatesData(List<String> templateKeys) {
        this.templateKeys = templateKeys;
    }

    public List<String> getTemplateKeys() {
        return templateKeys;
    }
}
