package teammates.ui.webapi;

import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.Const.TaskQueue;
import teammates.test.TestProperties;

/**
 * SUT: {@link InstructorSearchIndexingWorkerAction}.
 */
public class InstructorSearchIndexingWorkerActionTest extends BaseActionTest<InstructorSearchIndexingWorkerAction> {

    @Override
    protected String getActionUri() {
        return TaskQueue.INSTRUCTOR_SEARCH_INDEXING_WORKER_URL;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Override
    @Test
    public void testExecute() throws Exception {
        if (!TestProperties.isSearchServiceActive()) {
            return;
        }

        InstructorAttributes instructor1 = typicalBundle.instructors.get("instructor1OfCourse1");

        ______TS("instructor not yet indexed should not be searchable");

        List<InstructorAttributes> instructorList = logic.searchInstructorsInWholeSystem(instructor1.getEmail());
        assertEquals(0, instructorList.size());

        ______TS("instructor indexed should be searchable");

        String[] submissionParams = new String[] {
                ParamsNames.COURSE_ID, instructor1.getCourseId(),
                ParamsNames.INSTRUCTOR_EMAIL, instructor1.getEmail(),
        };

        InstructorSearchIndexingWorkerAction action = getAction(submissionParams);
        getJsonResult(action);

        instructorList = logic.searchInstructorsInWholeSystem(instructor1.getEmail());
        assertEquals(1, instructorList.size());
        assertEquals(instructor1.getName(), instructorList.get(0).getName());
    }

    @Override
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }
}
