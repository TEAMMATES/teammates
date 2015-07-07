package teammates.ui.template;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;

public class CourseEditSectionRow {
    private String sectionName;
    private InstructorAttributes instructor;
    private List<ElementTag> permissionInputGroup2;
    private List<ElementTag> permissionInputGroup3;
    private ElementTag toggleSessionLevelInSectionButton;
    private List<CourseEditFeedbackSessionRow> feedbackSessions;
    private List<List<ElementTag>> specialSections;
    
    public CourseEditSectionRow(String sectionName, List<String> sectionNames, int sectionIndex, 
                                InstructorAttributes instructor, int instructorIndex, 
                                List<String> feedbackNames) {
        this.sectionName = sectionName;
        this.instructor = instructor;
        feedbackSessions = new ArrayList<CourseEditFeedbackSessionRow>();
        
        specialSections = createSpecialSectionsForSectionRow(sectionNames, sectionIndex);
        permissionInputGroup2 = createPermissionInputGroup2ForSectionRow(sectionIndex);
        permissionInputGroup3 = createPermissionInputGroup3ForSectionRow(sectionIndex);
        
        String content = "";
        String onClick = "";
        if (!isSessionsInSectionSpecial()) {
            content = "Give different permissions for sessions in this section";
            onClick = "showTuneSessionnPermissionsDiv(" + instructorIndex + ", " + sectionIndex + ")";
        } else {
            content = "Hide session-level permissions";
            onClick = "hideTuneSessionnPermissionsDiv(" + instructorIndex + ", " + sectionIndex + ")";
        }
        
        String id = "toggleSessionLevelInSection" + sectionIndex + "ForInstructor" + instructorIndex;
        toggleSessionLevelInSectionButton = createButton(content, "small col-sm-5", id, "javascript:;", onClick);
        
        String[] privileges = {Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS,
                               Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS,
                               Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS};
        
        for (String feedbackName : feedbackNames) {
            List<ElementTag> checkBoxList = new ArrayList<ElementTag>();
            for (String privilege : privileges) {
                String name = privilege + Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + sectionIndex 
                              + "feedback" + feedbackName;
                boolean isChecked = (instructor != null) && instructor.isAllowedForPrivilege(sectionName, 
                                                                                             feedbackName,
                                                                                             privilege);
                checkBoxList.add(createCheckBox(null, name, "true", isChecked));
            }
            
            CourseEditFeedbackSessionRow feedbackSessionRow = new CourseEditFeedbackSessionRow(feedbackName, 
                                                                                               checkBoxList);
            feedbackSessions.add(feedbackSessionRow);
        }
    }
    
    public List<CourseEditFeedbackSessionRow> getFeedbackSessions() {
        return feedbackSessions;
    }
    
    public boolean isSectionSpecial() {
        return (instructor != null) && instructor.privileges.isSectionSpecial(sectionName);
    }
    
    public boolean isSessionsInSectionSpecial() {
        return (instructor != null) && instructor.privileges.isSessionsInSectionSpecial(sectionName);
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
    
    private List<ElementTag> createPermissionInputGroup3ForSectionRow(int sectionIndex) {
        List<ElementTag> permissionInputGroup = new ArrayList<ElementTag>();
        
        String[] privileges = {Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS,
                               Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS,
                               Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS};
        
        String[] checkboxContent = {"Sessions: Submit Responses and Add Comments",
                                    "Sessions: View Responses and Comments",
                                    "Sessions: Edit/Delete Responses/Comments by Others"};
                 
        int index = 0;
        for (String privilege : privileges) {
            boolean isChecked = (instructor != null) && instructor.isAllowedForPrivilege(sectionName, 
                                                                                         privilege);
            String name = privilege + Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + sectionIndex;
            permissionInputGroup.add(createCheckBox(checkboxContent[index], name, "true", isChecked));
            index++;
        }
        
        return permissionInputGroup;
    }

    private List<ElementTag> createPermissionInputGroup2ForSectionRow(int sectionIndex) {
        List<ElementTag> permissionInputGroup = new ArrayList<ElementTag>();
        
        String[] privileges = {Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS,
                               Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS,
                               Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_COMMENT_IN_SECTIONS,
                               Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS};
         
        String[] checkboxContent = {"View Students' Details",
                                    "Give Comments for Students",
                                    "View Others' Comments on Students",
                                    "Edit/Delete Others' Comments on Students"};
        
        int index = 0;
        for (String privilege : privileges) {
            boolean isChecked = (instructor != null) && instructor.isAllowedForPrivilege(sectionName, 
                                                                                         privilege);
            String name = privilege + Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + sectionIndex;
            permissionInputGroup.add(createCheckBox(checkboxContent[index], name, "true", isChecked));
            index++;
        }
        
        return permissionInputGroup;
    }

    private List<List<ElementTag>> createSpecialSectionsForSectionRow(List<String> sectionNames, 
                                                                      int sectionIndex) {
        List<List<ElementTag>> specialSections = new ArrayList<List<ElementTag>>();
        for (int i = 0; i < sectionNames.size(); i += 3) {
            List<ElementTag> specialSectionGroup = new ArrayList<ElementTag>();
            for (int j = 0; (j < 3) && (i + j < sectionNames.size()); j++) {
                String name = Const.ParamsNames.INSTRUCTOR_SECTION_GROUP + sectionIndex
                              + Const.ParamsNames.INSTRUCTOR_SECTION + (i + j);
                ElementTag checkbox = createCheckBox(sectionNames.get(i + j), name, sectionNames.get(i + j),
                                                     (i + j == sectionIndex));
                specialSectionGroup.add(checkbox);
            }
            specialSections.add(specialSectionGroup);
        }
        return specialSections;
    }
    
    private ElementTag createCheckBox(String content, String name, String value, boolean isChecked) {
        ElementTag result = new ElementTag(content, "name", name, "value", value, "type", "checkbox");
        if (isChecked) {
            result.setAttribute("checked", "checked");
        }
        return result;
    }
    
    private ElementTag createButton(String content, String buttonClass, String id, String href, String onClick) {
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
        
        if (onClick != null) {
            button.setAttribute("onclick", onClick);
        }
        
        return button;
    }
}
