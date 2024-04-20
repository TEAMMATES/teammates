package teammates.client.scripts.sql;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.InstructorPrivilegesLegacy;
import teammates.common.util.HibernateUtil;
import teammates.common.util.JsonUtils;
import teammates.storage.entity.Course;
import teammates.storage.entity.CourseStudent;
import teammates.storage.entity.DeadlineExtension;
import teammates.storage.entity.Instructor;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

/**
 * Class for verifying account attributes.
 */
@SuppressWarnings({ "PMD", "deprecation" })
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
            return verifyCourse(sqlEntity, datastoreEntity) && verifySectionChain(sqlEntity)
                    && verifyInstructors(sqlEntity) && verifyDataExtensions(sqlEntity);
        } catch (IllegalArgumentException iae) {
            return false;
        }
    }

    private boolean verifyCourse(teammates.storage.sqlentity.Course sqlEntity, Course datastoreEntity) {
        return sqlEntity.getId().equals(datastoreEntity.getUniqueId())
                && sqlEntity.getName().equals(datastoreEntity.getName())
                && sqlEntity.getTimeZone().equals(datastoreEntity.getTimeZone())
                && sqlEntity.getInstitute().equals(datastoreEntity.getInstitute())
                && sqlEntity.getCreatedAt().equals(datastoreEntity.getCreatedAt())
                && datastoreEntity.getDeletedAt() == null ? sqlEntity.getDeletedAt() == null
                        : sqlEntity.getDeletedAt().equals(datastoreEntity.getDeletedAt());
    }

    // Verify Section chain ----------------------------
    private boolean verifySectionChain(teammates.storage.sqlentity.Course newCourse) {
        // Get old and new students
        List<CourseStudent> oldStudents = ofy().load().type(CourseStudent.class).filter("courseId", newCourse.getId())
                .list();
        List<Student> newStudents = getNewStudents(newCourse.getId());

        // Group students by section
        Map<String, List<CourseStudent>> sectionToOldStuMap = oldStudents.stream()
                .collect(Collectors.groupingBy(CourseStudent::getSectionName));
        Map<String, List<Student>> sectionToNewStuMap = newStudents.stream()
                .collect(Collectors.groupingBy(Student::getSectionName));

        List<Section> newSection = newCourse.getSections();

        boolean isNotSectionsCountEqual = newSection.size() != sectionToOldStuMap.size()
                || newSection.size() != sectionToNewStuMap.size();
        if (isNotSectionsCountEqual) {
            return false;
        }

        return newSection.stream().allMatch(section -> {
            List<CourseStudent> oldSectionStudents = sectionToOldStuMap.get(section.getName());
            List<Student> newSectionStudents = sectionToNewStuMap.get(section.getName());

            // If either of the sectionStudent is null,
            // then section is not present in the corresponding datastore or sql
            // which means a possible migration error
            boolean sectionNameNotPresent = oldSectionStudents == null || newSectionStudents == null;
            if (sectionNameNotPresent) {
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

        List<Team> newTeams = newSection.getTeams();

        boolean isNotTeamCountEqual = newTeams.size() != teamNameToNewStuMap.size()
                || newTeams.size() != teamNameToOldStuMap.size();
        if (isNotTeamCountEqual) {
            return false;
        }

        return newTeams.stream().allMatch(team -> {
            List<CourseStudent> oldTeamStudents = teamNameToOldStuMap.get(team.getName());
            List<Student> newTeamStudents = teamNameToNewStuMap.get(team.getName());

            // If either of the teamStudent is null,
            // then team is not present in the corresponding datastore or sql
            // which means a possible migration error
            boolean teamNameNotPresent = oldTeamStudents == null || newTeamStudents == null;
            if (teamNameNotPresent) {
                return false;
            }
            return verifyStudents(oldTeamStudents, newTeamStudents);
        });
    }

    private boolean verifyStudents(
            List<CourseStudent> oldTeamStudents, List<Student> newTeamStudents) {
        if (oldTeamStudents.size() != newTeamStudents.size()) {
            return false;
        }
        oldTeamStudents.sort((a, b) -> a.getEmail().compareTo(b.getEmail()));
        newTeamStudents.sort((a, b) -> a.getEmail().compareTo(b.getEmail()));
        for (int i = 0; i < oldTeamStudents.size(); i++) {
            CourseStudent oldStudent = oldTeamStudents.get(i);
            Student newStudent = newTeamStudents.get(i);
            if (!verifyStudent(oldStudent, newStudent)) {
                return false;
            }
        }
        return true;
    }

    private boolean verifyStudent(CourseStudent oldStudent,
            Student newStudent) {
        return newStudent.getName().equals(oldStudent.getName())
                && newStudent.getEmail().equals(oldStudent.getEmail())
                && newStudent.getComments().equals(oldStudent.getComments())
                && newStudent.getUpdatedAt().equals(oldStudent.getUpdatedAt())
                && newStudent.getCreatedAt().equals(oldStudent.getCreatedAt())
                && newStudent.getRegKey().equals(oldStudent.getRegistrationKey());
    }

    // Verify Instructor ----------------------------
    private boolean verifyInstructors(teammates.storage.sqlentity.Course newCourse) {
        List<teammates.storage.sqlentity.Instructor> newInstructors = getNewInstructors(newCourse.getId());
        List<Instructor> oldInstructors = ofy().load().type(Instructor.class).filter("courseId", newCourse.getId())
                .list();
        newInstructors.sort((a, b) -> a.getEmail().compareTo(b.getEmail()));
        oldInstructors.sort((a, b) -> a.getEmail().compareTo(b.getEmail()));
        for (int i = 0; i < oldInstructors.size(); i++) {
            Instructor oldInstructor = oldInstructors.get(i);
            teammates.storage.sqlentity.Instructor newInstructor = newInstructors.get(i);
            if (!verifyInstructor(oldInstructor, newInstructor)) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("deprecation")
    private boolean verifyInstructor(Instructor oldInstructor,
            teammates.storage.sqlentity.Instructor newInstructor) {
        InstructorPrivileges oldPrivileges;
        if (oldInstructor.getInstructorPrivilegesAsText() == null) {
            oldPrivileges = new InstructorPrivileges(oldInstructor.getRole());
        } else {
            InstructorPrivilegesLegacy privilegesLegacy = JsonUtils
                    .fromJson(oldInstructor.getInstructorPrivilegesAsText(), InstructorPrivilegesLegacy.class);
            oldPrivileges = new InstructorPrivileges(privilegesLegacy);
        }

        return newInstructor.getName().equals(oldInstructor.getName())
                && newInstructor.getEmail().equals(oldInstructor.getEmail())
                && newInstructor.getRole().getRoleName().equals(oldInstructor.getRole())
                && newInstructor.getRegKey().equals(oldInstructor.getRegistrationKey())
                && newInstructor.getDisplayName().equals(oldInstructor.getDisplayedName())
                && newInstructor.getPrivileges().equals(oldPrivileges)
                && newInstructor.isDisplayedToStudents() == oldInstructor.isDisplayedToStudents()
                && newInstructor.getCreatedAt().equals(oldInstructor.getCreatedAt())
                && newInstructor.getUpdatedAt().equals(oldInstructor.getUpdatedAt());

    }

    // Verify DataExtension ----------------------------
    private boolean verifyDataExtensions(teammates.storage.sqlentity.Course newCourse) {
        List<teammates.storage.sqlentity.DeadlineExtension> newDeadlineExt = getNewDeadlineExt(newCourse.getId());
        List<DeadlineExtension> oldDeadlineExt = ofy().load()
                .type(DeadlineExtension.class).filter("courseId", newCourse.getId()).list();
        newDeadlineExt.sort((a, b) -> a.getId().compareTo(b.getId()));
        oldDeadlineExt.sort((a, b) -> a.getId().compareTo(b.getId()));
        for (int i = 0; i < oldDeadlineExt.size(); i++) {
            DeadlineExtension oldDeadline = oldDeadlineExt.get(i);
            teammates.storage.sqlentity.DeadlineExtension newDeadline = newDeadlineExt.get(i);
            if (!verifyDeadlineExtension(oldDeadline, newDeadline)) {
                return false;
            }
        }
        return true;
    }

    private boolean verifyDeadlineExtension(DeadlineExtension oldDeadline,
            teammates.storage.sqlentity.DeadlineExtension newDeadline) {
        return newDeadline.getFeedbackSession().getName().equals(oldDeadline.getFeedbackSessionName())
                && newDeadline.getUser().getEmail().equals(oldDeadline.getUserEmail())
                && newDeadline.getEndTime().equals(oldDeadline.getEndTime())
                && newDeadline.isClosingSoonEmailSent() == oldDeadline.getSentClosingEmail()
                && newDeadline.getUpdatedAt().equals(oldDeadline.getUpdatedAt())
                && newDeadline.getCreatedAt().equals(oldDeadline.getCreatedAt());
    }

    // Verify Get methods ----------------------------
    private List<Student> getNewStudents(String courseId) {
        HibernateUtil.beginTransaction();
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<teammates.storage.sqlentity.Student> cr = cb
                .createQuery(teammates.storage.sqlentity.Student.class);
        Root<teammates.storage.sqlentity.Student> courseRoot = cr.from(teammates.storage.sqlentity.Student.class);
        cr.select(courseRoot).where(cb.equal(courseRoot.get("courseId"), courseId));
        List<Student> newStudents = HibernateUtil.createQuery(cr).getResultList();
        HibernateUtil.commitTransaction();
        return newStudents;
    }

    private List<teammates.storage.sqlentity.Instructor> getNewInstructors(String courseId) {
        HibernateUtil.beginTransaction();
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<teammates.storage.sqlentity.Instructor> cr = cb
                .createQuery(teammates.storage.sqlentity.Instructor.class);
        Root<teammates.storage.sqlentity.Instructor> courseRoot = cr.from(teammates.storage.sqlentity.Instructor.class);
        cr.select(courseRoot).where(cb.equal(courseRoot.get("courseId"), courseId));
        List<teammates.storage.sqlentity.Instructor> newInstructors = HibernateUtil.createQuery(cr).getResultList();
        HibernateUtil.commitTransaction();
        return newInstructors;
    }

    private List<teammates.storage.sqlentity.DeadlineExtension> getNewDeadlineExt(String courseId) {
        HibernateUtil.beginTransaction();
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<teammates.storage.sqlentity.DeadlineExtension> cr = cb
                .createQuery(teammates.storage.sqlentity.DeadlineExtension.class);
        Root<teammates.storage.sqlentity.DeadlineExtension> courseRoot = cr
                .from(teammates.storage.sqlentity.DeadlineExtension.class);
        cr.select(courseRoot).where(cb.equal(courseRoot.get("courseId"), courseId));
        List<teammates.storage.sqlentity.DeadlineExtension> newDeadlineExt = HibernateUtil.createQuery(cr)
                .getResultList();
        HibernateUtil.commitTransaction();
        return newDeadlineExt;
    }
}
