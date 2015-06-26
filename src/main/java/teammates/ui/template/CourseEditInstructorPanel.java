package teammates.ui.template;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;

public class CourseEditInstructorPanel {
    private int index;
    private InstructorAttributes instructor;
    private List<ElementTag> permissionInputGroup1;
    private List<ElementTag> permissionInputGroup2;
    private List<ElementTag> permissionInputGroup3;
    private ElementTag resendInviteButton;
    private ElementTag editButton;
    private ElementTag deleteButton;
    private ElementTag addSectionLevelForInstructorButton;
    private boolean isAccessControlDisplayed;
    private List<CourseEditSectionRow> sectionRows;
    
    public CourseEditInstructorPanel(int instructorToShowIndex, int instructorIndex, InstructorAttributes instructor, 
                                    InstructorAttributes currentInstructor,List<String> sectionNames, List<String> feedbackNames) {
        
        if (instructorToShowIndex == -1) { 
            index = instructorIndex;
            isAccessControlDisplayed = false;
        } else {
            index = instructorToShowIndex;
            isAccessControlDisplayed = true;
        }
        
        this.instructor = instructor;
        
        sectionRows = new ArrayList<CourseEditSectionRow>();
        int sectionIndex = -1; 
        for (String sectionName : sectionNames) {
            sectionIndex++;
            CourseEditSectionRow sectionRow = new CourseEditSectionRow(sectionName, sectionNames, sectionIndex, instructor, instructorIndex, feedbackNames);
            sectionRows.add(sectionRow);
        }
        
        permissionInputGroup1 = createPermissionInputGroup1ForInstructorPanel(instructor);
        permissionInputGroup2 = createPermissionInputGroup2ForInstructorPanel(instructor);
        permissionInputGroup3 = createPermissionInputGroup3ForInstructorPanel(instructor);
        
        if (instructor != null) {
            String style = null;
            if (instructor.privileges.numberOfSectionsSpecial() >= sectionNames.size()) {
                style = "display: none;";
            }
            addSectionLevelForInstructorButton = createButton("Give different permissions for a specific section", "small",
                                                              "addSectionLevelForInstructor" + index, "javascript:;", style, 
                                                              "showTuneSectionPermissionsDiv(" + index + ", " + instructor.privileges.numberOfSectionsSpecial() + ")");
        }
    }
    
    public List<CourseEditSectionRow> getSectionRows() {
        return sectionRows;
    }
    
    public int getIndex() {
        return index;
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
    
    public void setDeleteButton(ElementTag deleteButton) {
        this.deleteButton = deleteButton;
    }
    
    public ElementTag getDeleteButton() {
        return deleteButton;
    }
    
    public InstructorAttributes getInstructor() {
        return instructor;
    }
    
    public ElementTag getAddSectionLevelForInstructorButton() {
        return addSectionLevelForInstructorButton;
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
    
    private List<ElementTag> createPermissionInputGroup3ForInstructorPanel(InstructorAttributes instructor) {
        List<ElementTag> permissionInputGroup = new ArrayList<ElementTag>();
        
        boolean isChecked = (instructor == null) || instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS);
        permissionInputGroup.add(createCheckBox("Sessions: Submit Responses and Add Comments", Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS, isChecked));
        
        isChecked = (instructor == null) || instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS);
        permissionInputGroup.add(createCheckBox("Sessions: View Responses and Comments", Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS, isChecked));
        
        isChecked = (instructor == null) || instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS);
        permissionInputGroup.add(createCheckBox("Sessions: Edit/Delete Responses/Comments by Others", Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS, isChecked));
        
        return permissionInputGroup;
    }

    private List<ElementTag> createPermissionInputGroup2ForInstructorPanel(InstructorAttributes instructor) {
        List<ElementTag> permissionInputGroup = new ArrayList<ElementTag>();
        
        boolean isChecked = (instructor == null) || instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS);
        permissionInputGroup.add(createCheckBox("View Students' Details", Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS, isChecked));
        
        isChecked = (instructor == null) || instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS);
        permissionInputGroup.add(createCheckBox("Give Comments for Students", Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS, isChecked));
        
        isChecked = (instructor == null) || instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS);
        permissionInputGroup.add(createCheckBox("View Others' Comments on Students", Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS, isChecked));
        
        isChecked = (instructor == null) || instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS);
        permissionInputGroup.add(createCheckBox("Edit/Delete Others' Comments on Students", Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS, isChecked));
        
        return permissionInputGroup;
    }

    private List<ElementTag> createPermissionInputGroup1ForInstructorPanel(InstructorAttributes instructor) {
        List<ElementTag> permissionInputGroup = new ArrayList<ElementTag>();
        
        boolean isChecked = (instructor == null) || instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE);
        permissionInputGroup.add(createCheckBox("Edit/Delete Course", Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE, isChecked));
        
        isChecked = (instructor == null) || instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR);
        permissionInputGroup.add(createCheckBox("Add/Edit/Delete Instructors", Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, isChecked));
        
        isChecked = (instructor == null) || instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
        permissionInputGroup.add(createCheckBox("Create/Edit/Delete Sessions", Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION, isChecked));
        
        isChecked = (instructor == null) || instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);
        permissionInputGroup.add(createCheckBox("Enroll/Edit/Delete Students", Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT, isChecked));
        
        return permissionInputGroup;
    }
    
    private ElementTag createCheckBox(String content, String name, boolean isChecked) {
        ElementTag result = new ElementTag(content, "name", name, "type", "checkbox", "value", "true");
        if (isChecked) {
            result.setAttribute("checked", "checked");
        }
        return result;
    }
    

    private ElementTag createButton(String content, String buttonClass, String id, String href, 
                                    String style, String onClick) {
        ElementTag button = new ElementTag(content);
        
        if (buttonClass != null) {
            button.setAttribute("class", buttonClass);
        }
        
        if (id != null) {
            button.setAttribute("id", id);
        }
        
        if (href != null) {
            button.setAttribute("href", href);
        }
        
        if (style != null) {
            button.setAttribute("style", style);
        }
        
        if (onClick != null) {
            button.setAttribute("onclick", onClick);
        }
        
        return button;
    }
}
