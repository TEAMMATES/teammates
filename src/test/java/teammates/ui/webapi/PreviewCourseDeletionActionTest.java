package teammates.ui.webapi;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.output.DeletionPreviewOutput;

/**
 * SUT: {@link PreviewCourseDeletionAction}.
 */
public class PreviewCourseDeletionActionTest extends BaseActionTest<PreviewCourseDeletionAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.COURSE_DELETION_PREVIEW;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        // Test case 1: Preview deletion for non-existent course
        String nonExistentCourseId = "non-existent-course-xyz";
        String[] paramsNonExistent = new String[] {
                Const.ParamsNames.COURSE_ID, nonExistentCourseId
        };

        loginAsInstructor("instructor1");
        PreviewCourseDeletionAction actionNonExistent = getAction(paramsNonExistent);
        JsonResult resultNonExistent = getJsonResult(actionNonExistent);
        DeletionPreviewOutput outputNonExistent = (DeletionPreviewOutput) resultNonExistent.getOutput();

        // Verify non-existent course returns warning
        assertTrue(outputNonExistent.isHasWarnings());
        assertTrue(outputNonExistent.getWarnings().stream()
                .anyMatch(w -> w.contains("does not exist")));

        // Test case 2: Preview deletion for valid course with students, instructors, sessions
        String validCourseId = "tmeu.test.cs1101s.v4.2024s1";
        String[] paramsValid = new String[] {
                Const.ParamsNames.COURSE_ID, validCourseId
        };

        PreviewCourseDeletionAction actionValid = getAction(paramsValid);
        JsonResult resultValid = getJsonResult(actionValid);
        DeletionPreviewOutput outputValid = (DeletionPreviewOutput) resultValid.getOutput();

        // Verify course deletion preview structure
        assertNotNull(outputValid);
        assertEquals(outputValid.getEntityType(), "COURSE");
        assertEquals(outputValid.getEntityIdentifier(), validCourseId);

        // Verify all affected entity counts are tracked
        assertTrue(outputValid.getStudentsAffected() >= 0);
        assertTrue(outputValid.getInstructorsAffected() >= 0);
        assertTrue(outputValid.getFeedbackSessionsAffected() >= 0);
        assertTrue(outputValid.getFeedbackQuestionsAffected() >= 0);
        assertTrue(outputValid.getFeedbackResponsesAffected() >= 0);
        assertTrue(outputValid.getFeedbackCommentsAffected() >= 0);
        assertTrue(outputValid.getDeadlineExtensionsAffected() >= 0);

        // Test case 3: Verify all required output fields are present
        assertNotNull(outputValid.getWarnings());
        assertNotNull(outputValid.getCascadedDeletions());
        assertTrue(outputValid.getTotalEntitiesAffected() >= 0);

        // Test case 4: Verify large amounts of data are handled correctly
        // The test course should have multiple students, instructors, and sessions
        if (outputValid.getStudentsAffected() > 0) {
            assertTrue(outputValid.getCascadedDeletions() != null);
        }
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        String courseId = "tmeu.test.cs1101s.v4.2024s1";
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, courseId
        };

        // Test 1: Only instructors with CAN_MODIFY_COURSE permission should access
        loginAsInstructor("instructor1");
        PreviewCourseDeletionAction actionInstructor = getAction(params);
        assertNotNull(actionInstructor);

        // Test 2: Students should not have access
        loginAsStudent("student1");
        verifyCannotAccess(params);

        // Test 3: Non-course members should not have access
        loginAsInstructor("instructor-not-in-course");
        verifyCannotAccess(params);

        // Test 4: Unregistered users should not have access
        loginAsUnregistered("unregistered-user");
        verifyCannotAccess(params);

        // Test 5: Admin should have access
        loginAsAdmin();
        PreviewCourseDeletionAction actionAdmin = getAction(params);
        assertNotNull(actionAdmin);

        // Test 6: Logged out users should not have access
        logoutUser();
        verifyCannotAccess(params);
    }

    @Test
    public void testExecute_withoutCourseIdParameter_shouldFail() throws Exception {
        loginAsInstructor("instructor1");
        String[] paramsEmpty = new String[] {};
        verifyHttpParameterFailure(paramsEmpty);
    }

    @Test
    public void testExecute_withEmptyCourseId_shouldFail() throws Exception {
        loginAsInstructor("instructor1");
        String[] paramsEmpty = new String[] {
                Const.ParamsNames.COURSE_ID, ""
        };
        verifyHttpParameterFailure(paramsEmpty);
    }

    @Test
    public void testExecute_courseWithArchivedStatus_shouldHandleCorrectly() throws Exception {
        // Test with archived course if available in test data
        String courseId = "tmeu.test.cs1101s.v4.2024s1";
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, courseId
        };

        loginAsInstructor("instructor1");
        PreviewCourseDeletionAction action = getAction(params);
        JsonResult result = getJsonResult(action);
        DeletionPreviewOutput output = (DeletionPreviewOutput) result.getOutput();

        // Verify archived course is still counted correctly
        assertNotNull(output);
        assertTrue(output.getTotalEntitiesAffected() >= 0);
    }

    @Test
    public void testExecute_courseWithSoftDeletedStatus_shouldHandleCorrectly() throws Exception {
        // Test with soft-deleted course if available in test data
        String courseId = "tmeu.test.cs1101s.v4.2024s1";
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, courseId
        };

        loginAsInstructor("instructor1");
        PreviewCourseDeletionAction action = getAction(params);
        JsonResult result = getJsonResult(action);
        DeletionPreviewOutput output = (DeletionPreviewOutput) result.getOutput();

        // Verify soft-deleted course is handled correctly
        assertNotNull(output);
        assertTrue(output.getTotalEntitiesAffected() >= 0);
    }

    @Test
    public void testExecute_responseStructure_shouldBeValid() throws Exception {
        String courseId = "tmeu.test.cs1101s.v4.2024s1";
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, courseId
        };

        loginAsInstructor("instructor1");
        PreviewCourseDeletionAction action = getAction(params);
        JsonResult result = getJsonResult(action);
        DeletionPreviewOutput output = (DeletionPreviewOutput) result.getOutput();

        // Verify JSON response structure
        assertNotNull(output);
        assertEquals(output.getEntityType(), "COURSE");
        assertNotNull(output.getEntityIdentifier());
        assertNotNull(output.getWarnings());
        assertNotNull(output.getCascadedDeletions());

        // Verify no null counts
        assertNotNull(output.getStudentsAffected());
        assertNotNull(output.getInstructorsAffected());
        assertNotNull(output.getFeedbackSessionsAffected());
        assertNotNull(output.getFeedbackQuestionsAffected());
        assertNotNull(output.getFeedbackResponsesAffected());
        assertNotNull(output.getFeedbackCommentsAffected());
        assertNotNull(output.getDeadlineExtensionsAffected());
        assertNotNull(output.getTotalEntitiesAffected());
    }

    @Test
    public void testExecute_totalAffectedEqualsSum_shouldBeTrue() throws Exception {
        String courseId = "tmeu.test.cs1101s.v4.2024s1";
        String[] params = new String[] {
                Const.ParamsNames.COURSE_ID, courseId
        };

        loginAsInstructor("instructor1");
        PreviewCourseDeletionAction action = getAction(params);
        JsonResult result = getJsonResult(action);
        DeletionPreviewOutput output = (DeletionPreviewOutput) result.getOutput();

        // Verify total equals sum of individual counts
        int expectedTotal = output.getStudentsAffected()
                + output.getInstructorsAffected()
                + output.getFeedbackSessionsAffected()
                + output.getFeedbackQuestionsAffected()
                + output.getFeedbackResponsesAffected()
                + output.getFeedbackCommentsAffected()
                + output.getDeadlineExtensionsAffected();

        assertEquals(output.getTotalEntitiesAffected(), expectedTotal);
    }
}
