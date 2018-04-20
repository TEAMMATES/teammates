/**
 * Contains constants to be used across the application.
 */

// Frontend only constants
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

// Shared constants between frontend and backend

/**
 * Subset of Bootstrap contextual colors for use in status messages and components of modals.
 * @enum {BootstrapContextualColors}
 */
const BootstrapContextualColors = {
    // Mirrored colors from StatusMessageColor
    SUCCESS: 'success',
    INFO: 'info',
    WARNING: 'warning',
    DANGER: 'danger',
    // Additional contextual colors that can be used in the components of modals
    PRIMARY: 'primary',
    isValidType(type) {
        return type === BootstrapContextualColors.SUCCESS || type === BootstrapContextualColors.INFO ||
        type === BootstrapContextualColors.PRIMARY || type === BootstrapContextualColors.WARNING ||
        type === BootstrapContextualColors.DANGER;
    },
};
BootstrapContextualColors.DEFAULT = BootstrapContextualColors.INFO;

// Mirrored subset of Const#ParamsNames
const ParamsNames = {
    SESSION_TOKEN: 'token',
    COPIED_FEEDBACK_SESSION_NAME: 'copiedfsname',

    COURSE_ID: 'courseid',
    COURSE_NAME: 'coursename',
    COURSE_TIME_ZONE: 'coursetimezone',

    FEEDBACK_SESSION_NAME: 'fsname',
    FEEDBACK_SESSION_STARTDATE: 'startdate',
    FEEDBACK_SESSION_STARTTIME: 'starttime',
    FEEDBACK_SESSION_VISIBLEDATE: 'visibledate',
    FEEDBACK_SESSION_VISIBLETIME: 'visibletime',
    FEEDBACK_SESSION_PUBLISHDATE: 'publishdate',
    FEEDBACK_SESSION_PUBLISHTIME: 'publishtime',
    FEEDBACK_SESSION_TIMEZONE: 'timezone',
    FEEDBACK_SESSION_SESSIONVISIBLEBUTTON: 'sessionVisibleFromButton',
    FEEDBACK_SESSION_RESULTSVISIBLEBUTTON: 'resultsVisibleFromButton',
    FEEDBACK_SESSION_ENABLE_EDIT: 'editsessiondetails',

    FEEDBACK_QUESTION_TEXT: 'questiontext',
    FEEDBACK_QUESTION_DESCRIPTION: 'questiondescription',
    FEEDBACK_QUESTION_TYPE: 'questiontype',
    FEEDBACK_QUESTION_NUMBEROFCHOICECREATED: 'noofchoicecreated',
    FEEDBACK_QUESTION_MCQCHOICE: 'mcqOption',
    FEEDBACK_QUESTION_MSQCHOICE: 'msqOption',
    FEEDBACK_QUESTION_CONSTSUMOPTION: 'constSumOption',
    FEEDBACK_QUESTION_CONSTSUMTORECIPIENTS: 'constSumToRecipients',
    FEEDBACK_QUESTION_CONSTSUMPOINTS: 'constSumPoints',
    FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHOPTION: 'constSumPointsForEachOption',
    FEEDBACK_QUESTION_CONSTSUMPOINTSFOREACHRECIPIENT: 'constSumPointsForEachRecipient',
    FEEDBACK_QUESTION_CONSTSUMALLUNEVENDISTRIBUTION: 'All options',
    FEEDBACK_QUESTION_CONSTSUMSOMEUNEVENDISTRIBUTION: 'At least some options',
    FEEDBACK_QUESTION_CONSTSUMNOUNEVENDISTRIBUTION: 'None',
    FEEDBACK_QUESTION_RECIPIENTTYPE: 'recipienttype',
    FEEDBACK_QUESTION_NUMBEROFENTITIESTYPE: 'numofrecipientstype',
    FEEDBACK_QUESTION_EDITTEXT: 'questionedittext',
    FEEDBACK_QUESTION_DISCARDCHANGES: 'questiondiscardchanges',
    FEEDBACK_QUESTION_EDITTYPE: 'questionedittype',
    FEEDBACK_QUESTION_SAVECHANGESTEXT: 'questionsavechangestext',
    FEEDBACK_QUESTION_SHOWRESPONSESTO: 'showresponsesto',
    FEEDBACK_QUESTION_SHOWGIVERTO: 'showgiverto',
    FEEDBACK_QUESTION_SHOWRECIPIENTTO: 'showrecipientto',
    FEEDBACK_QUESTION_NUMSCALE_MIN: 'numscalemin',
    FEEDBACK_QUESTION_NUMSCALE_MAX: 'numscalemax',
    FEEDBACK_QUESTION_NUMSCALE_STEP: 'numscalestep',
    FEEDBACK_QUESTION_RANKOPTION: 'rankOption',
    FEEDBACK_QUESTION_RANKTORECIPIENTS: 'rankToRecipients',
    FEEDBACK_QUESTION_RANK_IS_MIN_OPTIONS_TO_BE_RANKED_ENABLED: 'minOptionsToBeRankedEnabled',
    FEEDBACK_QUESTION_RANK_IS_MAX_OPTIONS_TO_BE_RANKED_ENABLED: 'maxOptionsToBeRankedEnabled',
    FEEDBACK_QUESTION_RANK_MIN_OPTIONS_TO_BE_RANKED: 'minOptionsToBeRanked',
    FEEDBACK_QUESTION_RANK_MAX_OPTIONS_TO_BE_RANKED: 'maxOptionsToBeRanked',
    FEEDBACK_QUESTION_RANK_IS_MIN_RECIPIENTS_TO_BE_RANKED_ENABLED: 'minRecipientsToBeRankedEnabled',
    FEEDBACK_QUESTION_RANK_IS_MAX_RECIPIENTS_TO_BE_RANKED_ENABLED: 'maxRecipientsToBeRankedEnabled',
    FEEDBACK_QUESTION_RANK_MIN_RECIPIENTS_TO_BE_RANKED: 'minRecipientsToBeRanked',
    FEEDBACK_QUESTION_RANK_MAX_RECIPIENTS_TO_BE_RANKED: 'maxRecipientsToBeRanked',
};

export {
    Const,
    ParamsNames,
    BootstrapContextualColors,
};
