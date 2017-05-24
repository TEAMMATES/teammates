package teammates.ui.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;

public class CourseEditSectionRow {
    private String sectionName;
    private int panelIndex;
    private InstructorAttributes instructor;
    private List<ElementTag> permissionInputGroup2;
    private List<ElementTag> permissionInputGroup3;
    private ElementTag toggleSessionLevelInSectionButton;
    private List<CourseEditFeedbackSessionRow> feedbackSessions;
    private List<List<ElementTag>> specialSections;

    public CourseEditSectionRow(String sectionName, List<String> sectionNames, int sectionIndex,
                                int panelIndex, InstructorAttributes instructor,
                                int instructorIndex, List<String> feedbackNames) {
        this.sectionName = sectionName;
        this.panelIndex = panelIndex;
        this.instructor = instructor;
        feedbackSessions = new ArrayList<CourseEditFeedbackSessionRow>();

        specialSections = createCheckboxesForSectionLevelPermissionsOfInstructors(sectionNames, panelIndex, sectionIndex);
        permissionInputGroup2 = createCheckboxesForStudentPermissionsOfInstructors(panelIndex);
        permissionInputGroup3 = createCheckboxesForSessionPermissionsOfInstructors(panelIndex);

        boolean isSectionSpecial = isSessionsInSectionSpecial();

        String content = isSectionSpecial
                ? "Hide session-level permissions"
                : "Give different permissions for sessions in this section";

        Map<String, String> attributes = new HashMap<>();
        attributes.put("class", "small col-sm-5 toggleSessionLevelInSection");
        attributes.put("id", "toggleSessionLevelInSection" + panelIndex + "ForInstructor" + instructorIndex);
        attributes.put("href", "javascript:;");
        attributes.put("data-instructor-index", Integer.toString(instructorIndex));
        attributes.put("data-panel-index", Integer.toString(panelIndex));
        attributes.put("data-is-section-special", Boolean.toString(isSectionSpecial));

        toggleSessionLevelInSectionButton = createButton(content, attributes);

        String[] privileges = {Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS,
                               Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS,
                               Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS};

        for (String feedbackName : feedbackNames) {
            List<ElementTag> checkBoxList = new ArrayList<ElementTag>();
            for (String privilege : privileges) {
                String name = privilege + Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + panelIndex
                              + "feedback" + feedbackName;
                boolean isChecked = instructor != null && instructor.isAllowedForPrivilege(sectionName,
                                                                                             feedbackName,
                                                                                             privilege);
                checkBoxList.add(createCheckBox(null, name, "true", isChecked));
            }

            CourseEditFeedbackSessionRow feedbackSessionRow = new CourseEditFeedbackSessionRow(feedbackName,
                                                                                               checkBoxList);
            feedbackSessions.add(feedbackSessionRow);
        }
    }

    public int getPanelIndex() {
        return panelIndex;
    }

    public List<CourseEditFeedbackSessionRow> getFeedbackSessions() {
        return feedbackSessions;
    }

    /**
     * Checks if the section this row corresponds to is special.
     * A section is considered special if the instructor has special privileges
     * ie. privileges that are not defined at course level.
     *
     * @return true if the section is special.
     */
    public boolean isSectionSpecial() {
        return instructor != null && instructor.privileges.isSectionSpecial(sectionName);
    }

    public boolean isSessionsInSectionSpecial() {
        return instructor != null && instructor.privileges.isSessionsInSectionSpecial(sectionName);
    }

    public List<List<ElementTag>> getSpecialSections() {
        return specialSections;
    }

    public List<ElementTag> getPermissionInputGroup2() {
        return permissionInputGroup2;
    }

    public List<ElementTag> getPermissionInputGroup3() {
        return permissionInputGroup3;
    }

    public ElementTag getToggleSessionLevelInSectionButton() {
        return toggleSessionLevelInSectionButton;
    }

    /**
     * Creates checkboxes for Instructor's permissions/priviliges related to sessions
     *   and automatically checks a single checkbox if special privileges have been assigned to
     *   the section it corresponds to.
     *
     * @param panelIndex   the index of the panel currently being created
     * @return             a list of checkboxes
     */
    private List<ElementTag> createCheckboxesForSessionPermissionsOfInstructors(int panelIndex) {
        List<ElementTag> permissionInputGroup = new ArrayList<ElementTag>();

        String[] privileges = {Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS,
                               Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS,
                               Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS};

        String[] checkboxContent = {"Sessions: Submit Responses and Add Comments",
                                    "Sessions: View Responses and Comments",
                                    "Sessions: Edit/Delete Responses/Comments by Others"};

        int index = 0;
        for (String privilege : privileges) {
            boolean isChecked = instructor != null && instructor.isAllowedForPrivilege(sectionName,
                                                                                       privilege);
            String name = privilege + Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + panelIndex;
            permissionInputGroup.add(createCheckBox(checkboxContent[index], name, "true", isChecked));
            index++;
        }

        return permissionInputGroup;
    }

    /**
     * Creates checkboxes for Instructor's permissions/priviliges related to students' details
     * and automatically checks a single checkbox if special privileges have been assigned
     * to the section it corresponds to.
     *
     * @param panelIndex   the index of the panel currently being created
     * @return             a list of checkboxes
     */
    private List<ElementTag> createCheckboxesForStudentPermissionsOfInstructors(int panelIndex) {
        List<ElementTag> permissionInputGroup = new ArrayList<ElementTag>();

        String[] privileges = { Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS };

        String[] checkboxContent = { "View Students' Details" };

        int index = 0;
        for (String privilege : privileges) {
            boolean isChecked = instructor != null && instructor.isAllowedForPrivilege(sectionName,
                                                                                       privilege);
            String name = privilege + Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + panelIndex;
            permissionInputGroup.add(createCheckBox(checkboxContent[index], name, "true", isChecked));
            index++;
        }

        return permissionInputGroup;
    }

    /**
     * Creates a row of selections elements (checkboxes), each representing a single section
     *   and automatically checks a single checkbox if special privileges have been assigned to
     *   the section it corresponds to.
     *
     * @param sectionNames the list of sections in the course
     * @param panelIndex   the index of the panel currently being created
     * @param sectionIndex the index of the section which will be checked for special privileges
     * @return             a list of checkboxes, separated into rows and columns
     */
    private List<List<ElementTag>> createCheckboxesForSectionLevelPermissionsOfInstructors(
                                   List<String> sectionNames, int panelIndex, int sectionIndex) {
        List<List<ElementTag>> specialSections = new ArrayList<List<ElementTag>>();
        // i represents the row (vertical alignment) of the checkbox
        for (int i = 0; i < sectionNames.size(); i += 3) {
            List<ElementTag> specialSectionGroup = new ArrayList<ElementTag>();

            // j represents the column (horizontal alignment) of the checkbox
            for (int j = 0; j < 3 && i + j < sectionNames.size(); j++) {
                int positionOfNewSection = i + j;
                String name = Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + panelIndex
                              + Const.ParamsNames.INSTRUCTOR_SECTION + positionOfNewSection;

                ElementTag checkbox;
                if (isSectionSpecial()) {
                    boolean isPositionMatchedWithSection = positionOfNewSection == sectionIndex;
                    checkbox = createCheckBox(sectionNames.get(positionOfNewSection), name,
                                                         sectionNames.get(positionOfNewSection),
                                                         isPositionMatchedWithSection);
                } else {
                    checkbox = createCheckBox(sectionNames.get(positionOfNewSection), name,
                                                         sectionNames.get(positionOfNewSection), false);
                }
                specialSectionGroup.add(checkbox);
            }

            specialSections.add(specialSectionGroup);
        }
        return specialSections;
    }

    private ElementTag createCheckBox(String content, String name, String value, boolean isChecked) {
        ElementTag result = new ElementTag(content, "name", name, "value", value, "type", "checkbox");
        if (isChecked) {
            result.setAttribute("checked", null);
        }
        return result;
    }

    private ElementTag createButton(String content, Map<String, String> attributes) {
        ElementTag button = new ElementTag(content);

        for (Map.Entry<String, String> attribute : attributes.entrySet()) {
            button.setAttribute(attribute.getKey(), attribute.getValue());
        }

        return button;
    }
}
