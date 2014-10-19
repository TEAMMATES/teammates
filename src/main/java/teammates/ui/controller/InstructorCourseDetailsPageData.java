package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.Url;

/**
 * PageData: data used for the "Course Details" page
 */
public class InstructorCourseDetailsPageData extends PageData {
    
    public InstructorCourseDetailsPageData(AccountAttributes account) {
        super(account);
    }

    public InstructorAttributes currentInstructor;
    public CourseDetailsBundle courseDetails;
    public List<StudentAttributes> students;
    public List<InstructorAttributes> instructors;
    public String studentListHtmlTableAsString;
    
    
    public String getInstructorCourseRemindLink(){
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_REMIND;
        link = Url.addParamToUrl(link,Const.ParamsNames.COURSE_ID,courseDetails.course.id);
        link = addUserIdToUrl(link);
        return link;
    }
    
    
    public String getCourseStudentDetailsLink(StudentAttributes student){
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE;
        link = Url.addParamToUrl(link,Const.ParamsNames.COURSE_ID,courseDetails.course.id);
        link = Url.addParamToUrl(link,Const.ParamsNames.STUDENT_EMAIL,student.email);
        link = addUserIdToUrl(link);
        return link;
    }
    
    
    public String getCourseStudentEditLink(StudentAttributes student){
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT;
        link = Url.addParamToUrl(link,Const.ParamsNames.COURSE_ID,courseDetails.course.id);
        link = Url.addParamToUrl(link,Const.ParamsNames.STUDENT_EMAIL,student.email);
        link = addUserIdToUrl(link);
        return link;
    }
    
    
    public String getCourseStudentRemindLink(StudentAttributes student){
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_REMIND;
        link = Url.addParamToUrl(link,Const.ParamsNames.COURSE_ID,courseDetails.course.id);
        link = Url.addParamToUrl(link,Const.ParamsNames.STUDENT_EMAIL,student.email);
        link = addUserIdToUrl(link);
        return link;
    }
    
    
    public String getCourseStudentDeleteLink(StudentAttributes student){
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DELETE;
        link = Url.addParamToUrl(link,Const.ParamsNames.COURSE_ID,courseDetails.course.id);
        link = Url.addParamToUrl(link,Const.ParamsNames.STUDENT_EMAIL,student.email);
        link = addUserIdToUrl(link);
        return link;
    }
    
    public String getStudentRecordsLink(StudentAttributes student) {
        String link = Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, student.course);
        link = Url.addParamToUrl(link, Const.ParamsNames.STUDENT_EMAIL,
                student.email);
        link = addUserIdToUrl(link);
        return link;
    }
    
}
