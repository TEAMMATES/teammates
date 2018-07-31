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
        This option allows you to select when you want the questions for the feedback
        session to be visible to users who need to participate. Users cannot submit their
        responses until the submission opening time.
    </p>
    `;

    const atOption = `
    <p>
        <label>At:</label> Select this option to enter in a specific date and time when
        the feedback session will become visible. Note that you can make a session visible
        before it is open for submissions so that users can preview the questions.
    </p>
    `;

    const openTimeOption = `
    <p>
        <label>Submission opening time:</label> Select this option to have the feedback
        session become visible when it is open for submissions.
    </p>
    `;

    const modalText = `
    ${generalInfo}<br>
    <label>Options:</label><br>
    <div>
        <ul>
            <li>${atOption}</li>
            <li>${openTimeOption}</li>
        </ul>
    </div>
    `;
    $('#sessionVisibleCustomInfo').on('click', () => {
        showModalAlert('Session Visibility Options', modalText);
    });
}

function setupResponsesVisibilityInfoModal() {
    const generalInfo = `
    <p>
        Please select when the responses for the feedback session will be visible to the
        designated recipients. You can select the response visibility for each type of 
        user and question later.
    </p>
    `;

    const atOption = `
    <p>
        <label>At:</label> Select this option to use a self defined time for when the responses
        of the feedback session will be visible to the designated recipients.
    </p>
    `;

    const onResponseSubmissionOption = `
    <p>
        <label>Immediately:</label> Select this option to have the feedback
        responses be immediately visible to others as soon as they are submitted. Note that who can
        see the responses depends on the feedback path of the question.
    </p>
    `;

    const publishManualOption = `
    <p>
        <label>Not now:</label> Select this option if you intend to manually publish the
        responses for this session later on.
    </p>
    `;

    const modalText = `
    ${generalInfo}<br>
    <label>Options:</label><br>
    <div>
        <ul>
            <li>${atOption}</li>
            <li>${onResponseSubmissionOption}</li>
            <li>${publishManualOption}</li>
        </ul>
    </div>
    `;
    $('#responsesVisibleCustomInfo').on('click', () => {
        showModalAlert('Responses Visibility Options', modalText);
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
    setupSessionVisibilityInfoModal,
    showUncommonPanelsIfNotInDefaultValues,
    updateUncommonSettingsInfo,
};
