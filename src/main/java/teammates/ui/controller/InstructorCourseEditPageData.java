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
    private int instructorToShowIndex;
    private ElementTag deleteCourseButton;
    private CourseAttributes course;
    private List<CourseEditInstructorPanel> instructorPanelList;
    private CourseEditInstructorPanel addInstructorPanel;
    private InstructorAttributes currentInstructor;
    private ElementTag addInstructorButton;
    
    public InstructorCourseEditPageData(AccountAttributes account, CourseAttributes course, 
                                        List<InstructorAttributes> instructorList, 
                                        InstructorAttributes currentInstructor, int instructorToShowIndex, 
                                        List<String> sectionNames, List<String> feedbackNames) {
        super(account);
        this.course = course;
        this.instructorToShowIndex = instructorToShowIndex;
        this.currentInstructor = currentInstructor;
        
        createButtons();
        
        instructorPanelList = createInstructorPanelList(instructorList, sectionNames, feedbackNames);
        addInstructorPanel = createInstructorPanel(instructorPanelList.size() + 1, null, sectionNames, 
                                                   feedbackNames);
    }

    private List<CourseEditInstructorPanel> createInstructorPanelList(List<InstructorAttributes> instructorList,
                                           List<String> sectionNames, List<String> feedbackNames) {
        List<CourseEditInstructorPanel> panelList = new ArrayList<CourseEditInstructorPanel>();
        int instructorIndex = 0;
        for (InstructorAttributes instructor : instructorList) {
            instructorIndex++;
            CourseEditInstructorPanel instructorPanel = createInstructorPanel(instructorIndex, instructor, 
                                                                              sectionNames, feedbackNames); 
            panelList.add(instructorPanel);
        }
        return panelList;
    }

    private void createButtons() {
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
    
    private CourseEditInstructorPanel createInstructorPanel(int instructorIndex, 
                                                            InstructorAttributes instructor,
                                                            List<String> sectionNames, 
                                                            List<String> feedbackNames) {
        CourseEditInstructorPanel instructorPanel = new CourseEditInstructorPanel(instructorToShowIndex, 
                                                                                  instructorIndex, instructor, 
                                                                                  currentInstructor, sectionNames, 
                                                                                  feedbackNames);
        
        if (instructor != null) {
            String buttonContent = "<span class=\"glyphicon glyphicon-envelope\"></span> Resend Invite";
            boolean isDisabled = !currentInstructor.isAllowedForPrivilege(
                                                            Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR);
            String href;
            String onClick;
            if (instructor.googleId == null) {
                href = getInstructorCourseInstructorRemindLink(instructor.courseId, instructor.email);
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
    
    private String getInstructorCourseInstructorDeleteLink(String courseId, String instructorEmail) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_INSTRUCTOR_DELETE;
        link = Url.addParamToUrl(link,Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link,Const.ParamsNames.INSTRUCTOR_EMAIL, instructorEmail);
        link = addUserIdToUrl(link);
        return link;
    }
    
    private String getInstructorCourseInstructorRemindLink(String courseId, String instructorEmail) {
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

