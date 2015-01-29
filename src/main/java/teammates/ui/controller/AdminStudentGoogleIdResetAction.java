package teammates.ui.controller;

import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;
import teammates.logic.api.Logic;

public class AdminStudentGoogleIdResetAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        
        Logic logic = new Logic();       
        new GateKeeper().verifyAdminPrivileges(account);
        
        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        String studentCourseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String wrongGoogleId = getRequestParamValue(Const.ParamsNames.STUDENT_ID);
        
        AdminStudentGoogleIdResetPageData data = new AdminStudentGoogleIdResetPageData(account);
        
        if(studentEmail != null && studentCourseId != null){
            try {
                logic.resetStudentGoogleId(studentEmail, studentCourseId);
                logic.sendRegistrationInviteToStudentAfterGoogleIdReset(studentCourseId, studentEmail);
            } catch (InvalidParametersException e) {
                statusToUser.add(Const.StatusMessages.STUDENT_GOOGLEID_RESET_FAIL);
                statusToAdmin = Const.StatusMessages.STUDENT_GOOGLEID_RESET_FAIL + "<br>" +
                                "Email: " + studentEmail + "<br>" +
                                "CourseId: " + studentCourseId + "<br>" + 
                                "Failed with error<br>" + 
                                e.getMessage();
                isError = true;
            }         
            
            StudentAttributes updatedStudent = logic.getStudentForEmail(studentCourseId, studentEmail);
     
            if(updatedStudent.googleId == null || updatedStudent.googleId.isEmpty()){
                
                statusToUser.add(Const.StatusMessages.STUDENT_GOOGLEID_RESET);
                statusToUser.add("Email : " + studentEmail);
                statusToUser.add("CourseId : " + studentCourseId);
                
                statusToAdmin = Const.StatusMessages.STUDENT_GOOGLEID_RESET + "<br>" +
                                "Email: " + studentEmail + "<br>" +
                                "CourseId: " + studentCourseId;
                
                data.statusForAjax = Const.StatusMessages.STUDENT_GOOGLEID_RESET + "<br>" + 
                                     "Email : " + studentEmail + "<br>" + 
                                     "CourseId : " + studentCourseId;
                
                data.isGoogleIdReset = true;
                deleteAccountIfNeeded(wrongGoogleId);
            } else {
                data.isGoogleIdReset = false;
                statusToUser.add(Const.StatusMessages.STUDENT_GOOGLEID_RESET_FAIL);
                statusToAdmin = Const.StatusMessages.STUDENT_GOOGLEID_RESET_FAIL + "<br>" +
                                "Email: " + studentEmail + "<br>" +
                                "CourseId: " + studentCourseId + "<br>";
                data.statusForAjax = Const.StatusMessages.STUDENT_GOOGLEID_RESET_FAIL + "<br>" + 
                                     "Email : " + studentEmail + "<br>" + 
                                     "CourseId : " + studentCourseId;
            } 
            
            isError = false;
            return createAjaxResult(Const.ViewURIs.ADMIN_SEARCH, data);
        }
        
        isError = true;
        return createAjaxResult(Const.ViewURIs.ADMIN_SEARCH, data);
    }
        
    
    private void deleteAccountIfNeeded(String wrongGoogleId){
        Logic logic = new Logic();
        
        if(logic.getStudentsForGoogleId(wrongGoogleId).isEmpty()
           && logic.getInstructorsForGoogleId(wrongGoogleId).isEmpty()){
            logic.deleteAccount(wrongGoogleId);
        }
        
        System.out.print("**************");
    }
}
