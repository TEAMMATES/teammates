package teammates.ui.webapi;

import teammates.common.util.NationalityHelper;
import teammates.ui.output.NationalitiesData;

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
    public JsonResult execute() {
        NationalitiesData nationalities = new NationalitiesData(NationalityHelper.getNationalities());
        return new JsonResult(nationalities);
    }
}
