package teammates.ui.output;

import java.util.ArrayList;
import java.util.List;

/**
 * The API output format for retrieving list of action classes.
 */
public class ActionClasses extends ApiOutput {
    private List<String> actionClasses = new ArrayList<>();

    public ActionClasses(List<String> actionClasses) {
        this.actionClasses = actionClasses;
    }

    public List<String> getActionClasses() {
        return actionClasses;
    }
}
