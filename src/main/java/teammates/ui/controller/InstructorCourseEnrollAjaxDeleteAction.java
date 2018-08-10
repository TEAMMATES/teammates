package teammates.ui.controller;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.ui.pagedata.InstructorCourseEnrollPageData;

import java.util.ArrayList;

public class InstructorCourseEnrollAjaxDeleteAction extends Action {

    private static ArrayList<String> deletedStudents;

    @Override
    public ActionResult execute() {

        deletedStudents = new ArrayList<>();
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);

        String deletedStudentsInfo = getRequestParamValue(Const.ParamsNames.STUDENTS_DELETE_SELECTED_INFO);
        Assumption.assertPostParamNotNull(Const.ParamsNames.STUDENTS_DELETE_SELECTED_INFO, deletedStudentsInfo);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        gateKeeper.verifyAccessible(
                instructor, logic.getCourse(courseId), Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);

        InstructorCourseEnrollPageData data = new InstructorCourseEnrollPageData(account, sessionToken);

        String[] deletedStudentsArray = deletedStudentsInfo.split(System.lineSeparator());
        for (String deletedStudentEmail : deletedStudentsArray) {
            logic.deleteStudent(courseId, deletedStudentEmail);
            deletedStudents.add(deletedStudentEmail);
            statusToAdmin += "Student <span class=\"bold\">" + deletedStudentEmail
                    + "</span> in Course <span class=\"bold\">[" + courseId + "]</span> deleted.";
        }
        data.setDeletedStudents(deletedStudents);
        return createAjaxResult(data);
    }
}
