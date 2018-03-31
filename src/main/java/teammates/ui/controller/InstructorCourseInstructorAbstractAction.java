package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;

public abstract class InstructorCourseInstructorAbstractAction extends Action {

    /**
     * Updates section and session level privileges for the instructor.
     *
     * @param courseId   Course that the instructor is being added to.
     * @param instructor Instructor that will be added.
     *                       This will be modified within the method.
     */
    protected void updateInstructorWithSectionLevelPrivileges(String courseId, InstructorAttributes instructor) {
        List<String> sectionNames = null;
        try {
            sectionNames = logic.getSectionNamesForCourse(courseId);
        } catch (EntityDoesNotExistException e) {
            return;
        }
        HashMap<String, Boolean> isSectionSpecialMappings = new HashMap<>();
        for (String sectionName : sectionNames) {
            isSectionSpecialMappings.put(sectionName, false);
        }

        List<String> feedbackNames = new ArrayList<>();

        List<FeedbackSessionAttributes> feedbacks = logic.getFeedbackSessionsForCourse(courseId);
        for (FeedbackSessionAttributes feedback : feedbacks) {
            feedbackNames.add(feedback.getFeedbackSessionName());
        }
        Map<String, List<String>> sectionNamesMap = getSectionsWithSpecialPrivilegesFromParameters(
                                                                instructor, sectionNames,
                                                                isSectionSpecialMappings);
        sectionNamesMap.forEach((sectionGroupName, specialSectionsInSectionGroup) -> {
            updateInstructorPrivilegesForSectionInSectionLevel(sectionGroupName,
                    specialSectionsInSectionGroup, instructor);

            //check if session-specific permissions are to be used
            String setSessionsStr = getRequestParamValue("is" + sectionGroupName + "sessionsset");
            boolean isSessionsForSectionGroupSpecial = Boolean.parseBoolean(setSessionsStr);
            if (isSessionsForSectionGroupSpecial) {
                updateInstructorPrivilegesForSectionInSessionLevel(sectionGroupName,
                        specialSectionsInSectionGroup, feedbackNames, instructor);
            } else {
                removeSessionLevelPrivileges(instructor, specialSectionsInSectionGroup);
            }
        });
        isSectionSpecialMappings.forEach((sectionNameToBeChecked, isSectionSpecial) -> {
            if (!isSectionSpecial) {
                instructor.privileges.removeSectionLevelPrivileges(sectionNameToBeChecked);
            }
        });
    }

    /**
     * Updates course level privileges for the instructor by retrieving request parameters.
     *
     * @param instructor Instructor that will be edited.
     *                       This will be modified within the method.
     */
    protected void updateInstructorCourseLevelPrivileges(InstructorAttributes instructor) {
        boolean isModifyCourseChecked =
                getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE) != null;
        boolean isModifyInstructorChecked =
                getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR) != null;
        boolean isModifySessionChecked =
                getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION) != null;
        boolean isModifyStudentChecked =
                getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT) != null;

        boolean isViewStudentInSectionsChecked =
                getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS) != null;

        boolean isViewSessionInSectionsChecked =
                getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS) != null;
        boolean isSubmitSessionInSectionsChecked =
                getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS) != null;
        boolean isModifySessionInSectionsChecked =
                getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS) != null;

        instructor.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE,
                                              isModifyCourseChecked);
        instructor.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR,
                                              isModifyInstructorChecked);
        instructor.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION,
                                              isModifySessionChecked);
        instructor.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT,
                                              isModifyStudentChecked);

        instructor.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS,
                                              isViewStudentInSectionsChecked);

        instructor.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS,
                                              isViewSessionInSectionsChecked);
        instructor.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS,
                                              isSubmitSessionInSectionsChecked);
        instructor.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS,
                                              isModifySessionInSectionsChecked);
    }

    /**
     * Removes session level privileges for the instructor under the given sections.
     *
     * @param instructor   Instructor that will be added.
     *                         This will be modified within the method.
     * @param sectionNames List of section names to be removed.
     */
    protected void removeSessionLevelPrivileges(InstructorAttributes instructor, List<String> sectionNames) {
        for (String sectionName : sectionNames) {
            instructor.privileges.removeSessionsPrivilegesForSection(sectionName);
        }
    }

    /**
     * Gets the sections that are special for the instructor to be added.
     *
     * @param instructor          Instructor that will be added.
     * @param sectionNames             List of section names in the course.
     * @param isSectionSpecialMappings Mapping of names of sections to boolean values indicating if they are special.
     *                                     This will be modified within the method.
     * @return List of section group names with their associated special sections.
     */
    protected Map<String, List<String>> getSectionsWithSpecialPrivilegesFromParameters(
            InstructorAttributes instructor, List<String> sectionNames,
            Map<String, Boolean> isSectionSpecialMappings) {
        HashMap<String, List<String>> specialSectionsInSectionGroups = new HashMap<>();
        if (instructor.role.equals(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM)) {
            getSectionsWithSpecialPrivilegesForCustomInstructor(sectionNames, isSectionSpecialMappings,
                                                                specialSectionsInSectionGroups);
        }
        return specialSectionsInSectionGroups;
    }

    /**
     * Gets the sections that are special for the custom instructor to be added.
     * Prereq: the added instructor must be given a custom role.
     *
     * @param sectionNames                   List of section names in the course.
     * @param isSectionSpecialMappings       Mapping of names of sections to boolean values indicating if they are special.
     *                                           This will be modified within the method.
     * @param specialSectionsInSectionGroups Mapping of section group names to the special sections that they contain.
     *                                           This will be modified within the method.
     */
    protected void getSectionsWithSpecialPrivilegesForCustomInstructor(List<String> sectionNames,
            Map<String, Boolean> isSectionSpecialMappings,
            Map<String, List<String>> specialSectionsInSectionGroups) {
        for (int i = 0; i < sectionNames.size(); i++) {
            String sectionGroupIsSetStr =
                    getRequestParamValue("is" + Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + i + "set");
            boolean isSectionGroupSpecial = Boolean.parseBoolean(sectionGroupIsSetStr);

            for (int j = 0; j < sectionNames.size(); j++) {
                String sectionNameFromParam = getRequestParamValue(
                                                     Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + i
                                                     + Const.ParamsNames.INSTRUCTOR_SECTION + j);
                boolean isSectionParamValid = sectionNameFromParam != null
                                              && isSectionSpecialMappings.containsKey(sectionNameFromParam);
                if (isSectionGroupSpecial && isSectionParamValid) {
                    markSectionAsSpecial(isSectionSpecialMappings, specialSectionsInSectionGroups,
                                         i, sectionNameFromParam);
                }
            }
        }
    }

    /**
     * Marks {@code sectionToMark} as special in the associated mappings.
     *
     * @param isSectionSpecialMappings       Mapping of names of sections to boolean values indicating if they are special.
     *                                           This will be modified within the method.
     * @param specialSectionsInSectionGroups Mapping of section group names to the special sections that they contain.
     *                                           This will be modified within the method.
     * @param sectionGroupIndex              Index of the section group to be updated.
     * @param sectionToMark                  Section that will be marked as special.
     */
    protected void markSectionAsSpecial(Map<String, Boolean> isSectionSpecialMappings,
            Map<String, List<String>> specialSectionsInSectionGroups, int sectionGroupIndex,
            String sectionToMark) {
        // indicate that section group covers the section
        // and mark that this section is special
        String sectionGroupParamName = Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + sectionGroupIndex;
        if (specialSectionsInSectionGroups.get(sectionGroupParamName) == null) {
            specialSectionsInSectionGroups.put(sectionGroupParamName, new ArrayList<String>());
        }
        specialSectionsInSectionGroups.get(sectionGroupParamName).add(sectionToMark);
        isSectionSpecialMappings.put(sectionToMark, true);
    }

    /**
     * Updates instructor privileges at section level by retrieving request parameters.
     * The parameters that are retrieved are based off {@code sectionGroupName}.
     *
     * @param sectionGroupName              Name of the section group.
     * @param specialSectionsInSectionGroup Sections marked as special under the section group.
     * @param instructor                    Instructor that will be edited.
     *                                          This will be modified within the method.
     */
    protected void updateInstructorPrivilegesForSectionInSectionLevel(String sectionGroupName,
            List<String> specialSectionsInSectionGroup, InstructorAttributes instructor) {
        boolean isViewStudentInSectionsChecked =
                getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS
                                     + sectionGroupName) != null;

        boolean isViewSessionInSectionsChecked =
                getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS
                                     + sectionGroupName) != null;
        boolean isSubmitSessionInSectionsChecked =
                getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS
                                     + sectionGroupName) != null;
        boolean isModifySessionInSectionsChecked =
                getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS
                                     + sectionGroupName) != null;

        for (String sectionName : specialSectionsInSectionGroup) {
            instructor.privileges.updatePrivilege(
                    sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS,
                    isViewStudentInSectionsChecked);
            instructor.privileges.updatePrivilege(
                    sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS,
                    isViewSessionInSectionsChecked);
            instructor.privileges.updatePrivilege(
                    sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS,
                    isSubmitSessionInSectionsChecked);
            instructor.privileges.updatePrivilege(
                    sectionName, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS,
                    isModifySessionInSectionsChecked);
        }
    }

    /**
     * Updates instructor privileges at session level by retrieving request parameters.
     * The parameters that are retrieved are based off {@code sectionGroupName} and {@code feedbackNames}.
     *
     * @param sectionGroupName              Name of the section group.
     * @param specialSectionsInSectionGroup Sections marked as special under the section group.
     * @param feedbackNames                 List of feedback names under the course.
     * @param instructor                    Instructor that will be added.
     *                                          This will be modified within the method.
     */
    protected void updateInstructorPrivilegesForSectionInSessionLevel(String sectionGroupName,
            List<String> specialSectionsInSectionGroup, List<String> feedbackNames,
            InstructorAttributes instructor) {
        for (String feedbackName : feedbackNames) {
            boolean isViewSessionInSectionsChecked =
                    getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS
                                         + sectionGroupName + "feedback" + feedbackName) != null;
            boolean isSubmitSessionInSectionsChecked =
                    getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS
                                         + sectionGroupName + "feedback" + feedbackName) != null;
            boolean isModifySessionInSectionsChecked =
                    getRequestParamValue(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS
                                         + sectionGroupName + "feedback" + feedbackName) != null;

            for (String sectionName : specialSectionsInSectionGroup) {
                instructor.privileges.updatePrivilege(sectionName, feedbackName,
                        Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS,
                        isViewSessionInSectionsChecked);
                instructor.privileges.updatePrivilege(sectionName, feedbackName,
                        Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS,
                        isSubmitSessionInSectionsChecked);
                instructor.privileges.updatePrivilege(sectionName, feedbackName,
                        Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS,
                        isModifySessionInSectionsChecked);
            }
        }
    }
}
