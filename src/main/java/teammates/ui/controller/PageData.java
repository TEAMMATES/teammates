package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Logger;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentParticipantType;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.common.util.StatusMessage;
import teammates.common.util.StringHelper;
import teammates.common.util.TimeHelper;
import teammates.common.util.Url;
import teammates.common.util.Utils;
import teammates.ui.template.ElementTag;
import teammates.ui.template.InstructorFeedbackSessionActions;

/**
 * Data and utility methods needed to render a specific page.
 */
public class PageData {
    
    protected static final Logger log = Utils.getLogger();
    
    /** The user for whom the pages are displayed (i.e. the 'nominal user').
     *  May not be the logged in user (under masquerade mode) */
    public AccountAttributes account;
    public StudentAttributes student;

    private List<StatusMessage> statusMessagesToUser;

    private static final List<String> nationalities = new ArrayList<>();

    /**
     * This list of nationalities was created for the drop-down list and contains 198 nationalities.
     * reference : "https://mytaskhelper.com/forums/5-faq-s/topics/98-nationality-dropdown-list"
     */
    static{
        createListForNationalities("Afghan");
        createListForNationalities("Albanian");
        createListForNationalities("Algerian");
        createListForNationalities("American");
        createListForNationalities("Andorran");
        createListForNationalities("Angolan");
        createListForNationalities("Antiguans");
        createListForNationalities("Argentinean");
        createListForNationalities("Armenian");
        createListForNationalities("Australian");
        createListForNationalities("Austrian");
        createListForNationalities("Azerbaijani");
        createListForNationalities("Bahamian");
        createListForNationalities("Bahraini");
        createListForNationalities("Bangladeshi");
        createListForNationalities("Barbadian");
        createListForNationalities("Barbudans");
        createListForNationalities("Batswana");
        createListForNationalities("Belarusian");
        createListForNationalities("Belgian");
        createListForNationalities("Belizean");
        createListForNationalities("Beninese");
        createListForNationalities("Bhutanese");
        createListForNationalities("Bolivian");
        createListForNationalities("Bosnian");
        createListForNationalities("Brazilian");
        createListForNationalities("British");
        createListForNationalities("Bruneian");
        createListForNationalities("Bulgarian");
        createListForNationalities("Burkinabe");
        createListForNationalities("Burmese");
        createListForNationalities("Burundian");
        createListForNationalities("Cambodian");
        createListForNationalities("Cameroonian");
        createListForNationalities("Canadian");
        createListForNationalities("Cape Verdean");
        createListForNationalities("Central African");
        createListForNationalities("Chadian");
        createListForNationalities("Chilean");
        createListForNationalities("Chinese");
        createListForNationalities("Colombian");
        createListForNationalities("Comoran");
        createListForNationalities("Congolese");
        createListForNationalities("Congolese");
        createListForNationalities("Costa Rican");
        createListForNationalities("Croatian");
        createListForNationalities("Cuban");
        createListForNationalities("Cypriot");
        createListForNationalities("Czech");
        createListForNationalities("Danish");
        createListForNationalities("Djibouti");
        createListForNationalities("Dominican");
        createListForNationalities("Dutch");
        createListForNationalities("Dutchman");
        createListForNationalities("Dutchwoman");
        createListForNationalities("East Timorese");
        createListForNationalities("Ecuadorean");
        createListForNationalities("Egyptian");
        createListForNationalities("Emirian");
        createListForNationalities("Equatorial Guinean");
        createListForNationalities("Eritrean");
        createListForNationalities("Estonian");
        createListForNationalities("Ethiopian");
        createListForNationalities("Fijian");
        createListForNationalities("Filipino");
        createListForNationalities("Finnish");
        createListForNationalities("French");
        createListForNationalities("Gabonese");
        createListForNationalities("Gambian");
        createListForNationalities("Georgian");
        createListForNationalities("German");
        createListForNationalities("Ghanaian");
        createListForNationalities("Greek");
        createListForNationalities("Grenadian");
        createListForNationalities("Guatemalan");
        createListForNationalities("Guinea-Bissauan");
        createListForNationalities("Guinean");
        createListForNationalities("Guyanese");
        createListForNationalities("Haitian");
        createListForNationalities("Herzegovinian");
        createListForNationalities("Honduran");
        createListForNationalities("Hungarian");
        createListForNationalities("I-Kiribati");
        createListForNationalities("Icelander");
        createListForNationalities("Indian");
        createListForNationalities("Indonesian");
        createListForNationalities("Iranian");
        createListForNationalities("Iraqi");
        createListForNationalities("Irish");
        createListForNationalities("Irish");
        createListForNationalities("Israeli");
        createListForNationalities("Italian");
        createListForNationalities("Ivorian");
        createListForNationalities("Jamaican");
        createListForNationalities("Japanese");
        createListForNationalities("Jordanian");
        createListForNationalities("Kazakhstani");
        createListForNationalities("Kenyan");
        createListForNationalities("Kittian and Nevisian");
        createListForNationalities("Kuwaiti");
        createListForNationalities("Kyrgyz");
        createListForNationalities("Laotian");
        createListForNationalities("Latvian");
        createListForNationalities("Lebanese");
        createListForNationalities("Liberian");
        createListForNationalities("Libyan");
        createListForNationalities("Liechtensteiner");
        createListForNationalities("Lithuanian");
        createListForNationalities("Luxembourger");
        createListForNationalities("Macedonian");
        createListForNationalities("Malagasy");
        createListForNationalities("Malawian");
        createListForNationalities("Malaysian");
        createListForNationalities("Maldivan");
        createListForNationalities("Malian");
        createListForNationalities("Maltese");
        createListForNationalities("Marshallese");
        createListForNationalities("Mauritanian");
        createListForNationalities("Mauritian");
        createListForNationalities("Mexican");
        createListForNationalities("Micronesian");
        createListForNationalities("Moldovan");
        createListForNationalities("Monacan");
        createListForNationalities("Mongolian");
        createListForNationalities("Moroccan");
        createListForNationalities("Mosotho");
        createListForNationalities("Motswana");
        createListForNationalities("Mozambican");
        createListForNationalities("Namibian");
        createListForNationalities("Nauruan");
        createListForNationalities("Nepalese");
        createListForNationalities("Netherlander");
        createListForNationalities("New Zealander");
        createListForNationalities("Ni-Vanuatu");
        createListForNationalities("Nicaraguan");
        createListForNationalities("Nigerian");
        createListForNationalities("Nigerien");
        createListForNationalities("North Korean");
        createListForNationalities("Northern Irish");
        createListForNationalities("Norwegian");
        createListForNationalities("Omani");
        createListForNationalities("Pakistani");
        createListForNationalities("Palauan");
        createListForNationalities("Panamanian");
        createListForNationalities("Papua New Guinean");
        createListForNationalities("Paraguayan");
        createListForNationalities("Peruvian");
        createListForNationalities("Polish");
        createListForNationalities("Portuguese");
        createListForNationalities("Qatari");
        createListForNationalities("Romanian");
        createListForNationalities("Russian");
        createListForNationalities("Rwandan");
        createListForNationalities("Saint Lucian");
        createListForNationalities("Salvadoran");
        createListForNationalities("Samoan");
        createListForNationalities("San Marinese");
        createListForNationalities("Sao Tomean");
        createListForNationalities("Saudi");
        createListForNationalities("Scottish");
        createListForNationalities("Senegalese");
        createListForNationalities("Serbian");
        createListForNationalities("Seychellois");
        createListForNationalities("Sierra Leonean");
        createListForNationalities("Singaporean");
        createListForNationalities("Slovakian");
        createListForNationalities("Solomon Islander");
        createListForNationalities("Somali");
        createListForNationalities("South African");
        createListForNationalities("South Korean");
        createListForNationalities("Spanish");
        createListForNationalities("Sri Lankan");
        createListForNationalities("Sudanese");
        createListForNationalities("Surinamer");
        createListForNationalities("Swazi");
        createListForNationalities("Swedish");
        createListForNationalities("Swiss");
        createListForNationalities("Syrian");
        createListForNationalities("Taiwanese");
        createListForNationalities("Tajik");
        createListForNationalities("Tanzanian");
        createListForNationalities("Thai");
        createListForNationalities("Togolese");
        createListForNationalities("Tongan");
        createListForNationalities("Tobagonian");
        createListForNationalities("Tunisian");
        createListForNationalities("Turkish");
        createListForNationalities("Tuvaluan");
        createListForNationalities("Ugandan");
        createListForNationalities("Ukrainian");
        createListForNationalities("Uruguayan");
        createListForNationalities("Uzbekistani");
        createListForNationalities("Venezuelan");
        createListForNationalities("Vietnamese");
        createListForNationalities("Welsh");
        createListForNationalities("Yemenite");
        createListForNationalities("Zambian");
        createListForNationalities("Zimbabwean");
    }

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
        return account.googleId == null || student != null && !student.isRegistered();
    }
    
    public static void createListForNationalities(String nationality) {
        nationalities.add(nationality);
    }

    public static List<String> getNationalities() {
        return nationalities;
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
    
    public static String truncate(String untruncatedString, int truncateLength) {
        return StringHelper.truncate(untruncatedString, truncateLength);
    }
    
    public static String displayDateTime(Date date) {
        return TimeHelper.formatTime12H(date);
    }
    
    public String addUserIdToUrl(String link) {
        return Url.addParamToUrl(link, Const.ParamsNames.USER_ID, account.googleId);
    }
    
    /**
     * Returns the timezone options as HTML code.
     * None is selected, since the selection should only be done in client side.
     */
    protected ArrayList<String> getTimeZoneOptionsAsHtml(double existingTimeZone) {
        List<Double> options = TimeHelper.getTimeZoneValues();
        ArrayList<String> result = new ArrayList<String>();
        if (existingTimeZone == Const.DOUBLE_UNINITIALIZED) {
            result.add("<option value=\"" + Const.INT_UNINITIALIZED + "\" selected></option>");
        }
        for (Double timeZoneOption : options) {
            String utcFormatOption = StringHelper.toUtcFormat(timeZoneOption);
            result.add("<option value=\"" + formatAsString(timeZoneOption) + "\""
                       + (existingTimeZone == timeZoneOption ? " selected" : "") + ">" + "(" + utcFormatOption
                       + ") " + TimeHelper.getCitiesForTimeZone(Double.toString(timeZoneOption)) + "</option>");
        }
        return result;
    }
    
    public static List<ElementTag> getTimeZoneOptionsAsElementTags(double existingTimeZone) {
        List<Double> options = TimeHelper.getTimeZoneValues();
        ArrayList<ElementTag> result = new ArrayList<ElementTag>();
        if (existingTimeZone == Const.DOUBLE_UNINITIALIZED) {
            ElementTag option = createOption("", String.valueOf(Const.INT_UNINITIALIZED), false);
            result.add(option);
        }
        
        for (Double timeZoneOption : options) {
            String utcFormatOption = StringHelper.toUtcFormat(timeZoneOption);
            String textToDisplay = "(" + utcFormatOption
                                            + ") " + TimeHelper.getCitiesForTimeZone(Double.toString(timeZoneOption));
            boolean isExistingTimeZone = existingTimeZone == timeZoneOption;
            
            ElementTag option = createOption(textToDisplay,
                                             formatAsString(timeZoneOption), isExistingTimeZone);
            result.add(option);
        }
        return result;
    }
    
    /**
     * Returns an element tag representing a HTML option
     */
    public static ElementTag createOption(String text, String value, boolean isSelected) {
        if (isSelected) {
            return new ElementTag(text, "value", value, "selected", null);
        }
        return new ElementTag(text, "value", value);
    }
    
    /**
     * Returns an element tag representing a HTML option
     */
    public static ElementTag createOption(String text, String value) {
        return new ElementTag(text, "value", value);
    }
    
    /**
     * Returns the grace period options as HTML code.
     */
    public static List<ElementTag> getGracePeriodOptionsAsElementTags(int existingGracePeriod) {
        ArrayList<ElementTag> result = new ArrayList<ElementTag>();
        for (int i = 0; i <= 30; i += 5) {
            ElementTag option = createOption(i + " mins", String.valueOf(i),
                                            isGracePeriodToBeSelected(existingGracePeriod, i));
            result.add(option);
        }
        return result;
    }
    
    /**
     * Returns the time options as HTML code.
     * By default the selected one is the last one.
     * @param timeToShowAsSelected
     */
    public static ArrayList<ElementTag> getTimeOptionsAsElementTags(Date timeToShowAsSelected) {
        ArrayList<ElementTag> result = new ArrayList<ElementTag>();
        for (int i = 1; i <= 24; i++) {
            ElementTag option = createOption(String.format("%04dH", i * 100 - (i == 24 ? 41 : 0)),
                                             String.valueOf(i), isTimeToBeSelected(timeToShowAsSelected, i));
            result.add(option);
        }
        return result;
    }
    
    /**
     * Returns the nationalities as HTML code.
     * @param existingNationality
     * @return
     */
    public static List<ElementTag> getNationalitiesAsElementTags(String existingNationality) {
        List<String> nationalities = getNationalities();
        ArrayList<ElementTag> result = new ArrayList<ElementTag>();

        for(String nationality : nationalities) {
            ElementTag option = createOption(nationality, nationality, 
                                             isNationalityToBeSelected(existingNationality, nationality));
            result.add(option);
        }

        return result;
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
        return student.getStudentStatus();
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
    
    public String getStudentCourseDetailsLink(String courseId) {
        String link = Const.ActionURIs.STUDENT_COURSE_DETAILS_PAGE;
        link = addUserIdToUrl(link);
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        return link;
    }
    
    public String getStudentFeedbackSubmissionEditLink(String courseId, String feedbackSessionName) {
        String link = Const.ActionURIs.STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        link = addUserIdToUrl(link);
        return link;
    }
    
    public String getStudentFeedbackResultsLink(String courseId, String feedbackSessionName) {
        String link = Const.ActionURIs.STUDENT_FEEDBACK_RESULTS_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        link = addUserIdToUrl(link);
        return link;
    }
    
    public String getStudentProfilePictureLink(String studentEmail, String courseId) {
        String link = Const.ActionURIs.STUDENT_PROFILE_PICTURE;
        link = Url.addParamToUrl(link, Const.ParamsNames.STUDENT_EMAIL, studentEmail);
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = addUserIdToUrl(link);
        return link;
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
    
    public String getInstructorCoursesLink() {
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

    public String getInstructorCourseDetailsLink(String courseId) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = addUserIdToUrl(link);
        return link;
    }

    public String getInstructorCourseEditLink(String courseId) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = addUserIdToUrl(link);
        return link;
    }
    
    public String getInstructorFeedbackStatsLink(String courseId, String feedbackSessionName) {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_STATS_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        link = addUserIdToUrl(link);
        return link;
    }

    public String getInstructorCourseStatsLink(String courseId) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_STATS_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = addUserIdToUrl(link);
        return link;
    }
    
    public String getInstructorFeedbackEditCopyLink() {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_COPY_PAGE;
        link = addUserIdToUrl(link);
        return link;
    }
    
    public String getInstructorQuestionCopyPageLink() {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_QUESTION_COPY_PAGE;
        return addUserIdToUrl(link);
    }
    
    /**
     * Retrieves the link to submit the request for copy of session.
     * Appends the return url to the link.
     * @param returnUrl the url to return to after submitting the request
     * @return submit link with return url appended to it
     */
    public String getInstructorFeedbackEditCopyActionLink(String returnUrl) {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_COPY;
        link = Url.addParamToUrl(link, Const.ParamsNames.NEXT_URL, returnUrl);
        
        return link;
    }
    
    /**
     * @param courseId
     * @param isHome True if the Browser should redirect to the Home page after the operation.
     */
    public String getInstructorCourseDeleteLink(String courseId, boolean isHome) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_DELETE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link,
                                 Const.ParamsNames.NEXT_URL,
                                 isHome ? Const.ActionURIs.INSTRUCTOR_HOME_PAGE
                                        : Const.ActionURIs.INSTRUCTOR_COURSES_PAGE);
        link = addUserIdToUrl(link);
        return link;
    }
    
    public String getInstructorCourseArchiveLink(String courseId, boolean archiveStatus, boolean isHome) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_ARCHIVE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ARCHIVE_STATUS, Boolean.toString(archiveStatus));
        link = Url.addParamToUrl(link,
                                 Const.ParamsNames.NEXT_URL,
                                 isHome ? Const.ActionURIs.INSTRUCTOR_HOME_PAGE
                                        : Const.ActionURIs.INSTRUCTOR_COURSES_PAGE);
        link = addUserIdToUrl(link);
        return link;
    }
    
    public String getInstructorFeedbacksLink() {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE;
        link = addUserIdToUrl(link);
        return link;
    }
    
    public String getInstructorFeedbacksLink(String courseId) {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE;
        link = addUserIdToUrl(link);
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        return link;
    }
    
    public String getInstructorStudentCommentClearPendingLink(String courseId) {
        String link = Const.ActionURIs.INSTRUCTOR_STUDENT_COMMENT_CLEAR_PENDING;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = addUserIdToUrl(link);
        return link;
    }
    
    /**
     * Retrieves the link to submit request to delete the session.
     * @param courseId course ID
     * @param feedbackSessionName the session name
     * @param returnUrl the url of the page to return to after the delete
     * @return the link to submit request to delete the session with return page link
     */
    public String getInstructorFeedbackDeleteLink(String courseId, String feedbackSessionName, String returnUrl) {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_DELETE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        link = Url.addParamToUrl(link, Const.ParamsNames.NEXT_URL, returnUrl);
        link = addUserIdToUrl(link);
        
        return link;
    }
    
    public String getInstructorFeedbackEditLink(String courseId, String feedbackSessionName) {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_EDIT_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        link = addUserIdToUrl(link);
        return link;
    }
    
    public String getInstructorFeedbackSubmissionEditLink(String courseId, String feedbackSessionName) {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        link = addUserIdToUrl(link);
        return link;
    }
    
    public String getInstructorFeedbackResultsLink(String courseId, String feedbackSessionName) {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_RESULTS_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        link = addUserIdToUrl(link);
        return link;
    }
    
    /**
     * Retrieves the link to submit the request for remind student
     * Appends the return url to the link.
     * @param courseId the course ID
     * @param feedbackSessionName the name of the feedback session
     * @param returnUrl the url to return to after submitting the request
     * @return submit link with return url appended to it
     */
    public String getInstructorFeedbackRemindLink(String courseId, String feedbackSessionName, String returnUrl) {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_REMIND;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        link = Url.addParamToUrl(link, Const.ParamsNames.NEXT_URL, returnUrl);
        link = addUserIdToUrl(link);
        
        return link;
    }
    
    /**
     * Retrieves the link to load remind modal
     * @param courseId the course ID
     * @param feedbackSessionName the name of the feedback session
     * @return the link to load remind modal
     */
    public String getInstructorFeedbackRemindParticularStudentsPageLink(String courseId, String feedbackSessionName) {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_REMIND_PARTICULAR_STUDENTS_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        link = addUserIdToUrl(link);
        return link;
    }
    
    /**
     * Retrieves the link to submit the request to remind a particular student(s)
     * @param returnUrl the url to return to after submitting the request
     * @return submit link with return url appended to it
     */
    public String getInstructorFeedbackRemindParticularStudentsLink(String returnUrl) {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_REMIND_PARTICULAR_STUDENTS;
        link = Url.addParamToUrl(link, Const.ParamsNames.NEXT_URL, returnUrl);
        
        return link;
    }

    public String getInstructorFeedbackPublishLink(String courseId, String feedbackSessionName, String returnUrl) {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_PUBLISH;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        link = Url.addParamToUrl(link, Const.ParamsNames.NEXT_URL, returnUrl);
        link = addUserIdToUrl(link);
        
        return link;
    }

    public String getInstructorFeedbackUnpublishLink(String courseId, String feedbackSessionName, String returnUrl) {
        String link = Const.ActionURIs.INSTRUCTOR_FEEDBACK_UNPUBLISH;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        link = Url.addParamToUrl(link, Const.ParamsNames.NEXT_URL, returnUrl);
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

    public String getInstructorCourseRemindLink(String courseId) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_REMIND;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = addUserIdToUrl(link);
        return link;
    }
    
    public String getInstructorCourseStudentDetailsLink(String courseId, String studentEmail) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.STUDENT_EMAIL, studentEmail);
        link = addUserIdToUrl(link);
        return link;
    }
    
    public String getInstructorCourseStudentDetailsLink(String courseId, String studentEmail, String showCommentBox) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.STUDENT_EMAIL, studentEmail);
        link = addUserIdToUrl(link);
        link = Url.addParamToUrl(link, Const.ParamsNames.SHOW_COMMENT_BOX, showCommentBox);
        return link;
    }
    
    public String getInstructorCourseStudentDetailsEditLink(String courseId, String studentEmail) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.STUDENT_EMAIL, studentEmail);
        link = addUserIdToUrl(link);
        return link;
    }
    
    public String getInstructorCourseRemindStudentLink(String courseId, String studentEmail) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_REMIND;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.STUDENT_EMAIL, studentEmail);
        link = addUserIdToUrl(link);
        return link;
    }
    
    // TODO: create another delete action which redirects to studentListPage?
    public String getInstructorCourseStudentDeleteLink(String courseId, String studentEmail) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DELETE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.STUDENT_EMAIL, studentEmail);
        link = addUserIdToUrl(link);
        return link;
    }
    
    public String getInstructorCourseInstructorDeleteLink(String courseId, String instructorEmail) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_INSTRUCTOR_DELETE;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.INSTRUCTOR_EMAIL, instructorEmail);
        link = addUserIdToUrl(link);
        return link;
    }
    
    public String getInstructorCourseRemindInstructorLink(String courseId, String instructorEmail) {
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_REMIND;
        link = Url.addParamToUrl(link, Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link, Const.ParamsNames.INSTRUCTOR_EMAIL, instructorEmail);
        link = addUserIdToUrl(link);
        return link;
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
        
        StringBuilder msg = new StringBuilder(50);
        msg.append("The feedback session has been created");
        
        if (session.isVisible()) {
            msg.append(Const.Tooltips.FEEDBACK_SESSION_STATUS_VISIBLE);
        }
        
        if (session.isOpened()) {
            msg.append(Const.Tooltips.FEEDBACK_SESSION_STATUS_OPEN);
        } else if (session.isWaitingToOpen()) {
            msg.append(Const.Tooltips.FEEDBACK_SESSION_STATUS_AWAITING);
        } else if (session.isClosed()) {
            msg.append(Const.Tooltips.FEEDBACK_SESSION_STATUS_CLOSED);
        }
        
        if (session.isPublished()) {
            msg.append(Const.Tooltips.FEEDBACK_SESSION_STATUS_PUBLISHED);
        }
        
        msg.append('.');
        
        return msg.toString();
    }
    
    /**
     * Returns the links of actions available for a specific session
     * @param session
     *         The feedback session details
     * @param returnUrl
     *         The return URL after performing the action.
     * @param instructor
     *         The Instructor details
     * @param sectionsInCourse
     *         The list of sections for the course
     * @return
     * @throws EntityDoesNotExistException
     */
    public InstructorFeedbackSessionActions getInstructorFeedbackSessionActions(FeedbackSessionAttributes session,
                                                                                String returnUrl,
                                                                                InstructorAttributes instructor) {
        return new InstructorFeedbackSessionActions(this, session, returnUrl, instructor);
    }

    /**
     * Returns the type of people that can view the comment.
     */
    public String getTypeOfPeopleCanViewComment(CommentAttributes comment) {
        StringBuilder peopleCanView = new StringBuilder(100);
        for (int i = 0; i < comment.showCommentTo.size(); i++) {
            CommentParticipantType commentViewer = comment.showCommentTo.get(i);
            if (i == comment.showCommentTo.size() - 1 && comment.showCommentTo.size() > 1) {
                peopleCanView.append("and ");
            }
            
            switch (commentViewer) {
            case PERSON:
                peopleCanView.append("recipient, ");
                break;
            case TEAM:
                if (comment.recipientType == CommentParticipantType.TEAM) {
                    peopleCanView.append("recipient team, ");
                } else {
                    peopleCanView.append("recipient's team, ");
                }
                break;
            case SECTION:
                if (comment.recipientType == CommentParticipantType.SECTION) {
                    peopleCanView.append("recipient section, ");
                } else {
                    peopleCanView.append("recipient's section, ");
                }
                break;
            case COURSE:
                if (comment.recipientType == CommentParticipantType.COURSE) {
                    peopleCanView.append("the whole class, ");
                } else {
                    peopleCanView.append("other students in this course, ");
                }
                break;
            case INSTRUCTOR:
                peopleCanView.append("instructors, ");
                break;
            default:
                break;
            }
        }
        String peopleCanViewString = peopleCanView.toString();
        if (peopleCanViewString.isEmpty()) {
            return peopleCanViewString;
        }
        return removeEndComma(peopleCanViewString);
    }
    
    /**
     * Returns the type of people that can view the response comment.
     */
    public String getTypeOfPeopleCanViewComment(FeedbackResponseCommentAttributes comment,
                                                FeedbackQuestionAttributes relatedQuestion) {
        StringBuilder peopleCanView = new StringBuilder(100);
        List<FeedbackParticipantType> showCommentTo;
        if (comment.isVisibilityFollowingFeedbackQuestion) {
            showCommentTo = relatedQuestion.showResponsesTo;
        } else {
            showCommentTo = comment.showCommentTo;
        }
        for (int i = 0; i < showCommentTo.size(); i++) {
            FeedbackParticipantType commentViewer = showCommentTo.get(i);
            if (i == showCommentTo.size() - 1 && showCommentTo.size() > 1) {
                peopleCanView.append("and ");
            }
            
            switch (commentViewer) {
            case GIVER:
                peopleCanView.append("response giver, ");
                break;
            case RECEIVER:
                peopleCanView.append("response recipient, ");
                break;
            case OWN_TEAM:
                peopleCanView.append("response giver's team, ");
                break;
            case RECEIVER_TEAM_MEMBERS:
                peopleCanView.append("response recipient's team, ");
                break;
            case STUDENTS:
                peopleCanView.append("other students in this course, ");
                break;
            case INSTRUCTORS:
                peopleCanView.append("instructors, ");
                break;
            default:
                break;
            }
        }
        String peopleCanViewString = peopleCanView.toString();
        return removeEndComma(peopleCanViewString);
    }
    
    public String removeEndComma(String str) {
        return str.substring(0, str.length() - 2);
    }

    private static boolean isTimeToBeSelected(Date timeToShowAsSelected, int hourOfTheOption) {
        boolean isEditingExistingFeedbackSession = timeToShowAsSelected != null;
        if (isEditingExistingFeedbackSession) {
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
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

    private static boolean isGracePeriodToBeSelected(int existingGracePeriodValue, int gracePeriodOptionValue) {
        boolean isEditingExistingEvaluation = existingGracePeriodValue != Const.INT_UNINITIALIZED;
        if (isEditingExistingEvaluation) {
            return gracePeriodOptionValue == existingGracePeriodValue;
        }
        int defaultGracePeriod = 15;
        return gracePeriodOptionValue == defaultGracePeriod;
    }

    /**
     * Decides whether there is a selected nationality or not.
     * @param existingNationality   The selected nationality.
     * @param nationality           Nationality from the drop-down list.
     * @return                      True if the existingNationality and the nationality are equal.
     */
    private static boolean isNationalityToBeSelected(String existingNationality, String nationality) {
        boolean isEditingExistingNationality = existingNationality != null;
        if(isEditingExistingNationality) {
            return nationality.equals(existingNationality);
        }
        return false;
    }

    private static String formatAsString(double num) {
        if ((int) num == num) {
            return Integer.toString((int) num);
        }
        return Double.toString(num);
    }
    
    public boolean isResponseCommentVisibleTo(FeedbackQuestionAttributes qn,
                                              FeedbackParticipantType viewerType) {
        if (viewerType == FeedbackParticipantType.GIVER) {
            return true;
        }
        return qn.isResponseVisibleTo(viewerType);
    }
    
    public boolean isResponseCommentGiverNameVisibleTo(FeedbackQuestionAttributes qn,
                                                       FeedbackParticipantType viewerType) {
        return true;
    }
    
    public String getResponseCommentVisibilityString(FeedbackQuestionAttributes qn) {
        String visibilityString = StringHelper.removeEnclosingSquareBrackets(qn.showResponsesTo.toString());
        return StringHelper.isWhiteSpace(visibilityString) ? "GIVER" : "GIVER, " + visibilityString;
    }
    
    public String getResponseCommentVisibilityString(FeedbackResponseCommentAttributes frComment,
                                                     FeedbackQuestionAttributes qn) {
        if (frComment.isVisibilityFollowingFeedbackQuestion) {
            return getResponseCommentVisibilityString(qn);
        }
        return StringHelper.removeEnclosingSquareBrackets(frComment.showCommentTo.toString());
    }
    
    public String getResponseCommentGiverNameVisibilityString(FeedbackQuestionAttributes qn) {
        return getResponseCommentVisibilityString(qn);
    }
    
    public String getResponseCommentGiverNameVisibilityString(FeedbackResponseCommentAttributes frComment,
                                                              FeedbackQuestionAttributes qn) {
        if (frComment.isVisibilityFollowingFeedbackQuestion) {
            return getResponseCommentGiverNameVisibilityString(qn);
        }
        return StringHelper.removeEnclosingSquareBrackets(frComment.showGiverNameTo.toString());
    }
    
    public String getPictureUrl(String pictureKey) {
        if (pictureKey == null || pictureKey.isEmpty()) {
            return Const.SystemParams.DEFAULT_PROFILE_PICTURE_PATH;
        }
        return Const.ActionURIs.STUDENT_PROFILE_PICTURE + "?"
               + Const.ParamsNames.BLOB_KEY + "=" + pictureKey + "&"
               + Const.ParamsNames.USER_ID + "=" + account.googleId;
    }
    
    public String getRecipientNames(Set<String> recipients, String courseId, String studentEmail, CourseRoster roster) {
        StringBuilder namesStringBuilder = new StringBuilder();
        int i = 0;
        
        for (String recipient : recipients) {
            if (i == recipients.size() - 1 && recipients.size() > 1) {
                namesStringBuilder.append(" and ");
            } else if (i > 0 && i < recipients.size() - 1 && recipients.size() > 2) {
                namesStringBuilder.append(", ");
            }
            StudentAttributes student = roster.getStudentForEmail(recipient);
            if (recipient.equals(studentEmail)) {
                namesStringBuilder.append("you");
            } else if (courseId.equals(recipient)) {
                namesStringBuilder.append("all students in this course");
            } else if (student == null) {
                namesStringBuilder.append(recipient);
            } else {
                if (recipients.size() == 1) {
                    namesStringBuilder.append(student.name + " (" + student.team + ", " + student.email + ")");
                } else {
                    namesStringBuilder.append(student.name);
                }
            }
            i++;
        }
        return namesStringBuilder.toString();
    }
    
    /**
     * Sets the list of status messages.
     * @param statusMessagesToUser a list of status messages that is to be displayed to the user
     */
    public void setStatusMessagesToUser(List<StatusMessage> statusMessagesToUser) {
        this.statusMessagesToUser = statusMessagesToUser;
    }
    
    /**
     * Gets the list of status messages.
     * @return a list of status messages that is to be displayed to the user
     */
    public List<StatusMessage> getStatusMessagesToUser() {
        return statusMessagesToUser;
    }

    public String getjQueryFilePath() {
        return Const.SystemParams.getjQueryFilePath(Config.isDevServer());
    }

    public String getjQueryUiFilePath() {
        return Const.SystemParams.getjQueryUiFilePath(Config.isDevServer());
    }

}
