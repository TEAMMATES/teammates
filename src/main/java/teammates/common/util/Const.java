package teammates.common.util;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackParticipantType;

/**
 * Stores constants that are widely used across classes.
 * this class contains several nested classes, each containing a specific
 * category of constants.
 */
public final class Const {

    /*
     * This section holds constants that are defined as constants primarily
     * because they are repeated in many places.
     */

    public static final String USER_NOBODY_TEXT = "-";
    public static final String USER_UNKNOWN_TEXT = "Unknown user";
    public static final String TEAM_OF_EMAIL_OWNER = "'s Team";

    public static final String NONE_OF_THE_ABOVE = "None of the above";
    public static final String DELETION_DATE_NOT_APPLICABLE = "Not Applicable";

    public static final String INSTRUCTOR_FEEDBACK_SESSION_VISIBLE_TIME_CUSTOM = "custom";
    public static final String INSTRUCTOR_FEEDBACK_SESSION_VISIBLE_TIME_ATOPEN = "atopen";

    public static final String INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_CUSTOM = "custom";
    public static final String INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_ATVISIBLE = "atvisible";
    public static final String INSTRUCTOR_FEEDBACK_RESULTS_VISIBLE_TIME_LATER = "later";
    public static final String INSTRUCTOR_FEEDBACK_RESULTS_MISSING_RESPONSE = "No Response";

    public static final String STUDENT_COURSE_STATUS_YET_TO_JOIN = "Yet to join";
    public static final String STUDENT_COURSE_STATUS_JOINED = "Joined";

    public static final String USER_NAME_FOR_SELF = "Myself";
    public static final String USER_TEAM_FOR_INSTRUCTOR = "Instructors";
    public static final String NO_SPECIFIC_SECTION = "No specific section";

    public static final String DISPLAYED_NAME_FOR_ANONYMOUS_PARTICIPANT = "Anonymous";

    public static final String ACTION_RESULT_FAILURE = "Servlet Action Failure";

    public static final int SIZE_LIMIT_PER_ENROLLMENT = 100;
    public static final int INSTRUCTOR_VIEW_RESPONSE_LIMIT = 8000;

    public static final String DEFAULT_SECTION = "None";

    public static final ZoneId DEFAULT_TIME_ZONE = ZoneId.of("UTC");

    public static final Duration FEEDBACK_SESSIONS_SEARCH_WINDOW = Duration.ofDays(30);

    /*
     * These constants are used as variable values to mean that the variable
     * is in a 'special' state.
     */
    public static final int INT_UNINITIALIZED = -9999;

    public static final int MAX_POSSIBLE_RECIPIENTS = -100;

    public static final int POINTS_EQUAL_SHARE = 100;
    public static final int POINTS_NOT_SURE = -101;
    public static final int POINTS_NOT_SUBMITTED = -999;

    public static final int VISIBILITY_TABLE_GIVER = 0;
    public static final int VISIBILITY_TABLE_RECIPIENT = 1;

    public static final String GENERAL_QUESTION = "%GENERAL%";
    public static final String USER_IS_TEAM = "%TEAM%";
    public static final String USER_IS_NOBODY = "%NOBODY%";
    public static final String USER_IS_MISSING = "%MISSING%";

    public static final Instant TIME_REPRESENTS_FOLLOW_OPENING;
    public static final Instant TIME_REPRESENTS_FOLLOW_VISIBLE;
    public static final Instant TIME_REPRESENTS_LATER;
    public static final Instant TIME_REPRESENTS_NOW;
    public static final Instant TIME_REPRESENTS_DEFAULT_TIMESTAMP;

    static {
        TIME_REPRESENTS_FOLLOW_OPENING = TimeHelper.parseInstant("1970-12-31 12:00 AM +0000");
        TIME_REPRESENTS_FOLLOW_VISIBLE = TimeHelper.parseInstant("1970-06-22 12:00 AM +0000");
        TIME_REPRESENTS_LATER = TimeHelper.parseInstant("1970-01-01 12:00 AM +0000");
        TIME_REPRESENTS_NOW = TimeHelper.parseInstant("1970-02-14 12:00 AM +0000");
        TIME_REPRESENTS_DEFAULT_TIMESTAMP = TimeHelper.parseInstant("2011-01-01 12:00 AM +0000");
    }

    /*
     * Other Constants
     */

    private Const() {
        // Utility class containing constants
    }

    public static class SystemParams {

        public static final String ENCODING = "UTF8";
        public static final int NUMBER_OF_HOURS_BEFORE_CLOSING_ALERT = 24;

        /**
         * This is the limit after which TEAMMATES will send error message.
         *
         * <p>Must be within the range of int.
         */
        public static final int MAX_PROFILE_PIC_SIZE = 5000000;

        /** e.g. "2014-04-01 11:59 PM UTC" */
        public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd h:mm a Z";

        /** Number to trim the Google ID when displaying to the user. */
        public static final int USER_ID_MAX_DISPLAY_LENGTH = 23;

        /* Field sizes and error messages for invalid fields can be found
         * in the FieldValidator class.
         */

        public static final ZoneId ADMIN_TIME_ZONE = ZoneId.of("Asia/Singapore");

        public static final String DEFAULT_PROFILE_PICTURE_PATH = "/images/profile_picture_default.png";

    }

    public static class FeedbackQuestion {

        public static final Map<String, String> COMMON_VISIBILITY_OPTIONS;

        static {
            Map<String, String> visibilityOptionInit = new LinkedHashMap<>();

            visibilityOptionInit.put("ANONYMOUS_TO_RECIPIENT_AND_INSTRUCTORS",
                                     "Shown anonymously to recipient and instructors");
            visibilityOptionInit.put("ANONYMOUS_TO_RECIPIENT_VISIBLE_TO_INSTRUCTORS",
                                     "Shown anonymously to recipient, visible to instructors");
            visibilityOptionInit.put("ANONYMOUS_TO_RECIPIENT_AND_TEAM_VISIBLE_TO_INSTRUCTORS",
                                     "Shown anonymously to recipient and team members, visible to instructors");
            visibilityOptionInit.put("VISIBLE_TO_INSTRUCTORS_ONLY", "Visible to instructors only");
            visibilityOptionInit.put("VISIBLE_TO_RECIPIENT_AND_INSTRUCTORS", "Visible to recipient and instructors");

            COMMON_VISIBILITY_OPTIONS = Collections.unmodifiableMap(visibilityOptionInit);
        }

        public static final Map<FeedbackParticipantType, List<FeedbackParticipantType>>
                COMMON_FEEDBACK_PATHS;

        static {
            Map<FeedbackParticipantType, List<FeedbackParticipantType>> initializer = new LinkedHashMap<>();

            initializer.put(FeedbackParticipantType.SELF,
                    new ArrayList<>(
                            Arrays.asList(FeedbackParticipantType.NONE,
                                    FeedbackParticipantType.SELF,
                                    FeedbackParticipantType.INSTRUCTORS)));

            initializer.put(FeedbackParticipantType.STUDENTS,
                    new ArrayList<>(
                            Arrays.asList(FeedbackParticipantType.NONE,
                                    FeedbackParticipantType.SELF,
                                    FeedbackParticipantType.INSTRUCTORS,
                                    FeedbackParticipantType.OWN_TEAM_MEMBERS,
                                    FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF)));

            initializer.put(FeedbackParticipantType.INSTRUCTORS,
                    new ArrayList<>(
                            Arrays.asList(FeedbackParticipantType.NONE,
                                    FeedbackParticipantType.SELF,
                                    FeedbackParticipantType.INSTRUCTORS)));

            COMMON_FEEDBACK_PATHS = Collections.unmodifiableMap(initializer);
        }

        // Mcq
        public static final int MCQ_MIN_NUM_OF_CHOICES = 2;
        public static final String MCQ_ERROR_NOT_ENOUGH_CHOICES =
                "Too little choices for " + Const.FeedbackQuestionTypeNames.MCQ + ". Minimum number of options is: ";
        public static final String MCQ_ERROR_INVALID_OPTION =
                " is not a valid option for the " + Const.FeedbackQuestionTypeNames.MCQ + ".";
        public static final String MCQ_ERROR_INVALID_WEIGHT =
                "The weights for the choices of a " + Const.FeedbackQuestionTypeNames.MCQ
                + " must be valid non-negative numbers with precision up to 2 decimal places.";
        public static final String MCQ_ERROR_EMPTY_MCQ_OPTION = "The Mcq options cannot be empty";
        public static final String MCQ_ERROR_OTHER_CONTENT_NOT_PROVIDED = "No text provided for other option";
        public static final String MCQ_ERROR_DUPLICATE_MCQ_OPTION = "The Mcq options cannot be duplicate";

        // Msq
        public static final int MSQ_MIN_NUM_OF_CHOICES = 2;
        public static final String MSQ_ERROR_EMPTY_MSQ_OPTION = "The Msq options cannot be empty";
        public static final String MSQ_ERROR_OTHER_CONTENT_NOT_PROVIDED = "No text provided for other option";
        public static final String MSQ_ERROR_NONE_OF_THE_ABOVE_ANSWER = "No other choices are allowed with "
                + "None of the above option";
        public static final String MSQ_ERROR_NOT_ENOUGH_CHOICES =
                "Too little choices for " + Const.FeedbackQuestionTypeNames.MSQ + ". Minimum number of options is: ";
        public static final String MSQ_ERROR_INVALID_OPTION =
                " is not a valid option for the " + Const.FeedbackQuestionTypeNames.MSQ + ".";
        public static final String MSQ_ERROR_MAX_SELECTABLE_EXCEEDED_TOTAL =
                "Maximum selectable choices exceeds the total number of options for " + Const.FeedbackQuestionTypeNames.MSQ;
        public static final String MSQ_ERROR_NUM_SELECTED_MORE_THAN_MAXIMUM =
                "Number of choices selected is more than the maximum number ";
        public static final String MSQ_ERROR_MIN_SELECTABLE_MORE_THAN_NUM_CHOICES =
                "Minimum selectable choices exceeds number of options ";
        public static final String MSQ_ERROR_NUM_SELECTED_LESS_THAN_MINIMUM =
                "Number of choices selected is less than the minimum number ";
        public static final String MSQ_ERROR_MIN_SELECTABLE_EXCEEDED_MAX_SELECTABLE =
                "Minimum selectable choices exceeds maximum selectable choices for "
                + Const.FeedbackQuestionTypeNames.MSQ;
        public static final String MSQ_ERROR_MIN_FOR_MAX_SELECTABLE_CHOICES =
                "Maximum selectable choices for " + Const.FeedbackQuestionTypeNames.MSQ + " must be at least 2.";
        public static final String MSQ_ERROR_MIN_FOR_MIN_SELECTABLE_CHOICES =
                "Minimum selectable choices for " + Const.FeedbackQuestionTypeNames.MSQ + " must be at least 1.";
        public static final String MSQ_ERROR_INVALID_WEIGHT =
                "The weights for the choices of a " + Const.FeedbackQuestionTypeNames.MSQ
                + " must be valid numbers with precision up to 2 decimal places.";
        /**
         * Special answer of a MSQ question indicating 'None of the above'.
         */
        public static final String MSQ_ANSWER_NONE_OF_THE_ABOVE = "";
        public static final String MSQ_ERROR_DUPLICATE_MSQ_OPTION = "The Msq options cannot be duplicate";

        // Numscale
        public static final String NUMSCALE_ERROR_MIN_MAX =
                "Minimum value must be < maximum value for " + Const.FeedbackQuestionTypeNames.NUMSCALE + ".";
        public static final String NUMSCALE_ERROR_STEP =
                "Step value must be > 0 for " + Const.FeedbackQuestionTypeNames.NUMSCALE + ".";
        public static final String NUMSCALE_ERROR_OUT_OF_RANGE =
                " is out of the range for " + Const.FeedbackQuestionTypeNames.NUMSCALE + ".";

        // Contribution
        public static final String CONTRIB_ERROR_INVALID_OPTION =
                "Invalid option for the " + Const.FeedbackQuestionTypeNames.CONTRIB + ".";
        public static final String CONTRIB_ERROR_INVALID_FEEDBACK_PATH =
                Const.FeedbackQuestionTypeNames.CONTRIB + " must have "
                + FeedbackParticipantType.STUDENTS.toDisplayGiverName()
                + " and " + FeedbackParticipantType.OWN_TEAM_MEMBERS_INCLUDING_SELF.toDisplayRecipientName()
                + " as the feedback giver and recipient respectively."
                + " These values will be used instead.";
        public static final String CONTRIB_ERROR_INVALID_VISIBILITY_OPTIONS =
                Const.FeedbackQuestionTypeNames.CONTRIB + " must use one of the common visibility options. The \""
                + Const.FeedbackQuestion.COMMON_VISIBILITY_OPTIONS
                                        .get("ANONYMOUS_TO_RECIPIENT_AND_TEAM_VISIBLE_TO_INSTRUCTORS")
                + "\" option will be used instead.";

        // Constant sum
        public static final int CONST_SUM_MIN_NUM_OF_OPTIONS = 2;
        public static final int CONST_SUM_MIN_NUM_OF_POINTS = 1;
        public static final String CONST_SUM_ERROR_NOT_ENOUGH_OPTIONS =
                "Too little options for " + Const.FeedbackQuestionTypeNames.CONSTSUM_OPTION
                + ". Minimum number of options is: ";
        public static final String CONST_SUM_ERROR_DUPLICATE_OPTIONS = "Duplicate options are not allowed.";
        public static final String CONST_SUM_ERROR_NOT_ENOUGH_POINTS =
                "Too little points for " + Const.FeedbackQuestionTypeNames.CONSTSUM_RECIPIENT
                + ". Minimum number of points is: ";
        public static final String CONST_SUM_ERROR_MISMATCH =
                "Please distribute all the points for distribution questions. "
                + "To skip a distribution question, leave the boxes blank.";
        public static final String CONST_SUM_ERROR_NEGATIVE = "Points given must be 0 or more.";
        public static final String CONST_SUM_ERROR_UNIQUE = "Every option must be given a different number of points.";
        public static final String CONST_SUM_ERROR_SOME_UNIQUE =
                "At least some options must be given a different number of points.";
        public static final String CONST_SUM_ANSWER_OPTIONS_NOT_MATCH = "The answers are inconsistent with the options";
        public static final String CONST_SUM_ANSWER_RECIPIENT_NOT_MATCH = "The answer is inconsistent with the recipient";

        // Rubric
        public static final int RUBRIC_ANSWER_NOT_CHOSEN = -1;

        public static final int RUBRIC_MIN_NUM_OF_CHOICES = 2;
        public static final String RUBRIC_ERROR_NOT_ENOUGH_CHOICES =
                "Too little choices for " + Const.FeedbackQuestionTypeNames.RUBRIC + ". Minimum number of options is: ";
        public static final int RUBRIC_MIN_NUM_OF_SUB_QUESTIONS = 1;
        public static final String RUBRIC_ERROR_NOT_ENOUGH_SUB_QUESTIONS =
                "Too little sub-questions for " + Const.FeedbackQuestionTypeNames.RUBRIC + ". "
                + "Minimum number of sub-questions is: ";
        public static final String RUBRIC_ERROR_DESC_INVALID_SIZE =
                "Invalid number of descriptions for " + Const.FeedbackQuestionTypeNames.RUBRIC;
        public static final String RUBRIC_ERROR_EMPTY_SUB_QUESTION =
                "Sub-questions for " + Const.FeedbackQuestionTypeNames.RUBRIC + " cannot be empty.";
        public static final String RUBRIC_ERROR_INVALID_WEIGHT =
                "The weights for the choices of each Sub-question of a "
                + Const.FeedbackQuestionTypeNames.RUBRIC
                + " must be valid numbers with precision up to 2 decimal places.";

        public static final String RUBRIC_EMPTY_ANSWER = "Empty answer.";
        public static final String RUBRIC_INVALID_ANSWER = "The answer for the rubric question is not valid.";

        // Text Question
        public static final String TEXT_ERROR_INVALID_RECOMMENDED_LENGTH = "Recommended length must be 1 or greater";
    }

    public static class FeedbackQuestionTypeNames {
        public static final String TEXT = "Essay question";
        public static final String MCQ = "Multiple-choice (single answer) question";
        public static final String MSQ = "Multiple-choice (multiple answers) question";
        public static final String NUMSCALE = "Numerical-scale question";
        public static final String CONSTSUM_OPTION = "Distribute points (among options) question";
        public static final String CONSTSUM_RECIPIENT = "Distribute points (among recipients) question";
        public static final String RANK_OPTION = "Rank (options) question";
        public static final String RANK_RECIPIENT = "Rank (recipients) question";
        public static final String CONTRIB = "Team contribution question";
        public static final String RUBRIC = "Rubric question";
    }

    public static class InstructorPermissionRoleNames {
        public static final String INSTRUCTOR_PERMISSION_ROLE_COOWNER = "Co-owner";
        public static final String INSTRUCTOR_PERMISSION_ROLE_MANAGER = "Manager";
        public static final String INSTRUCTOR_PERMISSION_ROLE_OBSERVER = "Observer";
        public static final String INSTRUCTOR_PERMISSION_ROLE_TUTOR = "Tutor";
        public static final String INSTRUCTOR_PERMISSION_ROLE_CUSTOM = "Custom";
    }

    public static class ParamsNames {

        public static final String IS_IN_RECYCLE_BIN = "isinrecyclebin";

        public static final String IS_STUDENT_REJOINING = "isstudentrejoining";
        public static final String IS_INSTRUCTOR_REJOINING = "isinstructorrejoining";

        public static final String BLOB_KEY = "blob-key";
        public static final String SESSION_TOKEN = "token";

        public static final String COPIED_FEEDBACK_SESSION_NAME = "copiedfsname";
        public static final String COPIED_COURSES_ID = "copiedcoursesid";

        public static final String CSV_TO_HTML_TABLE_NEEDED = "csvtohtmltable";

        public static final String COURSE_ID = "courseid";
        public static final String COURSE_NAME = "coursename";
        public static final String COURSE_TIME_ZONE = "coursetimezone";
        public static final String COURSE_EDIT_MAIN_INDEX = "courseeditmainindex";
        public static final String COURSE_STATUS = "coursestatus";
        public static final String INSTRUCTOR_ID = "instructorid";
        public static final String INSTRUCTOR_EMAIL = "instructoremail";
        public static final String INSTRUCTOR_INSTITUTION = "instructorinstitution";
        public static final String INSTITUTION_MAC = "mac";
        public static final String STUDENTS_ENROLLMENT_INFO = "enrollstudents";

        public static final String INSTRUCTOR_IS_DISPLAYED_TO_STUDENT = "instructorisdisplayed";
        public static final String INSTRUCTOR_DISPLAY_NAME = "instructordisplayname";
        public static final String INSTRUCTOR_ROLE_NAME = "instructorrole";
        public static final String INSTRUCTOR_SECTION = "section";
        public static final String INSTRUCTOR_SECTION_GROUP = "sectiongroup";

        public static final String INSTRUCTOR_PERMISSION_MODIFY_COURSE = "canmodifycourse";
        public static final String INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR = "canmodifyinstructor";
        public static final String INSTRUCTOR_PERMISSION_MODIFY_SESSION = "canmodifysession";
        public static final String INSTRUCTOR_PERMISSION_MODIFY_STUDENT = "canmodifystudent";

        public static final String INSTRUCTOR_PERMISSION_VIEW_STUDENT_IN_SECTIONS = "canviewstudentinsection";
        public static final String INSTRUCTOR_PERMISSION_VIEW_SESSION_IN_SECTIONS = "canviewsessioninsection";
        public static final String INSTRUCTOR_PERMISSION_SUBMIT_SESSION_IN_SECTIONS = "cansubmitsessioninsection";
        public static final String INSTRUCTOR_PERMISSION_MODIFY_SESSION_COMMENT_IN_SECTIONS =
                "canmodifysessioncommentinsection";

        public static final String INSTRUCTOR_REMIND_STUDENT_IS_FROM = "pagenameremindstudentisfrom";

        public static final String COURSE_ARCHIVE_STATUS = "archive";

        public static final String ADMIN_SEARCH_KEY = "searchkey";

        public static final String LOCAL_DATE_TIME = "localdatetime";
        public static final String TIME_ZONE = "timezone";

        public static final String FEEDBACK_SESSION_NAME = "fsname";
        public static final String FEEDBACK_SESSION_INDEX = "fsindex";
        public static final String FEEDBACK_SESSION_STARTDATE = "startdate";
        public static final String FEEDBACK_SESSION_STARTTIME = "starttime";
        public static final String FEEDBACK_SESSION_ENDDATE = "enddate";
        public static final String FEEDBACK_SESSION_ENDTIME = "endtime";
        public static final String FEEDBACK_SESSION_VISIBLEDATE = "visibledate";
        public static final String FEEDBACK_SESSION_VISIBLETIME = "visibletime";
        public static final String FEEDBACK_SESSION_PUBLISHDATE = "publishdate";
        public static final String FEEDBACK_SESSION_PUBLISHTIME = "publishtime";
        public static final String FEEDBACK_SESSION_GRACEPERIOD = "graceperiod";
        public static final String FEEDBACK_SESSION_SESSIONVISIBLEBUTTON = "sessionVisibleFromButton";
        public static final String FEEDBACK_SESSION_RESULTSVISIBLEBUTTON = "resultsVisibleFromButton";
        public static final String FEEDBACK_SESSION_SENDREMINDEREMAIL = "sendreminderemail";
        public static final String FEEDBACK_SESSION_INSTRUCTIONS = "instructions";
        public static final String FEEDBACK_SESSION_MODERATED_PERSON = "moderatedperson";

        public static final String FEEDBACK_QUESTION_ID = "questionid";
        public static final String FEEDBACK_QUESTION_NUMBER = "questionnum";
        public static final String FEEDBACK_QUESTION_NUMBER_STATIC = "questionnum-static";
        public static final String FEEDBACK_QUESTION_TEXT = "questiontext";
        public static final String FEEDBACK_QUESTION_TEXT_RECOMMENDEDLENGTH = "recommendedlength";
        public static final String FEEDBACK_QUESTION_DESCRIPTION = "questiondescription";
        public static final String FEEDBACK_QUESTION_TYPE = "questiontype";
        public static final String FEEDBACK_QUESTION_NUMBEROFCHOICECREATED = "noofchoicecreated";
        public static final String FEEDBACK_QUESTION_MCQCHOICE = "mcqOption";
        public static final String FEEDBACK_QUESTION_MCQOTHEROPTION = "mcqOtherOption";
        public static final String FEEDBACK_QUESTION_MCQOTHEROPTIONFLAG = "mcqOtherOptionFlag";
        public static final String FEEDBACK_QUESTION_MCQ_ISOTHEROPTIONANSWER = "mcqIsOtherOptionAnswer";
        public static final String FEEDBACK_QUESTION_MCQ_HAS_WEIGHTS_ASSIGNED = "mcqHasAssignedWeights";
        public static final String FEEDBACK_QUESTION_MCQ_WEIGHT = "mcqWeight";
        public static final String FEEDBACK_QUESTION_MCQ_OTHER_WEIGHT = "mcqOtherWeight";
        public static final String FEEDBACK_QUESTION_MSQCHOICE = "msqOption";
        public static final String FEEDBACK_QUESTION_MSQOTHEROPTION = "msqOtherOption";
        public static final String FEEDBACK_QUESTION_MSQOTHEROPTIONFLAG = "msqOtherOptionFlag";
        public static final String FEEDBACK_QUESTION_MSQ_ISOTHEROPTIONANSWER = "msqIsOtherOptionAnswer";
        public static final String FEEDBACK_QUESTION_MSQ_MAX_SELECTABLE_CHOICES = "msqMaxSelectableChoices";
        public static final String FEEDBACK_QUESTION_MSQ_MIN_SELECTABLE_CHOICES = "msqMinSelectableChoices";
        public static final String FEEDBACK_QUESTION_MSQ_ENABLE_MAX_SELECTABLE_CHOICES = "msqEnableMaxSelectableChoices";
        public static final String FEEDBACK_QUESTION_MSQ_ENABLE_MIN_SELECTABLE_CHOICES = "msqEnableMinSelectableChoices";
        public static final String FEEDBACK_QUESTION_MSQ_HAS_WEIGHTS_ASSIGNED = "msqHasAssignedWeights";
        public static final String FEEDBACK_QUESTION_MSQ_WEIGHT = "msqWeight";
        public static final String FEEDBACK_QUESTION_MSQ_OTHER_WEIGHT = "msqOtherWeight";
        public static final String FEEDBACK_QUESTION_CONSTSUMOPTION = "constSumOption";
        public static final String FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS = "constSumToRecipients";
        public static final String FEEDBACK_QUESTION_CONSTSUMNUMOPTION = "constSumNumOption";
        // TODO: rename FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION to a more accurate name
        public static final String FEEDBACK_QUESTION_CONSTSUMPOINTSPEROPTION = "constSumPointsPerOption";
        public static final String FEEDBACK_QUESTION_CONSTSUMPOINTS = "constSumPoints";
        public static final String FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHOPTION = "constSumPointsForEachOption";
        public static final String FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHRECIPIENT = "constSumPointsForEachRecipient";
        public static final String FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEUNEVENLY = "constSumUnevenDistribution";
        public static final String FEEDBACK_QUESTION_CONSTSUMDISTRIBUTEPOINTSOPTIONS = "constSumDistributePointsOptions";
        public static final String FEEDBACK_QUESTION_CONSTSUMALLUNEVENDISTRIBUTION = "All options";
        public static final String FEEDBACK_QUESTION_CONSTSUMSOMEUNEVENDISTRIBUTION = "At least some options";
        public static final String FEEDBACK_QUESTION_CONSTSUMNOUNEVENDISTRIBUTION = "None";
        public static final String FEEDBACK_QUESTION_CONTRIBISNOTSUREALLOWED = "isNotSureAllowedCheck";
        public static final String FEEDBACK_QUESTION_MCQ_GENERATED_OPTIONS = "mcqGeneratedOptions";
        public static final String FEEDBACK_QUESTION_MSQ_GENERATED_OPTIONS = "msqGeneratedOptions";
        public static final String FEEDBACK_QUESTION_GIVERTYPE = "givertype";
        public static final String FEEDBACK_QUESTION_RECIPIENTTYPE = "recipienttype";
        public static final String FEEDBACK_QUESTION_NUMBEROFENTITIES = "numofrecipients";
        public static final String FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE = "numofrecipientstype";
        public static final String FEEDBACK_QUESTION_EDITTYPE = "questionedittype";
        public static final String FEEDBACK_QUESTION_SHOWRESPONSESTO = "showresponsesto";
        public static final String FEEDBACK_QUESTION_SHOWGIVERTO = "showgiverto";
        public static final String FEEDBACK_QUESTION_SHOWRECIPIENTTO = "showrecipientto";
        public static final String FEEDBACK_QUESTION_NUMSCALE_MIN = "numscalemin";
        public static final String FEEDBACK_QUESTION_NUMSCALE_MAX = "numscalemax";
        public static final String FEEDBACK_QUESTION_NUMSCALE_STEP = "numscalestep";
        public static final String FEEDBACK_QUESTION_RUBRIC_SUBQUESTION = "rubricSubQn";
        public static final String FEEDBACK_QUESTION_RUBRIC_CHOICE = "rubricChoice";
        public static final String FEEDBACK_QUESTION_RUBRIC_DESCRIPTION = "rubricDesc";
        public static final String FEEDBACK_QUESTION_RUBRIC_WEIGHTS_ASSIGNED = "rubricAssignWeights";
        public static final String FEEDBACK_QUESTION_RUBRIC_WEIGHT = "rubricWeight";
        public static final String FEEDBACK_QUESTION_RUBRIC_NUM_ROWS = "rubricNumRows";
        public static final String FEEDBACK_QUESTION_RUBRIC_NUM_COLS = "rubricNumCols";
        public static final String FEEDBACK_QUESTION_RUBRIC_MOVE_COL_LEFT = "rubric-move-col-left";
        public static final String FEEDBACK_QUESTION_RUBRIC_MOVE_COL_RIGHT = "rubric-move-col-right";
        public static final String FEEDBACK_QUESTION_RANKOPTION = "rankOption";
        public static final String FEEDBACK_QUESTION_RANKTORECIPIENTS = "rankToRecipients";
        public static final String FEEDBACK_QUESTION_RANKNUMOPTIONS = "rankNumOptions";
        public static final String FEEDBACK_QUESTION_RANKISDUPLICATESALLOWED = "rankAreDuplicatesAllowed";
        public static final String FEEDBACK_QUESTION_RANK_IS_MIN_OPTIONS_TO_BE_RANKED_ENABLED =
                "minOptionsToBeRankedEnabled";
        public static final String FEEDBACK_QUESTION_RANK_IS_MAX_OPTIONS_TO_BE_RANKED_ENABLED =
                "maxOptionsToBeRankedEnabled";
        public static final String FEEDBACK_QUESTION_RANK_MIN_OPTIONS_TO_BE_RANKED = "minOptionsToBeRanked";
        public static final String FEEDBACK_QUESTION_RANK_MAX_OPTIONS_TO_BE_RANKED = "maxOptionsToBeRanked";
        public static final String FEEDBACK_QUESTION_RANK_IS_MIN_RECIPIENTS_TO_BE_RANKED_ENABLED =
                "minRecipientsToBeRankedEnabled";
        public static final String FEEDBACK_QUESTION_RANK_IS_MAX_RECIPIENTS_TO_BE_RANKED_ENABLED =
                "maxRecipientsToBeRankedEnabled";
        public static final String FEEDBACK_QUESTION_RANK_MIN_RECIPIENTS_TO_BE_RANKED = "minRecipientsToBeRanked";
        public static final String FEEDBACK_QUESTION_RANK_MAX_RECIPIENTS_TO_BE_RANKED = "maxRecipientsToBeRanked";

        public static final String FEEDBACK_RESPONSE_ID = "responseid";
        public static final String FEEDBACK_RESPONSE_RECIPIENT = "responserecipient";
        public static final String FEEDBACK_RESPONSE_TEXT = "responsetext";

        public static final String FEEDBACK_RESPONSE_COMMENT_ID = "responsecommentid";
        public static final String FEEDBACK_RESPONSE_COMMENT_TEXT = "responsecommenttext";
        public static final String FEEDBACK_RESPONSE_COMMENT_ADD_TEXT = "responsecommentaddtext";

        public static final String FEEDBACK_RESULTS_UPLOADDOWNLOADBUTTON = "fruploaddownloadbtn";
        public static final String FEEDBACK_RESULTS_SORTTYPE = "frsorttype";
        public static final String FEEDBACK_RESULTS_GROUPBYTEAM = "frgroupbyteam";
        public static final String FEEDBACK_RESULTS_GROUPBYSECTION = "frgroupbysection";
        public static final String FEEDBACK_RESULTS_GROUPBYSECTIONDETAIL = "frgroupbysectiondetail";
        public static final String FEEDBACK_RESULTS_SHOWSTATS = "frshowstats";
        public static final String FEEDBACK_RESULTS_INDICATE_MISSING_RESPONSES = "frindicatemissingresponses";
        public static final String FEEDBACK_RESULTS_NEED_AJAX = "frneedajax";
        public static final String FEEDBACK_RESULTS_MAIN_INDEX = "frmainindex";

        public static final String PREVIEWAS = "previewas";

        public static final String STUDENT_ID = "googleid";
        public static final String INVITER_ID = "invitergoogleid";

        public static final String REGKEY = "key";
        public static final String STUDENT_EMAIL = "studentemail";

        public static final String STUDENT_SHORT_NAME = "studentshortname";
        public static final String STUDENT_PROFILE_EMAIL = "studentprofileemail";
        public static final String STUDENT_PROFILE_INSTITUTION = "studentprofileinstitute";
        public static final String STUDENT_NATIONALITY = "studentnationality";
        public static final String STUDENT_GENDER = "studentgender";
        public static final String STUDENT_PROFILE_MOREINFO = "studentprofilemoreinfo";

        public static final String STUDENT_NAME = "studentname";
        public static final String RESPONSE_COMMENTS_SHOWCOMMENTSTO = "showresponsecommentsto";
        public static final String RESPONSE_COMMENTS_SHOWGIVERTO = "showresponsegiverto";
        public static final String SECTION_NAME = "sectionname";
        public static final String SECTION_NAME_DETAIL = "sectionnamedetail";

        public static final String TEAM_NAME = "teamname";
        public static final String TEAMMATES = "teammates";

        public static final String STATUS_MESSAGES_LIST = "statusMessagesToUser";
        public static final String ERROR = "error";
        public static final String NEXT_URL = "next";
        public static final String USER_ID = "user";
        public static final String HINT = "hint";

        public static final String SEND_SUBMISSION_EMAIL = "sendsubmissionemail";

        public static final String SEARCH_KEY = "searchkey";

        public static final String RESPONDENT_EMAIL = "respondentemail";
        public static final String RESPONDENT_IS_INSTRUCTOR = "respondentisinstructor";
        public static final String RESPONDENT_IS_TO_BE_REMOVED = "respondentistoberemoved";

        public static final String SESSION_LINKS_RECOVERY_EMAIL = "sessionlinksrecoveryemail";
        public static final String USER_CAPTCHA_RESPONSE = "captcharesponse";

        public static final String EMAIL_TYPE = "emailtype";

        //Parameters for checking persistence of data during Eventual Consistency
        public static final String CHECK_PERSISTENCE_COURSE = "persistencecourse";

        public static final String ENTITY_TYPE = "entitytype";

        public static final String INTENT = "intent";
    }

    public static class SearchIndex {
        public static final String FEEDBACK_RESPONSE_COMMENT = "feedbackresponsecomment";
        public static final String STUDENT = "student";
        public static final String INSTRUCTOR = "instructor";
    }

    public static class SearchDocumentField {
        public static final String FEEDBACK_RESPONSE_COMMENT_GIVER_NAME = "frCommentGiverName";
        public static final String FEEDBACK_RESPONSE_GIVER_NAME = "feedbackResponseGiverName";
        public static final String FEEDBACK_RESPONSE_RECEIVER_NAME = "feedbackResponseReceiverName";
        public static final String SEARCHABLE_TEXT = "searchableText";
        public static final String COURSE_ID = "courseId";
    }

    /**
     * The course status respect to the instructor's point of view.
     * This parameter is used to get a course list for instructor.
     */
    public static class CourseStatus {
        public static final String ACTIVE = "active";
        public static final String ARCHIVED = "archived";
        public static final String SOFT_DELETED = "softDeleted";
    }

    public static class EntityType {

        public static final String STUDENT = "student";
        public static final String INSTRUCTOR = "instructor";
        public static final String ADMIN = "admin";

    }

    public static class CsrfConfig {

        public static final String TOKEN_HEADER_NAME = "X-CSRF-TOKEN";
        public static final String TOKEN_COOKIE_NAME = "CSRF-TOKEN";

    }

    public static class LegacyURIs {

        public static final String INSTRUCTOR_COURSE_JOIN = "/page/instructorCourseJoin";
        public static final String STUDENT_COURSE_JOIN = "/page/studentCourseJoin";
        public static final String STUDENT_COURSE_JOIN_NEW = "/page/studentCourseJoinAuthentication";
        public static final String INSTRUCTOR_HOME_PAGE = "/page/instructorHomePage";
        public static final String STUDENT_HOME_PAGE = "/page/studentHomePage";
        public static final String STUDENT_FEEDBACK_SUBMISSION_EDIT_PAGE = "/page/studentFeedbackSubmissionEditPage";
        public static final String STUDENT_FEEDBACK_RESULTS_PAGE = "/page/studentFeedbackResultsPage";
        public static final String INSTRUCTOR_FEEDBACK_SUBMISSION_EDIT_PAGE = "/page/instructorFeedbackSubmissionEditPage";
        public static final String INSTRUCTOR_FEEDBACK_RESULTS_PAGE = "/page/instructorFeedbackResultsPage";

    }

    public static class WebPageURIs {

        private static final String URI_PREFIX = "/web";

        private static final String STUDENT_PAGE = URI_PREFIX + "/" + EntityType.STUDENT;
        private static final String INSTRUCTOR_PAGE = URI_PREFIX + "/" + EntityType.INSTRUCTOR;
        private static final String ADMIN_PAGE = URI_PREFIX + "/" + EntityType.ADMIN;
        private static final String FRONT_PAGE = URI_PREFIX + "/front";
        public static final String JOIN_PAGE = URI_PREFIX + "/join";

        public static final String ADMIN_HOME_PAGE = ADMIN_PAGE + "/home";
        public static final String ADMIN_ACCOUNTS_PAGE = ADMIN_PAGE + "/accounts";
        public static final String ADMIN_SEARCH_PAGE = ADMIN_PAGE + "/search";
        public static final String ADMIN_SESSIONS_PAGE = ADMIN_PAGE + "/sessions";
        public static final String ADMIN_TIMEZONE_PAGE = ADMIN_PAGE + "/timezone";

        public static final String INSTRUCTOR_HOME_PAGE = INSTRUCTOR_PAGE + "/home";
        public static final String INSTRUCTOR_SEARCH_PAGE = INSTRUCTOR_PAGE + "/search";
        public static final String INSTRUCTOR_SESSIONS_PAGE = INSTRUCTOR_PAGE + "/sessions";
        public static final String INSTRUCTOR_SESSION_SUBMISSION_PAGE = INSTRUCTOR_PAGE + "/sessions/submission";
        public static final String INSTRUCTOR_SESSION_EDIT_PAGE = INSTRUCTOR_PAGE + "/sessions/edit";
        public static final String INSTRUCTOR_SESSION_RESULTS_PAGE = INSTRUCTOR_PAGE + "/sessions/result";
        public static final String INSTRUCTOR_COURSES_PAGE = INSTRUCTOR_PAGE + "/courses";
        public static final String INSTRUCTOR_COURSE_DETAILS_PAGE = INSTRUCTOR_PAGE + "/courses/details";
        public static final String INSTRUCTOR_COURSE_EDIT_PAGE = INSTRUCTOR_PAGE + "/courses/edit";
        public static final String INSTRUCTOR_COURSE_ENROLL_PAGE = INSTRUCTOR_PAGE + "/courses/enroll";
        public static final String INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE = INSTRUCTOR_PAGE + "/courses/student/details";
        public static final String INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT_PAGE = INSTRUCTOR_PAGE + "/courses/student/edit";
        public static final String INSTRUCTOR_STUDENT_LIST_PAGE = INSTRUCTOR_PAGE + "/students";
        public static final String INSTRUCTOR_STUDENT_RECORDS_PAGE = INSTRUCTOR_PAGE + "/students/records";

        public static final String STUDENT_HOME_PAGE = STUDENT_PAGE + "/home";
        public static final String STUDENT_COURSE_DETAILS_PAGE = STUDENT_PAGE + "/course";
        public static final String STUDENT_PROFILE_PAGE = STUDENT_PAGE + "/profile";
        public static final String STUDENT_SESSION_RESULTS_PAGE = STUDENT_PAGE + "/sessions/result";

        public static final String SESSION_RESULTS_PAGE = URI_PREFIX + "/sessions/result";
        public static final String SESSION_SUBMISSION_PAGE = URI_PREFIX + "/sessions/submission";
        public static final String SESSIONS_LINK_RECOVERY_PAGE = FRONT_PAGE + "/help/session-links-recovery";
        public static final String INSTRUCTOR_HELP_PAGE = FRONT_PAGE + "/help/instructor";
    }

    public static class ResourceURIs {

        public static final String URI_PREFIX = "/webapi";
        public static final String LOGOUT = "/logout";

        public static final String DATABUNDLE = URI_PREFIX + "/databundle";
        public static final String DATABUNDLE_DOCUMENTS = URI_PREFIX + "/databundle/documents";
        public static final String EXCEPTION = URI_PREFIX + "/exception";
        public static final String ERROR_REPORT = URI_PREFIX + "/errorreport";
        public static final String AUTH = URI_PREFIX + "/auth";
        public static final String AUTH_REGKEY = URI_PREFIX + "/auth/regkey";
        public static final String ACCOUNT = URI_PREFIX + "/account";
        public static final String ACCOUNT_RESET = URI_PREFIX + "/account/reset";
        public static final String ACCOUNT_DOWNGRADE = URI_PREFIX + "/account/downgrade";
        public static final String RESPONSE_COMMENT = URI_PREFIX + "/responsecomment";
        public static final String COURSE = URI_PREFIX + "/course";
        public static final String COURSE_ARCHIVE = URI_PREFIX + "/course/archive";
        public static final String BIN_COURSE = URI_PREFIX + "/bin/course";
        public static final String COURSE_SECTIONS = URI_PREFIX + "/course/sections";
        public static final String COURSES = URI_PREFIX + "/courses";
        public static final String INSTRUCTORS = URI_PREFIX + "/instructors";
        public static final String INSTRUCTOR = URI_PREFIX + "/instructor";
        public static final String INSTRUCTOR_PRIVILEGE = URI_PREFIX + "/instructor/privilege";
        public static final String RESULT = URI_PREFIX + "/result";
        public static final String STUDENTS = URI_PREFIX + "/students";
        public static final String STUDENT = URI_PREFIX + "/student";
        public static final String SESSIONS_ONGOING = URI_PREFIX + "/sessions/ongoing";
        public static final String SESSION = URI_PREFIX + "/session";
        public static final String SESSION_PUBLISH = URI_PREFIX + "/session/publish";
        public static final String SESSION_REMIND_SUBMISSION = URI_PREFIX + "/session/remind/submission";
        public static final String SESSION_REMIND_RESULT = URI_PREFIX + "/session/remind/result";
        public static final String SESSION_STATS = URI_PREFIX + "/session/stats";
        public static final String SESSION_SUBMITTED_GIVER_SET = URI_PREFIX + "/session/submitted/giverset";
        public static final String SESSIONS = URI_PREFIX + "/sessions";
        public static final String SEARCH_COMMENTS = URI_PREFIX + "/search/comments";
        public static final String SEARCH_INSTRUCTORS = URI_PREFIX + "/search/instructors";
        public static final String SEARCH_STUDENTS = URI_PREFIX + "/search/students";
        public static final String BIN_SESSION = URI_PREFIX + "/bin/session";
        public static final String QUESTIONS = URI_PREFIX + "/questions";
        public static final String QUESTION = URI_PREFIX + "/question";
        public static final String QUESTION_RECIPIENTS = URI_PREFIX + "/question/recipients";
        public static final String RESPONSES = URI_PREFIX + "/responses";
        public static final String HAS_RESPONSES = URI_PREFIX + "/hasResponses";
        public static final String SUBMISSION_CONFIRMATION = URI_PREFIX + "/submission/confirmation";
        public static final String JOIN = URI_PREFIX + "/join";
        public static final String JOIN_REMIND = URI_PREFIX + "/join/remind";
        public static final String TIMEZONE = URI_PREFIX + "/timezone";
        public static final String LOCAL_DATE_TIME = URI_PREFIX + "/localdatetime";
        public static final String SESSION_LINKS_RECOVERY = URI_PREFIX + "/sessionlinksrecovery";
        public static final String NATIONALITIES = URI_PREFIX + "/nationalities";
        public static final String EMAIL = URI_PREFIX + "/email";

        public static final String STUDENT_PROFILE_PICTURE = URI_PREFIX + "/student/profilePic";
        public static final String STUDENT_PROFILE = URI_PREFIX + "/student/profile";
        public static final String STUDENT_COURSE_LINKS_REGENERATION = URI_PREFIX + "/student/courselinks/regeneration";
    }

    public static class CronJobURIs {
        public static final String URI_PREFIX = "/auto";

        public static final String AUTOMATED_LOG_COMPILATION = URI_PREFIX + "/compileLogs";
        public static final String AUTOMATED_DATASTORE_BACKUP = URI_PREFIX + "/datastoreBackup";
        public static final String AUTOMATED_FEEDBACK_OPENING_REMINDERS =
                URI_PREFIX + "/feedbackSessionOpeningReminders";
        public static final String AUTOMATED_FEEDBACK_CLOSED_REMINDERS =
                URI_PREFIX + "/feedbackSessionClosedReminders";
        public static final String AUTOMATED_FEEDBACK_CLOSING_REMINDERS =
                URI_PREFIX + "/feedbackSessionClosingReminders";
        public static final String AUTOMATED_FEEDBACK_PUBLISHED_REMINDERS =
                URI_PREFIX + "/feedbackSessionPublishedReminders";
    }

    /**
     * Configurations for task queue.
     */
    public static class TaskQueue {
        public static final String URI_PREFIX = "/worker";

        public static final String FEEDBACK_SESSION_PUBLISHED_EMAIL_QUEUE_NAME =
                "feedback-session-published-email-queue";
        public static final String FEEDBACK_SESSION_PUBLISHED_EMAIL_WORKER_URL =
                URI_PREFIX + "/feedbackSessionPublishedEmail";

        public static final String FEEDBACK_SESSION_RESEND_PUBLISHED_EMAIL_QUEUE_NAME =
                "feedback-session-resend-published-email-queue";
        public static final String FEEDBACK_SESSION_RESEND_PUBLISHED_EMAIL_WORKER_URL =
                URI_PREFIX + "/feedbackSessionResendPublishedEmail";

        public static final String FEEDBACK_SESSION_REMIND_EMAIL_QUEUE_NAME = "feedback-session-remind-email-queue";
        public static final String FEEDBACK_SESSION_REMIND_EMAIL_WORKER_URL = URI_PREFIX + "/feedbackSessionRemindEmail";

        public static final String FEEDBACK_SESSION_REMIND_PARTICULAR_USERS_EMAIL_QUEUE_NAME =
                "feedback-session-remind-particular-users-email-queue";
        public static final String FEEDBACK_SESSION_REMIND_PARTICULAR_USERS_EMAIL_WORKER_URL =
                URI_PREFIX + "/feedbackSessionRemindParticularUsersEmail";

        public static final String FEEDBACK_SESSION_UNPUBLISHED_EMAIL_QUEUE_NAME =
                "feedback-session-unpublished-email-queue";
        public static final String FEEDBACK_SESSION_UNPUBLISHED_EMAIL_WORKER_URL =
                URI_PREFIX + "/feedbackSessionUnpublishedEmail";

        public static final String FEEDBACK_SESSION_UPDATE_RESPONDENT_QUEUE_NAME =
                "feedback-session-update-respondent-queue";
        public static final String FEEDBACK_SESSION_UPDATE_RESPONDENT_WORKER_URL =
                URI_PREFIX + "/feedbackSessionUpdateRespondent";

        public static final String INSTRUCTOR_COURSE_JOIN_EMAIL_QUEUE_NAME = "instructor-course-join-email-queue";
        public static final String INSTRUCTOR_COURSE_JOIN_EMAIL_WORKER_URL = URI_PREFIX + "/instructorCourseJoinEmail";

        public static final String SEND_EMAIL_QUEUE_NAME = "send-email-queue";
        public static final String SEND_EMAIL_WORKER_URL = URI_PREFIX + "/sendEmail";

        public static final String STUDENT_COURSE_JOIN_EMAIL_QUEUE_NAME = "student-course-join-email-queue";
        public static final String STUDENT_COURSE_JOIN_EMAIL_WORKER_URL = URI_PREFIX + "/studentCourseJoinEmail";

    }

    /* These are status messages that may be shown to the user */
    @Deprecated
    public static class StatusMessages {

        public static final String DUPLICATE_EMAIL_INFO = "Same email address as the student in line";

        public static final String COURSE_EDITED = "The course has been edited.";
        public static final String COURSE_ARCHIVED =
                "The course %s has been archived. It will not appear in the home page any more.";
        // TODO: Let undo process to be in the Course page for now.
        // Should implement to be able to undo the archiving from the home page later.
        public static final String COURSE_MOVED_TO_RECYCLE_BIN =
                "The course %s has been deleted. You can restore it from the deleted courses table below.";
        public static final String COURSE_EMPTY =
                "You do not seem to have any courses. Use the form above to create a course.";
        public static final String COURSE_REMINDER_SENT_TO = "An email has been sent to ";

        public static final String TEAM_INVALID_SECTION_EDIT =
                "The team \"%s\" is in multiple sections. "
                + "The team ID should be unique across the entire course "
                + "and a team cannot be spread across multiple sections.<br>";
        public static final String SECTION_QUOTA_EXCEED =
                "You are trying enroll more than 100 students in section \"%s\". "
                + "To avoid performance problems, please do not enroll more than 100 students in a single section.<br>";

        public static final String COURSE_INSTRUCTOR_ADDED = "The instructor %s has been added successfully. "
                + "An email containing how to 'join' this course will be sent to %s in a few minutes.";
        public static final String COURSE_INSTRUCTOR_EXISTS =
                "An instructor with the same email address already exists in the course.";
        public static final String COURSE_INSTRUCTOR_EDITED = "The changes to the instructor %s has been updated.";
        public static final String COURSE_INSTRUCTOR_DELETED = "The instructor has been deleted from the course.";
        public static final String COURSE_INSTRUCTOR_DELETE_NOT_ALLOWED =
                "The instructor you are trying to delete is the last instructor in the course. "
                + "Deleting the last instructor from the course is not allowed.";

        public static final String STUDENT_EVENTUAL_CONSISTENCY =
                "If the student was created during the last few minutes, "
                + "try again in a few more minutes as the student may still be being saved.";

        public static final String STUDENT_EDITED = "The student has been edited successfully.";
        public static final String STUDENT_NOT_FOUND_FOR_EDIT =
                "The student you tried to edit does not exist. " + STUDENT_EVENTUAL_CONSISTENCY;
        public static final String STUDENT_DELETED = "The student has been removed from the course";
        public static final String STUDENT_PROFILE_EDITED = "Your profile has been edited successfully";
        public static final String STUDENT_PROFILE_PICTURE_SAVED = "Your profile picture has been saved successfully";
        public static final String STUDENT_PROFILE_PIC_TOO_LARGE = "The uploaded profile picture was too large. "
                + "Please try again with a smaller picture.";
        public static final String STUDENT_EMAIL_TAKEN_MESSAGE =
                "Trying to update to an email that is already used by: %s/%s";

        public static final String FEEDBACK_SESSION_ADDED =
                "The feedback session has been added. "
                + "Click the \"Add New Question\" button below to begin adding questions for the feedback session.";
        public static final String FEEDBACK_SESSION_COPIED =
                "The feedback session has been copied. Please modify settings/questions as necessary.";
        public static final String FEEDBACK_SESSION_COPY_NONESELECTED =
                "You have not selected any course to copy the feedback session to";
        public static final String FEEDBACK_SESSION_COPY_ALREADYEXISTS =
                "A feedback session with the name \"%s\" already exists in the following course(s): %s.";
        public static final String FEEDBACK_SESSION_EDITED = "The feedback session has been updated.";
        public static final String FEEDBACK_SESSION_END_TIME_EARLIER_THAN_START_TIME =
                "The end time for this feedback session cannot be earlier than the start time.";
        public static final String FEEDBACK_SESSION_MOVED_TO_RECYCLE_BIN =
                "The feedback session has been deleted. You can restore it from the deleted sessions table below.";
        public static final String FEEDBACK_SESSION_RESTORED = "The feedback session has been restored.";
        public static final String FEEDBACK_SESSION_ALL_RESTORED = "All sessions have been restored.";
        public static final String FEEDBACK_SESSION_DOWNLOAD_FILE_SIZE_EXCEEDED = "This session has more responses than "
                + "that can be downloaded in one go. Please download responses for one question at a time instead. "
                + "To download responses for a specific question, click on the corresponding question number.";
        public static final String FEEDBACK_SESSION_PUBLISHED =
                "The feedback session has been published. "
                + "Please allow up to 1 hour for all the notification emails to be sent out.";
        public static final String FEEDBACK_SESSION_RESEND_EMAIL_EMPTY_RECIPIENT =
                "You have not selected any student to email.";
        public static final String FEEDBACK_SESSION_UNPUBLISHED = "The feedback session has been unpublished.";
        public static final String FEEDBACK_SESSION_REMINDERSSENT =
                "Reminder e-mails have been sent out to those students and instructors. "
                + "Please allow up to 1 hour for all the notification emails to be sent out.";
        public static final String FEEDBACK_SESSION_REMINDERSSESSIONNOTOPEN =
                "The feedback session is not open for submissions. "
                + "You cannot send reminders for a session that is not open.";
        public static final String FEEDBACK_SESSION_REMINDERSEMPTYRECIPIENT = "You have not selected any student to remind.";
        public static final String FEEDBACK_SESSION_EXISTS =
                "A feedback session by this name already exists under this course";

        public static final String FEEDBACK_QUESTION_ADDED = "The question has been added to this feedback session.";
        public static final String FEEDBACK_QUESTION_ADDED_MULTIPLE =
                "The questions have been added to this feedback session.";
        public static final String FEEDBACK_QUESTION_DUPLICATED = "The question has been duplicated below.";
        public static final String FEEDBACK_QUESTION_EDITED = "The changes to the question have been updated.";
        public static final String FEEDBACK_QUESTION_DELETED = "The question has been deleted.";
        public static final String FEEDBACK_QUESTION_NUMBEROFENTITIESINVALID =
                "Please enter the maximum number of recipients each respondents should give feedback to.";
        public static final String FEEDBACK_QUESTION_TEXTINVALID =
                "Please enter a valid question. The question text cannot be empty.";

        public static final String FEEDBACK_RESPONSES_SAVED = "All responses submitted successfully!";
        public static final String FEEDBACK_RESPONSES_MSQ_MIN_CHECK = "Minimum selectable choices for question %d is %d.";
        public static final String FEEDBACK_RESPONSES_MSQ_MAX_CHECK = "Maximum selectable choices for question %d is %d.";

        public static final String FEEDBACK_RESPONSE_COMMENT_EMPTY = "Comment cannot be empty";

        public static final String FEEDBACK_UNANSWERED_QUESTIONS = "Note that some questions are yet to be answered. "
                + "They are: ";

        public static final String FEEDBACK_RESULTS_SECTIONVIEWWARNING =
                "This session seems to have a large number of responses. "
                + "It is recommended to view the results one question/section at a time. "
                + "To view responses for a particular question, click on the question below. "
                + "To view response for a particular section, click the 'Edit View' button above and choose a section.";
        public static final String FEEDBACK_RESULTS_QUESTIONVIEWWARNING =
                "This session seems to have a large number of responses. "
                + "It is recommended to view the results for one question at a time. "
                + "To view responses for a particular question, click on the question below.";
        public static final String ENROLL_LINES_PROBLEM_DETAIL_PREFIX = "&bull;";

        public static final String INSTRUCTOR_REMOVED_FROM_COURSE = "The Instructor has been removed from the Course";

        public static final String INSTRUCTOR_SEARCH_NO_RESULTS = "No results found.";
        public static final String INSTRUCTOR_SEARCH_TIPS =
                "Search Tips:<br>"
                + "<ul>"
                    + "<li>Put more keywords to search for more precise results.</li>"
                    + "<li>Put quotation marks around words <b>\"[any word]\"</b>"
                            + " to search for an exact phrase in an exact order.</li>"
                + "</ul>";

        public static final String HINT_FOR_NO_SESSIONS_STUDENT =
                "Currently, there are no open feedback sessions in the course %s. "
                + "When a session is open for submission you will be notified.";

        // Messages that are templates only
        /** Template String. Parameters: Student's name, Course ID */
        public static final String STUDENT_COURSE_JOIN_SUCCESSFUL = "You have been successfully added to the course %s.";

        /** Template String. Parameters:  Course ID */
        public static final String STUDENT_PROFILE_NOT_A_PICTURE = "The file that you have uploaded is not a picture. "
                + "Please upload a picture (usually it ends with .jpg or .png)";
        public static final String STUDENT_PROFILE_NO_PICTURE_GIVEN = "Please specify a file to be uploaded.";
        public static final String STUDENT_NOT_FOUND_FOR_RECORDS =
                "The student you tried to view records for does not exist. " + STUDENT_EVENTUAL_CONSISTENCY;

        public static final String AMBIGUOUS_LOCAL_DATE_TIME_GAP =
                "The %s, %s, falls within the gap period when clocks spring forward at the start of DST. "
                        + "It was resolved to %s.";

        public static final String AMBIGUOUS_LOCAL_DATE_TIME_OVERLAP =
                "The %s, %s, falls within the overlap period when clocks fall back at the end of DST. "
                        + "It can refer to %s or %s. It was resolved to %s.";
    }

    /**
     * These are status messages related to students logic that may be shown to the user.
     */
    public static class StudentsLogicConst {
        /**
         * Error message when trying to create the same team in more than one section.
         */
        public static final String ERROR_INVALID_TEAM_NAME =
                "Team \"%s\" is detected in both Section \"%s\" and Section \"%s\".";

        /**
         * Error message to be appended to the ERROR_INVALID_TEAM_NAME message.
         */
        public static final String ERROR_INVALID_TEAM_NAME_INSTRUCTION =
                "Please use different team names in different sections.";

        /**
         * Error message when trying to enroll to a section that has maximum capacity.
         */
        public static final String ERROR_ENROLL_EXCEED_SECTION_LIMIT =
                "You are trying enroll more than %s students in section \"%s\".";

        /**
         * Error message to be appended to the ERROR_ENROLL_EXCEED_SECTION_LIMIT message.
         */
        public static final String ERROR_ENROLL_EXCEED_SECTION_LIMIT_INSTRUCTION =
                "To avoid performance problems, please do not enroll more than %s students in a single section.";

        /**
         * The maximum allowable number of students to be enrolled in a section.
         */
        public static final int SECTION_SIZE_LIMIT = 100;
    }

    /* These indicate status of an operation, but they are not shown to the user */
    public static class StatusCodes {

        // Backdoor responses
        public static final String BACKDOOR_STATUS_SUCCESS = "[BACKDOOR_STATUS_SUCCESS]";
        public static final String BACKDOOR_STATUS_FAILURE = "[BACKDOOR_STATUS_FAILURE]";

        // General Error codes
        public static final String NULL_PARAMETER = "ERRORCODE_NULL_PARAMETER";

        // Error message used across DB level
        public static final String DBLEVEL_NULL_INPUT = "Supplied parameter was null";

        // HTTP parameter null message
        public static final String NULL_HTTP_PARAMETER = "The [%s] HTTP parameter is null.";

        // body parameter null message
        public static final String NULL_BODY_PARAMETER = "The body parameter is null";
    }
    
    public static class TestCase {
    	public static final String EMPTY_STRING = "";
    	public static final String TYPICAL_CASE_EXPAND_AND_COLLAPSE_LINKS = "Typical case: Expand and collapse links";
    	public static final String TYPICAL_CASE_SEARCH_COMMON_COURSE_ID = "Typical case: Search common course id";
    	public static final String TYPICAL_CASE_RESET_INSTRUCTOR_GOOGLE_ID = "Typical case: Reset instructor google id";
    	public static final String TYPICAL_CASE_SEARCH_FOR_INSTRUCTOR_EMAIL = "Typical case: Search for instructor email";
    	public static final String TYPICAL_CASE_REGENERATE_ALL_LINKS_FOR_A_COURSE_STUDENT = "Typical case: Regenerate all links for a course student";
    	public static final String TYPICAL_CASE_RESET_STUDENT_GOOGLE_ID = "Typical case: Reset student google id";
    	public static final String TYPICAL_CASE_SEARCH_STUDENT_GOOGLE_ID = "Typical case: Search student google id";
    	public static final String INSTRUCTOR1_OF_COURSE_CONTENT_LABLE = "instructor1OfCourse1";
    	public static final String STUDENT1_IN_COURSE_CONTENT_LABLE = "student1InCourse1";
    	public static final String ADMIN_SEARCH_PAGE_E2E_TEST_JSON = "/AdminSearchPageE2ETest.json";
    	public static final String GET_KEY_FOR_INSTRUCTOR = "getKeyForInstructor";
    	public static final String GET_INSTRUCTOR = "getInstructor";
    	public static final String GET_FEEDBACK_SESSION = "getFeedbackSession";
    	public static final String GET_FEEDBACK_QUESTION = "getFeedbackQuestion";
    	public static final String GET_COURSE = "getCourse";
    	public static final String GET_ACCOUNT = "getAccount";
    	public static final String FRONT_SLASH = "/";
    	public static final String DOT = ".";
    	public static final String FILE = "file:///";
    	public static final String EDIT_RESPONSE = "edit response";
    	public static final String CHECK_PREVIOUS_RESPONSE = "check previous response";
    	public static final String SUBMIT_RESPONSE = "submit response";
    	public static final String AT_LEAST_SOME_OPTIONS = "At least some options";
    	public static final String EDITED_OPTION = "Edited option.";
    	public static final String EDIT_QUESTION = "edit question";
    	public static final String QN1_FOR_SECOND_SESSION = "qn1ForSecondSession";
    	public static final String COPY_QUESTION = "copy question";
    	public static final String ADD_NEW_QUESTION = "add new question";
    	public static final String QN1_FOR_FIRST_SESSION = "qn1ForFirstSession";
    	public static final String VERIFY_LOADED_QUESTION = "verify loaded question";
    	public static final String ALICE_TMMS_F_CONST_SUM_OPTION_QUESTION_E2E_T_CS2104 = "alice.tmms@FConstSumOptionQuestionE2eT.CS2104";
    	public static final String OPEN_SESSION = "openSession";
    	public static final String COURSE_CONTENT = "course";
    	public static final String COURSE_CONTENT2 = "course2";
    	public static final String INSTRUCTOR_CONTENT = "instructor";
    	public static final String INSTRUCTOR_CONTENT2 = "instructor2";
    	public static final String FEEDBACK_CONST_SUM_OPTION_QUESTION_E2E_TEST_JSON = "/FeedbackConstSumOptionQuestionE2ETest.json";
    	public static final String ALICE_TMMS_F_CONST_SUM_RECIPIENT_QUESTION_E2E_T_CS2104 = "alice.tmms@FConstSumRecipientQuestionE2eT.CS2104";
    	public static final String FEEDBACK_CONST_SUM_RECIPIENT_QUESTION_E2E_TEST_JSON = "/FeedbackConstSumRecipientQuestionE2ETest.json";
    	public static final String CHARLIE_TMMS_F_CONST_SUM_RECIPIENT_QUESTION_E2E_T_CS2104 = "charlie.tmms@FConstSumRecipientQuestionE2eT.CS2104";
    	public static final String BENNY_TMMS_F_CONST_SUM_RECIPIENT_QUESTION_E2E_T_CS2104 = "benny.tmms@FConstSumRecipientQuestionE2eT.CS2104";
    	public static final String CHARLIE_TMMS_F_CONTRIBUTION_QUESTION_E2E_T_CS2104 = "charlie.tmms@FContributionQuestionE2eT.CS2104";
    	public static final String BENNY_TMMS_F_CONTRIBUTION_QUESTION_E2E_T_CS2104 = "benny.tmms@FContributionQuestionE2eT.CS2104";
    	public static final String ALICE_TMMS_F_CONTRIBUTION_QUESTION_E2E_T_CS2104 = "alice.tmms@FContributionQuestionE2eT.CS2104";
    	public static final String FEEDBACK_CONTRIBUTION_QUESTION_E2E_TEST_JSON = "/FeedbackContributionQuestionE2ETest.json";
    	public static final String OPEN_BRACE = "(";
    	public static final String CLOSE_BRACE = ")";
    	public static final String SPACE = " ";
    	public static final String SPACE_OPEN_BRACE = " (";
    	public static final String THIS_IS_THE_EDITED_RESPONSE = "This is the edited response.";
    	public static final String UI = "UI";
    	public static final String VERIFY_QUESTION_WITH_GENERATED_OPTIONS = "verify question with generated options";
    	public static final String EDITED_CHOICE = "Edited choice";
    	public static final String ALICE_TMMS_F_MCQ_QUESTION_E2E_T_CS2104 = "alice.tmms@FMcqQuestionE2eT.CS2104";
    	public static final String FEEDBACK_MCQ_QUESTION_E2E_TEST_JSON = "/FeedbackMcqQuestionE2ETest.json";
    	public static final String LEADERSHIP = "Leadership";
    	public static final String THIS_IS_THE_OTHER_RESPONSE = "This is the other response.";
    	public static final String VERIFY_LOADED_QUESTION_WITH_GENERATED_OPTIONS = "verify loaded question with generated options";
    	public static final String BENNY_TMMS_F_MSQ_QUESTION_E2E_T_CS2104 = "benny.tmms@FMsqQuestionE2eT.CS2104";
    	public static final String ALICE_TMMS_F_MSQ_QUESTION_E2E_T_CS2104 = "alice.tmms@FMsqQuestionE2eT.CS2104";
    	public static final String FEEDBACK_MSQ_QUESTION_E2E_TEST_JSON = "/FeedbackMsqQuestionE2ETest.json";
    	public static final String BENNY_TMMS_F_NUM_SCALE_QUESTION_E2E_T_CS2104 = "benny.tmms@FNumScaleQuestionE2eT.CS2104";
    	public static final String ALICE_TMMS_F_NUM_SCALE_QUESTION_E2E_T_CS2104 = "alice.tmms@FNumScaleQuestionE2eT.CS2104";
    	public static final String FEEDBACK_NUM_SCALE_QUESTION_E2E_TEST_JSON = "/FeedbackNumScaleQuestionE2ETest.json";
    	public static final String BENNY_TMMS_F_RANK_OPTION_QUESTION_E2E_T_CS2104 = "benny.tmms@FRankOptionQuestionE2eT.CS2104";
    	public static final String ALICE_TMMS_F_RANK_OPTION_QUESTION_E2E_T_CS2104 = "alice.tmms@FRankOptionQuestionE2eT.CS2104";
    	public static final String FEEDBACK_RANK_OPTION_QUESTION_E2E_TEST_JSON = "/FeedbackRankOptionQuestionE2ETest.json";
    	public static final String ALICE_TMMS_F_RANK_RECIPIENT_QUESTION_E2E_T_CS2104 = "alice.tmms@FRankRecipientQuestionE2eT.CS2104";
    	public static final String FEEDBACK_RANK_RECIPIENT_QUESTION_E2E_TEST_JSON = "/FeedbackRankRecipientQuestionE2ETest.json";
    	public static final String BENNY_TMMS_F_RUBRIC_QUESTION_E2E_T_CS2104 = "benny.tmms@FRubricQuestionE2eT.CS2104";
    	public static final String TEST = "test";
    	public static final String ADDED_SUBQUESTION = "Added subquestion.";
    	public static final String EDITED_SUBQUESTION = "Edited subquestion.";
    	public static final String EDIT_DESCRIPTION_2 = "Edit description 2";
    	public static final String EDIT_DESCRIPTION_1 = "Edit description 1.";
    	public static final String EDIT_DESCRIPTION = "Edit description.";
    	public static final String EDITED_CHOICE_DOT = "Edited choice.";
    	public static final String ALICE_TMMS_F_RUBRIC_QUESTION_E2E_T_CS2104 = "alice.tmms@FRubricQuestionE2eT.CS2104";
    	public static final String FEEDBACK_RUBRIC_QUESTION_E2E_TEST_JSON = "/FeedbackRubricQuestionE2ETest.json";
    	public static final String ALGO = "Algo";
    	public static final String SUBMIT_MODERATED_RESPONSE = "submit moderated response";
    	public static final String MODERATEDQUESTION_ID = "moderatedquestionId";
    	public static final String MODERATEDPERSON = "moderatedperson";
    	public static final String MODERATING_INSTRUCTOR_CANNOT_SEE_QUESTIONS_WITHOUT_INSTRUCTOR_VISIBILITY = "moderating instructor cannot see questions without instructor visibility";
    	public static final String PREVIEW_AS_INSTRUCTOR = "preview as instructor";
    	public static final String PREVIEWAS = "previewas";
    	public static final String PREVIEW_AS_STUDENT = "preview as student";
    	public static final String YOUR_COMMENT_HAS_BEEN_DELETED = "Your comment has been deleted!";
    	public static final String DELETE_COMMENT = "delete comment";
    	public static final String P_EDITED_COMMENT_P = "<p>edited comment</p>";
    	public static final String EDIT_COMMENT = "edit comment";
    	public static final String P_NEW_COMMENT_P = "<p>new comment</p>";
    	public static final String ADD_COMMENT = "add comment";
    	public static final String RIGHT_SQUARE_PAREN = "]";
    	public static final String LEFT_AND_RIGHT_SQR_PAREN_FEEDBACK_SESSION = "][Feedback Session: ";
    	public static final String SF_SUBMIT_E2E_T_CS2104 = "SFSubmitE2eT.CS2104";
    	public static final String LEFT_SQUARE_PAREN_COURSE = " [Course: ";
    	public static final String TEAMMATES_FEEDBACK_RESPONSES_SUCCESSFULLY_RECORDED = "TEAMMATES: Feedback responses successfully recorded";
    	public static final String CONFIRMATION_EMAIL = "confirmation email";
    	public static final String INIT_CAP_TEAM_2 = "Team 2";
    	public static final String QN1_IN_GRACE_PERIOD_SESSION = "qn1InGracePeriodSession";
    	public static final String CAN_SUBMIT_IN_GRACE_PERIOD = "can submit in grace period";
    	public static final String CANNOT_SUBMIT_IN_CLOSED_SESSION = "cannot submit in closed session";
    	public static final String SUBMIT_PARTIAL_RESPONSE = "submit partial response";
    	public static final String INIT_CAP_TEAM = "Team";
    	public static final String VERIFY_RECIPIENTS_TEAMS = "verify recipients: teams";
    	public static final String INIT_CAP_STUDENT = "Student";
    	public static final String VERIFY_RECIPIENTS_TEAM_MATES = "verify recipients: team mates";
    	public static final String INIT_CAP_INSTRUCTOR = "Instructor";
    	public static final String VERIFY_RECIPIENTS_INSTRUCTORS = "verify recipients: instructors";
    	public static final String VERIFY_RECIPIENTS_STUDENTS = "verify recipients: students";
    	public static final String QN4_IN_SESSION1 = "qn4InSession1";
    	public static final String QN3_IN_SESSION1 = "qn3InSession1";
    	public static final String QN2_IN_SESSION1 = "qn2InSession1";
    	public static final String QN1_IN_SESSION1 = "qn1InSession1";
    	public static final String QUESTIONS_WITH_GIVER_TYPE_STUDENTS = "questions with giver type students";
    	public static final String QN5_IN_SESSION1 = "qn5InSession1";
    	public static final String QUESTIONS_WITH_GIVER_TYPE_INSTRUCTOR = "questions with giver type instructor";
    	public static final String VERIFY_LOADED_SESSION_DATA = "verify loaded session data";
    	public static final String CLOSED_SPACE_SESSION = "Closed Session";
    	public static final String OPEN_SPACE_SESSION = "Open Session";
    	public static final String SF_SUBMIT_E2E_T_INSTR = "SFSubmitE2eT.instr";
    	public static final String ALICE = "Alice";
    	public static final String GRACE_PERIOD_SESSION = "Grace Period Session";
    	public static final String FEEDBACK_SUBMIT_PAGE_E2E_TEST_JSON = "/FeedbackSubmitPageE2ETest.json";
    	public static final String P_STRONG_EDITED_RESPONSE_STRONG_P = "<p><strong>Edited response</strong></p>";
    	public static final String P_THIS_IS_THE_RESPONSE_FOR_QN_1_P = "<p>This is the response for qn 1</p>";
    	public static final String ALICE_TMMS_F_TEXT_QUESTION_E2E_T_CS2104 = "alice.tmms@FTextQuestionE2eT.CS2104";
    	public static final String FEEDBACK_TEXT_QUESTION_E2E_TEST_JSON = "/FeedbackTextQuestionE2ETest.json";
    	public static final String YOU_CAN_RESTORE_IT_FROM_THE_RECYCLE_BIN_MANUALLY = "You can restore it from the Recycle Bin manually.";
    	public static final String HAS_BEEN_DELETED_FULLSTOP = " has been deleted. ";
    	public static final String THE_COURSE = "The course ";
    	public static final String DELETE_COURSE = "delete course";
    	public static final String THE_COURSE_HAS_BEEN_EDITED = "The course has been edited.";
    	public static final String NEW_COURSE_NAME = "New Course Name";
    	public static final String EDIT_COURSE = "edit course";
    	public static final String INSTRUCTOR_IS_SUCCESSFULLY_DELETED = "Instructor is successfully deleted.";
    	public static final String DELETE_INSTRUCTOR = "delete instructor";
    	public static final String HAS_BEEN_UPDATED = " has been updated.";
    	public static final String THE_INSTRUCTOR_SPACE = "The instructor ";
    	public static final String FIRST_FEEDBACK_SESSION = "First feedback session";
    	public static final String SECTION_1 = "Section 1";
    	public static final String SECTION_2 = "Section 2";
    	public static final String INS_CRS_EDIT_EDITED_GMAIL_TMT = "InsCrsEdit.edited@gmail.tmt";
    	public static final String EDITED_NAME = "Edited Name";
    	public static final String EDIT_INSTRUCTOR = "edit instructor";
    	public static final String AN_EMAIL_HAS_BEEN_SENT_TO = "An email has been sent to ";
    	public static final String RESEND_INVITE = "resend invite";
    	public static final String IN_A_FEW_MINUTES = " in a few minutes.\"";
    	public static final String AN_EMAIL_CONTAINING_HOW_TO_JOIN_THIS_COURSE_WILL_BE_SENT_TO = "An email containing how to 'join' this course will be sent to ";
    	public static final String HAS_BEEN_ADDED_SUCCESSFULLY = " has been added successfully. ";
    	public static final String THE_INSTRUCTOR = "\"The instructor ";
    	public static final String TUTOR = "Tutor";
    	public static final String TEAMMATES_TEST = "Teammates Test";
    	public static final String INS_CRS_EDIT_TEST_GMAIL_TMT = "InsCrsEdit.test@gmail.tmt";
    	public static final String ADD_INSTRUCTOR = "add instructor";
    	public static final String VERIFY_LOADED_DATA = "verify loaded data";
    	public static final String VERIFY_CANNOT_EDIT_WITHOUT_PRIVILEGE = "verify cannot edit without privilege";
    	public static final String INS_CRS_EDIT_TUTOR = "InsCrsEdit.tutor";
    	public static final String INS_CRS_EDIT_COOWNER = "InsCrsEdit.coowner";
    	public static final String INS_CRS_EDIT_OBSERVER = "InsCrsEdit.observer";
    	public static final String INS_CRS_EDIT_MANAGER = "InsCrsEdit.manager";
    	public static final String INS_CRS_EDIT_HELPER = "InsCrsEdit.helper";
    	public static final String INS_CRS_EDIT_CS2104 = "InsCrsEdit.CS2104";
    	public static final String INSTRUCTOR_COURSE_EDIT_PAGE_E2E_TEST_JSON = "/InstructorCourseEditPageE2ETest.json";
    	public static final String SOME_STUDENTS_FAILED_TO_BE_ENROLLED_SEE_THE_SUMMARY_BELOW = "Some students failed to be enrolled, see the summary below.";
    	public static final String COMMENT_FOR_INVALID = "Comment for Invalid";
    	public static final String INVALID_EMAIL = "invalid.email";
    	public static final String INVALID_STUDENT = "Invalid Student";
    	public static final String COMMENT_FOR_DANNY = "Comment for Danny";
    	public static final String DANNY_E_TMMS_GMAIL_TMT = "danny.e.tmms@gmail.tmt";
    	public static final String DANNY_ENGRID = "Danny Engrid";
    	public static final String TEAM_3 = "Team 3";
    	public static final String ENROLL_AND_MODIFY_STUDENTS_IN_EXISTING_COURSE = "Enroll and modify students in existing course";
    	public static final String ENROLLMENT_SUCCESSFUL_SUMMARY_GIVEN_BELOW = "Enrollment successful. Summary given below.";
    	public static final String COMMENT_FOR_CHARLIE = "Comment for Charlie";
    	public static final String CHARLIE_D_TMMS_GMAIL_TMT = "charlie.d.tmms@gmail.tmt";
    	public static final String CHARLIE_DAVIS = "Charlie Davis";
    	public static final String TEAM_2 = "Team 2";
    	public static final String COMMENT_FOR_BENNY = "Comment for Benny";
    	public static final String BENNY_C_TMMS_GMAIL_TMT = "benny.c.tmms@gmail.tmt";
    	public static final String BENNY_CHARLES = "Benny Charles";
    	public static final String COMMENT_FOR_ALICE = "Comment for Alice";
    	public static final String ALICE_B_TMMS_GMAIL_TMT = "alice.b.tmms@gmail.tmt";
    	public static final String ALICE_BETSY = "Alice Betsy";
    	public static final String TEAM_1 = "Team 1";
    	public static final String ENROLL_STUDENTS_TO_EMPTY_COURSE = "Enroll students to empty course";
    	public static final String ADD_ROWS_TO_ENROLL_SPREADSHEET = "Add rows to enroll spreadsheet";
    	public static final String IC_ENROLL_E2E_T_CS2104 = "ICEnrollE2eT.CS2104";
    	public static final String IC_ENROLL_E2E_T_TEAMMATES_TEST = "ICEnrollE2eT.teammates.test";
    	public static final String INSTRUCTOR_COURSE_ENROLL_PAGE_E2E_TEST_JSON = "/InstructorCourseEnrollPageE2ETest.json";
    	public static final String ALREADY_JOINED_NO_CONFIRMATION_PAGE = "Already joined, no confirmation page";
    	public static final String ICJ_CONFIRMATION_E2E_T_CS1101 = "ICJConfirmationE2eT.CS1101";
    	public static final String CLICK_JOIN_LINK_VALID_KEY = "Click join link: valid key";
    	public static final String NO_INSTRUCTOR_WITH_GIVEN_REGISTRATION_KEY = "No instructor with given registration key: ";
    	public static final String INVALID_KEY = "invalidKey";
    	public static final String CLICK_JOIN_LINK_INVALID_KEY = "Click join link: invalid key";
    	public static final String ICJ_CONFIRMATION_E2E_T_INSTR2 = "ICJConfirmationE2eT.instr2";
    	public static final String ICJ_CONFIRMATION_E2E_T_INSTR_CS1101 = "ICJConfirmationE2eT.instr.CS1101";
    	public static final String INSTRUCTOR_COURSE_JOIN_CONFIRMATION_PAGE_E2E_TEST_JSON = "/InstructorCourseJoinConfirmationPageE2ETest.json";
    	public static final String ALL_COURSES_HAVE_BEEN_PERMANENTLY_DELETED = "All courses have been permanently deleted.";
    	public static final String PERMANENTLY_DELETE_ALL = "permanently delete all";
    	public static final String ALL_COURSES_HAVE_BEEN_RESTORED = "All courses have been restored.";
    	public static final String RESTORE_ALL = "restore all";
    	public static final String HAS_BEEN_PERMANENTLY_DELETED = " has been permanently deleted.";
    	public static final String PERMANENTLY_DELETE_COURSE = "permanently delete course";
    	public static final String RESTORE_ARCHIVED_COURSE = "restore archived course";
    	public static final String MOVE_ARCHIVED_COURSE_TO_RECYCLE_BIN = "move archived course to recycle bin";
    	public static final String HAS_BEEN_RESTORED = " has been restored.";
    	public static final String RESTORE_ACTIVE_COURSE = "restore active course";
    	public static final String HAS_BEEN_DELETED = " has been deleted. ";
    	public static final String MOVE_ACTIVE_COURSE_TO_RECYCLE_BIN = "move active course to recycle bin";
    	public static final String THE_COURSE_HAS_BEEN_UNARCHIVED = "The course has been unarchived.";
    	public static final String UNARCHIVE_COURSE = "unarchive course";
    	public static final String IT_WILL_NOT_APPEAR_ON_THE_HOME_PAGE_ANYMORE = "It will not appear on the home page anymore.";
    	public static final String HAS_BEEN_ARCHIVED = " has been archived. ";
    	public static final String ARCHIVE_COURSE = "archive course";
    	public static final String THE_COURSE_HAS_BEEN_ADDED = "The course has been added.";
    	public static final String ADD_NEW_COURSE = "add new course";
    	public static final String VERIFY_CANNOT_MODIFY_WITHOUT_PERMISSIONS = "verify cannot modify without permissions";
    	public static final String VERIFY_STATISTICS = "verify statistics";
    	public static final String ASIA_SINGAPORE = "Asia/Singapore";
    	public static final String NEW_COURSE = "New Course";
    	public static final String IC_ADD_E2E_TEST_CS4100 = "ICAddE2ETest.CS4100";
    	public static final String CS2105 = "CS2105";
    	public static final String CS2104 = "CS2104";
    	public static final String CS1101 = "CS1101";
    	public static final String INSTRUCTOR_COURSES_PAGE_E2E_TEST_JSON = "/InstructorCoursesPageE2ETest.json";
        public static final String YOUR_ESTIMATE_OF_HOW_MUCH_EACH_TEAM_MEMBER_HAS_CONTRIBUTED = "Your estimate of how much each team member has contributed.";
    	public static final String DELETE_SESSION = "delete session";
    	public static final String PLEASE_MODIFY_SETTINGS_QUESTIONS_AS_NECESSARY = "Please modify settings/questions as necessary.";
    	public static final String THE_FEEDBACK_SESSION_HAS_BEEN_COPIED = "The feedback session has been copied. ";
    	public static final String COPIED_SESSION = "Copied Session";
    	public static final String COPY_SESSION_TO_OTHER_COURSE = "copy session to other course";
    	public static final String PREVIEW_SESSION_AS_INSTRUCTOR = "preview session as instructor";
    	public static final String BENNY_TMMS_C_FEEDBACK_EDIT_E2E_T_CS2104 = "benny.tmms@CFeedbackEditE2eT.CS2104";
    	public static final String PREVIEW_SESSION_AS_STUDENT = "preview session as student";
    	public static final String THE_QUESTION_HAS_BEEN_DELETED = "The question has been deleted.";
    	public static final String DELETE_QUESTION = "delete question";
    	public static final String THE_QUESTION_HAS_BEEN_DUPLICATED_BELOW = "The question has been duplicated below.";
    	public static final String DUPLICATE_QUESTION = "duplicate question";
    	public static final String P_EM_NEW_DESCRIPTION_EM_P = "<p><em>New Description</em></p>";
    	public static final String THE_CHANGES_TO_THE_QUESTION_HAVE_BEEN_UPDATED = "The changes to the question have been updated.";
    	public static final String REORDER_QUESTIONS = "reorder questions";
    	public static final String QN1 = "qn1";
    	public static final String COPY_QUESTION_FROM_OTHER_SESSION = "copy question from other session";
    	public static final String THE_QUESTION_HAS_BEEN_ADDED_TO_THIS_FEEDBACK_SESSION = "The question has been added to this feedback session.";
    	public static final String ADD_TEMPLATE_QUESTION = "add template question";
    	public static final String THE_FEEDBACK_SESSION_HAS_BEEN_UPDATED = "The feedback session has been updated.";
    	public static final String P_STRONG_NEW_INSTRUCTIONS_STRONG_P = "<p><strong>new instructions</strong></p>";
    	public static final String EDIT_SESSION_DETAILS = "edit session details";
    	public static final String INSTRUCTOR_FEEDBACK_EDIT_PAGE_E2E_TEST_JSON = "/InstructorFeedbackEditPageE2ETest.json";
    	public static final String SPACE_FRONT_SLASH_SPACE = " / ";
    	public static final String _0_0 = "0 / 0";
    	public static final String DELETE_ALL_SESSION = "delete all session";
    	public static final String ALL_SESSIONS_HAVE_BEEN_RESTORED = "All sessions have been restored.";
    	public static final String RESTORE_ALL_SESSION = "restore all session";
    	public static final String THE_FEEDBACK_SESSION_HAS_BEEN_PERMANENTLY_DELETED = "The feedback session has been permanently deleted.";
    	public static final String PERMANENTLY_DELETE_SESSION = "permanently delete session";
    	public static final String THE_FEEDBACK_SESSION_HAS_BEEN_RESTORED = "The feedback session has been restored.";
    	public static final String RESTORE_SESSION = "restore session";
    	public static final String YOU_CAN_RESTORE_IT_FROM_THE_DELETED_SESSIONS_TABLE_BELOW = "You can restore it from the deleted sessions table below.";
    	public static final String THE_FEEDBACK_SESSION_HAS_BEEN_DELETED = "The feedback session has been deleted. ";
    	public static final String SOFT_DELETE_SESSION = "soft delete session";
    	public static final String QUESTION_1_TESTING_QUESTION_TEXT = "Question 1,Testing question text";
    	public static final String SESSION_NAME_SECOND_SESSION = "Session Name,Second Session";
    	public static final String COURSE_C_FEEDBACK_SESSIONS_E2E_T_CS1101 = "Course,CFeedbackSessionsE2eT.CS1101";
    	public static final String DOWNLOAD_RESULTS = "download results";
    	public static final String TEAMMATES_FEEDBACK_SESSION_RESULTS_UNPUBLISHED = "TEAMMATES: Feedback session results unpublished";
    	public static final String THE_FEEDBACK_SESSION_HAS_BEEN_UNPUBLISHED = "The feedback session has been unpublished.";
    	public static final String UNPUBLISH_RESULTS = "unpublish results";
    	public static final String SENT_OUT = " sent out.";
    	public static final String TO_THOSE_STUDENTS_AND_INSTRUCTORS_PLEASE_ALLOW_UP_TO_1_HOUR_FOR_ALL_THE_NOTIFICATION_EMAILS_TO_BE = " to those students and instructors. Please allow up to 1 hour for all the notification emails to be";
    	public static final String SESSION_PUBLISHED_NOTIFICATION_EMAILS_HAVE_BEEN_RESENT = "Session published notification emails have been resent";
    	public static final String RESEND_RESULTS_LINK = "resend results link";
    	public static final String TEAMMATES_FEEDBACK_SESSION_REMINDER = "TEAMMATES: Feedback session reminder";
    	public static final String AND_INSTRUCTORS_PLEASE_ALLOW_UP_TO_1_HOUR_FOR_ALL_THE_NOTIFICATION_EMAILS_TO_BE_SENT_OUT = " and instructors. Please allow up to 1 hour for all the notification emails to be sent out.";
    	public static final String REMINDER_E_MAILS_HAVE_BEEN_SENT_OUT_TO_THOSE_STUDENTS = "Reminder e-mails have been sent out to those students";
    	public static final String SEND_REMINDER_EMAIL = "send reminder email";
    	public static final String TEAMMATES_FEEDBACK_SESSION_RESULTS_PUBLISHED = "TEAMMATES: Feedback session results published";
    	public static final String PLEASE_ALLOW_UP_TO_1_HOUR_FOR_ALL_THE_NOTIFICATION_EMAILS_TO_BE_SENT_OUT = "Please allow up to 1 hour for all the notification emails to be sent out.";
    	public static final String THE_FEEDBACK_SESSION_HAS_BEEN_PUBLISHED = "The feedback session has been published. ";
    	public static final String COPIED_NAME = "Copied Name";
    	public static final String ADD_NEW_COPIED_SESSION = "add new copied session";
    	public static final String ADD_NEW_SESSION = "add new session";
    	public static final String VERIFY_RESPONSE_RATE = "verify response rate";
    	public static final String PUBLISH_RESULTS = "publish results";
    	public static final String COPIED_NAME_2 = "Copied Name 2";
    	public static final String COPY_SESSION = "copy session";
    	public static final String CLICK_THE_ADD_NEW_QUESTION_BUTTON_BELOW_TO_BEGIN_ADDING_QUESTIONS_FOR_THE_FEEDBACK_SESSION = "Click the \"Add New Question\" button below to begin adding questions for the feedback session.";
    	public static final String THE_FEEDBACK_SESSION_HAS_BEEN_ADDED = "The feedback session has been added.";
    	public static final String _RESULT_CSV = "_result.csv";
    	public static final String UNDERSCORE = "_";
    	public static final String P_PLEASE_FILL_IN_THE_NEW_FEEDBACK_SESSION_P = "<p>Please fill in the new feedback session.</p>";
    	public static final String _2035_04_30_8_00_PM_0000 = "2035-04-30 8:00 PM +0000";
    	public static final String _2035_04_01_9_59_PM_0000 = "2035-04-01 9:59 PM +0000";
    	public static final String NEW_SESSION = "New Session";
    	public static final String CLOSED_SESSION = "closedSession";
    	public static final String CHARLIE_TMMS_C_FEEDBACK_SESSIONS_E2E_T_CS1101 = "charlie.tmms@CFeedbackSessionsE2eT.CS1101";
    	public static final String INSTRUCTOR_FEEDBACK_SESSIONS_PAGE_E2E_TEST_JSON = "/InstructorFeedbackSessionsPageE2ETest.json";
    	public static final String ALL_SESSIONS_HAVE_BEEN_PERMANENTLY_DELETED = "All sessions have been permanently deleted.";
    	public static final String SC_DETAILS_E2E_T_CHARLIE = "SCDetailsE2eT.charlie";
    	public static final String SC_DETAILS_E2E_T_BENNY = "SCDetailsE2eT.benny";
    	public static final String SC_DETAILS_E2E_T_INSTR2 = "SCDetailsE2eT.instr2";
    	public static final String SC_DETAILS_E2E_T_INSTR = "SCDetailsE2eT.instr";
    	public static final String SC_DETAILS_E2E_T_ALICE = "SCDetailsE2eT.alice";
    	public static final String SC_DETAILS_E2E_T_CS2104 = "SCDetailsE2eT.CS2104";
    	public static final String STUDENT_COURSE_DETAILS_PAGE_E2E_TEST_JSON = "/StudentCourseDetailsPageE2ETest.json";
    	public static final String NO_STUDENT_WITH_GIVEN_REGISTRATION_KEY = "No student with given registration key: ";
    	public static final String SCJ_CONFIRMATION_E2E_T_CS2104 = "SCJConfirmationE2eT.CS2104";
    	public static final String ALICE_TMMS = "alice.tmms";
    	public static final String ALICE_TMMS_SCJ_CONFIRMATION_E2E_T_CS2104 = "alice.tmms@SCJConfirmationE2eT.CS2104";
    	public static final String STUDENT_COURSE_JOIN_CONFIRMATION_PAGE_E2E_TEST_JSON = "/StudentCourseJoinConfirmationPageE2ETest.json";
    	public static final String DIV_TABLE_RESPONSIVE_TABLE_TABLE_TBODY = "div.table-responsive table.table tbody";
    	public static final String DIV_CARD_BG_LIGHT = "div.card.bg-light";
    	public static final String S_HOME_UI_T_STUDENT = "SHomeUiT.student";
    	public static final String STUDENT_HOME_PAGE_E2E_TEST_JSON = "/StudentHomePageE2ETest.json";
    	public static final String THIS_IS_ENOUGH_$ = "this is enough!$%&*</>";
    	public static final String AMERICAN = "American";
    	public static final String INST = "inst";
    	public static final String E_EMAIL_TMT = "e@email.tmt";
    	public static final String SHORT_NAME = "short.name";
    	public static final String TYPICAL_CASE_EDIT_PROFILE_PAGE = "Typical case: edit profile page";
    	public static final String _220PX = "220px";
    	public static final String SRC_TEST_RESOURCES_IMAGES_PROFILE_PIC_PNG = "src/test/resources/images/profile_pic.png";
    	public static final String TYPICAL_CASE_PICTURE_UPLOAD_AND_EDIT = "Typical case: picture upload and edit";
    	public static final String I_AM_JUST_ANOTHER_STUDENT_P = "I am just another student :P";
    	public static final String SINGAPOREAN = "Singaporean";
    	public static final String TEAMMATES_TEST_INSTITUTE_4 = "TEAMMATES Test Institute 4";
    	public static final String I_M_BENNY_GMAIL_TMT = "i.m.benny@gmail.tmt";
    	public static final String BEN = "Ben";
    	public static final String S_PROF_UI_T_STUDENT = "SProfUiT.student";
    	public static final String TYPICAL_CASE_LOG_IN_WITH_FILLED_PROFILE_VALUES = "Typical case: Log in with filled profile values";
    	public static final String STUDENT_PROFILE_PAGE_E2E_TEST_JSON = "/StudentProfilePageE2ETest.json";
    	public static final String ENTITY_NOT_FOUND_EXCEPTION_TESTING = "EntityNotFoundException testing";
    	public static final String INVALID_HTTP_PARAM_EXCEPTION_TESTING = "InvalidHttpParamException testing";
    	public static final String UNAUTHORIZED_ACCESS_EXCEPTION_TESTING = "UnauthorizedAccessException testing";
    	public static final String DATASTORE_TIMEOUT_EXCEPTION_TESTING = "DatastoreTimeoutException testing";
    	public static final String DEADLINE_EXCEEDED_EXCEPTION_TESTING = "DeadlineExceededException testing";
    	public static final String NULL_POINTER_EXCEPTION_TESTING = "NullPointerException testing";
    	public static final String ASSERTION_ERROR_TESTING = "AssertionError testing";
    	public static final String IANA_TIMEZONE_DATABASE_URL = "https://www.iana.org/time-zones";
    	public static final int DAYS_TO_UPDATE_TZ = 120;
    	public static final String YYYY_MM_DD = "yyyy-MM-dd";
    	public static final String RELEASED = "\\(Released (.+)\\)";
    	public static final String DATE = "date";
    	public static final String VERSION = "version";
    	public static final String END_ACTUAL = "</actual>";
    	public static final String START_ACTUAL = "<actual>";
    	public static final String END_EXPECTED = "</expected>";
    	public static final String START_EXPECTED = "<expected>";
    	public static final String TZVERSION_MOMENT = "tzversion-moment";
    	public static final String TZVERSION_JAVA = "tzversion-java";
    	public static final String TZ_MOMENT = "tz-moment";
    	public static final String TZ_JAVA = "tz-java";
    	
    	
    }

}
