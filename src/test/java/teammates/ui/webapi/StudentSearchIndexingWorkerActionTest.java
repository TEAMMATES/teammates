package teammates.ui.webapi;

import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.Const.TaskQueue;
import teammates.test.TestProperties;

/**
 * SUT: {@link StudentSearchIndexingWorkerAction}.
 */
public class StudentSearchIndexingWorkerActionTest extends BaseActionTest<StudentSearchIndexingWorkerAction> {

    @Override
    protected String getActionUri() {
        return TaskQueue.STUDENT_SEARCH_INDEXING_WORKER_URL;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Override
    @Test
    protected void testExecute() throws Exception {
        if (!TestProperties.isSearchServiceActive()) {
            return;
        }

        StudentAttributes student1 = typicalBundle.students.get("student1InCourse1");

        ______TS("student not yet indexed should not be searchable");

        List<StudentAttributes> studentList = logic.searchStudentsInWholeSystem(student1.getEmail());
        assertEquals(0, studentList.size());

        ______TS("student indexed should be searchable");

        String[] submissionParams = new String[] {
                ParamsNames.COURSE_ID, student1.getCourse(),
                ParamsNames.STUDENT_EMAIL, student1.getEmail(),
        };

        StudentSearchIndexingWorkerAction action = getAction(submissionParams);
        getJsonResult(action);

        studentList = logic.searchStudentsInWholeSystem(student1.getEmail());
        assertEquals(1, studentList.size());
        assertEquals(student1.getName(), studentList.get(0).getName());
    }

    @Override
    protected void testAccessControl() {
        verifyOnlyAdminCanAccess();
    }
}
