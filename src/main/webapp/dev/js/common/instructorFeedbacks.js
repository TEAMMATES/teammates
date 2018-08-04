import {
    showModalAlert,
} from './bootboxWrapper';

import {
    ParamsNames,
} from './const';

const FEEDBACK_SESSION_SESSIONVISIBILITY_DESCRIPTION = 'This option allows you to select when you'
        + ' want the questions for the feedback session to be visible to users who need to participate.'
        + ' Users cannot submit their responses until the submission opening time.';
const FEEDBACK_SESSION_RESULTSVISIBLE_CUSTOM = 'Select this option to enter in a specific date and time when'
        + ' the feedback session will become visible. Note that you can make a session visible'
        + ' before it is open for submissions so that users can preview the questions.';
const FEEDBACK_SESSION_SESSIONVISIBLE_ATOPEN = 'Select this option to have the feedback session become'
        + ' visible when it is open for submissions.';
const FEEDBACK_SESSION_RESPONSESVISIBILITY_DESCRIPTION = 'Please select when the responses for the feedback'
        + ' session will be visible to the designated recipients. You can select the response visibility for'
        + ' each type of user and question later.';
const FEEDBACK_SESSION_RESPONSESVISIBLE_CUSTOM = 'Select this option to use a self defined time for when the'
        + ' responses of the feedback session will be visible to the designated recipients.';
const FEEDBACK_SESSION_RESPONSESVISIBLE_ONSUBMIT = 'Select this option to have the feedback responses be immediately'
        + ' visible to others as soon as they are submitted. Note that who can see the responses depends on the feedback'
        + ' path of the question.';
const FEEDBACK_SESSION_RESPONSESVISIBLE_LATER = 'Select this option if you intend to manually publish the'
        + ' responses for this session later on.';
const FEEDBACK_SESSION_EMAILREMINDERS_DESCRIPTION = 'This setting allows you to automatically send email notifications'
        + ' to students on important events.';
const FEEDBACK_SESSION_EMAILREMINDERS_ATOPEN = 'Select this option to automatically send an email to students'
        + ' to notify them when the feedback session is open for submissions. Emails are sent within 15 minutes'
        + ' once the session opens.';
const FEEDBACK_SESSION_EMAILREMINDERS_ATCLOSED = 'Select this option to automatically send an email to students to remind'
        + ' them to submit their responses 24 hours before the end of the feedback session.';
const FEEDBACK_SESSION_EMAILREMINDERS_ONPUBLISH = 'Select this option to automatically send an email to students to notify'
        + ' them when the feedback session results are published.';

function updateUncommonSettingsSessionVisibilityInfo() {
    const info = 'Session is visible at submission opening time, '
             + 'responses are only visible when you publish the results.';

    $('#uncommonSettingsSessionResponsesVisibleInfoText').html(info);
}

function updateUncommonSettingsEmailSendingInfo() {
    const info = 'Emails are sent when session opens (within 15 mins), '
             + '24 hrs before session closes and when results are published.';

    $('#uncommonSettingsSendEmailsInfoText').html(info);
}

function updateUncommonSettingsInfo() {
    updateUncommonSettingsSessionVisibilityInfo();
    updateUncommonSettingsEmailSendingInfo();
}

function isDefaultSessionResponsesVisibleSetting() {
    return $('#sessionVisibleFromButton_atopen').prop('checked')
           && $('#resultsVisibleFromButton_later').prop('checked');
}

function isDefaultSendEmailsSetting() {
    return $('#sendreminderemail_open').prop('checked')
           && $('#sendreminderemail_closing').prop('checked')
           && $('#sendreminderemail_published').prop('checked');
}

function showUncommonPanelsForSessionResponsesVisible() {
    const $sessionResponsesVisiblePanel = $('#sessionResponsesVisiblePanel');

    $('#uncommonSettingsSessionResponsesVisible').after($sessionResponsesVisiblePanel);
    $sessionResponsesVisiblePanel.show();
    $('#uncommonSettingsSessionResponsesVisibleInfoText').parent().hide();
}

function showUncommonPanelsForSendEmails() {
    const $sendEmailsForPanel = $('#sendEmailsForPanel');

    $('#uncommonSettingsSendEmails').after($sendEmailsForPanel);
    $sendEmailsForPanel.show();
    $('#uncommonSettingsSendEmailsInfoText').parent().hide();
}

function showUncommonPanelsIfNotInDefaultValues() {
    if (!isDefaultSessionResponsesVisibleSetting()) {
        showUncommonPanelsForSessionResponsesVisible();
    }

    if (!isDefaultSendEmailsSetting()) {
        showUncommonPanelsForSendEmails();
    }
}

function bindUncommonSettingsEvents() {
    $('#editUncommonSettingsSessionResponsesVisibleButton')
            .click(showUncommonPanelsForSessionResponsesVisible);
    $('#editUncommonSettingsSendEmailsButton')
            .click(showUncommonPanelsForSendEmails);
}

/**
 * Saves the (disabled) state of the element in attribute data-last.<br>
 * Toggles whether the given element {@code id} is disabled or not based on
 * {@code bool}.<br>
 * Disabled if true, enabled if false.
 */
function toggleDisabledAndStoreLast(id, bool) {
    $(`#${id}`).prop('disabled', bool);
    $(`#${id}`).data('last', $(`#${id}`).prop('disabled'));
}

/**
 * Toggles whether custom fields are enabled or not for session visible time based
 * on checkbox selection.
 * @param $privateBtn
 */
function formatResponsesVisibilityGroup() {
    const $responsesVisibilityBtnGroup = $(`[name=${ParamsNames.FEEDBACK_SESSION_RESULTSVISIBLEBUTTON}]`);
    $responsesVisibilityBtnGroup.change(() => {
        if ($responsesVisibilityBtnGroup.filter(':checked').val() === 'custom') {
            toggleDisabledAndStoreLast(ParamsNames.FEEDBACK_SESSION_PUBLISHDATE, false);
            toggleDisabledAndStoreLast(ParamsNames.FEEDBACK_SESSION_PUBLISHTIME, false);
        } else {
            toggleDisabledAndStoreLast(ParamsNames.FEEDBACK_SESSION_PUBLISHDATE, true);
            toggleDisabledAndStoreLast(ParamsNames.FEEDBACK_SESSION_PUBLISHTIME, true);
        }
    });
}

/**
 * Constructs the modal text for different advanced settings that can be set
 * while creating a feedback session
 * @param generalInfo general information regarding the setting
 * @param options different options for the setting
 * @returns {string} modal body html
 */
function createAdvancedSettingsModalText(generalInfo, options) {
    let optionList = '<ul>';
    for (let i = 0; i < options.length; i += 1) {
        optionList += `<li>${options[i]}</li>`;
    }
    optionList += '</ul>';

    const modalText = `
    ${generalInfo}<br>
    <label>Options:</label><br>
    <div>${optionList}</div>
    `;
    return modalText;
}

/**
 * Initializes the modal providing more information on different options
 * for setting visibility of a feedback session
 */
function setupSessionVisibilityInfoModal() {
    const generalInfo = `<p>${FEEDBACK_SESSION_SESSIONVISIBILITY_DESCRIPTION}</p>`;
    const atOption = `<p><label>At:</label> ${FEEDBACK_SESSION_RESULTSVISIBLE_CUSTOM}</p>`;
    const openTimeOption = `<p><label>Submission opening time:</label> ${FEEDBACK_SESSION_SESSIONVISIBLE_ATOPEN}</p>`;
    const options = [atOption, openTimeOption];
    const modalText = createAdvancedSettingsModalText(generalInfo, options);

    $('#sessionVisibleCustomInfo').on('click', () => {
        showModalAlert('Session Visibility Options', modalText);
    });
}

/**
 * Initializes the modal providing more information on different options
 * for settings visibility of responses of a feedback session
 */
function setupResponsesVisibilityInfoModal() {
    const generalInfo = `<p>${FEEDBACK_SESSION_RESPONSESVISIBILITY_DESCRIPTION}</p>`;
    const atOption = `<p><label>At:</label> ${FEEDBACK_SESSION_RESPONSESVISIBLE_CUSTOM}</p>`;
    const onResponseSubmissionOption = `<p><label>Immediately:</label> ${FEEDBACK_SESSION_RESPONSESVISIBLE_ONSUBMIT}</p>`;
    const publishManualOption = `<p><label>Not now:</label> ${FEEDBACK_SESSION_RESPONSESVISIBLE_LATER}</p>`;
    const options = [atOption, onResponseSubmissionOption, publishManualOption];
    const modalText = createAdvancedSettingsModalText(generalInfo, options);

    $('#responsesVisibleCustomInfo').on('click', () => {
        showModalAlert('Responses Visibility Options', modalText);
    });
}

/**
 * Initializes the modal providing more information on different options
 * for sending email notifications to students on important events
 */
function setupSendingEmailsInfoModal() {
    const generalInfo = `<p>${FEEDBACK_SESSION_EMAILREMINDERS_DESCRIPTION}</p>`;
    const sessionOpenOption = `<p><label>Session opening reminder:</label> ${FEEDBACK_SESSION_EMAILREMINDERS_ATOPEN}</p>`;
    const sessionClosedOption = '<p><label>Session closing reminder:</label>'
            + ` ${FEEDBACK_SESSION_EMAILREMINDERS_ATCLOSED}</p>`;
    const resultsPublishedOption = '<p><label>Results published announcement:</label>'
            + ` ${FEEDBACK_SESSION_EMAILREMINDERS_ONPUBLISH}</p>`;
    const options = [sessionOpenOption, sessionClosedOption, resultsPublishedOption];
    const modalText = createAdvancedSettingsModalText(generalInfo, options);

    $('#sendEmailRemindersInfo').on('click', () => {
        showModalAlert('Send Email Reminders Options', modalText);
    });
}

/**
 * Hides / shows the 'Submissions Opening/Closing Time' and 'Grace Period' options
 * depending on whether a private session is selected.<br>
 * Toggles whether custom fields are enabled or not for session visible time based
 * on checkbox selection.
 * @param $privateBtn
 */
function formatSessionVisibilityGroup() {
    const $sessionVisibilityBtnGroup = $(`[name=${ParamsNames.FEEDBACK_SESSION_SESSIONVISIBLEBUTTON}]`);
    $sessionVisibilityBtnGroup.change(() => {
        if ($sessionVisibilityBtnGroup.filter(':checked').val() === 'custom') {
            toggleDisabledAndStoreLast(ParamsNames.FEEDBACK_SESSION_VISIBLEDATE, false);
            toggleDisabledAndStoreLast(ParamsNames.FEEDBACK_SESSION_VISIBLETIME, false);
        } else {
            toggleDisabledAndStoreLast(ParamsNames.FEEDBACK_SESSION_VISIBLEDATE, true);
            toggleDisabledAndStoreLast(ParamsNames.FEEDBACK_SESSION_VISIBLETIME, true);
        }
    });
}

export {
    bindUncommonSettingsEvents,
    formatResponsesVisibilityGroup,
    formatSessionVisibilityGroup,
    setupResponsesVisibilityInfoModal,
    setupSendingEmailsInfoModal,
    setupSessionVisibilityInfoModal,
    showUncommonPanelsIfNotInDefaultValues,
    updateUncommonSettingsInfo,
};
