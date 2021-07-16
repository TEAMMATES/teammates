package teammates.ui.webapi;

import java.util.List;
import java.util.stream.Collectors;

import teammates.ui.output.ActionClasses;

/**
 * Retrieves a list of action class names.
 */
class GetActionClassesAction extends AdminOnlyAction {
    @Override
    ActionResult execute() {
        List<String> actionClasses = ActionFactory.ACTION_MAPPINGS.values().stream()
                .flatMap(map -> map.values().stream().map(Class::getSimpleName))
                .collect(Collectors.toList());
        return new JsonResult(new ActionClasses(actionClasses));
    }
}
