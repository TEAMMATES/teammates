import {
    showModalAlert,
} from './bootboxWrapper';

import {
    ParamsNames,
} from './const';

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
    const sessionVisibilityDescription = $(`#${ParamsNames.FEEDBACK_SESSION_SESSIONVISIBILITY}`).val();
    const sessionVisibilityCustom = $(`#${ParamsNames.FEEDBACK_SESSION_SESSIONVISIBILITY_CUSTOM}`).val();
    const sessionVisibilityAtOpen = $(`#${ParamsNames.FEEDBACK_SESSION_SESSIONVISIBILITY_ATOPEN}`).val();
    const generalInfo = `<p>${sessionVisibilityDescription}</p>`;
    const atOption = `<p><label>At:</label> ${sessionVisibilityCustom}</p>`;
    const openTimeOption = `<p><label>Submission opening time:</label> ${sessionVisibilityAtOpen}</p>`;
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
    const responsesVisibilityDescription = $(`#${ParamsNames.FEEDBACK_SESSION_RESPONSESVISIBILITY}`).val();
    const responsesVisibilityCustom = $(`#${ParamsNames.FEEDBACK_SESSION_RESPONSESVISIBILITY_CUSTOM}`).val();
    const responsesVisibilityImmediately = $(`#${ParamsNames.FEEDBACK_SESSION_RESPONSESVISIBILITY_IMMEDIATELY}`).val();
    const responsesVisibilityLater = $(`#${ParamsNames.FEEDBACK_SESSION_RESPONSESVISIBILITY_LATER}`).val();
    const generalInfo = `<p>${responsesVisibilityDescription}</p>`;
    const atOption = `<p><label>At:</label> ${responsesVisibilityCustom}</p>`;
    const onResponseSubmissionOption = `<p><label>Immediately:</label> ${responsesVisibilityImmediately}</p>`;
    const publishManualOption = `<p><label>Not now:</label> ${responsesVisibilityLater}</p>`;
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
    const sendEmailsDescription = $(`#${ParamsNames.FEEDBACK_SESSION_SENDEMAILS}`).val();
    const sendEmailsAtOpen = $(`#${ParamsNames.FEEDBACK_SESSION_SENDEMAILS_ATOPEN}`).val();
    const sendEmailsAtClosed = $(`#${ParamsNames.FEEDBACK_SESSION_SENDEMAILS_ATCLOSED}`).val();
    const sendEmailsOnPublish = $(`#${ParamsNames.FEEDBACK_SESSION_SENDEMAILS_ONPUBLISH}`).val();
    const generalInfo = `<p>${sendEmailsDescription}</p>`;
    const sessionOpenOption = `<p><label>Session opening reminder:</label> ${sendEmailsAtOpen}</p>`;
    const sessionClosedOption = `<p><label>Session closing reminder:</label> ${sendEmailsAtClosed}</p>`;
    const resultsPublishedOption = `<p><label>Results published announcement:</label> ${sendEmailsOnPublish}</p>`;
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
