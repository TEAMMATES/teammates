package teammates.ui.webapi;

import teammates.common.datatransfer.CourseJoinKeyAccessResult;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.output.CourseJoinKeyAccessData;
import teammates.ui.request.CourseJoinKeyRequest;

/**
 * Preflight access check for course join links.
 */
public class CourseJoinKeyAccessAction extends PublicAction {

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException {
        CourseJoinKeyRequest requestBody = getAndValidateRequestBody(CourseJoinKeyRequest.class);
        CourseJoinKeyAccessResult result = logic.getCourseJoinKeyAccessResult(
                requestContext.getAccount(), requestBody.getKey());
        return new JsonResult(new CourseJoinKeyAccessData(result.decision(), result.message()));
    }
}
