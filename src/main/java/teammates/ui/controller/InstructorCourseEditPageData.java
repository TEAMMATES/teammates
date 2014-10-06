package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.common.util.Url;


public class InstructorCourseEditPageData extends PageData {

    public InstructorCourseEditPageData(AccountAttributes account) {
        super(account);
        index = -1;
    }
    
    public CourseAttributes course;
    public List<InstructorAttributes> instructorList;
    public InstructorAttributes currentInstructor;
    public List<String> sectionNames;
    public List<String> evalNames;
    public List<String> feedbackNames;
    public int index;
    public boolean isAccessControlDisplayed;

    public String getInstructorCourseInstructorEditLink(String courseId, String instructorId) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_INSTRUCTOR_EDIT_SAVE;
        link = Url.addParamToUrl(link,Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link,Const.ParamsNames.INSTRUCTOR_ID, instructorId);
        link = addUserIdToUrl(link);
        return link;
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
        link = Url.addParamToUrl(link,Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link,Const.ParamsNames.INSTRUCTOR_EMAIL, instructorEmail);
        link = addUserIdToUrl(link);
        return link;
    }
}

