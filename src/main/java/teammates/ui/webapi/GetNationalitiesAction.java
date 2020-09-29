package teammates.ui.webapi;

import teammates.common.util.NationalityHelper;
import teammates.ui.output.NationalitiesData;

/**
 * Action: Get a list of valid nationalities.
 */
class GetNationalitiesAction extends Action {
    @Override
    AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() {
        // Anyone can fetch the nationality data
    }

    @Override
    JsonResult execute() {
        NationalitiesData nationalities = new NationalitiesData(NationalityHelper.getNationalities());
        return new JsonResult(nationalities);
    }
}
