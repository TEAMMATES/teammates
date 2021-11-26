package teammates.ui.webapi;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.SearchServiceException;
import teammates.common.util.Const.ParamsNames;

/**
 * Task queue worker action: performs student search indexing.
 */
public class StudentSearchIndexingWorkerAction extends AdminOnlyAction {

    @Override
    public ActionResult execute() {
        String courseId = getNonNullRequestParamValue(ParamsNames.COURSE_ID);
        String email = getNonNullRequestParamValue(ParamsNames.STUDENT_EMAIL);

        StudentAttributes student = logic.getStudentForEmail(courseId, email);
        try {
            logic.putStudentDocument(student);
        } catch (SearchServiceException e) {
            // Set an arbitrary retry code outside of the range 200-299 to trigger automatic retry
            return new JsonResult("Failure", HttpStatus.SC_BAD_GATEWAY);
        }

        return new JsonResult("Successful");
    }
}
