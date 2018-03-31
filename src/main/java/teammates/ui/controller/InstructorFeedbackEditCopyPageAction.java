package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.ui.pagedata.InstructorFeedbackEditCopyPageData;

public class InstructorFeedbackEditCopyPageAction extends Action {

    @Override
    protected ActionResult execute() {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);

        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);

        List<InstructorAttributes> instructors = logic.getInstructorsForGoogleId(account.googleId);
        Assumption.assertNotNull(instructors);

        List<CourseAttributes> allCourses = logic.getCoursesForInstructor(account.googleId);

        List<CourseAttributes> coursesToAddToData = new ArrayList<>();

        // Only add courses to data if the course is not archived and instructor has sufficient permissions
        for (CourseAttributes course : allCourses) {
            InstructorAttributes instructor = logic.getInstructorForGoogleId(course.getId(), account.googleId);

            boolean isAllowedToMakeSession =
                    instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);

            if (!instructor.isArchived && isAllowedToMakeSession) {
                coursesToAddToData.add(course);
            }
        }

        CourseAttributes.sortByCreatedDate(coursesToAddToData);

        InstructorFeedbackEditCopyPageData data = new InstructorFeedbackEditCopyPageData(account, sessionToken,
                coursesToAddToData, courseId, feedbackSessionName);

        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_COPY_MODAL, data);
    }

}
