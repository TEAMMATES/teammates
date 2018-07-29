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

function setupSessionVisibilityInfoModal() {
    const generalInfo = `
    <p>
        This option allows you to select when you want the questions
        for the feedback session to be visible to users who need to participate.<br><br>
        <label>Note:</label> Users cannot submit their responses until the submissions
        opening time set using this option.
    </p>
    `;

    const atOptionInfo = `
    <p>
        <label>At:</label> Select this option to enter in a custom date and time for which
        the feedback session will become visible. Note that you can make
        a session visible before it is open for submissions so that users can preview the questions.
    </p>
    `;

    const openTimeOptionInfo = `
    <p>
        <label>Submission opening time:</label> Select this option to have the feedback
        session become visible when it is open for submissions as set before.
    </p>
    `;

    const modalText = `${generalInfo}<br>${atOptionInfo}<br>${openTimeOptionInfo}`;
    $('#sessionVisibleCustomInfo').on('click', () => {
        showModalAlert('Session Visibility Options', modalText);
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
    setupSessionVisibilityInfoModal,
    showUncommonPanelsIfNotInDefaultValues,
    updateUncommonSettingsInfo,
};
