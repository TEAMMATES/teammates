package teammates.test.scenariobuilder;

import java.util.UUID;

import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;

/**
 * Builder for Instructor entities used in test scenarios.
 */
public class GivenInstructor extends GivenBase<Instructor> {
    public GivenInstructor(GivenData given, UUID instructorId) {
        super(given);
        this.entity = defaultInstructor(instructorId);
    }

    /**
     * Sets the name for the instructor.
     */
    public GivenInstructor name(String name) {
        entity.setName(name);
        return this;
    }

    /**
     * Sets the email for the instructor.
     */
    public GivenInstructor email(String email) {
        entity.setEmail(email);
        return this;
    }

    /**
     * Sets the course for the instructor.
     */
    public GivenInstructor course(String courseAlias) {
        assert entity.getCourse() == null : "Course has already been set for this instructor";
        Course course = given.getOrCreate(courseAlias, given.dataBundle.courses, given::course);
        entity.setCourse(course);
        return this;
    }

    /**
     * Sets whether the instructor is displayed to students.
     */
    public GivenInstructor isDisplayedToStudents(boolean isDisplayedToStudents) {
        entity.setDisplayedToStudents(isDisplayedToStudents);
        return this;
    }

    /**
     * Sets the role for the instructor to co-owner.
     */
    public GivenInstructor coOwner() {
        entity.setRole(InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        entity.setPrivileges(
                new InstructorPrivileges(InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER.getRoleName()));
        return this;
    }

    /**
     * Sets the role for the instructor to manager.
     */
    public GivenInstructor manager() {
        entity.setRole(InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_MANAGER);
        entity.setPrivileges(
                new InstructorPrivileges(InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_MANAGER.getRoleName()));
        return this;
    }

    /**
     * Sets the role for the instructor to observer.
     */
    public GivenInstructor observer() {
        entity.setRole(InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_OBSERVER);
        entity.setPrivileges(
                new InstructorPrivileges(InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_OBSERVER.getRoleName()));
        return this;
    }

    /**
     * Sets the role for the instructor to tutor.
     */
    public GivenInstructor tutor() {
        entity.setRole(InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_TUTOR);
        entity.setPrivileges(
                new InstructorPrivileges(InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_TUTOR.getRoleName()));
        return this;
    }

    /**
     * Sets the role for the instructor to custom with the specified privileges.
     */
    public GivenInstructor custom(InstructorPrivileges privileges) {
        entity.setRole(InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_CUSTOM);
        entity.setPrivileges(privileges);
        return this;
    }

    @Override
    void ensureConsistent() {
        if (entity.getCourseId() == null) {
            String courseAlias = GivenCourse.getDefaultAlias();
            given.course(courseAlias);
        }
    }

    private Instructor defaultInstructor(UUID instructorId) {
        String name = "name:" + instructorId.toString();
        String email = instructorId.toString() + "@teammates.tmt";
        boolean isDisplayedToStudents = true;
        String displayName = name;
        InstructorPermissionRole role = InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        InstructorPrivileges privileges = new InstructorPrivileges(role.getRoleName());
        Instructor i = new Instructor(name, email, isDisplayedToStudents, displayName, role, privileges);
        i.setId(instructorId);
        return i;
    }

}
