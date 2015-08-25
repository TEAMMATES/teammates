package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.SectionDetailsBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.ui.template.ElementTag;
import teammates.ui.template.StudentListSectionData;

/**
 * PageData: data used for the "Course Details" page
 */
public class InstructorCourseDetailsPageData extends PageData {
    private InstructorAttributes currentInstructor;
    private CourseDetailsBundle courseDetails;
    private List<InstructorAttributes> instructors;
    private String studentListHtmlTableAsString;
    private ElementTag giveCommentButton;
    private ElementTag courseRemindButton;
    private List<StudentListSectionData> sections;
    private boolean hasSection;
    
    public InstructorCourseDetailsPageData(AccountAttributes account) {
        super(account);
    }
    
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
                          + "window.location.href='" + sanitizeForJs(getInstructorCourseRemindLink(courseDetails.course.id)) + "';";
        courseRemindButton = createButton(null, "btn btn-primary", "button_remind", null, 
                                          Const.Tooltips.COURSE_REMIND, "tooltip", onClick, isDisabled);
        
        String photoUrl = Const.ActionURIs.STUDENT_PROFILE_PICTURE
                        + "?" + Const.ParamsNames.STUDENT_EMAIL
                        + "=%s&" + Const.ParamsNames.COURSE_ID
                        + "=%s&" + Const.ParamsNames.USER_ID + "=" + account.googleId;

        this.sections = new ArrayList<StudentListSectionData>();
        for (SectionDetailsBundle section: courseDetails.sections) {
            Map<String, String> emailPhotoUrlMapping = new HashMap<String, String>();
            for (TeamDetailsBundle teamDetails : section.teams) {
                for (StudentAttributes student : teamDetails.students) {
                    emailPhotoUrlMapping.put(student.email, String.format(photoUrl,
                                                                          StringHelper.encrypt(student.email),
                                                                          StringHelper.encrypt(student.course)));
                }
            }
            boolean isAllowedToViewStudentInSection = currentInstructor.isAllowedForPrivilege(section.name,
                                            Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS);
            boolean isAllowedToModifyStudent = currentInstructor.isAllowedForPrivilege(section.name,
                                            Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);
            boolean isAllowedToGiveCommentInSection = currentInstructor.isAllowedForPrivilege(section.name,
                                            Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS);
            this.sections.add(new StudentListSectionData(section, isAllowedToViewStudentInSection,
                                                         isAllowedToModifyStudent, isAllowedToGiveCommentInSection,
                                                         emailPhotoUrlMapping, account.googleId));
        }
        if (sections.size() == 1) {
            StudentListSectionData section = sections.get(0);
            this.hasSection = !section.getSectionName().equals("None");
        } else {
            this.hasSection = true;
        }
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
    
    public ElementTag getGiveCommentButton() {
        return giveCommentButton;
    }
    
    public ElementTag getCourseRemindButton() {
        return courseRemindButton;
    }
    
    public void setStudentListHtmlTableAsString(String studentListHtmlTableAsString) {
        this.studentListHtmlTableAsString = studentListHtmlTableAsString;
    }
    
    public String getStudentListHtmlTableAsString() {
        return studentListHtmlTableAsString;
    }

    public List<StudentListSectionData> getSections() {
        return sections;
    }

    public boolean isHasSection() {
        return hasSection;
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
