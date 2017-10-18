package teammates.ui.controller;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.exception.EmailSendingException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;
import teammates.logic.api.EmailGenerator;

public abstract class CourseJoinAuthenticatedAbstractAction extends Action {

    private static final Logger log = Logger.getLogger();

    protected void sendCourseRegisteredEmail(String name, String emailAddress, boolean isInstructor, String courseId) {
        CourseAttributes course = logic.getCourse(courseId);
        EmailWrapper email = new EmailGenerator().generateUserCourseRegisteredEmail(
                name, emailAddress, account.googleId, isInstructor, course);
        try {
            emailSender.sendEmail(email);
        } catch (EmailSendingException e) {
            log.severe("User course register email failed to send: " + TeammatesException.toStringWithStackTrace(e));
        }
    }

}
