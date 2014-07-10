package teammates.ui.controller;

import javax.servlet.http.HttpServletRequest;

import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.HttpRequestHelper;
import teammates.logic.api.GateKeeper;
import teammates.logic.api.Logic;

public class UnregisteredStudentFeedbackSubmissionEditPageAction extends
        StudentFeedbackSubmissionEditPageAction {
    
    private StudentAttributes student;
    private String regkey;
    
    @Override
    @SuppressWarnings("unchecked")
    public void init(HttpServletRequest req){
        
        request = req;
        requestUrl = HttpRequestHelper.getRequestedURL(req);
        logic = new Logic();
        requestParameters = req.getParameterMap();
        session = req.getSession();
        isUnregistered = true;
        account = null;
        
        //---- set error status forwarded from the previous action
        
        isError = getRequestParamAsBoolean(Const.ParamsNames.ERROR);
        
        //---- set logged in user ------------------------------------------

        regkey = getRequestParamValue(Const.ParamsNames.REGKEY);
        String email = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSession = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        
        if(regkey == null || email == null || courseId == null || feedbackSession == null) {
            throw new UnauthorizedAccessException("Insufficient information to authenticate user");
        }
        
        student = logic.getStudentForRegistrationKey(regkey);
        if (student == null) {
            throw new UnauthorizedAccessException("Unknown Registration Key");
        } else if (!student.email.equals(email)
                || !student.course.equals(courseId)) {
            throw new UnauthorizedAccessException("Invalid email/course for given Registration Key");
        }
    }
    
    @Override
    protected boolean isInMasqueradeMode() {
        return false;
    }

    @Override
    public ActionResult executeAndPostProcess() throws EntityDoesNotExistException {
        
        //get the result from the child class.
        ActionResult response = execute();
        
        //set error flag of the result
        response.isError = isError;
        
        //Override the result if a redirect was requested by the action requester
        String redirectUrl = getRequestParamValue(Const.ParamsNames.NEXT_URL);
        if(redirectUrl != null && new FieldValidator().isLegitimateRedirectUrl(redirectUrl)) {
            RedirectResult rr = new RedirectResult(redirectUrl, response.account, requestParameters, response.statusToUser);
            rr.isError = response.isError;
            response = rr;
        }
        
        //Set the common parameters for the response
        response.responseParams.put(Const.ParamsNames.REGKEY, regkey);
        response.responseParams.put(Const.ParamsNames.ERROR, ""+response.isError);
        
        //Pass status message using session to prevent XSS attack
        if(!response.getStatusMessage().isEmpty()){
            putStatusMessageToSession(response);
        }
        
        return response;
    }

    @Override
    protected RedirectResult createSpecificRedirectResult() {
        return createRedirectResult(Const.ActionURIs.STUDENT_HOME_PAGE);
    }

    @Override
    protected ShowPageResult createSpecificShowPageResult() {
        data.regkey = regkey;
        data.email = student.email;
        return createShowPageResult(Const.ViewURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT, data);
    }
    
    @Override
    protected boolean isSpecificUserJoinedCourse() {
        return student.course.equals(courseId);
    }
    
    @Override
    protected void verifyAccesibleForSpecificUser() {
        new GateKeeper().verifyAccessible(
                student, 
                logic.getFeedbackSession(feedbackSessionName, courseId));
    }

    @Override
    protected String getUserEmailForCourse() {
        return student.email;
    }
}
