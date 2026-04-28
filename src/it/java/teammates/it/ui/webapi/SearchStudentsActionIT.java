package teammates.it.ui.webapi;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.DataBundle;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.StudentsData;
import teammates.ui.webapi.JsonResult;
import teammates.ui.webapi.SearchStudentsAction;

/**
 * SUT: {@link SearchStudentsAction}.
 */
public class SearchStudentsActionIT extends BaseActionIT<SearchStudentsAction> {
    private DataBundle typicalBundle;

    private Student student1InCourse1;
    private Instructor instructor1OfCourse1;

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        typicalBundle = persistDataBundle(getTypicalDataBundle());
        student1InCourse1 = typicalBundle.students.get("student1InCourse1");
        instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        HibernateUtil.flushSession();
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.SEARCH_STUDENTS;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    protected void testExecute() {
        // See test cases below.
    }

    @Test
    public void execute_invalidParameters_parameterFailure() {
        loginAsAdmin();
        verifyHttpParameterFailure();

        String[] notEnoughParams = new String[] {
                Const.ParamsNames.SEARCH_KEY, "dummy",
        };
        verifyHttpParameterFailure(notEnoughParams);

        String[] invalidEntityParams = new String[] {
                Const.ParamsNames.SEARCH_KEY, "dummy",
                Const.ParamsNames.ENTITY_TYPE, "dummy",
        };
        verifyHttpParameterFailure(invalidEntityParams);

        String[] adminParams = new String[] {
                Const.ParamsNames.SEARCH_KEY, "dummy",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.ADMIN,
        };
        String[] instructorParams = new String[] {
                Const.ParamsNames.SEARCH_KEY, "dummy",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        loginAsAdmin();
        verifyHttpParameterFailure(instructorParams);

        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        verifyHttpParameterFailure(adminParams);
    }

    @Test
    public void execute_adminSearchName_success() {
        loginAsAdmin();
        String[] accNameParams = new String[] {
                Const.ParamsNames.SEARCH_KEY, student1InCourse1.getName(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.ADMIN,
        };
        SearchStudentsAction a = getAction(accNameParams);
        JsonResult result = getJsonResult(a);
        StudentsData response = (StudentsData) result.getOutput();

        long expectedMatches = typicalBundle.students.values().stream()
                .filter(student -> student.getName().equals(student1InCourse1.getName()))
                .count();
        assertEquals((int) expectedMatches, response.getStudents().size());
    }

    @Test
    public void execute_adminSearchCourseId_success() {
        loginAsAdmin();
        String[] accCourseIdParams = new String[] {
                Const.ParamsNames.SEARCH_KEY, student1InCourse1.getCourseId(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.ADMIN,
        };
        SearchStudentsAction a = getAction(accCourseIdParams);
        JsonResult result = getJsonResult(a);
        StudentsData response = (StudentsData) result.getOutput();

        int expectedMatches = (int) typicalBundle.students.values().stream()
                .filter(student -> student.getCourseId().equals(student1InCourse1.getCourseId()))
                .count();
        assertEquals(expectedMatches, response.getStudents().size());
    }

    @Test
    public void execute_adminSearchEmail_success() {
        loginAsAdmin();
        String[] emailParams = new String[] {
                Const.ParamsNames.SEARCH_KEY, student1InCourse1.getEmail(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.ADMIN,
        };

        SearchStudentsAction a = getAction(emailParams);
        JsonResult result = getJsonResult(a);
        StudentsData response = (StudentsData) result.getOutput();

        assertEquals(4, response.getStudents().size());
    }

    @Test
    public void execute_adminSearchNoMatch_noMatch() {
        loginAsAdmin();
        String[] accNameParams = new String[] {
                Const.ParamsNames.SEARCH_KEY, "minuscoronavirus",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.ADMIN,
        };
        SearchStudentsAction a = getAction(accNameParams);
        JsonResult result = getJsonResult(a);
        StudentsData response = (StudentsData) result.getOutput();

        assertEquals(0, response.getStudents().size());
    }

    @Test
    public void execute_instructorSearchGoogleId_matchOnlyStudentsInCourse() {
        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        String[] googleIdParams = new String[] {
                Const.ParamsNames.SEARCH_KEY, "student1",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        SearchStudentsAction a = getAction(googleIdParams);
        JsonResult result = getJsonResult(a);
        StudentsData response = (StudentsData) result.getOutput();
        assertEquals(3, response.getStudents().size());
    }

    @Test
        public void execute_searchWithoutSearchService_shouldSucceed() {
        loginAsInstructor(instructor1OfCourse1.getGoogleId());
        String[] params = new String[] {
                Const.ParamsNames.SEARCH_KEY, "student1",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };
        SearchStudentsAction a = getAction(params);
        JsonResult result = getJsonResult(a);
        StudentsData output = (StudentsData) result.getOutput();

        assertEquals(3, output.getStudents().size());

        loginAsAdmin();
        params = new String[] {
                Const.ParamsNames.SEARCH_KEY, student1InCourse1.getEmail(),
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.ADMIN,
        };

        a = getAction(params);
        result = getJsonResult(a);
        output = (StudentsData) result.getOutput();

        assertEquals(4, output.getStudents().size());
    }

    @Override
    @Test
    protected void testAccessControl() throws InvalidParametersException, EntityAlreadyExistsException {
        verifyAccessibleForAdmin();
        Course course = typicalBundle.courses.get("course1");
        verifyOnlyInstructorsCanAccess(course);
    }
}
