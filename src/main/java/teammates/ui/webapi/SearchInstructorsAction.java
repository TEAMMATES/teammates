package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.SearchServiceException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.ui.output.InstructorData;
import teammates.ui.output.InstructorsData;

/**
 * Searches for instructors.
 */
class SearchInstructorsAction extends AdminOnlyAction {

    private String getInstituteFromGoogleId(String googleId) {
        if (googleId != null) {
            AccountAttributes account = logic.getAccount(googleId);
            if (account != null) {
                return StringHelper.isEmpty(account.getInstitute()) ? "None" : account.getInstitute();
            }
        }
        return null;
    }

    @Override
    public JsonResult execute() {
        String searchKey = getNonNullRequestParamValue(Const.ParamsNames.SEARCH_KEY);
        List<InstructorAttributes> instructors;
        try {
            instructors = logic.searchInstructorsInWholeSystem(searchKey);
        } catch (SearchServiceException e) {
            return new JsonResult(e.getMessage(), e.getStatusCode());
        }

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
