package teammates.ui.controller;

import javax.servlet.http.HttpServletRequest;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.UserType;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;

public class StudentAuthenticateAction extends Action {
    
    String regkey;
    String nextUrl;
    StudentAttributes student;

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        if (student.googleId == null) {
            // unregistered user - join or proceed as unreg
            
        } else if (!student.googleId.equals(loggedInUser.googleId)) {
            // wrong user, logout and redirect
            
        } else {
            // correct user, redirect right away
        }
        return null;
    }
}
