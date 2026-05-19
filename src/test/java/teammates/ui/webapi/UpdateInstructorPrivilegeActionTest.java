package teammates.ui.webapi;

import org.junit.jupiter.api.Assertions;
import static org.mockito.Mockito.when;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPermissionSet;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;
import teammates.common.util.Const.InstructorPermissions;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.output.InstructorPrivilegeData;
import teammates.ui.request.InstructorPrivilegeUpdateRequest;

/**
 * SUT: {@link UpdateInstructorPrivilegeAction}.
 */
public class UpdateInstructorPrivilegeActionTest extends BaseActionTest<UpdateInstructorPrivilegeAction> {

    String googleId = "user-googleId";
    String instructorEmail = "instructoremail@tm.tmt";
    String helperEmail = "helperemail@tm.tmt";

    Course course;
    Instructor instructor;
    Instructor helper;

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR_PRIVILEGE;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @BeforeMethod
    void setUp() {
        course = new Course("course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");

        InstructorPrivileges instructorPrivileges = new InstructorPrivileges();
        instructorPrivileges.updatePrivilege(InstructorPermissions.CAN_MODIFY_COURSE, true);
        instructorPrivileges.updatePrivilege(InstructorPermissions.CAN_MODIFY_SESSION, true);
        instructorPrivileges.updatePrivilege(InstructorPermissions.CAN_MODIFY_INSTRUCTOR, true);
        instructorPrivileges.updatePrivilege(InstructorPermissions.CAN_MODIFY_STUDENT, true);
        instructorPrivileges.updatePrivilege(InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS, true);
        instructorPrivileges.updatePrivilege(InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS, true);
        instructorPrivileges.updatePrivilege(InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS, true);
        instructorPrivileges.updatePrivilege(InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, true);
        instructor = new Instructor(course, "name", instructorEmail,
                false, "", null, instructorPrivileges);

        InstructorPrivileges helperPrivileges = new InstructorPrivileges();
        helper = new Instructor(course, "name", helperEmail,
                false, "", null, helperPrivileges);

        loginAsInstructor(googleId);
        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getInstructorByGoogleId(course.getId(), googleId)).thenReturn(instructor);
        when(mockLogic.getInstructorForEmail(course.getId(), instructorEmail)).thenReturn(instructor);
        when(mockLogic.getInstructorForEmail(course.getId(), helperEmail)).thenReturn(helper);
    }

    @Test
    protected void testExecute_validCourseLevelInput_shouldSucceed() {

        Assertions.assertFalse(helper.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_COURSE));
        Assertions.assertFalse(helper.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_SESSION));
        Assertions.assertFalse(helper.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
        Assertions.assertFalse(helper.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_STUDENT));
        Assertions.assertFalse(helper.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
        Assertions.assertFalse(helper.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        Assertions.assertFalse(helper.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        Assertions.assertFalse(helper.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, helperEmail,
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        InstructorPrivilegeUpdateRequest reqBody = new InstructorPrivilegeUpdateRequest();
        InstructorPrivileges newPrivileges = new InstructorPrivileges();
        newPrivileges.updatePrivilege(InstructorPermissions.CAN_MODIFY_COURSE, true);
        newPrivileges.updatePrivilege(InstructorPermissions.CAN_MODIFY_SESSION, true);
        newPrivileges.updatePrivilege(InstructorPermissions.CAN_MODIFY_INSTRUCTOR, true);
        newPrivileges.updatePrivilege(InstructorPermissions.CAN_MODIFY_STUDENT, true);
        newPrivileges.updatePrivilege(InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS, true);
        newPrivileges.updatePrivilege(InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS, true);
        newPrivileges.updatePrivilege(InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS, true);
        newPrivileges.updatePrivilege(InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, true);
        newPrivileges.updatePrivilege(InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS, true);
        reqBody.setPrivileges(newPrivileges);

        UpdateInstructorPrivilegeAction action = getAction(reqBody, submissionParams);
        InstructorPrivilegeData actionOutput = (InstructorPrivilegeData) getJsonResult(action).getOutput();

        InstructorPermissionSet courseLevelPrivilege = actionOutput.getPrivileges().getCourseLevelPrivileges();
        Assertions.assertTrue(courseLevelPrivilege.isCanModifyCourse());
        Assertions.assertTrue(courseLevelPrivilege.isCanModifySession());
        Assertions.assertTrue(courseLevelPrivilege.isCanModifyStudent());
        Assertions.assertTrue(courseLevelPrivilege.isCanModifyInstructor());
        Assertions.assertTrue(courseLevelPrivilege.isCanViewStudentInSections());
        Assertions.assertTrue(courseLevelPrivilege.isCanSubmitSessionInSections());
        Assertions.assertTrue(courseLevelPrivilege.isCanViewSessionInSections());
        Assertions.assertTrue(courseLevelPrivilege.isCanModifySessionCommentsInSections());

        // verify the privilege has indeed been updated
        Assertions.assertTrue(helper.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_COURSE));
        Assertions.assertTrue(helper.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_SESSION));
        Assertions.assertTrue(helper.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
        Assertions.assertTrue(helper.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_STUDENT));
        Assertions.assertTrue(helper.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
        Assertions.assertTrue(helper.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        Assertions.assertTrue(helper.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        Assertions.assertTrue(helper.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));
    }

    @Test
    protected void testExecute_validSectionLevelInput_shouldSucceed() {

        Assertions.assertFalse(helper.getPrivileges().isAllowedForPrivilege(
                "TUT1", Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
        Assertions.assertFalse(helper.getPrivileges().isAllowedForPrivilege(
                "TUT1", Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        Assertions.assertFalse(helper.getPrivileges().isAllowedForPrivilege(
                "TUT1", Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        Assertions.assertFalse(helper.getPrivileges().isAllowedForPrivilege(
                "TUT1", Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, helperEmail,
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        InstructorPrivilegeUpdateRequest reqBody = new InstructorPrivilegeUpdateRequest();
        InstructorPrivileges privilege = new InstructorPrivileges();
        privilege.updatePrivilege("TUT1", Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS, true);
        privilege.updatePrivilege("TUT1", Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS, true);
        privilege.updatePrivilege("TUT1", Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS, true);
        privilege.updatePrivilege("TUT1", Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, true);
        reqBody.setPrivileges(privilege);

        UpdateInstructorPrivilegeAction action = getAction(reqBody, submissionParams);
        InstructorPrivilegeData actionOutput = (InstructorPrivilegeData) getJsonResult(action).getOutput();

        InstructorPermissionSet sectionLevelPrivilege = actionOutput.getPrivileges().getSectionLevelPrivileges().get("TUT1");
        Assertions.assertFalse(sectionLevelPrivilege.isCanModifyCourse());
        Assertions.assertFalse(sectionLevelPrivilege.isCanModifySession());
        Assertions.assertFalse(sectionLevelPrivilege.isCanModifyStudent());
        Assertions.assertFalse(sectionLevelPrivilege.isCanModifyInstructor());
        Assertions.assertTrue(sectionLevelPrivilege.isCanViewStudentInSections());
        Assertions.assertTrue(sectionLevelPrivilege.isCanSubmitSessionInSections());
        Assertions.assertTrue(sectionLevelPrivilege.isCanViewSessionInSections());
        Assertions.assertTrue(sectionLevelPrivilege.isCanModifySessionCommentsInSections());

        // verify the privilege has indeed been updated
        Assertions.assertFalse(helper.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_COURSE));
        Assertions.assertFalse(helper.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_SESSION));
        Assertions.assertFalse(helper.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
        Assertions.assertFalse(helper.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_STUDENT));
        Assertions.assertFalse(helper.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
        Assertions.assertFalse(helper.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        Assertions.assertFalse(helper.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        Assertions.assertFalse(helper.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));

        Assertions.assertTrue(helper.getPrivileges().isAllowedForPrivilege(
                "TUT1", Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
        Assertions.assertTrue(helper.getPrivileges().isAllowedForPrivilege(
                "TUT1", Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        Assertions.assertTrue(helper.getPrivileges().isAllowedForPrivilege(
                "TUT1", Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        Assertions.assertTrue(helper.getPrivileges().isAllowedForPrivilege(
                "TUT1", Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));
    }

    @Test
    protected void testExecute_validSessionLevelInput_shouldSucceed() {

        Assertions.assertFalse(helper.getPrivileges().isAllowedForPrivilege("Tutorial1", "Session1",
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        Assertions.assertFalse(helper.getPrivileges().isAllowedForPrivilege("Tutorial1", "Session1",
                Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        Assertions.assertFalse(helper.getPrivileges().isAllowedForPrivilege("Tutorial1", "Session1",
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, helperEmail,
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        InstructorPrivilegeUpdateRequest reqBody = new InstructorPrivilegeUpdateRequest();
        InstructorPrivileges privilege = new InstructorPrivileges();
        privilege.updatePrivilege("Tutorial1", "Session1",
                Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS, true);
        privilege.updatePrivilege("Tutorial1", "Session1",
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS, true);
        privilege.updatePrivilege("Tutorial1", "Session1",
                Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS, true);
        privilege.updatePrivilege("Tutorial1", "Session1",
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, true);
        reqBody.setPrivileges(privilege);

        UpdateInstructorPrivilegeAction action = getAction(reqBody, submissionParams);
        InstructorPrivilegeData actionOutput = (InstructorPrivilegeData) getJsonResult(action).getOutput();

        InstructorPermissionSet sessionLevelPrivilege = actionOutput.getPrivileges().getSessionLevelPrivileges()
                .get("Tutorial1").get("Session1");
        Assertions.assertFalse(sessionLevelPrivilege.isCanModifyCourse());
        Assertions.assertFalse(sessionLevelPrivilege.isCanModifySession());
        Assertions.assertFalse(sessionLevelPrivilege.isCanModifyStudent());
        Assertions.assertFalse(sessionLevelPrivilege.isCanModifyInstructor());
        Assertions.assertFalse(sessionLevelPrivilege.isCanViewStudentInSections());
        Assertions.assertTrue(sessionLevelPrivilege.isCanSubmitSessionInSections());
        Assertions.assertTrue(sessionLevelPrivilege.isCanViewSessionInSections());
        Assertions.assertTrue(sessionLevelPrivilege.isCanModifySessionCommentsInSections());

        // verify the privilege has indeed been updated
        Assertions.assertFalse(helper.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_COURSE));
        Assertions.assertFalse(helper.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_SESSION));
        Assertions.assertFalse(helper.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
        Assertions.assertFalse(helper.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_STUDENT));
        Assertions.assertFalse(helper.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
        Assertions.assertFalse(helper.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        Assertions.assertFalse(helper.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        Assertions.assertFalse(helper.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));

        Assertions.assertFalse(helper.getPrivileges().isAllowedForPrivilege(
                "Tutorial1", Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
        Assertions.assertFalse(helper.getPrivileges().isAllowedForPrivilege(
                "Tutorial1", Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        Assertions.assertFalse(helper.getPrivileges().isAllowedForPrivilege(
                "Tutorial1", Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        Assertions.assertFalse(helper.getPrivileges().isAllowedForPrivilege(
                "Tutorial1", Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));

        Assertions.assertTrue(helper.getPrivileges().isAllowedForPrivilege(
                "Tutorial1", "Session1", Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        Assertions.assertTrue(helper.getPrivileges().isAllowedForPrivilege(
                "Tutorial1", "Session1", Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        Assertions.assertTrue(helper.getPrivileges().isAllowedForPrivilege(
                "Tutorial1", "Session1", Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));
    }

    @Test
    protected void testExecute_withNullPrivileges_shouldFail() {

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructorEmail,
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        InstructorPrivilegeUpdateRequest reqBody = new InstructorPrivilegeUpdateRequest();

        verifyHttpRequestBodyFailure(reqBody, submissionParams);
    }

    @Test
    protected void testExecute_withInvalidInstructorEmail_shouldFail() {

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, "invalid-instructor-email",
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        InstructorPrivilegeUpdateRequest reqBody = new InstructorPrivilegeUpdateRequest();
        reqBody.setPrivileges(new InstructorPrivileges());

        EntityNotFoundException enfe = verifyEntityNotFound(reqBody, submissionParams);
        Assertions.assertEquals("Instructor does not exist.", enfe.getMessage());

    }

    @Test
    void testSpecificAccessControl_instructorWithPermission_canAccess() {
        Course course = new Course("course-id", "name", Const.DEFAULT_TIME_ZONE, "institute");

        InstructorPrivileges instructorPrivileges = new InstructorPrivileges();
        instructorPrivileges.updatePrivilege(InstructorPermissions.CAN_MODIFY_INSTRUCTOR, true);
        Instructor instructor = new Instructor(course, "name", "instructoremail@tm.tmt",
                false, "", null, instructorPrivileges);

        loginAsInstructor(googleId);
        when(mockLogic.getCourse(course.getId())).thenReturn(course);
        when(mockLogic.getInstructorByGoogleId(course.getId(), googleId)).thenReturn(instructor);

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        verifyCanAccess(params);
    }

    @Test
    void testSpecificAccessControl_notInstructor_cannotAccess() {
        String[] params = {
                Const.ParamsNames.COURSE_ID, "course-id",
        };
        when(mockLogic.getInstructorByGoogleId("course-id", googleId)).thenReturn(null);

        loginAsStudent(googleId);
        verifyCannotAccess(params);

        logoutUser();
        verifyCannotAccess(params);
    }
}


