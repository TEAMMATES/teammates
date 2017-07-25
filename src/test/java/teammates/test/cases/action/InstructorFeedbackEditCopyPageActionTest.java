package teammates.test.cases.action;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.controller.InstructorFeedbackEditCopyPageAction;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.pagedata.InstructorFeedbackEditCopyPageData;

/**
 * SUT: {@link InstructorFeedbackEditCopyPageAction}.
 */
public class InstructorFeedbackEditCopyPageActionTest extends BaseActionTest {

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_COPY_PAGE;
    }

    @Override
    protected void prepareTestData() {
        dataBundle = loadDataBundle("/InstructorFeedbackEditCopyTest.json");
        removeAndRestoreDataBundle(dataBundle);
    }

    @Override
    @Test
    public void testExecuteAndPostProcess() {
        InstructorAttributes instructor = dataBundle.instructors.get("teammates.test.instructor2");
        String instructorId = instructor.googleId;

        gaeSimulation.loginAsInstructor(instructorId);

        ______TS("Successful case");

        String[] submissionParams = {
                Const.ParamsNames.COURSE_ID, "valid course id",
                Const.ParamsNames.FEEDBACK_SESSION_NAME, "valid fs name"
        };

        InstructorFeedbackEditCopyPageAction a = getAction(submissionParams);
        ShowPageResult r = getShowPageResult(a);

        assertFalse(r.isError);

        InstructorFeedbackEditCopyPageData pageData = (InstructorFeedbackEditCopyPageData) r.data;
        assertEquals(4, pageData.getCourses().size());

        List<String> idOfCourses = new ArrayList<>();

        for (CourseAttributes course : pageData.getCourses()) {
            idOfCourses.add(course.getId());
        }

        assertFalse(idOfCourses.contains("FeedbackEditCopy.CS1101")); // course is archived
        assertFalse(idOfCourses.contains("FeedbackEditCopy.CS2107")); // instructor does not have sufficient permissions

        assertTrue(idOfCourses.contains("FeedbackEditCopy.CS2102"));
        assertTrue(idOfCourses.contains("FeedbackEditCopy.CS2103"));
        assertTrue(idOfCourses.contains("FeedbackEditCopy.CS2103R"));
        assertTrue(idOfCourses.contains("FeedbackEditCopy.CS2104"));

    }

    @Override
    protected InstructorFeedbackEditCopyPageAction getAction(String... params) {
        return (InstructorFeedbackEditCopyPageAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        //TODO: implement this
    }
}
