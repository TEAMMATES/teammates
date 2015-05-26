package teammates.ui.controller;

import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;

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
