package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.ui.pagedata.InstructorCourseInstructorCopyPageData;

public class InstructorCourseInstructorCopyPageAction extends Action{

    @Override
    protected ActionResult execute() {
        String thisCourseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, thisCourseId);

        InstructorAttributes thisInstructor = logic.getInstructorForGoogleId(thisCourseId, account.googleId);

        gateKeeper.verifyAccessible(thisInstructor, logic.getCourse(thisCourseId),
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR);

        List<InstructorAttributes> copiableInstructors = new ArrayList();

        List<CourseAttributes> coursesOfThisInstructor = logic.getCoursesForInstructor(account.googleId);

        for (CourseAttributes course: coursesOfThisInstructor) {
            String courseId = course.getId();
            if (!courseId.equals(thisCourseId)) {
                List<InstructorAttributes> instructors = logic.getInstructorsForCourse(courseId);
                instructors.remove(thisInstructor);
                copiableInstructors.addAll(instructors);
            }
        }

        InstructorCourseInstructorCopyPageData data =
                new InstructorCourseInstructorCopyPageData(account, sessionToken, copiableInstructors);

        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_COURSE_INSTRUCTOR_COPY_MODAL, data);
    }
}
