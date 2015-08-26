package teammates.ui.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.Utils;
import teammates.common.util.Const.StatusMessageColor;
import teammates.logic.api.GateKeeper;

/**
 * Action: remind instructor or student to register for a course by sending reminder emails
 */
public class InstructorCourseRemindAction extends Action {
    protected static final Logger log = Utils.getLogger();
    
    @Override
    public ActionResult execute() throws EntityDoesNotExistException{
        
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        
        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        String instructorEmail = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
        
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        CourseAttributes course = logic.getCourse(courseId);
        if (studentEmail != null) {
            new GateKeeper().verifyAccessible(
                    instructor, course, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);
        } else if (instructorEmail != null) {
            new GateKeeper().verifyAccessible(
                    instructor, course, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR);
        } else {
            // this is sending registration emails to all students in the course and we will check if the instructor
            // canmodifystudent for course level since for modifystudent privilege there is only course level setting for now
            new GateKeeper().verifyAccessible(
                    instructor, course, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_STUDENT);
        }
        
        /* Process sending emails and setup status to be shown to user and admin */
        List<MimeMessage> emailsSent = new ArrayList<MimeMessage>();
        String redirectUrl = "";
        try {
            if (studentEmail != null) {
                MimeMessage emailSent = logic.sendRegistrationInviteToStudent(courseId, studentEmail);
                emailsSent.add(emailSent);
                
                statusToUser.add(new StatusMessage(Const.StatusMessages.COURSE_REMINDER_SENT_TO+studentEmail, StatusMessageColor.SUCCESS));
                redirectUrl = Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE;
            } else if (instructorEmail != null) {
                MimeMessage emailSent = logic.sendRegistrationInviteToInstructor(courseId, instructorEmail);
                emailsSent.add(emailSent);
                
                statusToUser.add(new StatusMessage(Const.StatusMessages.COURSE_REMINDER_SENT_TO + instructorEmail, StatusMessageColor.SUCCESS));
                redirectUrl = Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE;
            } else {
                emailsSent = logic.sendRegistrationInviteForCourse(courseId);
                
                statusToUser.add(new StatusMessage(Const.StatusMessages.COURSE_REMINDERS_SENT, StatusMessageColor.SUCCESS));
                redirectUrl = Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE;
            }
            
            statusToAdmin = generateStatusToAdmin(emailsSent, courseId);
        } catch (InvalidParametersException e) {
            Assumption.fail("InvalidParametersException not expected at this point");
        }
        
        /* Create redirection with URL based on type of sending email */
        RedirectResult response = createRedirectResult(redirectUrl);
        response.addResponseParam(Const.ParamsNames.COURSE_ID, courseId);
        
        return response;

    }
    
    private String generateStatusToAdmin(List<MimeMessage> emailsSent, String courseId) {
        String statusToAdmin = "Registration Key sent to the following users "
                + "in Course <span class=\"bold\">[" + courseId + "]</span>:<br/>";
        
        Iterator<Entry<String, JoinEmailData>> extractedEmailIterator = 
                extractEmailDataForLogging(emailsSent).entrySet().iterator();
        
        while (extractedEmailIterator.hasNext()) {
            Entry<String, JoinEmailData> extractedEmail = extractedEmailIterator.next();
            
            String userEmail = extractedEmail.getKey();
            JoinEmailData joinEmailData = extractedEmail.getValue();
            
            statusToAdmin += joinEmailData.userName + "<span class=\"bold\"> (" + userEmail + ")"
                    + "</span>.<br/>" + joinEmailData.regKey + "<br/>";
        }
        
        return statusToAdmin;
    }

    private Map<String, JoinEmailData> extractEmailDataForLogging(List<MimeMessage> emails) {
        Map<String, JoinEmailData> logData = new TreeMap<String, JoinEmailData>();
        
        for (MimeMessage email : emails) {
            try {
                String recipient = email.getAllRecipients()[0].toString();
                String userName = extractUserName((String) email.getContent());
                String regKey = extractRegistrationKey((String) email.getContent());
                logData.put(recipient, new JoinEmailData(userName, regKey));
            } catch (MessagingException e) {
                Assumption.fail("Join email corrupted");
            } catch (IOException e) {
                Assumption.fail("Join email corrupted");
            }
        }
        
        return logData;
    }
    
    private String extractUserName(String emailContent) {
        int startIndex = emailContent.indexOf("Hello ") + "Hello ".length();
        int endIndex = emailContent.indexOf(",");
        return emailContent.substring(startIndex, endIndex);
    }
    
    private String extractRegistrationKey(String emailContent) {
        int startIndex = emailContent.indexOf("key=") + "key=".length();
        int endIndex = emailContent.indexOf("\">http://");
        if (endIndex < 0) {
            endIndex = emailContent.indexOf("\">https://");
        }
        return emailContent.substring(startIndex, endIndex);
    }
    
    private class JoinEmailData {
        String userName;
        String regKey;
        
        public JoinEmailData(String userName, String regKey) {
            this.userName = userName;
            this.regKey = regKey;
        }
    }
}
