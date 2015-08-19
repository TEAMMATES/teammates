package teammates.common.util;

import java.util.HashMap;

import teammates.common.util.Const.StatusMessageColor;
import teammates.common.util.Const.StatusMessages;

public class StatusMessageColors {
    
    public static HashMap<String, Const.StatusMessageColor> getStatusMessageColorsList() {
        HashMap<String, Const.StatusMessageColor> statusMessageColors = 
                                        new HashMap<String, Const.StatusMessageColor>();
        setStatusMessageColors(statusMessageColors);
        
        return statusMessageColors;
    }
    
    public static String getStatusMessageColor(String statusMessage) {
        try {
            StatusMessageColor color = getStatusMessageColorsList().get(StringHelper.removeVariables(statusMessage));

            switch (color) {
                case SUCCESS:
                    return "success";
                case WARNING:
                    return "warning";
                case DANGER:
                    return "danger";
                case INFO:
                default:
                    return "info";
            }
        } catch (Exception e) {
            assert false : Const.StatusCodes.STATUS_MESSAGE_NOT_FOUND_IN_HASHMAP;
            return "info";
        }
    }

    public static void setStatusMessageColors(HashMap<String, StatusMessageColor> statusMessageColors) {
        // Image
        statusMessageColors.put(StatusMessages.IMAGE_TOO_LARGE, StatusMessageColor.DANGER);
        statusMessageColors.put(StatusMessages.FILE_NOT_A_PICTURE, StatusMessageColor.DANGER);
        statusMessageColors.put(StatusMessages.NO_IMAGE_GIVEN, StatusMessageColor.DANGER);
        
        // Receiver list
        statusMessageColors.put(StatusMessages.RECEIVER_LIST_FILE_TOO_LARGE, StatusMessageColor.DANGER);
        statusMessageColors.put(StatusMessages.NOT_A_RECEIVER_LIST_FILE, StatusMessageColor.DANGER);
        statusMessageColors.put(StatusMessages.NO_GROUP_RECEIVER_LIST_FILE_GIVEN, StatusMessageColor.DANGER);
        
        statusMessageColors.put(StatusMessages.LOADING, StatusMessageColor.INFO);
        statusMessageColors.put(StatusMessages.STUDENT_FIRST_TIME, StatusMessageColor.WARNING);
        statusMessageColors.put(StringHelper.removeVariables(StatusMessages.INVALID_EMAIL), StatusMessageColor.DANGER);
        
        // Courses
        statusMessageColors.put(StringHelper.removeVariables(StatusMessages.COURSE_ADDED), StatusMessageColor.SUCCESS);
        statusMessageColors.put(StatusMessages.COURSE_EXISTS, StatusMessageColor.DANGER);
        statusMessageColors.put(StatusMessages.COURSE_EDITED, StatusMessageColor.SUCCESS);
        statusMessageColors.put(StringHelper.removeVariables(StatusMessages.COURSE_ARCHIVED), StatusMessageColor.SUCCESS);
        statusMessageColors.put(StringHelper.removeVariables(StatusMessages.COURSE_ARCHIVED_FROM_HOMEPAGE), StatusMessageColor.SUCCESS);
        statusMessageColors.put(StringHelper.removeVariables(StatusMessages.COURSE_UNARCHIVED), StatusMessageColor.SUCCESS);
        statusMessageColors.put(StringHelper.removeVariables(StatusMessages.COURSE_DELETED), StatusMessageColor.SUCCESS);
        statusMessageColors.put(StatusMessages.COURSE_EMPTY, StatusMessageColor.WARNING);
        statusMessageColors.put(StringHelper.removeVariables(StatusMessages.COURSE_EMPTY_IN_INSTRUCTOR_FEEDBACKS), StatusMessageColor.WARNING);
        statusMessageColors.put(StringHelper.removeVariables(StatusMessages.COURSE_REMINDER_SENT_TO), StatusMessageColor.SUCCESS);
        statusMessageColors.put(StatusMessages.COURSE_REMINDERS_SENT, StatusMessageColor.SUCCESS);
        
        // Course enroll
        statusMessageColors.put(StringHelper.removeVariables(StatusMessages.COURSE_ENROLL_STUDENTS_ERROR), StatusMessageColor.DANGER);
        statusMessageColors.put(StringHelper.removeVariables(StatusMessages.COURSE_ENROLL_STUDENTS_ADDED), StatusMessageColor.SUCCESS);
        statusMessageColors.put(StringHelper.removeVariables(StatusMessages.COURSE_ENROLL_STUDENTS_MODIFIED), StatusMessageColor.SUCCESS);
        statusMessageColors.put(StringHelper.removeVariables(StatusMessages.COURSE_ENROLL_STUDENTS_UNMODIFIED), StatusMessageColor.INFO);
        statusMessageColors.put(StringHelper.removeVariables(StatusMessages.COURSE_ENROLL_STUDENTS_NOT_IN_LIST), StatusMessageColor.INFO);
        statusMessageColors.put(StringHelper.removeVariables(StatusMessages.COURSE_ENROLL_STUDENTS_UNKNOWN), StatusMessageColor.WARNING);
        
        statusMessageColors.put(StringHelper.removeVariables(StatusMessages.TEAM_INVALID_SECTION_EDIT), StatusMessageColor.DANGER);
        statusMessageColors.put(StringHelper.removeVariables(StatusMessages.SECTION_QUOTA_EXCEED), StatusMessageColor.DANGER);
        statusMessageColors.put(StatusMessages.QUOTA_PER_ENROLLMENT_EXCEED, StatusMessageColor.DANGER);
        
        // Course instructor
        statusMessageColors.put(StringHelper.removeVariables(StatusMessages.COURSE_INSTRUCTOR_ADDED), StatusMessageColor.SUCCESS);
        statusMessageColors.put(StatusMessages.COURSE_INSTRUCTOR_EXISTS, StatusMessageColor.DANGER);
        statusMessageColors.put(StatusMessages.COURSE_INSTRUCTOR_EDITED, StatusMessageColor.SUCCESS);
        statusMessageColors.put(StatusMessages.COURSE_INSTRUCTOR_DELETED, StatusMessageColor.SUCCESS);
        statusMessageColors.put(StatusMessages.COURSE_INSTRUCTOR_DELETE_NOT_ALLOWED, StatusMessageColor.DANGER);
        
        // Join key different user
        statusMessageColors.put(StringHelper.removeVariables(StatusMessages.JOIN_COURSE_KEY_BELONGS_TO_DIFFERENT_USER), 
                                                                                                   StatusMessageColor.WARNING);
        statusMessageColors.put(StringHelper.removeVariables(StatusMessages.JOIN_COURSE_GOOGLE_ID_BELONGS_TO_DIFFERENT_USER), 
                                                                                                    StatusMessageColor.DANGER);
        
        // Student Google ID reset
        statusMessageColors.put(StatusMessages.STUDENT_GOOGLEID_RESET, StatusMessageColor.SUCCESS);
        statusMessageColors.put(StatusMessages.STUDENT_GOOGLEID_RESET_FAIL, StatusMessageColor.DANGER);
        
        // Student actions and profile picture
        statusMessageColors.put(StatusMessages.STUDENT_EDITED, StatusMessageColor.SUCCESS);
        statusMessageColors.put(StatusMessages.STUDENT_DELETED, StatusMessageColor.SUCCESS);
        statusMessageColors.put(StatusMessages.STUDENT_EMAIL_CONFLIT, StatusMessageColor.DANGER);
        statusMessageColors.put(StatusMessages.STUDENT_PROFILE_EDITED, StatusMessageColor.SUCCESS);
        statusMessageColors.put(StatusMessages.STUDENT_PROFILE_PICTURE_SAVED, StatusMessageColor.SUCCESS);
        statusMessageColors.put(StatusMessages.STUDENT_PROFILE_PIC_TOO_LARGE, StatusMessageColor.DANGER);
        statusMessageColors.put(StatusMessages.STUDENT_PROFILE_PIC_SERVICE_DOWN, StatusMessageColor.DANGER);
        
        // Feedback session
        statusMessageColors.put(StatusMessages.FEEDBACK_SESSION_ADDED, StatusMessageColor.SUCCESS);
        statusMessageColors.put(StatusMessages.FEEDBACK_SESSION_ADD_DB_INCONSISTENCY, StatusMessageColor.INFO);
        statusMessageColors.put(StatusMessages.FEEDBACK_SESSION_COPIED, StatusMessageColor.SUCCESS);
        statusMessageColors.put(StatusMessages.FEEDBACK_SESSION_COPY_NONESELECTED, StatusMessageColor.DANGER);
        statusMessageColors.put(StringHelper.removeVariables(StatusMessages.FEEDBACK_SESSION_COPY_ALREADYEXISTS), StatusMessageColor.DANGER);
        statusMessageColors.put(StatusMessages.FEEDBACK_SESSION_EDITED, StatusMessageColor.SUCCESS);
        statusMessageColors.put(StatusMessages.FEEDBACK_SESSION_DELETED, StatusMessageColor.SUCCESS);
        statusMessageColors.put(StatusMessages.FEEDBACK_SESSION_PUBLISHED, StatusMessageColor.SUCCESS);
        statusMessageColors.put(StatusMessages.FEEDBACK_SESSION_UNPUBLISHED, StatusMessageColor.SUCCESS);
        statusMessageColors.put(StatusMessages.FEEDBACK_SESSION_REMINDERSSENT, StatusMessageColor.SUCCESS);
        statusMessageColors.put(StatusMessages.FEEDBACK_SESSION_REMINDERSEMPTYRECIPIENT, StatusMessageColor.DANGER);
        statusMessageColors.put(StatusMessages.FEEDBACK_SESSION_EXISTS, StatusMessageColor.DANGER);
        statusMessageColors.put(StatusMessages.FEEDBACK_SESSION_EMPTY, StatusMessageColor.WARNING);
        
        // Feedback question
        statusMessageColors.put(StatusMessages.FEEDBACK_QUESTION_ADDED, StatusMessageColor.SUCCESS);
        statusMessageColors.put(StatusMessages.FEEDBACK_QUESTION_EDITED, StatusMessageColor.SUCCESS);
        statusMessageColors.put(StatusMessages.FEEDBACK_QUESTION_DELETED, StatusMessageColor.SUCCESS);
        statusMessageColors.put(StatusMessages.FEEDBACK_QUESTION_EXISTS, StatusMessageColor.WARNING);
        statusMessageColors.put(StatusMessages.FEEDBACK_QUESTION_EMPTY, StatusMessageColor.WARNING);
        statusMessageColors.put(StatusMessages.FEEDBACK_QUESTION_NUMBEROFENTITIESINVALID, StatusMessageColor.DANGER);
        statusMessageColors.put(StatusMessages.FEEDBACK_QUESTION_TEXTINVALID, StatusMessageColor.DANGER);
        
        // Feedback responses
        statusMessageColors.put(StatusMessages.FEEDBACK_RESPONSES_SAVED, StatusMessageColor.SUCCESS);
        statusMessageColors.put(StringHelper.removeVariables(StatusMessages.FEEDBACK_RESPONSES_MISSING_RECIPIENT), StatusMessageColor.DANGER);
        statusMessageColors.put(StringHelper.removeVariables(StatusMessages.FEEDBACK_RESPONSES_WRONG_QUESTION_TYPE), StatusMessageColor.DANGER);
        statusMessageColors.put(StringHelper.removeVariables(StatusMessages.FEEDBACK_RESPONSES_INVALID_ID), StatusMessageColor.DANGER);
        
        // Feedback response comment
        statusMessageColors.put(StatusMessages.FEEDBACK_RESPONSE_COMMENT_EMPTY, StatusMessageColor.DANGER);
        statusMessageColors.put(StatusMessages.FEEDBACK_RESPONSE_COMMENT_ADDED, StatusMessageColor.SUCCESS);
        statusMessageColors.put(StatusMessages.FEEDBACK_RESPONSE_COMMENT_EDITED, StatusMessageColor.SUCCESS);
        statusMessageColors.put(StatusMessages.FEEDBACK_RESPONSE_COMMENT_DELETED, StatusMessageColor.SUCCESS);
        statusMessageColors.put(StringHelper.removeVariables(StatusMessages.FEEDBACK_RESPONSE_INVALID_RECIPIENT), StatusMessageColor.DANGER);
        statusMessageColors.put(StatusMessages.FEEDBACK_RESPONSE_RECIPIENT_ALREADY_EXISTS, StatusMessageColor.DANGER);
        
        // Feedback submissions
        statusMessageColors.put(StatusMessages.FEEDBACK_SUBMISSIONS_NOT_OPEN, StatusMessageColor.WARNING);
        statusMessageColors.put(StatusMessages.FEEDBACK_SUBMISSION_EXCEEDED_DEADLINE, StatusMessageColor.DANGER);
        
        // Feedback results
        statusMessageColors.put(StatusMessages.FEEDBACK_RESULTS_SOMETHINGNEW, StatusMessageColor.INFO);
        statusMessageColors.put(StatusMessages.FEEDBACK_RESULTS_NOTHINGNEW, StatusMessageColor.WARNING);
        statusMessageColors.put(StatusMessages.FEEDBACK_RESULTS_SECTIONVIEWWARNING, StatusMessageColor.WARNING);
        
        // Enroll line
        statusMessageColors.put(StatusMessages.ENROLL_LINE_EMPTY, StatusMessageColor.DANGER);
        statusMessageColors.put(StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX, StatusMessageColor.DANGER);
        statusMessageColors.put(StringHelper.removeVariables(StatusMessages.ENROLL_LINES_PROBLEM), StatusMessageColor.DANGER);
        
        statusMessageColors.put(StringHelper.removeVariables(StatusMessages.EVENTUAL_CONSISTENCY_MESSAGE_STUDENT), StatusMessageColor.WARNING);
        statusMessageColors.put(StatusMessages.NULL_POST_PARAMETER_MESSAGE, StatusMessageColor.WARNING);
        
        // Input validation
        statusMessageColors.put(StatusMessages.COURSE_INPUT_FIELDS_EXTRA, StatusMessageColor.DANGER);
        statusMessageColors.put(StatusMessages.COURSE_INPUT_FIELDS_MISSING, StatusMessageColor.DANGER);
        statusMessageColors.put(StatusMessages.COURSE_GOOGLEID_INVALID, StatusMessageColor.DANGER);
        statusMessageColors.put(StatusMessages.COURSE_EMAIL_INVALID, StatusMessageColor.DANGER);
        statusMessageColors.put(StatusMessages.COURSE_INSTRUCTORNAME_INVALID, StatusMessageColor.DANGER);
        statusMessageColors.put(StatusMessages.COURSE_COURSE_ID_EMPTY, StatusMessageColor.DANGER);
        statusMessageColors.put(StatusMessages.COURSE_COURSE_NAME_EMPTY, StatusMessageColor.DANGER);
        statusMessageColors.put(StatusMessages.COURSE_INSTRUCTOR_LIST_EMPTY, StatusMessageColor.DANGER);
        statusMessageColors.put(StatusMessages.COURSE_INVALID_ID, StatusMessageColor.DANGER);
        statusMessageColors.put(StatusMessages.COURSE_STUDENTNAME_INVALID, StatusMessageColor.DANGER);
        statusMessageColors.put(StatusMessages.COURSE_TEAMNAME_INVALID, StatusMessageColor.DANGER);
        
        statusMessageColors.put(StatusMessages.FIELDS_EMPTY, StatusMessageColor.DANGER);
        
        // Instructor
        statusMessageColors.put(StatusMessages.INSTRUCTOR_STATUS_DELETED, StatusMessageColor.SUCCESS);
        statusMessageColors.put(StatusMessages.INSTRUCTOR_ACCOUNT_DELETED, StatusMessageColor.SUCCESS);
        statusMessageColors.put(StatusMessages.INSTRUCTOR_REMOVED_FROM_COURSE, StatusMessageColor.SUCCESS);
        statusMessageColors.put(StatusMessages.INSTRUCTOR_COURSE_EMPTY, StatusMessageColor.WARNING);
        statusMessageColors.put(StatusMessages.INSTRUCTOR_PERSISTENCE_ISSUE, StatusMessageColor.WARNING);
        statusMessageColors.put(StatusMessages.INSTRUCTOR_NO_COURSE_AND_STUDENTS, StatusMessageColor.WARNING);
        statusMessageColors.put(StatusMessages.INSTRUCTOR_NO_STUDENT_RECORDS, StatusMessageColor.WARNING);
        statusMessageColors.put(StatusMessages.INSTRUCTOR_SEARCH_NO_RESULTS, StatusMessageColor.WARNING);
        statusMessageColors.put(StatusMessages.INSTRUCTOR_SEARCH_TIPS, StatusMessageColor.INFO);
        
        // Comment
        statusMessageColors.put(StatusMessages.COMMENT_ADDED, StatusMessageColor.SUCCESS);
        statusMessageColors.put(StatusMessages.COMMENT_EDITED, StatusMessageColor.SUCCESS);
        statusMessageColors.put(StatusMessages.COMMENT_DELETED, StatusMessageColor.SUCCESS);
        statusMessageColors.put(StatusMessages.COMMENT_CLEARED, StatusMessageColor.SUCCESS);
        statusMessageColors.put(StatusMessages.COMMENT_CLEARED_UNSUCCESSFULLY, StatusMessageColor.WARNING);
        statusMessageColors.put(StatusMessages.COMMENT_DUPLICATE, StatusMessageColor.DANGER);
        
        // Hints
        statusMessageColors.put(StatusMessages.HINT_FOR_NEW_INSTRUCTOR, StatusMessageColor.INFO);
        statusMessageColors.put(StringHelper.removeVariables(StatusMessages.HINT_FOR_NO_SESSIONS_STUDENT), StatusMessageColor.WARNING);
        
        // Student update profile
        statusMessageColors.put(StatusMessages.STUDENT_UPDATE_PROFILE, StatusMessageColor.INFO);
        statusMessageColors.put(StatusMessages.STUDENT_UPDATE_PROFILE_SHORTNAME, StatusMessageColor.INFO);
        statusMessageColors.put(StatusMessages.STUDENT_UPDATE_PROFILE_EMAIL, StatusMessageColor.INFO);
        statusMessageColors.put(StatusMessages.STUDENT_UPDATE_PROFILE_PICTURE, StatusMessageColor.INFO);
        statusMessageColors.put(StatusMessages.STUDENT_UPDATE_PROFILE_MOREINFO, StatusMessageColor.INFO);
        statusMessageColors.put(StatusMessages.STUDENT_UPDATE_PROFILE_NATIONALITY, StatusMessageColor.INFO);
        
        // Messages that are templates only
        statusMessageColors.put(StringHelper.removeVariables(StatusMessages.STUDENT_COURSE_JOIN_SUCCESSFUL), StatusMessageColor.SUCCESS);
        statusMessageColors.put(StatusMessages.STUDENT_PROFILE_NOT_A_PICTURE, StatusMessageColor.DANGER);
        statusMessageColors.put(StatusMessages.STUDENT_PROFILE_NO_PICTURE_GIVEN, StatusMessageColor.DANGER);
        statusMessageColors.put(StatusMessages.STUDENT_NOT_FOUND_FOR_RECORDS, StatusMessageColor.WARNING);
        statusMessageColors.put(StatusMessages.STUDENT_NOT_FOUND_FOR_COURSE_DETAILS, StatusMessageColor.WARNING);
        statusMessageColors.put(StatusMessages.STUDENT_PROFILE_PICTURE_EDIT_FAILED, StatusMessageColor.DANGER);
        statusMessageColors.put(StatusMessages.STUDENT_NOT_JOINED_YET_FOR_RECORDS, StatusMessageColor.WARNING);
        statusMessageColors.put(StatusMessages.STUDENT_PROFILE_UNACCESSIBLE_TO_INSTRUCTOR, StatusMessageColor.WARNING);
        
        // Unregistered student
        statusMessageColors.put(StringHelper.removeVariables(StatusMessages.UNREGISTERED_STUDENT), StatusMessageColor.WARNING);
        statusMessageColors.put(StringHelper.removeVariables(StatusMessages.UNREGISTERED_STUDENT_RESULTS), StatusMessageColor.WARNING);       
    }
}
