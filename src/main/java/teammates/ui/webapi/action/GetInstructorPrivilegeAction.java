package teammates.ui.webapi.action;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.webapi.output.InstructorPrivilegeData;

/**
 * Get the instructor privilege.
 */
public class GetInstructorPrivilegeAction extends Action {

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
        String instructorRole = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_ROLE_NAME);

        if (instructorRole != null) {
            // fetching privilege for a particular instructor role.
            InstructorPrivilegeData rolePrivilege = Const.InstructorPrivilegesMap
                    .INSTRUCTOR_PRIVILEGES.getOrDefault(instructorRole, null);
            if (rolePrivilege == null) {
                return new JsonResult("Invalid instructor role.", HttpStatus.SC_BAD_REQUEST);
            }
            return new JsonResult(rolePrivilege);
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        // is getting privilege of another instructor
        String instructorOfInterest = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_ID);

        InstructorAttributes instructor;
        if (instructorOfInterest == null) {
            instructor = logic.getInstructorForGoogleId(courseId, userInfo.getId());
        } else {
            instructor = logic.getInstructorForGoogleId(courseId, instructorOfInterest);
            if (instructor == null) {
                return new JsonResult("Instructor does not exist.", HttpStatus.SC_NOT_FOUND);
            }
        }

        String sectionName = getRequestParamValue(Const.ParamsNames.SECTION_NAME);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        InstructorPrivilegeData response = new InstructorPrivilegeData();

        // course level privileges.
        response.setCanModifyCourse(
                instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));
        response.setCanModifySession(
                instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));
        response.setCanModifyStudent(
                instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
        response.setCanModifyInstructor(
                instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR));

        if (sectionName == null) {
            response.setCanViewStudentInSections(
                    instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS));
            if (feedbackSessionName == null) {
                response.setCanSubmitSessionInSections(
                        instructor.isAllowedForPrivilege(
                                Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));
                response.setCanViewSessionInSections(
                        instructor.isAllowedForPrivilege(
                                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
                response.setCanModifySessionCommentsInSections(
                        instructor.isAllowedForPrivilege(
                                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS));
            } else {
                response.setCanSubmitSessionInSections(
                        instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS)
                                || instructor.isAllowedForPrivilegeAnySection(
                                feedbackSessionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));
                response.setCanViewSessionInSections(
                        instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS)
                                || instructor.isAllowedForPrivilegeAnySection(
                                feedbackSessionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
                response.setCanModifySessionCommentsInSections(
                        instructor.isAllowedForPrivilege(
                                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS)
                                || instructor.isAllowedForPrivilegeAnySection(feedbackSessionName,
                                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS));
            }
        } else {
            response.setCanViewStudentInSections(
                    instructor.isAllowedForPrivilege(sectionName,
                            Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS));

            if (feedbackSessionName == null) {
                response.setCanSubmitSessionInSections(instructor.isAllowedForPrivilege(
                        sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));
                response.setCanViewSessionInSections(instructor.isAllowedForPrivilege(
                        sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
                response.setCanModifySessionCommentsInSections(instructor.isAllowedForPrivilege(
                        sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS));
            } else {
                response.setCanSubmitSessionInSections(instructor.isAllowedForPrivilege(sectionName,
                        feedbackSessionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));
                response.setCanViewSessionInSections(instructor.isAllowedForPrivilege(sectionName,
                        feedbackSessionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));
                response.setCanModifySessionCommentsInSections(instructor.isAllowedForPrivilege(sectionName,
                        feedbackSessionName,
                        Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS));
            }
        }
        return new JsonResult(response);
    }

}
