package teammates.client.scripts.sql;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.googlecode.objectify.cmd.Query;

import teammates.common.util.HibernateUtil;
import teammates.storage.entity.Course;
import teammates.storage.entity.CourseStudent;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.Team;

/**
 * Data migration class for course entity.
 */
@SuppressWarnings("PMD")
public class DataMigrationForStudentSql extends
        DataMigrationEntitiesBaseScriptSql<Course, teammates.storage.sqlentity.Student> {

    public static void main(String[] args) {
        new DataMigrationForStudentSql().doOperationRemotely();
    }

    @Override
    protected Query<Course> getFilterQuery() {
        return ofy().load().type(Course.class);
    }

    @Override
    protected boolean isPreview() {
        return false;
    }

    /*
     * Sets the migration criteria used in isMigrationNeeded.
     */
    @Override
    protected void setMigrationCriteria() {
        // No migration criteria currently needed.
    }

    @Override
    protected boolean isMigrationNeeded(Course entity) {
        return true;
    }

    @Override
    protected void migrateEntity(Course oldCourse) throws Exception {
        HibernateUtil.beginTransaction();
        teammates.storage.sqlentity.Course newCourse =
                HibernateUtil.getReference(teammates.storage.sqlentity.Course.class, oldCourse.getUniqueId());
        Map<String, Account> accountMap = getAccountMap(oldCourse.getUniqueId());
        List<Team> teams = getTeams(newCourse);
        HibernateUtil.commitTransaction();

        getCourseStudents(oldCourse.getUniqueId())
                .forEach(oldStudent -> migrateStudent(oldStudent, newCourse, accountMap, teams));
    }

    private Map<String, Account> getAccountMap(String courseId) {
        return ofy()
                .load()
                .type(CourseStudent.class)
                .filter("courseId", courseId)
                .list()
                .stream()
                .map(CourseStudent::getGoogleId)
                .collect(Collectors.toMap(googleId -> googleId,
                        googleId -> HibernateUtil.getReferenceBySimpleNaturalId(Account.class, googleId)));
    }

    private List<Team> getTeams(teammates.storage.sqlentity.Course newCourse) {
        return newCourse.getSections().stream()
                .flatMap(section -> section.getTeams().stream())
                .collect(Collectors.toList());
    }

    private List<CourseStudent> getCourseStudents(String courseId) {
        return ofy()
                .load()
                .type(CourseStudent.class)
                .filter("courseId", courseId)
                .list();
    }

    private void migrateStudent(CourseStudent oldStudent, teammates.storage.sqlentity.Course course,
            Map<String, Account> accountMap, List<Team> teams) {

        Account account = accountMap.get(oldStudent.getGoogleId());

        Team team = teams.stream()
                .filter(t -> t.getName().equals(oldStudent.getTeamName()))
                .findFirst()
                .get();

        teammates.storage.sqlentity.Student newStudent = new teammates.storage.sqlentity.Student(
                course,
                oldStudent.getName(),
                oldStudent.getEmail(),
                oldStudent.getComments());

        newStudent.setAccount(account);
        newStudent.setTeam(team);
        newStudent.setCreatedAt(oldStudent.getCreatedAt());
        newStudent.setUpdatedAt(oldStudent.getUpdatedAt());
        newStudent.setRegKey(oldStudent.getRegistrationKey());

        saveEntityDeferred(newStudent);
    }
}
