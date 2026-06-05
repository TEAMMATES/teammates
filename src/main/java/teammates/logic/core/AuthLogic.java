package teammates.logic.core;

import teammates.common.datatransfer.AuthContext;
import teammates.storage.entity.Account;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.ui.webapi.AuthType;

/**
 * Handles operations related to authentication and authorization.
 */
public final class AuthLogic {
    private static final AuthLogic instance = new AuthLogic();

    private UsersLogic usersLogic;

    private AuthLogic() {
        // prevent initialization
    }

    void initLogicDependencies(UsersLogic usersLogic) {
        this.usersLogic = usersLogic;
    }

    public static AuthLogic inst() {
        return instance;
    }

    /**
     * Returns the student associated with the given authentication context and
     * course ID.
     *
     * <p>
     * If the authentication type is REG_KEY, it returns the unregistered student
     * from the authentication context.
     * Otherwise, it retrieves the student from the database linked to the account
     * and course ID.
     */
    public Student getStudentFromAuthContext(AuthContext authContext, String courseId) {
        if (authContext.authType() == AuthType.REG_KEY) {
            return getUnregisteredStudent(authContext);
        }

        Account account = authContext.account();
        if (account == null) {
            return null;
        }

        return usersLogic.getStudentByAccountId(account.getId(), courseId);
    }

    /**
     * Returns the instructor associated with the given authentication context and
     * course ID.
     *
     * <p>
     * If the authentication type is REG_KEY, it returns the unregistered instructor
     * from the authentication context. Otherwise, it retrieves the instructor from
     * the database linked to the account
     * and course ID.
     */
    public Instructor getInstructorFromAuthContext(AuthContext authContext, String courseId) {
        if (authContext.authType() == AuthType.REG_KEY) {
            return getUnregisteredInstructor(authContext);
        }

        Account account = authContext.account();
        if (account == null) {
            return null;
        }

        return usersLogic.getInstructorByAccountId(account.getId(), courseId);
    }

    private Student getUnregisteredStudent(AuthContext authContext) {
        if (authContext.regKeyUser() instanceof Student student) {
            return student;
        }
        return null;
    }

    private Instructor getUnregisteredInstructor(AuthContext authContext) {
        if (authContext.regKeyUser() instanceof Instructor instructor) {
            return instructor;
        }
        return null;
    }
}
