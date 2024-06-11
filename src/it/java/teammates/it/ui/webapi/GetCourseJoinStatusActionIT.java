package teammates.it.ui.webapi;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.AccountRequest;
import teammates.ui.output.JoinStatus;
import teammates.ui.webapi.GetCourseJoinStatusAction;
import teammates.ui.webapi.JsonResult;

/**
 * SUT: {@link GetCourseJoinStatusAction}.
 */
public class GetCourseJoinStatusActionIT extends BaseActionIT<GetCourseJoinStatusAction> {

    @Override
    @BeforeMethod
    protected void setUp() throws Exception {
        super.setUp();
        this.typicalBundle = loadSqlDataBundle("/typicalDataBundle.json");
        persistDataBundle(typicalBundle);
        HibernateUtil.flushSession();
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
    @Test
    protected void testExecute() {

        loginAsUnregistered("unreg.user");

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();
        verifyHttpParameterFailure(
                Const.ParamsNames.REGKEY, "regkey"
        );
        verifyHttpParameterFailure(
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT
        );

        ______TS("Normal case: student is already registered");
        String registeredStudentKey =
                logic.getStudentForEmail("course-1", "student1@teammates.tmt").getRegKey();

        String[] params = new String[] {
                Const.ParamsNames.REGKEY, registeredStudentKey,
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        GetCourseJoinStatusAction getCourseJoinStatusAction = getAction(params);
        JsonResult result = getJsonResult(getCourseJoinStatusAction);

        JoinStatus output = (JoinStatus) result.getOutput();
        assertTrue(output.getHasJoined());

        ______TS("Normal case: student is not registered");
        String unregisteredStudentKey =
                logic.getStudentForEmail("course-1", "unregisteredStudentInCourse1@teammates.tmt").getRegKey();

        params = new String[] {
                Const.ParamsNames.REGKEY, unregisteredStudentKey,
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        getCourseJoinStatusAction = getAction(params);
        result = getJsonResult(getCourseJoinStatusAction);

        output = (JoinStatus) result.getOutput();
        assertFalse(output.getHasJoined());

        ______TS("Failure case: regkey is not valid for student");

        params = new String[] {
                Const.ParamsNames.REGKEY, "ANXKJZNZXNJCZXKJDNKSDA",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.STUDENT,
        };

        verifyEntityNotFound(params);

        ______TS("Normal case: instructor is already registered");

        String registeredInstructorKey =
                logic.getInstructorForEmail("course-1", "instr1@teammates.tmt").getRegKey();

        params = new String[] {
                Const.ParamsNames.REGKEY, registeredInstructorKey,
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        getCourseJoinStatusAction = getAction(params);
        result = getJsonResult(getCourseJoinStatusAction);

        output = (JoinStatus) result.getOutput();
        assertTrue(output.getHasJoined());

        ______TS("Normal case: instructor is not registered");

        String unregisteredInstructorKey =
                logic.getInstructorForEmail("course-1", "unregisteredInstructor@teammates.tmt").getRegKey();

        params = new String[] {
                Const.ParamsNames.REGKEY, unregisteredInstructorKey,
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        getCourseJoinStatusAction = getAction(params);
        result = getJsonResult(getCourseJoinStatusAction);

        output = (JoinStatus) result.getOutput();
        assertFalse(output.getHasJoined());

        ______TS("Failure case: regkey is not valid for instructor");

        params = new String[] {
                Const.ParamsNames.REGKEY, "ANXKJZNZXNJCZXKJDNKSDA",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
        };

        verifyEntityNotFound(params);

        ______TS("Normal case: account request not used, instructor has not joined course");

        AccountRequest unregisteredInstructor1AccountRequest = typicalBundle.accountRequests.get("unregisteredInstructor1");
        String accountRequestNotUsedKey = unregisteredInstructor1AccountRequest.getRegistrationKey();

        params = new String[] {
                Const.ParamsNames.REGKEY, accountRequestNotUsedKey,
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
                Const.ParamsNames.IS_CREATING_ACCOUNT, "true",
        };

        getCourseJoinStatusAction = getAction(params);
        result = getJsonResult(getCourseJoinStatusAction);

        output = (JoinStatus) result.getOutput();
        assertFalse(output.getHasJoined());

        ______TS("Normal case: account request already used, instructor has joined course");

        AccountRequest instructor1AccountRequest = typicalBundle.accountRequests.get("instructor1");
        String accountRequestUsedKey = instructor1AccountRequest.getRegistrationKey();

        params = new String[] {
                Const.ParamsNames.REGKEY, accountRequestUsedKey,
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
                Const.ParamsNames.IS_CREATING_ACCOUNT, "true",
        };

        getCourseJoinStatusAction = getAction(params);
        result = getJsonResult(getCourseJoinStatusAction);

        output = (JoinStatus) result.getOutput();
        assertTrue(output.getHasJoined());

        ______TS("Failure case: account request regkey is not valid");

        params = new String[] {
                Const.ParamsNames.REGKEY, "invalid-registration-key",
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
                Const.ParamsNames.IS_CREATING_ACCOUNT, "true",
        };

        verifyEntityNotFound(params);

        ______TS("Failure case: invalid entity type");

        params = new String[] {
                Const.ParamsNames.REGKEY, unregisteredStudentKey,
                Const.ParamsNames.ENTITY_TYPE, "unknown",
        };

        verifyHttpParameterFailure(params);
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        verifyAnyLoggedInUserCanAccess();
    }
}
