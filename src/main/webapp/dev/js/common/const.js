/**
 * Contains constants to be used across the application.
 */

// Status message type
const StatusType = {
    SUCCESS: 'success',
    INFO: 'info',
    WARNING: 'warning',
    DANGER: 'danger',
    PRIMARY: 'primary',
    isValidType(type) {
        return type === StatusType.SUCCESS || type === StatusType.INFO || type === StatusType.PRIMARY
               || type === StatusType.WARNING || type === StatusType.DANGER;
    },
};
StatusType.DEFAULT = StatusType.INFO;

const ParamsNames = {
    COURSE_ID: 'courseid',
    COURSE_NAME: 'coursename',
    COURSE_TIME_ZONE: 'coursetimezone',
    FEEDBACK_SESSION_NAME: 'fsname',
    FEEDBACK_SESSION_STARTDATE: 'startdate',
    FEEDBACK_SESSION_STARTTIME: 'starttime',
    FEEDBACK_SESSION_TIMEZONE: 'timezone',
    FEEDBACK_SESSION_VISIBLEDATE: 'visibledate',
    FEEDBACK_SESSION_VISIBLETIME: 'visibletime',
    FEEDBACK_SESSION_PUBLISHDATE: 'publishdate',
    FEEDBACK_SESSION_PUBLISHTIME: 'publishtime',
    FEEDBACK_SESSION_SESSIONVISIBLEBUTTON: 'sessionVisibleFromButton',
    FEEDBACK_SESSION_RESULTSVISIBLEBUTTON: 'resultsVisibleFromButton',
    FEEDBACK_QUESTION_CONSTSUMOPTION: 'constSumOption',
    FEEDBACK_QUESTION_CONSTSUMOPTIONTABLE: 'constSumOptionTable',
    FEEDBACK_QUESTION_CONSTSUMPOINTS: 'constSumPoints',
    FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHOPTION: 'constSumPointsForEachOption',
    FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHRECIPIENT: 'constSumPointsForEachRecipient',
    FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS: 'constSumToRecipients',
    FEEDBACK_QUESTION_MCQCHOICE: 'mcqOption',
    FEEDBACK_QUESTION_MSQCHOICE: 'msqOption',
    FEEDBACK_QUESTION_NUMBEROFCHOICECREATED: 'noofchoicecreated',
    FEEDBACK_QUESTION_NUMSCALE_MIN: 'numscalemin',
    FEEDBACK_QUESTION_NUMSCALE_MAX: 'numscalemax',
    FEEDBACK_QUESTION_NUMSCALE_STEP: 'numscalestep',
    FEEDBACK_QUESTION_RANKOPTION: 'rankOption',
    FEEDBACK_QUESTION_RANKOPTIONTABLE: 'rankOptionTable',
    FEEDBACK_QUESTION_RANKTORECIPIENTS: 'rankToRecipients',
    FEEDBACK_QUESTION_SHOWRESPONSESTO: 'showresponsesto',
    FEEDBACK_QUESTION_SHOWGIVERTO: 'showgiverto',
    FEEDBACK_QUESTION_SHOWRECIPIENTTO: 'showrecipientto',
    FEEDBACK_QUESTION_TEXT: 'questiontext',
    FEEDBACK_QUESTION_TYPE: 'questiontype',
    FEEDBACK_QUESTION_EDITTEXT: 'questionedittext',
    FEEDBACK_QUESTION_EDITTYPE: 'questionedittype',
    FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE: 'numofrecipientstype',
    FEEDBACK_QUESTION_RECIPIENTTYPE: 'recipienttype',
    FEEDBACK_QUESTION_DESCRIPTION: 'questiondescription',
    FEEDBACK_QUESTION_DISCARDCHANGES: 'questiondiscardchanges',
    FEEDBACK_QUESTION_SAVECHANGESTEXT: 'questionsavechangestext',
    FEEDBACK_SESSION_ENABLE_EDIT: 'editsessiondetails',
    SESSION_TOKEN: 'token',
};

const Const = {

    ModalDialog: {

        UNREGISTERED_STUDENT: {
            header: 'Register for TEAMMATES',
            text: 'You have to register using a google account in order to access this page. '
                  + 'Would you like to proceed and register?',
        },

    },

    StatusMessages: {

        INSTRUCTOR_DETAILS_LENGTH_INVALID: 'Instructor Details must have 3 columns',

    },

};

export {
    Const,
    ParamsNames,
    StatusType,
};
