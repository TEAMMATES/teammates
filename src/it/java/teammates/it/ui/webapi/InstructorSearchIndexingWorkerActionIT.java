package teammates.it.ui.webapi;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.test.TestProperties;
import teammates.ui.webapi.InstructorSearchIndexingWorkerAction;

/**
 * SUT: {@link InstructorSearchIndexingWorkerAction}.
 */
public class InstructorSearchIndexingWorkerActionIT extends BaseActionIT<InstructorSearchIndexingWorkerAction> {
    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        persistDataBundle(typicalBundle);
        HibernateUtil.flushSession();
    }

    @Override
    protected String getActionUri() {
        return Const.TaskQueue.INSTRUCTOR_SEARCH_INDEXING_WORKER_URL;
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

        Instructor instructor1 = typicalBundle.instructors.get("instructor1OfCourse1");

        ______TS("instructor not yet indexed should not be searchable");

        List<Instructor> instructorList = logic.searchInstructorsInWholeSystem(instructor1.getEmail());
        assertEquals(0, instructorList.size());

        ______TS("instructor indexed should be searchable");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1.getCourseId(),
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor1.getEmail(),
        };

        InstructorSearchIndexingWorkerAction action = getAction(submissionParams);
        getJsonResult(action);

        instructorList = logic.searchInstructorsInWholeSystem(instructor1.getEmail());
        assertEquals(1, instructorList.size());
        assertEquals(instructor1.getId(), instructorList.get(0).getId());
    }

    @Override
    protected void testAccessControl() throws InvalidParametersException, EntityAlreadyExistsException {
        Instructor instructor1 = typicalBundle.instructors.get("instructor1OfCourse1");
        Course course = typicalBundle.courses.get("course1");
        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1.getCourseId(),
                Const.ParamsNames.INSTRUCTOR_EMAIL, instructor1.getEmail(),
        };

        verifyOnlyAdminCanAccess(course, submissionParams);
    }
}
