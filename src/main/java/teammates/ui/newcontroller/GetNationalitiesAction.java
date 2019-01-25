package teammates.ui.newcontroller;

import java.util.List;

import teammates.common.util.NationalityHelper;

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
    public static class NationalityData extends ActionResult.ActionOutput {
        private List<String> nationalities;

        public NationalityData(List<String> nationalities) {
            this.nationalities = nationalities;
        }

        public List<String> getNationalities() {
            return nationalities;
        }
    }
}
