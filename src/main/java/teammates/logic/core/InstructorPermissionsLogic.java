package teammates.logic.core;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.UUID;

import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPermissionSet;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;
import teammates.storage.api.InstructorPermissionsDb;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.InstructorCoursePrivilege;
import teammates.storage.entity.InstructorSectionPrivilege;
import teammates.storage.entity.InstructorSessionPrivilege;

/**
 * Handles the logic related to instructor permissions.
 */
public final class InstructorPermissionsLogic {
    private static final InstructorPermissionsLogic instance = new InstructorPermissionsLogic();

    private InstructorPermissionsDb instructorPermissionsDb;

    private InstructorPermissionsLogic() {
        // prevent instantiation
    }

    public static InstructorPermissionsLogic inst() {
        return instance;
    }

    void initLogicDependencies(InstructorPermissionsDb instructorPermissionsDb) {
        this.instructorPermissionsDb = instructorPermissionsDb;
    }

    /**
     * Checks if the given instructor has the specified permission.
     */
    public boolean hasPermissions(Instructor instructor, String... permissionNames) {
        Objects.requireNonNull(instructor, "Instructor cannot be null");

        InstructorPrivileges privileges = getInstructorPrivileges(instructor);

        for (String permissionName : permissionNames) {
            if (!privileges.isAllowedForPrivilege(permissionName)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the given instructor has the specified section-level permission.
     */
    public boolean hasPermissionsForSection(Instructor instructor, UUID sectionId, String... permissionNames) {
        Objects.requireNonNull(instructor, "Instructor cannot be null");
        Objects.requireNonNull(sectionId, "Section ID cannot be null");

        InstructorPrivileges privileges = getInstructorPrivileges(instructor);

        for (String permissionName : permissionNames) {
            if (!privileges.isAllowedForPrivilege(sectionId, permissionName)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the given instructor has the specified session-in-section-level permission.
     */
    public boolean hasPermissionsForSessionInSection(Instructor instructor, UUID sectionId,
            UUID feedbackSessionId, String... permissionNames) {
        Objects.requireNonNull(instructor, "Instructor cannot be null");
        Objects.requireNonNull(sectionId, "Section ID cannot be null");
        Objects.requireNonNull(feedbackSessionId, "Feedback session ID cannot be null");

        InstructorPrivileges privileges = getInstructorPrivileges(instructor);

        for (String permissionName : permissionNames) {
            if (!privileges.isAllowedForPrivilege(sectionId, feedbackSessionId, permissionName)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the given instructor has the specified session-in-section-level permission in any section.
     */
    public boolean hasPermissionsForSectionInAnySection(Instructor instructor,
            UUID sessionId, String... permissionNames) {
        Objects.requireNonNull(instructor, "Instructor cannot be null");

        InstructorPrivileges privileges = getInstructorPrivileges(instructor);

        for (String permissionName : permissionNames) {
            if (privileges.isAllowedForPrivilegeAnySection(sessionId, permissionName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a map of sections with the specified permission for the given instructor.
     */
    public Map<UUID, InstructorPermissionSet> getSectionsWithPermission(Instructor instructor, String permissionName) {
        Objects.requireNonNull(instructor, "Instructor cannot be null");
        Objects.requireNonNull(permissionName, "Permission name cannot be null");

        InstructorPrivileges privileges = getInstructorPrivileges(instructor);
        return privileges.getSectionsWithPrivilege(permissionName);
    }

    /**
     * Returns the InstructorPrivileges for the given instructor.
     *
     * <p>
     * For instructors with predefined roles, the privileges are determined by their
     * role.
     * For instructors with the custom role, the privileges are read from the instructor
     * privilege tables and merged into a single {@link InstructorPrivileges} object.
     */
    public InstructorPrivileges getInstructorPrivileges(Instructor instructor) {
        Objects.requireNonNull(instructor, "Instructor cannot be null");

        InstructorPermissionRole role = instructor.getRole();
        UUID instructorId = instructor.getId();
        switch (role) {
        case COOWNER:
            return new InstructorPrivileges(instructorId, Const.InstructorPermissionRoleNames.COOWNER);
        case MANAGER:
            return new InstructorPrivileges(instructorId, Const.InstructorPermissionRoleNames.MANAGER);
        case OBSERVER:
            return new InstructorPrivileges(instructorId, Const.InstructorPermissionRoleNames.OBSERVER);
        case TUTOR:
            return new InstructorPrivileges(instructorId, Const.InstructorPermissionRoleNames.TUTOR);
        case CUSTOM:
            return readCustomPrivileges(instructorId);
        default:
            throw new IllegalStateException("Unexpected instructor role: " + role);
        }
    }

    /**
     * Saves the instructor privileges for the given instructor.
     *
     * <p>Any existing privilege rows for the instructor are removed first. New rows are only
     * stored when the instructor's role is custom; for predefined roles the tables are left empty
     * and the privileges are derived from the role on read.
     */
    public void saveInstructorPrivileges(Instructor instructor, InstructorPrivileges newPrivileges) {
        Objects.requireNonNull(instructor, "Instructor cannot be null");
        Objects.requireNonNull(newPrivileges, "Privileges cannot be null");

        newPrivileges.validatePrivileges();

        instructorPermissionsDb.deleteAllForInstructor(instructor.getId());

        if (instructor.getRole() != InstructorPermissionRole.CUSTOM) {
            return;
        }

        instructorPermissionsDb.persistCoursePrivilege(
                new InstructorCoursePrivilege(instructor, newPrivileges.getCourseLevelPrivileges()));

        for (Entry<UUID, InstructorPermissionSet> entry : newPrivileges.getSectionLevelPrivileges().entrySet()) {
            instructorPermissionsDb.persistSectionPrivilege(new InstructorSectionPrivilege(
                    instructor, instructorPermissionsDb.getSectionReference(entry.getKey()), entry.getValue()));
        }

        for (Entry<UUID, Map<UUID, InstructorPermissionSet>> sectionEntry
                : newPrivileges.getSessionLevelPrivileges().entrySet()) {
            UUID sectionId = sectionEntry.getKey();
            for (Entry<UUID, InstructorPermissionSet> sessionEntry : sectionEntry.getValue().entrySet()) {
                instructorPermissionsDb.persistSessionPrivilege(new InstructorSessionPrivilege(
                        instructor,
                        instructorPermissionsDb.getSectionReference(sectionId),
                        instructorPermissionsDb.getSessionReference(sessionEntry.getKey()),
                        sessionEntry.getValue()));
            }
        }
    }

    /**
     * Reads the stored custom privileges for an instructor and merges the three privilege tables
     * into a single {@link InstructorPrivileges} object.
     */
    private InstructorPrivileges readCustomPrivileges(UUID instructorId) {
        InstructorPrivileges privileges = new InstructorPrivileges(instructorId);

        InstructorCoursePrivilege coursePrivilege = instructorPermissionsDb.getCoursePrivilege(instructorId);
        if (coursePrivilege != null) {
            applyToCourseLevel(privileges, coursePrivilege.getPrivileges());
        }

        for (InstructorSectionPrivilege sectionPrivilege : instructorPermissionsDb.getSectionPrivileges(instructorId)) {
            UUID sectionId = sectionPrivilege.getSectionId();
            privileges.addSectionWithDefaultPrivileges(sectionId);
            applyToSectionLevel(privileges, sectionId, sectionPrivilege.getPrivileges());
        }

        for (InstructorSessionPrivilege sessionPrivilege : instructorPermissionsDb.getSessionPrivileges(instructorId)) {
            applyToSessionLevel(privileges, sessionPrivilege.getSectionId(), sessionPrivilege.getSessionId(),
                    sessionPrivilege.getPrivileges());
        }

        return privileges;
    }

    private void applyToCourseLevel(InstructorPrivileges dest, InstructorPermissionSet src) {
        dest.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_COURSE, src.isCanModifyCourse());
        dest.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR, src.isCanModifyInstructor());
        dest.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_SESSION, src.isCanModifySession());
        dest.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_STUDENT, src.isCanModifyStudent());
        dest.updatePrivilege(Const.InstructorPermissions.CAN_VIEW_SESSION,
                src.isCanViewSession());
        dest.updatePrivilege(Const.InstructorPermissions.CAN_SUBMIT_SESSION,
                src.isCanSubmitSession());
        dest.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT,
                src.isCanModifySessionComments());
    }

    private void applyToSectionLevel(InstructorPrivileges dest, UUID sectionId, InstructorPermissionSet src) {
        dest.updatePrivilege(sectionId, Const.InstructorPermissions.CAN_VIEW_SESSION,
                src.isCanViewSession());
        dest.updatePrivilege(sectionId, Const.InstructorPermissions.CAN_SUBMIT_SESSION,
                src.isCanSubmitSession());
        dest.updatePrivilege(sectionId, Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT,
                src.isCanModifySessionComments());
    }

    private void applyToSessionLevel(InstructorPrivileges dest, UUID sectionId, UUID sessionId,
            InstructorPermissionSet src) {
        dest.updatePrivilege(sectionId, sessionId, Const.InstructorPermissions.CAN_VIEW_SESSION,
                src.isCanViewSession());
        dest.updatePrivilege(sectionId, sessionId, Const.InstructorPermissions.CAN_SUBMIT_SESSION,
                src.isCanSubmitSession());
        dest.updatePrivilege(sectionId, sessionId, Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT,
                src.isCanModifySessionComments());
    }
}
