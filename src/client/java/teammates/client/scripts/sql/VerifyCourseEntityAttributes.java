package teammates.client.scripts.sql;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import teammates.common.util.HibernateUtil;
import teammates.storage.entity.Course;
import teammates.storage.entity.CourseStudent;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

/**
 * Class for verifying account attributes.
 */
@SuppressWarnings("PMD")
public class VerifyCourseEntityAttributes
        extends VerifyNonCourseEntityAttributesBaseScript<Course, teammates.storage.sqlentity.Course> {

    public VerifyCourseEntityAttributes() {
        super(Course.class,
                teammates.storage.sqlentity.Course.class);
    }

    @Override
    protected String generateID(teammates.storage.sqlentity.Course sqlEntity) {
        return sqlEntity.getId();
    }

    public static void main(String[] args) {
        VerifyCourseEntityAttributes script = new VerifyCourseEntityAttributes();
        script.doOperationRemotely();
    }

    // Used for sql data migration
    @Override
    public boolean equals(teammates.storage.sqlentity.Course sqlEntity, Course datastoreEntity) {
        try {
            return verifyCourse(sqlEntity, datastoreEntity) && verifySectionChain(sqlEntity);
        } catch (IllegalArgumentException iae) {
            return false;
        }
    }

    private boolean verifyCourse(teammates.storage.sqlentity.Course sqlEntity, Course datastoreEntity) {
        return sqlEntity.getId().equals(datastoreEntity.getUniqueId())
                && sqlEntity.getName().equals(datastoreEntity.getName())
                && sqlEntity.getTimeZone().equals(datastoreEntity.getTimeZone())
                && sqlEntity.getInstitute().equals(datastoreEntity.getInstitute())
                // && sqlEntity.getCreatedAt().equals(datastoreEntity.getCreatedAt())
                && datastoreEntity.getDeletedAt() == null ? sqlEntity.getDeletedAt() == null
                        : sqlEntity.getDeletedAt().equals(datastoreEntity.getDeletedAt());
    }

    private boolean verifySectionChain(teammates.storage.sqlentity.Course sqlEntity) {
        // Get old and new students
        List<CourseStudent> oldStudents = ofy().load().type(CourseStudent.class).filter("courseId", sqlEntity.getId())
                .list();
        List<Student> newStudents = getCurrStudents(sqlEntity.getId());

        // Group students by section
        Map<String, List<CourseStudent>> sectionToOldStuMap = oldStudents.stream()
                .collect(Collectors.groupingBy(CourseStudent::getSectionName));
        Map<String, List<Student>> sectionToNewStuMap = newStudents.stream()
                .collect(Collectors.groupingBy(Student::getSectionName));

        List<Section> newSection = sqlEntity.getSections();

        boolean isSectionsCountEqual = newSection.size() != sectionToOldStuMap.size()
                || newSection.size() != sectionToNewStuMap.size();
        if (!isSectionsCountEqual) {
            return false;
        }

        return newSection.stream().allMatch(section -> {
            List<CourseStudent> oldSectionStudents = sectionToOldStuMap.get(section.getName());
            List<Student> newSectionStudents = sectionToNewStuMap.get(section.getName());

            // If sectionStudent is null, then section is not present in sql
            boolean isSectionNamePresent = oldSectionStudents != null && newSectionStudents != null;
            if (!isSectionNamePresent) {
                return false;
            }

            // Group students by team
            Map<String, List<CourseStudent>> teamNameToOldStuMap = oldSectionStudents.stream()
                    .collect(Collectors.groupingBy(CourseStudent::getTeamName));
            Map<String, List<Student>> teamNameToNewStuMap = newSectionStudents.stream()
                    .collect(Collectors.groupingBy(Student::getTeamName));
            return verifyTeams(section, teamNameToOldStuMap, teamNameToNewStuMap);
        });

    }

    private boolean verifyTeams(Section newSection,
            Map<String, List<CourseStudent>> teamNameToOldStuMap, Map<String, List<Student>> teamNameToNewStuMap) {

        List<Team> newTeam = newSection.getTeams();

        boolean isTeamCountEqual = newTeam.size() != teamNameToNewStuMap.size()
                || newTeam.size() != teamNameToOldStuMap.size();
        if (!isTeamCountEqual) {
            return false;
        }

        return newTeam.stream().allMatch(team -> {
            List<CourseStudent> oldTeamStudents = teamNameToOldStuMap.get(team.getName());
            List<Student> newTeamStudents = teamNameToNewStuMap.get(team.getName());

            // If teamStudents is null, then team is not present in sql
            boolean isTeamNamePresent = oldTeamStudents != null && newTeamStudents != null;
            if (!isTeamNamePresent) {
                return false;
            }
            return true; // verifyStudents(oldTeamStudents, newTeamStudents);
        });
    }
}
