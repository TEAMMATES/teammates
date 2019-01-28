package teammates.ui.webapi.action;

import java.util.List;

import teammates.common.util.NationalityHelper;
import teammates.ui.webapi.output.ApiOutput;

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
        NationalityData nationalities = new NationalityData(NationalityHelper.getNationalities());
        return new JsonResult(nationalities);
    }

    /**
     * Output format for {@link GetNationalitiesAction}.
     */
    public static class NationalityData extends ApiOutput {
        private List<String> nationalities;

        public NationalityData(List<String> nationalities) {
            this.nationalities = nationalities;
        }

        public List<String> getNationalities() {
            return nationalities;
        }
    }
}
