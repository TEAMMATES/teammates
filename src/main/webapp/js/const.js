"use strict";
var constants = {
      COURSE_ID_MAX_LENGTH : 40,
      COURSE_NAME_MAX_LENGTH : 64,
      EVAL_NAME_MAX_LENGTH : 38,
      FEEDBACK_SESSION_NAME_MAX_LENGTH : 38,
      FEEDBACK_SESSION_INSTRUCTIONS_MAX_LENGTH : 500,
    
    // Field names
      USER_ID : 'user',
    
    // Used in instructorCourse.js
      COURSE_ID : 'courseid',
      COURSE_NAME : 'coursename',
      COURSE_TIME_ZONE : 'coursetimezone',
      COURSE_INSTRUCTOR_NAME : 'instructorname',
      COURSE_INSTRUCTOR_EMAIL : 'instructoremail',
      COURSE_INSTRUCTOR_ID : 'instructorid',
    
    // Used in instructorEval.js
      EVALUATION_START : 'start',
      EVALUATION_STARTTIME : 'starttime',
      EVALUATION_TIMEZONE : 'timezone',
    
    // Used in instructorFeedback.js
    // TODO: Check if we can move most of these into instructorFeedback.js
      FEEDBACK_SESSION_NAME : 'fsname', // also used in feedbackResponseComments.js
      FEEDBACK_SESSION_STARTDATE : 'startdate',
      FEEDBACK_SESSION_STARTTIME : 'starttime',
      FEEDBACK_SESSION_TIMEZONE : 'timezone',
      FEEDBACK_SESSION_CHANGETYPE : 'feedbackchangetype',
      FEEDBACK_SESSION_VISIBLEDATE : 'visibledate',
      FEEDBACK_SESSION_VISIBLETIME : 'visibletime',
      FEEDBACK_SESSION_PUBLISHDATE : 'publishdate',
      FEEDBACK_SESSION_PUBLISHTIME : 'publishtime',
      FEEDBACK_SESSION_SESSIONVISIBLEBUTTON : 'sessionVisibleFromButton',
      FEEDBACK_SESSION_RESULTSVISIBLEBUTTON : 'resultsVisibleFromButton',
    
    // Used in instructorFeedbackEdit.js
    // TODO: Check if we can move most of these into instructorFeedbackEdit.js
      FEEDBACK_QUESTION_GIVERTYPE : 'givertype',
      FEEDBACK_QUESTION_RECIPIENTTYPE : 'recipienttype',
      FEEDBACK_QUESTION_NUMBEROFENTITIES : 'numofrecipients',
      FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE : 'numofrecipientstype',
      FEEDBACK_QUESTION_TYPE : 'questiontype',
      FEEDBACK_QUESTION_MCQCHOICE : 'mcqOption',
      FEEDBACK_QUESTION_MCQOTHEROPTION : 'mcqOtherOption',
      FEEDBACK_QUESTION_MCQOTHEROPTIONFLAG : 'mcqOtherOptionFlag',
      FEEDBACK_QUESTION_MSQCHOICE : 'msqOption',
      FEEDBACK_QUESTION_MSQOTHEROPTION : 'msqOtherOption',
      FEEDBACK_QUESTION_MSQOTHEROPTIONFLAG : 'msqOtherOptionFlag',
      FEEDBACK_QUESTION_CONSTSUMOPTION : 'constSumOption',
      FEEDBACK_QUESTION_CONSTSUMOPTIONTABLE : 'constSumOptionTable',
      FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS : 'constSumToRecipients',
      FEEDBACK_QUESTION_CONSTSUMPOINTS : 'constSumPoints',
      FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHOPTION : 'constSumPointsForEachOption',
      FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHRECIPIENT : 'constSumPointsForEachRecipient',
      FEEDBACK_QUESTION_NUMBEROFCHOICECREATED : 'noofchoicecreated',
      FEEDBACK_QUESTION_NUMSCALE_MIN : 'numscalemin',
      FEEDBACK_QUESTION_NUMSCALE_MAX : 'numscalemax',
      FEEDBACK_QUESTION_NUMSCALE_STEP : 'numscalestep',
      FEEDBACK_QUESTION_NUMBER : 'questionnum',
      FEEDBACK_QUESTION_TEXT : 'questiontext',
      FEEDBACK_QUESTION_DESCRIPTION : 'questiondescription',
      FEEDBACK_QUESTION_EDITTEXT : 'questionedittext',
      FEEDBACK_QUESTION_DISCARDCHANGES : 'questiondiscardchanges',
      FEEDBACK_QUESTION_EDITTYPE : 'questionedittype',
      FEEDBACK_QUESTION_SAVECHANGESTEXT : 'questionsavechangestext',
      FEEDBACK_QUESTION_SHOWRESPONSESTO : 'showresponsesto',
      FEEDBACK_QUESTION_SHOWGIVERTO : 'showgiverto',
      FEEDBACK_QUESTION_SHOWRECIPIENTTO : 'showrecipientto',
      FEEDBACK_QUESTION_TYPENAME_TEXT : 'Essay question',
      FEEDBACK_QUESTION_TYPENAME_MCQ : 'Multiple-choice (single answer)',
      FEEDBACK_QUESTION_TYPENAME_MSQ : 'Multiple-choice (multiple answers)',
      FEEDBACK_QUESTION_TYPENAME_NUMSCALE : 'Numerical-scale question',
      FEEDBACK_QUESTION_TYPENAME_CONSTSUM_OPTION : 'Distribute points (among options) question',
      FEEDBACK_QUESTION_TYPENAME_CONSTSUM_RECIPIENT : 'Distribute points (among recipients) question',
      FEEDBACK_QUESTION_TYPENAME_CONTRIB : 'Team contribution question',
      FEEDBACK_QUESTION_TYPENAME_RUBRIC : 'Rubric question',
      FEEDBACK_QUESTION_TYPENAME_RANK_OPTION : 'Rank options question',
      FEEDBACK_QUESTION_TYPENAME_RANK_RECIPIENT : 'Rank recipients question',
      FEEDBACK_QUESTION_RANKOPTION : 'rankOption',
      FEEDBACK_QUESTION_RANKOPTIONTABLE : 'rankOptionTable',
      FEEDBACK_QUESTION_RANKTORECIPIENTS : 'rankToRecipients',
    
    // Used in feedbackResponseComments.js
      FEEDBACK_RESPONSE_ID : 'responseid',
      FEEDBACK_RESPONSE_COMMENT_ID : 'responsecommentid',
      FEEDBACK_RESPONSE_COMMENT_TEXT : 'responsecommenttext',
    
    // Display messages
    // Used for validating input
      DISPLAY_INPUT_FIELDS_EXTRA : 'There are too many fields.',
      DISPLAY_INPUT_FIELDS_MISSING : 'There are missing fields.',
      DISPLAY_GOOGLEID_INVALID : 'GoogleID should only consist of alphanumerics, fullstops, dashes or underscores.',
      DISPLAY_EMAIL_INVALID : 'The e-mail address is invalid.',
      DISPLAY_NAME_INVALID : 'Name should only consist of alphanumerics or hyphens, apostrophes, fullstops, '
                               + 'commas, slashes, round brackets<br> and not more than 40 characters.',
      DISPLAY_STUDENT_TEAMNAME_INVALID : 'Team name should contain less than 60 characters.',
    
    // Used in instructorCourse.js only
      DISPLAY_COURSE_LONG_ID : 'Course ID should not exceed ' + COURSE_ID_MAX_LENGTH + ' characters.',
      DISPLAY_COURSE_LONG_NAME : 'Course name should not exceed ' + COURSE_NAME_MAX_LENGTH + ' characters.',
      DISPLAY_COURSE_INVALID_ID : 'Please use only alphabets, numbers, dots, hyphens, underscores and dollar signs '
                                    + 'in course ID. Spaces are not allowed for course ID.',
      DISPLAY_COURSE_INVALID_TIME_ZONE : 'Please select a valid course time zone from the provided options.',
      DISPLAY_COURSE_COURSE_ID_EMPTY : 'Course ID cannot be empty.',
      DISPLAY_COURSE_COURSE_NAME_EMPTY : 'Course name cannot be empty',
    
    // Used in instructorCourseEdit.js
      DISPLAY_INSTRUCTOR_ID_EMPTY : 'Instructor ID cannot be empty.',
      DISPLAY_INSTRUCTOR_NAME_EMPTY : 'Instructor name cannot be empty.',
      DISPLAY_INSTRUCTOR_EMAIL_EMPTY : 'Instructor email cannot be empty.',
      DISPLAY_CANNOT_DELETE_LAST_INSTRUCTOR : 'There is only ONE instructor left in the course. '
                                                + 'You are not allowed to delete this instructor.',
    
    // Used in instructorCourseEnroll.js only
      DISPLAY_ENROLLMENT_INPUT_EMPTY : 'Please input at least one student detail.',
    
      DISPLAY_FIELDS_EMPTY : 'Please fill in all the relevant fields.',
    
    // Used in instructorFeedback.js only
      FEEDBACK_SESSION_COPY_INVALID : 'There is no feedback session to be copied.',
      FEEDBACK_QUESTION_COPY_INVALID : 'There are no questions to be copied.',
      DISPLAY_FEEDBACK_SESSION_NAME_DUPLICATE :
            'This feedback session name already existed in this course. Please use another name.',
      DISPLAY_FEEDBACK_SESSION_NAME_EMPTY : 'Feedback session name must not be empty.',
      DISPLAY_FEEDBACK_QUESTION_NUMBEROFENTITIESINVALID :
            'Please enter the maximum number of recipients each respondents should give feedback to.',
    
      DISPLAY_FEEDBACK_QUESTION_TEXTINVALID : 'Please enter a valid question. The question text cannot be empty.',
      DISPLAY_FEEDBACK_QUESTION_NUMSCALE_OPTIONSINVALID : 'Please enter valid options. The min/max/step cannot be empty.',
      DISPLAY_FEEDBACK_QUESTION_NUMSCALE_INTERVALINVALID :
            'Please enter valid options. The interval is not divisible by the specified increment.',
    
      DISPLAY_FEEDBACK_SESSION_VISIBLE_DATEINVALID : 'Feedback session visible date must not be empty',
      DISPLAY_FEEDBACK_SESSION_PUBLISH_DATEINVALID : 'Feedback session publish date must not be empty',
    
    // Max length for input
      TEAMNAME_MAX_LENGTH : 60,
      NAME_MAX_LENGTH : 40,
      INSTITUTION_MAX_LENGTH : 64
}
