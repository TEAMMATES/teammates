package teammates.client.scripts.sql;

import java.util.Map;
import java.util.stream.Collectors;

import com.googlecode.objectify.cmd.Query;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;

import teammates.common.util.HibernateUtil;
import teammates.storage.entity.Course;
import teammates.storage.entity.CourseStudent;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Team;

/**
 * Data migration class for team entity.
 */
@SuppressWarnings("PMD")
public class DataMigrationForTeamSql extends
        DataMigrationEntitiesBaseScriptSql<Course, teammates.storage.sqlentity.Team> {

    public static void main(String[] args) {
        new DataMigrationForTeamSql().doOperationRemotely();
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
        teammates.storage.sqlentity.Course newCourse = getNewCourse(oldCourse.getUniqueId());
        Map<String, Section> sectionNameToSectionMap =
                newCourse.getSections().stream().collect(Collectors.toMap(Section::getName, s -> s));

        getTeamNameToSectionNameMap(oldCourse).forEach((teamName, sectionName) ->
                        HibernateUtil.persist(new Team(sectionNameToSectionMap.get(sectionName), teamName)));

        HibernateUtil.commitTransaction();
    }

    private teammates.storage.sqlentity.Course getNewCourse(String courseId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<teammates.storage.sqlentity.Course> cr = cb.createQuery(teammates.storage.sqlentity.Course.class);
        Root<teammates.storage.sqlentity.Course> courseRoot = cr.from(teammates.storage.sqlentity.Course.class);
        courseRoot.fetch("sections", JoinType.LEFT); // Fetch sections to avoid lazy-loading

        cr.select(courseRoot).where(cb.equal(courseRoot.get("id"), courseId));

        return HibernateUtil.createQuery(cr).getSingleResult();
    }

    private Map<String, String> getTeamNameToSectionNameMap(Course course) {
        return ofy()
                .load()
                .type(CourseStudent.class)
                .filter("courseId", course.getUniqueId())
                .list()
                .stream()
                .collect(Collectors.toMap(CourseStudent::getTeamName, CourseStudent::getSectionName));
    }

}
