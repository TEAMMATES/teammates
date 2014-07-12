package teammates.ui.controller;

import java.util.HashMap;
import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.Url;

public class InstructorStudentListPageData extends PageData {
    
    public InstructorStudentListPageData(AccountAttributes account) {
        super(account);
    }
    
    public HashMap<String, InstructorAttributes> instructors;
    public HashMap<String, String> numStudents;
    public List<CourseAttributes> courses;
    public String searchKey;
    public Boolean displayArchive;
    
    public String getCourseStudentDetailsLink(String courseId, StudentAttributes student){
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE;
        link = Url.addParamToUrl(link,Const.ParamsNames.COURSE_ID,courseId);
        link = Url.addParamToUrl(link,Const.ParamsNames.STUDENT_EMAIL,student.email);
        link = addUserIdToUrl(link);
        return link;
    }
    
    public String getCourseStudentEditLink(String courseId, StudentAttributes student){
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT;
        link = Url.addParamToUrl(link,Const.ParamsNames.COURSE_ID,courseId);
        link = Url.addParamToUrl(link,Const.ParamsNames.STUDENT_EMAIL,student.email);
        link = addUserIdToUrl(link);
        return link;
    }
    
    //TODO: create another delete action which redirects to studentListPage?
    public String getCourseStudentDeleteLink(String courseId, StudentAttributes student){
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DELETE;
        link = Url.addParamToUrl(link,Const.ParamsNames.COURSE_ID,courseId);
        link = Url.addParamToUrl(link,Const.ParamsNames.STUDENT_EMAIL,student.email);
        link = addUserIdToUrl(link);
        return link;
    }
    
    public String getStudentRecordsLink(String courseId, StudentAttributes student){
        String link = Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE;
        link = Url.addParamToUrl(link,Const.ParamsNames.COURSE_ID,courseId);
        link = Url.addParamToUrl(link,Const.ParamsNames.STUDENT_EMAIL,student.email);
        link = addUserIdToUrl(link);
        return link;
    }

}
