package teammates.common.datatransfer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import teammates.common.datatransfer.LinkKey;
import teammates.storage.entity.Account;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.ui.webapi.AuthType;

/**
 * Encapsulates the context of a web API request, including the authentication
 * context and cached data for instructors and students.
 */
public class RequestContext {
    private final AuthContext authContext;
    private final Map<String, Instructor> instructorsByCourseId = new HashMap<>();
    private final Map<String, Student> studentsByCourseId = new HashMap<>();

    public RequestContext(AuthContext authContext) {
        this.authContext = authContext;
    }

    public AuthContext getAuthContext() {
        return authContext;
    }

    public AuthType getAuthType() {
        return authContext.authType();
    }

    public boolean isMaintainer() {
        return authContext.isMaintainer();
    }

    public boolean isAdmin() {
        return authContext.isAdmin();
    }

    public Account getAccount() {
        return authContext.account();
    }

    public Student getRegKeyUser() {
        return authContext.regKeyStudent();
    }

    public LinkKey getLinkKey() {
        return authContext.linkKey();
    }

    /**
     * Returns the instructor for the specified course ID, using the provided loader
     * function if not already cached.
     */
    public Instructor getInstructorForCourse(String courseId, BiFunction<AuthContext, String, Instructor> loader) {
        return instructorsByCourseId.computeIfAbsent(courseId, cId -> loader.apply(authContext, cId));
    }

    /**
     * Returns the student for the specified course ID, using the provided loader
     * function if not already cached.
     */
    public Student getStudentForCourse(String courseId, BiFunction<AuthContext, String, Student> loader) {
        return studentsByCourseId.computeIfAbsent(courseId, cId -> loader.apply(authContext, cId));
    }
}
