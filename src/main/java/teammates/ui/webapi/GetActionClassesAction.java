package teammates.ui.webapi;

import java.util.List;
import java.util.stream.Collectors;

import teammates.ui.output.ActionClasses;

/**
 * Retrieves a list of action class names.
 */
public class GetActionClassesAction extends Action {
    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (!userInfo.isMaintainer && !userInfo.isAdmin) {
            throw new UnauthorizedAccessException("Only Maintainers or Admin are allowed to access this resource.");
        }
    }

    @Override
    public JsonResult execute() {
        List<String> actionClasses = ActionFactory.ACTION_MAPPINGS.values().stream()
                .flatMap(map -> map.values().stream().map(Class::getSimpleName))
                .collect(Collectors.toList());
        return new JsonResult(new ActionClasses(actionClasses));
    }
}
