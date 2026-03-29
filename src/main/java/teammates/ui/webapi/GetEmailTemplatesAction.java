package teammates.ui.webapi;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import teammates.ui.output.EmailTemplatesData;

/**
 * Gets the list of email template keys that are configurable by an admin.
 */
public class GetEmailTemplatesAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        List<String> keys = Arrays.stream(ConfigurableEmailTemplate.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        return new JsonResult(new EmailTemplatesData(keys));
    }
}
