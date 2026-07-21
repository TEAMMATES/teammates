package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackSessionQuery;
import teammates.common.util.Const;
import teammates.storage.entity.Instructor;
import teammates.ui.exception.InvalidHttpParameterException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.FeedbackSessionsData;

/**
 * Get a list of feedback sessions.
 */
public class GetFeedbackSessionsAction extends LoggedInAction {
    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (requestContext.isAdmin()) {
            return;
        }

        List<String> courseIds = getCourseIds();
        if (courseIds == null || courseIds.isEmpty()) {
            throw new InvalidHttpParameterException(Const.ParamsNames.COURSE_ID + " parameter is required");
        }

        for (String courseId : courseIds) {
            gateKeeper.verifyInstructorInCourse(requestContext, courseId);
        }
    }

    @Override
    public JsonResult execute() {
        List<String> courseIds = getCourseIds();
        boolean shouldIncludeInstructorDetails = shouldIncludeInstructorDetails(courseIds);
        Map<String, Instructor> courseIdToInstructor = shouldIncludeInstructorDetails
                ? getCourseIdToInstructor(courseIds) : Map.of();

        FeedbackSessionsData responseData = logic.getFeedbackSessionsData(
                getFeedbackSessionQuery(courseIds), courseIdToInstructor, shouldIncludeInstructorDetails);

        return new JsonResult(responseData);
    }

    private FeedbackSessionQuery getFeedbackSessionQuery(List<String> courseIds) {
        return new FeedbackSessionQuery(
                courseIds,
                getNullableBooleanRequestParamValue(Const.ParamsNames.IS_IN_RECYCLE_BIN).orElse(null));
    }

    private boolean shouldIncludeInstructorDetails(List<String> courseIds) {
        // Admin can retrieve all feedback sessions without instructor-specific details.
        return !requestContext.isAdmin() || courseIds != null;
    }

    private Map<String, Instructor> getCourseIdToInstructor(List<String> courseIds) {
        List<Instructor> instructors = getInstructorsForRequestedCourses(courseIds);
        Map<String, Instructor> courseIdToInstructor = new HashMap<>();
        instructors.forEach(instructor -> courseIdToInstructor.put(instructor.getCourseId(), instructor));
        return courseIdToInstructor;
    }

    private List<Instructor> getInstructorsForRequestedCourses(List<String> courseIds) {
        List<Instructor> instructors = new ArrayList<>();
        for (String courseId : courseIds) {
            Instructor instructor = getInstructorFromRequest(courseId);
            if (instructor != null) {
                instructors.add(instructor);
            }
        }
        return instructors;
    }

    private List<String> getCourseIds() {
        String[] courseIds = req.getParameterValues(Const.ParamsNames.COURSE_ID);
        return courseIds == null ? null : Arrays.asList(courseIds);
    }

}
