package teammates.logic.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.testng.annotations.Test;

import teammates.common.datatransfer.AuthContext;
import teammates.storage.entity.Account;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.test.GroupNames;
import teammates.ui.webapi.AuthType;

/**
 * Tests for {@link AuthLogic}.
 */
public class AuthLogicTest extends BaseLogicTest {
    private final AuthLogic authLogic = AuthLogic.inst();

    @Test(groups = GroupNames.LOGIC)
    public void getStudentFromAuthContext_regKeyAuthContext_returnsUnregisteredStudent() {
        var course = given.course("course");
        var expectedStudent = given.student("student", s -> s.course(course.alias()));
        persistGivenData(given);

        Student actualStudent = inTransaction(() -> {
            AuthContext authContext = buildRegKeyAuthContext(getEntity(Student.class, expectedStudent.id()));
            return authLogic.getStudentFromAuthContext(authContext, course.id());
        });

        assertEquals(expectedStudent.id(), actualStudent.getId());
    }

    @Test(groups = GroupNames.LOGIC)
    public void getStudentFromAuthContext_accountAuthContext_returnsStudentFromDatabase() {
        var course = given.course("course");
        var account = given.account("account");
        var expectedStudent = given.student("student", s -> s.course(course.alias()).account(account.alias()));
        persistGivenData(given);

        Student actualStudent = inTransaction(() -> {
            AuthContext authContext = buildAccountAuthContext(getEntity(Account.class, account.id()));
            return authLogic.getStudentFromAuthContext(authContext, course.id());
        });

        assertEquals(expectedStudent.id(), actualStudent.getId());
    }

    @Test(groups = GroupNames.LOGIC)
    public void getStudentFromAuthContext_publicAuthContext_returnsNull() {
        var course = given.course("course");
        persistGivenData(given);

        Student actualStudent = inTransaction(() -> {
            AuthContext authContext = buildPublicAuthContext();
            return authLogic.getStudentFromAuthContext(authContext, course.id());
        });

        assertNull(actualStudent);
    }

    @Test(groups = GroupNames.LOGIC)
    public void getInstructorFromAuthContext_regKeyAuthContext_returnsNull() {
        var course = given.course("course");
        var expectedStudent = given.student("student", s -> s.course(course.alias()));
        persistGivenData(given);

        Instructor actualInstructor = inTransaction(() -> {
            AuthContext authContext = buildRegKeyAuthContext(getEntity(Student.class, expectedStudent.id()));
            return authLogic.getInstructorFromAuthContext(authContext, course.id());
        });

        assertNull(actualInstructor);
    }

    @Test(groups = GroupNames.LOGIC)
    public void getInstructorFromAuthContext_accountAuthContext_returnsInstructorFromDatabase() {
        var course = given.course("course");
        var account = given.account("account");
        var expectedInstructor = given.instructor("instructor", s -> s.course(course.alias()).account(account.alias()));
        persistGivenData(given);

        Instructor actualInstructor = inTransaction(() -> {
            AuthContext authContext = buildAccountAuthContext(getEntity(Account.class, account.id()));
            return authLogic.getInstructorFromAuthContext(authContext, course.id());
        });

        assertEquals(expectedInstructor.id(), actualInstructor.getId());
    }

    @Test(groups = GroupNames.LOGIC)
    public void getInstructorFromAuthContext_publicAuthContext_returnsNull() {
        var course = given.course("course");
        given.instructor("instructor", i -> i.course(course.alias()));
        persistGivenData(given);

        Instructor actualInstructor = inTransaction(() -> {
            AuthContext authContext = buildPublicAuthContext();
            return authLogic.getInstructorFromAuthContext(authContext, course.id());
        });

        assertNull(actualInstructor);
    }

    private AuthContext buildRegKeyAuthContext(Student student) {
        return new AuthContext(AuthType.REG_KEY, null, student, null, false, false);
    }

    private AuthContext buildAccountAuthContext(Account account) {
        return new AuthContext(AuthType.LOGGED_IN, account, null, null, false, false);
    }

    private AuthContext buildPublicAuthContext() {
        return new AuthContext(AuthType.PUBLIC, null, null, null, false, false);
    }
}
