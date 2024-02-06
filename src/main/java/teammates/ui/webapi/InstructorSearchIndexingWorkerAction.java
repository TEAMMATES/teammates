package teammates.ui.webapi;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.SearchServiceException;
import teammates.common.util.Const.ParamsNames;
import teammates.storage.sqlentity.Instructor;

/**
 * Task queue worker action: performs instructor search indexing.
 */
public class InstructorSearchIndexingWorkerAction extends AdminOnlyAction {

    @Override
    public ActionResult execute() {
        String courseId = getNonNullRequestParamValue(ParamsNames.COURSE_ID);
        String email = getNonNullRequestParamValue(ParamsNames.INSTRUCTOR_EMAIL);

        if (isCourseMigrated(courseId)) {
            return executeWithSql(courseId, email);
        } else {
            return executeWithDataStore(courseId, email);
        }
    }

    private JsonResult executeWithSql(String courseId, String email) {
        Instructor instructor = sqlLogic.getInstructorForEmail(courseId, email);
        try {
            sqlLogic.putInstructorDocument(instructor);
        } catch (SearchServiceException e) {
            // Set an arbitrary retry code outside the range 200-299 to trigger automatic retry
            return new JsonResult("Failure", HttpStatus.SC_BAD_GATEWAY);
        }

        return new JsonResult("Successful");
    }

    private JsonResult executeWithDataStore(String courseId, String email) {
        InstructorAttributes instructor = logic.getInstructorForEmail(courseId, email);
        try {
            logic.putInstructorDocument(instructor);
        } catch (SearchServiceException e) {
            // Set an arbitrary retry code outside of the range 200-299 to trigger automatic retry
            return new JsonResult("Failure", HttpStatus.SC_BAD_GATEWAY);
        }

        return new JsonResult("Successful");
    }
}
