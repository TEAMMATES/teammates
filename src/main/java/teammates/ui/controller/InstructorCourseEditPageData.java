package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.ui.template.CourseEditInstructorPanel;
import teammates.ui.template.ElementTag;


public class InstructorCourseEditPageData extends PageData {
    public int offset;
    public ElementTag deleteCourseButton;
    public CourseAttributes course;
    public List<CourseEditInstructorPanel> instructorPanelList;
    public CourseEditInstructorPanel addInstructorPanel;
    public InstructorAttributes currentInstructor;
    public ElementTag addInstructorButton;
    
    public InstructorCourseEditPageData(AccountAttributes account) {
        super(account);
        instructorPanelList = new ArrayList<CourseEditInstructorPanel>();
    }
    
    public void init(CourseAttributes course, List<InstructorAttributes> instructorList, InstructorAttributes currentInstructor, 
                     int offset, List<String> sectionNames, List<String> feedbackNames) {
        this.course = course;
        this.offset = offset;
        this.currentInstructor = currentInstructor;
        
        boolean isDisabled = !currentInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE);
        deleteCourseButton = createButton("<span class=\"glyphicon glyphicon-trash\"></span>Delete", "btn btn-primary btn-xs pull-right",
                                          "courseDeleteLink", getInstructorCourseDeleteLink(this.course.id, false), Const.Tooltips.COURSE_DELETE,
                                          "return toggleDeleteCourseConfirmation('" + this.course.id + "');", isDisabled);
        
        isDisabled = !currentInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR);
        addInstructorButton = createButton(null, "btn btn-primary", "btnShowNewInstructorForm", null, null, "showNewInstructorForm()", isDisabled);
        
        int instructorIndex = 0;
        for (InstructorAttributes instructor : instructorList) {
            instructorIndex++;
            CourseEditInstructorPanel instructorPanel = createInstructorPanel(instructorIndex, instructor, sectionNames, feedbackNames); 
            instructorPanelList.add(instructorPanel);
        }
        
        addInstructorPanel = createInstructorPanel(instructorIndex + 1, null, sectionNames, feedbackNames);
    }
    
    private CourseEditInstructorPanel createInstructorPanel(int instructorIndex, InstructorAttributes instructor, 
                                                            List<String> sectionNames, List<String> feedbackNames) {
        CourseEditInstructorPanel instructorPanel = new CourseEditInstructorPanel(offset, instructorIndex, instructor, 
                                                                                  currentInstructor, sectionNames, feedbackNames);
        
        if (instructor != null) {
            String buttonContent = "<span class=\"glyphicon glyphicon-envelope\"></span> Resend Invite";
            boolean isDisabled;
            if (instructor.googleId == null) {
                isDisabled = !this.currentInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR);
                instructorPanel.resendInviteButton = createButton(buttonContent, "btn btn-primary btn-xs", 
                                                                  "instrRemindLink" + instructorPanel.index, 
                                                                  this.getInstructorCourseInstructorRemindLink(instructor.courseId, instructor.email),
                                                                  Const.Tooltips.COURSE_INSTRUCTOR_REMIND, 
                                                                  "return toggleSendRegistrationKey('" + instructor.courseId + "','" + instructor.email + ");",
                                                                  isDisabled);
            }
            
            buttonContent = "<span class=\"glyphicon glyphicon-pencil\"></span> Edit";
            isDisabled = !this.currentInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR);
            instructorPanel.editButton = createButton(buttonContent, "btn btn-primary btn-xs",
                                                      "instrEditLink" + instructorPanel.index, "javascript:;", 
                                                      Const.Tooltips.COURSE_INSTRUCTOR_EDIT,
                                                      null, isDisabled);
            
            buttonContent = "<span class=\"glyphicon glyphicon-trash\"></span> Delete";
            isDisabled = !this.currentInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR);
            instructorPanel.deleteButton = createButton(buttonContent, "btn btn-primary btn-xs", 
                                                        "instrDeleteLink" + instructorPanel.index, 
                                                        this.getInstructorCourseInstructorDeleteLink(instructor.courseId, instructor.email), 
                                                        Const.Tooltips.COURSE_INSTRUCTOR_DELETE, 
                                                        "return toggleDeleteInstructorConfirmation('" + instructor.courseId + "','" + instructor.email + "', " + instructor.email.equals(this.account.email) + ");",
                                                        isDisabled);
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
    
    public List<InstructorAttributes> getInstructorList() {
        ArrayList<InstructorAttributes> instructorList = new ArrayList<InstructorAttributes>();
        for (CourseEditInstructorPanel instructorPanel : instructorPanelList) {
            instructorList.add(instructorPanel.instructor);
        }
        return instructorList;
    }
    
    
    public int getOffset() {
        return this.offset;
    }
    
    public String getInstructorCourseInstructorDeleteLink(String courseId, String instructorEmail) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_INSTRUCTOR_DELETE;
        link = Url.addParamToUrl(link,Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link,Const.ParamsNames.INSTRUCTOR_EMAIL, instructorEmail);
        link = addUserIdToUrl(link);
        return link;
    }
    
    public String getInstructorCourseInstructorRemindLink(String courseId, String instructorEmail) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_REMIND;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.INSTRUCTOR_EMAIL, instructorEmail);
        link = addUserIdToUrl(link);
        return link;
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

