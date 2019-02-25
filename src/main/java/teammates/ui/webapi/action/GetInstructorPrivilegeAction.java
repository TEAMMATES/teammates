package teammates.ui.webapi.action;

import java.util.HashMap;
import java.util.Map;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.webapi.output.InstructorPrivilegeData;

/**
 * Get the instructor privilege.
 */
public class GetInstructorPrivilegeAction extends Action {

    static final Map<String, InstructorPrivileges> INSTRUCTOR_PRIVILEGES = new HashMap<>();

    static {
        InstructorPrivileges coOwnerPrivileges = new InstructorPrivileges(
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        InstructorPrivileges managerPrivileges = new InstructorPrivileges(
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_MANAGER);
        InstructorPrivileges observerPrivileges = new InstructorPrivileges(
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_OBSERVER);
        InstructorPrivileges tutorPrivileges = new InstructorPrivileges(
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_TUTOR);
        InstructorPrivileges customPrivileges = new InstructorPrivileges(
                Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM);

        INSTRUCTOR_PRIVILEGES.put("coowner", coOwnerPrivileges);
        INSTRUCTOR_PRIVILEGES.put("manager", managerPrivileges);
        INSTRUCTOR_PRIVILEGES.put("observer", observerPrivileges);
        INSTRUCTOR_PRIVILEGES.put("tutor", tutorPrivileges);
        INSTRUCTOR_PRIVILEGES.put("custom", customPrivileges);
    }

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.getId());
        if (instructor == null) {
            throw new UnauthorizedAccessException("Not instructor of the course");
        }
    }

    @Override
    public ActionResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String sectionName = getRequestParamValue(Const.ParamsNames.SECTION_NAME);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        String isAllPrivilegesNeeded = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PRIVILEGES_IS_ALL_NEEDED);
        String isMapNeeded = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_IS_PRIVILEGE_MAP_NEEDED);
        boolean isAllInstructorPrivilegesNeeded = isAllPrivilegesNeeded != null
                && Boolean.parseBoolean(isAllPrivilegesNeeded);
        boolean isPrivilegeMapNeeded = isMapNeeded != null && Boolean.parseBoolean(isMapNeeded);

        InstructorPrivilegeData response = new InstructorPrivilegeData();

        if (isPrivilegeMapNeeded) {
            response.setInstructorPrivilegesMap(INSTRUCTOR_PRIVILEGES);
        } else {
            InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.getId());

            if (isAllInstructorPrivilegesNeeded) {
                response.setPrivilegesBundle(instructor.privileges);
            } else if (sectionName == null) {
                response.setPrivilegesCourseLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE,
                        instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));

                response.setPrivilegesCourseLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION,
                        instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));

                response.setPrivilegesCourseLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT,
                        instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));

                response.setPrivilegesCourseLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS,
                        instructor.isAllowedForPrivilege(
                                Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));

                response.setPrivilegesSectionLevel(feedbackSessionName,
                        Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS,
                        instructor.isAllowedForPrivilegeAnySection(
                                feedbackSessionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));
            } else {
                boolean isAllowedToViewStudentInSection = instructor.isAllowedForPrivilege(sectionName,
                        Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS);
                // modify student is a privilege in course level.
                boolean isAllowedToModifyStudent = instructor.isAllowedForPrivilege(
                        Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);
                response.setPrivilegesSectionLevel(sectionName,
                        Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS,
                        isAllowedToViewStudentInSection);
                response.setPrivilegesCourseLevel(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT,
                        isAllowedToModifyStudent);
            }
        }
        return new JsonResult(response);
    }
}
