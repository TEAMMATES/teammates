package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.JoinCourseException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;

/**
 * This action handles students who attempt to join a course after
 * the student has been forced to re-authenticate himself by 
 * {@link StudentCourseJoinAction}. This action does the actual
 * joining of the student to the course.
 */
public class StudentCourseJoinAuthenticatedAction extends Action {
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        Assumption.assertNotNull(regkey);
        String nextUrl = getRequestParamValue(Const.ParamsNames.NEXT_URL);
        Assumption.assertNotNull(nextUrl);
        
        ensureStudentExists();
        
        try {
            logic.joinCourseForStudent(regkey, account.googleId);
        } catch (JoinCourseException e) {
            // Does not sanitize for html to allow insertion of mailto link
            if (e.errorCode == Const.StatusCodes.INVALID_KEY) {
                setStatusForException(e, String.format(e.getMessage(), requestUrl));
            } else {
                setStatusForException(e, e.getMessage());
            }
            nextUrl = Const.ActionURIs.STUDENT_HOME_PAGE;
            excludeStudentDetailsFromResponseParams();
            
            return createRedirectResult(nextUrl);
        }
        
        final String studentInfo = "Action Student Joins Course"
                + "<br/>Google ID: " + account.googleId
                + "<br/>Key : " + regkey; 
        RedirectResult response = createRedirectResult(nextUrl);
        response.addResponseParam(Const.ParamsNames.CHECK_PERSISTENCE_COURSE, getStudent().course);
        excludeStudentDetailsFromResponseParams();
        
        if(statusToAdmin != null && !statusToAdmin.trim().isEmpty()) {
            statusToAdmin += "<br/><br/>" + studentInfo;
        } else {
            statusToAdmin = studentInfo;
        }
        
        addStatusMessageToUser();
        
        return response;
    }

    private void addStatusMessageToUser() throws EntityDoesNotExistException {
        CourseAttributes course = logic.getCourse(getStudent().course);
        String courseDisplayText = "[" + course.id + "] " + course.name; 
        
        statusToUser.add(String.format(
                Const.StatusMessages.STUDENT_COURSE_JOIN_SUCCESSFUL, courseDisplayText));

       
        if (logic.getFeedbackSessionsForCourse(getStudent().course).isEmpty()) {
            statusToUser.add("Currently, there are no open evaluation/feedback sessions in the course " + courseDisplayText + ". When a session is open for submission you will be notified.");
            
            StudentProfileAttributes spa = logic.getStudentProfile(account.googleId);
            
            if (isProfileDefault(spa)) {
                statusToUser.add("Meanwhile, you can update your profile <a href=\"" + Const.ActionURIs.STUDENT_PROFILE_PAGE + "\"> here</a>. ");
            }
        }
        
    }

    private void ensureStudentExists() {
        StudentAttributes student = getStudent();
        if (student == null) {
            log.info("Student object not found for regkey: " + regkey);
            throw new UnauthorizedAccessException("No student with given registration key:" + regkey);
        }
    }

    private StudentAttributes getStudent() {
        if (student == null) {
            student = logic.getStudentForRegistrationKey(regkey);
        }
        
        return student;
    }
    
    /**
     * Return whether the profile is a default unmodified profile. 
     * A profile is unmodified if:
     *     email, moreInfo, nationality, picture, shortname is empty
     *     and the profile's institute is the same as the account's institute
     * @param spa
     * @return
     */
    private boolean isProfileDefault(StudentProfileAttributes spa) {
        if (!spa.email.isEmpty()) {
            return false;
        }
        if (!spa.gender.equals("other")) {
            return false;
        }
        if (!spa.moreInfo.isEmpty()) {
            return false;
        }
        if (!spa.nationality.isEmpty()) {
            return false;
        }
        if (!spa.shortName.isEmpty()) {
            return false;
        }
        
        if (!spa.pictureKey.isEmpty()) {
            return false;
        }
        
        boolean isDummyAccount = account.institute == null;
        String institute;
        if (isDummyAccount) {
            // if the account is newly created, the account attribute in the action
            // will still only be the dummy account and not the newly created account
            institute = logic.getAccount(account.googleId).institute;
        } else {
            institute = account.institute;
        }
        
        if (!spa.institute.equals(institute)) {
            return false;
        }
        
        return true;
    }
    
}
