package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.SearchServiceException;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.InstructorData;
import teammates.ui.output.InstructorsData;

/**
 * Searches for instructors.
 */
public class SearchInstructorsAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        // Search for sql db
        String searchKey = getNonNullRequestParamValue(Const.ParamsNames.SEARCH_KEY);
        List<Instructor> instructors;
        try {
            instructors = sqlLogic.searchInstructorsInWholeSystem(searchKey);
        } catch (SearchServiceException e) {
            return new JsonResult(e.getMessage(), e.getStatusCode());
        }

        // Search for datastore
        List<InstructorAttributes> instructorsDatastore;
        try {
            instructorsDatastore = logic.searchInstructorsInWholeSystem(searchKey);
        } catch (SearchServiceException e) {
            return new JsonResult(e.getMessage(), e.getStatusCode());
        }

        List<InstructorData> instructorDataList = new ArrayList<>();

        // Add instructors from sql db
        for (Instructor instructor : instructors) {
            InstructorData instructorData = new InstructorData(instructor);
            instructorData.addAdditionalInformationForAdminSearch(
                    instructor.getRegKey(),
                    sqlLogic.getCourseInstitute(instructor.getCourseId()),
                    instructor.getGoogleId());

            instructorDataList.add(instructorData);
        }

        // Add instructors from datastore
        for (InstructorAttributes instructor : instructorsDatastore) {

            InstructorData instructorData = new InstructorData(instructor);

            if (isCourseMigrated(instructorData.getCourseId())) {
                continue;
            }

            instructorData.addAdditionalInformationForAdminSearch(
                    instructor.getKey(),
                    logic.getCourseInstitute(instructor.getCourseId()),
                    instructor.getGoogleId());

            instructorDataList.add(instructorData);
        }

        InstructorsData instructorsData = new InstructorsData();
        instructorsData.setInstructors(instructorDataList);

        return new JsonResult(instructorsData);
    }
}
