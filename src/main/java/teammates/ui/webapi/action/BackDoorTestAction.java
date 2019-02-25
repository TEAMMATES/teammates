package teammates.ui.webapi.action;

import teammates.common.util.Const;

public class BackDoorTestAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.ALL_ACCESS;
    }

    @Override
    public void checkSpecificAccessControl() { }

    @Override
    public JsonResult execute() {
        // operation obtains the action called by the respective class
        String operation = getNonNullRequestParamValue(Const.ParamsNames.BACKDOOR_OPERATION);

        switch(operation) {
            case ("GetFeedbackSessionAction"):
                // TODO: how do I call the function to execute this?
                GetFeedbackSessionAction tempClass = new GetFeedbackSessionAction();
                tempClass.execute();
                break;
        }

        return new JsonResult("Test output");
    }
}
