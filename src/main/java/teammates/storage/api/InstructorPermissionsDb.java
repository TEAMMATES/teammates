package teammates.storage.api;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import teammates.common.util.HibernateUtil;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.InstructorCoursePrivilege;
import teammates.storage.entity.InstructorSectionPrivilege;
import teammates.storage.entity.InstructorSessionPrivilege;
import teammates.storage.entity.Section;

/**
 * Handles CRUD operations for the instructor privilege tables.
 *
 * @see InstructorCoursePrivilege
 * @see InstructorSectionPrivilege
 * @see InstructorSessionPrivilege
 */
public final class InstructorPermissionsDb {

    private static final InstructorPermissionsDb instance = new InstructorPermissionsDb();

    private InstructorPermissionsDb() {
        // prevent initialization
    }

    public static InstructorPermissionsDb inst() {
        return instance;
    }

    /**
     * Gets the course-level privileges of an instructor, or {@code null} if none are stored.
     */
    public InstructorCoursePrivilege getCoursePrivilege(UUID instructorId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<InstructorCoursePrivilege> cr = cb.createQuery(InstructorCoursePrivilege.class);
        Root<InstructorCoursePrivilege> root = cr.from(InstructorCoursePrivilege.class);
        cr.select(root).where(cb.equal(root.get("instructorId"), instructorId));

        return HibernateUtil.createQuery(cr).getResultStream().findFirst().orElse(null);
    }

    /**
     * Gets all section-level privileges of an instructor.
     */
    public List<InstructorSectionPrivilege> getSectionPrivileges(UUID instructorId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<InstructorSectionPrivilege> cr = cb.createQuery(InstructorSectionPrivilege.class);
        Root<InstructorSectionPrivilege> root = cr.from(InstructorSectionPrivilege.class);
        cr.select(root).where(cb.equal(root.get("instructorId"), instructorId));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Gets all session-level privileges of an instructor.
     */
    public List<InstructorSessionPrivilege> getSessionPrivileges(UUID instructorId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaQuery<InstructorSessionPrivilege> cr = cb.createQuery(InstructorSessionPrivilege.class);
        Root<InstructorSessionPrivilege> root = cr.from(InstructorSessionPrivilege.class);
        cr.select(root).where(cb.equal(root.get("instructorId"), instructorId));

        return HibernateUtil.createQuery(cr).getResultList();
    }

    /**
     * Persists a course-level privilege row.
     */
    public InstructorCoursePrivilege persistCoursePrivilege(InstructorCoursePrivilege coursePrivilege) {
        HibernateUtil.persist(coursePrivilege);
        return coursePrivilege;
    }

    /**
     * Persists a section-level privilege row.
     */
    public InstructorSectionPrivilege persistSectionPrivilege(InstructorSectionPrivilege sectionPrivilege) {
        HibernateUtil.persist(sectionPrivilege);
        return sectionPrivilege;
    }

    /**
     * Persists a session-level privilege row.
     */
    public InstructorSessionPrivilege persistSessionPrivilege(InstructorSessionPrivilege sessionPrivilege) {
        HibernateUtil.persist(sessionPrivilege);
        return sessionPrivilege;
    }

    /**
     * Returns a reference to the section with the given id without loading it from the database.
     */
    public Section getSectionReference(UUID sectionId) {
        return HibernateUtil.getReference(Section.class, sectionId);
    }

    /**
     * Returns a reference to the feedback session with the given id without loading it from the database.
     */
    public FeedbackSession getSessionReference(UUID sessionId) {
        return HibernateUtil.getReference(FeedbackSession.class, sessionId);
    }

    /**
     * Deletes all privilege rows (course, section and session level) belonging to an instructor.
     */
    public void deleteAllForInstructor(UUID instructorId) {
        deleteAllForInstructor(InstructorSessionPrivilege.class, instructorId);
        deleteAllForInstructor(InstructorSectionPrivilege.class, instructorId);
        deleteAllForInstructor(InstructorCoursePrivilege.class, instructorId);
        HibernateUtil.flushSession();
    }

    private <T> void deleteAllForInstructor(Class<T> entityType, UUID instructorId) {
        CriteriaBuilder cb = HibernateUtil.getCriteriaBuilder();
        CriteriaDelete<T> cd = cb.createCriteriaDelete(entityType);
        Root<T> root = cd.from(entityType);
        cd.where(cb.equal(root.get("instructorId"), instructorId));
        HibernateUtil.executeDelete(cd);
    }
}
