package teammates.ui.webapi.action;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.ui.webapi.output.InstructorData;
import teammates.ui.webapi.output.InstructorsData;

/**
 * Searches for instructors.
 */
public class SearchInstructorsAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        // Only admins can search for instructors
        if (!userInfo.isAdmin) {
            throw new UnauthorizedAccessException("Admin privilege is required to access this resource.");
        }
    }

    private String getInstituteFromGoogleId(String googleId) {
        if (googleId != null) {
            AccountAttributes account = logic.getAccount(googleId);
            if (account != null) {
                return StringHelper.isEmpty(account.institute) ? "None" : account.institute;
            }
        }
        return null;
    }

    @Override
    public ActionResult execute() {
        String searchKey = getNonNullRequestParamValue(Const.ParamsNames.ADMIN_SEARCH_KEY);
        List<InstructorAttributes> instructors = logic.searchInstructorsInWholeSystem(searchKey).instructorList;

        List<InstructorData> instructorDataList = new ArrayList<>();
        for (InstructorAttributes instructor : instructors) {
            InstructorData instructorData = new InstructorData(instructor);
            instructorData.addAdditionalInformationForAdminSearch(
                    StringHelper.encrypt(instructor.getKey()),
                    getInstituteFromGoogleId(instructor.getGoogleId()),
                    instructor.getGoogleId());

            instructorDataList.add(instructorData);
        }

        InstructorsData instructorsData = new InstructorsData();
        instructorsData.setInstructors(instructorDataList);

        return new JsonResult(instructorsData);
    }
}
