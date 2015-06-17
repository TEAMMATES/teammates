package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.common.util.Url;
import teammates.ui.template.CourseDetailsStudentsTable;
import teammates.ui.template.CourseDetailsStudentsTableRow;
import teammates.ui.template.ElementTag;

/**
 * PageData: data used for the "Course Details" page
 */
public class InstructorCourseDetailsPageData extends PageData {
    
    public InstructorCourseDetailsPageData(AccountAttributes account) {
        super(account);
    }

    public InstructorAttributes currentInstructor;
    public CourseDetailsBundle courseDetails;
    public List<InstructorAttributes> instructors;
    public String studentListHtmlTableAsString;
    public ElementTag giveCommentButton;
    public ElementTag courseRemindButton;
    public CourseDetailsStudentsTable studentsTable;
    
    public void init(InstructorAttributes currentInstructorParam, CourseDetailsBundle courseDetailsParam, 
                     List<InstructorAttributes> instructorsParam, List<StudentAttributes> students) {
        this.currentInstructor = currentInstructorParam;
        this.courseDetails = courseDetailsParam;
        this.instructors = instructorsParam;
        
        boolean isDisabled = !this.currentInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS);
        this.giveCommentButton = createButton("<span class=\"glyphicon glyphicon-comment glyphicon-primary\"></span>",
                                              "btn btn-default btn-xs icon-button pull-right", "button_add_comment", 
                                              null, "", "tooltip", null, isDisabled);
  
        isDisabled = !this.currentInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);
        String onClick = "if(toggleSendRegistrationKeysConfirmation('" + Sanitizer.sanitizeForJs(this.courseDetails.course.id) + "')) "
                                + "window.location.href='" + Sanitizer.sanitizeForJs(this.getInstructorCourseRemindLink()) + "';";
        
        this.courseRemindButton = createButton(null, "btn btn-primary", "button_remind", null, 
                                               Const.Tooltips.COURSE_REMIND, "tooltip", onClick, isDisabled);
        
        this.studentsTable = new CourseDetailsStudentsTable();
        int idx = 0;
        for (StudentAttributes student : students) {
            CourseDetailsStudentsTableRow row = new CourseDetailsStudentsTableRow();
            row.student = student;
            
            isDisabled = !this.currentInstructor.isAllowedForPrivilege(student.section, Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS);
            ElementTag viewButton = createButton("View", "btn btn-default btn-xs", null, this.getCourseStudentDetailsLink(student),
                                                 Const.Tooltips.COURSE_STUDENT_DETAILS, "tooltip", null, isDisabled);
            
            isDisabled = !this.currentInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);
            ElementTag editButton = createButton("Edit", "btn btn-default btn-xs", null, this.getCourseStudentEditLink(student),
                                                 Const.Tooltips.COURSE_STUDENT_EDIT, "tooltip", null, isDisabled);
            
            isDisabled = !this.currentInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);
            ElementTag sendInviteButton = createButton("Send Invite", "btn btn-default btn-xs", null, this.getCourseStudentRemindLink(student),
                                                 Const.Tooltips.COURSE_STUDENT_REMIND, "tooltip", "return toggleSendRegistrationKey()", isDisabled);
       
            isDisabled = !this.currentInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);
            onClick = "return toggleDeleteStudentConfirmation('" + sanitizeForJs(student.course) + "','" + sanitizeForJs(student.name) + "')";
            ElementTag deleteButton = createButton("Delete", "btn btn-default btn-xs", null, this.getCourseStudentDeleteLink(student),
                                                   Const.Tooltips.COURSE_STUDENT_DELETE, "tooltip", onClick, isDisabled);
            
            ElementTag allRecordsButton = createButton("All Records", "btn btn-default btn-xs", null, this.getStudentRecordsLink(student),
                                                   Const.Tooltips.COURSE_STUDENT_RECORDS, "tooltip", null, false);

            row.actions.add(viewButton);
            row.actions.add(editButton);
            if (!student.isRegistered()) {
                row.actions.add(sendInviteButton);
            }
            row.actions.add(deleteButton);
            row.actions.add(allRecordsButton);
            
            isDisabled = !this.currentInstructor.isAllowedForPrivilege(student.section, Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS);
            ElementTag addCommentButton = createButton("Add Comment", "btn btn-default btn-xs cursor-default", null, "javascript:;",
                                                       Const.Tooltips.COURSE_STUDENT_COMMENT, "tooltip", null, isDisabled);
            
            isDisabled = !this.currentInstructor.isAllowedForPrivilege(student.section, Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS);
            ElementTag addCommentsButton = createButton("<span class=\"caret\"></span><span class=\"sr-only\">Add comments</span>", 
                                                        "btn btn-default btn-xs dropdown-toggle", null, "javascript:;",
                                                        null, "dropdown", null, isDisabled);
            row.commentActions.add(addCommentButton);
            row.commentActions.add(addCommentsButton);
            
            ElementTag toStudentCommentOption = createButton("To student: " + sanitizeForHtml(row.student.name), 
                                                             "t_student_details_tostudent-c" + this.courseDetails.course.id + "." + idx, null,
                                                             getCourseStudentDetailsLink(row.student) + "&" + Const.ParamsNames.SHOW_COMMENT_BOX + "=student", 
                                                             null, null, null, false);
            toStudentCommentOption.setAttribute("role", "menuitem");
            toStudentCommentOption.setAttribute("tabindex", "-1");
            
            ElementTag toTeamCommentOption = createButton("To team: " + sanitizeForHtml(row.student.team), 
                                                          "t_student_details_toteam-c" + this.courseDetails.course.id + "." + idx, null,
                                                          getCourseStudentDetailsLink(row.student) + "&" + Const.ParamsNames.SHOW_COMMENT_BOX + "=team", 
                                                          null, null, null, false);
            toTeamCommentOption.setAttribute("role", "menuitem");
            toTeamCommentOption.setAttribute("tabindex", "-1");
            
            row.commentRecipientOptions.add(toStudentCommentOption);
            row.commentRecipientOptions.add(toTeamCommentOption);
            
            if ((row.student.section != null) && (!row.student.section.equals("None"))) {
                ElementTag toSectionCommentOption = createButton("To section: " + sanitizeForHtml(row.student.section), 
                                                                 "t_student_details_tosection-c" + this.courseDetails.course.id + "." + idx, null,
                                                                 getCourseStudentDetailsLink(row.student) + "&" + Const.ParamsNames.SHOW_COMMENT_BOX + "=section", 
                                                                 null, null, null, false);
                toSectionCommentOption.setAttribute("role", "menuitem");
                toSectionCommentOption.setAttribute("tabindex", "-1");
                  
                row.commentRecipientOptions.add(toSectionCommentOption);
            }
            
            studentsTable.rows.add(row);
            idx++;
        }
          
    }
    
    public InstructorAttributes getCurrentInstructor() {
        return this.currentInstructor;
    }
    
    public CourseDetailsBundle getCourseDetails() {
        return this.courseDetails;
    }
    
    public List<InstructorAttributes> getInstructors() {
        return this.instructors;
    }
    
    public CourseDetailsStudentsTable getStudentsTable() {
        return this.studentsTable;
    }
    
    public ElementTag getGiveCommentButton() {
        return this.giveCommentButton;
    }
    
    public ElementTag getCourseRemindButton() {
        return this.courseRemindButton;
    }
    
    public String getInstructorCourseRemindLink() {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_REMIND;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseDetails.course.id);
        link = addUserIdToUrl(link);
        return link;
    }
    
    
    public String getCourseStudentDetailsLink(StudentAttributes student) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseDetails.course.id);
        link = Url.addParamToUrl(link, Const.ParamsNames.STUDENT_EMAIL, student.email);
        link = addUserIdToUrl(link);
        return link;
    }
    
    
    public String getCourseStudentEditLink(StudentAttributes student) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseDetails.course.id);
        link = Url.addParamToUrl(link, Const.ParamsNames.STUDENT_EMAIL, student.email);
        link = addUserIdToUrl(link);
        return link;
    }
    
    
    public String getCourseStudentRemindLink(StudentAttributes student) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_REMIND;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseDetails.course.id);
        link = Url.addParamToUrl(link, Const.ParamsNames.STUDENT_EMAIL, student.email);
        link = addUserIdToUrl(link);
        return link;
    }
    
    
    public String getCourseStudentDeleteLink(StudentAttributes student) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DELETE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseDetails.course.id);
        link = Url.addParamToUrl(link, Const.ParamsNames.STUDENT_EMAIL, student.email);
        link = addUserIdToUrl(link);
        return link;
    }
    
    public String getStudentRecordsLink(StudentAttributes student) {
        String link = Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, student.course);
        link = Url.addParamToUrl(link, Const.ParamsNames.STUDENT_EMAIL, student.email);
        link = addUserIdToUrl(link);
        return link;
    }
    
    private ElementTag createButton(String content, String buttonClass, String id, String href, 
                            String title, String dataToggle, String onClick, boolean isDisabled){
        ElementTag button = new ElementTag(content);
        
        if ((buttonClass != null) && (!buttonClass.equals(""))) {
            button.setAttribute("class", buttonClass);
        }
        
        if ((id != null) && (!id.equals(""))) {
            button.setAttribute("id", id);
        }
        
        if ((href != null) && (!href.equals(""))) {
            button.setAttribute("href", href);
        }
        
        if (title != null) {
            button.setAttribute("title", title);
            button.setAttribute("data-placement", "top");
        }
        
        if (dataToggle != null) {
            button.setAttribute("data-toggle", dataToggle);
        }
                
        if ((onClick != null) && (!onClick.equals(""))) {
            button.setAttribute("onclick", onClick);
        }
        
        if (isDisabled) {
            button.setAttribute("disabled", "disabled");
        }
        return button;
    }
}
