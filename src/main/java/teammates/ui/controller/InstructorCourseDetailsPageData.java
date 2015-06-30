package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
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
    
    public void init(InstructorAttributes currentInstructor, CourseDetailsBundle courseDetails, 
                     List<InstructorAttributes> instructors, List<StudentAttributes> students) {
        this.currentInstructor = currentInstructor;
        this.courseDetails = courseDetails;
        this.instructors = instructors;
        
        boolean isDisabled = !currentInstructor.isAllowedForPrivilege(
                                                    Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS);
        
        String content = "<span class=\"glyphicon glyphicon-comment glyphicon-primary\"></span>";
        giveCommentButton = createButton(content, "btn btn-default btn-xs icon-button pull-right", 
                                         "button_add_comment", null, "", "tooltip", null, isDisabled);
        
        isDisabled = !currentInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);
        String onClick = "if(toggleSendRegistrationKeysConfirmation('" 
                          + sanitizeForJs(courseDetails.course.id) + "')) "
                          + "window.location.href='" + sanitizeForJs(getInstructorCourseRemindLink()) + "';";
        courseRemindButton = createButton(null, "btn btn-primary", "button_remind", null, 
                                          Const.Tooltips.COURSE_REMIND, "tooltip", onClick, isDisabled);
        
        studentsTable = new CourseDetailsStudentsTable();
        int studentIndex = 0;
        for (StudentAttributes student : students) {
            CourseDetailsStudentsTableRow row = createStudentsTableRow(studentIndex, student);
            studentsTable.getRows().add(row);
            studentIndex++;
        }
    }
    
    public CourseDetailsStudentsTableRow createStudentsTableRow(int studentIndex, StudentAttributes student) {
        CourseDetailsStudentsTableRow row = new CourseDetailsStudentsTableRow(student);
        
        boolean isDisabled = !currentInstructor.isAllowedForPrivilege(student.section, 
                                                    Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS);
        ElementTag viewButton = createButton("View", "btn btn-default btn-xs", null, getCourseStudentDetailsLink(student),
                                             Const.Tooltips.COURSE_STUDENT_DETAILS, "tooltip", null, isDisabled);
        
        isDisabled = !currentInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);
        ElementTag editButton = createButton("Edit", "btn btn-default btn-xs", null, getCourseStudentEditLink(student),
                                             Const.Tooltips.COURSE_STUDENT_EDIT, "tooltip", null, isDisabled);
        
        isDisabled = !currentInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);
        ElementTag sendInviteButton = createButton("Send Invite", "btn btn-default btn-xs", null, 
                                                   getCourseStudentRemindLink(student), Const.Tooltips.COURSE_STUDENT_REMIND, 
                                                   "tooltip", "return toggleSendRegistrationKey()", isDisabled);
   
        isDisabled = !currentInstructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);
        String onClick = "return toggleDeleteStudentConfirmation('" + sanitizeForJs(student.course) 
                         + "','" + sanitizeForJs(student.name) + "')";
        ElementTag deleteButton = createButton("Delete", "btn btn-default btn-xs", null, getCourseStudentDeleteLink(student),
                                               Const.Tooltips.COURSE_STUDENT_DELETE, "tooltip", onClick, isDisabled);
        
        ElementTag allRecordsButton = createButton("All Records", "btn btn-default btn-xs", null, 
                                                   getStudentRecordsLink(student), Const.Tooltips.COURSE_STUDENT_RECORDS, 
                                                   "tooltip", null, false);

        row.getActions().add(viewButton);
        row.getActions().add(editButton);
        if (!student.isRegistered()) {
            row.getActions().add(sendInviteButton);
        }
        row.getActions().add(deleteButton);
        row.getActions().add(allRecordsButton);
        
        isDisabled = !currentInstructor.isAllowedForPrivilege(student.section, 
                                            Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS);
        ElementTag addCommentButton = createButton("Add Comment", "btn btn-default btn-xs cursor-default", 
                                                   null, "javascript:;", Const.Tooltips.COURSE_STUDENT_COMMENT, 
                                                   "tooltip", null, isDisabled);
        
        isDisabled = !currentInstructor.isAllowedForPrivilege(student.section, 
                                            Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS);
        
        String content = "<span class=\"caret\"></span><span class=\"sr-only\">Add comments</span>";
        ElementTag addCommentsButton = createButton(content, "btn btn-default btn-xs dropdown-toggle", null, 
                                                    "javascript:;", null, "dropdown", null, isDisabled);
        row.getCommentActions().add(addCommentButton);
        row.getCommentActions().add(addCommentsButton);
        
        String buttonClass = "t_student_details_tostudent-c" + courseDetails.course.id + "." + studentIndex;
        String href = getCourseStudentDetailsLink(student) + "&" + Const.ParamsNames.SHOW_COMMENT_BOX + "=student";
        ElementTag toStudentCommentOption = createButton("To student: " + sanitizeForHtml(student.name), 
                                                         buttonClass, null, href, null, null, null, false);
        
        buttonClass = "t_student_details_toteam-c" + courseDetails.course.id + "." + studentIndex;
        href = getCourseStudentDetailsLink(student) + "&" + Const.ParamsNames.SHOW_COMMENT_BOX + "=team";
        ElementTag toTeamCommentOption = createButton("To team: " + sanitizeForHtml(student.team), 
                                                      buttonClass, null, href, null, null, null, false);
        
        row.getCommentRecipientOptions().add(toStudentCommentOption);
        row.getCommentRecipientOptions().add(toTeamCommentOption);
        
        if ((student.section != null) && (!student.section.equals("None"))) {
            buttonClass = "t_student_details_tosection-c" + courseDetails.course.id + "." + studentIndex;
            href = getCourseStudentDetailsLink(student) + "&" + Const.ParamsNames.SHOW_COMMENT_BOX + "=section";
            ElementTag toSectionCommentOption = createButton("To section: " + sanitizeForHtml(student.section), 
                                                             buttonClass, null, href, null, null, null, false);
            toSectionCommentOption.setAttribute("role", "menuitem");
            toSectionCommentOption.setAttribute("tabindex", "-1");
              
            row.getCommentRecipientOptions().add(toSectionCommentOption);
        }
        
        return row;
    }
    
    public InstructorAttributes getCurrentInstructor() {
        return currentInstructor;
    }
    
    public CourseDetailsBundle getCourseDetails() {
        return courseDetails;
    }
    
    public List<InstructorAttributes> getInstructors() {
        return instructors;
    }
    
    public CourseDetailsStudentsTable getStudentsTable() {
        return studentsTable;
    }
    
    public ElementTag getGiveCommentButton() {
        return giveCommentButton;
    }
    
    public ElementTag getCourseRemindButton() {
        return courseRemindButton;
    }
    
    private String getInstructorCourseRemindLink() {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_REMIND;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseDetails.course.id);
        link = addUserIdToUrl(link);
        return link;
    }
    
    
    private String getCourseStudentDetailsLink(StudentAttributes student) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseDetails.course.id);
        link = Url.addParamToUrl(link, Const.ParamsNames.STUDENT_EMAIL, student.email);
        link = addUserIdToUrl(link);
        return link;
    }
    
    
    private String getCourseStudentEditLink(StudentAttributes student) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseDetails.course.id);
        link = Url.addParamToUrl(link, Const.ParamsNames.STUDENT_EMAIL, student.email);
        link = addUserIdToUrl(link);
        return link;
    }
    
    
    private String getCourseStudentRemindLink(StudentAttributes student) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_REMIND;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseDetails.course.id);
        link = Url.addParamToUrl(link, Const.ParamsNames.STUDENT_EMAIL, student.email);
        link = addUserIdToUrl(link);
        return link;
    }
    
    
    private String getCourseStudentDeleteLink(StudentAttributes student) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DELETE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseDetails.course.id);
        link = Url.addParamToUrl(link, Const.ParamsNames.STUDENT_EMAIL, student.email);
        link = addUserIdToUrl(link);
        return link;
    }
    
    private String getStudentRecordsLink(StudentAttributes student) {
        String link = Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, student.course);
        link = Url.addParamToUrl(link, Const.ParamsNames.STUDENT_EMAIL, student.email);
        link = addUserIdToUrl(link);
        return link;
    }
    
    private ElementTag createButton(String content, String buttonClass, String id, String href, 
                            String title, String dataToggle, String onClick, boolean isDisabled){
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
            button.setAttribute("data-placement", "top");
        }
        
        if (dataToggle != null) {
            button.setAttribute("data-toggle", dataToggle);
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
