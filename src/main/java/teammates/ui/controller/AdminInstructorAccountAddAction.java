package teammates.ui.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EmailSendingException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.common.util.FieldValidator;
import teammates.common.util.JsonUtils;
import teammates.common.util.Logger;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.common.util.StringHelper;
import teammates.common.util.Templates;
import teammates.common.util.Url;
import teammates.logic.api.EmailGenerator;
import teammates.logic.backdoor.BackDoorLogic;
import teammates.ui.pagedata.AdminHomePageData;

public class AdminInstructorAccountAddAction extends Action {

    private static final Logger log = Logger.getLogger();

    @Override
    protected ActionResult execute() {

        gateKeeper.verifyAdminPrivileges(account);

        AdminHomePageData data = new AdminHomePageData(account, sessionToken);

        data.instructorShortName = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_SHORT_NAME).trim();
        data.instructorName = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_NAME).trim();
        data.instructorEmail = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL).trim();
        data.instructorInstitution = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_INSTITUTION).trim();
        data.isInstructorAddingResultForAjax = true;
        data.statusForAjax = "";

        data.instructorShortName = data.instructorShortName.trim();
        data.instructorName = data.instructorName.trim();
        data.instructorEmail = data.instructorEmail.trim();
        data.instructorInstitution = data.instructorInstitution.trim();

        try {
            logic.verifyInputForAdminHomePage(data.instructorShortName, data.instructorName,
                                              data.instructorInstitution, data.instructorEmail);
        } catch (InvalidParametersException e) {
            data.statusForAjax = e.getMessage().replace(Const.EOL, Const.HTML_BR_TAG);
            data.isInstructorAddingResultForAjax = false;
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
            retryUrl = Url.addParamToUrl(retryUrl, Const.ParamsNames.SESSION_TOKEN, data.getSessionToken());

            StringBuilder errorMessage = new StringBuilder(100);
            String retryLink = "<a href=" + retryUrl + ">Exception in Importing Data, Retry</a>";
            errorMessage.append(retryLink);

            statusToUser.add(new StatusMessage(errorMessage.toString(), StatusMessageColor.DANGER));

            String message = "<span class=\"text-danger\">Servlet Action failure in AdminInstructorAccountAddAction" + "<br>"
                             + e.getClass() + ": " + TeammatesException.toStringWithStackTrace(e) + "<br></span>";

            errorMessage.append("<br>").append(message);
            statusToUser.add(new StatusMessage("<br>" + message, StatusMessageColor.DANGER));
            statusToAdmin = message;

            data.isInstructorAddingResultForAjax = false;
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
        data.statusForAjax = "Instructor " + SanitizationHelper.sanitizeForHtml(data.instructorName)
                             + " has been successfully created " + "<a href=" + joinLink + ">" + Const.JOIN_LINK + "</a>";
        statusToUser.add(new StatusMessage(data.statusForAjax, StatusMessageColor.SUCCESS));
        statusToAdmin = "A New Instructor <span class=\"bold\">"
                + SanitizationHelper.sanitizeForHtmlTag(data.instructorName) + "</span> has been created.<br>"
                + "<span class=\"bold\">Id: </span>"
                + "ID will be assigned when the verification link was clicked and confirmed"
                + "<br>"
                + "<span class=\"bold\">Email: </span>" + SanitizationHelper.sanitizeForHtmlTag(data.instructorEmail)
                + "<span class=\"bold\">Institution: </span>"
                + SanitizationHelper.sanitizeForHtmlTag(data.instructorInstitution);

        return createAjaxResult(data);
    }

    /**
     * Imports Demo course to new instructor.
     * @param pageData data from AdminHomePageData
     * @return the ID of Demo course
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
        backDoorLogic.persistDataBundle(data);

        List<FeedbackResponseCommentAttributes> frComments =
                logic.getFeedbackResponseCommentForGiver(courseId, pageData.instructorEmail);
        List<StudentAttributes> students = logic.getStudentsForCourse(courseId);
        List<InstructorAttributes> instructors = logic.getInstructorsForCourse(courseId);

        logic.putFeedbackResponseCommentDocuments(frComments);
        logic.putStudentDocuments(students);
        logic.putInstructorDocuments(instructors);

        return courseId;
    }

    // Strategy to Generate New Demo Course Id:
    // a. keep the part of email before "@"
    //    replace "@" with "."
    //    replace email host with their first 3 chars. eg, gmail.com -> gma
    //    append "-demo"
    //    to sum up: lebron@gmail.com -> lebron.gma-demo
    //
    // b. if the generated courseId already exists, create another one by appending a integer to the previous courseId.
    //    if the newly generate id still exists, increment the id, until we find a feasible one
    //    eg.
    //    lebron@gmail.com -> lebron.gma-demo  // already exists!
    //    lebron@gmail.com -> lebron.gma-demo0 // already exists!
    //    lebron@gmail.com -> lebron.gma-demo1 // already exists!
    //    ...
    //    lebron@gmail.com -> lebron.gma-demo99 // already exists!
    //    lebron@gmail.com -> lebron.gma-demo100 // found! a feasible id
    //
    // c. in any cases(a or b), if generated Id is longer than FieldValidator.COURSE_ID_MAX_LENGTH, shorten the part
    //    before "@" of the intial input email, by continuously remove its last character

    /**
     * Generate a course ID for demo course, and if the generated id already exists, try another one.
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
     * Generate a course ID for demo course from a given email.
     *
     * @param instructorEmail is the instructor email.
     * @return the first proposed course id. eg.lebron@gmail.com -> lebron.gma-demo
     */
    private String getDemoCourseIdRoot(String instructorEmail) {
        String[] emailSplit = instructorEmail.split("@");

        String username = emailSplit[0];
        String host = emailSplit[1];

        String head = StringHelper.replaceIllegalChars(username, FieldValidator.REGEX_COURSE_ID, '_');
        String hostAbbreviation = host.substring(0, 3);

        return head + "." + hostAbbreviation + "-demo";
    }

    /**
     * Generate a course ID for demo course from a given email or a generated course Id.
     *
     * <p>Here we check the input string is an email or course Id and handle them accordingly;
     * check the resulting course id, and if bigger than maximumIdLength, cut it so that it equals maximumIdLength.
     *
     * @param instructorEmailOrProposedCourseId is the instructor email or a proposed course id that already exists.
     * @param maximumIdLength is the maximum resulting id length allowed, above which we will cut the part before "@"
     * @return the proposed course id, e.g.:
     *         <ul>
     *         <li>lebron@gmail.com -> lebron.gma-demo</li>
     *         <li>lebron.gma-demo -> lebron.gma-demo0</li>
     *         <li>lebron.gma-demo0 -> lebron.gma-demo1</li>
     *         <li>012345678901234567890123456789.gma-demo9 -> 01234567890123456789012345678.gma-demo10 (being cut)</li>
     *         </ul>
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
