package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.ui.pagedata.InstructorCourseInstructorCopyPageData;

/**
 * The action to fetch data for copy instructor modal when the copy instructor button is clicked.
 */
public class InstructorCourseInstructorCopyPageAction extends Action {

    @Override
    protected ActionResult execute() {
        String thisCourseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, thisCourseId);

        InstructorAttributes thisInstructor = logic.getInstructorForGoogleId(thisCourseId, account.googleId);

        gateKeeper.verifyAccessible(thisInstructor, logic.getCourse(thisCourseId),
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR);

        List<InstructorAttributes> copiableInstructors = new ArrayList();

        List<CourseAttributes> coursesOfThisInstructor = logic.getCoursesForInstructor(account.googleId);

        List<InstructorAttributes> existingInstructors = logic.getInstructorsForCourse(thisCourseId);

        // get all instructors who work together with thisInstructor in other courses and
        // prevent the existing instructors in this course from appearing in the modal
        for (CourseAttributes course : coursesOfThisInstructor) {
            String courseId = course.getId();
            if (!courseId.equals(thisCourseId)) {
                List<InstructorAttributes> instructors = logic.getInstructorsForCourse(courseId);
                instructors.forEach(instructor -> {
                    if (!instructorInThisCourse(instructor, existingInstructors)) {
                        copiableInstructors.add(instructor);
                    }
                });
            }
        }

        InstructorCourseInstructorCopyPageData data =
                new InstructorCourseInstructorCopyPageData(account, sessionToken, copiableInstructors);

        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_COURSE_INSTRUCTOR_COPY_MODAL, data);
    }

    private boolean instructorInThisCourse(
            InstructorAttributes instructorToBeChecked, List<InstructorAttributes> instructorsInTheCourse) {
        for (InstructorAttributes instructor : instructorsInTheCourse) {
            if (instructor.getEmail().equals(instructorToBeChecked.getEmail())) {
                return true;
            }
        }
        return false;
    }
}
