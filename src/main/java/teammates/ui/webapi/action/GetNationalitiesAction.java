package teammates.ui.webapi.action;

import teammates.common.util.NationalityHelper;
import teammates.ui.webapi.output.NationalitiesData;

/**
 * Action: Get a list of valid nationalities.
 */
public class GetNationalitiesAction extends Action {
    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    public void checkSpecificAccessControl() {
        // Anyone can fetch the nationality data
    }

    @Override
    public ActionResult execute() {
        NationalitiesData nationalities = new NationalitiesData(NationalityHelper.getNationalities());
        return new JsonResult(nationalities);
    }
}
