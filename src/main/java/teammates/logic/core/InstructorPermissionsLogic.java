package teammates.logic.core;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.UUID;

import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPermissionSet;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.InstructorPrivilegesLegacy;
import teammates.common.util.Const;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Section;

/**
 * Handles the logic related to instructor permissions.
 */
public final class InstructorPermissionsLogic {
    private static final InstructorPermissionsLogic instance = new InstructorPermissionsLogic();

    private CoursesLogic coursesLogic;
    private FeedbackSessionsLogic feedbackSessionsLogic;

    private InstructorPermissionsLogic() {
        // prevent instantiation
    }

    public static InstructorPermissionsLogic inst() {
        return instance;
    }

    void initLogicDependencies() {
        // Temporary dependency on courses & sessions logic,
        // will be removed once we have migrated InstructorPrivileges to SQL.
        coursesLogic = CoursesLogic.inst();
        feedbackSessionsLogic = FeedbackSessionsLogic.inst();
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
     * For instructors with the custom role, the privileges are determined by their
     * stored privileges.
     */
    public InstructorPrivileges getInstructorPrivileges(Instructor instructor) {
        Objects.requireNonNull(instructor, "Instructor cannot be null");

        InstructorPermissionRole role = instructor.getRole();
        switch (role) {
        case INSTRUCTOR_PERMISSION_ROLE_COOWNER:
            return new InstructorPrivileges(Const.InstructorPermissionRoleNames.COOWNER);
        case INSTRUCTOR_PERMISSION_ROLE_MANAGER:
            return new InstructorPrivileges(Const.InstructorPermissionRoleNames.MANAGER);
        case INSTRUCTOR_PERMISSION_ROLE_OBSERVER:
            return new InstructorPrivileges(Const.InstructorPermissionRoleNames.OBSERVER);
        case INSTRUCTOR_PERMISSION_ROLE_TUTOR:
            return new InstructorPrivileges(Const.InstructorPermissionRoleNames.TUTOR);
        case INSTRUCTOR_PERMISSION_ROLE_CUSTOM:
            return convertLegacyToNew(instructor.getPrivileges(), instructor.getCourseId());
        default:
            throw new IllegalStateException("Unexpected instructor role: " + role);
        }
    }

    /**
     * Saves instructor privileges for the given instructor.
     */
    public void saveInstructorPrivileges(Instructor instructor, InstructorPrivileges newPrivileges) {
        Objects.requireNonNull(instructor, "Instructor cannot be null");
        Objects.requireNonNull(newPrivileges, "Privileges cannot be null");

        newPrivileges.validatePrivileges();
        instructor.setPrivileges(convertNewToLegacy(newPrivileges));
    }

    /**
     * Converts the given legacy {@link InstructorPrivilegesLegacy} (name-keyed) to runtime format with UUID keys.
     */
    public InstructorPrivileges convertToRuntime(InstructorPrivilegesLegacy legacyPrivileges) {
        Objects.requireNonNull(legacyPrivileges, "Privileges cannot be null");

        return convertLegacyToNew(legacyPrivileges, /* courseId= */ null);
    }

    /**
     * Converts the given runtime {@link InstructorPrivileges} (UUID-keyed) to legacy name-keyed format.
     */
    public InstructorPrivilegesLegacy convertToLegacy(InstructorPrivileges newPrivileges) {
        Objects.requireNonNull(newPrivileges, "Privileges cannot be null");

        return convertNewToLegacy(newPrivileges);
    }

    /**
     * Creates a {@link InstructorPrivilegesLegacy} for a predefined instructor role.
     */
    public InstructorPrivilegesLegacy legacyPrivilegesForRole(String roleName) {
        InstructorPrivileges runtime = new InstructorPrivileges(roleName);
        return new InstructorPrivilegesLegacy(
                runtime.getCourseLevelPrivileges(),
                new LinkedHashMap<>(),
                new LinkedHashMap<>());
    }

    /**
     * Converts legacy name-keyed privileges to runtime UUID-keyed privileges.
     * Entries for sections or sessions that no longer exist are silently dropped.
     */
    private InstructorPrivileges convertLegacyToNew(InstructorPrivilegesLegacy legacy, String courseId) {
        InstructorPrivileges newPrivileges = new InstructorPrivileges();

        // Copy course-level
        copyCourseLevelPrivileges(legacy.getCourseLevelPrivileges(), newPrivileges);

        // Convert section-level: name → UUID
        for (Map.Entry<String, InstructorPermissionSet> entry : legacy.getSectionLevelPrivileges().entrySet()) {
            String sectionName = entry.getKey();
            Section section = coursesLogic.getSectionByName(courseId, sectionName);
            if (section == null) {
                continue;
            }
            UUID sectionId = section.getId();
            newPrivileges.addSectionWithDefaultPrivileges(sectionId);
            applyPermissionSet(newPrivileges, sectionId, entry.getValue());
        }

        // Convert session-level: sectionName → sectionUUID, sessionName → sessionUUID
        for (Map.Entry<String, Map<String, InstructorPermissionSet>> sectionEntry
                : legacy.getSessionLevelPrivileges().entrySet()) {
            String sectionName = sectionEntry.getKey();
            Section section = coursesLogic.getSectionByName(courseId, sectionName);
            if (section == null) {
                continue;
            }
            UUID sectionId = section.getId();

            for (Entry<String, InstructorPermissionSet> sessionEntry : sectionEntry.getValue().entrySet()) {
                String sessionName = sessionEntry.getKey();
                FeedbackSession feedbackSession = feedbackSessionsLogic.getFeedbackSession(sessionName, courseId);
                if (feedbackSession == null) {
                    continue;
                }
                UUID sessionId = feedbackSession.getId();
                applySessionPermissionSet(newPrivileges, sectionId, sessionId, sessionEntry.getValue());
            }
        }

        return newPrivileges;
    }

    /**
     * Converts runtime UUID-keyed privileges to legacy name-keyed privileges.
     * Entries for sections or sessions that cannot be resolved are silently dropped.
     */
    private InstructorPrivilegesLegacy convertNewToLegacy(InstructorPrivileges newPrivileges) {
        InstructorPermissionSet courseLevel = newPrivileges.getCourseLevelPrivileges();
        Map<String, InstructorPermissionSet> sectionLevel = new LinkedHashMap<>();
        Map<String, Map<String, InstructorPermissionSet>> sessionLevel = new LinkedHashMap<>();

        // Convert section-level: UUID → name
        for (Map.Entry<UUID, InstructorPermissionSet> entry : newPrivileges.getSectionLevelPrivileges().entrySet()) {
            Section section = coursesLogic.getSectionById(entry.getKey());
            if (section == null) {
                continue;
            }
            sectionLevel.put(section.getName(), entry.getValue());
        }

        // Convert session-level: sectionUUID → sectionName, sessionUUID → sessionName
        for (Map.Entry<UUID, Map<UUID, InstructorPermissionSet>> sectionEntry
                : newPrivileges.getSessionLevelPrivileges().entrySet()) {
            Section section = coursesLogic.getSectionById(sectionEntry.getKey());
            if (section == null) {
                continue;
            }
            String sectionName = section.getName();
            Map<String, InstructorPermissionSet> sessions = new LinkedHashMap<>();
            for (Map.Entry<UUID, InstructorPermissionSet> sessionEntry : sectionEntry.getValue().entrySet()) {
                FeedbackSession feedbackSession = feedbackSessionsLogic.getFeedbackSession(sessionEntry.getKey());
                if (feedbackSession == null) {
                    continue;
                }
                sessions.put(feedbackSession.getName(), sessionEntry.getValue());
            }
            if (!sessions.isEmpty()) {
                sessionLevel.put(sectionName, sessions);
            }
        }

        return new InstructorPrivilegesLegacy(courseLevel, sectionLevel, sessionLevel);
    }

    private void copyCourseLevelPrivileges(InstructorPermissionSet src, InstructorPrivileges dest) {
        dest.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_COURSE, src.isCanModifyCourse());
        dest.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR, src.isCanModifyInstructor());
        dest.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_SESSION, src.isCanModifySession());
        dest.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_STUDENT, src.isCanModifyStudent());
        dest.updatePrivilege(Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS,
                src.isCanViewStudentInSections());
        dest.updatePrivilege(Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS,
                src.isCanViewSessionInSections());
        dest.updatePrivilege(Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS,
                src.isCanSubmitSessionInSections());
        dest.updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS,
                src.isCanModifySessionCommentsInSections());
    }

    private void applyPermissionSet(InstructorPrivileges dest, UUID sectionId, InstructorPermissionSet src) {
        if (src.isCanViewStudentInSections()) {
            dest.updatePrivilege(sectionId, Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS, true);
        }
        if (src.isCanViewSessionInSections()) {
            dest.updatePrivilege(sectionId, Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS, true);
        }
        if (src.isCanSubmitSessionInSections()) {
            dest.updatePrivilege(sectionId, Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS, true);
        }
        if (src.isCanModifySessionCommentsInSections()) {
            dest.updatePrivilege(sectionId,
                    Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, true);
        }
    }

    private void applySessionPermissionSet(InstructorPrivileges dest, UUID sectionId, UUID sessionId,
            InstructorPermissionSet src) {
        if (src.isCanViewSessionInSections()) {
            dest.updatePrivilege(sectionId, sessionId,
                    Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS, true);
        }
        if (src.isCanSubmitSessionInSections()) {
            dest.updatePrivilege(sectionId, sessionId,
                    Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS, true);
        }
        if (src.isCanModifySessionCommentsInSections()) {
            dest.updatePrivilege(sectionId, sessionId,
                    Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS, true);
        }
    }
}
