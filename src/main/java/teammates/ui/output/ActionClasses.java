package teammates.ui.output;

import java.util.List;

/**
 * The API output format for retrieving list of action classes.
 */
public class ActionClasses extends ApiOutput {
    private final List<String> actionClasses;

    public ActionClasses(List<String> actionClasses) {
        this.actionClasses = actionClasses;
    }

    public List<String> getActionClasses() {
        return actionClasses;
    }
}
