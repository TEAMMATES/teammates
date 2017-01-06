package teammates.ui.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EmailSendingException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.common.util.StringHelper;
import teammates.common.util.Templates;
import teammates.common.util.ThreadHelper;
import teammates.common.util.Url;
import teammates.logic.api.EmailGenerator;
import teammates.logic.api.GateKeeper;
import teammates.logic.backdoor.BackDoorLogic;

public class AdminInstructorAccountAddAction extends Action {
    
    @Override
    protected ActionResult execute() {

        new GateKeeper().verifyAdminPrivileges(account);

        AdminHomePageData data = new AdminHomePageData(account);

        data.instructorDetailsSingleLine = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_DETAILS_SINGLE_LINE);
        data.instructorShortName = "";
        data.instructorName = "";
        data.instructorEmail = "";
        data.instructorInstitution = "";
        data.instructorAddingResultForAjax = true;
        data.statusForAjax = "";
        
        // If there is input from the instructorDetailsSingleLine form,
        // that data will be prioritized over the data from the 3-parameter form
        if (data.instructorDetailsSingleLine == null) {
            data.instructorShortName = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_SHORT_NAME);
            Assumption.assertNotNull(data.instructorShortName);
            data.instructorName = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_NAME);
            Assumption.assertNotNull(data.instructorName);
            data.instructorEmail = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
            Assumption.assertNotNull(data.instructorEmail);
            data.instructorInstitution = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_INSTITUTION);
            Assumption.assertNotNull(data.instructorInstitution);
        } else {
            try {
                String[] instructorInfo = extractInstructorInfo(data.instructorDetailsSingleLine);
                
                data.instructorShortName = instructorInfo[0];
                data.instructorName = instructorInfo[0];
                data.instructorEmail = instructorInfo[1];
                data.instructorInstitution = instructorInfo[2];
            } catch (InvalidParametersException e) {
                data.statusForAjax = e.getMessage().replace(Const.EOL, Const.HTML_BR_TAG);
                data.instructorAddingResultForAjax = false;
                statusToUser.add(new StatusMessage(data.statusForAjax, StatusMessageColor.DANGER));
                return createAjaxResult(data);
            }
        }
        
        data.instructorShortName = data.instructorShortName.trim();
        data.instructorName = data.instructorName.trim();
        data.instructorEmail = data.instructorEmail.trim();
        data.instructorInstitution = data.instructorInstitution.trim();
        
        try {
            logic.verifyInputForAdminHomePage(data.instructorShortName, data.instructorName,
                                              data.instructorInstitution, data.instructorEmail);
        } catch (InvalidParametersException e) {
            data.statusForAjax = e.getMessage().replace(Const.EOL, Const.HTML_BR_TAG);
            data.instructorAddingResultForAjax = false;
            statusToUser.add(new StatusMessage(data.statusForAjax, StatusMessageColor.DANGER));
            return createAjaxResult(data);
        }
            
        String courseId = null;
       
        try {
            courseId = importDemoData(data);
        } catch (Exception e) {
            
            String retryUrl = Const.ActionURIs.ADMIN_INSTRUCTORACCOUNT_ADD;
            retryUrl = Url.addParamToUrl(retryUrl, Const.ParamsNames.INSTRUCTOR_SHORT_NAME, data.instructorShortName);
            retryUrl = Url.addParamToUrl(retryUrl, Const.ParamsNames.INSTRUCTOR_NAME, data.instructorName);
            retryUrl = Url.addParamToUrl(retryUrl, Const.ParamsNames.INSTRUCTOR_EMAIL, data.instructorEmail);
            retryUrl = Url.addParamToUrl(retryUrl, Const.ParamsNames.INSTRUCTOR_INSTITUTION, data.instructorInstitution);
                       
            StringBuilder errorMessage = new StringBuilder(100);
            String retryLink = "<a href=" + retryUrl + ">Exception in Importing Data, Retry</a>";
            errorMessage.append(retryLink);
            
            statusToUser.add(new StatusMessage(errorMessage.toString(), StatusMessageColor.DANGER));
            
            String message = "<span class=\"text-danger\">Servlet Action failure in AdminInstructorAccountAddAction" + "<br>"
                             + e.getClass() + ": " + TeammatesException.toStringWithStackTrace(e) + "<br></span>";
            
            errorMessage.append("<br>").append(message);
            statusToUser.add(new StatusMessage("<br>" + message, StatusMessageColor.DANGER));
            statusToAdmin = message;
            
            data.instructorAddingResultForAjax = false;
            data.statusForAjax = errorMessage.toString();
            return createAjaxResult(data);
        }
        
        List<InstructorAttributes> instructorList = logic.getInstructorsForCourse(courseId);
        String joinLink = Config.getAppUrl(Const.ActionURIs.INSTRUCTOR_COURSE_JOIN)
                                .withRegistrationKey(StringHelper.encrypt(instructorList.get(0).key))
                                .withInstructorInstitution(data.instructorInstitution)
                                .toAbsoluteString();
        EmailWrapper email = new EmailGenerator().generateNewInstructorAccountJoinEmail(
                instructorList.get(0).email, data.instructorShortName, joinLink);
        try {
            emailSender.sendEmail(email);
        } catch (EmailSendingException e) {
            log.severe("Instructor welcome email failed to send: " + TeammatesException.toStringWithStackTrace(e));
        }
        data.statusForAjax = "Instructor " + data.instructorName
                             + " has been successfully created with join link:<br>" + joinLink;
        statusToUser.add(new StatusMessage(data.statusForAjax, StatusMessageColor.SUCCESS));
        statusToAdmin = "A New Instructor <span class=\"bold\">"
                + data.instructorName + "</span> has been created.<br>"
                + "<span class=\"bold\">Id: </span>"
                + "ID will be assigned when the verification link was clicked and confirmed"
                + "<br>"
                + "<span class=\"bold\">Email: </span>" + data.instructorEmail
                + "<span class=\"bold\">Institution: </span>"
                + data.instructorInstitution;
 
        
        return createAjaxResult(data);
    }

    /**
     * Extracts instructor's info from a string then store them in an array of string.
     * @param instructorDetails This string is in the format INSTRUCTOR_NAME | INSTRUCTOR_EMAIL | INSTRUCTOR_INSTITUTION
     * or INSTRUCTOR_NAME \t INSTRUCTOR_EMAIL \t INSTRUCTOR_INSTITUTION
     * @return A String array of size 3
     * @throws InvalidParametersException
     */
    private String[] extractInstructorInfo(String instructorDetails) throws InvalidParametersException {
        String[] result = instructorDetails.trim().replace('|', '\t').split("\t");
        if (result.length != Const.LENGTH_FOR_NAME_EMAIL_INSTITUTION) {
            throw new InvalidParametersException(String.format(Const.StatusMessages.INSTRUCTOR_DETAILS_LENGTH_INVALID,
                                                               Const.LENGTH_FOR_NAME_EMAIL_INSTITUTION));
        }
        return result;
    }

    /**
     * Imports Demo course to new instructor.
     * @param pageData data from AdminHomePageData
     * @return the ID of Demo course
     * @throws InvalidParametersException
     * @throws EntityDoesNotExistException
     */
    private String importDemoData(AdminHomePageData pageData)
            throws InvalidParametersException, EntityDoesNotExistException {

        String courseId = generateDemoCourseId(pageData.instructorEmail);
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        c.set(Calendar.AM_PM, Calendar.PM);
        c.set(Calendar.HOUR, 11);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.YEAR, c.get(Calendar.YEAR) + 1);
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm a Z");

        String jsonString = Templates.populateTemplate(Templates.INSTRUCTOR_SAMPLE_DATA,
                // replace email
                "teammates.demo.instructor@demo.course", pageData.instructorEmail,
                // replace name
                "Demo_Instructor", pageData.instructorName,
                // replace course
                "demo.course", courseId,
                // update feedback session time
                "2013-04-01 11:59 PM UTC", formatter.format(c.getTime()));

        DataBundle data = JsonUtils.fromJson(jsonString, DataBundle.class);
        
        BackDoorLogic backDoorLogic = new BackDoorLogic();
        
        try {
            backDoorLogic.persistDataBundle(data);
        } catch (EntityDoesNotExistException e) {
            ThreadHelper.waitFor(Config.PERSISTENCE_CHECK_DURATION);
            backDoorLogic.persistDataBundle(data);
            log.warning("Data Persistence was Checked Twice in This Request");
        }
        
        //produce searchable documents
        List<CommentAttributes> comments = logic.getCommentsForGiver(courseId, pageData.instructorEmail);
        List<FeedbackResponseCommentAttributes> frComments =
                logic.getFeedbackResponseCommentForGiver(courseId, pageData.instructorEmail);
        List<StudentAttributes> students = logic.getStudentsForCourse(courseId);
        List<InstructorAttributes> instructors = logic.getInstructorsForCourse(courseId);
        
        for (CommentAttributes comment : comments) {
            logic.putDocument(comment);
        }
        for (FeedbackResponseCommentAttributes comment : frComments) {
            logic.putDocument(comment);
        }
        for (StudentAttributes student : students) {
            logic.putDocument(student);
        }
        for (InstructorAttributes instructor : instructors) {
            logic.putDocument(instructor);
        }
        
        return courseId;
    }

    /**
    * Strategy to Generate New Demo Course Id:
    *     a.  keep the part of email before "@"
    *         replace "@" with "."
    *         replace email host with their first 3 chars. eg, gmail.com -> gma
    *         append "-demo"
    *       to sum up: lebron@gmail.com -> lebron.gma-demo
    * 
    *   b.  if the generated courseId already exists, create another one by appending a integer to the previous courseId.
    *       if the newly generate id still exists, increment the id, until we find a feasible one
    *       eg.
    *       lebron@gmail.com -> lebron.gma-demo  // already exists!
    *       lebron@gmail.com -> lebron.gma-demo0 // already exists!
    *       lebron@gmail.com -> lebron.gma-demo1 // already exists!
    *       ...
    *       lebron@gmail.com -> lebron.gma-demo99 // already exists!
    *       lebron@gmail.com -> lebron.gma-demo100 // found! a feasible id
    * 
    *   c.  in any cases(a or b), if generated Id is longer than FieldValidator.COURSE_ID_MAX_LENGTH, shorten the part
    *       before "@" of the intial input email, by continuously remove its last character
    * 
    *    @see #generateDemoCourseId(String)
    *    @see #generateNextDemoCourseId(String, int)
    */
    
    /** 
    * Generate a course ID for demo course, and if the generated id already exists, try another one
    * 
    * @param instructorEmail is the instructor email.
    * @return generated course id
    */
    private String generateDemoCourseId(String instructorEmail) {
        String proposedCourseId = generateNextDemoCourseId(instructorEmail, FieldValidator.COURSE_ID_MAX_LENGTH);
        while (logic.getCourse(proposedCourseId) != null) {
            proposedCourseId = generateNextDemoCourseId(proposedCourseId, FieldValidator.COURSE_ID_MAX_LENGTH);
        }
        return proposedCourseId;
    }
    
    /** 
    * Generate a course ID for demo course from a given email
    * 
    * @param instructorEmail is the instructor email.
    * @return the first proposed course id. eg.lebron@gmail.com -> lebron.gma-demo
    */
    private String getDemoCourseIdRoot(String instructorEmail) {
        final String[] splitedEmail = instructorEmail.split("@");
        final String head = splitedEmail[0];
        final String emailAbbreviation = splitedEmail[1].substring(0, 3);
        return head + "." + emailAbbreviation
                + "-demo";
    }
    
    /** 
    * Generate a course ID for demo course from a given email or a generated course Id
    * here we check the input string is a email or course Id and handle them accordingly
    * check the resulting course id, and if bigger than maximumIdLength, cut it so that it equals maximumIdLength
    * 
    * @param instructorEmailOrProposedCourseId is the instructor email or a proposed course id that already exists.
    * @param maximumIdLength is the maximum resulting id length allowed, above which we will cut the part before "@"
    * @return the proposed course id.
    *     eg.
    *         lebron@gmail.com -> lebron.gma-demo
    *         lebron.gma-demo -> lebron.gma-demo0
    *         lebron.gma-demo0 -> lebron.gma-demo1
    *         012345678901234567890123456789.gma-demo9 -> 01234567890123456789012345678.gma-demo10 (being cut)
    */
    private String generateNextDemoCourseId(String instructorEmailOrProposedCourseId, int maximumIdLength) {
        final boolean isFirstCourseId = instructorEmailOrProposedCourseId.contains("@");
        if (isFirstCourseId) {
            return StringHelper.truncateHead(getDemoCourseIdRoot(instructorEmailOrProposedCourseId),
                                             maximumIdLength);
        }
        
        final boolean isFirstTimeDuplicate = instructorEmailOrProposedCourseId.endsWith("-demo");
        if (isFirstTimeDuplicate) {
            return StringHelper.truncateHead(instructorEmailOrProposedCourseId + "0",
                                             maximumIdLength);
        }
        
        final int lastIndexOfDemo = instructorEmailOrProposedCourseId.lastIndexOf("-demo");
        final String root = instructorEmailOrProposedCourseId.substring(0, lastIndexOfDemo);
        final int previousDedupSuffix = Integer.parseInt(instructorEmailOrProposedCourseId.substring(lastIndexOfDemo + 5));

        return StringHelper.truncateHead(root + "-demo" + (previousDedupSuffix + 1), maximumIdLength);
    }
}
