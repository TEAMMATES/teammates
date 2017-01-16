package teammates.test.cases.ui;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.test.driver.AssertHelper;
import teammates.ui.controller.InstructorFeedbacksPageAction;
import teammates.ui.controller.InstructorFeedbacksPageData;
import teammates.ui.controller.ShowPageResult;

public class InstructorFeedbacksPageActionTest extends BaseActionTest {

    private static final DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
        removeAndRestoreTypicalDataBundle();
        uri = Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE;
    }
    
    @Test
    public void testExecuteAndPostProcess() throws Exception {
        String instructorId = dataBundle.instructors.get("instructor1OfCourse1").googleId;
        String adminUserId = "admin.user";
        String[] submissionParams = new String[]{Const.ParamsNames.IS_USING_AJAX, "true"};
        
        InstructorAttributes instructor1ofCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        
        ______TS("Typical case, 2 courses");
        if (CoursesLogic.inst().isCoursePresent("new-course")) {
            CoursesLogic.inst().deleteCourseCascade("new-course");
        }
        
        CoursesLogic.inst().createCourseAndInstructor(instructorId, "new-course", "New course", "UTC");
        gaeSimulation.loginAsInstructor(instructorId);
        InstructorFeedbacksPageAction a = getAction(submissionParams);
        ShowPageResult r = (ShowPageResult) a.executeAndPostProcess();
        
        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACKS + "?error=false&user=idOfInstructor1OfCourse1",
                     r.getDestinationWithParams());
        assertFalse(r.isError);
        assertEquals("", r.getStatusMessage());
        
        InstructorFeedbacksPageData pageData = (InstructorFeedbacksPageData) r.data;
        assertEquals(instructorId, pageData.account.googleId);
        assertEquals(2, pageData.getNewFsForm().getCourses().size());
        assertEquals(6, pageData.getFsList().getExistingFeedbackSessions().size());
        assertEquals("", pageData.getNewFsForm().getFsName());
        assertEquals(null, pageData.getNewFsForm().getCourseId());
        
        String expectedLogMessage =
                "TEAMMATESLOG|||instructorFeedbacksPage|||instructorFeedbacksPage|||"
                + "true|||Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||"
                + "instr1@course1.tmt|||Number of feedback sessions: 6|||/page/instructorFeedbacksPage";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());

        ______TS("0 sessions");
        
        FeedbackSessionsLogic.inst().deleteFeedbackSessionsForCourseCascade(instructor1ofCourse1.courseId);
        
        submissionParams = new String[]{Const.ParamsNames.COURSE_ID, instructor1ofCourse1.courseId,
                                        Const.ParamsNames.IS_USING_AJAX, "true"};
        a = getAction(addUserIdToParams(instructorId, submissionParams));
        r = (ShowPageResult) a.executeAndPostProcess();
        
        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACKS + "?error=false&user=idOfInstructor1OfCourse1",
                     r.getDestinationWithParams());
        assertEquals(Const.StatusMessages.FEEDBACK_SESSION_EMPTY, r.getStatusMessage());
        assertFalse(r.isError);
        
        pageData = (InstructorFeedbacksPageData) r.data;
        assertEquals(instructorId, pageData.account.googleId);
        assertEquals(2, pageData.getNewFsForm().getCourses().size());
        assertEquals(0, pageData.getFsList().getExistingFeedbackSessions().size());
        assertEquals("", pageData.getNewFsForm().getFsName());
        assertEquals(instructor1ofCourse1.courseId, pageData.getNewFsForm().getCourseId());
        
        expectedLogMessage =
                "TEAMMATESLOG|||instructorFeedbacksPage|||instructorFeedbacksPage|||"
                + "true|||Instructor|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||"
                + "instr1@course1.tmt|||Number of feedback sessions: 0|||/page/instructorFeedbacksPage";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());
        
        ______TS("Masquerade mode, 0 courses");
        
        gaeSimulation.loginAsAdmin(adminUserId);
        
        CoursesLogic.inst().deleteCourseCascade(instructor1ofCourse1.courseId);
        CoursesLogic.inst().deleteCourseCascade("new-course");
        
        submissionParams = new String[]{Const.ParamsNames.IS_USING_AJAX, "true"};
        a = getAction(addUserIdToParams(instructorId, submissionParams));
        r = (ShowPageResult) a.executeAndPostProcess();
        
        assertEquals(Const.ViewURIs.INSTRUCTOR_FEEDBACKS + "?error=false&user=idOfInstructor1OfCourse1",
                     r.getDestinationWithParams());
        assertEquals("You have not created any courses yet, or you have no active courses. "
                     + "Go <a href=\"/page/instructorCoursesPage?user=idOfInstructor1OfCourse1\">here</a> "
                     + "to create or unarchive a course.",
                     r.getStatusMessage());
        assertFalse(r.isError);
        
        pageData = (InstructorFeedbacksPageData) r.data;
        assertEquals(instructorId, pageData.account.googleId);
        assertEquals(0, pageData.getNewFsForm().getCourses().size());
        assertEquals(0, pageData.getFsList().getExistingFeedbackSessions().size());
        assertEquals("", pageData.getNewFsForm().getFsName());
        
        expectedLogMessage =
                "TEAMMATESLOG|||instructorFeedbacksPage|||instructorFeedbacksPage|||true|||"
                + "Instructor(M)|||Instructor 1 of Course 1|||idOfInstructor1OfCourse1|||"
                + "instr1@course1.tmt|||Number of feedback sessions: 0|||/page/instructorFeedbacksPage";
        AssertHelper.assertLogMessageEquals(expectedLogMessage, a.getLogMessage());
    }

    private InstructorFeedbacksPageAction getAction(String... params) {
        return (InstructorFeedbacksPageAction) gaeSimulation.getActionObject(uri, params);
    }

}
