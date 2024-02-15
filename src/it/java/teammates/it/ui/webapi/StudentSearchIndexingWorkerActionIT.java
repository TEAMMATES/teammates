package teammates.it.ui.webapi;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.Const.TaskQueue;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlsearch.SearchManagerFactory;
import teammates.test.TestProperties;
import teammates.ui.webapi.StudentSearchIndexingWorkerAction;

/**
 * SUT: {@link StudentSearchIndexingWorkerAction}.
 */
public class StudentSearchIndexingWorkerActionIT extends BaseActionIT<StudentSearchIndexingWorkerAction> {

    private final Student student = typicalBundle.students.get("student1InCourse1");

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
        HibernateUtil.flushSession();
        SearchManagerFactory.getStudentSearchManager().resetCollections();
    }

    @Override
    protected String getActionUri() {
        return TaskQueue.STUDENT_SEARCH_INDEXING_WORKER_URL;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Override
    protected void testExecute() throws Exception {
        // See test cases below
    }

    @Test
    protected void testExecute_studentNotYetIndexed_shouldNotBeSearchable() throws Exception {
        if (!TestProperties.isSearchServiceActive()) {
            return;
        }

        List<Student> studentList = logic.searchStudentsInWholeSystem(student.getEmail());
        assertEquals(0, studentList.size());
    }

    @Test
    protected void testExecute_studentIndexed_shouldBeSearchable() throws Exception {
        if (!TestProperties.isSearchServiceActive()) {
            return;
        }

        String[] submissionParams = new String[] {
                ParamsNames.COURSE_ID, student.getCourseId(),
                ParamsNames.STUDENT_EMAIL, student.getEmail(),
        };

        StudentSearchIndexingWorkerAction action = getAction(submissionParams);
        getJsonResult(action);

        List<Student> studentList = logic.searchStudentsInWholeSystem(student.getEmail());
        assertEquals(1, studentList.size());
        assertEquals(student.getName(), studentList.get(0).getName());
    }

    @Override
    @Test
    protected void testAccessControl() throws InvalidParametersException, EntityAlreadyExistsException {
        Course course = typicalBundle.courses.get("course1");
        verifyOnlyAdminCanAccess(course);
    }
}
