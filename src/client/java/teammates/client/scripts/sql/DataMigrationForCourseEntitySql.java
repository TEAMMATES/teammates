package teammates.client.scripts.sql;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.googlecode.objectify.cmd.Query;

import teammates.storage.entity.Course;
import teammates.storage.entity.CourseStudent;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;

/**
 * Data migration class for course entity.
 */
@SuppressWarnings("PMD")
public class DataMigrationForCourseEntitySql extends
        DataMigrationEntitiesBaseScriptSql<teammates.storage.entity.Course, teammates.storage.sqlentity.BaseEntity> {

    public static void main(String[] args) {
        new DataMigrationForCourseEntitySql().doOperationRemotely();
    }

    @Override
    protected Query<Course> getFilterQuery() {
        return ofy().load().type(teammates.storage.entity.Course.class);
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
        teammates.storage.sqlentity.Course newCourse = createCourse(oldCourse);
        // TODO: add shutdown hook to save the entity
        // Runnable shutdownScript = () -> { cascadeDelete(newCourse)};
        // Runtime.getRuntime().addShutdownHook(new Thread(shutdownScript));

        migrateCourseEntity(newCourse);
        // verifyCourseEntity(newCourse);
        // markOldCourseAsMigrated(courseId)
        // Runtime.getRuntime().removeShutDownHook(new Thread(shutdownScript));
    }

    private void migrateCourseEntity(teammates.storage.sqlentity.Course newCourse) {
        Map<String, teammates.storage.sqlentity.Section> sectionNameToSectionMap = migrateSectionChain(newCourse);
        System.out.println(sectionNameToSectionMap); // To stop lint from complaining
        // TODO: Add mirgrateFeedbackChain
        // migrateFeedbackChain(sectionNameToSectionMap);
    }

    private Map<String, teammates.storage.sqlentity.Section> migrateSectionChain(
            teammates.storage.sqlentity.Course newCourse) {
        List<CourseStudent> oldStudents = ofy().load().type(CourseStudent.class).filter("courseId", newCourse.getId())
                .list();
        Map<String, teammates.storage.sqlentity.Section> sections = new HashMap<>();
        Map<String, List<CourseStudent>> sectionToStuMap = oldStudents.stream()
                .collect(Collectors.groupingBy(CourseStudent::getSectionName));

        for (Map.Entry<String, List<CourseStudent>> entry : sectionToStuMap.entrySet()) {
            String sectionName = entry.getKey();
            List<CourseStudent> stuList = entry.getValue();
            teammates.storage.sqlentity.Section newSection = createSection(newCourse, sectionName);
            sections.put(sectionName, newSection);
            saveEntityDeferred(newSection);
            migrateTeams(newCourse, newSection, stuList);   
        }
        return sections;
    }

    private void migrateTeams(teammates.storage.sqlentity.Course newCourse,
            teammates.storage.sqlentity.Section newSection, List<CourseStudent> studentsInSection) {
        Map<String, List<CourseStudent>> teamNameToStuMap = studentsInSection.stream()
                .collect(Collectors.groupingBy(CourseStudent::getTeamName));
        for (Map.Entry<String, List<CourseStudent>> entry : teamNameToStuMap.entrySet()) {
            String teamName = entry.getKey();
            List<CourseStudent> stuList = entry.getValue();
            teammates.storage.sqlentity.Team newTeam = createTeam(newSection, teamName);
            saveEntityDeferred(newTeam);
            migrateStudents(newCourse, newTeam, stuList);
        }
    }

    private void migrateStudents(teammates.storage.sqlentity.Course newCourse, teammates.storage.sqlentity.Team newTeam,
            List<CourseStudent> studentsInTeam) {
        for (CourseStudent oldStudent : studentsInTeam) {
            teammates.storage.sqlentity.Student newStudent = createStudent(newCourse, newTeam, oldStudent);
            saveEntityDeferred(newStudent);
        }
    }

    private teammates.storage.sqlentity.Course createCourse(Course oldCourse) {
        teammates.storage.sqlentity.Course newCourse = new teammates.storage.sqlentity.Course(
                oldCourse.getUniqueId(),
                oldCourse.getName(),
                oldCourse.getTimeZone(),
                oldCourse.getInstitute());
        newCourse.setDeletedAt(oldCourse.getDeletedAt());
        newCourse.setCreatedAt(oldCourse.getCreatedAt());

        saveEntityDeferred(newCourse);
        return newCourse;
    }

    private teammates.storage.sqlentity.Section createSection(teammates.storage.sqlentity.Course newCourse,
            String sectionName) {
        // if (sectionName.equals(Const.DEFAULT_SECTION)) {
        // return Const.DEFAULT_SQL_SECTION;
        // }
        Section newSection = new Section(newCourse, sectionName);
        newSection.setCreatedAt(Instant.now());
        return newSection;
    }

    private teammates.storage.sqlentity.Team createTeam(teammates.storage.sqlentity.Section section, String teamName) {
        Team newTeam = new teammates.storage.sqlentity.Team(section, teamName);
        newTeam.setCreatedAt(Instant.now());
        return newTeam;
    }

    private Student createStudent(teammates.storage.sqlentity.Course newCourse,
            teammates.storage.sqlentity.Team newTeam,
            CourseStudent oldStudent) {
        Student newStudent = new Student(newCourse, oldStudent.getName(), oldStudent.getEmail(),
                oldStudent.getComments(), newTeam);
        newStudent.setUpdatedAt(oldStudent.getUpdatedAt());
        newStudent.setRegKey(oldStudent.getRegistrationKey());
        newStudent.setCreatedAt(oldStudent.getCreatedAt());

        return newStudent;
    }
}
