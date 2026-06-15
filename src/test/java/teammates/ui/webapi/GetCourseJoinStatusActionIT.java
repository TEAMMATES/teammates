package teammates.ui.webapi;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.GroupNames;
import teammates.ui.output.JoinStatus;

/**
 * SUT: {@link GetCourseJoinStatusAction}.
 */
public class GetCourseJoinStatusActionIT extends BaseActionIT<GetCourseJoinStatusAction> {

    @BeforeMethod(alwaysRun = true)
    protected void setUp() {
        persistDataBundle(getTypicalDataBundle());
    }

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.JOIN;
    }

    @Override
    protected String getRequestMethod() {
        return GET;
    }

    @Override
    @Test(groups = GroupNames.INTEGRATION)
    protected void testExecute() {

        loginAsUnregistered("unreg.user");

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();

        ______TS("Normal case: student is already registered");
        String registeredStudentKey = inTransaction(() ->
                logic.getStudentForEmail("course-1", "student1@teammates.tmt").getRegKey());

        String[] params = new String[] {
                Const.ParamsNames.REGKEY, registeredStudentKey,
        };

        GetCourseJoinStatusAction getCourseJoinStatusAction = getAction(params);
        JsonResult result = getJsonResult(getCourseJoinStatusAction);

        JoinStatus output = (JoinStatus) result.getOutput();
        assertTrue(output.getHasJoined());

        ______TS("Normal case: student is not registered");
        String unregisteredStudentKey = inTransaction(() ->
                logic.getStudentForEmail("course-1", "unregisteredstudentincourse1@teammates.tmt").getRegKey());

        params = new String[] {
                Const.ParamsNames.REGKEY, unregisteredStudentKey,
        };

        getCourseJoinStatusAction = getAction(params);
        result = getJsonResult(getCourseJoinStatusAction);

        output = (JoinStatus) result.getOutput();
        assertFalse(output.getHasJoined());

        ______TS("Failure case: regkey is not valid for student");

        params = new String[] {
                Const.ParamsNames.REGKEY, "ANXKJZNZXNJCZXKJDNKSDA",
        };

        verifyEntityNotFound(params);

        ______TS("Normal case: instructor is already registered");

        String registeredInstructorKey = inTransaction(() ->
                logic.getInstructorForEmail("course-1", "instr1@teammates.tmt").getRegKey());

        params = new String[] {
                Const.ParamsNames.REGKEY, registeredInstructorKey,
        };

        getCourseJoinStatusAction = getAction(params);
        result = getJsonResult(getCourseJoinStatusAction);

        output = (JoinStatus) result.getOutput();
        assertTrue(output.getHasJoined());

        ______TS("Normal case: instructor is not registered");

        String unregisteredInstructorKey = inTransaction(() ->
                logic.getInstructorForEmail("course-1", "unregisteredinstructor@teammates.tmt").getRegKey());

        params = new String[] {
                Const.ParamsNames.REGKEY, unregisteredInstructorKey,
        };

        getCourseJoinStatusAction = getAction(params);
        result = getJsonResult(getCourseJoinStatusAction);

        output = (JoinStatus) result.getOutput();
        assertFalse(output.getHasJoined());

        ______TS("Failure case: regkey is not valid for instructor");

        params = new String[] {
                Const.ParamsNames.REGKEY, "ANXKJZNZXNJCZXKJDNKSDA",
        };

        verifyEntityNotFound(params);

    }

    @Test(groups = GroupNames.INTEGRATION)
    @Override
    protected void testAccessControl() throws Exception {
        verifyAnyLoggedInUserCanAccess();
    }
}
