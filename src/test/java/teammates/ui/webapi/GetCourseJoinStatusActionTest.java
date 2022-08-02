package teammates.ui.webapi;

import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.ui.output.JoinStatus;

/**
 * SUT: {@link GetCourseJoinStatusAction}.
 */
public class GetCourseJoinStatusActionTest extends BaseActionTest<GetCourseJoinStatusAction> {

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
                logic.getStudentForEmail("idOfTypicalCourse1", "student1InCourse1@gmail.tmt").getKey();

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
                logic.getStudentForEmail("idOfUnregisteredCourse", "student1InUnregisteredCourse@gmail.tmt").getKey();

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
                logic.getInstructorForEmail("idOfTypicalCourse1", "instructor1@course1.tmt").getKey();

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
                logic.getInstructorForEmail("idOfTypicalCourse1", "instructorNotYetJoinedCourse1@email.tmt").getKey();

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

        ______TS("Normal case: account request status is APPROVED, instructor has not joined course");

        String approvedAccountRequestKey =
                logic.getAccountRequest("approvedUnregisteredInstructor1@tmt.tmt", "TMT, Singapore").getRegistrationKey();

        params = new String[] {
                Const.ParamsNames.REGKEY, approvedAccountRequestKey,
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
                Const.ParamsNames.IS_CREATING_ACCOUNT, "true",
        };

        getCourseJoinStatusAction = getAction(params);
        result = getJsonResult(getCourseJoinStatusAction);

        output = (JoinStatus) result.getOutput();
        assertFalse(output.getHasJoined());

        ______TS("Normal case: account request status is REGISTERED, instructor has joined course");

        String registeredAccountRequestKey =
                logic.getAccountRequest("instr1@course1.tmt", "TEAMMATES Test Institute 1").getRegistrationKey();

        params = new String[] {
                Const.ParamsNames.REGKEY, registeredAccountRequestKey,
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
                Const.ParamsNames.IS_CREATING_ACCOUNT, "true",
        };

        getCourseJoinStatusAction = getAction(params);
        result = getJsonResult(getCourseJoinStatusAction);

        output = (JoinStatus) result.getOutput();
        assertTrue(output.getHasJoined());

        ______TS("Failure case: account request status is SUBMITTED");

        String submittedAccountRequestKey =
                logic.getAccountRequest("submittedInstructor1@tmt.tmt", "TMT, Singapore").getRegistrationKey();

        params = new String[] {
                Const.ParamsNames.REGKEY, submittedAccountRequestKey,
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
                Const.ParamsNames.IS_CREATING_ACCOUNT, "true",
        };

        verifyInvalidOperation(params);

        ______TS("Failure case: account request status is REJECTED");

        String rejectedAccountRequestKey =
                logic.getAccountRequest("rejectedInstructor1@tmt.tmt", "TMT, Singapore").getRegistrationKey();

        params = new String[] {
                Const.ParamsNames.REGKEY, rejectedAccountRequestKey,
                Const.ParamsNames.ENTITY_TYPE, Const.EntityType.INSTRUCTOR,
                Const.ParamsNames.IS_CREATING_ACCOUNT, "true",
        };

        verifyInvalidOperation(params);

        ______TS("Failure case: account request registration key is not valid");

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

    @Override
    @Test
    protected void testAccessControl() {
        verifyAnyLoggedInUserCanAccess();
    }

}
