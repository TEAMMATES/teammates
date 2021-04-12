package teammates.ui.webapi;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.InvalidHttpRequestBodyException;
import teammates.common.util.Const;
import teammates.ui.output.InstructorPrivilegeData;
import teammates.ui.output.MessageOutput;
import teammates.ui.request.InstructorPrivilegeUpdateRequest;

/**
 * SUT: {@link UpdateInstructorPrivilegeAction}.
 */
public class UpdateInstructorPrivilegeActionTest extends BaseActionTest<UpdateInstructorPrivilegeAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.INSTRUCTOR_PRIVILEGE;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Override
    protected void testExecute() throws Exception {
        // see individual tests
    }

    @Test
    protected void testExecute_withCourseId_shouldUpdatePrivilegesInCourseLevel() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        assertTrue(instructor1OfCourse1.privileges.isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_COURSE));
        assertTrue(instructor1OfCourse1.privileges.isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_SESSION));
        assertTrue(instructor1OfCourse1.privileges.isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
        assertTrue(instructor1OfCourse1.privileges.isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_STUDENT));
        assertTrue(instructor1OfCourse1.privileges.isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
        assertTrue(instructor1OfCourse1.privileges.isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        assertTrue(instructor1OfCourse1.privileges.isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        assertTrue(instructor1OfCourse1.privileges.isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor1OfCourse1.getEmail(),
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
        };

        InstructorPrivilegeUpdateRequest reqBody = new InstructorPrivilegeUpdateRequest();
        reqBody.setCanModifyCourse(false);
        reqBody.setCanModifyInstructor(false);
        reqBody.setCanModifyStudent(false);
        reqBody.setCanModifySession(false);
        reqBody.setCanViewStudentInSections(false);
        reqBody.setCanSubmitSessionInSections(false);
        reqBody.setCanViewSessionInSections(false);
        reqBody.setCanModifySessionCommentsInSections(false);

        UpdateInstructorPrivilegeAction action = getAction(reqBody, submissionParams);

        JsonResult result = getJsonResult(action);
        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        InstructorPrivilegeData response = (InstructorPrivilegeData) result.getOutput();
        assertFalse(response.isCanModifyCourse());
        assertFalse(response.isCanModifySession());
        assertFalse(response.isCanModifyStudent());
        assertFalse(response.isCanModifyInstructor());

        assertFalse(response.isCanViewStudentInSections());

        assertFalse(response.isCanSubmitSessionInSections());
        assertFalse(response.isCanViewSessionInSections());
        assertFalse(response.isCanModifySessionCommentsInSections());

        // verify the privilege has indeed been updated
        InstructorAttributes instructor = logic.getInstructorForGoogleId(
                instructor1OfCourse1.getCourseId(), instructor1OfCourse1.getGoogleId());

        assertFalse(instructor.privileges.isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_COURSE));
        assertFalse(instructor.privileges.isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_SESSION));
        assertFalse(instructor.privileges.isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
        assertFalse(instructor.privileges.isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_STUDENT));
        assertFalse(instructor.privileges.isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
        assertFalse(instructor.privileges.isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        assertFalse(instructor.privileges.isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        assertFalse(instructor.privileges.isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));
    }

    @Test
    protected void testExecute_withCourseIdAndSectionName_shouldUpdatePrivilegesInSectionLevel() {
        InstructorAttributes helper1OfCourse1 = typicalBundle.instructors.get("helperOfCourse1");

        assertFalse(helper1OfCourse1.privileges.isAllowedForPrivilege(
                "TUT1", Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
        assertFalse(helper1OfCourse1.privileges.isAllowedForPrivilege(
                "TUT1", Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        assertFalse(helper1OfCourse1.privileges.isAllowedForPrivilege(
                "TUT1", Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        assertFalse(helper1OfCourse1.privileges.isAllowedForPrivilege(
                "TUT1", Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, helper1OfCourse1.getEmail(),
                Const.ParamsNames.COURSE_ID, helper1OfCourse1.courseId,
        };

        InstructorPrivilegeUpdateRequest reqBody = new InstructorPrivilegeUpdateRequest();
        reqBody.setCanViewStudentInSections(true);
        reqBody.setCanSubmitSessionInSections(true);
        reqBody.setCanViewSessionInSections(true);
        reqBody.setCanModifySessionCommentsInSections(true);
        reqBody.setSectionName("TUT1");

        UpdateInstructorPrivilegeAction action = getAction(reqBody, submissionParams);

        JsonResult result = getJsonResult(action);
        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        InstructorPrivilegeData response = (InstructorPrivilegeData) result.getOutput();
        assertFalse(response.isCanModifyCourse());
        assertFalse(response.isCanModifySession());
        assertFalse(response.isCanModifyStudent());
        assertFalse(response.isCanModifyInstructor());

        assertTrue(response.isCanViewStudentInSections());

        assertTrue(response.isCanSubmitSessionInSections());
        assertTrue(response.isCanViewSessionInSections());
        assertTrue(response.isCanModifySessionCommentsInSections());
    }

    @Test
    protected void testExecute_withCourseIdAndSectionNameAndSessionName_shouldUpdatePrivilegesInSessionLevel() {
        InstructorAttributes helper1OfCourse1 = typicalBundle.instructors.get("helperOfCourse1");

        assertFalse(helper1OfCourse1.privileges.isAllowedForPrivilege("Tutorial1", "Session1",
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        assertFalse(helper1OfCourse1.privileges.isAllowedForPrivilege("Tutorial1", "Session1",
                Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        assertFalse(helper1OfCourse1.privileges.isAllowedForPrivilege("Tutorial1", "Session1",
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, helper1OfCourse1.getEmail(),
                Const.ParamsNames.COURSE_ID, helper1OfCourse1.courseId,
        };

        InstructorPrivilegeUpdateRequest reqBody = new InstructorPrivilegeUpdateRequest();
        reqBody.setCanSubmitSessionInSections(true);
        reqBody.setCanViewSessionInSections(true);
        reqBody.setCanModifySessionCommentsInSections(true);
        reqBody.setSectionName("Tutorial1");
        reqBody.setFeedbackSessionName("Session1");

        UpdateInstructorPrivilegeAction action = getAction(reqBody, submissionParams);

        JsonResult result = getJsonResult(action);
        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        InstructorPrivilegeData response = (InstructorPrivilegeData) result.getOutput();
        assertFalse(response.isCanModifyCourse());
        assertFalse(response.isCanModifySession());
        assertFalse(response.isCanModifyStudent());
        assertFalse(response.isCanModifyInstructor());

        assertFalse(response.isCanViewStudentInSections());

        assertTrue(response.isCanSubmitSessionInSections());
        assertTrue(response.isCanViewSessionInSections());
        assertTrue(response.isCanModifySessionCommentsInSections());
    }

    @Test
    protected void testExecute_requestPrivilegesInconsistent_shouldBeAutoFixed() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        assertTrue(instructor1OfCourse1.privileges.isAllowedForPrivilege(
                "TUT1", Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
        assertTrue(instructor1OfCourse1.privileges.isAllowedForPrivilege(
                "TUT1", Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        assertTrue(instructor1OfCourse1.privileges.isAllowedForPrivilege(
                "TUT1", Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        assertTrue(instructor1OfCourse1.privileges.isAllowedForPrivilege(
                "TUT1", Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor1OfCourse1.getEmail(),
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
        };

        InstructorPrivilegeUpdateRequest reqBody = new InstructorPrivilegeUpdateRequest();
        reqBody.setCanModifySessionCommentsInSections(true);
        reqBody.setSectionName("TUT1");

        UpdateInstructorPrivilegeAction action = getAction(reqBody, submissionParams);

        JsonResult result = getJsonResult(action);
        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        InstructorPrivilegeData response = (InstructorPrivilegeData) result.getOutput();
        assertTrue(response.isCanModifyCourse());
        assertTrue(response.isCanModifySession());
        assertTrue(response.isCanModifyStudent());
        assertTrue(response.isCanModifyInstructor());

        assertTrue(response.isCanViewStudentInSections());

        assertTrue(response.isCanSubmitSessionInSections());
        assertTrue(response.isCanViewSessionInSections());
        assertTrue(response.isCanModifySessionCommentsInSections());
    }

    @Test
    protected void testExecute_lastInstructorWithModifyInstructorPrivilege_shouldPreserve() {
        InstructorAttributes instructor1OfCourse4 = typicalBundle.instructors.get("instructor1OfCourse4");

        assertTrue(instructor1OfCourse4.privileges.isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_COURSE));
        assertFalse(instructor1OfCourse4.privileges.isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_SESSION));
        assertTrue(instructor1OfCourse4.privileges.isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
        assertTrue(instructor1OfCourse4.privileges.isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_STUDENT));
        assertTrue(instructor1OfCourse4.privileges.isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS));
        assertTrue(instructor1OfCourse4.privileges.isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
        assertTrue(instructor1OfCourse4.privileges.isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
        assertTrue(instructor1OfCourse4.privileges.isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));

        List<InstructorAttributes> instructorsWithModifyInstructorPrivilege =
                logic.getInstructorsForCourse(instructor1OfCourse4.courseId).stream().filter(
                        instructor -> instructor.privileges.isAllowedForPrivilege(
                        Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR)).collect(Collectors.toList());
        assertEquals(1, instructorsWithModifyInstructorPrivilege.size());
        assertEquals(instructor1OfCourse4.getGoogleId(), instructorsWithModifyInstructorPrivilege.get(0).getGoogleId());

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor1OfCourse4.getEmail(),
                Const.ParamsNames.COURSE_ID, instructor1OfCourse4.courseId,
        };

        InstructorPrivilegeUpdateRequest reqBody = new InstructorPrivilegeUpdateRequest();
        reqBody.setCanModifyInstructor(false);

        UpdateInstructorPrivilegeAction action = getAction(reqBody, submissionParams);

        JsonResult result = getJsonResult(action);
        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        InstructorPrivilegeData response = (InstructorPrivilegeData) result.getOutput();
        assertTrue(response.isCanModifyCourse());
        assertFalse(response.isCanModifySession());
        assertTrue(response.isCanModifyStudent());
        assertTrue(response.isCanModifyInstructor());

        assertTrue(response.isCanViewStudentInSections());

        assertTrue(response.isCanSubmitSessionInSections());
        assertTrue(response.isCanViewSessionInSections());
        assertTrue(response.isCanModifySessionCommentsInSections());
    }

    @Test
    protected void testExecute_withCourseIdAndSessionName_missingSectionNameShouldFail() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor1OfCourse1.getEmail(),
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
        };

        InstructorPrivilegeUpdateRequest reqBody = new InstructorPrivilegeUpdateRequest();
        reqBody.setCanViewStudentInSections(true);
        reqBody.setCanSubmitSessionInSections(true);
        reqBody.setFeedbackSessionName("session1");

        assertThrows(InvalidHttpRequestBodyException.class, () -> {
            UpdateInstructorPrivilegeAction illegalAction = getAction(reqBody, submissionParams);
            getJsonResult(illegalAction);
        });
    }

    @Test
    protected void testExecute_withCourseIdAndSectionNameAndWrongGranularity_shouldFail() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor1OfCourse1.getEmail(),
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
        };

        InstructorPrivilegeUpdateRequest reqBody = new InstructorPrivilegeUpdateRequest();
        reqBody.setCanModifyInstructor(true);
        reqBody.setSectionName("section1");

        assertThrows(InvalidHttpRequestBodyException.class, () -> {
            UpdateInstructorPrivilegeAction illegalAction = getAction(reqBody, submissionParams);
            getJsonResult(illegalAction);
        });
    }

    @Test
    protected void testExecute_withCourseIdAndSectionNameAndSessionNameAndWrongGranularity_shouldFail() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor1OfCourse1.getEmail(),
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
        };

        InstructorPrivilegeUpdateRequest reqBody = new InstructorPrivilegeUpdateRequest();
        reqBody.setCanViewStudentInSections(true);
        reqBody.setSectionName("section1");
        reqBody.setFeedbackSessionName("session1");

        assertThrows(InvalidHttpRequestBodyException.class, () -> {
            UpdateInstructorPrivilegeAction illegalAction = getAction(reqBody, submissionParams);
            getJsonResult(illegalAction);
        });
    }

    @Test
    protected void testExecute_noPrivilegesInRequestBody_nothingToUpdateShouldFail() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor1OfCourse1.getEmail(),
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
        };

        InstructorPrivilegeUpdateRequest reqBody = new InstructorPrivilegeUpdateRequest();

        assertThrows(InvalidHttpRequestBodyException.class, () -> {
            UpdateInstructorPrivilegeAction illegalAction = getAction(reqBody, submissionParams);
            getJsonResult(illegalAction);
        });
    }

    @Test
    protected void testExecute_withInvalidInstructorId_shouldFail() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_EMAIL, "invalid-instructor-email",
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.getCourseId(),
        };

        InstructorPrivilegeUpdateRequest reqBody = new InstructorPrivilegeUpdateRequest();
        reqBody.setCanViewStudentInSections(true);
        reqBody.setCanSubmitSessionInSections(true);
        reqBody.setFeedbackSessionName("session1");

        UpdateInstructorPrivilegeAction action = getAction(reqBody, submissionParams);

        JsonResult result = getJsonResult(action);
        assertEquals(HttpStatus.SC_NOT_FOUND, result.getStatusCode());

        MessageOutput msg = (MessageOutput) result.getOutput();
        assertEquals("Instructor does not exist.", msg.getMessage());

    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        InstructorAttributes instructor = typicalBundle.instructors.get("instructor3OfCourse1");

        ______TS("only instructors of the same course with modify permission can access");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor.courseId,
        };

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR, submissionParams);

        ______TS("invalid course id cannot access");

        String[] invalidCouseIdParams = new String[] {
                Const.ParamsNames.COURSE_ID, "invalid-course-id",
        };

        verifyCannotAccess(invalidCouseIdParams);
    }
}

