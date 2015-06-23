package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentParticipantType;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentResultSummary;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;
import teammates.common.util.Url;
import teammates.logic.api.Logic;
import teammates.ui.template.ElementTag;

/**
 * Data and utility methods needed to render a specific page.
 */
public class PageData {

    public static final String DISABLED = " disabled\" onclick=\"return false\"";

    /** The user for whom the pages are displayed (i.e. the 'nominal user'). 
     *  May not be the logged in user (under masquerade mode) */
    public AccountAttributes account;
    public StudentAttributes student;

    /**
     * @param account The account for the nominal user.
     */
    public PageData(AccountAttributes account) {
        this.account = account;
        this.student = null;
    }
    
    /**
     * @param account The account for the nominal user.
     */
    public PageData(AccountAttributes account, StudentAttributes student) {
        this.account = account;
        this.student = student;
    }
    
    public AccountAttributes getAccount() {
        return account;
    }
    
    public boolean isUnregisteredStudent() {
        return account.googleId == null || (student != null && !student.isRegistered());
    }

    @SuppressWarnings("unused")
    private void _________general_util_methods() {
    //========================================================================    
    }
    
    /* These util methods simply delegate the work to the matching *Helper
     * class. We keep them here so that JSP pages do not have to import
     * those *Helper classes.
     */
    public static String sanitizeForHtml(String unsanitizedStringLiteral) {
        return Sanitizer.sanitizeForHtml(unsanitizedStringLiteral);
    }
    
    public static String sanitizeForJs(String unsanitizedStringLiteral) {
        return Sanitizer.sanitizeForJs(unsanitizedStringLiteral);
    }
    
    public static String truncate (String untruncatedString, int truncateLength) {
        return StringHelper.truncate(untruncatedString, truncateLength);
    }
    
    public static String displayDateTime (Date date) {
        return TimeHelper.formatTime(date);
    }
    
    public String addUserIdToUrl(String link) {
        return Url.addParamToUrl(link, Const.ParamsNames.USER_ID, account.googleId);
    }
    
    /**
     * Method to color the points by adding <code>span</code> tag with appropriate
     * class (posDiff and negDiff).
     * Positive points will be green, negative will be red, 0 will be black.
     * This will also put N/A or Not Sure for respective points representation.
     * The output will be E+x% for positive points, E-x% for negative points,
     * and just E for equal share.
     * Zero contribution will be printed as 0%
     * @param points
     *         In terms of full percentage, so equal share will be 100, 20% more
     *         from equal share will be 120, etc.
     */
    public static String getPointsAsColorizedHtml(int points) {
        if (points == Const.POINTS_NOT_SUBMITTED || points == Const.INT_UNINITIALIZED) {
            return "<span class=\"color_neutral\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"" + 
                   Const.Tooltips.FEEDBACK_CONTRIBUTION_NOT_AVAILABLE + "\">N/A</span>";
        } else if (points == Const.POINTS_NOT_SURE) {
            return "<span class=\"color-negative\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"" + 
                   Const.Tooltips.FEEDBACK_CONTRIBUTION_NOT_SURE + "\">N/S</span>";
        } else if (points == 0) {
            return "<span class=\"color-negative\">0%</span>";
        } else if (points > 100) {
            return "<span class=\"color-positive\">E +" + (points - 100) + "%</span>";
        } else if (points < 100) {
            return "<span class=\"color-negative\">E -" + (100 - points) + "%</span>";
        } else {
            return "<span class=\"color_neutral\">E</span>";
        }
    }
    
    /**
     * Method to color the points by adding <code>span</code> tag with appropriate
     * class (posDiff and negDiff).
     * Positive points will be green, negative will be red, 0 will be black.
     * This will also put N/A or Not Sure for respective points representation.
     * The output will be Equal Share + x% for positive points,
     * Equal Share - x% for negative points,
     * and just Equal Share for equal share.
     * Zero contribution will be printed as 0%
     * @see #colorizePoints
     * @param points
     *         In terms of full percentage, so equal share will be 100, 20% more
     *         from equal share will be 120, etc.
     * @param inline
     *         Whether or not the "Equal Share" and the percentage will be
     *         displayed in one line.
     */
    protected static String getPointsInEqualShareFormatAsHtml(int points, boolean inline) {
        int delta = 0;
        if (points == Const.POINTS_NOT_SUBMITTED || points == Const.INT_UNINITIALIZED) {
            return "<span class=\"badge background-color-white color_neutral\">N/A</span>";
        } else if (points == Const.POINTS_NOT_SURE) {
            return "<span class=\"badge background-color-white color-negative\">Not sure</span>";
        } else if (points == 0) {
            return "<span class=\"badge background-color-white color-negative\">0%</span>";
        } else if (points > 100) {
            delta = points - 100;
            if(inline) return "<span class=\"badge background-color-white color-positive\"> E +" + delta + "%</span>";
            else return "Equal Share<br /><span class=\"badge background-color-white color-positive\"> + " + delta + 
                        "%</span>";
        } else if (points < 100) {
            delta = 100 - points;
            if(inline) return "<span class=\"badge background-color-white color-negative\"> E -" + delta + "%</span>";
            else return "Equal Share<br /><span class=\"badge background-color-white color-negative\"> - " + delta + 
                        "%</span>";
        } else {
            return "<span class=\"badge background-color-white color-positive\"> E </span>";
        }
    }
    
    
    public static String getPointsDiffAsHtml(StudentResultSummary summary) {
        int claimed = summary.claimedToInstructor;
        int perceived = summary.perceivedToInstructor;
        int diff = perceived - claimed;
        if (perceived == Const.POINTS_NOT_SUBMITTED || perceived == Const.INT_UNINITIALIZED
                || claimed == Const.POINTS_NOT_SUBMITTED || claimed == Const.INT_UNINITIALIZED) {
            return "<span class=\"color_neutral\" data-toggle=\"tooltip\" data-placement=\"top\" "
                   + "data-container=\"body\" title=\"" + Const.Tooltips.FEEDBACK_CONTRIBUTION_NOT_AVAILABLE 
                   + "\">N/A</span>";
        } else if (perceived == Const.POINTS_NOT_SURE || claimed == Const.POINTS_NOT_SURE) {
            return "<span class=\"color-negative\" data-toggle=\"tooltip\" data-placement=\"top\" "
                   + "data-container=\"body\" title=\"" + Const.Tooltips.FEEDBACK_CONTRIBUTION_NOT_SURE + "\">N/S"
                   + "</span>";
        } else if (diff > 0) {
            return "<span class=\"color-positive\">+" + diff + "%</span>";
        } else if (diff < 0) {
            return "<span class=\"color-negative\">" + diff + "%</span>";
        } else {
            return "<span>" + diff + "</span>";
        }
    }
    

    
    /**
     * Formats P2P feedback.
     * Make the headings bold, and converts newlines to html line breaks.
     */
    protected static String getP2pFeedbackAsHtml(String str, boolean enabled) {
        if (!enabled) {
            return "<span style=\"font-style: italic;\">Disabled</span>";
        }
        if (str == null || str.equals("")) {
            return "N/A";
        }
        return str.replace("&lt;&lt;What I appreciate about you as a team member&gt;&gt;:", 
                           "<strong>What I appreciate about you as a team member:</strong>")
                  .replace("&lt;&lt;Areas you can improve further&gt;&gt;:", 
                           "<strong class=\"bold\">Areas you can improve further:</strong>")
                  .replace("&lt;&lt;Other comments&gt;&gt;:", "<strong>Other comments:</strong>")
                  .replace("&#010;", "<br>");
    }
    
    /**
     * Returns the timezone options as HTML code.
     * None is selected, since the selection should only be done in client side.
     */
    protected ArrayList<String> getTimeZoneOptionsAsHtml(double existingTimeZone) {
        double[] options = new double[] {-12, -11, -10, -9, -8, -7, -6, -5, -4.5, -4, -3.5, -3, -2, -1, 0, 1, 2, 3, 
                                        3.5, 4, 4.5, 5, 5.5, 5.75, 6, 7, 8, 9, 10, 11, 12, 13};
       ArrayList<String> result = new ArrayList<String>();
       if (existingTimeZone == Const.DOUBLE_UNINITIALIZED) {
           result.add("<option value=\"" + Const.INT_UNINITIALIZED + "\" selected=\"selected\"></option>");
       }
       for (int i = 0; i < options.length; i++) {
           String utcFormatOption = StringHelper.toUtcFormat(options[i]);      
           result.add("<option value=\"" + formatAsString(options[i]) + "\"" 
                      + (existingTimeZone == options[i] ? " selected=\"selected\"" : "") + ">" + "(" + utcFormatOption 
                      + ") " + TimeHelper.getCitiesForTimeZone(Double.toString(options[i])) + "</option>");
       }
       return result;
    }
    
    protected ArrayList<ElementTag> getTimeZoneOptionsAsElementTags(double existingTimeZone) {
        double[] options = new double[] {-12, -11, -10, -9, -8, -7, -6, -5, -4.5, -4, -3.5, -3, -2, -1, 0, 1, 2, 3, 
                                         3.5, 4, 4.5, 5, 5.5, 5.75, 6, 7, 8, 9, 10, 11, 12, 13};
        ArrayList<ElementTag> result = new ArrayList<ElementTag>();
        if (existingTimeZone == Const.DOUBLE_UNINITIALIZED) {
            ElementTag option = createOption("", String.valueOf(Const.INT_UNINITIALIZED), false);
            result.add(option);
        }
        
        for (int i = 0; i < options.length; i++) {
            String utcFormatOption = StringHelper.toUtcFormat(options[i]);
            String textToDisplay = "(" + utcFormatOption 
                                            + ") " + TimeHelper.getCitiesForTimeZone(Double.toString(options[i]));
            boolean isExistingTimeZone = (existingTimeZone == options[i]);
            
            ElementTag option = createOption(textToDisplay, 
                                            formatAsString(options[i]), isExistingTimeZone);
            result.add(option);
        }
        return result;
    }
    
    public ElementTag createOption(String text, String value, boolean isSelected) {
        if (isSelected) {
            return new ElementTag(text, "value", value, "selected", "selected");
        } else {
            return new ElementTag(text, "value", value);
        }
    }
    
    /**
     * Returns the grace period options as HTML code.
     */
    protected ArrayList<String> getGracePeriodOptionsAsHtml(int existingGracePeriod) {
        ArrayList<String> result = new ArrayList<String>();
        for(int i = 0; i <= 30; i += 5) {
            result.add("<option value=\"" + i + "\"" 
                       + (isGracePeriodToBeSelected(existingGracePeriod, i) ? " selected=\"selected\"" : "") 
                       + ">" + i + " mins</option>");
        }
        return result;
    }
    
    protected ArrayList<ElementTag> getGracePeriodOptionsAsElementTags(int existingGracePeriod) {
        ArrayList<ElementTag> result = new ArrayList<ElementTag>();
        for(int i = 0; i <= 30; i += 5) {
            ElementTag option = createOption(String.valueOf(i) + " mins", String.valueOf(i), 
                                            (isGracePeriodToBeSelected(existingGracePeriod, i)));
            result.add(option);
        }
        return result;
    }
    
    /**
     * Returns the time options as HTML code.
     * By default the selected one is the last one.
     * @param timeToShowAsSelected
     */
    public ArrayList<String> getTimeOptionsAsHtml(Date timeToShowAsSelected) {
        ArrayList<String> result = new ArrayList<String>();
        for(int i = 1; i <= 24; i++) {
            result.add("<option value=\"" + i + "\"" +
                       (isTimeToBeSelected(timeToShowAsSelected, i) ? " selected=\"selected\"" : "") + ">" 
                       + String.format("%04dH", i * 100 - (i == 24 ? 41 : 0)) + "</option>");
        }
        return result;
    }
    
    public ArrayList<ElementTag> getTimeOptionsAsElementTags(Date timeToShowAsSelected) {
        ArrayList<ElementTag> result = new ArrayList<ElementTag>();
        for(int i = 1; i <= 24; i++) {
            ElementTag option = createOption(String.format("%04dH", i * 100 - (i == 24 ? 41 : 0)), 
                                             String.valueOf(i), (isTimeToBeSelected(timeToShowAsSelected, i)));
            result.add(option);
        }
        return result;
    }
    
    
    @SuppressWarnings("unused")
    private void ___________methods_to_generate_student_links() {
    //========================================================================    
    }
    
    //TODO: methods below this point should be made 'protected' and only the
    //  child classes that need them should expose them using public methods
    //  with similar name. That way, we know which child needs which method.
    
    /**
     * Returns the status of the student, whether he has joined the course.
     * This is based on googleId, if it's null or empty, then we assume he
     * has not joined the course yet.
     * @return "Yet to Join" or "Joined"
     */
    public String getStudentStatus(StudentAttributes student) {
        if (student.googleId == null || student.googleId.equals("")) {
            return Const.STUDENT_COURSE_STATUS_YET_TO_JOIN;
        } else {
            return Const.STUDENT_COURSE_STATUS_JOINED;
        }
    }
    
    /**
     * @return The relative path to the student home page.
     * Defaults to whether the student is unregistered.
     */
    public String getStudentHomeLink() {
        return getStudentHomeLink(isUnregisteredStudent());
    }

    /**
     * @return The relative path to the student home page. 
     * The user Id is encoded in the url as a parameter.
     */
    public String getStudentHomeLink(boolean isUnregistered) {
        String link = Const.ActionURIs.STUDENT_HOME_PAGE;
        link = addUserIdToUrl(link);
        if (isUnregistered) {
            link = Url.addParamToUrl(student.getRegistrationUrl(), Const.ParamsNames.NEXT_URL, link);
        }
        return link;
    }
    
    /**
     * @return The relative path to the student profile page.
     * Defaults to whether the student is unregistered.
     */
    public String getStudentProfileLink() {
        return getStudentProfileLink(isUnregisteredStudent());
    }
    
    /**
     * @return The relative path to the student profile page. 
     * The user Id is encoded in the url as a parameter.
     */
    public String getStudentProfileLink(boolean isUnregistered) {
        String link = Const.ActionURIs.STUDENT_PROFILE_PAGE;
        link = addUserIdToUrl(link);
        if (isUnregistered) {
            link = Url.addParamToUrl(student.getRegistrationUrl(), Const.ParamsNames.NEXT_URL, link);
        }
        return link;
    }
    
    /**
     * @return The relative path to the student comments page.
     * Defaults to whether the student is unregistered.
     */
    public String getStudentCommentsLink() {
        return getStudentCommentsLink(isUnregisteredStudent());
    }
    
    /**
     * @return The relative path to the student comments page. 
     * The user Id is encoded in the url as a parameter.
     */
    public String getStudentCommentsLink(boolean isUnregistered) {
        String link = Const.ActionURIs.STUDENT_COMMENTS_PAGE;
        link = addUserIdToUrl(link);
        if (isUnregistered) {
            link = Url.addParamToUrl(student.getRegistrationUrl(), Const.ParamsNames.NEXT_URL, link);
        }
        return link;
    }
    
    @SuppressWarnings("unused")
    private void ___________methods_to_generate_instructor_links() {
    //========================================================================    
    }
    
    /**
     * @return The relative path to the instructor home page. 
     * The user Id is encoded in the url as a parameter.
     */
    public String getInstructorHomeLink() {
        String link = Const.ActionURIs.INSTRUCTOR_HOME_PAGE;
        link = addUserIdToUrl(link);
        return link;
    }
    
    public String getInstructorCourseLink() {
        String link = Const.ActionURIs.INSTRUCTOR_COURSES_PAGE;
        link = addUserIdToUrl(link);
        return link;
    }
    
    
    public String getInstructorCourseEnrollLink(String courseId) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = addUserIdToUrl(link);
        return link;
    }
    
    
    public String getInstructorCourseEnrollSaveLink(String courseId) {
        //TODO: instead of using this method, the form should include these data as hidden fields?
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_ENROLL_SAVE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = addUserIdToUrl(link);
        return link;
    }

    public String getInstructorCourseDetailsLink(String courseID) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseID); 
        link = addUserIdToUrl(link);
        return link;
    }
    
    
    public String getInstructorCourseEditLink(String courseID) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseID); 
        link = addUserIdToUrl(link);
        return link;
    }
    
    public String getFeedbackSessionStatsLink(String courseID, String feedbackSessionName) {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_STATS_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseID);
        link = Url.addParamToUrl(link, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName); 
        link = addUserIdToUrl(link);
        return link;
    }
    
    public String getFeedbackSessionEditCopyLink() {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_COPY_PAGE;
        link = addUserIdToUrl(link);
        return link;
    }
    
  
    /**
     * @param courseId
     * @param isHome True if the Browser should redirect to the Home page after the operation. 
     */
    public String getInstructorCourseDeleteLink(String courseId, boolean isHome) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_DELETE;
        link = Url.addParamToUrl(link,Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, 
                                 Const.ParamsNames.NEXT_URL,
                                 (isHome ? Const.ActionURIs.INSTRUCTOR_HOME_PAGE 
                                         : Const.ActionURIs.INSTRUCTOR_COURSES_PAGE));
        link = addUserIdToUrl(link);
        return link;
    }
    
    public String getInstructorEditStudentFeedbackLink() {
        String link = Const.ActionURIs.INSTRUCTOR_EDIT_STUDENT_FEEDBACK_PAGE;
        link = addUserIdToUrl(link);
        return link;
    }
    
    public String getInstructorCourseArchiveLink(String courseId, boolean archiveStatus, boolean isHome) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_ARCHIVE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ARCHIVE_STATUS, Boolean.toString(archiveStatus));
        link = Url.addParamToUrl(link,
                                 Const.ParamsNames.NEXT_URL,
                                 (isHome ? Const.ActionURIs.INSTRUCTOR_HOME_PAGE 
                                         : Const.ActionURIs.INSTRUCTOR_COURSES_PAGE));
        link = addUserIdToUrl(link);
        return link;
    }
    
    public String getInstructorFeedbacksPageLink() {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE;
        link = addUserIdToUrl(link);
        return link;
    }
    
    public String getInstructorClearPendingCommentsLink(String courseId) {
        String link = Const.ActionURIs.INSTRUCTOR_STUDENT_COMMENT_CLEAR_PENDING;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = addUserIdToUrl(link);
        return link;
    }
    
    public String getInstructorFeedbackSessionLink() {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE;
        link = addUserIdToUrl(link);
        return link;
    }
    
    public String getInstructorFeedbackSessionDeleteLink(String courseId, String feedbackSessionName, String nextURL) {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_DELETE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        link = Url.addParamToUrl(link, Const.ParamsNames.NEXT_URL, addUserIdToUrl(nextURL));
        link = addUserIdToUrl(link);
        return link;
    }    
    
    public String getInstructorFeedbackSessionEditLink(String courseId, String feedbackSessionName) {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        link = addUserIdToUrl(link);
        return link;
    }
    
    public String getInstructorFeedbackSessionSubmitLink(String courseId, String feedbackSessionName) {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        link = addUserIdToUrl(link);
        return link;
    }
    
    public String getInstructorFeedbackSessionResultsLink(String courseId, String feedbackSessionName) {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID,courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        link = addUserIdToUrl(link);
        return link;
    }
    
    public String getInstructorFeedbackSessionRemindLink(String courseID, String feedbackSessionName) {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_REMIND;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseID);
        link = Url.addParamToUrl(link, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        link = addUserIdToUrl(link);
        return link;
    }
    
    public String getInstructorFeedbackSessionRemindParticularStudentsPageLink(String courseID, 
                                                                               String feedbackSessionName) {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_REMIND_PARTICULAR_STUDENTS_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseID);
        link = Url.addParamToUrl(link, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        link = addUserIdToUrl(link);
        return link;
    }
    
    public String getInstructorFeedbackSessionPublishLink(String courseID, 
                                                          String feedbackSessionName, 
                                                          boolean isHome) {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_PUBLISH;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseID);
        link = Url.addParamToUrl(link, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        link = Url.addParamToUrl(link, 
                                 Const.ParamsNames.NEXT_URL, 
                                 (isHome ? addUserIdToUrl(Const.ActionURIs.INSTRUCTOR_HOME_PAGE) 
                                         : addUserIdToUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE)));
        link = addUserIdToUrl(link);
        return link;
    }
    
    
    public String getInstructorFeedbackSessionUnpublishLink(String courseID, String feedbackSessionName, boolean isHome) {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_UNPUBLISH;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseID);
        link = Url.addParamToUrl(link, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        link = Url.addParamToUrl(link, 
                                 Const.ParamsNames.NEXT_URL,
                                 (isHome ? addUserIdToUrl(Const.ActionURIs.INSTRUCTOR_HOME_PAGE)
                                         : addUserIdToUrl(Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE)));
        link = addUserIdToUrl(link);
        return link;
    }
    
    public String getInstructorStudentListLink() {
        String link = Const.ActionURIs.INSTRUCTOR_STUDENT_LIST_PAGE;
        link = addUserIdToUrl(link);
        return link;
    }
    
    public String getInstructorSearchLink() {
        String link = Const.ActionURIs.INSTRUCTOR_SEARCH_PAGE;
        link = addUserIdToUrl(link);
        return link;
    }
    
    public String getInstructorStudentRecordsLink(String courseId, String studentEmail) {
        String link = Const.ActionURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.STUDENT_EMAIL, studentEmail);
        link = addUserIdToUrl(link);
        return link;
    }
    
    public String getInstructorCommentsLink() {
        String link = Const.ActionURIs.INSTRUCTOR_COMMENTS_PAGE;
        link = addUserIdToUrl(link);
        return link;
    }

    
    @SuppressWarnings("unused")
    private void _________other_util_methods_for_instructor_pages() {
    //========================================================================    
    }

    public static String getInstructorStatusForFeedbackSession(FeedbackSessionAttributes session) {
        if (session.isPrivateSession()) {
             return "Private";
        } else if (session.isOpened()) {
            return "Open";
        } else if (session.isWaitingToOpen()) {
            return "Awaiting";
        } else if (session.isPublished()) {
            return "Published";
        } else {
            return "Closed";
        }
    }

    public static String getInstructorHoverMessageForFeedbackSession(FeedbackSessionAttributes session) {
        
        if (session.isPrivateSession()) {
            return Const.Tooltips.FEEDBACK_SESSION_STATUS_PRIVATE;
        }
        
        String msg = "The feedback session has been created";
        
        if (session.isVisible()) {
            msg += Const.Tooltips.FEEDBACK_SESSION_STATUS_VISIBLE;
        }
        
        if (session.isOpened()) {
            msg += Const.Tooltips.FEEDBACK_SESSION_STATUS_OPEN;
        } else if (session.isWaitingToOpen()) {
            msg += Const.Tooltips.FEEDBACK_SESSION_STATUS_AWAITING;
        } else if(session.isClosed()) {
            msg += Const.Tooltips.FEEDBACK_SESSION_STATUS_CLOSED;
        }
        
        if (session.isPublished()) {
            msg += Const.Tooltips.FEEDBACK_SESSION_STATUS_PUBLISHED;
        }
        
        msg += ".";
        
        return msg;
    }
    
    /**
     * Gets a Map with courseId as key and List of sectionNames as value
     * @param fsaList
     * @return
     * @throws EntityDoesNotExistException
     */
    public Map<String, List<String>> getCourseIdSectionNamesMap(List<FeedbackSessionAttributes> fsaList) throws EntityDoesNotExistException {
        Map<String, List<String>> courseIdSectionNamesMap = new HashMap<String, List<String>>();
        
        for (FeedbackSessionAttributes fsa : fsaList) {
            String courseId = fsa.courseId;
            if (courseIdSectionNamesMap.get(courseId) == null) {
                List<String> sectionsInCourse = new Logic().getSectionNamesForCourse(courseId);
                courseIdSectionNamesMap.put(courseId, sectionsInCourse);
            }
        }
        return courseIdSectionNamesMap;
    }
    
    /**
     * Returns the links of actions available for a specific session
     * @param session
     *         The feedback session details
     * @param isHome
     *         Flag whether the link is to be put at homepage (to determine the redirect link in delete / publish)
     * @param instructor
     *         The Instructor details
     * @param sectionsInCourse
     *         The list of sections for the course
     * @return
     * @throws EntityDoesNotExistException 
     */
    public String getInstructorFeedbackSessionActions(FeedbackSessionAttributes session,
            boolean isHome, InstructorAttributes instructor, List<String> sectionsInCourse) throws EntityDoesNotExistException{
        StringBuilder result = new StringBuilder();
        
        // Allowing ALL instructors to view results regardless of publish state.
        boolean hasSubmit = session.isVisible() || session.isPrivateSession();
        boolean hasRemind = session.isOpened();
        String disabledStr = "disabled=\"disabled\"";
        String disableEditSessionStr = instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION) ? "" : disabledStr;
        String disableDeleteSessionStr = instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION) ? "" : disabledStr;
        String disableRemindSessionStr = (instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION) && hasRemind) ? "" : disabledStr;
        boolean shouldEnableSubmitLink = instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS);
        
        for (String section : sectionsInCourse) {
            if (instructor.isAllowedForPrivilege(section, 
                                                 session.feedbackSessionName, 
                                                 Const.ParamsNames.INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS)) {
                shouldEnableSubmitLink = true;
                break;
            }
        }
        String disableSubmitSessionStr = shouldEnableSubmitLink ? "" : disabledStr;
        result.append("<a class=\"btn btn-default btn-xs btn-tm-actions session-view-for-test\"" 
                      + "href=\"" + getInstructorFeedbackSessionResultsLink(session.courseId,
                                                                            session.feedbackSessionName) + "\" " 
                      + "title=\"" + Const.Tooltips.FEEDBACK_SESSION_RESULTS + "\" data-toggle=\"tooltip\" "
                      + "data-placement=\"top\">View Results</a> ");
        result.append("<a class=\"btn btn-default btn-xs btn-tm-actions session-edit-for-test\"" 
                       + "href=\"" + getInstructorFeedbackSessionEditLink(session.courseId,
                                                                          session.feedbackSessionName) + "\" " 
                       + "title=\"" + Const.Tooltips.FEEDBACK_SESSION_EDIT + "\" data-toggle=\"tooltip\" "
                       + "data-placement=\"top\" " + disableEditSessionStr + ">Edit</a> ");
        result.append("<a class=\"btn btn-default btn-xs btn-tm-actions session-delete-for-test\"" 
                      + "href=\"" + getInstructorFeedbackSessionDeleteLink(
                                      session.courseId, 
                                      session.feedbackSessionName, 
                                      (isHome ? Const.ActionURIs.INSTRUCTOR_HOME_PAGE 
                                              : Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE)) + "\" " 
                      + "title=\"" + Const.Tooltips.FEEDBACK_SESSION_DELETE + "\" data-toggle=\"tooltip\" "
                      + "data-placement=\"top\" onclick=\"return toggleDeleteFeedbackSessionConfirmation('" 
                      + session.courseId + "','" + session.feedbackSessionName + "');\" " + disableDeleteSessionStr 
                      + ">Delete</a> "
        );
        result.append("<a class=\"btn btn-default btn-xs btn-tm-actions session-copy-for-test\" href=\"#\"" 
                      + "title=\"" + Const.Tooltips.FEEDBACK_SESSION_COPY + "\" data-actionlink=\"" 
                      + getFeedbackSessionEditCopyLink() + "\" data-courseid=\"" + session.courseId + "\" "
                      + "data-fsname=\"" + session.feedbackSessionName + "\" data-toggle=\"modal\" "
                      + "data-target=\"#fsCopyModal\" data-placement=\"top\" id=\"button_fscopy" + "-" 
                      + session.courseId + "-" + session.feedbackSessionName + "\">Copy</a> ");
        result.append("<div title=\"" + Const.Tooltips.FEEDBACK_SESSION_SUBMIT + "\" data-toggle=\"tooltip\" "
                      + "data-placement=\"top\"" + " style=\"display: inline-block; padding-right: 5px;\">" 
                      + "<a class=\"btn btn-default btn-xs btn-tm-actions session-submit-for-test" 
                      + (hasSubmit ? "\"" : DISABLED) + "href=\"" + getInstructorFeedbackSessionSubmitLink(
                                                                              session.courseId, 
                                                                              session.feedbackSessionName) 
                      + "\" " + disableSubmitSessionStr + ">Submit</a></div>");
        
        // Don't need to show any other links if private
        if (session.isPrivateSession()) {
            return result.toString();
        }
        
        result.append(
            "<div title=\"" + Const.Tooltips.FEEDBACK_SESSION_REMIND + "\" data-toggle=\"tooltip\" data-placement=\"top\" style=\"display: inline-block; padding-right: 5px;\">" +
                "<div class=\"btn-group\">" +
                    "<a class=\"btn btn-default btn-xs btn-tm-actions session-remind-for-test" 
                        + ((instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION) && hasRemind) ? "\" " : DISABLED) 
                        + "href=\"" + getInstructorFeedbackSessionRemindLink(session.courseId, session.feedbackSessionName) + "\" " 
                        + ((instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION) && hasRemind) ? "onclick=\"return toggleRemindStudents('" + session.feedbackSessionName + "');\" " : "") +
                        disableRemindSessionStr + ">Remind" +
                    "</a>" +
                    "<button type=\"button\" class=\"btn btn-default btn-xs btn-tm-actions dropdown-toggle session-remind-options-for-test\"" + 
                        disableRemindSessionStr + "data-toggle=\"dropdown\" aria-expanded=\"false\">" +
                        "<span class=\"caret\"></span>" +
                    "</button>" +
                    "<ul class=\"dropdown-menu\" role=\"menu\">" +
                        "<li>" +
                            "<a href=\"" + getInstructorFeedbackSessionRemindLink(session.courseId, session.feedbackSessionName) + "\" " +
                                "class=\"session-remind-inner-for-test\" " +
                                (hasRemind ? "onclick=\"return toggleRemindStudents('" + session.feedbackSessionName + "');\" " : " ") +
                                disableRemindSessionStr + ">Remind all students" +
                            "</a>" +
                        "</li>" +
                        "<li>" +
                            "<a href=\"#\" data-actionlink=\"" 
                                + getInstructorFeedbackSessionRemindParticularStudentsPageLink(
                                            session.courseId, 
                                            session.feedbackSessionName) + "\" " 
                                + "class=\"session-remind-particular-for-test\" " + disableRemindSessionStr 
                                + "data-courseid=\"" + session.courseId + "\" data-fsname=\"" 
                                + session.feedbackSessionName + "\" data-toggle=\"modal\" "
                                + "data-target=\"#remindModal\">Remind particular students" +
                            "</a>" +
                        "</li>" +
                    "</ul>" +
                "</div>" +
            "</div>"
        );
        
        result.append(getInstructorFeedbackSessionPublishAndUnpublishAction(session, isHome, instructor));

        return result.toString();
    }
    
    /**
     * Returns the link of publish and unpublish action for the session feedback
     * @param session
     *         The session details
     * @param isHome
     *         Flag whether the link is to be put at homepage (to determine the redirect link in delete / publish)
     * @param instructor
     *         The instructor attributes of the session feedback
     * @return
     */
    public String getInstructorFeedbackSessionPublishAndUnpublishAction(FeedbackSessionAttributes session, 
                                                                        boolean isHome, 
                                                                        InstructorAttributes instructor) {
        boolean hasPublish = !session.isWaitingToOpen() && !session.isPublished();
        boolean hasUnpublish = !session.isWaitingToOpen() && session.isPublished();
        String disabledStr = "disabled=\"disabled\"";
        String disableUnpublishSessionStr = 
                instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION) ? "" 
                                                                                                         : disabledStr;
        String disablePublishSessionStr = 
                instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION) ? "" 
                                                                                                         : disabledStr;
        String result = "";
        if (hasUnpublish) {
            result =
                "<a class=\"btn btn-default btn-xs btn-tm-actions session-unpublish-for-test\""
                    + "href=\"" + getInstructorFeedbackSessionUnpublishLink(session.courseId, 
                                                                            session.feedbackSessionName, 
                                                                            isHome) + "\" " 
                    + "title=\"" + Const.Tooltips.FEEDBACK_SESSION_UNPUBLISH + "\" data-toggle=\"tooltip\" "
                    + "data-placement=\"top\" onclick=\"return toggleUnpublishEvaluation('" 
                    + session.feedbackSessionName + "');\" " + disableUnpublishSessionStr + ">Unpublish Results</a> ";
        } else {
            result = 
                "<div title=\"" + (hasPublish ? Const.Tooltips.FEEDBACK_SESSION_PUBLISH 
                                              : Const.Tooltips.FEEDBACK_SESSION_AWAITING) + "\" "
                    + "data-toggle=\"tooltip\" data-placement=\"top\" style=\"display: inline-block; "
                    + "padding-right: 5px;\">" +
                    "<a class=\"btn btn-default btn-xs btn-tm-actions session-publish-for-test" 
                        + (hasPublish ? "\"" : DISABLED) + "href=\"" + getInstructorFeedbackSessionPublishLink(
                                                                                session.courseId, 
                                                                                session.feedbackSessionName, isHome) 
                        + "\" " + (hasPublish ? "onclick=\"return togglePublishEvaluation('" 
                                                + session.feedbackSessionName + "', " 
                                                + session.isPublishedEmailEnabled + ");\" " 
                                              : " ") 
                        + disablePublishSessionStr + ">Publish Results" +
                    "</a> " +
                "</div>";
        }
        return result;
    }
    
    /**
     * Returns the type of people that can view the comment. 
     */
    public String getTypeOfPeopleCanViewComment(CommentAttributes comment) {
        StringBuilder peopleCanView = new StringBuilder();
        for(int i = 0; i < comment.showCommentTo.size(); i++){
            CommentParticipantType commentViewer = comment.showCommentTo.get(i);
            if(i == comment.showCommentTo.size() - 1 && comment.showCommentTo.size() > 1) {
                peopleCanView.append("and ");
            }
            
            switch (commentViewer) {
            case PERSON :
                peopleCanView.append("recipient, ");
                break;
            case TEAM :
                if(comment.recipientType == CommentParticipantType.TEAM) {
                    peopleCanView.append("recipient team, ");
                } else {
                    peopleCanView.append("recipient's team, ");
                }
                break;
            case SECTION :
                if(comment.recipientType == CommentParticipantType.SECTION) {
                    peopleCanView.append("recipient section, ");
                } else {
                    peopleCanView.append("recipient's section, ");
                }
                break;
            case COURSE :
                if(comment.recipientType == CommentParticipantType.COURSE) {
                    peopleCanView.append("the whole class, ");
                } else {
                    peopleCanView.append("other students in this course, ");
                }
                break;
            case INSTRUCTOR :
                peopleCanView.append("instructors, ");
                break;
            default :
                break;
            }
        }
        String peopleCanViewString = peopleCanView.toString();
        if(peopleCanViewString.isEmpty()) {
            return peopleCanViewString;
        }
        return removeEndComma(peopleCanViewString);
    }
    
    /**
     * Returns the type of people that can view the response comment. 
     */
    public String getTypeOfPeopleCanViewComment(FeedbackResponseCommentAttributes comment,
                                                FeedbackQuestionAttributes relatedQuestion) {
        StringBuilder peopleCanView = new StringBuilder();
        List<FeedbackParticipantType> showCommentTo = new ArrayList<FeedbackParticipantType>();
        if (comment.isVisibilityFollowingFeedbackQuestion) {
            showCommentTo = relatedQuestion.showResponsesTo;
        } else {
            showCommentTo = comment.showCommentTo;
        }
        for(int i = 0; i < showCommentTo.size(); i++) {
            FeedbackParticipantType commentViewer = showCommentTo.get(i);
            if (i == showCommentTo.size() - 1 && showCommentTo.size() > 1) {
                peopleCanView.append("and ");
            }
            
            switch (commentViewer) {
            case GIVER :
                peopleCanView.append("response giver, ");
                break;
            case RECEIVER :
                peopleCanView.append("response recipient, ");
                break;
            case OWN_TEAM :
                peopleCanView.append("response giver's team, ");
                break;
            case RECEIVER_TEAM_MEMBERS :
                peopleCanView.append("response recipient's team, ");
                break;
            case STUDENTS :
                peopleCanView.append("other students in this course, ");
                break;
            case INSTRUCTORS :
                peopleCanView.append("instructors, ");
                break;
            default :
                break;
            }
        }
        String peopleCanViewString = peopleCanView.toString();
        return removeEndComma(peopleCanViewString);
    }
    
    public String removeEndComma(String str) {
        return str.substring(0, str.length() - 2);
    }

    
    private boolean isTimeToBeSelected(Date timeToShowAsSelected, int hourOfTheOption) {
        boolean isEditingExistingFeedbackSession = (timeToShowAsSelected!=null);
        if (isEditingExistingFeedbackSession) {
            Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
            cal.setTime(timeToShowAsSelected);
            if (cal.get(Calendar.MINUTE) == 0) {
                if (cal.get(Calendar.HOUR_OF_DAY) == hourOfTheOption) {
                    return true;
                }
            } else {
                if (hourOfTheOption == 24) {
                    return true;
                }
            }
        } else {
            if (hourOfTheOption == 24) {
                return true;
            }
        }
        return false;
    }

    private boolean isGracePeriodToBeSelected(int existingGracePeriodValue, int gracePeriodOptionValue) {
        int defaultGracePeriod = 15;
        boolean isEditingExistingEvaluation = (existingGracePeriodValue != Const.INT_UNINITIALIZED);
        if (isEditingExistingEvaluation) {
            return gracePeriodOptionValue == existingGracePeriodValue;
        } else {
            return gracePeriodOptionValue == defaultGracePeriod;
        }
    }

    private static String formatAsString(double num) {
        if ((int) num == num) {
            return "" + (int) num;
        } else {
            return "" + num;
        }
    }
    
    public boolean isCourseArchived(String courseId, String googleId) {
        return Logic.isCourseArchived(courseId, googleId);
    }
    
    @SuppressWarnings("unused")
    private void ___________methods_to_generate_feedback_response_comments() {
    //========================================================================    
    }
    
    public boolean isResponseCommentVisibleTo(FeedbackQuestionAttributes qn,
                                              FeedbackParticipantType viewerType) {
        if (viewerType == FeedbackParticipantType.GIVER) {
            return true;
        } else {
            return qn.isResponseVisibleTo(viewerType);
        }
    }
    
    public boolean isResponseCommentGiverNameVisibleTo(FeedbackQuestionAttributes qn,
                                                       FeedbackParticipantType viewerType) {
        return true;
    }
    
    public boolean isResponseCommentVisibleTo(FeedbackResponseCommentAttributes frComment, 
                                              FeedbackQuestionAttributes qn,
                                              FeedbackParticipantType viewerType) {
        if (frComment.isVisibilityFollowingFeedbackQuestion && viewerType == FeedbackParticipantType.GIVER) {
            return true;
        } else if (frComment.isVisibilityFollowingFeedbackQuestion) {
            return qn.isResponseVisibleTo(viewerType);
        } else {
            return frComment.isVisibleTo(viewerType);
        }
    }
    
    public boolean isResponseCommentGiverNameVisibleTo(FeedbackResponseCommentAttributes frComment, 
                                                       FeedbackQuestionAttributes qn,
                                                       FeedbackParticipantType viewerType) {
        if (frComment.isVisibilityFollowingFeedbackQuestion) {
            return true;
        } else {
            return frComment.showGiverNameTo.contains(viewerType);
        }
    }
    
    public String getResponseCommentVisibilityString(FeedbackQuestionAttributes qn) {
        return "GIVER," + removeBracketsForArrayString(qn.showResponsesTo.toString());
    }
    
    public String getResponseCommentVisibilityString(FeedbackResponseCommentAttributes frComment, 
                                                     FeedbackQuestionAttributes qn) {
        if (frComment.isVisibilityFollowingFeedbackQuestion) {
            return getResponseCommentVisibilityString(qn);
        } else {
            return removeBracketsForArrayString(frComment.showCommentTo.toString());
        }
    }
    
    public String getResponseCommentGiverNameVisibilityString(FeedbackQuestionAttributes qn) {
        return getResponseCommentVisibilityString(qn);
    }
    
    public String getResponseCommentGiverNameVisibilityString(FeedbackResponseCommentAttributes frComment, 
                                                              FeedbackQuestionAttributes qn) {
        if (frComment.isVisibilityFollowingFeedbackQuestion) {
            return getResponseCommentGiverNameVisibilityString(qn);
        } else {
            return removeBracketsForArrayString(frComment.showGiverNameTo.toString());
        }
    }
    
    public String removeBracketsForArrayString(String arrayString) {
        return arrayString.substring(1, arrayString.length() - 1).trim();
    }
}
