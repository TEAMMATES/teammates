package teammates.ui.webapi.action;

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

    private void addAdditionalSearchFields(InstructorsData instructorsData, List<InstructorAttributes> instructors) {
        instructorsData.getInstructors()
                .forEach((InstructorData instructor) -> {
                    if (instructor.getGoogleId() != null) {
                        AccountAttributes account = logic.getAccount(instructor.getGoogleId());
                        if (account != null) {
                            String institute = StringHelper.isEmpty(account.institute) ? "None" : account.institute;
                            instructor.setInstitute(institute);
                        }
                        instructor.setKey(instructors);
                    }
                    // Hide information
                    instructor.hideInformationForSearch();
                });
    }

    @Override
    public ActionResult execute() {
        String searchKey = getNonNullRequestParamValue(Const.ParamsNames.ADMIN_SEARCH_KEY);
        List<InstructorAttributes> instructors = logic.searchInstructorsInWholeSystem(searchKey).instructorList;
        InstructorsData instructorsData = new InstructorsData(instructors);
        instructorsData.getInstructors().forEach(InstructorData::hideInformationForSearch);
        this.addAdditionalSearchFields(instructorsData, instructors);
        // Set additional fields for search
        return new JsonResult(instructorsData);
    }
}
