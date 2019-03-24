package teammates.test.cases.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.InvalidHttpRequestBodyException;
import teammates.common.util.Const;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.action.UpdateInstructorPrivilegeAction;
import teammates.ui.webapi.output.InstructorPrivilegeData;
import teammates.ui.webapi.output.MessageOutput;
import teammates.ui.webapi.request.InstructorPrivilegeUpdateRequest;

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

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, instructor1OfCourse1.getGoogleId(),
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
        };

        InstructorPrivilegeUpdateRequest reqBody = new InstructorPrivilegeUpdateRequest();
        reqBody.setCanModifyCourse(false);
        reqBody.setCanModifySessionCommentsInSections(false);

        UpdateInstructorPrivilegeAction action = getAction(reqBody, submissionParams);

        JsonResult result = getJsonResult(action);
        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        InstructorPrivilegeData response = (InstructorPrivilegeData) result.getOutput();
        assertFalse(response.isCanModifyCourse());
        assertTrue(response.isCanModifySession());
        assertTrue(response.isCanModifyStudent());
        assertTrue(response.isCanModifyInstructor());

        assertTrue(response.isCanViewStudentInSections());

        assertTrue(response.isCanSubmitSessionInSections());
        assertTrue(response.isCanViewSessionInSections());
        assertFalse(response.isCanModifySessionCommentsInSections());
    }

    @Test
    protected void testExecute_withCourseIdAndSectionName_shouldUpdatePrivilegesInSectionLevel() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, instructor1OfCourse1.getGoogleId(),
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
        };

        InstructorPrivilegeUpdateRequest reqBody = new InstructorPrivilegeUpdateRequest();
        reqBody.setCanViewStudentInSections(false);
        reqBody.setCanSubmitSessionInSections(true);
        reqBody.setSectionName("TUT1");

        UpdateInstructorPrivilegeAction action = getAction(reqBody, submissionParams);

        JsonResult result = getJsonResult(action);
        assertEquals(HttpStatus.SC_OK, result.getStatusCode());

        InstructorPrivilegeData response = (InstructorPrivilegeData) result.getOutput();
        assertTrue(response.isCanModifyCourse());
        assertTrue(response.isCanModifySession());
        assertTrue(response.isCanModifyStudent());
        assertTrue(response.isCanModifyInstructor());

        assertFalse(response.isCanViewStudentInSections());

        assertTrue(response.isCanSubmitSessionInSections());
        assertFalse(response.isCanViewSessionInSections());
        assertFalse(response.isCanModifySessionCommentsInSections());
    }

    @Test
    protected void testExecute_withCourseIdAndSectionNameAndSessionName_shouldUpdatePrivilegesInSessionLevel() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, instructor1OfCourse1.getGoogleId(),
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
        };

        InstructorPrivilegeUpdateRequest reqBody = new InstructorPrivilegeUpdateRequest();
        reqBody.setCanSubmitSessionInSections(true);
        reqBody.setSectionName("Tutorial1");
        reqBody.setFeedbackSessionName("Session1");

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
        assertFalse(response.isCanViewSessionInSections());
        assertFalse(response.isCanModifySessionCommentsInSections());
    }

    @Test
    protected void testExecute_requestPrivilegesInconsistent_shouldBeAutoFixed() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, instructor1OfCourse1.getGoogleId(),
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

        assertFalse(response.isCanViewStudentInSections());

        assertFalse(response.isCanSubmitSessionInSections());
        assertTrue(response.isCanViewSessionInSections());
        assertTrue(response.isCanModifySessionCommentsInSections());
    }

    @Test
    protected void testExecute_lastInstructorWithModifyInstructorPrivilege_shouldPreserve() {
        InstructorAttributes instructor1OfCourse4 = typicalBundle.instructors.get("instructor1OfCourse4");

        String[] submissionParams = new String[] {
                Const.ParamsNames.INSTRUCTOR_ID, instructor1OfCourse4.getGoogleId(),
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
                Const.ParamsNames.INSTRUCTOR_ID, instructor1OfCourse1.getGoogleId(),
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
                Const.ParamsNames.INSTRUCTOR_ID, instructor1OfCourse1.getGoogleId(),
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
                Const.ParamsNames.INSTRUCTOR_ID, instructor1OfCourse1.getGoogleId(),
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
                Const.ParamsNames.INSTRUCTOR_ID, instructor1OfCourse1.getGoogleId(),
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
                Const.ParamsNames.INSTRUCTOR_ID, "invalid-instructor-id",
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

        verifyOnlyInstructorsOfTheSameCourseCanAccess(submissionParams);
        verifyInaccessibleWithoutModifyInstructorPrivilege(submissionParams);

        ______TS("instructors of other courses cannot access");

        verifyInaccessibleForInstructorsOfOtherCourses(submissionParams);

        ______TS("invalid course id cannot access");

        String[] invalidCouseIdParams = new String[] {
                Const.ParamsNames.COURSE_ID, "invalid-course-id",
        };

        verifyCannotAccess(invalidCouseIdParams);
    }
}

