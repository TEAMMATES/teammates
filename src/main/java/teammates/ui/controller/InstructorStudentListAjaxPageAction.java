package teammates.ui.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.SectionDetailsBundle;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.ui.pagedata.InstructorStudentListAjaxPageData;

public class InstructorStudentListAjaxPageAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {

        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull("null course id", courseId);

        String courseIndexString = getRequestParamValue(Const.ParamsNames.COURSE_INDEX);
        Assumption.assertNotNull("null course index", courseIndexString);

        gateKeeper.verifyInstructorPrivileges(account);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        CourseAttributes course = logic.getCourse(courseId);

        gateKeeper.verifyAccessible(instructor, course);

        List<SectionDetailsBundle> courseSectionDetails = logic.getSectionsForCourse(courseId);
        int courseIndex = Integer.parseInt(courseIndexString);
        boolean hasSection = logic.hasIndicatedSections(courseId);

        Map<String, String> emailPhotoUrlMapping = new HashMap<String, String>();
        Map<String, Map<String, Boolean>> sectionPrivileges = new HashMap<>();
        for (SectionDetailsBundle sectionDetails : courseSectionDetails) {
            for (TeamDetailsBundle teamDetails : sectionDetails.teams) {
                for (StudentAttributes student : teamDetails.students) {
                    String studentPhotoUrl = student.getPublicProfilePictureUrl();
                    studentPhotoUrl = Url.addParamToUrl(studentPhotoUrl,
                                                    Const.ParamsNames.USER_ID, account.googleId);
                    emailPhotoUrlMapping.put(student.email, studentPhotoUrl);
                }
            }
            Map<String, Boolean> sectionPrivilege = new HashMap<String, Boolean>();
            sectionPrivilege.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS,
                                 instructor.isAllowedForPrivilege(
                                         sectionDetails.name,
                                         Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS));
            sectionPrivilege.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT,
                                 instructor.isAllowedForPrivilege(sectionDetails.name,
                                                                  Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
            sectionPrivilege.put(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS,
                                 instructor.isAllowedForPrivilege(
                                         sectionDetails.name,
                                         Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS));
            sectionPrivileges.put(sectionDetails.name, sectionPrivilege);
        }

        InstructorStudentListAjaxPageData data = new InstructorStudentListAjaxPageData(account, courseId, courseIndex,
                                                                                       hasSection, courseSectionDetails,
                                                                                       sectionPrivileges,
                                                                                       emailPhotoUrlMapping);

        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_STUDENT_LIST_AJAX, data);
    }

}
