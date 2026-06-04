package teammates.test.scenariobuilder;

import java.util.UUID;

import teammates.storage.entity.Account;
import teammates.storage.entity.Course;
import teammates.storage.entity.Student;
import teammates.storage.entity.Team;

/**
 * Builder for Student entities used in test scenarios.
 */
public class GivenStudent extends GivenBase<Student> {
    public GivenStudent(GivenData given, UUID studentId) {
        super(given);
        this.entity = defaultStudent(studentId);
    }

    /**
     * Sets the name for the student.
     */
    public GivenStudent name(String name) {
        entity.setName(name);
        return this;
    }

    /**
     * Sets the email for the student.
     */
    public GivenStudent email(String email) {
        entity.setEmail(email);
        return this;
    }

    /**
     * Sets the comments for the student.
     */
    public GivenStudent comments(String comments) {
        entity.setComments(comments);
        return this;
    }

    /**
     * Sets the course for the student.
     */
    public GivenStudent course(String courseAlias) {
        assert entity.getCourse() == null : "Course has already been set for this student";
        Course course = given.getOrCreate(courseAlias, given.dataBundle.courses, given::course);
        entity.setCourse(course);
        return this;
    }

    /**
     * Sets the team for the student.
     */
    public GivenStudent team(String teamAlias) {
        assert entity.getTeam() == null : "Team has already been set for this student";
        if (entity.getCourse() == null) {
            this.course(GivenCourse.getDefaultAlias());
        }

        String courseAlias = given.getAlias(entity.getCourse());
        Team team = given.getOrCreate(teamAlias, given.dataBundle.teams, (String tAlias) -> {
            given.team(tAlias, t -> t.course(courseAlias));
        });
        team.addUser(entity);
        return this;
    }

    /**
     * Sets the team with the specified section for the student.
     */
    public GivenStudent section(String sectionAlias) {
        assert entity.getTeam() == null : "Team has already been set for this student";
        if (entity.getCourse() == null) {
            this.course(GivenCourse.getDefaultAlias());
        }

        String courseAlias = given.getAlias(entity.getCourse());
        given.getOrCreate(sectionAlias, given.dataBundle.sections, (String sAlias) -> {
            given.section(sAlias, sect -> sect.course(courseAlias));
        });

        String teamAlias = GivenTeam.getDefaultAlias(courseAlias, sectionAlias, "default");
        given.getOrCreate(teamAlias, given.dataBundle.teams, (String tAlias) -> {
            given.team(tAlias, t -> t.section(sectionAlias));
        });

        return this;
    }

    /**
     * Sets the account for the student.
     */
    public GivenStudent account(String accountAlias) {
        assert entity.getAccount() == null : "Account has already been set for this student";
        Account account = given.getOrCreate(accountAlias, given.dataBundle.accounts, given::account);
        entity.setAccount(account);
        return this;
    }

    /**
     * Sets no account for the student.
     */
    public GivenStudent noAccount() {
        entity.setAccount(null);
        return this;
    }

    @Override
    void ensureConsistent() {
        if (entity.getCourseId() == null) {
            String courseAlias = GivenCourse.getDefaultAlias();
            this.course(courseAlias);
        }

        if (entity.getTeamId() == null) {
            String courseAlias = given.getAlias(entity.getCourse());
            String sectionAlias = GivenSection.getDefaultAlias(courseAlias);
            String teamAlias = GivenTeam.getDefaultAlias(courseAlias, sectionAlias, "default");
            this.team(teamAlias);
        }
    }

    private Student defaultStudent(UUID studentId) {
        String name = "name:" + studentId.toString();
        String email = studentId.toString() + "@teammates.tmt";
        String comments = "";
        Student s = new Student(name, email, comments);
        s.setId(studentId);
        return s;
    }

}
