package teammates.test.scenariobuilder;

import java.util.UUID;

import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.storage.entity.Account;
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
     * Sets the default course for the instructor.
     */
    public GivenInstructor defaultCourse() {
        return course(GivenData.DEFAULT_COURSE_ALIAS);
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
        entity.setRole(InstructorPermissionRole.COOWNER);
        return this;
    }

    /**
     * Sets the role for the instructor to manager.
     */
    public GivenInstructor manager() {
        entity.setRole(InstructorPermissionRole.MANAGER);
        return this;
    }

    /**
     * Sets the role for the instructor to observer.
     */
    public GivenInstructor observer() {
        entity.setRole(InstructorPermissionRole.OBSERVER);
        return this;
    }

    /**
     * Sets the role for the instructor to tutor.
     */
    public GivenInstructor tutor() {
        entity.setRole(InstructorPermissionRole.TUTOR);
        return this;
    }

    /**
     * Sets the instructor to have no privileges.
     */
    public GivenInstructor noPrivileges() {
        entity.setRole(InstructorPermissionRole.CUSTOM);
        return this;
    }

    /**
     * Sets the role for the instructor to custom with the specified privileges.
     *
     * <p>The privileges are recorded in the data bundle's combined instructor privileges section
     * and expanded into the privilege tables when the bundle is persisted.
     */
    public GivenInstructor custom(InstructorPrivileges privileges) {
        entity.setRole(InstructorPermissionRole.CUSTOM);
        privileges.setInstructorId(entity.getId());
        given.dataBundle.instructorPrivileges.put(entity.getId().toString(), privileges);
        return this;
    }

    /**
     * Sets the account for the instructor.
     */
    public GivenInstructor account(String accountAlias) {
        assert entity.getAccount() == null : "Account has already been set for this instructor";
        Account account = given.getOrCreate(accountAlias, given.dataBundle.accounts, given::account);
        entity.setAccount(account);
        return this;
    }

    /**
     * Sets no account for the instructor.
     */
    public GivenInstructor noAccount() {
        entity.setAccount(null);
        return this;
    }

    @Override
    void ensureConsistent() {
        if (entity.getCourseId() == null) {
            String courseAlias = GivenCourse.getDefaultAlias();
            this.course(courseAlias);
        }
    }

    private Instructor defaultInstructor(UUID instructorId) {
        String name = "name:" + instructorId.toString();
        String email = instructorId.toString() + "@teammates.tmt";
        boolean isDisplayedToStudents = true;
        String displayName = name;
        InstructorPermissionRole role = InstructorPermissionRole.COOWNER;
        Instructor i = new Instructor(name, email, isDisplayedToStudents, displayName, role);
        i.setId(instructorId);
        return i;
    }

}
