package teammates.ui.webapi.action;

import java.util.List;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.webapi.output.FeedbackSessionsData;

/**
 * Get a list of feedback sessions.
 */
public class GetFeedbackSessionsAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        if (courseId == null) {
            gateKeeper.verifyInstructorPrivileges(logic.getAccount(userInfo.getId()));
        } else {
            CourseAttributes courseAttributes = logic.getCourse(courseId);
            gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(courseId, userInfo.getId()), courseAttributes);
        }
    }

    @Override
    public ActionResult execute() {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);

        List<FeedbackSessionAttributes> feedbackSessionAttributes;
        if (courseId == null) {
            boolean isInRecycleBin = getBooleanRequestParamValue(Const.ParamsNames.IS_IN_RECYCLE_BIN);

            List<InstructorAttributes> instructors = logic.getInstructorsForGoogleId(userInfo.getId(), true);

            if (isInRecycleBin) {
                feedbackSessionAttributes = logic.getSoftDeletedFeedbackSessionsListForInstructors(instructors);
            } else {
                feedbackSessionAttributes = logic.getFeedbackSessionsListForInstructor(instructors);
            }
        } else {
            feedbackSessionAttributes = logic.getFeedbackSessionsForCourse(courseId);
        }

        return new JsonResult(new FeedbackSessionsData(feedbackSessionAttributes));
    }

}
