package teammates.test.cases.ui;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.EvaluationsLogic;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.ui.controller.InstructorEvalsPageAction;
import teammates.ui.controller.InstructorEvalPageData;
import teammates.ui.controller.ShowPageResult;

public class InstructorEvalsPageActionTest extends BaseActionTest {

    private final DataBundle dataBundle = getTypicalDataBundle();

    @BeforeClass
    public static void classSetUp() throws Exception {
        printTestClassHeader();
		removeAndRestoreTypicalDataInDatastore();
        uri = Const.ActionURIs.INSTRUCTOR_EVALS_PAGE;
    }

    //@Test
    //This test is deprecated along with creating evaluations.
    public void testExecuteAndPostProcess() throws Exception {
        InstructorAttributes instructor1OfCourse1 = dataBundle.instructors
                .get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;

        String[] submissionParams = new String[] {};

        InstructorAttributes instructor1ofCourse1 = dataBundle.instructors
                .get("instructor1OfCourse1");

        ______TS("Typical case, 2 courses");
        
        if (CoursesLogic.inst().isCoursePresent("new-course")){
            CoursesLogic.inst().deleteCourseCascade("new-course");
        }
        CoursesLogic.inst().createCourseAndInstructor(instructorId, "new-course", "New course");

        gaeSimulation.loginAsInstructor(instructorId);
        InstructorEvalsPageAction a = getAction(submissionParams);
        ShowPageResult r = (ShowPageResult) a.executeAndPostProcess();

        assertEquals(Const.ViewURIs.INSTRUCTOR_EVALS
                + "?error=false&user=idOfInstructor1OfCourse1",
                r.getDestinationWithParams());
        assertEquals(false, r.isError);
        assertEquals("", r.getStatusMessage());

        InstructorEvalPageData pageData = (InstructorEvalPageData) r.data;
        assertEquals(instructorId, pageData.account.googleId);
        assertEquals(2, pageData.courses.size());
        assertEquals(2, pageData.existingEvalSessions.size());
        assertEquals(null, pageData.newEvaluationToBeCreated);
        assertEquals(null, pageData.courseIdForNewEvaluation);

        String expectedLogMessage = "TEAMMATESLOG|||instructorEvalsPage|||instructorEvalsPage"
                +
                "|||true|||Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1"
                +
                "|||instr1@course1.com|||Number of evaluations :2|||/page/instructorEvalsPage";
        assertEquals(expectedLogMessage, a.getLogMessage());

        ______TS("Masquerade mode, 0 evaluations");

        EvaluationsLogic.inst().deleteEvaluationsForCourse(
                instructor1ofCourse1.courseId);
        FeedbackSessionsLogic.inst().deleteFeedbackSessionsForCourse(
                instructor1OfCourse1.courseId);
        gaeSimulation.loginAsAdmin("admin.user");
        submissionParams = new String[] { Const.ParamsNames.COURSE_ID,
                instructor1ofCourse1.courseId };
        a = getAction(addUserIdToParams(instructorId, submissionParams));
        r = (ShowPageResult) a.executeAndPostProcess();

        assertEquals(
                Const.ViewURIs.INSTRUCTOR_EVALS
                        + "?error=false&user=idOfInstructor1OfCourse1",
                r.getDestinationWithParams());
        assertEquals(Const.StatusMessages.EVALUATION_EMPTY,
                r.getStatusMessage());
        assertEquals(false, r.isError);

        pageData = (InstructorEvalPageData) r.data;
        assertEquals(instructorId, pageData.account.googleId);
        assertEquals(2, pageData.courses.size());
        assertEquals(0, pageData.existingEvalSessions.size());
        assertEquals(null, pageData.newEvaluationToBeCreated);
        assertEquals(instructor1ofCourse1.courseId,
                pageData.courseIdForNewEvaluation);

        expectedLogMessage = "TEAMMATESLOG|||instructorEvalsPage|||instructorEvalsPage"
                +
                "|||true|||Instructor(M)|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1"
                +
                "|||instr1@course1.com|||Number of evaluations :0|||/page/instructorEvalsPage";
        assertEquals(expectedLogMessage, a.getLogMessage());

        ______TS("Masquerade mode, 0 courses");

        CoursesLogic.inst().deleteCourseCascade(instructor1ofCourse1.courseId);
        CoursesLogic.inst().deleteCourseCascade("new-course");

        submissionParams = new String[] {};
        a = getAction(addUserIdToParams(instructorId, submissionParams));
        r = (ShowPageResult) a.executeAndPostProcess();

        assertEquals(
                Const.ViewURIs.INSTRUCTOR_EVALS
                        + "?error=false&user=idOfInstructor1OfCourse1",
                r.getDestinationWithParams());
        assertEquals(
                "You have not created any courses yet. Go <a href=\"/page/instructorCoursesPage?user=idOfInstructor1OfCourse1\">here</a> to create one.",
                r.getStatusMessage());
        assertEquals(false, r.isError);

        pageData = (InstructorEvalPageData) r.data;
        assertEquals(instructorId, pageData.account.googleId);
        assertEquals(0, pageData.courses.size());
        assertEquals(0, pageData.existingEvalSessions.size());
        assertEquals(null, pageData.newEvaluationToBeCreated);
        assertEquals(null, pageData.courseIdForNewEvaluation);

        expectedLogMessage = "TEAMMATESLOG|||instructorEvalsPage|||instructorEvalsPage"
                +
                "|||true|||Instructor(M)|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1"
                +
                "|||instr1@course1.com|||Number of evaluations :0|||/page/instructorEvalsPage";
        assertEquals(expectedLogMessage, a.getLogMessage());
    }

    private InstructorEvalsPageAction getAction(String... params)
            throws Exception {
        return (InstructorEvalsPageAction) (gaeSimulation.getActionObject(uri,
                params));
    }

}
