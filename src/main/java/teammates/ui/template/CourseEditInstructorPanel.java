package teammates.ui.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;

public class CourseEditInstructorPanel {
    private int index;
    private InstructorAttributes instructor;
    private List<ElementTag> permissionInputGroup1;
    private List<ElementTag> permissionInputGroup2;
    private List<ElementTag> permissionInputGroup3;
    private ElementTag resendInviteButton;
    private ElementTag editButton;
    private ElementTag cancelButton;
    private ElementTag cancelAddInstructorButton;
    private ElementTag deleteButton;
    private boolean isAccessControlDisplayed;
    private List<CourseEditSectionRow> sectionRows;
    private int firstBlankSectionRowIndex;

    public CourseEditInstructorPanel(int instructorToShowIndex, int instructorIndex,
                                     InstructorAttributes instructor, List<String> sectionNames,
                                     List<String> feedbackNames) {
        if (instructorToShowIndex == -1) {
            index = instructorIndex;
            isAccessControlDisplayed = false;
        } else {
            index = instructorToShowIndex;
            isAccessControlDisplayed = true;
        }
        this.instructor = instructor;

        //TODO TO REMOVE AFTER DATA MIGRATION
        if (this.instructor != null) {
            this.instructor.displayedName = SanitizationHelper.desanitizeIfHtmlSanitized(this.instructor.displayedName);
            this.instructor.role = SanitizationHelper.desanitizeIfHtmlSanitized(this.instructor.role);

        }

        sectionRows = createSectionRows(instructorIndex, sectionNames, feedbackNames);
        permissionInputGroup1 = createPermissionInputGroup1ForInstructorPanel();
        permissionInputGroup2 = createPermissionInputGroup2ForInstructorPanel();
        permissionInputGroup3 = createPermissionInputGroup3ForInstructorPanel();
    }

    private List<CourseEditSectionRow> createSectionRows(
            int instructorIndex, List<String> sectionNames, List<String> feedbackNames) {
        firstBlankSectionRowIndex = sectionNames.size();
        Map<Integer, String> specialSectionNames = new TreeMap<>();
        Map<Integer, String> nonSpecialSectionNames = new TreeMap<>();

        distinguishSpecialAndNonSpecialSections(sectionNames, specialSectionNames, nonSpecialSectionNames);

        return createSpecialAndNonSpecialSectionRowsInOrder(instructorIndex, sectionNames, feedbackNames,
                                                            specialSectionNames, nonSpecialSectionNames);
    }

    /**
     * Splits the list of section names into two Integer to String mappings
     * matching the index of the section to the section's name.
     */
    private void distinguishSpecialAndNonSpecialSections(List<String> sectionNames,
            Map<Integer, String> specialSectionNames, Map<Integer, String> nonSpecialSectionNames) {
        int sectionIndex = 0;

        for (String sectionName : sectionNames) {
            if (isSectionSpecial(sectionName)) {
                specialSectionNames.put(sectionIndex, sectionName);
            } else {
                nonSpecialSectionNames.put(sectionIndex, sectionName);
            }
            sectionIndex++;
        }
    }

    /**
     * Creates a list of section rows such that all rows for special sections
     * occur before those of non-special sections.
     */
    private List<CourseEditSectionRow> createSpecialAndNonSpecialSectionRowsInOrder(
            int instructorIndex, List<String> sectionNames, List<String> feedbackNames,
            Map<Integer, String> specialSectionNames, Map<Integer, String> nonSpecialSectionNames) {
        List<CourseEditSectionRow> rows = new ArrayList<>();

        createSpecialSectionRows(instructorIndex, sectionNames, feedbackNames, rows, specialSectionNames);
        createNonSpecialSectionRows(instructorIndex, sectionNames, feedbackNames, rows, nonSpecialSectionNames);
        return rows;
    }

    /**
     * Adds special section rows as defined in {@code specialSectionNames} to {@code rows}.
     */
    private void createSpecialSectionRows(
            int instructorIndex, List<String> sectionNames, List<String> feedbackNames,
            List<CourseEditSectionRow> rows, Map<Integer, String> specialSectionNames) {
        int panelIndex = rows.size();

        for (Map.Entry<Integer, String> sectionNameEntry : specialSectionNames.entrySet()) {
            int sectionIndex = sectionNameEntry.getKey();
            String sectionName = sectionNameEntry.getValue();
            CourseEditSectionRow sectionRow = new CourseEditSectionRow(sectionName, sectionNames, sectionIndex,
                                                                       panelIndex, instructor,
                                                                       instructorIndex, feedbackNames);

            rows.add(sectionRow);
            panelIndex++;
        }
    }

    /**
     * Adds non special sections as defined in {@code nonSpecialSectionNames} to {@code rows}.
     */
    private void createNonSpecialSectionRows(
            int instructorIndex, List<String> sectionNames, List<String> feedbackNames,
            List<CourseEditSectionRow> rows, Map<Integer, String> nonSpecialSectionNames) {
        int panelIndex = rows.size();

        for (Map.Entry<Integer, String> sectionNameEntry : nonSpecialSectionNames.entrySet()) {
            if (firstBlankSectionRowIndex == sectionNames.size()) {
                firstBlankSectionRowIndex = panelIndex;
            }
            int sectionIndex = sectionNameEntry.getKey();
            String sectionName = sectionNameEntry.getValue();
            CourseEditSectionRow sectionRow = new CourseEditSectionRow(sectionName, sectionNames, sectionIndex,
                                                                       panelIndex, instructor,
                                                                       instructorIndex, feedbackNames);

            rows.add(sectionRow);
            panelIndex++;
        }
    }

    public List<CourseEditSectionRow> getSectionRows() {
        return sectionRows;
    }

    public int getIndex() {
        return index;
    }

    public int getFirstBlankSectionRowIndex() {
        return firstBlankSectionRowIndex;
    }

    public void setResendInviteButton(ElementTag resendInviteButton) {
        this.resendInviteButton = resendInviteButton;
    }

    public ElementTag getResendInviteButton() {
        return resendInviteButton;
    }

    public void setEditButton(ElementTag editButton) {
        this.editButton = editButton;
    }

    public ElementTag getEditButton() {
        return editButton;
    }

    public void setCancelButton(ElementTag cancelButton) {
        this.cancelButton = cancelButton;
    }

    public ElementTag getCancelButton() {
        return cancelButton;
    }

    public void setCancelAddInstructorButton(ElementTag cancelAddInstructorButton) {
        this.cancelAddInstructorButton = cancelAddInstructorButton;
    }

    public ElementTag getCancelAddInstructorButton() {
        return cancelAddInstructorButton;
    }

    public void setDeleteButton(ElementTag deleteButton) {
        this.deleteButton = deleteButton;
    }

    public ElementTag getDeleteButton() {
        return deleteButton;
    }

    public InstructorAttributes getInstructor() {
        return instructor;
    }

    public boolean isAccessControlDisplayed() {
        return isAccessControlDisplayed;
    }

    public List<ElementTag> getPermissionInputGroup1() {
        return permissionInputGroup1;
    }

    public List<ElementTag> getPermissionInputGroup2() {
        return permissionInputGroup2;
    }

    public List<ElementTag> getPermissionInputGroup3() {
        return permissionInputGroup3;
    }

    private boolean isSectionSpecial(String sectionName) {
        return instructor != null && instructor.privileges.isSectionSpecial(sectionName);
    }

    private List<ElementTag> createPermissionInputGroup3ForInstructorPanel() {
        List<ElementTag> permissionInputGroup = new ArrayList<>();

        permissionInputGroup.add(createCheckBox("Sessions: Submit Responses and Add Comments",
                                                Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS));

        permissionInputGroup.add(createCheckBox("Sessions: View Responses and Comments",
                                                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS));

        permissionInputGroup.add(createCheckBox(
                                    "Sessions: Edit/Delete Responses/Comments by Others",
                                    Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS));

        return permissionInputGroup;
    }

    private List<ElementTag> createPermissionInputGroup2ForInstructorPanel() {
        List<ElementTag> permissionInputGroup = new ArrayList<>();

        permissionInputGroup.add(createCheckBox("View Students' Details",
                                                Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS));

        return permissionInputGroup;
    }

    private List<ElementTag> createPermissionInputGroup1ForInstructorPanel() {
        List<ElementTag> permissionInputGroup = new ArrayList<>();

        permissionInputGroup.add(createCheckBox("Edit/Delete Course",
                                                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE));

        permissionInputGroup.add(createCheckBox("Add/Edit/Delete Instructors",
                                                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR));

        permissionInputGroup.add(createCheckBox("Create/Edit/Delete Sessions",
                                                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION));

        permissionInputGroup.add(createCheckBox("Enroll/Edit/Delete Students",
                                                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));

        return permissionInputGroup;
    }

    private ElementTag createCheckBox(String content, String privilege) {
        boolean isChecked = instructor != null && instructor.isAllowedForPrivilege(privilege);
        ElementTag result = new ElementTag(content, "name", privilege, "type", "checkbox", "value", "true");
        if (isChecked) {
            result.setAttribute("checked", null);
        }
        return result;
    }

}
