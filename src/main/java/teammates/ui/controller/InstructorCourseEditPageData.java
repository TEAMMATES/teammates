package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.template.CourseEditInstructorPanel;
import teammates.ui.template.ElementTag;


public class InstructorCourseEditPageData extends PageData {
    private int instructorToShowIndex;
    private ElementTag deleteCourseButton;
    private CourseAttributes course;
    private List<CourseEditInstructorPanel> instructorPanelList;
    private CourseEditInstructorPanel addInstructorPanel;
    private ElementTag addInstructorButton;
    
    public InstructorCourseEditPageData(AccountAttributes account, CourseAttributes course, 
                                        List<InstructorAttributes> instructorList, 
                                        InstructorAttributes currentInstructor, int instructorToShowIndex, 
                                        List<String> sectionNames, List<String> feedbackNames) {
        super(account);
        this.course = course;
        this.instructorToShowIndex = instructorToShowIndex;
        
        createButtons(currentInstructor);
        boolean isShowingAllInstructors = instructorToShowIndex == -1; 
        if (isShowingAllInstructors) {
            instructorPanelList = createInstructorPanelList(currentInstructor, instructorList, sectionNames,
                                                            feedbackNames);
        } else {
            instructorPanelList = createInstructorPanelForSingleInstructor(
                                            currentInstructor, instructorList.get(0), instructorToShowIndex, 
                                            sectionNames, feedbackNames);
        }
        addInstructorPanel = createInstructorPanel(currentInstructor, instructorPanelList.size() + 1, null, 
                                                   sectionNames, feedbackNames);
    }

    private List<CourseEditInstructorPanel> createInstructorPanelList(InstructorAttributes currentInstructor,
                                           List<InstructorAttributes> instructorList,
                                           List<String> sectionNames, List<String> feedbackNames) {
        List<CourseEditInstructorPanel> panelList = new ArrayList<CourseEditInstructorPanel>();
        int instructorIndex = 0;
        for (InstructorAttributes instructor : instructorList) {
            instructorIndex++;
            CourseEditInstructorPanel instructorPanel = createInstructorPanel(currentInstructor, 
                                                                              instructorIndex, instructor, 
                                                                              sectionNames, feedbackNames); 
            panelList.add(instructorPanel);
        }
        return panelList;
    }
    
    private List<CourseEditInstructorPanel> createInstructorPanelForSingleInstructor(InstructorAttributes currentInstructor,
                                    InstructorAttributes instructorForPanel, int instructorIndex,
                                    List<String> sectionNames, List<String> feedbackNames) {
         List<CourseEditInstructorPanel> panelList = new ArrayList<CourseEditInstructorPanel>();
         CourseEditInstructorPanel instructorPanel = createInstructorPanel(
                                                             currentInstructor, 
                                                             instructorIndex, instructorForPanel, 
                                                             sectionNames, feedbackNames); 
         panelList.add(instructorPanel);
     
         return panelList;
     }

    private void createButtons(InstructorAttributes currentInstructor) {
        boolean isDisabled = !currentInstructor.isAllowedForPrivilege(
                                                    Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE);
        String content = "<span class=\"glyphicon glyphicon-trash\"></span>Delete";
        String onClick = "return toggleDeleteCourseConfirmation('" + course.id + "');";
        deleteCourseButton = createButton(content, "btn btn-primary btn-xs pull-right", "courseDeleteLink", 
                                          getInstructorCourseDeleteLink(course.id, false), 
                                          Const.Tooltips.COURSE_DELETE, onClick, isDisabled);
        
        isDisabled = !currentInstructor.isAllowedForPrivilege(
                                            Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR);
        addInstructorButton = createButton(null, "btn btn-primary", "btnShowNewInstructorForm", null, null, 
                                           "showNewInstructorForm()", isDisabled);
    }
    
    private CourseEditInstructorPanel createInstructorPanel(InstructorAttributes currentInstructor, 
                                                            int instructorIndex, 
                                                            InstructorAttributes instructor,
                                                            List<String> sectionNames, 
                                                            List<String> feedbackNames) {
        CourseEditInstructorPanel instructorPanel = new CourseEditInstructorPanel(instructorToShowIndex, 
                                                                          instructorIndex, instructor,
                                                                          sectionNames, feedbackNames);
        
        if (instructor != null) {
            String buttonContent = "<span class=\"glyphicon glyphicon-envelope\"></span> Resend Invite";
            boolean isDisabled = !currentInstructor.isAllowedForPrivilege(
                                                            Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR);
            String href;
            String onClick;
            if (instructor.googleId == null) {
                href = getInstructorCourseRemindInstructorLink(instructor.courseId, instructor.email);
                onClick = "return toggleSendRegistrationKey('" + instructor.courseId + "','" + instructor.email + ");";
                instructorPanel.setResendInviteButton(createButton(buttonContent, "btn btn-primary btn-xs", 
                                                                   "instrRemindLink" + instructorPanel.getIndex(), 
                                                                   href, Const.Tooltips.COURSE_INSTRUCTOR_REMIND, 
                                                                   onClick, isDisabled));
            }
            
            buttonContent = "<span class=\"glyphicon glyphicon-pencil\"></span> Edit";
            instructorPanel.setEditButton(createButton(buttonContent, "btn btn-primary btn-xs", 
                                                       "instrEditLink" + instructorPanel.getIndex(), 
                                                       "javascript:;", Const.Tooltips.COURSE_INSTRUCTOR_EDIT,
                                                       null, isDisabled));
            
            buttonContent = "<span class=\"glyphicon glyphicon-trash\"></span> Delete";
            href = getInstructorCourseInstructorDeleteLink(instructor.courseId, instructor.email);
            onClick = "return toggleDeleteInstructorConfirmation('" + instructor.courseId + "','" 
                      + instructor.email + "', " + instructor.email.equals(this.account.email) + ");";
            instructorPanel.setDeleteButton(createButton(buttonContent, "btn btn-primary btn-xs", 
                                                         "instrDeleteLink" + instructorPanel.getIndex(), 
                                                         href, Const.Tooltips.COURSE_INSTRUCTOR_DELETE, 
                                                         onClick, isDisabled));
        }
        
        return instructorPanel;
    }
    
    public ElementTag getDeleteCourseButton() {
        return deleteCourseButton;
    }
    
    public ElementTag getAddInstructorButton() {
        return addInstructorButton;
    }
    
    public CourseEditInstructorPanel getAddInstructorPanel() {
        return addInstructorPanel;
    }
    
    public CourseAttributes getCourse() {
        return course;
    }
    
    public List<CourseEditInstructorPanel> getInstructorPanelList() {
        return instructorPanelList;
    }
    
    public int getInstructorToShowIndex() {
        return instructorToShowIndex;
    }
    
    private ElementTag createButton(String content, String buttonClass, String id, String href, 
                                    String title, String onClick, boolean isDisabled) {
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
        
        if (title != null) {
            button.setAttribute("title", title);
            button.setAttribute("data-toggle", "tooltip");
            button.setAttribute("data-placement", "top");
        }
        
        if (onClick != null) {
            button.setAttribute("onclick", onClick);
        }
        
        if (isDisabled) {
            button.setAttribute("disabled", "disabled");
        }
        return button;
    }
}

