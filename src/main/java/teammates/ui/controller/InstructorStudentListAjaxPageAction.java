package teammates.ui.controller;

import java.util.HashMap;
import java.util.Map;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.SectionDetailsBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.logic.api.GateKeeper;

public class InstructorStudentListAjaxPageAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull("null course id", courseId);
        
        new GateKeeper().verifyInstructorPrivileges(account);
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        CourseAttributes course = logic.getCourse(courseId);
        
        new GateKeeper().verifyAccessible(instructor, course);
        
        InstructorStudentListAjaxPageData data = new InstructorStudentListAjaxPageData(account);
        data.courseSectionDetails = logic.getSectionsForCourse(courseId);
        data.course = course;
        data.hasSection = logic.hasIndicatedSections(courseId);
        
        String photoUrl = Const.ActionURIs.STUDENT_PROFILE_PICTURE + "?" + 
                Const.ParamsNames.STUDENT_EMAIL+"=%s&" + 
                Const.ParamsNames.COURSE_ID + "=%s&" +
                Const.ParamsNames.USER_ID + "=" + account.googleId;
        
        data.emailPhotoUrlMapping = new HashMap<String, String>();
        data.sectionPrivileges = new HashMap<>();
        for(SectionDetailsBundle sectionDetails : data.courseSectionDetails){
            for(TeamDetailsBundle teamDetails: sectionDetails.teams){
                for(StudentAttributes student: teamDetails.students){
                    data.emailPhotoUrlMapping.put(student.email, String.format(photoUrl, StringHelper.encrypt(student.email),  StringHelper.encrypt(student.course)));
                }
            }
            Map<String, Boolean> sectionPrivilege = new HashMap<String, Boolean>();
            sectionPrivilege
                    .put(Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS,
                            instructor.isAllowedForPrivilege(
                                            sectionDetails.name,
                                            Const.ParamsNames.INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS));
            sectionPrivilege
                    .put(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT,
                            instructor.isAllowedForPrivilege(
                                            sectionDetails.name,
                                            Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT));
            sectionPrivilege
                    .put(Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS,
                            instructor.isAllowedForPrivilege(
                                            sectionDetails.name,
                                            Const.ParamsNames.INSTRUCTOR_PERMISSION_GIVE_COMMENT_IN_SECTIONS));
            data.sectionPrivileges.put(sectionDetails.name, sectionPrivilege);
        }
        
       
        
        return createAjaxResult(Const.ViewURIs.INSTRUCTOR_STUDENT_LIST, data);
    }

}
