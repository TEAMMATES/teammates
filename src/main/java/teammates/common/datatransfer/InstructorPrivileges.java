package teammates.common.datatransfer;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import jakarta.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;

import teammates.common.util.Const;

/**
 * Stores the permissions of an instructor using UUIDs as keys for section and session level.
 *
 * <p>This is the runtime format used throughout the application. The persisted form is split
 * across the instructor privilege tables and reassembled by {@code InstructorPermissionsLogic}.
 */
public final class InstructorPrivileges {

    private static final String[] COURSE_LEVEL_ONLY_LIST = new String[] {
            Const.InstructorPermissions.CAN_MODIFY_COURSE,
            Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR,
            Const.InstructorPermissions.CAN_MODIFY_SESSION,
            Const.InstructorPermissions.CAN_MODIFY_STUDENT,
    };

    private static final String[] SECTION_LEVEL_ONLY_LIST = new String[] {
    };

    private static final String[] SESSION_LEVEL_ONLY_LIST = new String[] {
            Const.InstructorPermissions.CAN_VIEW_SESSION,
            Const.InstructorPermissions.CAN_SUBMIT_SESSION,
            Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT,
    };

    private static final Set<String> COURSE_LEVEL_ONLY_PRIVILEGES =
            new LinkedHashSet<>(Arrays.asList(COURSE_LEVEL_ONLY_LIST));
    private static final Set<String> SECTION_LEVEL_ONLY_PRIVILEGES =
            new LinkedHashSet<>(Arrays.asList(SECTION_LEVEL_ONLY_LIST));
    private static final Set<String> SESSION_LEVEL_ONLY_PRIVILEGES =
            new LinkedHashSet<>(Arrays.asList(SESSION_LEVEL_ONLY_LIST));

    @Nullable
    private UUID instructorId;
    private final InstructorPermissionSet courseLevel;
    private final Map<UUID, InstructorPermissionSet> sectionLevel;
    private final Map<UUID, Map<UUID, InstructorPermissionSet>> sessionLevel;

    @JsonCreator
    private InstructorPrivileges(
            UUID instructorId,
            InstructorPermissionSet courseLevel,
            Map<UUID, InstructorPermissionSet> sectionLevel,
            Map<UUID, Map<UUID, InstructorPermissionSet>> sessionLevel) {
        this.instructorId = instructorId;
        this.courseLevel = courseLevel;
        this.sectionLevel = sectionLevel;
        this.sessionLevel = sessionLevel;
    }

    public InstructorPrivileges(UUID instructorId) {
        Objects.requireNonNull(instructorId, "Instructor ID cannot be null");
        this.instructorId = instructorId;
        this.courseLevel = new InstructorPermissionSet();
        this.sectionLevel = new LinkedHashMap<>();
        this.sessionLevel = new LinkedHashMap<>();
    }

    public InstructorPrivileges(UUID instructorId, String instrRole) {
        this(instructorId);
        switch (instrRole) {
        case Const.InstructorPermissionRoleNames.COOWNER:
            setDefaultPrivilegesForCoowner();
            break;
        case Const.InstructorPermissionRoleNames.MANAGER:
            setDefaultPrivilegesForManager();
            break;
        case Const.InstructorPermissionRoleNames.OBSERVER:
            setDefaultPrivilegesForObserver();
            break;
        case Const.InstructorPermissionRoleNames.TUTOR:
            setDefaultPrivilegesForTutor();
            break;
        case Const.InstructorPermissionRoleNames.CUSTOM:
        default:
            setDefaultPrivilegesForCustom();
            break;
        }
    }

    /**
     * Returns true if the given string is a valid privilege name.
     */
    public static boolean isPrivilegeNameValid(String privilegeName) {
        return COURSE_LEVEL_ONLY_PRIVILEGES.contains(privilegeName)
                || SECTION_LEVEL_ONLY_PRIVILEGES.contains(privilegeName)
                || SESSION_LEVEL_ONLY_PRIVILEGES.contains(privilegeName);
    }

    /**
     * Returns true if the given string is a valid section-level privilege name.
     */
    public static boolean isPrivilegeNameValidForSectionLevel(String privilegeName) {
        return SECTION_LEVEL_ONLY_PRIVILEGES.contains(privilegeName)
                || SESSION_LEVEL_ONLY_PRIVILEGES.contains(privilegeName);
    }

    /**
     * Returns true if the given string is a valid session-level privilege name.
     */
    public static boolean isPrivilegeNameValidForSessionLevel(String privilegeName) {
        return SESSION_LEVEL_ONLY_PRIVILEGES.contains(privilegeName);
    }

    void setDefaultPrivilegesForCoowner() {
        setDefaultPrivileges(DefaultInstructorPrivileges.PRIVILEGES_ALL);
    }

    void setDefaultPrivilegesForManager() {
        setDefaultPrivileges(DefaultInstructorPrivileges.PRIVILEGES_MANAGER);
    }

    void setDefaultPrivilegesForObserver() {
        setDefaultPrivileges(DefaultInstructorPrivileges.PRIVILEGES_OBSERVER);
    }

    void setDefaultPrivilegesForTutor() {
        setDefaultPrivileges(DefaultInstructorPrivileges.PRIVILEGES_TUTOR);
    }

    void setDefaultPrivilegesForCustom() {
        setDefaultPrivileges(DefaultInstructorPrivileges.PRIVILEGES_NONE);
    }

    private void setDefaultPrivileges(InstructorPermissionSet defaultPrivileges) {
        courseLevel.setCanModifyCourse(defaultPrivileges.isCanModifyCourse());
        courseLevel.setCanModifyInstructor(defaultPrivileges.isCanModifyInstructor());
        courseLevel.setCanModifySession(defaultPrivileges.isCanModifySession());
        courseLevel.setCanModifyStudent(defaultPrivileges.isCanModifyStudent());
        courseLevel.setCanViewSession(defaultPrivileges.isCanViewSession());
        courseLevel.setCanSubmitSession(defaultPrivileges.isCanSubmitSession());
        courseLevel.setCanModifySessionComments(defaultPrivileges.isCanModifySessionComments());
    }

    private InstructorPermissionSet getOverallPrivilegesForSections() {
        InstructorPermissionSet privileges = new InstructorPermissionSet();

        privileges.setCanViewSession(courseLevel.isCanViewSession());
        privileges.setCanSubmitSession(courseLevel.isCanSubmitSession());
        privileges.setCanModifySessionComments(courseLevel.isCanModifySessionComments());

        return privileges;
    }

    /**
     * Sets privilege for the privilege specified by privilegeName.
     */
    public void updatePrivilege(String privilegeName, boolean isAllowed) {
        updatePrivilegeInCourseLevel(privilegeName, isAllowed);
    }

    /**
     * Sets privilege for the privilege specified by privilegeName for sectionId.
     */
    public void updatePrivilege(UUID sectionId, String privilegeName, boolean isAllowed) {
        updatePrivilegeInSectionLevel(sectionId, privilegeName, isAllowed);
    }

    /**
     * Sets privilege for the privilege specified by privilegeName for sessionId in sectionId.
     */
    public void updatePrivilege(UUID sectionId, UUID sessionId, String privilegeName, boolean isAllowed) {
        updatePrivilegeInSessionLevel(sectionId, sessionId, privilegeName, isAllowed);
    }

    private void updatePrivilegeInCourseLevel(String privilegeName, boolean isAllowed) {
        if (!isPrivilegeNameValid(privilegeName)) {
            return;
        }
        this.courseLevel.put(privilegeName, isAllowed);
    }

    private void updatePrivilegeInSectionLevel(UUID sectionId, String privilegeName, boolean isAllowed) {
        if (!isPrivilegeNameValidForSectionLevel(privilegeName)) {
            return;
        }
        addSectionWithDefaultPrivileges(sectionId);
        this.sectionLevel.get(sectionId).put(privilegeName, isAllowed);
    }

    private void updatePrivilegeInSessionLevel(UUID sectionId, UUID sessionId,
                                               String privilegeName, boolean isAllowed) {
        if (!isPrivilegeNameValidForSessionLevel(privilegeName)) {
            return;
        }
        verifyExistenceOfSectionId(sectionId);
        this.sessionLevel.get(sectionId).computeIfAbsent(sessionId, key -> new InstructorPermissionSet())
                                        .put(privilegeName, isAllowed);
    }

    private void verifyExistenceOfSectionId(UUID sectionId) {
        this.sessionLevel.computeIfAbsent(sectionId, key -> {
            addSectionWithDefaultPrivileges(sectionId);
            return new LinkedHashMap<>();
        });
    }

    /**
     * Adds a section entry with default privileges if it does not exist.
     */
    public void addSectionWithDefaultPrivileges(UUID sectionId) {
        this.sectionLevel.putIfAbsent(sectionId, getOverallPrivilegesForSections());
    }

    /**
     * Returns true if it is allowed for the privilege specified by privilegeName.
     */
    public boolean isAllowedForPrivilege(String privilegeName) {
        return isAllowedInCourseLevel(privilegeName);
    }

    /**
     * Returns true if it is allowed for the privilege specified by privilegeName in sectionId.
     */
    public boolean isAllowedForPrivilege(UUID sectionId, String privilegeName) {
        return isAllowedInSectionLevel(sectionId, privilegeName);
    }

    /**
     * Returns true if it is allowed for the privilege specified by privilegeName for sessionId in sectionId.
     */
    public boolean isAllowedForPrivilege(UUID sectionId, UUID sessionId, String privilegeName) {
        return isAllowedInSessionLevel(sectionId, sessionId, privilegeName);
    }

    /**
     * Returns true if privilege for session is present for any section.
     */
    public boolean isAllowedForPrivilegeAnySection(UUID sessionId, String privilegeName) {
        return isAllowedInSessionLevelAnySection(sessionId, privilegeName);
    }

    /**
     * Returns true if the instructor has all course level privileges.
     */
    public boolean hasAllPrivileges() {
        return courseLevel.equals(DefaultInstructorPrivileges.PRIVILEGES_ALL);
    }

    private boolean isAllowedInCourseLevel(String privilegeName) {

        assert isPrivilegeNameValid(privilegeName);

        return this.courseLevel.get(privilegeName);
    }

    private boolean isAllowedInSectionLevel(UUID sectionId, String privilegeName) {

        assert isPrivilegeNameValid(privilegeName);

        if (!this.sectionLevel.containsKey(sectionId)) {
            return isAllowedInCourseLevel(privilegeName);
        }

        return this.sectionLevel.get(sectionId).get(privilegeName);
    }

    private boolean isAllowedInSessionLevel(UUID sectionId, UUID sessionId, String privilegeName) {

        assert isPrivilegeNameValid(privilegeName);

        if (!this.sessionLevel.containsKey(sectionId)
                || !this.sessionLevel.get(sectionId).containsKey(sessionId)) {
            return isAllowedInSectionLevel(sectionId, privilegeName);
        }

        return this.sessionLevel.get(sectionId).get(sessionId).get(privilegeName);
    }

    private boolean isAllowedInSessionLevelAnySection(UUID sessionId, String privilegeName) {

        assert isPrivilegeNameValid(privilegeName);

        Set<UUID> sections = new LinkedHashSet<>(this.sessionLevel.keySet());
        sections.addAll(this.sectionLevel.keySet());
        for (UUID sectionId : sections) {
            if (isAllowedInSessionLevel(sectionId, sessionId, privilegeName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validates the privileges in course level, section level and session level.
     *
     * <p>Makes sure there is nothing wrong with privileges hierarchy by adding the
     * prerequisite privileges if they have not been granted yet.
     */
    public void validatePrivileges() {
        if (this.courseLevel.isCanModifySessionComments()) {
            this.courseLevel.setCanViewSession(true);
        }
        for (InstructorPermissionSet sectionMap : this.sectionLevel.values()) {
            if (sectionMap.isCanModifySessionComments()) {
                sectionMap.setCanViewSession(true);
            }
        }
        for (Map<UUID, InstructorPermissionSet> section : this.sessionLevel.values()) {
            for (InstructorPermissionSet sessionMap : section.values()) {
                if (sessionMap.isCanModifySessionComments()) {
                    sessionMap.setCanViewSession(true);
                }
            }
        }
    }

    public InstructorPermissionSet getCourseLevelPrivileges() {
        return courseLevel.getCopy();
    }

    /**
     * Returns the section level privileges of the instructor, keyed by section UUID.
     */
    public Map<UUID, InstructorPermissionSet> getSectionLevelPrivileges() {
        Map<UUID, InstructorPermissionSet> copy = new LinkedHashMap<>();
        sectionLevel.forEach((key, value) -> copy.put(key, value.getCopy()));
        return copy;
    }

    /**
     * Returns the session level privileges of the instructor, keyed by section UUID then session UUID.
     */
    public Map<UUID, Map<UUID, InstructorPermissionSet>> getSessionLevelPrivileges() {
        Map<UUID, Map<UUID, InstructorPermissionSet>> copy = new LinkedHashMap<>();
        sessionLevel.forEach((sessionLevelKey, sessionLevelValue) -> {
            Map<UUID, InstructorPermissionSet> sectionCopy = new LinkedHashMap<>();
            sessionLevelValue.forEach((key, value) -> sectionCopy.put(key, value.getCopy()));

            copy.put(sessionLevelKey, sectionCopy);
        });
        return copy;
    }

    /**
     * Returns the list of sections the instructor has the specified privilege name, keyed by section UUID.
     */
    public Map<UUID, InstructorPermissionSet> getSectionsWithPrivilege(String privilegeName) {
        Map<UUID, InstructorPermissionSet> copy = new LinkedHashMap<>();
        sectionLevel.forEach((key, value) -> {
            if (isAllowedInSectionLevel(key, privilegeName)) {
                copy.put(key, value.getCopy());
            }
        });
        return copy;
    }

    public UUID getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(UUID instructorId) {
        this.instructorId = instructorId;
    }

    @Override
    public boolean equals(Object another) {
        if (!(another instanceof InstructorPrivileges)) {
            return false;
        }
        if (another == this) {
            return true;
        }

        InstructorPrivileges rhs = (InstructorPrivileges) another;
        return Objects.equals(this.instructorId, rhs.instructorId)
                && this.getCourseLevelPrivileges().equals(rhs.getCourseLevelPrivileges())
                && this.getSectionLevelPrivileges().equals(rhs.getSectionLevelPrivileges())
                && this.getSessionLevelPrivileges().equals(rhs.getSessionLevelPrivileges());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            this.instructorId,
            this.getCourseLevelPrivileges(),
            this.getSectionLevelPrivileges(),
            this.getSessionLevelPrivileges()
        );
    }

}
